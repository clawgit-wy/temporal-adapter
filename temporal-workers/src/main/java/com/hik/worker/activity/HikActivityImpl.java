package com.hik.worker.activity;

import com.hik.worker.client.HikApiClient;
import com.hik.worker.model.PersonInfo;
import com.hik.worker.model.PersonResult;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
public class HikActivityImpl implements HikActivity {

    private HikApiClient hikApiClient;

    @Value("${hik.api.url:http://localhost:19090/api/hikvision/verify}")
    private String hikApiUrl;

    @PostConstruct
    public void init() {
        this.hikApiClient = new HikApiClient(hikApiUrl);
    }

    @Override
    public PersonResult verifySinglePerson(PersonInfo person) {
        log.info("Activity 开始校验人员, personId: {}", person.getPersonId());
        try {
            return hikApiClient.verifyPerson(person);
        } catch (RuntimeException e) {
            log.warn("Activity 执行失败，等待 Temporal 重试, personId: {}", person.getPersonId());
            throw Activity.wrap(e);
        }
    }
}
