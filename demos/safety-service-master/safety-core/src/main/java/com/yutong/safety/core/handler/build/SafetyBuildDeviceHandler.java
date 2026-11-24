package com.yutong.safety.core.handler.build;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.*;
import com.yutong.safety.core.db.entity.build.vo.BuildAreaVO;
import com.yutong.safety.core.db.entity.build.vo.BuildDeviceVO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.SafetyBuildAreaService;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildDeviceHandler {

    @Value("${build.getDevice.url}")
    private String getBuildTermUrl;

    @Value("${build.getGrantInfo.url}")
    private String getGrantInfoUrl;

    @Value("${build.encryption.key}")
    private String buildEncryKey;

    @Value("${build.signature.key}")
    private String buildSignKey;

    @Value("${build.appid}")
    private String buildAppID;

    @Value("${build.dpcode}")
    private String buildDpcode;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;

    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Resource
    private SafetyBuildAreaService safetyBuildAreaService;

    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;

    @Autowired
    private YxtRebotUtils yxtRebotUtils;

    @Resource
    private SafetySysUserService safetySysUserService;

    /**
     * 数据列表
     * @param param 检索条件
     * @return 结果集
     */
    public Object pageList(BuildDeviceListParam param,String token) {
        List<BuildDeviceVO> list = safetyBuildDeviceService.selectPageListTwo(param);
        BuildPowerListParam powerListParam = new BuildPowerListParam();
        if(!CollectionUtils.isEmpty(list) && list.size() < 1000){
            List<String> deviceCodeList = list.stream().map(SafetyBuildDeviceDO::getDeviceCode).distinct().collect(Collectors.toList());
            powerListParam.setDeviceCodeList(deviceCodeList);
        }
        if(!CollectionUtils.isEmpty(list)){
            List<SafetyBuildPowerDO> powerDOList = safetyBuildPowerService.selectList(powerListParam);
            Map<String,List<SafetyBuildPowerDO>> listMap=new HashMap<>();
            if(!CollectionUtils.isEmpty(powerDOList)){
                listMap = powerDOList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDeviceCode));
            }
            for (BuildDeviceVO deviceDO:list){
                deviceDO.setDutyDeptList(listMap.get(deviceDO.getDeviceCode()));
            }
        }
        long total = safetyBuildDeviceService.selectListCountTwo(param);
        return new PageVO<>(total, list);
    }

    /**
     * 数据更新操作
     * @param param 入参
     * @return 返回
     */
    public Object update(BuildDeviceAddParam param, String token) throws Exception {
        SafetyBuildDeviceDO deviceDO = new SafetyBuildDeviceDO();
        BeanUtils.copyProperties(param,deviceDO);
        return safetyBuildDeviceService.updateByPrimaryKeySelective(deviceDO);
    }

    /**
     * 数据更新操作
     * @param param 入参
     * @return 返回
     */
    public Object updateForAn(BuildAnDeviceAddParam param, String token) throws Exception {
        SafetyBuildDeviceDO deviceDO = new SafetyBuildDeviceDO();
        BeanUtils.copyProperties(param,deviceDO);
        return safetyBuildDeviceService.updateByPrimaryKeySelective(deviceDO);
    }

    /**
     * 数据列表
     * @param param 检索条件
     * @return 结果集
     */
    public Object  pageListForAn(BuildDeviceListParam param,String token) {
        List<BuildDeviceVO> list = safetyBuildDeviceService.selectPageList(param);
        long total = safetyBuildDeviceService.selectListCount(param);
        return new PageVO<>(total, list);
    }


    /**
     * 数据展示操作
     * @param id 入参ID
     * @return 返回值
     */
    public Object view(String id) {
        return safetyBuildDeviceService.selectByPrimaryKey(id);
    }

    /**
     * 根据门禁编码获取门禁信息
     * @param deviceCode 门禁编码
     * @return 返回值
     */
    public Object getByDeviceCode(String deviceCode){
        BuildDeviceListParam listParam = new BuildDeviceListParam();
        listParam.setExtendId(deviceCode);
        List<SafetyBuildDeviceDO> list = safetyBuildDeviceService.selectByExample(listParam);
        if(!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据检索条件获取设备
     * @param listParam 检索条件
     * @return 返回值
     */
    public Object selectList(BuildDeviceListParam listParam){
        return safetyBuildDeviceService.selectByExample(listParam);
    }

    /**
     * 定时更新楼宇门禁设备
     * @param areaId 区域ID
     */
    public int updateDeviceTask(String areaId){
        int buildPageSize = 500;
        int buildPageNo = 1;
        int totalPage = 1;
        int updateRow = 0;
        Map<Integer,String> areaFactoryMap = formatAreaFactoryMap();
        try {
            JSONObject retObj = sendBuildDevicePost(buildPageNo,buildPageSize,areaId);
            if(retObj == null){
                return updateRow;
            }
            Integer count = retObj.getInteger("count");
            if(count == null || count == 0){
                return updateRow;
            }
            JSONArray dataArr = retObj.getJSONArray("data");
            List<SafetyBuildDeviceDO> deviceDOS = formatDeviceResponse(dataArr,areaFactoryMap);
            if(!CollectionUtils.isEmpty(deviceDOS)){
                updateRow = safetyBuildDeviceService.batchUpdate(deviceDOS);
            }
            Integer totalCount = retObj.getInteger("totalcount");
            if(totalCount>buildPageSize){
                totalPage = totalCount%buildPageSize>0?totalCount/buildPageSize+1:totalCount/buildPageSize;
            }
        }catch (Exception e){
            LogUtils.error("调用接口异常",e);
        }
        for(int i=1;i<=totalPage-1;i++){
            try {
                JSONObject returnObj = sendBuildDevicePost(buildPageNo + i, buildPageSize, areaId);
                if (returnObj == null) {
                    continue;
                }
                Integer curCount = returnObj.getInteger("count");
                if (curCount == null || curCount == 0) {
                    continue;
                }
                JSONArray curDataArr = returnObj.getJSONArray("data");
                List<SafetyBuildDeviceDO> curDeviceDOS = formatDeviceResponse(curDataArr,areaFactoryMap);
                if(!CollectionUtils.isEmpty(curDeviceDOS)){
                    int curRow = safetyBuildDeviceService.batchUpdate(curDeviceDOS);
                    updateRow += curRow;
                }
            }catch (Exception cure){
                LogUtils.error("调用接口异常",cure);
            }
        }
        //全量更新时，把未更新到的设备标记为禁用状态
        if(StringUtils.isEmpty(areaId)){
            SafetyBuildDeviceDO record = new SafetyBuildDeviceDO();
            record.setDeviceStatus("0"); //禁用
            record.setIsDelete("1");
            BuildDeviceListParam param = new BuildDeviceListParam();
            param.setUpdateBegin(DateUtils.format(new Date(),1));
            param.setDeviceType("1"); //只对新开普的设备做此逻辑处理
            safetyBuildDeviceService.updateBySelective(record,param);
        }

        return updateRow;
    }

    /**
     * 移除 DEVICE_STATUS = 0  或  REMOVE_FLAG = 1的门禁 对应的权限
     */
    public void removeDevicePower() {
        //获取DEVICE_STATUS = 0  或  REMOVE_FLAG = 1的门禁编码
        List<SafetyBuildDeviceDO> safetyBuildDeviceDOS = safetyBuildDeviceService.selectByRemoveAndDisableList();
        List<String> deviceCodeList = safetyBuildDeviceDOS.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.toList());
        //移除员工门禁权限信息
        try{
            BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
            listParam.setDeviceCodeList(deviceCodeList);
            SafetyBuildEmployeePowerDO employeePowerDO = new SafetyBuildEmployeePowerDO();
            employeePowerDO.setRemark("门禁拆除或禁用移除权限");
            safetyBuildEmployeePowerService.deleteByExample(listParam,employeePowerDO);
        }catch (Exception e){
            LogUtils.error("删除门禁对应员工权限异常",e);
        }
        try {
            //删除责任部门管理的门禁信息
            SafetyBuildPowerDOExample parkExample = new SafetyBuildPowerDOExample();
            SafetyBuildPowerDOExample.Criteria parkCriteria = parkExample.createCriteria();
            parkCriteria.andDeviceCodeIn(deviceCodeList);
            SafetyBuildPowerDO powerDO = new SafetyBuildPowerDO();
            powerDO.setIsDelete("1");
            powerDO.setUpdateTime(DateUtils.format(new Date(),2));
            safetyBuildPowerService.updateByExample(powerDO,parkExample);
        }catch (Exception e1){
            LogUtils.error("删除门禁对应责任部门关系数据异常",e1);
        }
    }

    private JSONObject sendBuildDevicePost(int buildPageNo,int buildPageSize,String areaId) throws Exception{
        JSONObject firstData = new JSONObject();
        firstData.put("pageno",buildPageNo);
        firstData.put("pagesize",buildPageSize);
        if(!StringUtils.isEmpty(areaId)){
            firstData.put("areaid",areaId);
        }
        String retStr = BuildEncryptUtil.sendBuildHttpPost(getBuildTermUrl, firstData.toJSONString(),
                buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        if(ResponseEnum.FAIL.getName().equals(retStr)){
            return null;
        }
        return JSONObject.parseObject(retStr);
    }

    /**
     * 处理设备相关信息
     * @param jsonArray 设备结果集
     * @return 对象集合
     */
    private List<SafetyBuildDeviceDO> formatDeviceResponse(JSONArray jsonArray,Map<Integer,String> areaFactoryMap){
        List<SafetyBuildDeviceDO> deviceDOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(jsonArray)){
            return deviceDOS;
        }
        List<String> listV1 = new ArrayList<>();
        List<String> listV2 = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            try{
                JSONObject curObj = jsonArray.getJSONObject(i);
                Integer areaId = curObj.getInteger("areaid");

                SafetyBuildDeviceDO curDeviceDO = new SafetyBuildDeviceDO();
                String extendid = String.valueOf(curObj.get("extendid"));
                curDeviceDO.setId(UUID.randomUUID().toString().replaceAll("-",""));
                curDeviceDO.setDeviceCode(String.valueOf(curObj.get("extendid")));
                curDeviceDO.setDeviceName(String.valueOf(curObj.get("extendname")));
                curDeviceDO.setAreaName(curObj.getString("areaname"));
                curDeviceDO.setLevf(curObj.getInteger("levf"));
                curDeviceDO.setTermId(curObj.getInteger("termid"));
                curDeviceDO.setTermName(curObj.getString("termname"));
                curDeviceDO.setTermAddr(curObj.getString("termaddr"));
                curDeviceDO.setDeviceStatus(String.valueOf(curObj.get("isuse")));
                if (!StringUtils.isEmpty(curDeviceDO.getDeviceStatus()) && curDeviceDO.getDeviceStatus().equals("1")) {
                    listV2.add(curDeviceDO.getDeviceCode());
                }
                curDeviceDO.setDeviceType("1"); //新开普
                curDeviceDO.setIsDelete("0");
                //areaId = 0 标识设备不可用
                if(areaId > 0){
                    curDeviceDO.setAreaId(areaId);
                    curDeviceDO.setRemoveFlag("0");
                    //设置楼宇门禁的厂区信息
                    String factoryAreaName = areaFactoryMap.get(areaId);
                    if(!StringUtils.isEmpty(factoryAreaName)){
                        String[] curFac = factoryAreaName.split(";");
                        curDeviceDO.setFactoryName(curFac[0]);
                        if(curFac.length>1){
                            curDeviceDO.setFactoryCode(curFac[1]);
                        }
                    }
                }else{
                    curDeviceDO.setAreaId(areaId);
                    curDeviceDO.setRemoveFlag("1");
                    listV1.add(curDeviceDO.getDeviceCode());
                }
                deviceDOS.add(curDeviceDO);
            }catch (Exception e){
                LogUtils.error("参数整理失败，入参="+jsonArray.getJSONObject(i),e);
            }
        }
        LogUtils.info("楼宇设备-拆除-{}", listV1);
        LogUtils.info("楼宇设备-禁用-{}", listV2);
        return deviceDOS;
    }

    /**
     * 处理area的厂区归属信息
     * @return 返回值
     */
    public Map<Integer,String> formatAreaFactoryMap(){
        Map<Integer,String> areaFactoryMap = new HashMap<>();
        BuildAreaListParam listParam = new BuildAreaListParam();
        listParam.setPageFlag(false);
        List<SafetyBuildAreaDO> areaDOS = safetyBuildAreaService.selectList(listParam);
        List<SafetyBuildAreaDO> factoryAreaList = safetyBuildAreaService.selectFactoryList();
        if(CollectionUtils.isEmpty(factoryAreaList) || CollectionUtils.isEmpty(areaDOS)){
            return areaFactoryMap;
        }
        List<BuildAreaVO> newFactoryList = new ArrayList<>();
        for(SafetyBuildAreaDO facArea:factoryAreaList){
            BuildAreaVO vo = new BuildAreaVO();
            BeanUtils.copyProperties(facArea,vo);
            String areaName = facArea.getAreaName();
            if (areaName.contains("十八里河")) {
                vo.setAnFactoryId("147");
            } else if (areaName.contains("新能源")) {
                vo.setAnFactoryId("148");
            } else if (areaName.contains("精益达二十五大街")) {
                vo.setAnFactoryId("160");
            } else if (areaName.contains("深澜动力")) {
                vo.setAnFactoryId("152");
            } else if (areaName.contains("精益达西区")) {
                vo.setAnFactoryId("159");
            } else if (areaName.contains("精益达八大街")) {
                vo.setAnFactoryId("161");
            } else if (areaName.contains("专用车")) {
                vo.setAnFactoryId("154");
            } else if (areaName.contains("重工")) {
                vo.setAnFactoryId("151");
            }else{
                vo.setAnFactoryId("");
            }
            newFactoryList.add(vo);
        }
        areaDOS.forEach(e->{
            String curFullAreaId = e.getFullAreaId();
            List<BuildAreaVO> curFacList = newFactoryList.stream().filter(cur->curFullAreaId.startsWith(cur.getFullAreaId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(curFacList)){
                return;
            }
            areaFactoryMap.put(e.getAreaId(),curFacList.get(0).getAreaName()+";"+curFacList.get(0).getAnFactoryId());
        });
        return areaFactoryMap;
    }

    /**
     *
     * @param param 入参
     * @param token 秘钥
     * @return 返回值
     */
    public HSSFWorkbook exportForAn(BuildDeviceListParam param, String token) {
        String userName = TokenUtils.getUserName(token);
        if(com.alibaba.excel.util.StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"token已失效");
        }
        param.setRemoveFlag("0"); //只查询未移除的设备
        List<BuildDeviceVO> list = safetyBuildDeviceService.selectList(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"门禁名称", "门禁名称(AN)", "序号", "门禁编号","厂区(AN)","楼栋(AN)","状态","备注"};
        String[][] values = new String[list.size()][];
        BuildDeviceVO deviceVO;
        for(int i=0; i<list.size(); i++) {
            deviceVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = deviceVO.getDeviceName();
            values[i][1] = deviceVO.getAnDeviceName();
            values[i][2] = deviceVO.getAnShort()+"";
            values[i][3] = deviceVO.getDeviceCode();
            values[i][4] = deviceVO.getAnFactoryName();
            values[i][5] = deviceVO.getAnBuilding();
            values[i][6] = "1".equals(deviceVO.getDeviceStatus())?"离线":"在线";
            values[i][7] = deviceVO.getAnRemark();
        }
        String sheetName = "楼宇门禁设备列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 初始化人员门禁权限明细
     * @param empNo 员工号
     * @return 返回值
     */
    public Object initEmployeePower(String empNo){
        int buildPageSize = 500;
        int buildPageNo = 1;
        int updateRow = 0;
        int totalPage = 0;
        JSONObject paramObj = new JSONObject();
        paramObj.put("pagesize",buildPageSize);
        paramObj.put("pageno",buildPageNo);
        paramObj.put("status",1); //获取全员授权信息
        paramObj.put("ver",0); //增量标识，0-可获取全部
        if(!StringUtils.isEmpty(empNo)){
            paramObj.put("outid",empNo);
        }
        try{
            BuildDeviceListParam param = new BuildDeviceListParam();
            List<BuildDeviceVO> buildDeviceDOList = safetyBuildDeviceService.selectList(param);
            if(CollectionUtils.isEmpty(buildDeviceDOList)){
                LogUtils.info("未找到有效的设备集合，初始化权限终止");
                return null;
            }
            List<String> deviceCodeList = buildDeviceDOList.stream().map(BuildDeviceVO::getDeviceCode).collect(Collectors.toList());
            JSONObject retObj = syncEmpGrantPost(paramObj);
            if(retObj == null){
                return updateRow;
            }
            Integer count = retObj.getInteger("count");
            if(count == null || count == 0){
                return updateRow;
            }
            JSONObject oneObj = JSONObject.parseObject(retObj.getString("data"));
            JSONArray dataArr = oneObj.getJSONArray("Table");
            try {
                List<SafetyBuildEmployeePowerDO> deviceDOS = formatBuildEmpPower(dataArr);
                if(!CollectionUtils.isEmpty(deviceDOS)){
                    //只保存有效设备的权限
                    deviceDOS = deviceDOS.stream().filter(e->deviceCodeList.contains(e.getDeviceCode())).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(deviceDOS)){
                        safetyBuildEmployeePowerService.insertBatchForInit(deviceDOS);
                    }
                }
            }catch (Exception e){
                LogUtils.error("初始化员工门禁第1页保存失败",e);
            }
            Integer totalCount = retObj.getInteger("totalcount");
            if(totalCount>buildPageSize){
                totalPage = totalCount%buildPageSize>0?totalCount/buildPageSize+1:totalCount/buildPageSize;
            }
            for(int i=1;i<=totalPage-1;i++){
                try {
                    paramObj.put("pageno",buildPageNo + i);
                    JSONObject returnObj = syncEmpGrantPost(paramObj);
                    if (returnObj == null) {
                        continue;
                    }
                    Integer curCount = returnObj.getInteger("count");
                    if (curCount == null || curCount == 0) {
                        continue;
                    }
                    JSONObject forOneObj = JSONObject.parseObject(returnObj.getString("data"));
                    JSONArray curDataArr = forOneObj.getJSONArray("Table");
                    List<SafetyBuildEmployeePowerDO> curDeviceDOS = formatBuildEmpPower(curDataArr);
                    if(CollectionUtils.isEmpty(curDeviceDOS)){
                        continue;
                    }
                    //只保存有效设备的权限
                    curDeviceDOS = curDeviceDOS.stream().filter(e->deviceCodeList.contains(e.getDeviceCode())).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(curDeviceDOS)){
                        continue;
                    }
                    safetyBuildEmployeePowerService.insertBatchForInit(curDeviceDOS);
                }catch (Exception cure){
                    LogUtils.error("初始化员工门禁权限异常",cure);
                }
            }
        }catch (Exception e){
            LogUtils.error("初始化员工门禁权限失败",e);
        }
        return updateRow;
    }

    /**
     * 清理掉新开普无效的门禁权限 （包含员工对应权限以及责任部门对应权限）
     * @param updateTime -全量更新的时间
     */
    public void clearBuildPowerXKP(String updateTime){

        if(StringUtils.isEmpty(updateTime)){
            updateTime = DateUtils.format(DateUtils.addHours(new Date(),-3),2);
        }
        try {
            BuildDeviceListParam buildParam = new BuildDeviceListParam();
            buildParam.setDeviceType("1"); //新开普
            List<SafetyBuildDeviceDO> buildList = safetyBuildDeviceService.selectByExample(buildParam);
            List<String> buildCodeList = buildList.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(buildCodeList)) {
                return;
            }
            try {
                //删除员工的对应权限
                SafetyBuildEmployeePowerDOExample example1 = new SafetyBuildEmployeePowerDOExample();
                SafetyBuildEmployeePowerDOExample.Criteria criteria1 = example1.createCriteria();
                criteria1.andUpdateTimeLessThan(DateUtils.parse(updateTime, 2));
                criteria1.andDeviceCodeIn(buildCodeList);
                safetyBuildEmployeePowerService.delete(example1);
                //删除
                SafetyBuildEmployeePowerDOExample example2 = new SafetyBuildEmployeePowerDOExample();
                SafetyBuildEmployeePowerDOExample.Criteria criteria2 = example2.createCriteria();
                criteria2.andDeleteFlagEqualTo("1"); //删除标记删除的数据
                safetyBuildEmployeePowerService.delete(example2);
            }catch (Exception e2){
                LogUtils.error("删除员工对应门禁数据异常",e2);
            }
        }catch (Exception e){
            LogUtils.error("清理门禁权限异常",e);
        }
    }


    /**
     * 初始化人员门禁权限明细
     * @param empNo 员工号
     * @return
     */
    public Object initEmpDutyDept(String empNo){
        BuildEmployeePowerListParam employeePowerParam = new BuildEmployeePowerListParam();
        employeePowerParam.setDutyDept("null");
        employeePowerParam.setPageFlag(false);

        if(!StringUtils.isEmpty(empNo)){
            employeePowerParam.setEmpCodeList(Arrays.asList(empNo.split(",")));
        }

        List<SafetyEmployeeMessageDO> employeePowerDOS = safetyBuildEmployeePowerService.getEmployeeForEmpPower(employeePowerParam);
        if(CollectionUtils.isEmpty(employeePowerDOS)){
            return null;
        }
        List<SafetyBuildPowerDO> dutyEmpList = safetyBuildPowerService.getDutyEmpByGroup();
        if(CollectionUtils.isEmpty(dutyEmpList)){
            return null;
        }
        Map<String,List<SafetyEmployeeMessageDO>> empMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyEmployeeMessageDO::getOrgCode));
        Map<String,SafetyBuildPowerDO> dutyEmpMap = dutyEmpList.stream().collect(Collectors.toMap(SafetyBuildPowerDO::getDutyDeptCode,
                cur -> cur,(oldValue, newValue) -> newValue));
        List<String> orgList = employeePowerDOS.stream().map(SafetyEmployeeMessageDO::getOrgCode).distinct().collect(Collectors.toList());
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();

        int size = orgList.size();
        int total = (size / 1000) + (((size % 1000) > 0) ? 1 : 0);
        for(int i=0;i<total;i++){
            int minSize = (i)*1000;
            int maxSize = Math.min((i+1)*1000,size);
            List<String> subList = orgList.subList(minSize,maxSize);
            if(CollectionUtils.isEmpty(subList)){
               continue;
            }
            List<BuildGroupDutyVO> groupDutyVOS = safetyBuildPowerService.getGroupDuty(subList);
            if(CollectionUtils.isEmpty(groupDutyVOS)){
                continue;
            }
            groupDutyVOS.forEach(e->{
                String curOrgCode = e.getCode();
                String curDutyCode = e.getDutyDeptCode();
                List<SafetyEmployeeMessageDO> empList = empMap.get(curOrgCode);
                if(CollectionUtils.isEmpty(empList)){
                    return;
                }
                try{
                    List<String> curModifyList = empList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
                    SafetyBuildEmployeePowerDO employeePowerDO = new SafetyBuildEmployeePowerDO();
                    employeePowerDO.setDutyDeptCode(e.getDutyDeptCode());
                    employeePowerDO.setDutyDeptName(e.getDutyDeptName());
                    SafetyBuildPowerDO buildPowerDO = dutyEmpMap.get(curDutyCode);
                    if(buildPowerDO != null){
                        employeePowerDO.setDutyEmpNo(buildPowerDO.getEmployeeCode());
                        employeePowerDO.setDutyEmpName(buildPowerDO.getEmployeeName());
                    }
                    listParam.setEmpCodeList(curModifyList);
                    safetyBuildEmployeePowerService.updateByExample(listParam,employeePowerDO);
                }catch (Exception cure){
                    LogUtils.error("初始化员工责任部门",cure);
                }
            });

        }
        return null;
    }

    /**
     * 获取员工授权信息
     * @return 返回值
     * @throws Exception
     */
    private JSONObject syncEmpGrantPost(JSONObject firstData) throws Exception{
        String retStr = BuildEncryptUtil.sendBuildHttpPost(getGrantInfoUrl, firstData.toJSONString(),
                buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        if(ResponseEnum.FAIL.getName().equals(retStr)){
            return null;
        }
        return JSONObject.parseObject(retStr);
    }

    private List<SafetyBuildEmployeePowerDO> formatBuildEmpPower(JSONArray jsonArray){
        List<SafetyBuildEmployeePowerDO> curList = new ArrayList<>();
        if(jsonArray == null || jsonArray.size()<=0){
            return curList;
        }
        for(int i=0;i<jsonArray.size();i++){
            JSONObject curObj = jsonArray.getJSONObject(i);
            try {
                SafetyBuildEmployeePowerDO curPowerDO = new SafetyBuildEmployeePowerDO();
                curPowerDO.setSourceType("3");//数据初始化
                curPowerDO.setDeviceCode(curObj.getInteger("EXTENDID").toString());
                curPowerDO.setDeviceName(String.valueOf(curObj.get("EXTENDNAME")));
                curPowerDO.setEmployeeCode(String.valueOf(curObj.get("OUTID")));
                curPowerDO.setEmployeeName(String.valueOf(curObj.get("NAME")));
                curPowerDO.setBeginTime(String.valueOf(curObj.get("STARTTIME")).replace("T", " "));
                curPowerDO.setEndTime(String.valueOf(curObj.get("ENDTIME")).replace("T", " "));
                curPowerDO.setCreateBy("数据初始化");
                curList.add(curPowerDO);
            }catch (Exception e){
                LogUtils.error("门禁授权数据处理失败,数据={}",curObj.toJSONString());
            }
        }

        return curList;
    }

    /**
     * 给新设备厂区的 保卫主管 发送消息通知，无对应工厂时给门禁系统管理员发送
     */
    public void sendMessageForNewDevice(){
        String firstRole = "门禁巡逻组长";
        String secondRole = "门禁系统管理员";
        String link = anEipTodoUrl + "buildingAccess/baseInfo/accessManage";
        String content = "[安保管理平台]请维护新增楼宇门禁基础信息,链接："+link;
        //发送
        Set<String> toEmpList = new HashSet<>();

        BuildDeviceListParam parm = new BuildDeviceListParam();
        //查询近两个小时内新增的设备
        parm.setCreateBegin(DateUtils.format(DateUtils.addHours(new Date(),-2),2));
        List<SafetyBuildDeviceDO> newDeviceList = safetyBuildDeviceService.selectByExample(parm);
        if(CollectionUtils.isEmpty(newDeviceList)){
            return;
        }
        List<SafetyBuildDeviceDO> nullFacCodeList = newDeviceList.stream()
                .filter(e->StringUtils.isEmpty(e.getFactoryCode()))
                .collect(Collectors.toList());
        List<SafetyBuildDeviceDO> facCodeList = newDeviceList.stream()
                .filter(e->!StringUtils.isEmpty(e.getFactoryCode()))
                .collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(nullFacCodeList)){
            UserMessageListParam userParam = new UserMessageListParam();
            userParam.setRoleName(secondRole);
            List<SafetySysUserDO> adminList =  safetySysUserService.getList(userParam);
            if(!CollectionUtils.isEmpty(adminList)){
                toEmpList = adminList.stream().map(SafetySysUserDO::getEmpNo).collect(Collectors.toSet());
            }
        }
        if(!CollectionUtils.isEmpty(facCodeList)){
            UserMessageListParam userParam = new UserMessageListParam();
            userParam.setRoleName(firstRole);
            String powerFacArr = facCodeList.stream()
                    .map(SafetyBuildDeviceDO::getFactoryCode)
                    .distinct().collect(Collectors.joining(","));

            userParam.setPowerFactoryArr(powerFacArr);
            List<SafetySysUserDO> roleList =  safetySysUserService.getList(userParam);
            if(!CollectionUtils.isEmpty(roleList)){
               toEmpList.addAll(roleList.stream()
                       .map(SafetySysUserDO::getEmpNo)
                       .collect(Collectors.toSet()));
            }
        }
        //判断接收人不为空时发送宇信通消息
        if(!CollectionUtils.isEmpty(toEmpList)){
            RestTemplate restTemplate = new RestTemplate();
            try {
                yxtRebotUtils.postForEntity(null, incomintRebotId, toEmpList.stream().collect(Collectors.joining(",")), content, restTemplate);
            }catch (Exception e){
                LogUtils.error("发送宇信通消息异常，{}", e);
            }
        }
    }

    public Object getCopyDeviceInfo(String deviceNameOrCode) {
        return safetyBuildDeviceService.getCopyDeviceInfo(deviceNameOrCode);
    }

    /**
     * 权限克隆
     * @param param 入参
     * @return true表示成功
     */
    public Boolean submitCopyData(BuildDeviceCopyPermissionParams param, String token) {
        String id = param.getId();
        String deviceCode = param.getDeviceCode();
        SafetyBuildDeviceDO safetyBuildDeviceDO = safetyBuildDeviceService.selectByPrimaryKey(id);
        if (safetyBuildDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取数据行信息失败：ID=" + id);
        }

        BuildDeviceListParam exam = new BuildDeviceListParam();
        exam.setDeviceCode(deviceCode);
        List<SafetyBuildDeviceDO> safetyBuildDeviceDOS = safetyBuildDeviceService.selectByExample(exam);
        if (CollectionUtils.isEmpty(safetyBuildDeviceDOS)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取克隆的信息失败：DEVICE_CODE=" + deviceCode);
        }
        SafetyBuildDeviceDO copyDataInfo = safetyBuildDeviceDOS.get(0);
        if (id.equals(copyDataInfo.getId())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "权限克隆失败，不能克隆自身的权限信息");
        }

        // 获取拥有DeviceCode权限的所有员工
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectDeviceCodeInfo(Lists.newArrayList(), deviceCode);
        if (CollectionUtils.isEmpty(employeePowerDOS)) {
            return true;
        }
        List<String> empNoList = employeePowerDOS.stream().map(SafetyBuildEmployeePowerDO::getEmployeeCode).filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.toList());

        // 获取此部分员工中拥有ID所在楼宇门禁权限的员工
        List<SafetyBuildEmployeePowerDO> deviceCodeInfos = safetyBuildEmployeePowerService.selectDeviceCodeInfo(empNoList, safetyBuildDeviceDO.getDeviceCode());

        // 数据过滤， 过滤出需要克隆权限的员工
        List<SafetyBuildEmployeePowerDO> lastDataList;
        if (CollectionUtils.isEmpty(deviceCodeInfos)) {
            lastDataList = employeePowerDOS;
        } else {
            // 拥有权限的员工
            Set<String> empNos = deviceCodeInfos.stream().map(SafetyBuildEmployeePowerDO::getEmployeeCode).collect(Collectors.toSet());
            lastDataList = employeePowerDOS.stream().filter(li -> org.apache.commons.lang3.StringUtils.isNotBlank(li.getEmployeeCode()) && !empNos.contains(li.getEmployeeCode())).collect(Collectors.toList());
        }

        // 为这部门员工增加Id所在的权限
        if (!CollectionUtils.isEmpty(lastDataList)) {
            saveData(token, lastDataList, safetyBuildDeviceDO);
        }
        return true;
    }

    /**
     * 数据生成入库
     * @param token token
     * @param lastDataList 需要克隆权限的用户数据
     * @param safetyBuildDeviceDO 克隆的权限
     */
    private void saveData(String token, List<SafetyBuildEmployeePowerDO> lastDataList, SafetyBuildDeviceDO safetyBuildDeviceDO) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String realName = tokenUser.get("realName");
        Date date = new Date();
        String newDeviceCode = safetyBuildDeviceDO.getDeviceCode();
        String anDeviceName = safetyBuildDeviceDO.getAnDeviceName();
        List<SafetyBuildEmployeePowerDO> saveData = new ArrayList<>();
        lastDataList.forEach(data -> {
            SafetyBuildEmployeePowerDO info = new SafetyBuildEmployeePowerDO();
            info.setDeleteFlag("0");
            info.setCreateTime(date);
            info.setUpdateTime(date);
            info.setCreateBy(realName);
            info.setRemark("克隆权限");
            info.setSourceType("3");
            info.setBeginTime(data.getBeginTime());
            info.setEndTime(data.getEndTime());
            info.setDeviceCode(newDeviceCode);
            info.setDeviceName(anDeviceName);
            info.setEmployeeCode(data.getEmployeeCode());
            info.setEmployeeName(data.getEmployeeName());
            saveData.add(info);
        });
        safetyBuildEmployeePowerService.insertBatch(saveData);
    }
}
