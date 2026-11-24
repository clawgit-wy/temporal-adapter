package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.PermissionIncrementWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 增量权限工作流实现
 * 流程：人脸增量权限（增加/删除设备） -> Webhook回调
 * powerType: 0=增加设备权限, 1=删除设备权限
 */
@Slf4j
public class PermissionIncrementWorkflowImpl implements PermissionIncrementWorkflow {

    private final FaceIncrementActivity faceIncrementActivity = Workflow.newActivityStub(FaceIncrementActivity.class,
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
    public DevicePermissionResult modifyIncrementPermission(DevicePermissionRequest request) {
        Workflow.getLogger(PermissionIncrementWorkflowImpl.class)
                .info("增量权限工作流启动, batchId: {}, operationType: {}", request.getBatchId(), request.getOperationType());

        // 根据 operationType 判断是增加还是删除
        int powerType = "INCREMENT_DELETE".equals(request.getOperationType()) ? 1 : 0;
        String opDesc = powerType == 0 ? "增量增加" : "增量删除";

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (StaffInfo staff : request.getStaffList()) {
            String staffNo = staff.getStaffNo();
            String staffName = staff.getStaffName();
            try {
                Workflow.getLogger(PermissionIncrementWorkflowImpl.class)
                        .info(">>> {} , staffNo: {}, 设备: {}", opDesc, staffNo, request.getAuthorizeDeviceNo());

                FaceIncrementRequest incReq = FaceIncrementRequest.builder()
                        .OperType(1)
                        .Staff(staff)
                        .BeginTime(request.getBeginTime())
                        .EndTime(request.getEndTime())
                        .AuthorizeDeviceNo(request.getAuthorizeDeviceNo())
                        .powerType(powerType)
                        .StaffState(0)
                        .FacePicture(request.getFacePicture())
                        .build();
                FaceIssueResponse resp = faceIncrementActivity.modifyByPowerType(incReq);

                results.add(StaffResult.success(staffNo, staffName, opDesc, resp.getMsg()));
                successCount++;
            } catch (Exception e) {
                results.add(StaffResult.fail(staffNo, staffName, opDesc, e.getMessage()));
                failCount++;
                Workflow.getLogger(PermissionIncrementWorkflowImpl.class)
                        .error(">>> {} 失败, staffNo: {}, 错误: {}", opDesc, staffNo, e.getMessage());
            }
        }

        // Webhook 回调
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
            Workflow.getLogger(PermissionIncrementWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }
    }
}
