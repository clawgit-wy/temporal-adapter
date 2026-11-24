package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetySpotCheckRuleDOBizMapper extends SafetySpotCheckRuleDOMapper {

    /**
     * 批量添加
     * @param blackList
     */
    public int batchInsert(@Param("blackList") List<SafetySpotCheckRuleDO> blackList);

    public List<SafetySpotCheckRuleDO> selectRuleList(Map<String,Object> param);

    long countByExampleNew(SafetySpotCheckRuleDOExample example);

    List<SafetySpotCheckRuleDO> selectByExampleNew(SafetySpotCheckRuleDOExample example);

    SafetySpotCheckRuleDO selectByPrimaryKeyNew(Integer id);
}