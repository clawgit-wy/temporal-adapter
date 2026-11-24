package com.yutong.modules.user.vo;

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
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人流程映射表", description="安全责任人流程信息表")
public class YtsafetyResponsibleProcessVO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型标识*/
    @ApiModelProperty(value = "模型标识")
    private String modelFlag;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String flowName;
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
    /**申请开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请开始时间")
    private Date applyStartTime;
    /**处理时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date handlerDate;
    /**申请结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请结束时间")
    private Date applyEndTime;
    /**申请类型 0 新增 1 变更*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更")
    private Integer changeType;
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private String superApproval;
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人名称“,”隔开")
    private String superApprovalName;
    /**任务主键*/
    @ApiModelProperty(value = "任务主键")
    private String taskId;
    /**流程节点ID*/
    @ApiModelProperty(value = "流程节点ID")
    private String nodeId;
    /**流程节点名称*/
    @ApiModelProperty(value = "流程节点名称")
    private String nodeName;
    /**变更明细*/
    @ApiModelProperty(value = "变更明细")
    private List<YtsafetyResponsibleProcessDetailsVO> details;
    /**上级人员列表*/
    @ApiModelProperty(value = "上级人员列表")
    private List<SafetyEmployeeMessageVO> superApproves;

    @ApiModelProperty(value = "系统标识")
    private String sysId;

    @ApiModelProperty(value = "表单主键")
    private String formId;

    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;

    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;
}
