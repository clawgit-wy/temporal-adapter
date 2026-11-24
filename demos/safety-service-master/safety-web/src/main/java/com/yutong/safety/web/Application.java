package com.yutong.safety.web;

import com.yutong.safety.web.config.EntryNetworkFilter;
import com.yutong.safety.web.config.SSOLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by gwcuiyx on 23-07-06
 */
@EnableAsync
@ImportResource("classpath:spring/application.xml")
@SpringBootApplication(scanBasePackages = "com.yutong")
public class Application {

    @Autowired
    private SSOLoginFilter SSOLoginFilter;

    @Autowired
    private EntryNetworkFilter entryNetworkFilter;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(SSOLoginFilter);
        registration.addUrlPatterns("/web/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBea1n() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(entryNetworkFilter);
        registration.addUrlPatterns("/itf/*");
        return registration;
    }

}
