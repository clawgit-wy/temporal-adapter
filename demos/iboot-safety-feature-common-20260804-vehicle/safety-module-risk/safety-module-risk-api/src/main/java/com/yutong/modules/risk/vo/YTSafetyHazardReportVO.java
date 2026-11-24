package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */
@Data
@ApiModel(value="隐患上报表详情VO", description="隐患上报表")
public class YTSafetyHazardReportVO {

    @ApiModelProperty(value = "主键")
	private String id;
    @ApiModelProperty(value = "隐患编码")
    private String hazardCode;
    @ApiModelProperty(value = "模型主键")
    private String modelFlag;
    @ApiModelProperty(value = "流程主键")
    private String flowId;
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
    @ApiModelProperty(value = "上级审批人名称“,”隔开")
    private String reviewPersonEmpNo;

    @Excel(name = "用户任务执行id（不关联任务时默认为0）", width = 15)
    @ApiModelProperty(value = "用户任务执行id（不关联任务时默认为0）")
	private String taskAccountId;

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

    @ApiModelProperty(value = "所属组织code")
    private String orgCode;

	@ApiModelProperty(value = "问题责任组织名称")
	private String responsibleOrgNamePath;

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

    @Excel(name = "整改责任人", width = 15)
    @ApiModelProperty(value = "整改责任人")
	private String rectifyPersonName;

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

    @Excel(name = "抄送人", width = 15)
    @ApiModelProperty(value = "抄送人")
    private String ccPersonName;

	@Excel(name = "草稿状态：1-是，0-否", width = 15)
    @ApiModelProperty(value = "草稿状态：1-是，0-否")
	private Integer draftStatus;

	@Excel(name = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审批中，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审批中，30-废弃")
	private Integer status;

    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;

    @ApiModelProperty(value = "是否可提交部分整改：true-是,false-否")
    private Boolean isCanSubmitPartRectification;

    @ApiModelProperty(value = "是否可延期申请：true-是,false-否")
    private Boolean isCanDelayApply;

    @ApiModelProperty(value = "隐患问题描述")
    List<YTSafetyHazardDescriptionVO> descriptionList;

    @ApiModelProperty(value = "处理结果")
    List<YTSafetyHazardResultVO> resultList;

    @ApiModelProperty(value = "延期申请")
    List<YTSafetyHazardDelayApplyVO> delayApplyList;

    @ApiModelProperty(value = "隐患上报修改历史表")
    List<YTSafetyHazardReportHistoryVO> reportHistoryList;

    @ApiModelProperty(value = "匿名临时token")
    private String temporaryToken;

    @ApiModelProperty(value = "匿名临时LRToken")
    private String LRToken;
}
