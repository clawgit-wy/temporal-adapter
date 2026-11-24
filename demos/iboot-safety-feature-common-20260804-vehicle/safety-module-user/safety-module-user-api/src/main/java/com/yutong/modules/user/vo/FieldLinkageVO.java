package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: 联动列表VO（厂区-区域树形结构）
 * @Author: jeecg-boot
 * @Date:   2026-07-06
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel(value="联动列表VO", description="联动列表VO")
public class FieldLinkageVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String fieldName;

    @ApiModelProperty(value = "子节点列表")
    private List<FieldLinkageVO> regionList;
}
