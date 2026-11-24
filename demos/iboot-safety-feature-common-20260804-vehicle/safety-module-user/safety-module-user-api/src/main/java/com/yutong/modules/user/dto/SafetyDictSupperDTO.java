package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

@Data
public class SafetyDictSupperDTO {
    /**bigint*/
    @Excel(name = "bigint", width = 15)
    @ApiModelProperty(value = "bigint")
    @TableId(type = IdType.AUTO)
    private Long id;
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
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer supperOrder;
    /**类型*/
    @NotBlank(message = "类型不能为空")
    @ApiModelProperty(value = "类型")
    private String type;
    /**类型*/
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

}
