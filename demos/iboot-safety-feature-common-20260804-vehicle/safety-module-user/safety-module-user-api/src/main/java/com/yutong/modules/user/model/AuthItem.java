package com.yutong.modules.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("权限项")
public class AuthItem {

    @ApiModelProperty("权限标识")
    private String action;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("类型 1-按钮/表单访问权限 0-菜单权限")
    private String type;

    @ApiModelProperty("按钮权限状态(0无效1有效)")
    private String status;
}
