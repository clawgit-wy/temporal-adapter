package com.yutong.safety.core.db.mapper;

import org.apache.ibatis.annotations.Param;

public interface SafetyWaterFlowNoMapper {
    int getNowByTypeNoDay(String type);

    int getNowByType(String type);

    void insertNew(@Param("type") String type, @Param("next") int next);

    void updateNew(@Param("type") String type, @Param("next") int next);
}
