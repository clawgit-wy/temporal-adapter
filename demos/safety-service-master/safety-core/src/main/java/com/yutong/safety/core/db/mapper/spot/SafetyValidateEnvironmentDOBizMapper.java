package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDOExample;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsDetails;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutValidDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyValidateEnvironmentDOBizMapper {
    //查询
    List<SafetyValidateEnvironmentDO> selectByExampleList(SafetyValidateEnvironmentDO example);
    Integer selectByExampleCount(SafetyValidateEnvironmentDO example);

    List<SafetyGoodsOutValidDTO> getPageList(SafetyGoodsOutValidDTO en);
    Long getPageListCount(SafetyGoodsOutValidDTO en);

    List<SafetyGoodsDetails> getDetailInfoAll(@Param("processId") String processId);
}