package com.yutong.safety.core.dto.captcha;

import lombok.Data;

@Data
public class SafetyCaptcha {

    private String type;
    private String backgroundImage;
    private String templateImage;
    private String backgroundImageTag;
    private String templateImageTag;
    private Integer backgroundImageWidth;
    private Integer backgroundImageHeight;
    private Integer templateImageWidth;
    private Integer templateImageHeight;
    private Object data;
}
