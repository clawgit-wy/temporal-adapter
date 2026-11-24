package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.WebPageParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2023-10-23 9:19
 */
@Data
public class VisitorAppListParam extends WebPageParam implements Check {

    private String wxPhone;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(wxPhone), ResponseEnum.PARAM_ERROR.getCode(), "wxPhone不能为空！");
    }
}
