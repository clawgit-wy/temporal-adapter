package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.TASK_OBJECT_TYPE_CUSTOMIZE;

@Data
public class SafetyFireInspectionTaskSetAddParams implements Check {

    @ApiModelProperty("数据ID：修改时使用，新增时为空")
    private Long id;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("作业环节编码（作业环节下拉框赋值）")
    private String workStepCode;

    @ApiModelProperty("作业环节名称（作业环节下拉框赋值）")
    private String workStepName;

    @ApiModelProperty("作业部门编码（作业环节下拉框赋值）")
    private String workDeptCode;

    @ApiModelProperty("作业部门名称（作业环节下拉框赋值）")
    private String workDeptName;

    @ApiModelProperty("风险等级（风险等级下拉框 riskLevel 赋值）")
    private String riskLevel;

    @ApiModelProperty("任务对像类型（0：默认； 1：自定义）")
    private Long taskObjectType;

    @ApiModelProperty("任务对像名称（任务对象下拉框赋值）")
    private String empNo;

    @ApiModelProperty("任务对象工号（任务对象下拉框赋值）")
    private String empName;

    @ApiModelProperty("任务开始时间(yyyy-DD-MM)")
    private String taskBeginDate;

    @ApiModelProperty("任务结束时间(yyyy-DD-MM)")
    private String taskEndDate;

    @ApiModelProperty("备注")
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(taskName), ResponseEnum.PARAM_ERROR.getCode(), "任务名称 taskName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workStepCode), ResponseEnum.PARAM_ERROR.getCode(), "作业环节编码 workStepCode不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workStepName), ResponseEnum.PARAM_ERROR.getCode(), "作业环节名称 workStepName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workDeptCode), ResponseEnum.PARAM_ERROR.getCode(), "作业部门编码 workDeptCode不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workDeptName), ResponseEnum.PARAM_ERROR.getCode(), "作业部门名称 workDeptName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(riskLevel), ResponseEnum.PARAM_ERROR.getCode(), "风险等级 riskLevel不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(taskObjectType), ResponseEnum.PARAM_ERROR.getCode(), "任务对像类型 taskObjectType不能为空！");
        PreconditonsEx.checkArgumentNull(taskObjectType.equals(TASK_OBJECT_TYPE_CUSTOMIZE) && StringUtils.isBlank(empNo), ResponseEnum.PARAM_ERROR.getCode(), "任务对像名称 empNo不能为空！");
        PreconditonsEx.checkArgumentNull(taskObjectType.equals(TASK_OBJECT_TYPE_CUSTOMIZE) && StringUtils.isBlank(empName), ResponseEnum.PARAM_ERROR.getCode(), "任务对象工号 empName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(taskBeginDate), ResponseEnum.PARAM_ERROR.getCode(), "任务开始时间 taskBeginDate不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(taskEndDate), ResponseEnum.PARAM_ERROR.getCode(), "任务结束时间 taskEndDate不能为空！");

        // 特殊处理
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
