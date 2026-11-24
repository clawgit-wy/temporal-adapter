package com.yutong.modules.bpmcallback;

import com.yutong.modules.common.factory.IThirdApiFactory;
import com.yutong.modules.common.factory.ThirdApiFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BpmCallBackFactoryConfig {
    
    @Bean("IBpmCallBackFactory")
    public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(IBpmCallBackFactory.class);
        return factoryBean;
    }
}
