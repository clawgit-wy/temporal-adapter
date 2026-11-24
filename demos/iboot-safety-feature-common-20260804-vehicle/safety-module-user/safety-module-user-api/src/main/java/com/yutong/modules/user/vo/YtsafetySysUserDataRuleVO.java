package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

@Data
public class YtsafetySysUserDataRuleVO {

    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;
    /**用户主键*/
    @Excel(name = "用户主键", width = 15)
    @ApiModelProperty(value = "用户主键")
    private Long userId;
    /**菜单主键*/
    @Excel(name = "菜单主键", width = 15)
    @ApiModelProperty(value = "菜单主键")
    private Long permId;
    /**fieldCode*/
    @Excel(name = "fieldCode", width = 15)
    @ApiModelProperty(value = "fieldCode")
    private String fieldCode;
    /**字段名称*/
    @Excel(name = "字段名称", width = 15)
    @ApiModelProperty(value = "字段名称")
    private String fieldName;
    /**关系: 0 或 1 且*/
    @Excel(name = "关系: or 或 and 且", width = 15)
    @ApiModelProperty(value = "关系: or 或 and 且")
    private String relation;
    /**rule conditions*/
    @Excel(name = "rule conditions", width = 15)
    @ApiModelProperty(value = ">,=,!=,like")
    private String ruleConditions;
    /**ruleValue*/
    @Excel(name = "ruleValue", width = 15)
    @ApiModelProperty(value = "ruleValue")
    private String ruleValue;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "菜单编码")
    private String permCode;

}
