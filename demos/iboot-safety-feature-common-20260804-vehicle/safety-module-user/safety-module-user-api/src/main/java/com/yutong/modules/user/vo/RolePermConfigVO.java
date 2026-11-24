package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 角色权限配置返回结果
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@ApiModel(value = "角色权限配置结果", description = "菜单树权限配置返回结果")
public class RolePermConfigVO {

    @ApiModelProperty("全部菜单树")
    private List<MenuTree> treeList;

    @ApiModelProperty("角色已授权的权限ID列表")
    private List<String> ids;
}