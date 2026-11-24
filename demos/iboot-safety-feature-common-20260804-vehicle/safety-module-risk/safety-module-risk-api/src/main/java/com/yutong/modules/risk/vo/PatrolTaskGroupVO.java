package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "待办任务分组统计VO", description = "按任务类型+岗位类型分组的待办任务统计")
public class PatrolTaskGroupVO {

    @ApiModelProperty("任务类型")
    private String taskType;

    @ApiModelProperty("任务类型名称")
    private String taskTypeName;

    @ApiModelProperty("岗位类型")
    private String postType;

    @ApiModelProperty("总任务数")
    private Integer totalCount;

    @ApiModelProperty("已完成数")
    private Integer completedCount;

    @ApiModelProperty("待完成数（执行中+超期未完成）")
    private Integer pendingCount;

    @ApiModelProperty("完成率（百分比）")
    private String rate;
}
