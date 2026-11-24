package com.yutong.modules.user.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * @Description: 安全责任人流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-22
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_responsible_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_responsible_process对象", description="安全责任人流程信息表")
public class YtsafetyResponsibleProcess {
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
    /**流程标识*/
    @ApiModelProperty(value = "流程标识")
    private String modelFlag;
    /**流程编码*/
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
	/**流程主题*/
    @ApiModelProperty(value = "流程主题")
	private String flowSubject;
	/**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
	private String applyNo;
	/**申请员工名*/
    @ApiModelProperty(value = "申请员工名")
	private String applyName;
	/**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
	private String applyDepartCode;
	/**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
	private String applyDepartName;
	/**申请人电话*/
    @ApiModelProperty(value = "申请人电话")
	private String applyPhone;
	/**原因说明*/
    @ApiModelProperty(value = "原因说明")
	private String applyReason;
	/**申请类型 0 新增 1 变更*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更")
	private Integer changeType;
	/**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
	private String superApproval;
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人名称“,”隔开")
    private String superApprovalName;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建员工号")
	private String createUser;
    /**创建人*/
    @ApiModelProperty(value = "创建人名称")
    private String createName;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**更新人*/
    @ApiModelProperty(value = "更新员工号")
	private String updateUser;
    /**更新人*/
    @ApiModelProperty(value = "更新人名称")
    private String updateName;
	/**删除标识 0 正常 1 删除*/
    @ApiModelProperty(value = "删除标识 0 正常 1 删除")
	private Integer delFlag;
	/**乐观锁*/
    @ApiModelProperty(value = "乐观锁")
	private Integer version;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细")
    private List<YtsafetyResponsibleProcessDetails> details;
}
