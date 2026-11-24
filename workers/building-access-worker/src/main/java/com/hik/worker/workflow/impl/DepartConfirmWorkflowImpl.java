package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.DepartConfirmWorkflow;
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
 * 人员变更权限确认工作流实现
 * 员工部门变动时批量处理门禁权限
 * RETAIN: 校验 → 人脸发行（跨部门）→ 卡发行 → 通知
 * CANCEL: 校验 → 注销权限 → 通知
 */
@Slf4j
public class DepartConfirmWorkflowImpl implements DepartConfirmWorkflow {

    private final ValidateActivity validateActivity = Workflow.newActivityStub(ValidateActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

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

    private final FaceIncrementActivity faceIncrementActivity = Workflow.newActivityStub(FaceIncrementActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final PermissionCancelActivity cancelActivity = Workflow.newActivityStub(PermissionCancelActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final NotificationActivity notificationActivity = Workflow.newActivityStub(NotificationActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    private final WebhookActivity webhookActivity = Workflow.newActivityStub(WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    @Override
    public DepartConfirmResult processDepartChanges(DepartConfirmRequest request) {
        Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                .info("人员变更权限确认工作流启动, batchId: {}, operationType: {}, 人数: {}",
                        request.getBatchId(), request.getOperationType(), request.getStaffList().size());

        // Step 3: 并行处理所有人员
        List<Promise<StaffResult>> promises = new ArrayList<>();
        for (StaffInfo staff : request.getStaffList()) {
            promises.add(Async.function(this::processSingleStaff, staff, request));
        }

        // Step 4: 汇总结果
        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        for (Promise<StaffResult> p : promises) {
            try {
                StaffResult result = p.get();
                results.add(result);
                if (result.isPassed()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                results.add(StaffResult.fail("unknown", "unknown", request.getOperationType(), e.getMessage()));
                failCount++;
            }
        }

        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                .info("人员变更权限确认完成, 成功: {}, 失败: {}", successCount, failCount);

        // Step 5: Webhook 回调
        String payload = String.format(
                "{\"batchId\":\"%s\",\"operationType\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                request.getBatchId(), request.getOperationType(), request.getStaffList().size(),
                successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }

        return DepartConfirmResult.builder()
                .batchId(request.getBatchId())
                .total(request.getStaffList().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(status)
                .build();
    }

    /**
     * 处理单个人员的权限变更
     * RETAIN: 校验 → checkToken → 人脸发行（跨部门时）→ 卡发行 → 通知
     * CANCEL: 校验 → 注销权限 → 通知
     */
    private StaffResult processSingleStaff(StaffInfo staff, DepartConfirmRequest request) {
        String staffNo = staff.getStaffNo();
        String staffName = staff.getStaffName();
        try {
            // Step 1: 校验
            Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                    .info(">>> 校验, staffNo: {}, operationType: {}", staffNo, request.getOperationType());
            boolean valid = validateActivity.validateWhitelist(staffNo);
            if (!valid) {
                return StaffResult.fail(staffNo, staffName, request.getOperationType(), "白名单校验不通过");
            }

            if ("RETAIN".equals(request.getOperationType())) {
                // Step 2: checkToken
                Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                        .info(">>> RETAIN checkToken, staffNo: {}", staffNo);
                CheckTokenRequest checkReq = CheckTokenRequest.builder()
                        .TokenType(1).Token(staffNo).build();
                checkTokenActivity.checkToken(checkReq);

                // Step 3: 人脸发行（跨部门时重新发行）
                if (request.isCrossDepartment()) {
                    Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                            .info(">>> RETAIN 跨部门人脸发行, staffNo: {}", staffNo);
                    FaceIssueRequest faceReq = FaceIssueRequest.builder()
                            .OperType(1)
                            .Staff(staff)
                            .AuthorizeDeviceNo(request.getAuthorizeDeviceNo())
                            .StaffState(2)
                            .build();
                    faceIssueActivity.issueFace(faceReq);
                }

                // Step 4: 卡发行
                Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                        .info(">>> RETAIN 卡发行, staffNo: {}", staffNo);
                CardIssueRequest cardReq = CardIssueRequest.builder()
                        .useModel(1)
                        .orginazitionId(staff.getOrganizationId())
                        .staffNo(staffNo)
                        .tokenId(staffNo)
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
                        .staffName(staffName)
                        .tokenType(1)
                        .AuthDevice(request.getAuthDevice())
                        .build();
                cardIssueActivity.issueCard(cardReq);

                // 通知
                try {
                    notificationActivity.sendNotification("DEPART_RETAIN", staffNo,
                            "部门变更权限保留成功, 新部门: " + request.getNewDeptCode());
                } catch (Exception e) {
                    Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                            .error(">>> 通知发送失败: {}", e.getMessage());
                }

                return StaffResult.success(staffNo, staffName, "RETAIN", "权限保留成功");
            } else if ("CANCEL".equals(request.getOperationType())) {
                // Step 2: 注销权限
                Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                        .info(">>> CANCEL 注销权限, staffNo: {}", staffNo);
                List<String> gids = List.of("8A0ADE28-0000-0000-0000-000000000001",
                        "FF7F139D-0000-0000-0000-000000000002",
                        "35EC9884-0000-0000-0000-000000000003");
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
                            .Remark("人员变更注销操作")
                            .build());
                }
                cancelActivity.unregisterFace(faceRequests);

                String cardNo = cancelActivity.queryCardNo(staffNo);
                if (cardNo != null && !cardNo.isEmpty()) {
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
                                .Remark("人员变更注销操作")
                                .build());
                    }
                    cancelActivity.unregisterCard(cardRequests);
                }

                // 通知
                try {
                    notificationActivity.sendNotification("DEPART_CANCEL", staffNo,
                            "部门变更权限已取消, 原部门: " + request.getOriginalDeptCode());
                } catch (Exception e) {
                    Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                            .error(">>> 通知发送失败: {}", e.getMessage());
                }

                return StaffResult.success(staffNo, staffName, "CANCEL", "权限注销成功");
            }
            return StaffResult.fail(staffNo, staffName, request.getOperationType(), "未知操作类型");
        } catch (Exception e) {
            Workflow.getLogger(DepartConfirmWorkflowImpl.class)
                    .error(">>> 权限变更失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            return StaffResult.fail(staffNo, staffName, request.getOperationType(), e.getMessage());
        }
    }
}
