package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 作业环节流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_homework_stage_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_homework_stage_process对象", description="作业环节流程信息表")
public class YtsafetyHomeworkStageProcess {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型主键*/
    @Excel(name = "模型标识", width = 15)
    @ApiModelProperty(value = "模型标识")
    private String modelFlag;
    /**流程编码*/
    @Excel(name = "流程编码", width = 15)
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**流程主题*/
    @Excel(name = "流程主题", width = 15)
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    /**申请员工号*/
    @Excel(name = "申请员工号", width = 15)
    @ApiModelProperty(value = "申请员工号")
    private String applyNo;
    /**EIP用户名*/
    @Excel(name = "EIP用户名", width = 15)
    @ApiModelProperty(value = "EIP用户名")
    private String applyLogin;
    /**申请员工名*/
    @Excel(name = "申请员工名", width = 15)
    @ApiModelProperty(value = "申请员工名")
    private String applyName;
    /**所属部门编码*/
    @Excel(name = "所属部门编码", width = 15)
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    /**所属部门名称*/
    @Excel(name = "所属部门名称", width = 15)
    @ApiModelProperty(value = "所属部门名称")
    private String applyDepartName;
    /**申请人电话*/
    @Excel(name = "申请人电话", width = 15)
    @ApiModelProperty(value = "申请人电话")
    private String applyPhone;
    /**原因说明*/
    @Excel(name = "原因说明", width = 15)
    @ApiModelProperty(value = "原因说明")
    private String applyReason;
    /**申请类型 0 新增 1 变更*/
    @Excel(name = "申请类型 0 草稿 1 新增 2 变更", width = 15)
    @ApiModelProperty(value = "申请类型 0 草稿 1 新增 2 变更")
    private Integer changeType;
    /**上级审批人 多个以“,”隔开*/
    @Excel(name = "上级审批人 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private String superApproval;
    /**上级审批人名称*/
    @Excel(name = "上级审批人名称", width = 15)
    @ApiModelProperty(value = "上级审批人名称")
    private String superApprovalName;

    /**提案人 多个以“,”隔开*/
    @Excel(name = "提案人 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "提案人 多个以“,”隔开")
    private String submitApproval;
    /**提案人名称*/
    @Excel(name = "提案人名称", width = 15)
    @ApiModelProperty(value = "提案人名称")
    private String submitApprovalName;

    /**是否管理员提交 1是0否*/
    @Excel(name = "是否管理员提交 1是0否", width = 15)
    @ApiModelProperty(value = "是否管理员提交 1是0否")
    private Integer adminSubmit;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createUser;
    /**创建人名称*/
    @Excel(name = "创建人名称", width = 15)
    @ApiModelProperty(value = "创建人名称")
    private String createName;
    /**更新时间*/
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**更新人*/
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateUser;
    /**更新人工号*/
    @Excel(name = "更新人工号", width = 15)
    @ApiModelProperty(value = "更新人工号")
    private String updateName;
    /**删除标识 0 正常 1 删除*/
    @Excel(name = "删除标识 0 正常 1 删除", width = 15)
    @ApiModelProperty(value = "删除标识 0 正常 1 删除")
    private Integer delFlag;
    /**乐观锁*/
    @Excel(name = "乐观锁", width = 15)
    @ApiModelProperty(value = "乐观锁")
    private Integer version;

    /**所属厂区主键*/
    @ApiModelProperty(value = "所属厂区主键")
    @TableField(exist = false)
    private Long factoryId;
    /**厂区名称*/
    @ApiModelProperty(value = "厂区名称")
    @TableField(exist = false)
    private String factoryName;

}
