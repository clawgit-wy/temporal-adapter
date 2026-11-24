package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-3 16:06
 */
@Data
public class VisitorSetUpdateParam implements Check {
    private Integer id;
    /**
     * 类别
     */
    private String positionType;
    /**
     * 岗位
     */
    private String positionName;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 是否可当被访人
     */
    private Integer visitorState;
    /**
     * 备注
     */
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(positionType), ResponseEnum.PARAM_ERROR.getCode(), "类别不能为空！");
    }
}
