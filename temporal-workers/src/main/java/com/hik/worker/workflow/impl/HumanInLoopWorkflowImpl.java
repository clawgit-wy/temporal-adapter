package com.hik.worker.workflow.impl;

import com.hik.worker.model.BatchResult;
import com.hik.worker.workflow.HumanInLoopWorkflow;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;

@Slf4j
public class HumanInLoopWorkflowImpl implements HumanInLoopWorkflow {

    private boolean approved = false;
    private boolean rejected = false;
    private String currentStatus = "WAITING_FOR_APPROVAL";

    @Override
    public BatchResult waitForApproval(String workflowId) {
        Workflow.getLogger(HumanInLoopWorkflowImpl.class)
                .info("Human-in-Loop工作流启动, 等待审批信号, workflowId: {}", workflowId);
        currentStatus = "WAITING_FOR_APPROVAL";
        boolean received = Workflow.await(Duration.ofDays(7), () -> approved || rejected);
        if (!received) {
            currentStatus = "TIMEOUT";
            return BatchResult.builder().batchId("HUMAN_LOOP_" + workflowId).total(1).successCount(0).failCount(1).status("TIMEOUT").build();
        }
        if (approved) {
            currentStatus = "APPROVED";
            Workflow.getLogger(HumanInLoopWorkflowImpl.class).info("审批通过! 继续执行后续业务, workflowId: {}", workflowId);
            Workflow.sleep(Duration.ofSeconds(2));
            return BatchResult.builder().batchId("HUMAN_LOOP_" + workflowId).total(1).successCount(1).failCount(0).status("APPROVED").build();
        } else {
            currentStatus = "REJECTED";
            return BatchResult.builder().batchId("HUMAN_LOOP_" + workflowId).total(1).successCount(0).failCount(1).status("REJECTED").build();
        }
    }

    @Override
    public void approve() {
        approved = true;
    }

    @Override
    public void reject() {
        rejected = true;
    }

    @Override
    public String getStatus() {
        return currentStatus;
    }
}
