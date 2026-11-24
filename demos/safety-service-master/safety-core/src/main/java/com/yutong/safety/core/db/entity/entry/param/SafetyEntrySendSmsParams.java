package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.SMS_CODE_TYPE_LOGIN;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.SMS_CODE_TYPE_REGION;

@Data
public class SafetyEntrySendSmsParams implements Check {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("类型： LOGIN：登录， REGION：注册")
    private String type;

    @ApiModelProperty("校验验证码接口返回的token")
    private String token;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(phone), ResponseEnum.PARAM_ERROR.getCode(), "手机号 phone不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(type), ResponseEnum.PARAM_ERROR.getCode(), "验证类型 type不能为空！");
        PreconditonsEx.checkArgument(SMS_CODE_TYPE_LOGIN.equals(type) || SMS_CODE_TYPE_REGION.equals(type) , ResponseEnum.PARAM_ERROR.getCode(), "验证类型 type不正确！");
        PreconditonsEx.checkArgument(Objects.nonNull(token), ResponseEnum.PARAM_ERROR.getCode(), "校验验证码接口返回的token不能为空！");

    }
}
