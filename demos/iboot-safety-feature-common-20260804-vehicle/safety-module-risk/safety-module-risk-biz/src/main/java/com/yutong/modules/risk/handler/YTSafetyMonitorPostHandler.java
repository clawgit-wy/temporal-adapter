package com.yutong.modules.risk.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.bpm.ISafetyBpmModelService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfo;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfoResponse;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcess;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcessResponse;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import com.yutong.modules.common.utils.YTWebserviceUtil;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.constants.YTConstants;
import com.yutong.modules.risk.constants.YTResponseEnum;
import com.yutong.modules.risk.dto.SafetyMonitorPostProcessDTO;
import com.yutong.modules.risk.exception.YTBizException;
import com.yutong.modules.risk.service.YTSafetyMonitorPostService;
import com.yutong.modules.risk.service.YTSafetyWaterFlowNoService;
import com.yutong.modules.risk.util.YTDateUtils;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.vo.YTSafetyMonitorPostExportVO;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.entity.SafetySysUser;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lukunpeng
 * @time 2024-4-30
 *
 * 监控岗位上报流程handler
 */
@Component
public class YTSafetyMonitorPostHandler {

    /** 监控岗位上报流程对应的formId */
    private static final String MONITOR_POST_FORM_ID = "ytsafety_monitor_post_process";

    @Autowired
    YTSafetyMonitorPostService safetyMonitorPostService;

    @Autowired
    private YTWebserviceUtil webserviceUtil;

    @Autowired
    private YTSafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    @Autowired
    private ISafetyEmployeeMessageService safetyEmployeeMessageService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private ISafetyBpmModelService safetyBpmModelService;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;



    /**
     * 保存数据并创建/提交流程
     */
    public String saveAndCerateProcess(SafetyMonitorPostProcessDTO dto) {

        dto.setTitle(YTDateUtils.format(new Date(), 7) + dto.getTitle());
        // 2. 生成事件编号
        String flowNo = getFlowNo();
        dto.setFlowNo(flowNo);

        // 3. 获取登录用户信息
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        String realName = loginUser != null ? loginUser.getRealname() : null;
        String eipName = loginUser != null ? loginUser.getEipName() : null;

        // 4. 设置创建人信息
        dto.setCreator(workNo);
        dto.setSubmitNo(workNo);
        if (StringUtils.isBlank(dto.getSubmitName())) {
            dto.setSubmitName(realName);
        }

        // 5. 获取异常事件接收人 EIP 登录名
        String abnormalCreatorNo = dto.getAbnormalCreatorNo();
        if (StringUtils.isNotBlank(abnormalCreatorNo)) {
            String abnormalEipName = safetyEmployeeMessageService.getLoginNameByNo(abnormalCreatorNo);
            dto.setAbnormalCreatorEip(abnormalEipName);
        }

        // 6. 查询 BPM 模型配置
        SafetyBpmModel model = getBpmModel();
        if (model == null) {
            throw new YTBizException(YTResponseEnum.EXCEPTION.getCode(), "未找到监控岗位上报流程的BPM模型配置");
        }
        dto.setModelId(model.getId());

        // 7. 设置初始状态
        dto.setStatus(20);

        // 8. 先保存业务数据
        safetyMonitorPostService.addData(dto);

        // 9. 创建并提交流程
        BpmRequestParamsDTO submitDto = buildSubmitParams(dto, model, eipName, realName, workNo);
        BpmReponseParamsVO result = safetySummaryProcessService.createAndApproveProcess(submitDto);
        String flowId = result.getFlowId();

        // 10. 回写 flow_id 到业务表
        dto.setFlowId(flowId);
        safetyMonitorPostService.updateFlowId(dto.getId(), flowId, model.getId());

        return flowId;
    }

