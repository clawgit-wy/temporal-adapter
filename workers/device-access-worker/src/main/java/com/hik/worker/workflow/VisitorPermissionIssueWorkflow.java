package com.hik.worker.workflow;

import com.hik.worker.model.VisitorPermissionRequest;
import com.hik.worker.model.VisitorPermissionResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 访客权限下发工作流接口
 * 含合并下发、涉密过滤、黑名单过滤等访客特有逻辑
 */
@WorkflowInterface
public interface VisitorPermissionIssueWorkflow {
    @WorkflowMethod
    VisitorPermissionResult issueVisitorPermission(VisitorPermissionRequest request);
}
