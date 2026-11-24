package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 组织架构映射表 Excel导入模板DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "组织架构映射表导入模板对象", description = "组织架构映射表导入模板DTO")
public class YTSafetySysOrgFactoryImportTemplateDTO {

    @Excel(name = "组织架构*", width = 100)
    @ApiModelProperty(value = "组织架构名称")
    private String orgName;

    @Excel(name = "厂区*", width = 36)
    @ApiModelProperty(value = "厂区")
    private String factoryName;
}