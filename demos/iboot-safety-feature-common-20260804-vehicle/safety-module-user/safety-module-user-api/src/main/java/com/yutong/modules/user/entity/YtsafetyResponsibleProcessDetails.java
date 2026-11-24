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
 * @Description: 安全责任人变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-06-22
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_responsible_process_details")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_responsible_process_details对象", description="安全责任人变更流程明细表")
public class YtsafetyResponsibleProcessDetails {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private Long processId;
	/**变更人员主键*/
    @ApiModelProperty(value = "变更人员主键")
	private Long changeUserId;
	/**员工号*/
    @ApiModelProperty(value = "员工号")
	private String changeEmpNo;
	/**用户名*/
    @ApiModelProperty(value = "用户名")
	private String changeEmpName;
	/**用户主键*/
    @ApiModelProperty(value = "用户主键")
	private Integer userId;
	/**用户名*/
    @ApiModelProperty(value = "用户名")
	private String empName;
	/**员工号*/
    @ApiModelProperty(value = "员工号")
	private String empNo;
    /**EIP登录名*/
    @ApiModelProperty(value = "EIP登录名")
    private String loginName;
	/**人员类型 0 非保卫 1 保卫人员*/
    @ApiModelProperty(value = "人员类型 0 非保卫 1 保卫人员")
	private Integer userType;
	/**有效期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期")
	private Date validPeriod;
	/**状态:0 正常 1 禁用*/
    @ApiModelProperty(value = "状态:0 正常 1 禁用")
	private Integer status;
	/**变更类型 0 新增 1 变更*/
    @ApiModelProperty(value = "变更类型 0 新增 1 变更")
	private Integer changeType;
	/**授权厂区*/
    @ApiModelProperty(value = "授权厂区")
	private String factoryId;
    @ApiModelProperty(value = "责任部门")
	private String departId;
	/**授权角色*/
    @ApiModelProperty(value = "授权角色")
	private String roleId;
	/**备注*/
    @ApiModelProperty(value = "备注")
	private String remark;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
    /**创建人名称*/
    @ApiModelProperty(value = "创建人名称")
    private String createName;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人工号")
	private String updateUser;
    /**修改人*/
    @ApiModelProperty(value = "修改人名称")
    private String updateName;
	/**删除标识*/
    @ApiModelProperty(value = "删除标识")
	private Integer delFlag;
	/**修改版本*/
    @ApiModelProperty(value = "修改版本")
	private Integer version;
}
