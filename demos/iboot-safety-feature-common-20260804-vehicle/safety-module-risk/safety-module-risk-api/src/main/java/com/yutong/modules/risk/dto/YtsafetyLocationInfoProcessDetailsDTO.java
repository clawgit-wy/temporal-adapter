package com.yutong.modules.risk.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoProcessDetailsDTO对象", description="位置信息表明细表DTO")
public class YtsafetyLocationInfoProcessDetailsDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.Long id;
    /**系统流程主键*/
    @ApiModelProperty(value = "系统流程主键")
    private java.lang.Long processId;
    /**变更ID*/
    @ApiModelProperty(value = "变更ID")
    private java.lang.Long changeId;
    /**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
    private java.lang.Long factoryId;
    /**所属厂区编码*/
    @ApiModelProperty(value = "所属厂区编码")
    private java.lang.String factoryCode;
    /**工厂名称*/
    @ApiModelProperty(value = "工厂名称")
    private java.lang.String factoryName;
    /**所属区域id*/
    @ApiModelProperty(value = "所属区域id")
    private java.lang.Long areaId;
    /**区域编码*/
    @ApiModelProperty(value = "区域编码")
    private java.lang.String areaCode;
    /**所属区域*/
    @ApiModelProperty(value = "所属区域")
    private java.lang.String areaName;
    /**所属组织id*/
    @ApiModelProperty(value = "所属组织id")
    private java.lang.Long orgId;
    /**所属组织编码*/
    @ApiModelProperty(value = "所属组织编码")
    private java.lang.String orgCode;
    /**组织名称*/
    @ApiModelProperty(value = "组织名称")
    private java.lang.String orgName;
    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private java.lang.String riskLevel;
    /**风险颜色*/
    @ApiModelProperty(value = "风险颜色")
    private java.lang.String riskColor;
    /**位置名称*/
    @ApiModelProperty(value = "位置名称")
    private java.lang.String locationName;
    /**位置编码*/
    @ApiModelProperty(value = "位置编码")
    private java.lang.String locationCode;
    /**位置主图(图片表第一个)*/
    @ApiModelProperty(value = "位置主图(图片表第一个)")
    private java.lang.String mainImage;
    /**状态： 0 启用 1 停用 */
    @ApiModelProperty(value = "状态： 0 启用 1 停用")
    private java.lang.Integer status;
}
