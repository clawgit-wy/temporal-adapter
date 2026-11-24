package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoProcessVO对象", description="位置信息表映射对象")
public class YtsafetyLocationInfoProcessVO {
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
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private java.lang.String superApproval;
    /**上级审批人名称*/
    @ApiModelProperty(value = "上级审批人名称")
    private java.lang.String superApprovalName;
    /**明细*/
    @ApiModelProperty(value = "明细")
    private List<YtsafetyLocationInfoProcessDetailsVO> details;
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
