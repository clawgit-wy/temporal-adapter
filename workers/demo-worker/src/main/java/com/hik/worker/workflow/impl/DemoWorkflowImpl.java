package com.hik.worker.workflow.impl;

import com.hik.worker.activity.DemoActivity;
import com.hik.worker.model.DemoRequest;
import com.hik.worker.model.DemoResult;
import com.hik.worker.workflow.DemoWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Demo 工作流实现
 * 流程: 记录开始时间 -> Workflow.sleep 模拟初始化 -> 并行执行3个Activity -> 汇总结果
 */
@Slf4j
public class DemoWorkflowImpl implements DemoWorkflow {

    /** 调用mokapi Activity: 30s超时, 3次重试, 2s退避 */
    private final DemoActivity apiActivity = Workflow.newActivityStub(DemoActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 数据处理 Activity: 60s超时, 2次重试 */
    private final DemoActivity processActivity = Workflow.newActivityStub(DemoActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(2)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 生成报告 Activity: 30s超时, 不重试 */
    private final DemoActivity reportActivity = Workflow.newActivityStub(DemoActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build());

    /** 当前进度 (通过 QueryMethod 查询) */
    private String status = "INIT";

    @Override
    public DemoResult run(DemoRequest request) {
        Workflow.getLogger(DemoWorkflowImpl.class)
                .info("Demo 工作流启动, workflowId: {}, personId: {}", request.getWorkflowId(), request.getPersonId());

        long startTime = Workflow.currentTimeMillis();

        // Step 1: 模拟初始化处理时间
        status = "INITIALIZING";
        Workflow.sleep(Duration.ofSeconds(1));
        Workflow.getLogger(DemoWorkflowImpl.class).info(">>> Step 1: 初始化完成");

        // Step 2: 并行执行3个 Activity
        status = "RUNNING_ACTIVITIES";
        Workflow.getLogger(DemoWorkflowImpl.class).info(">>> Step 2: 并行执行3个 Activity");
        Promise<String> apiPromise = Async.function(apiActivity::callMockApi, request);
        Promise<Integer> processPromise = Async.function(processActivity::processData, request);
        Promise<String> reportPromise = Async.function(reportActivity::generateReport, request);

        String apiResponse = apiPromise.get();
        int processedCount = processPromise.get();
        String report = reportPromise.get();

        // Step 3: 汇总结果
        status = "BUILDING_RESULT";
        long duration = Workflow.currentTimeMillis() - startTime;
        Workflow.getLogger(DemoWorkflowImpl.class)
                .info(">>> Step 3: 汇总结果, processedCount: {}, duration: {}ms", processedCount, duration);

        status = "COMPLETED";
        return DemoResult.builder()
                .success(true)
                .message(report)
                .processedCount(processedCount)
                .apiResponse(apiResponse)
                .duration(duration)
                .build();
    }

    @Override
    public String getStatus() {
        return status;
    }
}
