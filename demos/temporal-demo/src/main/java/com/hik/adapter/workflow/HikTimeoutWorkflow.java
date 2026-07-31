package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchRequest;
import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Demo 3: 带超时控制的批量校验工作流
 * 设置整体超时时间，超时后返回已完成的部分结果
 */
@WorkflowInterface
public interface HikTimeoutWorkflow {

    @WorkflowMethod
    BatchResult processBatch(BatchRequest request);
}
