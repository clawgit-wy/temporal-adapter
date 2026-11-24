package com.hik.worker.workflow;

import com.hik.worker.model.DistributionProgress;
import com.hik.worker.model.PermissionDistributionRequest;
import com.hik.worker.model.PermissionDistributionResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

/**
 * 权限下发工作流接口
 * 大规模权限下发：校验 → 创建日志 → 并行下发 → 汇总 → 更新状态 → 通知
 */
@WorkflowInterface
public interface PermissionDistributionWorkflow {
    @WorkflowMethod
    PermissionDistributionResult distributePermissions(PermissionDistributionRequest request);

    @SignalMethod
    void retryFailedItems(List<String> staffNos);

    @QueryMethod
    DistributionProgress getProgress();
}
