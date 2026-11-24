package com.yutong.modules.user.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 系统角色列表查询query
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="系统角色列表查询query", description="系统角色")
public class SafetySysRoleQuery {

	/**角色名*/
	@Excel(name = "角色名", width = 15)
    @ApiModelProperty(value = "角色名")
	private String roleName;
	/**角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色*/
	@Excel(name = "角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色 ", width = 15)
    @ApiModelProperty(value = "角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色")
	private String roleType;
	/**角色编码*/
	@Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
	private String roleCode;
}
