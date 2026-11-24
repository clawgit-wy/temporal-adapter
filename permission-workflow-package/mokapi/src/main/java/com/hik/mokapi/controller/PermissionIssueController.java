package com.hik.mokapi.controller;

import com.hik.mokapi.config.MokApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模拟海康威视 FaceHandSet 人员权限下发接口
 *
 * 支持的接口:
 * 1. POST /Api/FaceHandSet/CheckTokenState  - 发行校验（检查卡号/人脸是否存在）
 * 2. POST /Api/FaceHandSet/FaceIssue        - 人脸发行
 * 3. POST /Api/FaceHandSet/FaceModify        - 人脸改写
 * 4. POST /Api/FaceHandSet/DoorIssue         - 卡发行
 * 5. POST /Api/FaceHandSet/FaceModifyBypowerType - 人脸增量权限/删除
 *
 * 响应规则（通过 StaffNo 或 Token 中的关键字控制）：
 * - 包含 "FAIL"   -> 返回业务失败（code=500, msg=操作失败）
 * - 包含 "ERROR"  -> 返回 HTTP 500 异常（模拟接口报错，触发 Temporal 重试）
 * - 包含 "SLOW"   -> 延迟 3 秒返回
 * - 包含 "TIMEOUT" -> 延迟 30 秒返回
 * - 包含 "NOTFOUND" -> CheckTokenState 返回不存在
 * - 其他           -> 正常成功响应
 */
@Slf4j
@RestController
@RequestMapping("/Api/FaceHandSet")
@RequiredArgsConstructor
public class PermissionIssueController {

    private final MokApiProperties properties;

    /** 模拟数据存储: token -> 发行信息 */
    private final Map<String, Map<String, Object>> tokenStore = new ConcurrentHashMap<>();

    /** 模拟数据存储: staffNo -> 人员信息 */
    private final Map<String, Map<String, Object>> staffStore = new ConcurrentHashMap<>();

    /**
     * 1. 发行校验
     * POST /Api/FaceHandSet/CheckTokenState
     */
    @PostMapping("/CheckTokenState")
    public ResponseEntity<Map<String, Object>> checkTokenState(@RequestBody Map<String, Object> request) throws InterruptedException {
        String tokenType = String.valueOf(request.getOrDefault("TokenType", 0));
        String token = String.valueOf(request.getOrDefault("Token", ""));
        log.info("[Mock权限] CheckTokenState 请求: TokenType={}, Token={}", tokenType, token);

        applyGlobalDelay();
        checkErrorKeywords(token, "CheckTokenState");

        // NOTFOUND 关键字 -> 返回不存在
        if (token.contains("NOTFOUND")) {
            log.info("[Mock权限] Token {} 不存在发行信息", token);
            return ResponseEntity.ok(buildResponse(500, "操作失败", null));
        }

        // 模拟已存在的 token
        Map<String, Object> result = tokenStore.getOrDefault(token, buildMockStaffInfo(token));
        if (!tokenStore.containsKey(token)) {
            tokenStore.put(token, result);
        }

        String msg = String.format("卡号【%s】存在发行信息,项目编号：%s", token, result.get("Gid"));
        log.info("[Mock权限] Token {} 存在发行信息", token);
        return ResponseEntity.ok(buildResponse(200, msg, result));
    }

    /**
     * 2. 人脸发行
     * POST /Api/FaceHandSet/FaceIssue
     */
    @PostMapping("/FaceIssue")
    public ResponseEntity<Map<String, Object>> faceIssue(@RequestBody Map<String, Object> request) throws InterruptedException {
        log.info("[Mock权限] FaceIssue 请求: OperType={}, StaffState={}",
                request.get("OperType"), request.get("StaffState"));

        @SuppressWarnings("unchecked")
        Map<String, Object> staff = (Map<String, Object>) request.get("Staff");
        String staffNo = staff != null ? String.valueOf(staff.get("StaffNo")) : "";
        applyGlobalDelay();
        checkErrorKeywords(staffNo, "FaceIssue");

        staffStore.put(staffNo, staff);
        log.info("[Mock权限] 人脸发行成功, StaffNo={}", staffNo);
        return ResponseEntity.ok(buildResponse(200, "人脸发行成功", Map.of("StaffNo", staffNo)));
    }

    /**
     * 3. 人脸改写
     * POST /Api/FaceHandSet/FaceModify
     */
    @PostMapping("/FaceModify")
    public ResponseEntity<Map<String, Object>> faceModify(@RequestBody Map<String, Object> request) throws InterruptedException {
        log.info("[Mock权限] FaceModify 请求: OperType={}", request.get("OperType"));

        @SuppressWarnings("unchecked")
        Map<String, Object> staff = (Map<String, Object>) request.get("Staff");
        String staffNo = staff != null ? String.valueOf(staff.get("StaffNo")) : "";
        applyGlobalDelay();
        checkErrorKeywords(staffNo, "FaceModify");

        staffStore.put(staffNo, staff);
        log.info("[Mock权限] 人脸改写成功, StaffNo={}", staffNo);
        return ResponseEntity.ok(buildResponse(200, "人脸改写成功", Map.of("StaffNo", staffNo)));
    }

