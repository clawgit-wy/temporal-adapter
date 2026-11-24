package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.dto.SafetyMonitorPostProcessDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyMonitorPostMapper {

    int insert(SafetyMonitorPostProcessDTO dto);

    int updateData(SafetyMonitorPostProcessDTO dto);

    SafetyMonitorPostProcessDTO queryById(int id);

    void insertCheckor(@Param("checkUserno") List<String> checkUserno, @Param("id") Integer id);

    List<SafetyMonitorPostProcessDTO> findPageList(@Param("dto") SafetyMonitorPostProcessDTO processDTO, @Param("start") int start);

    int findCount(@Param("dto") SafetyMonitorPostProcessDTO processDTO);

    List<SafetyMonitorPostProcessDTO> findList(@Param("dto") SafetyMonitorPostProcessDTO processDTO);
}
