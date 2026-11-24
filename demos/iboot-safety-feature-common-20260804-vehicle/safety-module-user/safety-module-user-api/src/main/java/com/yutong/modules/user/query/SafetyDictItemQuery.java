package com.yutong.modules.user.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyDictItemQuery {

    @ApiModelProperty(value = "bigint")
    private Long id;

    @ApiModelProperty(value = "字典id")
    private Long supperId;

    @ApiModelProperty(value = "字典子项目名称")
    private String itemName;

    @ApiModelProperty(value = "类型，必传。字典:dict,字段:field")
    private String type;

    @ApiModelProperty(value = "字典编码")
    private String supperCode;

    @ApiModelProperty("上级字典项id")
    private Integer pid;
}
