package com.yutong.safety.core.handler.captcha;

import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class SafetyCaptchaHandler {

    @Value("${an.captcha.appId}")
    private String appId;

    @Value("${an.captcha.captchaSecondCheckUrl}")
    private String captchaSecondCheckUrl;

    @Resource
    private WebserviceUtil webserviceUtil;

    public Map<String, Object> captchaSecondCheck(String token) {
        Map<String, String> headParam = new HashMap<>();
        headParam.put("Accept", "application/json");
        String result = webserviceUtil.sendHttp(String.format("%s&appId=%s&token=%s", captchaSecondCheckUrl, appId, token), "1", null, headParam, "POST");
        LogUtils.info("二次验证出参：" + result);
        return JsonUtils.toMap(result);
    }
}
