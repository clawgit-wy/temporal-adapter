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
public class FieldManageUpdateParam implements Check {

    private Integer id;

    private String fieldName;

    private String fieldType;

    private String fieldCode;

    private String fieldValue;

    private String envirInfo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(fieldName), ResponseEnum.PARAM_ERROR.getCode(), "名称fieldName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(fieldType), ResponseEnum.PARAM_ERROR.getCode(), "类型fieldType不能为空！");
    }
}