    /**
     * 更新数据并审批流程
     */
    public Object updateAndAudit(SafetyMonitorPostProcessDTO dto) {
        // 1. 更新业务数据
        safetyMonitorPostService.insertCheckor(dto);
        safetyMonitorPostService.updateData(dto);

        LoginUser user = BusUtits.getLoginUser();
        dto.getBpmProcessParamDTO().setFormInstanceId(dto.getId());
        dto.getBpmProcessParamDTO().setLoginName(user.getEipName());
        dto.getBpmProcessParamDTO().setNodeHandler(user.getEipName());
        dto.getBpmProcessParamDTO().setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        BpmReponseParamsVO result = safetySummaryProcessService.executeProcess(dto.getBpmProcessParamDTO());

        if ("handler_pass".equals(dto.getBpmProcessParamDTO().getOperationType())){
            if (result != null) {
                String applyNo = result.getApplyNo();
                if (StringUtils.isNotBlank(applyNo)) {
                    String abnormalEipName = safetyEmployeeMessageService.getLoginNameByNo(applyNo);
                    String flowId = result.getFlowId();
                    if (StringUtils.isNotBlank(abnormalEipName) && StringUtils.isNotBlank(flowId)) {
                        circulate(flowId, user.getEipName(), abnormalEipName);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据流程ID获取当前节点ID
     * @param flowId
     * @return
     */
    public Object getNowNode(String id, String flowId, String formId) {
        GetCurrentNodesInfo getCurrentNodesInfo = new GetCurrentNodesInfo();
        SafetyBpmModel model = getBpmModel();
        String formIdStr = JaxbXmlUtil.CombineFormId(String.valueOf(id), bpmSysId, model.getModelId(), model.getFormId());

        getCurrentNodesInfo.setFormId(formIdStr);
        getCurrentNodesInfo.setProcessId(flowId);
        return bpmProcessService.getCurrentNodesInfo(getCurrentNodesInfo);
    }

    public Object circulate(String flowId, String fromUser, String toUser) {
        PassProcess passProcess = new PassProcess();
        passProcess.setProcessId(flowId);
        passProcess.setCirculated(JaxbXmlUtil.CombineCreator(fromUser));

        passProcess.setBeCirculated(JaxbXmlUtil.CombineCreator(Arrays.asList(new String[]{toUser})));

        PassProcessResponse response = bpmProcessService.setProcessCirculate(passProcess);
        return response != null ? response.getReturnMsg() : null;
    }
    /**
     * 获取当前节点字段权限
     * 0-可编辑，1-不可见，2-只读
     */
    public Object getAuthFields(Long id, String flowId, String nodeId) {
        SafetyBpmModel model = getBpmModel();
        if (model == null) {
            throw new YTBizException(YTResponseEnum.EXCEPTION.getCode(), "未找到BPM模型配置");
        }
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(id), bpmSysId,
                model.getModelId(), model.getFormId());

        GetFieldAuthInfo getFieldAuthInfo = new GetFieldAuthInfo();
        getFieldAuthInfo.setFormId(formId);
        getFieldAuthInfo.setProcessId(flowId);
        getFieldAuthInfo.setNodeFactIds(nodeId);

        GetFieldAuthInfoResponse response = bpmProcessService.getAuthFields(getFieldAuthInfo);
        return response.isSuccess();
    }

    private String getFlowNo(){
        String date = YTDateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("monitor_post");
        String flowNo = Joiner.on("-").join("JKYZ",date+nowNum);
        return flowNo;
    }

    /**
     * 查询 BPM 模型配置
     */
    private SafetyBpmModel getBpmModel() {
        List<SafetyBpmModel> models = safetyBpmModelService.list(
                new LambdaQueryWrapper<SafetyBpmModel>()
                        .eq(SafetyBpmModel::getFormId, MONITOR_POST_FORM_ID)
                        .last("limit 1"));
        if (models != null && !models.isEmpty()) {
            return models.get(0);
        }
        return null;
    }

    /**
     * 获取流程 URL 前缀
     */
    private String getBpmUrlPre() {
        SafetyBpmModel model = getBpmModel();
        if (model != null) {
            return model.getFormUrl() + "?sysId=ytsafety&modelId=" + model.getModelId()
                    + "&templateFormId=" + model.getFormId();
        }
        return "";
    }

    /**
     * 构建草稿参数
     */
    private BpmRequestParamsDTO buildDraftParams(SafetyMonitorPostProcessDTO dto, SafetyBpmModel model,
                                                 String userName, String realName, String workNo) {
        BpmRequestParamsDTO params = new BpmRequestParamsDTO();
        params.setFormInstanceId(dto.getId());
        params.setModelFlag(model.getModelId());
        params.setFlowSubject(dto.getTitle());
        params.setApplyEipName(userName);
        params.setApplyNo(workNo);
        params.setApplyName(realName);
        params.setApplyDepartCode(dto.getSubmitDept());
        params.setApplyDepartName(dto.getSubmitDept());
        params.setStartDate(new Date());
        return params;
    }

    /**
     * 构建提交流程参数
     */
    private BpmRequestParamsDTO buildSubmitParams(SafetyMonitorPostProcessDTO dto, SafetyBpmModel model,
                                                  String eipName, String realName, String workNo) {
        BpmRequestParamsDTO params = new BpmRequestParamsDTO();
        params.setFormInstanceId(dto.getId());
        params.setFlowCode(dto.getFlowNo());
        params.setModelFlag(model.getModelId());
        params.setFlowSubject(dto.getTitle());
        params.setApplyEipName(eipName);
        params.setCurNodeHandler(eipName);
        params.setLoginName(eipName);
        params.setApplyNo(workNo);
        params.setApplyName(realName);
        params.setLoginName(eipName);
        params.setApplyDepartCode(dto.getSubmitDept());
        params.setApplyDepartName(dto.getSubmitDept());
        params.setStartDate(new Date());
        params.setPreNodeHandler(eipName);
        params.setPreNodeHandlerName(realName + "(" + workNo + ")");
        params.setCurNodeHandler(dto.getAbnormalCreatorEip());
        return params;
    }

    // ==================== 以下为查询/导出方法，逻辑不变 ====================

    public SafetyMonitorPostProcessDTO getDetail(Long id) {
        return safetyMonitorPostService.getDetail(id);
    }

    public IPage<SafetyMonitorPostProcessDTO> getList(Page<SafetyMonitorPostProcessDTO> page, SafetyMonitorPostProcessDTO processDTO) {
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        YTLogUtils.info("获取当前登录用户, workNo=" + workNo);
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                if (processDTO.getFactory() == null) {
                    processDTO.setFactory(user.getPowerFactoryId());
                }
            }
        }

        // 查询流程链接前缀
        String bpmUrlPre = getBpmUrlPre();
        processDTO.setBpmUrlPre(bpmUrlPre);

        return safetyMonitorPostService.getList(page, processDTO);
    }

    public List<YTSafetyMonitorPostExportVO> exportMonitorPost(HttpServletRequest request, SafetyMonitorPostProcessDTO processDTO) {
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        YTLogUtils.info("获取当前登录用户, workNo=" + workNo);
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                if (processDTO.getFactory() == null) {
                    processDTO.setFactory(user.getPowerFactoryId());
                }
            }
        }
        List<SafetyMonitorPostProcessDTO> list = safetyMonitorPostService.exportData(processDTO);
        if (list.size() > YTConstants.FILE_SIZE) {
            throw new YTBizException(YTResponseEnum.FAIL.getCode(), "导出数据不能超过" + YTConstants.FILE_SIZE + "条！");
        }
        List<YTSafetyMonitorPostExportVO> exportList = new ArrayList<>();
        for (SafetyMonitorPostProcessDTO dto : list) {
            YTSafetyMonitorPostExportVO vo = new YTSafetyMonitorPostExportVO();
            vo.setFlowNo(dto.getFlowNo());
            vo.setFactory(dto.getFactory());
            vo.setPositionPost(dto.getPositionPost());
            vo.setCheckTimeStart(dto.getCheckTimeStart());
            vo.setAbnormalType(dto.getAbnormalType());
            vo.setAbnormalDesc(dto.getAbnormalDesc());
            vo.setAbnormalCreator(dto.getAbnormalCreator());
            vo.setAbnormalDept(dto.getAbnormalDept());
            if (dto.getCheckName() != null) {
                vo.setCheckName(Joiner.on("-").join(dto.getCheckName(), dto.getCheckUserno()));
            }
            vo.setCheckDept(dto.getCheckDept());
            vo.setCheckException(dto.getCheckException());
            vo.setExamFulfill(dto.getExamFulfill());
            vo.setImproveResult(dto.getImproveResult());
            vo.setLensName(dto.getLensName());
            vo.setWaysMeans(dto.getWaysMeans());
            vo.setSubmitName(dto.getSubmitName());
            vo.setSubmitDept(dto.getSubmitDept());
            vo.setCreateTime(YTDateUtils.format(dto.getCreateTime(), 2));
            if (dto.getPublishTime() != null) {
                vo.setPublishTime(YTDateUtils.format(dto.getPublishTime(), 2));
            }
            vo.setRemark(dto.getRemark());
            vo.setLink(exportUrl+"taskreport?formInstanceId="+dto.getId()+"&processId="+ dto.getFlowId()+"&look=1&activeTab=monitor");
            exportList.add(vo);
        }
        return exportList;
    }

}
