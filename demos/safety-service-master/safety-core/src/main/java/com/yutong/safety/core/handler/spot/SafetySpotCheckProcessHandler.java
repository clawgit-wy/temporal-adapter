package com.yutong.safety.core.handler.spot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmAuditOptionVO;
import com.yutong.safety.common.vo.BpmWorkitemVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckResultDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotLinkRecordRuleDO;
import com.yutong.safety.core.dto.SafetySpotCheckProcessDTO;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.spot.SafetySpotCheckDeviceInfoService;
import com.yutong.safety.core.service.spot.SafetySpotCheckProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lukunpeng
 * @time 2024-5-15
 */
@Component
public class SafetySpotCheckProcessHandler {
    @Autowired
    private SafetySpotCheckProcessService safetySpotCheckProcessService;
    @Autowired
    private SafetyBpmProcessService safetyBpmProcessService;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private MailCenterUtils mailCenterUtils;
    @Autowired
    private SafetySpotCheckDeviceInfoService safetySpotCheckDeviceInfoService;
    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Value("${iam.retention.robotId}")
    private String robotId;
    @Value("${bpm.spot.todo}")
    private String todoPre;
    @Value("${im.sendMsg.postUrl}")
    private String sendMsgImUrl;
    @Value("${iam.emplyee.byWorkNo.url}")
    private String iamEmployForWorkNoUrl;

    private static String SPOT_CHECK_FORMID = "safety_spot_check_process";

    public Object findForAdd(String deviceId, String username) {
        return JSON.toJSON(safetySpotCheckProcessService.findForAdd(deviceId, username));
    }

    /**
     * 创建流程
     * @param processDTO 入参
     * @return 返回值
     */
    public Object createFlow(SafetySpotCheckProcessDTO processDTO) {
        List<String> recordIds = findIds(processDTO.getDeviceId());

        PreconditonsEx.checkArgument(recordIds!=null && recordIds.size() > 0, ResponseEnum.FAIL.getCode(), "未生成应点检信息!");

        String username = processDTO.getCreator();
        //触发流程
        String flowId = safetyBpmProcessService.createAndApproveProcessByUserName(processDTO.getProcessParam().getSubject(),  processDTO.getProcessParam().getModelId(), processDTO.getProcessParam().getFormId(), username,null);
        //保存safety_spot_check_process
        processDTO.setFlowId(flowId.split(",")[1]);
        //更新表safety_spot_check_record 根据设备ID和taskId查询当前需要巡检的设备及任务
        //更新多条record数据
        SafetySpotCheckRecordDO recordDO = new SafetySpotCheckRecordDO();
        recordDO.setCheckEmp(processDTO.getCheckName());
        recordDO.setCheckEmpNo(processDTO.getCheckEmpNo());
        recordDO.setUpdateEmp(processDTO.getCheckName());
        recordDO.setUpdateEmpNo(processDTO.getCheckEmpNo());
        recordDO.setCheckTime(DateUtils.parse(processDTO.getCheckTime(), 2));
        recordDO.setStatus(1);
        //处理record状态
        getRecordData(processDTO, recordDO);
        safetySpotCheckProcessService.updateRecord(recordDO, recordIds, flowId.split(",")[1]);
        int needReform = 1;
        if(recordDO.getIsReform() != null && recordDO.getIsReform() ==0){
            needReform = 0;
        }
        processDTO.setNeedReform(needReform);

        //设置设备预设点检人信息
        String deviceId = processDTO.getDeviceId();
        String checkEmpNo = "";
        if(StringUtils.isNotEmpty(deviceId)){
            SafetySpotCheckDeviceInfoDO entity = safetySpotCheckDeviceInfoService.getById(Integer.parseInt(deviceId));
            if(!ObjectUtils.isEmpty(entity)){
                checkEmpNo = entity.getCheckEmpNo();
                processDTO.setDeviceCheckNo(checkEmpNo);
                processDTO.setDeviceCheckName(entity.getCheckEmp());
            }
        }

        if(StringUtils.isNotEmpty(checkEmpNo)){
            //临时点检人是否是多人
            String[] split = checkEmpNo.split(",");

            String planLoginName = "";

            for (String s : split) {
                String loginNameByNo = safetyEmployeeMessageService.getLoginNameByNo(s);
                planLoginName += StringUtils.isEmpty(loginNameByNo) ? "" : loginNameByNo + ",";
            }
            processDTO.setDeviceCheckLogin(planLoginName);
        }

        safetySpotCheckProcessService.save(processDTO);

        //根据record_id ,rule_id ,device_id 更新表safety_spot_link_record_rule
        String recordRuleDOSStr = processDTO.getRecordRuleDOS();
        List<SafetySpotLinkRecordRuleDO> recordRuleDOS;
        if(StringUtils.isNotBlank(recordRuleDOSStr)){
            recordRuleDOS = JSONArray.parseArray(recordRuleDOSStr,SafetySpotLinkRecordRuleDO.class);
            safetySpotCheckProcessService.updateLinkRecordRule(recordRuleDOS, recordIds);
        }
        return flowId;
    }

