package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "隐患批量提交", description = "隐患批量提交")
public class YTSafetyHazardBatchSubmissionDTO {

    @ApiModelProperty("批量提交的数据id")
    private List<String> ids;
}
