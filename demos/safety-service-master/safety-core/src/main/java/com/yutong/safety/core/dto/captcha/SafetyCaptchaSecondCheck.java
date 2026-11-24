package com.yutong.safety.core.dto.captcha;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyCaptchaSecondCheck implements Check {

    @ApiModelProperty("校验验证码接口返回的token")
    private String token;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(token), ResponseEnum.PARAM_ERROR.getCode(), "校验验证码接口返回的token不能为空！");
    }
}
