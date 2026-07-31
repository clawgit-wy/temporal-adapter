package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchRequest;
import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Demo 1: 基础批量校验工作流
 * 顺序遍历所有人员，逐个调用海康API校验
 */
@WorkflowInterface
public interface HikBatchWorkflow {

    /**
     * 批量校验入口
     *
     * @param request 批量请求
     * @return 批量校验结果
     */
    @WorkflowMethod
    BatchResult processBatch(BatchRequest request);
}
