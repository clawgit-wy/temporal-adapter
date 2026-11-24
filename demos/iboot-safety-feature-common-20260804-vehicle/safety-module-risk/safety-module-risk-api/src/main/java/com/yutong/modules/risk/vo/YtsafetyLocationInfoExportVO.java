package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoExportVO对象", description="位置信息表导出映射对象")
public class YtsafetyLocationInfoExportVO {
    /**位置编码*/
    @Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    @DataPermissionField(deName="位置编码",deCode = "location_code")
    private java.lang.String locationCode;
    /**厂区*/
    @Excel(name = "厂区", width = 15)
    @ApiModelProperty(value = "厂区")
    @DataPermissionField(deName="厂区",deCode = "factory_name")
    private java.lang.String factoryName;
    /**区域*/
    @Excel(name = "区域", width = 15)
    @ApiModelProperty(value = "区域")
    @DataPermissionField(deName="区域",deCode = "area_name")
    private java.lang.String areaName;
    /**所属组织*/
    @Excel(name = "所属组织", width = 15)
    @DataPermissionField(deName="所属组织",deCode = "org_name")
    @ApiModelProperty(value = "所属组织")
    private java.lang.String orgName;
    /**位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    @DataPermissionField(deName="位置",deCode = "location_name")
    private java.lang.String locationName;
    /**riskLevel*/
    @Excel(name = "风险等级", width = 15)
    @ApiModelProperty(value = "风险等级")
    @DataPermissionField(deName="风险等级",deCode = "risk_level")
    private java.lang.String riskLevel;
    /**风险颜色*/
    @Excel(name = "风险颜色", width = 15)
    @DataPermissionField(deName="风险颜色",deCode = "risk_color")
    @ApiModelProperty(value = "风险颜色")
    private java.lang.String riskColor;
    /**安全员*/
    @Excel(name = "安全员", width = 15)
    @ApiModelProperty(value = "安全员")
    private java.lang.String safetyNames;
    /**位置图*/
    @Excel(name = "位置图", width = 15)
    @ApiModelProperty(value = "位置图")
    private java.lang.String mainImage;
    /**状态 0启用 1停用*/
    @Excel(name = "数据状态",replace = {"启用_0", "停用_1"}, width = 15)
    @ApiModelProperty(value = "状态 0启用 1停用")
    @DataPermissionField(deName="数据状态",deCode = "status",enumMappings = {"0:启用","1:停用"})
    private java.lang.Integer status;
    /**历史隐患*/
    @Excel(name = "历史隐患", width = 15)
    @ApiModelProperty(value = "历史隐患")
    @DataPermissionField(deName="历史隐患",deCode = "hiddenDangerNum")
    private java.lang.Integer hiddenDangerNum;
    /**最近更新时间*/
    @Excel(name = "最近更新时间", width = 15,exportFormat="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间")
    private java.util.Date updateTime;
    /**最近更新人*/
    @Excel(name = "最近更新人", width = 15)
    @ApiModelProperty(value = "最近更新人")
    private java.lang.String updateName;
    /**详情链接*/
    @Excel(name = "详情链接", width = 15)
    @ApiModelProperty(value = "详情链接")
    private java.lang.String detailUrl;
}
