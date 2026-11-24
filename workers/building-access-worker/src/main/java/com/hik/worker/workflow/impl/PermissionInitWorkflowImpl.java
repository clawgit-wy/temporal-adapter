package com.hik.worker.workflow.impl;

import com.hik.worker.activity.LogActivity;
import com.hik.worker.activity.PermissionQueryActivity;
import com.hik.worker.activity.WebhookActivity;
import com.hik.worker.model.PermissionInitRequest;
import com.hik.worker.model.PermissionInitResult;
import com.hik.worker.workflow.PermissionInitWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限全量初始化工作流实现
 * 从第三方系统拉取所有员工门禁权限并初始化
 * 流程：并行拉取（新开普/富士） → 合并 → 去重 → 创建日志 → Webhook
 */
@Slf4j
public class PermissionInitWorkflowImpl implements PermissionInitWorkflow {

    private final PermissionQueryActivity permissionQueryActivity = Workflow.newActivityStub(PermissionQueryActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final LogActivity logActivity = Workflow.newActivityStub(LogActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    private final WebhookActivity webhookActivity = Workflow.newActivityStub(WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    @Override
    public PermissionInitResult initAllPermissions(PermissionInitRequest request) {
        Workflow.getLogger(PermissionInitWorkflowImpl.class)
                .info("权限初始化工作流启动, initType: {}, deduplicate: {}", request.getInitType(), request.isDeduplicate());

        int parallelPages = request.getParallelPages() > 0 ? request.getParallelPages() : 5;
        List<String> factoryCodes = request.getFactoryCodes() != null ? request.getFactoryCodes() : new ArrayList<>();
        List<Map<String, Object>> allPermissions = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        // Step 1: 新开普（XKP）权限拉取
        if ("XKP".equals(request.getInitType()) || "ALL".equals(request.getInitType())) {
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> Step 1: 并行拉取新开普权限, parallelPages: {}", parallelPages);
            List<Promise<List<Map<String, Object>>>> xkpPromises = new ArrayList<>();
            for (String factoryCode : factoryCodes) {
                xkpPromises.add(Async.function(permissionQueryActivity::batchQueryGrantInfo, List.of(factoryCode)));
            }
            // 若无厂区编码，进行单次全量查询
            if (factoryCodes.isEmpty()) {
                xkpPromises.add(Async.function(permissionQueryActivity::batchQueryGrantInfo, new ArrayList<>()));
            }

            for (Promise<List<Map<String, Object>>> p : xkpPromises) {
                try {
                    List<Map<String, Object>> batch = p.get();
                    if (batch != null) {
                        allPermissions.addAll(batch);
                        successCount += batch.size();
                    }
                } catch (Exception e) {
                    failCount++;
                    Workflow.getLogger(PermissionInitWorkflowImpl.class)
                            .error(">>> 新开普权限拉取失败: {}", e.getMessage());
                }
            }
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> 新开普权限拉取完成, 数量: {}", allPermissions.size());
        }

        // Step 2: 富士（CRK）权限拉取
        if ("CRK".equals(request.getInitType()) || "ALL".equals(request.getInitType())) {
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> Step 2: 并行拉取富士权限, parallelPages: {}", parallelPages);
            List<Promise<Map<String, Object>>> crkPromises = new ArrayList<>();
            for (String factoryCode : factoryCodes) {
                crkPromises.add(Async.function(permissionQueryActivity::queryGrantInfo, factoryCode));
            }
            if (factoryCodes.isEmpty()) {
                crkPromises.add(Async.function(permissionQueryActivity::queryGrantInfo, "ALL"));
            }

            int crkBefore = allPermissions.size();
            for (Promise<Map<String, Object>> p : crkPromises) {
                try {
                    Map<String, Object> result = p.get();
                    if (result != null) {
                        allPermissions.add(result);
                        successCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    Workflow.getLogger(PermissionInitWorkflowImpl.class)
                            .error(">>> 富士权限拉取失败: {}", e.getMessage());
                }
            }
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> 富士权限拉取完成, 数量: {}", allPermissions.size() - crkBefore);
        }

        // Step 3: 合并结果（allPermissions 已在上述步骤中合并）
        Workflow.getLogger(PermissionInitWorkflowImpl.class)
                .info(">>> Step 3: 合并结果, 总权限数: {}", allPermissions.size());

        // Step 4: 去重（按 员工+设备 去重）
        int deduplicateCount = 0;
        if (request.isDeduplicate() && !allPermissions.isEmpty()) {
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> Step 4: 按员工+设备去重");
            Set<String> seenKeys = new HashSet<>();
            List<Map<String, Object>> dedupedPermissions = new ArrayList<>();
            for (Map<String, Object> perm : allPermissions) {
                String employee = getStr(perm, "employee");
                String device = getStr(perm, "deviceNo");
                String key = employee + ":" + device;
                if (seenKeys.add(key)) {
                    dedupedPermissions.add(perm);
                } else {
                    deduplicateCount++;
                }
            }
            allPermissions = dedupedPermissions;
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .info(">>> 去重完成, 去重数: {}, 剩余: {}", deduplicateCount, allPermissions.size());
        }

        int totalEmployees = allPermissions.size();

        // Step 5: 创建初始化日志
        Workflow.getLogger(PermissionInitWorkflowImpl.class)
                .info(">>> Step 5: 创建初始化日志");
        List<Map<String, Object>> logs = new ArrayList<>();
        for (Map<String, Object> perm : allPermissions) {
            Map<String, Object> log = new HashMap<>();
            log.put("initType", request.getInitType());
            log.put("employee", getStr(perm, "employee"));
            log.put("deviceNo", getStr(perm, "deviceNo"));
            log.put("status", "INITIALIZED");
            logs.add(log);
        }
        try {
            logActivity.createIssueLogs(logs);
        } catch (Exception e) {
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .error(">>> 创建初始化日志失败: {}", e.getMessage());
        }

        // Step 6: Webhook 回调
        Workflow.getLogger(PermissionInitWorkflowImpl.class)
                .info(">>> Step 6: Webhook 回调");
        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        String payload = String.format(
                "{\"initType\":\"%s\",\"totalEmployees\":%d,\"successCount\":%d,\"failCount\":%d,\"deduplicateCount\":%d,\"status\":\"%s\"}",
                request.getInitType(), totalEmployees, successCount, failCount, deduplicateCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(PermissionInitWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }

        Workflow.getLogger(PermissionInitWorkflowImpl.class)
                .info("权限初始化工作流完成, 总数: {}, 成功: {}, 失败: {}", totalEmployees, successCount, failCount);

        return PermissionInitResult.builder()
                .initType(request.getInitType())
                .totalEmployees(totalEmployees)
                .successCount(successCount)
                .failCount(failCount)
                .deduplicateCount(deduplicateCount)
                .status(status)
                .build();
    }

    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }
}
