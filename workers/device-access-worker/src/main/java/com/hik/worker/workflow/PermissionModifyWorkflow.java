package com.hik.worker.workflow;

import com.hik.worker.model.DevicePermissionRequest;
import com.hik.worker.model.DevicePermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 权限改写工作流接口
 * 流程：发行校验 -> 人脸改写 -> Webhook回调
 */
@WorkflowInterface
public interface PermissionModifyWorkflow {
    @WorkflowMethod
    DevicePermissionResult modifyPermission(DevicePermissionRequest request);
}
