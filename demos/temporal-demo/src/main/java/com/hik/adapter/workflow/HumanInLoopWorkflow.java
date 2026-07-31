package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 场景4: 长耗时等待与异步回调 (Human-in-the-Loop)
 * Workflow进入等待状态，直到收到approveSignal信号才继续执行
 * 验证长等待期间零CPU/内存占用，以及Signal唤醒能力
 */
@WorkflowInterface
public interface HumanInLoopWorkflow {

    /**
     * 工作流入口，等待人工审批信号
     * @param workflowId 工作流ID
     * @return 审批结果
     */
    @WorkflowMethod
    BatchResult waitForApproval(String workflowId);

    /**
     * 审批通过信号
     */
    @SignalMethod
    void approve();

    /**
     * 审批拒绝信号
     */
    @SignalMethod
    void reject();

    /**
     * 查询当前状态
     */
    @io.temporal.workflow.QueryMethod
    String getStatus();
}
