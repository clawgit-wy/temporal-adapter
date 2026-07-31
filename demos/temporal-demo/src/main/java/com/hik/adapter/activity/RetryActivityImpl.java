package com.hik.adapter.activity;

import com.hik.adapter.client.HikApiClient;
import com.hik.adapter.model.PersonInfo;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * 场景2: 重试Activity实现
 * 通过ActivityExecutionContext获取当前重试次数
 * 前3次调用故意使用ERROR标记的personId触发mokapi报错，第4次使用正常personId
 */
@Slf4j
@Component
public class RetryActivityImpl implements RetryActivity {

    private HikApiClient hikApiClient;

    @Value("${hik.api.url:http://localhost:19090/api/hikvision/verify}")
    private String hikApiUrl;

    @PostConstruct
    public void init() {
        this.hikApiClient = new HikApiClient(hikApiUrl);
    }

    @Override
    public boolean callWithRetry(int index) {
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        int attempt = ctx.getInfo().getAttempt();

        log.info("重试Activity执行, 任务序号: {}, 当前尝试次数: {}", index, attempt);

        try {
            // 前3次使用ERROR标记的personId，触发mokapi返回500错误
            // 第4次起使用正常personId
            String personId;
            if (attempt < 4) {
                personId = "P_ERROR_" + index;
                log.info("任务 {} 第 {} 次尝试, 使用ERROR标记触发失败", index, attempt);
            } else {
                personId = "P_" + index;
                log.info("任务 {} 第 {} 次尝试, 使用正常ID", index, attempt);
            }

            PersonInfo person = PersonInfo.builder()
                    .personId(personId)
                    .name("重试测试人员" + index)
                    .faceUrl("http://example.com/face/" + index + ".jpg")
                    .build();

            var result = hikApiClient.verifyPerson(person);
            log.info("任务 {} 第 {} 次尝试成功, passed: {}", index, attempt, result.isPassed());
            return result.isPassed();
        } catch (ActivityCompletionException e) {
            throw e;
        } catch (Exception e) {
            log.warn("任务 {} 第 {} 次尝试失败: {}", index, attempt, e.getMessage());
            throw Activity.wrap(e);
        }
    }
}
