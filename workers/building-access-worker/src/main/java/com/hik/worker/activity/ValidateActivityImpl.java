package com.hik.worker.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.worker.model.StaffInfo;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 校验 Activity 实现
 * 通过 webhook 调用业务系统校验 API 进行白名单与设备权限校验
 */
@Slf4j
@Component
public class ValidateActivityImpl implements ValidateActivity {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${webhook.callback.url}")
    private String webhookCallbackUrl;

    @Value("${validate.api.whitelist-path:/api/validate/whitelist}")
    private String whitelistPath;

    @Value("${validate.api.device-permission-path:/api/validate/devicePermission}")
    private String devicePermissionPath;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public boolean validateWhitelist(String staffNo) {
        log.info("校验白名单, staffNo: {}", staffNo);
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("staffNo", staffNo);
            Object result = doPost(webhookCallbackUrl + whitelistPath, payload);
            return parseBoolean(result, "allowed");
        } catch (RuntimeException e) {
            log.warn("校验白名单失败, staffNo: {}", staffNo);
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean validateDevicePermission(String staffNo, List<Integer> deviceNos) {
        log.info("校验设备权限, staffNo: {}, 设备数量: {}", staffNo, deviceNos.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("staffNo", staffNo);
            payload.put("deviceNos", deviceNos);
            Object result = doPost(webhookCallbackUrl + devicePermissionPath, payload);
            return parseBoolean(result, "allowed");
        } catch (RuntimeException e) {
            log.warn("校验设备权限失败, staffNo: {}", staffNo);
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<StaffInfo> filterWhitelistStaff(List<StaffInfo> staffList) {
        log.info("过滤白名单人员, 总数: {}", staffList.size());
        List<StaffInfo> whitelisted = new ArrayList<>();
        for (StaffInfo staff : staffList) {
            try {
                if (validateWhitelist(staff.getStaffNo())) {
                    whitelisted.add(staff);
                }
            } catch (Exception e) {
                log.warn("过滤白名单人员异常, staffNo: {}, 错误: {}", staff.getStaffNo(), e.getMessage());
            }
        }
        log.info("过滤白名单人员完成, 通过数量: {}", whitelisted.size());
        return whitelisted;
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
                    log.info("校验接口调用成功, url: {}, 响应: {}", url, responseBody);
                    return objectMapper.readValue(responseBody, Object.class);
                } else {
                    log.warn("校验接口调用失败, url: {}, HTTP状态码: {}", url, response.code());
                    throw new RuntimeException("校验接口调用失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("校验接口调用异常, url: {}, 错误: {}", url, e.getMessage());
            throw new RuntimeException("校验接口调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从响应中解析布尔结果
     */
    @SuppressWarnings("unchecked")
    private boolean parseBoolean(Object result, String key) {
        if (result instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) result;
            Object data = map.get("data");
            if (data instanceof Boolean) {
                return (Boolean) data;
            }
            Object allowed = map.get(key);
            if (allowed instanceof Boolean) {
                return (Boolean) allowed;
            }
        }
        return false;
    }
}
