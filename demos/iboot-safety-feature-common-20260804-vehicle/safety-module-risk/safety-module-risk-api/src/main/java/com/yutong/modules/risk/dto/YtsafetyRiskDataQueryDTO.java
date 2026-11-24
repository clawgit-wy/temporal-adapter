package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetyRiskDataQueryDTO {

    @ApiModelProperty(value = "风险编码")
    private String riskCode;

    @ApiModelProperty(value = "风险类型主键")
    private Long riskTypeId;

    @ApiModelProperty(value = "所属厂区主键(多选)")
    private List<Long> factoryIds;

    @ApiModelProperty(value = "所属区域主键(多选)")
    private List<Long> areaIds;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;

    @ApiModelProperty(value = "风险等级")
    private String riskLevel;

    @ApiModelProperty(value = "风险值(起)")
    private Integer riskValueStart;

    @ApiModelProperty(value = "风险值(止)")
    private Integer riskValueEnd;

    @ApiModelProperty(value = "风险类别主键")
    private Long riskCategoryId;

    @ApiModelProperty(value = "作业环节编码")
    private String jobStepCode;

    @Excel(name = "作业环节名称", width = 15)
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;

    @ApiModelProperty(value = "风险描述")
    private String riskDescription;

    @ApiModelProperty(value = "安全员名称")
    private String safetyStaffName;

    @ApiModelProperty(value = "安全主管名称")
    private String safetyManagerName;

    @ApiModelProperty(value = "最近更新人")
    private String updateName;

    @ApiModelProperty(value = "app查询")
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间开始")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间结束")
    private Date endTime;

    @ApiModelProperty("台账ID列表（导出时使用，逗号分隔）")
    private String selections;
}
