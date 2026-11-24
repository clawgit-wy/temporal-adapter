package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class SafetyFireInspectionTaskSetQueryParams extends WebPageParam {

    @ApiModelProperty("作业环节名称")
    private String workStepName;

    @ApiModelProperty("最新提交人")
    private String updateBy;

    @ApiModelProperty("作业部门编码-（单选-当前组织的Code）")
    private String workDeptCode;

    @ApiModelProperty("作业环节编码（详情页时必填）")
    private String workStepCode;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("风险等级（详情页时必填）")
    private String riskLevel;

    @Override
    public void check() {
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (StringUtils.isNotBlank(riskLevel) && riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
