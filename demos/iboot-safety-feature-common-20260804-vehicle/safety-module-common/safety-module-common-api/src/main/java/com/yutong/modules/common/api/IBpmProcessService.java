package com.yutong.modules.common.api;

import com.yutong.modules.common.entity.bpm.ProcessApprovers;
import com.yutong.modules.common.entity.bpm.Validate.ValidateProcess;
import com.yutong.modules.common.entity.bpm.Validate.ValidateProcessResponse;
import com.yutong.modules.common.entity.bpm.approve.*;
import com.yutong.modules.common.entity.bpm.approver.GetApproverList;
import com.yutong.modules.common.entity.bpm.approver.GetApproverListResponse;
import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionList;
import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionListResponse;
import com.yutong.modules.common.entity.bpm.create.CreateProcess;
import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcess;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessResponse;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfo;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfoResponse;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuth;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuthResponse;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoResponse;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogList;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogListResponse;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoResponse;
import com.yutong.modules.common.entity.bpm.operation.GetOperationList;
import com.yutong.modules.common.entity.bpm.operation.GetOperationListResponse;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcess;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcessResponse;
import com.yutong.modules.common.entity.bpm.tasks.GetTasksInfo;
import com.yutong.modules.common.entity.bpm.tasks.GetTasksInfoResponse;

public interface IBpmProcessService {

    /**
     * 创建BPM流程
     * @param createProcess
     * @return
     */
    public CreateProcessResponse createProcess(CreateProcess createProcess);



    /**
     * 获取当前节点
     * @param nodeInfo
     * @return
     */
    public GetCurrentNodesInfoResponse getCurrentNodesInfo(GetCurrentNodesInfo nodeInfo);

    /**
     * 审批流程
     * @param approveProcess
     * @return
     */
    public ApproveProcessResponse approveProcess(ApproveProcess approveProcess);

    /**
     * 获取流程节点信息
     * @param nodeInfo
     * @return
     */
    public FlowInfoResponse selfFlowInfoService(FlowInfo nodeInfo);

    /**
     * 流程是否存在
     * @param existProcess
     * @return
     */
    public ExistProcessResponse IsExistProcess(ExistProcess existProcess);

    /**
     * 创建并审批
     * @param createAndApproveProcess
     * @return
     */
    public CreateAndApproveProcessResponse createAndApproveProcess(CreateAndApproveProcess createAndApproveProcess);

    /**
     * 获取节点权限
     * @param getFieldAuthInfo
     * @return
     */
    public GetFieldAuthInfoResponse getAuthFields(GetFieldAuthInfo getFieldAuthInfo);

    /**
     * 获取当前节点处理人
     * @param approverList
     * @return
     */
    public GetApproverListResponse getApproverList(GetApproverList approverList);

    /**
     * 获取流程审批记录集合
     * @param auditOptionList
     * @return
     */
    public GetAuditOptionListResponse getAuditOptionList(GetAuditOptionList auditOptionList) ;

    /**
     * 获取流程操作清单
     * @param operationList
     * @return
     */
    public GetOperationListResponse getOperationList(GetOperationList operationList);

    /**
     * 获取任务信息
     * @param tasksInfo
     * @return
     */
    public GetTasksInfoResponse getTasksInfo(GetTasksInfo tasksInfo) ;

    /**
     * 流程合法性校验
     * @param validateProcess
     * @return
     */
    public ValidateProcessResponse validateProcess(ValidateProcess validateProcess) ;

    /**
     * 获取任务信息
     * @param flowLogList
     * @return
     */
    public GetFlowLogListResponse getFlowLogInfo(GetFlowLogList flowLogList) ;

    /**
     * 获取审批人记录
     * @return
     */
    public ProcessApprovers getPorcessApprovers(Long formInstanceId,String formId, String processId) ;

    /**
     * 流程传阅
     * @param passProcess
     * @return
     */
    public PassProcessResponse setProcessCirculate(PassProcess passProcess);

    /**
     * 登录人是否有审批操作权限
     * @param canApprovalProcess
     * @return
     */
    public CanApprovalProcessResponse queryCanApproveProcess(CanApprovalProcess canApprovalProcess) ;

    /**
     * 批量获取流程相关人列表
     * @param searchFlowAuth
     * @return
     */
    public GetSearchFlowAuthResponse querySearchFlowAuth(GetSearchFlowAuth searchFlowAuth);
}
