package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-2-27 15:21
 */
@Data
public class GeneralConfigInfoParam implements Check {

    private String generalType;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(generalType), ResponseEnum.PARAM_ERROR.getCode(), "类型不能为空！");
    }

}
