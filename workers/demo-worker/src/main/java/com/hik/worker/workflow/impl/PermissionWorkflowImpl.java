package com.hik.worker.workflow.impl;

import com.hik.worker.activity.PermissionActivity;
import com.hik.worker.model.PermissionIssueRequest;
import com.hik.worker.model.PermissionIssueResult;
import com.hik.worker.workflow.PermissionWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 人员权限下发工作流实现
 *
 * 流程:
 * 1. 记录开始时间
 * 2. 调用 checkTokenState 进行发行校验
 * 3. 根据操作类型调用对应的发行/改写接口
 * 4. 汇总结果并返回
 *
 * 校验失败(返回code!=200)时:
 * - 如果是 FACE_ISSUE 或 DOOR_ISSUE, 继续发行(新用户)
 * - 如果是 FACE_MODIFY, POWER_ADD, POWER_DELETE, 返回失败
 */
@Slf4j
public class PermissionWorkflowImpl implements PermissionWorkflow {

    /** 校验Token Activity: 30s超时, 3次重试, 2s退避 */
    private final PermissionActivity checkActivity = Workflow.newActivityStub(PermissionActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 发行/改写 Activity: 60s超时, 3次重试 */
    private final PermissionActivity issueActivity = Workflow.newActivityStub(PermissionActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(3))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 当前进度 (通过 QueryMethod 查询) */
    private String status = "INIT";

    @Override
    public PermissionIssueResult run(PermissionIssueRequest request) {
        Workflow.getLogger(PermissionWorkflowImpl.class)
                .info("权限下发工作流启动, workflowId: {}, operationType: {}",
                        request.getWorkflowId(), request.getOperationType());

        long startTime = Workflow.currentTimeMillis();
        List<String> steps = new ArrayList<>();
        String operationType = request.getOperationType();

        // Step 1: 发行校验
        status = "CHECKING_TOKEN";
        steps.add("开始发行校验");
        Workflow.getLogger(PermissionWorkflowImpl.class)
                .info(">>> Step 1: 发行校验, token: {}, tokenType: {}",
                        request.getToken(), request.getTokenType());

        String checkResult;
        boolean tokenExists = false;
        try {
            checkResult = checkActivity.checkTokenState(request);
            steps.add("发行校验完成: " + summarizeResult(checkResult));
            // code=200 表示已存在, code=500 表示不存在
            tokenExists = checkResult != null && checkResult.contains("\"code\":200");
            Workflow.getLogger(PermissionWorkflowImpl.class)
                    .info("发行校验结果: tokenExists={}", tokenExists);
        } catch (Exception e) {
            Workflow.getLogger(PermissionWorkflowImpl.class)
                    .error("发行校验异常: {}", e.getMessage());
            steps.add("发行校验异常: " + e.getMessage());
            long duration = Workflow.currentTimeMillis() - startTime;
            return PermissionIssueResult.builder()
                    .success(false)
                    .message("发行校验失败: " + e.getMessage())
                    .operationType(operationType)
                    .tokenExists(false)
                    .steps(steps)
                    .duration(duration)
                    .build();
        }

        // Step 2: 根据操作类型执行对应操作
        status = "ISSUING_PERMISSION";
        String issueResult = null;
        boolean success = false;
        String message;

        switch (operationType) {
            case "FACE_ISSUE":
                steps.add("执行人脸发行");
                Workflow.getLogger(PermissionWorkflowImpl.class).info(">>> Step 2: 人脸发行");
                if (tokenExists) {
                    message = "Token已存在,无需重复发行";
                    steps.add("Token已存在,跳过人脸发行");
                    success = true;
                } else {
                    issueResult = issueActivity.faceIssue(request);
                    steps.add("人脸发行完成: " + summarizeResult(issueResult));
                    success = isResponseSuccess(issueResult);
                    message = success ? "人脸发行成功" : "人脸发行失败";
                }
                break;

            case "FACE_MODIFY":
                steps.add("执行人脸改写");
                Workflow.getLogger(PermissionWorkflowImpl.class).info(">>> Step 2: 人脸改写");
                if (!tokenExists) {
                    long duration = Workflow.currentTimeMillis() - startTime;
                    return PermissionIssueResult.builder()
                            .success(false)
                            .message("Token不存在,无法改写")
                            .operationType(operationType)
                            .tokenExists(false)
                            .checkResult(checkResult)
                            .steps(steps)
                            .duration(duration)
                            .build();
                }
                issueResult = issueActivity.faceModify(request);
                steps.add("人脸改写完成: " + summarizeResult(issueResult));
                success = isResponseSuccess(issueResult);
                message = success ? "人脸改写成功" : "人脸改写失败";
                break;

            case "DOOR_ISSUE":
                steps.add("执行卡发行");
                Workflow.getLogger(PermissionWorkflowImpl.class).info(">>> Step 2: 卡发行");
                issueResult = issueActivity.doorIssue(request);
                steps.add("卡发行完成: " + summarizeResult(issueResult));
                success = isResponseSuccess(issueResult);
                message = success ? "卡发行成功" : "卡发行失败";
                break;

            case "POWER_ADD":
                steps.add("执行增量增加权限");
                Workflow.getLogger(PermissionWorkflowImpl.class).info(">>> Step 2: 增量增加权限");
                issueResult = issueActivity.faceModifyBypowerType(request, 0);
                steps.add("增量增加权限完成: " + summarizeResult(issueResult));
                success = isResponseSuccess(issueResult);
                message = success ? "增量增加权限成功" : "增量增加权限失败";
                break;

            case "POWER_DELETE":
                steps.add("执行增量删除权限");
                Workflow.getLogger(PermissionWorkflowImpl.class).info(">>> Step 2: 增量删除权限");
                issueResult = issueActivity.faceModifyBypowerType(request, 1);
                steps.add("增量删除权限完成: " + summarizeResult(issueResult));
                success = isResponseSuccess(issueResult);
                message = success ? "增量删除权限成功" : "增量删除权限失败";
                break;

            default:
                message = "不支持的操作类型: " + operationType;
                steps.add(message);
                Workflow.getLogger(PermissionWorkflowImpl.class).error(message);
                break;
        }

        // Step 3: 汇总结果
        status = "COMPLETED";
        long duration = Workflow.currentTimeMillis() - startTime;
        Workflow.getLogger(PermissionWorkflowImpl.class)
                .info(">>> Step 3: 工作流完成, success: {}, duration: {}ms", success, duration);

        return PermissionIssueResult.builder()
                .success(success)
                .message(message)
                .operationType(operationType)
                .tokenExists(tokenExists)
                .checkResult(checkResult)
                .issueResult(issueResult)
                .steps(steps)
                .duration(duration)
                .build();
    }

    @Override
    public String getStatus() {
        return status;
    }

    // ========== 私有方法 ==========

    /**
     * 简化响应结果用于步骤记录
     */
    private String summarizeResult(String result) {
        if (result == null || result.isEmpty()) {
            return "空响应";
        }
        if (result.length() > 100) {
            return result.substring(0, 100) + "...";
        }
        return result;
    }

    /**
     * 判断 mokapi 响应是否成功 (code=200)
     */
    private boolean isResponseSuccess(String result) {
        return result != null && result.contains("\"code\":200");
    }
}