    /**
     * 整合record数据
     * @param processDTO 流程信息
     * @return 返回值
     */
    private SafetySpotCheckRecordDO getRecordData(SafetySpotCheckProcessDTO processDTO, SafetySpotCheckRecordDO recordDO){
        String recordRuleDOSStr = processDTO.getRecordRuleDOS();
        List<SafetySpotLinkRecordRuleDO> recordRuleDOS = new ArrayList<>();
        if(StringUtils.isNotBlank(recordRuleDOSStr)){
            recordRuleDOS = JSONArray.parseArray(recordRuleDOSStr,SafetySpotLinkRecordRuleDO.class);
        }
        int deviceStatus = 0;  //有一项异常,即为异常
        String checkImgs = "";  //所有异常图片拼接
        String exceptionDesc = "";  //所有问题描述拼接
        Integer isReform = null;  //默认已整改 有异常且非当场整改为待整改
        Integer validateStatus = null; //设置未待验证状态，
        String reformImgs = "";

        for (SafetySpotLinkRecordRuleDO recordRuleDO:recordRuleDOS) {
            if(recordRuleDO.getIsException() == 1){
                deviceStatus = 1;  //设备状态0-正常 1-异常
                if(recordRuleDO.getIsLocal() == 0){
                    isReform = 0; //待整改 有异常但是非当场整改
                    validateStatus = 0; //待验证，有异常但非当场整改
                }else if(isReform == null){
                    isReform = 1;   //默认已整改
                }
            }
            //添加当场整改图片
            if(StringUtils.isNotBlank(recordRuleDO.getFinishUrl())){
                reformImgs = Joiner.on(",").join(reformImgs,recordRuleDO.getFinishUrl());
                if(reformImgs.indexOf(",")==0){
                    reformImgs = reformImgs.substring(1);
                }
            }
            if(StringUtils.isNotEmpty(recordRuleDO.getImgUrl())){
                checkImgs = Joiner.on(",").join(checkImgs,recordRuleDO.getImgUrl());
                if(checkImgs.indexOf(",")==0){
                    checkImgs = checkImgs.substring(1);
                }
            }
            if(StringUtils.isNotEmpty(recordRuleDO.getExcepDesc())){
                exceptionDesc = Joiner.on(";").join(exceptionDesc,recordRuleDO.getRuleName()+":"+recordRuleDO.getExcepDesc());
                if(exceptionDesc.indexOf(";")==0){
                    exceptionDesc = exceptionDesc.substring(1);
                }
            }
        }
        if(deviceStatus == 0){
            recordDO.setIsReform(null);
            recordDO.setValidateStatus(null);
        }else{
            recordDO.setIsReform(isReform);
            recordDO.setValidateStatus(validateStatus);
        }
        recordDO.setDeviceStatus(deviceStatus);
        recordDO.setCheckImg(checkImgs);
        recordDO.setReformImg(reformImgs); //整改照片
        recordDO.setExceptionDesc(exceptionDesc);

        return recordDO;
    }
    /**
     * 根据设备ID获取所有任务ID用于更新 获取所有符合规则的safety_spot_check_record表ID,根据safety_spot_check_record表ID更新
     */
    private List<String> findIds(String deviceId){
        return safetySpotCheckProcessService.findIds(deviceId);
    }
    private List<String> findIdsAudit(String flowId){
        return safetySpotCheckProcessService.findIdsAudit(flowId);
    }

