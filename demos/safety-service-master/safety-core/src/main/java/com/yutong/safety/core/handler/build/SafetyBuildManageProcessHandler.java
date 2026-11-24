package com.yutong.safety.core.handler.build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.SafetySysUserRoleDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildDeviceVO;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.SafetySysRoleService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildManageProcessHandler {
    @Autowired
    SafetyBuildManageProcessService safetyBuildManageProcessService;

    @Autowired
    SafetyBuildDeviceService safetyBuildDeviceService;

    @Autowired
    SafetySysUserService safetySysUserService;

    @Autowired
    SafetyBpmProcessService safetyBpmProcessService;

    @Autowired
    SafetyBuildPowerService safetyBuildPowerService;

    @Autowired
    SafetyBuildConfigService safetyBuildConfigService;

    @Autowired
    SafetyBuildWhiteListService safetyBuildWhiteListService;

    @Autowired
    WebserviceUtil webserviceUtil;

    @Autowired
    SafetyBuildInfoConfigService safetyBuildInfoConfigService;

    @Autowired
    SafetySysRoleService safetySysRoleService;

    @Value("${build.getRoleList}")
    String getRoleListUrl;

    private static final String BUILD_MANAGE = "build_manage_process";
    private static final String BUILD_MANAGE_PROCESS = "safety_build_manage_process";
    private static final String FLOW_STATUS_ABANDON = "00";
    private static final String BUILD_DUTY_ROLE_NAME = "门禁管理员";

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Autowired
    private YxtRebotUtils yxtRebotUtils;

    @Autowired
    SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    private SafetyGroupService safetyGroupService;

    public SafetyBuildManageProcessDTO getGuardInfoListByDept(String dutyDeptCode) {
        if (StringUtils.isBlank(dutyDeptCode)) {
            return null;
        }
        List<SafetyBuildPowerDO> list = safetyBuildManageProcessService.getGuardInfoListByDept(dutyDeptCode);
        SafetyBuildManageProcessDTO en = new SafetyBuildManageProcessDTO();
        if(!CollectionUtils.isEmpty(list)){
            BeanUtils.copyProperties(list.get(0),en);
        }
        List<String> deviceCodeList = new ArrayList<>();
        List<String> deptEmpCodeList = new ArrayList<>();

        StringBuilder oldDeviceCode = new StringBuilder();
        StringBuilder oldDeviceName = new StringBuilder();
        StringBuilder oldDeptEmpLogin = new StringBuilder();
        StringBuilder oldDeptEmpCode = new StringBuilder();
        StringBuilder oldDeptEmpName = new StringBuilder();

        for (SafetyBuildPowerDO safetyBuildPowerDO : list) {
            //校验门禁编码不存在时拼接门禁信息
            if(!deviceCodeList.contains(safetyBuildPowerDO.getDeviceCode())){
                deviceCodeList.add(safetyBuildPowerDO.getDeviceCode());
                appendIfNotContains(oldDeviceCode, safetyBuildPowerDO.getDeviceCode());
                appendIfNotContains(oldDeviceName, safetyBuildPowerDO.getDeviceName());
            }
            //校验责任人不存在时拼接责任人信息
            if(!deptEmpCodeList.contains(safetyBuildPowerDO.getEmployeeCode())){
                deptEmpCodeList.add(safetyBuildPowerDO.getEmployeeCode());
                appendIfNotContains(oldDeptEmpName, safetyBuildPowerDO.getEmployeeName());
                appendIfNotContains(oldDeptEmpLogin, safetyBuildPowerDO.getEmployeeLogin());
                appendIfNotContains(oldDeptEmpCode, safetyBuildPowerDO.getEmployeeCode());
            }
        }
        en.setOldDeviceCode(oldDeviceCode.toString());
        en.setOldDeviceName(oldDeviceName.toString());
        en.setNewDeviceCode(oldDeviceCode.toString());
        en.setNewDeviceName(oldDeviceName.toString());

        en.setOldDepartEmployeeLogin(oldDeptEmpLogin.toString());
        en.setOldDepartEmployeeName(oldDeptEmpName.toString());
        en.setOldDepartEmployeeCode(oldDeptEmpCode.toString());
        en.setNeweDepartEmployeeLogin(oldDeptEmpLogin.toString());
        en.setNewDepartEmployeeName(oldDeptEmpName.toString());
        en.setNewDepartEmployeeCode(oldDeptEmpCode.toString());

        if (en.getBuildNum() == null || en.getEmposerNum() == null) {
            List<SafetyBuildConfigDO> configDOList = safetyBuildConfigService.query();
            if (CollectionUtils.isNotEmpty(configDOList)) {
                if(en.getBuildNum() == null){
                    en.setBuildNum(configDOList.get(0).getDepartManageNum());
                }
                if(en.getEmposerNum() == null){
                    en.setEmposerNum(configDOList.get(0).getDepartGrantNum());
                }
            }
        }
        return en;
    }

    private void appendIfNotContains(StringBuilder sb, String value) {
        if (!StringUtils.isBlank(value)) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(value);
        }
    }

    public SafetyBuildManageProcessDO getDetailById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        return safetyBuildManageProcessService.getDetailById(id);
    }

    public void saveAndCreateProcess(SafetyBuildManageProcessDTO entity, String token) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        entity.setId(uuid);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        entity.setCreateEmployeeLogin(tockenUser.get("userName"));
        entity.setApplyTime(DateUtils.format(new Date(),2));

        String format = DateUtils.format(new Date(), 5);
        String processCode = "LYMJGL" + format.substring(0,format.length()-2);
        entity.setProcessCode(processCode);

        //流程状态
        entity.setProcessStatus("20");

        //是否涉及绝密区 1-普通员工  2-管理员 3-保密负责人
        if(!"2".equals(entity.getRoleType())){
            relateSecret(entity);
        }

        //检测门禁是否变动
        deviceChange(entity);

        //设置厂区巡逻组长
        setPatrolLeader(entity);

        //设置部门门禁管理员
        setBuildManage(entity);

        //根据门禁信息处理对应的工厂楼栋
        dealFactory(entity);

        SafetyBuildManageProcessDO safetyBuildManageProcessDO = new SafetyBuildManageProcessDO();
        BeanUtils.copyProperties(entity, safetyBuildManageProcessDO);
        safetyBuildManageProcessDO.setCreateTime(DateUtils.format(new Date(), 2));
        Integer row = safetyBuildManageProcessService.save(safetyBuildManageProcessDO);

        if (row > 0) {
            //手动提交事务
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(entity.getProcessSubject(),
                    BUILD_MANAGE, BUILD_MANAGE_PROCESS, entity.getCreateEmployeeLogin(), uuid);
            if (StringUtils.isNotBlank(returnFlowId)) {
                safetyBuildManageProcessDO.setBpmFlowId(returnFlowId.split(",")[1]);
            }
            safetyBuildManageProcessService.update(safetyBuildManageProcessDO);
        }
    }


    public Object approvalProcess(SafetyBuildManageProcessDTO entity, String token) {
        SafetyBuildManageProcessDO process = new SafetyBuildManageProcessDO();
        BeanUtils.copyProperties(entity, process);
        process.setApplyTime(DateUtils.format(new Date(),2));
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String userName = tockenUser.get("userName");
        if(com.yutong.gw.utils.StringUtils.isEmpty(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        process.setApplyEmployee(tockenUser.get("userName"));
        if("不通过".equals(entity.getAuditNote())){
            process.setApplyStatus("2");
            process.setProcessStatus(FLOW_STATUS_ABANDON); //废弃
            process.setRefuseReason("审核不通过");
        }else{
            process.setApplyStatus("1");
        }
        safetyBuildManageProcessService.update(process);

        //提取审批意见
        String retunAuditNote = initAuditNote(entity);
        //审核不通过废弃流程
        BpmProcessParam processParam;
        if(FLOW_STATUS_ABANDON.equals(process.getProcessStatus())){
            processParam = safetyBpmProcessService.initProcessAbandonParam(entity.getId(),entity.getBpmFlowId(),
                    BUILD_MANAGE,BUILD_MANAGE_PROCESS,userName,retunAuditNote);
        }else{
            processParam = safetyBpmProcessService.initProcessParam(entity.getId(),entity.getBpmFlowId(),
                    BUILD_MANAGE,BUILD_MANAGE_PROCESS,userName,false,retunAuditNote,"");
        }
        
        String status = safetyBpmProcessService.approveProcessByUserName(entity.getId(), entity.getBpmFlowId(),
                BUILD_MANAGE, BUILD_MANAGE_PROCESS,processParam.getProcessParam(), userName);
        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        assert status != null;
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);
        return returnVO;
    }

    private String initAuditNote(SafetyBuildManageProcessDTO entity){
        String returnAduit = entity.getAuditNote();
        if(StringUtils.isNotBlank(entity.getFdParameterJson())){
            try{
                JSONObject paramJson = JSONObject.parseObject(entity.getFdParameterJson());
                if(paramJson != null){
                    JSONObject auditParam = paramJson.getJSONObject("param");
                    String auditNote = auditParam.getString("auditNote");
                    if(StringUtils.isNotBlank(auditNote)){
                        returnAduit = auditNote;
                    }
                }
            }catch (Exception e){
                LogUtils.error("异常处理",e);
            }
        }
        if("2".equals(entity.getApplyStatus())){
            if("同意".equals(returnAduit)){
                returnAduit = "不通过";
            }else{
                return "不通过;"+returnAduit;
            }
        }
        return returnAduit;
    }

    public Object getPageList(SafetyBuildManageProcessDTO param,String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String userName = tockenUser.get("userName");
        if(com.yutong.gw.utils.StringUtils.isEmpty(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        param.setCreateEmployeeLogin(userName);

        String s = safetyBuildProcessHandler.checkUserType(token);
        param.setRoleType(s.split(":")[0]);
        List<SafetyBuildManageProcessDTO> list = safetyBuildManageProcessService.selectList(param);
        long total = safetyBuildManageProcessService.selectListCount(param);
        return new PageVO<>(total, list);
    }

    public Object getRoleList(String roleName) {
        JSONArray result = null;
                //拼接参数调用接口
        String param = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.lbpmdocking.sys.kmss.landray.com/\">" + "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ws:searchRole>" +
                "<searchRole>" + roleName + "</searchRole>" +
                "<pageNo>" + 1 + "</pageNo>" +
                "<pageSize>" + 100 + "</pageSize>" +
                "<type>like</type>" +
                "</ws:searchRole>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        String webResult = webserviceUtil.doWebservicePostNew(getRoleListUrl, param);
        LogUtils.info("调用审批流程接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject approveProcessResponse = null;
        try {
            approveProcessResponse = jsonObject.getJSONObject("soap:Envelope")
                    .getJSONObject("soap:Body")
                    .getJSONObject("ns2:searchRoleResponse")
                    .getJSONObject("return");

            if (approveProcessResponse != null && "T".equals(approveProcessResponse.getString("code"))) {
                result = approveProcessResponse.getJSONArray("result");
            }
        }catch (Exception e){
            LogUtils.info("查询Eip角色调用接口失败，失败原因={}，返回参数={}",e.getMessage(),approveProcessResponse);
        }

        return result;
    }

    /**
     * 门禁责任部门权限流程回调事件
     * @param processId bpm流程ID
     * @param processData 其他流程参数
     */
    public void bpmCallBackProcess(String processId, String processData) {
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(processData)) {
            LogUtils.info("保密区域管理责任部门调整流程异常-流程ID或实体类ID为空");
            return;
        }
        SafetyBuildManageProcessDO param = new SafetyBuildManageProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyBuildManageProcessDO> processList = safetyBuildManageProcessService.getList(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("保密区域管理责任部门调整流程异常-未找到对应的流程信息");
                return;
            }
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.containsKey("nodeFactId")?processDataObj.getString("nodeFactId"):"N3";
            SafetyBuildManageProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(nodeFactId);
            switch (nodeFactId) {
                case "N4":
                    processDTO.setCurNodeName("公司绝密区负责人");
                    processDTO.setCurNodeHandler(processDTO.getTopSecretEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getTopSecretEmployeeName());
                    processDTO.setNodeFlag("1");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N5":
                    processDTO.setCurNodeName("本部门部长");
                    processDTO.setCurNodeHandler(processDTO.getLeaderEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getLeaderEmployeeName());
                    processDTO.setNodeFlag("2");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N10":
                    processDTO.setCurNodeName("门禁系统管理员");
                    processDTO.setCurNodeHandler(processDTO.getBuildSysManagerEip());
                    processDTO.setNodeFlag("3");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N6":
                    //发宇信通消息
                    sendYXT(processDTO);
                    break;
                case "N3":
                    //非废弃流程才进行权限下发
                    if(!FLOW_STATUS_ABANDON.equals(processDTO.getProcessStatus())){
                        saveDetail(processDTO);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LogUtils.error("保密区域管理责任部门调整流程回调-处理逻辑异常", e);
        }
    }

    /**
     * 废弃流程
     */
    public void discardProcess(String bpmFlowId,String processData){
        if (StringUtils.isBlank(bpmFlowId) || StringUtils.isBlank(processData)) {
            LogUtils.info("门禁责任部门权限调整流程异常-流程ID或实体类ID为空，入参processData="+processData);
            return;
        }
        SafetyBuildManageProcessDO param = new SafetyBuildManageProcessDO();
        param.setBpmFlowId(bpmFlowId);
        try {
            List<SafetyBuildManageProcessDO> processList = safetyBuildManageProcessService.getList(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("保密区域管理责任部门调整流程异常-未找到对应的流程信息");
                return;
            }
            SafetyBuildManageProcessDO processDTO = processList.get(0);
            processDTO.setProcessStatus(FLOW_STATUS_ABANDON); //废弃
            processDTO.setApplyStatus("2"); //审批不通过
            processDTO.setRefuseReason("流程废弃");
            safetyBuildManageProcessService.update(processDTO);
        } catch (Exception e) {
            LogUtils.error("责任部门调整流程废弃异常", e);
        }
    }

    public void testCallBackForBpm(String processId, String processData){
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(processData)) {
            LogUtils.info("保密区域管理责任部门调整流程异常-流程ID或实体类ID为空");
            return;
        }
        SafetyBuildManageProcessDO param = new SafetyBuildManageProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyBuildManageProcessDO> processList = safetyBuildManageProcessService.getList(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("保密区域管理责任部门调整流程异常-未找到对应的流程信息");
                return;
            }
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.containsKey("nodeFactId")?processDataObj.getString("nodeFactId"):"N3";
            SafetyBuildManageProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(nodeFactId);
            switch (nodeFactId) {
                case "N4":
                    processDTO.setCurNodeName("公司绝密区负责人");
                    processDTO.setCurNodeHandler(processDTO.getTopSecretEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getTopSecretEmployeeName());
                    processDTO.setNodeFlag("1");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N5":
                    processDTO.setCurNodeName("本部门部长");
                    processDTO.setCurNodeHandler(processDTO.getLeaderEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getLeaderEmployeeName());
                    processDTO.setNodeFlag("2");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N10":
                    processDTO.setCurNodeName("门禁系统管理员");
                    processDTO.setCurNodeHandler(processDTO.getBuildSysManagerEip());
                    processDTO.setNodeFlag("3");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N6":
                    //发宇信通消息
                    sendYXT(processDTO);
                    break;
                default:
                    //结束事件时没有节点信息非废弃流程才进行权限下发
                    LogUtils.info("进入楼宇责任部门调整的结束事件");
                    if(!FLOW_STATUS_ABANDON.equals(processDTO.getProcessStatus())){
                        saveDetail(processDTO);
                    }
                    break;
            }
        } catch (Exception e) {
            LogUtils.error("保密区域管理责任部门调整流程回调-处理逻辑异常", e);
        }
    }

    public void testCallBack(String processId,String nodeFactId){
        SafetyBuildManageProcessDO param = new SafetyBuildManageProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyBuildManageProcessDO> processList = safetyBuildManageProcessService.getList(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("保密区域管理责任部门调整流程异常-未找到对应的流程信息");
                return;
            }
            SafetyBuildManageProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(nodeFactId);
            switch (nodeFactId) {
                case "N4":
                    processDTO.setCurNodeName("公司绝密区负责人");
                    processDTO.setCurNodeHandler(processDTO.getTopSecretEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getTopSecretEmployeeName());
                    processDTO.setNodeFlag("1");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N5":
                    processDTO.setCurNodeName("本部门部长");
                    processDTO.setCurNodeHandler(processDTO.getLeaderEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getLeaderEmployeeName());
                    processDTO.setNodeFlag("2");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N10":
                    processDTO.setCurNodeName("门禁系统管理员");
                    processDTO.setCurNodeHandler(processDTO.getBuildSysManagerEip());
                    processDTO.setNodeFlag("3");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N6":
                    //发宇信通消息
                    sendYXT(processDTO);
                    break;
                default:
                    //结束事件时没有节点信息非废弃流程才进行权限下发
                    LogUtils.info("进入楼宇责任部门调整的结束事件");
                    if(!FLOW_STATUS_ABANDON.equals(processDTO.getProcessStatus())){
                        saveDetail(processDTO);
                    }
                    break;
            }
        } catch (Exception e) {
            LogUtils.error("保密区域管理责任部门调整流程回调-处理逻辑异常", e);
        }
    }

    /**
     * 保存详情
     *
     */
    public void saveDetail(SafetyBuildManageProcessDO processDTO) {
        //更新流程信息
        processDTO.setCurNodeId("N3");
        processDTO.setProcessStatus("30");
        processDTO.setCurNodeName("结束节点");
        processDTO.setApplyStatus("1");
        processDTO.setApplyTime(DateUtils.format(new Date(),2));

        safetyBuildManageProcessService.update(processDTO);

        //保存信息至  保密负责人及责任部门管理表
        //责任部门：保密负责人：授权门禁 = 1：1：1
        //保密负责人
        String empCode = processDTO.getNewDepartEmployeeCode();
        String empName = processDTO.getNewDepartEmployeeName();
        String empLogin = processDTO.getNeweDepartEmployeeLogin();

        //门禁
        String newDeviceName = processDTO.getNewDeviceName();
        String newDeviceCode = processDTO.getNewDeviceCode();

        if(StringUtils.isBlank(empCode)){return;}

        String[] empCodeS = empCode.split(",");
        String[] empNameS = empName.split(",");
        String[] empLoginS = empLogin.split(",");
        //获取历史的责任部门权限
        List<SafetyBuildPowerDO> oldPowerList = safetyBuildManageProcessService.getGuardInfoListByDept(processDTO.getDutyDeptCodePath());
        List<SafetyBuildPowerDO> list = new ArrayList<>();

        //查询授权配置信息
        Integer emposerNum = null,buildNum = null;
        if(!CollectionUtils.isEmpty(oldPowerList)){
            emposerNum = oldPowerList.get(0).getEmposerNum();
            buildNum = oldPowerList.get(0).getBuildNum();
        }
        if(emposerNum == null || buildNum == null){
            List<SafetyBuildConfigDO> configList = safetyBuildConfigService.query();
            if (CollectionUtils.isNotEmpty(configList)) {
                emposerNum = configList.get(0).getDepartGrantNum();
                buildNum = configList.get(0).getDepartManageNum();
            }
        }
        String deptNamePath = processDTO.getDutyDeptName();
        String dutyDeptCode = processDTO.getDutyDeptCode();
        String dutyDeptCodePath = processDTO.getDutyDeptCodePath();
        if(StringUtils.isNotBlank(dutyDeptCode)){
            SafetyGroupInfoDO groupInfoDO = safetyGroupService.getGroupByCode(dutyDeptCode);
            if(!ObjectUtils.isEmpty(groupInfoDO)){
                deptNamePath = groupInfoDO.getOrgNamePath();
            }
        }

        for (int i = 0; i < empCodeS.length; i++) {
            SafetyBuildPowerDO safetyBuildPowerDO = new SafetyBuildPowerDO();
            safetyBuildPowerDO.setEmposerNum(emposerNum);
            safetyBuildPowerDO.setBuildNum(buildNum);
            //责任部门
            safetyBuildPowerDO.setDutyDeptCode(dutyDeptCodePath);
            safetyBuildPowerDO.setDutyDeptName(deptNamePath);
            //员工号
            safetyBuildPowerDO.setEmployeeCode(StringUtils.isBlank(empCodeS[i])?null:empCodeS[i]);
            safetyBuildPowerDO.setEmployeeName(StringUtils.isBlank(empNameS[i])?null:empNameS[i]);
            safetyBuildPowerDO.setEmployeeLogin(StringUtils.isBlank(empLoginS[i])?null:empLoginS[i]);
            //流程信息
            safetyBuildPowerDO.setProcessId(processDTO.getId());
            safetyBuildPowerDO.setProcessCode(processDTO.getProcessCode());
            safetyBuildPowerDO.setCreateTime(processDTO.getApplyTime());
            safetyBuildPowerDO.setUpdateTime(processDTO.getApplyTime());
            safetyBuildPowerDO.setIsDelete("0");
            safetyBuildPowerDO.setBuildSysManagerRole(processDTO.getBuildSysManagerRole());
            //20250618-恩连要求调整门禁为空也可以保存更新
            if(StringUtils.isNotBlank(newDeviceCode)){
                String[] newDeviceNameS = newDeviceName.split(",");
                String[] newDeviceCodeS = newDeviceCode.split(",");
                for (int j = 0; j < newDeviceCodeS.length; j++) {
                    SafetyBuildPowerDO newsafetyBuildPowerDO = new SafetyBuildPowerDO();
                    BeanUtils.copyProperties(safetyBuildPowerDO,newsafetyBuildPowerDO);
                    //授权门禁
                    newsafetyBuildPowerDO.setDeviceCode(StringUtils.isBlank(newDeviceCodeS[j])?null:newDeviceCodeS[j]);
                    newsafetyBuildPowerDO.setDeviceName(StringUtils.isBlank(newDeviceNameS[j])?null:newDeviceNameS[j]);
                    list.add(newsafetyBuildPowerDO);
                }
            }else{
                list.add(safetyBuildPowerDO);
            }

        }
        //处理白名单管理
        dealWhite(processDTO);
        //保存前先删除该责任部门下的所有门禁信息
        safetyBuildPowerService.deleteByDeptCode(dutyDeptCodePath,"责任部门调整");
        safetyBuildPowerService.batchInsert(list);

        //根据员工号来刷新该员工对应的流程ID
        safetyBuildPowerService.batchUpdate(Arrays.asList(dutyDeptCodePath.split(";")),processDTO);
        //0702-增加逻辑 不存在SysUser的门禁管理员自动添加到SysUser中
        try {
            safetySysUserService.batchInsertUserForEmpnos(Arrays.asList(empCodeS));
        }catch (Exception e){
            LogUtils.error("批量增加用户信息异常",e);
        }
        //查询保密负责人对应的userId
        List<Integer> userIdByCodeList = safetySysUserService.getUserIdByCodeList(Arrays.asList(empCodeS));
        //查询保密负责人角色对应的人员ID
        UserMessageListParam userParam = new UserMessageListParam();
        userParam.setRoleName(BUILD_DUTY_ROLE_NAME);
        List<SafetySysUserDO> userRoleList = safetySysUserService.getList(userParam);
        Set<Integer> set2 = new HashSet<>();
        if(!CollectionUtils.isEmpty(userRoleList)){
            set2 = userRoleList.stream().map(SafetySysUserDO::getId).collect(Collectors.toSet());
        }

        Set<Integer> set1 = new HashSet<>(userIdByCodeList);
        set1.removeAll(set2);

        SafetySysRoleDO roleDO = safetySysRoleService.getByRoleName(BUILD_DUTY_ROLE_NAME);
        if(ObjectUtils.isEmpty(roleDO)){
            LogUtils.error("\"code\":\"9999\",\"msg\":\"未找到角色"+BUILD_DUTY_ROLE_NAME+",请尽快维护！\"");
        }else {
            List<SafetySysUserRoleDO> paramList = new ArrayList<>();
            for (Integer userId : set1) {
                SafetySysUserRoleDO userRoleDO = new SafetySysUserRoleDO();
                userRoleDO.setUserId(userId);
                userRoleDO.setRoleId(roleDO.getId());
                paramList.add(userRoleDO);
            }
            //保存保密负责人
            if(!CollectionUtils.isEmpty(paramList)){
                safetySysUserService.batchInserUserRole(paramList);
            }
        }
    }

    /**
     * 处理白名单管理
     */
    public void dealWhite(SafetyBuildManageProcessDO processDTO){
        List<String> newEmpCodeSet = null;
        try {
            //根据责任部门查询其原来的保密负责人
            BuildPowerListParam listParam = new BuildPowerListParam();
            listParam.setDutyDeptCode(processDTO.getDutyDeptCodePath());
            List<SafetyBuildPowerDO> oldList = safetyBuildPowerService.selectList(listParam);

            //为空代表此次是新加的责任部门，直接返回
            if(CollectionUtils.isEmpty(oldList)){return;}
            List<String> oldEmpCodeSet = new ArrayList<>();
            List<String> oldEmpNameSet = new ArrayList<>();
            for (SafetyBuildPowerDO e : oldList) {
                if(oldEmpNameSet.contains(e.getEmployeeCode())){
                    continue;
                }
                oldEmpCodeSet.add(e.getEmployeeCode());
                //防止名字为空
                oldEmpNameSet.add(StringUtils.isBlank(e.getEmployeeName())?" ":e.getEmployeeName());
            }

            //现设的保密负责人
            newEmpCodeSet = Arrays.asList(processDTO.getNewDepartEmployeeCode().split(","));
            List<String> newEmpNameSet = Arrays.asList(processDTO.getNewDepartEmployeeName().split(","));

            //获取此次删除的保密负责人（如果没有，就不改变白名单的添加人）
            oldEmpCodeSet.removeAll(newEmpCodeSet);
            if(CollectionUtils.isEmpty(oldEmpCodeSet)){return;}

            //根据责任部门code和保密负责人更新
            safetyBuildWhiteListService.updateByConditions(processDTO.getDutyDeptCodePath(),oldEmpCodeSet,newEmpCodeSet.get(0),newEmpNameSet.get(0));
        } catch (Exception e) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"在责任部门调整流程中更新白名单的创建人的时候，出错！！！对应的流程ID="+processDTO.getId()
                    + "，白名单的创建人={"+newEmpCodeSet+"}"
                    + "，错误信息={"+e.getMessage()+"}\"}");
        }
    }

    /**
     * 发送宇信通消息
     *
     */
    public void sendYXT(SafetyBuildManageProcessDO processDTO) {
        String newDepartEmployeeCode = processDTO.getNewDepartEmployeeCode();
        if (StringUtils.isBlank(newDepartEmployeeCode)) {
            return;
        }

        SafetyBuildInfoConfigDO e = new SafetyBuildInfoConfigDO();
        e.setShowFlag("1");
        e.setId(5);
        List<SafetyBuildInfoConfigDO> infoModelList = safetyBuildInfoConfigService.query(e);
        if (CollectionUtils.isEmpty(infoModelList)) {return;}
        SafetyBuildInfoConfigDO info = infoModelList.get(0);

        //变更后授权门禁姓名
        String newDeviceName = processDTO.getNewDeviceName();
        //变更后保密负责人姓名
        String newDepartEmployeeName = processDTO.getNewDepartEmployeeName();
        String sendmsg = info.getInfoContent()
                .replace("{buildName}", newDeviceName)
                .replace("{secretUser}", newDepartEmployeeName);

        String[] split = newDepartEmployeeCode.split(",");
        for (String s : split) {
            RestTemplate restTemplate = new RestTemplate();
            LogUtils.info("保密区域管理责任部门调整发宇信通消息：{}", s);
            yxtRebotUtils.postForEntity(null, incomintRebotId, s, sendmsg, restTemplate);
        }
    }

    /**
     * 是否涉及绝密区
     */
    private void relateSecret(SafetyBuildManageProcessDTO entity) {
        entity.setTopSecretFlag("0");
        if (StringUtils.isBlank(entity.getNewDeviceCode())) {
            return;
        }

        String newDeviceCode = entity.getNewDeviceCode();
        String[] split = newDeviceCode.split(",");

        //获取所有绝密区的门禁code
        //查询绝密区，为空直接返回
        BuildDeviceListParam en = new BuildDeviceListParam();
        en.setSecurityLevel("937_gsjm_2");
        List<BuildDeviceVO> deviceDOList = safetyBuildDeviceService.selectList(en);
        if (CollectionUtils.isEmpty(deviceDOList)) {
            return;
        }

        Map<String, SafetyBuildDeviceDO> deviceMap = deviceDOList.stream().collect(Collectors.toMap(
                SafetyBuildDeviceDO::getDeviceCode,
                device -> device,
                (newData, oldData) -> newData
        ));

        boolean hasTopSecretDevice = false;
        Set<String> secretEmpLoginSet = new HashSet<>();
        Set<String> secretEmpNameSet = new HashSet<>();
        for (String s : split) {
            SafetyBuildDeviceDO safetyBuildDeviceDO = deviceMap.get(s);
            if (safetyBuildDeviceDO != null) {
                hasTopSecretDevice = true;
                if (StringUtils.isNotBlank(safetyBuildDeviceDO.getTopSecretEmpLogin())) {
                    secretEmpLoginSet.add(safetyBuildDeviceDO.getTopSecretEmpLogin());
                }
                if (StringUtils.isNotBlank(safetyBuildDeviceDO.getTopSecretEmpName())) {
                    secretEmpNameSet.add(safetyBuildDeviceDO.getTopSecretEmpName());
                }
            }
        }

        if (hasTopSecretDevice) {
            entity.setTopSecretFlag("1");
        }
        entity.setTopSecretEmployeeLogin(String.join(",", secretEmpLoginSet));
        entity.setTopSecretEmployeeName(String.join(",", secretEmpNameSet));

    }

    /**
     * 检测门禁是否变动
     *
     * @param entity
     */
    private void deviceChange(SafetyBuildManageProcessDTO entity) {
        entity.setDevicedChange(0);
        //原门禁
        String oldDeviceCode = entity.getOldDeviceCode();
        //变更后门禁
        String newDeviceCode = entity.getNewDeviceCode();

        //二者都为空，代表不变动
        if (StringUtils.isBlank(oldDeviceCode) && StringUtils.isBlank(newDeviceCode)) {
            return;
        }

        //二者有一方不为空
        if ((StringUtils.isBlank(oldDeviceCode) && StringUtils.isNotBlank(newDeviceCode))
            || (StringUtils.isNotBlank(oldDeviceCode) && StringUtils.isBlank(newDeviceCode))
        ) {
            entity.setDevicedChange(1);
            return;
        }

        //双方都不为空
        String[] split = newDeviceCode.split(",");
        if(oldDeviceCode.length() != newDeviceCode.length()){
            entity.setDevicedChange(1);
            return;
        }
        for (String s : split) {
            if (!oldDeviceCode.contains(s)) {
                entity.setDevicedChange(1);
                break;
            }
        }
    }

    /**
     * 门禁系统管理员
     * @param entity
     */
    private void setBuildManage(SafetyBuildManageProcessDTO entity){
        //查询门禁系统管理员
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName("门禁系统管理员");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (CollectionUtils.isEmpty(list)) {return;}

        String strBuilder = list.stream().map(SafetySysUserDO::getUserName).distinct().collect(Collectors.joining(","));
        entity.setBuildSysManagerEip(strBuilder);
    }

    /**
     * 设置厂区巡逻组长
     *
     * @param entity
     */
    private void setPatrolLeader(SafetyBuildManageProcessDTO entity) {
        if (StringUtils.isBlank(entity.getNewDeviceCode())) {
            return;
        }

        List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectList(Arrays.asList(entity.getNewDeviceCode().split(",")));
        if(CollectionUtils.isEmpty(deviceDOS)){
            return;
        }
        String factoryId = deviceDOS.stream().filter(e->StringUtils.isNotBlank(e.getAnFactoryCode()))
                .map(SafetyBuildDeviceDO::getAnFactoryCode).distinct().collect(Collectors.joining(","));

        if (StringUtils.isBlank(factoryId)) {
            return;
        }
        //查询对应的角色下的人
        UserMessageListParam param = new UserMessageListParam();
        param.setPowerFactoryArr(factoryId);
        param.setRoleName("门禁巡逻组长");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        String loginBuild = list.stream().map(SafetySysUserDO::getUserName)
                .filter(name->StringUtils.isNotBlank(name)).distinct()
                .collect(Collectors.joining(","));
        String nameBuild = list.stream().map(SafetySysUserDO::getEmpName)
                .filter(name->StringUtils.isNotBlank(name)).distinct()
                .collect(Collectors.joining(","));

        entity.setPatrolEmployeeLogin(loginBuild);
        entity.setPatrolEmployeeName(nameBuild);
    }

    /**
     * 处理工厂楼栋
     * @param entity
     */
    public void dealFactory(SafetyBuildManageProcessDTO entity){
        if (StringUtils.isBlank(entity.getNewDeviceCode())) {
            return;
        }
        String newDeviceCode = entity.getNewDeviceCode();
        List<String> list = Arrays.asList(newDeviceCode.split(","));

        //根据门禁Code查询寻门禁信息
        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceService.selectList(list);
        if (CollectionUtils.isEmpty(deviceDOList)) {return;}

        String combFactoryName = deviceDOList.stream()
                .map(SafetyBuildDeviceDO::getAnFactoryName)
                .filter(name -> StringUtils.isNotBlank(name))
                .distinct().collect(Collectors.joining(","));
        String combBuild = deviceDOList.stream()
                .map(SafetyBuildDeviceDO::getAnBuilding)
                .filter(building -> StringUtils.isNotBlank(building))
                .distinct().collect(Collectors.joining(","));
        entity.setCombFactoryName(combFactoryName);
        entity.setCombBuild(combBuild);
    }

    public Object testCallbackEvent(String processId,String curNodeId){
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(curNodeId)) {
            LogUtils.info("保密区域管理责任部门调整流程异常-流程ID或实体类ID为空");
            return null;
        }
        SafetyBuildManageProcessDO param = new SafetyBuildManageProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyBuildManageProcessDO> processList = safetyBuildManageProcessService.getList(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("保密区域管理责任部门调整流程异常-未找到对应的流程信息");
                return null;
            }
            SafetyBuildManageProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(curNodeId);
            switch (curNodeId) {
                case "N4":
                    processDTO.setCurNodeName("公司绝密区负责人");
                    processDTO.setCurNodeHandler(processDTO.getTopSecretEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getTopSecretEmployeeName());
                    processDTO.setNodeFlag("1");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N5":
                    processDTO.setCurNodeName("本部门部长");
                    processDTO.setCurNodeHandler(processDTO.getLeaderEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getLeaderEmployeeName());
                    processDTO.setNodeFlag("2");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N10":
                    processDTO.setCurNodeName("门禁系统管理员");
                    processDTO.setCurNodeHandler(processDTO.getBuildSysManagerEip());
                    processDTO.setNodeFlag("3");
                    safetyBuildManageProcessService.update(processDTO);
                    break;
                case "N6":
                    //发宇信通消息
                    sendYXT(processDTO);
                    break;
                case "N3":
                    //非废弃流程才进行权限下发
                    if(!FLOW_STATUS_ABANDON.equals(processDTO.getProcessStatus())){
                        saveDetail(processDTO);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LogUtils.error("保密区域管理责任部门调整流程回调-处理逻辑异常", e);
        }
        return null;
    }
}
