package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.HikActivity;
import com.hik.adapter.model.*;
import com.hik.adapter.workflow.HikTimeoutWorkflow;
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
 * Demo 3: 带超时控制的批量校验工作流实现
 * 并发调用 + 整体超时控制
 * 超时后返回已完成部分的结果，状态标记为 TIMEOUT
 */
@Slf4j
public class HikTimeoutWorkflowImpl implements HikTimeoutWorkflow {

    private final HikActivity activity = Workflow.newActivityStub(HikActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10)) // 单次调用10秒超时
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(2) // 最多重试2次
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(BatchRequest request) {
        log.info("带超时控制的批量校验工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getPersons().size());

        // 并发提交所有 Activity
        List<Promise<PersonResult>> promises = new ArrayList<>();
        for (PersonInfo person : request.getPersons()) {
            promises.add(Async.function(activity::verifySinglePerson, person));
        }

        // 设置整体超时：60秒
        Duration overallTimeout = Duration.ofSeconds(60);
        long deadlineMs = Workflow.currentTimeMillis() + overallTimeout.toMillis();

        List<PersonResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        boolean timeout = false;

        for (int i = 0; i < promises.size(); i++) {
            // 计算剩余超时时间
            long remainingMs = deadlineMs - Workflow.currentTimeMillis();
            if (remainingMs <= 0) {
                // 已超时，未完成的标记为超时失败
                timeout = true;
                for (int j = i; j < promises.size(); j++) {
                    PersonInfo person = request.getPersons().get(j);
                    results.add(PersonResult.fail(person.getPersonId(), "整体超时，未执行"));
                    failCount++;
                }
                break;
            }

            try {
                // 带剩余超时时间等待结果
                PersonResult result = promises.get(i).get();
                results.add(result);
                if (result.isPassed()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                PersonInfo person = request.getPersons().get(i);
                log.warn("人员 {} 校验失败(超时模式): {}", person.getPersonId(), e.getMessage());
                results.add(PersonResult.fail(person.getPersonId(), "校验异常: " + e.getMessage()));
                failCount++;
            }
        }

        String status = timeout ? "TIMEOUT" : (failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED");

        log.info("工作流完成, batchId: {}, status: {}, success: {}, fail: {}",
                request.getBatchId(), status, successCount, failCount);

        return BatchResult.builder()
                .batchId(request.getBatchId())
                .total(request.getPersons().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(status)
                .build();
    }
}
