package com.yutong.modules.user.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 账号角色关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_sys_user_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_sys_user_role对象", description="账号角色关联表")
public class SafetySysUserRole {
    
	/**账号ID*/
	@Excel(name = "账号ID", width = 15)
    @ApiModelProperty(value = "账号ID")
	private Long userId;
	/**角色ID*/
	@Excel(name = "角色ID", width = 15)
    @ApiModelProperty(value = "角色ID")
	private Long roleId;
	/**状态 0 启用 1 禁用*/
	@Excel(name = "状态 0 启用 1 禁用", width = 15)
    @ApiModelProperty(value = "状态 0 启用 1 禁用")
	private Integer status;
	/**角色类型：0系统角色 1 安保角色 2 非安保角色*/
	@Excel(name = "角色类型：0系统角色 1 安保角色 2 非安保角色", width = 15)
    @ApiModelProperty(value = "角色类型：0系统角色 1 安保角色 2 非安保角色")
	private String roleType;
    /**角色编码*/
    @Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**创建时间*/
    @Excel(name = "创建时间", width = 15)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**状态：0 - 启用 1 - 删除*/
    @Excel(name = "状态：0 - 启用 1 - 删除", width = 15)
    @ApiModelProperty(value = "状态：0 - 启用 1 - 删除")
    private Integer delFlag;
}
