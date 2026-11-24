package com.hik.worker.config;

import com.hik.worker.activity.impl.DemoActivityImpl;
import com.hik.worker.workflow.impl.DemoWorkflowImpl;
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
 * 创建 DEMO_TASK_QUEUE 的 Worker, 注册 DemoWorkflow 与 DemoActivity, 并启动 WorkerFactory
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.address:127.0.0.1:7233}")
    private String temporalAddress;

    public static final String TASK_QUEUE_DEMO = "DEMO_TASK_QUEUE";

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
                                        DemoActivityImpl demoActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker(TASK_QUEUE_DEMO,
                WorkerOptions.newBuilder()
                        .setMaxConcurrentActivityExecutionSize(10)
                        .build());
        worker.registerWorkflowImplementationTypes(DemoWorkflowImpl.class);
        worker.registerActivitiesImplementations(demoActivity);
        log.info("Demo Worker 已注册, taskQueue: {}", TASK_QUEUE_DEMO);

        factory.start();
        log.info("WorkerFactory 已启动, taskQueue: {}", TASK_QUEUE_DEMO);
        return factory;
    }
}
