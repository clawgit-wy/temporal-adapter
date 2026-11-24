package com.yutong.safety.core.db.entity.fireInspection.excel;

import com.yutong.safety.common.constants.FireInspectionEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class SafetyFireInspectionStandardExcel extends SafetyFireInspectionBaseExcel {
    /**
     * 作业环节编码
     */
    private String workStepCode;

    /**
     * 检查项目
     */
    private String inspectionItem;

    /**
     * 检查对象
     */
    private String inspectionObject;

    /**
     * 检查类别
     */
    private String inspectionCategory;

    /**
     * 检查细分维度
     */
    private String inspectionSubDimension;

    /**
     * 检查内容
     */
    private String inspectionContent;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 检查方法
     */
    private String inspectionMethod;


    /**
     * 校验
     */
    public SafetyFireInspectionStandardExcel check() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(this.workStepCode)) {
            sb.append("作业环节编码为空，");
        }
        if (StringUtils.isBlank(this.inspectionItem)) {
            sb.append("检查项目为空，");
        }
        if (StringUtils.isBlank(this.inspectionObject)) {
            sb.append("检查对象为空，");
        }
        if (StringUtils.isBlank(this.inspectionCategory)) {
            sb.append("检查类别为空，");
        }
        if (StringUtils.isBlank(this.inspectionSubDimension)) {
            sb.append("检查细分维度为空，");
        }
        if (StringUtils.isBlank(this.inspectionContent)) {
            sb.append("检查内容为空；");
        }
        if (StringUtils.isBlank(this.riskLevel)) {
            sb.append("等级为空；");
        } else {
            List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
            if (!riskLevelList.contains(this.riskLevel)) {
                sb.append("等级输入不规范：要求").append(riskLevel).append(";");
            } else {
                this.riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(this.riskLevel).getCode();
            }
        }
        if (StringUtils.isBlank(this.inspectionMethod)) {
            sb.append("检查方法为空；");
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            sb.append("\r\n");
        }
        this.failInfo = sb.toString();
        return this;
    }

    public String generateKey() {
        return this.workStepCode + this.inspectionItem + this.inspectionObject + this.inspectionCategory + this.inspectionSubDimension + this.inspectionContent + this.riskLevel + this.inspectionMethod;
    }
}
