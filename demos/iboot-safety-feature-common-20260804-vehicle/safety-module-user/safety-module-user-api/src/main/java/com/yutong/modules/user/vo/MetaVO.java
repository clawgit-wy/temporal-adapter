package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("菜单元信息")
public class MetaVO {

    @ApiModelProperty("是否keepAlive")
    private Boolean keepAlive;

    @ApiModelProperty("是否内外部链接")
    private Boolean internalOrExternal;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("组件名称")
    private String componentName;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("重定向")
    private String redirect;
}
