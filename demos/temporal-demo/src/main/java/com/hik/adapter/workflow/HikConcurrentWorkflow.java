package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchRequest;
import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Demo 2: 并发批量校验工作流
 * 使用 Temporal 的 Promise 并发调用海康API，提升吞吐量
 * 并发度由 Worker 的 MaxConcurrentActivityExecutionSize(15) 控制
 */
@WorkflowInterface
public interface HikConcurrentWorkflow {

    @WorkflowMethod
    BatchResult processBatch(BatchRequest request);
}
