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
 * @Description: 安全责任人映射表
 * @Author: jeecg-boot
 * @Date:   2026-08-07
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_responsible_mapping")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_responsible_mapping对象", description="安全责任人映射表")
public class YtsafetySysResponsibleMapping {
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**工厂主键*/
    @ApiModelProperty(value = "工厂主键")
	private Long factoryId;
    /**工厂名称*/
    @ApiModelProperty(value = "工厂名称")
    private String factoryName;
	/**部门主键*/
    @ApiModelProperty(value = "部门主键")
	private Long departId;
    /**部门名称*/
    @ApiModelProperty(value = "部门名称")
    private String departName;
	/**部门编码*/
    @ApiModelProperty(value = "部门编码")
	private String departCode;
	/**部门路径*/
    @ApiModelProperty(value = "部门路径")
	private String departPath;
    /**角色主键*/
    @ApiModelProperty(value = "角色主键")
    private Long roleId;
    /**角色编码*/
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**角色名称*/
    @ApiModelProperty(value = "角色名称")
    private String roleName;
	/**用户主键*/
    @ApiModelProperty(value = "用户主键")
	private Long userId;
	/**员工号*/
    @ApiModelProperty(value = "员工号")
	private String empNo;
	/**员工名称*/
    @ApiModelProperty(value = "员工名称")
	private String empName;
	/**登录账号*/
    @ApiModelProperty(value = "登录账号")
	private String userName;
	/**createTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
}
