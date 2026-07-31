package com.hik.adapter.workflow.impl;

import com.hik.adapter.activity.HikActivity;
import com.hik.adapter.model.*;
import com.hik.adapter.workflow.HikBatchWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo 1: 基础批量校验工作流实现
 * 逐个调用海康API，演示最基本的批量处理流程
 */
@Slf4j
public class HikBatchWorkflowImpl implements HikBatchWorkflow {

    private final HikActivity activity = Workflow.newActivityStub(HikActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build());

    @Override
    public BatchResult processBatch(BatchRequest request) {
        log.info("基础批量校验工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getPersons().size());

        List<PersonResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        // 逐个调用海康API校验
        for (PersonInfo person : request.getPersons()) {
            try {
                PersonResult result = activity.verifySinglePerson(person);
                results.add(result);
                if (result.isPassed()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                // Activity 最终重试耗尽后仍失败，记录失败结果，继续处理下一个
                log.warn("人员 {} 校验最终失败: {}", person.getPersonId(), e.getMessage());
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
