package com.yutong.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_sys_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_sys_role对象", description="系统角色")
public class SafetySysRole {
    
	/**记录ID*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "记录ID")
	private Long id;
	/**角色名*/
	@Excel(name = "角色名", width = 15)
    @ApiModelProperty(value = "角色名")
	private String roleName;
	/**角色描述*/
	@Excel(name = "角色描述", width = 15)
    @ApiModelProperty(value = "角色描述")
	private String roleDesc;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
    @ApiModelProperty(value = "操作人")
	private String lastUser;
	/**操作时间*/
	@Excel(name = "操作时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
	private Date lastTime;
	/**有效状态（0正常1删除）*/
	@Excel(name = "有效状态（0正常1删除）", width = 15)
    @ApiModelProperty(value = "有效状态（0正常1删除）")
	private String deleteFalg;
	/**角色类型：0 非安保角色 1 安保角色 2 系统角色*/
	@Excel(name = "角色类型：0 非安保角色 1 安保角色 2 系统角色", width = 15)
    @ApiModelProperty(value = "角色类型：0 非安保角色 1 安保角色 2 系统角色 ")
	private String roleType;
	/**角色编码*/
	@Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
	private String roleCode;
}
