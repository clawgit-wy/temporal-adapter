package com.hik.adapter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.adapter.model.PersonInfo;
import com.hik.adapter.model.PersonResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 海康 API 客户端
 * 负责调用海康开放平台的单人校验接口
 * 本 demo 中指向本应用内置的 Mock 接口
 */
@Slf4j
public class HikApiClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String hikApiUrl;

    public HikApiClient(String hikApiUrl) {
        this.hikApiUrl = hikApiUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * 调用海康单人校验接口
     */
    public PersonResult verifyPerson(PersonInfo person) {
        try {
            String json = objectMapper.writeValueAsString(person);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(hikApiUrl)
                    .post(body)
                    .build();

            log.info("调用海康校验接口, personId: {}, name: {}", person.getPersonId(), person.getName());

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";

                if (response.isSuccessful()) {
                    log.info("海康校验成功, personId: {}", person.getPersonId());
                    // 解析返回结果，mock接口返回 { "passed": true/false, "errorMsg": null }
                    var responseMap = objectMapper.readValue(responseBody, java.util.Map.class);
                    boolean passed = Boolean.TRUE.equals(responseMap.get("passed"));
                    String errorMsg = (String) responseMap.get("errorMsg");

                    if (passed) {
                        return PersonResult.success(person.getPersonId(), responseBody);
                    } else {
                        return PersonResult.fail(person.getPersonId(), errorMsg != null ? errorMsg : "校验未通过");
                    }
                } else {
                    log.warn("海康接口返回非200, personId: {}, code: {}", person.getPersonId(), response.code());
                    return PersonResult.fail(person.getPersonId(), "HTTP " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("调用海康接口异常, personId: {}", person.getPersonId(), e);
            // 抛出异常让 Temporal 触发重试机制
            throw new RuntimeException("海康接口调用异常: " + e.getMessage(), e);
        }
    }
}
