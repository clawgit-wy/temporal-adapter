package com.hik.worker.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 日志管理 Activity 实现
 * 通过 webhook 调用业务系统 API 进行下发日志管理
 */
@Slf4j
@Component
public class LogActivityImpl implements LogActivity {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${webhook.callback.url}")
    private String webhookCallbackUrl;

    @Value("${log.api.create-path:/api/log/createIssueLogs}")
    private String createPath;

    @Value("${log.api.update-status-path:/api/log/updateStatus}")
    private String updateStatusPath;

    @Value("${log.api.batch-update-status-path:/api/log/batchUpdateStatus}")
    private String batchUpdateStatusPath;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public List<Integer> createIssueLogs(List<Map<String, Object>> logs) {
        log.info("创建下发日志, 数量: {}", logs.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("logs", logs);
            Object result = doPost(webhookCallbackUrl + createPath, payload);
            return parseLogIds(result, logs.size());
        } catch (RuntimeException e) {
            log.warn("创建下发日志失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean updateLogStatus(int logId, String status, String remark) {
        log.info("更新日志状态, logId: {}, status: {}", logId, status);
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("logId", logId);
            payload.put("status", status);
            payload.put("remark", remark);
            return doPost(webhookCallbackUrl + updateStatusPath, payload) != null;
        } catch (RuntimeException e) {
            log.warn("更新日志状态失败, logId: {}", logId);
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean batchUpdateLogStatus(List<Integer> logIds, String status) {
        log.info("批量更新日志状态, 数量: {}, status: {}", logIds.size(), status);
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("logIds", logIds);
            payload.put("status", status);
            return doPost(webhookCallbackUrl + batchUpdateStatusPath, payload) != null;
        } catch (RuntimeException e) {
            log.warn("批量更新日志状态失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    /**
     * 发送 HTTP POST 请求（与 WebhookActivityImpl 一致的模式）
     */
    private Object doPost(String url, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    log.info("日志接口调用成功, url: {}, 响应: {}", url, responseBody);
                    return objectMapper.readValue(responseBody, Object.class);
                } else {
                    log.warn("日志接口调用失败, url: {}, HTTP状态码: {}", url, response.code());
                    throw new RuntimeException("日志接口调用失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("日志接口调用异常, url: {}, 错误: {}", url, e.getMessage());
            throw new RuntimeException("日志接口调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从响应中解析日志ID列表
     */
    @SuppressWarnings("unchecked")
    private List<Integer> parseLogIds(Object result, int expectedSize) {
        List<Integer> logIds = new ArrayList<>();
        if (result instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) result;
            Object data = map.get("data");
            if (data instanceof List) {
                for (Object item : (List<?>) data) {
                    if (item instanceof Map) {
                        Object id = ((Map<String, Object>) item).get("id");
                        if (id instanceof Number) {
                            logIds.add(((Number) id).intValue());
                        }
                    } else if (item instanceof Number) {
                        logIds.add(((Number) item).intValue());
                    }
                }
            }
        }
        return logIds;
    }
}
