package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 组织架构映射表 Excel导入DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "组织架构映射表导入对象", description = "组织架构映射表导入DTO")
public class YTSafetySysOrgFactoryImportDTO {

    @Excel(name = "组织架构*", width = 100)
    @ApiModelProperty(value = "组织架构名称")
    private String orgName;

    @ApiModelProperty(value = "组织架构编码")
    private String orgCode;

    @Excel(name = "厂区*", width = 36)
    @ApiModelProperty(value = "厂区")
    private String factoryName;

    @ApiModelProperty(value = "厂区编码")
    private String factoryCode;

    /**
     * 错误信息（Jeecg固定字段，必须加）
     */
    @Excel(name="异常信息",width=30)
    @ApiModelProperty(value = "异常信息")
    private String errorMsg;
}