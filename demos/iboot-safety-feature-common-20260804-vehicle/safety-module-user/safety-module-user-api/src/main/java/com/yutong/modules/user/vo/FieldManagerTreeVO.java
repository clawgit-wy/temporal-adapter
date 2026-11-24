package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: 字段管理树节点VO
 * @Author: jeecg-boot
 * @Date:   2026-07-06
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel(value="字段管理树节点", description="字段管理树节点")
public class FieldManagerTreeVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String fieldName;

    @ApiModelProperty(value = "排序")
    private Integer fieldSort;

    @ApiModelProperty(value = "类型")
    private String fieldType;

    @ApiModelProperty(value = "父类id")
    private Long parentId;

    @ApiModelProperty(value = "字段编码")
    private String fieldCode;

    @ApiModelProperty(value = "字段level")
    private Integer fieldLevel;

    @ApiModelProperty(value = "标记")
    private String remark;

    @ApiModelProperty(value = "字段值")
    private String fieldValue;

    @ApiModelProperty(value = "环保信息")
    private String envirInfo;

    @ApiModelProperty(value = "最大排序值")
    private Integer maxSort;

    @ApiModelProperty(value = "子节点列表")
    private List<FieldManagerTreeVO> regionList;
}
