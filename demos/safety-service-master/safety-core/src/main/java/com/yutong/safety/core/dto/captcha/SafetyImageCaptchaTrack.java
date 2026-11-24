package com.yutong.safety.core.dto.captcha;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SafetyImageCaptchaTrack {

    private Integer bgImageWidth;
    private Integer bgImageHeight;
    private Integer templateImageWidth;
    private Integer templateImageHeight;
    private Date startTime;
    private Date stopTime;
    private Integer left;
    private Integer top;
    private List<ImageCaptchaTrack.Track> trackList;
    private Object data;
}
