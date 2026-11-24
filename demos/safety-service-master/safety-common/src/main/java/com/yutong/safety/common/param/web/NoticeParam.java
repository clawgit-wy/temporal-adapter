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
public class NoticeParam implements Check {

    private Integer id;
    private String content;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(content), ResponseEnum.PARAM_ERROR.getCode(), "content不能为空！");
    }
}
