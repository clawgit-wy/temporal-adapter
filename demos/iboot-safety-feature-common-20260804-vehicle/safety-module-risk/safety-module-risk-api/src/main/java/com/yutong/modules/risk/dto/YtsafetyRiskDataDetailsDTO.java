package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetyRiskDataDetailsDTO {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "系统流程主键")
    private Long processId;
    @ApiModelProperty(value = "主键")
    private Long changeId;
    @ApiModelProperty(value = "编码")
    private String riskCode;
    @ApiModelProperty(value = "厂区主键")
    private Long factoryId;
    @ApiModelProperty(value = "厂区")
    private String factoryName;
    @ApiModelProperty(value = "区域主键")
    private Long areaId;
    @ApiModelProperty(value = "区域编码")
    private java.lang.String areaCode;
    @ApiModelProperty(value = "区域")
    private String areaName;
    @ApiModelProperty(value = "位置主键")
    private Long locationId;
    @ApiModelProperty(value = "位置")
    private String locationName;
    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;
    @ApiModelProperty(value = "所属组织编码")
    private String orgCode;
    @ApiModelProperty(value = "所属组织")
    private String orgName;
    @ApiModelProperty(value = "风险类型主键")
    private Long riskTypeId;
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    @ApiModelProperty(value = "风险类别主键")
    private Long riskCategoryId;
    @ApiModelProperty(value = "风险类别编码")
    private String riskCategoryCode;
    @ApiModelProperty(value = "风险类别")
    private String riskCategoryName;
    @ApiModelProperty(value = "作业环节主键")
    private Long jobStepId;
    @ApiModelProperty(value = "作业环节编码")
    private String jobStepCode;
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;
    @ApiModelProperty(value = "风险因素主键")
    private Long riskFactorId;
    @ApiModelProperty(value = "风险因素")
    private String riskFactorName;
    @ApiModelProperty(value = "检查对象")
    private String checkObject;
    @ApiModelProperty(value = "风险识别维度主键")
    private Long riskIdentifyId;
    @ApiModelProperty(value = "风险识别维度")
    private String riskIdentifyName;
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
    @ApiModelProperty(value = "防范措施")
    private String preventMeasure;
    @ApiModelProperty(value = "风险值")
    private Integer riskValue;
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    @ApiModelProperty(value = "风险警示色")
    private String riskWarnColor;
    @ApiModelProperty(value = "安全员")
    private String safetyNames;
    @ApiModelProperty(value = "安全主管")
    private String managerNames;
    @ApiModelProperty(value = "更新状态")
    private Integer updateStatus;
    @ApiModelProperty(value = "风险暴露频率主键")
    private Long exposeFreqId;
    @ApiModelProperty(value = "风险暴露频率名称")
    private String exposeFreqName;
    @ApiModelProperty(value = "风险可能性主键")
    private Long possibilityId;
    @ApiModelProperty(value = "风险可能性名称")
    private String possibilityName;
    @ApiModelProperty(value = "后果严重性主键")
    private Long severityId;
    @ApiModelProperty(value = "后果严重性名称")
    private String severityName;
    @ApiModelProperty(value = "安全员角色编码")
    private String safetyRoleCode;
    @ApiModelProperty(value = "安全主管角色编码")
    private String managerRoleCode;
}
