package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyVisitorAuthorizationApprovalParam implements Check {

    @ApiModelProperty("数据ID")
    private Integer id;

    @ApiModelProperty("当前审批人工号")
    private String empNo;

    @ApiModelProperty("审批类型 0：通过， 1：驳回")
    private Integer approvalType;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据ID id 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(approvalType), ResponseEnum.PARAM_ERROR.getCode(), "审批类型 approvalType 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), ResponseEnum.PARAM_ERROR.getCode(), "当前审批人工号 empNo 不能为空！");
    }
}
