package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: 位置信息流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_location_info_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_location_info_process对象", description="位置信息流程信息表")
public class YtsafetyLocationInfoProcess {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.Long id;
    /**模型主键*/
    @ApiModelProperty(value = "模型主键")
    private java.lang.String modelFlag;
    /**流程编码*/
    @ApiModelProperty(value = "流程编码")
    private java.lang.String flowCode;
    /**流程主题*/
    @ApiModelProperty(value = "流程主题")
    private java.lang.String flowSubject;
    /**厂区主键*/
    @ApiModelProperty(value = "厂区主键")
    private java.lang.Long factoryId;
    /**厂区名称*/
    @ApiModelProperty(value = "厂区名称")
    private java.lang.String factoryName;
    /**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
    private java.lang.String applyNo;
    /**申请员工名*/
    @Excel(name = "申请人登录账号", width = 15)
    @ApiModelProperty(value = "申请人登录账号")
    private String applyLogin;
    /**申请员工名*/
    @ApiModelProperty(value = "申请员工名")
    private java.lang.String applyName;
    /**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
    private java.lang.String applyDepartCode;
    /**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
    private java.lang.String applyDepartName;
    /**原因说明*/
    @ApiModelProperty(value = "原因说明")
    private java.lang.String applyReason;
    /**申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送")
    private java.lang.Integer changeType;
    /**提案审批人*/
    @ApiModelProperty(value = "提案审批人")
    private java.lang.String proposalApproval;
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private java.lang.String superApproval;
    /**上级审批人名称*/
    @ApiModelProperty(value = "上级审批人名称")
    private java.lang.String superApprovalName;
    /**批准审批人*/
    @ApiModelProperty(value = "批准审批人")
    private java.lang.String authorizeApproval;
    /**备案审批人*/
    @ApiModelProperty(value = "备案审批人")
    private java.lang.String recordApproval;
    /**是否需要超级管理员审批 0 否 1 是*/
    @ApiModelProperty(value = "是否需要超级管理员审批 0 否 1 是")
    private java.lang.Integer isSupper;
    /**是否保卫 0 否 1 是*/
    @ApiModelProperty(value = "是否保卫 0 否 1 是")
    private java.lang.Integer isGuard;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createUser;
    /**创建人名称*/
    @ApiModelProperty(value = "创建人名称")
    private java.lang.String createName;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateUser;
    /**更新人工号*/
    @ApiModelProperty(value = "更新人工号")
    private java.lang.String updateName;
    /**删除标识 0 正常 1 删除*/
    @ApiModelProperty(value = "删除标识 0 正常 1 删除")
    private java.lang.Integer delFlag;
    /**乐观锁*/
    @ApiModelProperty(value = "乐观锁")
    private java.lang.Integer version;
}
