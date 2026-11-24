package com.hik.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 楼宇门禁业务工作流 Worker 启动类
 * 负责注册权限下发、设备同步、权限初始化、人员变更确认等工作流
 * 通过 WORKER_SCENARIOS 环境变量控制启动哪些场景
 */
@SpringBootApplication
public class BuildingAccessWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildingAccessWorkerApplication.class, args);
    }
}
