package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("菜单权限树形实体")
public class MenuTree {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("是否叶子节点")
    private Boolean isLeaf;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("规则标识")
    private Integer ruleFlag;

    @ApiModelProperty("权限编码")
    private String code;

    @ApiModelProperty("层级")
    private Integer level;

    @ApiModelProperty("排序号")
    private Integer sort;

    @ApiModelProperty("分类 1-目录,2-页面菜单,3-按钮")
    private String cate;

    @ApiModelProperty("父ID")
    private String pid;

    @ApiModelProperty("权限访问路径")
    private String permUrl;

    private List<YtsafetySysUserDataRuleVO> userDataRuleVOS;

    @ApiModelProperty("子节点")
    private List<MenuTree> children;

    @ApiModelProperty("外部链接地址")
    private String externalUrl;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("组件名字")
    private String componentName;

    @ApiModelProperty(value = "是否显示")
    private String isShow;

    @ApiModelProperty("父ID")
    private String parentId;

    /**映射类*/
    @ApiModelProperty(value = "映射类")
    private String mappingClass;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("重定向")
    private String redirect;

    @ApiModelProperty("是否流程显示")
    private String processDisplay;

    @ApiModelProperty("是否缓存路由")
    private String keepAlive;

}
