package com.hik.worker.workflow;

import com.hik.worker.model.HkSyncRequest;
import com.hik.worker.model.HkSyncResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * 海康数据同步工作流接口
 * 增量同步安保系统人员/人脸数据到海康中间库
 */
@WorkflowInterface
public interface HikSyncWorkflow {
    @WorkflowMethod
    HkSyncResult syncToHik(HkSyncRequest request);

    @QueryMethod
    String getSyncProgress();
}
