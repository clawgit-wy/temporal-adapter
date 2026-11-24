package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 岗位角色映射 Excel导入模板DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "岗位角色映射导入对象模板", description = "岗位角色映射导入模板DTO")
public class YTSafetySysJobRoleMappingImportTemplateDTO {

    @Excel(name = "岗位编码*", width = 30)
    @ApiModelProperty(value = "岗位编码")
    private String jobCode;

    @Excel(name = "岗位名称", width = 30)
    @ApiModelProperty(value = "岗位名称")
    private String jobName;

    @Excel(name = "角色名称*", width = 30)
    @ApiModelProperty(value = "角色名称")
    private String roleName;
}