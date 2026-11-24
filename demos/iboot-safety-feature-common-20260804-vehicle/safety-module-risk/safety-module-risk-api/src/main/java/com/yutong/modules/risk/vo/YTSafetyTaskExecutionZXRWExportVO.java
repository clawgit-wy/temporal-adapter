package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Data
@ApiModel(value="用户任务执行表VO", description="用户任务执行表VO")
public class YTSafetyTaskExecutionZXRWExportVO {

    private Long id;
    /**任务编号*/
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private String taskCode;
    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**任务描述*/
    @Excel(name = "任务描述", width = 15)
    @ApiModelProperty(value = "任务描述")
    private String taskDescription;

    /**执行用户姓名(单选)*/
    @Excel(name = "任务对象", width = 15)
    @ApiModelProperty(value = "执行用户姓名(单选)")
    private String execUserName;

    /**风险类别(风险类型字典表id)*/
    @Excel(name = "风险类型", width = 15)
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
    private String riskCategoryId;
    /**任务开始时间*/
    @Excel(name = "任务开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务开始时间")
    private Date taskStartTime;
    /**任务完成时间*/
    @Excel(name = "任务完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务完成时间")
    private Date taskCompleteTime;
    /**完成间隔时间*/
    @Excel(name = "任务截止时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任务截止时间")
    private Date endDate;
    /**任务状态(已完成、执行中、超期完成、超期未完成)*/
    @Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态(已完成、执行中、超期完成、超期未完成)")
    private String taskStatus;
//    /**是否异常(1是0否)*/
//    @Excel(name = "是否异常", width = 15)
//    @ApiModelProperty(value = "是否异常(1是0否)")
//    private Integer isAbnormal;
    /**附件图片id(文件服务回传id)*/
    @Excel(name = "任务照片", width = 15)
    @ApiModelProperty(value = "任务照片")
    private String attachmentImgId;

    /**附件图片id(文件服务回传id)*/
    @Excel(name = "任务附件", width = 15)
    @ApiModelProperty(value = "任务附件")
    private String attachmentId;

    @Excel(name = "详情Url", width = 15)
    @ApiModelProperty(value = "详情Url")
    private String detailUrl;

    List<YTSafetyFileInfo> attachmentImgList;

    List<YTSafetyFileInfo> attachmentList;


}
