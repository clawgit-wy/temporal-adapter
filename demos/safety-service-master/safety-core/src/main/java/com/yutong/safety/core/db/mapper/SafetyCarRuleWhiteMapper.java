package com.yutong.safety.core.db.mapper;

import com.yutong.safety.common.param.web.CarRuleWhiteListParam;
import com.yutong.safety.core.db.entity.SafetyCarRuleWhiteDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyCarRuleWhiteMapper{

    List<SafetyCarRuleWhiteDO> selectList(@Param("param") CarRuleWhiteListParam param, @Param("start") int start, @Param("size") int size);

    long countList(CarRuleWhiteListParam param);

    int insert(CarRuleWhiteListParam param);

    int update(CarRuleWhiteListParam param);

    int updateDelFlagByPrimaryKey(int id);

    List<String> findAll();

    int batchInsert(List<CarRuleWhiteListParam> insertList);

    int batchUpdate(List<CarRuleWhiteListParam> updateList);
}