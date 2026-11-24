package com.hik.worker.workflow;

import com.hik.worker.model.DevicePermissionRequest;
import com.hik.worker.model.DevicePermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 权限注销工作流接口
 * 流程：查询卡号 -> 注销人脸(3个GID) -> 注销卡(3个GID) -> Webhook回调
 */
@WorkflowInterface
public interface PermissionCancelWorkflow {
    @WorkflowMethod
    DevicePermissionResult cancelPermission(DevicePermissionRequest request);
}
