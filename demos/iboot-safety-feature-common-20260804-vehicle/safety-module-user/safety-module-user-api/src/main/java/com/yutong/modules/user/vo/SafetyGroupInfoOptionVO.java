package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyGroupInfoOptionVO {
    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;
    /**编码*/
    @ApiModelProperty(value = "编码")
    private String code;
    private String orgNamePath;

}
