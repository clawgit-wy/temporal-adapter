package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.PermissionDistributionWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限下发工作流实现
 * 大规模权限下发：校验 → 创建日志 → 并行下发 → 汇总 → 更新状态 → 通知
 * 支持 ISSUE（开通）与 CANCEL（关闭）两种操作类型，支持限流并发与失败重试信号
 */
@Slf4j
public class PermissionDistributionWorkflowImpl implements PermissionDistributionWorkflow {

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

    private final LogActivity logActivity = Workflow.newActivityStub(LogActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    private final ValidateActivity validateActivity = Workflow.newActivityStub(ValidateActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
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

    // 进度跟踪（供 QueryMethod 查询）
    private int progressProcessed = 0;
    private int progressTotal = 0;
    private int progressSuccess = 0;
    private int progressFail = 0;
    private String progressStatus = "PENDING";
    private String currentStaffNo = "";

    // 重试支持
    private final List<String> pendingRetryStaffNos = new ArrayList<>();
    private boolean retrySignaled = false;
    private PermissionDistributionRequest currentRequest;
    private List<StaffInfo> allStaff;
    private List<Integer> issuedLogIds;
    private final Map<String, StaffResult> resultMap = new LinkedHashMap<>();
    private final List<String> failedStaffNos = new ArrayList<>();

    @Override
    public PermissionDistributionResult distributePermissions(PermissionDistributionRequest request) {
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info("权限下发工作流启动, batchId: {}, operationType: {}", request.getBatchId(), request.getOperationType());

        currentRequest = request;
        progressStatus = "RUNNING";
        progressTotal = request.getStaffList() != null ? request.getStaffList().size() : 0;
        allStaff = request.getStaffList() != null ? request.getStaffList() : new ArrayList<>();

        // Step 1: 白名单校验
        List<StaffInfo> validStaff = allStaff;
        if (request.isWhitelist() && validStaff != null && !validStaff.isEmpty()) {
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .info(">>> Step 1: 白名单校验, 原人数: {}", validStaff.size());
            validStaff = validateActivity.filterWhitelistStaff(validStaff);
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .info(">>> 白名单过滤后人数: {}", validStaff.size());
            progressTotal = validStaff.size();
        }

        // Step 2: 创建下发日志
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> Step 2: 创建下发日志");
        List<Map<String, Object>> logs = new ArrayList<>();
        for (StaffInfo staff : validStaff) {
            Map<String, Object> log = new HashMap<>();
            log.put("batchId", request.getBatchId());
            log.put("processId", request.getProcessId());
            log.put("staffNo", staff.getStaffNo());
            log.put("staffName", staff.getStaffName());
            log.put("operationType", request.getOperationType());
            log.put("deviceType", request.getDeviceType());
            log.put("status", "PROCESSING");
            logs.add(log);
        }
        issuedLogIds = logActivity.createIssueLogs(logs);
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> 日志创建完成, logIds: {}", issuedLogIds);

        // Step 3: 并行下发（限流 maxConcurrency）
        int maxConcurrency = request.getMaxConcurrency() > 0 ? request.getMaxConcurrency() : 20;
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> Step 3: 并行下发, 最大并发: {}", maxConcurrency);

        for (int i = 0; i < validStaff.size(); i += maxConcurrency) {
            int end = Math.min(i + maxConcurrency, validStaff.size());
            List<StaffInfo> batch = validStaff.subList(i, end);
            List<Promise<StaffResult>> promises = new ArrayList<>();
            for (StaffInfo staff : batch) {
                currentStaffNo = staff.getStaffNo();
                promises.add(Async.function(this::processStaff, staff, request));
            }
            for (Promise<StaffResult> p : promises) {
                try {
                    StaffResult result = p.get();
                    resultMap.put(result.getStaffNo(), result);
                    if (result.isPassed()) {
                        progressSuccess++;
                    } else {
                        progressFail++;
                        failedStaffNos.add(result.getStaffNo());
                    }
                } catch (Exception e) {
                    progressFail++;
                    Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                            .error(">>> 下发异常: {}", e.getMessage());
                }
                progressProcessed++;
            }
        }

        // Step 7: 更新日志状态
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> Step 7: 更新日志状态");
        String logStatus = progressFail == 0 ? "SUCCESS" : "PARTIAL_FAILED";
        if (issuedLogIds != null && !issuedLogIds.isEmpty()) {
            try {
                logActivity.batchUpdateLogStatus(issuedLogIds, logStatus);
            } catch (Exception e) {
                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .error(">>> 更新日志状态失败: {}", e.getMessage());
            }
        }

        // Step 8: 发送通知
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> Step 8: 发送通知");
        String notifyContent = String.format(
                "权限下发完成, 批次: %s, 总数: %d, 成功: %d, 失败: %d",
                request.getBatchId(), progressTotal, progressSuccess, progressFail);
        try {
            notificationActivity.sendNotification("PERMISSION_DISTRIBUTION",
                    request.getDutyEmpCode() != null ? request.getDutyEmpCode() : "system", notifyContent);
        } catch (Exception e) {
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .error(">>> 通知发送失败: {}", e.getMessage());
        }

        // 等待重试信号（存在失败项时）
        if (progressFail > 0) {
            progressStatus = "WAITING_RETRY";
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .info(">>> 存在失败项, 等待重试信号, 失败数: {}", progressFail);
            boolean signaled = Workflow.await(Duration.ofMinutes(10), () -> retrySignaled);
            if (signaled && !pendingRetryStaffNos.isEmpty()) {
                progressStatus = "RETRYING";
                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .info(">>> 收到重试信号, 重试人数: {}", pendingRetryStaffNos.size());
                processRetry();
            }
        }

        // Step 9: Webhook 回调
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info(">>> Step 9: Webhook 回调");
        progressStatus = progressFail == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        sendWebhookCallback(request.getBatchId(), progressTotal, progressSuccess, progressFail, progressStatus);

        List<StaffResult> details = new ArrayList<>(resultMap.values());
        return PermissionDistributionResult.builder()
                .processId(request.getProcessId())
                .batchId(request.getBatchId())
                .total(progressTotal)
                .successCount(progressSuccess)
                .failCount(progressFail)
                .details(details)
                .status(progressStatus)
                .failedStaffNos(new ArrayList<>(failedStaffNos))
                .issuedLogIds(issuedLogIds)
                .build();
    }

    @Override
    @SignalMethod
    public void retryFailedItems(List<String> staffNos) {
        Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                .info("收到重试信号, staffNos: {}", staffNos);
        pendingRetryStaffNos.addAll(staffNos);
        retrySignaled = true;
    }

    @Override
    @QueryMethod
    public DistributionProgress getProgress() {
        int percent = progressTotal == 0 ? 0 : (int) ((double) progressProcessed / progressTotal * 100);
        return DistributionProgress.builder()
                .processed(progressProcessed)
                .total(progressTotal)
                .successCount(progressSuccess)
                .failCount(progressFail)
                .progressPercent(percent)
                .status(progressStatus)
                .currentStaffNo(currentStaffNo)
                .build();
    }

    /**
     * 处理单个人员的权限下发/注销
     * ISSUE: 校验 → 人脸发行 → 卡发行
     * CANCEL: 注销人脸 → 查询卡号 → 注销卡
     */
    private StaffResult processStaff(StaffInfo staff, PermissionDistributionRequest request) {
        String staffNo = staff.getStaffNo();
        String staffName = staff.getStaffName();
        try {
            if ("ISSUE".equals(request.getOperationType())) {
                // Step 4: 校验 → 人脸发行 → 卡发行
                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .info(">>> ISSUE 校验, staffNo: {}", staffNo);
                CheckTokenRequest checkReq = CheckTokenRequest.builder()
                        .TokenType(1).Token(staffNo).build();
                checkTokenActivity.checkToken(checkReq);

                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .info(">>> ISSUE 人脸发行, staffNo: {}", staffNo);
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

                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .info(">>> ISSUE 卡发行, staffNo: {}", staffNo);
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

                return StaffResult.success(staffNo, staffName, "ISSUE", faceResp.getMsg());
            } else if ("CANCEL".equals(request.getOperationType())) {
                // Step 5: 注销权限
                Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                        .info(">>> CANCEL 注销, staffNo: {}", staffNo);
                List<String> gids = request.getProjectGids();
                if (gids == null || gids.isEmpty()) {
                    gids = List.of("8A0ADE28-0000-0000-0000-000000000001",
                            "FF7F139D-0000-0000-0000-000000000002",
                            "35EC9884-0000-0000-0000-000000000003");
                }
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
                            .Remark("权限下发注销操作")
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
                                .Remark("权限下发注销操作")
                                .build());
                    }
                    cancelActivity.unregisterCard(cardRequests);
                }
                return StaffResult.success(staffNo, staffName, "CANCEL", "注销成功");
            }
            return StaffResult.fail(staffNo, staffName, request.getOperationType(), "未知操作类型");
        } catch (Exception e) {
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .error(">>> 下发失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            return StaffResult.fail(staffNo, staffName, request.getOperationType(), e.getMessage());
        }
    }

