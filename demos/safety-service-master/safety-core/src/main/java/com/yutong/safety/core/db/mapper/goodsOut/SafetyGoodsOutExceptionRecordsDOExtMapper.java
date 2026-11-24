package com.yutong.safety.core.db.mapper.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionRecordsDO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionRequestDTO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionResponseDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyGoodsOutExceptionRecordsDOExtMapper {

    List<SafetyGoodsOutExceptionResponseDTO> pageList(SafetyGoodsOutExceptionRequestDTO en);
    Integer pageListCount(SafetyGoodsOutExceptionRequestDTO en);

    void saveBatch(@Param("list") List<SafetyGoodsOutExceptionRecordsDO> enList);

    List<SafetyGoodsOutDetailReceiveDO> getDetailReceiveList(Map<String,Object> map);

    void updateExceptionStatus(Map<String,Object> map);

}