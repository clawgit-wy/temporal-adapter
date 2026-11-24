package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class SafetyFireInspectionTaskObjectQueryParams implements Check {

    @ApiModelProperty("作业部门编码：作业环节对应的作业部门编码，作业环节下拉框中获取（默认的任务对象必传）")
    private String workDeptCode;

    @ApiModelProperty("风险等级： 默认的任务对象必传")
    private String riskLevel;

    @ApiModelProperty("员工工号或者姓名： 自定义的任务对象必传")
    private String empNoAndName;

    @Override
    public void check() {
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (StringUtils.isNotBlank(riskLevel) && riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
