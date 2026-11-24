package com.yutong.safety.common.vo.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 设备点检代办VO
 * @author gwxiexj
 * @since 20240-05-20 11:03
 */
@Data
public class SpotCheckToDoVO implements Serializable {
    @ApiModelProperty("设备类型ID")
    private Integer typeId;
    @ApiModelProperty("设备类型")
    private String typeName;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("项目设备总数")
    private Integer deviceNum;
    @ApiModelProperty("已点检数")
    private Integer checkNum;
    @ApiModelProperty("未点检数")
    private Integer unCheckNum;
    @ApiModelProperty("任务开始时间")
    private String taskBeginDate;
    @ApiModelProperty("任务结束时间")
    private String taskEndDate;
    @ApiModelProperty("完成率")
    private String checkRate;
    @ApiModelProperty("长期任务ID")
    private Integer planId;
    @ApiModelProperty("子列表")
    List<SpotCheckToDoVO> subList;
}
