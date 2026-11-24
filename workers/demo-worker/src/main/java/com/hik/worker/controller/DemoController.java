package com.hik.worker.controller;

import com.hik.worker.model.DemoRequest;
import com.hik.worker.model.DemoResult;
import com.hik.worker.workflow.DemoWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Demo 控制器 - 提供 REST API 触发各种演示场景
 *
 * 演示场景：
 * 1. 日志查看场景：GET  /api/demo/scenario-logging
 * 2. 正常第三方调用：GET  /api/demo/scenario-normal
 * 3. 并发接口调用：GET  /api/demo/scenario-concurrent
 * 4. 接口错误处理：GET  /api/demo/scenario-error
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final WorkflowClient workflowClient;

    /**
     * 场景1：日志查看场景
     * 触发一个完整的 Workflow 执行，通过日志追踪每个关键步骤
     */
    @GetMapping("/scenario-logging")
    public Map<String, Object> scenarioLogging(
            @RequestParam(defaultValue = "LOG-001") String personId,
            @RequestParam(defaultValue = "日志演示用户") String name) {

        String workflowId = "demo-logging-" + System.currentTimeMillis();
        log.info("===== 场景1: 日志查看场景启动 =====");
        log.info("WorkflowId: {}, personId: {}, name: {}", workflowId, personId, name);

        DemoWorkflow workflow = createWorkflow(workflowId);
        DemoRequest request = DemoRequest.builder()
                .workflowId(workflowId)
                .personId(personId)
                .name(name)
                .phone("13800138000")
                .build();

        DemoResult result = workflow.run(request);
        log.info("===== 场景1: 日志查看场景完成, 结果: {} =====", result);
        return buildResponse(workflowId, result);
    }

    /**
     * 场景2：正常第三方接口调用
     * 演示 Worker 通过 mokapi 正常调用第三方接口的完整流程
     */
    @GetMapping("/scenario-normal")
    public Map<String, Object> scenarioNormal(
            @RequestParam(defaultValue = "NORMAL-001") String personId,
            @RequestParam(defaultValue = "正常调用用户") String name) {

        String workflowId = "demo-normal-" + System.currentTimeMillis();
        log.info("===== 场景2: 正常第三方接口调用启动 =====");

        DemoWorkflow workflow = createWorkflow(workflowId);
        DemoRequest request = DemoRequest.builder()
                .workflowId(workflowId)
                .personId(personId)
                .name(name)
                .phone("13900139000")
                .build();

        DemoResult result = workflow.run(request);
        log.info("===== 场景2: 正常第三方接口调用完成 =====");
        return buildResponse(workflowId, result);
    }

    /**
     * 场景3：并发接口调用
     * 同时启动多个 Workflow，模拟多任务并发调用第三方接口
     */
    @GetMapping("/scenario-concurrent")
    public Map<String, Object> scenarioConcurrent(
            @RequestParam(defaultValue = "5") int count) {

        String batchId = "demo-concurrent-" + System.currentTimeMillis();
        log.info("===== 场景3: 并发接口调用启动, 并发数: {} =====", count);

        ExecutorService executor = Executors.newFixedThreadPool(count);
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            final int index = i;
            String workflowId = batchId + "-" + index;

            futures.add(executor.submit(() -> {
                DemoWorkflow workflow = createWorkflow(workflowId);
                DemoRequest request = DemoRequest.builder()
                        .workflowId(workflowId)
                        .personId("CONCURRENT-" + String.format("%03d", index))
                        .name("并发用户" + index)
                        .phone("13800" + String.format("%05d", index))
                        .build();

                log.info("[并发-{}] 启动 Workflow, personId: {}", index, request.getPersonId());
                DemoResult result = workflow.run(request);
                log.info("[并发-{}] Workflow 完成, success: {}", index, result.isSuccess());
                return buildResponse(workflowId, result);
            }));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < futures.size(); i++) {
            try {
                Map<String, Object> result = futures.get(i).get(60, TimeUnit.SECONDS);
                results.add(result);
                if (Boolean.TRUE.equals(result.get("success"))) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                log.error("[并发-{}] 执行异常: {}", i + 1, e.getMessage());
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("workflowId", batchId + "-" + (i + 1));
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                results.add(errorResult);
                failCount++;
            }
        }

        executor.shutdown();
        log.info("===== 场景3: 并发接口调用完成, 成功: {}, 失败: {} =====", successCount, failCount);

        return Map.of(
                "batchId", batchId,
                "totalCount", count,
                "successCount", successCount,
                "failCount", failCount,
                "results", results
        );
    }

    /**
     * 场景4：接口错误处理
     * 使用不同的 personId 关键字触发不同的错误场景，展示 Worker 的错误处理机制
     *
     * 可选错误类型：
     * - ERROR:  模拟接口返回 500，触发 Temporal 自动重试
     * - FAIL:   模拟校验失败（业务失败，非异常）
     * - SLOW:   模拟慢响应（3秒延迟）
     * - TIMEOUT: 模拟超时（30秒延迟）
     */
    @GetMapping("/scenario-error")
    public Map<String, Object> scenarioError(
            @RequestParam(defaultValue = "ERROR") String errorType) {

        String workflowId = "demo-error-" + System.currentTimeMillis();
        String personId = errorType.toUpperCase() + "-" + System.currentTimeMillis();
        log.info("===== 场景4: 接口错误处理启动, 错误类型: {} =====", errorType);

        DemoWorkflow workflow = createWorkflow(workflowId);
        DemoRequest request = DemoRequest.builder()
                .workflowId(workflowId)
                .personId(personId)
                .name("错误处理测试用户")
                .phone("13700137000")
                .build();

        DemoResult result;
        try {
            result = workflow.run(request);
            log.info("===== 场景4: 接口错误处理完成, success: {} =====", result.isSuccess());
        } catch (Exception e) {
            log.error("===== 场景4: 接口错误处理最终失败: {} =====", e.getMessage());
            return Map.of(
                    "workflowId", workflowId,
                    "errorType", errorType,
                    "success", false,
                    "error", e.getMessage(),
                    "message", "Workflow 在重试 " + 3 + " 次后仍然失败（符合预期）"
            );
        }

        return buildResponse(workflowId, result);
    }

    /**
     * 查询工作流状态
     */
    @GetMapping("/status/{workflowId}")
    public Map<String, Object> getStatus(@PathVariable String workflowId) {
        try {
            DemoWorkflow workflow = workflowClient.newWorkflowStub(DemoWorkflow.class, workflowId);
            String status = workflow.getStatus();
            return Map.of(
                    "workflowId", workflowId,
                    "status", status
            );
        } catch (Exception e) {
            return Map.of(
                    "workflowId", workflowId,
                    "error", e.getMessage()
            );
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "demo-worker",
                "timestamp", System.currentTimeMillis()
        );
    }

    // ========== 私有方法 ==========

    private DemoWorkflow createWorkflow(String workflowId) {
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue("DEMO_TASK_QUEUE")
                .setWorkflowExecutionTimeout(Duration.ofMinutes(5))
                .build();
        return workflowClient.newWorkflowStub(DemoWorkflow.class, options);
    }

    private Map<String, Object> buildResponse(String workflowId, DemoResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("workflowId", workflowId);
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("processedCount", result.getProcessedCount());
        response.put("apiResponse", result.getApiResponse());
        response.put("duration", result.getDuration() + "ms");
        return response;
    }
}
