package com.yutong.modules.risk.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface SafetyWaterFlowNoMapper {
    int getNowByTypeNoDay(String type);

    int getNowByType(String type);

    void insertNew(@Param("id")Long id, @Param("type") String type, @Param("next") int next);

    void updateNew(@Param("type") String type, @Param("next") int next);

    int getNowByTypeOrDate(@Param("type") String type,@Param("curDate") Date curDate);

    void insertTypeOrDate(@Param("id")Long id, @Param("type") String type, @Param("next") int next,@Param("curDate") Date curDate);

    void updateTypeOrDate(@Param("type") String type, @Param("next") int next, @Param("curDate") Date curDate);

}
