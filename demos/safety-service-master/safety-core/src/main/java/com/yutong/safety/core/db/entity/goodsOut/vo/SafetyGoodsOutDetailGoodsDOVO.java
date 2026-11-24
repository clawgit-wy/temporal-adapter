package com.yutong.safety.core.db.entity.goodsOut.vo;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author caomg
 * @date 2024-9-29 13:32
 * @description SafetyGoodsOutDetailGoodsDOVO
 */
public class SafetyGoodsOutDetailGoodsDOVO extends SafetyGoodsOutDetailGoodsDO {
    @ApiModelProperty("货物是否验证一致(0:否;1:是)")
    private String isAccord;

    public String getIsAccord() {
        return isAccord;
    }

    public void setIsAccord(String isAccord) {
        this.isAccord = isAccord;
    }
}
