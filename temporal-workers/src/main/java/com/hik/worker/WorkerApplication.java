package com.hik.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Temporal Worker 独立启动类
 * 负责注册所有 Workflow 和 Activity，监听 Temporal 任务队列
 */
@SpringBootApplication
public class WorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