    /**
     * 4. 卡发行
     * POST /Api/FaceHandSet/DoorIssue
     */
    @PostMapping("/DoorIssue")
    public ResponseEntity<Map<String, Object>> doorIssue(@RequestBody Map<String, Object> request) throws InterruptedException {
        String staffNo = String.valueOf(request.getOrDefault("staffNo", ""));
        String tokenId = String.valueOf(request.getOrDefault("tokenId", ""));
        log.info("[Mock权限] DoorIssue 请求: staffNo={}, tokenId={}", staffNo, tokenId);

        applyGlobalDelay();
        checkErrorKeywords(staffNo + tokenId, "DoorIssue");

        Map<String, Object> issueResult = new HashMap<>();
        issueResult.put("tokenId", tokenId);
        issueResult.put("staffNo", staffNo);
        tokenStore.put(tokenId, issueResult);

        log.info("[Mock权限] 卡发行成功, staffNo={}, tokenId={}", staffNo, tokenId);
        return ResponseEntity.ok(buildResponse(200, "卡发行成功", issueResult));
    }

    /**
     * 5. 人脸增量权限及删除
     * POST /Api/FaceHandSet/FaceModifyBypowerType
     */
    @PostMapping("/FaceModifyBypowerType")
    public ResponseEntity<Map<String, Object>> faceModifyBypowerType(@RequestBody Map<String, Object> request) throws InterruptedException {
        int powerType = request.get("powerType") != null ? (int) request.get("powerType") : 0;
        @SuppressWarnings("unchecked")
        Map<String, Object> staff = (Map<String, Object>) request.get("Staff");
        String staffNo = staff != null ? String.valueOf(staff.get("StaffNo")) : "";
        log.info("[Mock权限] FaceModifyBypowerType 请求: powerType={}, StaffNo={}", powerType, staffNo);

        applyGlobalDelay();
        checkErrorKeywords(staffNo, "FaceModifyBypowerType");

        String action = powerType == 1 ? "删除" : "增加";
        log.info("[Mock权限] {}设备权限成功, StaffNo={}", action, staffNo);
        return ResponseEntity.ok(buildResponse(200, action + "设备权限成功", Map.of("StaffNo", staffNo, "powerType", powerType)));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "mokapi-permission-issue",
                "timestamp", System.currentTimeMillis()
        );
    }

    // ========== 私有方法 ==========

    private void applyGlobalDelay() throws InterruptedException {
        if (properties.getDefaultDelayMs() > 0) {
            Thread.sleep(properties.getDefaultDelayMs());
        }
    }

    /**
     * 检查关键字并触发对应行为
     */
    private void checkErrorKeywords(String key, String apiName) throws InterruptedException {
        if (key == null || key.isEmpty()) return;

        if (key.contains("FAIL")) {
            log.warn("[Mock权限] {} 触发 FAIL (业务失败)", apiName);
            throw new RuntimeException("模拟业务失败: " + apiName);
        }
        if (key.contains("ERROR")) {
            log.warn("[Mock权限] {} 触发 ERROR (接口报错)", apiName);
            throw new RuntimeException("模拟接口内部错误 500: " + apiName);
        }
        if (key.contains("SLOW")) {
            log.info("[Mock权限] {} 触发 SLOW, 延迟 {}ms", apiName, properties.getSlowDelayMs());
            Thread.sleep(properties.getSlowDelayMs());
        }
        if (key.contains("TIMEOUT")) {
            log.warn("[Mock权限] {} 触发 TIMEOUT, 延迟30秒", apiName);
            Thread.sleep(30000);
        }
    }

    /**
     * 构造标准响应
     */
    private Map<String, Object> buildResponse(int code, String msg, Object result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", code);
        response.put("msg", msg);
        response.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("result", result);
        return response;
    }

    /**
     * 构造模拟的发行信息
     */
    private Map<String, Object> buildMockStaffInfo(String token) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("ID", ThreadLocalRandom.current().nextInt(1, 1000));
        info.put("TokenId", token);
        info.put("TokenType", 1);
        info.put("SerialNo", token);
        info.put("StaffId", UUID.randomUUID().toString());
        info.put("StaffNo", "MOCK-" + token);
        info.put("StaffName", "模拟用户");
        info.put("StaffStatus", 0);
        info.put("OrganizationId", UUID.randomUUID().toString());
        info.put("OrganizationName", "模拟组织");
        info.put("State", 1);
        info.put("BeginDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        info.put("EndDate", "2099-12-31T23:59:59");
        info.put("Gid", UUID.randomUUID().toString());
        info.put("Rid", UUID.randomUUID().toString());
        return info;
    }
}
