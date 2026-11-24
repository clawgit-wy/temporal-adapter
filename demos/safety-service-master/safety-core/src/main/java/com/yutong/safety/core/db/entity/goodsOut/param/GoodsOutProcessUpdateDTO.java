package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.ProcessParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsOutProcessUpdateDTO extends GoodsOutProcessUpdateParam {
    @ApiModelProperty("流程参数")
    private ProcessParam processParam;
}
