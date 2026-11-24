package com.yutong.modules.risk.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
public class YtsafetyHomeworkStageProcessDTO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型主键*/
    @ApiModelProperty(value = "模型主键")
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
    /**申请类型  0 新增 1 变更新增 2 批量推送*/
    @ApiModelProperty(value = "申请类型  0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送")
    private Integer changeType;
    /**上级审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private String superApproval;
    /**上级审批人名称*/
    @ApiModelProperty(value = "上级审批人名称")
    private String superApprovalName;

    /**提案人 多个以“,”隔开*/
    @ApiModelProperty(value = "提案人 多个以“,”隔开")
    private String submitApproval;
    /**提案人名称*/
    @ApiModelProperty(value = "提案人名称")
    private String submitApprovalName;

    /**是否管理员提交 1是0否 管理员提交跳过审批流程*/
    @ApiModelProperty(value = "是否管理员提交 1是0否")
    private Integer adminSubmit;

    /**操作类型 0 草稿 1 提交*/
    @ApiModelProperty(value = "操作类型 0 草稿 1 提交")
    private Integer opType;

    /**明细*/
    @ApiModelProperty(value = "作业环节明细")
    private List<YtsafetyHomeworkStageProcessDetailsDTO> details;

}
