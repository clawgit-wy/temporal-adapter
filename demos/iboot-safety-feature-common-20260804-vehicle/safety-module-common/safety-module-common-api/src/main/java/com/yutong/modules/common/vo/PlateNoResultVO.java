package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "车牌号识别结果", description = "车牌号OCR识别结果")
public class PlateNoResultVO {

    @ApiModelProperty(value = "车牌号")
    private String result;
}
