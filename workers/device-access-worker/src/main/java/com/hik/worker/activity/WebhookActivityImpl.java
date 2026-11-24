package com.hik.worker.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Webhook 回调 Activity 实现
 */
@Slf4j
@Component
public class WebhookActivityImpl implements WebhookActivity {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${webhook.callback.url}")
    private String webhookCallbackUrl;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public boolean sendWebhook(String payload) {
        log.info("发送Webhook回调, url: {}, payload: {}", webhookCallbackUrl, payload);
        try {
            RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
            Request request = new Request.Builder().url(webhookCallbackUrl).post(body).build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    log.info("Webhook回调发送成功, 响应: {}", responseBody);
                    return true;
                } else {
                    log.warn("Webhook回调失败, HTTP状态码: {}", response.code());
                    throw new RuntimeException("Webhook回调失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("Webhook回调发送异常: {}", e.getMessage());
            throw new RuntimeException("Webhook回调发送异常: " + e.getMessage(), e);
        }
    }
}
