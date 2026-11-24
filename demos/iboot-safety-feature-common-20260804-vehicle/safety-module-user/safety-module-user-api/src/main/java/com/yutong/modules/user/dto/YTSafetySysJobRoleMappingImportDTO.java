package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 岗位角色映射 Excel导入DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "岗位角色映射导入对象", description = "岗位角色映射导入DTO")
public class YTSafetySysJobRoleMappingImportDTO {

    @Excel(name = "岗位编码*", width = 30)
    @ApiModelProperty(value = "岗位编码")
    private String jobCode;

    @Excel(name = "岗位名称", width = 30)
    @ApiModelProperty(value = "岗位名称")
    private String jobName;

//    @Excel(name = "角色编码", width = 15)
//    @ApiModelProperty(value = "角色编码")
//    private String roleCode;

    @Excel(name = "角色名称*", width = 30)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 错误信息（Jeecg固定字段，必须加）
     */
    @Excel(name="异常信息",width=30)
    @ApiModelProperty(value = "异常信息")
    private String errorMsg;
}