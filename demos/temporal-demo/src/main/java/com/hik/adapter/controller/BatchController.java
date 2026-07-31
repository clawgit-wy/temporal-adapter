package com.hik.adapter.controller;

import com.hik.adapter.model.*;
import com.hik.adapter.workflow.HikBatchWorkflow;
import com.hik.adapter.workflow.HikConcurrentWorkflow;
import com.hik.adapter.workflow.HikTimeoutWorkflow;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 批量校验任务提交 Controller
 * 提供 3 种工作流的触发接口 + 状态查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/hik")
public class BatchController {

    @Autowired
    private WorkflowClient workflowClient;

    @Autowired
    private WorkflowServiceStubs workflowServiceStubs;

    private static final String TASK_QUEUE = "HIK_BATCH_TASK_QUEUE";

    /**
     * Demo 1: 触发基础批量校验工作流（顺序处理）
     * POST /api/hik/batch?count=10
     */
    @PostMapping("/batch")
    public Map<String, Object> submitBatch(@RequestParam(defaultValue = "10") int count) {
        return submitWorkflow(count, "basic", HikBatchWorkflow.class);
    }

    /**
     * Demo 2: 触发并发批量校验工作流（并发+重试）
     * POST /api/hik/concurrent?count=20
     */
    @PostMapping("/concurrent")
    public Map<String, Object> submitConcurrent(@RequestParam(defaultValue = "20") int count) {
        return submitWorkflow(count, "concurrent", HikConcurrentWorkflow.class);
    }

    /**
     * Demo 3: 触发带超时控制的批量校验工作流
     * POST /api/hik/timeout?count=30
     */
    @PostMapping("/timeout")
    public Map<String, Object> submitTimeout(@RequestParam(defaultValue = "30") int count) {
        return submitWorkflow(count, "timeout", HikTimeoutWorkflow.class);
    }

    /**
     * 同步等待工作流结果（阻塞直到完成）
     * POST /api/hik/batch-sync?count=5
     */
    @PostMapping("/batch-sync")
    public BatchResult submitBatchSync(@RequestParam(defaultValue = "5") int count) {
        String batchId = "BATCH_" + System.currentTimeMillis();
        List<PersonInfo> persons = generatePersons(count);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId(batchId)
                .build();

        HikBatchWorkflow workflow = workflowClient.newWorkflowStub(HikBatchWorkflow.class, options);
        log.info("同步提交批量校验任务, batchId: {}, 人数: {}", batchId, count);

        // 同步阻塞调用，等待结果返回
        return workflow.processBatch(BatchRequest.builder().batchId(batchId).persons(persons).build());
    }

    /**
     * 查询工作流状态
     * GET /api/hik/status/{workflowId}
     */
    @GetMapping("/status/{workflowId}")
    public Map<String, Object> getStatus(@PathVariable String workflowId) {
        try {
            DescribeWorkflowExecutionRequest request = DescribeWorkflowExecutionRequest.newBuilder()
                    .setNamespace("default")
                    .setExecution(io.temporal.api.common.v1.WorkflowExecution.newBuilder()
                            .setWorkflowId(workflowId)
                            .build())
                    .build();

            var response = workflowServiceStubs.blockingStub().describeWorkflowExecution(request);

            String status = response.getWorkflowExecutionInfo().getStatus().name();
            long startTime = response.getWorkflowExecutionInfo().getStartTime().getSeconds() * 1000;
            long closeTime = response.getWorkflowExecutionInfo().getCloseTime().getSeconds() * 1000;
            long duration = closeTime > 0 ? closeTime - startTime : System.currentTimeMillis() - startTime;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("workflowId", workflowId);
            result.put("status", status);
            result.put("durationMs", duration);
            result.put("startTime", startTime);
            result.put("taskQueue", response.getWorkflowExecutionInfo().getTaskQueue());
            return result;
        } catch (Exception e) {
            log.error("查询工作流状态失败, workflowId: {}", workflowId, e);
            return Map.of("workflowId", workflowId, "error", e.getMessage());
        }
    }

