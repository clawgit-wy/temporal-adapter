package com.yutong.safety.core.dto.captcha;

import lombok.Data;

@Data
public class SafetyImageCaptchaVO {

    private String id;

    private SafetyCaptcha captcha;
}
