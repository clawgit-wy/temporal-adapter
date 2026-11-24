package com.hik.worker.workflow;

import com.hik.worker.model.DemoRequest;
import com.hik.worker.model.DemoResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Demo 工作流接口
 * 并行执行3个 Activity (调用mokapi/数据处理/生成报告), 用于验证 Temporal UI 集成
 */
@WorkflowInterface
public interface DemoWorkflow {
    @WorkflowMethod
    DemoResult run(DemoRequest request);

    @QueryMethod
    String getStatus();
}
