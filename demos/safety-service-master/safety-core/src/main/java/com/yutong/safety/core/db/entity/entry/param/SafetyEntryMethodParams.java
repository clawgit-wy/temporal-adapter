package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyEntryMethodParams implements Check {

    @ApiModelProperty("入厂方式 0：刷脸+二维码，1：刷脸，2：二维码")
    private Integer entryMethod;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(entryMethod), ResponseEnum.PARAM_ERROR.getCode(), "手机号 phone不能为空！");
    }
}
