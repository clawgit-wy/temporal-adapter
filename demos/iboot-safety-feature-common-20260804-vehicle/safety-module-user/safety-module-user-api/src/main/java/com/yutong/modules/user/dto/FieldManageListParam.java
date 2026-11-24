package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 字段管理列表查询参数
 * @Author: jeecg-boot
 * @Date:   2026-07-06
 * @Version: V1.0
 */
@Data
@ApiModel(value="字段管理列表查询参数", description="字段管理列表查询参数")
public class FieldManageListParam {

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "是否只查询各type的第一级数据清单 true-是 false-否")
    private boolean topFlag;

    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "字段编码")
    private String fieldCode;
}
