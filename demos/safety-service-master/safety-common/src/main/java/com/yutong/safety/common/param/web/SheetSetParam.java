package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.10
 */
@Data
public class SheetSetParam implements Check {
    private Integer id;
    private Integer mustSet;
    private Integer takeEffect;
    private String eipId;
    private String visitor;
    private String info;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "id不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(mustSet) || Objects.nonNull(takeEffect) , ResponseEnum.PARAM_ERROR.getCode(), "必填设置或者是否生效不能为空！");
    }
}
