package com.hik.worker.workflow.impl;

import com.hik.worker.activity.HikActivity;
import com.hik.worker.model.*;
import com.hik.worker.workflow.HikTimeoutWorkflow;
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
public class HikTimeoutWorkflowImpl implements HikTimeoutWorkflow {

    private final HikActivity activity = Workflow.newActivityStub(HikActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(2)
                            .build())
                    .build());

    @Override
    public BatchResult processBatch(BatchRequest request) {
        log.info("带超时控制的批量校验工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getPersons().size());
        List<Promise<PersonResult>> promises = new ArrayList<>();
        for (PersonInfo person : request.getPersons()) {
            promises.add(Async.function(activity::verifySinglePerson, person));
        }
        Duration overallTimeout = Duration.ofSeconds(60);
        long deadlineMs = Workflow.currentTimeMillis() + overallTimeout.toMillis();
        List<PersonResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        boolean timeout = false;
        for (int i = 0; i < promises.size(); i++) {
            long remainingMs = deadlineMs - Workflow.currentTimeMillis();
            if (remainingMs <= 0) {
                timeout = true;
                for (int j = i; j < promises.size(); j++) {
                    PersonInfo person = request.getPersons().get(j);
                    results.add(PersonResult.fail(person.getPersonId(), "整体超时，未执行"));
                    failCount++;
                }
                break;
            }
            try {
                PersonResult result = promises.get(i).get();
                results.add(result);
                if (result.isPassed()) successCount++;
                else failCount++;
            } catch (Exception e) {
                PersonInfo person = request.getPersons().get(i);
                results.add(PersonResult.fail(person.getPersonId(), "校验异常: " + e.getMessage()));
                failCount++;
            }
        }
        String status = timeout ? "TIMEOUT" : (failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED");
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
