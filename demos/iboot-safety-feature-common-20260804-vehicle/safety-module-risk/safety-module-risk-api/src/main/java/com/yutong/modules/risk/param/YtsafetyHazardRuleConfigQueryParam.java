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
@ApiModel(description="隐患规则配置查询条件")
public class YtsafetyHazardRuleConfigQueryParam {

    /**名称*/
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    /**名称*/
    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

}
