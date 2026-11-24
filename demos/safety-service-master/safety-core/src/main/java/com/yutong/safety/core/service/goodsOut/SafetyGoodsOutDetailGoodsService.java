package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDOExample;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailGoodsListParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailListParam;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutDetailGoodsVO;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutDetailGoodsDOBizMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyGoodsOutDetailGoodsService {

    @Autowired
    private SafetyGoodsOutDetailGoodsDOBizMapper safetyGoodsOutDetailGoodsDOMapper;

    public int save(SafetyGoodsOutDetailGoodsDO safetyGoodsOutDetailGoodsDO) {
        return safetyGoodsOutDetailGoodsDOMapper.insert(safetyGoodsOutDetailGoodsDO);
    }

    public int update(SafetyGoodsOutDetailGoodsDO safetyGoodsOutDetailGoodsDO) {
        return safetyGoodsOutDetailGoodsDOMapper.updateByPrimaryKeySelective(safetyGoodsOutDetailGoodsDO);
    }

    public List<SafetyGoodsOutDetailGoodsDO> selectList(GoodsOutDetailGoodsListParam goodsListParam){
        return safetyGoodsOutDetailGoodsDOMapper.selectByCondition(goodsListParam);
    }

    public long selectCount(GoodsOutDetailGoodsListParam goodsListParam){
        return safetyGoodsOutDetailGoodsDOMapper.countByCondition(goodsListParam);
    }

    public List<GoodsOutDetailGoodsVO> selectDetailList(GoodsOutDetailListParam goodsListParam){
        return safetyGoodsOutDetailGoodsDOMapper.selectDetailList(goodsListParam);
    }

    public long  selectDetailCount(GoodsOutDetailListParam goodsListParam){
        return safetyGoodsOutDetailGoodsDOMapper.selectDetailCount(goodsListParam);
    }

    public int modifyReceive(SafetyGoodsOutDetailReceiveDO receiveDO) {
        return safetyGoodsOutDetailGoodsDOMapper.modifyReceive(receiveDO);
    }

    public SafetyGoodsOutDetailReceiveDO getRecord(SafetyGoodsOutDetailReceiveDO receiveDO) {
        return safetyGoodsOutDetailGoodsDOMapper.getRecord(receiveDO);
    }

    public List<GoodsOutDetailGoodsVO> getListByProcessList(List<String> processIdList){
        GoodsOutDetailListParam en = new GoodsOutDetailListParam();
        en.setProcessIdList(processIdList);
        return safetyGoodsOutDetailGoodsDOMapper.getListByProcessList(en);
    }
}
