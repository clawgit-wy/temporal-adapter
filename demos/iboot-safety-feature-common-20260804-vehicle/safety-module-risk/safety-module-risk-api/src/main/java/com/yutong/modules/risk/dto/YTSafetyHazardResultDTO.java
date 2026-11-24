package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Description: 隐患处理结果表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
public class YTSafetyHazardResultDTO {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "处理完成时间不能为空")
    @ApiModelProperty(value = "处理完成时间")
	private Date handleCompleteTime;
    @ApiModelProperty(value = "处理措施")
	private String handleMeasure;
    @ApiModelProperty(value = "附件图片id")
	private String attachmentIds;
    @ApiModelProperty(value = "处理人")
    private String handlerUserEmpNo;
    @NotBlank(message = "整改类型不能为空")
    @ApiModelProperty(value = "整改类型：all-全部整改，part-部分整改")
	private String handlerType;
}
