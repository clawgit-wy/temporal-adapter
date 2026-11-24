package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class SafetyFireInspectionStandardQueryParams extends WebPageParam {

    @ApiModelProperty("作业环节名称")
    private String workStepName;

    @ApiModelProperty("作业环节编码")
    private String workStepCode;

    @ApiModelProperty("检查细分维度")
    private String inspectionSubDimension;

    @ApiModelProperty("风险等级（风险等级下拉框 riskLevel 赋值）")
    private String riskLevel;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("风险等级（后端使用）")
    private List<String> riskLevelList;

    @Override
    public void check() {
        List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
        if (StringUtils.isNotBlank(riskLevel) && riskLevelList.contains(riskLevel)) {
            riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(riskLevel).getCode();
        }
    }
}
