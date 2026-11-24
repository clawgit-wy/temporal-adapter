package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 作业环节下拉列表VO
 */
@Data
public class YtsafetyHomeworkStageDropdownVO {

    @ApiModelProperty(value = "作业环节主键ID")
    private Long id;

    @ApiModelProperty(value = "作业环节编码")
    private String code;

    @ApiModelProperty(value = "作业环节名称")
    private String name;

    @ApiModelProperty(value = "厂区主键")
    private Long factoryId;

    @ApiModelProperty(value = "厂区编码")
    private String factoryCode;

    @ApiModelProperty(value = "厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "区域主键")
    private Long areaId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;

    @ApiModelProperty(value = "所属组织编码")
    private String orgCode;

    @ApiModelProperty(value = "所属组织名称")
    private String orgName;

    @ApiModelProperty(value = "位置列表")
    private List<LocationDropdownVO> locations;
}