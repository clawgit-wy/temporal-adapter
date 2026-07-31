package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.WebhookActivity;
import com.hik.adapter.model.BatchResult;
import com.hik.adapter.workflow.WebhookWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 场景5实现:
 * 1. 并发执行批量校验任务
 * 2. 汇总结果后通过Webhook Activity发送回调通知
 * 3. Webhook发送失败时自动重试（由Temporal RetryPolicy保障）
 */
@Slf4j
public class WebhookWorkflowImpl implements WebhookWorkflow {

    // 批量校验Activity（复用已有的限流Activity）
    private final WebhookActivity webhookActivity = Workflow.newActivityStub(WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(5)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(int total) {
        Workflow.getLogger(WebhookWorkflowImpl.class)
                .info("Webhook回调工作流启动, 总任务数: {}", total);

        // 阶段1: 并发执行批量校验
        List<Promise<Boolean>> promises = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            promises.add(Async.function(webhookActivity::executeTask, i));
        }

        int successCount = 0;
        int failCount = 0;
        for (Promise<Boolean> p : promises) {
            try {
                if (p.get()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        Workflow.getLogger(WebhookWorkflowImpl.class)
                .info("批量任务完成, 成功: {}, 失败: {}, 开始发送Webhook回调", successCount, failCount);

        // 阶段2: 通过Webhook Activity发送结果回调
        // 构造回调payload
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                Workflow.getInfo().getWorkflowId(), total, successCount, failCount,
                failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED");

        try {
            boolean webhookResult = webhookActivity.sendWebhook(payload);
            Workflow.getLogger(WebhookWorkflowImpl.class)
                    .info("Webhook回调发送成功: {}", webhookResult);
        } catch (Exception e) {
            Workflow.getLogger(WebhookWorkflowImpl.class)
                    .error("Webhook回调发送最终失败(重试耗尽): {}", e.getMessage());
        }

        return BatchResult.builder()
                .batchId("WEBHOOK_" + Workflow.getInfo().getWorkflowId())
                .total(total)
                .successCount(successCount)
                .failCount(failCount)
                .status(failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED")
                .build();
    }
}
