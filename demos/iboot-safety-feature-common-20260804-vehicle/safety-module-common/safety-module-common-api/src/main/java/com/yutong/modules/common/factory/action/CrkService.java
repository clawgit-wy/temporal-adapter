package com.yutong.modules.common.factory.action;

import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * 出入口
 */
@Slf4j
@Component("crk")
public class CrkService extends ActionService {

    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(input, header);
        String url = config.getSysUrl();
        return restTemplate.postForObject(url, httpEntity, String.class);
    }
}
