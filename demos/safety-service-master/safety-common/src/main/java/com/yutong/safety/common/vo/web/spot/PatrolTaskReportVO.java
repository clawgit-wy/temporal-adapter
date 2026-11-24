package com.yutong.safety.common.vo.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 治安巡查任务报表统计
 */
@Data
public class PatrolTaskReportVO {

    @ApiModelProperty("任务ID")
    private Integer planId;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("任务类型 1-治安巡查 2-业务验证")
    private Integer planType;
    @ApiModelProperty("类别ID")
    private Integer typeId;
    @ApiModelProperty("类别名称")
    private String typeName;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目名称")
    private String teamName;
    @ApiModelProperty("周期任务开始日期")
    private String taskBeginDate;
    @ApiModelProperty("周期任务结束日期")
    private String taskEndDate;
    @ApiModelProperty("当前周期数")
    private Integer curOrder;
    @ApiModelProperty("工厂名称")
    private String factoryName;
    @ApiModelProperty("次数")
    private Integer frequency;
    @ApiModelProperty("已检数量")
    private Integer checkNum;
    @ApiModelProperty("待检数量")
    private Integer unCheckNum;
    @ApiModelProperty("已检率")
    private Integer checkRate;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("任务ID")
    private Integer taskId;
    @ApiModelProperty("任务描述")
    private String planRemark;
    @ApiModelProperty("已巡检项")
    private List<PatrolRecordVO> recordList;
}
