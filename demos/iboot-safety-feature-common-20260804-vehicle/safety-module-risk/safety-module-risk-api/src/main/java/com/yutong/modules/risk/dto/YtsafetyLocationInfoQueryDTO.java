package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoQueryDTO对象", description="位置信息查询DTO")
public class YtsafetyLocationInfoQueryDTO {
    /**位置编码*/
    @ApiModelProperty(value = "位置编码")
    private java.lang.String locationCode;

    @ApiModelProperty(value = "所属厂区主键(多选)")
    private List<Long> factoryIds;

    @ApiModelProperty(value = "所属区域主键(多选)")
    private List<Long> areaIds;

    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "风险等级")
    private java.lang.String riskLevel;

    @ApiModelProperty(value = "风险颜色")
    private java.lang.String riskColor;

    /**安全员*/
    @ApiModelProperty(value = "安全员")
    private java.lang.String safetyNames;

    /**状态： 0 启用 1 停用 */
    @ApiModelProperty(value = "状态： 0 启用 1 停用")
    private java.lang.Integer status;

    @ApiModelProperty("台账ID列表（导出时使用，逗号分隔）")
    private String selections;
}
