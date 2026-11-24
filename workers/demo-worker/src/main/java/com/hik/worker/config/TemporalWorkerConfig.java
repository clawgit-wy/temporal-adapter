package com.hik.worker.config;

import com.hik.worker.activity.impl.DemoActivityImpl;
import com.hik.worker.activity.impl.PermissionActivityImpl;
import com.hik.worker.workflow.impl.DemoWorkflowImpl;
import com.hik.worker.workflow.impl.PermissionWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Temporal Worker 配置类
 * 创建 DEMO_TASK_QUEUE 的 Worker, 注册 DemoWorkflow/DemoActivity 和 PermissionWorkflow/PermissionActivity
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.address:127.0.0.1:7233}")
    private String temporalAddress;

    public static final String TASK_QUEUE_DEMO = "DEMO_TASK_QUEUE";
    public static final String TASK_QUEUE_PERMISSION = "PERMISSION_TASK_QUEUE";

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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(destroyMethod = "shutdown")
    public WorkerFactory workerFactory(WorkflowClient workflowClient,
                                        DemoActivityImpl demoActivity,
                                        PermissionActivityImpl permissionActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        // Demo Worker
        Worker demoWorker = factory.newWorker(TASK_QUEUE_DEMO,
                WorkerOptions.newBuilder()
                        .setMaxConcurrentActivityExecutionSize(10)
                        .build());
        demoWorker.registerWorkflowImplementationTypes(DemoWorkflowImpl.class);
        demoWorker.registerActivitiesImplementations(demoActivity);
        log.info("Demo Worker 已注册, taskQueue: {}", TASK_QUEUE_DEMO);

        // Permission Worker
        Worker permissionWorker = factory.newWorker(TASK_QUEUE_PERMISSION,
                WorkerOptions.newBuilder()
                        .setMaxConcurrentActivityExecutionSize(10)
                        .build());
        permissionWorker.registerWorkflowImplementationTypes(PermissionWorkflowImpl.class);
        permissionWorker.registerActivitiesImplementations(permissionActivity);
        log.info("Permission Worker 已注册, taskQueue: {}", TASK_QUEUE_PERMISSION);

        factory.start();
        log.info("WorkerFactory 已启动, taskQueues: [{}, {}]", TASK_QUEUE_DEMO, TASK_QUEUE_PERMISSION);
        return factory;
    }
}
