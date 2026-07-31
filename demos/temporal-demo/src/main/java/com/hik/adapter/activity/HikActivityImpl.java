package com.hik.adapter.activity;

import com.hik.adapter.client.HikApiClient;
import com.hik.adapter.model.PersonInfo;
import com.hik.adapter.model.PersonResult;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * 海康校验 Activity 实现
 * 每次调用即对应一次海康 API 请求
 */
@Slf4j
@Component
public class HikActivityImpl implements HikActivity {

    private HikApiClient hikApiClient;

    @Value("${hik.api.url:http://localhost:18080/mock/hikvision/verify}")
    private String hikApiUrl;

    @PostConstruct
    public void init() {
        this.hikApiClient = new HikApiClient(hikApiUrl);
    }

    @Override
    public PersonResult verifySinglePerson(PersonInfo person) {
        log.info("Activity 开始校验人员, personId: {}, name: {}", person.getPersonId(), person.getName());
        try {
            return hikApiClient.verifyPerson(person);
        } catch (RuntimeException e) {
            // 抛出异常，Temporal 会根据 RetryOptions 自动重试
            log.warn("Activity 执行失败，等待 Temporal 重试, personId: {}", person.getPersonId());
            throw Activity.wrap(e);
        }
    }
}
