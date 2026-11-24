package com.hik.worker.workflow;

import com.hik.worker.model.DevicePermissionRequest;
import com.hik.worker.model.DevicePermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 权限下发工作流接口
 * 流程：发行校验 -> 人脸发行 -> 卡发行 -> Webhook回调
 */
@WorkflowInterface
public interface PermissionIssueWorkflow {
    @WorkflowMethod
    DevicePermissionResult issuePermission(DevicePermissionRequest request);
}
