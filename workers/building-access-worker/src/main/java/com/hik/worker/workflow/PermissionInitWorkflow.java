package com.hik.worker.workflow;

import com.hik.worker.model.PermissionInitRequest;
import com.hik.worker.model.PermissionInitResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 权限全量初始化工作流接口
 * 从第三方系统拉取所有员工门禁权限并初始化
 */
@WorkflowInterface
public interface PermissionInitWorkflow {
    @WorkflowMethod
    PermissionInitResult initAllPermissions(PermissionInitRequest request);
}
