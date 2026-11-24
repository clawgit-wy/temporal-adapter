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
 * @Description: 作业环节变更明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_homework_stage_process_details")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_homework_stage_process_details对象", description="作业环节变更明细表")
public class YtsafetyHomeworkStageProcessDetails {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**系统流程主键*/
    @Excel(name = "系统流程主键", width = 15)
    @ApiModelProperty(value = "系统流程主键")
    private Long processId;
    /**作业环节变更主键ID*/
    @Excel(name = "作业环节变更主键ID", width = 15)
    @ApiModelProperty(value = "作业环节变更主键ID")
    private Long changeId;
    /**作业环节名称*/
    @Excel(name = "作业环节名称", width = 15)
    @ApiModelProperty(value = "作业环节名称")
    private String linkName;
    /**作业环节编码*/
    @Excel(name = "作业环节编码", width = 15)
    @ApiModelProperty(value = "作业环节编码")
    private String linkCode;
    /**所属厂区id*/
    @Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id")
    private Long factoryId;
    /**所属厂区编码*/
    @Excel(name = "所属厂区编码", width = 15)
    @ApiModelProperty(value = "所属厂区编码")
    private String factoryCode;
    /**所属厂区名称*/
    @Excel(name = "所属厂区名称", width = 15)
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;
    /**所属区域id*/
    @Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id")
    private Long areaId;
    /**所属区域编码*/
    @Excel(name = "所属区域编码", width = 15)
    @ApiModelProperty(value = "所属区域编码")
    private String areaCode;
    /**所属区域名称*/
    @Excel(name = "所属区域名称", width = 15)
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;
    /**所属组织id*/
    @Excel(name = "所属组织id", width = 15)
    @ApiModelProperty(value = "所属组织id")
    private Long orgId;
    /**所属组织名称*/
    @Excel(name = "所属组织名称", width = 15)
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;
    /**所属组织编码*/
    @Excel(name = "所属组织编码", width = 15)
    @ApiModelProperty(value = "所属组织编码")
    private String orgCode;
    /**位置id(多选，位置信息库表id，逗号分割)*/
    @Excel(name = "位置id(多选，位置信息库表id，逗号分割)", width = 15)
    @ApiModelProperty(value = "位置id(多选，位置信息库表id，逗号分割)")
    private String locationIds;
    /**位置名称(多选，逗号分割)*/
    @Excel(name = "位置名称(多选，逗号分割)", width = 15)
    @ApiModelProperty(value = "位置名称(多选，逗号分割)")
    private String locationNames;
    /**安全员id*/
    @Excel(name = "安全员id", width = 15)
    @ApiModelProperty(value = "安全员id")
    private String safetyStaffId;
    /**安全员*/
    @Excel(name = "安全员", width = 15)
    @ApiModelProperty(value = "安全员")
    private String safetyStaff;
    /**安全主管*/
    @Excel(name = "安全主管", width = 15)
    @ApiModelProperty(value = "安全主管")
    private String safetyManager;
    /**安全主管id*/
    @Excel(name = "安全主管id", width = 15)
    @ApiModelProperty(value = "安全主管id")
    private String safetyManagerId;
    /**创建人姓名*/
    @Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
    private String createName;
    /**创建人工号*/
    @Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
    private String createUser;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**修改人姓名*/
    @Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
    private String updateName;
    /**修改人工号*/
    @Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
    private String updateUser;
    /**修改时间*/
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**删除标记 0未删除 1已删除*/
    @Excel(name = "删除标记 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除标记 0未删除 1已删除")
    private Integer delFlag;
    /**审批状态 0、草稿 1、审批中 2、完成*/
    @Excel(name = "审批状态 0、草稿 1、审批中 2、完成", width = 15)
    @ApiModelProperty(value = "审批状态 0、草稿 1、审批中 2、完成")
    private Integer bpmState;
}
