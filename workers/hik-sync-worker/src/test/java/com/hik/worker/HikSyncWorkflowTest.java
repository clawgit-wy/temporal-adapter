package com.hik.worker;

import com.hik.worker.model.HkSyncRequest;
import com.hik.worker.model.HkSyncResult;
import com.hik.worker.workflow.HikSyncWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Disabled("需先启动 Temporal Server 与 Worker，手动执行时移除 @Disabled")
public class HikSyncWorkflowTest {

    @Test
    public void startIncrementalSync() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newConnectedServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget("127.0.0.1:7233")
                        .build(),
                Duration.ofSeconds(10));
        WorkflowClient client = WorkflowClient.newInstance(service);

        HkSyncRequest request = HkSyncRequest.builder()
                .syncType("INCREMENTAL")
                .timeWindowMinutes(123)
                .batchSize(900)
                .build();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("HIK_SYNC_TASK_QUEUE")
                .setWorkflowId("hik-sync-test-" + System.currentTimeMillis())
                .build();

        HikSyncWorkflow workflow = client.newWorkflowStub(HikSyncWorkflow.class, options);

        System.out.println(">>> 启动海康增量同步工作流...");
        HkSyncResult result = workflow.syncToHik(request);

        System.out.println(">>> 同步完成:");
        System.out.println("    状态:        " + result.getStatus());
        System.out.println("    黑名单数:    " + result.getBlackListCount());
        System.out.println("    员工数:      " + result.getEmployeeCount());
        System.out.println("    备案数:      " + result.getRegisterCount());
        System.out.println("    访客数:      " + result.getVisitorCount());
        System.out.println("    去重后总数:  " + result.getTotalAfterDedup());
        System.out.println("    成功写入数:  " + result.getSuccessCount());
        System.out.println("    失败数:      " + result.getFailCount());
        if (result.getErrorMessage() != null) {
            System.out.println("    错误信息:    " + result.getErrorMessage());
        }
    }

    @Test
    public void startFullSync() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newConnectedServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget("127.0.0.1:7233")
                        .build(),
                Duration.ofSeconds(10));
        WorkflowClient client = WorkflowClient.newInstance(service);

        HkSyncRequest request = HkSyncRequest.builder()
                .syncType("FULL")
                .customStartTime("2020-01-01 00:00:00")
                .customEndTime("2026-08-14 23:59:59")
                .batchSize(500)
                .build();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("HIK_SYNC_TASK_QUEUE")
                .setWorkflowId("hik-sync-full-" + System.currentTimeMillis())
                .build();

        HikSyncWorkflow workflow = client.newWorkflowStub(HikSyncWorkflow.class, options);
        HkSyncResult result = workflow.syncToHik(request);
        System.out.println(">>> 全量同步结果: " + result);
    }
}
