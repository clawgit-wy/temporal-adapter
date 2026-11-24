package com.hik.worker.activity.impl;

import com.hik.worker.activity.PermissionActivity;
import com.hik.worker.model.PermissionIssueRequest;
import com.hik.worker.model.StaffInfo;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员权限下发 Activity 实现
 * 使用 RestTemplate 调用 mokapi 模拟的海康 FaceHandSet 接口
 */
@Slf4j
@Component
public class PermissionActivityImpl implements PermissionActivity {

    @Value("${mokapi.url:http://localhost:19090}")
    private String mokapiUrl;

    private final RestTemplate restTemplate;

    public PermissionActivityImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String checkTokenState(PermissionIssueRequest request) {
        log.info("发行校验开始, token: {}, tokenType: {}", request.getToken(), request.getTokenType());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("发行校验开始");

        String url = mokapiUrl + "/Api/FaceHandSet/CheckTokenState";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = new HashMap<>();
            body.put("TokenType", request.getTokenType());
            body.put("Token", request.getToken());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("发行校验返回: {}", response);
            ctx.heartbeat("发行校验完成");
            return response;
        } catch (RuntimeException e) {
            log.error("发行校验失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public String faceIssue(PermissionIssueRequest request) {
        log.info("人脸发行开始, staffNo: {}", request.getStaff().getStaffNo());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("人脸发行开始");

        String url = mokapiUrl + "/Api/FaceHandSet/FaceIssue";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = buildFaceRequestBody(request, 0);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("人脸发行返回: {}", response);
            ctx.heartbeat("人脸发行完成");
            return response;
        } catch (RuntimeException e) {
            log.error("人脸发行失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public String faceModify(PermissionIssueRequest request) {
        log.info("人脸改写开始, staffNo: {}", request.getStaff().getStaffNo());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("人脸改写开始");

        String url = mokapiUrl + "/Api/FaceHandSet/FaceModify";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = buildFaceRequestBody(request, 1);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("人脸改写返回: {}", response);
            ctx.heartbeat("人脸改写完成");
            return response;
        } catch (RuntimeException e) {
            log.error("人脸改写失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public String doorIssue(PermissionIssueRequest request) {
        log.info("卡发行开始, staffNo: {}, token: {}", request.getStaff().getStaffNo(), request.getToken());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("卡发行开始");

        String url = mokapiUrl + "/Api/FaceHandSet/DoorIssue";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = buildDoorRequestBody(request);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("卡发行返回: {}", response);
            ctx.heartbeat("卡发行完成");
            return response;
        } catch (RuntimeException e) {
            log.error("卡发行失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public String faceModifyBypowerType(PermissionIssueRequest request, int powerType) {
        log.info("增量权限操作开始, staffNo: {}, powerType: {}", request.getStaff().getStaffNo(), powerType);
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("增量权限操作开始, powerType=" + powerType);

        String url = mokapiUrl + "/Api/FaceHandSet/FaceModifyBypowerType";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = buildFaceRequestBody(request, 0);
            body.put("powerType", powerType);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("增量权限操作返回: {}", response);
            ctx.heartbeat("增量权限操作完成");
            return response;
        } catch (RuntimeException e) {
            log.error("增量权限操作失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    // ========== 私有方法 ==========

    /**
     * 构造人脸发行/改写请求体
     *
     * @param operType 0=发行, 1=改写
     */
    private Map<String, Object> buildFaceRequestBody(PermissionIssueRequest request, int operType) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("OperType", operType);

        StaffInfo staff = request.getStaff();
        if (staff != null) {
            Map<String, Object> staffMap = new LinkedHashMap<>();
            staffMap.put("StaffNo", staff.getStaffNo());
            staffMap.put("StaffName", staff.getStaffName());
            staffMap.put("Sex", staff.getSex());
            staffMap.put("OrganizationId", staff.getOrganizationId());
            staffMap.put("Rid", staff.getRid());
            staffMap.put("IDNo", staff.getIdNo());
            staffMap.put("MobileNo", staff.getMobileNo());
            staffMap.put("Email", staff.getEmail());
            body.put("Staff", staffMap);
        }

        body.put("BeginTime", request.getBeginTime());
        body.put("EndTime", request.getEndTime());

        List<Integer> deviceNos = request.getAuthorizeDeviceNo();
        if (deviceNos != null) {
            body.put("AuthorizeDeviceNo", deviceNos);
        }

        List<String> gids = request.getProjectGids();
        if (gids != null) {
            body.put("ProjectGids", gids);
        }

        body.put("StaffState", request.getStaffState());
        if (request.getFacePicture() != null) {
            body.put("FacePicture", request.getFacePicture());
        }

        return body;
    }

    /**
     * 构造卡发行请求体
     */
    private Map<String, Object> buildDoorRequestBody(PermissionIssueRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("useModel", request.getUseModel());

        StaffInfo staff = request.getStaff();
        if (staff != null) {
            body.put("orginazitionId", staff.getOrganizationId());
            body.put("staffNo", staff.getStaffNo());
            body.put("staffName", staff.getStaffName());
        }

        body.put("tokenId", request.getToken());
        body.put("endDate", request.getEndTime());
        body.put("tokenOper", 0);
        body.put("stcm", request.getStcm() != null ? request.getStcm() : "");
        body.put("tcm", request.getTcm() != null ? request.getTcm() : "");
        body.put("serialNo", request.getToken());
        body.put("token", request.getToken());
        body.put("tokenType", request.getTokenType());
        body.put("beginDate", request.getBeginTime());

        if (request.getCarPass() != null) {
            body.put("CarPass", request.getCarPass());
        }

        List<Integer> deviceNos = request.getAuthorizeDeviceNo();
        if (deviceNos != null) {
            body.put("AuthDevice", deviceNos);
        }

        return body;
    }
}
