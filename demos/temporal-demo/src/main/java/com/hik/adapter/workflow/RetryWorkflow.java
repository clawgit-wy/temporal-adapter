package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 场景2: 第三方接口故障与自动指数退避重试
 * Activity前3次调用故意失败，第4次成功
 * RetryPolicy: initialInterval=1s, backoffCoefficient=2.0, maximumAttempts=5
 */
@WorkflowInterface
public interface RetryWorkflow {

    @WorkflowMethod
    BatchResult processBatch(int total);
}
