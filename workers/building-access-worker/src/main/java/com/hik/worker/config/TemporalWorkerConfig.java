package com.hik.worker.config;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Temporal Worker 配置类
 * 通过 WORKER_SCENARIOS 环境变量控制启动哪些场景的 Worker
 * 可选值：all, permissionDistribution, deviceSync, permissionInit, departConfirm, hikSync
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    @Value("${worker.scenarios:all}")
    private String workerScenarios;

    public static final String TASK_QUEUE_PERMISSION_DISTRIBUTION = "PERMISSION_DISTRIBUTION_TASK_QUEUE";
    public static final String TASK_QUEUE_DEVICE_SYNC = "DEVICE_SYNC_TASK_QUEUE";
    public static final String TASK_QUEUE_PERMISSION_INIT = "PERMISSION_INIT_TASK_QUEUE";
    public static final String TASK_QUEUE_DEPART_CONFIRM = "DEPART_CONFIRM_TASK_QUEUE";
    public static final String TASK_QUEUE_HIK_SYNC = "HIK_SYNC_TASK_QUEUE";

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        log.info("连接 Temporal Server: {}", temporalAddress);
        return WorkflowServiceStubs.newConnectedServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(temporalAddress).build(),
                Duration.ofSeconds(10));
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs);
    }

    @Bean(destroyMethod = "shutdown")
    public WorkerFactory workerFactory(WorkflowClient workflowClient,
                                        com.hik.worker.activity.CheckTokenActivityImpl checkTokenActivity,
                                        com.hik.worker.activity.FaceIssueActivityImpl faceIssueActivity,
                                        com.hik.worker.activity.FaceModifyActivityImpl faceModifyActivity,
                                        com.hik.worker.activity.CardIssueActivityImpl cardIssueActivity,
                                        com.hik.worker.activity.FaceIncrementActivityImpl faceIncrementActivity,
                                        com.hik.worker.activity.PermissionCancelActivityImpl permissionCancelActivity,
                                        com.hik.worker.activity.WebhookActivityImpl webhookActivity,
                                        com.hik.worker.activity.DeviceSyncActivityImpl deviceSyncActivity,
                                        com.hik.worker.activity.PermissionQueryActivityImpl permissionQueryActivity,
                                        com.hik.worker.activity.LogActivityImpl logActivity,
                                        com.hik.worker.activity.NotificationActivityImpl notificationActivity,
                                        com.hik.worker.activity.ValidateActivityImpl validateActivity,
                                        com.hik.worker.activity.HikSyncActivityImpl hikSyncActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        List<String> scenarios = Arrays.asList(workerScenarios.split(","));
        log.info("启动Worker场景: {}", scenarios);

        // 场景：权限下发（校验 → 创建日志 → 并行下发 → 汇总 → 更新状态 → 通知 → Webhook）
        if (scenarios.contains("all") || scenarios.contains("permissionDistribution")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_DISTRIBUTION,
                    WorkerOptions.newBuilder()
                            .setMaxConcurrentActivityExecutionSize(20)
                            .setMaxWorkerActivitiesPerSecond(20.0)
                            .build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionDistributionWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    checkTokenActivity, faceIssueActivity, cardIssueActivity,
                    faceIncrementActivity, permissionCancelActivity,
                    logActivity, validateActivity, notificationActivity, webhookActivity);
            log.info("权限下发Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_DISTRIBUTION);
        }

        // 场景：设备数据同步（拉取 → 合并 → 保存 → 标记移除 → 通知 → Webhook）
        if (scenarios.contains("all") || scenarios.contains("deviceSync")) {
            Worker worker = factory.newWorker(TASK_QUEUE_DEVICE_SYNC,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(10).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.DeviceSyncWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    deviceSyncActivity, notificationActivity, webhookActivity);
            log.info("设备同步Worker已注册, taskQueue: {}", TASK_QUEUE_DEVICE_SYNC);
        }

        // 场景：权限全量初始化（并行拉取 → 合并 → 去重 → 创建日志 → Webhook）
        if (scenarios.contains("all") || scenarios.contains("permissionInit")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_INIT,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(15).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionInitWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    permissionQueryActivity, logActivity, webhookActivity);
            log.info("权限初始化Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_INIT);
        }

        // 场景：人员变更权限确认（校验 → 发行/注销 → 通知 → Webhook）
        if (scenarios.contains("all") || scenarios.contains("departConfirm")) {
            Worker worker = factory.newWorker(TASK_QUEUE_DEPART_CONFIRM,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(15).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.DepartConfirmWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    validateActivity, checkTokenActivity, faceIssueActivity, cardIssueActivity,
                    faceIncrementActivity, permissionCancelActivity,
                    notificationActivity, webhookActivity);
            log.info("人员变更确认Worker已注册, taskQueue: {}", TASK_QUEUE_DEPART_CONFIRM);
        }

        // 场景：海康数据同步（并行查询 → 合并 → 处理人脸图片 → 去重 → 批量写入海康中间库）
        if (scenarios.contains("all") || scenarios.contains("hikSync")) {
            Worker worker = factory.newWorker(TASK_QUEUE_HIK_SYNC,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(10).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.HikSyncWorkflowImpl.class);
            worker.registerActivitiesImplementations(hikSyncActivity);
            log.info("海康同步Worker已注册, taskQueue: {}", TASK_QUEUE_HIK_SYNC);
        }

        factory.start();
        log.info("WorkerFactory 已启动, 场景: {}", scenarios);
        return factory;
    }
}
