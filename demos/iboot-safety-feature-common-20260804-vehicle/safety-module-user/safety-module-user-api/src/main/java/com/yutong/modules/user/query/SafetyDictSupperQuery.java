package com.yutong.modules.user.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SafetyDictSupperQuery {

    /**字典名称*/
    @Excel(name = "字典名称", width = 15)
    @ApiModelProperty(value = "字典名称")
    private String supperName;
    /**字典编码*/
    @Excel(name = "字典编码", width = 15)
    @ApiModelProperty(value = "字典编码")
    private String supperCode;
    /**描述*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String supperDesc;

    @Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private String type;

}
