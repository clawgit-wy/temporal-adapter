package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.13
 */
@Data
public class LogMessageAddParam implements Check {
    private String operatePage;
    private String ability;
    private Integer abilityId;
    private String message;
    private String operator;
    private Integer operatorId;

    @Override
    public void check() {
//        PreconditonsEx.checkArgument(Objects.nonNull(operatePage), ResponseEnum.PARAM_ERROR.getCode(), "operatePage不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(ability), ResponseEnum.PARAM_ERROR.getCode(), "ability不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(message), ResponseEnum.PARAM_ERROR.getCode(), "message不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(operator), ResponseEnum.PARAM_ERROR.getCode(), "operator不能为空！");
    }
}
