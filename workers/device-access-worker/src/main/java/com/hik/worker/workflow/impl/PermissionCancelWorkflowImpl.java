package com.hik.worker.workflow.impl;

import com.hik.worker.activity.PermissionCancelActivity;
import com.hik.worker.activity.WebhookActivity;
import com.hik.worker.model.*;
import com.hik.worker.workflow.PermissionCancelWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限注销工作流实现
 * 流程：查询卡号 -> 注销人脸(3个GID) -> 注销卡(3个GID) -> Webhook回调
 */
@Slf4j
public class PermissionCancelWorkflowImpl implements PermissionCancelWorkflow {

    private final PermissionCancelActivity cancelActivity = Workflow.newActivityStub(
            PermissionCancelActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final WebhookActivity webhookActivity = Workflow.newActivityStub(
            WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    @Override
    public DevicePermissionResult cancelPermission(DevicePermissionRequest request) {
        Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                .info("权限注销工作流启动, batchId: {}", request.getBatchId());

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        // 3个固定GID（从配置或请求中获取，这里使用默认值）
        List<String> gids = request.getProjectGids();
        if (gids == null || gids.isEmpty()) {
            gids = List.of("8A0ADE28-0000-0000-0000-000000000001",
                    "FF7F139D-0000-0000-0000-000000000002",
                    "35EC9884-0000-0000-0000-000000000003");
        }

        for (StaffInfo staff : request.getStaffList()) {
            String staffNo = staff.getStaffNo();
            String staffName = staff.getStaffName();
            try {
                Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                        .info(">>> 开始注销, staffNo: {}", staffNo);

                // Step 1: 注销人脸权限（对3个GID逐一注销）
                Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                        .info(">>> Step 1: 注销人脸权限, staffNo: {}", staffNo);
                List<UnregisterRequest> faceRequests = new ArrayList<>();
                for (String gid : gids) {
                    faceRequests.add(UnregisterRequest.builder()
                            .SubSystems(List.of(2))
                            .TokenType(1)
                            .TokenOper(12)
                            .Token(staffNo)
                            .Redate(Workflow.currentTimeMillis() + "")
                            .Gid(gid)
                            .ProjectGids(List.of(gid))
                            .Remark("访客接口操作员")
                            .build());
                }
                cancelActivity.unregisterFace(faceRequests);

                // Step 2: 查询卡号
                Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                        .info(">>> Step 2: 查询卡号, staffNo: {}", staffNo);
                String cardNo = cancelActivity.queryCardNo(staffNo);

                // Step 3: 注销卡权限（如有卡号）
                if (cardNo != null && !cardNo.isEmpty()) {
                    Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                            .info(">>> Step 3: 注销卡权限, cardNo: {}", cardNo);
                    List<UnregisterRequest> cardRequests = new ArrayList<>();
                    for (String gid : gids) {
                        cardRequests.add(UnregisterRequest.builder()
                                .SubSystems(List.of(2))
                                .TokenType(0)
                                .TokenOper(12)
                                .Token(cardNo)
                                .Redate(Workflow.currentTimeMillis() + "")
                                .Gid(gid)
                                .ProjectGids(List.of(gid))
                                .Remark("访客接口操作员")
                                .build());
                    }
                    cancelActivity.unregisterCard(cardRequests);
                }

                results.add(StaffResult.success(staffNo, staffName, "CANCEL", "注销成功"));
                successCount++;
                Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                        .info(">>> 注销完成, staffNo: {}", staffNo);

            } catch (Exception e) {
                results.add(StaffResult.fail(staffNo, staffName, "CANCEL", e.getMessage()));
                failCount++;
                Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                        .error(">>> 注销失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            }
        }

        // Step 4: Webhook 回调
        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                request.getBatchId(), request.getStaffList().size(), successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(PermissionCancelWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }

        return DevicePermissionResult.builder()
                .batchId(request.getBatchId())
                .total(request.getStaffList().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(status)
                .build();
    }
}
