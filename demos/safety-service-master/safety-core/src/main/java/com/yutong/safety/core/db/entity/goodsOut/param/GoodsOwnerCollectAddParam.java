package com.yutong.safety.core.db.entity.goodsOut.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsOwnerCollectAddParam {

    private Integer id;

    @ApiModelProperty("收藏人员工号")
    private String ownerEmpNo;

    @ApiModelProperty("权限ID")
    private Integer authId;

    @ApiModelProperty("类型 1-收藏 2-最近使用")
    private String collectType;
}
