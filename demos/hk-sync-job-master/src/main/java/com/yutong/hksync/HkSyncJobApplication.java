package com.yutong.hksync;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 海康同步定时任务启动类
 */
@SpringBootApplication
@MapperScan("com.yutong.hksync.mapper")
public class HkSyncJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(HkSyncJobApplication.class, args);
    }
}
