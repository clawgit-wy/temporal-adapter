package com.yutong.modules.risk.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.api.*;
import com.yutong.modules.risk.convert.SafetyHazardReportConvert;
import com.yutong.modules.risk.dto.*;
import com.yutong.modules.risk.entity.*;
import com.yutong.modules.risk.enums.HazardReportConstant;
import com.yutong.modules.risk.enums.HazardReportStatus;
import com.yutong.modules.risk.enums.ProblemSourceEnum;
import com.yutong.modules.risk.mapper.YTSafetyHazardReportMapper;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class YTSafetyHazardReportServiceImpl extends ServiceImpl<YTSafetyHazardReportMapper, YTSafetyHazardReport> implements IYTSafetyHazardReportService {

    @Autowired
    private IYTSafetyHazardDescriptionService safetyHazardDescriptionService;

    @Autowired
    private IYTSafetyHazardResultService safetyHazardResultService;

    @Autowired
    private IYTSafetyHazardDelayApplyService safetyHazardDelayApplyService;

    @Autowired
    private IYTSafetyHazardReportHistoryService safetyHazardReportHistoryService;

    @Autowired
    private YTSafetyHazardReportMapper baseMapper;

    @Autowired
    private SafetyHazardReportConvert safetyHazardReportConvert;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private ISafetyGroupInfoService safetyGroupInfoService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    private FileService fileService;

    @Autowired
    private IYTSafetyHazardReportNodeRoleService safetyHazardReportNodeRoleService;

    @Autowired
    YTSafetyWaterFlowNoService ytSafetyWaterFlowNoService;

    @Autowired
    YTSafetyTaskService ytSafetyTaskService;

    @Autowired
    IYtsafetyLocationInfoService safetyLocationInfoService;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    private static final String MODEL_ID = "hazard_report_process";

    @Value("${ytsafety.process.config.nickAccount}")
    private String nickAccount;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YTSafetyHazardReport submitReport(YTSafetyHazardReportDTO reportDTO) {
        Long reportId = reportDTO.getId();
        if (ProblemSourceEnum.EMPLOYEE_PHOTO.getVale().equals(reportDTO.getProblemSource())){
            reportDTO.setIsImmediateHandle(0);
            reportDTO.setRectifyPersonEmpNo("");
        }else {
            if (reportDTO.getIsImmediateHandle() == null){
                throw new IllegalArgumentException("是否当场处理必填！");
            }
        }
        YTSafetyHazardReport report = new YTSafetyHazardReport();
        BeanUtils.copyProperties(reportDTO,report);
        LoginUser user = BusUtits.getLoginUser();
        if (reportDTO.getModelFlag() == null || reportDTO.getModelFlag().isEmpty()) {
            reportDTO.setModelFlag(MODEL_ID);
        }
        boolean isNew = reportId == null;
        if (isNew) {
            fillCommonFields(report, reportDTO, user, HazardReportStatus.APPROVING.getCode(), 0, true);
            setrecordCirculation(report);
            setReviewPerson(report, user);
            userDispose(report);
            report.setFlowCode(ytSafetyWaterFlowNoService.getNowByTypeDay("YHSB",null));
            if (report.getHazardCategory() == 1){
                report.setHazardCode(ytSafetyWaterFlowNoService.getNowByTypeDay("XFYH",null));
            }else if (report.getHazardCategory() == 2){
                report.setHazardCode(ytSafetyWaterFlowNoService.getNowByTypeDay("ZAYH",null));
            }
            save(report);
        } else {
            report = getById(reportId);
            if (ObjectUtil.isEmpty(report)) {
                throw new IllegalArgumentException("提交的草稿不存在！");
            }
            if (report.getStatus() != HazardReportStatus.DRAW_UP.getCode()){
                if (!report.getDraftStatus().equals(HazardReportStatus.APPROVING.getCode()) ) {
                    throw new IllegalArgumentException("非草稿状态数据不可提交！");
                }
            }

            fillCommonFields(report, reportDTO, user, HazardReportStatus.APPROVING.getCode(), 0, false);
            setrecordCirculation(report);
            setReviewPerson(report, user);
            userDispose(report);
            if(!StringUtils.isNotEmpty(report.getFlowCode())){
                report.setFlowCode(ytSafetyWaterFlowNoService.getNowByTypeDay("YHSB",null));
                if (report.getHazardCategory() == 1){
                    report.setHazardCode(ytSafetyWaterFlowNoService.getNowByTypeDay("XFYH",null));
                }else if (report.getHazardCategory() == 2){
                    report.setHazardCode(ytSafetyWaterFlowNoService.getNowByTypeDay("ZAYH",null));
                }
            }
            updateById(report);
            safetyHazardDescriptionService.clearBYHazardReportId(reportId);
            safetyHazardResultService.clearBYHazardReportId(reportId);
        }

        Long finalReportId = report.getId();
        List<YTSafetyHazardDescriptionDTO> descriptionList = reportDTO.getDescriptionList();
        if (descriptionList != null && !descriptionList.isEmpty()) {
            safetyHazardDescriptionService.batchAdd(finalReportId, descriptionList);
        }
        if (Integer.valueOf(1).equals(reportDTO.getIsImmediateHandle())) {
            List<YTSafetyHazardResultDTO> resultList = reportDTO.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                safetyHazardResultService.batchAdd(finalReportId, resultList);
            }
        }
        if (report.getTaskAccountId() != null) {
            try {
                ytSafetyTaskService.updateTaskExecutionIsAbnormal(report.getTaskAccountId());
            } catch (Exception e) {
                log.error("更新任务执行异常", e);
            }
        }
        submitProcess(report);
        try {
            Long locationId = report.getLocationId();
            if (locationId != null){
                safetyLocationInfoService.syncLocationRiskLevel(Collections.singletonList(locationId));
            }
        } catch (Exception e) {
            log.error("同步位置风险等级失败", e);
        }

        return report;
    }

    //处理治安人员匿名
    private String getNickName(){
        String nickName = "治安巡查上报";
        SafetyEmployeeMessage safetyEmployeeMessage = safetyCommonService.querySafetyEmployeeMessageByEmpLogin(nickAccount);
        if (safetyEmployeeMessage != null) {
            nickName = safetyEmployeeMessage.getEmpName();
        }
        return nickName;
    }


    /**
     * 处理备案节点人员
     * @param report
     */
    private void setrecordCirculation(YTSafetyHazardReport report){
        report.setRecordCirculationEmpNo("");
        ArrayList<YtsafetySysResponsibleVO> responsibleList = new ArrayList<>();

        // 查询需要的人员配置
        YTSafetyHazardReportNodeRole queryParam = new YTSafetyHazardReportNodeRole();
        queryParam.setNodeType(HazardReportConstant.NodeType.RECORD);
        queryParam.setModelFlag(MODEL_ID);
        queryParam.setHazardCategory(report.getHazardCategory());
        queryParam.setProblemSource(report.getProblemSource());
        queryParam.setProblemLevel(report.getProblemLevel());
        queryParam.setPriority(HazardReportConstant.PriorityType.ONE);
        List<YTSafetyHazardReportNodeRole> nodeRoles = safetyHazardReportNodeRoleService.queryList(queryParam);

        if (nodeRoles == null || nodeRoles.isEmpty()) {
            return;
        }

        for (YTSafetyHazardReportNodeRole nodeRole : nodeRoles) {
            String roleCode = nodeRole.getRoleCode();
            if (StringUtils.isBlank(roleCode)) {
                continue;
            }
            List<String> roleCodeList = Arrays.asList(roleCode.split(","));
            String scopeType = nodeRole.getScopeType();

            if (HazardReportConstant.ScopeType.DEPT.equals(scopeType)) {
                SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                if (org == null) {
                    continue;
                }
                List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByOrgCodeHierarchy(org.getCode(), roleCodeList);
                if (list != null) {
                    responsibleList.addAll(list);
                }
            } else if (HazardReportConstant.ScopeType.DEPT_FACTORY.equals(scopeType)) {
                SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                if (org == null) {
                    continue;
                }
                List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactoryAndDepart(org.getCode(), report.getPlantId(), roleCodeList);
                if (list != null) {
                    responsibleList.addAll(list);
                }
            } else if (HazardReportConstant.ScopeType.FACTORY.equals(scopeType)) {
                List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactory(report.getPlantId(), roleCodeList);
                if (list != null) {
                    responsibleList.addAll(list);
                }
            }
        }

        String userNames = responsibleList.stream()
                .map(YtsafetySysResponsibleVO::getUserName)
                .distinct()
                .collect(Collectors.joining(","));
        report.setRecordCirculationEmpNo(userNames);
    }

    private void setReviewPerson(YTSafetyHazardReport report, LoginUser user) {
        // 审核节点审核人员
        // 查询需要的人员配置（优先级一）
        YTSafetyHazardReportNodeRole queryParam = new YTSafetyHazardReportNodeRole();
        queryParam.setNodeType(HazardReportConstant.NodeType.CHECK);
        queryParam.setModelFlag(MODEL_ID);
        queryParam.setHazardCategory(HazardReportConstant.HazardCategoryType.UNIVERSAL);
        queryParam.setProblemSource(HazardReportConstant.ProblemSourceType.UNIVERSAL);
        queryParam.setProblemLevel(HazardReportConstant.ProblemLevelType.UNIVERSAL);
        queryParam.setPriority(HazardReportConstant.PriorityType.ONE);
        List<YTSafetyHazardReportNodeRole> nodeRoles = safetyHazardReportNodeRoleService.queryList(queryParam);

        List<String> roleCodeList = extractRoleCodes(nodeRoles);
        List<YtsafetySysResponsibleVO> responsibleVOList = Collections.emptyList();

        if (!roleCodeList.isEmpty()) {
            responsibleVOList = safetyCommonService.queryResponsibleByOrgCodeHierarchy(user.getOrgCode(), roleCodeList);
        }

        // 优先级一为空则查优先级二（向上递归查询父级组织）
        if (responsibleVOList.isEmpty()) {
            queryParam.setPriority(HazardReportConstant.PriorityType.TWO);
            nodeRoles = safetyHazardReportNodeRoleService.queryList(queryParam);
            roleCodeList = extractRoleCodes(nodeRoles);
            responsibleVOList = safetyCommonService.queryResponsibleByOrgCodeHierarchy(user.getOrgCode(), roleCodeList);
        }
        String userNames = responsibleVOList.stream()
                .map(YtsafetySysResponsibleVO::getUserName)
                .collect(Collectors.joining(","));
        String empNames = responsibleVOList.stream()
                .map(YtsafetySysResponsibleVO::getEmpName)
                .collect(Collectors.joining(","));

        report.setSuperApproval(userNames);
        report.setSuperApprovalName(empNames);
        report.setReviewPersonEmpNo(userNames);
        report.setReviewPersonEmpName(empNames);
    }

    private List<String> extractRoleCodes(List<YTSafetyHazardReportNodeRole> nodeRoles) {
        if (nodeRoles == null || nodeRoles.isEmpty()) {
            return Collections.emptyList();
        }
        return nodeRoles.stream()
                .map(YTSafetyHazardReportNodeRole::getRoleCode)
                .filter(StringUtils::isNotBlank)
                .flatMap(code -> Arrays.stream(code.split(",")))
                .distinct()
                .collect(Collectors.toList());
    }

    private void userDispose(YTSafetyHazardReport report){
        //抄送人
        if (report.getCcPersonEmpNo() != null && !report.getCcPersonEmpNo().isEmpty()){
            List<String> ccPersonEmpNos = Arrays.asList(report.getCcPersonEmpNo().split(","));
            List<LoginUser> userList = safetySysUserService.queryUserListByEmpNos(ccPersonEmpNos);
            String ccPersonEipNames = userList.stream()
                    .map(LoginUser::getEipName)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(","));
            report.setCcPersonEmpUser(ccPersonEipNames);
        }

        if (report.getRectifyPersonEmpNo() != null && !report.getRectifyPersonEmpNo().isEmpty()){
            List<String> empNos = Arrays.asList(report.getRectifyPersonEmpNo().split(","));
            List<LoginUser> userList = safetySysUserService.queryUserListByEmpNos(empNos);
            String eipNames = userList.stream()
                    .map(LoginUser::getEipName)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(","));
            //整改责任人
            report.setRectifyPersonEmpUser(eipNames);
        }

        //验证一人员处理
        setVerifyOneUser(report);
        //验证二人员处理
        setVerifyTwoUser(report);
        //延期审核
        setDelayCheck(report);
    }

    private void setDelayCheck(YTSafetyHazardReport report) {
        if (ProblemSourceEnum.COMPANIES_INSPECTOR.getVale().equals(report.getProblemSource())) {
            // 公司检查员的延期审批人：发起人
            LoginUser applyUser = safetySysUserService.queryUser(report.getApplyNo());
            if (applyUser != null) {
                report.setDelayCheckUser(applyUser.getEipName());
            }
        } else {
            // 非公司检查员的延期审批人：审核人员
            report.setDelayCheckUser(report.getReviewPersonEmpNo());
        }
    }

    private void setVerifyOneUser(YTSafetyHazardReport report){
        if (report.getHazardCategory() != null && report.getHazardCategory() == 2){
            report.setVerifyOneUser(nickAccount);
            return;
        }
        List<YtsafetySysResponsibleVO> verifyUserList = new ArrayList<>();
        LoginUser applyUser = safetySysUserService.queryUser(report.getApplyNo());
        if (applyUser != null) {
            report.setVerifyOneUser(applyUser.getEipName());
        }

        if (report.getHazardCategory() == 1 && ProblemSourceEnum.COMPANIES_INSPECTOR.getVale().equals(report.getProblemSource())) {
            YTSafetyHazardReportNodeRole queryParam = new YTSafetyHazardReportNodeRole();
            queryParam.setNodeType(HazardReportConstant.NodeType.VERIFY_ONE);
            queryParam.setModelFlag(HazardReportConstant.MODEL_ID);
            queryParam.setHazardCategory(report.getHazardCategory());
            queryParam.setProblemSource(report.getProblemSource());
            queryParam.setProblemLevel(HazardReportConstant.ProblemLevelType.UNIVERSAL);
            queryParam.setPriority(HazardReportConstant.PriorityType.ONE);
            List<YTSafetyHazardReportNodeRole> nodeRoles = safetyHazardReportNodeRoleService.queryList(queryParam);
            if (nodeRoles != null && !nodeRoles.isEmpty()) {
                for (YTSafetyHazardReportNodeRole nodeRole : nodeRoles) {
                    String scopeType = nodeRole.getScopeType();
                    String roleCode = nodeRole.getRoleCode();
                    if (StringUtils.isBlank(scopeType) || StringUtils.isBlank(roleCode)) {
                        continue;
                    }
                    List<String> roleCodeList = Arrays.asList(roleCode.split(","));
                    if (HazardReportConstant.ScopeType.DEPT.equals(scopeType)) {
                        SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                        if (org == null) continue;
                        List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByOrgCodeHierarchy(org.getCode(), roleCodeList);
                        verifyUserList.addAll(list);
                    } else if (HazardReportConstant.ScopeType.DEPT_FACTORY.equals(scopeType)) {
                        SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                        if (org == null) continue;
                        List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactoryAndDepart(org.getCode(), report.getPlantId(), roleCodeList);
                        verifyUserList.addAll(list);
                    } else if (HazardReportConstant.ScopeType.FACTORY.equals(scopeType)) {
                        List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactory(report.getPlantId(), roleCodeList);
                        verifyUserList.addAll(list);
                    }
                }
            }
            HashMap<String, String> userManage = userManage(verifyUserList);
            report.setVerifyOneUser(userManage.get("USER"));
        }
    }

    private void setVerifyTwoUser(YTSafetyHazardReport report){
        List<YtsafetySysResponsibleVO> verifyTwoUserList = new ArrayList<>();

        YTSafetyHazardReportNodeRole queryParam = new YTSafetyHazardReportNodeRole();
        queryParam.setNodeType(HazardReportConstant.NodeType.VERIFY_TWO);
        queryParam.setModelFlag(HazardReportConstant.MODEL_ID);
        queryParam.setHazardCategory(report.getHazardCategory());
        queryParam.setProblemSource(report.getProblemSource());
        queryParam.setProblemLevel(report.getProblemLevel());
        queryParam.setPriority(HazardReportConstant.PriorityType.ONE);
        List<YTSafetyHazardReportNodeRole> nodeRoles = safetyHazardReportNodeRoleService.queryList(queryParam);

        if (nodeRoles != null && !nodeRoles.isEmpty()) {
            for (YTSafetyHazardReportNodeRole nodeRole : nodeRoles) {
                String scopeType = nodeRole.getScopeType();
                String roleCode = nodeRole.getRoleCode();
                if (StringUtils.isBlank(scopeType) || StringUtils.isBlank(roleCode)) {
                    continue;
                }

                List<String> roleCodeList = Arrays.asList(roleCode.split(","));
                if (HazardReportConstant.ScopeType.DEPT.equals(scopeType)) {
                    SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                    if (org != null) {
                        List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByOrgCodeHierarchy(org.getCode(), roleCodeList);
                        verifyTwoUserList.addAll(list);
                    }
                } else if (HazardReportConstant.ScopeType.DEPT_FACTORY.equals(scopeType)) {
                    SafetyGroupInfo org = safetyGroupInfoService.getById(report.getOrgId());
                    if (org != null) {
                        List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactoryAndDepart(org.getCode(), report.getPlantId(), roleCodeList);
                        verifyTwoUserList.addAll(list);
                    }
                } else if (HazardReportConstant.ScopeType.FACTORY.equals(scopeType)) {
                    List<YtsafetySysResponsibleVO> list = safetyCommonService.queryResponsibleByFactory(report.getPlantId(), roleCodeList);
                    verifyTwoUserList.addAll(list);
                }
            }
        }
        HashMap<String, String> userManage = userManage(verifyTwoUserList);
        report.setVerifyTwoUser(userManage.get("USER"));
    }

    private HashMap<String,String> userManage(List<YtsafetySysResponsibleVO> list) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        String user = list.stream()
                .map(YtsafetySysResponsibleVO::getUserName)
                .distinct()
                .collect(Collectors.joining(","));
        String userName = list.stream()
                .map(YtsafetySysResponsibleVO::getEmpName)
                .distinct()
                .collect(Collectors.joining(","));

        HashMap<String, String> map = new HashMap<>();
        map.put("USER",user);
        map.put("USER_NAME",userName);
        return map;
    }

    private void fillCommonFields(YTSafetyHazardReport report, YTSafetyHazardReportDTO reportDTO, LoginUser user,
                                   int status, int draftStatus, boolean isNew) {
        safetyHazardReportConvert.dtoToEntity(reportDTO, report);
        report.setStatus(status);
        report.setDraftStatus(draftStatus);
        if (isNew) {
            report.setId(IdWorker.getId());
        }
        report.setApplyDepartCode(user.getOrgCode());
        report.setApplyDepartName(user.getOrgName());
        report.setApplyName(user.getRealname());
        report.setApplyNo(user.getEmpNo());
        report.setFlowSubject(buildFlowSubject(reportDTO, user));
        report.setSubmitType(1);
        report.setHandlerType("all");
        report.setRectifyDelayTime(report.getRectifyDeadline());
        report.setOrgPathName(safetyCommonService.getOrgPathName(report.getOrgId()));
        if (report.getProblemLevel() == null) {
            report.setProblemLevel(2);
        }
    }

    @Async
    private void submitProcess(YTSafetyHazardReport report){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginUser user = BusUtits.getLoginUser();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(report.getModelFlag());
        dto.setFormInstanceId(report.getId());
        dto.setFlowCode(report.getFlowCode());
        dto.setFlowSubject(report.getFlowSubject());
        dto.setCurNodeHandler(report.getSuperApproval());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setLoginName(user.getEipName());
        dto.setApplyNo(user.getEmpNo());
        dto.setPreNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        if (report.getHazardCategory() != null && report.getHazardCategory() == 2){
            dto.setIsNickname("1");
            dto.setNickName(getNickName());
        }
        safetySummaryProcessService.createAndApproveProcess(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YTSafetyHazardReport TSDraft(YTSafetyHazardReportDTO reportDTO) {
        Long reportId = reportDTO.getId();
        YTSafetyHazardReport report = new YTSafetyHazardReport();
        BeanUtils.copyProperties(reportDTO,report);
        LoginUser user = BusUtits.getLoginUser();
        if (reportDTO.getModelFlag() == null || reportDTO.getModelFlag().isEmpty()) {
            reportDTO.setModelFlag(MODEL_ID);
        }
        setReviewPerson(report, user);

        boolean isNew = reportId == null;
        if (isNew) {
            fillCommonFields(report, reportDTO, user, HazardReportStatus.DRAFT.getCode(), 1, true);
            save(report);
        } else {
            report = getById(reportId);
            if (ObjectUtil.isEmpty(report)) {
                throw new IllegalArgumentException("提交的草稿不存在！");
            }
            if (!report.getDraftStatus().equals(1)) {
                throw new IllegalArgumentException("非草稿状态数据不可保存到草稿！");
            }
            fillCommonFields(report, reportDTO, user, HazardReportStatus.DRAFT.getCode(), 1, false);
            updateById(report);
            safetyHazardDescriptionService.clearBYHazardReportId(reportId);
            safetyHazardResultService.clearBYHazardReportId(reportId);
        }

        Long finalReportId = report.getId();
        List<YTSafetyHazardDescriptionDTO> descriptionList = reportDTO.getDescriptionList();
        if (descriptionList != null && !descriptionList.isEmpty()) {
            safetyHazardDescriptionService.batchAdd(finalReportId, descriptionList);
        }
        if (Integer.valueOf(1).equals(reportDTO.getIsImmediateHandle())) {
            List<YTSafetyHazardResultDTO> resultList = reportDTO.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                safetyHazardResultService.batchAdd(finalReportId, resultList);
            }
        }
        return report;
    }

    @Override
    public IPage<YTSafetyHazardReportListVO> historyList(Integer pageNo, Integer pageSize, String screeningCondition) {
        LoginUser loginUser =  BusUtits.getLoginUser();
        if (loginUser == null || loginUser.getEmpNo() == null){
            return null;
        }
        Page<YTSafetyHazardReportListVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.historyList(page, loginUser.getEmpNo(), screeningCondition);
    }

    @Override
    public IPage<YTSafetyHazardReportListVO> getFactoryHazardList(Integer pageNo, Integer pageSize, String screeningCondition) {
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser == null || loginUser.getEmpNo() == null) {
            return new Page<>();
        }
        Boolean b = safetyCommonService.checkIsResponsible(loginUser.getEmpNo(),Arrays.asList(roleCodeConstant.getSecurityAdministrator().split(",")));
        if (!b){
            return new Page<>();
        }
        //获取授权组织
        List<Long> factoryList = safetyCommonService.getAuthorizedFactoryIds(loginUser.getEmpNo());
        if (factoryList == null || factoryList.isEmpty()) {
            return new Page<>();
        }
        Page<YTSafetyHazardReportListVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.getFactoryHazardList(page, factoryList, screeningCondition);
    }

    @Override
    public IPage<YTSafetyHazardReportListVO> getDraftList(Integer pageNo, Integer pageSize) {
        LoginUser loginUser =  BusUtits.getLoginUser();
        if (loginUser == null || loginUser.getEmpNo() == null){
            return null;
        }
        Page<YTSafetyHazardReportListVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.getDraftList(page, loginUser.getEmpNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteDraftBatch(List<String> ids) {
        List<YTSafetyHazardReport> records = this.listByIds(ids);
        List<String> draftIds = new ArrayList<>();
        List<String> nonDraftIds = new ArrayList<>();
        for (YTSafetyHazardReport record : records) {
            if (HazardReportStatus.DRAFT.getCode() == record.getStatus()) {
                draftIds.add(String.valueOf(record.getId()));
                safetyHazardDescriptionService.clearBYHazardReportId(record.getId());
                safetyHazardResultService.clearBYHazardReportId(record.getId());
            } else {
                nonDraftIds.add(String.valueOf(record.getId()));
            }
        }
        if (!draftIds.isEmpty()) {
            this.getBaseMapper().deleteBatchIds(draftIds);
        }
        if (!nonDraftIds.isEmpty()) {
            return Result.error("以下记录已提交，无法删除：" + String.join(",", nonDraftIds));
        }
        return Result.OK("删除成功！");
    }

    @Override
    public YTSafetyHazardReportVO getReportDetail(Long id) {
        LoginUser user = BusUtits.getLoginUser();
        YTSafetyHazardReportVO ytSafetyHazardReportVO = baseMapper.getByIdDetail(id);
        if (ytSafetyHazardReportVO == null) {
            throw new IllegalArgumentException("记录不存在");
        }

        if (ytSafetyHazardReportVO.getRectifyPersonEmpNo() != null){
            List<String> empNos = Arrays.asList(ytSafetyHazardReportVO.getRectifyPersonEmpNo().split(","));
            List<LoginUser> userList = safetySysUserService.queryUserListByEmpNos(empNos);
            String userName = userList.stream()
                    .map(LoginUser::getRealname)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(","));
            ytSafetyHazardReportVO.setRectifyPersonName(userName);
        }
        if (ytSafetyHazardReportVO.getCcPersonEmpNo() != null){
            List<String> empNos = Arrays.asList(ytSafetyHazardReportVO.getCcPersonEmpNo().split(","));
            List<LoginUser> userList = safetySysUserService.queryUserListByEmpNos(empNos);
            String userName = userList.stream()
                    .map(LoginUser::getRealname)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(","));
            ytSafetyHazardReportVO.setCcPersonName(userName);
        }
        if (ytSafetyHazardReportVO.getRectifyDelayTime() != null){
            ytSafetyHazardReportVO.setRectifyDeadline(ytSafetyHazardReportVO.getRectifyDelayTime());
        }

        // 查询问题描述列表
        List<YTSafetyHazardDescriptionVO> descriptionVOList = safetyHazardDescriptionService.listBYHazardReportId(id);
        ytSafetyHazardReportVO.setDescriptionList(descriptionVOList);

        // 查询处理结果列表
        List<YTSafetyHazardResultVO> resultVOList = safetyHazardResultService.listBYHazardReportId(id);
        List<YTSafetyHazardResultVO> hazardResultVOS = resultVOList.stream().map(r -> {
            LoginUser applyUser = safetySysUserService.queryUser(r.getHandlerUserEmpNo());
            if (applyUser != null) {
                r.setHandlerUserEmpName(applyUser.getRealname());
            }
            return r;
        }).collect(Collectors.toList());
        ytSafetyHazardReportVO.setResultList(hazardResultVOS);

        // 查询延期申请列表
        List<YTSafetyHazardDelayApplyVO> delayVOList = safetyHazardDelayApplyService.listBYHazardReportId(id);
        ytSafetyHazardReportVO.setDelayApplyList(delayVOList);

        if (ytSafetyHazardReportVO.getDraftStatus().equals(0)){
            BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(Long.valueOf(ytSafetyHazardReportVO.getId()),user.getEipName());
            if (detailsVO != null){
                ytSafetyHazardReportVO.setIsCanApprove(detailsVO.getIsCanApprove());
                ytSafetyHazardReportVO.setIsCanEdit(detailsVO.getIsCanEdit());
                ytSafetyHazardReportVO.setFlowId(detailsVO.getFlowId());
                ytSafetyHazardReportVO.setTemporaryToken(detailsVO.getTemporaryToken());
                ytSafetyHazardReportVO.setLRToken(detailsVO.getLRToken());
            }
        }
        if (HazardReportStatus.APPROVING.getCode() == ytSafetyHazardReportVO.getStatus()) {
            String username = user.getUsername();
            String reviewPersonEmpNo = ytSafetyHazardReportVO.getReviewPersonEmpNo();
            boolean canEdit = false;
            if (username != null && reviewPersonEmpNo != null) {
                Set<String> allowedSet = new HashSet<>(Arrays.asList(reviewPersonEmpNo.split(",")));
                canEdit = allowedSet.contains(username);
            }
            if (!canEdit){
                if (username != null && reviewPersonEmpNo != null) {
                    Set<String> allowedSet = new HashSet<>(Arrays.asList(reviewPersonEmpNo.split(",")));
                    canEdit = allowedSet.contains(username);
                }
            }
            ytSafetyHazardReportVO.setIsCanEdit(canEdit);
        }

        ytSafetyHazardReportVO.setIsCanSubmitPartRectification(false);
        ytSafetyHazardReportVO.setIsCanDelayApply(false);
        if (HazardReportStatus.RECTIFYING.getCode() == ytSafetyHazardReportVO.getStatus() && ytSafetyHazardReportVO.getRectifyPersonEmpNo() != null && ytSafetyHazardReportVO.getRectifyPersonEmpNo().equals(user.getEmpNo())) {
            //判断是否有部分整改提交权限
            ytSafetyHazardReportVO.setIsCanSubmitPartRectification(isCanSubmitPartRectification(id,"part"));
            //判断是否有延期申请权限
            ytSafetyHazardReportVO.setIsCanDelayApply(isCanDelayApply(id));
        }

        // 查询历史记录列表
        List<YTSafetyHazardReportHistoryVO> historyVOList = safetyHazardReportHistoryService.listBYHazardReportId(id);
        // 解析历史记录，填充责任部门和整改时间的展示字段
        parseHistoryDisplayFields(historyVOList);
        ytSafetyHazardReportVO.setReportHistoryList(historyVOList);

        return ytSafetyHazardReportVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewReport(YTSafetyHazardReportReviewDTO reportReviewDTO) {
        LoginUser loginUser =  BusUtits.getLoginUser();
        if (loginUser == null || loginUser.getEmpNo() == null){
            return;
        }
        Long reportId = reportReviewDTO.getId();
        YTSafetyHazardReport report = getById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        if (report.getIsImmediateHandle() != null && report.getIsImmediateHandle().equals(1)) {
            throw new IllegalArgumentException("数据异常，不可操作");
        }
        if (report.getStatus() != HazardReportStatus.APPROVING.getCode()) {
            throw new IllegalArgumentException("当前状态不允许审核操作");
        }
        if (reportReviewDTO.getIsImmediateHandle() == 0){
            if (StringUtils.isEmpty(reportReviewDTO.getRectifyPersonEmpNo())){
                throw new IllegalArgumentException("整改责任人必填");
            }
            if (reportReviewDTO.getRectifyDeadline() == null){
                throw new IllegalArgumentException("整改要求完成时间必填");
            }
        }
        // 记录修改前快照
        String beforeSnapshot = JSON.toJSONString(report);
        // 应用修改（隐患类别不可修改）
        BeanUtils.copyProperties(reportReviewDTO, report, "hazardCategory");
        safetyHazardDescriptionService.clearBYHazardReportId(reportId);
        List<YTSafetyHazardDescriptionDTO> descriptionList = reportReviewDTO.getDescriptionList();
        if (descriptionList != null && !descriptionList.isEmpty()) {
            safetyHazardDescriptionService.batchAdd(reportId, descriptionList);
        }
        if (Integer.valueOf(1).equals(reportReviewDTO.getIsImmediateHandle())) {
            List<YTSafetyHazardResultDTO> resultList = reportReviewDTO.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                safetyHazardResultService.batchAdd(reportId, resultList);
            }
        }
        setrecordCirculation(report);
        userDispose(report);
        report.setRectifyDelayTime(report.getRectifyDeadline());
        report.setOrgPathName(safetyCommonService.getOrgPathName(report.getOrgId()));
        updateById(report);
        // 记录历史快照
        recordHistory(reportId, loginUser.getEmpNo(), loginUser.getEipName(), beforeSnapshot);
        try {
            Long locationId = report.getLocationId();
            if (locationId != null){
                safetyLocationInfoService.syncLocationRiskLevel(Collections.singletonList(locationId));
            }
        } catch (Exception e) {
            log.error("同步位置风险等级失败", e);
        }
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        List<FieldAuth> fieldAuths = safetySummaryProcessService.queryFieldAuth(processParamDTO.getFormInstanceId());
        LoginUser user = BusUtits.getLoginUser();
        processParamDTO.setLoginName(user.getEipName());
        processParamDTO.setNodeHandler(user.getEipName());
        processParamDTO.setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        BpmReponseParamsVO bpmReponseParamsVO = safetySummaryProcessService.executeProcess(processParamDTO);
        nodeApprovalStatusManage(bpmReponseParamsVO,fieldAuths);
        return bpmReponseParamsVO;
    }