    /**
     * 查询详情
     * @param flowId 流程ID
     * @return 返回值
     */
    public Object view(String flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("Flow ID cannot be null");
        }
        return JSON.toJSON(safetySpotCheckProcessService.view(flowId));
    }

    /**
     * 审批
     * @param processDTO 流程相关信息
     * @return 返回值
     */
    public Object audit(SafetySpotCheckProcessDTO processDTO) {
        List<String> recordIds = findIdsAudit(processDTO.getFlowId());
        String recordRuleDOSStr = processDTO.getRecordRuleDOS();
        if(StringUtils.isNotBlank(recordRuleDOSStr)){
            List<SafetySpotLinkRecordRuleDO> recordRuleDOS = JSONArray.parseArray(recordRuleDOSStr,SafetySpotLinkRecordRuleDO.class);
            safetySpotCheckProcessService.updateLinkRecordRule(recordRuleDOS, recordIds);
        }
        String resultDTOSStr = processDTO.getResultDTOS();
        SafetySpotCheckProcessDTO view = null;
        if(StringUtils.isNotBlank(resultDTOSStr)){
            view = safetySpotCheckProcessService.view(processDTO.getFlowId());
            List<SafetySpotCheckResultDO> resultDTOS = JSONArray.parseArray(resultDTOSStr, SafetySpotCheckResultDO.class);
            SafetySpotCheckResultDO resultDO = resultDTOS.get(0);
            resultDO.setReformFinish("1");
            resultDO.setFinishTime(new Date());
            resultDO.setReformTime(new Date());
            //判断有整改责任人上级, 已延期
            if(StringUtils.isNotEmpty(view.getReformUserLeader())){
                resultDO.setReformDelay("1");
                resultDO.setDelayTime(DateUtils.parse(view.getReformTime(),1));
                Map<String,String> curUser = safetySpotCheckProcessService.getEmpInfoByUsername(view.getReformUserLeader());
                if(curUser != null){
                    resultDO.setReformLeader(curUser.get("empName"));
                }
            }
            Map<String,String> curEmp = safetySpotCheckProcessService.getEmpInfoByUsername(processDTO.getLoginName());
            if(curEmp != null){
                resultDO.setReformName(curEmp.get("empName"));
            }
            resultDO.setAuditNoteMsg(StringUtils.isEmpty(processDTO.getAuditNoteMsg()) ? "同意" : processDTO.getAuditNoteMsg());
            safetySpotCheckProcessService.saveResult(resultDO, recordIds);
        }
        //整改节点审批通过后 进入待验证 更新整改状态为已整改
        if("3".equals(processDTO.getNodeFlag())){
            //获取最新的设备点检人作为验证人
            if(view != null && view.getDeviceId() != null){
                Map<String,String> checkPlanEmp = safetySpotCheckDeviceInfoService.getPlanCheckEmp(view.getDeviceId(),null);
                processDTO.setPlanValidateEmp(checkPlanEmp.get("planCheckEmp"));
                processDTO.setPlanValidateEmpNo(checkPlanEmp.get("planCheckEmpNo"));
                processDTO.setPlanValidateEmpLogin(checkPlanEmp.get("planCheckEmpLogin"));

                view.setPlanValidateEmp(checkPlanEmp.get("planCheckEmp"));
                view.setPlanValidateEmpNo(checkPlanEmp.get("planCheckEmpNo"));
                view.setPlanValidateEmpLogin(checkPlanEmp.get("planCheckEmpLogin"));

                //发送验证信息
                sendValidateTips(view);
            }
            //更新record
            safetySpotCheckProcessService.updateRecordStatus(processDTO.getFlowId(), null, "reform");

        }
        //填写整改完成时间 更新safety_spot_check_record 计划整改时间及整改责任人 整改责任人上级
        if("2".equals(processDTO.getNodeFlag())){
            //根据登录名取整改责任人员工信息
            Map<String, String> reformUser = safetySpotCheckProcessService.getEmpInfoByUsername(processDTO.getReformUser());
            if(reformUser != null){
                reformUser.put("reformTime", processDTO.getReformTime());
            }
            safetySpotCheckProcessService.updateRecordStatus(processDTO.getFlowId(), reformUser, "reformTwo");
        }
        //验证通过 修改验证相关信息 验证人 验证结果 验证时间
        if("1".equals(processDTO.getCheckResult())){
            String loginName = processDTO.getLoginName();
            Map<String, String> user = safetySpotCheckProcessService.getEmpInfoByUsername(loginName);
            safetySpotCheckProcessService.updateRecordStatus(processDTO.getFlowId(), user, "validate");

            //更新详情页面的验证状态
            safetySpotCheckProcessService.updateValidDetailU("1", processDTO.getFlowId(),processDTO.getValidRemark(),user);
        }
        //验证不合格
        if("4".equals(processDTO.getCheckResult())){
            String loginName = processDTO.getLoginName();
            Map<String, String> user = safetySpotCheckProcessService.getEmpInfoByUsername(loginName);
            safetySpotCheckProcessService.updateRecordStatus(processDTO.getFlowId(), user, "validateBelow");

            //更新详情页面的验证状态
            safetySpotCheckProcessService.updateValidDetailU("4", processDTO.getFlowId(),processDTO.getValidRemark(),user);
        }
        //更新流程基本信息
        safetySpotCheckProcessService.updateProcessData(processDTO);
        //处理流程
        return safetyBpmProcessService.approveProcessByTaskId(processDTO.getId(),
                processDTO.getFlowId(),
                processDTO.getProcessParam().getModelId(),
                processDTO.getProcessParam().getFormId(),
                processDTO.getLoginName(),
                processDTO.getAuditNoteMsg());
    }

    /**
     * 延期整改
     * @param flowId 流程ID
     * @param delayTime 延期时间
     * @param leaderUsername 直系领导
     * @return 返回值
     */
    public Object delay(String flowId, String delayTime, String leaderUsername) {
        SafetySpotCheckProcessDTO processDTO = safetySpotCheckProcessService.view(flowId);
        safetySpotCheckProcessService.delay(flowId, delayTime,processDTO);
        if(StringUtils.isNotEmpty(leaderUsername)){
            //给整改责任人上级发通知
            //【公共服务设备设施点检】设备整改流程主题，设备整改人张瑶延期整改，将计划整改完成时间由2024/05/26调整到2024/06/07，请知悉。流程链接：XXX
            Map<String, String> user = safetySpotCheckProcessService.getEmpInfoByUsername(leaderUsername);
            String reformUserName = "";
            Map<String,String> reformUser = safetySpotCheckProcessService.getEmpInfoByUsername(processDTO.getReformUser());
            if(reformUser != null){
                reformUserName = reformUser.get("empName");
            }
            String flowUrl = Joiner.on("").join(
                    todoPre,
                    "&formInstanceId=",
                    processDTO.getId(),
                    "&processId=",
                    processDTO.getFlowId()
            );
            String msg = Joiner.on("").join("【公共服务设备设施点检】",
                    processDTO.getTitle(),
                    "，设备整改人",
                    reformUserName,
                    "延期整改，将计划整改完成时间由",
                    processDTO.getReformTime(),
                    "调整到",
                    delayTime,
                    "，请知悉。流程链接：",
                    flowUrl
                    );

            Map<String, String> params = new HashMap<>();
            params.put("appId", "1");
            params.put("sessionType", "r");
            params.put("contentType", "0");
            params.put("content", msg);
            params.put("from", robotId);
            //判断是否外协 5/6开头的员工号 需要转换为 01开头
            String userNo = user.get("empNo");
            if(StringUtils.isNotEmpty(userNo) && (userNo.startsWith("5") || userNo.startsWith("6"))){
                userNo = safetySpotCheckProcessService.getOldEmpNo(userNo);
            }
            params.put("to", userNo);
            LogUtils.info("发送宇信通信息={}", params);

            if (StringUtils.isNotEmpty(userNo)){
                RestTemplate restTemplate = new RestTemplate();
                safetySpotCheckProcessService.postForEntity(sendMsgImUrl, params, restTemplate);
            }

            String title = Joiner.on("").join("请查阅",
                    reformUserName,
                    "延期整改异常设备：",
                    processDTO.getDeviceName(),
                    "-",
                    processDTO.getDeviceCode());

            String text = Joiner.on("").join(
                    "设备整改人",
                    reformUserName,
                    "延期整改异常设备：",
                    processDTO.getDeviceName(),
                    "-",
                    processDTO.getDeviceCode(),
                    "，将计划整改完成时间由",
                    processDTO.getReformTime(),
                    "调整到",
                    delayTime,
                    "，请知悉。",
                    "<br/>流程链接：",
                    "<a href='",
                    flowUrl,"' target='_blank'>",
                    flowUrl,
                    "</a>");
            String deviceId = processDTO.getDeviceId();
            String factoryId = safetySpotCheckProcessService.getFactoryById(deviceId);
            //抄送治安管理员
            UserMessageListParam param = new UserMessageListParam();
            param.setPowerFactoryId(factoryId);
            param.setRoleName("治安管理员");
            List<SafetySysUserDO> list = safetySysUserService.getList(param);
            String copiers = "";
            for(SafetySysUserDO userDO: list){
                Map<String,String> curUser = safetySpotCheckProcessService.getEmpInfoByUsername(userDO.getUserName());
                if(curUser != null){
                    String email = curUser.get("email");
                    copiers = Joiner.on(";").join(email,copiers);
                }
            }
            mailCenterUtils.sendMailNew(title, text, user.get("email"), "治安管理邮箱", "zagl@yutong.com", copiers);
        }
        return true;
    }

    /**
     * 发送验证消息提醒
     * @return 返回值
     */
    public boolean sendValidateTips(SafetySpotCheckProcessDTO processDTO){
        List<String> userList = new ArrayList<>();
        String valuidateEmpLogin = processDTO.getPlanValidateEmpLogin();
        if(StringUtils.isBlank(valuidateEmpLogin)){
            valuidateEmpLogin = processDTO.getCreator();

            //设备预设点检人
            String deviceCheckLogin = processDTO.getDeviceCheckLogin();
            if(!StringUtils.isBlank(deviceCheckLogin)){
                List<String> list2 = Arrays.asList(deviceCheckLogin.split(","));
                userList.addAll(list2);
            }
        }
        if(StringUtils.isNotEmpty(valuidateEmpLogin)) {
            List<String> list1 = Arrays.asList(valuidateEmpLogin.split(","));
            userList.addAll(list1);
        }

        String flowUrl = "";
        String reformUserName = "";
        StringBuilder users = new StringBuilder();
        if(!CollectionUtils.isEmpty(userList)){
            for(String loginName: userList){
                //给整改责任人上级发通知
                //【公共服务设备设施点检】设备整改流程主题，设备整改人张瑶延期整改，将计划整改完成时间由2024/05/26调整到2024/06/07，请知悉。流程链接：XXX
                Map<String, String> user = safetySpotCheckProcessService.getEmpInfoByUsername(loginName);
                if(ObjectUtils.isEmpty(user)){
                    continue;
                }
                if(!StringUtils.isEmpty(user.get("email")) && !users.toString().contains(user.get("email"))){
                    users.append(user.get("email")).append(",");
                }

                Map<String,String> reformUser = safetySpotCheckProcessService.getEmpInfoByUsername(processDTO.getReformUser());
                if(reformUser != null){
                    reformUserName = reformUser.get("empName");
                }
                flowUrl = Joiner.on("").join(
                        todoPre,
                        "&formInstanceId=",
                        processDTO.getId(),
                        "&processId=",
                        processDTO.getFlowId()
                );
                String msg = Joiner.on("").join("【公共服务设备设施点检】",
                        processDTO.getTitle(),
                        "，设备整改人",
                        reformUserName,
                        "已整改完成",
                        "，请前往验证。流程链接：",
                        flowUrl
                );

                Map<String, String> params = new HashMap<>();
                params.put("appId", "1");
                params.put("sessionType", "r");
                params.put("contentType", "0");
                params.put("content", msg);
                params.put("from", robotId);
                //判断是否外协 5/6开头的员工号 需要转换为 01开头
                String userNo = user.get("empNo");
                if(StringUtils.isNotEmpty(userNo) && (userNo.startsWith("5") || userNo.startsWith("6"))){
                    userNo = safetySpotCheckProcessService.getOldEmpNo(userNo);
                }
                params.put("to", userNo);
                LogUtils.info("发送宇信通信息={}", params);

                if (StringUtils.isNotEmpty(userNo)){
                    RestTemplate restTemplate = new RestTemplate();
                    safetySpotCheckProcessService.postForEntity(sendMsgImUrl, params, restTemplate);
                }
            }
            String title = Joiner.on("").join("请查阅",
                    reformUserName,
                    "整改完成，设备：",
                    processDTO.getDeviceName(),
                    "-",
                    processDTO.getDeviceCode());
            String text = Joiner.on("").join(
                    "设备整改人",
                    reformUserName,
                    "已整改异常设备：",
                    processDTO.getDeviceName(),
                    "-",
                    processDTO.getDeviceCode(),
                    "，请前往验证。",
                    "<br/>流程链接：",
                    "<a href='",
                    flowUrl,"' target='_blank'>",
                    flowUrl,
                    "</a>");
            String deviceId = processDTO.getDeviceId();
            String factoryId = safetySpotCheckProcessService.getFactoryById(deviceId);
            //抄送治安管理员
            UserMessageListParam param = new UserMessageListParam();
            param.setPowerFactoryId(factoryId);
            param.setRoleName("治安管理员");
            List<SafetySysUserDO> list = safetySysUserService.getList(param);
            String copiers = "";
            for(SafetySysUserDO userDO: list){
                Map<String,String> curUser = safetySpotCheckProcessService.getEmpInfoByUsername(userDO.getUserName());
                if(curUser != null){
                    String email = curUser.get("email");
                    copiers = Joiner.on(";").join(email,copiers);
                }
            }
            if (StringUtils.isNotEmpty(users.toString())) {
                mailCenterUtils.sendMailNew(title, text, users.substring(0, users.length()-1), "治安管理邮箱", "zagl@yutong.com", copiers);
            }
        }
        return true;
    }

    /**
     * 超过24小时填写计划完成日期，提示当前节点处理人，所有治安管理员或已转派的治安管理员和整改责任人
     * @param diffHour 逾期时长
     * @return
     */
    public Object sendTipsForNullPlanReformTime(Integer diffHour){
        if(diffHour == null){
            diffHour = 24;
        }
        Map<String,Object> params = new HashMap<>();
        params.put("reformTipFlag",1);
        params.put("status",1);
        //获取整改计划完成日期为空的流程
        List<SafetySpotCheckProcessDTO> processDTOS = safetySpotCheckProcessService.selectRemindProcessList(params);
        if(ObjectUtils.isEmpty(processDTOS)){
            return null;
        }
        String title = "【公共服务设备设施点检】请确认{}提交的{}-{}异常点检流程";
        String content = "【公共服务设备设施点检】{}提交的{}-{}异常点检流程已超过24小时未填写计划完成日期，请前往确认。流程链接：{}";
        for(SafetySpotCheckProcessDTO processDTO:processDTOS){
            String auditUserName = "";
            //获取当前节点处理人
            List<BpmWorkitemVO> workitemVOS = this.getCurrentNodesInfo(processDTO.getId(),processDTO.getFlowId());
            if(workitemVOS != null && workitemVOS.size() > 0){
                auditUserName = workitemVOS.stream().map(BpmWorkitemVO::getExpecter).collect(Collectors.joining(";"));
            }
            //获取审批记录，获取上个节点审批人
            List<BpmAuditOptionVO> auditOptionVOS = safetyBpmProcessService.getAuditOptionList(processDTO.getId(),processDTO.getFlowId(),SPOT_CHECK_FORMID,"admin");
            if(auditOptionVOS == null || auditOptionVOS.size()<=0){
                continue;
            }
            Optional<BpmAuditOptionVO> doMaxShort = auditOptionVOS.stream().max(Comparator.comparing(BpmAuditOptionVO::getCreateTime));
            BpmAuditOptionVO maxAudit = doMaxShort.get();
            String maxAuditTime = maxAudit.getCreateTime();
            if(!"".equals(auditUserName)){
                auditUserName += ";";
            }
            auditUserName += maxAudit.getHandler();
            if(DateUtils.loseHourNew(maxAuditTime,DateUtils.format(new Date(),2))>diffHour){
                safetySpotCheckProcessService.sendPlanReformTips(processDTO,title,content,auditUserName);
            }
        }
        return null;
    }

    /**
     * 获取超过计划整改完成日期的流程给整改人，领导，专拍的治安管理员发送提醒
     * @return
     */
    public Object sendTipsForBeOverdueReform(){
        Map<String,Object> params = new HashMap<>();
        params.put("reformDueFlag",1);
        params.put("nodeFlag",2);
        //获取超过计划整改完成日期的流程
        List<SafetySpotCheckProcessDTO> processDTOS = safetySpotCheckProcessService.selectRemindProcessList(params);
        if(ObjectUtils.isEmpty(processDTOS)){
            return null;
        }
        String title = "【公共服务设备设施点检】请整改{}提交的{}-{}异常点检流程";
        String content = "【公共服务设备设施点检】{}提交的{}-{}已超出计划完成日期，请前往整改。流程链接：{}";
        for(SafetySpotCheckProcessDTO processDTO:processDTOS){
            String auditUserName = "";
            //获取当前节点处理人
            List<BpmWorkitemVO> workitemVOS = this.getCurrentNodesInfo(processDTO.getId(),processDTO.getFlowId());
            if(workitemVOS != null && workitemVOS.size() > 0){
                auditUserName = workitemVOS.stream().map(BpmWorkitemVO::getExpecter).collect(Collectors.joining(";"));
            }
            //如果是转办的话，获取转办的治安管理员
            if("1".equals(processDTO.getCommissionFlag())){
                List<BpmAuditOptionVO> auditOptionVOS = safetyBpmProcessService.getAuditOptionList(processDTO.getId(),processDTO.getFlowId(),SPOT_CHECK_FORMID,"admin");
                if(auditOptionVOS == null || auditOptionVOS.size()<=0){
                    continue;
                }
                Optional<BpmAuditOptionVO> doMaxShort = auditOptionVOS.stream().max(Comparator.comparing(BpmAuditOptionVO::getCreateTime));
                BpmAuditOptionVO maxAudit = doMaxShort.get();
                if(!"".equals(auditUserName)){
                    auditUserName += ";";
                }
                auditUserName += maxAudit.getHandler();
            }

            safetySpotCheckProcessService.sendPlanReformTips(processDTO,title,content,auditUserName);
        }
        return null;
    }

    /**
     * 处理获取的当前节点
     * @param recordId recordID
     * @param flowId 流程ID
     * @return 返回值
     */
    public List<BpmWorkitemVO> getCurrentNodesInfo(String recordId,String flowId){
        String returnData = (String) safetyBpmProcessService.getCurrentNodesInfo(recordId,flowId,SPOT_CHECK_FORMID);
        if(StringUtils.isBlank(returnData)){
            return new ArrayList();
        }
        JSONArray jsonArray = JSONArray.parseArray(returnData);
        if(jsonArray.size() <= 0){
            return new ArrayList();
        }
        JSONObject first = jsonArray.getJSONObject(0);
        JSONArray jsonArray1 = first.getJSONArray("workitems");
        if(jsonArray1 == null ||jsonArray1.size()<=0){
            return new ArrayList<>();
        }
        return JSONArray.parseArray(jsonArray1.toString(),BpmWorkitemVO.class);
    }
}
