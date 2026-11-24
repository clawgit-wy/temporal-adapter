package com.hik.worker.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通知 Activity 实现
 * 通过 webhook/HTTP 发送 YxtRebot 消息
 */
@Slf4j
@Component
public class NotificationActivityImpl implements NotificationActivity {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${notification.yxtrebot.url:http://localhost:18092/api/notification}")
    private String yxtRebotUrl;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public boolean sendNotification(String messageType, String recipient, String content) {
        log.info("发送通知, messageType: {}, recipient: {}", messageType, recipient);
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messageType", messageType);
            payload.put("recipient", recipient);
            payload.put("content", content);
            return doPost(yxtRebotUrl, payload);
        } catch (RuntimeException e) {
            log.warn("发送通知失败, recipient: {}", recipient);
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean sendBatchNotification(String messageType, List<String> recipients, String content) {
        log.info("批量发送通知, messageType: {}, 接收人数量: {}", messageType, recipients.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messageType", messageType);
            payload.put("recipients", recipients);
            payload.put("content", content);
            return doPost(yxtRebotUrl, payload);
        } catch (RuntimeException e) {
            log.warn("批量发送通知失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    /**
     * 发送 HTTP POST 请求（与 WebhookActivityImpl 一致的模式）
     */
    private boolean doPost(String url, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    log.info("YxtRebot通知发送成功, url: {}, 响应: {}", url, responseBody);
                    return true;
                } else {
                    log.warn("YxtRebot通知发送失败, url: {}, HTTP状态码: {}", url, response.code());
                    throw new RuntimeException("YxtRebot通知发送失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("YxtRebot通知发送异常, url: {}, 错误: {}", url, e.getMessage());
            throw new RuntimeException("YxtRebot通知发送异常: " + e.getMessage(), e);
        }
    }
}
