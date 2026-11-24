package com.yutong.modules.user.api;
import com.yutong.modules.user.api.fallback.DemoFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "jeecg-demo", fallbackFactory = DemoFallback.class)
public interface DemoApi {

    /**
     * demo hello 微服务接口
     * @param
     * @return
     */
    @GetMapping(value = "/demo/hello")
    String callHello();
}
