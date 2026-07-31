package com.hik.mokapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mock API 服务启动类
 * 模拟第三方接口（如海康威视开放平台）的响应行为
 *
 * 提供以下模拟接口：
 * 1. /api/hikvision/verify - 单人校验接口
 * 2. /api/hikvision/batch-verify - 批量校验接口
 * 3. /api/health - 健康检查
 */
@SpringBootApplication
public class MokApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MokApiApplication.class, args);
    }
}
