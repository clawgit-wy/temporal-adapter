package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@ApiModel(value="系统角色VO", description="系统角色")
public class SafetySysRoleOptionListVO {
    /**角色编码*/
    @ApiModelProperty(value = "角色主键")
    private Long id;
    /**角色编码*/
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**角色名*/
    @ApiModelProperty(value = "角色名")
    private String roleName;
}
