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
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 在岗签到表
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_duty_checkin")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ytsafety_duty_checkin对象", description = "在岗签到表")
public class YTSafetyDutyCheckin {

    /**主键（雪花 ID）*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**厂区 ID*/
    @ApiModelProperty(value = "厂区 ID")
    private Long factoryId;

    /**位置 ID*/
    @ApiModelProperty(value = "位置 ID")
    private Long locationId;

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**人员类型*/
    @ApiModelProperty(value = "人员类型")
    private String personType;

    /**厂区*/
    @ApiModelProperty(value = "厂区")
    private String factory;

    /**姓名*/
    @ApiModelProperty(value = "姓名")
    private String empName;

    /**手机号*/
    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    /**岗位*/
    @ApiModelProperty(value = "岗位")
    private String position;

    /**位置*/
    @ApiModelProperty(value = "位置")
    private String location;

    /**部门班组*/
    @ApiModelProperty(value = "部门班组")
    private String departmentTeam;

    /**班次代码*/
    @ApiModelProperty(value = "班次代码")
    private String shiftCode;

    /**班次上班时间*/
    @ApiModelProperty(value = "班次上班时间")
    private String shiftStartTime;

    /**班次下班时间*/
    @ApiModelProperty(value = "班次下班时间")
    private String shiftEndTime;

    /**值班日期*/
    @ApiModelProperty(value = "值班日期")
    private String dutyDate;

    /**签到时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "签到时间（打卡上班时间）")
    private Date checkinTime;

    /**第三方打卡上班时间*/
    @ApiModelProperty(value = "第三方打卡上班时间")
    private String clockInTime;

    /**第三方打卡下班时间*/
    @ApiModelProperty(value = "第三方打卡下班时间")
    private String clockOutTime;

    /**岗位状态：0-未到岗 1-在岗*/
    @ApiModelProperty(value = "岗位状态：0-未到岗 1-在岗")
    private Integer dutyStatus;

    /**是否补签：0-否 1-是*/
    @ApiModelProperty(value = "是否补签：0-否 1-是")
    private Integer isMakeup;

    /**补岗原因*/
    @ApiModelProperty(value = "补岗原因")
    private String applyReason;

    /**申请时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    /**审批人工号*/
    @ApiModelProperty(value = "审批人工号")
    private String approveUser;

    /**审批备注*/
    @ApiModelProperty(value = "审批备注")
    private String approveRemark;

    /**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**修改人工号*/
    @ApiModelProperty(value = "修改人工号")
    private String updateUser;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**修改时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**删除标志：0-未删除 1-已删除*/
    @ApiModelProperty(value = "删除标志：0-未删除 1-已删除")
    private Integer delFlag;

    /**模型标识*/
    @ApiModelProperty(value = "模型标识")
    private String modelFlag;

    /**流程编码*/
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    /**流程主题*/
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;

    @ApiModelProperty(value = "是否可审批")
    @TableField(exist = false)
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    @TableField(exist = false)
    private Boolean isCanEdit;
}
