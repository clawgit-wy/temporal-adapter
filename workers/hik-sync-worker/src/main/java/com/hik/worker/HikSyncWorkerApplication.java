package com.hik.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 海康数据同步 Worker 启动类
 * 负责注册海康数据同步工作流
 * 通过 WORKER_SCENARIOS 环境变量控制启动哪些场景
 */
@SpringBootApplication
public class HikSyncWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HikSyncWorkerApplication.class, args);
    }
}
