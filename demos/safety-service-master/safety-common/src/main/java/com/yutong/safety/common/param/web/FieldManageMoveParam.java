package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.12
 */
@Data
public class FieldManageMoveParam implements Check {

    /**
     * 数据id
     */
    private Integer id;

    /**
     * move：0向上，1向下
     */
    private int move;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(move), ResponseEnum.PARAM_ERROR.getCode(), "move不能为空！");
    }
}