//    private void nodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
//        String nodeId = bpmReponseParamsVO.getNodeId();
//        if ("N5".equals(nodeId))
//            drawUpNodeApprovalStatusManage(bpmReponseParamsVO);
//        if ("N9".equals(nodeId))
//            delayCheckNodeApprovalStatusManage(bpmReponseParamsVO);
//        if ("N10".equals(nodeId))
//            verifyOneNodeApprovalStatusManage(bpmReponseParamsVO);
//        if ("N12".equals(nodeId))
//            verifyTwoNodeApprovalStatusManage(bpmReponseParamsVO);
//
//    }

    private void nodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO,List<FieldAuth> fieldAuths) {
        if (isProcessEdit(fieldAuths,"review_person_emp_no")){
            drawUpNodeApprovalStatusManage(bpmReponseParamsVO);
        }else if (isProcessEdit(fieldAuths,"delay_check_user")){
            delayCheckNodeApprovalStatusManage(bpmReponseParamsVO);
        }else if (isProcessEdit(fieldAuths,"verify_one_user")){
            verifyOneNodeApprovalStatusManage(bpmReponseParamsVO);
        }else if (isProcessEdit(fieldAuths,"verify_two_user")){
            verifyTwoNodeApprovalStatusManage(bpmReponseParamsVO);
        }

    }

    private boolean isProcessEdit(List<FieldAuth>  fieldAuths,String fieldName ){
        if(fieldAuths==null|| fieldAuths.isEmpty()) {
            return false;
        }
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> fieldName.equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        return fieldAuth != null;
    }

    private void drawUpNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyHazardReport hazardReport = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (hazardReport == null) {
            return;
        }
        hazardReport.setStatus(HazardReportStatus.DRAW_UP.getCode());
        if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
            hazardReport.setStatus(HazardReportStatus.RECTIFYING.getCode());
            if (hazardReport.getIsImmediateHandle() != null && hazardReport.getIsImmediateHandle().equals(1)){
                endStateManage(hazardReport);
            }
        }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            hazardReport.setStatus(HazardReportStatus.DRAW_UP.getCode());
        }
        updateById(hazardReport);
    }

    // 延期审核节点审批状态管理
    private void delayCheckNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyHazardReport hazardReport = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (hazardReport == null) {
            return;
        }
        if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
            //延期申请审核
            YTSafetyHazardDelayApply hazardDelayApply = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardDelayApply = safetyHazardDelayApplyService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardDelayApply = safetyHazardDelayApplyService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardDelayApply != null && (hazardDelayApply.getCheckSign() == null || hazardDelayApply.getCheckSign() != 1)){
                hazardDelayApply.setCheckSign(1);
                hazardDelayApply.setStatus(2);
                safetyHazardDelayApplyService.updateById(hazardDelayApply);
                hazardReport.setRectifyDelayTime(hazardDelayApply.getApplyDeadline());
            }
        }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            //延期申请审核
            YTSafetyHazardDelayApply hazardDelayApply = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardDelayApply = safetyHazardDelayApplyService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardDelayApply = safetyHazardDelayApplyService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardDelayApply != null && (hazardDelayApply.getCheckSign() == null || hazardDelayApply.getCheckSign() != 1)){
                hazardDelayApply.setCheckSign(1);
                hazardDelayApply.setStatus(3);
                safetyHazardDelayApplyService.updateById(hazardDelayApply);
            }
        }
        updateById(hazardReport);
    }

    // 一级审核节点审批状态管理
    private void verifyOneNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyHazardReport hazardReport = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (hazardReport == null) {
            return;
        }
        if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
            YTSafetyHazardResult hazardResult = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardResult = safetyHazardResultService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
                hazardResult.setCheckSign(1);
                hazardResult.setStatus(2);
                safetyHazardResultService.updateById(hazardResult);
            }
            if ( !"part" .equals(hazardReport.getHandlerType()) ){
                if ( ! (HazardReportConstant.ProblemLevelType.SERIOUS.equals(hazardReport.getProblemLevel()) || (HazardReportConstant.ProblemSourceType.COMPANY_INSPECTION.equals(hazardReport.getProblemSource()) && HazardReportConstant.ProblemLevelType.GREATER.equals(hazardReport.getProblemLevel())))){
                    endStateManage(hazardReport);
                    updateById(hazardReport);
                }
            }
        }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            YTSafetyHazardResult hazardResult = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardResult = safetyHazardResultService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
                hazardResult.setCheckSign(1);
                hazardResult.setStatus(3);
                safetyHazardResultService.updateById(hazardResult);
            }
        }
    }
    // 二级审核节点审批状态管理
    private void verifyTwoNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyHazardReport hazardReport = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (hazardReport == null) {
            return;
        }
        if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
            YTSafetyHazardResult hazardResult = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardResult = safetyHazardResultService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
                hazardResult.setCheckSign(1);
                hazardResult.setStatus(2);
                safetyHazardResultService.updateById(hazardResult);
            }
            endStateManage(hazardReport);
            updateById(hazardReport);
        }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            YTSafetyHazardResult hazardResult = null;
            if (hazardReport.getCurrentManageId() != null){
                hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
            }else {
                hazardResult = safetyHazardResultService.getNewestByProcessId(hazardReport.getId());
            }
            if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
                hazardResult.setCheckSign(1);
                hazardResult.setStatus(3);
                safetyHazardResultService.updateById(hazardResult);
            }
        }
    }

    private void endStateManage(YTSafetyHazardReport hazardReport){
        hazardReport.setStatus(HazardReportStatus.DONE.getCode());
        hazardReport.setRectifyCompleteTime(new Date());

        // 判断是否逾期完成
        Date originalDeadline = hazardReport.getRectifyDeadline();  // 原始截止期限
        Date delayTime = hazardReport.getRectifyDelayTime();
        Date completeTime = hazardReport.getRectifyCompleteTime();

        if (delayTime != null) {
            originalDeadline = delayTime;  // 有延期时间，用延期时间替代
        }

        if (completeTime != null && originalDeadline != null && completeTime.after(originalDeadline)) {
            hazardReport.setStatus(HazardReportStatus.OVERDUE.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> submitRectification(YTSafetyHazardReportAbarbeitungDTO reportAbarbeitungDTO) {
        String existUnreviewedData = isExistUnreviewedData(reportAbarbeitungDTO.getId());
        if (existUnreviewedData != null) {
            return Result.ok(existUnreviewedData);
        }
        YTSafetyHazardReport report = getById(reportAbarbeitungDTO.getId());
        if (report == null) {
            return Result.error("记录不存在");
        }
        if (HazardReportStatus.RECTIFYING.getCode() != report.getStatus()) {
            return Result.error("当前状态不允许提交整改");
        }

        String handlerType = reportAbarbeitungDTO.getAbarbeitungResult() != null
                ? reportAbarbeitungDTO.getAbarbeitungResult().getHandlerType() : null;
        //校验部分整改提交验证通过次数
        if (!isCanSubmitPartRectification(reportAbarbeitungDTO.getId(), handlerType)) {
            return Result.error("该隐患上报部分整改提交已达上限，不可再提交整改结果");
        }

        LoginUser loginUser = BusUtits.getLoginUser();

        // 批量保存处理结果
        YTSafetyHazardResultDTO abarbeitungResult = reportAbarbeitungDTO.getAbarbeitungResult();
        YTSafetyHazardResult entity = new YTSafetyHazardResult();
        BeanUtils.copyProperties(abarbeitungResult, entity);
        entity.setProcessId(reportAbarbeitungDTO.getId());
        entity.setHandlerUserEmpNo(loginUser.getEmpNo());
        entity.setHandleCompleteTime(new Date());
        entity.setCheckSign(0);
        safetyHazardResultService.save(entity);
        report.setHandlerType(entity.getHandlerType());
        report.setSubmitType(1);
        report.setCurrentManageId(entity.getId());
        updateById(report);
        return Result.OK("整改结果提交成功");
    }

    private boolean isCanSubmitPartRectification( Long hazardReportId,String handlerType) {
        if (!"part".equals(handlerType)) {
            return true;
        }
        long approvedPartCount = safetyHazardResultService.count(
                new LambdaQueryWrapper<YTSafetyHazardResult>()
                        .eq(YTSafetyHazardResult::getProcessId, hazardReportId)
                        .eq(YTSafetyHazardResult::getHandlerType, "part")
                        .eq(YTSafetyHazardResult::getStatus, 2)
        );
        return approvedPartCount < 2;
    }

    private String isExistUnreviewedData(Long hazardReportId) {
        //判断是否有未审核的整改
        LambdaQueryWrapper<YTSafetyHazardResult> resultLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resultLambdaQueryWrapper.eq(YTSafetyHazardResult::getProcessId, hazardReportId)
                .ne(YTSafetyHazardResult::getCheckSign, 1)
                .eq(YTSafetyHazardResult::getStatus, 0);
        List<YTSafetyHazardResult> resultList = safetyHazardResultService.list(resultLambdaQueryWrapper);
        if (resultList != null && !resultList.isEmpty()){
            return "该隐患单有未审核的整改，请先处理";
        }
        //判断是否有未审核的延期申请
        LambdaQueryWrapper<YTSafetyHazardDelayApply> delayApplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        delayApplyLambdaQueryWrapper.eq(YTSafetyHazardDelayApply::getProcessId, hazardReportId)
                .ne(YTSafetyHazardDelayApply::getCheckSign, 1)
                .eq(YTSafetyHazardDelayApply::getStatus, 0);
        List<YTSafetyHazardDelayApply> delayApplyList = safetyHazardDelayApplyService.list(delayApplyLambdaQueryWrapper);
        if (delayApplyList != null && !delayApplyList.isEmpty()){
            return "该隐患单有未审核的延期申请，请先处理";
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> applyDelay(YTSafetyHazardDelayApplyDTO delayDTO) {
        String existUnreviewedData = isExistUnreviewedData(delayDTO.getHazardReportId());
        if (existUnreviewedData != null) {
            return Result.ok(existUnreviewedData);
        }
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser == null || loginUser.getEmpNo() == null) {
            return Result.error("请先登录");
        }
        if (delayDTO.getHazardReportId() == null) {
            return Result.error("请选择要延期的隐患单");
        }
        YTSafetyHazardReport report = getById(delayDTO.getHazardReportId());
        if (report == null) {
            return Result.error("记录不存在");
        }
        if (HazardReportStatus.RECTIFYING.getCode() != report.getStatus()) {
            return Result.error("仅整改中状态可提交延期申请");
        }
        if (delayDTO.getApplyDeadline() == null) {
            return Result.error("请选择申请延期的完成时间");
        }
        //判断延期次数是否超过限制
        if (!isCanDelayApply(delayDTO.getHazardReportId())) {
            return Result.error("该隐患上报延期申请已达上限，不可再提交延期申请");
        }

        // 保存延期申请记录
        YTSafetyHazardDelayApply delayApply = new YTSafetyHazardDelayApply();
        BeanUtils.copyProperties(delayDTO, delayApply);
        delayApply.setProcessId(delayDTO.getHazardReportId());
        delayApply.setApplicantUserEmpNo(loginUser.getEmpNo());
        delayApply.setOriginalDeadline(report.getRectifyDeadline());
        delayApply.setCheckSign(0);
        safetyHazardDelayApplyService.save(delayApply);
        LoginUser applyUser = safetySysUserService.queryUser(report.getApplyNo());
        if (applyUser != null){
            report.setSuperApproval(applyUser.getEipName());
            report.setSuperApprovalName(applyUser.getRealname());
        }
        if (!ProblemSourceEnum.COMPANIES_INSPECTOR.getVale().equals(report.getProblemSource())) {
            //延期节点审批人
            report.setSuperApproval(report.getReviewPersonEmpNo());
            report.setSuperApprovalName(report.getReviewPersonEmpName());
        }
        report.setSubmitType(2);
        report.setCurrentManageId(delayApply.getId());
        updateById(report);
        return Result.OK("延期申请已提交");
    }

    private boolean isCanDelayApply(Long hazardReportId){

        // 判断已通过的延期申请次数是否达到上限（最多2次）
        long approvedCount = safetyHazardDelayApplyService.count(
                new LambdaQueryWrapper<YTSafetyHazardDelayApply>()
                        .eq(YTSafetyHazardDelayApply::getProcessId, hazardReportId)
                        .eq(YTSafetyHazardDelayApply::getStatus, 2)
        );
        if (approvedCount >= 2) {
            return false;
        }

        // 判断是否存在未审批的延期申请
        long pendingCount = safetyHazardDelayApplyService.count(
                new LambdaQueryWrapper<YTSafetyHazardDelayApply>()
                        .eq(YTSafetyHazardDelayApply::getProcessId, hazardReportId)
                        .and(w -> w.isNull(YTSafetyHazardDelayApply::getCheckSign)
                                .or()
                                .ne(YTSafetyHazardDelayApply::getCheckSign, 1))
        );
        return pendingCount <= 0;
    }

    @Override
    public List<YTSafetyHazardReportHistoryVO> getModifyHistoryList(Long reportId) {
        List<YTSafetyHazardReportHistoryVO> historyVOList = safetyHazardReportHistoryService.listBYHazardReportId(reportId);
        parseHistoryDisplayFields(historyVOList);
        return historyVOList;
    }

    @Override
    public IPage<YTSafetyHazardLedgerVO> getHazardLedgerList(YTSafetyHazardLedgerQueryDTO queryDTO) {
        LoginUser loginUser = BusUtits.getLoginUser();
        queryDTO.setApplyNo(loginUser.getEmpNo());
        Page<YTSafetyHazardLedgerVO> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        IPage<YTSafetyHazardLedgerVO> resultPage = baseMapper.getHazardLedgerList(page, queryDTO);

        // 解析抄送人姓名
        resolveDisplayDatas(resultPage.getRecords());
        // 解析问题集和结果集的附件列表
        resolveAttachmentLists(resultPage.getRecords());
        return resultPage;
    }

    @Override
    public List<YTSafetyHazardLedgerVO> exportHazardLedger(YTSafetyHazardLedgerQueryDTO queryDTO, HttpServletRequest request) {
        String selections = queryDTO.getSelections();
        if (StringUtils.isNotBlank(selections)){
            List<String> ids = Arrays.asList(selections.split(","));
            queryDTO.setIds(ids);
        }
        List<YTSafetyHazardLedgerVO> list = baseMapper.exportHazardLedger(queryDTO);
        resolveDisplayDatas(list);
        resolveAttachmentLists(list);
        return list;
    }

    @Override
    public Result<?> batchSubmission(YTSafetyHazardBatchSubmissionDTO batchSubmissionDTO) {
        if (batchSubmissionDTO == null || batchSubmissionDTO.getIds() == null || batchSubmissionDTO.getIds().isEmpty()) {
            return Result.error("请选择要提交的数据");
        }

        List<YTSafetyHazardReport> reportList = this.listByIds(batchSubmissionDTO.getIds());
        if (reportList.isEmpty()) {
            return Result.error("未找到要提交的数据");
        }

        // 批量查询所有报告的问题描述和处理结果
        List<Long> reportIds = reportList.stream().map(YTSafetyHazardReport::getId).collect(Collectors.toList());
        List<YTSafetyHazardDescription> allDescriptions = safetyHazardDescriptionService.listByReportIds(reportIds).stream()
                .map(vo -> {
                    YTSafetyHazardDescription desc = new YTSafetyHazardDescription();
                    desc.setId(Long.valueOf(vo.getId()));
                    desc.setProcessId(Long.valueOf(vo.getProcessId()));
                    desc.setProblemDescription(vo.getProblemDescription());
                    desc.setAttachmentIds(vo.getAttachmentIds());
                    return desc;
                })
                .collect(Collectors.toList());
        List<YTSafetyHazardResult> allResults = safetyHazardResultService.listByReportIds(reportIds).stream()
                .map(vo -> {
                    YTSafetyHazardResult result = new YTSafetyHazardResult();
                    result.setId(Long.valueOf(vo.getId()));
                    result.setProcessId(Long.valueOf(vo.getProcessId()));
                    result.setHandleCompleteTime(vo.getHandleCompleteTime());
                    result.setHandlerUserEmpNo(vo.getHandlerUserEmpNo());
                    result.setHandleMeasure(vo.getHandleMeasure());
                    result.setAttachmentIds(vo.getAttachmentIds());
                    result.setHandlerType(vo.getHandlerType());
                    result.setStatus(vo.getStatus());
                    return result;
                })
                .collect(Collectors.toList());

        Map<Long, List<YTSafetyHazardDescription>> descMap = allDescriptions.stream()
                .collect(Collectors.groupingBy(YTSafetyHazardDescription::getProcessId));
        Map<Long, List<YTSafetyHazardResult>> resultMap = allResults.stream()
                .collect(Collectors.groupingBy(YTSafetyHazardResult::getProcessId));

        StringBuilder errorInfo = new StringBuilder();
        List<YTSafetyHazardReport> validReports = new ArrayList<>();

        for (YTSafetyHazardReport report : reportList) {
            if (HazardReportStatus.DRAFT.getCode() != report.getStatus()) {
                errorInfo.append(report.getId()).append("非草稿状态,");
                continue;
            }

            String error = validateReport(report, descMap.get(report.getId()), resultMap.get(report.getId()));
            if (StringUtils.isNotBlank(error)) {
                errorInfo.append(report.getId()).append("：").append(error).append(",");
            } else {
                validReports.add(report);
            }
        }

        if (validReports.isEmpty()) {
            return Result.error("没有可提交的记录");
        }

        //通过的数据进入流程发起,修改状态
        for (YTSafetyHazardReport report : validReports) {
            submitProcess(report);
            report.setStatus(HazardReportStatus.APPROVING.getCode());
            report.setDraftStatus(0);
            this.updateById(report);
        }

        if (errorInfo.length() > 0) {
            return Result.error("以下记录校验未通过：" + errorInfo.toString());
        }

        return Result.OK("批量提交成功，共提交 " + validReports.size() + " 条记录");
    }

    private String validateReport(YTSafetyHazardReport report,
                                         List<YTSafetyHazardDescription> descList,
                                         List<YTSafetyHazardResult> resultList) {
        if (report.getHazardCategory() == null) {
            return "隐患类别未填";
        }
        if (StringUtils.isBlank(report.getProblemSource())) {
            return "问题来源未填";
        }
        if (report.getCheckTime() == null) {
            return "检查时间未填";
        }
        if (report.getPlantId() == null) {
            return "所属厂区未填";
        }
        if (report.getAreaId() == null) {
            return "所属区域未填";
        }
        if (report.getLocationId() == null) {
            return "位置未填";
        }
        if (report.getOrgId() == null) {
            return "所属组织未填";
        }
        if (StringUtils.isBlank(report.getProblemCategory())) {
            return "问题类别未填";
        }
        if (report.getProblemLevel() == null) {
            return "问题等级未填";
        }
        if (report.getRectifyDeadline() == null) {
            return "要求整改完成时间未填";
        }
        if (Integer.valueOf(0).equals(report.getIsImmediateHandle())) {
            if (StringUtils.isBlank(report.getRectifyPersonEmpNo())) {
                return "整改责任人工号未填";
            }
        }

        // 校验问题描述列表（所有场景必填）
        if (descList == null || descList.isEmpty()) {
            return "问题描述不能为空";
        }
        for (int i = 0; i < descList.size(); i++) {
            YTSafetyHazardDescription desc = descList.get(i);
            if (StringUtils.isBlank(desc.getProblemDescription())) {
                return "第" + (i + 1) + "条问题描述不能为空";
            }
        }

        // 校验处理结果列表（仅当场处理=是时校验）
        if (Integer.valueOf(1).equals(report.getIsImmediateHandle())) {
            if (resultList == null || resultList.isEmpty()) {
                return "处理结果不能为空";
            }
            for (int i = 0; i < resultList.size(); i++) {
                YTSafetyHazardResult result = resultList.get(i);
                if (result.getHandleCompleteTime() == null) {
                    return "第" + (i + 1) + "条处理完成时间未填";
                }
                if (StringUtils.isBlank(result.getHandlerUserEmpNo())) {
                    return "第" + (i + 1) + "条处理人工号未填";
                }
                if (StringUtils.isBlank(result.getHandleMeasure())) {
                    return "第" + (i + 1) + "条处理措施未填";
                }
                if (StringUtils.isBlank(result.getAttachmentIds())) {
                    return "第" + (i + 1) + "条处理完成照片未填";
                }
            }
        }

        return null;
    }

    private void resolveDisplayDatas(List<YTSafetyHazardLedgerVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        // 批量查询抄送人姓名，避免 N+1 查询
        Set<String> allEmpNos = new HashSet<>();
        for (YTSafetyHazardLedgerVO vo : records) {
            if (StringUtils.isNotBlank(vo.getCcPersonEmpNo())) {
                allEmpNos.addAll(Arrays.asList(vo.getCcPersonEmpNo().split(",")));
            }
        }

        Map<String, LoginUser> userMap;
        if (!allEmpNos.isEmpty()) {
            List<LoginUser> users = safetySysUserService.queryUserListByEmpNos(new ArrayList<>(allEmpNos));
            userMap = users.stream()
                    .collect(Collectors.toMap(LoginUser::getEmpNo, u -> u, (a, b) -> a));
        } else {
            userMap = Collections.emptyMap();
        }

        for (YTSafetyHazardLedgerVO vo : records) {
            // 抄送人姓名
            if (StringUtils.isNotBlank(vo.getCcPersonEmpNo())) {
                List<String> ccNames = Arrays.stream(vo.getCcPersonEmpNo().split(","))
                        .map(String::trim)
                        .filter(StringUtils::isNotEmpty)
                        .map(empNo -> {
                            LoginUser user = userMap.get(empNo);
                            return user != null ? user.getRealname() : empNo;
                        })
                        .collect(Collectors.toList());
                vo.setCcPersonName(String.join(",", ccNames));
            }

            // 详情链接
            if (vo.getId() != null) {
                vo.setDetailUrl(exportUrl + "safety/potential/potentialReport?id=" + vo.getId()+"&mode=detail");
            }

            // 延期时优先使用延期完成时间
            if (vo.getRectifyDelayTime() != null) {
                vo.setRectifyDeadline(vo.getRectifyDelayTime());
            }

            // 动态计算逾期/超期状态
            resolveDynamicStatus(vo);
        }
    }

    /**
     * 动态计算逾期/超期状态（status=6 逾期未完成 / status=7 逾期完成）
     */
    private void resolveDynamicStatus(YTSafetyHazardLedgerVO vo) {
        Date now = new Date();
        int status = vo.getStatus() != null ? vo.getStatus() : 0;

        Date effectiveDeadline = vo.getRectifyDelayTime();
        if (effectiveDeadline == null){
            effectiveDeadline = vo.getRectifyDeadline();
        }
        Date completeTime = vo.getRectifyCompleteTime();

        if (status != 0 && status != 5 && status != 6 && status != 7 && status != 9 && status != 30) {
            // 未完成且已逾期的场景 -> 动态标记为逾期未完成
            if (effectiveDeadline != null && now.after(effectiveDeadline)) {
                    vo.setStatusText("逾期未完成");
                    vo.setStatus(HazardReportStatus.EXPECTED_INCOMPLETE.getCode());
            }
        }
    }

    private void resolveAttachmentLists(List<YTSafetyHazardLedgerVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        Set<String> allFileIds = new HashSet<>();
        for (YTSafetyHazardLedgerVO vo : records) {
            if (vo.getDescriptionList() != null) {
                for (YTSafetyHazardDescriptionVO desc : vo.getDescriptionList()) {
                    addFileIds(allFileIds, desc.getAttachmentIds());
                }
            }
            if (vo.getResultList() != null) {
                for (YTSafetyHazardResultVO result : vo.getResultList()) {
                    addFileIds(allFileIds, result.getAttachmentIds());
                }
            }
        }

        if (allFileIds.isEmpty()) {
            return;
        }

        List<String> fileIdList = new ArrayList<>(allFileIds);
        List<YTSafetyFileInfo> fileInfoList = fileService.listByIds(fileIdList);
        Map<Long, YTSafetyFileInfo> fileMap = fileInfoList.stream()
                .collect(Collectors.toMap(YTSafetyFileInfo::getId, f -> f));

        for (YTSafetyHazardLedgerVO vo : records) {
            fillAttachmentList(vo.getDescriptionList(), fileMap);
            fillAttachmentList(vo.getResultList(), fileMap);
            buildSummaryFields(vo);
        }
    }

    private void addFileIds(Set<String> ids, String attachmentIds) {
        if (StringUtils.isBlank(attachmentIds)) {
            return;
        }
        Arrays.stream(attachmentIds.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .forEach(ids::add);
    }

    private void fillAttachmentList(List<?> list, Map<Long, YTSafetyFileInfo> fileMap) {
        if (list == null) {
            return;
        }
        for (Object obj : list) {
            if (obj instanceof YTSafetyHazardDescriptionVO) {
                YTSafetyHazardDescriptionVO desc = (YTSafetyHazardDescriptionVO) obj;
                fillDescriptionAttachmentList(desc, fileMap);
            } else if (obj instanceof YTSafetyHazardResultVO) {
                YTSafetyHazardResultVO result = (YTSafetyHazardResultVO) obj;
                fillResultAttachmentList(result, fileMap);
            }
        }
    }

    private void fillDescriptionAttachmentList(YTSafetyHazardDescriptionVO desc, Map<Long, YTSafetyFileInfo> fileMap) {
        String ids = desc.getAttachmentIds();
        if (StringUtils.isNotBlank(ids)) {
            List<YTSafetyFileInfo> list = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .map(id -> fileMap.get(Long.valueOf(id)))
                    .filter(f -> f != null)
                    .collect(Collectors.toList());
            desc.setAttachmentList(list);
        }
    }

    private void fillResultAttachmentList(YTSafetyHazardResultVO result, Map<Long, YTSafetyFileInfo> fileMap) {
        String ids = result.getAttachmentIds();
        if (StringUtils.isNotBlank(ids)) {
            List<YTSafetyFileInfo> list = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .map(id -> fileMap.get(Long.valueOf(id)))
                    .filter(f -> f != null)
                    .collect(Collectors.toList());
            result.setAttachmentList(list);
        }
    }

    private void buildSummaryFields(YTSafetyHazardLedgerVO vo) {
        if (vo.getDescriptionList() != null && !vo.getDescriptionList().isEmpty()) {
            List<String> descTexts = new ArrayList<>();
            List<String> descPhotoPaths = new ArrayList<>();
            for (YTSafetyHazardDescriptionVO desc : vo.getDescriptionList()) {
                if (StringUtils.isNotBlank(desc.getProblemDescription())) {
                    descTexts.add(desc.getProblemDescription());
                }
                if (desc.getAttachmentList() != null) {
                    for (YTSafetyFileInfo fileInfo : desc.getAttachmentList()) {
                        if (StringUtils.isNotBlank(fileInfo.getFilePath())) {
                            descPhotoPaths.add(fileInfo.getFilePath());
                        }
                    }
                }
            }
            vo.setProblemDescriptionSummary(String.join("\n", descTexts));
            vo.setProblemPhotosSummary(String.join(";", descPhotoPaths));
        }
        if (vo.getResultList() != null && !vo.getResultList().isEmpty()) {
            List<String> resultTexts = new ArrayList<>();
            List<String> resultPhotoPaths = new ArrayList<>();
            for (YTSafetyHazardResultVO result : vo.getResultList()) {
                if (StringUtils.isNotBlank(result.getHandleMeasure())) {
                    resultTexts.add(result.getHandleMeasure());
                }
                if (result.getAttachmentList() != null) {
                    for (YTSafetyFileInfo fileInfo : result.getAttachmentList()) {
                        if (StringUtils.isNotBlank(fileInfo.getFilePath())) {
                            resultPhotoPaths.add(fileInfo.getFilePath());
                        }
                    }
                }
            }
            vo.setResultSummary(String.join("\n", resultTexts));
            vo.setResultPhotosSummary(String.join(";", resultPhotoPaths));
        }
    }

    /**
     * 构建流程标题：年月日+厂区+姓名/匿名+发起的+类型+问题来源+隐患上报
     */
    private String buildFlowSubject(YTSafetyHazardReportDTO reportDTO, LoginUser user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String plantName = baseMapper.selectFieldNameById(reportDTO.getPlantId());
        String nameStr = user.getRealname() != null && !user.getRealname().isEmpty() ? user.getRealname() : "匿名";

        String hazardType;
        switch (reportDTO.getHazardCategory()) {
            case 1:
                hazardType = "消防";
                break;
            case 2:
                hazardType = "治安";
                nameStr = getNickName();
                break;
            default:
                hazardType = "未分类";
                break;
        }
        String problemSource = ProblemSourceEnum.getByVale(reportDTO.getProblemSource()).getName();

        return dateStr + plantName + nameStr + "发起的" + hazardType  + problemSource + "隐患上报";
    }

    /**
     * 解析历史记录JSON快照，填充责任部门和整改时间的展示字段
     */
    private void parseHistoryDisplayFields(List<YTSafetyHazardReportHistoryVO> historyVOList) {
        if (historyVOList == null || historyVOList.isEmpty()) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (YTSafetyHazardReportHistoryVO vo : historyVOList) {
            try {
                JSONObject before = JSON.parseObject(vo.getBeforeSnapshot());
                JSONObject after = JSON.parseObject(vo.getAfterSnapshot());

                StringBuilder updateBeforeData = new StringBuilder();
                StringBuilder updateAfterData = new StringBuilder();
                // 解析责任部门名称
                Long beforeOrgId = before.getLong("orgId");
                Long afterOrgId = after.getLong("orgId");
                if (beforeOrgId != null) {
                    SafetyGroupInfo beforeOrg = safetyGroupInfoService.getById(beforeOrgId);
                    if (beforeOrg != null) {
                        updateBeforeData.append("责任部门: ").append(beforeOrg.getName()).append(";");
                    }
                }
                if (afterOrgId != null) {
                    SafetyGroupInfo afterOrg = safetyGroupInfoService.getById(afterOrgId);
                    if (afterOrg != null) {
                        updateAfterData.append("责任部门: ").append(afterOrg.getName()).append(";");
                    }
                }

                // 解析整改时间
                Date beforeDeadline = before.getDate("rectifyDeadline");
                Date afterDeadline = after.getDate("rectifyDeadline");
                if (beforeDeadline != null) {
                    updateBeforeData.append("整改时间: ").append(sdf.format(beforeDeadline)).append(";");
                }
                if (afterDeadline != null) {
                    updateAfterData.append("整改时间: ").append(sdf.format(afterDeadline)).append(";");
                }
                vo.setUpdateBeforeData(updateBeforeData.toString());
                vo.setUpdateAfterData(updateAfterData.toString());
            } catch (Exception e) {
                log.warn("解析历史记录快照失败, processId={}", vo.getProcessId(), e);
            }
        }
    }

    /**
     * 记录修改历史快照（仅追踪责任部门orgId和要求整改完成时间rectifyDeadline的变更）
     */
    private void recordHistory(Long reportId, String operatorEip, String operatorName, String beforeSnapshot) {
        YTSafetyHazardReport current = getById(reportId);
        if (current == null) {
            return;
        }
        String afterSnapshot = JSON.toJSONString(current);

        // 只追踪orgId和rectifyDeadline两个字段的变化
        JSONObject before = JSON.parseObject(beforeSnapshot);
        JSONObject after = JSON.parseObject(afterSnapshot);

        Object beforeOrgId = before.get("orgId");
        Object afterOrgId = after.get("orgId");
        Object beforeDeadline = before.get("rectifyDeadline");
        Object afterDeadline = after.get("rectifyDeadline");

        boolean orgIdChanged = !Objects.equals(beforeOrgId, afterOrgId);
        boolean deadlineChanged = !Objects.equals(beforeDeadline, afterDeadline);

        // 如果两个字段都没变化，不保存历史记录
        if (!orgIdChanged && !deadlineChanged) {
            return;
        }

        YTSafetyHazardReportHistory history = new YTSafetyHazardReportHistory();
        history.setId(IdWorker.getId());
        history.setProcessId(Long.valueOf(reportId));
        history.setChangeTime(new Date());
        history.setChangeUserEmpNo(operatorEip);
        history.setChangeUserName(operatorName);
        history.setBeforeSnapshot(beforeSnapshot);
        history.setAfterSnapshot(afterSnapshot);
        safetyHazardReportHistoryService.save(history);
    }
}
