package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetyRiskDataVO {
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "流程主键")
    private Long processId;
    @DataPermissionField(deName="编码",deCode = "risk_code")
    @ApiModelProperty(value = "编码")
    private String riskCode;
    @ApiModelProperty(value = "厂区主键")
    private Long factoryId;
    @DataPermissionField(deName="厂区",deCode = "factory_name")
    @ApiModelProperty(value = "厂区")
    private String factoryName;
    @ApiModelProperty(value = "区域主键")
    private Long areaId;
    @DataPermissionField(deName="区域编码",deCode = "area_code")
    @ApiModelProperty(value = "区域编码")
    private java.lang.String areaCode;
    @DataPermissionField(deName="区域",deCode = "area_name")
    @ApiModelProperty(value = "区域")
    private String areaName;
    @ApiModelProperty(value = "位置主键")
    private Long locationId;
    @DataPermissionField(deName="位置",deCode = "location_name")
    @ApiModelProperty(value = "位置")
    private String locationName;
    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;
    @DataPermissionField(deName="所属组织编码",deCode = "org_code")
    @ApiModelProperty(value = "所属组织编码")
    private String orgCode;
    @DataPermissionField(deName="所属组织",deCode = "org_name")
    @ApiModelProperty(value = "所属组织")
    private String orgName;
    @ApiModelProperty(value = "风险类型主键")
    private Long riskTypeId;
    @DataPermissionField(deName="所属组织",deCode = "risk_type_name")
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    @ApiModelProperty(value = "风险类别主键")
    private Long riskCategoryId;
    @DataPermissionField(deName="所属组织",deCode = "risk_category_code")
    @ApiModelProperty(value = "风险类别编码")
    private String riskCategoryCode;
    @DataPermissionField(deName="风险类别",deCode = "risk_category_name")
    @ApiModelProperty(value = "风险类别")
    private String riskCategoryName;
    @ApiModelProperty(value = "作业环节主键")
    private Long jobStepId;
    @DataPermissionField(deName="作业环节编码",deCode = "job_step_code")
    @ApiModelProperty(value = "作业环节编码")
    private String jobStepCode;
    @DataPermissionField(deName="作业环节名称",deCode = "job_step_name")
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;
    @ApiModelProperty(value = "风险因素主键")
    private Long riskFactorId;
    @DataPermissionField(deName="风险因素",deCode = "risk_factor_name")
    @ApiModelProperty(value = "风险因素")
    private String riskFactorName;
    @DataPermissionField(deName="检查对象",deCode = "check_object")
    @ApiModelProperty(value = "检查对象")
    private String checkObject;
    @ApiModelProperty(value = "风险识别维度主键")
    private Long riskIdentifyId;
    @DataPermissionField(deName="风险识别维度",deCode = "risk_identify_name")
    @ApiModelProperty(value = "风险识别维度")
    private String riskIdentifyName;
    @DataPermissionField(deName="风险描述",deCode = "risk_description")
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
    @DataPermissionField(deName="防范措施",deCode = "prevent_measure")
    @ApiModelProperty(value = "防范措施")
    private String preventMeasure;
    @DataPermissionField(deName="风险值",deCode = "risk_value")
    @ApiModelProperty(value = "风险值")
    private Integer riskValue;
    @DataPermissionField(deName="风险等级",deCode = "risk_level")
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    @DataPermissionField(deName="风险警示色",deCode = "risk_warn_color")
    @ApiModelProperty(value = "风险警示色")
    private String riskWarnColor;
    @ApiModelProperty(value = "安全员角色编码")
    private String safetyRoleCode;
    @ApiModelProperty(value = "安全员")
    private String safetyNames;
    @ApiModelProperty(value = "安全主管角色编码")
    private String managerRoleCode;
    @ApiModelProperty(value = "安全主管")
    private String managerNames;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "最近更新人")
    private String updateName;
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
}
