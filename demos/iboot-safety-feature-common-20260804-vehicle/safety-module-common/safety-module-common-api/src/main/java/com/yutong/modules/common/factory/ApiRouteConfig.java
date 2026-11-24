package com.yutong.modules.common.factory;

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRouteConfig {
    /**
     * 注册 ApiRoute 实体Bean
     */
    @Bean("ThirdApiFactory")
    public ThirdApiFactory thirdApiFactory() {
        return new ThirdApiFactory();
    }

    @Bean("IThirdApiFactory")
    public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IThirdApiFactory.class);
        return factoryBean;
    }

}
