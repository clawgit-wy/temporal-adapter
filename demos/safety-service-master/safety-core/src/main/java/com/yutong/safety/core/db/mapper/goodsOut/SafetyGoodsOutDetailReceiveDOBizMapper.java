package com.yutong.safety.core.db.mapper.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyGoodsOutDetailReceiveDOBizMapper {
    void updateByCondition(SafetyGoodsOutDetailReceiveDO safetyGoodsOutDetailReceiveDO);

    void updateReceiveInfo(@Param("changeList") List<SafetyGoodsOutDetailReceiveDO> changeList);
}