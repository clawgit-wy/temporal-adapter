package com.yutong.safety.core.dto.fireInspection;

import lombok.Data;

/**
 * 定时任务变更整改信息的实体
 */
@Data
public class FireInspectionRectificationResultDto {
    /**
     * 数据行ID
     */
    private Long id;
    /**
     * 明细表数据行 ID
     */
    private Long detailId;
    /**
     * 检查结果
     */
    private String inspectionResult;
    /**
     * 整改结果
     */
    private String rectificationResult;
    /**
     * 流程ID
     */
    private String processId;
}
