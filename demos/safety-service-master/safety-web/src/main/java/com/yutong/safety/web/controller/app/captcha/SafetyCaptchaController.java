package com.yutong.safety.web.controller.app.captcha;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.dto.captcha.*;
import com.yutong.safety.core.handler.captcha.SafetyCaptchaHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Api(tags = "图文校验")
@RestController
@RequestMapping("/itf/safety/captcha")
public class SafetyCaptchaController extends VoUtil {

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @Resource
    private SafetyCaptchaHandler safetyCaptchaHandler;

    @PostMapping(value = "/gen")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "验证类型：SLIDER：滑块，ROTATE：旋转，CONCAT：滑动，WORD_IMAGE_CLICK：点选，RANDOM：随机", required = false, dataType = "String", paramType = "query")
    })
    public Object genCaptcha(@RequestParam(value = "type", required = false) String type) {
        if (StringUtils.isBlank(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        if ("RANDOM".equals(type)) {
            int i = ThreadLocalRandom.current().nextInt(0, 4);
            if (i == 0) {
                type = CaptchaTypeConstant.SLIDER;
            } else if (i == 1) {
                type = CaptchaTypeConstant.CONCAT;
            } else if (i == 2) {
                type = CaptchaTypeConstant.ROTATE;
            } else {
                type = CaptchaTypeConstant.WORD_IMAGE_CLICK;
            }
        }

        ApiResponse<ImageCaptchaVO> imageCaptchaVOApiResponse = imageCaptchaApplication.generateCaptcha(type);
        ImageCaptchaVO captchaVO = imageCaptchaVOApiResponse.getData();
        SafetyImageCaptchaVO data = new SafetyImageCaptchaVO();
        SafetyCaptcha captcha = new SafetyCaptcha();
        BeanUtils.copyProperties(captchaVO, captcha);
        data.setId(captchaVO.getId());
        data.setCaptcha(captcha);
        return data;
    }

    @PostMapping(value = "/check")
    public ApiResponse<?> checkCaptcha(@RequestBody SafetyCaptchaCheckParam data) {
        SafetyImageCaptchaTrack checkParam = data.getData();
        ImageCaptchaTrack imageCaptchaTrack = new ImageCaptchaTrack();
        BeanUtils.copyProperties(checkParam, imageCaptchaTrack);
        imageCaptchaTrack.setStartTime(new Date().getTime());
        imageCaptchaTrack.setStopTime(new Date().getTime());
        imageCaptchaTrack.setTrackList(checkParam.getTrackList());
        ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), imageCaptchaTrack);
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
        }
        return response;
    }

    @ApiOperation(value = "二次验证")
    @PostMapping(value = "/captchaSecondCheck")
    public Object captchaSecondCheck(@RequestBody SafetyCaptchaSecondCheck captchaSecondCheck) {
        return safetyCaptchaHandler.captchaSecondCheck(captchaSecondCheck.getToken());
    }
}
