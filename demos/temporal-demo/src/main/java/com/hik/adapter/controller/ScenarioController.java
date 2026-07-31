package com.hik.adapter.controller;

import com.hik.adapter.config.TemporalWorkerConfig;
import com.hik.adapter.model.BatchResult;
import com.hik.adapter.workflow.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 场景验证Controller
 * 提供文档中5个验证场景的触发接口
 */
@Slf4j
@RestController
@RequestMapping("/api/scenario")
public class ScenarioController {

    @Autowired
    private WorkflowClient workflowClient;

    /**
     * 场景1: 海量并发与限流
     * POST /api/scenario/1/rate-limit?total=1000
     * 验证Temporal限流能力：1000次Activity调用，20 QPS平滑消费
     */
    @PostMapping("/1/rate-limit")
    public Map<String, Object> scenario1RateLimit(@RequestParam(defaultValue = "1000") int total) {
        String workflowId = "SCENARIO1_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_RATE_LIMIT)
                .setWorkflowId(workflowId)
                .build();

        RateLimitWorkflow workflow = workflowClient.newWorkflowStub(RateLimitWorkflow.class, options);
        WorkflowClient.start(workflow::processBatch, total);

        log.info("场景1限流工作流已启动, workflowId: {}, total: {}", workflowId, total);
        return Map.of(
                "scenario", "1-限流验证",
                "workflowId", workflowId,
                "total", total,
                "message", "1000次调用以20 QPS限流执行，观察Web UI进度"
        );
    }

    /**
     * 场景2: 第三方接口重试
     * POST /api/scenario/2/retry?total=3
     * 验证指数退避重试：前3次失败，第4次成功，间隔1s->2s->4s
     */
    @PostMapping("/2/retry")
    public Map<String, Object> scenario2Retry(@RequestParam(defaultValue = "3") int total) {
        String workflowId = "SCENARIO2_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_RETRY)
                .setWorkflowId(workflowId)
                .build();

        RetryWorkflow workflow = workflowClient.newWorkflowStub(RetryWorkflow.class, options);
        WorkflowClient.start(workflow::processBatch, total);

        log.info("场景2重试工作流已启动, workflowId: {}, total: {}", workflowId, total);
        return Map.of(
                "scenario", "2-重试验证",
                "workflowId", workflowId,
                "total", total,
                "message", "每个任务前3次失败第4次成功，观察Web UI中1s->2s->4s重试"
        );
    }

    /**
     * 场景3: 断点续传
     * POST /api/scenario/3/crash-recovery
     * 验证Worker崩溃后从断点恢复：3步顺序执行，Kill Worker后重启从断点继续
     */
    @PostMapping("/3/crash-recovery")
    public Map<String, Object> scenario3CrashRecovery() {
        String workflowId = "SCENARIO3_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_CRASH)
                .setWorkflowId(workflowId)
                .build();

        CrashRecoveryWorkflow workflow = workflowClient.newWorkflowStub(CrashRecoveryWorkflow.class, options);
        WorkflowClient.start(workflow::processBatch, 0);

        log.info("场景3断点续传工作流已启动, workflowId: {}", workflowId);
        return Map.of(
                "scenario", "3-断点续传",
                "workflowId", workflowId,
                "message", "3步顺序执行(扣款->门禁->短信)，在Step2时Kill Worker验证断点续传"
        );
    }

    /**
     * 场景4: 长等待与Signal唤醒
     * POST /api/scenario/4/human-in-loop
     * 验证Human-in-the-Loop：Workflow等待Signal，零CPU占用
     */
    @PostMapping("/4/human-in-loop")
    public Map<String, Object> scenario4HumanInLoop() {
        String workflowId = "SCENARIO4_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_HUMAN_LOOP)
                .setWorkflowId(workflowId)
                .build();

        HumanInLoopWorkflow workflow = workflowClient.newWorkflowStub(HumanInLoopWorkflow.class, options);
        WorkflowClient.start(workflow::waitForApproval, workflowId);

        log.info("场景4长等待工作流已启动, workflowId: {}", workflowId);
        return Map.of(
                "scenario", "4-长等待Signal",
                "workflowId", workflowId,
                "message", "工作流等待审批信号，通过approve或reject接口唤醒"
        );
    }

    /**
     * 场景4: 发送审批通过信号
     * POST /api/scenario/4/{workflowId}/approve
     */
    @PostMapping("/4/{workflowId}/approve")
    public Map<String, Object> approve(@PathVariable String workflowId) {
        HumanInLoopWorkflow workflow = workflowClient.newWorkflowStub(HumanInLoopWorkflow.class, workflowId);
        workflow.approve();
        log.info("已发送approve信号, workflowId: {}", workflowId);
        return Map.of("workflowId", workflowId, "action", "approved");
    }

    /**
     * 场景4: 发送审批拒绝信号
     * POST /api/scenario/4/{workflowId}/reject
     */
    @PostMapping("/4/{workflowId}/reject")
    public Map<String, Object> reject(@PathVariable String workflowId) {
        HumanInLoopWorkflow workflow = workflowClient.newWorkflowStub(HumanInLoopWorkflow.class, workflowId);
        workflow.reject();
        log.info("已发送reject信号, workflowId: {}", workflowId);
        return Map.of("workflowId", workflowId, "action", "rejected");
    }

    /**
     * 场景4: 查询工作流状态
     * GET /api/scenario/4/{workflowId}/status
     */
    @GetMapping("/4/{workflowId}/status")
    public Map<String, Object> getStatus(@PathVariable String workflowId) {
        HumanInLoopWorkflow workflow = workflowClient.newWorkflowStub(HumanInLoopWorkflow.class, workflowId);
        String status = workflow.getStatus();
        return Map.of("workflowId", workflowId, "status", status);
    }

    /**
     * 场景5: Webhook回调
     * POST /api/scenario/5/webhook?total=20
     * 验证结果通知与Webhook回调可靠性
     */
    @PostMapping("/5/webhook")
    public Map<String, Object> scenario5Webhook(@RequestParam(defaultValue = "20") int total) {
        String workflowId = "SCENARIO5_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_WEBHOOK)
                .setWorkflowId(workflowId)
                .build();

        WebhookWorkflow workflow = workflowClient.newWorkflowStub(WebhookWorkflow.class, options);
        WorkflowClient.start(workflow::processBatch, total);

        log.info("场景5 Webhook回调工作流已启动, workflowId: {}, total: {}", workflowId, total);
        return Map.of(
                "scenario", "5-Webhook回调",
                "workflowId", workflowId,
                "total", total,
                "message", "批量任务完成后自动发送Webhook回调到mokapi"
        );
    }

    /**
     * 通用: 获取工作流结果
     * GET /api/scenario/result/{workflowId}
     */
    @GetMapping("/result/{workflowId}")
    public Map<String, Object> getResult(@PathVariable String workflowId) {
        try {
            WorkflowStub stub = WorkflowStub.fromTyped(
                    workflowClient.newWorkflowStub(HikBatchWorkflow.class, workflowId));
            BatchResult result = stub.getResult(BatchResult.class);
            return Map.of("workflowId", workflowId, "status", "COMPLETED", "result", result);
        } catch (Exception e) {
            return Map.of("workflowId", workflowId, "error", e.getMessage());
        }
    }
}
