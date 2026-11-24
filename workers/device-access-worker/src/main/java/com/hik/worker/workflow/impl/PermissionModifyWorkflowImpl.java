package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.PermissionModifyWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限改写工作流实现
 * 流程：发行校验 -> 人脸改写 -> Webhook回调
 */
@Slf4j
public class PermissionModifyWorkflowImpl implements PermissionModifyWorkflow {

    private final CheckTokenActivity checkTokenActivity = Workflow.newActivityStub(CheckTokenActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    private final FaceModifyActivity faceModifyActivity = Workflow.newActivityStub(FaceModifyActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final WebhookActivity webhookActivity = Workflow.newActivityStub(WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    @Override
    public DevicePermissionResult modifyPermission(DevicePermissionRequest request) {
        Workflow.getLogger(PermissionModifyWorkflowImpl.class)
                .info("权限改写工作流启动, batchId: {}", request.getBatchId());

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (StaffInfo staff : request.getStaffList()) {
            String staffNo = staff.getStaffNo();
            String staffName = staff.getStaffName();
            try {
                // Step 1: 发行校验
                Workflow.getLogger(PermissionModifyWorkflowImpl.class)
                        .info(">>> Step 1: 发行校验, staffNo: {}", staffNo);
                CheckTokenRequest checkReq = CheckTokenRequest.builder()
                        .TokenType(1).Token(staffNo).build();
                checkTokenActivity.checkToken(checkReq);

                // Step 2: 人脸改写
                Workflow.getLogger(PermissionModifyWorkflowImpl.class)
                        .info(">>> Step 2: 人脸改写, staffNo: {}", staffNo);
                FaceIssueRequest modifyReq = FaceIssueRequest.builder()
                        .OperType(1)
                        .Staff(staff)
                        .BeginTime(request.getBeginTime())
                        .EndTime(request.getEndTime())
                        .AuthorizeDeviceNo(request.getAuthorizeDeviceNo())
                        .ProjectGids(request.getProjectGids())
                        .StaffState(2)
                        .FacePicture(request.getFacePicture())
                        .build();
                FaceIssueResponse modifyResp = faceModifyActivity.modifyFace(modifyReq);

                results.add(StaffResult.success(staffNo, staffName, "MODIFY", modifyResp.getMsg()));
                successCount++;
            } catch (Exception e) {
                results.add(StaffResult.fail(staffNo, staffName, "MODIFY", e.getMessage()));
                failCount++;
                Workflow.getLogger(PermissionModifyWorkflowImpl.class)
                        .error(">>> 权限改写失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            }
        }

        // Step 3: Webhook 回调
        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        sendWebhookCallback(request.getBatchId(), request.getStaffList().size(), successCount, failCount, status);

        return DevicePermissionResult.builder()
                .batchId(request.getBatchId())
                .total(request.getStaffList().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(status)
                .build();
    }

    private void sendWebhookCallback(String batchId, int total, int successCount, int failCount, String status) {
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                batchId, total, successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(PermissionModifyWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }
    }
}
