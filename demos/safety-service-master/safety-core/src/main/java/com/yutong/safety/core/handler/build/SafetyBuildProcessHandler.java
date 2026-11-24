package com.yutong.safety.core.handler.build;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.*;
import com.yutong.safety.core.db.entity.build.vo.*;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEmpCardVO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.dto.SafetyClosePublicBuildParams;
import com.yutong.safety.core.dto.SafetyKeyValueDto;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.common.SafetySendYxtHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.*;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.employeeprivatecar.SafetyEmployeePrivateCarService;
import com.yutong.safety.core.service.faceUnqualified.SafetyFaceUnqualifiedService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SafetyBuildProcessHandler {

    private static final String PROCESS_MODEL_ID = "build_process";
    private static final String PROCESS_FORM_ID = "safety_build_process";
    private static final String BUILD_ADMIN_ROLE = "门禁系统管理员,超级管理员";
    private static final String ACCESS_CONTROL_ADMIN_ROLE = "门禁系统管理员";
    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;
    @Value("${iam.apikey}")
    private String apikey;

    private static final String FLOW_STATUS_ABANDON = "00";
    private static final String ISSUED_TYPE_OPEN = "开通";
    private static final String ISSUED_TYPE_CLOSE = "关闭";
    private static final String ISSUED_STATUS_NOT_ISSUED = "未下发";
    private static final String SOURCE_TYPE_PROCESS = "1";

    @Resource
    private SafetyBuildProcessService safetyBuildProcessService;
    @Resource
    private SafetyBpmProcessService safetyBpmProcessService;
    @Resource
    private SafetyWaterFlowNoService safetyWaterFlowNoService;
    @Resource
    private SafetySysUserService safetySysUserService;
    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;
    @Resource
    private SafetyBuildProcessOpenService safetyBuildProcessOpenService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;
    @Resource
    private SafetyBuildAutoDeptService safetyBuildAutoDeptService;
    @Resource
    private SafetyBpmWebServiceService safetyBpmWebServiceService;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private SafetyBuildWhiteListService safetyBuildWhiteListService;
    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;
    @Autowired
    private SafetyBuildInfoConfigService safetyBuildInfoConfigService;
    @Autowired
    private SafetyBuildInfoRecordService safetyBuildInfoRecordService;
    @Autowired
    private SafetyBuildConfigService safetyBuildConfigService;
    @Autowired
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Autowired
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;
    @Autowired
    private SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Autowired
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Autowired
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyEmployeePrivateCarService safetyEmployeePrivateCarService;
    @Autowired
    private SafetyBuildManageProcessService safetyBuildManageProcessService;

    @Resource
    private SafetySendYxtHandler safetySendYxtHandler;

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;
    @Resource
    private SafetyFaceUnqualifiedService safetyFaceUnqualifiedService;
    @Resource
    private EipCommonService eipCommonService;

    /**
     * 创建流程-保存信息
     * @param dto 入参
     * @return 返回值
     */
    public Map<String,Object> saveAndCreateProcessByUserName(BuildProcessAddParam dto,String token)
            throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        String flowId = "";
        String creator = dto.getCreateEmployeeLogin();
        String uuid = UUID.randomUUID().toString().replace("-","");
        dto.setId(uuid);
        dto.setProcessCode(getFlowNo());
        dto.setProcessSubject(dto.getCreateEmployeeName()+dto.getCreateEmployeeNo()+"提交的门禁权限调整流程");

        //根据当前用户类型 设置检索条件 2-门禁系统管理员 3-门禁管理员
        String userType = checkUserType(token);
        //门禁管理员且有开通权限时校验
        if(userType.startsWith("3")){
            //校验门禁管理员申请的人员或门禁归自己管理
            String result = validSubmitForCreate(dto,token);
            if(!"true".equals(result)){
                throw new BizException(ResponseEnum.FAIL.getCode(),result);
            }
        }
        if(!"2".equals(userType)){
            String s = validWhite(dto);
            if(StringUtils.isNotBlank(s)){
                resultMap.put("success",false);
                resultMap.put("mes",s + "在白名单中，故流程不能提交！");

                SafetyBuildInfoRecordDO safetyBuildInfoRecordDO = new SafetyBuildInfoRecordDO();
                safetyBuildInfoRecordDO.setInfoConfirm("1");
                String mes = dto.getCreateEmployeeName()
                        + "于"
                        + DateUtils.format(new Date(),2)
                        + "关闭白名单锁定人员"
                        + s
                        +"失败，请及时跟进处理";
                safetyBuildInfoRecordDO.setInfoContent(mes);
                safetyBuildInfoRecordDO.setInfoType("白名单授权");
                safetyBuildInfoRecordDO.setInfoTime(DateUtils.format(new Date(),2));
                safetyBuildInfoRecordService.save(safetyBuildInfoRecordDO);

                // 给门禁管理员发送宇信通消息
                sendYuXinTongMsg(mes);

                return resultMap;
            }
        }

        int row = safetyBuildProcessService.save(dto);
        if(row>0){
            //手动提交事务 liuc
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(dto.getProcessSubject(),
                    PROCESS_MODEL_ID, PROCESS_FORM_ID, creator, uuid);
            if (StringUtils.isBlank(returnFlowId)) {
                throw new BizException(ResponseEnum.FAIL.getCode(),"创建流程异常返回值"+returnFlowId);
            }
            flowId = returnFlowId.split(",")[1];
            dto.setBpmFlowId(flowId);
            safetyBuildProcessService.modifyFlowId(dto);

            //消息提醒
            List<SafetyBuildProcessOpenDO> openDOList = dto.getOpenDOList();
            sendMessageForBuildCreate(openDOList);
        }
        resultMap.put("success",true);
        resultMap.put("mes",uuid+","+flowId);
        return resultMap;
    }

    /**
     * app端提交流程方法
     * @param dto 入参
     * @throws Exception 异常
     */
    public Map<String,Object> saveAndCreateProcessByUserNameApp(BuildProcessAddParam dto)
            throws Exception{
        List<SafetyBuildInfoRecordDO> recordList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        String flowId = "";
        String creator = dto.getCreateEmployeeLogin();
        String uuid = UUID.randomUUID().toString().replace("-","");
        dto.setId(uuid);
        dto.setProcessCode(getFlowNo());
        dto.setProcessSubject(dto.getCreateEmployeeName()+dto.getCreateEmployeeNo()+"提交的门禁权限调整流程");

        int row = safetyBuildProcessService.save(dto);
        if(row>0){
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(dto.getProcessSubject(),
                    PROCESS_MODEL_ID, PROCESS_FORM_ID, creator, uuid);
            if (StringUtils.isBlank(returnFlowId)) {
                throw new BizException(ResponseEnum.FAIL.getCode(),"创建流程异常返回值"+returnFlowId);
            }
            flowId = returnFlowId.split(",")[1];
            dto.setBpmFlowId(flowId);
            safetyBuildProcessService.modifyFlowId(dto);

            //消息提醒
            List<SafetyBuildProcessOpenDO> openDOList = dto.getOpenDOList();
            sendMessageForBuildCreate(openDOList);
        }
        resultMap.put("success",true);
        resultMap.put("mes",uuid+","+flowId);
        return resultMap;
    }

    /**
     * 创建门禁流程，发消息提醒并保存提醒记录
     * @param openDOList 开通的门禁
     */
    private void sendMessageForBuildCreate(List<SafetyBuildProcessOpenDO> openDOList){
        List<SafetyBuildInfoRecordDO> recordList = new ArrayList<>();
        //查询规则
        List<SafetyBuildConfigDO> configDOList = safetyBuildConfigService.query();
        if(!CollectionUtils.isEmpty(openDOList) && !CollectionUtils.isEmpty(configDOList)){
            SafetyBuildConfigDO safetyBuildConfigDO = configDOList.get(0);
            Integer buildGrantNum = safetyBuildConfigDO.getBuildGrantNum();

            for (SafetyBuildProcessOpenDO processOpenDO : openDOList) {
                if ("2".equals(processOpenDO.getEmpowerType())){continue;}
                String empowerAreaName = processOpenDO.getEmpowerAreaName();
                int length = empowerAreaName.split(";").length;
                if(length > buildGrantNum){
                    SafetyBuildInfoRecordDO safetyBuildInfoRecordDO = new SafetyBuildInfoRecordDO();
                    safetyBuildInfoRecordDO.setInfoConfirm("1");
                    String mes = processOpenDO.getDeviceName()
                            + "门禁通过流程"
                            + processOpenDO.getProcessId()
                            + "批量授权超过"
                            + buildGrantNum
                            +"人，请确认";
                    safetyBuildInfoRecordDO.setInfoContent(mes);
                    safetyBuildInfoRecordDO.setInfoType("批量授权");
                    safetyBuildInfoRecordDO.setInfoTime(DateUtils.format(new Date(),2));
                    recordList.add(safetyBuildInfoRecordDO);
                }
            }
            if (!CollectionUtils.isEmpty(recordList)) {
                safetyBuildInfoRecordService.batchSave(recordList);
            }
        }
    }

    /**
     * 校验白名单
     * @param dto 入参
     * @return 校验结果
     */
    public String validWhite(BuildProcessAddParam dto){
        if("1".equals(dto.getPowerType())){
            return "";
        }

        List<SafetyBuildProcessCloseDO> closeList = dto.getCloseList();
        if(CollectionUtils.isEmpty(closeList)){return "";}

        ArrayList<String> empCodeList = new ArrayList<>();
        for (SafetyBuildProcessCloseDO en : closeList) {
            String empowerType = en.getEmpowerType();
            if("2".equals(empowerType)){continue;}

            empCodeList.add(en.getEmpowerAreaCode());
        }
        if(CollectionUtils.isEmpty(empCodeList)){
            empCodeList.add("-1");
        }
        //查询白名单
        List<SafetyBuildWhiteListDO> list = safetyBuildWhiteListService.selectByEmpCodeList(empCodeList);
        if(CollectionUtils.isEmpty(list)){return "";}

        LogUtils.info("楼宇门禁申请流程在关闭的时候的涉及的白名单人={}",JSON.toJSONString(list));
        StringBuilder sb = new StringBuilder();
        for (SafetyBuildWhiteListDO en : list) {
            if(sb.indexOf(en.getEmployeeName()) != -1){continue;}
            sb.append(en.getEmployeeName())
                    .append("、");
        }

        return sb.substring(0,sb.length()-1);
    }

    public String validSubmitForCreate(BuildProcessAddParam dto,String token){
        List<SafetyBuildProcessOpenDO> openList = dto.getOpenDOList();
        List<SafetyBuildProcessCloseDO> closeList = dto.getCloseList();
        if((openList == null || openList.isEmpty()) &&(closeList == null || closeList.isEmpty())){
            return "true";
        }
        //根据当前用户类型 设置检索条件 2-管理员 3-保密负责人
        String empNo = TokenUtils.getWorkNo(token);
        if (StringUtils.isBlank(empNo)) {
            return "false";
        }
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(empNo);
        List<SafetyBuildPowerDO> manePowerList = safetyBuildPowerService.selectList(listParam);
        if(CollectionUtils.isEmpty(manePowerList)){
            return "false";
        }
        //管理的设备
        List<String> manageDeviceList = manePowerList.stream().map(SafetyBuildPowerDO::getDeviceCode)
                .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<String> manageDeptList = manePowerList.stream().map(SafetyBuildPowerDO::getDutyDeptCode)
                .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

        if(manageDeptList.isEmpty() && manageDeviceList.isEmpty()){
            return "false";
        }
        StringBuilder sbd = new StringBuilder();
        if(!CollectionUtils.isEmpty(openList)){
            String openErrorMsg = validOpenListForCreate(openList,manageDeviceList,manageDeptList);
            if(StringUtils.isNotBlank(openErrorMsg)){
                sbd.append("开通权限校验：").append(openErrorMsg).append(";");
            }
        }
        if(!CollectionUtils.isEmpty(closeList)){
            String closeErrorMsg = validCloseListForCreate(closeList,manageDeviceList,manageDeptList);
            if(StringUtils.isNotBlank(closeErrorMsg)){
                sbd.append("关闭权限校验：").append(closeErrorMsg).append(";");
            }
        }
        return "".contentEquals(sbd)?"true":sbd.toString();
    }

    /**
     * 更新流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateAndAuditByUserName(BuildProcessUpdateParam param, String token) throws Exception{
        // 入参校验
        checkParam(param, token);
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonLogin();
        SafetyBuildProcessDO curBuildProcessDO = safetyBuildProcessService.selectByPrimaryKey(param.getId());
        String curNodeId = curBuildProcessDO.getCurNodeId();
        SafetyBuildProcessDOWithBLOBs bloBs = new SafetyBuildProcessDOWithBLOBs();
        //N11节点不通过时，废弃流程
        if("N11".equals(curNodeId) && !"1".equals(param.getApplyStatus())){
            bloBs.setId(curBuildProcessDO.getId());
            bloBs.setStatus(FLOW_STATUS_ABANDON); //废弃状态
            bloBs.setCurNodeHandler("");
            safetyBuildProcessService.updateByPrimaryKeySelective(bloBs);
        }else{
            this.modifyProcessInfo(param,curBuildProcessDO);
        }
        //提取流程的审批意见
        String returnAuditNote = initAuditNote(param);
        BpmProcessParam processParam;
        if(FLOW_STATUS_ABANDON.equals(bloBs.getStatus())){
            processParam = safetyBpmProcessService.initProcessAbandonParam(param.getId(),param.getFlowId(),
                    PROCESS_MODEL_ID,PROCESS_FORM_ID,curPersonNo,returnAuditNote);
        }else{
            processParam = safetyBpmProcessService.initProcessParam(param.getId(),param.getFlowId(),
                    PROCESS_MODEL_ID,PROCESS_FORM_ID,curPersonNo,false,returnAuditNote,"");
        }
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(), param.getFlowId(),
                PROCESS_MODEL_ID, PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);
        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        assert status != null;
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);
        return returnVO;
    }

    /**
     * 整理审批意见
     * @param entity 入参
     */
    private String initAuditNote(BuildProcessUpdateParam entity){
        String returnAduit = entity.getAuditNote();
        if(StringUtils.isNotBlank(entity.getFdParameterJson())){
            try{
                com.alibaba.fastjson.JSONObject paramJson = com.alibaba.fastjson.JSONObject.parseObject(entity.getFdParameterJson());
                if(paramJson != null){
                    com.alibaba.fastjson.JSONObject auditParam = paramJson.getJSONObject("param");
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
    /**
     * 更新安保流程数据信息@
     * @param param 入参
     */
    public void modifyProcessInfo(BuildProcessUpdateParam param,SafetyBuildProcessDO curBuildProcessDO) {
        String id = param.getId();
        String applyTime = DateUtils.format(new Date(),2);
        String refause = "审批通过";
        if("2".equals(param.getApplyStatus())){
            refause = param.getCurPersonName()+"审批不通过";
            if(StringUtils.isBlank(param.getAuditNote())){
                param.setAuditNote("审核不通过");
            }
        }

        String openIdStr = param.getOpenIdStr();
        if(StringUtils.isNotBlank(openIdStr)){
            String[] openDOList = openIdStr.split(",");
            List<SafetyBuildProcessOpenDO> processOpenDOList = new ArrayList<>();
            for(String openId :openDOList){
                SafetyBuildProcessOpenDO curOpen = new SafetyBuildProcessOpenDO();
                curOpen.setId(openId);
                curOpen.setProcessId(param.getId());
                curOpen.setApplyStatus(param.getApplyStatus());
                curOpen.setRefuseReason(refause);
                curOpen.setApplyEmployee(param.getCurPersonName());
                curOpen.setApplyTime(applyTime);
                processOpenDOList.add(curOpen);
            }
            safetyBuildProcessService.batchUpdateOpen(processOpenDOList);
        }
        String closeIdStr = param.getCloseIdStr();
        if(StringUtils.isNotBlank(closeIdStr)){
            String[] closeDOList = closeIdStr.split(",");
            List<SafetyBuildProcessCloseDO> processCloseDOList = new ArrayList<>();
            for(String closeId:closeDOList){
                SafetyBuildProcessCloseDO curClose = new SafetyBuildProcessCloseDO();
                curClose.setId(closeId);
                curClose.setProcessId(param.getId());
                curClose.setApplyStatus(param.getApplyStatus());
                curClose.setRefuseReason(refause);
                curClose.setApplyEmployee(param.getCurPersonName());
                curClose.setApplyTime(applyTime);
                processCloseDOList.add(curClose);
            }
            safetyBuildProcessService.batchUpdateClose(processCloseDOList);
        }
        BuildProcessOpenListParam listParam = new BuildProcessOpenListParam();
        listParam.setProcessId(id);
        List<SafetyBuildProcessOpenDO> openDOList1 = safetyBuildProcessService.selectOpenList(listParam);
        boolean openRefuseFlag = true;
        if (!CollectionUtils.isEmpty(openDOList1)) {
            //提取未审批或者审批通过的数量，判断是否>0
            List<SafetyBuildProcessOpenDO> normalOpenList = openDOList1.stream().filter(e->StringUtils.isBlank(e.getApplyStatus())||"1".equals(e.getApplyStatus())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(normalOpenList)){
                openRefuseFlag = false;
            }
        }

        SafetyBuildProcessDOWithBLOBs buildProcessDO = new SafetyBuildProcessDOWithBLOBs();
        //开通列表全部拒绝后，设置表拒绝标记
        if(openRefuseFlag && !"2".equals(curBuildProcessDO.getPowerType())){
            buildProcessDO.setId(id);
            buildProcessDO.setRefuseFlag("1");
        }
        //员工提交，本部门负责人设置为审批人
        if("1".equals(curBuildProcessDO.getRoleType()) &&"N5".equals(curBuildProcessDO.getCurNodeId())){
            buildProcessDO.setId(id);
            buildProcessDO.setDepartEmployeeLogin(param.getCurPersonLogin());
            buildProcessDO.setDepartEmployeeName(param.getCurPersonName());
        }
        if(StringUtils.isNotBlank(buildProcessDO.getId())){
            safetyBuildProcessService.updateByPrimaryKeySelective(buildProcessDO);
        }
    }

    /**
     * 分页查询
     * @param param 查询参数
     * @return 结果集
     */
    public Object getPageList(BuildProcessListParam param,String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"token已失效");
        }
        if(StringUtils.isNotBlank(param.getCreateTimeEnd())){
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getCreateTimeEnd(),1),1),1);
            param.setCreateTimeEnd(endDate);
        }
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = this.checkUserType(token);
        if(!"2".equals(userType)){
            param.setCreateEmpLogin(userName);
        }
        if(userType.startsWith("3")){
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        }else{
            param.setFilterDutyDept(null);
        }
        param.setPageFlag(1);
        String bpmPreUrl = safetyBpmWebServiceService.getBpmPreUrl(PROCESS_FORM_ID);
        param.setBpmPreUrl(bpmPreUrl);
        List<BuildProcessListVO> voList = safetyBuildProcessService.selectPageListWithBlobs(param);
        // 获取所有数据
        List<SafetyBuildDeviceDO> safetyBuildDeviceDOS = safetyBuildDeviceService.selectByExample(new BuildDeviceListParam());
        Map<String, SafetyBuildDeviceDO> buildDataMap = safetyBuildDeviceDOS.stream().filter(li -> StringUtils.isNotBlank(li.getDeviceCode())).collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, t -> t, (t1, t2) ->t2));

        if(!CollectionUtils.isEmpty(voList)){
            // 关闭流程的数据ID -> 获得关闭流程的信息
            List<String> ids = voList.stream().filter(li -> "2".equals(li.getPowerType())).map(SafetyBuildProcessDO::getId).collect(Collectors.toList());
            List<SafetyBuildProcessCloseDO> closeDOList = safetyBuildProcessService.selectCloseListByProcessIds(ids);
            Map<String, List<SafetyBuildProcessCloseDO>> dataMap = closeDOList.stream().collect(Collectors.groupingBy(SafetyBuildProcessCloseDO::getProcessId));

            List<String> orgCode = voList.stream().filter(e-> StringUtils.isNotBlank(e.getPowerType()))
                    .map(e->PinYinUtils.getAfterLastComma(e.getCreateDeptCode(),",")).distinct().collect(Collectors.toList());

            List<BuildGroupDutyVO> buildPowerDOS = safetyBuildPowerService.getGroupDuty(orgCode);
            Map<String,BuildGroupDutyVO> deptDutyMap;
            List<String> dutyCodeList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(buildPowerDOS)){
                dutyCodeList = buildPowerDOS.stream().map(BuildGroupDutyVO::getDutyDeptCode).collect(Collectors.toList());
                deptDutyMap = buildPowerDOS.stream().collect(Collectors.toMap(
                        BuildGroupDutyVO::getCode,
                        dto -> dto,
                        (oldValue, newValue) -> oldValue
                ));
            }else{
                deptDutyMap = new HashMap<>();
            }
            //获取员工组织对应的责任部门对应的责任人
            //所有设备对应的责任部门
            Map<String,List<SafetyBuildPowerDO>> deviceDutyMap;
            BuildPowerListParam buildPowerListParam = new BuildPowerListParam();
            buildPowerListParam.setDutyDeptCodeList(dutyCodeList);
            List<SafetyBuildPowerDO> deviceDutyList = safetyBuildPowerService.selectList(buildPowerListParam);
            if(!CollectionUtils.isEmpty(deviceDutyList)){
                deviceDutyMap = deviceDutyList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));
            }else{
                deviceDutyMap = new HashMap<>();
            }
            voList.forEach(cur->{
                String powerType = cur.getPowerType();
                if ("2".equals(powerType)) {
                    // 关闭门禁的门禁信息
                    List<SafetyBuildProcessCloseDO> closeDOS = dataMap.get(cur.getId());
                    if (!CollectionUtils.isEmpty(closeDOS)) {
                        String deviceName = closeDOS.stream().map(SafetyBuildProcessCloseDO::getDeviceName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(";"));
                        cur.setMontageDeviceNameStr(deviceName);
                        List<String> names = new ArrayList<>();
                        List<String> builds = new ArrayList<>();
                        closeDOS.forEach(ll -> {
                            String deviceCode = ll.getDeviceCode();
                            if (StringUtils.isNotBlank(deviceCode)) {
                                List<String> deviceCodes = Arrays.asList(deviceCode.split(","));
                                List<SafetyBuildDeviceDO> lastList = deviceCodes.stream().map(buildDataMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                                names.addAll(lastList.stream().map(SafetyBuildDeviceDO::getAnFactoryName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
                                builds.addAll(lastList.stream().map(SafetyBuildDeviceDO::getAnBuilding).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
                            }
                        });
                        cur.setMontageFactoryNameStr(String.join(";", names));
                        cur.setMontageBuildNameStr(String.join(";", builds));
                    }
                }
                String curOrg = PinYinUtils.getAfterLastComma(cur.getCreateDeptCode(),",");
                BuildGroupDutyVO dutyDept = deptDutyMap.get(curOrg);
                if(ObjectUtils.isEmpty(dutyDept)){
                    return;
                }
                String dutyDeptCode = dutyDept.getDutyDeptCode();
                cur.setDutyDeptCode(dutyDeptCode);
                cur.setDutyDeptCode(dutyDept.getDutyDeptName());
                List<SafetyBuildPowerDO> buildPowerDOList = deviceDutyMap.get(dutyDeptCode);
                if(CollectionUtils.isEmpty(buildPowerDOList)){
                    return;
                }
                cur.setCurDutyPerson(buildPowerDOList.stream().map(SafetyBuildPowerDO::getEmployeeName).distinct().collect(Collectors.joining(",")));
            });
        }
        long total = safetyBuildProcessService.countWithBlobs(param);
        return new PageVO<>(total, voList);
    }

    /**
     * 分页查询
     * @param param 查询参数
     * @return 结果集
     */
    public List<BuildProcessListVO> getList(BuildProcessListParam param,String token){
        return safetyBuildProcessService.selectListWithBlobs(param);
    }


    public HSSFWorkbook export(BuildProcessListParam param, String token) {
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"token已失效");
        }
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = this.checkUserType(token);
        if(!"2".equals(userType)){
            param.setCreateEmpLogin(userName);
        }
        if(userType.startsWith("3")){
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        }else{
            param.setFilterDutyDept(null);
        }
        String bpmPreUrl = safetyBpmWebServiceService.getBpmPreUrl(PROCESS_FORM_ID);
        param.setBpmPreUrl(bpmPreUrl);

        List<BuildProcessListVO> list = safetyBuildProcessService.selectListWithBlobs(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"流程编码", "提交时间", "申请人", "角色","申请人部门","厂区","楼栋","申请门禁",
                "对接部门","申请授权期限","流程状态","当前责任人","流程链接"};
        String[][] values = new String[list.size()][];
        BuildProcessListVO buildProcessListVO;
        for(int i=0; i<list.size(); i++) {
            buildProcessListVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = buildProcessListVO.getProcessCode();
            values[i][1] = buildProcessListVO.getCreateTime();
            values[i][2] = buildProcessListVO.getCreateEmployeeName();
            values[i][3] = buildProcessListVO.getRoleType();
            values[i][4] = buildProcessListVO.getCreateDeptName();
            values[i][5] = buildProcessListVO.getMontageFactoryNameStr();
            values[i][6] = buildProcessListVO.getMontageBuildNameStr();
            values[i][7] = buildProcessListVO.getMontageDeviceNameStr();
            values[i][8] = buildProcessListVO.getMontageDeptStr();
            values[i][9] = buildProcessListVO.getMontageTimeStr();
            values[i][10] = buildProcessListVO.getStatus();
            values[i][11] = buildProcessListVO.getCurDutyPerson();
            values[i][12] = buildProcessListVO.getFlowUrl();
        }
        String sheetName = "楼宇门禁申请流程列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }


    /**
     * 根据主键ID获取详情
     * @param id 主键ID
     * @return 返回详情页
     */
    public BuildProcessVO showProcessDetail(String id,String token){
        String curUserName = TokenUtils.getUserName(token);
        BuildProcessVO vo = new BuildProcessVO();
        SafetyBuildProcessDO processDO = safetyBuildProcessService.selectByPrimaryKey(id);
        if(processDO == null){
            return vo;
        }
        BeanUtils.copyProperties(processDO,vo);
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = "1";
        if(StringUtils.isNotBlank(token)){
            userType = this.checkUserType(token);
        }

        BuildProcessOpenListParam openListParam = new BuildProcessOpenListParam();
        openListParam.setProcessId(id); //主流程ID

        if(StringUtils.isBlank(curUserName) || "30".equals(processDO.getStatus())
                || "00".equals(processDO.getStatus())
                || curUserName.equals(processDO.getCreateEmployeeLogin()) || "2".equals(userType)){
            //包含开通功能的走以下逻辑判断
            if(processDO.getPowerType().contains("1")){
                List<SafetyBuildProcessOpenDO> openDOList = safetyBuildProcessService.selectOpenList(openListParam);
                vo.setOpenList(openDOList);
            }
            if(processDO.getPowerType().contains("2")){
                List<SafetyBuildProcessCloseDO> closeDOList = safetyBuildProcessService.selectCloseList(id);
                vo.setCloseList(closeDOList);
            }
        }else{
            String curNodeId = "";
            Object curInfo = safetyBpmProcessService.getCurrentNodesInfo(id,processDO.getBpmFlowId(),PROCESS_FORM_ID);
            if(StringUtils.isNotBlank(String.valueOf(curInfo))){
                JSONArray jsonArray = new JSONArray(String.valueOf(curInfo));
                if(!jsonArray.isEmpty()){
                    JSONObject curObj = jsonArray.getJSONObject(0);
                    curNodeId = curObj.getString("nodeId");
                }
            }
            //包含开通功能的走以下逻辑判断,本部门审批节点也展示全部
            if(processDO.getPowerType().contains("1")){
                //openListParam.setApplyStatus("1"); //只展示审批通过的
                if(!"N2".equals(curNodeId) && !"N11".equals(curNodeId)&& !"N5".equals(curNodeId) && !"N16".equals(curNodeId)){
                    openListParam.setCurAuditLogin(curUserName);
                }
                List<SafetyBuildProcessOpenDO> openDOList = safetyBuildProcessService.selectOpenList(openListParam);
                vo.setOpenList(openDOList);
            }
            if(processDO.getPowerType().contains("2") && ("N2".equals(curNodeId) || "N11".equals(curNodeId)) || "N16".equals(curNodeId)){
                List<SafetyBuildProcessCloseDO> closeDOList = safetyBuildProcessService.selectCloseList(id);
                vo.setCloseList(closeDOList);
            }
        }
        return vo;
    }

    /**
     * 生成流水号
     * @return 返回编码
     */
    private String getFlowNo(){
        String date = DateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("safety_build_process");
        return Joiner.on("").join("LYMJSQ",date+nowNum);
    }


    /**
     * 根据主键ID获取详情
     * @param id 主键ID
     * @return 返回详情页
     */
    public SafetyBuildProcessDO getProcessByPrimaryKey(String id){
        return safetyBuildProcessService.selectByPrimaryKey(id);
    }

    /**
     * 更新流程状态
     * @param bpmFlowId BPM流程ID
     * @param status 状态
     */
    @Async
    public void updateFlowStatus(String bpmFlowId,String status){
        LogUtils.info("进入状态更新时间");
        BuildProcessListParam listParam = new BuildProcessListParam();
        listParam.setBpmFlowId(bpmFlowId);
        List<SafetyBuildProcessDO> list = safetyBuildProcessService.selectList(listParam);
        if(CollectionUtils.isEmpty(list)){
            LogUtils.error(ResponseEnum.FAIL.getCode(),"未找到对应ID="+bpmFlowId+"的BPM流程的员工权限调整信息");
            return;
        }
        SafetyBuildProcessDO buildProcessDO = list.get(0);
        //已废弃的流程直接退出
        if(FLOW_STATUS_ABANDON.equals(buildProcessDO.getStatus())){
            return;
        }
        try{
            SafetyBuildProcessDOWithBLOBs bloBs = new SafetyBuildProcessDOWithBLOBs();
            bloBs.setId(buildProcessDO.getId());
            bloBs.setStatus(status);
            //更新发布状态
            safetyBuildProcessService.updateByPrimaryKeySelective(bloBs);
            safetyBuildProcessService.updateOpenStatusByProcessId(buildProcessDO.getId());
        }catch (Exception e){
            LogUtils.error("楼宇门禁人员权限调整流程状态更新失败，bpmFlowId="+bpmFlowId+",{}",e);
        }
        //发布状态
        try{
            sendPowerByProcessNew(buildProcessDO.getId());
        }catch (Exception e){
            LogUtils.error(ResponseEnum.FAIL.getCode(),"记录下发日志异常："+e);
        }
    }
    /**
     * 根据流程主键ID发送宇信通消息
     * @param buildProcessDO 流程实体
     */
    public void sendYxtMsg(SafetyBuildProcessDO buildProcessDO){
        String processId = buildProcessDO.getId();
        if("1".equals(buildProcessDO.getRoleType())){
            safetySendYxtHandler.sendYxtMsgByPerson(buildProcessDO.getCreateEmployeeNo(), processId, buildProcessDO.getBpmFlowId());
        }else if("3".equals(buildProcessDO.getRoleType())){
            SafetyBuildIssuedLogDO issuedLogDO = new SafetyBuildIssuedLogDO();
            issuedLogDO.setTargetId(processId);
            issuedLogDO.setIssuedStatus("成功");
            List<SafetyBuildIssuedLogDO> issuedLogDOS = safetyBuildIssuedLogService.selectList(issuedLogDO);
            List<SafetyBuildIssuedLogDO> openList = issuedLogDOS.stream().filter(e->"开通".equals(e.getIssuedType())).collect(Collectors.toList());
            List<SafetyBuildIssuedLogDO> closeList = issuedLogDOS.stream().filter(e->"关闭".equals(e.getIssuedType())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(openList) && CollectionUtils.isEmpty(closeList) ){
                return;
            }
            Set<String> empCode = issuedLogDOS.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).collect(Collectors.toSet());
            safetySendYxtHandler.sendYxtMsgBySecrecyManager(empCode, buildProcessDO.getCreateEmployeeName());
        }
    }

    /**
     * 根据流程主键ID发送宇信通消息
     * @param buildProcessDO 流程实体
     */
    public void sendYxtMessage(SafetyBuildProcessDO buildProcessDO){
        String processId = buildProcessDO.getId();
        SafetyBuildInfoConfigDO en = new SafetyBuildInfoConfigDO();
        en.setShowFlag("1");
        RestTemplate restTemplate = new RestTemplate();
        if("1".equals(buildProcessDO.getRoleType())){
            String bpmPreUrl = safetyBuildManageProcessService.getBpmPreUrl(PROCESS_FORM_ID);
            String processLink = String.format("%s&formInstanceId=%s&processId==%s",bpmPreUrl,
                    buildProcessDO.getId(),buildProcessDO.getBpmFlowId());
            en.setId(1);
            //查询消息模板
            List<SafetyBuildInfoConfigDO> infoModelList = safetyBuildInfoConfigService.query(en);
            if(CollectionUtils.isEmpty(infoModelList)){return;}
            SafetyBuildInfoConfigDO info = infoModelList.get(0);

            String toEmpCode = buildProcessDO.getCreateEmployeeNo();
            String mes = info.getInfoContent()
                    .replace("{buildProcessLink}", processLink);

            yxtRebotUtils.postForEntity(null,incomintRebotId,toEmpCode,mes,restTemplate);
        }else if("3".equals(buildProcessDO.getRoleType())){
            String powerType = buildProcessDO.getPowerType();
            if("1".equals(powerType) || "2".equals(powerType)){
                en.setId(2);
            }else{
                en.setId(9);
            }
            StringBuilder messageBuilder = new StringBuilder();
            //门禁管理员
            String createEmpName = buildProcessDO.getCreateEmployeeName();
            //查询消息模板
            List<SafetyBuildInfoConfigDO> infoModelList = safetyBuildInfoConfigService.query(en);
            if(CollectionUtils.isEmpty(infoModelList)){return;}
            SafetyBuildInfoConfigDO info = infoModelList.get(0);

            SafetyBuildIssuedLogDO issuedLogDO = new SafetyBuildIssuedLogDO();
            issuedLogDO.setTargetId(processId);
            issuedLogDO.setIssuedStatus("成功");
            List<SafetyBuildIssuedLogDO> issuedLogDOS = safetyBuildIssuedLogService.selectList(issuedLogDO);
            List<SafetyBuildIssuedLogDO> openList = issuedLogDOS.stream().filter(e->"开通".equals(e.getIssuedType())).collect(Collectors.toList());
            List<SafetyBuildIssuedLogDO> closeList = issuedLogDOS.stream().filter(e->"关闭".equals(e.getIssuedType())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(openList) && CollectionUtils.isEmpty(closeList) ){
                return;
            }
            Set<String> empCode = issuedLogDOS.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).collect(Collectors.toSet());

            Map<String,List<SafetyBuildIssuedLogDO>> empMap = openList.stream()
                    .collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
            Map<String,List<SafetyBuildIssuedLogDO>> excMap = closeList.stream()
                    .collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
            empCode.forEach((key)->{
                List<SafetyBuildIssuedLogDO> empOpens = empMap.get(key);
                List<SafetyBuildIssuedLogDO> empCloses = excMap.get(key);
                String openDeviceName = CollectionUtils.isEmpty(empOpens)?"":empOpens.stream().map(SafetyBuildIssuedLogDO::getDeviceName)
                        .distinct().collect(Collectors.joining(","));
                String closeDeviceName = CollectionUtils.isEmpty(empCloses)?"":empCloses.stream().map(SafetyBuildIssuedLogDO::getDeviceName)
                        .distinct().collect(Collectors.joining(","));
                messageBuilder.setLength(0); // Clear the StringBuilder for reuse
                messageBuilder.append(info.getInfoContent());

                if ("1".equals(powerType)) {
                    messageBuilder.replace(messageBuilder.indexOf("{grantBuild}"), messageBuilder.indexOf("{grantBuild}") + "{grantBuild}".length(), openDeviceName)
                            .replace(messageBuilder.indexOf("{powerType}"), messageBuilder.indexOf("{powerType}") + "{powerType}".length(), "开通");
                } else if ("2".equals(powerType)) {
                    messageBuilder.replace(messageBuilder.indexOf("{grantBuild}"), messageBuilder.indexOf("{grantBuild}") + "{grantBuild}".length(), closeDeviceName)
                            .replace(messageBuilder.indexOf("{powerType}"), messageBuilder.indexOf("{powerType}") + "{powerType}".length(), "关闭");
                } else {
                    messageBuilder.replace(messageBuilder.indexOf("{openBuild}"), messageBuilder.indexOf("{openBuild}") + "{openBuild}".length(), openDeviceName)
                            .replace(messageBuilder.indexOf("{closeBuild}"), messageBuilder.indexOf("{closeBuild}") + "{closeBuild}".length(), closeDeviceName);
                }

                messageBuilder.replace(messageBuilder.indexOf("{secretUser}"), messageBuilder.indexOf("{secretUser}") + "{secretUser}".length(), createEmpName);

                String mes = messageBuilder.toString();
                yxtRebotUtils.postForEntity(null, incomintRebotId, key, mes, restTemplate);
            });
        }
    }

    /**
     * 开通门禁权限处理逻辑
     * @param openDOList 开通权限
     * @param safetyBuildProcessDO 入参
     * @param processId 流程ID
     * @param dutyDeptCode 责任部门编码
     * @param dutyDeptName 责任部门名称
     * @param changeEmpList 变更人员工号集合
     */
    public void dealBuildOpenList(Set<String> changeEmpList,List<SafetyBuildProcessOpenDO> openDOList,SafetyBuildProcessDO safetyBuildProcessDO,
                                  String processId,String dutyDeptCode,String dutyDeptName){
        Map<String,List<SafetyBuildProcessOpenDO>> openMap = openDOList.stream().collect(Collectors.groupingBy(SafetyBuildProcessOpenDO::getEmpowerType));
        List<SafetyBuildProcessOpenDO> empOpenList = openMap.get("1"); //员工
        List<SafetyBuildProcessOpenDO> deptOpenList = openMap.get("2"); //根据部门授权
        List<SafetyBuildIssuedLogDO> logDOS =  new ArrayList<>();
        if(!CollectionUtils.isEmpty(empOpenList)){
            Map<String,SafetyEmployeeMessageDO> userMap = getEmployeeByOpenList(empOpenList);
            // 获取员工非公共楼宇的最大截止日期信息 key:员工号+楼栋编码  value：最大日期
            Map<String, String> maxEndTimeByEmpNo = getMaxEndTimeByEmpNo(new ArrayList<>(userMap.keySet()));
            for(SafetyBuildProcessOpenDO processOpenDO:empOpenList){
                String empowerAreaCode = processOpenDO.getEmpowerAreaCode();
                if (StringUtils.isBlank(empowerAreaCode)) {
                    continue;
                }
                String[] areaCodeArr = empowerAreaCode.split(";");
                for(String empNo:areaCodeArr){
                    changeEmpList.add(empNo);
                    SafetyEmployeeMessageDO curEmp = userMap.get(empNo);
                    if(ObjectUtils.isEmpty(curEmp)){
                        continue;
                    }
                    SafetyBuildIssuedLogDO buildIssuedLogDO = createBuildIssuedLogDOForOpen(processOpenDO,curEmp,safetyBuildProcessDO,processId,dutyDeptName,dutyDeptCode);
                    logDOS.add(buildIssuedLogDO);
                    if(StringUtils.isNotBlank(processOpenDO.getPublicDeviceCode())){
                        String[] publicDeviceArr = processOpenDO.getPublicDeviceCode().split(",");
                        String[] publicDeviceNameArr = processOpenDO.getPublicDeviceName().split(",");
                        for(int i=0;i<publicDeviceArr.length;i++){
                            SafetyBuildIssuedLogDO publicIssuedLog = new SafetyBuildIssuedLogDO();
                            BeanUtils.copyProperties(buildIssuedLogDO,publicIssuedLog);
                            publicIssuedLog.setDeviceCode(publicDeviceArr[i]);
                            publicIssuedLog.setDeviceName(publicDeviceNameArr[i]);
                            checkEndTime(maxEndTimeByEmpNo, publicIssuedLog, processOpenDO.getAnBuildingCode());
                            logDOS.add(publicIssuedLog);
                        }
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(deptOpenList)){
            //组织授权不为空时添加
            try{
                initAutoDeptList(safetyBuildProcessDO,deptOpenList);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"批量添加自动授权组织异常，{}",e);
            }
            // 获取员工非公共楼宇的最大截止日期信息 key:员工号+楼栋编码  value：最大日期
            Map<String, String> maxEndTimeByDept = getMaxEndTimeByDept(deptOpenList);
            for(SafetyBuildProcessOpenDO openDO:deptOpenList){
                String deptPathCode = openDO.getEmpowerAreaCode();
                EmployeeMessageListParam listParam = new EmployeeMessageListParam();
                listParam.setOrgPathCode(deptPathCode);
                listParam.setNoEmpNoStart("01");
                //下发权限排除掉01开头的员工号
                List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getUserListByCondition(listParam);
                //黑名单中的人员禁止下发人脸 条件：状态=禁止入厂，备注=禁止授权条件判断
                //employeeList = safetyBaseHandler.filterBlackEmployee(employeeList);
                for(SafetyEmployeeMessageDO messageDO:employeeList){
                    String empNo = messageDO.getEmpNo();
                    changeEmpList.add(empNo);
                    SafetyBuildIssuedLogDO buildIssuedLogDO = createBuildIssuedLogDOForOpen(openDO,messageDO,safetyBuildProcessDO,processId,dutyDeptName,dutyDeptCode);
                    logDOS.add(buildIssuedLogDO);
                    // 处理公共设备
                    String publicDeviceCode = openDO.getPublicDeviceCode();
                    if (StringUtils.isBlank(publicDeviceCode)) {
                        continue;
                    }
                    String[] publicDeviceArr = openDO.getPublicDeviceCode().split(",");
                    String[] publicDeviceNameArr = openDO.getPublicDeviceName().split(",");

                    for(int i=0;i<publicDeviceArr.length;i++){
                        SafetyBuildIssuedLogDO publicIssuedLog = new SafetyBuildIssuedLogDO();
                        BeanUtils.copyProperties(buildIssuedLogDO,publicIssuedLog);
                        publicIssuedLog.setDeviceCode(publicDeviceArr[i]);
                        publicIssuedLog.setDeviceName(publicDeviceNameArr[i]);
                        checkEndTime(maxEndTimeByDept, publicIssuedLog, openDO.getAnBuildingCode());
                        logDOS.add(publicIssuedLog);
                    }
                }
            }
        }
        //根据门禁判断是否需要下发大门权限
        dealEmpDoorPowerNew("1",logDOS,"0");
        //下发门禁权限并保存日志
        issuedPowerByListNew("1",logDOS);
    }

    /**
     * 公共楼宇最大截止日期替换
     * @param dataMap 历史最大时间的数据
     * @param issuedLogDO 保存对象
     * @param buildingCode 楼栋编码
     */
    private void checkEndTime(Map<String, String> dataMap, SafetyBuildIssuedLogDO issuedLogDO, String buildingCode) {
        String endTime = issuedLogDO.getEndTime();
        String maxTime = dataMap.get(issuedLogDO.getEmployeeCode() + buildingCode);
        if(StringUtils.isBlank(maxTime)){
            return;
        }
        // 结束时间为空， 结束时间不是最大的时间， 最大时间不为空，并且最大时间比当前时间大
        if (StringUtils.isBlank(endTime) ||
                (!endTime.startsWith("2099") && DateUtils.isBeforeStr(DateUtils.format(DateUtils.parse(endTime, 1), 1), DateUtils.format(DateUtils.parse(maxTime, 1), 1)))) {
            issuedLogDO.setEndTime(maxTime);
        }
    }

    /**
     * 获取员工非公共楼宇的最大截止日期信息
     * @param empNoList 员工号
     * @return 返回Map类型
     */
    private Map<String, String> getMaxEndTimeByEmpNo(List<String> empNoList) {
        List<BuildEmpNoMaxPowerTimeVo> powerTimeVoList = safetyBuildEmployeePowerService.selectMaxEndTimeByEmpNo(empNoList);
        return powerTimeVoList.stream().filter(li -> StringUtils.isNotBlank(li.getBuildingCode())).collect(Collectors.toMap(ln -> ln.getEmpNo() + ln.getBuildingCode(), BuildEmpNoMaxPowerTimeVo::getMaxEndTime));
    }

    /**
     * 获取组织下员工非公共楼宇的最大截止日期信息
     * @param deptOpenList 组织信息
     * @return 返回Map类型
     */
    private Map<String, String> getMaxEndTimeByDept(List<SafetyBuildProcessOpenDO> deptOpenList){
        Set<String> deptPathCode = deptOpenList.stream().map(SafetyBuildProcessOpenDO::getEmpowerAreaCode).collect(Collectors.toSet());
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.selectListByOrgCodePath(new ArrayList<>(deptPathCode));
        if (CollectionUtils.isEmpty(employeeList)) {
            return new HashMap<>();
        }
        Set<String> empNoList = employeeList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toSet());
        return getMaxEndTimeByEmpNo(new ArrayList<>(empNoList));
    }

    /**
     * 整理开通楼宇下发日志数据
     * @param processOpenDO 开通楼宇详情DO
     * @param employeeMessageDO 员工台账信息DO
     * @param safetyBuildProcessDO 楼宇权限申请DO
     * @param processId 流程ID
     * @param dutyDeptName 责任部门名称
     * @param dutyDeptCode 责任部门编码
     */
    private SafetyBuildIssuedLogDO createBuildIssuedLogDOForOpen(SafetyBuildProcessOpenDO processOpenDO,
                                                          SafetyEmployeeMessageDO employeeMessageDO,
                                                          SafetyBuildProcessDO safetyBuildProcessDO,
                                                          String processId, String dutyDeptName,
                                                          String dutyDeptCode) {
        SafetyBuildIssuedLogDO buildIssuedLogDO = new SafetyBuildIssuedLogDO();
        BeanUtils.copyProperties(safetyBuildProcessDO, buildIssuedLogDO);

        buildIssuedLogDO.setEmployeeCode(employeeMessageDO.getEmpNo());
        buildIssuedLogDO.setEmployeeName(employeeMessageDO.getEmpName());
        buildIssuedLogDO.setDoorId(processOpenDO.getAnFactoryCode());
        buildIssuedLogDO.setDoorName(processOpenDO.getAnFactoryName());
        buildIssuedLogDO.setDeviceCode(processOpenDO.getDeviceCode());
        buildIssuedLogDO.setDeviceName(processOpenDO.getDeviceName());
        buildIssuedLogDO.setBeginTime(processOpenDO.getPowerBeginTime());
        buildIssuedLogDO.setEndTime(processOpenDO.getPowerEndTime());
        buildIssuedLogDO.setTargetId(processId);
        buildIssuedLogDO.setCreateBy(safetyBuildProcessDO.getDepartEmployeeName());
        buildIssuedLogDO.setRemark(safetyBuildProcessDO.getApplyReason()); // 申请原因
        buildIssuedLogDO.setIssuedType(ISSUED_TYPE_OPEN); // 0-关闭 1-开通
        buildIssuedLogDO.setIssuedStatus(ISSUED_STATUS_NOT_ISSUED); // 未下发
        buildIssuedLogDO.setSourceType(SOURCE_TYPE_PROCESS); // 流程
        buildIssuedLogDO.setDutyDeptName(dutyDeptName);
        buildIssuedLogDO.setDutyDeptCode(dutyDeptCode);

        return buildIssuedLogDO;
    }


    /**
     * 整理管理楼宇下发日志数据
     * @param processCloseDO 关闭楼宇DO
     * @param employeeMessageDO 员工台账DO
     * @param safetyBuildProcessDO 楼宇申请流程DO
     * @param processId 流程ID
     * @param dutyDeptName 责任部门名称
     * @param dutyDeptCode 责任部门编码
     */
    private SafetyBuildIssuedLogDO createBuildIssuedLogDOForClose(SafetyBuildProcessCloseDO processCloseDO,
                                                                 SafetyEmployeeMessageDO employeeMessageDO,
                                                                 SafetyBuildProcessDO safetyBuildProcessDO,
                                                                 String processId, String dutyDeptName,
                                                                 String dutyDeptCode,String beginDate,String endDate) {
        SafetyBuildIssuedLogDO buildIssuedLogDO = new SafetyBuildIssuedLogDO();
        BeanUtils.copyProperties(safetyBuildProcessDO, buildIssuedLogDO);

        buildIssuedLogDO.setEmployeeCode(employeeMessageDO.getEmpNo());
        buildIssuedLogDO.setEmployeeName(employeeMessageDO.getEmpName());
        buildIssuedLogDO.setDoorId(processCloseDO.getAnFactoryCode());
        buildIssuedLogDO.setDoorName(processCloseDO.getAnFactoryName());
        buildIssuedLogDO.setDeviceCode(processCloseDO.getDeviceCode());
        buildIssuedLogDO.setDeviceName(processCloseDO.getDeviceName());
        buildIssuedLogDO.setBeginTime(beginDate);
        buildIssuedLogDO.setEndTime(endDate);
        buildIssuedLogDO.setTargetId(processId);
        buildIssuedLogDO.setCreateBy(safetyBuildProcessDO.getDepartEmployeeName());
        buildIssuedLogDO.setRemark(safetyBuildProcessDO.getApplyReason()); // 申请原因
        buildIssuedLogDO.setIssuedType(ISSUED_TYPE_CLOSE); // 0-关闭 1-开通
        buildIssuedLogDO.setIssuedStatus(ISSUED_STATUS_NOT_ISSUED); // 未下发
        buildIssuedLogDO.setSourceType(SOURCE_TYPE_PROCESS); // 流程
        buildIssuedLogDO.setDutyDeptName(dutyDeptName);
        buildIssuedLogDO.setDutyDeptCode(dutyDeptCode);

        return buildIssuedLogDO;
    }

    /**
     * 关闭门禁权限处理逻辑
     * @param closeDOList 管理楼宇清单
     * @param safetyBuildProcessDO 楼宇申请流程DO
     * @param processId 流程ID
     * @param dutyDeptCode 责任部门
     * @param dutyDeptName 责任部门
     */
    public void dealBuildCloseList(Set<String> changeEmpNos,List<SafetyBuildProcessCloseDO> closeDOList,
                                   SafetyBuildProcessDO safetyBuildProcessDO,
                                   String processId,String dutyDeptCode,String dutyDeptName){
        String curTime = DateUtils.format(new Date(),2);
        String endTime = DateUtils.format(DateUtils.addDays(new Date(),-1),2);

        Map<String, List<SafetyBuildProcessCloseDO>> openMap = closeDOList.stream().collect(Collectors.groupingBy(SafetyBuildProcessCloseDO::getEmpowerType));
        List<SafetyBuildProcessCloseDO> empOpenList = openMap.get("1"); //员工
        List<SafetyBuildProcessCloseDO> deptCloseList = openMap.get("2"); //根据部门授权
        List<SafetyBuildIssuedLogDO> logDOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(empOpenList)) {
            Map<String, SafetyEmployeeMessageDO> userMap = getEmployeeByCloseList(empOpenList);

            // 公共区域的门禁 - 人员
            Map<String, String> empInfoMap = userMap.values().stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, SafetyEmployeeMessageDO::getEmpName));
            List<SafetyKeyValueDto> commonBuildInfo = getCommonEmpNoBuildInfo(empOpenList);
            if (!CollectionUtils.isEmpty(commonBuildInfo)) {
                logDOS.addAll(createBuildIssuedLogDOForClose(commonBuildInfo, empInfoMap, safetyBuildProcessDO, processId, dutyDeptName, dutyDeptCode));
            }

            for (SafetyBuildProcessCloseDO processOpenDO : empOpenList) {
                String empowerAreaCode = processOpenDO.getEmpowerAreaCode();
                String[] areaCodeArr = empowerAreaCode.split(";");
                for (String empNo : areaCodeArr) {
                    SafetyEmployeeMessageDO curEmp = userMap.get(empNo);
                    if (ObjectUtils.isEmpty(curEmp)) {
                        continue;
                    }
                    changeEmpNos.add(empNo);
                    SafetyBuildIssuedLogDO buildIssuedLogDO = createBuildIssuedLogDOForClose(processOpenDO,curEmp,
                            safetyBuildProcessDO,processId,dutyDeptName,dutyDeptCode,curTime,endTime);
                    logDOS.add(buildIssuedLogDO);
                }
            }
        }
        if(!CollectionUtils.isEmpty(deptCloseList)){
            // 公共区域的门禁 - 组织
            Map<String, String> empInfoMap = new HashMap<>();
            List<SafetyKeyValueDto> commonDeptBuildInfo = getCommonDeptBuildInfo(deptCloseList, empInfoMap);
            if (!CollectionUtils.isEmpty(commonDeptBuildInfo)) {
                logDOS.addAll(createBuildIssuedLogDOForClose(commonDeptBuildInfo, empInfoMap, safetyBuildProcessDO, processId, dutyDeptName, dutyDeptCode));
            }

            for(SafetyBuildProcessCloseDO openDO:deptCloseList){
                String deptPathCode = openDO.getEmpowerAreaCode();
                EmployeeMessageListParam listParam = new EmployeeMessageListParam();
                listParam.setOrgPathCode(deptPathCode);
                List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getUserListByCondition(listParam);
                for(SafetyEmployeeMessageDO messageDO:employeeList){
                    changeEmpNos.add(messageDO.getEmpNo());
                    SafetyBuildIssuedLogDO buildIssuedLogDO = createBuildIssuedLogDOForClose(openDO,messageDO,
                            safetyBuildProcessDO,processId,dutyDeptName,dutyDeptCode,curTime,endTime);;
                    logDOS.add(buildIssuedLogDO);
                }
            }
        }
        //关闭楼宇时不用关闭大门
        issuedPowerByListNew("2",logDOS);
    }

    /**
     * 根据人员判断是否存在没有非公共楼宇的楼栋的公共楼宇权限，存在的话，拿出来
     *
     * @param empCloseList 根据人员关闭楼宇的信息
     * @return 返回符合条件的数据
     */
    private List<SafetyKeyValueDto> getCommonEmpNoBuildInfo(List<SafetyBuildProcessCloseDO> empCloseList) {
        // 返回值
        List<SafetyKeyValueDto> result = new ArrayList<>();

        // 循环获取具体人员本次需要关闭的设备信息
        List<SafetyKeyValueDto> keyValueDtos = new ArrayList<>();
        for (SafetyBuildProcessCloseDO li : empCloseList) {
            String deviceCode = li.getDeviceCode();
            String empowerAreaCode = li.getEmpowerAreaCode();
            if (StringUtils.isBlank(deviceCode)) {
                continue;
            }
            if (StringUtils.isBlank(empowerAreaCode)) {
                continue;
            }

            for (String empNo : empowerAreaCode.split(";")) {
                for (String code : deviceCode.split(",")) {
                    SafetyKeyValueDto dto = new SafetyKeyValueDto();
                    dto.setKey(empNo);
                    dto.setValue(code);
                    keyValueDtos.add(dto);
                }
            }
        }
        if (CollectionUtils.isEmpty(keyValueDtos)) {
            return result;
        }

        // 构建closePublicBuildIng方法需要的参数
        List<SafetyClosePublicBuildParams> list = new ArrayList<>();
        Map<String, List<SafetyKeyValueDto>> dataMap = keyValueDtos.stream().collect(Collectors.groupingBy(SafetyKeyValueDto::getKey));
        dataMap.keySet().forEach(key -> {
            List<String> deviceCodeList = dataMap.get(key).stream().map(SafetyKeyValueDto::getValue).distinct().collect(Collectors.toList());
            list.add(safetyBaseHandler.createClosePublicBuildParams(key, deviceCodeList));
        });

        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        // 获取此部分员工的公共区域楼宇信息
        return safetyBaseHandler.closePublicBuildIng(list, false, "楼宇门禁流程关闭");
    }

    /**
     * 根据组织区分具体的人员是否存在没有非公共楼宇的楼栋的公共楼宇权限， 存在的话，拿出来
     * @param deptCloseList 根据组织关闭楼宇的信息
     * @param empInfoMap 人员信息
     * @return 返回符合条件的数据
     */
    private List<SafetyKeyValueDto> getCommonDeptBuildInfo(List<SafetyBuildProcessCloseDO> deptCloseList, Map<String, String> empInfoMap) {
        // 返回值
        List<SafetyKeyValueDto> result = new ArrayList<>();

        Set<String> deptPathCode = deptCloseList.stream().map(SafetyBuildProcessCloseDO::getEmpowerAreaCode).collect(Collectors.toSet());
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.selectListByOrgCodePath(new ArrayList<>(deptPathCode));
        if (CollectionUtils.isEmpty(employeeList)) {
            return result;
        }
        Map<String, List<SafetyEmployeeMessageDO>> orgPathCodeMap = employeeList.stream().collect(Collectors.groupingBy(SafetyEmployeeMessageDO::getOrgCodePath));
        // 循环获取具体组织下人员本次需要关闭的设备信息
        List<SafetyKeyValueDto> keyValueDtos = new ArrayList<>();
        for (SafetyBuildProcessCloseDO li : deptCloseList) {
            String deviceCode = li.getDeviceCode();
            String empowerAreaCode = li.getEmpowerAreaCode();
            if (StringUtils.isBlank(deviceCode)) {
                continue;
            }
            if (StringUtils.isBlank(empowerAreaCode)) {
                continue;
            }
            List<SafetyEmployeeMessageDO> employeeMessageDOList = orgPathCodeMap.get(empowerAreaCode);
            if (CollectionUtils.isEmpty(employeeList)) {
                continue;
            }
            employeeMessageDOList.forEach(ln -> {
                for (String code : deviceCode.split(",")) {
                    SafetyKeyValueDto dto = new SafetyKeyValueDto();
                    dto.setKey(ln.getEmpNo());
                    dto.setValue(code);
                    keyValueDtos.add(dto);
                    empInfoMap.put(ln.getEmpNo(), ln.getEmpName());
                }

            });
        }
        if (CollectionUtils.isEmpty(keyValueDtos)) {
            return result;
        }

        // 构建closePublicBuildIng方法需要的参数
        List<SafetyClosePublicBuildParams> list = new ArrayList<>();
        Map<String, List<SafetyKeyValueDto>> dataMap = keyValueDtos.stream().collect(Collectors.groupingBy(SafetyKeyValueDto::getKey));
        dataMap.keySet().forEach(key -> {
            List<String> deviceCodeList = dataMap.get(key).stream().map(SafetyKeyValueDto::getValue).distinct().collect(Collectors.toList());
            list.add(safetyBaseHandler.createClosePublicBuildParams(key, deviceCodeList));
        });

        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        // 获取此部分员工的公共区域楼宇信息
        return safetyBaseHandler.closePublicBuildIng(list, false, "楼宇门禁流程关闭");
    }

    /**
     * 生成下发的数据
     * @param list 人员信息和公共的楼宇设备
     * @param empInfoMap 人员信息
     * @param safetyBuildProcessDO 楼宇申请流程DO
     * @param processId 流程ID
     * @param dutyDeptName 责任部门
     * @param dutyDeptCode 责任部门编码
     * @return 返回生成的数据集
     */
    private List<SafetyBuildIssuedLogDO> createBuildIssuedLogDOForClose(List<SafetyKeyValueDto> list,
                                                                        Map<String, String> empInfoMap,
                                                                        SafetyBuildProcessDO safetyBuildProcessDO,
                                                                        String processId,
                                                                        String dutyDeptName,
                                                                        String dutyDeptCode) {
        String curTime = DateUtils.format(new Date(),2);
        String endTime = DateUtils.format(DateUtils.addDays(new Date(),-1),2);
        // 数据生成
        return list.stream().map(li -> {
            SafetyBuildIssuedLogDO info = new SafetyBuildIssuedLogDO();
            BeanUtils.copyProperties(safetyBuildProcessDO, info);
            info.setEmployeeCode(li.getKey());
            info.setEmployeeName(empInfoMap.get(li.getKey()));
            info.setDeviceCode(li.getValue());
            info.setDeviceName(li.getValueTwo());
            info.setCreateBy(safetyBuildProcessDO.getDepartEmployeeName());
            info.setRemark(safetyBuildProcessDO.getApplyReason()); // 申请原因
            info.setIssuedType(ISSUED_TYPE_CLOSE); // 0-关闭 1-开通
            info.setIssuedStatus(ISSUED_STATUS_NOT_ISSUED); // 未下发
            info.setSourceType(SOURCE_TYPE_PROCESS); // 流程
            info.setDutyDeptName(dutyDeptName);
            info.setDutyDeptCode(dutyDeptCode);
            info.setBeginTime(curTime);
            info.setEndTime(endTime);
            info.setTargetId(processId);
            return info;
        }).collect(Collectors.toList());
    }

    /**
     * 新方法
     * 根据流程ID下发门禁权限
     * @param processId 流程ID
     */
    public void sendPowerByProcessNew(String processId){
        LogUtils.info("进入sendPowerByProcess方法 processId={}",processId);
        SafetyBuildProcessDO safetyBuildProcessDO = safetyBuildProcessService.selectByPrimaryKey(processId);
        if(ObjectUtils.isEmpty(safetyBuildProcessDO)){
            return;
        }
        //超级管理员时不设置责任部门
        String dutyDeptName = safetyBuildProcessDO.getDutyDeptName();
        String dutyDeptCode = safetyBuildProcessDO.getDutyDeptCode();
        if("2".equals(safetyBuildProcessDO.getRoleType())){
            dutyDeptName = "";
            dutyDeptCode = "";
        }
        Set<String> changeEmpNos = new HashSet<>();

        List<SafetyBuildProcessCloseDO> closeDOList = safetyBuildProcessService.selectCloseList(processId);
        if(!CollectionUtils.isEmpty(closeDOList)) {
            dealBuildCloseList(changeEmpNos,closeDOList,safetyBuildProcessDO,processId,dutyDeptCode,dutyDeptName);
        }
        //排除掉不通过的
        List<SafetyBuildProcessOpenDO> openDOList = safetyBuildProcessOpenService.selectOpenList(processId,"2");
        if(!CollectionUtils.isEmpty(openDOList)){
            dealBuildOpenList(changeEmpNos,openDOList,safetyBuildProcessDO,processId,dutyDeptCode,dutyDeptName);
        }
        //发送宇信通消息
        try{
            sendYxtMsg(safetyBuildProcessDO);
        }catch (Exception e){
            LogUtils.error("发送宇信通消息失败",e);
        }
    }

    /**
     * 更新员工出入口权限
     * @param issuedLogDOS 下发日志
     * @param dealType 权限类型 1-开通 2-关闭
     */
    public void updateCrkPower(String dealType,List<SafetyBuildIssuedLogDO> issuedLogDOS) {
        List<SafetyBuildIssuedLogDO> saveLogs = new ArrayList<>();
        //1.提取变更员工号
        List<String> empNoList = issuedLogDOS.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).distinct().collect(Collectors.toList());
        //2.过滤黑名单
        List<String> filterEmpNos = safetyBaseHandler.filterBlackEmpNos(empNoList);
        if(CollectionUtils.isEmpty(filterEmpNos)){
            return;
        }
        issuedLogDOS = issuedLogDOS.stream().filter(e->filterEmpNos.contains(e.getEmployeeCode())).collect(Collectors.toList());

        Map<String,List<SafetyBuildIssuedLogDO>> issuedLogMap = issuedLogDOS.stream().collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
        //3.查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(filterEmpNos);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarService.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream()
                .collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));
        //2.获取员工出入口人事信息
        Map<String, SafetyEmpCardVO> empCardMap = safetyBaseHandler.generalCrkStaffForBatch(filterEmpNos);
        //3.获取所有的厂区名称；
        Map<Integer, String> map = safetyFieldManageService.getFactoryNameList();
        //4.获取员工信息台账
        Map<String,SafetyEmployeeMessageDO> employeeMap = safetyEmployeeMessageService.getMaptByStaffNo(filterEmpNos);

        //5.开通权限时，需要获取员工所有厂区楼宇权限
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = new HashMap<>();
        Map<String, List<SafetyParkEmployeePowerInfoDO>> parkPowerMap = new HashMap<>();
        Map<String,SafetyOutsiderPromiseDTO> outsiderPowerMap = new HashMap<>();
        if("1".equals(dealType) || "3".equals(dealType)){
            Map<String,Object> retMap = getDeviceId(empNoList,buildPowerMap,parkPowerMap,outsiderPowerMap);
            buildPowerMap = (Map<String, List<SafetyBuildEmployeePowerDO>>) retMap.get("buildPowerMap");
            parkPowerMap = (Map<String, List<SafetyParkEmployeePowerInfoDO>>) retMap.get("parkPowerMap");
            outsiderPowerMap = (Map<String, SafetyOutsiderPromiseDTO>) retMap.get("outsiderPowerMap");
        }

        //权限关闭时-结束日期
        AtomicReference<String> beginTime = new AtomicReference<>(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
        Map<String, List<SafetyParkEmployeePowerInfoDO>> finalParkPowerMap = parkPowerMap;
        Map<String, List<SafetyBuildEmployeePowerDO>> finalBuildPowerMap = buildPowerMap;
        Map<String, SafetyOutsiderPromiseDTO> finalOutsiderPowerMap = outsiderPowerMap;

        for(Map.Entry<String, List<SafetyBuildIssuedLogDO>> entry : issuedLogMap.entrySet()){
            String empNo = entry.getKey();
            List<SafetyBuildIssuedLogDO> logs = entry.getValue();
            if(StringUtils.isBlank(empNo) || logs.isEmpty()){
                continue;
            }
            try{
                String endTime = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59";
                String buildDevice = logs.stream().filter(e->"2".equals(e.getDeviceType())).map(SafetyBuildIssuedLogDO::getDeviceCode).distinct().collect(Collectors.joining(","));
                String buildDeviceName = logs.stream().filter(e->"2".equals(e.getDeviceType())).map(SafetyBuildIssuedLogDO::getDeviceName).distinct().collect(Collectors.joining(","));
                String factoryIds = logs.stream().map(SafetyBuildIssuedLogDO::getDoorId).distinct().collect(Collectors.joining(","));
                SafetyBuildIssuedLogDO curLog = logs.get(0);
                curLog.setDeviceCode(buildDevice);
                curLog.setDeviceName(buildDeviceName);
                curLog.setDoorId(factoryIds);
                //设置厂区
                String factoryNames = safetyBaseHandler.getParkNameBYCode(factoryIds, map);
                curLog.setDoorName(factoryNames);
                SafetyEmpCardVO empCardVO = empCardMap.get(empNo);
                List<SafetyEmployeePrivateCarDO> carList = carinfoMap.get(empNo);
                SafetyEmployeeMessageDO empInfo = employeeMap.get(empNo);
                String userType = "";
                String telephone = "";
                if(empInfo != null){
                    userType = empInfo.getUserType();
                    if(StringUtils.isNotBlank(empInfo.getPhoneNo())){
                        telephone = empInfo.getPhoneNo();
                    }
                }
                SafetyBuildIssuedLogDO actualLog = new SafetyBuildIssuedLogDO();
                BeanUtils.copyProperties(curLog,actualLog);
                if("1".equals(dealType) || "3".equals(dealType)){
                    if("WX".equals(userType)){
                        SafetyOutsiderPromiseDTO curOut = finalOutsiderPowerMap.get(empNo);
                        if(curOut != null && StringUtils.isNotBlank(curOut.getServiceEndDate())){
                            endTime = curOut.getServiceEndDate() + " 23:59:59";
                            actualLog.setDoorId(curOut.getFactory());
                        }else{
                            endTime = DateUtils.addDays(DateUtils.addYears(new Date(),1),1)+" 23:59:59";
                        }
                    }else{
                        List<SafetyParkEmployeePowerInfoDO> parkList = finalParkPowerMap.get(empNo);
                        if(!CollectionUtils.isEmpty(parkList)){
                            String allDoorIds = parkList.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).distinct().collect(Collectors.joining(","));
                            actualLog.setDoorId(allDoorIds);
                        }
                        // 获取结束时间的最大值
                        Optional<String> curEndTimeOpt = logs.stream()
                                .map(SafetyBuildIssuedLogDO::getEndTime)
                                .max(String::compareTo);

                        Optional<String> parkMaxEndTimeOpt = parkList.stream()
                                .map(SafetyParkEmployeePowerInfoDO::getPowerEndTime)
                                .max(String::compareTo);

                        String curEndTime = curEndTimeOpt.orElse(null);
                        String parkMaxEndTime = parkMaxEndTimeOpt.orElse(null);

                        if (StringUtils.isNotBlank(curEndTime) || StringUtils.isNotBlank(parkMaxEndTime)) {
                            endTime = getMaxEnd(curEndTime,parkMaxEndTime) + " 23:59:59";
                        }else{
                            endTime = "2099-12-30 23:59:59";
                        }
                        if("3".equals(dealType)){
                            endTime = "2099-12-30 23:59:59";
                        }
                    }
                    actualLog.setIssuedType("开通");
                    List<SafetyBuildEmployeePowerDO> buildList = finalBuildPowerMap.get(empNo);
                    if(!CollectionUtils.isEmpty(buildList)){
                        String allDeviceIds = buildList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).distinct().collect(Collectors.joining(","));
                        actualLog.setDeviceCode(allDeviceIds);
                    }else{
                        actualLog.setDeviceCode("");
                    }
                }else{
                    //关闭楼宇不关闭厂区
                    actualLog.setDoorId("");
                }
                curLog.setEndTime(endTime);
                actualLog.setEndTime(endTime);
                actualLog.setBeginTime(beginTime.get());
                //下发状态
                boolean issueFlag = powerUpdateForBuild(actualLog,carList,empCardVO,telephone,userType);
                curLog.setIssuedStatus(issueFlag?"成功":"异常");
                //关闭后重新下发的权限，楼宇记录为空的，不写入日志
                if(!"3".equals(dealType) && StringUtils.isNotBlank(curLog.getDeviceCode())){
                    //实际下发记录
                    saveLogs.add(curLog);
                }
            }catch (Exception e){
                LogUtils.info(ResponseEnum.EXCEPTION.getCode(),empNo+"权限更新失败");
                LogUtils.error(empNo+"权限更新失败",e);
            }
        }

        // 7.日志记录
        if (!CollectionUtils.isEmpty(saveLogs)) {
            safetyBuildIssuedLogService.batchInsert(saveLogs);
        };
    }

    /**
     * 整理门禁和厂区的最大截止日期，取最大
     * @param buildMaxEndTime 门禁最大日期
     * @param parkMaxEndTime 厂区最大日期
     * @return
     */
    private String getMaxEnd(String buildMaxEndTime,String parkMaxEndTime){
        String curMaxDay = "";
        if(StringUtils.isBlank(buildMaxEndTime)){
            curMaxDay = parkMaxEndTime.substring(0,10);
        }else if(StringUtils.isBlank(parkMaxEndTime)){
            curMaxDay = buildMaxEndTime.substring(0,10);
        }else{
            String buildMaxEnd = buildMaxEndTime.substring(0,10);
            String parkMaxEnd = parkMaxEndTime.substring(0,10);
            curMaxDay = DateUtils.isBeforeStr(buildMaxEnd,parkMaxEnd)?parkMaxEnd:buildMaxEnd;
        }
        return curMaxDay.substring(0,10) + " 23:59:59";
    }

    /**
     * 多个员工获取设备ID信息
     * @param empNoList 员工号
     * @param parkPowerMap 楼宇权限
     * @param outsiderPowerMap 厂区权限
     */
    public Map<String,Object> getDeviceId(List<String> empNoList,Map<String,
                            List<SafetyBuildEmployeePowerDO>> buildPowerMap,
                            Map<String, List<SafetyParkEmployeePowerInfoDO>> parkPowerMap,
                            Map<String,SafetyOutsiderPromiseDTO> outsiderPowerMap) {
        Map<String,Object> retMap = new HashMap<>();
        // 获取员工所有的门禁权限对应的设备
        // 根据员工号获取员工所有已开通门禁权限
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setEmpCodeList(empNoList);
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectByExample(listParam);
        // 过滤掉非富士的门禁数据
        employeePowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(employeePowerDOS)) {
            // 员工门禁 数据分组 根据员工号分组
            buildPowerMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        }
        //获取厂区大门权限
        ParkEmployeePowerListParam parkParam = new ParkEmployeePowerListParam();
        parkParam.setPageFlag(false);
        parkParam.setEmpNoList(empNoList);
        parkParam.setDeleteFlag("0");
        List<SafetyParkEmployeePowerInfoDO> parkPowerList = safetyParkEmployeePowerInfoService.selectList(parkParam);
        parkPowerMap = parkPowerList.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));

        //获取外协权限
        List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(empNoList);
        outsiderPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo, e -> e));

        retMap.put("buildPowerMap",buildPowerMap);
        retMap.put("parkPowerMap",parkPowerMap);
        retMap.put("outsiderPowerMap",outsiderPowerMap);

        return retMap;
    }


    /**
     * bpm流程回调接口
     * @param bpmFlowId 流程ID
     * @param processData BPM流程表单数据
     */
    public void bpmCallBackProcess(String bpmFlowId, String processData) {
        LogUtils.info(String.format("楼宇门禁员工权限调整：bpm回调参数processId=%s;processData=%s", bpmFlowId, processData));
        if (StringUtils.isBlank(bpmFlowId) || StringUtils.isBlank(processData)) {
            LogUtils.info("楼宇门禁员工权限调整流程异常-流程ID或实体类ID为空");
            return;
        }
        try {
            com.alibaba.fastjson.JSONObject processDataObj = com.alibaba.fastjson.JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            BuildProcessListParam listParam = new BuildProcessListParam();
            listParam.setBpmFlowId(bpmFlowId);
            SafetyBuildProcessDOWithBLOBs bloBs = new SafetyBuildProcessDOWithBLOBs();
            bloBs.setCurNodeId(nodeFactId);
            //根据bpm流程ID更新节点
            safetyBuildProcessService.updateByExampleSelective(listParam,bloBs);

        } catch (Exception e) {
            LogUtils.error("楼宇门禁员工权限调整流程回调-处理逻辑异常", e.getMessage());
        }
    }

    /**
     * 流程废弃回调事件
     * @param bpmFlowId 流程ID
     * @param processData BPM流程表单数据
     */
    public void discardProcess(String bpmFlowId, String processData) {
        LogUtils.info(String.format("废弃楼宇门禁员工权限调整：bpm回调参数processId=%s;processData=%s", bpmFlowId, processData));
        if (StringUtils.isBlank(bpmFlowId) || StringUtils.isBlank(processData)) {
            LogUtils.info("废弃楼宇门禁员工权限调整流程异常-流程ID或实体类ID为空");
            return;
        }
        try {
            com.alibaba.fastjson.JSONObject processDataObj = com.alibaba.fastjson.JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            BuildProcessListParam listParam = new BuildProcessListParam();
            listParam.setBpmFlowId(bpmFlowId);
            SafetyBuildProcessDOWithBLOBs bloBs = new SafetyBuildProcessDOWithBLOBs();
            bloBs.setCurNodeId(nodeFactId);
            bloBs.setStatus(FLOW_STATUS_ABANDON);  //流程废弃
            bloBs.setCurNodeHandler(""); //节点处理人置空
            bloBs.setRefuseFlag("2"); //审批不通过
            //根据bpm流程ID更新节点
            safetyBuildProcessService.updateByExampleSelective(listParam,bloBs);

        } catch (Exception e) {
            LogUtils.error("楼宇门禁员工权限调整流程回调-处理逻辑异常", e.getMessage());
        }
    }

    /**
     * 节点结束时间
     * @param bpmFlowId BPM流程ID
     * @param processData 回写参数
     */
    public void afterProcess(String bpmFlowId, String processData){
        LogUtils.info(String.format("节点结束事件：bpm回调参数processId=%s;processData=%s", bpmFlowId, processData));
        if (StringUtils.isBlank(bpmFlowId) || StringUtils.isBlank(processData)) {
            LogUtils.info("楼宇门禁员工权限调整流程异常-流程ID或实体类ID为空");
            return;
        }
        try {
            com.alibaba.fastjson.JSONObject processDataObj = com.alibaba.fastjson.JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            if(!"N5".equals(nodeFactId)){
                return;
            }
            BuildProcessListParam listParam = new BuildProcessListParam();
            listParam.setBpmFlowId(bpmFlowId);
            List<BuildProcessListVO> listVO = safetyBuildProcessService.selectListWithBlobs(listParam);
            if(CollectionUtils.isEmpty(listVO)){
                return;
            }
            BuildProcessListVO curVO = listVO.get(0);
            String processId = curVO.getId();
            String dutyDeptCode = curVO.getDutyDeptCode();

            List<SafetyBuildProcessOpenDO> openList = safetyBuildProcessOpenService.selectOpenList(processId,"2");
            Set<String> eipRoles = new HashSet<>();
            Set<String> topSecretEmpCode = new HashSet<>();
            Set<String> topSecretEmpName = new HashSet<>();
            if(!CollectionUtils.isEmpty(openList)){
                for(SafetyBuildProcessOpenDO curOpen:openList){
                    if(StringUtils.isNotBlank(dutyDeptCode)){
                        if(!dutyDeptCode.equals(curOpen.getDockingDeptCode())){
                            eipRoles.add(curOpen.getBuildSysManagerRole());
                        }
                    }else{
                        eipRoles.add(curOpen.getDockingDeptCode());
                    }
                    if(StringUtils.isNotBlank(curOpen.getTopSecretEmployeeLogin())){
                        topSecretEmpCode.add(curOpen.getTopSecretEmployeeLogin());
                        topSecretEmpName.add(curOpen.getTopSecretEmployeeName());
                    }
                }
            }
            SafetyBuildProcessDOWithBLOBs bloBs = new SafetyBuildProcessDOWithBLOBs();
            bloBs.setId(processId);
            bloBs.setTopSecretEmployeeLogin(String.join(",", topSecretEmpCode));
            bloBs.setTopSecretEmployeeName(String.join(",", topSecretEmpName));
            bloBs.setAcrossBuildSysManagerRole(String.join(",", eipRoles));
            if(eipRoles.isEmpty()){
                bloBs.setAcrossFlag("0"); //没有对接角色时，设置为不跨部门
            }
            //根据bpm流程ID更新节点
            safetyBuildProcessService.updateByPrimaryKeySelective(bloBs);

        } catch (Exception e) {
            LogUtils.error("楼宇门禁员工权限调整流程-处理逻辑异常", e.getMessage());
        }
    }

    /**
     * 初始化自动授权部门数据
     * @param deptOpenList 申请的自动授权部门信息
     */
    public void initAutoDeptList(SafetyBuildProcessDO processDO,List<SafetyBuildProcessOpenDO> deptOpenList){
        String processId = processDO.getId();
        String bpmFlowId = processDO.getBpmFlowId();
        String effectiveDate = DateUtils.format(new Date(),1);

        List<SafetyBuildAutoDeptDO> powerDOList = new ArrayList<>();
        for(SafetyBuildProcessOpenDO openDO:deptOpenList){
            SafetyBuildAutoDeptDO newPower = new SafetyBuildAutoDeptDO();
            BeanUtils.copyProperties(processDO,newPower);
            newPower.setDeviceCode(openDO.getDeviceCode());
            String[] deptCodeArr = openDO.getEmpowerAreaCode().split(",");
            newPower.setDeptCode(deptCodeArr[deptCodeArr.length-1]); //把最后一级的编码保存
            newPower.setDeptName(openDO.getEmpowerAreaName());
            newPower.setProcessId(processId);
            newPower.setBpmFlowId(bpmFlowId);
            newPower.setAutoFlag("0");
            newPower.setBeginTime(openDO.getPowerBeginTime().substring(0,10));
            newPower.setEndTime(openDO.getPowerEndTime().substring(0,10));
            newPower.setEffectiveDate(effectiveDate);
            newPower.setRemark(processDO.getApplyReason());

            newPower.setPublicDeviceCode(openDO.getPublicDeviceCode());
            newPower.setPublicDeviceName(openDO.getPublicDeviceName());

            powerDOList.add(newPower);
        }
        if(!CollectionUtils.isEmpty(powerDOList)){
            safetyBuildAutoDeptService.batchInsert(powerDOList);
        }
    }

    /**
     * 校验当前用户的类型 1-员工 2-管理员 3-绝密负责人
     * 默认的角色
     */
    public String checkUserType(String token){
        return checkUserType(token, BUILD_ADMIN_ROLE);
    }

    /**
     * 校验当前用户的类型 1-员工 2-管理员 3-绝密负责人
     * @param token 登录人令牌
     * @param roleName 角色信息
     * @return 返回值
     */
    public String checkUserType(String token, String roleName){
        String roleType = "1"; //普通用户
        boolean isAdmin = safetySysUserService.checkRole(token, roleName);
        if(isAdmin){
            return "2"; //管理员
        }
        String empNo = TokenUtils.getWorkNo(token);
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(empNo);
        List<SafetyBuildPowerDO> manePowerList = safetyBuildPowerService.selectList(listParam);
        if(CollectionUtils.isEmpty(manePowerList)){
            return roleType;
        }
        String dutyDeptCode = manePowerList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).distinct().collect(Collectors.joining(";"));
        roleType = "3"+":"+dutyDeptCode;
        return roleType;
    }


    /**
     * 富士的门禁下发，需要整合所有权限进行下发分正式工和外协
     * @param logDOList 日志
     */
    public void issuedBuildPowerForProcessForTwoFs(List<SafetyBuildIssuedLogDO> logDOList){
        Map<String,List<SafetyBuildIssuedLogDO>> empMap = logDOList.stream()
                .collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
        //1.员工集合
        List<String> empNos = new ArrayList<>(empMap.keySet());

        //2.获取员工已有门禁权限
        BuildEmployeePowerListParam powerListParam = new BuildEmployeePowerListParam();
        powerListParam.setEmpCodeList(empNos);
        List<SafetyBuildEmployeePowerDO> buildEmpPowerList = safetyBuildEmployeePowerService.selectByExample(powerListParam);
        Map<String,List<SafetyBuildEmployeePowerDO>> empBuildPowerMap = buildEmpPowerList.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));

        //3.获取所有门禁权限对应的门禁设备信息
        List<String> newDevices = logDOList.stream().map(SafetyBuildIssuedLogDO::getDeviceCode).distinct().collect(Collectors.toList());
        List<String> oldDevices = buildEmpPowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).distinct().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(oldDevices)){
            newDevices.addAll(oldDevices);
        }
        List<SafetyBuildDeviceDO> buildDeviceDOS = safetyBuildDeviceService.selectList(newDevices);

        //获取大门权限
        //先查询员工当前的大门授权，判断是否有新增厂区 然后进行下发
        ParkEmployeePowerListParam param = new ParkEmployeePowerListParam();
        param.setPageFlag(false);
        param.setEmpNoList(empNos);
        List<SafetyParkEmployeePowerInfoDO> empParkList = safetyParkEmpPowerReportService.selectByExample(param);
        Map<String, List<SafetyParkEmployeePowerInfoDO>> empParkMap = empParkList.stream()
                .collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));

        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNos);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        //获取员工信息
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(empNos);
        Map<String,SafetyEmployeeMessageDO> employeeMap = employeeList.stream().collect(java.util.stream.Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));

        List<SafetyBuildIssuedLogDO> issuedList = new ArrayList<>();
        //根据下发逻辑整合逻辑
        empMap.forEach((key,issuedLogs)->{
            String deviceCodeStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDeviceCode)
                    .distinct().collect(Collectors.joining(","));
            String deviceNameStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDeviceName)
                    .distinct().collect(Collectors.joining(","));

            String endTime = issuedLogs.stream()
                    .map(SafetyBuildIssuedLogDO::getEndTime)
                    .max(String::compareTo)
                    .orElse(null);

            issuedLogs.get(0).setDeviceCode(deviceCodeStr);
            issuedLogs.get(0).setDeviceName(deviceNameStr);
            issuedLogs.get(0).setEndTime(endTime); //按最大时间下发

            issuedList.add(issuedLogs.get(0));
        });

        // 获取所有的厂区名称；
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();

        List<SafetyBuildIssuedLogDO> resultList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> yesList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(issuedList)){
            for(SafetyBuildIssuedLogDO issuedLogDO:issuedList){
                //员工号
                String empNo = issuedLogDO.getEmployeeCode();
                List<SafetyBuildEmployeePowerDO> oldPowers = empBuildPowerMap.get(empNo);
                SafetyEmployeeMessageDO curEmp = employeeMap.get(empNo);
                if(curEmp == null){
                    LogUtils.error("{}未找到对应的员信息台账",empNo);
                    continue;
                }
                String deviceCode = issuedLogDO.getDeviceCode();
                String issuedStatus = "异常";
                try {
                    Set<String> initFactoryId = new HashSet<>();
                    List<SafetyParkEmployeePowerInfoDO> curParkPower = empParkMap.get(empNo);
                    if(curParkPower != null && !CollectionUtils.isEmpty(curParkPower)){
                        initFactoryId = curParkPower.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).collect(Collectors.toSet());
                    }
                    Set<String> curNews = new HashSet<>(Arrays.asList(deviceCode.split(",")));
                    if(!CollectionUtils.isEmpty(oldPowers)){
                        Set<String> curOldDevices = oldPowers.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.toSet());
                        curNews.addAll(curOldDevices);
                    }
                    //根据设备号查询其对应的工厂
                    List<SafetyBuildDeviceDO> deviceDOList = buildDeviceDOS.stream().filter(e->curNews.contains(e.getDeviceCode())).collect(Collectors.toList());
                    //过滤出富士的门禁
                    List<SafetyBuildDeviceDO> fushiDOList = deviceDOList.stream().filter(e->"2".equals(e.getDeviceType())).collect(Collectors.toList());
                    String deviceNos = fushiDOList.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.joining(","));
                    String deviceNames = fushiDOList.stream().map(SafetyBuildDeviceDO::getAnDeviceName).collect(Collectors.joining(","));
                    issuedLogDO.setDeviceCode(deviceNos);
                    issuedLogDO.setDeviceName(deviceNames);

                    //已有门禁+新申请门禁厂区集合
                    Set<String> collect = deviceDOList.stream().map(SafetyBuildDeviceDO::getAnFactoryCode).collect(Collectors.toSet());
                    initFactoryId.addAll(collect);
                    String factoryIds = String.join(",",initFactoryId);

                    if(StringUtils.isBlank(factoryIds)){
                        continue;
                    }
                    issuedLogDO.setDoorId(factoryIds);
                    String factoryNames = safetyBaseHandler.getParkNameBYCode(factoryIds,factoryMap);
                    issuedLogDO.setDoorName(factoryNames);
                    List<SafetyEmployeePrivateCarDO> carNos = carinfoMap.get(empNo);
                    // 获取组织ID
                    Pair<String, Object> pair = safetyBaseHandler.generalOrgId(empNo);
                    //执行下发
                    formatIssuePower(factoryIds,issuedLogDO,deviceNos,curEmp,carNos,pair);
                    issuedStatus = "成功";
                } catch (Exception e) {
                    LogUtils.error("富士权限下发失败,参数{},错误{}", issuedLogDO, e);
                }

                issuedLogDO.setIssuedStatus(issuedStatus);
                String key = issuedLogDO.getEmployeeCode();
                List<SafetyBuildIssuedLogDO> curList = empMap.get(key);
                issuedLogDO.setIssuedStatus(issuedStatus);
                if("成功".equals(issuedStatus) && !CollectionUtils.isEmpty(curList)){
                    yesList.addAll(curList);
                }
                issuedLogDO.setBeginTime(issuedLogDO.getBeginTime().substring(0,10));
                issuedLogDO.setEndTime(issuedLogDO.getEndTime().substring(0,10));
                resultList.add(issuedLogDO);
            }
            try {
                safetyBuildIssuedLogService.batchInsert(resultList);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"批量保存异常{}",e);
            }
            if(!CollectionUtils.isEmpty(yesList)){
                try{
                    List<SafetyBuildEmployeePowerDO> employeePowerDOS = formatEmployeePowerDO(yesList);
                    safetyBuildEmployeePowerService.insertBatch(employeePowerDOS);
                }catch (Exception e){
                    LogUtils.error(ResponseEnum.FAIL.getCode(),"批量添加人员明细异常{}",e);
                }
            }
        }
    }

    /**
     * 执行批次下发 人脸，卡，车
     * @param factoryIds 所有授权厂区
     * @param issuedLogDO 日志信息
     * @param deviceNos 现有门禁权限
     * @param carNos 员工私家车
     */
    private void formatIssuePower(String factoryIds,SafetyBuildIssuedLogDO issuedLogDO,String deviceNos,
                                  SafetyEmployeeMessageDO emp,List<SafetyEmployeePrivateCarDO> carNos,Pair<String, Object> pair){
        String userType = emp.getUserType();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType","正式员工");
        if("WX".equals(userType)){
            paramMap.put("empowerType","外协");
        }
        paramMap.put("factoryIds",Arrays.asList(factoryIds.split(",")));
        paramMap.put("regionName","大门人脸");
        //根据工厂ID 设备类型(人脸) "外协" 获取授权设备号
        String faceDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
        if(org.springframework.util.StringUtils.isEmpty(faceDevices)){
            LogUtils.info("issuedBuildPowerForProcessForTwoFs:员工号："+issuedLogDO.getEmployeeCode()+"，未获取到 设备类型(人脸)授权设备号！");
        }else{
            //更新门禁人脸权限
            safetyBaseHandler.updateFaceData(issuedLogDO, faceDevices+","+deviceNos,pair);
        }
        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("empowerType","正式员工");
        if("WX".equals(userType)){
            paramMap2.put("empowerType","外协");
        }
        paramMap2.put("factoryIds",Arrays.asList(factoryIds.split(",")));
        //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
        paramMap2.put("regionName","大门门禁");
        String cardDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap2);
        //更新门禁卡权限
        if(StringUtils.isEmpty(cardDevices)){
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"issuedBuildPowerForProcessForTwoFs:员工号："+issuedLogDO.getEmployeeCode()+"，未获取到 设备类型(门禁)授权设备号！\"}");
        }else{
            safetyBaseHandler.updateCardData(issuedLogDO, cardDevices+","+deviceNos, "modify",pair);
        }

        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("factoryIds",Arrays.asList(factoryIds.split(",")));
        //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
        paramMap3.put("regionName","大门车行");
        if("WX".equals(userType)){
            paramMap3.put("empowerType","外协车辆");
        }else{
            paramMap3.put("empowerType","员工车辆");
        }
        if(CollectionUtils.isEmpty(carNos)){
            return;
        }
        String carDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap3);
        //更新门禁卡权限
        if (StringUtils.isBlank(carDevices)) {
            LogUtils.info("issuedBuildPowerForProcessForTwoFs.downPowerByEmpNO:员工号：" + issuedLogDO.getEmployeeCode() + "，未获取到大门车行授权设备号！");
        } else {
            // 更新车权限
            safetyBaseHandler.downCarPower(issuedLogDO, carDevices, carNos,emp.getPhoneNo(),userType);
        }
    }

    //对已整理好的门禁分类下发
    public void issuedPowerByList(List<SafetyBuildIssuedLogDO> logDOS){
        if(!CollectionUtils.isEmpty(logDOS)){
            String codeStr = logDOS.stream().map(SafetyBuildIssuedLogDO::getDeviceCode).collect(Collectors.joining(","));
            Set<String> codeSet = new HashSet<>(Arrays.asList(codeStr.split(",")));
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectList(new ArrayList<>(codeSet));
            Map<String,SafetyBuildDeviceDO> deviceDOMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, Function.identity()));
            List<SafetyBuildIssuedLogDO> oneLogList = new ArrayList<>();
            List<SafetyBuildIssuedLogDO> twoLogList = new ArrayList<>();
            logDOS.forEach(e->{
                String deviceCode = e.getDeviceCode();
                String[] curDeviceCode = deviceCode.split(",");
                for(String str:curDeviceCode){
                    SafetyBuildIssuedLogDO newLogDo = new SafetyBuildIssuedLogDO();
                    BeanUtils.copyProperties(e,newLogDo);
                    SafetyBuildDeviceDO curDevice = deviceDOMap.get(str);
                    newLogDo.setDeviceCode(str);
                    String deviceName = "";
                    if(curDevice != null){
                        if(StringUtils.isNotBlank(curDevice.getAnDeviceName())){
                            deviceName = curDevice.getAnDeviceName();
                        }else{
                            deviceName = curDevice.getDeviceName();
                        }
                    }
                    newLogDo.setDeviceName(deviceName); //优先取AN的名称
                    if(curDevice == null || "1".equals(curDevice.getDeviceType())){
                        newLogDo.setDeviceType("1");
                        oneLogList.add(newLogDo);
                    }else{
                        newLogDo.setDeviceType("2");
                        twoLogList.add(newLogDo);
                    }
                }
            });
            //下发并保存日志
            if(!CollectionUtils.isEmpty(oneLogList)){
                try{
                    issuedBuildPowerForProcessForOne(oneLogList);
                }catch (Exception e){
                    LogUtils.error("下发新开普楼宇门禁异常",e);
                }
            }
            //保存到门禁明细表中
            if(!CollectionUtils.isEmpty(twoLogList)){
                try{
                    issuedBuildPowerForProcessForTwoFs(twoLogList);
                }catch (Exception e){
                    LogUtils.error("下发富士楼宇门禁异常",e);
                }
            }
        }
    }

    //对已整理好的门禁分类下发-富士设备只保存不下发
    public void issuedPowerByListNew(String dealType,List<SafetyBuildIssuedLogDO> logDOS){
        if(!CollectionUtils.isEmpty(logDOS)){
            String codeStr = logDOS.stream().map(SafetyBuildIssuedLogDO::getDeviceCode).collect(Collectors.joining(","));
            Set<String> codeSet = new HashSet<>(Arrays.asList(codeStr.split(",")));
            List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectList(new ArrayList<>(codeSet));
            Map<String,SafetyBuildDeviceDO> deviceDOMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, Function.identity()));
            List<SafetyBuildIssuedLogDO> oneLogList = new ArrayList<>();
            List<SafetyBuildIssuedLogDO> twoLogList = new ArrayList<>();
            logDOS.forEach(e->{
                String deviceCode = e.getDeviceCode();
                String[] curDeviceCode = deviceCode.split(",");
                for(String str:curDeviceCode){
                    try{
                        SafetyBuildIssuedLogDO newLogDo = new SafetyBuildIssuedLogDO();
                        BeanUtils.copyProperties(e,newLogDo);
                        SafetyBuildDeviceDO curDevice = deviceDOMap.get(str);
                        newLogDo.setDeviceCode(str);
                        String deviceName = "";
                        if(curDevice != null){
                            if(StringUtils.isNotBlank(curDevice.getAnDeviceName())){
                                deviceName = curDevice.getAnDeviceName();
                            }else{
                                deviceName = curDevice.getDeviceName();
                            }
                            newLogDo.setDoorId(curDevice.getAnFactoryCode());
                            newLogDo.setDoorName(curDevice.getAnFactoryName());
                        }
                        newLogDo.setDeviceName(deviceName); //优先取AN的名称
                        if(curDevice == null || "1".equals(curDevice.getDeviceType())){
                            newLogDo.setDeviceType("1");
                            oneLogList.add(newLogDo);
                        }else{
                            newLogDo.setDeviceType("2");
                            twoLogList.add(newLogDo);
                        }
                    }catch (Exception ex){
                        LogUtils.error("forEache处理异常,参数："+ new JSONObject(e),ex);
                    }
                }
            });
            List<SafetyBuildIssuedLogDO> allLogs = new ArrayList<>();
            //下发并保存日志
            if(!CollectionUtils.isEmpty(oneLogList)){
                try{
                    issuedBuildPowerForProcessForOne(oneLogList);
                }catch (Exception e){
                    LogUtils.error("下发新开普楼宇门禁异常",e);
                }
                if("1".equals(dealType)){
                    allLogs.addAll(oneLogList);
                }
            }
            //保存到门禁明细表中，并标记为未下发
            if(!CollectionUtils.isEmpty(twoLogList)){
                try{
                    issuedBuildPowerForTwoFs(twoLogList);
                }catch (Exception e){
                    LogUtils.error("更新富士门禁权限异常",e);
                }
                allLogs.addAll(twoLogList);
            }
            //增加门禁时，需要把新开普涉及的大门权限也下发
            if(!CollectionUtils.isEmpty(allLogs)){
                try{
                    updateCrkPower(dealType,allLogs);
                    //关闭权限后需要对现有权限重新全量下发
                    if("2".equals(dealType)){
                        updateCrkPower("3",allLogs);
                    }
                }catch (Exception e){
                    LogUtils.error("下发富士楼宇门禁异常",e);
                }
            }
        }
    }


    /**
     * 富士的门禁下发-富士类的只整合保存不下发权限（定时任务下发权限）
     * @param logDOList 日志集合
     */
    public void issuedBuildPowerForTwoFs(List<SafetyBuildIssuedLogDO> logDOList){
        if(!CollectionUtils.isEmpty(logDOList)){
            try{
                List<SafetyBuildEmployeePowerDO> employeePowerDOS = formatEmployeePowerDO(logDOList);
                safetyBuildEmployeePowerService.insertBatch(employeePowerDOS);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"批量添加人员明细异常{}",e);
            }
        }
    }

    /**
     * 批量下发门禁权限-根据流程ID -新开普下发
     * @param logDOList 封装好的日志
     */
    public void issuedBuildPowerForProcessForOne(List<SafetyBuildIssuedLogDO> logDOList){
        Map<String,List<SafetyBuildIssuedLogDO>> empMap = logDOList.stream()
                .collect(Collectors.groupingBy(s->s.getEmployeeCode()+"_"+s.getEndTime()));
        List<SafetyBuildIssuedLogDO> issuedList = new ArrayList<>();
        empMap.forEach((key,issuedLogs)->{
            String deviceCodeStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDeviceCode)
                    .distinct().collect(Collectors.joining(","));
            String deviceNameStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDeviceName)
                    .distinct().collect(Collectors.joining(","));
            int lenth = 0;
            if(StringUtils.isNotBlank(deviceNameStr)){
                lenth = deviceNameStr.length();
            }
            String factoryIdStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDoorId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(","));
            String factoryNameStr = issuedLogs.stream().map(SafetyBuildIssuedLogDO::getDoorName).filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(","));
            issuedLogs.get(0).setDoorId(factoryIdStr);
            issuedLogs.get(0).setDoorName(factoryNameStr);
            issuedLogs.get(0).setDeviceCode(deviceCodeStr);
            issuedLogs.get(0).setDeviceName(lenth>3000?deviceNameStr.substring(0,3000):deviceNameStr);
            issuedList.add(issuedLogs.get(0));
        });

        List<SafetyBuildIssuedLogDO> resultList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> yesList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(issuedList)){
            for(SafetyBuildIssuedLogDO issuedLogDO:issuedList){
                String issuedStatus = "异常";
                try{
                    issuedStatus = safetyBuildIssuedLogService.issuedBuildPower(issuedLogDO);
                }catch (Exception e){
                    LogUtils.error("权限下发失败,参数{}", JSONObject.valueToString(issuedLogDO));
                }
                issuedLogDO.setIssuedRemark(issuedStatus);
                String key = issuedLogDO.getEmployeeCode()+"_"+issuedLogDO.getEndTime();
                List<SafetyBuildIssuedLogDO> curList = empMap.get(key);
                if("成功".equals(issuedStatus)){
                    issuedLogDO.setIssuedStatus(issuedStatus);
                    yesList.addAll(curList);
                }else{
                    issuedLogDO.setIssuedStatus("异常");
                    LogUtils.info("员工{}下发新开普失败{}",key,issuedStatus);
                }
                resultList.add(issuedLogDO);
            }
            try {
                safetyBuildIssuedLogService.batchInsert(resultList);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"批量保存异常{}",e);
            }
            if(!CollectionUtils.isEmpty(yesList)){
                try{
                    List<SafetyBuildEmployeePowerDO> employeePowerDOS = formatEmployeePowerDO(yesList);
                    safetyBuildEmployeePowerService.insertBatch(employeePowerDOS);
                }catch (Exception e){
                    LogUtils.error(ResponseEnum.FAIL.getCode(),"批量添加人员明细异常{}",e);
                }
            }
        }
    }

    /**
     * 整理个人权限数据
     * @param logDOList 日志
     * @return 返回值
     */
    public List<SafetyBuildEmployeePowerDO> formatEmployeePowerDO(List<SafetyBuildIssuedLogDO> logDOList){
        List<SafetyBuildEmployeePowerDO> powerDOS = new ArrayList<>();
        logDOList.forEach(e->{
            SafetyBuildEmployeePowerDO curEmpPower = new SafetyBuildEmployeePowerDO();
            BeanUtils.copyProperties(e,curEmpPower);
            curEmpPower.setId(null);
            curEmpPower.setProcessId(e.getTargetId());
            curEmpPower.setDeviceCode(e.getDeviceCode().split(",")[0]);
            curEmpPower.setDeviceName(e.getDeviceName().split(",")[0]);
            curEmpPower.setRemark(e.getRemark());
            curEmpPower.setDutyEmpName(e.getCreateBy());
            curEmpPower.setDeleteFlag("0");
            if("关闭".equals(e.getIssuedType())){
                curEmpPower.setDeleteFlag("1");
                curEmpPower.setDeleteTime(new Date());
                curEmpPower.setDeleteProcessId(e.getTargetId());
            }
            powerDOS.add(curEmpPower);
        });
        return powerDOS;
    }

    /**
     * 提取开通清单中的人员信息
     * @param empOpenList 开通清单
     */
    private Map<String,SafetyEmployeeMessageDO> getEmployeeByOpenList(List<SafetyBuildProcessOpenDO> empOpenList){
        String empNoStr = empOpenList.stream().map(SafetyBuildProcessOpenDO::getEmpowerAreaCode).distinct().collect(Collectors.joining(";"));
        List<SafetyEmployeeMessageDO> emloyeeList = safetyEmployeeMessageService.getListByStaffNo(Arrays.asList(empNoStr.split(";")));
        //黑名单中的人员禁止下发人脸 条件：状态=禁止入厂，备注=禁止授权条件判断
        //emloyeeList = safetyBaseHandler.filterBlackEmployee(emloyeeList);
        return emloyeeList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, Function.identity(),(existing, replacement) -> existing));
    }

    /**
     * 提取关闭清单中的人员信息
     * @param empCloseList 关闭清单
     */
    private Map<String,SafetyEmployeeMessageDO> getEmployeeByCloseList(List<SafetyBuildProcessCloseDO> empCloseList){
        String empNoStr = empCloseList.stream().map(SafetyBuildProcessCloseDO::getEmpowerAreaCode).distinct().collect(Collectors.joining(";"));
        List<SafetyEmployeeMessageDO> emloyeeList = safetyEmployeeMessageService.getListByStaffNo(Arrays.asList(empNoStr.split(";")));
        return emloyeeList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, Function.identity(),(existing, replacement) -> existing));
    }


    //获取流程当前节点处理人信息
    public Object getProcessCurDealHandler(String processId){
        SafetyBuildProcessDO buildProcessDO = safetyBuildProcessService.selectByPrimaryKey(processId);
        if(ObjectUtils.isEmpty(buildProcessDO)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未找到对应信息，请确认入参processId="+processId+"，是否正确");
        }
        return  safetyBpmProcessService.getFlowInfoForSelf(buildProcessDO.getBpmFlowId());
    }

    /**
     *
     * @param dealType 1-开通 2-关闭
     * @param logDOList 日志
     * @param downMark 下发标记 0-已下发 1-未下发
     */
    public void dealEmpDoorPower(String dealType,List<SafetyBuildIssuedLogDO> logDOList,String downMark) {
        List<SafetyParkEmployeePowerInfoDO> powerInfoDOS = new ArrayList<>();
        List<SafetyOutsiderPromiseDTO> outsiderPromiseDTOS = new ArrayList<>();
        try {
            //权限下发涉及人员
            List<SafetyBuildIssuedLogDO> subList = logDOList.stream().filter(e -> StringUtils.isNotBlank(e.getDoorId())).collect(Collectors.toList());
            Map<String, List<SafetyBuildIssuedLogDO>> empNewPowerMap = subList.stream()
                    .collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
            //员工号集合
            List<String> empList = subList.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).distinct().collect(Collectors.toList());
            //获取员工信息
            List<SafetyEmployeeMessageDO> empMessageList = safetyEmployeeMessageService.getListByStaffNo(empList);
            //获取外包外协权限
            List<SafetyOutsiderPromiseDTO> outSiderList = safetyOutsiderPromiseChangeService.findOldPromise(String.join(",", empList), "");
            Map<String,SafetyEmployeeMessageDO> empMessageMap;
            if(!CollectionUtils.isEmpty(empMessageList)){
                empMessageMap = empMessageList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));
            }else{
                empMessageMap = new HashMap<>();
            }
            Map<String,SafetyOutsiderPromiseDTO> outSiderMap;
            if(!CollectionUtils.isEmpty(outSiderList)){
                outSiderMap = outSiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo, e -> e));
            }else{
                outSiderMap = new HashMap<>();
            }
            //新开通的门禁，确认是否需要开通大门权限
            if ("1".equals(dealType)) {
                //先查询员工当前的大门授权，判断是否有新增厂区 然后进行下发
                ParkEmployeePowerListParam param = new ParkEmployeePowerListParam();
                param.setPageFlag(false);
                param.setEmpNoList(empList);
                List<SafetyParkEmployeePowerInfoDO> empDoorList = safetyParkEmpPowerReportService.selectByExample(param);
                Map<String, List<SafetyParkEmployeePowerInfoDO>> empDoorMap;
                if (!CollectionUtils.isEmpty(empDoorList)) {
                    empDoorMap = empDoorList.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));
                } else {
                    empDoorMap = new HashMap<>();
                }
                //逐个人员排查确认
                empNewPowerMap.forEach((key, value) -> {
                    SafetyEmployeeMessageDO curEmpMessage = empMessageMap.get(key);
                    if(curEmpMessage == null){
                        return;
                    }
                    String newFactoryIds = value.stream()
                            .map(SafetyBuildIssuedLogDO::getDoorId)
                            .filter(org.apache.commons.lang3.StringUtils::isNotBlank).distinct()
                            .collect(Collectors.joining(","));

                    // 如果factoryIdList为空，直接返回
                    if (StringUtils.isBlank(newFactoryIds)) {
                        return;
                    }
                    Set<String> factoryIdList = Sets.newHashSet(newFactoryIds.split(","));;

                    String userType = curEmpMessage.getUserType();

                    //正式工处理方式
                    if("ZS".equals(userType)){
                        Set<String> nowSet = new HashSet<>();
                        List<SafetyParkEmployeePowerInfoDO> empNowList = empDoorMap.get(key);
                        if (!CollectionUtils.isEmpty(empNowList)) {
                            nowSet = empNowList.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).collect(Collectors.toSet());
                        }
                        factoryIdList.removeAll(nowSet);
                        if (!CollectionUtils.isEmpty(factoryIdList)) {
                            // 批量保存并下发
                            for (String factoryId : factoryIdList) {
                                List<SafetyBuildIssuedLogDO> curFactory = value.stream().filter(e -> e.getDoorId().equals(factoryId))
                                        .collect(Collectors.toList());

                                if (!curFactory.isEmpty()) {
                                    SafetyParkEmployeePowerInfoDO curInfo = new SafetyParkEmployeePowerInfoDO(); // 重新初始化对象
                                    curInfo.setEmployeeCode(key);
                                    curInfo.setIsDelete((short) 0);
                                    curInfo.setRemark("开通厂区门禁");
                                    curInfo.setDownMark(downMark); // 未下发
                                    curInfo.setFactoryId(factoryId);
                                    // 获取第一个元素的工厂名称和开始时间
                                    SafetyBuildIssuedLogDO firstEntry = curFactory.get(0);
                                    curInfo.setFactoryName(firstEntry.getDoorName());
                                    curInfo.setPowerBeginTime(firstEntry.getBeginTime());

                                    // 获取结束时间的最大值
                                    String endTime = value.stream()
                                            .map(SafetyBuildIssuedLogDO::getEndTime)
                                            .max(String::compareTo)
                                            .orElse(null);
                                    if (StringUtils.isNotBlank(endTime)) {
                                        curInfo.setPowerEndTime(endTime);
                                    } else {
                                        // 如果没有找到结束时间的最大值，可以设置一个默认值或者记录日志
                                        curInfo.setPowerEndTime(firstEntry.getEndTime()); // 或者设置一个默认值
                                    }
                                    powerInfoDOS.add(curInfo);
                                }
                            }
                        }else{
                            //新增的门禁权限未涉及到新厂区时，标记现有厂区权限 用户门禁权限合并下发
                            SafetyParkEmployeePowerInfoDO oldPower1 = empNowList.get(0);
                            oldPower1.setDownMark(downMark);
                            powerInfoDOS.add(oldPower1);
                        }
                    }else{
                        //外协人员处理方式
                        SafetyOutsiderPromiseDTO promiseDTO = outSiderMap.get(key);
                        if(promiseDTO == null){
                            promiseDTO = new SafetyOutsiderPromiseDTO();
                            promiseDTO.setEmpNo(key);
                            promiseDTO.setEmpName(curEmpMessage.getEmpName());
                            promiseDTO.setEmpOrg(curEmpMessage.getOrgNamePath());
                            promiseDTO.setStatus(1);
                            promiseDTO.setRemark("自动授权门禁下发厂区");
                        }
                        if(StringUtils.isBlank(promiseDTO.getServiceEndDate())){
                            // 获取结束时间的最大值
                            String endTime = Optional.of(value)
                                    .flatMap(list -> list.stream()
                                            .map(SafetyBuildIssuedLogDO::getEndTime)
                                            .max(String::compareTo))
                                    .orElse(null);
                            if (StringUtils.isNotBlank(endTime)) {
                                promiseDTO.setServiceEndDate(endTime);
                            }
                        }
                        // 获取当前厂区权限
                        Set<String> curSet = new HashSet<>();
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(promiseDTO.getFactory())) {
                            curSet.addAll(Arrays.asList(promiseDTO.getFactory().split(",")));
                        }
                        // 添加新的厂区权限
                        curSet.addAll(factoryIdList);
                        //无论厂区是否有变化，都标记出来以便门禁权限合并下发 - 20250723
                        promiseDTO.setCardStatus(downMark); // 设置卡未下发
                        promiseDTO.setFactory(String.join(",", curSet));
                        outsiderPromiseDTOS.add(promiseDTO);
                    }
                });
            } else {
                //先判断需要删除的厂区权限，再进行判断是否有需要重新下发的厂区权限
                List<String> empCodeList = logDOList.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).collect(Collectors.toList());
                BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
                listParam.setPageFlag(false);
                listParam.setEmpCodeList(empCodeList);
                //当前当前有效的门禁权限
                List<BuildEmployeePowerVO> empBuildPowerList = safetyBuildEmployeePowerService.selectList(listParam);
                Map<String, List<BuildEmployeePowerVO>> empBuildMap;
                if (!CollectionUtils.isEmpty(empBuildPowerList)) {
                    empBuildMap = empBuildPowerList.stream().collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
                } else {
                    empBuildMap = new HashMap<>();
                }
                //逐个人员排查确认
                empNewPowerMap.forEach((key, value) -> {
                    SafetyEmployeeMessageDO curEmpMessage = empMessageMap.get(key);
                    if(curEmpMessage == null){
                        return;
                    }
                    String userType = curEmpMessage.getUserType();
                    //关闭的门禁涉及厂区集合
                    String newFactoryIds = value.stream()
                            .map(SafetyBuildIssuedLogDO::getDoorId)
                            .filter(StringUtils::isNotBlank).distinct()
                            .collect(Collectors.joining(","));

                    // 如果factoryIdList为空，直接返回
                    if (StringUtils.isBlank(newFactoryIds)) {
                        return;
                    }
                    Set<String> factoryIdList = Sets.newHashSet(newFactoryIds.split(","));;

                    if (!CollectionUtils.isEmpty(factoryIdList)) {
                        Set<String> nowSet = new HashSet<>();
                        List<BuildEmployeePowerVO> empBuildList = empBuildMap.get(key);
                        if(!CollectionUtils.isEmpty(empBuildList)){
                            nowSet = empBuildList.stream().map(BuildEmployeePowerVO::getAnFactoryCode).collect(Collectors.toSet());
                        }
                        if("ZS".equals(userType)){
                            //关闭的门禁涉及厂区去掉现有门禁厂区
                            factoryIdList.removeAll(nowSet);
                            if(!CollectionUtils.isEmpty(factoryIdList)){
                                //批量保存并下发
                                for (String factoryId : factoryIdList) {
                                    List<SafetyBuildIssuedLogDO> curFactory = value.stream()
                                            .filter(e -> e.getDoorId().equals(factoryId))
                                            .collect(Collectors.toList());
                                    if (!curFactory.isEmpty()) {
                                        SafetyBuildIssuedLogDO firstEntry = curFactory.get(0);
                                        SafetyParkEmployeePowerInfoDO curInfo = new SafetyParkEmployeePowerInfoDO(); // 重新初始化对象
                                        curInfo.setEmployeeCode(key);
                                        curInfo.setIsDelete(Short.parseShort("1")); // 标记删除
                                        curInfo.setRemark("关闭厂区门禁");
                                        curInfo.setDownMark(downMark); // 未下发
                                        curInfo.setFactoryId(factoryId);
                                        curInfo.setFactoryName(firstEntry.getDoorName());
                                        curInfo.setPowerBeginTime(firstEntry.getBeginTime());
                                        curInfo.setPowerEndTime(firstEntry.getEndTime()); // 结束日期T-1
                                        powerInfoDOS.add(curInfo);
                                    }
                                }
                            }else{
                                SafetyBuildIssuedLogDO oldPower = value.get(0);
                                SafetyParkEmployeePowerInfoDO curInfo = new SafetyParkEmployeePowerInfoDO();
                                curInfo.setEmployeeCode(key);
                                curInfo.setIsDelete((short) 0);
                                curInfo.setRemark("门禁权限变更");
                                curInfo.setDownMark(downMark); // 未下发
                                curInfo.setFactoryId(oldPower.getDoorId());
                                curInfo.setFactoryName(oldPower.getDoorName());
                                curInfo.setPowerBeginTime(oldPower.getBeginTime());
                                curInfo.setPowerEndTime(oldPower.getEndTime());
                                powerInfoDOS.add(curInfo); //标记需重新下发
                            }
                        }else{
                            //外协人员处理方式
                            SafetyOutsiderPromiseDTO promiseDTO = outSiderMap.get(key);
                            if(promiseDTO == null){
                                promiseDTO = new SafetyOutsiderPromiseDTO();
                                promiseDTO.setEmpNo(key);
                                promiseDTO.setEmpName(curEmpMessage.getEmpName());
                                promiseDTO.setEmpOrg(curEmpMessage.getOrgNamePath());
                                promiseDTO.setStatus(1);
                                // 获取结束时间的最大值
                                String endTime = value.stream()
                                        .map(SafetyBuildIssuedLogDO::getEndTime)
                                        .max(String::compareTo)
                                        .orElse(null);

                                if (StringUtils.isNotBlank(endTime)) {
                                    promiseDTO.setServiceEndDate(endTime.substring(0,10));
                                }
                            }
                            if("1".equals(downMark)){
                                promiseDTO.setCardStatus("0"); // 设置卡未下发
                            }else{
                                promiseDTO.setCardStatus("1"); // 设置卡未下发
                            }
                            promiseDTO.setFactory(String.join(",", nowSet));
                            outsiderPromiseDTOS.add(promiseDTO);
                        }
                    }
                });
            }
            //此处只更新正式工和外协人员的大门权限，不做下发动作，定时任务定时下发
            if(!CollectionUtils.isEmpty(powerInfoDOS)){
                //保存涉及人员的变更前大门权限
                List<String> empNos = powerInfoDOS.stream().map(SafetyParkEmployeePowerInfoDO::getEmployeeCode).distinct().collect(Collectors.toList());
                try{
                    //查询出注销人员当前的门禁权限，保存到history表中
                    ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
                    powerList.setEmpNoList(empNos);
                    List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoService.getDetailByCon(powerList);
                    safetyParkEmpPowerReportService.batchUpdateHistoryPower(curList,"权限变更");
                }catch (Exception e){
                    LogUtils.error("批量保存最后大门权限异常",e);
                }
                safetyParkPowerProcessService.dealPower(powerInfoDOS,"ZS");
            }
            if(!CollectionUtils.isEmpty(outsiderPromiseDTOS)){
                safetyOutsiderPromiseChangeService.saveOrUpdate(outsiderPromiseDTOS);
                //0-标识权限已下发，需回传EHR
                if("0".equals(downMark)){
                    safetyBaseHandler.sendToEHRForFactory(outsiderPromiseDTOS);
                }
            }
        }catch (Exception e){
            LogUtils.error("处理员工大门权限异常",e);
        }
    }


    private void updateExistingFactory(String factoryId, String applyEndTime, Map<String, String> oldDoorEndMap,
                                       Map<String, SafetyParkEmployeePowerInfoDO> oldDoorPowerMap, List<SafetyParkEmployeePowerInfoDO> powerInfoDOS) {
        String oldEndTime = oldDoorEndMap.get(factoryId);
        if (org.apache.commons.lang3.StringUtils.isBlank(applyEndTime) || org.apache.commons.lang3.StringUtils.isBlank(oldEndTime)) {
            return;
        }
        oldEndTime = oldEndTime.substring(0,10);
        applyEndTime = applyEndTime.substring(0,10);
        boolean isBefore = DateUtils.isBeforeStr(oldEndTime, applyEndTime);
        if (isBefore) {
            SafetyParkEmployeePowerInfoDO curParkPower = oldDoorPowerMap.get(factoryId);
            curParkPower.setPowerEndTime(applyEndTime);
            curParkPower.setIsDelete((short) 0);
            curParkPower.setRemark("根据申请楼宇截止日期更新厂区截止日期");
            powerInfoDOS.add(curParkPower);
        }
    }

    private void addNewFactory(String key, String factoryId, List<SafetyBuildIssuedLogDO> value, String applyEndTime,
                               String endTime, String downMark, List<SafetyParkEmployeePowerInfoDO> powerInfoDOS) {
        List<SafetyBuildIssuedLogDO> curFactory = value.stream()
                .filter(e -> e.getDoorId().equals(factoryId))
                .collect(Collectors.toList());

        if (!curFactory.isEmpty()) {
            SafetyParkEmployeePowerInfoDO curInfo = new SafetyParkEmployeePowerInfoDO();
            curInfo.setEmployeeCode(key);
            curInfo.setIsDelete((short) 0);
            curInfo.setRemark("开通厂区门禁");
            curInfo.setDownMark(downMark);
            curInfo.setFactoryId(factoryId);

            SafetyBuildIssuedLogDO firstEntry = curFactory.get(0);
            curInfo.setFactoryName(firstEntry.getDoorName());
            curInfo.setPowerBeginTime(firstEntry.getBeginTime());

            if (StringUtils.isBlank(applyEndTime)) {
                curInfo.setPowerEndTime(StringUtils.isNotBlank(endTime) ? endTime : firstEntry.getEndTime());
            } else {
                curInfo.setPowerEndTime(applyEndTime);
            }

            powerInfoDOS.add(curInfo);
        }
    }

    /**
     *
     * @param dealType 1-开通 2-关闭
     * @param logDOList 集合
     * @param downMark 下发标记 0-已下发 1-未下发
     */
    public void dealEmpDoorPowerNew(String dealType,List<SafetyBuildIssuedLogDO> logDOList,String downMark) {
        List<SafetyParkEmployeePowerInfoDO> powerInfoDOS = new ArrayList<>();
        List<SafetyOutsiderPromiseDTO> outsiderPromiseDTOS = new ArrayList<>();
        try {
            //权限下发涉及人员
            List<SafetyBuildIssuedLogDO> subList = logDOList.stream().filter(e -> StringUtils.isNotBlank(e.getDoorId())).collect(Collectors.toList());
            Map<String, List<SafetyBuildIssuedLogDO>> empNewPowerMap = subList.stream()
                    .collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
            //员工号集合
            List<String> empList = subList.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).distinct().collect(Collectors.toList());
            //获取员工信息
            List<SafetyEmployeeMessageDO> empMessageList = safetyEmployeeMessageService.getListByStaffNo(empList);
            //获取外包外协权限
            List<SafetyOutsiderPromiseDTO> outSiderList = safetyOutsiderPromiseChangeService.findOldPromise(String.join(",", empList), "");
            Map<String,SafetyEmployeeMessageDO> empMessageMap;
            if(!CollectionUtils.isEmpty(empMessageList)){
                empMessageMap = empMessageList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));
            }else{
                empMessageMap = new HashMap<>();
            }
            Map<String,SafetyOutsiderPromiseDTO> outSiderMap;
            if(!CollectionUtils.isEmpty(outSiderList)){
                outSiderMap = outSiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo, e -> e));
            }else{
                outSiderMap = new HashMap<>();
            }
            //新开通的门禁，确认是否需要开通大门权限
            if ("1".equals(dealType)) {
                //先查询员工当前的大门授权，判断是否有新增厂区 然后进行下发
                ParkEmployeePowerListParam param = new ParkEmployeePowerListParam();
                param.setPageFlag(false);
                param.setEmpNoList(empList);
                List<SafetyParkEmployeePowerInfoDO> empDoorList = safetyParkEmpPowerReportService.selectByExample(param);
                Map<String, List<SafetyParkEmployeePowerInfoDO>> empDoorMap;
                if (!CollectionUtils.isEmpty(empDoorList)) {
                    empDoorMap = empDoorList.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));
                } else {
                    empDoorMap = new HashMap<>();
                }
                //逐个人员排查确认
                empNewPowerMap.forEach((key, value) -> {
                    SafetyEmployeeMessageDO curEmpMessage = empMessageMap.get(key);
                    if(curEmpMessage == null){
                        return;
                    }
                    String newFactoryIds = value.stream()
                            .map(SafetyBuildIssuedLogDO::getDoorId)
                            .filter(StringUtils::isNotBlank).distinct()
                            .collect(Collectors.joining(","));
                    //申请的楼宇涉及厂区及厂区对应最大截止日期
                    Map<String,String> newDoorEndMap = new HashMap<>();
                    Map<String,List<SafetyBuildIssuedLogDO>> factoryBuildMap = value.stream().collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getDoorId));
                    factoryBuildMap.forEach((doorId,logList)->{
                        if(StringUtils.isBlank(doorId) || CollectionUtils.isEmpty(logList)){
                            return;
                        }
                        Optional<SafetyBuildIssuedLogDO> doMaxShort = logList.stream().max(Comparator.comparing(SafetyBuildIssuedLogDO::getEndTime));
                        doMaxShort.ifPresent(build -> {
                            newDoorEndMap.put(doorId,build.getEndTime());
                        });
                    });

                    // 如果factoryIdList为空，直接返回
                    if (StringUtils.isBlank(newFactoryIds)) {
                        return;
                    }
                    Set<String> factoryIdList = Sets.newHashSet(newFactoryIds.split(","));;
                    String userType = curEmpMessage.getUserType();

                    //正式工处理方式
                    if(!"WX".equals(userType)){
                        Set<String> nowSet = new HashSet<>();
                        //申请的楼宇涉及厂区及厂区对应最大截止日期
                        Map<String,String> oldDoorEndMap = new HashMap<>();
                        Map<String,SafetyParkEmployeePowerInfoDO> oldDoorPowerMap = new HashMap<>();
                        List<SafetyParkEmployeePowerInfoDO> empNowList = empDoorMap.get(key);
                        if (!CollectionUtils.isEmpty(empNowList)) {
                            nowSet = empNowList.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).collect(Collectors.toSet());
                            empNowList.forEach(e->{
                                oldDoorEndMap.put(e.getFactoryId(),e.getPowerEndTime());
                                oldDoorPowerMap.put(e.getFactoryId(),e);
                            });
                        }
                        // 获取结束时间的最大值
                        String endTime = value.stream()
                                .map(SafetyBuildIssuedLogDO::getEndTime)
                                .max(String::compareTo)
                                .orElse(null);

                        for(String factoryId : factoryIdList){
                            String applyEndTime = newDoorEndMap.get(factoryId);
                            try {
                                //已存在的厂区，判断是否需要更新服务截止日期
                                if (nowSet.contains(factoryId)) {
                                    updateExistingFactory(factoryId, applyEndTime, oldDoorEndMap, oldDoorPowerMap, powerInfoDOS);
                                } else {
                                    addNewFactory(key, factoryId, value, applyEndTime, endTime, downMark, powerInfoDOS);
                                }
                            }catch (Exception e){
                                LogUtils.error(factoryId+"工厂下楼宇权限处理异常",e);
                            }
                        }
                    }else{
                        //外协人员处理方式-只更新不新增
                        SafetyOutsiderPromiseDTO promiseDTO = outSiderMap.get(key);
                        if(promiseDTO == null){
                            return;
                        }
                        if(StringUtils.isBlank(promiseDTO.getServiceEndDate())){
                            // 获取结束时间的最大值
                            String endTime = Optional.of(value)
                                    .flatMap(list -> list.stream()
                                            .map(SafetyBuildIssuedLogDO::getEndTime)
                                            .max(String::compareTo))
                                    .orElse(null);
                            if (StringUtils.isNotBlank(endTime)) {
                                promiseDTO.setServiceEndDate(endTime);
                            }
                        }
                        // 获取当前厂区权限
                        Set<String> curSet = new HashSet<>();
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(promiseDTO.getFactory())) {
                            curSet.addAll(Arrays.asList(promiseDTO.getFactory().split(",")));
                        }
                        // 添加新的厂区权限
                        curSet.addAll(factoryIdList);
                        //无论厂区是否有变化，都标记出来以便门禁权限合并下发 - 20250723
                        promiseDTO.setCardStatus(downMark); // 设置卡未下发
                        promiseDTO.setFactory(String.join(",", curSet));
                        outsiderPromiseDTOS.add(promiseDTO);
                    }
                });
            } else {
                //先判断需要删除的厂区权限，再进行判断是否有需要重新下发的厂区权限
                List<String> empCodeList = logDOList.stream().map(SafetyBuildIssuedLogDO::getEmployeeCode).collect(Collectors.toList());
                BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
                listParam.setPageFlag(false);
                listParam.setEmpCodeList(empCodeList);
                //当前当前有效的门禁权限
                List<BuildEmployeePowerVO> empBuildPowerList = safetyBuildEmployeePowerService.selectList(listParam);
                //逐个人员排查确认 关闭楼宇权限时不关闭对应厂区权限
                empNewPowerMap.forEach((key, value) -> {
                    SafetyEmployeeMessageDO curEmpMessage = empMessageMap.get(key);
                    if(curEmpMessage == null){
                        return;
                    }
                    String userType = curEmpMessage.getUserType();
                    if("ZS".equals(userType)){
                        SafetyBuildIssuedLogDO oldPower = value.get(0);
                        SafetyParkEmployeePowerInfoDO curInfo = new SafetyParkEmployeePowerInfoDO();
                        curInfo.setEmployeeCode(key);
                        curInfo.setIsDelete((short) 0);
                        curInfo.setRemark("门禁权限变更");
                        curInfo.setDownMark(downMark); // 未下发
                        curInfo.setFactoryId(oldPower.getDoorId());
                        curInfo.setFactoryName(oldPower.getDoorName());
                        curInfo.setPowerBeginTime(oldPower.getBeginTime());
                        curInfo.setPowerEndTime(oldPower.getEndTime());
                        powerInfoDOS.add(curInfo); //标记需重新下发
                    }else{
                        //外协人员处理方式
                        SafetyOutsiderPromiseDTO promiseDTO = outSiderMap.get(key);
                        if("1".equals(downMark)){
                            promiseDTO.setCardStatus("0"); // 设置卡未下发
                        }else{
                            promiseDTO.setCardStatus("1"); // 设置卡未下发
                        }
                        outsiderPromiseDTOS.add(promiseDTO);
                    }
                });
            }
            //此处只更新正式工和外协人员的大门权限，不做下发动作，定时任务定时下发
            if(!CollectionUtils.isEmpty(powerInfoDOS)){
                //保存涉及人员的变更前大门权限
                List<String> empNos = powerInfoDOS.stream().map(SafetyParkEmployeePowerInfoDO::getEmployeeCode).distinct().collect(Collectors.toList());
                try{
                    //查询出注销人员当前的门禁权限，保存到history表中
                    ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
                    powerList.setEmpNoList(empNos);
                    List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoService.getDetailByCon(powerList);
                    safetyParkEmpPowerReportService.batchUpdateHistoryPower(curList,"权限变更");
                }catch (Exception e){
                    LogUtils.error("批量保存最后大门权限异常",e);
                }
                safetyParkPowerProcessService.dealPower(powerInfoDOS,"ZS");
            }
            if(!CollectionUtils.isEmpty(outsiderPromiseDTOS)){
                safetyOutsiderPromiseChangeService.saveOrUpdate(outsiderPromiseDTOS);
                //0-标识权限已下发,且开通楼宇权限的，需回传EHR
                if("0".equals(downMark) && "1".equals(dealType)){
                    safetyBaseHandler.sendToEHRForFactory(outsiderPromiseDTOS);
                }
            }
        }catch (Exception e){
            LogUtils.error("处理员工大门权限异常",e);
        }
    }

    /**
     * 发送消息给门禁管理员
     * @param message 消息内容
     */
    private void sendYuXinTongMsg(String message) {
        // 1.获取门禁管理员的人员信息
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName(ACCESS_CONTROL_ADMIN_ROLE);
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if (CollectionUtils.isEmpty(list)) return;
        // 2.发送消息
        list.forEach(li -> yxtRebotUtils.postForEntity(null, incomintRebotId, li.getEmpNo(), message, new RestTemplate()));
    }

    /**
     * 权限下发
     * @param logDO 下发参数
     * @param carList 员工车辆
     * @return 返回状态
     */
    public boolean powerUpdateForBuild(SafetyBuildIssuedLogDO logDO, List<SafetyEmployeePrivateCarDO> carList,
                                       SafetyEmpCardVO empCardVO,String telephone,String userType) {
        boolean faceResult = false, cardResult = false, carResult = false;
        //大门和楼宇都为空时，跳出
        if (StringUtils.isBlank(logDO.getDoorId()) && StringUtils.isBlank(logDO.getDeviceCode())) {
            return false;
        }
        String empNo = logDO.getEmployeeCode();
        String factoryId = logDO.getDoorId();
        String buildDeviceCode = logDO.getDeviceCode();
        String faceDevices = "";  //大门人脸设备
        String cardDevices = "";  //大门卡设备
        String carDevices = "";  //大门车行设备
        if(StringUtils.isNotBlank(factoryId)){
            Map<String, Object> map = new HashMap<>();
            map.put("factoryIds", Arrays.asList(factoryId.split(",")));
            map.put("empowerType", "正式员工");
            map.put("regionName", "大门人脸");
            if("WX".equals(userType)){
                map.put("empowerType","外协");
            }
            faceDevices = safetyOutsiderPromiseChangeService.findDevices(map);
            map.put("regionName", "大门门禁");
            cardDevices = safetyOutsiderPromiseChangeService.findDevices(map);

            map.put("regionName", "大门车行");
            map.put("empowerType", "员工车辆");
            if("WX".equals(userType)){
                map.put("empowerType", "外协车辆");
            }
            if(!CollectionUtils.isEmpty(carList)) {
                carDevices = safetyOutsiderPromiseChangeService.findDevices(map);
            }
        }

        if (StringUtils.isBlank(faceDevices) && StringUtils.isBlank(buildDeviceCode)) {
            LogUtils.info("powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(人脸) \"正式员工\" 授权设备号！",empNo,factoryId);
        } else {
            faceDevices = PinYinUtils.stringFormat(faceDevices, buildDeviceCode);
            try {
                //获取生活照 - 接口取不到生活照会自动去证件照
                String lifePhoto = safetyBaseHandler.getPhoto(empNo, 1);
                //未获取到照片
                if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
                    LogUtils.info("{}获取员工人脸照片失败!",empNo);
                }else{
                    //图片转化为二进制
                    lifePhoto = lifePhoto + "&apikey=" + apikey;
                    Map<String, List<String>> deviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(faceDevices);
                    // 更新门禁人脸权限
                    faceResult = safetyBaseHandler.updateFaceData(logDO,empCardVO,deviceGidMap,lifePhoto);
                }
            }catch (Exception e){
                LogUtils.error("下发人脸权限失败{}",e);
            }
        }

        //更新门禁卡权限
        if (StringUtils.isBlank(cardDevices) && StringUtils.isBlank(buildDeviceCode)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"正式员工\" 授权设备号！\"}",empNo,factoryId);
        } else {
            cardDevices = PinYinUtils.stringFormat(cardDevices, buildDeviceCode);
            try{
                Map<String, List<String>> deviceGidMap = safetyBaseHandler.getDeviceGidMapModelFromAb(cardDevices);
                cardResult = safetyBaseHandler.updateCardData(logDO,"modify", empCardVO,deviceGidMap);
            }catch (Exception e){
                LogUtils.error("下发卡权限失败{}",e);
            }
        }

        //下发车辆
        if (CollectionUtils.isEmpty(carList)) {
            return faceResult || cardResult;
        }
        if (StringUtils.isBlank(carDevices)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"员工车辆\" 授权设备号！\"}",empNo,factoryId);
        } else {
            try {
                carResult = safetyBaseHandler.downCarPower(logDO, carDevices, carList,telephone,userType);
            }catch (Exception e){
                LogUtils.error("下发车辆权限失败{}",e);
            }
        }
        return faceResult || cardResult || carResult;
    }

    /**
     * 获取指定流程涉及到的EIP角色
     * @param processId 流程ID
     * @return 返回值
     */
    public Object getEipRoleByProcessId(String processId){
        List<SafetyBuildPowerDO> retList = new ArrayList<>();
        BuildProcessOpenListParam listParam = new BuildProcessOpenListParam();
        listParam.setProcessId(processId);
        //listParam.setApplyStatus("1"); //只返回审核通过的
        List<SafetyBuildProcessOpenDO> openDOList = safetyBuildProcessService.selectOpenList(listParam);
        List<String> dutyDeptList = openDOList.stream().map(SafetyBuildProcessOpenDO::getDockingDeptCode).distinct().collect(Collectors.toList());
        if(dutyDeptList.isEmpty()) {
            return retList;
        }
        BuildPowerListParam powerParam = new BuildPowerListParam();
        powerParam.setDutyDeptCodeList(dutyDeptList);
        List<SafetyBuildPowerDO> buildPowerDOS = safetyBuildPowerService.selectList(powerParam);
        if(buildPowerDOS.isEmpty()){
            return retList;
        }
        Map<String,SafetyBuildPowerDO> powerDOMap = new HashMap<>();
        for(SafetyBuildPowerDO curPower:buildPowerDOS){
            SafetyBuildPowerDO newPower = new SafetyBuildPowerDO();
            String role = curPower.getBuildSysManagerRole();
            if(StringUtils.isNotBlank(role)){
                newPower.setDutyDeptCode(curPower.getDutyDeptCode());
                newPower.setBuildSysManagerRole(curPower.getBuildSysManagerRole());
                powerDOMap.put(curPower.getDutyDeptCode(),newPower);
            }
        }
        return new ArrayList<>(powerDOMap.values());
    }

    /**
     * 楼宇门禁申请流程-部分通过
     * @param param 入参
     * @param token 人员信息
     */
    public BpmNodeReturnVO auditPartProcess(BuildProcessUpdateParam param, String token) {
        // 入参校验
        checkParam(param, token);

        // 更新开通的门禁的状态
        this.updateOpenDataForStatus(param);
        // 提取流程的审批意见
        String returnAuditNote = initAuditNote(param);
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId(), param.getFlowId(),
                PROCESS_MODEL_ID, PROCESS_FORM_ID, param.getCurPersonLogin(), false, returnAuditNote, "");

        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(), param.getFlowId(),
                PROCESS_MODEL_ID, PROCESS_FORM_ID, processParam.getProcessParam(), param.getCurPersonLogin());
        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        assert status != null;
        returnVO.setStatus(status.substring(0, 1));
        returnVO.setMessage(status);
        return returnVO;
    }

    /**
     * 更新开通的门禁的状态
     * @param param 入参，基础信息
     */
    private void updateOpenDataForStatus(BuildProcessUpdateParam param) {
        // 获取流程数据
        SafetyBuildProcessDO curBuildProcessDO = safetyBuildProcessService.selectByPrimaryKey(param.getId());
        if (curBuildProcessDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前审批流程数据不存在");
        }

        // 审批通过的数据
        List<SafetyBuildProcessOpenDO> updateList = generateOpenData(param.getPassIdStr(), param, true);
        // 审批不通过的数据
        updateList.addAll(generateOpenData(param.getFailIdStr(), param, false));
        // 更新开通的数据状态
        if (!CollectionUtils.isEmpty(updateList)) {
            safetyBuildProcessService.batchUpdateOpen(updateList);
        }
        // 关闭的流程
        updateCloseData(param);

        String id = param.getId();
        // 获取开通的所有明细
        BuildProcessOpenListParam listParam = new BuildProcessOpenListParam();
        listParam.setProcessId(id);
        List<SafetyBuildProcessOpenDO> openList = safetyBuildProcessService.selectOpenList(listParam);
        // 提取未审批或者审批通过的数量
        long count = openList.stream().filter(e -> StringUtils.isBlank(e.getApplyStatus()) || "1".equals(e.getApplyStatus())).count();
        // true 表示没有审批通过的
        boolean openRefuseFlag = count == 0;

        SafetyBuildProcessDOWithBLOBs buildProcessDO = new SafetyBuildProcessDOWithBLOBs();
        //开通列表全部拒绝后，设置表拒绝标记
        if(openRefuseFlag && !"2".equals(curBuildProcessDO.getPowerType())){
            buildProcessDO.setId(id);
            buildProcessDO.setRefuseFlag("1");
        }
        //员工提交，本部门负责人设置为审批人
        if("1".equals(curBuildProcessDO.getRoleType()) &&"N5".equals(curBuildProcessDO.getCurNodeId())){
            buildProcessDO.setId(id);
            buildProcessDO.setDepartEmployeeLogin(param.getCurPersonLogin());
            buildProcessDO.setDepartEmployeeName(param.getCurPersonName());
        }
        if(StringUtils.isNotBlank(buildProcessDO.getId())){
            safetyBuildProcessService.updateByPrimaryKeySelective(buildProcessDO);
        }
    }

    /**
     * 生成更新开通明细的数据
     * @param dataIdStr 数据行ID
     * @param param 入参，获取主数据行ID和人员信息
     * @param type 类型， true表示通过的，false表示不通过的
     * @return 返回数据明细
     */
    private List<SafetyBuildProcessOpenDO> generateOpenData(String dataIdStr, BuildProcessUpdateParam param, Boolean type) {
        if (StringUtils.isBlank(dataIdStr)) {
            return Lists.newArrayList();
        }
        String status = type ? "1" : "2";
        String reason = type ? "审批通过" : param.getCurPersonName() + "审批不通过";
        String applyTime = DateUtils.format(new Date(),2);
        return Arrays.stream(dataIdStr.split(",")).map(li -> {
            SafetyBuildProcessOpenDO curOpen = new SafetyBuildProcessOpenDO();
            curOpen.setId(li);
            curOpen.setProcessId(param.getId());
            curOpen.setApplyStatus(status);
            curOpen.setRefuseReason(reason);
            curOpen.setApplyEmployee(param.getCurPersonName());
            curOpen.setApplyTime(applyTime);
            return curOpen;
        }).collect(Collectors.toList());
    }

    /**
     * 部门通过按钮中，关闭的门禁，默认是通过的状态
     */
    private void updateCloseData(BuildProcessUpdateParam param) {
        if (StringUtils.isBlank(param.getCloseIdStr())) {
            return;
        }

        String applyTime = DateUtils.format(new Date(),2);
        List<SafetyBuildProcessCloseDO> closeData = Arrays.stream(param.getCloseIdStr().split(",")).map(li -> {
            SafetyBuildProcessCloseDO curOpen = new SafetyBuildProcessCloseDO();
            curOpen.setId(li);
            curOpen.setProcessId(param.getId());
            curOpen.setApplyStatus("1");
            curOpen.setRefuseReason("审批通过");
            curOpen.setApplyEmployee(param.getCurPersonName());
            curOpen.setApplyTime(applyTime);
            return curOpen;
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(closeData)) {
            safetyBuildProcessService.batchUpdateClose(closeData);
        }
    }


    /**
     * 入参校验
     * @param param 入参
     * @param token 人员信息
     */
    private void checkParam(BuildProcessUpdateParam param, String token) {
        if(StringUtils.isBlank(param.getCurPersonLogin()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“curPersonLogin”不能为空");
        }
        Map<String, String> userMap = TokenUtils.getTockenUser(token);
        if(StringUtils.isBlank(param.getCurPersonLogin())){
            if(StringUtils.isBlank(userMap.get("userName"))){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            param.setCurPersonLogin(userMap.get("userName"));
        }
        param.setCurPersonNo(userMap.get("workNo"));
        param.setCurPersonName(userMap.get("realName"));
    }

    /**
     * 校验开通门禁是否合规（门禁管理员，可对本部门下的员工或管理的门禁进行开通申请）
     * @return 校验结果
     */
    public String validCloseListForCreate(List<SafetyBuildProcessCloseDO> closeList,
                                          List<String> manageDeviceList,
                                          List<String> manageDeptList){
        List<SafetyBuildProcessCloseDO> deptCloseList = closeList.stream().filter(e->"2".equals(e.getEmpowerType())).collect(Collectors.toList());
        List<SafetyBuildProcessCloseDO> empCloseList = closeList.stream().filter(e->"1".equals(e.getEmpowerType())).collect(Collectors.toList());
        StringBuilder errorMsg = new StringBuilder();
        //判断组织
        String errorDeptMsg = subValidCloseForDept(deptCloseList,manageDeviceList,manageDeptList);
        errorMsg.append(errorDeptMsg);
        //判断员工
        String errorEmpMsg = subValidCloseForEmp(empCloseList,manageDeviceList,manageDeptList);
        if(!"".contentEquals(errorMsg)){
            errorMsg.append(",");
        }
        errorMsg.append(errorEmpMsg);
        if(!"".contentEquals(errorMsg)){
            errorMsg.insert(0,"第").append("行申请不符合规范，非您责任部门内员工或组织，且非您管理门禁");
        }
        return errorMsg.toString();
    }

    /**
     * 校验按组织开通的权限
     * @param deptCloseList 按组织开通的集合
     * @param manageDeviceList 管理员管理的门禁设备
     * @param manageDeptList 管理员管理的责任部门
     * @return 返回值
     */
    private String subValidCloseForDept(List<SafetyBuildProcessCloseDO> deptCloseList,
                                        List<String> manageDeviceList,
                                        List<String> manageDeptList){
        if(deptCloseList.isEmpty()){
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        for(SafetyBuildProcessCloseDO closeDO:deptCloseList){
            boolean isMatched = false;
            String curDept = closeDO.getEmpowerAreaCode();
            String[] deviceCodeArr = closeDO.getDeviceCode().split(",");
            for(String curs:deviceCodeArr){
                if(manageDeviceList.contains(curs)) {
                    isMatched = true;
                    break;
                }
            }
            if (isMatched) {
                continue;
            }
            for(String curDuty:manageDeptList) {
                if(curDept.startsWith(curDuty)){
                    isMatched = true;
                    break;
                }
            }
            if (isMatched) {
                continue;
            }
            if(!"".contentEquals(errorMsg)){
                errorMsg.append(",");
            }
            errorMsg.append(closeDO.getRowNo());
        }
        return errorMsg.toString();
    }

    /**
     * 校验按人员开通的权限
     * @param openList 按人员开通的集合
     * @param manageDeviceList 管理员管理的门禁设备
     * @param manageDeptList 管理员管理的责任部门
     * @return 返回值
     */
    private String subValidCloseForEmp(List<SafetyBuildProcessCloseDO> openList,List<String> manageDeviceList,
                                      List<String> manageDeptList){
        if(openList.isEmpty()){
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        String validEmpNos = openList.stream().map(SafetyBuildProcessCloseDO::getEmpowerAreaCode).distinct().collect(Collectors.joining(";"));
        List<SafetyEmployeeMessageDO> empList = safetyEmployeeMessageService.getListByStaffNo(Arrays.asList(validEmpNos.split(";")));
        Map<String,String> empDeptMap = empList.stream().filter(e->StringUtils.isNotBlank(e.getOrgCodePath()))
                .collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo,
                        SafetyEmployeeMessageDO::getOrgCodePath,
                        (oldValue,newValue)->newValue));
        for(SafetyBuildProcessCloseDO closeDO:openList){
            String[] empNoArr = closeDO.getEmpowerAreaCode().split(";");

            String[] deviceCodeArr = closeDO.getDeviceCode().split(",");

            boolean isMatched = true;
            for(String curs:deviceCodeArr){
                if(!manageDeviceList.contains(curs)) {
                    isMatched = false;
                    break;
                }
            }
            if (isMatched) {
                continue;
            }
            for(String curEmp:empNoArr){
                String orgCodePath = empDeptMap.get(curEmp);
                boolean curEmpFlag = false;
                if(StringUtils.isNotBlank(orgCodePath)){
                    //同一条记录中多人拼接的申请，只要有一个人不归当前管理员负责则判断false跳出单条循环
                    for(String curDuty:manageDeptList){
                        if(orgCodePath.startsWith(curDuty)){
                            curEmpFlag = true;
                            break;
                        }
                    }
                }
                isMatched = curEmpFlag;
                //同一条记录中多人拼接的申请，只要有一个人不归当前管理员负责则判断false跳出单条循环
                if(!isMatched){
                    break;
                }
            }
            //整条记录的人或设备有一个条件满足归门禁管理员管理，判断true,不拼接异常判断
            if (isMatched) {
                continue;
            }
            if(!"".contentEquals(errorMsg)){
                errorMsg.append(",");
            }
            errorMsg.append(closeDO.getRowNo());
        }

        return errorMsg.toString();
    }

    /**
     * 校验开通门禁是否合规（门禁管理员，可对本部门下的员工或管理的门禁进行开通申请）
     * @param openList 入参
     * @return 校验结果
     */
    public String validOpenListForCreate(List<SafetyBuildProcessOpenDO> openList,
                                         List<String> manageDeviceList,
                                         List<String> manageDeptList){
        List<SafetyBuildProcessOpenDO> deptOpenList = openList.stream().filter(e->"2".equals(e.getEmpowerType())).collect(Collectors.toList());
        List<SafetyBuildProcessOpenDO> empOpenList = openList.stream().filter(e->"1".equals(e.getEmpowerType())).collect(Collectors.toList());
        StringBuilder errorMsg = new StringBuilder();
        //判断组织
        String errorDeptMsg = subValidOpenForDept(deptOpenList,manageDeviceList,manageDeptList);
        errorMsg.append(errorDeptMsg);
        //判断员工
        String errorEmpMsg = subValidOpenForEmp(empOpenList,manageDeviceList,manageDeptList);
        if(!"".contentEquals(errorMsg)){
            errorMsg.append(",");
        }
        errorMsg.append(errorEmpMsg);
        if(!"".contentEquals(errorMsg)){
            errorMsg.insert(0,"第").append("行申请不符合规范，非您责任部门内员工或组织，且非您管理门禁");
        }
        return errorMsg.toString();
    }

    /**
     * 校验按组织开通的权限
     * @param deptOpenList 按组织开通的集合
     * @param manageDeviceList 管理员管理的门禁设备
     * @param manageDeptList 管理员管理的责任部门
     * @return 校验结果
     */
    private String subValidOpenForDept(List<SafetyBuildProcessOpenDO> deptOpenList,List<String> manageDeviceList,
                                       List<String> manageDeptList){
        if(deptOpenList.isEmpty()){
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        for(SafetyBuildProcessOpenDO openDO:deptOpenList){
            String curDept = openDO.getEmpowerAreaCode();
            String deviceCode = openDO.getDeviceCode();
            if(manageDeviceList.contains(deviceCode)) {
                continue;
            }
            boolean isMatched = false;
            for(String curDuty:manageDeptList) {
                if(curDept.startsWith(curDuty)){
                    isMatched = true;
                    break;
                }
            }
            if (isMatched) {
                continue;
            }
            if(!"".contentEquals(errorMsg)){
                errorMsg.append(",");
            }
            errorMsg.append(openDO.getRowNo());
        }
        return errorMsg.toString();
    }

    /**
     * 校验按人员开通的权限
     * @param openList 按人员开通的集合
     * @param manageDeviceList 管理员管理的门禁设备
     * @param manageDeptList 管理员管理的责任部门
     * @return 校验结果
     */
    private String subValidOpenForEmp(List<SafetyBuildProcessOpenDO> openList,List<String> manageDeviceList,
                                      List<String> manageDeptList){
        if(openList.isEmpty()){
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        String validEmpNos = openList.stream().map(SafetyBuildProcessOpenDO::getEmpowerAreaCode).distinct().collect(Collectors.joining(";"));
        Set<String> empNoSet = new HashSet<>(Arrays.asList(validEmpNos.split(";")));
        List<String> empNoList = new ArrayList<>(empNoSet);
        List<SafetyEmployeeMessageDO> empList = safetyEmployeeMessageService.getListByStaffNo(empNoList);
        Map<String,String> empDeptMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(empList)){
            empDeptMap = empList.stream().filter(e->StringUtils.isNotBlank(e.getOrgCodePath()))
                    .collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo,
                            SafetyEmployeeMessageDO::getOrgCodePath,
                            (oldValue,newValue)->newValue));
        }
        for(SafetyBuildProcessOpenDO openDO:openList){
            String empNo = openDO.getEmpowerAreaCode();
            String deviceCode = openDO.getDeviceCode();
            if(manageDeviceList.contains(deviceCode)) {
                continue;
            }
            //单条记录存在多个员工号时
            String[] empNos = empNo.split(";");
            boolean curRowFlag = true;
            for(String curEmp:empNos){
                String orgCodePath = empDeptMap.get(curEmp);
                boolean isMatched = false;
                if(StringUtils.isNotBlank(orgCodePath)){
                    for(String curDuty:manageDeptList){
                        if(orgCodePath.startsWith(curDuty)){
                            isMatched = true;
                            break;
                        }
                    }
                }
                curRowFlag = isMatched;
                //同一条记录中多人拼接的申请，只要有一个人不归当前管理员负责则判断false跳出单条循环
                if(!curRowFlag){
                    break;
                }
            }
            //整条记录的人或设备有一个条件满足归门禁管理员管理，判断true,不拼接异常判断
            if(curRowFlag){
                continue;
            }
            if(!"".contentEquals(errorMsg)){
                errorMsg.append(",");
            }
            errorMsg.append(openDO.getRowNo());
        }

        return errorMsg.toString();
    }

    /**
     * 照片校验
     * @param empCode 员工号
     * @param oldPhoto 旧照片
     * @param newPhoto 新照片
     */
    public VO checkPhoto(String empCode, String oldPhoto, String newPhoto) {
        if (StringUtils.isBlank(oldPhoto)) {
            // 更新人脸不合格的识别照
            updateFace(empCode, newPhoto);
            return VoUtil.successData(true);
        }
        if (StringUtils.isBlank(newPhoto)) {
            return VoUtil.fail("照片不能为空");
        }
        // 员工号以9开头就算正式员工，其他就是外协，不在区分绿都员工
        UserTypeEnum type = empCode.startsWith("9") ? UserTypeEnum.ZS : UserTypeEnum.WX;
        Boolean aBoolean = safetyArtemisHandler.checkOneToOne(newPhoto, oldPhoto, type, SafetyHikThresholdEnum.UPDATE_FACE);
        if (aBoolean) {
            // 更新人脸不合格的识别照
            updateFace(empCode, newPhoto);
        }
        return VoUtil.successData(aBoolean);
    }

    /**
     * 把照片更新到对应的人脸不合格记录中
     * @param empCode 员工号
     * @param newPhoto 新照片
     */
    private void updateFace(String empCode, String newPhoto) {
        Long id = safetyFaceUnqualifiedService.selectListByEmpNo(empCode);
        if (id == null) {
            return;
        }
        String remark = "[" + DateUtils.format(new Date(), 2) + "]-生活照更新1V1校验成功回写；";
        safetyFaceUnqualifiedService.updateById(id, newPhoto, remark);

        // 消除EIP代办
        String modelId = "YT-SAFETY-faceUnqualifiedRecordConfirm-" + empCode;
        eipCommonService.eipToDoDone(modelId, empCode);
    }
}


