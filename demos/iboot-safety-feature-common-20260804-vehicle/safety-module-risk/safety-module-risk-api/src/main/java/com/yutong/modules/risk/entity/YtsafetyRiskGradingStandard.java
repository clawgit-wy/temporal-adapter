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
 * @Description: 风险分级标准表
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_risk_grading_standard")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ytsafety_risk_grading_standard对象", description = "风险分级标准表")
public class YtsafetyRiskGradingStandard {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**适用类型：1安全，2消防*/
    @ApiModelProperty(value = "适用类型：1安全，2消防")
    private Integer riskType;

    /**公式：1 P*C，2 F*P*C*/
    @ApiModelProperty(value = "公式：1 P*C，2 F*P*C")
    private Integer formula;

    /**类型：1-暴露频率，2-风险可能性，3-风险严重性*/
    @ApiModelProperty(value = "类型：1-暴露频率，2-风险可能性，3-风险严重性")
    private Integer type;

    /**等级*/
    @ApiModelProperty(value = "等级")
    private String level;

    /**描述*/
    @ApiModelProperty(value = "描述")
    private String description;

    /**分值*/
    @ApiModelProperty(value = "分值")
    private Double score;
}
