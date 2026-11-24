package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoDetailsExportVO对象", description="位置信息表导出映射对象")
public class YtsafetyLocationInfoDetailsExportVO {

    @Excel(name = "所属区域*", width = 15)
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;

    @Excel(name = "所属组织*", width = 15)
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;

    @Excel(name = "所属厂区*", width = 15)
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @Excel(name = "位置*", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private String locationCode;
}
