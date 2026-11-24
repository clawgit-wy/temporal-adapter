package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.PermissionIssueWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限下发工作流实现
 * 参照门禁下发流程：发行校验 -> 人脸发行 -> 卡发行 -> Webhook回调
 */
@Slf4j
public class PermissionIssueWorkflowImpl implements PermissionIssueWorkflow {

    private final CheckTokenActivity checkTokenActivity = Workflow.newActivityStub(CheckTokenActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    private final FaceIssueActivity faceIssueActivity = Workflow.newActivityStub(FaceIssueActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final CardIssueActivity cardIssueActivity = Workflow.newActivityStub(CardIssueActivity.class,
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
    public DevicePermissionResult issuePermission(DevicePermissionRequest request) {
        Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                .info("权限下发工作流启动, batchId: {}, deviceType: {}", request.getBatchId(), request.getDeviceType());

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (StaffInfo staff : request.getStaffList()) {
            String staffNo = staff.getStaffNo();
            String staffName = staff.getStaffName();
            try {
                // Step 1: 发行校验
                Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                        .info(">>> Step 1: 发行校验, staffNo: {}", staffNo);
                CheckTokenRequest checkReq = CheckTokenRequest.builder()
                        .TokenType(1).Token(staffNo).build();
                CheckTokenResponse checkResp = checkTokenActivity.checkToken(checkReq);

                // Step 2: 人脸发行
                Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                        .info(">>> Step 2: 人脸发行, staffNo: {}", staffNo);
                FaceIssueRequest faceReq = FaceIssueRequest.builder()
                        .OperType(0)
                        .Staff(staff)
                        .BeginTime(request.getBeginTime())
                        .EndTime(request.getEndTime())
                        .AuthorizeDeviceNo(request.getAuthorizeDeviceNo())
                        .ProjectGids(request.getProjectGids())
                        .StaffState(1)
                        .FacePicture(request.getFacePicture())
                        .build();
                FaceIssueResponse faceResp = faceIssueActivity.issueFace(faceReq);

                // Step 3: 卡发行
                Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                        .info(">>> Step 3: 卡发行, staffNo: {}", staffNo);
                CardIssueRequest cardReq = CardIssueRequest.builder()
                        .useModel(1)
                        .orginazitionId(staff.getOrganizationId())
                        .staffNo(staffNo)
                        .tokenId(staffNo)
                        .endDate(request.getEndTime())
                        .tokenOper(1)
                        .stcm("2C3DB448-9918-4665-A4C1-C99797E2A4DD")
                        .tcm("BBB817CE-E6A9-4F9C-8C2D-BDFD2431C053")
                        .serialNo(staffNo)
                        .token(staffNo)
                        .icStartTimeL1("00:00").icStartTimeL2("00:00")
                        .icStartTimeL3("00:00").icStartTimeL4("00:00")
                        .icStartTimeR1("23:59").icStartTimeR2("00:00")
                        .icStartTimeR3("00:00").icStartTimeR4("00:00")
                        .CarPass("123456")
                        .beginDate(request.getBeginTime())
                        .staffName(staffName)
                        .tokenType(1)
                        .AuthDevice(request.getAuthDevice())
                        .build();
                CardIssueResponse cardResp = cardIssueActivity.issueCard(cardReq);

                results.add(StaffResult.success(staffNo, staffName, "ISSUE", faceResp.getMsg()));
                successCount++;
                Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                        .info(">>> 权限下发完成, staffNo: {}", staffNo);

            } catch (Exception e) {
                results.add(StaffResult.fail(staffNo, staffName, "ISSUE", e.getMessage()));
                failCount++;
                Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                        .error(">>> 权限下发失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            }
        }

        // Step 4: Webhook 回调
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
            Workflow.getLogger(PermissionIssueWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }
    }
}
