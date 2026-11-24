package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class YTSafetyMyTaskInfoVO {

    private Long id;

    @Excel(name = "任务周期开始时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务开始时间")
    private Date startDate;
    /**任务完成时间*/
    @Excel(name = "任务周期结束时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务周期结束时间")
    private Date endDate;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "岗位验证类型 319：监控岗位上报，355：现场岗位上报，356：模拟岗位上报")
    private String postType;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务详情")
    private String taskDescription;

    @Excel(name = "任务完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务完成时间")
    private Date taskCompleteTime;

    @ApiModelProperty(value = "上报岗位id")
    private Long reportPostId;


}
