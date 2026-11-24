package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 用户任务执行表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Data
@ApiModel(value="ytsafety_task_execution对象", description="用户任务执行表")
public class YTSafetyTaskExecutionDTO {

	/**任务编号*/
	@Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
	private String taskCode;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
	private String taskName;

	/**执行用户姓名(单选)*/
	@Excel(name = "执行用户姓名", width = 15)
    @ApiModelProperty(value = "执行用户姓名(单选)")
	private String execUserName;

	/**风险类别(风险类型字典表id)*/
	@Excel(name = "风险类别", width = 15)
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
	private String riskCategoryId;
	/**任务开始时间*/
	@Excel(name = "任务开始时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务开始时间")
	private Date taskStartTime;
	/**任务完成时间*/
	@Excel(name = "任务完成时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dds")
    @ApiModelProperty(value = "任务完成时间")
        private Date taskCompleteTime;
	/**完成间隔时间*/
	@Excel(name = "完成间隔时间", width = 15)
    @ApiModelProperty(value = "完成间隔时间")
	private Integer completeIntervalStart;

    @Excel(name = "完成间隔时间", width = 15)
    @ApiModelProperty(value = "完成间隔时间")
    private Integer completeIntervalEnd;
	/**任务状态(已完成、执行中、超期完成、超期未完成)*/
	@Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态(已完成、执行中、超期完成、超期未完成)")
	private String taskStatus;
	/**是否异常(1是0否)*/
	@Excel(name = "是否异常", width = 15)
    @ApiModelProperty(value = "是否异常(1是0否)")
	private Integer isAbnormal;

	/**任务开始时间-开始*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "任务开始时间-开始")
	private Date taskStartTimeStart;

	/**任务开始时间-结束*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "任务开始时间-结束")
	private Date taskStartTimeEnd;

	/**任务完成时间-开始*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "任务完成时间-开始")
	private Date taskCompleteTimeStart;

	/**任务完成时间-结束*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "任务完成时间-结束")
	private Date taskCompleteTimeEnd;

    /**任务完成时间-开始*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务完成时间-开始")
    private Date endDateStart;

    /**任务完成时间-结束*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务完成时间-结束")
    private Date endDateEnd;

    //  查询条件
    /**页码*/
    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    /**每页条数*/
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;

    @Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private String type;

    private String selections;

    private String taskDescription;

}
