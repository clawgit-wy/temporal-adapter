package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
public class YtsafetyHomeworkStageVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**作业环节变更主键ID*/
    @ApiModelProperty(value = "作业环节变更主键ID")
    private Long changeId;

    @Excel(name = "作业环节编码", width = 15)
    @ApiModelProperty(value = "作业环节编码")
    private String linkCode;

    /**作业环节名称*/
    @Excel(name = "作业环节名称", width = 15)
    @ApiModelProperty(value = "作业环节名称")
    private String linkName;

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

    @ApiModelProperty(value = "字段是否更新")
    private List<String> fieldCodes;
    @ApiModelProperty(value = "原字段字段值")
    private HashMap<String,String> fieldsValues;

}
