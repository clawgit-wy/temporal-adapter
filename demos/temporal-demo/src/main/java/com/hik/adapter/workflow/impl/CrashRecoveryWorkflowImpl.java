package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.CrashRecoveryActivity;
import com.hik.adapter.model.BatchResult;
import com.hik.adapter.workflow.CrashRecoveryWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 场景3实现: 3步顺序执行，每步间5秒延迟
 * 如果Worker在第2步执行中被Kill，重启后从第2步继续
 */
@Slf4j
public class CrashRecoveryWorkflowImpl implements CrashRecoveryWorkflow {

    private final CrashRecoveryActivity activity = Workflow.newActivityStub(CrashRecoveryActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(3)
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(int dummy) {
        Workflow.getLogger(CrashRecoveryWorkflowImpl.class)
                .info("断点续传工作流启动, WorkflowId: {}", Workflow.getInfo().getWorkflowId());

        int successCount = 0;
        int failCount = 0;
        java.util.List<String> steps = new java.util.ArrayList<>();

        // Step 1: 扣款
        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 1: 扣款开始");
        try {
            String result = activity.executeStep("Step1-扣款");
            steps.add("Step1成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 1: 扣款完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step1失败: " + e.getMessage());
            failCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).error("Step 1失败", e);
        }

        // 5秒延迟
        Workflow.sleep(Duration.ofSeconds(5));

        // Step 2: 下发门禁
        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 2: 下发门禁开始");
        try {
            String result = activity.executeStep("Step2-下发门禁");
            steps.add("Step2成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 2: 下发门禁完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step2失败: " + e.getMessage());
            failCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).error("Step 2失败", e);
        }

        // 5秒延迟
        Workflow.sleep(Duration.ofSeconds(5));

        // Step 3: 发短信
        Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 3: 发短信开始");
        try {
            String result = activity.executeStep("Step3-发短信");
            steps.add("Step3成功: " + result);
            successCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).info(">>> Step 3: 发短信完成 - {}", result);
        } catch (Exception e) {
            steps.add("Step3失败: " + e.getMessage());
            failCount++;
            Workflow.getLogger(CrashRecoveryWorkflowImpl.class).error("Step 3失败", e);
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
