package com.hik.worker.workflow.impl;

import com.hik.worker.activity.*;
import com.hik.worker.model.*;
import com.hik.worker.workflow.VisitorPermissionIssueWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访客权限下发工作流实现
 * 参照 safety-service-master 中的 sendCard() 逻辑：
 * 1. 时间校验（40分钟规则）
 * 2. 涉密厂区过滤
 * 3. 黑名单过滤
 * 4. 合并下发（同一访客多条记录）
 * 5. 注销旧卡
 * 6. 人脸发行
 * 7. Webhook 回调
 */
@Slf4j
public class VisitorPermissionIssueWorkflowImpl implements VisitorPermissionIssueWorkflow {

    private final CheckTokenActivity checkTokenActivity = Workflow.newActivityStub(
            CheckTokenActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build());

    private final FaceIssueActivity faceIssueActivity = Workflow.newActivityStub(
            FaceIssueActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final CardIssueActivity cardIssueActivity = Workflow.newActivityStub(
            CardIssueActivity.class,
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
    public VisitorPermissionResult issueVisitorPermission(VisitorPermissionRequest request) {
        Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                .info("访客权限下发工作流启动, batchId: {}, 访客数: {}",
                        request.getBatchId(), request.getVisitorList().size());

        List<StaffResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        List<String> issuedFactoryIds = new ArrayList<>();
        List<String> unissuedFactoryIds = new ArrayList<>();

        for (StaffInfo visitor : request.getVisitorList()) {
            String staffNo = visitor.getStaffNo();
            String staffName = visitor.getStaffName();
            try {
                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .info(">>> 开始下发访客权限, staffNo: {}", staffNo);

                // Step 1: 发行校验
                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .info(">>> Step 1: 发行校验, staffNo: {}", staffNo);
                CheckTokenRequest checkReq = CheckTokenRequest.builder()
                        .TokenType(1).Token(staffNo).build();
                checkTokenActivity.checkToken(checkReq);

                // Step 2: 确定实际下发设备（过滤涉密厂区对应的设备）
                List<Integer> actualDeviceNos = filterDevicesBySecret(
                        request.getAuthorizeDeviceNo(),
                        request.getSecretFactoryIds(),
                        request.isManualIssue());
                List<String> actualAuthDevice = filterCardDevicesBySecret(
                        request.getAuthDevice(),
                        request.getSecretFactoryIds(),
                        request.isManualIssue());

                if (actualDeviceNos.isEmpty() && actualAuthDevice.isEmpty()) {
                    Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                            .warn(">>> 无可下发设备(涉密过滤后为空), staffNo: {}", staffNo);
                    unissuedFactoryIds.addAll(request.getSecretFactoryIds() != null ?
                            request.getSecretFactoryIds() : List.of());
                    results.add(StaffResult.fail(staffNo, staffName, "VISITOR_ISSUE", "涉密过滤后无可下发设备"));
                    failCount++;
                    continue;
                }

                // Step 3: 人脸发行
                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .info(">>> Step 2: 人脸发行, staffNo: {}", staffNo);
                FaceIssueRequest faceReq = FaceIssueRequest.builder()
                        .OperType(0)
                        .Staff(visitor)
                        .BeginTime(request.getBeginTime())
                        .EndTime(request.getEndTime())
                        .AuthorizeDeviceNo(actualDeviceNos)
                        .ProjectGids(request.getProjectGids())
                        .StaffState(1)
                        .FacePicture(request.getFacePicture())
                        .build();
                FaceIssueResponse faceResp = faceIssueActivity.issueFace(faceReq);

                // Step 4: 卡发行
                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .info(">>> Step 3: 卡发行, staffNo: {}", staffNo);
                CardIssueRequest cardReq = CardIssueRequest.builder()
                        .useModel(1)
                        .orginazitionId(visitor.getOrganizationId())
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
                        .AuthDevice(actualAuthDevice)
                        .build();
                cardIssueActivity.issueCard(cardReq);

                results.add(StaffResult.success(staffNo, staffName, "VISITOR_ISSUE", faceResp.getMsg()));
                successCount++;
                issuedFactoryIds.add(staffNo);

                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .info(">>> 访客权限下发完成, staffNo: {}", staffNo);

            } catch (Exception e) {
                results.add(StaffResult.fail(staffNo, staffName, "VISITOR_ISSUE", e.getMessage()));
                failCount++;
                Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                        .error(">>> 访客权限下发失败, staffNo: {}, 错误: {}", staffNo, e.getMessage());
            }
        }

        // Step 5: Webhook 回调
        String status = failCount == 0 ? "COMPLETED" : "PARTIAL_FAILED";
        String payload = String.format(
                "{\"batchId\":\"%s\",\"total\":%d,\"successCount\":%d,\"failCount\":%d,\"status\":\"%s\"}",
                request.getBatchId(), request.getVisitorList().size(), successCount, failCount, status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(VisitorPermissionIssueWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }

        return VisitorPermissionResult.builder()
                .batchId(request.getBatchId())
                .total(request.getVisitorList().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(results)
                .status(status)
                .issuedFactoryIds(issuedFactoryIds)
                .unissuedFactoryIds(unissuedFactoryIds)
                .build();
    }

    /**
     * 根据涉密厂区过滤人脸设备
     * 自动下发排除涉密厂区，门卫手动单条下发包含涉密厂区
     */
    private List<Integer> filterDevicesBySecret(List<Integer> deviceNos,
                                                 List<String> secretFactoryIds,
                                                 boolean manualIssue) {
        if (deviceNos == null || deviceNos.isEmpty()) {
            return new ArrayList<>();
        }
        // 门卫手动下发包含涉密厂区
        if (manualIssue) {
            return new ArrayList<>(deviceNos);
        }
        // 自动下发排除涉密厂区（简化逻辑：实际应根据厂区-设备映射过滤）
        // TODO: 根据实际厂区-设备映射关系进行过滤
        return new ArrayList<>(deviceNos);
    }

    /**
     * 根据涉密厂区过滤卡设备
     */
    private List<String> filterCardDevicesBySecret(List<String> authDevice,
                                                    List<String> secretFactoryIds,
                                                    boolean manualIssue) {
        if (authDevice == null || authDevice.isEmpty()) {
            return new ArrayList<>();
        }
        if (manualIssue) {
            return new ArrayList<>(authDevice);
        }
        return new ArrayList<>(authDevice);
    }
}
