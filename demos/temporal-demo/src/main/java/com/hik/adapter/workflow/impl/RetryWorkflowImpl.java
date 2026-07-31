package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.RetryActivity;
import com.hik.adapter.model.BatchResult;
import com.hik.adapter.workflow.RetryWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 场景2实现: 调用RetryActivity，配置指数退避重试策略
 * 初始间隔1s，退避系数2.0，最大5次尝试
 */
@Slf4j
public class RetryWorkflowImpl implements RetryWorkflow {

    private final RetryActivity activity = Workflow.newActivityStub(RetryActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))   // 初始1秒
                            .setBackoffCoefficient(2.0)                   // 指数退避系数2
                            .setMaximumInterval(Duration.ofSeconds(30))
                            .setMaximumAttempts(5)                        // 最多5次尝试
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(int total) {
        Workflow.getLogger(RetryWorkflowImpl.class)
                .info("重试工作流启动, 总任务数: {}", total);

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < total; i++) {
            try {
                boolean result = activity.callWithRetry(i);
                if (result) {
                    successCount++;
                } else {
                    failCount++;
                }
                Workflow.getLogger(RetryWorkflowImpl.class)
                        .info("任务 {} 完成, 结果: {}", i, result ? "成功" : "失败");
            } catch (Exception e) {
                Workflow.getLogger(RetryWorkflowImpl.class)
                        .warn("任务 {} 最终失败(重试耗尽): {}", i, e.getMessage());
                failCount++;
            }
        }

        return BatchResult.builder()
                .batchId("RETRY_" + Workflow.getInfo().getWorkflowId())
                .total(total)
                .successCount(successCount)
                .failCount(failCount)
                .status(failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED")
                .build();
    }
}
