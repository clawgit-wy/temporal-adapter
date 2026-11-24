package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.FB;
import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.FQ;

@Data
public class SafetyFireInspectionEipProcessParams implements Check {
    @ApiModelProperty("EIP流程ID")
    private String processId;

    @ApiModelProperty("EIP流程状态：00 废弃； 30 发布")
    private String status;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(processId), ResponseEnum.PARAM_ERROR.getCode(), "EIP流程ID processId不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(status), ResponseEnum.PARAM_ERROR.getCode(), "EIP流程状态 status不能为空！");
        PreconditonsEx.checkArgumentNull(!status.equals(FQ) && !status.equals(FB), ResponseEnum.PARAM_ERROR.getCode(), "EIP流程状态 status格式不正确！");

    }
}
