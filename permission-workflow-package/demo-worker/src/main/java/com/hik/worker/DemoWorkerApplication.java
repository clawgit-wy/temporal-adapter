package com.hik.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo Worker 启动类
 * 用于验证 Temporal UI 集成的轻量级工作流, 通过 mokapi 模拟第三方接口调用, 无需数据库
 */
@SpringBootApplication
public class DemoWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoWorkerApplication.class, args);
    }
}
