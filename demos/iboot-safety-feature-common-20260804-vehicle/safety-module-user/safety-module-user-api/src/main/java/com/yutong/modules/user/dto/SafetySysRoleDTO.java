package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="系统角色DTO", description="系统角色")
public class SafetySysRoleDTO {

    /**记录ID*/
    @ApiModelProperty(value = "记录ID")
    private Long id;
	/**角色名*/
    @ApiModelProperty(value = "角色名")
    @NotBlank(message = "角色名称必填")
    @Length(min = 1, max = 50, message = "输入角色名称不可超出50字符")
	private String roleName;
	/**角色描述*/
    @ApiModelProperty(value = "角色描述")
	private String roleDesc;
	/**角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色*/
    @ApiModelProperty(value = "角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色")
    @NotBlank(message = "角色类型必填")
    @Pattern(regexp = "^[0123]$", message = "角色类型只能填写 0、1、2、3")
	private String roleType;
}
