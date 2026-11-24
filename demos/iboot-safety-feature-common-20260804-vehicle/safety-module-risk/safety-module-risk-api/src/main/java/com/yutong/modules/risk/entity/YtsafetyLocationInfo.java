package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.domain.BaseDO;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 位置信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_location_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_location_info对象", description="位置信息表")
public class YtsafetyLocationInfo {

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.Long id;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private java.lang.Long processId;
    /**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
    private java.lang.Long factoryId;
    /**厂区编码*/
    @ApiModelProperty(value = "厂区编码")
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
    /**区域名称*/
    @ApiModelProperty(value = "区域名称")
    private java.lang.String areaName;
    /**所属组织id*/
    @ApiModelProperty(value = "所属组织id")
    private java.lang.Long orgId;
    /**所属组织编码*/
    @ApiModelProperty(value = "所属组织编码")
    private java.lang.String orgCode;
    /**orgName*/
    @ApiModelProperty(value = "orgName")
    private java.lang.String orgName;
    /**位置名称*/
    @ApiModelProperty(value = "位置名称")
    private java.lang.String locationName;
    /**位置编码*/
    @ApiModelProperty(value = "位置编码")
    private java.lang.String locationCode;
    /**安全员*/
    @ApiModelProperty(value = "安全员")
    private java.lang.String safetyRoleCode;
    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private java.lang.String riskLevel;
    /**风险颜色*/
    @ApiModelProperty(value = "风险颜色")
    private java.lang.String riskColor;
    /**风险数量*/
    @ApiModelProperty(value = "风险数量")
    private java.lang.Integer riskQuantity;
    /**位置主图(图片表第一个)*/
    @ApiModelProperty(value = "位置主图(图片表第一个)")
    private java.lang.String mainImage;
    /**创建人姓名*/
    @ApiModelProperty(value = "创建人姓名")
    private java.lang.String createName;
    /**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
    private java.lang.String createUser;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**修改人姓名*/
    @ApiModelProperty(value = "修改人姓名")
    private java.lang.String updateName;
    /**修改人工号*/
    @ApiModelProperty(value = "修改人工号")
    private java.lang.String updateUser;
    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
    /**状态 0启用 1停用*/
    @ApiModelProperty(value = "状态 0启用 1停用")
    private java.lang.Integer status;
    /**delFlag*/
    @ApiModelProperty(value = "delFlag")
    private java.lang.Integer delFlag;
    /**乐观锁*/
    @ApiModelProperty(value = "乐观锁")
    private java.lang.Integer version;
}
