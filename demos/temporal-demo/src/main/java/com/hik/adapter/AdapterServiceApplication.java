package com.hik.adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * Temporal Demo 启动类
 * 包含 Worker、工作流、Mock海康接口、任务提交接口
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class AdapterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdapterServiceApplication.class, args);
    }
}
