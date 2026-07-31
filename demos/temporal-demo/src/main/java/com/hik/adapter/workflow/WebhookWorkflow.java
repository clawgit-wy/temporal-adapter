package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 场景5: 结果通知与Webhook回调工作流
 * 执行完批量任务后，通过HTTP POST将结果回调给业务系统
 * 验证Webhook回调的可靠性及结果提取
 */
@WorkflowInterface
public interface WebhookWorkflow {

    /**
     * @param total 批量任务总数
     * @return 批量处理结果（含webhook发送结果）
     */
    @WorkflowMethod
    BatchResult processBatch(int total);
}
