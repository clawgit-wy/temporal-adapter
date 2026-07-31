package com.hik.mokapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Webhook回调接收Controller
 * 模拟业务系统接收Temporal Worker的执行结果通知
 */
@Slf4j
@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    /** 存储接收到的Webhook回调记录（内存存储，用于验证） */
    private final List<Map<String, Object>> receivedCallbacks = new ArrayList<>();

    /** 控制开关：设为true时模拟业务系统宕机（返回500），用于验证Webhook重试 */
    private volatile boolean simulateDown = false;

    /**
     * 接收Webhook回调
     * POST /api/webhook/callback
     */
    @PostMapping("/callback")
    public Map<String, Object> receiveCallback(@RequestBody String payload) {
        log.info("[Webhook接收] 收到回调: {}", payload);

        if (simulateDown) {
            log.warn("[Webhook接收] 模拟业务系统宕机，返回500");
            throw new RuntimeException("模拟业务系统宕机");
        }

        Map<String, Object> record = Map.of(
                "payload", payload,
                "receivedAt", System.currentTimeMillis(),
                "status", "RECEIVED"
        );
        receivedCallbacks.add(record);

        log.info("[Webhook接收] 回调处理成功，累计接收: {} 条", receivedCallbacks.size());
        return Map.of("status", "OK", "message", "Webhook接收成功");
    }

    /**
     * 查询已接收的Webhook回调列表
     * GET /api/webhook/list
     */
    @GetMapping("/list")
    public Map<String, Object> listCallbacks() {
        return Map.of(
                "total", receivedCallbacks.size(),
                "callbacks", receivedCallbacks
        );
    }

    /**
     * 控制模拟宕机开关
     * POST /api/webhook/simulate-down?enabled=true
     * 设为true后，回调接口返回500，用于验证Temporal自动重试Webhook
     */
    @PostMapping("/simulate-down")
    public Map<String, Object> simulateDown(@RequestParam boolean enabled) {
        this.simulateDown = enabled;
        log.info("[Webhook] 模拟宕机开关已设为: {}", enabled);
        return Map.of("simulateDown", enabled);
    }

    /**
     * 清空回调记录
     * POST /api/webhook/clear
     */
    @PostMapping("/clear")
    public Map<String, Object> clear() {
        int count = receivedCallbacks.size();
        receivedCallbacks.clear();
        log.info("[Webhook] 已清空 {} 条回调记录", count);
        return Map.of("cleared", count);
    }
}
