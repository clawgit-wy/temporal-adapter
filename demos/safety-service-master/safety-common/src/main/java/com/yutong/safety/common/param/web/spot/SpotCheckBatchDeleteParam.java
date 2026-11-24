package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-29 15:12
 */
@Data
public class SpotCheckBatchDeleteParam implements Check {
    private String ids;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(ids), ResponseEnum.PARAM_ERROR.getCode(), "ids不能为空！");
    }
}
