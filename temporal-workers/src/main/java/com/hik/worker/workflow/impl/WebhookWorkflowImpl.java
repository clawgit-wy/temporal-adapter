package com.hik.worker.workflow.impl;

import com.hik.worker.activity.WebhookActivity;
import com.hik.worker.model.BatchResult;
import com.hik.worker.workflow.WebhookWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebhookWorkflowImpl implements WebhookWorkflow {

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
        Workflow.getLogger(WebhookWorkflowImpl.class).info("Webhook回调工作流启动, 总任务数: {}", total);
        List<Promise<Boolean>> promises = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            promises.add(Async.function(webhookActivity::executeTask, i));
        }
        int successCount = 0;
        int failCount = 0;
        for (Promise<Boolean> p : promises) {
            try {
                if (p.get()) successCount++;
                else failCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        Workflow.getLogger(WebhookWorkflowImpl.class)
                .info("批量任务完成, 成功: {}, 失败: {}, 开始发送Webhook回调", successCount, failCount);
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                Workflow.getInfo().getWorkflowId(), total, successCount, failCount,
                failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED");
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(WebhookWorkflowImpl.class).error("Webhook回调发送最终失败: {}", e.getMessage());
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
