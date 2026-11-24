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
 * 可选值：all, permissionIssue, permissionModify, permissionCancel, permissionIncrement, batchIssue, visitorIssue
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    @Value("${worker.scenarios:all}")
    private String workerScenarios;

    public static final String TASK_QUEUE_PERMISSION_ISSUE = "PERMISSION_ISSUE_TASK_QUEUE";
    public static final String TASK_QUEUE_PERMISSION_MODIFY = "PERMISSION_MODIFY_TASK_QUEUE";
    public static final String TASK_QUEUE_PERMISSION_CANCEL = "PERMISSION_CANCEL_TASK_QUEUE";
    public static final String TASK_QUEUE_PERMISSION_INCREMENT = "PERMISSION_INCREMENT_TASK_QUEUE";
    public static final String TASK_QUEUE_BATCH_ISSUE = "BATCH_ISSUE_TASK_QUEUE";
    public static final String TASK_QUEUE_VISITOR_ISSUE = "VISITOR_ISSUE_TASK_QUEUE";

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
                                        com.hik.worker.activity.WebhookActivityImpl webhookActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        List<String> scenarios = Arrays.asList(workerScenarios.split(","));
        log.info("启动Worker场景: {}", scenarios);

        // 场景：权限下发（校验->人脸发行->卡发行->回调）
        if (scenarios.contains("all") || scenarios.contains("permissionIssue")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_ISSUE,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(15).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionIssueWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    checkTokenActivity, faceIssueActivity, cardIssueActivity, webhookActivity);
            log.info("权限下发Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_ISSUE);
        }

        // 场景：权限改写（校验->人脸改写->回调）
        if (scenarios.contains("all") || scenarios.contains("permissionModify")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_MODIFY);
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionModifyWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    checkTokenActivity, faceModifyActivity, webhookActivity);
            log.info("权限改写Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_MODIFY);
        }

        // 场景：权限注销（查询卡号->注销人脸->注销卡->回调）
        if (scenarios.contains("all") || scenarios.contains("permissionCancel")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_CANCEL);
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionCancelWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    permissionCancelActivity, webhookActivity);
            log.info("权限注销Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_CANCEL);
        }

        // 场景：增量权限（增加/删除设备权限）
        if (scenarios.contains("all") || scenarios.contains("permissionIncrement")) {
            Worker worker = factory.newWorker(TASK_QUEUE_PERMISSION_INCREMENT);
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.PermissionIncrementWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    faceIncrementActivity, webhookActivity);
            log.info("增量权限Worker已注册, taskQueue: {}", TASK_QUEUE_PERMISSION_INCREMENT);
        }

        // 场景：批量权限下发
        if (scenarios.contains("all") || scenarios.contains("batchIssue")) {
            Worker worker = factory.newWorker(TASK_QUEUE_BATCH_ISSUE,
                    WorkerOptions.newBuilder()
                            .setMaxConcurrentActivityExecutionSize(20)
                            .setMaxWorkerActivitiesPerSecond(20.0)
                            .build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.BatchPermissionIssueWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    checkTokenActivity, faceIssueActivity, cardIssueActivity,
                    faceModifyActivity, faceIncrementActivity, webhookActivity);
            log.info("批量权限下发Worker已注册, taskQueue: {}", TASK_QUEUE_BATCH_ISSUE);
        }

        // 场景：访客权限下发（含合并下发、涉密过滤、黑名单过滤）
        if (scenarios.contains("all") || scenarios.contains("visitorIssue")) {
            Worker worker = factory.newWorker(TASK_QUEUE_VISITOR_ISSUE,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(15).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.VisitorPermissionIssueWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    checkTokenActivity, faceIssueActivity, cardIssueActivity, webhookActivity);
            log.info("访客权限下发Worker已注册, taskQueue: {}", TASK_QUEUE_VISITOR_ISSUE);
        }

        factory.start();
        log.info("WorkerFactory 已启动, 场景: {}", scenarios);
        return factory;
    }
}
