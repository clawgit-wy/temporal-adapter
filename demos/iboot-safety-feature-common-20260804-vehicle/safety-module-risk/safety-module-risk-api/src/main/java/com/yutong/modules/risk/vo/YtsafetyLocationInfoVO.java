package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecg.common.domain.BaseVO;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoVO对象", description="位置信息表映射对象")
public class YtsafetyLocationInfoVO {
    /**主键ID*/
    @ApiModelProperty(value = "主键ID")
    private java.lang.Long id;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private java.lang.Long processId;
    /**位置编码*/
    @DataPermissionField(deName="位置编码",deCode = "location_code")
    @ApiModelProperty(value = "位置编码")
    private java.lang.String locationCode;
    /**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
    private java.lang.Long factoryId;
    /**厂区编码*/
    @DataPermissionField(deName="位置编码",deCode = "factory_code")
    @ApiModelProperty(value = "厂区编码")
    private java.lang.String factoryCode;
    /**厂区*/
    @ApiModelProperty(value = "厂区")
    @DataPermissionField(deName="厂区",deCode = "factory_name")
    private java.lang.String factoryName;
    /**所属区域主键*/
    @ApiModelProperty(value = "所属区域主键")
    private java.lang.Long areaId;
    /**区域编码*/
    @ApiModelProperty(value = "区域编码")
    @DataPermissionField(deName="区域编码",deCode = "area_code")
    private java.lang.String areaCode;
    /**区域*/
    @ApiModelProperty(value = "区域")
    @DataPermissionField(deName="区域",deCode = "area_name")
    private java.lang.String areaName;
    /**所属组织id*/
    @ApiModelProperty(value = "所属组织id")
    private java.lang.Long orgId;
    /**所属组织编码*/
    @ApiModelProperty(value = "所属组织编码")
    @DataPermissionField(deName="所属组织编码",deCode = "org_code")
    private java.lang.String orgCode;
    /**所属组织*/
    @ApiModelProperty(value = "所属组织")
    @DataPermissionField(deName="所属组织",deCode = "org_name")
    private java.lang.String orgName;
    /**位置*/
    @ApiModelProperty(value = "位置")
    @DataPermissionField(deName="位置",deCode = "location_name")
    private java.lang.String locationName;
    /**安全员编码*/
    @ApiModelProperty(value = "安全员编码")
    private java.lang.String safetyRoleCode;
    /**riskLevel*/
    @ApiModelProperty(value = "风险等级")
    @DataPermissionField(deName="风险等级",deCode = "risk_level")
    private java.lang.String riskLevel;
    /**风险颜色*/
    @ApiModelProperty(value = "风险颜色")
    @DataPermissionField(deName="风险等级",deCode = "risk_color")
    private java.lang.String riskColor;
    /**安全员*/
    @ApiModelProperty(value = "安全员")
    private java.lang.String safetyNames;
    /**位置图*/
    @ApiModelProperty(value = "位置图")
    private java.lang.String mainImage;
    /**状态 0启用 1停用*/
    @ApiModelProperty(value = "数据状态")
    @DataPermissionField(deName="风险等级",deCode = "status",enumMappings = {"0:启用","1:停用"})
    private java.lang.Integer status;
    /**历史隐患*/
    @ApiModelProperty(value = "历史隐患")
    @DataPermissionField(deName="风险等级",deCode = "hiddenDangerNum")
    private java.lang.Integer hiddenDangerNum;
    /**重大风险个数（红）*/
    @ApiModelProperty(value = "重大风险个数（红）")
    private java.lang.Integer redRiskCount;
    /**较大风险个数（橙）*/
    @ApiModelProperty(value = "较大风险个数（橙）")
    private java.lang.Integer orangeRiskCount;
    /**一般风险个数（黄）*/
    @ApiModelProperty(value = "一般风险个数（黄）")
    private java.lang.Integer yellowRiskCount;
    /**低风险个数（蓝）*/
    @ApiModelProperty(value = "低风险个数（蓝）")
    private java.lang.Integer blueRiskCount;
    /**风险分数*/
    @ApiModelProperty(value = "风险分数")
    @DataPermissionField(deName="风险分数",deCode = "risk_score")
    private java.lang.Double riskScore;
    /**最近更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间")
    private java.util.Date updateTime;
    /**最近更新人*/
    @ApiModelProperty(value = "最近更新人")
    private java.lang.String updateName;
}
