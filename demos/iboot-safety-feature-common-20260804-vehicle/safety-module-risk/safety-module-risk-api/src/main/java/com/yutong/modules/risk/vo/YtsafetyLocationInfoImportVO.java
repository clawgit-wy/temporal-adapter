package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.excel.core.BaseImportData;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class YtsafetyLocationInfoImportVO extends BaseImportData {

    @Excel(name = "所属区域*")
    @NotBlank(message = "所属区域不能为空")
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;

    @Excel(name = "所属组织*")
    @NotBlank(message = "所属组织不能为空")
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;

    @Excel(name = "所属厂区*")
    @NotBlank(message = "所属厂区不能为空")
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @Excel(name = "位置*")
    @NotBlank(message = "位置名称不能为空")
    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @Excel(name = "位置编码")
    @ApiModelProperty(value = "位置编码")
    private String locationCode;
}

