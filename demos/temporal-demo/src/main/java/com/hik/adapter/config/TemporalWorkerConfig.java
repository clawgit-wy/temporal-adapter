package com.hik.adapter.config;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Temporal Client 配置类（纯业务系统，不含 Worker）
 * 仅创建 WorkflowClient 用于提交和查询工作流
 * Worker 代码在独立的 temporal-workers 项目中
 */
@Slf4j
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    /** 各场景TaskQueue名称（与 temporal-workers 项目保持一致） */
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
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(temporalAddress)
                        .build(),
                Duration.ofSeconds(10)
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs);
    }
}
