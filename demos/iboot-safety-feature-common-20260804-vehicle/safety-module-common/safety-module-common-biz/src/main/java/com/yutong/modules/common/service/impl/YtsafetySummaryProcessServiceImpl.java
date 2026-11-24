package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessApproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessNowapproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.ProcessConstant;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.*;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.entity.bpm.*;
import com.yutong.modules.common.entity.bpm.approve.*;
import com.yutong.modules.common.entity.bpm.create.CreateProcess;
import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcess;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessResponse;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfo;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfoResponse;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoApprover;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoResponse;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogList;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoResponse;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcess;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcessResponse;
import com.yutong.modules.common.factory.action.JsonService;
import com.yutong.modules.common.mapper.SafetyBpmModelMapper;
import com.yutong.modules.common.mapper.SafetyProcessAbandonConfiglMapper;
import com.yutong.modules.common.mapper.YtsafetySummaryProcessMapper;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.common.vo.TokenVO;
import com.yutong.modules.common.vo.YtsafetySummaryProcessVO;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 流程汇总信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Service
public class YtsafetySummaryProcessServiceImpl extends ServiceImpl<YtsafetySummaryProcessMapper, YtsafetySummaryProcess> implements IYtsafetySummaryProcessService {

    @Autowired
    private YtsafetySummaryProcessMapper safetySummaryProcessMapper;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Autowired
    private SafetyBpmModelMapper safetyBpmModelMapper;

    @Autowired
    private IYtsafetySummaryProcessApproverService summaryProcessApproverService;

    @Autowired
    private IYtsafetySummaryProcessNowapproverService summaryProcessNextapproverService;

    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;

    @Value("${ytsafety.process.config.nickAccount}")
    private String nickAccount;

    @Value("${ytsafety.process.config.eipLogin}")
    private String eipLogin;

    @Autowired
    private SafetyProcessAbandonConfiglMapper safetyProcessAbandonConfiglMapper;


    @Autowired
    private YtsafetySummaryProcessMapper tsafetySummaryProcessMapper;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource(name = "json")
    private JsonService jsonService;

