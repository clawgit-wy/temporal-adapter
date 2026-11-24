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
 * 仅注册海康数据同步工作流
 * 通过 WORKER_SCENARIOS 环境变量控制启动哪些场景
 * 可选值：all, hikSync
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    @Value("${worker.scenarios:hikSync}")
    private String workerScenarios;

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
                                        com.hik.worker.activity.HikSyncActivityImpl hikSyncActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        List<String> scenarios = Arrays.asList(workerScenarios.split(","));
        log.info("启动Worker场景: {}", scenarios);

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
