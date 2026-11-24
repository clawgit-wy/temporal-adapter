package com.hik.worker.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.worker.model.*;
import com.hik.worker.model.UnregisterRequest;
import com.hik.worker.model.UnregisterResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 富士设备 API 客户端
 * 封装5个富士设备权限管理接口：
 * 1. 发行校验 (CheckTokenState)
 * 2. 人脸发行 (FaceIssue)
 * 3. 人脸发行改写 (FaceModify)
 * 4. 卡发行 (DoorIssue)
 * 5. 人脸增量权限及删除 (FaceModifyBypowerType)
 */
@Slf4j
@Component
public class FujiApiClient {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${fuji.api.base-url}")
    private String baseUrl;

    @Value("${fuji.api.check-token-path}")
    private String checkTokenPath;

    @Value("${fuji.api.face-issue-path}")
    private String faceIssuePath;

    @Value("${fuji.api.face-modify-path}")
    private String faceModifyPath;

    @Value("${fuji.api.card-issue-path}")
    private String cardIssuePath;

    @Value("${fuji.api.face-increment-path}")
    private String faceIncrementPath;

    @Value("${fuji.api.unregister-path:/api/TokenService/Unregister}")
    private String unregisterPath;

    @Value("${fuji.api.get-by-func-path:/api/Staff/GetByFunc}")
    private String getByFuncPath;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * 接口1: 发行校验
     * 校验卡号/人脸是否已有发行信息
     * TokenType: 0=卡, 1=人脸
     */
    public CheckTokenResponse checkTokenState(CheckTokenRequest request) {
        return doPost(checkTokenPath, request, CheckTokenResponse.class);
    }

    /**
     * 接口2: 人脸发行
     * 向设备下发人脸权限
     */
    public FaceIssueResponse faceIssue(FaceIssueRequest request) {
        return doPost(faceIssuePath, request, FaceIssueResponse.class);
    }

    /**
     * 接口3: 人脸发行改写
     * 修改已发行的人脸权限
     */
    public FaceIssueResponse faceModify(FaceIssueRequest request) {
        return doPost(faceModifyPath, request, FaceIssueResponse.class);
    }

    /**
     * 接口4: 卡发行
     * 向门禁设备下发卡权限
     */
    public CardIssueResponse cardIssue(CardIssueRequest request) {
        return doPost(cardIssuePath, request, CardIssueResponse.class);
    }

    /**
     * 接口5: 人脸增量权限及删除
     * powerType: 0=增加设备权限, 1=删除设备权限
     */
    public FaceIssueResponse faceModifyByPowerType(FaceIncrementRequest request) {
        return doPost(faceIncrementPath, request, FaceIssueResponse.class);
    }

    /**
     * 接口6: 权限注销
     * 注销人脸/卡权限，需对3个固定GID分别调用
     * TokenType: 1=人脸, 0=卡
     * TokenOper: 12=注销
     */
    public UnregisterResponse unregister(UnregisterRequest request) {
        return doPost(unregisterPath, request, UnregisterResponse.class);
    }

    /**
     * 接口7: 根据手机号查询卡号
     * 用于退卡时查询访客的卡号(Reserve2字段)
     */
    public Object getByFunc(String phone) {
        try {
            String json = objectMapper.writeValueAsString(
                    java.util.Map.of("phone", phone));
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + getByFuncPath).post(body).build();
            log.info("调用富士查询卡号接口, phone: {}", phone);
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, Object.class);
                } else {
                    throw new RuntimeException("查询卡号失败, HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("查询卡号异常: " + e.getMessage(), e);
        }
    }

    /**
     * 统一 HTTP POST 请求
     */
    private <T> T doPost(String path, Object request, Class<T> responseClass) {
        String url = baseUrl + path;
        try {
            String json = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder().url(url).post(body).build();

            log.info("调用富士接口: {}, 请求参数: {}", path, json);

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.info("富士接口响应: {}, HTTP状态码: {}", path, response.code());

                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, responseClass);
                } else {
                    log.error("富士接口调用失败: {}, HTTP状态码: {}, 响应: {}", path, response.code(), responseBody);
                    throw new RuntimeException("富士接口调用失败, HTTP " + response.code() + ": " + responseBody);
                }
            }
        } catch (IOException e) {
            log.error("富士接口调用异常: {}, 错误: {}", path, e.getMessage());
            throw new RuntimeException("富士接口调用异常: " + e.getMessage(), e);
        }
    }
}
