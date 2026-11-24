package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_report")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_report对象", description="隐患上报表")
public class YTSafetyHazardReport {

	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
    @ApiModelProperty(value = "隐患编码")
    private String hazardCode;

    @ApiModelProperty(value = "模型主键")
    private String modelFlag;
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    @ApiModelProperty(value = "申请员工号")
    private String applyNo;
    @ApiModelProperty(value = "申请员工名")
    private String applyName;
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    @ApiModelProperty(value = "所属部门名称")
    private String applyDepartName;
    @ApiModelProperty(value = "上级审批人 多个以“,”隔开")
    private String superApproval;
    @ApiModelProperty(value = "上级审批人名称“,”隔开")
    private String superApprovalName;

    @ApiModelProperty(value = "审核人 多个以“,”隔开")
    private String reviewPersonEmpNo;
    @ApiModelProperty(value = "审核人姓名 多个以“,”隔开")
    private String reviewPersonEmpName;

    @ApiModelProperty(value = "备案传阅人 多个以“,”隔开")
    private String recordCirculationEmpNo;

    @Excel(name = "用户任务执行id（不关联任务时默认为0）", width = 15)
    @ApiModelProperty(value = "用户任务执行id（不关联任务时默认为0）")
	private Long taskAccountId;

	@Excel(name = "隐患类别：0-未分类，1-消防，2-治安", width = 15)
    @ApiModelProperty(value = "隐患类别：0-未分类，1-消防，2-治安")
	private Integer hazardCategory;

	@Excel(name = "问题来源", width = 15)
    @ApiModelProperty(value = "问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
	private String problemSource;

	@Excel(name = "检查名称", width = 15)
    @ApiModelProperty(value = "检查名称")
	private String checkName;

	@Excel(name = "检查时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "检查时间")
	private Date checkTime;

	@Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id")
	private Long plantId;

	@Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id")
	private Long areaId;

	@Excel(name = "所属组织id", width = 15)
    @ApiModelProperty(value = "所属组织id")
	private Long orgId;

    @Excel(name = "所属组织路径名称", width = 15)
    @ApiModelProperty(value = "所属组织路径名称")
	private String orgPathName;

	@Excel(name = "位置id", width = 15)
    @ApiModelProperty(value = "位置id")
	private Long locationId;

	@Excel(name = "问题类别", width = 15)
    @ApiModelProperty(value = "问题类别")
	private String problemCategory;

	@Excel(name = "问题等级", width = 15)
    @ApiModelProperty(value = "问题等级：2-一般，3-较大，4-重大")
	private Integer problemLevel;

	@Excel(name = "是否当场处理：0-否，1-是", width = 15)
    @ApiModelProperty(value = "是否当场处理：0-否，1-是")
	private Integer isImmediateHandle;

	@Excel(name = "整改责任人", width = 15)
    @ApiModelProperty(value = "整改责任人")
	private String rectifyPersonEmpNo;

    @ApiModelProperty(value = "整改责任人user")
	private String rectifyPersonEmpUser;

	@Excel(name = "整改要求完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "整改要求完成时间")
	private Date rectifyDeadline;

	@Excel(name = "整改延期完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "整改延期完成时间")
	private Date rectifyDelayTime;

	@Excel(name = "整改完成时间（可为空）", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "整改完成时间（可为空）")
	private Date rectifyCompleteTime;

	@Excel(name = "抄送人", width = 15)
    @ApiModelProperty(value = "抄送人")
	private String ccPersonEmpNo;

    @ApiModelProperty(value = "抄送人user")
    private String ccPersonEmpUser;

    @ApiModelProperty(value = "抄送人user")
    private String verifyOneUser;

    @ApiModelProperty(value = "抄送人user")
    private String verifyTwoUser;

    @ApiModelProperty(value = "延期审核人user")
    private String delayCheckUser;

	@Excel(name = "草稿状态：1-是，0-否", width = 15)
    @ApiModelProperty(value = "草稿状态：1-是，0-否")
	private Integer draftStatus;

	@Excel(name = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审批中，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审批中，30-废弃")
	private Integer status;

    @Excel(name = "提交类型:1-整改结果提交, 2-延期申请提交", width = 15)
    @ApiModelProperty(value = "提交类型:1-整改结果提交, 2-延期申请提交")
    private Integer submitType;

    @Excel(name = "整改类型：all-全部；part-部分", width = 15)
    @ApiModelProperty(value = "整改类型：all-全部；part-部分")
    private String handlerType;

    @Excel(name = "整改类型：all-全部；part-部分", width = 15)
    @ApiModelProperty(value = "整改类型：all-全部；part-部分")
    private Long currentManageId;

	@Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
	private String createName;

	@Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
	private String createUser;

	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;

	@Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
	private String updateName;

	@Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
	private String updateUser;

	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;

	@Excel(name = "删除标记：0-未删除，1-已删除", width = 15)
    @ApiModelProperty(value = "删除标记：0-未删除，1-已删除")
	private Integer delFlag;

	@Excel(name = "修改版本（乐观锁）", width = 15)
    @ApiModelProperty(value = "修改版本（乐观锁）")
	private Integer version;
}
