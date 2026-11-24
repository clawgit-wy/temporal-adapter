package com.yutong.modules.common.factory.action;

import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * 通用Json
 */
@Slf4j
@Component("json")
public class JsonService extends ActionService {

    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        String result="";
        String url = config.getSysUrl()+config.getIntefacePath();
        return post(url,input);
    }

    public String post(String url,Object obj){
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type","application/json;charset=UTF-8");
        header.add("Accept", MediaType.APPLICATION_JSON.getType());
        HttpEntity<Object> requestEntity = new HttpEntity<>(obj,header);
        return restTemplate.postForEntity(url,requestEntity,String.class).getBody();
    }
}
