package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-2-27 17:37
 */
@Data
public class GeneralConfigAppParam implements Check {

    /**
     * 发送数据类型
     */
    private String generalType;

    /**
     * 手机号
     */
    private String phones;

    /**
     * 员工号
     */
    private String empNo;

    /**
     * 机器人id
     */
    private String robotId;

    /**
     * eip流程id
     */
    private String eipId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(generalType), ResponseEnum.PARAM_ERROR.getCode(), "数据类型不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(eipId), ResponseEnum.PARAM_ERROR.getCode(), "eip流程不能为空！");
        PreconditonsEx.checkArgument((Objects.nonNull(empNo) || Objects.nonNull(phones)), ResponseEnum.PARAM_ERROR.getCode(), "员工号或手机号不能为空！");
    }
}
