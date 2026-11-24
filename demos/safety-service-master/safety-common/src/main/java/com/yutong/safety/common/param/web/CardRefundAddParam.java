package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-1-18 11:00
 */
@Data
public class CardRefundAddParam implements Check {
    //访客id
    private Integer visitorId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(visitorId), ResponseEnum.PARAM_ERROR.getCode(), "访客数据id不能为空！");
    }
}
