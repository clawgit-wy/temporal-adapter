package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.HikActivity;
import com.hik.adapter.model.*;
import com.hik.adapter.workflow.HikConcurrentWorkflow;
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
 * Demo 2: 并发批量校验工作流实现
 * 并发提交所有 Activity，由 Worker 端的并发度(15)控制实际执行速率
 * 同时演示自定义重试策略：最多重试3次，间隔2秒
 */
@Slf4j
public class HikConcurrentWorkflowImpl implements HikConcurrentWorkflow {

    private final HikActivity activity = Workflow.newActivityStub(HikActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumInterval(Duration.ofSeconds(10))
                            .setMaximumAttempts(3) // 最多重试3次
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(BatchRequest request) {
        log.info("并发批量校验工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getPersons().size());

        // 并发提交所有 Activity 调用，返回 Promise 列表
        List<Promise<PersonResult>> promises = new ArrayList<>();
        for (PersonInfo person : request.getPersons()) {
            promises.add(Async.function(activity::verifySinglePerson, person));
        }

        // 等待所有 Activity 完成
        List<PersonResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < promises.size(); i++) {
            try {
                PersonResult result = promises.get(i).get();
                results.add(result);
                if (result.isPassed()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                PersonInfo person = request.getPersons().get(i);
                log.warn("人员 {} 并发校验最终失败: {}", person.getPersonId(), e.getMessage());
                results.add(PersonResult.fail(person.getPersonId(), "校验异常: " + e.getMessage()));
                failCount++;
            }
        }

        return BatchResult.builder()
                .batchId(request.getBatchId())
                .total(request.getPersons().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED")
                .build();
    }
}
