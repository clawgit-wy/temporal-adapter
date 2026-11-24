package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.08.03
 */
@Data
public class SheetSetAddParam implements Check {

    private String visitor;
    private String eipId;
    private String info;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(visitor), ResponseEnum.PARAM_ERROR.getCode(), "visitor不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(eipId) , ResponseEnum.PARAM_ERROR.getCode(), "eipId不能为空！");
    }
}
