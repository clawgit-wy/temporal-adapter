package com.yutong.modules.user.api.fallback;

import com.yutong.modules.user.api.DemoApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author JeecgBoot
 */
@Slf4j
@Component
public class DemoFallback implements FallbackFactory<DemoApi> {
    @Setter
    private Throwable cause;

    @Override
    public DemoApi create(Throwable throwable) {
        log.error("微服务接口调用失败： {}", cause);
        return null;
    }

}
