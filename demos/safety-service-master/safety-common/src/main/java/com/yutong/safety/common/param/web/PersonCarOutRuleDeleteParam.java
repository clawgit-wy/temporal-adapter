package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwxiexj
 * @since 2024-04-01 17:15:00
 */
@Data
public class PersonCarOutRuleDeleteParam implements Check {

    private Integer id;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据id不能为空！");
    }
}
