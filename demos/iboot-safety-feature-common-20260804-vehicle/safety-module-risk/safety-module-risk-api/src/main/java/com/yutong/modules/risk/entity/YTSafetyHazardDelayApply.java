package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 延期申请表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_delay_apply")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_delay_apply对象", description="延期申请表")
public class YTSafetyHazardDelayApply {

	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	@Excel(name = "隐患上报id", width = 15)
    @ApiModelProperty(value = "隐患上报id")
	private Long processId;
	@Excel(name = "申请人工号", width = 15)
    @ApiModelProperty(value = "申请人工号")
	private String applicantUserEmpNo;
	@Excel(name = "原截止完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "原截止完成时间")
	private Date originalDeadline;
	@Excel(name = "申请截止时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请截止时间")
	private Date applyDeadline;
	@Excel(name = "延期原因", width = 50)
    @ApiModelProperty(value = "延期原因")
	private String delayReason;
    @ApiModelProperty(value = "是否已审核：1-是")
    private Integer checkSign;
	@Excel(name = "申请处理结果", width = 15)
    @ApiModelProperty(value = "申请处理结果")
	private String applyResult;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
