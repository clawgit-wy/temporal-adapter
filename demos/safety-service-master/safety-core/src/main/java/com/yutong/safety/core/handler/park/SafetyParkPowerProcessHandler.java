package com.yutong.safety.core.handler.park;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.UpdatePhotoEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutAuthDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutProcessDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDOExample;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyClosePublicBuildParams;
import com.yutong.safety.core.dto.SafetyKeyValueDto;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import com.yutong.safety.core.dto.park.SafetyParkPowerProcessDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.common.SafetySendYxtHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.emp.SafetyDepartChangeListService;
import com.yutong.safety.core.service.enter.SafetyEmpCardService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutAuthService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutProcessService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessDetailService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import com.yutong.safety.core.service.spot.SafetySpotCheckProcessService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import okhttp3.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SafetyParkPowerProcessHandler {
    @Autowired
    SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Autowired
    SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    SafetyParkPowerProcessDetailService safetyParkPowerProcessDetailService;
    @Autowired
    SafetyBpmProcessService safetyBpmProcessService;
    @Autowired
    SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Autowired
    SafetyBuildIssuedLogService safetyBuildIssuedLogService;
    @Autowired
    SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Autowired
    SafetyGoodsOutProcessService safetyGoodsOutProcessService;
    @Autowired
    SafetySpotCheckTaskService safetySpotCheckTaskService;
    @Autowired
    SafetySysUserService safetySysUserService;
    @Resource
    SafetyEmpCardService safetyEmpCardService;
    @Resource
    SafetySpotCheckProcessService safetySpotCheckProcessService;
    @Resource
    SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Autowired
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Resource
    private SafetyDepartChangeListService safetyDepartChangeListService;

    @Resource
    private SafetyGoodsOutAuthService safetyGoodsOutAuthService;

    @Resource
    private SafetySendYxtHandler safetySendYxtHandler;

    @Value("${iam.apikey}")
    String apikey;

    @Value("${iam.FaceModify.url}")
    String faceModifyUrl;

    @Value("${iam.CardModify.url}")
    String cardModifyUrl;

    @Value("${iam.FaceIssue.url}")
    String faceIssueUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Value("${crk.IssueParkView.url}")
    private String carInFactoryInvehicleUrl;

    @Value("${crk.IssueDoorView.url}")
    private String doorUrl;
    @Value("${crk.Unregister.url}")
    private String doorUnregistertUrl;
    @Value("${crk.IssueParkView.url}")
    private String parkUrl;
    @Value("${crk.ParkUnregister.url}")
    private String parkUnregistertUrl;

    @Value("${crk.insertUrl}")
    private String crkInsertUrl;

    @Value("${crk.updateUrl}")
    private String crkUpdateUrl;

    @Value("${crk.getIssueDoorView.custom.url}")
    private String issueDoorViewCustomUrl;

    @Value("${crk.IssueDoorView.url}")
    private String issueDoorViewFuncUrl;

    @Value("${park.process.factory.code}")
    private String factoryCode;

    private static final String BUILD_MANAGE = "park_power_process";
    private static final String BUILD_MANAGE_PROCESS = "safety_park_power_process";
    private static final String CRK_ORGANTION_ID = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";
    //员工车辆RID
    private static final String CRK_EMP_CAR_RID = "C97611CE-C6EA-4582-9680-55934B79F9F7";
    //外协车辆RID
    private static final String CRK_EMP_WX_CAR_RID = "A4807A31-2DAA-462C-BC27-FAC3C2044720";
    //车行默认项目GID
    private static final String CRK_EMP_CAR_GID = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";
    private static final String FLOW_STATUS_ABANDON = "00";
    //车行默认组织ID
    private static final String CAR_DEFAULT_ORGANTION = "0D864B2A-A7BD-4511-94A2-A5AB6E453588";
    //卡注销入参
    private static final String cardParam ="{\"SubSystems\":[2],\"DepositMoney\":0,\"VmsOperMoney\":0,\"CmsOperMoney\":0,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":1,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10卡注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"&Gid\",\"ProjectGids\":[\"&Gid\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";
    //车注销入参
    private static final String carParam ="{\"OperMoney\":150,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":0,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10车注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\",\"ProjectGids\":[\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";

    public void saveAndCreateProcess(SafetyParkPowerProcessDTO entity, String token) {
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        entity.setId(uuid);
        BeanUtils.copyProperties(entity, param);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        //01开头的员工号转为60开头的新员工号
        if(workNo.startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(workNo);
            workNo = newPatrolEmpNo;
        }
        SafetyEmployeeMessageDO emp = safetyEmployeeMessageService.getByNo(workNo);
        if(emp == null){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"未查询到当前登录人的信息!");
        }
        param.setCreateEmployeeLogin(emp.getEmpLogin());
        param.setCreateEmployeeName(emp.getEmpName());
        param.setCreateEmployeeDept(emp.getOrgName());
        param.setCreateEmployeeDeptCode(emp.getOrgCode());
        param.setCreateEmployeeNo(emp.getEmpNo());
        param.setProcessSubject(DateUtils.format(new Date(),1)+"-"+emp.getEmpName()+"-申请的大门权限申请流程");

        dealPowerManager(param);

        String format = DateUtils.format(new Date(), 5);
        String processCode = "DMQXSQ" + format.substring(0,format.length()-2);
        param.setProcessCode(processCode);

        //流程状态
        param.setStatus("10");

        param.setCreateTime(DateUtils.format(new Date(), 2));
        Integer row = safetyParkPowerProcessService.save(param);

        //人员信息
        String roleType = entity.getRoleType();
        //管理员和保密负责人
        if("3".equals(roleType) || "2".equals(roleType)){
            List<SafetyParkPowerProcessDetailDO> detailList = dealDataByRole(entity);
            safetyParkPowerProcessDetailService.batchSave(detailList);
        }else{//员工
            SafetyParkPowerProcessDetailDO detailDO = new SafetyParkPowerProcessDetailDO();
            detailDO.setProcessId(uuid);
            detailDO.setPowerType(param.getPowerType());
            detailDO.setEmployeeCode(param.getCreateEmployeeNo());
            detailDO.setEmployeeName(param.getCreateEmployeeName());
            detailDO.setCreateTime(DateUtils.format(new Date(),2));
            detailDO.setApplyTime(DateUtils.format(new Date(),2));

            safetyParkPowerProcessDetailService.save(detailDO);
        }

        if (row > 0) {
            //手动提交事务
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(param.getProcessSubject(),
                    BUILD_MANAGE, BUILD_MANAGE_PROCESS, param.getCreateEmployeeLogin(), uuid);
            if (StringUtils.isNotBlank(returnFlowId)) {
                param.setBpmFlowId(returnFlowId.split(",")[1]);
            }
            param.setStatus("20");
            safetyParkPowerProcessService.update(param);
        }
    }

    /**
     * 判断是否走园区权限负责人节点
     * @param param 入参
     */
    public void dealPowerManager(SafetyParkPowerProcessDO param){
        String montageFactoryCode = param.getMontageFactoryCode();
        if(StringUtils.isBlank(montageFactoryCode)){return;}

        //因为正式和测试的厂区编码不太一致，所以放在配置文件中，特殊区域：研发中心、工程试验中心、九龙仓库（KD）、九龙仓库（售后）、九龙成品库、智驱科技、深澜动力、精益达座椅新工厂
        List<String> list = Arrays.asList(factoryCode.split(","));

        String[] split = montageFactoryCode.split(",");
        List<String> paramList = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        for (String s : split) {
            if(list.contains(s)){
                paramList.add(s);
            }
        }

        if(CollectionUtils.isEmpty(paramList)){return;}

        paramMap.put("list", paramList);
        paramMap.put("roleId", 30);
        List<SafetySysUserDO> userListByParam = safetySysUserService.getUserListByParam(paramMap);
        if(CollectionUtils.isEmpty(userListByParam)){return;}

        StringBuilder sb = new StringBuilder();
        StringBuilder nSb = new StringBuilder();
        for (SafetySysUserDO safetySysUserDO : userListByParam) {
            sb.append(safetySysUserDO.getUserName()).append(",");
            nSb.append(safetySysUserDO.getEmpName()).append(",");
        }
        String substring = sb.substring(0, sb.length() - 1);
        param.setFactoryPowerManager(substring);

        String nSub = nSb.substring(0, nSb.length() - 1);
        param.setFactoryPowerManagerName(nSub);
    }

    public void saveAndCreateProcessApp(SafetyParkPowerProcessDTO entity) {
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        entity.setId(uuid);
        BeanUtils.copyProperties(entity, param);

        //01开头的员工号转为60开头的新员工号
        if(entity.getCreateEmployeeNo().startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(entity.getCreateEmployeeNo());
            entity.setCreateEmployeeNo(newPatrolEmpNo);
        }
        SafetyEmployeeMessageDO emp = safetyEmployeeMessageService.getByNo(entity.getCreateEmployeeNo());
        if(emp == null){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"未查询到当前登录人的信息!");
        }
        param.setCreateEmployeeLogin(emp.getEmpLogin());
        param.setCreateEmployeeName(emp.getEmpName());
        param.setCreateEmployeeDept(emp.getOrgName());
        param.setCreateEmployeeDeptCode(emp.getOrgCode());
        param.setCreateEmployeeNo(emp.getEmpNo());
        param.setProcessSubject(DateUtils.format(new Date(),1)+"-"+emp.getEmpName()+"-申请的大门权限申请流程");

        dealPowerManager(param);

        String format = DateUtils.format(new Date(), 5);
        String processCode = "DMQXSQ" + format.substring(0,format.length()-2);
        param.setProcessCode(processCode);

        //流程状态
        param.setStatus("10");

        param.setCreateTime(DateUtils.format(new Date(), 2));
        Integer row = safetyParkPowerProcessService.save(param);

        //人员信息
        SafetyParkPowerProcessDetailDO detailDO = new SafetyParkPowerProcessDetailDO();
        detailDO.setProcessId(uuid);
        detailDO.setPowerType(param.getPowerType());
        detailDO.setEmployeeCode(param.getCreateEmployeeNo());
        detailDO.setEmployeeName(param.getCreateEmployeeName());
        detailDO.setCreateTime(DateUtils.format(new Date(),2));
        detailDO.setApplyTime(DateUtils.format(new Date(),2));

        safetyParkPowerProcessDetailService.save(detailDO);

        if (row > 0) {
            //手动提交事务
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(param.getProcessSubject(),
                    BUILD_MANAGE, BUILD_MANAGE_PROCESS, param.getCreateEmployeeLogin(), uuid);
            if (StringUtils.isNotBlank(returnFlowId)) {
                param.setBpmFlowId(returnFlowId.split(",")[1]);
            }
            param.setStatus("20");
            safetyParkPowerProcessService.update(param);
        }
    }

    public Object approvalProcess(SafetyParkPowerProcessDTO entity, String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String userName = tockenUser.get("userName");
        if(com.yutong.gw.utils.StringUtils.isEmpty(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        if("通过".equals(entity.getAuditNote())){
            entity.setResultStatus("1"); //通过
        }else{
            entity.setStatus("00"); //废弃
            entity.setResultRemark(entity.getAuditNote());
            entity.setResultStatus("2"); //驳回
        }
        //获取审批意见
        String retunAuditNote = initAuditNote(entity);

        SafetyParkPowerProcessDO process = new SafetyParkPowerProcessDO();
        BeanUtils.copyProperties(entity, process);
        process.setUpdateTime(DateUtils.format(new Date(), 2));
        safetyParkPowerProcessService.update(process);
        BpmProcessParam processParam;
        if("00".equals(entity.getStatus())){
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

    /**
     * 处理界面提交的审批意见
     * @param entity
     * @return
     */
    private String initAuditNote(SafetyParkPowerProcessDTO entity){
        String returnAduit = entity.getAuditNote();

        if(StringUtils.isNotBlank(entity.getFdParameterJson())){
            try{
                JSONObject paramJson = JSONObject.parseObject(entity.getFdParameterJson());
                if(paramJson == null){
                    return returnAduit;
                }
                JSONObject auditParam = paramJson.getJSONObject("param");
                if(auditParam == null){
                    return returnAduit;
                }
                String auditNote = auditParam.getString("auditNote");
                if(StringUtils.isNotBlank(auditNote)){
                    returnAduit = auditNote;
                }
            }catch (Exception e){
                LogUtils.error("异常处理",e);
            }
        }
        return returnAduit;
    }

    public SafetyParkPowerProcessDTO getDetailById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        SafetyParkPowerProcessDTO en = new SafetyParkPowerProcessDTO();
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();
        param.setId(id);
        List<SafetyParkPowerProcessDO> list = safetyParkPowerProcessService.getDetailById(param);
        if(CollectionUtils.isEmpty(list)){return en;}

        BeanUtils.copyProperties(list.get(0), en);

        List<SafetyParkPowerProcessDetailDO> detailList = safetyParkPowerProcessDetailService.select(en.getId());
        en.setUserList(detailList);

        return en;
    }

    public List<SafetyEmployeePrivateCarDO> getListByUserCode(List<String> userCodeList) {
        if (CollectionUtils.isEmpty(userCodeList)) {
            return null;
        }
        // 从员工台账表中获取数据，不从私家车中直接回去数据
        List<SafetyEmployeePrivateCarDO> carDOList = Lists.newArrayList();
        // 员工台账信息
        List<SafetyEmployeeMessageDO> list = safetySpotCheckProcessService.getEmpInfoByUserCodeList(userCodeList);
        if (CollectionUtils.isEmpty(list)) return carDOList;
        // 私家车数据信息
        List<SafetyEmployeePrivateCarDO> listByUserCode = safetyParkPowerProcessService.getListByUserCode(userCodeList);
        // 数据合并
        list.forEach(li -> {
            SafetyEmployeePrivateCarDO data = new SafetyEmployeePrivateCarDO();
            data.setEmployeeNo(li.getEmpNo());
            data.setEmployeeName(li.getEmpName());
            listByUserCode.forEach(ln -> {
                if (ln.getEmployeeNo().equals(li.getEmpNo())) {
                    data.setCarNo(ln.getCarNo());
                }
            });
            carDOList.add(data);
        });

        return carDOList;
    }

    public Object getPageList(SafetyParkPowerProcessDTO param,String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }

        String retVal = safetyBaseHandler.checkUserType(token);
        String role = retVal.split(":")[0];
        param.setRoleType(role);

        if("1".equals(role) || "3".equals(role)){
            param.setCurPerson(workNo);
            param.setCurNodeHandler(tockenUser.get("userName"));
        }

        List<SafetyParkPowerProcessDTO> list = safetyParkPowerProcessService.selectList(param);
        long total = safetyParkPowerProcessService.selectListCount(param);

        return new PageVO<>(total, list);
    }

    public void bpmCallBackProcess(String processId, String processData) {
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(processData)) {
            return;
        }
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyParkPowerProcessDO> processList = safetyParkPowerProcessService.getDetailById(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("大门权限申请流程异常-未找到对应的流程信息");
                return;
            }
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            SafetyParkPowerProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(nodeFactId);
            switch (nodeFactId) {
                case "N7":
                    processDTO.setCurNodeName("科长审批");
                    processDTO.setCurNodeHandler(processDTO.getChiefEmployeeLogin());
                    processDTO.setCurHandlerName(processDTO.getChiefEmployeeName());
                    break;
                case "N10":
                    processDTO.setCurNodeName("权限下发节点");
                    processDTO.setCurNodeHandler("机器人节点");
                    processDTO.setCurHandlerName("机器人节点");
                    //权限下发
                    sendToCrk(processDTO);
                    break;
                case "N12":
                    processDTO.setCurNodeName("园区权限负责人");
                    processDTO.setCurNodeHandler(processDTO.getFactoryPowerManager());
                    processDTO.setCurHandlerName(processDTO.getFactoryPowerManagerName());
                    break;
                case "N3":
                    processDTO.setCurNodeName("结束节点");
                    processDTO.setCurNodeHandler("");
                    processDTO.setCurHandlerName("");
                    processDTO.setStatus("30");
                    break;
                default:
                    break;
            }
            if(!"00".equals(processDTO.getStatus())){
                safetyParkPowerProcessService.update(processDTO);
            }

        } catch (Exception e) {
            LogUtils.error("大门车行权限流程回调-处理逻辑异常", e);
        }
    }

    /**
     * 发布事件
     * @param processId
     * @param processData
     */
    @Async
    public void publishProcess(String processId, String processData){
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(processData)) {
            return;
        }
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyParkPowerProcessDO> processList = safetyParkPowerProcessService.getDetailById(param);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("大门权限申请流程异常-未找到对应的流程信息");
                return;
            }
            SafetyParkPowerProcessDO processDTO = processList.get(0);
            if(FLOW_STATUS_ABANDON.equals(processDTO.getStatus())) {
                LogUtils.info("流程{}已废弃，不更新权限",processId);
                return;
            }
            //流程标记发布状态
            processDTO.setCurNodeName("结束节点");
            processDTO.setCurNodeHandler("");
            processDTO.setCurHandlerName("");
            processDTO.setStatus("30");
            safetyParkPowerProcessService.update(processDTO);
            sendToCrk(processDTO);
        }catch (Exception e){
            LogUtils.error("权限下发异常",e);
        }
    }

    /**
     * 流程废弃事件
     * @param processId 流程ID
     * @param processData 流程参数
     */
    public void discardProcess(String processId, String processData){
        LogUtils.info("回调废弃方法 参数processId=%s;processData=%s", processId, processData);
        if (StringUtils.isBlank(processId) || StringUtils.isBlank(processData)) {
            LogUtils.info("大门权限申请流程异常-流程ID或实体类ID为空 参数processId=%s;processData=%s", processId, processData);
            return;
        }
        SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();
        param.setBpmFlowId(processId);
        try {
            List<SafetyParkPowerProcessDO> processList = safetyParkPowerProcessService.getDetailById(param);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(processList)) {
                LogUtils.info("大门权限申请流程异常-未找到对应的流程信息");
                return;
            }
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            SafetyParkPowerProcessDO processDTO = processList.get(0);
            processDTO.setCurNodeId(nodeFactId);
            processDTO.setStatus("00"); //废弃状态
            processDTO.setResultStatus("2"); //不通过
            processDTO.setResultRemark("流程废弃");
            processDTO.setUpdateTime(DateUtils.format(new Date(),2));
            safetyParkPowerProcessService.update(processDTO);
        } catch (Exception e) {
            LogUtils.error("大门车行权限流程废弃回调-处理逻辑异常", e);
        }
    }

    public List<SafetyParkPowerProcessDetailDO> dealDataByRole(SafetyParkPowerProcessDTO entity){
        String roleType = entity.getRoleType();

        List<SafetyParkPowerProcessDetailDO> userList = entity.getUserList();
        if(!CollectionUtils.isEmpty(userList)){
            for (SafetyParkPowerProcessDetailDO openUser : userList){
                openUser.setProcessId(entity.getId());
                openUser.setPowerType(roleType);
                openUser.setCreateTime(DateUtils.format(new Date(),2));
                openUser.setApplyTime(DateUtils.format(new Date(),2));
            }
        }
        // 员工校验，排除WX员工
        // 1.获取员工号
        List<String> empCodeList = userList.stream().map(SafetyParkPowerProcessDetailDO::getEmployeeCode).collect(Collectors.toList());
        // 2.获取员工详情信息
        List<SafetyEmployeeMessageDO> list = safetySpotCheckProcessService.getEmpInfoByUserCodeList(empCodeList);
        if (CollectionUtils.isEmpty(list)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "员工信息不存在");
        }
        // 3.取出外协的员工 为空表示没有外协员工，不为空表示存在外协员工
        List<String> wxEmpNos = list.stream().filter(li -> "WX".equals(li.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(wxEmpNos)) {
            // 表示没有外协员工
            return userList;
        }
        throw new BizException(ResponseEnum.FAIL.getCode(), "请删除外协外包人员，调整外协外包人员厂区大门权限请联系内部对接人提交外协外包进出权限调整流程");
    }

    public void testSendToCrk(String bpmFlowId){
        try {
            SafetyParkPowerProcessDO param = new SafetyParkPowerProcessDO();
            param.setBpmFlowId(bpmFlowId);
            List<SafetyParkPowerProcessDO> processList = safetyParkPowerProcessService.getDetailById(param);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(processList)) {
                LogUtils.info("大门权限申请流程异常-未找到对应的流程信息");
                return;
            }
            SafetyParkPowerProcessDO processDTO = processList.get(0);
            sendToCrk(processDTO);
        }catch (Exception e){
            LogUtils.error("下发权限异常",e);
        }
    }


    /**
     * 下发权限 -异步
     * @param param 流程流转参数
     */
    @Async
    public void sendToCrk(SafetyParkPowerProcessDO param) {
        LogUtils.info("进入sendToCrk,{}",System.currentTimeMillis());
        try {
            //根据流程ID查询人员信息
            List<ParamDTO> userList = safetyParkPowerProcessService.getUserList(param.getId());
            if (CollectionUtils.isEmpty(userList)) {
                return;
            }
            //操作类型 1开通2关闭
            String powerType = param.getPowerType();
            //此批次操作的工厂code
            String newFactory = param.getMontageFactoryCode();
            //获取流程中的截止时间
            String applyEndTime = DateUtils.format(DateUtils.parse(param.getApplyEndTime(), 1), 1) + " 23:59:59";
            downPower(userList, powerType, newFactory, applyEndTime, "ZS", param.getId());
        }catch (Exception e){
            LogUtils.error("下发权限异常",e);
        }
        LogUtils.info("结束sendToCrk,{}",System.currentTimeMillis());
    }

    /**
     * 定时任务更新权限
     */
    public void updatePower(){
        //查询今天到期的数据
        String endTimeBegin = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        String endTimeEnd = DateUtils.format(new Date(),1);
        SafetyParkEmployeePowerInfoDO param = new SafetyParkEmployeePowerInfoDO();
        param.setPowerBeginTime(endTimeBegin);
        param.setPowerEndTime(endTimeEnd);
        List<SafetyParkEmployeePowerInfoDO> list = safetyParkEmployeePowerInfoService.getList(param);

        if(CollectionUtils.isEmpty(list)){return;}

        //每个人要关闭的厂区的map
        Map<String, String> userFactoryMap = list.stream().collect(Collectors.toMap(
                SafetyParkEmployeePowerInfoDO::getEmployeeCode,
                SafetyParkEmployeePowerInfoDO::getFactoryId,
                (oldV, newV) -> oldV
        ));
        Set<String> empNoSet = list.stream().map(SafetyParkEmployeePowerInfoDO::getEmployeeCode).collect(Collectors.toSet());

        //查询上面map中的人对应的所有的厂区和最大的权限有效时间
        List<ParamDTO> userList = safetyParkPowerProcessService.getTimeOutData(new ArrayList<>(empNoSet));

        // 根据员工工号可能会获取不到数据，不做处理在下一步查询会报异常
        if (CollectionUtils.isEmpty(userList)) return;

        // 获取员工的门禁权限和门禁对应的设备ID
        getDeviceId(userList);

        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(new ArrayList<>(empNoSet));
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));
        // 初始化组织ID和curId
        safetyBaseHandler.generalOrgIdForBatch(userList);

        //遍历集合，下发权限
        for(ParamDTO paramDTO : userList){
            try {
                //此次要关闭的厂区
                String factoryClose = userFactoryMap.get(paramDTO.getEmpNo());
                //当前人现有的所有厂区
                String oldFactory = paramDTO.getFactoryId();
                //获取当前人所有厂区中的最大权限有效时间
                String powerEndTime = paramDTO.getPowerEndTime();

                if(StringUtils.isBlank(factoryClose)){continue;}
                paramDTO.setFactoryId(factoryClose);
                paramDTO.setBeginDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
                paramDTO.setEndDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59");

                List<SafetyEmployeePrivateCarDO> carList = carinfoMap.get(paramDTO.getEmpNo());
                //关闭
                boolean closeBol = powerUpdate(paramDTO, carList);

                //取前后厂区的差集
                HashSet<String> newStr = new HashSet<>(Arrays.asList(factoryClose.split(",")));
                HashSet<String> oldStr = new HashSet<>(Arrays.asList(oldFactory.split(",")));
                oldStr.removeAll(newStr);

                if(CollectionUtils.isEmpty(oldStr)){continue;}
                paramDTO.setFactoryId(String.join(",", oldStr));
                paramDTO.setEndDate(powerEndTime);
                //开启剩余厂区权限
                powerUpdate(paramDTO, carList);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"入参dto={}\"}",paramDTO);
                LogUtils.error("定时任务sendToCrk下发出错----",e);
            }
        }
        //删除掉已到期的厂区权限 TODO 关闭厂区时关闭对应楼宇权限
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        example.createCriteria().andIsDeleteEqualTo(Short.valueOf("0")).andPowerEndTimeGreaterThanOrEqualTo(endTimeBegin).andPowerEndTimeLessThan(endTimeEnd);
        safetyParkEmployeePowerInfoService.deleteByExample(example,"厂区权限到期自动关闭");
    }

    private Set<String> getEmployeeCodeSet(Stream<BuildEmployeePowerVO> stream) {
        return stream
                .map(BuildEmployeePowerVO::getEmployeeCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    /**
     * 把厂区权限及楼宇权限整合成paramDTO对象
     * @param empNo
     * @param employeeMessageDO
     * @param parkList
     * @param buildList
     */
    private ParamDTO initParamDTO(String empNo,SafetyEmployeeMessageDO employeeMessageDO,
                                  List<BuildEmployeePowerVO> parkList,List<BuildEmployeePowerVO> buildList){
        AtomicReference<String> beginDate = new AtomicReference<>("");
        AtomicReference<String> endDate = new AtomicReference<>("");
        ParamDTO newParam = new ParamDTO();
        newParam.setEmpNo(empNo);
        if(employeeMessageDO != null){
            newParam.setUserType(employeeMessageDO.getUserType());
            newParam.setEmpName(employeeMessageDO.getEmpName());
        }else{
            newParam.setUserType("WX");
            newParam.setEmpName("已离职人员");
        }
        Set<String> buildSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(parkList)){
            Optional<BuildEmployeePowerVO> maxBeginVO = parkList.stream().max(Comparator.comparing(BuildEmployeePowerVO::getEndTime));
            maxBeginVO.ifPresent(build -> {
                beginDate.set(build.getBeginTime());
                endDate.set(build.getEndTime());
            });
            //设置厂区
            newParam.setFactoryId(parkList.stream().map(BuildEmployeePowerVO::getAnFactoryCode).distinct().collect(Collectors.joining(",")));
            //只处理富士设备
            buildSet.addAll(parkList.stream().filter(e->!"1".equals(e.getDeviceType())).map(BuildEmployeePowerVO::getDeviceCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
        }
        if(!CollectionUtils.isEmpty(buildList)){
            buildSet.addAll(buildList.stream().map(BuildEmployeePowerVO::getDeviceCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
            if(StringUtils.isBlank(beginDate.get())){
                Optional<BuildEmployeePowerVO> minBeginVO = buildList.stream().min(Comparator.comparing(BuildEmployeePowerVO::getEndTime));
                minBeginVO.ifPresent(build -> {
                    beginDate.set(build.getBeginTime());
                    endDate.set(build.getEndTime());
                });
            }
        }
        //设置设备
        newParam.setDeviceCode(String.join(",", buildSet));
        //设置下发的起止时间
        newParam.setBeginDate(beginDate.get());
        newParam.setEndDate(endDate.get());

        return newParam;
    }

    /**
     * 整理富士权限下发参数
     * @param empNoSet
     * @param empMap
     * @param closeParkMap
     * @param closeFsBuildMap
     * @return
     */
    public List<ParamDTO> initParamDTOList(Set<String> empNoSet,Map<String,SafetyEmployeeMessageDO> empMap,
                                           Map<String,List<BuildEmployeePowerVO>> closeParkMap,
                                           Map<String,List<BuildEmployeePowerVO>> closeFsBuildMap){
        List<ParamDTO> paramDTOS = new ArrayList<>();
        //逐个关闭权限
        empNoSet.forEach(curEmpNo->{
            if(StringUtils.isBlank(curEmpNo)){
                return;
            }
            List<BuildEmployeePowerVO> closePark = closeParkMap.get(curEmpNo);
            List<BuildEmployeePowerVO> closeFsBuild = closeFsBuildMap.get(curEmpNo);
            if(CollectionUtils.isEmpty(closePark) && CollectionUtils.isEmpty(closeFsBuild)){
                return;
            }
            SafetyEmployeeMessageDO curEmpDO = empMap.get(curEmpNo);
            ParamDTO paramDTO = initParamDTO(curEmpNo,curEmpDO,closePark,closeFsBuild);

            paramDTOS.add(paramDTO);
        });
        return paramDTOS;
    }

    /**
     * format BuildEmployeePowerVO集合
     * @param xkpClosePowerList
     * @return
     */
    private List<SafetyBuildEmployeePowerDO> createSafetyBuildEmployeePowerDOList(List<BuildEmployeePowerVO> xkpClosePowerList) {
        return xkpClosePowerList.stream()
                .map(vo -> {
                    SafetyBuildEmployeePowerDO newDO = new SafetyBuildEmployeePowerDO();
                    BeanUtils.copyProperties(vo, newDO);
                    return newDO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 定时关闭员工到期厂区的人车权限及楼宇权限
     */
    public void updatePowerNew(){
        String endTimeEnd = DateUtils.format(new Date(),1);
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setPowerEndTimeEnd(endTimeEnd);
        //查询到期的大门权限及厂区下的楼宇权限
        List<BuildEmployeePowerVO> parkList = safetyBuildEmployeePowerService.selectEmpPowerList(listParam);

        //查询到期楼宇权限的数据
        listParam.setDeviceType("2"); //只检索富士设备
        List<BuildEmployeePowerVO> buildList = safetyBuildEmployeePowerService.selectList(listParam);

        if(CollectionUtils.isEmpty(parkList) && CollectionUtils.isEmpty(buildList)){
            LogUtils.info("任务updatePowerNew没有到期的权限要关闭");
            return;
        }
        //1.获取人员集合
        Set<String> empNoSet = getEmployeeCodeSet(Stream.concat(parkList.stream(), buildList.stream()));
        if(CollectionUtils.isEmpty(empNoSet)){
            return;
        }
        List<SafetyEmployeeMessageDO> empList = safetyEmployeeMessageService.getListByStaffNo(new ArrayList<>(empNoSet));
        Map<String,SafetyEmployeeMessageDO> empMap = empList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));

        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(new ArrayList<>(empNoSet));
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        //2.整理待关闭的权限
        Map<String,List<BuildEmployeePowerVO>> closeParkMap = parkList.stream().collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
        //2-1新开普楼宇
        Map<String,List<BuildEmployeePowerVO>> closeXkpBuildMap = buildList.stream().filter(e->"1".equals(e.getDeviceType())).collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
        //2-2富士楼宇
        Map<String,List<BuildEmployeePowerVO>> closeFsBuildMap = buildList.stream().filter(e->"2".equals(e.getDeviceType())).collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
        List<ParamDTO> paramDTOS = initParamDTOList(empNoSet,empMap,closeParkMap,closeFsBuildMap);
        // 公共的门禁
        getCommonEmpNoBuildInfo(paramDTOS, closeXkpBuildMap);
        //关闭到期的厂区及到期厂区下楼宇以及到期楼宇权限
        subBatchEmpPower(paramDTOS,carinfoMap);//关闭
        // 新开普门禁关闭
        xkpDataOperation(closeXkpBuildMap);

        //关闭的权限标记删除（包含厂区和楼宇）
        subDeleteEmpPower(parkList,buildList);

        //开通相关员工现有权限
        BuildEmployeePowerListParam openPram = new BuildEmployeePowerListParam();
        openPram.setEmpCodeList(new ArrayList<>(empNoSet));
        //查询到期的大门权限及厂区下的楼宇权限
        List<BuildEmployeePowerVO> openParkList = safetyBuildEmployeePowerService.selectEmpPowerList(openPram);
        if(CollectionUtils.isEmpty(openParkList)){
            LogUtils.info("没有需要重新开通的权限!");
            return;
        }
        Map<String,List<BuildEmployeePowerVO>> openParkMap = openParkList.stream().collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
        List<ParamDTO> opemDTOS = initParamDTOList(empNoSet,empMap,openParkMap,new HashMap<>());
        subBatchEmpPower(opemDTOS,carinfoMap); //开通
    }

    /**
     * 新开普设备关闭
     *  @param XkpBuildMap 新开铺的数据信息
     */
    private void xkpDataOperation(Map<String, List<BuildEmployeePowerVO>> XkpBuildMap) {
        if (XkpBuildMap.isEmpty()) {
            return;
        }
        String issue = "关闭";
        XkpBuildMap.keySet().forEach(key -> {
            List<BuildEmployeePowerVO> buildEmployeePowerVOS = XkpBuildMap.get(key);
            //关闭新开普楼宇
            if(!CollectionUtils.isEmpty(buildEmployeePowerVOS)){
                String factoryIdStr = buildEmployeePowerVOS.stream().filter(el->StringUtils.isNotBlank(el.getAnFactoryCode()))
                        .map(BuildEmployeePowerVO::getAnFactoryCode).distinct().collect(Collectors.joining(","));
                List<SafetyBuildEmployeePowerDO> closeList = createSafetyBuildEmployeePowerDOList(buildEmployeePowerVOS);
                updateXkpEmpPower(key,closeList,factoryIdStr,issue);
            }
        });
    }

    /**
     * 公共楼宇门禁处理
     *
     * @param paramDTOS        人员信息
     * @param closeXkpBuildMap 新开普的数据
     */
    private void getCommonEmpNoBuildInfo(List<ParamDTO> paramDTOS, Map<String,List<BuildEmployeePowerVO>> closeXkpBuildMap) {
        // closePublicBuildIng 方法的入参
        List<SafetyClosePublicBuildParams> list = new ArrayList<>();
        // 为空直接返回
        Set<String> allEmpNos = new HashSet<>();
        if (!CollectionUtils.isEmpty(paramDTOS)) {
            // 处理paramDTOS中的数据
            paramDTOS.forEach(li -> {
                String empNo = li.getEmpNo();
                allEmpNos.add(empNo);
                List<String> deviceCodeList = streamDeviceCodes(li, closeXkpBuildMap.get(empNo));
                list.add(safetyBaseHandler.createClosePublicBuildParams(empNo, deviceCodeList));
            });
        }
        // 处理closeXkpBuildMap中未在paramDTOS中的数据
        if (!closeXkpBuildMap.isEmpty()) {
            Set<String> closeEmpNos = closeXkpBuildMap.keySet().stream().filter(li -> !allEmpNos.contains(li)).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(closeEmpNos)) {
                closeEmpNos.forEach(li -> {
                    List<String> deviceCodeList = streamDeviceCodes(new ParamDTO(), closeXkpBuildMap.get(li));
                    list.add(safetyBaseHandler.createClosePublicBuildParams(li, deviceCodeList));
                });
            }
        }
        // 调用关闭公共楼宇接口
        LogUtils.info("公共楼宇待关闭楼宇门禁数据-入参", list.toString());
        List<SafetyKeyValueDto> keyValueDtos = safetyBaseHandler.closePublicBuildIng(list, false, "定时关闭员工到期厂区的人车权限及楼宇权限");
        if (CollectionUtils.isEmpty(keyValueDtos)) {
            return;
        }
        LogUtils.info("公共楼宇待关闭楼宇门禁数据-出参", keyValueDtos.toString());
        Map<String, List<SafetyKeyValueDto>> dataMap = keyValueDtos.stream().collect(Collectors.groupingBy(SafetyKeyValueDto::getKey));
        // 更新ParamDTO中的deviceCode
        for (ParamDTO li : paramDTOS) {
            String empNo = li.getEmpNo();
            String deviceCode = li.getDeviceCode();
            List<SafetyKeyValueDto> keyValueDtosList = dataMap.get(empNo);
            if (CollectionUtils.isEmpty(keyValueDtosList)) {
                continue;
            }
            String pubDeviceCode = keyValueDtosList.stream().map(SafetyKeyValueDto::getValue).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(deviceCode)) {
                li.setDeviceCode(deviceCode + "," + pubDeviceCode);
            } else {
                li.setDeviceCode(pubDeviceCode);
            }
        }
    }
    /**
     * 流处理设备编码，组合param中的设备码和closeXkpBuildMap中的设备码
     */
    private List<String> streamDeviceCodes(ParamDTO param, List<BuildEmployeePowerVO> buildEmployeePowerVOS) {
        List<String> deviceCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(buildEmployeePowerVOS)) {
            deviceCodeList.addAll(buildEmployeePowerVOS.stream()
                    .map(SafetyBuildEmployeePowerDO::getDeviceCode)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList()));
        }
        if (StringUtils.isNotBlank(param.getDeviceCode())) {
            deviceCodeList.addAll(Arrays.asList(param.getDeviceCode().split(",")));
        }
        return deviceCodeList;
    }

    /**
     * 子方法-关闭厂区及楼宇权限
     * @param paramDTOS
     * @param carinfoMap
     */
    public void subBatchEmpPower(List<ParamDTO> paramDTOS,Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap){
        // 初始化组织ID和curId
        safetyBaseHandler.generalOrgIdForBatch(paramDTOS);
        //获取厂区Map
        Map<Integer,String> factoryMap = safetyFieldManageService.getFactoryNameList();

        paramDTOS.forEach(e->{
            String doorName = safetyBaseHandler.getParkNameBYCode(e.getFactoryId(),factoryMap);
            List<SafetyEmployeePrivateCarDO> carList = carinfoMap.get(e.getEmpNo());
            e.setFactoryName(doorName);
            powerUpdateNew(e, carList);
        });
    }

    /**
     * 子方法-删除员工到期权限
     * @param parkList 到期的厂区厂区及厂区下楼宇
     * @param buildList 到期的楼宇
     */
    public void subDeleteEmpPower(List<BuildEmployeePowerVO> parkList,List<BuildEmployeePowerVO> buildList){
        List<Integer> parkIds = parkList.stream().map(BuildEmployeePowerVO::getParkId).collect(Collectors.toList());
        List<String> buildIds = parkList.stream().filter(e->StringUtils.isNotBlank(e.getId()))
                .map(BuildEmployeePowerVO::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(parkIds)){
            try{
                List<String> empNos = parkList.stream().map(BuildEmployeePowerVO::getEmployeeCode).distinct().collect(Collectors.toList());
                //查询出注销人员当前的门禁权限，保存到history表中
                ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
                powerList.setEmpNoList(empNos);
                List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoService.getDetailByCon(powerList);
                safetyParkEmpPowerReportService.batchUpdateHistoryPower(curList,"权限变更");
            }catch (Exception e){
                LogUtils.error("批量保存最后大门权限异常",e);
            }
            SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
            //删除掉已到期的厂区权限
            List<List<Integer>> partition = ListUtils.partition(parkIds, 1000);
            partition.forEach(li -> {
                example.createCriteria().andIsDeleteEqualTo(Short.valueOf("0")).andIdIn(li);
                safetyParkEmployeePowerInfoService.deleteByExample(example,"厂区权限到期自动关闭");
            });
        }
        //删除掉已到期的楼宇权限
        BuildEmployeePowerListParam listParam1 = new BuildEmployeePowerListParam();
        SafetyBuildEmployeePowerDO powerDO = new SafetyBuildEmployeePowerDO();
        if(!CollectionUtils.isEmpty(buildIds)){
            powerDO.setRemark("厂区权限到期关闭对应楼宇");
            List<List<String>> partition = ListUtils.partition(buildIds, 1000);
            partition.forEach(li -> {
                listParam1.setIdList(li);
                safetyBuildEmployeePowerService.deleteByExample(listParam1,powerDO);
            });

        }
        List<String> buildIds2 = buildList.stream().map(BuildEmployeePowerVO::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(buildIds2)){
            //删除掉已到期的楼宇权限
            powerDO.setRemark("楼宇权限到期自动关闭");
            List<List<String>> partition = ListUtils.partition(buildIds2, 1000);
            partition.forEach(li -> {
                listParam1.setIdList(li);
                safetyBuildEmployeePowerService.deleteByExample(listParam1,powerDO);
            });
        }
    }

    /**
     * 关闭新开普设备，并保存日志
     * @param paramDTO
     * @param closeBuildPowerIdList
     * @param logList
     * @param curEmpNo
     * @param xkpEmpCloseMap
     * @param newFactory
     * @param log
     * @param reason
     * @param createBy
     * @param userType
     * @param processId
     * @param powerType
     */
    private void subDownPowerForCloseXkp(ParamDTO paramDTO,
                                         List<String> closeBuildPowerIdList,
                                         List<SafetyBuildIssuedLogDO> logList,
                                         String curEmpNo,
                                         Map<String,List<SafetyBuildEmployeePowerDO>> xkpEmpCloseMap,
                                         String newFactory,SafetyBuildIssuedLogDO log,
                                         String reason,String createBy,String userType,
                                         String processId,String powerType
                                         ){
        String xkpCloseDeviceCode = "";
        String xkpCloseDeviceName = "";
        List<SafetyBuildEmployeePowerDO> xkpClosePowerList = xkpEmpCloseMap.get(paramDTO.getEmpNo());
        //记录日志时，把关闭的新开普楼宇也加上
        if(!CollectionUtils.isEmpty(xkpClosePowerList)){
            closeBuildPowerIdList.addAll(xkpClosePowerList.stream().map(SafetyBuildEmployeePowerDO::getId).collect(Collectors.toList()));
            //关闭新开普权限
            boolean xkpStatus = updateXkpEmpPower(curEmpNo,xkpClosePowerList,newFactory,"关闭");

            xkpCloseDeviceCode = xkpClosePowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode)
                    .distinct().collect(Collectors.joining(","));
            xkpCloseDeviceName = xkpClosePowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName)
                    .distinct().collect(Collectors.joining(","));
            paramDTO.setDeviceCode(xkpCloseDeviceCode);
            paramDTO.setDeviceName(xkpCloseDeviceName);
            //添加新开普关闭日志
            SafetyBuildIssuedLogDO xkpLog = new SafetyBuildIssuedLogDO();
            BeanUtils.copyProperties(log,xkpLog);
            initIssueLog(xkpLog,paramDTO,reason,createBy,userType,processId,xkpStatus,powerType);
            logList.add(xkpLog);
        }
    }

    /**
     * 权限更新子方法
     * @param paramDTO 员工权限
     * @param closeBuildPowerIdList 管理的门禁ID
     * @param fsEmpCloseMap 富士设备
     * @param curCarList
     * @return
     */
    private boolean subDownPowerForCloseFs(ParamDTO paramDTO,
                                       List<String> closeBuildPowerIdList,
                                       Map<String,List<BuildEmployeePowerVO>> fsEmpCloseMap,
                                       List<SafetyEmployeePrivateCarDO> curCarList ){
        String closeDeviceCode = "";
        String closeDeviceName = "";
        List<BuildEmployeePowerVO> closeBuildPowerList = fsEmpCloseMap.get(paramDTO.getEmpNo());
        if(!CollectionUtils.isEmpty(closeBuildPowerList)){
            closeBuildPowerIdList.addAll(closeBuildPowerList.stream().map(BuildEmployeePowerVO::getId).collect(Collectors.toList()));

            closeDeviceCode = closeBuildPowerList.stream().map(BuildEmployeePowerVO::getDeviceCode)
                    .distinct().collect(Collectors.joining(","));
            closeDeviceName = closeBuildPowerList.stream().map(BuildEmployeePowerVO::getDeviceName)
                    .distinct().collect(Collectors.joining(","));
        }
        paramDTO.setDeviceCode(closeDeviceCode);
        paramDTO.setDeviceName(closeDeviceName);
        //关闭厂区及楼宇权限
        boolean closeBol = powerUpdate(paramDTO, curCarList);
        return closeBol;
    }


    public void subDownPowerFormatParamDTO(ParamDTO paramDTO,String downFactory,
                                         String oldFactory, String newFactory,
                                         String deviceCodeAll,String deviceNameAll) {
        String closeDeviceCode = paramDTO.getDeviceCode();
        String closeDeviceName = paramDTO.getDeviceName();
        try{
            if (!StringUtils.isBlank(oldFactory)) {
                //取差集
                HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                HashSet<String> oldStr = new HashSet<>(Arrays.asList(oldFactory.split(",")));
                oldStr.removeAll(newStr);
                if(!CollectionUtils.isEmpty(oldStr)){
                    downFactory = String.join(",", oldStr);
                }
            }
            if(StringUtils.isNotBlank(deviceCodeAll)){
                Set<String> closeDeviceCodeSet = StringUtils.isNotBlank(closeDeviceCode)?
                        Arrays.stream(closeDeviceCode.split(",")).collect(Collectors.toSet()):new HashSet<>();
                Set<String> closeDeviceNameSet = StringUtils.isNotBlank(closeDeviceName)?
                        Arrays.stream(closeDeviceName.split(",")).collect(Collectors.toSet()):new HashSet<>();

                List<String> codelist = Arrays.stream(deviceCodeAll.split(","))
                        .filter(code -> !closeDeviceCodeSet.contains(code))
                        .collect(Collectors.toList());

                List<String> nameList = Arrays.stream(deviceNameAll.split(","))
                        .filter(na -> !closeDeviceNameSet.contains(na))
                        .collect(Collectors.toList());
                paramDTO.setDeviceCode(String.join(",",codelist));
                paramDTO.setDeviceName(String.join(",",nameList));
            }
        }catch (Exception e){
            LogUtils.error("数据处理异常",e);
        }
        paramDTO.setFactoryId(downFactory);
    }

    /**
     * 下发权限
     * @param userList 人员权限的list
     * @param powerType 操作类型 1开通2关闭
     * @param newFactory 此批次操作的工厂code（不能为空）
     * @param applyEndTime 权限申请的结束时间（没有就传当天 带时分秒）
     * @param userType 人员类型（WX  ZS）
     * @param processId 流程id（记日志用）
     */
    public void downPower(List<ParamDTO> userList,String powerType,String newFactory,String applyEndTime,
                          String userType,String processId){
        String applyBeginTime = DateUtils.format(new Date(), 1) + " 00:00:00";

        List<SafetyParkEmployeePowerInfoDO> successList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        //保存涉及人员的变更前大门权限
        List<String> empNos = userList.stream().map(ParamDTO::getEmpNo).distinct().collect(Collectors.toList());
        //过滤掉黑名单员工，在下发权限时校验判断
        List<String> filterEmpNos = safetyBaseHandler.filterBlackEmpNos(empNos);
        try{
            //查询出注销人员当前的门禁权限，保存到history表中
            ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
            powerList.setEmpNoList(empNos);
            List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoService.getDetailByCon(powerList);
            safetyParkEmpPowerReportService.batchUpdateHistoryPower(curList,"权限变更");
        }catch (Exception e){
            LogUtils.error("批量保存最后大门权限异常",e);
        }
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNos);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        List<String> closeBuildPowerIdList = new ArrayList<>();
        //关闭厂区时，查询对应厂区下的楼宇权限，需要同步删除
        Map<String,List<BuildEmployeePowerVO>> fsEmpCloseMap = new HashMap<>();
        Map<String,List<SafetyBuildEmployeePowerDO>> xkpEmpCloseMap = new HashMap<>();
        if("2".equals(powerType)){
            Map<String,Object> retMap = getBuildPowerForPark(xkpEmpCloseMap,fsEmpCloseMap,newFactory,empNos);
            fsEmpCloseMap = (Map<String, List<BuildEmployeePowerVO>>) retMap.get("fsEmpCloseMap");
            xkpEmpCloseMap = (Map<String, List<SafetyBuildEmployeePowerDO>>) retMap.get("xkpEmpCloseMap");
        }
        //查询员工的楼宇权限
        getDeviceId(userList);
        // 初始化组织ID和curId
        safetyBaseHandler.generalOrgIdForBatch(userList);
        //根据流程获取日志所属字段
        String reason = "";
        String createBy = "";
        String dutyDeptCode = "";
        String dutyDeptName = "";
        if(StringUtils.isNotBlank(processId)){
            SafetyParkPowerProcessDO parkPowerDO = safetyParkPowerProcessService.selectByPrimaryKey(processId);
            if(parkPowerDO != null){
                reason = parkPowerDO.getApplyReason();
                createBy = parkPowerDO.getCreateEmployeeName();
                Map<String,String> dutyDeptMap = getParkProcessDutyDept(parkPowerDO,dutyDeptCode,dutyDeptName);
                dutyDeptCode = dutyDeptMap.get("dutyDeptCode");
                dutyDeptName = dutyDeptMap.get("dutyDeptName");
            }
        }
        //获取所有的厂区名称；
        Map<Integer, String> facMap = safetyFieldManageService.getFactoryNameList();
        
        for(ParamDTO paramDTO : userList){
            boolean openBol = false;
            //记录日志
            SafetyBuildIssuedLogDO log = new SafetyBuildIssuedLogDO();
            log.setDutyDeptCode(dutyDeptCode);
            log.setDutyDeptName(dutyDeptName);
            try {
                String curEmpNo = paramDTO.getEmpNo();
                List<SafetyEmployeePrivateCarDO> curCarList = carinfoMap.get(curEmpNo);
                //原来的厂区
                String oldFactory = paramDTO.getFactoryId();
                String downFactory = "";

                String powerEndTime = applyEndTime;
                if(!StringUtils.isBlank(paramDTO.getPowerEndTime())){
                    powerEndTime = DateUtils.format(DateUtils.parse(paramDTO.getPowerEndTime(), 1),1)+" 23:59:59";
                }
                //员工所有楼宇权限
                String deviceCodeAll = paramDTO.getDeviceCode();
                String deviceNameAll = paramDTO.getDeviceName();

                if ("1".equals(powerType)) {//开通
                    if (!StringUtils.isBlank(oldFactory)) {
                        //取合集
                        HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                        HashSet<String> oldStr = new HashSet<>(Arrays.asList(oldFactory.split(",")));
                        oldStr.addAll(newStr);
                        downFactory = String.join(",", oldStr);
                    }else{
                        downFactory = newFactory;
                    }
                    // 只对新操作的厂区进行时间变更，原来的厂区不操作
                    HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                    String finalApplyEndTime = applyEndTime;
                    Short deleFlag = Short.valueOf("0");
                    newStr.forEach(li -> {
                        SafetyParkEmployeePowerInfoDO successEn = formatParkPowerDO(curEmpNo,li,applyBeginTime,finalApplyEndTime,deleFlag,"0");;
                        successList.add(successEn);
                    });
                    //比较结束时间，取最大值
                    applyEndTime = DateUtils.loseDay(powerEndTime,applyEndTime)>0?applyEndTime:powerEndTime;
                    paramDTO.setEndDate(applyEndTime);
                    paramDTO.setFactoryId(downFactory);

                } else if ("2".equals(powerType)) {//关闭
                    paramDTO.setFactoryId(newFactory);
                    String facName = safetyBaseHandler.getParkNameBYCode(newFactory,facMap);
                    paramDTO.setFactoryName(facName);
                    paramDTO.setBeginDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
                    paramDTO.setEndDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59");

                    String[] split = newFactory.split(",");
                    String powerBegin = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00";
                    String powerEnd = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00";
                    Short deleFlag = Short.valueOf("1");
                    for (String str:split){
                        SafetyParkEmployeePowerInfoDO successEn = formatParkPowerDO(curEmpNo,str,powerBegin,powerEnd,deleFlag,"0");
                        successList.add(successEn);
                    }
                    //关闭新开普设备，并保存日志
                    subDownPowerForCloseXkp(paramDTO,closeBuildPowerIdList,logList,curEmpNo,
                            xkpEmpCloseMap,newFactory,log,reason,createBy,userType,processId,powerType);
                    //关闭富士设备
                    boolean closeBol = subDownPowerForCloseFs(paramDTO,closeBuildPowerIdList,fsEmpCloseMap,curCarList);
                    //厂区关闭时保留厂区的关闭日志
                    initIssueLog(log,paramDTO,reason,createBy,userType,processId,closeBol,powerType);
                    logList.add(log);
                    //整理厂区和设备等信息
                    subDownPowerFormatParamDTO(paramDTO,downFactory,oldFactory,newFactory,deviceCodeAll,deviceNameAll);
                    paramDTO.setEndDate(powerEndTime);
                }
                paramDTO.setBeginDate(applyBeginTime);
                // 赋值厂区名称
                getParkNameBYCode(paramDTO, facMap);
                //判断员工不在黑名单中时，进行权限更新
                if(!CollectionUtils.isEmpty(filterEmpNos) && filterEmpNos.contains(curEmpNo)){
                    //风险富士权限
                    openBol = powerUpdate(paramDTO, curCarList);
                }

            } catch (Exception e) {
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"入参dto={}\"}",paramDTO);
                LogUtils.error("正式员工啊sendToCrk下发出错----",e);
            }

            if("1".equals(powerType)){
                initIssueLog(log,paramDTO,reason,createBy,userType,processId,openBol,powerType);
                logList.add(log);
            }
        }
        //更新员工厂区权限
        safetyParkPowerProcessService.dealPower(successList,userType);
        //更新员工厂区楼宇权限
        if("2".equals(powerType) && !CollectionUtils.isEmpty(closeBuildPowerIdList)){
            try{
                SafetyBuildEmployeePowerDO record = new SafetyBuildEmployeePowerDO();
                record.setRemark("关闭厂区时关闭对应楼宇");
                List<List<String>> parIdList = ListUtils.partition(closeBuildPowerIdList,500);
                parIdList.forEach(e->{
                    BuildEmployeePowerListParam param = new BuildEmployeePowerListParam();
                    param.setIdList(closeBuildPowerIdList);
                    safetyBuildEmployeePowerService.deleteByExample(param,record);
                });
            }catch (Exception e){
                LogUtils.error("关闭员工楼宇权限异常",e);
            }
        }

        safetyParkPowerProcessService.dealPower(successList,userType);
        if(!CollectionUtils.isEmpty(logList)){
            safetyBuildIssuedLogService.batchInsert(logList);
        }
    }

    private Map<String,Object> getBuildPowerForPark(Map<String,List<SafetyBuildEmployeePowerDO>> xkpEmpCloseMap,
                                      Map<String,List<BuildEmployeePowerVO>> fsEmpCloseMap,
                                      String newFactory,List<String> empNos){
        Map<String,Object> retMap = new HashMap<>();
        BuildEmployeePowerListParam param = new BuildEmployeePowerListParam();
        param.setFactoryIdList(Arrays.asList(newFactory.split(",")));
        param.setEmpCodeList(empNos);
        param.setDeleteFlag("0"); //未删除的对应厂区权限
        List<BuildEmployeePowerVO> empPowers = safetyBuildEmployeePowerService.selectList(param);
        //新开普楼宇
        List<BuildEmployeePowerVO> xkpEmpPowers = empPowers.stream().filter(e->"1".equals(e.getDeviceType())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(xkpEmpPowers)){
            try{
                xkpEmpCloseMap = xkpEmpPowers.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
            }catch (Exception e){
                LogUtils.error("关闭员工"+empNos+"新开普权限异常",e);
            }
        }
        //富士楼宇
        List<BuildEmployeePowerVO> fsEmpPowers = empPowers.stream().filter(e->"2".equals(e.getDeviceType())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(fsEmpPowers)){
            fsEmpCloseMap = fsEmpPowers.stream().collect(Collectors.groupingBy(BuildEmployeePowerVO::getEmployeeCode));
        }
        retMap.put("xkpEmpCloseMap",xkpEmpCloseMap);
        retMap.put("fsEmpCloseMap",fsEmpCloseMap);
        return retMap;
    }

    /**
     * SUB-整理厂区权限DO
     * @param empNo
     * @param factoryId
     * @param beginTime
     * @param endTime
     * @param deleFlag
     * @param downMark
     * @return
     */
    private SafetyParkEmployeePowerInfoDO formatParkPowerDO(String empNo,String factoryId,String beginTime,
                                                            String endTime,Short deleFlag,String downMark){
        SafetyParkEmployeePowerInfoDO successEn = new SafetyParkEmployeePowerInfoDO();
        successEn.setEmployeeCode(empNo);
        successEn.setFactoryId(factoryId);
        successEn.setPowerBeginTime(beginTime);
        successEn.setPowerEndTime(endTime);
        successEn.setCreateTime(new Date());
        successEn.setUpdateTime(new Date());
        successEn.setIsDelete(deleFlag);
        successEn.setDownMark(downMark); //标记已下发
        return successEn;
    }

    /**
     * 获取员工责任部门
     * @param processDO
     * @param dutyDeptCode
     * @param dutyDeptName
     */
    private Map<String,String> getParkProcessDutyDept(SafetyParkPowerProcessDO processDO,String dutyDeptCode,String dutyDeptName){
        Map<String,String> dutyDeptMap = new HashMap<>();
        //楼宇管理员时取管辖的责任部门，系统管理员和个人，取员工所属责任部门
        List<String> deptCodeList = new ArrayList<>();
        if("1".equals(processDO.getRoleType())){
            String createDeptCode = processDO.getCreateEmployeeDeptCode();
            deptCodeList.add(createDeptCode);

            List<BuildGroupDutyVO> buildDutyVOs = safetyBuildPowerService.getGroupDuty(deptCodeList);
            if (!CollectionUtils.isEmpty(buildDutyVOs)) {
                dutyDeptCode = buildDutyVOs.get(0).getDutyDeptCode();
                dutyDeptName = buildDutyVOs.get(0).getDutyDeptName();
            } else {
                // 处理 buildDutyVOs 为空的情况
                LogUtils.warn("No build duty found for department code: {}", createDeptCode);
            }
        }else{
            deptCodeList.add(processDO.getCreateEmployeeNo());
            List<SafetyBuildPowerDO> list = safetyBuildPowerService.getListByUserCode(deptCodeList);
            if (!CollectionUtils.isEmpty(list)) {
                dutyDeptCode = list.get(0).getDutyDeptCode();
                dutyDeptName = list.get(0).getDutyDeptName();
            } else {
                // 处理 buildDutyVOs 为空的情况
                LogUtils.warn("No build duty found for department code: {}", processDO.getCreateEmployeeNo());
            }
        }
        dutyDeptMap.put("dutyDeptCode",dutyDeptCode);
        dutyDeptMap.put("dutyDeptName",dutyDeptName);
        return dutyDeptMap;
    }

    /**
     * 整理日志数据
     * @param log
     * @param paramDTO
     * @param reason
     * @param createBy
     * @param userType
     * @param processId
     * @param openBol
     * @param powerType
     * @return
     */
    private SafetyBuildIssuedLogDO initIssueLog(SafetyBuildIssuedLogDO log,ParamDTO paramDTO,String reason,String createBy,
                                                String userType,String processId,Boolean openBol,
                                                String powerType){
        log.setEmployeeCode(paramDTO.getEmpNo());
        log.setCreateTime(new Date());
        log.setEmployeeName(paramDTO.getEmpName());
        log.setDoorId(paramDTO.getFactoryId());
        log.setDoorName(paramDTO.getFactoryName());
        log.setDeviceCode(paramDTO.getDeviceCode());
        log.setDeviceName(paramDTO.getDeviceName());
        log.setBeginTime(paramDTO.getBeginDate());
        log.setEndTime(paramDTO.getEndDate());
        log.setRemark(reason);  //创建原因
        log.setCreateBy(createBy);  //创建人
        if("ZS".equals(userType)){
            log.setModelId("park_power_process");
            log.setTargetId(processId);
            log.setSourceType("1");
        }else if("WX".equals(userType)){
            log.setModelId("WX");
        }
        log.setIssuedStatus(openBol?"成功":"异常");
        log.setIssuedType("1".equals(powerType)?"开通":"关闭");

        return log;
    }

    /**
     *
     * @param empNo 员工号
     * @param buildList 楼宇权限
     * @param factoryIdStr 厂区
     */
    public Boolean updateXkpEmpPower(String empNo,List<SafetyBuildEmployeePowerDO> buildList,String factoryIdStr,String issueType){
        String powerEndDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        SafetyBuildIssuedLogDO safetyBuildIssuedLogDO = new SafetyBuildIssuedLogDO();
        String deviceCode = buildList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.joining(","));
        String deviceName = buildList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).collect(Collectors.joining(","));

        safetyBuildIssuedLogDO.setEmployeeCode(empNo);
        safetyBuildIssuedLogDO.setDoorId(factoryIdStr);
        safetyBuildIssuedLogDO.setDeviceCode(deviceCode);
        safetyBuildIssuedLogDO.setDeviceName(deviceName);
        safetyBuildIssuedLogDO.setIssuedType(issueType); //关闭 /开通
        safetyBuildIssuedLogDO.setBeginTime(powerEndDate);
        safetyBuildIssuedLogDO.setEndTime(powerEndDate);

        String status = safetyBuildIssuedLogService.issuedBuildPower(safetyBuildIssuedLogDO);

        return "成功".equals(status)?true:false;
    }
    /**
     * 暴击权限-不下发
     * @param userList 人员权限的list
     * @param powerType 操作类型 1开通2关闭
     * @param newFactory 此批次操作的工厂code（不能为空）
     * @param applyEndTime 权限申请的结束时间（没有就传当天 带时分秒）
     * @param userType 人员类型（WX  ZS）
     * @param processId 流程id（记日志用）
     */
    public void downPowerNew(List<ParamDTO> userList,String powerType,String newFactory,String applyEndTime,
                             String userType,String processId){
        String applyBeginTime = DateUtils.format(new Date(), 1) + " 00:00:00";
        List<String> empNoList = userList.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(new ArrayList<>(empNoList));
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        List<SafetyFieldManageDO> factroyList = safetyFieldManageService.getFactoryListNew();
        Map<String,SafetyFieldManageDO> factoryMap = factroyList.stream().collect(Collectors.toMap(e->String.valueOf(e.getId()), e -> e));

        List<SafetyParkEmployeePowerInfoDO> successList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        //保存涉及人员的变更前大门权限
        try{
            List<String> empNos = userList.stream().map(ParamDTO::getEmpNo).distinct().collect(Collectors.toList());
            //查询出注销人员当前的门禁权限，保存到history表中
            ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
            powerList.setEmpNoList(empNos);
            List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoService.getDetailByCon(powerList);
            safetyParkEmpPowerReportService.batchUpdateHistoryPower(curList,"权限变更");
        }catch (Exception e){
            LogUtils.error("批量保存最后大门权限异常",e);
        }

        // 初始化组织ID和curId
        safetyBaseHandler.generalOrgIdForBatch(userList);

        for(ParamDTO paramDTO : userList){
            boolean openBol = false;
            try {
                String curEmpNo = paramDTO.getEmpNo();
                //原来的厂区
                String oldFactory = paramDTO.getFactoryId();
                String downFactory = "";

                String powerEndTime = applyEndTime;
                if ("1".equals(powerType)) {//开通
                    if (!StringUtils.isBlank(oldFactory)) {
                        //取合集
                        HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                        HashSet<String> oldStr = new HashSet<>(Arrays.asList(oldFactory.split(",")));
                        oldStr.addAll(newStr);
                        downFactory = String.join(",", oldStr);
                    }else{
                        downFactory = newFactory;
                    }

                    // 只对新操作的厂区进行时间变更，原来的厂区不操作
                    HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                    Short deleFlag = Short.valueOf("0");
                    newStr.forEach(li -> {
                        SafetyParkEmployeePowerInfoDO successEn = formatParkPowerDO(curEmpNo,li,applyBeginTime,powerEndTime,deleFlag,"1");;
                        successEn.setFactoryName(factoryMap.get(li).getFieldName());
                        successList.add(successEn);
                    });
                    //比较结束时间，取最大值
                    applyEndTime = DateUtils.loseDay(powerEndTime,applyEndTime)>0?applyEndTime:powerEndTime;

                    paramDTO.setEndDate(applyEndTime);
                } else if ("2".equals(powerType)) {//关闭
                    paramDTO.setFactoryId(newFactory);
                    paramDTO.setBeginDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
                    paramDTO.setEndDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59");

                    String[] split = newFactory.split(",");

                    String powerBegin = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00";
                    String powerEnd = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00";
                    Short deleFlag = Short.valueOf("1");
                    for (String str:split){
                        SafetyParkEmployeePowerInfoDO successEn = formatParkPowerDO(curEmpNo,str,powerBegin,powerEnd,deleFlag,"1");
                        successList.add(successEn);
                    }
                    //关闭  20250721 - 下发的动作都放到定时任务中指向
                    if (!StringUtils.isBlank(oldFactory)) {
                        //取差集
                        HashSet<String> newStr = new HashSet<>(Arrays.asList(newFactory.split(",")));
                        HashSet<String> oldStr = new HashSet<>(Arrays.asList(oldFactory.split(",")));
                        oldStr.removeAll(newStr);
                        if(CollectionUtils.isEmpty(oldStr)){continue;}
                        downFactory = String.join(",", oldStr);
                    }
                }
            } catch (Exception e) {
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"入参dto={}\"}",paramDTO);
                LogUtils.error("正式员工啊sendToCrk下发出错----",e);
            }
        }
        safetyParkPowerProcessService.dealPower(successList,userType);
    }

    /**
     * 权限下发
     * @param paramDTO
     * @param carList
     * @return
     */
    public boolean powerUpdate(ParamDTO paramDTO,List<SafetyEmployeePrivateCarDO> carList) {
        boolean faceResult = false;
        boolean cardResult = false;
        boolean carResult = false;

        if (StringUtils.isEmpty(paramDTO.getFactoryId())) {
            return false;
        }

        String empNo = paramDTO.getEmpNo();
        String factoryId = paramDTO.getFactoryId();
        String userType = paramDTO.getUserType();

        Map<String, Object> map = new HashMap<>();
        map.put("empowerType", "正式员工");
        map.put("regionName", "大门人脸");
        map.put("factoryIds", Arrays.asList(paramDTO.getFactoryId().split(",")));

        //下发人脸
        String faceDevices = safetyOutsiderPromiseChangeService.findDevices(map);
        if (StringUtils.isEmpty(faceDevices) && StringUtils.isBlank(paramDTO.getDeviceCode())) {
            LogUtils.info("powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(人脸) \"正式员工\" 授权设备号！",empNo,factoryId);
        } else {
            faceDevices = stringFormat(faceDevices, paramDTO.getDeviceCode());
            try {
                //更新门禁人脸权限
                faceResult = updateFaceData(paramDTO, faceDevices);
            }catch (Exception e){
                LogUtils.error("下发人脸权限失败{}",e);
            }
        }

        //下发卡
        map.put("regionName", "大门门禁");
        String cardDevices = safetyOutsiderPromiseChangeService.findDevices(map);
        //更新门禁卡权限
        if (StringUtils.isEmpty(cardDevices) && StringUtils.isBlank(paramDTO.getDeviceCode())) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"正式员工\" 授权设备号！\"}",empNo,factoryId);
        } else {
            cardDevices = stringFormat(cardDevices, paramDTO.getDeviceCode());
            try{
                cardResult = updateCardData(paramDTO, cardDevices);
            }catch (Exception e){
                LogUtils.error("下发卡权限失败{}",e);
            }
        }

        //下发车辆
        if (CollectionUtils.isEmpty(carList)) {
            return faceResult || cardResult;
        }
        map.put("regionName", "大门车行");
        map.put("empowerType", "员工车辆");
        if(!CollectionUtils.isEmpty(carList)){
            String carDevices = safetyOutsiderPromiseChangeService.findDevices(map);
            if (StringUtils.isEmpty(carDevices)) {
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"员工车辆\" 授权设备号！\"}",empNo,factoryId);
            } else {
                try {
                    carResult = downCarPower(paramDTO, carDevices, carList,userType);
                }catch (Exception e){
                    LogUtils.error("下发车辆权限失败{}",e);
                }
            }
        }

        return faceResult || cardResult || carResult;
    }


    /**
     * 权限下发
     * @param paramDTO
     * @param carList
     * @return
     */
    public boolean powerUpdateNew(ParamDTO paramDTO,List<SafetyEmployeePrivateCarDO> carList) {
        boolean faceResult = false, cardResult = false, carResult = false;
        if (StringUtils.isEmpty(paramDTO.getFactoryId()) && StringUtils.isEmpty(paramDTO.getDeviceCode())) {
            return false;
        }

        String empNo = paramDTO.getEmpNo();
        String factoryId = paramDTO.getFactoryId();
        String userType = paramDTO.getUserType();

        String deviceCode = paramDTO.getDeviceCode();
        String faceDevices = "";
        String cardDevices = "";
        String carDevices = "";
        
        if(StringUtils.isNotBlank(factoryId)){
            Map<String, Object> map = new HashMap<>();
            map.put("empowerType", "正式员工");
            map.put("regionName", "大门人脸");
            if("WX".equals(userType)){
                map.put("empowerType", "外协");
            }
            map.put("factoryIds", Arrays.asList(paramDTO.getFactoryId().split(",")));
            //获取人脸设备
            faceDevices = safetyOutsiderPromiseChangeService.findDevices(map);

            //下发卡
            map.put("regionName", "大门门禁");
            cardDevices = safetyOutsiderPromiseChangeService.findDevices(map);

            map.put("regionName", "大门车行");
            map.put("empowerType", "员工车辆");
            if("WX".equals(userType)){
                map.put("empowerType", "外协车辆");
            }
            carDevices = safetyOutsiderPromiseChangeService.findDevices(map);
        }
        
        if (StringUtils.isEmpty(faceDevices) && StringUtils.isBlank(deviceCode)) {
            LogUtils.info("powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(人脸) \"正式员工\" 授权设备号！",empNo,factoryId);
        } else {
            faceDevices = stringFormat(faceDevices, deviceCode);
            try {
                //更新门禁人脸权限
                faceResult = updateFaceData(paramDTO, faceDevices);
            }catch (Exception e){
                LogUtils.error("下发人脸权限失败{}",e);
            }
        }

        //更新门禁卡权限
        if (StringUtils.isEmpty(cardDevices) && StringUtils.isBlank(deviceCode)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"正式员工\" 授权设备号！\"}",empNo,factoryId);
        } else {
            cardDevices = stringFormat(cardDevices, deviceCode);
            try{
                cardResult = updateCardData(paramDTO, cardDevices);
            }catch (Exception e){
                LogUtils.error("下发卡权限失败{}",e);
            }
        }
        //下发车辆
        if (CollectionUtils.isEmpty(carList)) {
            return faceResult || cardResult;
        }
        if (StringUtils.isEmpty(carDevices)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"员工车辆\" 授权设备号！\"}",empNo,factoryId);
        } else {
            try {
                carResult = downCarPower(paramDTO, carDevices, carList,userType);
            }catch (Exception e){
                LogUtils.error("下发车辆权限失败{}",e);
            }
        }

        return faceResult || cardResult || carResult;
    }

    /**
     * 下发车权限
     * @param paramDTO
     * @param carDevices
     * @param carList
     * @return
     */
    public boolean downCarPower(ParamDTO paramDTO, String carDevices, List<SafetyEmployeePrivateCarDO> carList,String userType) {
        boolean result = false;
        if(CollectionUtils.isEmpty(carList)){return false;}
        String empNo = paramDTO.getEmpNo();
        String tcmId = CRK_EMP_CAR_RID;
        if("WX".equals(userType)){
            tcmId = CRK_EMP_WX_CAR_RID;
        }
        for (SafetyEmployeePrivateCarDO carInfo : carList) {
            boolean bool = false;
            String carNo = carInfo.getCarNo();

            String bodyParams = "\"TokenId='"+carNo+"'\"";
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");

            String tcmName = "";
            //查询出入口是否有该辆车的进出厂记录
            String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);
            if (StringUtils.isEmpty(returnJson)) {
                LogUtils.info("出入口接口未找到{}的出入厂数据",carNo);
                continue;
            }else {
                JSONObject retObject = JSONObject.parseObject(returnJson);
                JSONObject retMap = retObject.getJSONObject("Model");
                if (retMap == null) {
                    bool = true;
                }else{
                    tcmName = retMap.getString("TcmName");
                }
            }
            //只修改员工车辆和外协车辆的权限
            if("".equals(tcmName)||"员工车辆".equals(tcmName) || "外协车辆".equals(tcmName)){
                if(bool){
                    if(!"false".equals(syncCrk(paramDTO, carDevices, carInfo,tcmId))){
                        result = true;
                    }
                }else {
                    if(!"false".equals(updateCrk(paramDTO,carDevices,carInfo,tcmId))){
                        result = true;
                    }
                }
            }else{
                LogUtils.info("员工{}车辆{}，车辆类型是{}，不做权限更新",empNo,carNo,tcmName);
            }

        }
        return result;
    }

    /**
     * 下发车辆入场权限
     * @param paramDTO
     * @param carDevices
     * @param carInfo
     * @return
     */
    public String syncCrk(ParamDTO paramDTO, String carDevices, SafetyEmployeePrivateCarDO carInfo,String tcmId) {
        OkHttpClient client = new OkHttpClient();
        String[] AuthDevice = carDevices.split(",");

        String carNo = carInfo.getCarNo();
        String carColor = carInfo.getCarColor();
        String carType = carInfo.getVehicleType();

        String StaffNo = paramDTO.getEmpNo();    //人员编号
        String StaffName = paramDTO.getEmpName();       //人员姓名
        String TelphoneNo = paramDTO.getPhoneNo();     //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "eippark";   //操作员姓名
        String OperNo = "eippark";     //操作员编号
        Integer ID = 0;         //操作编号
        String[] ProjectGids = new String[]{CRK_EMP_CAR_GID};    //项目编号集合
        Map<String,Object> map = new HashMap();

        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("TelphoneNo", TelphoneNo);
        map.put("Redate", Redate);
        map.put("OperName", OperName);
        map.put("OperNo", OperNo);
        map.put("OrginazitionId", CAR_DEFAULT_ORGANTION);
        map.put("Gid", CRK_EMP_CAR_GID);
        map.put("ID", ID);
        map.put("ProjectGids", ProjectGids);
        map.put("VehicleBand", carType); //车辆型号
        map.put("VehicleColor", carColor); //车辆颜色

        map.put("TokenType", 0);//凭证类型（0 车牌 1 卡片 2 ETC 3纸票 4二维码 5 生物）
        map.put("IcNumber", "");//Ic号
        map.put("IdNumber", "");//Id号
        map.put("Deposit", 0);//办卡押金
        map.put("IsWrite", false);//输入/录入
        map.put("IsLegal", true);//是否合法

        JSONObject ParkIssue = new JSONObject();
        ParkIssue.put("StaffNo", StaffNo);
        ParkIssue.put("StaffName", StaffName);
        ParkIssue.put("TelphoneNo", TelphoneNo);
        ParkIssue.put("Redate", Redate);
        ParkIssue.put("OperName", OperName);
        ParkIssue.put("OperNo", OperNo);
        ParkIssue.put("OrginazitionId", CAR_DEFAULT_ORGANTION);
        ParkIssue.put("Gid", CRK_EMP_CAR_GID);
        ParkIssue.put("ID", ID);
        ParkIssue.put("ProjectGids", ProjectGids);
        ParkIssue.put("TokenOper", 1);
        ParkIssue.put("AlreadyIn", false);
        ParkIssue.put("TokenId", carNo);//这个是关键值，出入口系统根据这个判断是否唯一车辆
        ParkIssue.put("SerialNo", carNo);
        ParkIssue.put("Tcm", tcmId);
        ParkIssue.put("PayType", 0);
        ParkIssue.put("AccountBalance", 0);
        ParkIssue.put("BeginDate", paramDTO.getBeginDate());
        ParkIssue.put("EndDate", paramDTO.getEndDate());
        ParkIssue.put("AuthDevice", AuthDevice);
        ParkIssue.put("Token", carNo);
        ParkIssue.put("VehicleColor", carColor); //车辆颜色
        ParkIssue.put("VehicleBand", carType); //车辆类型
        ParkIssue.put("TokenType", 0);
        ParkIssue.put("UseModel", 1);
        ParkIssue.put("Plate", carNo);

        map.put("ParkIssue", ParkIssue);

        String toCrkString = JSON.toJSONString(map);
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, toCrkString);
        Request request = new Request.Builder()
                .url(crkInsertUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("入厂请求同步-出入口系统 接口地址 {} \n 请求参数 {}", crkInsertUrl, toCrkString);

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            LogUtils.info("下发入厂请求同步-出入口系统新增 返回结果：" + str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("State") != null){
                //
                JSONObject stateObj = obj.getJSONObject("State");
                if(stateObj.get("IsSucess") != null){
                    String IsSucess = stateObj.get("IsSucess").toString();
                    LogUtils.info("下发入厂请求同步-出入口系统新增成功：" + IsSucess);
                    return IsSucess;
                }
            }
        } catch (IOException e) {
            LogUtils.info("大门门禁下发入厂请求同步-出入口系统新增调用失败:{};参数={}", e.getMessage(),ParkIssue);
        }
        return "false";
    }

    /**
     * 更新车辆入场权限
     * @param paramDTO
     * @param carDevices
     * @param carInfo
     */
    public String updateCrk(ParamDTO paramDTO, String carDevices, SafetyEmployeePrivateCarDO carInfo,String tcmId) {
        OkHttpClient client = new OkHttpClient();
        String[] AuthDevice = carDevices.split(",");
        LogUtils.info("厂区映射 AuthDevice={}", AuthDevice.length);

        String carNo = carInfo.getCarNo();
        String carColor = carInfo.getCarColor();
        String carType = carInfo.getVehicleType();

        String StaffNo = paramDTO.getEmpNo();    //人员编号
        String StaffName = paramDTO.getEmpName();       //人员姓名
        String TelphoneNo = paramDTO.getPhoneNo();     //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "eippark";   //操作员姓名
        String OperNo = "eippark";     //操作员编号

        String[] ProjectGids = new String[]{CRK_EMP_CAR_GID};    //项目编号集合
        Map<String,Object> map = new HashMap();

        map.put("TokenId", carNo);       //车牌号
        map.put("Token", carNo);        //车牌号
        map.put("Plate", carNo);        //车牌号
        map.put("VehicleColor", carColor); //车辆颜色
        map.put("VehicleBand", carType); //车辆类型
        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("Tcm", tcmId);
        map.put("PayType", "0");
        map.put("AccountBalance", "0");
        map.put("TelphoneNo", TelphoneNo);        //车上第一个人的电话
        map.put("OrginazitionId", CAR_DEFAULT_ORGANTION);
        map.put("BeginDate", paramDTO.getBeginDate());
        map.put("EndDate", paramDTO.getEndDate());
        map.put("AuthDevice", AuthDevice);
        map.put("IsExistCard", false);
        map.put("TokenType", 0);
        map.put("UseModel", 1);
        map.put("TokenOper", 2);
        map.put("OperNo", OperNo);        //mobile eippark
        map.put("OperName", OperName);        //mobile eippark
        map.put("IsUpdateBeginDate", true);
        map.put("IsUpdateEndDate", true);
        map.put("Redate", Redate);
        map.put("Gid", CRK_EMP_CAR_GID);
        map.put("ProjectGids", ProjectGids);
        map.put("OldAccountBlace", 0);
        map.put("IsLegal", true);


        String toCrkString = JSON.toJSONString(map);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, toCrkString);
        Request request = new Request.Builder()
                .url(crkUpdateUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("入厂请求同步-出入口系统更新 接口地址 {} \n 请求参数 {}", crkUpdateUrl, toCrkString);

        Response response;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;
            String str = response.body().string();
            LogUtils.info("更新入厂请求同步-出入口系统更新 返回结果 \n {}", str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("IsSucess") != null){
                String IsSucess = obj.get("IsSucess").toString();
                LogUtils.info("更新入厂请求同步-出入口系统更新成功：" + IsSucess);
                return IsSucess;
            }
        } catch (Exception e) {
            LogUtils.info("大门门禁更新接口调用失败：{};参数={}",e.getMessage(),toCrkString);
        }
        return "false";
    }

    /**
     * 下发人脸-更新权限（带补发逻辑）
     * @param dto
     * @param empDevice
     * @return
     */
    private boolean updateFaceData(ParamDTO dto, String empDevice) {
        boolean result = false;
        String empNo = dto.getEmpNo();
        String empName = dto.getEmpName();
        //获取生活照
        String lifePhoto = safetyBaseHandler.getPhoto(empNo, 1);
        if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
            //获取证件照
            lifePhoto = safetyBaseHandler.getPhoto(empNo, 0);
        }
        //未获取到照片
        if(StringUtils.isEmpty(lifePhoto)){
            LogUtils.info("正式员工啊{}获取员工人脸照片失败!",empNo);
            //TODO 记日志
            return result;
        }
        //图片转化为二进制
        lifePhoto = lifePhoto + "&apikey=" + apikey;
        byte[] imageBytes = safetyBaseHandler.getPictureDataInputStream(lifePhoto);
        String base64_str = DatatypeConverter.printBase64Binary(imageBytes);

        Map<String, List<String>> deviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(empDevice);

        for (Map.Entry<String, List<String>> entry : deviceGidMap.entrySet()){
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if(!value.isEmpty()){
                JSONObject params = new JSONObject();
                JSONObject Staff = new JSONObject();
                //员工号
                Staff.put("StaffNo", empNo);
                //姓名
                Staff.put("StaffName", empName);

                Staff.put("OrganizationId", dto.getOrgId());
                Staff.put("Gid",key);
                //开始日期
                params.put("BeginTime",dto.getBeginDate());
                //结束日期
                params.put("EndTime",dto.getEndDate());
                params.put("AuthorizeDeviceNo", value);
                String[] ProjectGids = new String[]{key};
                params.put("ProjectGids", ProjectGids);
                params.put("Staff", Staff);
                params.put("Gid", key);
                params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
                LogUtils.info("正式员工啊人脸下发请求参数={}" , params);
                params.put("FacePicture", base64_str);

                Map<String,Object> requestReturn= handleRequest(faceModifyUrl,params,empName,empNo);
                boolean modifyStats = (boolean) requestReturn.get("issuedStats");
                String errorMsg = (String) requestReturn.get("errorMsg");
                if(!modifyStats  && (errorMsg.contains("未授权")|| errorMsg.contains("无门禁发行记录"))){
                    /*人脸更新提示未授权时，进行人脸发行操作 250721 */
                    params.put("OperType", UpdatePhotoEnum.FACEISSUE.getCode());
                    //执行卡发行
                    Map<String,Object> issueReturn = handleRequest(faceIssueUrl,params,empName,empNo);
                    boolean issuedStats = (boolean) issueReturn.get("issuedStats");
                    if(!issuedStats){
                        LogUtils.info("updateFaceData人脸发行失败name={}返回消息={}" , empName ,issueReturn.get("errorMsg"));
                    }
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * 处理请求
     * @param postUrl
     * @param params
     * @param name
     * @param workNo
     */
    public Map<String,Object> handleRequest(String postUrl,JSONObject params,String name,String workNo){
        boolean issuedStats = true;
        Map<String,Object> retMap = new HashMap<>();
        String r4 = HttpFileUtil.sendPost(postUrl, params.toJSONString());
        LogUtils.info("人脸权限下发name={},workNo={},返回消息={}" , name , workNo, r4);

        if (StringUtils.isBlank(r4)) {
            issuedStats = false;
        }else{
            JSONObject json4 = JSONObject.parseObject(r4);
            if(!"200".equals(json4.get("State")+"")){
                issuedStats = false;
            }
        }
        retMap.put("errorMsg",r4);
        retMap.put("issuedStats",issuedStats);
        return retMap;
    }

    private boolean updateCardData(ParamDTO dto, String empDevice) {
        boolean issuedStats = false;
        String empNo = dto.getEmpNo();
        String empName = dto.getEmpName();
        //根据设备号,获取所属gid 按照项目下发
        Map<String, List<String>> deviceGidMap = safetyBaseHandler.getDeviceGidMapModelFromAb(empDevice);
        String returnValue = "";
        try {
            //根据员工号获取员工token
            returnValue = HttpFileUtil.sendPost(issueDoorViewFuncUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
            JSONObject retObject = JSONObject.parseObject(returnValue);
            Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
            if (sucess) {
                JSONObject model = retObject.getJSONObject("Model");

                JSONObject params = new JSONObject();
                params.put("beginDate", dto.getBeginDate());
                params.put("endDate", dto.getEndDate());


                params.put("orginazitionId", dto.getOrgId());
                params.put("serialNo", model.getString("SerialNo"));
                params.put("staffName", empName);
                params.put("staffNo", empNo);
                params.put("stcm", "2C3DB448-9918-4665-A4C1-C99797E2A4DD");
                params.put("tcm", model.getString("TcmId"));
                params.put("token", model.getString("TokenId"));
                params.put("tokenId", model.getString("TokenId"));
                params.put("rid", dto.getCurId());
                params.put("tokenOper", 2); //修改
                params.put("tokenType", 1);
                params.put("useModel", 1);
                params.put("icStartTimeL1", "00:00");
                params.put("icStartTimeL2", "00:00");
                params.put("icStartTimeL3", "00:00");
                params.put("icStartTimeL4", "00:00");
                params.put("icStartTimeR1", "23:59");
                params.put("icStartTimeR2", "00:00");
                params.put("icStartTimeR3", "00:00");
                params.put("icStartTimeR4", "00:00");
                params.put("CarPass", "123456");

                for (Map.Entry<String, List<String>> entry : deviceGidMap.entrySet()) {
                    String key = entry.getKey();
                    String[] ProjectGids = new String[]{key};
                    List<String> value = entry.getValue();
                    params.put("projectGids", ProjectGids);
                    params.put("gid", key);
                    //根据员工号和gid获取是否已经授权过,没授权走新增
                    String checkGid = HttpFileUtil.sendPost(issueDoorViewCustomUrl, "{\"where\": \"StaffNo='" + empNo + "' and Gid='" + key + "' and TokenType=1 \"}");
                    JSONObject checkGidObject = JSONObject.parseObject(checkGid);
                    if (!ObjectUtils.isEmpty(checkGidObject)) {
                        JSONArray gidRecords = checkGidObject.getJSONArray("Records");
                        if (CollectionUtils.isEmpty(gidRecords)) {
                            params.put("tokenOper", 1);
                        }
                    }
                    JSONArray doorGrant = new JSONArray();
                    if (value != null && !value.isEmpty()) {
                        for (String deviceModelStr : value) {
                            JSONObject deviceModel = JSONObject.parseObject(deviceModelStr);
                            JSONObject doorGrantMap = new JSONObject();
                            doorGrantMap.put("collectCode", deviceModel.getString("collectCode"));
                            doorGrantMap.put("d1Grant", 1);
                            doorGrantMap.put("d1OpenMode", 1);
                            doorGrantMap.put("d1Valid", 0);
                            doorGrantMap.put("devName", deviceModel.getString("DevName"));
                            doorGrantMap.put("devNo", deviceModel.getString("DevNo"));
                            doorGrantMap.put("tokenId", model.getString("TokenId"));
                            /*设备设为动态获取 解决人脸和卡两用设备，卡权限授权时下发参数中设备类型要设置为31才生效 */
                            String devTypeCode = deviceModel.getString("DevTypeCode");
                            if(StringUtils.isBlank(devTypeCode)){
                                devTypeCode = "00";
                            }
                            //给697人脸调用卡片发行接口发行 "DevTypeCode": "31", 时新增参数 "CollectCode": "20",（多加个这个）
                            if("31".equals(devTypeCode)){
                                doorGrantMap.put("CollectCode","20");
                            }
                            doorGrantMap.put("devTypeCode", devTypeCode);
                            doorGrantMap.put("gid", deviceModel.getString("Gid"));
                            doorGrantMap.put("rid", UUID.randomUUID());
                            doorGrant.add(doorGrantMap);
                        }
                        params.put("doorGrant", doorGrant);
                        LogUtils.info("正式员工啊card修改,请求参数={}", params);
                        String modifyResult = HttpFileUtil.sendPost(cardModifyUrl, params.toJSONString());
                        LogUtils.info("正式员工啊card修改name={}返回消息={}", empName, modifyResult);
                        if (!org.springframework.util.StringUtils.isEmpty(modifyResult)) {
                            JSONObject result = JSONObject.parseObject(modifyResult);
                            if (result.getJSONObject("State") != null && result.getJSONObject("State").getBoolean("IsSucess")) {
                                issuedStats = true;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            LogUtils.error("获取员工token，员工号={},返回值={}",empNo,returnValue);
            LogUtils.error("正式工下发权限异常，{}",e);
        }
        return issuedStats;
    }

    /**
     * 人员组织变更时同步删除出门证权限
     *  1.获取需要删除的人员工号
     *  2.检查待删除人员工号，是否存在出门证权限
     *  3.存在出门证权限的人，进行删除并发送消息和创建EIP流程
     */
    public void deleteOutPower(){
        String beginTime = DateUtils.format(new Date(),1) + " 00:00:00";
        String endTime = DateUtils.format(new Date(),1) + " 23:59:59";
        // 1.获取需要删除的人员工号
        List<String> empNoList = safetyDepartChangeListService.selectEmpNoList(beginTime, endTime);
        if (CollectionUtils.isEmpty(empNoList)) {
            return;
        }
        // 2.检查待删除人员工号，是否存在出门证权限
        List<SafetyGoodsOutAuthDO> queryList = safetyGoodsOutAuthService.selectList(empNoList);
        if (CollectionUtils.isEmpty(queryList)) {
            return;
        }
        // 3.数据删除
        safetyGoodsOutProcessService.deleteOutPower(beginTime, endTime);
        // 4.获取存在出门证权限的人员工号
        Set<String> collect = queryList.stream().map(SafetyGoodsOutAuthDO::getEmpNo).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        // 5.进行消息发送和创建EIP流程
        collect.forEach(li -> {
            // 发送消息
            // TODO: 2026/4/8 暂不上线，等创建流程后在上线 
//            safetySendYxtHandler.sendYxtMsgByGoodsOut(li);
            // TODO: 2026/3/25  创建EIP流程

        });
    }

    /**
     * 获取组织ID和 curId
     * @param empNo 员工 ID
     * @return 返回组织ID和 curId left:组织ID right：curId
     */
    public Pair<String, Object> generalOrgId(String empNo) {
        // 1.获取人事ID 如果获取失败返回默认信息
        SafetyEmpCard safetyEmpCard = safetyEmpCardService.selectCrkIdByEmpNo(empNo);
        if (safetyEmpCard == null || StringUtils.isEmpty(safetyEmpCard.getCrkId()))  return Pair.of(CRK_ORGANTION_ID, UUID.randomUUID());
        String crkId = safetyEmpCard.getCrkId();

        // 2.调用出入口接口获取组织ID
        Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
        String orgid = crkEmpMap.get("orgid");
        String curId = crkEmpMap.get("rid");
        if(StringUtils.isBlank(orgid)){
            // 为空赋值为默认的值
            orgid = CRK_ORGANTION_ID;
        }
        if(StringUtils.isBlank(curId)){
            return Pair.of(orgid, UUID.randomUUID());
        }
        return Pair.of(orgid, curId);
    }

    /**
     * 获取组织ID和 curId
     * @param paramDTOS 入参
     */
    public void generalOrgId(List<ParamDTO> paramDTOS) {
        List<String> empNos = paramDTOS.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        // 1.获取人事ID 如果获取失败返回默认信息
        List<SafetyEmpCard> list = safetyEmpCardService.selectListCrkIdByEmpNo(empNos);
        Map<String, SafetyEmpCard> map = list.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, t -> t, (t1, t2) -> t2));
        paramDTOS.forEach(li -> {
            SafetyEmpCard empCard = map.get(li.getEmpNo());
            if (empCard == null || StringUtils.isBlank(empCard.getCrkId())) {
                // 没有值，则提供默认值
                li.setOrgId(CRK_ORGANTION_ID);
                li.setCurId(UUID.randomUUID().toString());
            } else {
                // 调用出入口接口获取组织ID
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(empCard.getCrkId());
                String orgid = crkEmpMap.get("orgid");
                String curId = crkEmpMap.get("rid");
                if (StringUtils.isBlank(orgid)) {
                    // 为空赋值为默认的值
                    li.setOrgId(CRK_ORGANTION_ID);
                } else {
                    li.setOrgId(orgid);
                }
                if(StringUtils.isBlank(curId)){
                    li.setCurId(UUID.randomUUID().toString());
                } else {
                    li.setCurId(curId);
                }
            }
        });
    }


    /**
     * 定时下发卡，人脸，车的权限
     */
    public void autoChangeFactoryAreaPower() {
        // 1.获取外协数据
        List<ParamDTO> list = safetyOutsiderPromiseChangeService.selectListByWxInfo(null);
        if (!CollectionUtils.isEmpty(list)) {
            // 区分外协 下发和注销权限的人
            list.forEach(li -> {
                if (StringUtils.isBlank(li.getFactoryId())) {
                    li.setType("1");
                } else {
                    li.setType("0");
                }
            });
        }

        // 2.获取正式工数据 (正式工注销和下发的数据分开，下发需要特殊处理)
        List<ParamDTO> paramDTOS = safetyParkEmployeePowerInfoService.selectListByDownMark(null);
        list.addAll(paramDTOS.stream().filter(li -> li.getType().equals("1")).collect(Collectors.toList()));
        // 正式工待下发的所有员工工号
        List<String> zsEmpNos = paramDTOS.stream().filter(li -> li.getType().equals("0")).map(ParamDTO::getEmpNo).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(zsEmpNos)) {
            // 获取正式工待下发权限的员工的所有厂区ID
            list.addAll(safetyParkEmployeePowerInfoService.selectByEmpNoList(zsEmpNos));
        }
        list = list.stream().filter(li -> StringUtils.isNotBlank(li.getEmpNo())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) return;

        // 获取员工的门禁权限和门禁对应的设备ID
        getDeviceId(list);

        // 3.查询员工对应的车辆信息
        Map<String, String> carMap = new HashMap<>();
        List<ParamDTO> carList = safetyParkPowerProcessService.getCarList(list);
        if (!CollectionUtils.isEmpty(carList)) {
            carMap = carList.stream().collect(Collectors.toMap(
                    ParamDTO::getEmpNo,
                    ParamDTO::getCarNo,
                    (oldValue, newValue) -> (newValue+","+oldValue)
            ));
        }
        Map<String, String> finalCarMap = carMap;

        List<String> empnoList = list.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        List<String> filterEmpNos = safetyBaseHandler.filterBlackEmpNos(empnoList);
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empnoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        // 4.车牌号填充
        list.forEach(li -> {
            String carNo = finalCarMap.get(li.getEmpNo());
            if (StringUtils.isNotBlank(carNo)) {
                li.setCarNo(carNo);
            }
        });

        // 5.注销
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        List<ParamDTO> updateStatusList = new ArrayList<>();
        // 注销开始时间
        String zxBeginDate = DateUtils.format(DateUtils.addDays(new Date(), -2), 1) + " 00:00:00";
        // 注销结束时间
        String zxEndDate = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59";
        List<ParamDTO> closeList = list.stream().filter(li -> li.getType().equals("1")).collect(Collectors.toList());
        closeList.forEach(li -> {
            // 确定开始时间
            li.setBeginDate(zxBeginDate);
            li.setEndDate(zxEndDate);
            li.setPowerEndTime(zxEndDate);
            String empNo = li.getEmpNo();
            List<SafetyEmployeePrivateCarDO> carinfoList = carinfoMap.get(empNo);
            // 正式工权限注销
            if (li.getUserType().equals("ZS")) {
                boolean b = powerUpdate(li, carinfoList);
                if (b) {
                    // 关闭成功，回写状态
                    updateStatusList.add(li);
                }
                String message = li.getEmpName() + "[" + li.getEmpNo() + "]:人脸、卡、车辆关闭" +  (b ? "成功" : "失败。");
                generatePowerLogRecord(logList, li, "关闭", message, b);
            }
        });
        // 6.下发
        List<ParamDTO> openList = list.stream().filter(li -> li.getType().equals("0")).collect(Collectors.toList());
        // 初始化组织ID和curId
        safetyBaseHandler.generalOrgIdForBatch(openList);

        // 下发开始时间
        String xfBeginDate = DateUtils.format(new Date(), 1) + " 00:00:00";
        // 下发结束时间
        String xfEndDate = "2099-12-31 23:59:59";
        openList.forEach(li -> {
            // 确定开始时间
            li.setBeginDate(xfBeginDate);
            // 确认结束时间
            String powerEndTime = li.getPowerEndTime();
            if (StringUtils.isNotBlank(powerEndTime)) {
                li.setEndDate(li.getPowerEndTime());
            } else {
                li.setEndDate(xfEndDate);
                li.setPowerEndTime(xfEndDate);
            }
            List<SafetyEmployeePrivateCarDO> carinfoList = carinfoMap.get(li.getEmpNo());
            //过滤后不在黑名单的员工正常下发权限
            if(!CollectionUtils.isEmpty(filterEmpNos) && filterEmpNos.contains(li.getEmpNo())){
                // 正式工权限下发
                if (li.getUserType().equals("ZS")) {
                    boolean b = powerUpdate(li, carinfoList);
                    if (b) {
                        // 下发成功，回写状态
                        updateStatusList.add(li);
                    }
                    String message = li.getEmpName() + "[" + li.getEmpNo() + "]:人脸、卡、车辆下发" +  (b ? "成功" : "失败。");
                    generatePowerLogRecord(logList, li, "开通", message, b);
                }
                // 外协权限下发
                if (li.getUserType().equals("WX")) {
                    boolean result = wxPowerUpdate(li, logList,carinfoList);
                    if (result) {
                        // 下发成功，回写状态
                        updateStatusList.add(li);
                    }
                }
            }
        });

        // 7.日志记录
        if (!CollectionUtils.isEmpty(logList)) {
            safetyBuildIssuedLogService.batchInsert(logList);
        }

        // 8.回写状态
        List<ParamDTO> wx = updateStatusList.stream().filter(li -> li.getUserType().equals("WX")).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(wx)) {
            // 回写安保
            List<String> wxEmpNos = wx.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
            safetyOutsiderPromiseChangeService.updateStatus(wxEmpNos);
            // 回写EHR
            List<SafetyOutsiderPromiseDTO> ehrList = new ArrayList<>();
            wx.forEach(li -> {
                SafetyOutsiderPromiseDTO data = new SafetyOutsiderPromiseDTO();
                BeanUtils.copyProperties(li, data);
                data.setFactory(li.getFactoryId());
                data.setServiceEndDate(li.getPowerEndTime());
                ehrList.add(data);
            });
            safetyBaseHandler.sendToEHRForFactory(ehrList);
        }
        // 正式工需要区分注销和下发。
        List<String> zsOpen = updateStatusList.stream().filter(li -> li.getUserType().equals("ZS") && li.getType().equals("0")).map(ParamDTO::getEmpNo).collect(Collectors.toList());
        List<String> zsClose = updateStatusList.stream().filter(li -> li.getUserType().equals("ZS") && li.getType().equals("1")).map(ParamDTO::getEmpNo).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(zsOpen)) {
            safetyParkEmployeePowerInfoService.updateZsStatus(zsOpen, "0");
        }
        if (!CollectionUtils.isEmpty(zsClose)) {
            safetyParkEmployeePowerInfoService.updateZsStatus(zsClose,"1");
        }
    }

    /**
     * 外协人脸下发
     * @param paramDTO 入参
     * @param logList 日志集合
     */
    public boolean wxPowerUpdate(ParamDTO paramDTO, List<SafetyBuildIssuedLogDO> logList,
                                  List<SafetyEmployeePrivateCarDO> carList) {

        boolean faceResult = false;
        boolean cardResult = false;
        boolean carResult = false;

        String empNo = paramDTO.getEmpNo();
        String factoryId = paramDTO.getFactoryId();

        SafetyBuildIssuedLogDO info = new SafetyBuildIssuedLogDO();
        info.setEmployeeCode(empNo);
        info.setEmployeeName(paramDTO.getEmpName());
        info.setEndTime(paramDTO.getPowerEndTime());

        Pair<String, Object> pair;
        if(StringUtils.isNotBlank(paramDTO.getOrgId())){
            pair = Pair.of(paramDTO.getOrgId(), paramDTO.getCurId());
        }else{
            // 获取组织ID
            pair = generalOrgId(paramDTO.getEmpNo());
        }

        // 下发人脸
        StringBuilder message = new StringBuilder();
        message.append(paramDTO.getEmpName()).append("[").append(paramDTO.getEmpNo()).append("]:人脸下发");
        //根据工厂ID 设备类型获取授权设备号
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType","外协");
        paramMap.put("factoryIds",Arrays.asList(factoryId.split(",")));
        paramMap.put("regionName","大门人脸");
        //下发人脸
        try{
            String faceDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            if (StringUtils.isEmpty(faceDevices) && StringUtils.isBlank(paramDTO.getDeviceCode())) {
                LogUtils.info("powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(人脸) \"正式员工\" 授权设备号！",empNo,factoryId);
            } else {
                faceDevices = stringFormat(faceDevices, paramDTO.getDeviceCode());
                try {
                    //更新门禁人脸权限
                    faceResult = updateFaceData(paramDTO, faceDevices);
                }catch (Exception e){
                    LogUtils.error("下发人脸权限失败{}",e);
                }
            }
        }catch (Exception e){
            message.append("失败；---");
        }
        try {
            paramMap.put("regionName", "大门门禁");
            String cardDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            //更新门禁卡权限
            if (StringUtils.isBlank(cardDevices) && StringUtils.isBlank(paramDTO.getDeviceCode())) {
                message.append("下发门禁失败，未获取到设备类型(门禁) 授权设备号；---");
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"wxPowerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"外协\" 授权设备号！\"}",empNo,factoryId);
            } else {
                cardDevices = stringFormat(cardDevices, paramDTO.getDeviceCode());
                // 更新门禁卡权限
                cardResult = safetyBaseHandler.updateCardData(info, cardDevices, "modify",pair);
                message.append("下发门禁成功；---");
            }
        } catch (Exception e) {
            message.append("下发门禁失败；---");
            faceResult = false;
        }

        //下发车辆
        if (CollectionUtils.isEmpty(carList)) {
            return faceResult || cardResult;
        }
        try{
            if(!CollectionUtils.isEmpty(carList)){
                paramMap.put("regionName", "大门车行");
                paramMap.put("empowerType", "外协车辆");
                String carDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
                if (StringUtils.isEmpty(carDevices)) {
                    message.append("下发车辆失败，未获取到大门车行 授权设备号。");
                    LogUtils.info("{\"code\":\"9999\",\"msg\":\"powerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"员工车辆\" 授权设备号！\"}",empNo,factoryId);
                } else {
                    carResult = downCarPower(paramDTO, carDevices, carList,"WX");
                    message.append("下发车辆成功。");
                }
            }
        }catch (Exception e){
            message.append("下发车辆失败；---");
        }
        boolean result = faceResult || cardResult || carResult;
        // 日志
        generatePowerLogRecord(logList, paramDTO, "开通", message.toString(), result);
        return result;
    }

    /**
     * 日志对象
     * @param logList 日志集合
     * @param paramDTO 必要参数
     * @param remark 备注
     */
    public void generatePowerLogRecord(List<SafetyBuildIssuedLogDO> logList, ParamDTO paramDTO, String issuedType, String remark, boolean result) {
        SafetyBuildIssuedLogDO en = new SafetyBuildIssuedLogDO();
        BeanUtils.copyProperties(paramDTO, en);
        en.setCreateTime(new Date());
        en.setBeginTime(DateUtils.format(new Date(),1) + " 00:00:00");
        en.setEmployeeCode(paramDTO.getEmpNo());
        en.setIssuedType(issuedType);
        en.setEndTime(paramDTO.getPowerEndTime());
        en.setEmployeeName(paramDTO.getEmpName());
        en.setRemark(remark);
        en.setDoorId(paramDTO.getFactoryId());
        en.setDoorName(paramDTO.getFactoryName());
        en.setDutyDeptName(paramDTO.getOrgNamePath());
        en.setDutyDeptCode(paramDTO.getOrgCodePath());
        en.setIssuedStatus(result ? "成功" : "失败");
        logList.add(en);
    }

    /**
     * 人员权限注销
     * @param paramDTO 入参
     * @param logList 日志集合
     */
    private boolean logoutPower(ParamDTO paramDTO, List<SafetyBuildIssuedLogDO> logList) {
        String empNo = paramDTO.getEmpNo();
        StringBuffer logInfo = new StringBuffer();
        logInfo.append(paramDTO.getEmpName()).append("[").append(empNo).append("]:人脸注销");
        // 注销 人脸
        boolean faceResult = logoutFaceAndCard(empNo, "5", logInfo);
        // 注销卡
        logInfo.append("卡注销");
        boolean cardResult = logoutFaceAndCard(empNo, "1", logInfo);
        // 注销车
        boolean carResult = logoutCar(empNo, logInfo);
        boolean result = faceResult && cardResult && carResult;
        generatePowerLogRecord(logList, paramDTO,"注销", logInfo.toString(), result);
        return result;
    }

    /**
     * 注销人脸和卡 逻辑
     * @param empNo 员工工号
     * @param type 5：人脸，1：卡
     */
    private boolean logoutFaceAndCard(String empNo, String type, StringBuffer logInfo) {
        try {
            String returnValue = HttpFileUtil.sendPost(doorUrl, "\"StaffNo='" + empNo + "'and TokenType =" + type +" and State=1\"");
            JSONObject retObject = JSONObject.parseObject(returnValue);
            Boolean sucess = false;
            if (!ObjectUtils.isEmpty(retObject.getJSONObject("State")) && retObject.getJSONObject("State").containsKey("IsSucess")) {
                sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
            }
            String redate = DateUtils.format(new Date(), 2);
            boolean bool = true;
            if(sucess){
                JSONArray models = retObject.getJSONArray("Models");
                for (int i = 0; i < models.size(); i++) {
                    JSONObject obj = models.getJSONObject(i);
                    String tokenId = obj.getString("TokenId");
                    String gid = obj.getString("Gid");
                    String param = cardParam.replace("$Token", tokenId).replaceAll("&Gid", gid).replace("$Redate",redate);
                    //注销卡
                    String rest = HttpFileUtil.sendPost(doorUnregistertUrl, param);
                    JSONObject od = JSONObject.parseObject(rest);
                    if (od.getBoolean("IsSucess")){
                        LogUtils.info("需要注销的卡={}, gid={} 注销成功",empNo, gid);
                    }else{
                        bool = false;
                        LogUtils.info("需要注销的卡={}, gid={} 注销失败 原因: {}",empNo, gid,returnValue);
                    }
                }
            }
            // 此处备注没有进行细分
            logInfo.append("成功；---");

            return bool;
        } catch (Exception e) {
            logInfo.append("失败；---");
            return false;
        }
    }

    /**
     * 车辆注销
     * @param empNo 员工号
     */
    private boolean logoutCar(String empNo, StringBuffer logInfo) {
        try {
            String redate = DateUtils.format(new Date(), 2);
            //查询车牌信息 生产地址
            String json = HttpFileUtil.sendPost(parkUrl, "\"TcmType=2 and StaffNo='" + empNo + "'\"");
            //解析返回值获取车牌号
            JSONObject jsonObject = JSONObject.parseObject(json);
            //是否需要注销车权限
            Boolean isSucess = jsonObject.getJSONObject("State").getBoolean("IsSucess");
            boolean bool = true;
            if (isSucess) {
                JSONArray models = jsonObject.getJSONArray("Models");
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(models)) {
                    for (int i = 0; i < models.size(); i++) {
                        //TokenId 车牌号
                        String tokenId = models.getJSONObject(i).getString("TokenId");
                        if (com.yutong.gw.utils.StringUtils.isNotEmpty(tokenId)) {
                            String param = carParam.replace("$Token", tokenId).replace("$Redate", redate);
                            //调用注销逻辑
                            String returnValue = HttpFileUtil.sendPost(parkUnregistertUrl, param);
                            JSONObject data = JSONObject.parseObject(returnValue);
                            if (data.getBoolean("IsSucess")) {
                                LogUtils.info("需要注销的车牌号：" + tokenId + "成功");
                            } else {
                                bool = false;
                                LogUtils.warn("需要注销的车牌号：" + tokenId + "失败" + returnValue);
                            }
                        }
                    }
                }
            }
            logInfo.append("车辆注销成功。");
            return bool;
        } catch (Exception e) {
            logInfo.append("车辆注销失败。");
            return false;
        }
    }

    /**
     * 多个员工获取设备ID信息
     * @param list 集合
     */
    public void getDeviceId(List<ParamDTO> list) {
        // 获取员工所有的门禁权限对应的设备
        // 汇总员工号
        List<String> employeeCodes = list.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        // 根据员工号获取员工所有已开通门禁权限
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setEmpCodeList(employeeCodes);
        listParam.setOrderByClause(" UPDATE_TIME DESC");
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectByExample(listParam);
        // 过滤掉非富士的门禁数据
        employeePowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(employeePowerDOS)) {
            // 员工门禁 数据分组 根据员工号分组
            Map<String, List<SafetyBuildEmployeePowerDO>> dataMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
            // 1.获取所有的厂区名称；
            Map<Integer, String> map = safetyFieldManageService.getFactoryNameList();
            // 赋值
            list.forEach(li -> {
                String empNo = li.getEmpNo();
                // 赋值门禁信息
                List<SafetyBuildEmployeePowerDO> employeePowerDOList = dataMap.get(empNo);
                if (!CollectionUtils.isEmpty(employeePowerDOList)) {
                    // 赋值
                    assignmentValue(employeePowerDOList, li);
                }
                // 赋值厂区名称
                getParkNameBYCode(li, map);
            });
        }
    }

    /**
     * 单个员工获取设备ID信息
     * @param paramDTO 对象
     */
    public void getDeviceId(ParamDTO paramDTO) {
        String empNo = paramDTO.getEmpNo();
        // 根据员工号获取员工所有已开通门禁权限
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setEmployeeCode(empNo);
        listParam.setOrderByClause(" UPDATE_TIME DESC");
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectByExample(listParam);
        // 过滤掉非富士的门禁数据
        employeePowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(employeePowerDOS)) {
            // 赋值
            assignmentValue(employeePowerDOS, paramDTO);
            // 1.获取所有的厂区名称；
            Map<Integer, String> map = safetyFieldManageService.getFactoryNameList();
            getParkNameBYCode(paramDTO, map);
        }
    }


    /**
     * 给对象赋值， 设备ID、设备名称、创建人、流程ID
     * @param employeePowerDOS 取值的集合
     * @param paramDTO 被赋值的对象
     */
    private void assignmentValue(List<SafetyBuildEmployeePowerDO> employeePowerDOS, ParamDTO paramDTO) {
        // 获取设备ID、设备名称、操作人、流程ID、
        List<String> deviceCodes = new ArrayList<>();
        List<String> deviceNames = new ArrayList<>();
        List<String> createByList = new ArrayList<>();
        List<String> bpmFlowIdList = new ArrayList<>();
        employeePowerDOS.forEach(li -> {
            checkStringDistinct(li.getDeviceCode(), deviceCodes);
            checkStringDistinct(li.getDeviceName(), deviceNames);
            checkStringDistinct(li.getCreateBy(), createByList);
            checkStringDistinct(li.getBpmFlowId(), bpmFlowIdList);
        });
        // 赋值
        paramDTO.setDeviceCode(String.join(",", deviceCodes));
        paramDTO.setDeviceName(String.join(",", deviceNames));
        paramDTO.setCreateBy(listByEightToString(createByList));
        paramDTO.setBpmFlowId(listByEightToString(bpmFlowIdList));
    }

    /**
     * 把字符串放在List中，并去重
     * @param value 字符串
     * @param list List数组
     */
    private void checkStringDistinct(String value, List<String> list) {
        if (StringUtils.isNotBlank(value) && !list.contains(value)) {
            list.add(value);
        }
    }

    /**
     * List转字符串， 并限制只需要8个值
     * @param list List数组
     * @return 返回字符串值
     */
    private String listByEightToString(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        if (list.size() <= 8) {
            return String.join(",", list);
        }
        return String.join(",", list.subList(0, 7));
    }

    /**
     * 字符串去重拼接
     */
    private String stringFormat(String value1, String value2) {
        if (StringUtils.isBlank(value1)) {
            return value2;
        }
        if (StringUtils.isBlank(value2)) {
            return value1;
        }
        List<String> list1 = Arrays.asList(value1.split(","));
        List<String> list2 = Arrays.asList(value2.split(","));
        Set<String> result = new HashSet<>();
        result.addAll(list1);
        result.addAll(list2);
        return String.join(",", result);
    }

    /**
     * 根据厂区编码获取厂区名称
     * @param paramDTO 赋值对象
     */
    private void getParkNameBYCode(ParamDTO paramDTO, Map<Integer, String> map) {
        try {
            // 厂区编码不为空并且厂区名称为空的时候才做处理
            String factoryId = paramDTO.getFactoryId();
            if (StringUtils.isNotBlank(factoryId)) {
                // 厂区编码字符串转List
                List<String> list = Arrays.asList(factoryId.split(","));
                List<String> nameList = new ArrayList<>();
                // 根据字符串取出对应的厂区编码
                list.forEach(li -> {
                    String name = map.get(Integer.parseInt(li));
                    if (StringUtils.isNotBlank(name)) {
                        nameList.add(name);
                    }
                });
                // 赋值
                paramDTO.setFactoryName(String.join(",", nameList));
            }
        } catch (Exception e) {
            LogUtils.info("getParkNameBYCode:员工号：{}，厂区编码：{}，，根据厂区编码获取厂区名称异常！", paramDTO.getEmpNo(), map.keySet());
        }
    }

    /**
     * 关闭到期车辆权限
     * @param carNos 车牌号
     */
    public Object autoCloseCarPower(String carNos){
        Set<String> carNoSet;
        if(StringUtils.isBlank(carNos)){
            //默认<T-1为到期
            String severEndDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
            SafetyEmployeePrivateCarDO param = new SafetyEmployeePrivateCarDO();
            param.setServerEndDate(severEndDate);
            param.setDeleteFlag("0"); //只查询未删除的
            List<SafetyEmployeePrivateCarDO> carList = safetyEmployeePrivateCarHandler.selectList(param);
            if(CollectionUtils.isEmpty(carList)){
                return null;
            }
            carNoSet = carList.stream().map(SafetyEmployeePrivateCarDO::getCarNo).collect(Collectors.toSet());
        }else{
            carNoSet = new HashSet<>(Arrays.asList(carNos.split(",")));
        }
        if(carNoSet.isEmpty()){
            return null;
        }

        List<SafetyKeyValueDto> list = new ArrayList<>();
        for(String carNo:carNoSet){
            SafetyKeyValueDto newDto = new SafetyKeyValueDto();
            newDto.setKey(carNo);
            list.add(newDto);
        }
        //调用出入口接口注销车辆权限
        safetyBaseHandler.carNoLogOut(list);
        //标记对应车辆信息为删除状态
        List<String> stringList = new ArrayList<>(carNoSet);  // 基础转换
        safetyEmployeePrivateCarHandler.deleteCarForCarNo(stringList);

        return null;
    }

}
