package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoProcessDetailsVO对象", description="位置信息明细表映射对象")
public class YtsafetyLocationInfoProcessDetailsVO {
    /**主键*/
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
    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private java.lang.String riskLevel;
    /**风险颜色*/
    @ApiModelProperty(value = "风险颜色")
    private java.lang.String riskColor;
    /**组织名称*/
    @ApiModelProperty(value = "组织名称")
    private java.lang.String orgName;
    /**安全员编码*/
    @ApiModelProperty(value = "安全员编码")
    private java.lang.String safetyRoleCode;
    /**安全员*/
    @ApiModelProperty(value = "安全员")
    private java.lang.String safetyNames;
    /**位置名称*/
    @ApiModelProperty(value = "位置名称")
    private java.lang.String locationName;
    /**位置编码*/
    @ApiModelProperty(value = "位置编码")
    private java.lang.String locationCode;
    /**位置主图(图片表第一个)*/
    @ApiModelProperty(value = "位置主图(图片表第一个)")
    private java.lang.String mainImage;
    /**状态 0启用 1停用*/
    @ApiModelProperty(value = "状态 0启用 1停用")
    private java.lang.Integer status;
    @ApiModelProperty(value = "创建人姓名")
    private java.lang.String createName;
    @ApiModelProperty(value = "创建人工号")
    private java.lang.String createUser;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    @ApiModelProperty(value = "修改人姓名")
    private java.lang.String updateName;
    @ApiModelProperty(value = "修改人工号")
    private java.lang.String updateUser;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;

    @ApiModelProperty(value = "字段是否更新")
    private List<String> fieldCodes;
    @ApiModelProperty(value = "原字段字段值")
    private HashMap<String,String> fieldsValues;
}
