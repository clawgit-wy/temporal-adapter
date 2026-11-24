package com.yutong.modules.common.factory.action;

import cn.hutool.core.codec.Base64;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * bpm 流程
 */
@Slf4j
@Component("bpm")
public class BpmService extends ActionService {



    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        String result="";
        if("soap1.1".equals(config.getInterfaceMethod())){
            result = httpSoap1_1(config,input);
        }else{
            result = httpSoap1_2(config,input);
        }
        return result;
    }

    /**
     * soap1.1 协议
     * @param config
     * @param input
     * @return
     */
    private String httpSoap1_1(YtsafetyApiRouteConfig config, Object input) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "text/xml;charset=UTF-8");
        if (StringUtils.isNotEmpty(config.getInterfaceAccount())) {
            header.add("Authorization", "Basic "
                    + Base64.encode(String.format("%s:%s", config.getInterfaceAccount(), config.getInterfacePwd()).getBytes()));
        }
        header.add("SOAPAction", "");
        HttpEntity<Object> httpEntity = new HttpEntity<>(input, header);
        String url = config.getSysUrl() + (config.getIntefacePath() == null ? "" : config.getIntefacePath());
        return restTemplate.postForObject(url, httpEntity, String.class);
    }

    /**
     * soap1.1 协议
     * @param config
     * @param input
     * @return
     */
    private String httpSoap1_2(YtsafetyApiRouteConfig config, Object input){
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type","application/soap+xml;charset=UTF-8");
        if(StringUtils.isNotEmpty(config.getInterfaceAccount())) {
            header.add("Authorization", "Basic "
                    + Base64.encode(String.format("%s:%s", config.getInterfaceAccount(), config.getInterfacePwd()).getBytes()));
        }
        header.add("SOAPAction", "");
        HttpEntity<Object> httpEntity = new HttpEntity<>(input, header);
        String url = config.getSysUrl()+config.getIntefacePath();
        return restTemplate.postForObject(url, httpEntity, String.class);
    }


}
