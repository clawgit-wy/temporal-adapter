package com.hik.worker.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.worker.model.PersonInfo;
import com.hik.worker.model.PersonResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 海康 API 客户端
 * 调用 mokapi 模拟海康威视开放平台接口
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

    public PersonResult verifyPerson(PersonInfo person) {
        try {
            String json = objectMapper.writeValueAsString(person);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder().url(hikApiUrl).post(body).build();

            log.info("调用海康校验接口, personId: {}", person.getPersonId());

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                if (response.isSuccessful()) {
                    var responseMap = objectMapper.readValue(responseBody, java.util.Map.class);
                    boolean passed = Boolean.TRUE.equals(responseMap.get("passed"));
                    String errorMsg = (String) responseMap.get("errorMsg");
                    if (passed) {
                        return PersonResult.success(person.getPersonId(), responseBody);
                    } else {
                        return PersonResult.fail(person.getPersonId(), errorMsg != null ? errorMsg : "校验未通过");
                    }
                } else {
                    // HTTP 5xx 错误抛出异常，触发 Temporal 自动重试
                    log.warn("海康接口返回HTTP {}, personId: {}", response.code(), person.getPersonId());
                    throw new RuntimeException("海康接口HTTP " + response.code() + ": " + responseBody);
                }
            }
        } catch (IOException e) {
            log.error("调用海康接口异常, personId: {}", person.getPersonId(), e);
            throw new RuntimeException("海康接口调用异常: " + e.getMessage(), e);
        }
    }
}
