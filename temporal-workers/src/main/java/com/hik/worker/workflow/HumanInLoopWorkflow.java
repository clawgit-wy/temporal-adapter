package com.hik.worker.workflow;

import com.hik.worker.model.BatchResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface HumanInLoopWorkflow {
    @WorkflowMethod
    BatchResult waitForApproval(String workflowId);
    @SignalMethod
    void approve();
    @SignalMethod
    void reject();
    @QueryMethod
    String getStatus();
}
