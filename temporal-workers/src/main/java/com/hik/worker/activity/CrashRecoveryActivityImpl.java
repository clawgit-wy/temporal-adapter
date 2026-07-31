package com.hik.worker.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CrashRecoveryActivityImpl implements CrashRecoveryActivity {

    private final AtomicInteger stepCounter = new AtomicInteger(0);

    @Override
    public String executeStep(String stepName) {
        int count = stepCounter.incrementAndGet();
        log.info("[断点续传] 执行步骤: {}, 全局步骤计数: {}", stepName, count);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("[断点续传] 步骤 {} 执行完成", stepName);
        return stepName + " 处理成功 @" + System.currentTimeMillis();
    }
}
