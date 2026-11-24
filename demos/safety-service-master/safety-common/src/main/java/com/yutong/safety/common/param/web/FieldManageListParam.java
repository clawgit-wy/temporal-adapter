package com.yutong.safety.common.param.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-28 13:26
 */
@Data
public class FieldManageListParam extends WebPageParam {

    @ApiModelProperty("字段类型")
    private String fieldType;

    @ApiModelProperty("是否只查询各type的第一级数据清单  1-是 其他-否")
    private boolean topFlag;

    @ApiModelProperty("父级ID")
    private Integer parentId;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("工厂ID拼接串-后端使用")
    private String factoryIds;

    private List<Integer> factoryIdList;
}
