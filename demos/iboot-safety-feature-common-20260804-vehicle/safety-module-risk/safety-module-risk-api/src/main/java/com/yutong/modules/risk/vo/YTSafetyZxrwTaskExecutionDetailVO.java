package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class YTSafetyZxrwTaskExecutionDetailVO {

    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**任务类型(巡查、专项、定点)*/
    @Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型(巡查、专项、定点)")
    private String taskType;
    /**任务描述(任务要求)*/
    @Excel(name = "任务描述", width = 15)
    @ApiModelProperty(value = "任务描述(任务要求)")
    private String taskDescription;
    /**结束日期*/
    @Excel(name = "结束日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    @Excel(name = "下发范围", width = 15)
    @ApiModelProperty(value = "下发范围")
    private String execScope;

    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String taskStatus;


    @Excel(name = "是否附件必传", width = 15)
    @ApiModelProperty(value = "是否附件必传(0 否 1 是)")
    private String fileMust;

    @Excel(name = "是否图片必传", width = 15)
    @ApiModelProperty(value = "是否图片必传(0 否 1 是)")
    private String photoMust;

    @Excel(name = "模型id", width = 15)
    @ApiModelProperty(value = "模型id")
    private String modelFlag;

    @Excel(name = "流程编码", width = 15)
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Excel(name = "流程主题", width = 15)
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;

    /**下发审批*/
    @ApiModelProperty(value = "下发审批:0:部审批,1:发起人审批,2:消防员审批,3:自定义")
    private String issueApproval;

    /**审批人*/
    @ApiModelProperty(value = "审批人 多个逗号分割")
    private String approvalUser;

    /**审批人*/
    @ApiModelProperty(value = "审批人 多个逗号分割")
    private String approvalName;

    List<YTSafetyFileInfo> attachmentImgList;

    List<YTSafetyFileInfo> attachmentList;

    List<YTSafetyFileInfo> taskAttachmentImgList;

    List<YTSafetyFileInfo> taskAttachmentList;


    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;

    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;


}
