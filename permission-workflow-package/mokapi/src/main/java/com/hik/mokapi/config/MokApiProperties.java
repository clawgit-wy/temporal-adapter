package com.hik.mokapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mock API 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "mokapi")
public class MokApiProperties {

    /** 默认延迟(毫秒) */
    private long defaultDelayMs = 0;

    /** 错误率(0.0~1.0) */
    private double errorRate = 0.0;

    /** 慢响应延迟(毫秒) */
    private long slowDelayMs = 3000;
}
