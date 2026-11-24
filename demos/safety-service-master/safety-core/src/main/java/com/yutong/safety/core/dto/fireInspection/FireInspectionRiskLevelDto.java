package com.yutong.safety.core.dto.fireInspection;

import lombok.Data;

@Data
public class FireInspectionRiskLevelDto {
    /**
     * 编码
     */
    private int code;
    /**
     * 风险等级简称
     */
    private String riskLevel;
    /**
     * 风险等级详情
     */
    private String riskLevelDetail;
    /**
     * 检查级别
     */
    private String inspectionLevel;
    /**
     * 检查周期
     */
    private String inspectionCycle;
}
