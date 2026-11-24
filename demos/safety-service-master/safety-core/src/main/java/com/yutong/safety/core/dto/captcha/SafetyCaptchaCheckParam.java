package com.yutong.safety.core.dto.captcha;

import lombok.Data;

@Data
public class SafetyCaptchaCheckParam {
    private String id;

    private SafetyImageCaptchaTrack data;
}
