package com.hik.worker.workflow;

import com.hik.worker.model.DevicePermissionRequest;
import com.hik.worker.model.DevicePermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 增量权限工作流接口
 * 流程：人脸增量权限（增加/删除设备） -> Webhook回调
 */
@WorkflowInterface
public interface PermissionIncrementWorkflow {
    @WorkflowMethod
    DevicePermissionResult modifyIncrementPermission(DevicePermissionRequest request);
}
