package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class SafetyFireInspectionTaskInfoDetailAddParams implements Check {

    @ApiModelProperty("任务详情数据行ID")
    private Long taskInfoId;

    @ApiModelProperty("任务详情明细信息")
    private List<SafetyFireInspectionTaskInfoDetailParams> detailInfoList;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(taskInfoId), ResponseEnum.PARAM_ERROR.getCode(), "任务详情数据行ID不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(detailInfoList), ResponseEnum.PARAM_ERROR.getCode(), "任务详情明细信息detailInfoList不能为空！");
    }
}
