package com.yutong.safety.web.controller.web.webService.conf;

import com.yutong.safety.web.controller.web.webService.service.SafetyBpmWebService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * webService接口配置类
 * @author lukunpeng
 * @time 2024-4-26
 */
@Configuration
public class WebServiceConf {
    @Autowired
    private Bus bus;
    @Autowired
    private SafetyBpmWebService safetyBpmWebService;

    @Bean
    public Endpoint endpoint1() {
        EndpointImpl endpoint = new EndpointImpl(bus, safetyBpmWebService);
        endpoint.publish("/safetyBpmWebService");
        return endpoint;
    }
}
