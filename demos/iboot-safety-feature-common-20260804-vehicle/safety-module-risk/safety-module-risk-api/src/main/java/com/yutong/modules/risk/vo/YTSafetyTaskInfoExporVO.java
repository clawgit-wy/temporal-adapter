package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 任务台账表VO
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Data
@ApiModel(value="任务台账表VO", description="任务台账表")
public class YTSafetyTaskInfoExporVO {

    private Long id;
    /**任务编号*/
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private String taskCode;
    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**任务类型(巡查、专项、定点)*/
    @Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型(巡查、专项、定点)")
    private String taskType;
    @Excel(name = "任务对象", width = 15)
    @ApiModelProperty(value = "任务对象")
    private String personnelName;
    /**风险类别(风险类型字典表id)*/
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
    private String riskCategoryId;

    @Excel(name = "风险类别", width = 15)
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
    private String riskCategory;
    /**任务描述(任务要求)*/
    @Excel(name = "任务描述", width = 15)
    @ApiModelProperty(value = "任务描述(任务要求)")
    private String taskDescription;
    /**开始日期*/
    @Excel(name = "开始日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**结束日期*/
    @Excel(name = "结束日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    /**检查周期(每周一次/每月一次)*/
    @Excel(name = "检查周期", width = 15)
    @ApiModelProperty(value = "检查周期(每周一次/每月一次)")
    private String inspectionCycle;
    /**任务执行范围(电脑:1、手机:2)*/
    @Excel(name = "任务执行范围", width = 15)
    @ApiModelProperty(value = "任务执行范围(电脑:1、手机:2)")
    private String execScope;
    /**任务状态(待执行、执行中、已过期、已休眠)*/
    @Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态(待执行、执行中、已过期、已休眠)")
    private String taskStatus;

    /**休眠日期(休眠状态下展示)*/
    @Excel(name = "休眠日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "休眠日期(休眠状态下展示)")
    private Date dormantDate;
    /**创建人姓名*/
    @Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
    private String createName;

    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**修改人姓名*/
    @Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
    private String updateName;

    /**修改时间*/
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @Excel(name = "详情链接", width = 15)
    @ApiModelProperty(value = "详情链接")
    private String detailUrl;


    @ApiModelProperty(value = "岗位类型(1:监控岗位 2:现场岗位 3:模拟岗位)")
    private String postType;

    @ApiModelProperty(value = "下发类型")
    private String issueType;

    @ApiModelProperty(value = "任务对象工号")
    private String personnelId;

    @ApiModelProperty(value = "岗位code")
    private String personnelPostId;

    @ApiModelProperty(value = "自定义人员姓名")
    private String responsibleEmpNos;

    @ApiModelProperty(value = "自定义人员工号")
    private String responsibleEmpNames;

    @ApiModelProperty(value = "自定义人员工号")
    private String personnelPostName;
}
