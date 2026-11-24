package com.yutong.safety.core.handler.build;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.EipToDoVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.db.entity.build.vo.BuildLongConfirmVO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.*;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.eip.EipCommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildLongConfirmHandler {

    @Resource
    private SafetyBuildLongConfirmService safetyBuildLongConfirmService;

    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    private SafetyBuildWhiteListService safetyBuildWhiteListService;

    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    private SafetySysUserService safetySysUserService;

    @Resource
    private SafetyBuildConfigService safetyBuildConfigService;

    @Resource
    private SafetySystemParamService safetySystemParamService;

    @Resource
    private SafetyBuildLongConfirmHandler safetyBuildLongConfirmHandler;

    @Resource
    private EipCommonService eipCommonService;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;
    //人员权限变更确认Form
    private static String ownerFormId = "empoyBuildAuthConfirm";
    // TODO: 2025/5/24 代办的发起人待定
    @Value("${an.eip.todo.from}")
    private String formEmpNo;

    public PageVO selectPageList(BuildLongConfirmListParam listParam,String token){
        listParam.setPageFlag(true);
        String workNo = TokenUtils.getWorkNo(token);
        if(workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if(!"2".equals(userType)){
            listParam.setCurEmpNo(workNo);
        }
        if(userType.startsWith("3")){
            String dutyDeptCode = userType.split(":")[1];
            listParam.setFilterDutyDept(dutyDeptCode);
        }else{
            listParam.setFilterDutyDept(null);
        }
        // 过滤掉关闭权限的
        listParam.setNotConfirmFlag("2");
        List<BuildLongConfirmVO> listVO = new ArrayList<>();
        List<SafetyBuildLongConfirmDO> list = safetyBuildLongConfirmService.selectList(listParam);
        if(!CollectionUtils.isEmpty(list)){
            List<String> deviceCodeList = list.stream().map(SafetyBuildLongConfirmDO::getDeviceCode).collect(Collectors.toList());
            BuildDeviceListParam deviceListParam = new BuildDeviceListParam();
            deviceListParam.setDeviceCodeList(deviceCodeList);
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectByExample(deviceListParam);
            Map<String,SafetyBuildDeviceDO> deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
            list.forEach(e->{
                SafetyBuildDeviceDO cur = deviceMap.get(e.getDeviceCode());
                BuildLongConfirmVO vo = new BuildLongConfirmVO();
                BeanUtils.copyProperties(e,vo);
                if(cur != null){
                    vo.setFactoryName(cur.getAnFactoryName());
                    vo.setFactoryId(cur.getAnFactoryCode());
                }
                listVO.add(vo);
            });
        }

        Long total = safetyBuildLongConfirmService.selectCount(listParam);
        return new PageVO(total,listVO);
    }

    /**
     * 员工有非本部门管理且在长期未进出门禁列表
     * @param token
     * @return
     */
    public PageVO selectOwnerList(String token, BuildLongConfirmListParam listParam){

        listParam.setPageFlag(true);
        //只查看自己跨部门的权限
        String workNo = TokenUtils.getWorkNo(token);
        listParam.setEmployeeCode(workNo);
        listParam.setCorresFlag("0");
        int cancelDays = 180;
        //获取配置参数-长期未进出天数
        SafetyBuildConfigDO buildConfigDO = safetyBuildConfigService.queryForFirst();
        if(buildConfigDO != null && buildConfigDO.getCancelAccessNum() != null){
            cancelDays = buildConfigDO.getCancelAccessNum();
        }
        listParam.setConfirmFlag("0"); //未确认的
        listParam.setMinDay(cancelDays-7);
        List<BuildLongConfirmVO> listVO = new ArrayList<>();
        List<SafetyBuildLongConfirmDO> list = safetyBuildLongConfirmService.selectList(listParam);
        if(!CollectionUtils.isEmpty(list)){
            List<String> deviceCodeList = list.stream().map(SafetyBuildLongConfirmDO::getDeviceCode).collect(Collectors.toList());
            BuildDeviceListParam deviceListParam = new BuildDeviceListParam();
            deviceListParam.setDeviceCodeList(deviceCodeList);
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectByExample(deviceListParam);
            Map<String,SafetyBuildDeviceDO> deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
            list.forEach(e->{
                SafetyBuildDeviceDO cur = deviceMap.get(e.getDeviceCode());
                BuildLongConfirmVO vo = new BuildLongConfirmVO();
                BeanUtils.copyProperties(e,vo);
                if(cur != null){
                    vo.setFactoryName(cur.getAnFactoryName());
                    vo.setFactoryId(cur.getAnFactoryCode());
                }
                listVO.add(vo);
            });
        }

        Long total = safetyBuildLongConfirmService.selectCount(listParam);
        return new PageVO(total,listVO);
    }

    /**
     * 处理长期未进出门禁信息
     * @return
     */
    public Object initBuildLongTerm(){
        int longDay = 60;
        //获取配置参数-长期未进出天数
        SafetyBuildConfigDO buildConfigDO = safetyBuildConfigService.queryForFirst();
        if(buildConfigDO != null && buildConfigDO.getAccessNum() != null){
            longDay = buildConfigDO.getAccessNum();
        }
        //删除未确认的未进出信息或在白名单中的员工数据
//        safetyBuildLongConfirmService.deleteHistoryConfirm();
        Map<String, Integer> hashMap = safetyBuildLongConfirmService.selectDeleteList();

        BuildDeviceListParam deviceListParam = new BuildDeviceListParam();
        deviceListParam.setSecurityLevelList("937_gsjm_2,937_gssm_1");
        List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectByExample(deviceListParam);
        List<String> deviceCodes;
        if(!CollectionUtils.isEmpty(deviceDOS)){
            deviceCodes = deviceDOS.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.toList());
        }else{
            deviceCodes = Collections.singletonList("-1");
        }
        //根据配置的参数获取长期未进出记录
        String operTimeEnd = DateUtils.format(DateUtils.addDays(new Date(),0-longDay),2);
        BuildLongConfirmListParam param = new BuildLongConfirmListParam();
        param.setOperTimeEnd(operTimeEnd);
        param.setDeviceCodeList(deviceCodes);

        List<SafetyBuildLongConfirmDO> longConfirmDOList = safetyBuildLongConfirmService.queryLongTermList(param);
        if(CollectionUtils.isEmpty(longConfirmDOList)){
            return null;
        }

        //所有设备对应的责任部门
        BuildPowerListParam curListParam = new BuildPowerListParam();
        curListParam.setDeviceCodeList(deviceCodes);

        List<SafetyBuildPowerDO> deviceDutyList = safetyBuildPowerService.selectList(curListParam);
        Map<String,List<SafetyBuildPowerDO>> deviceDutyMap = deviceDutyList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDeviceCode));

        List<SafetyBuildLongConfirmDO> finalList;
                List<SafetyBuildWhiteListDO> whiteListDOList = safetyBuildWhiteListService.selectByEmpCodeList(new ArrayList<>());
        if(!CollectionUtils.isEmpty(whiteListDOList)){
            Set<String> empNoSet = whiteListDOList.stream().map(SafetyBuildWhiteListDO::getEmployeeCode).collect(Collectors.toSet());
            finalList = longConfirmDOList.stream().filter(e->!empNoSet.contains(e.getEmployeeCode())).collect(Collectors.toList());
        }else{
            finalList = longConfirmDOList;
        }
        // 取出不存在的数据进行删除
        Set<String> setList = hashMap.keySet();
        List<String> collect = finalList.stream().filter(li -> StringUtils.isNotBlank(li.getEmployeeCode()) && StringUtils.isNotBlank(li.getDeviceCode())).map(ln -> ln.getEmployeeCode() + "&" + ln.getDeviceCode()).collect(Collectors.toList());
        List<Integer> list = setList.stream().filter(li -> !collect.contains(li)).map(hashMap::get).collect(Collectors.toList());
        Lists.partition(list, 1000).forEach(li -> safetyBuildLongConfirmService.deleteListById(li));

        //总条数超过1000条时，分批处理
        if(finalList.size()>1000){
            int curTotal = finalList.size();
            int totalPage = curTotal/1000+(curTotal%1000>0?1:0);
            for(int j=0;j<totalPage;j++){
                int min = j*1000;
                int max = Math.min((j+1)*1000,curTotal);
                List<SafetyBuildLongConfirmDO> subList = finalList.subList(min,max);
                batchInsertConfirm(subList,deviceDutyMap);
            }
        }else{
            batchInsertConfirm(finalList,deviceDutyMap);
        }

        //针对历史已确认的数据，更新延期时长

        return null;
    }

    /**
     * 长期未进出的代码优化，依据 initBuildLongTerm
     * 把 initBuildLongTerm 的代码进行方法的拆分
     */
    public Object initBuildLongTermV2() {
        // 获取配置参数
        int inactivityDays = obtainInactivityDays();
        String operTimeEnd = DateUtils.format(DateUtils.addDays(new Date(), -inactivityDays), 2);
        List<String> deviceCodes = obtainDeviceCodes();

        // 删除未确认的未进出信息或在白名单中的员工数据
        deleteOldList();

        // 长期未进出列表-根据未进出表，匹配全量数据
        BuildLongConfirmListParam param = new BuildLongConfirmListParam();
        param.setOperTimeEnd(operTimeEnd);
        param.setDeviceCodeList(deviceCodes);
        List<SafetyBuildLongConfirmDO> longConfirmDOList = safetyBuildLongConfirmService.queryLongTermList(param);
        if (CollectionUtils.isEmpty(longConfirmDOList)) {
            return null;
        }

        // 所有设备对应的责任部门
        Map<String, List<SafetyBuildPowerDO>> deviceDutyMap = obtainDeviceDutyMap(deviceCodes);

        // 过滤白名单
        List<SafetyBuildLongConfirmDO> finalList = processLongTermList(longConfirmDOList);

        // 数据入库处理
        handleBatchInsert(finalList, deviceDutyMap);

        // 更新必要字段
        safetyBuildLongConfirmService.updateOfHistoryInfo();
        return null;
    }

    /**
     * 获取配置参数
     */
    private int obtainInactivityDays() {
        SafetyBuildConfigDO buildConfigDO = safetyBuildConfigService.queryForFirst();
        if (buildConfigDO != null && buildConfigDO.getAccessNum() != null) {
            return buildConfigDO.getAccessNum();
        }
        return 60; // 默认60天
    }

    /**
     * 获取绝密区楼宇
     */
    private List<String> obtainDeviceCodes() {
        BuildDeviceListParam deviceListParam = new BuildDeviceListParam();
        deviceListParam.setSecurityLevelList("937_gsjm_2,937_gssm_1");
        List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectByExample(deviceListParam);

        if (CollectionUtils.isEmpty(deviceDOS)) {
            return Collections.singletonList("-1");
        }
        return deviceDOS.stream()
                .map(SafetyBuildDeviceDO::getDeviceCode)
                .collect(Collectors.toList());
    }

    private void deleteOldList() {
        // 数据同步到历史表
        safetyBuildLongConfirmService.saveHistory();
        // 删除未确认的未进出信息或在白名单中的员工数据
        safetyBuildLongConfirmService.deleteHistoryConfirm();
    }

    /**
     * 所有设备对应的责任部门
     */
    private Map<String, List<SafetyBuildPowerDO>> obtainDeviceDutyMap(List<String> deviceCodes) {
        if (CollectionUtils.isEmpty(deviceCodes)) {
            return Maps.newHashMap();
        }

        BuildPowerListParam curListParam = new BuildPowerListParam();
        curListParam.setDeviceCodeList(deviceCodes);
        List<SafetyBuildPowerDO> deviceDutyList = safetyBuildPowerService.selectList(curListParam);

        return deviceDutyList.stream()
                .collect(Collectors.groupingBy(SafetyBuildPowerDO::getDeviceCode));
    }

    /**
     * 过滤白名单
     */
    private List<SafetyBuildLongConfirmDO> processLongTermList(List<SafetyBuildLongConfirmDO> longConfirmDOList) {
        List<SafetyBuildWhiteListDO> whiteListDOList = safetyBuildWhiteListService.selectByEmpCodeList(new ArrayList<>());
        if (CollectionUtils.isEmpty(whiteListDOList)) {
            return longConfirmDOList;
        }

        Set<String> empNoSet = whiteListDOList.stream()
                .map(SafetyBuildWhiteListDO::getEmployeeCode)
                .collect(Collectors.toSet());

        return longConfirmDOList.stream()
                .filter(e -> !empNoSet.contains(e.getEmployeeCode()))
                .collect(Collectors.toList());
    }

    /**
     * 数据分批入库
     */
    private void handleBatchInsert(List<SafetyBuildLongConfirmDO> finalList,
                                    Map<String, List<SafetyBuildPowerDO>> deviceDutyMap) {
        int batchSize = 1000;
        int total = finalList.size();
        int totalPages = (int) Math.ceil((double) total / batchSize);

        for (int i = 0; i < totalPages; i++) {
            int start = i * batchSize;
            int end = Math.min((i + 1) * batchSize, total);
            List<SafetyBuildLongConfirmDO> subList = finalList.subList(start, end);
            // 逐页处理的业务逻辑
            batchInsertConfirm(subList, deviceDutyMap);
        }
    }
    /**
     * 判断设备是否本部门，以及跨部门时的保密负责人
     * @param finalList 入参集合
     * @param deviceDutyMap 责任部门Map
     */
    public void batchInsertConfirm(List<SafetyBuildLongConfirmDO> finalList,Map<String,List<SafetyBuildPowerDO>> deviceDutyMap){
        try {
            //员工信息
            Map<String, SafetyEmployeeMessageDO> empMap = new HashMap<>();
            Map<String,BuildGroupDutyVO> deptDutyMap = new HashMap<>();
            List<String> empList = finalList.stream().map(SafetyBuildLongConfirmDO::getEmployeeCode).distinct().collect(Collectors.toList());
            List<SafetyEmployeeMessageDO> employeePowerDOS = safetyEmployeeMessageService.getListByStaffNo(empList);

            Map<String,List<SafetyBuildPowerDO>> dutyPowerMap;
            if (!CollectionUtils.isEmpty(employeePowerDOS)) {
                empMap = employeePowerDOS.stream().collect(Collectors.toMap(
                        SafetyEmployeeMessageDO::getEmpNo,
                        dto -> dto,
                        (oldValue, newValue) -> oldValue
                ));
                //获取员工组织对应的责任部门
                List<String> orgList = employeePowerDOS.stream().map(SafetyEmployeeMessageDO::getOrgCode).distinct().collect(Collectors.toList());
                List<BuildGroupDutyVO> buildPowerDOS = safetyBuildPowerService.getGroupDuty(orgList);
                if(!CollectionUtils.isEmpty(buildPowerDOS)){
                    deptDutyMap = buildPowerDOS.stream().collect(Collectors.toMap(
                            BuildGroupDutyVO::getCode,
                            dto -> dto,
                            (oldValue, newValue) -> oldValue
                    ));
                    List<String> dutyDeptCodeList = buildPowerDOS.stream().map(BuildGroupDutyVO::getDutyDeptCode).distinct().collect(Collectors.toList());
                    BuildPowerListParam listParam = new BuildPowerListParam();
                    listParam.setDutyDeptCodeList(dutyDeptCodeList);
                    List<SafetyBuildPowerDO> allPowerList = safetyBuildPowerService.selectList(listParam);
                    dutyPowerMap = allPowerList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));
                } else {
                    dutyPowerMap = new HashMap<>();
                }
            } else {
                dutyPowerMap = new HashMap<>();
            }
            Map<String, SafetyEmployeeMessageDO> finalEmpMap = empMap;
            Map<String, BuildGroupDutyVO> finalDeptDutyMap = deptDutyMap;
            finalList.forEach(e->{
                e.setCorresFlag("1");//是否本部门 1-是
                String empNo = e.getEmployeeCode();
                String deviceCode = e.getDeviceCode();
                SafetyEmployeeMessageDO emp = finalEmpMap.get(empNo);
                if(emp == null || StringUtils.isBlank(emp.getOrgCode())){
                    return;
                }
                String curdepetCode;
                //变更员工所在的责任部门
                BuildGroupDutyVO vo = finalDeptDutyMap.get(emp.getOrgCode());
                if(vo != null && StringUtils.isNotBlank(vo.getDutyDeptCode())){
                    curdepetCode = vo.getDutyDeptCode();
                } else {
                    curdepetCode = "";
                }
                if(StringUtils.isNotBlank(curdepetCode)){
                    //获取本部门责任部门的负责人信息
                    List<SafetyBuildPowerDO> buildPowerDOList = dutyPowerMap.get(vo.getDutyDeptCode());
                    if(!CollectionUtils.isEmpty(buildPowerDOList)){
                        e.setDutyEmpName(buildPowerDOList.stream().map(SafetyBuildPowerDO::getEmployeeName).distinct().collect(Collectors.joining(",")));
                        e.setDutyEmpNo(buildPowerDOList.stream().map(SafetyBuildPowerDO::getEmployeeCode).distinct().collect(Collectors.joining(",")));
                        e.setDutyDeptCode(vo.getDutyDeptCode());
                        e.setDutyDeptName(vo.getDutyDeptName());
                    }
                }
                //获取设备的所有责任部门信息
                List<SafetyBuildPowerDO> buildPowerList = deviceDutyMap.get(deviceCode);
                if(CollectionUtils.isEmpty(buildPowerList)){
                    return;
                }
                e.setCorresFlag("0");//是否本部门 0-否
                List subList = buildPowerList.stream().filter(p->curdepetCode.equals(p.getDutyDeptCode())).collect(Collectors.toList());
                boolean isCorresDept = false;
                if(!CollectionUtils.isEmpty(subList)){
                    isCorresDept = true;
                    e.setCorresFlag("1");//是否本部门 1-是
                }
                if (!isCorresDept) {
                    e.setCorresDutyDeptCode(buildPowerList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).distinct().collect(Collectors.joining(";")));
                    e.setCorresDutyEmpCode(buildPowerList.stream().map(SafetyBuildPowerDO::getEmployeeCode).distinct().collect(Collectors.joining(",")));
                }
            });

            safetyBuildLongConfirmService.insertBatch(finalList);
        }catch (Exception e){
            LogUtils.error("");
        }
    }

    public String batchSaveWhite(String idStr,String reason,String token) {
        Map<String, String> curUser = TokenUtils.getTockenUser(token);
        String realName = curUser.get("realName");
        String workNo = curUser.get("workNo");
        if(workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("1".equals(userType)){
            return "你无权限添加白名单，请联系门禁管理员！";
        }
        if(StringUtils.isBlank(reason)){
            reason = "长期未进出门禁添加白名单";
        }
        SafetyBuildWhiteListDO param = new SafetyBuildWhiteListDO();
        param.setIsDelete("0");
        param.setCreateTime(DateUtils.format(new Date(), 2));
        param.setUpdateTime(DateUtils.format(new Date(), 2));
        param.setSourceFrom("0");
        param.setCreateUserRole("2".equals(userType) ? "门禁管理员" : "保密负责人");
        param.setCreateUserCode(workNo);
        param.setCreateUserName(realName);
        param.setCreateReason(reason);
        //查找对应的长期为进出门禁信息
        List<SafetyBuildLongConfirmDO> longConfirmDOS = getListByIds(idStr);
        if (CollectionUtils.isEmpty(longConfirmDOS)) {
            return "未找到对应的长期未进出记录";
        }
        // 获取对应的员工号信息
        List<String> employeeCodes = longConfirmDOS.stream().map(SafetyBuildLongConfirmDO::getEmployeeCode).distinct().collect(Collectors.toList());

        SafetyBuildLongConfirmDO longConfirmDO = new SafetyBuildLongConfirmDO();
        longConfirmDO.setConfirmEmpNo(workNo);
        longConfirmDO.setConfirmEmpName(realName);
        longConfirmDO.setConfirmFlag("1"); //保留权限
        longConfirmDO.setUpdateTime(DateUtils.format(new Date(),2));
        // 根据员工号更新记录状态
        batchUpdateEmployeeCodes(employeeCodes, longConfirmDO);


        List<SafetyBuildWhiteListDO> insertList = fomatWhiteList(longConfirmDOS, param);

        safetyBuildWhiteListService.batchSave(insertList);
        return "操作成功";
    }

    public List<SafetyBuildLongConfirmDO> getListByIds(String idStr){
        Set<String> ids = Sets.newHashSet(idStr.split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        confirmListParam.setIdList(idList);
        confirmListParam.setPageFlag(false);
        confirmListParam.setConfirmFlag("0");
        List<SafetyBuildLongConfirmDO> longConfirmDOS = safetyBuildLongConfirmService.selectList(confirmListParam);
        return longConfirmDOS;
    }

    /**
     * 一键取消门禁权限
     * @param idStr
     * @param dealType 1-开通 0-关闭
     * @param token
     * @return
     */
    public String batchDealBuild(String idStr,int dealType,String token){
        Map<String, String> curUser = TokenUtils.getTockenUser(token);
        String realName = curUser.get("realName");
        String workNo = curUser.get("workNo");
        if(workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        List<SafetyBuildLongConfirmDO> longConfirmDOS = getListByIds(idStr);
        if(CollectionUtils.isEmpty(longConfirmDOS)){
            return "未找到对应的记录";
        }

        SafetyBuildLongConfirmDO longConfirmDO = new SafetyBuildLongConfirmDO();
        longConfirmDO.setConfirmEmpNo(workNo);
        longConfirmDO.setConfirmEmpName(realName);
        longConfirmDO.setConfirmFlag(dealType==1?"1":"2");
        longConfirmDO.setUpdateTime(DateUtils.format(new Date(),2));
        //更新记录状态
        batchUpdate(idStr,longConfirmDO);

        //只有关闭时才下发权限，保留权限时不用重新下发
        if(dealType == 0){
            List<SafetyBuildIssuedLogDO> issuedLogDOS = new ArrayList<>();
            longConfirmDOS.forEach(e->{
                SafetyBuildIssuedLogDO logDO = new SafetyBuildIssuedLogDO();
                BeanUtils.copyProperties(e,logDO);
                logDO.setRoleType(userType.split(":")[0]);
                logDO.setCreateBy(realName);
                logDO.setRemark("长期未进出关闭");
                logDO.setIssuedType("关闭");
                logDO.setSourceType("3"); //长期未进出
                issuedLogDOS.add(logDO);
            });
            safetyBuildProcessHandler.issuedPowerByListNew("2",issuedLogDOS);
        }
        return "操作成功";
    }

    public int batchUpdate(String idStr,SafetyBuildLongConfirmDO record){
        Set<String> ids = Sets.newHashSet(idStr.split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        confirmListParam.setIdList(idList);
        confirmListParam.setPageFlag(false);
        return safetyBuildLongConfirmService.updateByExample(confirmListParam,record);
    }
    public int batchUpdateById(List<Integer> idList,SafetyBuildLongConfirmDO record){
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        confirmListParam.setIdList(idList);
        confirmListParam.setPageFlag(false);
        return safetyBuildLongConfirmService.updateByExample(confirmListParam,record);
    }

    /**
     * 根据员工号修改
     * @param employeeCodes 员工号信息
     * @param record
     * @return
     */
    public int batchUpdateEmployeeCodes(List<String> employeeCodes,SafetyBuildLongConfirmDO record){
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        confirmListParam.setEmployeeCodeList(employeeCodes);
        confirmListParam.setPageFlag(false);
        return safetyBuildLongConfirmService.updateByExample(confirmListParam,record);
    }

    /**
     * 方法提取，根据长期未进出门禁，整理白名单对象集合
     * @param longConfirmDOS
     * @param param
     * @return
     */
    private List<SafetyBuildWhiteListDO> fomatWhiteList(List<SafetyBuildLongConfirmDO> longConfirmDOS,SafetyBuildWhiteListDO param) {
        List<SafetyBuildWhiteListDO> insertList = new ArrayList<>();
        Set<String> empSet = new HashSet<>();
        longConfirmDOS.forEach(e->{
            SafetyBuildWhiteListDO en = new SafetyBuildWhiteListDO();
            BeanUtils.copyProperties(param, en);
            en.setEmployeeCode(e.getEmployeeCode());
            en.setEmployeeName(e.getEmployeeName());
            insertList.add(en);
            empSet.add(e.getEmployeeCode());
        });
        List<String> empList = new ArrayList<>(empSet);
        //根据人员code查询人员职位和责任部门信息
        List<SafetyBuildPowerDTO> list = safetyBuildPowerService.getListWithPosByUserCode(empList);
        if (!CollectionUtils.isEmpty(list)) {
            Map<String, SafetyBuildPowerDTO> collect = list.stream().collect(Collectors.toMap(
                    SafetyBuildPowerDTO::getEmployeeCode,
                    e -> e,
                    (oldValue, newValue) -> newValue
            ));

            for (SafetyBuildWhiteListDO en : insertList) {
                String employeeCode = en.getEmployeeCode();
                SafetyBuildPowerDTO safetyBuildPowerDTO = collect.get(employeeCode);
                if (safetyBuildPowerDTO == null) {
                    continue;
                }
                en.setDutyDeptCode(safetyBuildPowerDTO.getDutyDeptCode());
                en.setDutyDeptName(safetyBuildPowerDTO.getDutyDeptName());
                en.setPositionCode(safetyBuildPowerDTO.getPositionCode());
                en.setPositionName(safetyBuildPowerDTO.getPositionName());
            }
        }
        return insertList;
    }


    /**
     * 整理待确认的部门变更人员信息，并发送代办
     *  1.修改 权限保留的，跨部门的，天数为 173,273,373,473...的确认状态为待确认
     *  2.查询需要发送代办的数据，条件 天数为173,273,373,473...权限为待确认的，跨部门的，或特殊要求的条件
     * @return
     */
    public Object sendOwnerLongToDo(){

        int cancelDays = 180;
        int maxLongDay = 1000;
        //获取配置参数-长期未进出天数
        SafetyBuildConfigDO buildConfigDO = safetyBuildConfigService.queryForFirst();
        if(buildConfigDO != null && buildConfigDO.getCancelAccessNum() != null){
            cancelDays = buildConfigDO.getCancelAccessNum();
        }
        //计算出需要提醒代办的diffDay
        List<Integer> diffDayList = new ArrayList<>();
        int curDay = Math.max(cancelDays - 7, 0); // 确保curDay不为负数
        while(curDay < maxLongDay){
            int curNum = curDay+7-cancelDays;
            if (curNum >= 0 && curNum % 100 == 0) {
                diffDayList.add(curDay);
            }
            curDay+=100;
        }
        BuildLongConfirmListParam queryParam = new BuildLongConfirmListParam();
        queryParam.setConfirmFlag("1");
        queryParam.setCorresFlag("0");
        queryParam.setDiffDayList(diffDayList);
        try {
            SafetyBuildLongConfirmDO newDO = new SafetyBuildLongConfirmDO();
            newDO.setConfirmFlag("0");
            safetyBuildLongConfirmService.updateByExample(queryParam, newDO);
        }catch (Exception e){
            LogUtils.error("更新长期未进出门禁的确认状态异常，{}",e);
        }

        queryParam.setConfirmFlag("0");
        queryParam.setPageFlag(false);
        List<SafetyBuildLongConfirmDO> list = safetyBuildLongConfirmService.selectList(queryParam);
        list.addAll(safetyBuildLongConfirmService.selectOtherList());
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        // 过滤白名单
        List<String> empNoList = list.stream().map(SafetyBuildLongConfirmDO::getEmployeeCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<SafetyBuildWhiteListDO> whiteListDOS = safetyBuildWhiteListService.selectByEmpCodeList(empNoList);
        if (!CollectionUtils.isEmpty(whiteListDOS)) {
            Set<String> empNoSet = whiteListDOS.stream().map(SafetyBuildWhiteListDO::getEmployeeCode).collect(Collectors.toSet());
            list = list.stream().filter(li -> StringUtils.isNotBlank(li.getEmployeeCode()) && !empNoSet.contains(li.getEmployeeCode())).collect(Collectors.toList());
        }

        //发送代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("BUILD_OWNER_LONG_TODO");
        if(systemParamDO != null && "1".equals(systemParamDO.getParamValue())){
            // 更新代办时间
            List<Integer> idList = list.stream().filter(e -> StringUtils.isNotBlank(e.getEmployeeCode())).map(SafetyBuildLongConfirmDO::getId).collect(Collectors.toList());
            BuildLongConfirmListParam param = new BuildLongConfirmListParam();
            param.setIdList(idList);
            SafetyBuildLongConfirmDO longConfirmDO = new SafetyBuildLongConfirmDO();
            longConfirmDO.setEipTodoDate(new Date());
            longConfirmDO.setEipTodoStatus(1);
            safetyBuildLongConfirmService.updateByExample(param, longConfirmDO);

            String empStr = list.stream().filter(e-> StringUtils.isNotBlank(e.getEmployeeCode()))
                    .map(SafetyBuildLongConfirmDO::getEmployeeCode).distinct().collect(Collectors.joining(","));
            Set<String> empSet = Sets.newHashSet(empStr.split(","));
            for(String empCode:empSet){
                if(StringUtils.isBlank(empCode)){
                    continue;
                }
                String modelId = "SAFETY-" + ownerFormId + "-" + empCode;
                try{
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                    eipCommonService.setEipToDoDone(eipToDoVO);
                }catch (Exception e){
                    LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empCode+":",e);
                }
                try {
                    String taragets = "[{\"PersonNo\":\"" + empCode + "\"}]";
                    String docCreator = "{\"PersonNo\":\"" + formEmpNo + "\"}";
                    String subject = "[安保管理平台]请确认长期未进出门禁权限";
                    String modelName = "请确认长期未进出门禁权限";
                    String link = anEipTodoUrl + ownerFormId + "?tab=1";
                    //others = isOpenInMobile 标识禁止移动端预览
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null,"isOpenInMobile");
                    eipCommonService.createEipToDo(eipToDoVO);
                }catch (Exception e){
                    LogUtils.error("【发送代办失败】请确认长期未进出门禁权限 to "+empCode+":",e);
                }
            }
        }
        return null;
    }

    /**
     * 员工超7天不确认非本部门管理的长期未进出门禁时，自动消除待办和取消待确认的门禁
     */
    public void closeOwnerLongToDo() {
        // 获取符合条件的数据
        String beginTime = DateUtils.format(DateUtils.addDays(new Date(), -7), 2);
        String endTime = DateUtils.format(DateUtils.addDays(new Date(), -9), 2);
        BuildLongConfirmListParam param = new BuildLongConfirmListParam();
        param.setConfirmFlag("0");
        param.setCorresFlag("0");
        param.setPageFlag(false);
        param.setLtEipTodoDate(beginTime);
        param.setGtEipTodoDate(endTime);
        param.setEipTodoDateNotNull(true);
        List<SafetyBuildLongConfirmDO> longConfirmDOList = safetyBuildLongConfirmService.selectList(param);
        if (CollectionUtils.isEmpty(longConfirmDOList)) {
            return;
        }

        // 取消代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode(Constants.BUILD_OWNER_LONG_TODO);
        if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
            Set<String> empSet = longConfirmDOList.stream().map(SafetyBuildLongConfirmDO::getEmployeeCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            for (String empCode : empSet) {
                if (StringUtils.isBlank(empCode)) {
                    continue;
                }
                String modelId = "SAFETY-" + ownerFormId + "-" + empCode;
                try {
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId, "", "", "", "", "", "", "", "");
                    eipCommonService.setEipToDoDone(eipToDoVO);
                } catch (Exception e) {
                    LogUtils.error("【消除代办异常】" + ",from " + formEmpNo + " to " + empCode + ":", e);
                }
            }
        }
    }

    public void delTodo(String token) {
        LogUtils.info("【长期未进出个人消除代办】---" + TokenUtils.getWorkNo(token));
        BuildLongConfirmListParam param = new BuildLongConfirmListParam();
        PageVO pageVO = this.selectOwnerList(token, param);
        LogUtils.info("【长期未进出个人消除代办】---代办数量" + pageVO.getTotal());
        if (pageVO.getTotal() != 0) {
            return;
        }
        // 取消代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode(Constants.BUILD_OWNER_LONG_TODO);
        if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
            String workNo = TokenUtils.getWorkNo(token);
            if (StringUtils.isBlank(workNo)) {
                return;
            }
            String modelId = "SAFETY-" + ownerFormId + "-" + workNo;
            try {
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, "", "", "", "", "", "", "", "");
                eipCommonService.setEipToDoDone(eipToDoVO);
            } catch (Exception e) {
                LogUtils.error("【消除代办异常】" + ",from " + formEmpNo + " to " + workNo + ":", e);
            }
        }
    }

    public VO delTodoOfWorkNo(String workNos) {
        if (StringUtils.isBlank(workNos)) {
            return VoUtil.success();
        }
        List<String> workNo = Arrays.asList(workNos.split(","));
        // 取消代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode(Constants.BUILD_OWNER_LONG_TODO);
        if (systemParamDO != null && Constants.STATUS_1.toString().equals(systemParamDO.getParamValue())) {
            workNo.forEach(li -> {
                String modelId = "SAFETY-" + ownerFormId + "-" + li;
                try {
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId, "", "", "", "", "", "", "", "");
                    eipCommonService.setEipToDoDone(eipToDoVO);
                } catch (Exception e) {
                    LogUtils.error("【消除代办异常】" + ",from " + formEmpNo + " to " + li + ":", e);
                }
            });
        }
        return VoUtil.success();
    }
}
