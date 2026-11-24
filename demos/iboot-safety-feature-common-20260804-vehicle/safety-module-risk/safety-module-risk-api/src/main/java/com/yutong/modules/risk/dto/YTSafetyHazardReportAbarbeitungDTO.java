package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class YTSafetyHazardReportAbarbeitungDTO {

    @NotNull(message = "隐患主键不能为空")
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "整改数据")
    private YTSafetyHazardResultDTO abarbeitungResult;

}
