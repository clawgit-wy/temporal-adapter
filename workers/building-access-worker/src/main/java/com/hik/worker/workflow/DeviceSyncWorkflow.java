package com.hik.worker.workflow;

import com.hik.worker.model.DeviceSyncRequest;
import com.hik.worker.model.DeviceSyncResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 设备数据同步工作流接口
 * 从第三方门禁系统全量/增量同步设备数据
 */
@WorkflowInterface
public interface DeviceSyncWorkflow {
    @WorkflowMethod
    DeviceSyncResult syncDevices(DeviceSyncRequest request);
}
