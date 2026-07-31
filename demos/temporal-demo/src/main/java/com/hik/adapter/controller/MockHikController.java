package com.hik.adapter.controller;

import com.hik.adapter.model.PersonInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock 海康威视单人校验接口
 * 模拟海康开放平台的 API 行为，用于本地验证 Temporal 工作流
 *
 * 模拟规则：
 * 1. personId 包含 "FAIL" → 返回校验失败
 * 2. personId 包含 "ERROR" → 抛出异常（模拟接口报错，触发 Temporal 重试）
 * 3. personId 包含 "SLOW" → 延迟 3 秒返回（模拟慢响应）
 * 4. 其他 → 随机 80% 通过 / 20% 失败
 */
@Slf4j
@RestController
@RequestMapping("/mock/hikvision")
public class MockHikController {

    /**
     * 模拟海康单人校验接口
     */
    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody PersonInfo person) throws InterruptedException {
        log.info("[Mock海康] 收到校验请求, personId: {}, name: {}", person.getPersonId(), person.getName());

        String personId = person.getPersonId() != null ? person.getPersonId() : "";

        // 规则1: personId 包含 FAIL → 校验失败
        if (personId.contains("FAIL")) {
            log.info("[Mock海康] 人员 {} 校验失败(FAIL标记)", personId);
            return Map.of("passed", false, "errorMsg", "特征值不匹配");
        }

        // 规则2: personId 包含 ERROR → 模拟接口报错（触发Temporal重试）
        if (personId.contains("ERROR")) {
            log.warn("[Mock海康] 人员 {} 接口报错(ERROR标记)", personId);
            throw new RuntimeException("模拟海康接口内部错误 500");
        }

        // 规则3: personId 包含 SLOW → 延迟3秒
        if (personId.contains("SLOW")) {
            log.info("[Mock海康] 人员 {} 模拟慢响应, 延迟3秒", personId);
            Thread.sleep(3000);
        }

        // 规则4: 正常情况 → 随机 80% 通过
        boolean passed = ThreadLocalRandom.current().nextDouble() < 0.8;
        if (passed) {
            log.info("[Mock海康] 人员 {} 校验通过", personId);
            return Map.of("passed", true, "errorMsg", "");
        } else {
            log.info("[Mock海康] 人员 {} 校验未通过(随机)", personId);
            return Map.of("passed", false, "errorMsg", "人脸特征值相似度不足");
        }
    }
}
