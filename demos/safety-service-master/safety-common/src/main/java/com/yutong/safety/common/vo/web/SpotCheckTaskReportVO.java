package com.yutong.safety.common.vo.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpotCheckTaskReportVO implements Serializable {

    @ApiModelProperty("期内任务ID")
    private Integer id;
    @ApiModelProperty("任务ID")
    private Integer planId;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("类型ID")
    private Integer deviceTypeId;
    @ApiModelProperty("类型")
    private String deviceType;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("工厂ID")
    private String factoryId;
    @ApiModelProperty("工厂")
    private String factoryName;
    @ApiModelProperty("当期点检开始日期")
    private String taskBeginDate;
    @ApiModelProperty("当期点检结束日期")
    private String taskEndDate;
    @ApiModelProperty("周期任务状态")
    private String status;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("期次")
    private Integer curOrder;
    @ApiModelProperty("周期")
    private String cycleDesc;
    @ApiModelProperty("项目设备数")
    private Integer deviceNum;
    @ApiModelProperty("周期内已点检设备数")
    private Integer checkNum;
    @ApiModelProperty("周期内未点检设备数")
    private Integer unCheckNum;
    @ApiModelProperty("点检完成率")
    private Float checkRate;
    @ApiModelProperty("周期内异常数量")
    private Integer exceptionNum;
    @ApiModelProperty("整改数量")
    private Integer reformNum;
    @ApiModelProperty("整改率")
    private Float reformRate;
    @ApiModelProperty("整改验证数量")
    private Integer validateNum;

    @ApiModelProperty("点检延期数量")
    private Integer spotDelayNum;
    @ApiModelProperty("点检延期率")
    private Float spotDelayRate;
    @ApiModelProperty("整改延期数量")
    private Integer reformDelayNum;
    @ApiModelProperty("整改延期率")
    private Float reformDelayRate;
}
