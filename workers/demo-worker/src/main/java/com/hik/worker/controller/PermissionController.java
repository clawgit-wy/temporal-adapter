package com.hik.worker.controller;

import com.hik.worker.config.TemporalWorkerConfig;
import com.hik.worker.model.PermissionIssueRequest;
import com.hik.worker.model.PermissionIssueResult;
import com.hik.worker.model.StaffInfo;
import com.hik.worker.workflow.PermissionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

/**
 * 人员权限下发控制器
 *
 * 提供以下 API 端点:
 * 1. POST /api/permission/face-issue    - 人脸发行
 * 2. POST /api/permission/face-modify   - 人脸改写
 * 3. POST /api/permission/door-issue    - 卡发行
 * 4. POST /api/permission/power-add     - 增量增加权限
 * 5. POST /api/permission/power-delete   - 增量删除权限
 * 6. GET  /api/permission/status/{id}   - 查询工作流状态
 * 7. GET  /api/permission/health       - 健康检查
 */
@Slf4j
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final WorkflowClient workflowClient;

    /**
     * 1. 人脸发行
     */
    @PostMapping("/face-issue")
    public Map<String, Object> faceIssue(@RequestBody Map<String, Object> body) {
        String workflowId = "perm-face-issue-" + System.currentTimeMillis();
        log.info("===== 人脸发行工作流启动, workflowId: {} =====", workflowId);

        PermissionIssueRequest request = buildRequest(body, "FACE_ISSUE", workflowId);
        PermissionWorkflow workflow = createWorkflow(workflowId);
        PermissionIssueResult result = workflow.run(request);

        log.info("===== 人脸发行工作流完成, success: {} =====", result.isSuccess());
        return buildResponse(workflowId, result);
    }

    /**
     * 2. 人脸改写
     */
    @PostMapping("/face-modify")
    public Map<String, Object> faceModify(@RequestBody Map<String, Object> body) {
        String workflowId = "perm-face-modify-" + System.currentTimeMillis();
        log.info("===== 人脸改写工作流启动, workflowId: {} =====", workflowId);

        PermissionIssueRequest request = buildRequest(body, "FACE_MODIFY", workflowId);
        PermissionWorkflow workflow = createWorkflow(workflowId);
        PermissionIssueResult result = workflow.run(request);

        log.info("===== 人脸改写工作流完成, success: {} =====", result.isSuccess());
        return buildResponse(workflowId, result);
    }

    /**
     * 3. 卡发行
     */
    @PostMapping("/door-issue")
    public Map<String, Object> doorIssue(@RequestBody Map<String, Object> body) {
        String workflowId = "perm-door-issue-" + System.currentTimeMillis();
        log.info("===== 卡发行工作流启动, workflowId: {} =====", workflowId);

        PermissionIssueRequest request = buildRequest(body, "DOOR_ISSUE", workflowId);
        PermissionWorkflow workflow = createWorkflow(workflowId);
        PermissionIssueResult result = workflow.run(request);

        log.info("===== 卡发行工作流完成, success: {} =====", result.isSuccess());
        return buildResponse(workflowId, result);
    }

    /**
     * 4. 增量增加权限
     */
    @PostMapping("/power-add")
    public Map<String, Object> powerAdd(@RequestBody Map<String, Object> body) {
        String workflowId = "perm-power-add-" + System.currentTimeMillis();
        log.info("===== 增量增加权限工作流启动, workflowId: {} =====", workflowId);

        PermissionIssueRequest request = buildRequest(body, "POWER_ADD", workflowId);
        PermissionWorkflow workflow = createWorkflow(workflowId);
        PermissionIssueResult result = workflow.run(request);

        log.info("===== 增量增加权限工作流完成, success: {} =====", result.isSuccess());
        return buildResponse(workflowId, result);
    }

    /**
     * 5. 增量删除权限
     */
    @PostMapping("/power-delete")
    public Map<String, Object> powerDelete(@RequestBody Map<String, Object> body) {
        String workflowId = "perm-power-delete-" + System.currentTimeMillis();
        log.info("===== 增量删除权限工作流启动, workflowId: {} =====", workflowId);

        PermissionIssueRequest request = buildRequest(body, "POWER_DELETE", workflowId);
        PermissionWorkflow workflow = createWorkflow(workflowId);
        PermissionIssueResult result = workflow.run(request);

        log.info("===== 增量删除权限工作流完成, success: {} =====", result.isSuccess());
        return buildResponse(workflowId, result);
    }

    /**
     * 查询工作流状态
     */
    @GetMapping("/status/{workflowId}")
    public Map<String, Object> getStatus(@PathVariable String workflowId) {
        try {
            PermissionWorkflow workflow = workflowClient.newWorkflowStub(PermissionWorkflow.class, workflowId);
            String status = workflow.getStatus();
            return Map.of(
                    "workflowId", workflowId,
                    "status", status
            );
        } catch (Exception e) {
            return Map.of(
                    "workflowId", workflowId,
                    "error", e.getMessage()
            );
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "permission-worker",
                "timestamp", System.currentTimeMillis()
        );
    }

    // ========== 私有方法 ==========

    private PermissionWorkflow createWorkflow(String workflowId) {
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(TemporalWorkerConfig.TASK_QUEUE_PERMISSION)
                .setWorkflowExecutionTimeout(Duration.ofMinutes(5))
                .build();
        return workflowClient.newWorkflowStub(PermissionWorkflow.class, options);
    }

    /**
     * 从请求体构建 PermissionIssueRequest
     */
    @SuppressWarnings("unchecked")
    private PermissionIssueRequest buildRequest(Map<String, Object> body, String operationType, String workflowId) {
        StaffInfo staff = StaffInfo.builder().build();
        if (body.containsKey("staff")) {
            Map<String, Object> s = (Map<String, Object>) body.get("staff");
            staff.setStaffNo(getString(s, "staffNo", "S" + System.currentTimeMillis()));
            staff.setStaffName(getString(s, "staffName", "测试用户"));
            staff.setSex(getInt(s, "sex", 0));
            staff.setOrganizationId(getString(s, "organizationId", "ORG-001"));
            staff.setRid(getString(s, "rid", ""));
            staff.setIdNo(getString(s, "idNo", ""));
            staff.setMobileNo(getString(s, "mobileNo", ""));
            staff.setEmail(getString(s, "email", ""));
        }

        List<Integer> deviceNos = (List<Integer>) body.get("authorizeDeviceNo");
        if (deviceNos == null) {
            deviceNos = Arrays.asList(1, 2, 3);
        }

        List<String> projectGids = (List<String>) body.get("projectGids");
        if (projectGids == null) {
            projectGids = Arrays.asList("proj-001");
        }

        return PermissionIssueRequest.builder()
                .workflowId(workflowId)
                .operationType(operationType)
                .staff(staff)
                .token(getString(body, "token", "TEST-TOKEN-" + System.currentTimeMillis()))
                .tokenType(getInt(body, "tokenType", 0))
                .authorizeDeviceNo(deviceNos)
                .projectGids(projectGids)
                .beginTime(getString(body, "beginTime", "2025-01-01"))
                .endTime(getString(body, "endTime", "2099-12-31"))
                .staffState(getInt(body, "staffState", 1))
                .facePicture(getString(body, "facePicture", ""))
                .useModel(getInt(body, "useModel", 0))
                .tcm(getString(body, "tcm", "TCM-001"))
                .stcm(getString(body, "stcm", "STCM-001"))
                .carPass(getString(body, "carPass", ""))
                .build();
    }

    private Map<String, Object> buildResponse(String workflowId, PermissionIssueResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("workflowId", workflowId);
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("operationType", result.getOperationType());
        response.put("tokenExists", result.isTokenExists());
        response.put("checkResult", result.getCheckResult());
        response.put("issueResult", result.getIssueResult());
        response.put("steps", result.getSteps());
        response.put("duration", result.getDuration() + "ms");
        return response;
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : defaultValue;
    }

    private int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (val == null) return defaultValue;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
