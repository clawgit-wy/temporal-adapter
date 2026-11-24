package com.yutong.safety.web.config;

import com.yutong.monitor.Monitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * falcon监控切面初始化
 *
 * @author gwcuiyx
 * @date 2024-1-11 19:52
 */
@Configuration
public class VisionConfig {
    @Value("${vision.appname}")
    private String appName;

    @Value("${vision.agentUrl}")
    private String agentUrl;


    @PostConstruct
    public void init() {
        Monitor.MonitorBuilder builder = new Monitor.MonitorBuilder();
        builder.endPoint(appName).agentUrl(agentUrl).build().start();
    }
}
