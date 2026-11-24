package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyEntryLoginParams implements Check {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("验证码")
    private String code;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(phone), ResponseEnum.PARAM_ERROR.getCode(), "手机号 phone不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(code), ResponseEnum.PARAM_ERROR.getCode(), "验证码 code不能为空！");

    }
}
