package com.hik.worker.workflow.impl;

import com.hik.worker.activity.HikActivity;
import com.hik.worker.model.*;
import com.hik.worker.workflow.HikBatchWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HikBatchWorkflowImpl implements HikBatchWorkflow {

    private final HikActivity activity = Workflow.newActivityStub(HikActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(30)).build());

    @Override
    public BatchResult processBatch(BatchRequest request) {
        log.info("基础批量校验工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getPersons().size());
        List<PersonResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        for (PersonInfo person : request.getPersons()) {
            try {
                PersonResult result = activity.verifySinglePerson(person);
                results.add(result);
                if (result.isPassed()) successCount++;
                else failCount++;
            } catch (Exception e) {
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
