package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyFireInspectionWorkStepAddParams implements Check {

    @ApiModelProperty("数据ID：修改时使用，新增时为空")
    private Long id;

    @ApiModelProperty("作业环节编码")
    private String workStepCode;

    @ApiModelProperty("作业环节名称")
    private String workStepName;

    @ApiModelProperty("工序")
    private String process;

    @ApiModelProperty("作业单元")
    private String workUnit;

    @ApiModelProperty("作业类型")
    private String workType;

    @ApiModelProperty("作业部门编码")
    private String workDeptCode;

    @ApiModelProperty("作业部门名称")
    private String workDeptName;

    @ApiModelProperty("备注")
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(workStepCode), ResponseEnum.PARAM_ERROR.getCode(), "作业环节编码workStepCode不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workStepName), ResponseEnum.PARAM_ERROR.getCode(), "作业环节名称workStepName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(process), ResponseEnum.PARAM_ERROR.getCode(), "工序process不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workUnit), ResponseEnum.PARAM_ERROR.getCode(), "作业单元workUnit不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workType), ResponseEnum.PARAM_ERROR.getCode(), "作业类型workType不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workDeptCode), ResponseEnum.PARAM_ERROR.getCode(), "作业部门编码workDeptCode不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workDeptName), ResponseEnum.PARAM_ERROR.getCode(), "作业部门名称workDeptName不能为空！");
    }
}