    /**
     * 获取工作流结果（如果已完成）
     * GET /api/hik/result/{workflowId}
     */
    @GetMapping("/result/{workflowId}")
    public Map<String, Object> getResult(@PathVariable String workflowId) {
        try {
            // 查询工作流状态
            DescribeWorkflowExecutionRequest request = DescribeWorkflowExecutionRequest.newBuilder()
                    .setNamespace("default")
                    .setExecution(io.temporal.api.common.v1.WorkflowExecution.newBuilder()
                            .setWorkflowId(workflowId)
                            .build())
                    .build();

            var response = workflowServiceStubs.blockingStub().describeWorkflowExecution(request);
            WorkflowExecutionStatus status = response.getWorkflowExecutionInfo().getStatus();

            if (status == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED) {
                // 已完成，尝试获取结果
                HikBatchWorkflow workflow = workflowClient.newWorkflowStub(HikBatchWorkflow.class, workflowId);
                WorkflowStub stub = WorkflowStub.fromTyped(workflow);
                BatchResult result = stub.getResult(BatchResult.class);
                return Map.of("workflowId", workflowId, "status", "COMPLETED", "result", result);
            } else {
                return Map.of("workflowId", workflowId, "status", status.name());
            }
        } catch (Exception e) {
            return Map.of("workflowId", workflowId, "error", e.getMessage());
        }
    }

    // ==================== 内部方法 ====================

    private <T> Map<String, Object> submitWorkflow(int count, String type, Class<T> workflowClass) {
        String batchId = "BATCH_" + type.toUpperCase() + "_" + System.currentTimeMillis();
        List<PersonInfo> persons = generatePersons(count);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId(batchId) // 用 batchId 作为 WorkflowId，天然防重
                .build();

        T workflow = workflowClient.newWorkflowStub(workflowClass, options);
        BatchRequest request = BatchRequest.builder().batchId(batchId).persons(persons).build();

        // 异步启动，非阻塞（耗时 < 5ms）
        // 使用 Async 异步触发工作流
        io.temporal.workflow.Functions.Func1<BatchRequest, BatchResult> func = null;
        if (workflowClass == HikBatchWorkflow.class) {
            WorkflowClient.start(((HikBatchWorkflow) workflow)::processBatch, request);
        } else if (workflowClass == HikConcurrentWorkflow.class) {
            WorkflowClient.start(((HikConcurrentWorkflow) workflow)::processBatch, request);
        } else if (workflowClass == HikTimeoutWorkflow.class) {
            WorkflowClient.start(((HikTimeoutWorkflow) workflow)::processBatch, request);
        }

        log.info("异步提交{}工作流, batchId: {}, 人数: {}", type, batchId, count);

        return Map.of(
                "batchId", batchId,
                "type", type,
                "total", count,
                "status", "PROCESSING",
                "message", "任务已提交，可通过 /api/hik/status/" + batchId + " 查询状态"
        );
    }

    /**
     * 生成测试人员数据
     * 每10个人中包含1个FAIL、1个ERROR、1个SLOW，用于测试各种场景
     */
    private List<PersonInfo> generatePersons(int count) {
        AtomicInteger counter = new AtomicInteger(0);
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    int idx = counter.incrementAndGet();
                    String personId;
                    // 每10个人注入一个FAIL、一个ERROR、一个SLOW
                    int mod = idx % 10;
                    if (mod == 3) {
                        personId = "P_FAIL_" + idx;
                    } else if (mod == 6) {
                        personId = "P_ERROR_" + idx;
                    } else if (mod == 9) {
                        personId = "P_SLOW_" + idx;
                    } else {
                        personId = "P_" + idx;
                    }
                    return PersonInfo.builder()
                            .personId(personId)
                            .name("测试人员" + idx)
                            .faceUrl("http://example.com/face/" + idx + ".jpg")
                            .build();
                })
                .collect(Collectors.toList());
    }
}
