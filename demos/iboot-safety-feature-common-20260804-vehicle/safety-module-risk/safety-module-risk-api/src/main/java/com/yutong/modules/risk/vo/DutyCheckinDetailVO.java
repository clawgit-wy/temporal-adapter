package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 在岗签到详情 VO
 * @Author: jeecg-boot
 * @Date: 2026-08-05
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "在岗签到详情", description = "在岗签到详情（含流程信息）")
public class DutyCheckinDetailVO {

    // ==================== YTSafetyDutyCheckin 字段 ====================

    /** 主键（雪花 ID） */
    @ApiModelProperty(value = "主键")
    private Long id;

    /** 厂区 ID */
    @ApiModelProperty(value = "厂区 ID")
    private Long factoryId;

    /** 员工号 */
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /** 姓名 */
    @ApiModelProperty(value = "姓名")
    private String empName;

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    /** 人员类型 */
    @ApiModelProperty(value = "人员类型")
    private String personType;

    /** 岗位 */
    @ApiModelProperty(value = "岗位")
    private String position;

    /** 厂区 */
    @ApiModelProperty(value = "厂区")
    private String factory;

    /** 位置 */
    @ApiModelProperty(value = "位置")
    private String location;

    /** 部门班组 */
    @ApiModelProperty(value = "部门班组")
    private String departmentTeam;

    /** 班次代码 */
    @ApiModelProperty(value = "班次代码")
    private String shiftCode;

    /** 值班日期 */
    @ApiModelProperty(value = "值班日期")
    private String dutyDate;

    /** 签到时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "签到时间")
    private Date checkinTime;

    /** 岗位状态：0-未到岗 1-在岗 */
    @ApiModelProperty(value = "岗位状态：0-未到岗 1-在岗")
    private Integer dutyStatus;

    /** 是否补签：0-否 1-是 */
    @ApiModelProperty(value = "是否补签：0-否 1-是")
    private Integer isMakeup;

    /** 补岗原因 */
    @ApiModelProperty(value = "补岗原因")
    private String applyReason;

    /** 申请时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    /** 审批人工号 */
    @ApiModelProperty(value = "审批人工号")
    private String approveUser;

    /** 审批备注 */
    @ApiModelProperty(value = "审批备注")
    private String approveRemark;

    /** 创建人工号 */
    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    /** 创建人 */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 修改人工号 */
    @ApiModelProperty(value = "修改人工号")
    private String updateUser;

    /** 修改人 */
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /** 修改时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /** 删除标志：0-未删除 1-已删除 */
    @ApiModelProperty(value = "删除标志：0-未删除 1-已删除")
    private Integer delFlag;

    /** 模型标识 */
    @ApiModelProperty(value = "模型标识")
    private String modelFlag;

    /** 流程编码 */
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    /** 流程主题 */
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;

    // ==================== BmpProcessDetailsVO 流程字段 ====================

    @ApiModelProperty(value = "流程主键")
    private String flowId;

    @ApiModelProperty(value = "任务主键")
    private String taskId;

    @ApiModelProperty(value = "系统标识")
    private String sysId;

    @ApiModelProperty(value = "表单主键")
    private String formId;

    @ApiModelProperty(value = "表单详情")
    private String formDetail;

    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;

    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;

    @ApiModelProperty(value = "流程申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyStartTime;

    @ApiModelProperty(value = "匿名临时token")
    private String temporaryToken;

    @ApiModelProperty(value = "匿名临时LRToken")
    private String LRToken;
}
