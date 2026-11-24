package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="部门带路径对象", description="部门带路径对象")
public class SafetyGroupInfoPathVO {
    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;
    /**编码*/
    @ApiModelProperty(value = "编码")
    private String code;
    /**名称*/
    @ApiModelProperty(value = "名称")
    private String name;
    /**上级组织路径名称*/
    @ApiModelProperty(value = "上级组织路径名称")
    private String orgNamePath;
}
