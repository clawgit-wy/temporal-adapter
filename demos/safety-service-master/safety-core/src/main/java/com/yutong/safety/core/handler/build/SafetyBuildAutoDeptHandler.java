package com.yutong.safety.core.handler.build;

import com.alibaba.fastjson.JSONArray;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptListParam;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptUpdateParam;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildAutoDeptVO;
import com.yutong.safety.core.db.entity.build.vo.BuildDeviceVO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.param.DepartChangeListParam;
import com.yutong.safety.core.db.entity.emp.vo.DepartChangeVO;
import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.common.SafetySendYxtHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.build.SafetyBuildAutoDeptService;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildInfoConfigService;
import com.yutong.safety.core.service.emp.SafetyDepartChangeListService;
import com.yutong.safety.core.service.enter.SafetyEmpCardService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class SafetyBuildAutoDeptHandler {

    @Resource
    private SafetyBuildAutoDeptService safetyBuildAutoDeptService;
    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;
    @Resource
    private SafetyDepartChangeListService safetyDepartChangeListService;
    @Resource
    private SafetyBuildInfoConfigService safetyBuildInfoConfigService;
    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private SafetySendYxtHandler safetySendYxtHandler;

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;
    @Autowired
    private SafetyEmpCardService safetyEmpCardService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;


    /**
     * 分页查询
     * @param param 查询参数
     * @return 结果集
     */
    public Object getPageList(BuildAutoDeptListParam param, String token){
        if(!StringUtils.isEmpty(param.getCreateTimeEnd())){
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getCreateTimeEnd(),1),1),1);
            param.setCreateTimeEnd(endDate);
        }
        param.setPageFlag(true);
        param.setAutoFlag("0"); //未禁用的
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userName = TokenUtils.getUserName(token);
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if(!"2".equals(userType)){
            param.setCreateEmployeeLogin(userName);
        }
        if(userType.startsWith("3")){
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        }else{
            param.setFilterDutyDept(null);
        }
        List<BuildAutoDeptVO> voList = safetyBuildAutoDeptService.selectList(param);
        long total = safetyBuildAutoDeptService.selectCount(param);
        return new PageVO<>(total, voList);
    }

    /**
     * 分页查询
     * @param param 查询参数
     * @return 结果集
     */
    public List<BuildAutoDeptVO> getList(BuildAutoDeptListParam param,String token){
        if(!StringUtils.isEmpty(param.getCreateTimeEnd())){
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getCreateTimeEnd(),1),1),1);
            param.setCreateTimeEnd(endDate);
        }
        param.setPageFlag(false);
        param.setAutoFlag("0"); //未禁用的
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userName = TokenUtils.getUserName(token);
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if(!"2".equals(userType)){
            param.setCreateEmployeeLogin(userName);
        }
        if(userType.startsWith("3")){
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        }else{
            param.setFilterDutyDept(null);
        }
        return  safetyBuildAutoDeptService.selectList(param);
    }

    public int update(BuildAutoDeptUpdateParam updateParam,String token){
        SafetyBuildAutoDeptDO autoDeptDO = new SafetyBuildAutoDeptDO();
        autoDeptDO.setId(updateParam.getId());
        autoDeptDO.setAutoFlag(updateParam.getAutoFlag());
        autoDeptDO.setUpdateTime(DateUtils.format(new Date(),2));
        autoDeptDO.setUpdateBy(TokenUtils.getRealName(token));
        return safetyBuildAutoDeptService.updateByPrimaryKeySelective(autoDeptDO);
    }

    /**
     * 对设备进行分类处理
     * @param map 自动授权
     * @param curValue 当前信息
     * @param key key
     * @param deviceType 设备类型
     */
    private void processDeviceType(Map<String, List<BuildAutoDeptVO>> map, Map<String, List<BuildAutoDeptVO>> curValue, String key, String deviceType) {
        List<BuildAutoDeptVO> curList = map.get(key);
        if (curList == null) {
            curList = new ArrayList<>();
        }
        List<BuildAutoDeptVO> curSub = curValue.get(deviceType);
        if (curSub != null) {
            curList.addAll(curSub);
            map.put(key, curList);
        }
    }


    private void formatIssueLogsForNew(List<SafetyBuildIssuedLogDO> logDOS,
                                       String curKey,List<BuildAutoDeptVO> value,
                                        String beginDate,List<SafetyDepartChangeListDO> curChangeList,
                                       Map<String,SafetyBuildDeviceDO> deviceMap){
        SafetyBuildIssuedLogDO commonLogDO = new SafetyBuildIssuedLogDO();
        commonLogDO.setSourceType("1"); //组织自动手枪
        commonLogDO.setBeginTime(beginDate);
        commonLogDO.setCreateBy("定时任务");
        commonLogDO.setIssuedType("开通"); //卡通业务
        commonLogDO.setDutyDeptCode(curKey); //责任部门
        commonLogDO.setDutyDeptName(value.get(0).getDutyDeptName());

        //责任部门下的变更人
        curChangeList.forEach(changeListDO->{
            commonLogDO.setEmployeeName(changeListDO.getEmployeeName());
            commonLogDO.setEmployeeCode(changeListDO.getEmployeeCode());

            //自动授权的门禁
            value.forEach(deptVO->{
                SafetyBuildIssuedLogDO curLogDO = new SafetyBuildIssuedLogDO();
                BeanUtils.copyProperties(commonLogDO,curLogDO);
                curLogDO.setDeviceCode(deptVO.getDeviceCode());
                curLogDO.setDeviceType(deptVO.getDeviceType());
                curLogDO.setDeviceName(deptVO.getDeviceName());
                curLogDO.setTargetId(deptVO.getProcessId());
                curLogDO.setBpmFlowId(deptVO.getBpmFlowId());
                curLogDO.setRemark(deptVO.getRemark());
                curLogDO.setDoorId(deptVO.getAnFactoryCode());
                curLogDO.setDoorName(deptVO.getAnFactoryName());
                curLogDO.setEndTime(deptVO.getEndTime());

                logDOS.add(curLogDO);

                //处理公共区域门禁
                String publicDevics = deptVO.getPublicDeviceCode();
                if(!StringUtils.isEmpty(publicDevics)){
                    String[] publicDeviceArr = deptVO.getPublicDeviceCode().split(",");
                    for (String curDnos : publicDeviceArr) {
                        SafetyBuildDeviceDO curPubDeviceDO = deviceMap.get(curDnos);
                        if (curPubDeviceDO == null) {
                            continue;
                        }
                        SafetyBuildIssuedLogDO publicIssuedLog = new SafetyBuildIssuedLogDO();
                        BeanUtils.copyProperties(curLogDO, publicIssuedLog);
                        publicIssuedLog.setDeviceCode(curDnos);
                        publicIssuedLog.setDeviceName(curPubDeviceDO.getAnDeviceName());
                        publicIssuedLog.setDoorId(curPubDeviceDO.getAnFactoryCode());
                        publicIssuedLog.setDoorName(curPubDeviceDO.getAnFactoryName());
                        publicIssuedLog.setDeviceType(curPubDeviceDO.getDeviceType());
                        logDOS.add(publicIssuedLog);
                    }
                }
            });
        });
    }

    private List<SafetyBuildIssuedLogDO> formatIssueLogsForEmpPower(List<SafetyBuildEmployeePowerDO> nowPowers,
                                                                    Map<String,SafetyBuildDeviceDO> deviceDOMap){
        List<SafetyBuildIssuedLogDO> nowList = new ArrayList<>();
        nowPowers.forEach(e->{
            SafetyBuildDeviceDO deviceDO = deviceDOMap.get(e.getDeviceCode());
            SafetyBuildIssuedLogDO curLog = new SafetyBuildIssuedLogDO();
            BeanUtils.copyProperties(e,curLog);
            if(deviceDO != null){
                curLog.setDeviceType(deviceDO.getDeviceType());
                curLog.setSourceType("2");
                curLog.setDeviceName(StringUtils.isEmpty(deviceDO.getAnDeviceName())?e.getDeviceName():deviceDO.getAnDeviceName());
                curLog.setIssuedType("开通"); //卡通业务
                curLog.setTargetId(e.getId());
                nowList.add(curLog);
            }
        });

        return nowList;
    }

    /**
     * 规范化日志类型
     * @param key key
     * @param value 自动授权记录
     * @param logDOS 日志集合
     * @param beginDate 开始时间
     * @param deviceType 设备类型
     * @param curChangeList 变更记录
     */
    private void formatIssueLogs(String key,List<BuildAutoDeptVO> value,List<SafetyBuildIssuedLogDO> logDOS,
                  String beginDate,String deviceType,List<SafetyDepartChangeListDO> curChangeList){
        String deviceCode = value.stream().map(SafetyBuildAutoDeptDO::getDeviceCode).distinct().collect(Collectors.joining(","));
        String deviceName = value.stream().map(SafetyBuildAutoDeptDO::getDeviceName).distinct().collect(Collectors.joining(","));
        String doorIds = value.stream().map(BuildAutoDeptVO::getAnFactoryCode).distinct().collect(Collectors.joining(","));
        String doorNames = value.stream().map(BuildAutoDeptVO::getAnFactoryName).distinct().collect(Collectors.joining(","));
        SafetyBuildIssuedLogDO commonLogDO = new SafetyBuildIssuedLogDO();
        commonLogDO.setDeviceType(deviceType);
        commonLogDO.setSourceType("1"); //组织自动手枪
        commonLogDO.setDeviceCode(deviceCode);
        commonLogDO.setDeviceName(deviceName);
        commonLogDO.setEndTime(value.get(0).getEndTime());
        commonLogDO.setBeginTime(beginDate);
        commonLogDO.setCreateBy("定时任务");
        commonLogDO.setIssuedType("开通"); //卡通业务
        commonLogDO.setDutyDeptCode(key); //责任部门
        commonLogDO.setDutyDeptName(value.get(0).getDutyDeptName());
        commonLogDO.setTargetId(value.get(0).getProcessId());
        commonLogDO.setBpmFlowId(value.get(0).getBpmFlowId());
        commonLogDO.setRemark(value.get(0).getRemark());

        for(SafetyDepartChangeListDO changeListDO:curChangeList){
            SafetyBuildIssuedLogDO curLogDO = new SafetyBuildIssuedLogDO();
            BeanUtils.copyProperties(commonLogDO,curLogDO);
            curLogDO.setEmployeeName(changeListDO.getEmployeeName());
            curLogDO.setEmployeeCode(changeListDO.getEmployeeCode());
            curLogDO.setDoorId(doorIds);
            curLogDO.setDoorName(doorNames);
            logDOS.add(curLogDO);
        }
    }

    /**
     * 根据指定日期更新 部门变更过的员工门禁权限-过滤掉新员工
     * @param beginDate 开始日期
     * @return 返回值
     */
    public Object updatePowerByDate(String beginDate){
        String curTime = DateUtils.format(new Date(),1);

        //1.获取指定日期以来的组织变更人员清单
        DepartChangeListParam listParam1 =  new DepartChangeListParam();
        listParam1.setUpdateTimeBegin(beginDate);
        listParam1.setEmployeeNoStart("01");
        listParam1.setChangeType("否"); //获取非离职人员的变动
        List<DepartChangeVO> changeList = safetyDepartChangeListService.selectChangEmpList(listParam1);
        if(CollectionUtils.isEmpty(changeList)){
            LogUtils.info("获取部门变更人员，条数为0，"+DateUtils.format(new Date(),2));
            return null;
        }

        //2.获取根据自动授权门禁的有效激励
        BuildAutoDeptListParam listParam = new BuildAutoDeptListParam();
        listParam.setPageFlag(false);
        listParam.setAutoFlag("0"); //查询未终止的
        listParam.setEndTimeStr(curTime); //结束时间还在有效期内 endTime>=curDay
        List<BuildAutoDeptVO> autoDeptDOS = safetyBuildAutoDeptService.selectList(listParam);
        if(CollectionUtils.isEmpty(autoDeptDOS)){
            LogUtils.info("获取自动授权组织为空，"+DateUtils.format(new Date(),2));
            return null;
        }
        //3.过滤掉未发卡且发脸的员工信息（排除掉新员工）
        List<DepartChangeVO> filterChangeList = filterDepartChange(changeList);
        if(CollectionUtils.isEmpty(filterChangeList)){
            LogUtils.info("未获取到符合逻辑的员工，原始数据changeList={}}", JSONArray.toJSONString(changeList));
            return null;
        }
        // 3.查询出自动授权任务中的公共门禁+现有权限设备信息
        String publicDevices = autoDeptDOS.stream().map(BuildAutoDeptVO::getPublicDeviceCode)
                .filter(publicDeviceCode ->!StringUtils.isEmpty(publicDeviceCode)).distinct().collect(Collectors.joining(","));
        Map<String,SafetyBuildDeviceDO> deviceMap;
        if(!StringUtils.isEmpty(publicDevices) ){
            Set<String> publicDeviceSet =  new HashSet<>(Arrays.asList(publicDevices.split(",")));
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectList(new ArrayList<>(publicDeviceSet));
            deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
        }else{
            deviceMap = new HashMap<>();
        }

        Map<String,List<SafetyDepartChangeListDO>> newDeptMap = filterChangeList.stream().collect(Collectors.groupingBy(SafetyDepartChangeListDO::getNewDeptCode));
        //按照责任部门分组要自动授权的门禁
        Map<String, List<BuildAutoDeptVO>> aotuDeptMap = autoDeptDOS.stream().collect(Collectors.groupingBy(BuildAutoDeptVO::getDutyDeptCode));

        //查询消息模板
        String personMsg = safetyBuildInfoConfigService.queryById(3);
        String managerMsg = safetyBuildInfoConfigService.queryById(4);

        String betinDate = DateUtils.format(new Date(),1);

        List<SafetyBuildIssuedLogDO> newLogDOS = new ArrayList<>();
        //迭代组织变更的人员责任部门
        newDeptMap.forEach((key, value) -> {
            //匹配该责任部门下是否有自动分配的门禁信息
            aotuDeptMap.entrySet().stream().filter(entry -> key.startsWith(entry.getKey())).forEach(entry -> {
                //整理需要授权的门禁信息
                formatIssueLogsForNew(newLogDOS, entry.getKey(), entry.getValue(), betinDate, value, deviceMap);
                //发送消息
                safetySendYxtHandler.sendYxtMsgByAutoDept(personMsg, managerMsg, entry.getValue(), value, entry.getKey());
            });
        });

        if(CollectionUtils.isEmpty(newLogDOS)){
            LogUtils.info("未匹配到可授权的门禁信息");
            return null;
        }
        Map<String,List<SafetyBuildIssuedLogDO>> logsMap = newLogDOS.stream().collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getDeviceType));
        //下发并保存日志
        if(!CollectionUtils.isEmpty(logsMap.get("1"))){
            try {
                safetyBuildProcessHandler.issuedBuildPowerForProcessForOne(logsMap.get("1"));
            }catch (Exception e){
                LogUtils.error("issuedBuildPowerForProcessForOne下发异常",e);
            }
        }
        //保存到门禁明细表中，并标记为未下发
        if(!CollectionUtils.isEmpty(logsMap.get("2"))){
            try {
                safetyBuildProcessHandler.issuedBuildPowerForProcessForTwoFs(logsMap.get("2"));
            }catch (Exception e){
                LogUtils.error("updatePowerByDate下发异常",e);
            }
        }
        try{
            //根据门禁判断是否需要下发大门权限-标记已下发
            safetyBuildProcessHandler.dealEmpDoorPower("1",newLogDOS,"0");
        }catch (Exception e){
            LogUtils.error("dealEmpDoorPower判断大门权限异常",e);
        }
        return null;
    }

    /**
     * 过滤掉新员工的自动授权逻辑（新员工的自动授权逻辑在员工发卡和发脸中实现）
     * @param changeList 组织变更员工集合
     * @return 过滤后的集合
     */
    private List<DepartChangeVO> filterDepartChange(List<DepartChangeVO> changeList){
        List<DepartChangeVO> filterList = new ArrayList<>();
        //1.过滤掉新部门为空的人员工信息
        List<DepartChangeVO> subList = changeList.stream().filter(e->!StringUtils.isEmpty(e.getNewDeptCode())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(subList)){
            LogUtils.info("获取部门变更人员,新组织不为空的条数为0，"+DateUtils.format(new Date(),2));
            return null;
        }
        //取出外协人员，判断是否有外协员工取车权限
        List<String> allEmpNos = subList.stream().map(DepartChangeVO::getEmployeeCode).distinct().collect(Collectors.toList());
        List<SafetyEmpCard> empCardList = safetyEmpCardService.selectEmpCardDatasByEmpList(allEmpNos);

        Map<String,DepartChangeVO> changeEmpMap = subList.stream().collect(Collectors.toMap(DepartChangeVO::getEmployeeCode, e -> e));
        
        //2.过滤掉没有粗入口信息的员工-标识未发卡和发脸
        if(!CollectionUtils.isEmpty(empCardList)){
            Set<String> autoEmpNos = new HashSet<>();

            for(SafetyEmpCard empCard:empCardList){
                String empNo = empCard.getEmpNo();
                String cardFlag = empCard.getCardStatus();
                DepartChangeVO curChange = changeEmpMap.get(empNo);
                String faceFlag = "0";
                if(curChange != null && !StringUtils.isEmpty(curChange.getFaceIssueFlag())){
                    faceFlag = curChange.getFaceIssueFlag();
                }
                if("1".equals(cardFlag) || "1".equals(faceFlag)){
                    autoEmpNos.add(empNo);
                }
            }
            filterList = subList.stream().filter(e->autoEmpNos.contains(e.getEmployeeCode())).collect(Collectors.toList());
        }

        return filterList;
    }

    /**
     * 组织变更消息发送
     * @param infoModelList
     * @param seInfoModelList
     * @param curValue
     * @param value
     */
    private void sendMessageForAutoDept(List<SafetyBuildInfoConfigDO> infoModelList,
                                        List<SafetyBuildInfoConfigDO> seInfoModelList,
                                        List<BuildAutoDeptVO> curValue,
                                        List<SafetyDepartChangeListDO> value){
        try {
            if(!CollectionUtils.isEmpty(infoModelList)){
                SafetyBuildInfoConfigDO info = infoModelList.get(0);

                StringBuilder deviceNameBuilder = new StringBuilder();
                Set<String> empCodeSet = new HashSet<>();
                Set<String> empNameSet = new HashSet<>();

                for (BuildAutoDeptVO buildAutoDeptVO : curValue) {
                    if (!StringUtils.isEmpty(buildAutoDeptVO.getDeviceName())) {
                        deviceNameBuilder.append(buildAutoDeptVO.getDeviceName()).append("、");
                    }
                    if (!StringUtils.isEmpty(buildAutoDeptVO.getCreateEmployeeNo())) {
                        empCodeSet.add(buildAutoDeptVO.getCreateEmployeeNo());
                    }
                    if (!StringUtils.isEmpty(buildAutoDeptVO.getCreateEmployeeName())) {
                        empNameSet.add(buildAutoDeptVO.getCreateEmployeeName());
                    }
                }
                StringBuilder deviceName = new StringBuilder(deviceNameBuilder.toString());
                if (deviceName.toString().endsWith("、")) {
                    deviceName = new StringBuilder(deviceName.substring(0, deviceName.length() - 1));
                }

                for (BuildAutoDeptVO buildAutoDeptVO : curValue) {
                    deviceName.append(buildAutoDeptVO.getDeviceName()).append("、");
                    empCodeSet.add(buildAutoDeptVO.getCreateEmployeeNo());
                    empNameSet.add(buildAutoDeptVO.getCreateEmployeeNo());
                }

                //消息体
                String mes = info.getInfoContent()
                        .replace("{grantBuild}", deviceName.toString())
                        .replace("{secretUser}", String.join("、",empNameSet));

                //获取变动的员工名
                for (SafetyDepartChangeListDO safetyDepartChangeListDO : value) {
                    String employeeCode = safetyDepartChangeListDO.getEmployeeCode();
                    String employeeName = safetyDepartChangeListDO.getEmployeeName();
                    RestTemplate restTemplate = new RestTemplate();
                    yxtRebotUtils.postForEntity(null,incomintRebotId,employeeCode,mes,restTemplate);

                    if(!CollectionUtils.isEmpty(seInfoModelList)){
                        SafetyBuildInfoConfigDO seInfo = seInfoModelList.get(0);
                        String secretMes = seInfo.getInfoContent()
                                .replace("{person}", employeeName)
                                .replace("{grantBuild}", deviceName.toString());
                        for (String s : empCodeSet) {
                            RestTemplate rt = new RestTemplate();
                            yxtRebotUtils.postForEntity(null,incomintRebotId,s,secretMes,restTemplate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.info("组织部门新进人员自动下发门禁，发消息的时候报错={}",e.getMessage());
        }
    }


    /**
     * 根据指定日期更新 部门变更过的员工门禁权限
     * @param beginDate 开始日期
     * @return 返回值
     */
    public Object updatePowerByDateNew(String beginDate){
        //1.获取根据自动授权门禁的有效激励
        BuildAutoDeptListParam listParam = new BuildAutoDeptListParam();
        listParam.setPageFlag(false);
        listParam.setAutoFlag("0"); //查询未终止的
        List<BuildAutoDeptVO> autoDeptDOS = safetyBuildAutoDeptService.selectList(listParam);
        if(CollectionUtils.isEmpty(autoDeptDOS)){
            LogUtils.info("获取自动授权组织为空，"+DateUtils.format(new Date(),2));
            return null;
        }
        //按照责任部门分组要自动授权的门禁
        Map<String, List<BuildAutoDeptVO>> aotuDeptMap = autoDeptDOS.stream().collect(Collectors.groupingBy(BuildAutoDeptVO::getDutyDeptCode));

        //2.获取指定日期以来的组织变更人员清单
        DepartChangeListParam listParam1 =  new DepartChangeListParam();
        listParam1.setUpdateTimeBegin(beginDate);
        listParam1.setEmployeeNoStart("01");
        listParam1.setChangeType("否"); //获取非离职人员的变动
        List<SafetyDepartChangeListDO> changeList = safetyDepartChangeListService.selectChangList(listParam1);
        if(CollectionUtils.isEmpty(changeList)){
            LogUtils.info("获取部门变更人员，条数为0，"+DateUtils.format(new Date(),2));
            return null;
        }
        //过滤掉新部门为空的人员工信息
        List<SafetyDepartChangeListDO> subList = changeList.stream().filter(e->!StringUtils.isEmpty(e.getNewDeptCode())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(subList)){
            LogUtils.info("获取部门变更人员,新组织不为空的条数为0，"+DateUtils.format(new Date(),2));
            return null;
        }

        Map<String,List<SafetyDepartChangeListDO>> newDeptMap = subList.stream().collect(Collectors.groupingBy(SafetyDepartChangeListDO::getNewDeptCode));

        List<String> changeEmpNos = subList.stream().map(SafetyDepartChangeListDO::getEmployeeCode).collect(Collectors.toList());
        // 3.查询出自动授权任务中的公共门禁+现有权限设备信息
        String publicDevices = autoDeptDOS.stream().map(BuildAutoDeptVO::getPublicDeviceCode)
                .filter(publicDeviceCode ->!StringUtils.isEmpty(publicDeviceCode)).distinct().collect(Collectors.joining(","));
        Map<String,SafetyBuildDeviceDO> deviceMap;
        if(!StringUtils.isEmpty(publicDevices) ){
            Set<String> publicDeviceSet =  new HashSet<>(Arrays.asList(publicDevices.split(",")));
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectList(new ArrayList<>(publicDeviceSet));
            deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
        }else{
            deviceMap = new HashMap<>();
        }
        //发消息
        SafetyBuildInfoConfigDO en = new SafetyBuildInfoConfigDO();
        en.setShowFlag("1");
        en.setId(3);
        //查询消息模板
        List<SafetyBuildInfoConfigDO> infoModelList = safetyBuildInfoConfigService.query(en);
        en.setId(4);
        List<SafetyBuildInfoConfigDO> seInfoModelList = safetyBuildInfoConfigService.query(en);

        String betinDate = DateUtils.format(new Date(),1);

        List<SafetyBuildIssuedLogDO> newLogDOS = new ArrayList<>();
        //迭代组织变更的人员责任部门
        newDeptMap.forEach((key,value)->{
            //匹配该责任部门下是否有自动分配的门禁信息
            aotuDeptMap.forEach((curKey,curValue)->{
                if(key.startsWith(curKey)){
                    //整理需要授权的门禁信息
                    formatIssueLogsForNew(newLogDOS,curKey,curValue,betinDate,value,deviceMap);
                    //发送消息
                    sendMessageForAutoDept(infoModelList,seInfoModelList,curValue,value);
                }
            });
        });

        if(CollectionUtils.isEmpty(newLogDOS)){
            LogUtils.info("未匹配到可授权的门禁信息");
            return null;
        }
        Map<String,List<SafetyBuildIssuedLogDO>> logsMap = newLogDOS.stream().collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getDeviceType));
        //下发并保存日志
        if(!CollectionUtils.isEmpty(logsMap.get("1"))){
            try {
                safetyBuildProcessHandler.issuedBuildPowerForProcessForOne(logsMap.get("1"));
            }catch (Exception e){
                LogUtils.error("issuedBuildPowerForProcessForOne下发异常",e);
            }
        }
        //保存到门禁明细表中，并标记为未下发
        if(!CollectionUtils.isEmpty(logsMap.get("2"))){
            try {
                safetyBuildProcessHandler.issuedBuildPowerForProcessForTwoFs(logsMap.get("2"));
            }catch (Exception e){
                LogUtils.error("updatePowerByDate下发异常",e);
            }
        }
        try{
            //根据门禁判断是否需要下发大门权限-标记已下发
            safetyBuildProcessHandler.dealEmpDoorPower("1",newLogDOS,"0");
        }catch (Exception e){
            LogUtils.error("dealEmpDoorPower判断大门权限异常",e);
        }
        return null;
    }


    /**
     * 初始化员工自动授权门禁信息
     * @param empNos 员工号集合
     */
    public void initAutoBuildForNewEmp(List<String> empNos){
        if(CollectionUtils.isEmpty(empNos)){
            return;
        }
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(empNos);
        if(CollectionUtils.isEmpty(employeeList)){
            return;
        }
        String curTime = DateUtils.format(new Date(),1);
        BuildAutoDeptListParam listParam = new BuildAutoDeptListParam();
        listParam.setPageFlag(false);
        listParam.setAutoFlag("0"); //查询未终止的
        listParam.setEndTimeStr(curTime); //结束时间还在有效期内 endTime>=curDay
        List<BuildAutoDeptVO> autoDeptDOS = safetyBuildAutoDeptService.selectList(listParam);
        if(CollectionUtils.isEmpty(autoDeptDOS)){
            return;
        }
        //按照责任部门分组要自动授权的门禁
        Map<String, List<BuildAutoDeptVO>> aotuDeptMap = autoDeptDOS.stream().collect(Collectors.groupingBy(BuildAutoDeptVO::getDutyDeptCode));

        // 3.查询出自动授权任务中的公共门禁+现有权限设备信息
        String publicDevices = autoDeptDOS.stream().map(BuildAutoDeptVO::getPublicDeviceCode)
                .filter(publicDeviceCode ->!StringUtils.isEmpty(publicDeviceCode)).distinct().collect(Collectors.joining(","));
        Map<String,BuildDeviceVO> deviceMap;
        if(!StringUtils.isEmpty(publicDevices) ){
            Set<String> publicDeviceSet =  new HashSet<>(Arrays.asList(publicDevices.split(",")));
            BuildDeviceListParam param = new BuildDeviceListParam();
            param.setDeviceCodeList(new ArrayList<>(publicDeviceSet));
            List<BuildDeviceVO> deviceDOS = safetyBuildDeviceService.selectList(param);
            deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
        }else{
            deviceMap = new HashMap<>();
        }
        initSubEmpPower(aotuDeptMap,employeeList,deviceMap);
    }

    /**
     * 整理员工自助授权逻辑
     * @param aotuDeptMap 组织授权组织
     * @param employeeList 员工
     * @param deviceMap 设备
     */
    public void initSubEmpPower(Map<String, List<BuildAutoDeptVO>> aotuDeptMap,
                                List<SafetyEmployeeMessageDO> employeeList,
                                Map<String,BuildDeviceVO> deviceMap){
        List<SafetyEmployeeMessageDO> wxEmpList = employeeList.stream().filter(e->"WX".equals(e.getUserType())).collect(Collectors.toList());
        List<SafetyEmployeeMessageDO> zsEmpList = employeeList.stream().filter(e->!"WX".equals(e.getUserType())).collect(Collectors.toList());

        //查询外协权限
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap = new HashMap<>();;
        if(!CollectionUtils.isEmpty(wxEmpList)){
            List<String> wxEmpNos = wxEmpList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(wxEmpNos)){
                List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNos);
                if(!CollectionUtils.isEmpty(outsiderList)){
                    outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
                }
            }
        }
        //查询正式工权限
        Map<String,List<SafetyParkEmployeePowerInfoDO>> parkPowerMap = new HashMap<>();;
        if(!CollectionUtils.isEmpty(zsEmpList)){
            List<String> zsEmpNos = zsEmpList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
            ParkEmployeePowerListParam param = new ParkEmployeePowerListParam();
            param.setEmpNoList(zsEmpNos);
            param.setPageFlag(false);
            param.setDeleteFlag("0");
            List<SafetyParkEmployeePowerInfoDO> parkPowerList = safetyParkEmployeePowerInfoService.selectList(param);
            if(!CollectionUtils.isEmpty(parkPowerList)){
                parkPowerMap = parkPowerList.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));
            }
        }
        List<SafetyOutsiderPromiseDTO> updateOursiderList;
        Map<String,SafetyOutsiderPromiseDTO> updateOutsicerMap = new HashMap<>();
        List<SafetyParkEmployeePowerInfoDO> updateParkList = new ArrayList<>();
        List<SafetyBuildEmployeePowerDO> updateBuildList = new ArrayList<>();
        for (Map.Entry<String, List<BuildAutoDeptVO>> entry : aotuDeptMap.entrySet())  {
            String deptCode = entry.getKey();
            List<BuildAutoDeptVO> value = entry.getValue();
            //外协员工循环处理权限
            if(!CollectionUtils.isEmpty(wxEmpList)){
                for(SafetyEmployeeMessageDO curEmp:wxEmpList){
                    SafetyOutsiderPromiseDTO curDTO = outPowerMap.get(curEmp.getEmpNo());
                    if(curDTO==null || StringUtils.isEmpty(curEmp.getOrgCodePath())
                            || !curEmp.getOrgCodePath().contains(deptCode)){
                        continue;
                    }
                    //外协权限整理
                    initSubEmpPower_wx(updateOutsicerMap,updateBuildList,curDTO,value,deviceMap,curEmp);
                }
            }
            //正式员工循环处理权限
            if(!CollectionUtils.isEmpty(zsEmpList)){
                for(SafetyEmployeeMessageDO curEmp:zsEmpList){
                    if(StringUtils.isEmpty(curEmp.getOrgCodePath()) || !curEmp.getOrgCodePath().contains(deptCode)){
                        continue;
                    }
                    List<SafetyParkEmployeePowerInfoDO> curParkList = parkPowerMap.get(curEmp.getEmpNo());
                    initSubEmpPower_zs(updateParkList,updateBuildList,curParkList,value,deviceMap,curEmp);
                }
            }
        }
        if(!updateOutsicerMap.isEmpty()){
            updateOursiderList = Collections.unmodifiableList(
                    new ArrayList<>(updateOutsicerMap.values())
            );
            safetyOutsiderPromiseChangeService.saveOrUpdate(updateOursiderList);
            //回写EHR
            try {
                safetyBaseHandler.sendToEHRForFactory(updateOursiderList);
            }catch (Exception e){
                LogUtils.error("回写EHR异常",e);
            }
        }
        if(!CollectionUtils.isEmpty(updateParkList)){
            safetyParkPowerProcessService.dealPower(updateParkList,"ZS");
        }
        if(!CollectionUtils.isEmpty(updateBuildList)){
            safetyBuildEmployeePowerService.insertBatch(updateBuildList);
        }
    }

    /**
     * 处理外协员工大门楼宇权限
     * @param updateOutsiderMap 外协
     * @param updateBuildList 楼宇
     * @param curDTO 当前员工
     * @param autoDeptVOS 自动组织授权门禁
     * @param deviceMap 公共区域门禁
     */
    public void initSubEmpPower_wx(Map<String,SafetyOutsiderPromiseDTO> updateOutsiderMap,
                                   List<SafetyBuildEmployeePowerDO> updateBuildList,
                                   SafetyOutsiderPromiseDTO curDTO,
                                   List<BuildAutoDeptVO> autoDeptVOS,
                                   Map<String,BuildDeviceVO> deviceMap,
                                   SafetyEmployeeMessageDO employee){
        Set<String> newFactorySet = new HashSet<>();
        String beginTime = DateUtils.format(new Date(),1);
        for(BuildAutoDeptVO autoDeptVO:autoDeptVOS){
            String endTime = autoDeptVO.getEndTime();
            String processId = autoDeptVO.getProcessId();
            String createBy = autoDeptVO.getCreateEmployeeName();
            String anFactoryCode = autoDeptVO.getAnFactoryCode();
            newFactorySet.add(anFactoryCode);
            //非公共楼宇
            SafetyBuildEmployeePowerDO employeePowerDO = formatBuildForAuto(autoDeptVO,employee,beginTime);
            updateBuildList.add(employeePowerDO);
            String publicDevice = autoDeptVO.getPublicDeviceCode();
            if(!StringUtils.isEmpty(publicDevice)){
                //公共楼宇
                String[] publicDeviceArr = publicDevice.split(",");
                for(String curPublic:publicDeviceArr){
                    BuildDeviceVO deviceDO = deviceMap.get(curPublic);
                    if(deviceDO == null){
                        break;
                    }
                    newFactorySet.add(deviceDO.getAnFactoryCode());
                    SafetyBuildEmployeePowerDO pulicPowerDO = formatBuildForPublic(deviceDO,employee,beginTime,endTime,processId,createBy);
                    updateBuildList.add(pulicPowerDO);
                }
            }
        }
        if(CollectionUtils.isEmpty(newFactorySet)){
           return;
        }
        String curFactory = curDTO.getFactory();
        if(!StringUtils.isEmpty(curFactory)){
            //TODO 注释不能放后面
            Set<String> oldFactorySet = Arrays.stream(curFactory.split(","))
                    .map(String::trim)          // 移除每个元素首尾空格
                    .filter(s -> !s.isEmpty())   // 过滤空字符串
                    .collect(Collectors.toCollection(LinkedHashSet::new));  // 保留原始顺序
            newFactorySet.addAll(oldFactorySet);
        }
        String result = String.join(",", newFactorySet);
        curDTO.setFactory(result);
        updateOutsiderMap.put(employee.getEmpNo(), curDTO);
    }

    /**
     * 处理外协员工大门楼宇权限
     * @param updateParkList 外协
     * @param updateBuildList 楼宇
     * @param curParkList 当前员工
     * @param autoDeptVOS 自动组织授权门禁
     * @param deviceMap 公共区域门禁
     */
    public void initSubEmpPower_zs(List<SafetyParkEmployeePowerInfoDO> updateParkList,
                                   List<SafetyBuildEmployeePowerDO> updateBuildList,
                                   List<SafetyParkEmployeePowerInfoDO> curParkList,
                                   List<BuildAutoDeptVO> autoDeptVOS,
                                   Map<String,BuildDeviceVO> deviceMap,
                                   SafetyEmployeeMessageDO employee){
        Set<String> newFactorySet = new HashSet<>();
        String beginTime = DateUtils.format(new Date(),1);
        for(BuildAutoDeptVO autoDeptVO:autoDeptVOS){
            String endTime = autoDeptVO.getEndTime();
            String processId = autoDeptVO.getProcessId();
            String createBy = autoDeptVO.getCreateEmployeeName();
            String anFactoryCode = autoDeptVO.getAnFactoryCode();
            newFactorySet.add(anFactoryCode);
            //非公共楼宇
            SafetyBuildEmployeePowerDO employeePowerDO = formatBuildForAuto(autoDeptVO,employee,beginTime);
            updateBuildList.add(employeePowerDO);
            String publicDevice = autoDeptVO.getPublicDeviceCode();
            if(!StringUtils.isEmpty(publicDevice)){
                //公共楼宇
                String[] publicDeviceArr = publicDevice.split(",");
                for(String curPublic:publicDeviceArr){
                    BuildDeviceVO deviceDO = deviceMap.get(curPublic);
                    if(deviceDO == null){
                        break;
                    }
                    newFactorySet.add(deviceDO.getAnFactoryCode());
                    SafetyBuildEmployeePowerDO pulicPowerDO = formatBuildForPublic(deviceDO,employee,beginTime,endTime,processId,createBy);
                    updateBuildList.add(pulicPowerDO);
                }
            }
        }
        if(CollectionUtils.isEmpty(newFactorySet)){
            return;
        }
        if(!CollectionUtils.isEmpty(curParkList)){
            Set<String> oldFactorySet = curParkList.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).collect(Collectors.toSet());
            newFactorySet.removeAll(oldFactorySet);
        }
        if(CollectionUtils.isEmpty(newFactorySet)){
            return;
        }
        AtomicReference<String> maxEndTime = new AtomicReference<>("2099-12-30");
        Optional<BuildAutoDeptVO> doMaxAuto = autoDeptVOS.stream().max(Comparator.comparing(BuildAutoDeptVO::getEndTime));
        doMaxAuto.ifPresent(build -> {
            maxEndTime.set(build.getEndTime());
        });
        for(String s:newFactorySet){
            SafetyParkEmployeePowerInfoDO parkPowerDO = formatParkPower(employee,s,beginTime, maxEndTime.get());
            updateParkList.add(parkPowerDO);
        }
    }

    /**
     * 整理员工厂区权限
     * @param employee 员工号
     * @param facCode 厂区编码
     * @param beginTime 开始时间
     * @param endTime 结束时间
     */
    public SafetyParkEmployeePowerInfoDO formatParkPower(SafetyEmployeeMessageDO employee,
                                                         String facCode,String beginTime,
                                                         String endTime){
        SafetyParkEmployeePowerInfoDO parkPowerDO = new SafetyParkEmployeePowerInfoDO();
        parkPowerDO.setEmployeeCode(employee.getEmpNo());
        parkPowerDO.setFactoryId(facCode);
        parkPowerDO.setPowerBeginTime(beginTime);
        parkPowerDO.setPowerEndTime(endTime);
        parkPowerDO.setIsDelete(Short.valueOf("0"));
        parkPowerDO.setDownMark("0"); //无需单独下载
        return parkPowerDO;
    }

    /**
     * 整理非公共楼宇设备权限
     * @param autoDeptVO 组织自动授权
     * @param employee 员工号
     * @param beginTime 开始时间
     */
    public SafetyBuildEmployeePowerDO formatBuildForAuto(BuildAutoDeptVO autoDeptVO,
                                                      SafetyEmployeeMessageDO employee,
                                                      String beginTime){
        SafetyBuildEmployeePowerDO employeePowerDO = new SafetyBuildEmployeePowerDO();
        employeePowerDO.setEmployeeCode(employee.getEmpNo());
        employeePowerDO.setEmployeeName(employee.getEmpName());
        employeePowerDO.setDeviceCode(autoDeptVO.getDeviceCode());
        employeePowerDO.setDeviceName(autoDeptVO.getDeviceName());
        employeePowerDO.setBeginTime(beginTime);
        employeePowerDO.setEndTime(autoDeptVO.getEndTime().substring(0,10));
        employeePowerDO.setDeleteFlag("0");
        employeePowerDO.setDeviceType(autoDeptVO.getDeviceType());
        employeePowerDO.setRemark(autoDeptVO.getRemark()==null?"":autoDeptVO.getRemark());
        employeePowerDO.setProcessId(autoDeptVO.getProcessId());
        employeePowerDO.setDutyEmpName(autoDeptVO.getCreateEmployeeName());
        employeePowerDO.setSourceType("2");
        return employeePowerDO;
    }

    /**
     * 整理公共区域楼宇设备权限
     * @param deviceDO 设备
     * @param employee 员工号
     * @param beginTime 开始时间
     */
    public SafetyBuildEmployeePowerDO formatBuildForPublic(BuildDeviceVO deviceDO,
                                                         SafetyEmployeeMessageDO employee,
                                                         String beginTime, String endTime,
                                                           String processId,String createBy){
        SafetyBuildEmployeePowerDO employeePowerDO = new SafetyBuildEmployeePowerDO();
        employeePowerDO.setEmployeeCode(employee.getEmpNo());
        employeePowerDO.setEmployeeName(employee.getEmpName());
        employeePowerDO.setDeviceCode(deviceDO.getDeviceCode());
        employeePowerDO.setDeviceName(deviceDO.getDeviceName());
        employeePowerDO.setBeginTime(beginTime);
        employeePowerDO.setEndTime(endTime.substring(0,10));
        employeePowerDO.setDeleteFlag("0");
        employeePowerDO.setDeviceType(deviceDO.getDeviceType());
        employeePowerDO.setProcessId(processId);
        employeePowerDO.setDutyEmpName(createBy);
        employeePowerDO.setSourceType("2");
        employeePowerDO.setRemark("公共区域授权");
        return employeePowerDO;
    }
}
