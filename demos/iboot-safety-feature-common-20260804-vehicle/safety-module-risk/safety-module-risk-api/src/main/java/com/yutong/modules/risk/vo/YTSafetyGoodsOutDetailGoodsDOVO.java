package com.yutong.modules.risk.vo;

import com.yutong.modules.risk.out.YTSafetyGoodsOutDetailGoodsDO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author caomg
 * @date 2024-9-29 13:32
 * @description YTSafetyGoodsOutDetailGoodsDOVO
 */
public class YTSafetyGoodsOutDetailGoodsDOVO extends YTSafetyGoodsOutDetailGoodsDO {
    @ApiModelProperty("货物是否验证一致(0:否;1:是)")
    private String isAccord;

    public String getIsAccord() {
        return isAccord;
    }

    public void setIsAccord(String isAccord) {
        this.isAccord = isAccord;
    }
}
