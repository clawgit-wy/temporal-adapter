package com.hik.worker.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.worker.client.XinkaipuApiClient;
import com.hik.worker.model.DeviceSyncActivityRequest;
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
 * 设备数据同步 Activity 实现
 * 使用 XinkaipuApiClient 拉取设备数据，通过 webhook 回调业务系统进行保存与通知
 */
@Slf4j
@Component
public class DeviceSyncActivityImpl implements DeviceSyncActivity {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final XinkaipuApiClient xinkaipuApiClient;

    @Value("${webhook.callback.url}")
    private String webhookCallbackUrl;

    @Value("${device.sync.save-path:/api/device/save}")
    private String savePath;

    @Value("${device.sync.mark-removed-path:/api/device/markRemoved}")
    private String markRemovedPath;

    @Value("${device.sync.notify-new-path:/api/device/notifyNew}")
    private String notifyNewPath;

    public DeviceSyncActivityImpl(XinkaipuApiClient xinkaipuApiClient) {
        this.xinkaipuApiClient = xinkaipuApiClient;
    }

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public List<Map<String, Object>> fetchDevicePage(DeviceSyncActivityRequest request) {
        log.info("拉取设备数据, deviceType: {}", request.getDeviceType());
        try {
            Object result = xinkaipuApiClient.getAccessTermAreaInfo();
            List<Map<String, Object>> devices = new ArrayList<>();
            if (result instanceof List) {
                for (Object item : (List<?>) result) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> device = (Map<String, Object>) item;
                        devices.add(device);
                    }
                }
            } else if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;
                Object data = map.get("data");
                if (data instanceof List) {
                    for (Object item : (List<?>) data) {
                        if (item instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> device = (Map<String, Object>) item;
                            devices.add(device);
                        }
                    }
                }
            }
            log.info("拉取设备数据完成, 数量: {}", devices.size());
            return devices;
        } catch (RuntimeException e) {
            log.warn("拉取设备数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean saveDevices(List<Map<String, Object>> devices, String deviceType) {
        log.info("保存设备数据到业务系统, deviceType: {}, 数量: {}", deviceType, devices.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceType", deviceType);
            payload.put("devices", devices);
            return doPost(webhookCallbackUrl + savePath, payload);
        } catch (RuntimeException e) {
            log.warn("保存设备数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean markRemovedDevices(List<String> deviceCodes) {
        log.info("标记已移除设备, 数量: {}", deviceCodes.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceCodes", deviceCodes);
            return doPost(webhookCallbackUrl + markRemovedPath, payload);
        } catch (RuntimeException e) {
            log.warn("标记已移除设备失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public boolean notifyNewDevices(List<String> deviceCodes) {
        log.info("发送新设备通知, 数量: {}", deviceCodes.size());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceCodes", deviceCodes);
            return doPost(webhookCallbackUrl + notifyNewPath, payload);
        } catch (RuntimeException e) {
            log.warn("发送新设备通知失败: {}", e.getMessage());
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
                    log.info("业务系统回调成功, url: {}, 响应: {}", url, responseBody);
                    return true;
                } else {
                    log.warn("业务系统回调失败, url: {}, HTTP状态码: {}", url, response.code());
                    throw new RuntimeException("业务系统回调失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("业务系统回调异常, url: {}, 错误: {}", url, e.getMessage());
            throw new RuntimeException("业务系统回调异常: " + e.getMessage(), e);
        }
    }
}
