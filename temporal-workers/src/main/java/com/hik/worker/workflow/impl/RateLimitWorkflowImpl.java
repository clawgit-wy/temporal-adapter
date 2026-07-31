package com.hik.worker.workflow.impl;

import com.hik.worker.activity.RateLimitActivity;
import com.hik.worker.model.BatchResult;
import com.hik.worker.workflow.RateLimitWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RateLimitWorkflowImpl implements RateLimitWorkflow {

    private final RateLimitActivity activity = Workflow.newActivityStub(RateLimitActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(10)).build());

    @Override
    public BatchResult processBatch(int total) {
        Workflow.getLogger(RateLimitWorkflowImpl.class).info("限流工作流启动, 总任务数: {}", total);
        List<Promise<Boolean>> promises = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            promises.add(Async.function(activity::callApi, i));
        }
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < promises.size(); i++) {
            try {
                if (promises.get(i).get()) successCount++;
                else failCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        Workflow.getLogger(RateLimitWorkflowImpl.class).info("限流工作流完成, 成功: {}, 失败: {}", successCount, failCount);
        return BatchResult.builder()
                .batchId("RATE_LIMIT_" + Workflow.getInfo().getWorkflowId())
                .total(total)
                .successCount(successCount)
                .failCount(failCount)
                .status(failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED")
                .build();
    }
}
