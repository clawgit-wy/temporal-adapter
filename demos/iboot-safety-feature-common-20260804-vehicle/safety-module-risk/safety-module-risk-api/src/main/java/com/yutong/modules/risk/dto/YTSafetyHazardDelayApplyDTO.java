package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 延期申请表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
public class YTSafetyHazardDelayApplyDTO {

    @NotNull(message = "隐患上报id不能为空")
    @ApiModelProperty(value = "隐患上报id")
	private Long hazardReportId;
    @ApiModelProperty(value = "申请人工号")
	private String applicantUserEmpNo;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "原截止完成时间")
	private Date originalDeadline;
    @NotNull(message = "申请截止时间不能为空")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请截止时间")
	private Date applyDeadline;
    @NotBlank(message = "延期原因不能为空")
    @ApiModelProperty(value = "延期原因")
	private String delayReason;
}
