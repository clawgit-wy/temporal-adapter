package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
public class YtsafetyHomeworkStageExportVO {

    @ApiModelProperty(value = "主键ID")
    @ExcelIgnore
    private Long id;

    @Excel(name = "作业环节编码", width = 15)
    private String linkCode;

    /**作业环节名称*/
    @Excel(name = "作业环节名称", width = 15)
    private String linkName;

    /**所属厂区名称*/
    @Excel(name = "所属厂区名称", width = 15)
    private String factoryName;

    /**所属区域名称*/
    @Excel(name = "所属区域名称", width = 15)
    private String areaName;

    /**所属组织名称*/
    @Excel(name = "所属组织名称", width = 15)
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;

    /**位置名称(多选，逗号分割)*/
    @Excel(name = "位置名称", width = 15)
    private String locationNames;

    /**所属组织编码（用于解析安全员，不导出）*/
    @ExcelIgnore
    private String orgCode;
    /**安全角色编码（用于解析安全员，不导出）*/
    @ExcelIgnore
    private String safetyRoleCode;
    /**安全员*/
    @Excel(name = "安全员", width = 15)
    private String safetyStaff;
    /**安全主管*/
    @Excel(name = "安全主管", width = 15)
    private String safetyManager;

    /**修改时间*/
    @Excel(name = "最近更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**修改人姓名*/
    @Excel(name = "最近更新人", width = 15)
    private String updateName;


    @Excel(name = "详情", width = 15)
    private String stageInfo;
}
