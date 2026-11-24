package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class YTSafetyHazardLedgerVO {

    @ApiModelProperty("主键")
    private String id;

    @Excel(name = "隐患编码", width = 12)
    @ApiModelProperty(value = "隐患编码")
    private String hazardCode;

    @ApiModelProperty("隐患类别：0-未分类，1-消防，2-治安")
    private Integer hazardCategory;

    @Excel(name = "隐患类型", width = 12)
    @ApiModelProperty("隐患类型文本")
    private String hazardCategoryText;

    @ApiModelProperty("问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
    private String problemSource;

    @Excel(name = "问题来源", width = 12)
    @ApiModelProperty("问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
    private String problemSourceText;

    @ApiModelProperty("问题类别")
    private String problemCategory;

    @Excel(name = "问题类别", width = 12)
    @ApiModelProperty("问题类别")
    private String problemCategoryText;

    @ApiModelProperty("问题等级：2-一般，3-较大，4-重大")
    private Integer problemLevel;

    @Excel(name = "隐患等级", width = 10)
    @ApiModelProperty("问题等级：2-一般，3-较大，4-重大")
    private String problemLevelText;

    @ApiModelProperty("所属厂区ID")
    private String plantId;

    @Excel(name = "所属厂区", width = 12)
    @ApiModelProperty("所属厂区名称")
    private String plantName;

    @ApiModelProperty("所属区域ID")
    private String areaId;

    @Excel(name = "所属区域", width = 12)
    @ApiModelProperty("所属区域名称")
    private String areaName;

    @ApiModelProperty("位置ID")
    private String locationId;

    @Excel(name = "位置", width = 15)
    @ApiModelProperty("位置名称")
    private String locationName;

    @Excel(name = "问题描述", width = 50)
    @ApiModelProperty("问题描述汇总")
    private String problemDescriptionSummary;

    @Excel(name = "问题图片", width = 50)
    @ApiModelProperty("问题描述相关图片")
    private String problemPhotosSummary;

    @ApiModelProperty("整改责任人工号")
    private String rectifyPersonEmpNo;

    @Excel(name = "整改责任人", width = 10)
    @ApiModelProperty("整改责任人姓名")
    private String rectifyPersonName;

    @ApiModelProperty("问题责任组织ID")
    private String orgId;

    @Excel(name = "问题责任组织", width = 15)
    @ApiModelProperty("问题责任组织名称")
    private String orgName;

    @Excel(name = "要求整改完成时间", width = 18, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("整改要求完成时间")
    private Date rectifyDeadline;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("整改延期完成时间")
    private Date rectifyDelayTime;

    @Excel(name = "事件处理过程及结果", width = 50)
    @ApiModelProperty("处理结果汇总")
    private String resultSummary;

    @Excel(name = "处理完成照片", width = 50)
    @ApiModelProperty("处理结果图片汇总")
    private String resultPhotosSummary;

    @Excel(name = "实际完成日期", width = 18, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("整改完成时间")
    private Date rectifyCompleteTime;

    @ApiModelProperty("抄送人工号（逗号分隔）")
    private String ccPersonEmpNo;

    @Excel(name = "问题抄送人", width = 15)
    @ApiModelProperty("抄送人姓名（逗号分隔）")
    private String ccPersonName;

    @Excel(name = "检查时间", width = 18, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("检查时间")
    private Date checkTime;

    @ApiModelProperty("是否当场处理：0-否，1-是")
    private Integer isImmediateHandle;

    @Excel(name = "是否当场处理", width = 10)
    @ApiModelProperty("是否当场处理文本")
    private String isImmediateHandleText;

    @Excel(name = "填报人", width = 10)
    @ApiModelProperty("填报人")
    private String applyName;

    @ApiModelProperty("状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审核，30-废弃")
    private Integer status;

    @Excel(name = "当前状态", width = 10)
    @ApiModelProperty("状态文本")
    private String statusText;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @Excel(name = "详情链接", width = 15)
    @ApiModelProperty(value = "详情链接")
    private String detailUrl;

    @ApiModelProperty("问题描述列表")
    private List<YTSafetyHazardDescriptionVO> descriptionList;

    @ApiModelProperty("整改结果列表")
    private List<YTSafetyHazardResultVO> resultList;
}
