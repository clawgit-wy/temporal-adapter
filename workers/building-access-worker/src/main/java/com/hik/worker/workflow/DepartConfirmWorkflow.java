package com.hik.worker.workflow;

import com.hik.worker.model.DepartConfirmRequest;
import com.hik.worker.model.DepartConfirmResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 人员变更权限确认工作流接口
 * 员工部门变动时批量处理门禁权限
 */
@WorkflowInterface
public interface DepartConfirmWorkflow {
    @WorkflowMethod
    DepartConfirmResult processDepartChanges(DepartConfirmRequest request);
}
