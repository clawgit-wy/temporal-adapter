package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtSafetyRiskTypeDictVO {
    /**风险类别主键*/
    @ApiModelProperty(value = "风险类型主键")
    private Long riskTypeId;
    /**风险类型*/
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    /**风险类别主键*/
    @ApiModelProperty(value = "风险类别主键")
    private Long riskCategoryId;
    @ApiModelProperty(value = "风险类别编码")
    private String riskCategoryCode;
    /**风险类别名称*/
    @ApiModelProperty(value = "风险类别名称")
    private String riskCategoryName;
    /**风险因素主键*/
    @ApiModelProperty(value = "风险因素主键")
    private Long riskFactorId;
    /**风险因素名称*/
    @ApiModelProperty(value = "风险因素名称")
    private String riskFactorName;
    /**风险识别维度*/
    @ApiModelProperty(value = "风险识别维度")
    private Long riskIdentifyId;
    /**风险识别维度名称*/
    @ApiModelProperty(value = "风险识别维度名称")
    private String riskIdentifyName;
}
