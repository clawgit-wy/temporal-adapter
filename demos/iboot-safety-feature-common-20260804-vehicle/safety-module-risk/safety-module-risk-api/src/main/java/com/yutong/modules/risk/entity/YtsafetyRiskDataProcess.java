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
 * @Description: 风险数据流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_risk_data_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_risk_data_process对象", description="风险数据流程信息表")
public class YtsafetyRiskDataProcess {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
    @ApiModelProperty(value = "申请人登录账号")
    private String applyLogin;
	/**申请员工名*/
    @ApiModelProperty(value = "申请员工名")
	private String applyName;
	/**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
	private String applyDepartCode;
	/**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
	private String applyDepartName;
    /**所属厂区主键*/
    @ApiModelProperty(value = "所属厂区主键")
    private Long factoryId;
    /**厂区名称*/
    @ApiModelProperty(value = "厂区名称")
    private String factoryName;
	/**原因说明*/
	@Excel(name = "原因说明", width = 15)
    @ApiModelProperty(value = "原因说明")
	private String applyReason;
    /**申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送")
    private Integer changeType;
    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    /**风险类型*/
    @ApiModelProperty(value = "风险类型主键")
    private Long riskTypeId;
    /**风险类型*/
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    /**提案审批人*/
    @ApiModelProperty(value = "提案审批人")
    private java.lang.String proposalApproval;
	/**上级审批人 多个以“,”隔开*/
	@Excel(name = "上级审批人 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
	private String superApproval;
	/**上级审批人名称*/
	@Excel(name = "上级审批人名称", width = 15)
    @ApiModelProperty(value = "上级审批人名称")
	private String superApprovalName;
    /**批准审批人*/
    @ApiModelProperty(value = "批准审批人")
    private java.lang.String authorizeApproval;
    /**备案审批人*/
    @ApiModelProperty(value = "备案审批人")
    private java.lang.String recordApproval;
    /**是否超管 0 否 1 是*/
    @ApiModelProperty(value = "是否超管 0 否 1 是")
    private Integer isSupper;
    /**是否审批 0 否 1 是*/
    @ApiModelProperty(value = "是否审批 0 否 1 是")
    private Integer isApprove;
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
}
