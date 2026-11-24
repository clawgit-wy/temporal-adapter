package com.yutong.modules.risk.vo;

import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDetailsDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: 作业环节流程信息VO
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetyHomeworkStageProcessVO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型主键*/
    @ApiModelProperty(value = "模型主键")
    private Long modelId;
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
    @ApiModelProperty(value = "申请类型 0 新增 1 变更 2 批量推送")
    private Integer changeType;
    /**上级审批人 多个以","隔开*/
    @ApiModelProperty(value = "上级审批人 多个以','隔开")
    private String superApproval;
    /**上级审批人名称*/
    @ApiModelProperty(value = "上级审批人名称")
    private String superApprovalName;
    /**是否管理员提交*/
    @ApiModelProperty(value = "是否管理员提交")
    private Integer adminSubmit;
    /**明细*/
    @ApiModelProperty(value = "明细")
    private List<YtsafetyHomeworkStageVO> details;
    /**系统标识*/
    @ApiModelProperty(value = "系统标识")
    private String sysId;
    /**表单主键*/
    @ApiModelProperty(value = "表单主键")
    private String formId;
    /**是否可审批*/
    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;
    /**是否可修改*/
    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;
    /**流程状态*/
    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;
}
