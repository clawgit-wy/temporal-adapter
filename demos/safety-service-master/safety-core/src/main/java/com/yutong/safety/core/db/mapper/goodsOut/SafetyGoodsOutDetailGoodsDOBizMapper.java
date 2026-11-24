package com.yutong.safety.core.db.mapper.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDOExample;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailGoodsListParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailListParam;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutDetailGoodsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyGoodsOutDetailGoodsDOBizMapper extends SafetyGoodsOutDetailGoodsDOMapper{

    List<SafetyGoodsOutDetailGoodsDO> selectByCondition(GoodsOutDetailGoodsListParam example);

    long countByCondition(GoodsOutDetailGoodsListParam example);

    List<GoodsOutDetailGoodsVO> selectDetailList(GoodsOutDetailListParam example);

    long selectDetailCount(GoodsOutDetailListParam example);

    int modifyReceive(SafetyGoodsOutDetailReceiveDO record);
    SafetyGoodsOutDetailReceiveDO getRecord(SafetyGoodsOutDetailReceiveDO record);

    List<GoodsOutDetailGoodsVO> getListByProcessList(GoodsOutDetailListParam en);
}