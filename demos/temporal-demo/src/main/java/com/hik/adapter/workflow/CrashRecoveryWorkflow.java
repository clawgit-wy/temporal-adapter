package com.hik.adapter.workflow;

import com.hik.adapter.model.BatchResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 场景3: Worker崩溃/断点续传工作流
 * 3个Activity按顺序执行，每个之间5秒延迟
 * Step1: 扣款 -> Step2: 下发门禁 -> Step3: 发短信
 * 验证Worker被Kill后重启，从断点处恢复
 */
@WorkflowInterface
public interface CrashRecoveryWorkflow {

    @WorkflowMethod
    BatchResult processBatch(int dummy);
}
