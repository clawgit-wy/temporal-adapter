package com.yutong.modules.risk.vo;

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
public class YtsafetyRiskDataExportVO {

    @Excel(name = "编码", width = 15)
    @DataPermissionField(deName="编码",deCode = "risk_code")
    @ApiModelProperty(value = "编码")
    private String riskCode;
    @Excel(name = "风险类别", width = 15)
    @DataPermissionField(deName="风险类别",deCode = "risk_category_name")
    @ApiModelProperty(value = "风险类别")
    private String riskCategoryName;
    @DataPermissionField(deName="厂区",deCode = "factory_name")
    @Excel(name = "厂区", width = 15)
    @ApiModelProperty(value = "厂区")
    private String factoryName;
    @DataPermissionField(deName="区域",deCode = "area_name")
    @Excel(name = "区域", width = 15)
    @ApiModelProperty(value = "区域")
    private String areaName;
    @DataPermissionField(deName="位置",deCode = "location_name")
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private String locationName;
    @DataPermissionField(deName="所属组织",deCode = "org_name")
    @Excel(name = "所属组织", width = 15)
    @ApiModelProperty(value = "所属组织")
    private String orgName;
    @DataPermissionField(deName="风险类型",deCode = "risk_type_name")
    @Excel(name = "风险类型", width = 15)
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    @DataPermissionField(deName="作业环节编码",deCode = "job_step_code")
    @Excel(name = "作业环节编码", width = 15)
    @ApiModelProperty(value = "作业环节编码")
    private String jobStepCode;
    @DataPermissionField(deName="作业环节名称",deCode = "job_step_name")
    @Excel(name = "作业环节名称", width = 15)
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;
    @DataPermissionField(deName="风险因素",deCode = "risk_factor_name")
    @Excel(name = "风险因素", width = 15)
    @ApiModelProperty(value = "风险因素")
    private String riskFactorName;
    @DataPermissionField(deName="检查对象",deCode = "check_object")
    @Excel(name = "检查对象", width = 15)
    @ApiModelProperty(value = "检查对象")
    private String checkObject;
    @DataPermissionField(deName="风险识别维度",deCode = "risk_identify_name")
    @Excel(name = "风险识别维度", width = 15)
    @ApiModelProperty(value = "风险识别维度")
    private String riskIdentifyName;
    @DataPermissionField(deName="风险描述",deCode = "risk_description")
    @Excel(name = "风险描述", width = 15)
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
    @DataPermissionField(deName="风险描述",deCode = "risk_value")
    @Excel(name = "风险值", width = 15)
    @ApiModelProperty(value = "风险值")
    private Integer riskValue;
    @DataPermissionField(deName="风险等级",deCode = "risk_level")
    @Excel(name = "风险等级", width = 15)
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    @DataPermissionField(deName="风险等级",deCode = "risk_warn_color")
    @Excel(name = "风险警示色", width = 15)
    @ApiModelProperty(value = "风险警示色")
    private String riskWarnColor;
    @Excel(name = "安全员", width = 15)
    @ApiModelProperty(value = "安全员")
    private String safetyNames;
    @Excel(name = "安全主管", width = 15)
    @ApiModelProperty(value = "安全主管")
    private String managerNames;
    @Excel(name = "最近更新时间", width = 15,exportFormat="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间")
    private Date updateTime;
    @Excel(name = "最近更新人", width = 15)
    @ApiModelProperty(value = "最近更新人")
    private String updateName;
    @Excel(name = "更新状态",replace = {"绿_0", "红_1"}, width = 15)
    @ApiModelProperty(value = "更新状态")
    private Integer updateStatus;
    @Excel(name = "详情链接", width = 15)
    @ApiModelProperty(value = "详情链接")
    private String detailUrl;
}
