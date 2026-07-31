package com.hik.worker.workflow.impl;

import com.hik.worker.activity.CrashRecoveryActivity;
import com.hik.worker.model.BatchResult;
import com.hik.worker.workflow.CrashRecoveryWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CrashRecoveryWorkflowImpl implements CrashRecoveryWorkflow {

    private final CrashRecoveryActivity activity = Workflow.newActivityStub(CrashRecoveryActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    @Override
    public BatchResult processBatch(int dummy) {
        Workflow.getLogger(CrashRecoveryWorkflowImpl.class)
                .info("断点续传工作流启动, WorkflowId: {}", Workflow.getInfo().getWorkflowId());
        int successCount = 0;
        int failCount = 0;
        List<String> steps = new ArrayList<>();

        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 1: 扣款开始");
        try {
            String result = activity.executeStep("Step1-扣款");
            steps.add("Step1成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 1: 扣款完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step1失败: " + e.getMessage());
            failCount++;
        }
        Workflow.sleep(Duration.ofSeconds(5));

        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 2: 下发门禁开始");
        try {
            String result = activity.executeStep("Step2-下发门禁");
            steps.add("Step2成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 2: 下发门禁完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step2失败: " + e.getMessage());
            failCount++;
        }
        Workflow.sleep(Duration.ofSeconds(5));

        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 3: 发短信开始");
        try {
            String result = activity.executeStep("Step3-发短信");
            steps.add("Step3成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 3: 发短信完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step3失败: " + e.getMessage());
            failCount++;
        }
        return BatchResult.builder()
                .batchId("CRASH_RECOVERY_" + Workflow.getInfo().getWorkflowId())
                .total(3)
                .successCount(successCount)
                .failCount(failCount)
                .status(failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED")
                .build();
    }
}
