package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人流程变更新增对象", description="安全责任人流程信息表")
public class YtsafetyResponsibleProcessDTO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型主键*/
    @NotNull
    @ApiModelProperty(value = "模型主键")
    private String modelFlag;
    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String flowName;
    /**流程主题*/
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    /**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
    private String applyNo;
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
    /**操作类型 0 草稿 1 创建流程 2 创建并启用流程*/
    @ApiModelProperty(value = "操作类型 0 草稿 1 创建并启用流程")
    private Integer opType;
    /**变更明细*/
    @ApiModelProperty(value = "变更明细")
    private List<YtsafetyResponsibleProcessDetailsDTO> details;
}
