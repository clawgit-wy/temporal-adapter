package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsProcessGooutListParam extends WebPageParam {

    private String submitNo;
    private String status;
    @ApiModelProperty("分页标识-后台使用")
    private Integer begin;
}
