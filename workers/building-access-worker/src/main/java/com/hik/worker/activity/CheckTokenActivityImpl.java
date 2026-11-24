package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.CheckTokenRequest;
import com.hik.worker.model.CheckTokenResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 发行校验 Activity 实现
 */
@Slf4j
@Component
public class CheckTokenActivityImpl implements CheckTokenActivity {

    private final FujiApiClient fujiApiClient;

    public CheckTokenActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public CheckTokenResponse checkToken(CheckTokenRequest request) {
        log.info("发行校验, TokenType: {}, Token: {}", request.getTokenType(), request.getToken());
        try {
            return fujiApiClient.checkTokenState(request);
        } catch (RuntimeException e) {
            log.warn("发行校验失败, Token: {}", request.getToken());
            throw Activity.wrap(e);
        }
    }
}
