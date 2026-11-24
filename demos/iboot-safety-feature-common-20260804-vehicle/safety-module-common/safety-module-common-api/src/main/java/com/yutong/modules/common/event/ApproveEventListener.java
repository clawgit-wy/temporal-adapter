package com.yutong.modules.common.event;

import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessApproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessNowapproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.ProcessConstant;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.entity.bpm.*;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuth;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuthResponse;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ApproveEventListener {

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Autowired
    private IYtsafetySummaryProcessApproverService summaryProcessApproverService;

    @Autowired
    private IYtsafetySummaryProcessNowapproverService summaryProcessNowapproverService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Value("${ytsafety.process.config.nickAccount}")
    private String nickAccount;

    @Async // 异步执行，主线程直接返回不等待通知完成
    @EventListener(ApproveEvent.class)
    public void approveEvent(ApproveEvent event) {
        setPermit(event);
        queryCurentChange(event);
        ProcessApprovers approvers = bpmProcessService.getPorcessApprovers(event.getFormInstanceId(), event.getFormId(), event.getProcessId());
        if (approvers == null || event.getFormInstanceId() == null) return;
        List<ProcessApprover> processApproverList=approvers.getCurApprover();
        List<ProcessApprover> processApproverListNew=new ArrayList<>();
        if(event.getFormId().contains("hazard_report_process")){
            for(ProcessApprover processApprover:processApproverList){
               if(processApprover.getLoginName().equals(nickAccount)){
                   YtsafetySummaryProcess safetySummaryProcess=safetySummaryProcessService.getById(event.getFormInstanceId());
                   if(safetySummaryProcess!=null){
                       processApprover.setLoginName(safetySummaryProcess.getApplyLogin());
                       processApprover.setName(safetySummaryProcess.getApplyName());
                   }
               }
                processApproverListNew.add(processApprover);
            }
        }else{
            processApproverListNew=processApproverList;
        }
        summaryProcessNowapproverService.saveNextApprover(event.getFormInstanceId(), processApproverListNew);
        List<ProcessApprover> approverRecordList=approvers.getApproverRecord();
        List<ProcessApprover> approverRecordListNew=new ArrayList<>();
        if(event.getFormId().contains("hazard_report_process")){
            for(ProcessApprover processApprover:approverRecordList){
                if(processApprover.getLoginName().equals(nickAccount)){
                    YtsafetySummaryProcess safetySummaryProcess=safetySummaryProcessService.getById(event.getFormInstanceId());
                    if(safetySummaryProcess!=null){
                        processApprover.setLoginName(safetySummaryProcess.getApplyLogin());
                        processApprover.setName(safetySummaryProcess.getApplyName());
                    }
                }
                approverRecordListNew.add(processApprover);
            }
        }else{
            approverRecordListNew=approverRecordList;
        }
        summaryProcessApproverService.saveApproverRecord(event.getFormInstanceId(),approverRecordListNew);
    }

    /**
     * 设置特权人
     * @param event
     */
    public void setPermit(ApproveEvent event){
        if (event.isCreateFlow()) {
            GetSearchFlowAuth searchFlowAuth = new GetSearchFlowAuth();
            searchFlowAuth.setModelIds(event.getProcessId());
            GetSearchFlowAuthResponse searchFlowAuthResponse = bpmProcessService.querySearchFlowAuth(searchFlowAuth);
            List<FlowAuthUserEntity> userEntities = searchFlowAuthResponse.getFlowAuthUser();
            if (userEntities != null && !userEntities.isEmpty()) {
                StringBuilder userLogins = new StringBuilder();
                for (FlowAuthUserEntity entity : userEntities) {
                    if (entity == null || StringUtils.isEmpty(entity.getUserLogin())) continue;
                    userLogins.append(userLogins.length() == 0 ? entity.getUserLogin() : ("," + entity.getUserLogin()));
                }
                summaryProcessApproverService.savePermit(event.getFormInstanceId(), userLogins.toString());
            }
        }
    }

    /**
     * 轮询等待BPM节点变更完成，最大循环3次
     * 查询节点与传递的当前节点不一致时说明节点已变更，执行完毕
     * 一致时等待2秒后重试，最大循环3次
     */
    private void queryCurentChange(ApproveEvent event) {
        try {
            int maxRetries = 2;
            for (int i = 0; i < maxRetries; i++) {
                Thread.sleep(2000);
                YtsafetySummaryProcess process = new YtsafetySummaryProcess();
                process.setId(event.getFormInstanceId());
                NodeInfo node = queryCurrentNodeInfo(event.getFormId(), event.getProcessId());
                // 流程已完成或节点已变更，退出循环
                if (StringUtils.equals(node.getNodeId(), event.getCurNodeId())) {
                    process.setIsSynchro("1");
                    safetySummaryProcessService.updateById(process);
                    continue;
                }
                process.setNodeId(node.getNodeId() == null ? "" : node.getNodeId());
                process.setNodeName(node.getNodeName() == null ? "" : node.getNodeName());
                process.setTaskId(node.getTaskId() == null ? "" : node.getTaskId());
                if (StringUtils.isEmpty(node.getTaskId()) || ProcessConstant.finishNodeId.equals(node.getNodeId())) {
                    process.setFlowStatus(ProcessStatusEnums.finish.getCode());
                    process.setApplyEndTime(new Date());
                }
                process.setIsSynchro("0");
                safetySummaryProcessService.updateById(process);
                if (!StringUtils.equals(node.getNodeId(), event.getCurNodeId())) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
