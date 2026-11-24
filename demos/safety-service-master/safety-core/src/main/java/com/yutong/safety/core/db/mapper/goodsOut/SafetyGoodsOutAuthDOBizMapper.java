package com.yutong.safety.core.db.mapper.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutAuthDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthListParam;

import java.util.List;

public interface SafetyGoodsOutAuthDOBizMapper extends SafetyGoodsOutAuthDOMapper{

    List<SafetyGoodsOutAuthDO> selectAuthList(GoodsOutAuthListParam param);

    long selectAuthCount(GoodsOutAuthListParam param);
}