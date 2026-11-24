package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 位置下拉VO
 */
@Data
public class LocationDropdownVO {

    @ApiModelProperty(value = "位置主键")
    private Long id;

    @ApiModelProperty(value = "位置编码")
    private String code;

    @ApiModelProperty(value = "位置名称")
    private String name;
}