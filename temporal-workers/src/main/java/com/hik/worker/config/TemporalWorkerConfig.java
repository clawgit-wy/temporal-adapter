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
 * 通过 WORKER_SCENARIOS 环境变量控制启动哪些场景的Worker
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    @Value("${worker.scenarios:all}")
    private String workerScenarios;

    public static final String TASK_QUEUE_BATCH = "HIK_BATCH_TASK_QUEUE";
    public static final String TASK_QUEUE_RATE_LIMIT = "RATE_LIMIT_TASK_QUEUE";
    public static final String TASK_QUEUE_RETRY = "RETRY_TASK_QUEUE";
    public static final String TASK_QUEUE_CRASH = "CRASH_RECOVERY_TASK_QUEUE";
    public static final String TASK_QUEUE_HUMAN_LOOP = "HUMAN_LOOP_TASK_QUEUE";
    public static final String TASK_QUEUE_WEBHOOK = "WEBHOOK_TASK_QUEUE";

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
                                        com.hik.worker.activity.HikActivityImpl hikActivity,
                                        com.hik.worker.activity.RateLimitActivityImpl rateLimitActivity,
                                        com.hik.worker.activity.RetryActivityImpl retryActivity,
                                        com.hik.worker.activity.CrashRecoveryActivityImpl crashRecoveryActivity,
                                        com.hik.worker.activity.WebhookActivityImpl webhookActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        List<String> scenarios = Arrays.asList(workerScenarios.split(","));
        log.info("启动Worker场景: {}", scenarios);

        if (scenarios.contains("all") || scenarios.contains("demo")) {
            Worker worker = factory.newWorker(TASK_QUEUE_BATCH,
                    WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(15).build());
            worker.registerWorkflowImplementationTypes(
                    com.hik.worker.workflow.impl.HikBatchWorkflowImpl.class,
                    com.hik.worker.workflow.impl.HikConcurrentWorkflowImpl.class,
                    com.hik.worker.workflow.impl.HikTimeoutWorkflowImpl.class);
            worker.registerActivitiesImplementations(hikActivity);
            log.info("Demo Worker已注册, taskQueue: {}", TASK_QUEUE_BATCH);
        }

        if (scenarios.contains("all") || scenarios.contains("scenario1")) {
            Worker worker = factory.newWorker(TASK_QUEUE_RATE_LIMIT,
                    WorkerOptions.newBuilder()
                            .setMaxConcurrentActivityExecutionSize(20)
                            .setMaxWorkerActivitiesPerSecond(20.0)
                            .build());
            worker.registerWorkflowImplementationTypes(com.hik.worker.workflow.impl.RateLimitWorkflowImpl.class);
            worker.registerActivitiesImplementations(rateLimitActivity);
            log.info("场景1 Worker已注册, taskQueue: {}, 限流: 20 QPS", TASK_QUEUE_RATE_LIMIT);
        }

        if (scenarios.contains("all") || scenarios.contains("scenario2")) {
            Worker worker = factory.newWorker(TASK_QUEUE_RETRY);
            worker.registerWorkflowImplementationTypes(com.hik.worker.workflow.impl.RetryWorkflowImpl.class);
            worker.registerActivitiesImplementations(retryActivity);
            log.info("场景2 Worker已注册, taskQueue: {}", TASK_QUEUE_RETRY);
        }

        if (scenarios.contains("all") || scenarios.contains("scenario3")) {
            Worker worker = factory.newWorker(TASK_QUEUE_CRASH);
            worker.registerWorkflowImplementationTypes(com.hik.worker.workflow.impl.CrashRecoveryWorkflowImpl.class);
            worker.registerActivitiesImplementations(crashRecoveryActivity);
            log.info("场景3 Worker已注册, taskQueue: {}", TASK_QUEUE_CRASH);
        }

        if (scenarios.contains("all") || scenarios.contains("scenario4")) {
            Worker worker = factory.newWorker(TASK_QUEUE_HUMAN_LOOP);
            worker.registerWorkflowImplementationTypes(com.hik.worker.workflow.impl.HumanInLoopWorkflowImpl.class);
            log.info("场景4 Worker已注册, taskQueue: {}", TASK_QUEUE_HUMAN_LOOP);
        }

        if (scenarios.contains("all") || scenarios.contains("scenario5")) {
            Worker worker = factory.newWorker(TASK_QUEUE_WEBHOOK);
            worker.registerWorkflowImplementationTypes(com.hik.worker.workflow.impl.WebhookWorkflowImpl.class);
            worker.registerActivitiesImplementations(webhookActivity);
            log.info("场景5 Worker已注册, taskQueue: {}", TASK_QUEUE_WEBHOOK);
        }

        factory.start();
        log.info("WorkerFactory 已启动, 场景: {}", scenarios);
        return factory;
    }
}
