package com.yutong.safety.core.db.mapper.spot;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetySpotRulePermDOBizMapper extends SafetySpotRulePermDOMapper {

    /**
     * 批量添加
     * @param spotId
     * @param ruleList
     */
    void batchInsert(@Param("spotId") Integer spotId, @Param("blackList") List<Integer> ruleList);
}