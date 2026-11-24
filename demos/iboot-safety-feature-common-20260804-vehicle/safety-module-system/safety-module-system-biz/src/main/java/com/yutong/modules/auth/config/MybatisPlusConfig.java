package com.yutong.modules.auth.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.yutong.modules.auth.api.UserAuthDataApi;
import com.yutong.modules.auth.config.interceptor.DataPermissionInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@MapperScan(value={"com.yutong.modules.**.mapper*","org.jeecg.modules.**.mapper*"})
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptorData(@Lazy UserAuthDataApi ruleService) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 注册数据权限拦截器
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(ruleService));
        //interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
