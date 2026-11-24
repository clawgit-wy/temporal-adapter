package com.yutong.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("菜单节点")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuNodeVO {

    @ApiModelProperty("重定向路径")
    private String redirect;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("路由标识 0-菜单 1-目录")
    private String route;

    @ApiModelProperty("菜单元信息")
    private MetaVO meta;

    @ApiModelProperty("菜单名称(用于路由)")
    private String name;

    @ApiModelProperty("菜单ID")
    private String id;

    @ApiModelProperty("子菜单")
    private List<MenuNodeVO> children;

    private String isShow;
}
