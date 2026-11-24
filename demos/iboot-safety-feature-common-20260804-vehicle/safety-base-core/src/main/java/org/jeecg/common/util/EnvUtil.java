package org.jeecg.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvUtil implements ApplicationContextAware {

    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        environment = applicationContext.getEnvironment();
    }

    // 获取当前环境
    public static String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 ? profiles[0] : "default";
    }

    // 判断是否是开发环境
    public static boolean isDev() {
        return "local".equals(getActiveProfile());
    }

    // 判断是否是测试环境
    public static boolean isTest() {
        return "test".equals(getActiveProfile());
    }

    // 判断是否是生产环境
    public static boolean isProd() {
        return "prod".equals(getActiveProfile());
    }
}
