package com.hik.adapter.activity;

import com.hik.adapter.client.HikApiClient;
import com.hik.adapter.model.PersonInfo;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 场景1: 限流Activity实现
 * 调用mokapi模拟接口，每次约50ms
 */
@Slf4j
@Component
public class RateLimitActivityImpl implements RateLimitActivity {

    private HikApiClient hikApiClient;

    @Value("${hik.api.url:http://localhost:19090/api/hikvision/verify}")
    private String hikApiUrl;

    private final AtomicInteger counter = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        this.hikApiClient = new HikApiClient(hikApiUrl);
    }

    @Override
    public boolean callApi(int index) {
        try {
            // 构造正常人员信息调用mokapi
            PersonInfo person = PersonInfo.builder()
                    .personId("P_" + index)
                    .name("测试人员" + index)
                    .faceUrl("http://example.com/face/" + index + ".jpg")
                    .build();
            var result = hikApiClient.verifyPerson(person);
            int count = counter.incrementAndGet();
            if (count % 100 == 0) {
                log.info("限流场景进度: 已完成 {} 次调用", count);
            }
            return result.isPassed();
        } catch (Exception e) {
            throw Activity.wrap(e);
        }
    }
}
