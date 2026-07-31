package com.hik.adapter.workflow.impl;

import com.hik.adapter.model.BatchResult;
import com.hik.adapter.workflow.HumanInLoopWorkflow;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 场景4实现: 使用Workflow.await()等待Signal
 * 等待期间零CPU占用，收到信号后立即唤醒继续执行
 */
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

        // 等待审批信号，最多等待7天（模拟长时间等待）
        // Workflow.await() 在等待期间不占用Worker CPU/内存
        boolean received = Workflow.await(Duration.ofDays(7), () -> approved || rejected);

        if (!received) {
            // 超时
            currentStatus = "TIMEOUT";
            Workflow.getLogger(HumanInLoopWorkflowImpl.class)
                    .warn("审批超时(7天), workflowId: {}", workflowId);
            return BatchResult.builder()
                    .batchId("HUMAN_LOOP_" + workflowId)
                    .total(1)
                    .successCount(0)
                    .failCount(1)
                    .status("TIMEOUT")
                    .build();
        }

        if (approved) {
            currentStatus = "APPROVED";
            Workflow.getLogger(HumanInLoopWorkflowImpl.class)
                    .info("审批通过! 继续执行后续业务, workflowId: {}", workflowId);

            // 模拟审批后的业务处理
            Workflow.sleep(Duration.ofSeconds(2));

            return BatchResult.builder()
                    .batchId("HUMAN_LOOP_" + workflowId)
                    .total(1)
                    .successCount(1)
                    .failCount(0)
                    .status("APPROVED")
                    .build();
        } else {
            currentStatus = "REJECTED";
            Workflow.getLogger(HumanInLoopWorkflowImpl.class)
                    .info("审批拒绝, workflowId: {}", workflowId);
            return BatchResult.builder()
                    .batchId("HUMAN_LOOP_" + workflowId)
                    .total(1)
                    .successCount(0)
                    .failCount(1)
                    .status("REJECTED")
                    .build();
        }
    }

    @Override
    public void approve() {
        approved = true;
        Workflow.getLogger(HumanInLoopWorkflowImpl.class).info("收到approve信号");
    }

    @Override
    public void reject() {
        rejected = true;
        Workflow.getLogger(HumanInLoopWorkflowImpl.class).info("收到reject信号");
    }

    @Override
    public String getStatus() {
        return currentStatus;
    }
}
