package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.BatchPermissionIssueWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量权限下发工作流实现
 * 并发下发多个人员的设备权限，支持限流（20 QPS）
 */
@Slf4j
public class BatchPermissionIssueWorkflowImpl implements BatchPermissionIssueWorkflow {

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
    public DevicePermissionResult batchIssue(DevicePermissionRequest request) {
        Workflow.getLogger(BatchPermissionIssueWorkflowImpl.class)
                .info("批量权限下发工作流启动, batchId: {}, 人数: {}", request.getBatchId(), request.getStaffList().size());

        List<Promise<StaffResult>> promises = new ArrayList<>();
        for (StaffInfo staff : request.getStaffList()) {
            promises.add(Async.function(this::processSingleStaff, staff, request));
        }

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        for (Promise<StaffResult> p : promises) {
            try {
                StaffResult result = p.get();
                results.add(result);
                if (result.isPassed()) successCount++;
                else failCount++;
            } catch (Exception e) {
                results.add(StaffResult.fail("unknown", "unknown", "BATCH_ISSUE", e.getMessage()));
                failCount++;
            }
        }

        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        Workflow.getLogger(BatchPermissionIssueWorkflowImpl.class)
                .info("批量权限下发完成, 成功: {}, 失败: {}", successCount, failCount);

        // Webhook 回调
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                request.getBatchId(), request.getStaffList().size(), successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(BatchPermissionIssueWorkflowImpl.class)
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

    /**
     * 处理单个人员的权限下发（校验 -> 人脸发行 -> 卡发行）
     */
    private StaffResult processSingleStaff(StaffInfo staff, DevicePermissionRequest request) {
        String staffNo = staff.getStaffNo();
        String staffName = staff.getStaffName();
        try {
            // Step 1: 发行校验
            CheckTokenRequest checkReq = CheckTokenRequest.builder()
                    .TokenType(1).Token(staffNo).build();
            checkTokenActivity.checkToken(checkReq);

            // Step 2: 人脸发行
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
            faceIssueActivity.issueFace(faceReq);

            // Step 3: 卡发行
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
            cardIssueActivity.issueCard(cardReq);

            return StaffResult.success(staffNo, staffName, "BATCH_ISSUE", "下发成功");
        } catch (Exception e) {
            return StaffResult.fail(staffNo, staffName, "BATCH_ISSUE", e.getMessage());
        }
    }
}
