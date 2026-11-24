package com.yutong.safety.common.param.web;

import com.yutong.gw.utils.StringUtils;
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
public class PersonCarOutRuleUpdateParam implements Check {

    private Integer id;

    private Integer employeeId;

    private String carNo;

    private String checkFlag;

    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(employeeId), ResponseEnum.PARAM_ERROR.getCode(), "员工信息不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(checkFlag), ResponseEnum.PARAM_ERROR.getCode(), "私家车出场规则“标记”不能为空！");
    }
}
