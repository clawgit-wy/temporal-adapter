package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 场景1: 海量并发与限流工作流
 * 模拟500人x270门禁的批量下发，验证Temporal的限流能力
 * 1000次Activity调用，由Worker端setMaxWorkerActivityTasksPerSecond(20)限制为20 QPS
 */
@WorkflowInterface
public interface RateLimitWorkflow {

    /**
     * @param total 总调用次数（如1000）
     * @return 批量处理结果
     */
    @WorkflowMethod
    BatchResult processBatch(int total);
}
