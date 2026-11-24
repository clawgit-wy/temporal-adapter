package com.yutong.modules.risk.vo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qr.base-url")
public class QrBaseUrlConfig {
    private String sign;
    private String pass;
}