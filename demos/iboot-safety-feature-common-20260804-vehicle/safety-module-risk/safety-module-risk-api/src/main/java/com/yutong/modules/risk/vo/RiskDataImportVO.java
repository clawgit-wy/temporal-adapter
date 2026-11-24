package com.yutong.modules.risk.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.excel.core.BaseImportData;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 风险数据Excel导入模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RiskDataImportVO extends BaseImportData {

    @Excel(name = "风险编码")
    private String riskCode;

    @Excel(name = "位置名称")
    private String locationName;

    @Excel(name = "风险识别维度名称")
    private String riskIdentifyName;

    @Excel(name = "作业环节编码")
    private String jobStepCode;

    @Excel(name = "作业环节名称")
    private String jobStepName;

    @Excel(name = "检查对象")
    private String checkObject;

    @Excel(name = "风险描述")
    private String riskDescription;

    @Excel(name = "风险可能性名称")
    private String possibilityName;

    @Excel(name = "后果严重性名称")
    private String severityName;

}
