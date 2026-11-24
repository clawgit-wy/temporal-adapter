package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Data
public class SafetyFireInspectionTaskInfoQueryParams extends WebPageParam {

    @ApiModelProperty("作业环节名称")
    private String workStepName;

    @ApiModelProperty("检查项目")
    private String inspectionItem;

    /**
     * 说明，前端传组织编码后，会获取当前组织编码包含的所有子组织编码的数据一起查询
     */
    @ApiModelProperty("作业部门编码-（单选-当前组织的Code）-（PC端）")
    private String workDeptCode;

    @ApiModelProperty("风险等级（风险等级下拉框 riskLevelDetail 赋值）")
    private String riskLevel;

    @ApiModelProperty("任务状态")
    private String taskStatus;

    @ApiModelProperty("巡检名称")
    private String empName;

    @ApiModelProperty("任务周期开始时间 PC端和手机端 手机端默认本周")
    private String taskCycleBeginDate;

    @ApiModelProperty("任务周期结束时间 PC端和手机端 手机端默认本周")
    private String taskCycleEndDate;


    @ApiModelProperty("任务状态：手机端必传参数  0：未完成（进行中 默认值）1：已完成")
    private Integer taskStatusNum;

    @ApiModelProperty("巡检人工号：手机端必传参数")
    private String empNo;

    private List<String> workDeptCodes;

    @Override
    public void check() {
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (StringUtils.isNotBlank(riskLevel) && riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
