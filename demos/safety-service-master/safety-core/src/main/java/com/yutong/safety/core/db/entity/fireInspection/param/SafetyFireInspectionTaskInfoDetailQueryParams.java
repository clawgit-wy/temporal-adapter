package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.WebPageParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyFireInspectionTaskInfoDetailQueryParams extends WebPageParam implements Check {

    @ApiModelProperty("主数据行ID")
    private Long inspectionTaskId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionTaskId), ResponseEnum.PARAM_ERROR.getCode(), "主数据行ID inspectionTaskId不能为空！");
    }
}
