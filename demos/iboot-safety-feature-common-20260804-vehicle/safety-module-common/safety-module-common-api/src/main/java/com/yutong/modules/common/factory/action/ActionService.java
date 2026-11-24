package com.yutong.modules.common.factory.action;

import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class ActionService {

    @Autowired
    public RestTemplate restTemplate;

    /**
     * 执行参数
     * @param config
     * @param input
     * @return
     */
    public abstract String run(YtsafetyApiRouteConfig config, Object input) throws Exception;
}
