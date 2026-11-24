package com.hik.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 设备权限下发 Worker 启动类
 * 负责注册所有工作流和Activity，监听 Temporal 任务队列
 * 支持富士、新开普设备的权限下发、改写、增量管理
 */
@SpringBootApplication
public class DeviceAccessWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeviceAccessWorkerApplication.class, args);
    }
}
