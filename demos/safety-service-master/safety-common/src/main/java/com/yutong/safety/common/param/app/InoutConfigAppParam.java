package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-9 9:43
 */
@Data
public class InoutConfigAppParam implements Check {
    /**
     * 员工号
     */
    private String empCode;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empCode), ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空！");
    }
}
