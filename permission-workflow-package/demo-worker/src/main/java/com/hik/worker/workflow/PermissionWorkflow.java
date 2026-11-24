package com.hik.worker.workflow;

import com.hik.worker.model.PermissionIssueRequest;
import com.hik.worker.model.PermissionIssueResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 人员权限下发工作流接口
 *
 * 支持的操作类型:
 * - FACE_ISSUE:    先校验Token -> 再人脸发行
 * - FACE_MODIFY:   先校验Token -> 再人脸改写
 * - DOOR_ISSUE:    先校验Token -> 再卡发行
 * - POWER_ADD:     先校验Token -> 再增量增加权限
 * - POWER_DELETE:  先校验Token -> 再增量删除权限
 */
@WorkflowInterface
public interface PermissionWorkflow {

    @WorkflowMethod
    PermissionIssueResult run(PermissionIssueRequest request);

    @QueryMethod
    String getStatus();
}
