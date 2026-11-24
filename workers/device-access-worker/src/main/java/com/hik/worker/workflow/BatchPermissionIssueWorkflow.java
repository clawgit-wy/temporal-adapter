package com.hik.worker.workflow;

import com.hik.worker.model.DevicePermissionRequest;
import com.hik.worker.model.DevicePermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 批量权限下发工作流接口
 * 并发下发多个人员的设备权限
 */
@WorkflowInterface
public interface BatchPermissionIssueWorkflow {
    @WorkflowMethod
    DevicePermissionResult batchIssue(DevicePermissionRequest request);
}
