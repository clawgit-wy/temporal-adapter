package com.yutong.modules.risk.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetyRiskDataDTO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**模型标识*/
    @ApiModelProperty(value = "模型标识")
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
    /**原因说明*/
    @ApiModelProperty(value = "原因说明")
    private String applyReason;
    /**申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送")
    private Integer changeType;
    /**上级审批人 多个以","隔开*/
    @ApiModelProperty(value = "上级审批人 多个以','隔开")
    private String superApproval;
    /**上级审批人名称*/
    @ApiModelProperty(value = "上级审批人名称")
    private String superApprovalName;
    /**操作类型 0 草稿 1 提交*/
    @ApiModelProperty(value = "操作类型 0 草稿 1 提交")
    private Integer opType;
    /**明细*/
    @ApiModelProperty(value = "明细")
    private List<YtsafetyRiskDataDetailsDTO> details;

}
