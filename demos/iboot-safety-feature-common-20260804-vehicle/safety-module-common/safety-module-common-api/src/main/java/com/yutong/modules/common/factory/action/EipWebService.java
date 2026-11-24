package com.yutong.modules.common.factory.action;


import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component("eip")
public class EipWebService extends ActionService {
    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json;charset=UTF-8");
        header.add("Connection", "Close");
        header.add("SOAPAction", "application/soap+json; charset=UTF-8");
        header.add("Accept", MediaType.APPLICATION_JSON.getType());
        HttpEntity<Object> httpEntity = new HttpEntity<>(input, header);
        String url = config.getSysUrl();
        return restTemplate.postForObject(url, httpEntity, String.class);
    }
}
