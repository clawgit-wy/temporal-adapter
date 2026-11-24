package com.yutong.safety.web.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.CrudResourceStore;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 图片资源
 */
@Component
@RequiredArgsConstructor
public class CaptchaResourceConfiguration {

    private final ResourceStore resourceStore;

    @PostConstruct
    public void init() {
        CrudResourceStore resourceStore = (CrudResourceStore) this.resourceStore;
        // 2. 添加自定义背景图片 list为文件名
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6");
        list.forEach(li -> {
            resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "images/" + li + ".png", "default"));
            resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "images/" + li + ".png", "default"));
            resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "images/" + li + ".png", "default"));
            resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "images/" + li + ".png", "default"));
        });
    }
}
