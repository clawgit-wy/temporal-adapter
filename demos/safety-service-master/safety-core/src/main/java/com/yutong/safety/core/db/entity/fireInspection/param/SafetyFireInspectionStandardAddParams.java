package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class SafetyFireInspectionStandardAddParams implements Check {

    @ApiModelProperty("数据ID：修改时使用，新增时为空")
    private Long id;

    @ApiModelProperty("作业环节名称（作业环节下拉框赋值）")
    private String workStepName;

    @ApiModelProperty("作业环节编码（作业环节下拉框赋值）")
    private String workStepCode;

    @ApiModelProperty("检查项目")
    private String inspectionItem;

    @ApiModelProperty("检查对象")
    private String inspectionObject;

    @ApiModelProperty("检查类别")
    private String inspectionCategory;

    @ApiModelProperty("检查细分维度")
    private String inspectionSubDimension;

    @ApiModelProperty("检查内容")
    private String inspectionContent;

    @ApiModelProperty("风险等级（风险等级下拉框 riskLevel 赋值）")
    private String riskLevel;

    @ApiModelProperty("检查方法（检查方法下拉框赋值）")
    private String inspectionMethod;

    @ApiModelProperty("检查方法ID（检查方法下拉框赋值）")
    private Long inspectionMethodId;

    @ApiModelProperty("备注")
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(workStepCode), ResponseEnum.PARAM_ERROR.getCode(), "作业环节编码 workStepCode不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(workStepName), ResponseEnum.PARAM_ERROR.getCode(), "作业环节名称 workStepName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionCategory), ResponseEnum.PARAM_ERROR.getCode(), "检查类别 inspectionCategory不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionSubDimension), ResponseEnum.PARAM_ERROR.getCode(), "检查细分维度 inspectionSubDimension不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionContent), ResponseEnum.PARAM_ERROR.getCode(), "检查内容 inspectionContent不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(riskLevel), ResponseEnum.PARAM_ERROR.getCode(), "风险等级 riskLevel不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionMethod), ResponseEnum.PARAM_ERROR.getCode(), "检查方法 inspectionMethod不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(inspectionMethodId), ResponseEnum.PARAM_ERROR.getCode(), "检查方法Id inspectionMethodId不能为空！");

        // 特殊处理
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
