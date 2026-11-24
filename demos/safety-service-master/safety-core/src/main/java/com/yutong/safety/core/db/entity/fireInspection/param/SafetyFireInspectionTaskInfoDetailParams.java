package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
public class SafetyFireInspectionTaskInfoDetailParams implements Check {

    @ApiModelProperty("任务详情明细数据行ID")
    private Long id;

    @ApiModelProperty("检查结果 0：异常；1：正常")
    private Integer result;

    @ApiModelProperty("异常流程ID 异常时必填")
    private String processId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "任务详情明细数据行ID不能为空！");
        PreconditonsEx.checkArgumentNull(0 == result && StringUtils.isBlank(processId), ResponseEnum.PARAM_ERROR.getCode(), "异常时 异常流程ID processId不能为空！");
    }
}
