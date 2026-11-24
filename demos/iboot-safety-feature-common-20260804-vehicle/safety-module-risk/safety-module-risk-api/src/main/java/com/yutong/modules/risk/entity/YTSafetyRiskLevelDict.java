package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 风险等级字典表
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_risk_level_dict")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ytsafety_risk_level_dict对象", description = "风险等级字典表")
public class YTSafetyRiskLevelDict {

    /**主键*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**风险值*/
    @ApiModelProperty(value = "风险值")
    private Double riskValue;

    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;

    /**风险提示色*/
    @ApiModelProperty(value = "风险提示色")
    private String riskColor;

    /**风险描述*/
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
}
