package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class YtsafetyHomeworkStageDTO {
    
    /**主键ID*/
    @ApiModelProperty(value = "主键ID")
    private Long id;
    /**作业环节名称*/
    @NotBlank(message = "作业环节名称不能为空")
    @Size(max = 100,message = "作业环节名称最多输入100字符")
    @ApiModelProperty(value = "作业环节名称")
    private String linkName;

    /**所属厂区id*/
    @NotNull(message = "所属厂区不能为空")
    @Excel(name = "所属厂区id")
    @ApiModelProperty(value = "所属厂区id")
    private Long factoryId;
    /**所属厂区名称*/
    @Excel(name = "所属厂区名称")
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;
    /**所属厂区名称*/
    @Excel(name = "所属厂区编码")
    @ApiModelProperty(value = "所属厂区编码")
    private String factoryCode;
    /**所属区域id(多选，逗号分割)*/
    @NotNull(message = "所属区域不能为空")
    @Excel(name = "所属区域id(多选，逗号分割)")
    @ApiModelProperty(value = "所属区域id(多选，逗号分割)")
    private Long areaIds;
    /**所属区域名称*/
    @Excel(name = "所属区域名称")
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;
    /**所属组织id*/
    @NotNull(message = "所属组织不能为空")
    @Excel(name = "所属组织id")
    @ApiModelProperty(value = "所属组织id")
    private Long orgId;
    /**所属组织名称*/
    @Excel(name = "所属组织名称")
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;
    /**风险类别id*/
    @Excel(name = "风险类别id")
    @ApiModelProperty(value = "风险类别id")
    private Long riskTypeId;
    /**位置id(多选，位置信息库表id，逗号分割)*/
    @NotBlank(message = "位置不能为空")
    @Excel(name = "位置id(多选，位置信息库表id，逗号分割)")
    @ApiModelProperty(value = "位置id(多选，位置信息库表id，逗号分割)")
    private String locationIds;
    /**位置名称(多选，逗号分割)*/
    @Excel(name = "位置名称(多选，逗号分割)")
    @ApiModelProperty(value = "位置名称(多选，逗号分割)")
    private String locationNames;
    /**安全员*/
    @Excel(name = "安全员")
    @ApiModelProperty(value = "安全员")
    private String safetyStaff;

    @NotNull(message = "安全员不能为空")
    @ApiModelProperty(value = "安全员id")
    private Long safetyStaffId;

    @NotNull(message = "安全主管不能为空")
    @ApiModelProperty(value = "安全主管id")
    private Long safetyManagerId;
    /**安全主管*/
    @Excel(name = "安全主管")
    @ApiModelProperty(value = "安全主管")
    private String safetyManager;

    /**删除标记 0未删除 1已删除*/
    @Excel(name = "删除标记 0未删除 1已删除")
    @ApiModelProperty(value = "删除标记 0未删除 1已删除")
    private Integer delFlag;
}
