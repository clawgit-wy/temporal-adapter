package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.SafetyMonitorPostProcessDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YTSafetyMonitorPostMapper {

    Long insert(SafetyMonitorPostProcessDTO dto);

    Long updateData(SafetyMonitorPostProcessDTO dto);

    Long updateFlowId(@Param("id") Long id, @Param("flowId") String flowId, @Param("modelId") Long modelId);

    SafetyMonitorPostProcessDTO queryById(Long id);

    void insertCheckor(@Param("ids") List<Long> ids, @Param("checkUserno") List<String> checkUserno, @Param("id") Long id);

    IPage<SafetyMonitorPostProcessDTO> findPageList(Page<?> page, @Param("dto") SafetyMonitorPostProcessDTO processDTO);

    List<SafetyMonitorPostProcessDTO> findList(@Param("dto") SafetyMonitorPostProcessDTO processDTO);
}
