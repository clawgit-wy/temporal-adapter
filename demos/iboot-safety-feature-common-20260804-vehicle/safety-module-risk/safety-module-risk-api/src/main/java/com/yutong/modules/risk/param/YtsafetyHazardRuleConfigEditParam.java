package com.yutong.modules.risk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 隐患规则配置
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="隐患规则配置修改")
public class YtsafetyHazardRuleConfigEditParam {

    /**主键*/
    @ApiModelProperty(value = "主键")
    private Integer id;
    /**名称*/
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    /**名称*/
    @ApiModelProperty(value = "规则编码")
    private String ruleCode;
    /**数据值*/
    @ApiModelProperty(value = "规则值")
    private String ruleValue;
    /**数据值*/
    @ApiModelProperty(value = "描述")
    private String description;

}