    @Override
    public IPage<YtsafetySummaryProcessVO> queryProcessTodo(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO) {
        setDateFormat(safetySummaryProcessQueryDTO);
        LambdaQueryWrapper<YtsafetySummaryProcess> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowSubject())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowSubject,safetySummaryProcessQueryDTO.getFlowSubject());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowCode())){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowCode,safetySummaryProcessQueryDTO.getFlowCode());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getNodeName())){
            queryWrapper.like(YtsafetySummaryProcess::getNodeName,safetySummaryProcessQueryDTO.getNodeName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowName())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowName,safetySummaryProcessQueryDTO.getFlowName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getUserInfo())){
            queryWrapper.and(wrapper -> wrapper
                    .like(YtsafetySummaryProcess::getApplyName, safetySummaryProcessQueryDTO.getUserInfo())
                    .or()
                    .like(YtsafetySummaryProcess::getNickName, safetySummaryProcessQueryDTO.getUserInfo()));
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getPreNodeHandler())){
            queryWrapper.like(YtsafetySummaryProcess::getPreNodeHandlerName,safetySummaryProcessQueryDTO.getPreNodeHandler());
        }
        if(safetySummaryProcessQueryDTO.getFlowStatus()!=null){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowStatus,safetySummaryProcessQueryDTO.getFlowStatus());
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeStart()==null){
            safetySummaryProcessQueryDTO.setHandleTimeStart(DateUtils.parseShortDate(CommonConstant.sysStartDate));
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeEnd()==null){
            safetySummaryProcessQueryDTO.setHandleTimeEnd(DateUtils.parseShortDate(CommonConstant.sysEndDate));
        }
        queryWrapper.between(YtsafetySummaryProcess::getHandlerDate, safetySummaryProcessQueryDTO.getHandleTimeStart(),safetySummaryProcessQueryDTO.getHandleTimeEnd());

        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeEnd());
        }
        queryWrapper.ne(YtsafetySummaryProcess::getFlowStatus,ProcessStatusEnums.discarded.getCode());
        LoginUser user = BusUtits.getLoginUser();
        return safetySummaryProcessMapper.queryProcessTodo(page,queryWrapper,user.getEmpNo());
    }

    @Override
    public IPage<YtsafetySummaryProcessVO> queryProcessCompleted(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO) {
        setDateFormat(safetySummaryProcessQueryDTO);
        LambdaQueryWrapper<YtsafetySummaryProcess> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowSubject())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowSubject,safetySummaryProcessQueryDTO.getFlowSubject());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowCode())){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowCode,safetySummaryProcessQueryDTO.getFlowCode());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getNodeName())){
            queryWrapper.like(YtsafetySummaryProcess::getNodeName,safetySummaryProcessQueryDTO.getNodeName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowName())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowName,safetySummaryProcessQueryDTO.getFlowName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getUserInfo())){
            queryWrapper.and(wrapper -> wrapper
                    .like(YtsafetySummaryProcess::getApplyName, safetySummaryProcessQueryDTO.getUserInfo())
                    .or()
                    .like(YtsafetySummaryProcess::getNickName, safetySummaryProcessQueryDTO.getUserInfo()));
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getPreNodeHandler())){
            queryWrapper.like(YtsafetySummaryProcess::getPreNodeHandlerName,safetySummaryProcessQueryDTO.getPreNodeHandler());
        }
        if(safetySummaryProcessQueryDTO.getFlowStatus()!=null){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowStatus,safetySummaryProcessQueryDTO.getFlowStatus());
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getHandlerDate,safetySummaryProcessQueryDTO.getHandleTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getHandlerDate,safetySummaryProcessQueryDTO.getHandleTimeEnd());
        }
        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeEnd());
        }
        LoginUser user = BusUtits.getLoginUser();
        return safetySummaryProcessMapper.queryProcessCompleted(page,queryWrapper,user.getEmpNo());
    }

    @Override
    public IPage<YtsafetySummaryProcessVO> queryAllProcess(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO) {
        setDateFormat(safetySummaryProcessQueryDTO);
        LambdaQueryWrapper<YtsafetySummaryProcess> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowSubject())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowSubject,safetySummaryProcessQueryDTO.getFlowSubject());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowCode())){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowCode,safetySummaryProcessQueryDTO.getFlowCode());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getNodeName())){
            queryWrapper.like(YtsafetySummaryProcess::getNodeName,safetySummaryProcessQueryDTO.getNodeName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowName())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowName,safetySummaryProcessQueryDTO.getFlowName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getUserInfo())){
            queryWrapper.and(wrapper -> wrapper
                    .like(YtsafetySummaryProcess::getApplyName, safetySummaryProcessQueryDTO.getUserInfo())
                    .or()
                    .like(YtsafetySummaryProcess::getNickName, safetySummaryProcessQueryDTO.getUserInfo()));
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getPreNodeHandler())){
            queryWrapper.like(YtsafetySummaryProcess::getPreNodeHandlerName,safetySummaryProcessQueryDTO.getPreNodeHandler());
        }
        if(safetySummaryProcessQueryDTO.getFlowStatus()!=null){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowStatus,safetySummaryProcessQueryDTO.getFlowStatus());
        }

        if(safetySummaryProcessQueryDTO.getApplyEndTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getApplyEndTime,safetySummaryProcessQueryDTO.getApplyEndTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getApplyEndTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getApplyEndTime,safetySummaryProcessQueryDTO.getApplyEndTimeEnd());
        }
        
        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeEnd());
        }

        LoginUser user = BusUtits.getLoginUser();
        return safetySummaryProcessMapper.queryAllProcess(page,queryWrapper,user.getEmpNo());
    }

    @Override
    public IPage<YtsafetySummaryProcessVO> queryProcessDraft(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO) {
        setDateFormat(safetySummaryProcessQueryDTO);
        LambdaQueryWrapper<YtsafetySummaryProcess> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowSubject())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowSubject,safetySummaryProcessQueryDTO.getFlowSubject());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowName())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowName,safetySummaryProcessQueryDTO.getFlowName());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeEnd());
        }
        LoginUser user = BusUtits.getLoginUser();
        queryWrapper.eq(YtsafetySummaryProcess::getFlowStatus,0);
        queryWrapper.eq(YtsafetySummaryProcess::getApplyNo,user.getEmpNo());
        return safetySummaryProcessMapper.queryProcessDraft(page,queryWrapper);
    }

    @Override
    public IPage<YtsafetySummaryProcessVO> queryMyApply(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO) {
        setDateFormat(safetySummaryProcessQueryDTO);
        LambdaQueryWrapper<YtsafetySummaryProcess> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowSubject())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowSubject,safetySummaryProcessQueryDTO.getFlowSubject());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowCode())){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowCode,safetySummaryProcessQueryDTO.getFlowCode());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getNodeName())){
            queryWrapper.like(YtsafetySummaryProcess::getNodeName,safetySummaryProcessQueryDTO.getNodeName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getFlowName())){
            queryWrapper.like(YtsafetySummaryProcess::getFlowName,safetySummaryProcessQueryDTO.getFlowName());
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getUserInfo())){
            queryWrapper.and(wrapper -> wrapper
                    .like(YtsafetySummaryProcess::getApplyName, safetySummaryProcessQueryDTO.getUserInfo())
                    .or()
                    .like(YtsafetySummaryProcess::getNickName, safetySummaryProcessQueryDTO.getUserInfo()));
        }

        if(StringUtils.isNotEmpty(safetySummaryProcessQueryDTO.getPreNodeHandler())){
            queryWrapper.like(YtsafetySummaryProcess::getPreNodeHandlerName,safetySummaryProcessQueryDTO.getPreNodeHandler());
        }

        if(safetySummaryProcessQueryDTO.getFlowStatus()!=null){
            queryWrapper.eq(YtsafetySummaryProcess::getFlowStatus,safetySummaryProcessQueryDTO.getFlowStatus());
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getHandlerDate,safetySummaryProcessQueryDTO.getHandleTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getHandlerDate,safetySummaryProcessQueryDTO.getHandleTimeEnd());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            queryWrapper.ge(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeStart());
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            queryWrapper.le(YtsafetySummaryProcess::getCreateTime,safetySummaryProcessQueryDTO.getCreateTimeEnd());
        }

        LoginUser user = BusUtits.getLoginUser();
        queryWrapper.eq(YtsafetySummaryProcess::getApplyNo,user.getEmpNo());
        queryWrapper.ne(YtsafetySummaryProcess::getFlowStatus,0);
        return safetySummaryProcessMapper.queryMyApply(page,queryWrapper);
    }

    /**
     * 设置时间格式
     * @param safetySummaryProcessQueryDTO
     */
    private void setDateFormat(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO){

        if(safetySummaryProcessQueryDTO.getCreateTimeStart()!=null){
            String startDate = DateUtils.formatDate(safetySummaryProcessQueryDTO.getCreateTimeStart(),"yyyy-MM-dd")+" 00:00:00";
            safetySummaryProcessQueryDTO.setCreateTimeStart(DateUtils.parseDatetime(startDate));
        }

        if(safetySummaryProcessQueryDTO.getCreateTimeEnd()!=null){
            String endDate = DateUtils.formatDate(safetySummaryProcessQueryDTO.getCreateTimeEnd(),"yyyy-MM-dd")+" 23:59:59";
            safetySummaryProcessQueryDTO.setCreateTimeEnd(DateUtils.parseDatetime(endDate));
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeStart()!=null){
            String startDate = DateUtils.formatDate(safetySummaryProcessQueryDTO.getHandleTimeStart(),"yyyy-MM-dd")+" 00:00:00";
            safetySummaryProcessQueryDTO.setHandleTimeStart(DateUtils.parseDatetime(startDate));
        }

        if(safetySummaryProcessQueryDTO.getHandleTimeEnd()!=null){
            String endDate = DateUtils.formatDate(safetySummaryProcessQueryDTO.getHandleTimeEnd(),"yyyy-MM-dd")+" 23:59:59";
            safetySummaryProcessQueryDTO.setHandleTimeEnd(DateUtils.parseDatetime(endDate));
        }
    }

    @Override
    public YtsafetySummaryProcess addSafetySummaryProcess(BpmReponseParamsVO reponseParamsVO) {
        YtsafetySummaryProcess processTemp =  safetySummaryProcessMapper.selectById(reponseParamsVO.getFormInstanceId());
        YtsafetySummaryProcess process = new YtsafetySummaryProcess();
        if(processTemp==null) {
            BeanUtils.copyProperties(reponseParamsVO, process);
            process.setId(reponseParamsVO.getFormInstanceId());
            process.setApplyStartTime(reponseParamsVO.getStartDate());
            process.setFlowSubject(reponseParamsVO.getFlowSubject());
            safetySummaryProcessMapper.insert(process);
        }else{
            BeanUtils.copyProperties(reponseParamsVO, processTemp);
            processTemp.setId(reponseParamsVO.getFormInstanceId());
            processTemp.setApplyStartTime(reponseParamsVO.getStartDate());
            processTemp.setFlowSubject(reponseParamsVO.getFlowSubject());
            safetySummaryProcessMapper.updateById(processTemp);
        }
        return process;
    }

    @Override
    public BpmReponseParamsVO createAndApproveProcess(BpmRequestParamsDTO dto) {
        BpmReponseParamsVO vo = new BpmReponseParamsVO();
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(dto.getFormInstanceId());
        if (process != null&& ProcessStatusEnums.draft.getCode()!=process.getFlowStatus()){
            BeanUtils.copyProperties(process, vo);
            return vo;
        }
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(dto.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(dto.getFormInstanceId()), bpmSysId, model.getModelId(), model.getFormId());
        String creator = JaxbXmlUtil.CombineCreator(dto.getApplyEipName());
        if(StringUtils.isNotEmpty(dto.getIsNickname())&&dto.getIsNickname().equals("1")){
            creator = JaxbXmlUtil.CombineCreator(nickAccount);
            vo.setIsNickName(dto.getIsNickname());
            vo.setNickName(dto.getNickName());
            vo.setNickAccount(nickAccount);
            vo.setPreNodeHandler(nickAccount);
            vo.setPreNodeHandlerName(dto.getNickName());
        }
        String extParam = JaxbXmlUtil.CombineExParam(dto.getFlowSubject());

        String form_url = safetyBpmModelMapper.queryModelFormUrl(model.getPermId());
        String flowId = createAndApproveProcess(dto.getFormInstanceId(), formId, creator, extParam);
        NodeInfo nodeInfo = queryCurrentNodeInfo(formId, flowId);
        vo.setFormInstanceId(dto.getFormInstanceId());
        vo.setModelFlag(dto.getModelFlag());
        vo.setApplyNo(dto.getApplyNo());
        vo.setApplyName(dto.getApplyName());
        vo.setStartDate(dto.getStartDate());
        vo.setFlowId(flowId);
        vo.setFlowCode(dto.getFlowCode());
        vo.setFlowName(model.getModelName());
        vo.setModelFlag(model.getModelId());
        vo.setNodeName(nodeInfo.getNodeName());
        vo.setNodeId(nodeInfo.getNodeId());
        vo.setTaskId(nodeInfo.getTaskId());
        vo.setFlowSubject(dto.getFlowSubject());
        vo.setFormUrl(form_url);
        vo.setCurNodeHandler(dto.getCurNodeHandler());
        vo.setFlowStatus(ProcessStatusEnums.approve.getCode());
        vo.setHandlerDate(new Date());
        vo.setApplyDepartName(dto.getApplyDepartName());
        vo.setApplyDepartCode(dto.getApplyDepartCode());
        vo.setFactoryId(dto.getFactoryId());
        vo.setFactoryName(dto.getFactoryName());
        vo.setApplyLogin(dto.getLoginName());
        GetFlowLogList logs = new GetFlowLogList();
        logs.setFormId(formId);
        logs.setProcessId(flowId);

        //同步待办
        addSafetySummaryProcess(vo);
        return vo;
    }

    @Override
    public BpmReponseParamsVO createProcess(BpmRequestParamsDTO dto) {
        BpmReponseParamsVO vo = new BpmReponseParamsVO();
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(dto.getFormInstanceId());
        if (process != null&& ProcessStatusEnums.draft.getCode()!=process.getFlowStatus()){
            BeanUtils.copyProperties(process, vo);
            return vo;
        }
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(dto.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(dto.getFormInstanceId()), bpmSysId, model.getModelId(), model.getFormId());
        String creator = JaxbXmlUtil.CombineCreator(dto.getApplyEipName());
        String extParam = JaxbXmlUtil.CombineExParam(dto.getFlowSubject());
        String form_url = safetyBpmModelMapper.queryModelFormUrl(model.getPermId());
        String flowId = createProcess(dto.getFormInstanceId(), formId, creator, extParam);
        NodeInfo nodeInfo = queryCurrentNodeInfo(formId, flowId);
        vo.setFormInstanceId(dto.getFormInstanceId());
        vo.setModelFlag(dto.getModelFlag());
        vo.setApplyNo(dto.getApplyNo());
        vo.setApplyName(dto.getApplyName());
        vo.setStartDate(dto.getStartDate());
        vo.setFlowId(flowId);
        vo.setFlowCode(dto.getFlowCode());
        vo.setFlowName(model.getModelName());
        vo.setModelFlag(model.getModelId());
        vo.setNodeName(nodeInfo.getNodeName());
        vo.setNodeId(nodeInfo.getNodeId());
        vo.setTaskId(nodeInfo.getTaskId());
        vo.setFlowSubject(dto.getFlowSubject());
        vo.setFormUrl(form_url);
        vo.setCurNodeHandler(dto.getCurNodeHandler());
        vo.setPreNodeHandler(dto.getPreNodeHandler());
        vo.setPreNodeHandlerName(dto.getPreNodeHandlerName());
        vo.setFlowStatus(ProcessStatusEnums.approve.getCode());
        vo.setHandlerDate(new Date());
        vo.setApplyDepartName(dto.getApplyDepartName());
        vo.setApplyDepartCode(dto.getApplyDepartCode());
        vo.setFactoryId(dto.getFactoryId());
        vo.setFactoryName(dto.getFactoryName());
        vo.setApplyLogin(dto.getLoginName());
        GetFlowLogList logs = new GetFlowLogList();
        logs.setFormId(formId);
        logs.setProcessId(flowId);

        //同步待办
        addSafetySummaryProcess(vo);
        return vo;
    }
    /**
     * 获取流程主键
     */
    private String  createProcess(Long formInstanceId,String formId,String creator,String extParam) {
        CreateProcess createProcess = new CreateProcess();
        createProcess.setExParam(extParam);
        createProcess.setCreator(creator);
        createProcess.setFormId(formId);
        CreateProcessResponse processResponse = bpmProcessService.createProcess(createProcess);
        if(!processResponse.isSuccess()) throw new RuntimeException(processResponse.getErrorMsg());
        return processResponse.getFlowId();
    }


    @Override
    public YtsafetySummaryProcess saveDraft(BpmRequestParamsDTO dto) {
        BpmReponseParamsVO vo = new BpmReponseParamsVO();
        BeanUtils.copyProperties(dto,vo);
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(dto.getModelFlag());
        String form_url = safetyBpmModelMapper.queryModelFormUrl(model.getPermId());
        vo.setModelFlag(model.getModelId());
        vo.setFormUrl(form_url);
        vo.setFlowStatus(ProcessStatusEnums.draft.getCode());
        vo.setFlowName(model.getModelName());
        vo.setApplyLogin(dto.getLoginName());
        return addSafetySummaryProcess(vo);
    }

    /**
     * 获取流程主键
     */
    private String  createAndApproveProcess(Long formInstanceId,String formId,String creator,String extParam) {
        CreateAndApproveProcess createAndApproveProcess = new CreateAndApproveProcess();
        createAndApproveProcess.setFormInstanceId(formInstanceId);
        createAndApproveProcess.setFormId(formId);
        createAndApproveProcess.setCreator(creator);
        createAndApproveProcess.setExParam(extParam);
        CreateAndApproveProcessResponse createAndApproveProcessResponse = bpmProcessService.createAndApproveProcess(createAndApproveProcess);
        if(!createAndApproveProcessResponse.isSuccess()) throw new RuntimeException(createAndApproveProcessResponse.getErrorMsg());
        return createAndApproveProcessResponse.getFlowId();
    }

    /**
     * 获取当前节点
     * @param formId
     * @param flowId
     * @return
     */
    private NodeInfo queryCurrentNodeInfo(String formId, String flowId) {
        GetCurrentNodesInfo nodesInfo = new GetCurrentNodesInfo();
        nodesInfo.setFormId(formId);
        nodesInfo.setProcessId(flowId);
        GetCurrentNodesInfoResponse nodesInfoResponse = bpmProcessService.getCurrentNodesInfo(nodesInfo);
        if(!nodesInfoResponse.isSuccess()) throw new RuntimeException(nodesInfoResponse.getErrorMsg());
        List<NodeInfo> nodes = nodesInfoResponse.getCurrentNodeInfo();
        if(nodes==null||nodes.isEmpty()) return new NodeInfo();
        return nodes.get(0);
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        LoginUser user = BusUtits.getLoginUser();
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(processParamDTO.getFormInstanceId());
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(process.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(process.getId()), bpmSysId, model.getModelId(), model.getFormId());
        String creator = JaxbXmlUtil.CombineCreator(processParamDTO.getLoginName());
        Boolean  isCanApprove=isCanApproval(process.getId(),bpmSysId,model.getModelId(),model.getFormId(),user.getEipName());
        if(process.getApplyLogin().equalsIgnoreCase(user.getEipName())&&isCanApprove==false&&process.getIsNickName().equals("1")){
            creator = JaxbXmlUtil.CombineCreator(nickAccount);
            process.setPreNodeHandler(process.getNickAccount());
            process.setPreNodeHandlerName(process.getNickName());
        }else{
            process.setPreNodeHandler(processParamDTO.getNodeHandler());
            process.setPreNodeHandlerName(processParamDTO.getNodeHandlerName());
        }

        String extParam = JaxbXmlUtil.CombineExParam(process.getFlowSubject());
        Parameter parameter = new Parameter();
        parameter.setActivityType(processParamDTO.getActivityType());
        parameter.setOperationType(processParamDTO.getOperationType());
        parameter.setParam(processParamDTO.getParam());
        parameter.setTaskId(processParamDTO.getTaskId());
        ApproveProcess approveProcess = new ApproveProcess();
        approveProcess.setCurNodeId(process.getNodeId());
        approveProcess.setFormInstanceId(process.getId());
        approveProcess.setFormId(formId);
        approveProcess.setProcessId(process.getFlowId());
        approveProcess.setHandler(creator);
        approveProcess.setProcessParam(JaxbXmlUtil.CombineParameterJson(parameter));
        process.setFlowStatus(JaxbXmlUtil.getProcessStatus(processParamDTO.getOperationType()));
        Date handlerDate = new Date();
        process.setHandlerDate(handlerDate);
        if("handler_refuse".equals(processParamDTO.getOperationType())){
            process.setDenyReason(processParamDTO.getParam().getAuditNote());
        }
        safetySummaryProcessMapper.updateById(process);
        if(ProcessStatusEnums.discarded.getCode() != process.getFlowStatus()&&ProcessStatusEnums.finish.getCode()!=process.getFlowStatus()) {
            ApproveProcessResponse response = bpmProcessService.approveProcess(approveProcess);
            if (!response.isSuccess()) throw new RuntimeException(response.getErrorMsg());
        }
        BpmReponseParamsVO vo = new BpmReponseParamsVO();
        BeanUtils.copyProperties(process, vo);
        vo.setFormInstanceId(process.getId());
        return vo;
    }

    @Override
    public boolean abandoned(Long formInstanceId) {
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(formInstanceId);
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(process.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(process.getId()), bpmSysId, model.getModelId(), model.getFormId());
        ExistProcess existProcess=new ExistProcess();
        existProcess.setFormId(formId);
        ExistProcessResponse approveProcessResponse =bpmProcessService.IsExistProcess(existProcess);
        return approveProcessResponse.isSuccess();
    }


    @Override
    public List<FlowInfoApprover> getFlowInfo(Long formInstanceId) {
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(formInstanceId);
        FlowInfo nodeInfo=new FlowInfo();
        nodeInfo.setModelIds(process.getFlowId());
        FlowInfoResponse flowInfoResponse=bpmProcessService.selfFlowInfoService(nodeInfo);

        return flowInfoResponse.getFlowInfoApprover();
    }

    @Override
    public void updataProcessStatus(Long formInstanceId) {
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(formInstanceId);
        BpmMethodProcessDTO bpmTemplateFormVO=new BpmMethodProcessDTO();
        FunctionIdEntity functionIdEntity=new FunctionIdEntity();
        functionIdEntity.setFunctionId("process_abandon");
        bpmTemplateFormVO.setFunctionId(functionIdEntity);
        FormIdEntity formIdEntity=new FormIdEntity();
        formIdEntity.setFormInstanceId(String.valueOf(formInstanceId));
        formIdEntity.setModelId(process.getModelFlag());
        bpmTemplateFormVO.setFormId(formIdEntity);
        eventPublisher.publishEvent(bpmTemplateFormVO);
        tsafetySummaryProcessMapper.updateSummaryProcessStatus(ProcessStatusEnums.discarded.getCode(),formInstanceId+"");
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public YtsafetySummaryProcess deleteDraftById(Long id) {
        YtsafetySummaryProcess process =  safetySummaryProcessMapper.selectById(id);
        if(process==null||process.getFlowStatus()!=0) throw new RuntimeException("草稿信息不存在！");
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(process.getModelFlag());
        safetySummaryProcessMapper.deleteDraftById(id,model.getFormId());
        safetySummaryProcessMapper.deleteDraftExtendById(id,model.getFormId());
        if(StringUtils.isNotEmpty(model.getFormDetail())){
            safetySummaryProcessMapper.deleteDraftExtendDetailsById(id,model.getFormDetail());
        }
        return process;
    }

    @Override
    public void batchDeleteDraftById(List<Long> ids) {
        for (Long id : ids) {
            deleteDraftById(id);
        }
    }

    public TokenVO getTemporaryToken() {
        // OkHttpClient自带CookieJar自动管理cookie
        TokenVO tokenVO=new TokenVO();
        OkHttpClient client = new OkHttpClient();
        String temporaryToken="";
        // 表单参数构建
        RequestBody formBody = new FormBody.Builder()
                .add("username", nickAccount)
                .add("password", "L9D5xVDe5FNgYlWAZOvcgg==")
                .add("service", "")
                .build();
        Request request = new Request.Builder()
                .url(eipLogin)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            // 1. 获取所有Set-Cookie
            List<Cookie> cookieList = Cookie.parseAll(request.url(), response.headers());
            System.out.println("=== 全部Cookie ===");
            StringBuilder cookieTotal = new StringBuilder();
            for (Cookie cookie : cookieList) {
                System.out.println(cookie.toString());
                if(cookie.name().equals("MP-X-Access-Token")){
                    tokenVO.setTemporaryToken(cookie.value());
                }
                if(cookie.name().equals("LRToken")){
                    tokenVO.setLRToken(cookie.value());
                }
                cookieTotal.append(cookie.name()).append("=").append(cookie.value()).append("; ");
            }
            System.out.println(cookieTotal.toString().trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tokenVO;
    }



    @Override
    public BmpProcessDetailsVO queryProcessModelInfo(Long formInstanceId, String loginName) {
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(formInstanceId);
        BmpProcessDetailsVO vo= new BmpProcessDetailsVO();
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(process.getModelFlag());
        vo.setSysId(bpmSysId);
        vo.setTaskId(process.getTaskId());
        vo.setFlowId(process.getFlowId());
        vo.setModelFlag(model.getModelId());
        vo.setFormDetail(model.getFormDetail());
        vo.setId(process.getId());
        vo.setFormId(model.getFormId());
        vo.setIsCanEdit(false);
        vo.setIsCanApprove(false);
        vo.setFlowStatus(process.getFlowStatus());
        vo.setApplyStartTime(process.getApplyStartTime());
        if(process.getFlowStatus()!=null&&process.getFlowStatus()==ProcessStatusEnums.draft.getCode()) {
            vo.setIsCanEdit(true);
            vo.setIsCanApprove(true);
            return vo;
        }
        if(process.getNodeId()!=null && process.getNodeId().equals(ProcessConstant.draftNodeId) && loginName.equalsIgnoreCase(process.getApplyLogin())){
            vo.setIsCanEdit(true);
            vo.setIsCanApprove(true);
            return vo;
        }
        if(process.getFlowStatus()!=null&&process.getFlowStatus()==ProcessStatusEnums.deny.getCode()&& ProcessConstant.draftNodeId.equals(process.getNodeId())&& loginName.equalsIgnoreCase(process.getApplyLogin())){
            vo.setIsCanEdit(true);
        }
        if(process.getFlowStatus()!=null&&process.getFlowStatus()==ProcessStatusEnums.finish.getCode()){
            vo.setIsCanApprove(false);
        }else{
            vo.setIsCanApprove(isCanApproval(process.getId(),bpmSysId,model.getModelId(),model.getFormId(),loginName));
            if(process.getApplyLogin().equalsIgnoreCase(loginName)&&vo.getIsCanApprove()==false&&process.getIsNickName().equals("1")){
                vo.setIsCanApprove(isCanApproval(process.getId(),bpmSysId,model.getModelId(),model.getFormId(),nickAccount));
                if(vo.getIsCanApprove()) {
                    TokenVO tokenVO = getTemporaryToken();
                    if (tokenVO != null) {
                        vo.setTemporaryToken(tokenVO.getTemporaryToken());
                        vo.setLRToken(tokenVO.getLRToken());
                    }
                }
            }
        }
        return vo;
    }

    private Boolean isCanApproval(Long id,String bpmSysId,String modelId,String formId,String loginName){
        String formIdStr = JaxbXmlUtil.CombineFormId(String.valueOf(id), bpmSysId,modelId,formId);
        String creator = JaxbXmlUtil.CombineCreator(loginName);
        CanApprovalProcess canApprovalProcess = new CanApprovalProcess();
        canApprovalProcess.setActionUid(creator);
        canApprovalProcess.setFormId(formIdStr);
        CanApprovalProcessResponse canApproval =  bpmProcessService.queryCanApproveProcess(canApprovalProcess);
        return canApproval.isSuccess();
    }

    @Override
    public void passProcess(BpmPassProcessDTO dto) {
        YtsafetySummaryProcess process = safetySummaryProcessMapper.selectById(dto.getProcessId());
        if(process==null) throw new RuntimeException("传阅流程不存在");
        if(process.getFlowStatus()==ProcessStatusEnums.draft.getCode()) throw new RuntimeException("草稿状态下不能传阅");
        PassProcess passProcess = new PassProcess();
        passProcess.setProcessId(process.getFlowId());
        passProcess.setCirculated(JaxbXmlUtil.CombineCreator(dto.getCirculated()));
        passProcess.setBeCirculated(JaxbXmlUtil.CombineCreator(dto.getBeCirculated()));
        PassProcessResponse passProcessResponse = bpmProcessService.setProcessCirculate(passProcess);
        if(!passProcessResponse.isSuccess()) throw new RuntimeException(passProcessResponse.getErrorMsg());
    }

    @Override
    public void syncProcessInfo(Long id) {
        YtsafetySummaryProcess processRecord = safetySummaryProcessMapper.selectById(id);
        if (processRecord == null) throw new RuntimeException("流程记录不存在");
        if(ProcessStatusEnums.discarded.getCode()== processRecord.getFlowStatus()) throw new RuntimeException("流程已废弃");
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(processRecord.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(processRecord.getId()), bpmSysId, model.getModelId(), model.getFormId());
        String processId = processRecord.getFlowId();

        NodeInfo node = queryCurrentNodeInfo(formId, processId);
        YtsafetySummaryProcess process = new YtsafetySummaryProcess();
        process.setId(id);
        process.setNodeId(node.getNodeId() == null ? "" : node.getNodeId());
        process.setNodeName(node.getNodeName() == null ? "" : node.getNodeName());
        process.setTaskId(node.getTaskId() == null ? "" : node.getTaskId());
        if (StringUtils.isEmpty(node.getTaskId()) || ProcessConstant.finishNodeId.equals(node.getNodeId())) {
            process.setFlowStatus(ProcessStatusEnums.finish.getCode());
            process.setApplyEndTime(new Date());
        }
        safetySummaryProcessMapper.updateById(process);

        ProcessApprovers approvers = bpmProcessService.getPorcessApprovers(id, formId, processId);
        if (approvers != null) {
            summaryProcessNextapproverService.saveNextApprover(id, approvers.getCurApprover());
            summaryProcessApproverService.saveApproverRecord(id, approvers.getApproverRecord());
        }
    }

    @Override
    public NodeInfo getCurrentNodeInfo(Long id) {
        YtsafetySummaryProcess processRecord = safetySummaryProcessMapper.selectById(id);
        if (processRecord == null) throw new RuntimeException("流程记录不存在");
        if(ProcessStatusEnums.discarded.getCode()== processRecord.getFlowStatus()) throw new RuntimeException("流程已废弃");
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(processRecord.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(processRecord.getId()), bpmSysId, model.getModelId(), model.getFormId());
        String processId = processRecord.getFlowId();
        NodeInfo node = queryCurrentNodeInfo(formId, processId);
        return node;
    }

    @Override
    public List<FieldAuth> queryFieldAuth(Long formInstanceId) {
        YtsafetySummaryProcess processRecord = safetySummaryProcessMapper.selectById(formInstanceId);
        if (processRecord == null) throw new RuntimeException("流程记录不存在");
        if(StringUtils.isEmpty(processRecord.getNodeId())) return new ArrayList<FieldAuth>();
        SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(processRecord.getModelFlag());
        String formId = JaxbXmlUtil.CombineFormId(String.valueOf(processRecord.getId()), bpmSysId, model.getModelId(), model.getFormId());
        String processId = processRecord.getFlowId();
        GetFieldAuthInfo fieldAuthInfo = new GetFieldAuthInfo();
        fieldAuthInfo.setProcessId(processId);
        fieldAuthInfo.setNodeFactIds(processRecord.getNodeId());
        fieldAuthInfo.setFormId(formId);
        GetFieldAuthInfoResponse fieldAuths =  bpmProcessService.getAuthFields(fieldAuthInfo);
        if(!fieldAuths.isSuccess()) return new ArrayList<FieldAuth>();
        return fieldAuths.getFieldAuth();
    }

    /**
     * 定时更新流程信息
     */
    @Scheduled(fixedRate = 1000000)
    public void syncProcessInfo() {
        LambdaQueryWrapper<YtsafetySummaryProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetySummaryProcess::getIsSynchro, "1");
        wrapper.eq(YtsafetySummaryProcess::getDelFlag, String.valueOf(CommonConstant.DEL_FLAG_0));
        List<YtsafetySummaryProcess> processRecordList = this.safetySummaryProcessMapper.selectList(wrapper);
        for(YtsafetySummaryProcess processRecord: processRecordList) {
            if (processRecord == null) throw new RuntimeException("流程记录不存在");
            Long id=processRecord.getId();
            SafetyBpmModel model = safetyBpmModelMapper.queryModelByFlag(processRecord.getModelFlag());
            String formId = JaxbXmlUtil.CombineFormId(String.valueOf(processRecord.getId()), bpmSysId, model.getModelId(), model.getFormId());
            String processId = processRecord.getFlowId();

            NodeInfo node = queryCurrentNodeInfo(formId, processId);
            YtsafetySummaryProcess process = new YtsafetySummaryProcess();
            process.setId(id);
            process.setNodeId(node.getNodeId() == null ? "" : node.getNodeId());
            process.setNodeName(node.getNodeName() == null ? "" : node.getNodeName());
            process.setTaskId(node.getTaskId() == null ? "" : node.getTaskId());
            if (StringUtils.isEmpty(node.getTaskId()) || ProcessConstant.finishNodeId.equals(node.getNodeId())) {
                process.setFlowStatus(ProcessStatusEnums.finish.getCode());
                process.setApplyEndTime(new Date());
            }
            process.setIsSynchro("0");
            safetySummaryProcessMapper.updateById(process);
            ProcessApprovers approvers = bpmProcessService.getPorcessApprovers(id, formId, processId);
            if (approvers != null) {
                summaryProcessNextapproverService.saveNextApprover(id, approvers.getCurApprover());
                summaryProcessApproverService.saveApproverRecord(id, approvers.getApproverRecord());
            }
        }
    }

    @Async
    @Override
    public void  asynchronousUpdateProcess(Long formInstanceId) throws InterruptedException {
        Thread.sleep(15000);
        if(abandoned(formInstanceId)){
            List<FlowInfoApprover> flowApproverList=getFlowInfo(formInstanceId);
            if(!flowApproverList.isEmpty()){
                FlowInfoApprover flowInfoApprover= flowApproverList.get(0);
                if(flowInfoApprover.getFdStatus().equals("00")){
                    updataProcessStatus(formInstanceId);
                }
            }
        }
    }
}
