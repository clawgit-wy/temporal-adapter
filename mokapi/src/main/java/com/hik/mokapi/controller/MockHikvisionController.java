package com.hik.mokapi.controller;

import com.hik.mokapi.config.MokApiProperties;
import com.hik.mokapi.model.PersonInfo;
import com.hik.mokapi.model.VerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模拟海康威视开放平台接口
 *
 * 响应规则（通过 personId 中的关键字控制）：
 * 1. personId 包含 "FAIL"   -> 返回校验失败（HTTP 200, passed=false）
 * 2. personId 包含 "ERROR"  -> 返回 HTTP 500 异常（模拟接口报错，触发 Temporal 重试）
 * 3. personId 包含 "SLOW"   -> 延迟 3 秒返回（模拟慢响应）
 * 4. personId 包含 "TIMEOUT" -> 延迟 30 秒返回（模拟超时）
 * 5. 其他                    -> 随机 80% 通过 / 20% 失败
 *
 * 额外配置（通过 application.yml）：
 * - mokapi.default-delay-ms: 全局默认延迟
 * - mokapi.error-rate: 随机错误率
 * - mokapi.slow-delay-ms: 慢响应延迟时间
 */
@Slf4j
@RestController
@RequestMapping("/api/hikvision")
@RequiredArgsConstructor
public class MockHikvisionController {

    private final MokApiProperties properties;

    /**
     * 模拟海康单人校验接口
     * POST /api/hikvision/verify
     */
    @PostMapping("/verify")
    public VerifyResponse verify(@RequestBody PersonInfo person) throws InterruptedException {
        log.info("[Mock海康] 收到校验请求, personId: {}, name: {}", person.getPersonId(), person.getName());

        String personId = person.getPersonId() != null ? person.getPersonId() : "";

        // 全局默认延迟
        if (properties.getDefaultDelayMs() > 0) {
            Thread.sleep(properties.getDefaultDelayMs());
        }

        // 规则1: personId 包含 FAIL -> 校验失败
        if (personId.contains("FAIL")) {
            log.info("[Mock海康] 人员 {} 校验失败(FAIL标记)", personId);
            return VerifyResponse.builder()
                    .personId(personId)
                    .passed(false)
                    .errorMsg("特征值不匹配")
                    .similarity(45.3)
                    .build();
        }

        // 规则2: personId 包含 ERROR -> 模拟接口报错（触发 Temporal 重试）
        if (personId.contains("ERROR")) {
            log.warn("[Mock海康] 人员 {} 接口报错(ERROR标记)", personId);
            throw new RuntimeException("模拟海康接口内部错误 500");
        }

        // 规则3: personId 包含 SLOW -> 慢响应
        if (personId.contains("SLOW")) {
            log.info("[Mock海康] 人员 {} 模拟慢响应, 延迟 {}ms", personId, properties.getSlowDelayMs());
            Thread.sleep(properties.getSlowDelayMs());
        }

        // 规则4: personId 包含 TIMEOUT -> 超长延迟（模拟超时）
        if (personId.contains("TIMEOUT")) {
            log.warn("[Mock海康] 人员 {} 模拟超时, 延迟30秒", personId);
            Thread.sleep(30000);
        }

        // 规则5: 全局随机错误率
        if (properties.getErrorRate() > 0 && ThreadLocalRandom.current().nextDouble() < properties.getErrorRate()) {
            log.warn("[Mock海康] 人员 {} 触发随机错误率", personId);
            throw new RuntimeException("模拟随机接口错误");
        }

        // 规则6: 正常情况 -> 随机 80% 通过
        boolean passed = ThreadLocalRandom.current().nextDouble() < 0.8;
        double similarity = 50 + ThreadLocalRandom.current().nextDouble() * 50; // 50~100

        if (passed) {
            log.info("[Mock海康] 人员 {} 校验通过, 相似度: {:.1f}", personId, similarity);
            return VerifyResponse.builder()
                    .personId(personId)
                    .passed(true)
                    .similarity(similarity)
                    .build();
        } else {
            log.info("[Mock海康] 人员 {} 校验未通过(随机), 相似度: {:.1f}", personId, similarity);
            return VerifyResponse.builder()
                    .personId(personId)
                    .passed(false)
                    .errorMsg("人脸特征值相似度不足")
                    .similarity(similarity)
                    .build();
        }
    }

    /**
     * 模拟海康批量校验接口
     * POST /api/hikvision/batch-verify
     */
    @PostMapping("/batch-verify")
    public Map<String, Object> batchVerify(@RequestBody java.util.List<PersonInfo> persons) throws InterruptedException {
        log.info("[Mock海康] 收到批量校验请求, 人数: {}", persons.size());

        java.util.List<VerifyResponse> results = new java.util.ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (PersonInfo person : persons) {
            try {
                VerifyResponse result = verify(person);
                results.add(result);
                if (result.isPassed()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                results.add(VerifyResponse.builder()
                        .personId(person.getPersonId())
                        .passed(false)
                        .errorMsg("接口异常: " + e.getMessage())
                        .build());
                failCount++;
            }
        }

        return Map.of(
                "total", persons.size(),
                "successCount", successCount,
                "failCount", failCount,
                "results", results
        );
    }

    /**
     * 健康检查接口
     * GET /api/hikvision/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "mokapi-hikvision",
                "timestamp", System.currentTimeMillis()
        );
    }
}
