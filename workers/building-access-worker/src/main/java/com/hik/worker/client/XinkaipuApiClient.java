package com.hik.worker.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 新开普设备 API 客户端
 * 对接新开普 IdentityWebService，通过 ThirdOpenApiCall 接口进行门禁权限管理
 * 设备类型标识: 1=新开普, 2=富士
 */
@Slf4j
@Component
public class XinkaipuApiClient {

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${xinkaipu.api.base-url}")
    private String baseUrl;

    @Value("${xinkaipu.api.get-device-path}")
    private String getDevicePath;

    @Value("${xinkaipu.api.get-area-path}")
    private String getAreaPath;

    @Value("${xinkaipu.api.grant-build-path}")
    private String grantBuildPath;

    @Value("${xinkaipu.api.get-grant-info-path}")
    private String getGrantInfoPath;

    @Value("${xinkaipu.api.get-record-path}")
    private String getRecordPath;

    @Value("${xinkaipu.api.dpcode}")
    private String dpcode;

    @Value("${xinkaipu.api.appid}")
    private String appid;

    @Value("${xinkaipu.api.encryption-key}")
    private String encryptionKey;

    @Value("${xinkaipu.api.signature-key}")
    private String signatureKey;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * 接口1: 获取门禁设备区域信息
     * 路径: /IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetAccessTermAreaInfo
     */
    public Object getAccessTermAreaInfo() {
        Map<String, Object> params = buildAuthParams();
        return doPost(getDevicePath, params);
    }

    /**
     * 接口2: 获取门禁区域信息
     * 路径: /IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetAccessAreaInfo
     */
    public Object getAccessAreaInfo() {
        Map<String, Object> params = buildAuthParams();
        return doPost(getAreaPath, params);
    }

    /**
     * 接口3: 门禁权限下发（授权）
     * 路径: /IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/AddGrantByOutid
     * 新开普授权设备按200个分批授权
     *
     * @param outid     人员外部ID（员工号）
     * @param extendIds 设备编码列表
     * @param empcode   固定账号 "mjqxdj"
     * @param beginDate 生效日期
     * @param endDate   失效日期
     */
    public Object addGrantByOutid(String outid, List<String> extendIds, String empcode,
                                   String beginDate, String endDate) {
        // 新开普授权设备按200个分批授权
        int batchSize = 200;
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < extendIds.size(); i += batchSize) {
            List<String> batch = extendIds.subList(i, Math.min(i + batchSize, extendIds.size()));
            Map<String, Object> params = buildAuthParams();
            params.put("outid", outid);
            params.put("extendids", batch);
            params.put("empcode", empcode != null ? empcode : "mjqxdj");
            params.put("begindate", beginDate);
            params.put("enddate", endDate);
            log.info("新开普权限下发, outid: {}, 设备数: {}, 批次: {}/{}",
                    outid, batch.size(), (i / batchSize) + 1,
                    (extendIds.size() + batchSize - 1) / batchSize);
            results.add(doPost(grantBuildPath, params));
        }
        return results;
    }

    /**
     * 接口4: 查询授权信息
     * 路径: /IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetGrantInfo
     *
     * @param outid 人员外部ID（员工号）
     */
    public Object getGrantInfo(String outid) {
        Map<String, Object> params = buildAuthParams();
        params.put("outid", outid);
        return doPost(getGrantInfoPath, params);
    }

    /**
     * 接口5: 查询进出记录
     * 路径: /IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetRecordInfo
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param outid     人员外部ID（可选）
     */
    public Object getRecordInfo(String beginTime, String endTime, String outid) {
        Map<String, Object> params = buildAuthParams();
        params.put("begintime", beginTime);
        params.put("endtime", endTime);
        if (outid != null) {
            params.put("outid", outid);
        }
        return doPost(getRecordPath, params);
    }

    /**
     * 构建新开普认证参数
     * TODO: 根据新开普加密协议实现加密逻辑（BuildEncryptUtil）
     */
    private Map<String, Object> buildAuthParams() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("dpcode", dpcode);
        params.put("appid", appid);
        // TODO: 使用 encryptionKey 和 signatureKey 对请求进行加密签名
        // 原 safety-service-master 中通过 BuildEncryptUtil 实现加密通信
        return params;
    }

    /**
     * 发送 HTTP POST 请求
     */
    private Object doPost(String path, Map<String, Object> params) {
        String url = baseUrl + path;
        try {
            String json = objectMapper.writeValueAsString(params);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder().url(url).post(body).build();

            log.info("调用新开普接口: {}, 请求参数: {}", path, json);

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, Object.class);
                } else {
                    throw new RuntimeException("新开普接口调用失败, HTTP " + response.code() + ", 响应: " + responseBody);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("新开普接口调用异常: " + e.getMessage(), e);
        }
    }
}
