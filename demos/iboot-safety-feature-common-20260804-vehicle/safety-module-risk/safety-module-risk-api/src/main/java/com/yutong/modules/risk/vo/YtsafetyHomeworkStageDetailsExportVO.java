package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 作业环节流程详情导出VO
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Data
public class YtsafetyHomeworkStageDetailsExportVO {
    @Excel(name = "作业环节名称", width = 15)
    @ApiModelProperty(value = "作业环节名称")
    private String linkName;

    @Excel(name = "作业环节编码", width = 15)
    @ApiModelProperty(value = "作业环节编码")
    private String linkCode;

    @Excel(name = "所属厂区名称", width = 15)
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @Excel(name = "所属区域名称", width = 15)
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;

    @Excel(name = "所属组织名称", width = 15)
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;

    @Excel(name = "位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String locationNames;

    @Excel(name = "安全员", width = 15)
    @ApiModelProperty(value = "安全员")
    private String safetyStaff;

    @Excel(name = "安全主管", width = 15)
    @ApiModelProperty(value = "安全主管")
    private String safetyManager;
}
