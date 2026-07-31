package com.hik.worker.workflow;

import com.hik.worker.model.BatchRequest;
import com.hik.worker.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface HikConcurrentWorkflow {
    @WorkflowMethod
    BatchResult processBatch(BatchRequest request);
}