    /**
     * 处理重试失败项
     */
    private void processRetry() {
        List<StaffInfo> retryStaff = new ArrayList<>();
        for (String staffNo : pendingRetryStaffNos) {
            for (StaffInfo s : allStaff) {
                if (s.getStaffNo().equals(staffNo)) {
                    retryStaff.add(s);
                    break;
                }
            }
        }
        int maxConcurrency = currentRequest.getMaxConcurrency() > 0 ? currentRequest.getMaxConcurrency() : 20;
        for (int i = 0; i < retryStaff.size(); i += maxConcurrency) {
            int end = Math.min(i + maxConcurrency, retryStaff.size());
            List<StaffInfo> batch = retryStaff.subList(i, end);
            List<Promise<StaffResult>> promises = new ArrayList<>();
            for (StaffInfo staff : batch) {
                promises.add(Async.function(this::processStaff, staff, currentRequest));
            }
            for (Promise<StaffResult> p : promises) {
                try {
                    StaffResult result = p.get();
                    String staffNo = result.getStaffNo();
                    StaffResult prev = resultMap.get(staffNo);
                    if (prev != null && !prev.isPassed()) {
                        if (result.isPassed()) {
                            progressSuccess++;
                            progressFail--;
                            failedStaffNos.remove(staffNo);
                        }
                    }
                    resultMap.put(staffNo, result);
                } catch (Exception e) {
                    Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                            .error(">>> 重试失败: {}", e.getMessage());
                }
            }
        }
        pendingRetryStaffNos.clear();
        retrySignaled = false;
    }

    private void sendWebhookCallback(String batchId, int total, int successCount, int failCount, String status) {
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                batchId, total, successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(PermissionDistributionWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }
    }
}
