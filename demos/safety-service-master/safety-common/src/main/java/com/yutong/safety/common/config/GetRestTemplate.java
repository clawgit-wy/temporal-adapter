package com.yutong.safety.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author caomg
 * @date 2024-8-12 14:31
 * @description GetRestTemplate
 */
@Component
@Configuration
public class GetRestTemplate {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
