package com.yutong.safety.core.dto.fireInspection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FireInspectionTaskInfoCountDto {
    /**
     * 主数据ID 任务详情数据ID
     */
    private Long inspectionTaskId;
    /**
     * 异常的数据量
     */
    private Integer errorCount = 0;
    /**
     * 整改完成数据量
     */
    private Integer rectificationCount = 0;

    @ApiModelProperty("未完成数据量")
    private Integer waitCount = 0;

    @ApiModelProperty("已完成数据量")
    private Integer doneCount = 0;

    @ApiModelProperty("总数")
    private Integer totalCount = 0;

}
