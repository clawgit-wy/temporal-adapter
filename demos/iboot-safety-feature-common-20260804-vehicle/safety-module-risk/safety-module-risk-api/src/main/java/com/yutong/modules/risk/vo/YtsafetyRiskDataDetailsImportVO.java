package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.excel.core.BaseImportData;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class YtsafetyRiskDataDetailsImportVO extends BaseImportData {

    @Excel(name="风险编码",width=15)
    @ApiModelProperty(value = "风险编码")
    private String riskCode;
    @Excel(name="风险类型*",width=15)
    @NotBlank(message="风险类型不能为空")
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    @Excel(name="作业环节名称",width=15)
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;
    @Excel(name="厂区*",width=15)
    @NotBlank(message="厂区不能为空")
    @ApiModelProperty(value = "厂区")
    private String factoryName;
    @Excel(name="区域*",width=15)
    @NotBlank(message="区域不能为空")
    @ApiModelProperty(value = "区域")
    private String areaName;
    @Excel(name="所属组织*",width=15)
    @NotBlank(message="所属组织不能为空")
    @ApiModelProperty(value = "所属组织")
    private String orgName;
    @Excel(name="位置*",width=15)
    @NotBlank(message="位置不能为空")
    @ApiModelProperty(value = "位置")
    private String locationName;
    @Excel(name="检查对象",width=15)
    @ApiModelProperty(value = "检查对象")
    private String checkObject;
    @Excel(name="风险因素",width=15)
    @ApiModelProperty(value = "风险因素")
    private String riskFactorName;
    @Excel(name="风险类别*",width=15)
    @NotBlank(message="风险类别不能为空")
    @ApiModelProperty(value = "风险类别")
    private String riskCategoryName;
    @Excel(name="风险识别维度",width=15)
    @ApiModelProperty(value = "风险识别维度")
    private String riskIdentifyName;
    @Excel(name="风险描述*",width=15)
    @NotBlank(message="风险描述不能为空")
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
    @Excel(name="防范措施*",width=15)
    @NotBlank(message="防范措施不能为空")
    @ApiModelProperty(value = "防范措施")
    private String preventMeasure;
    @Excel(name="风险可能性*",width=15)
    @NotBlank(message="风险可能性不能为空")
    @ApiModelProperty(value = "风险可能性")
    private String possibilityName;
    @Excel(name="后果严重性*",width=15)
    @NotBlank(message="后果严重性不能为空")
    @ApiModelProperty(value = "后果严重性")
    private String severityName;
    @Excel(name="风险暴露频率",width=15)
    @ApiModelProperty(value = "风险暴露频率")
    private String exposeFreqName;
}
