package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.core.db.entity.spot.SafetyPatrolPositionValidQuestDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolPositionValidQuestDOExample;
import com.yutong.safety.core.dto.spot.SafetyPatrolPositionValidQuestDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyPatrolPositionValidQuestDOBizMapper {
    void insertSelectiveBiz(@Param("paramList")List<SafetyPatrolPositionValidQuestDO> paramList);
    void updateSelectiveBiz(@Param("paramList")List<SafetyPatrolPositionValidQuestDO> paramList);
    List<SafetyPatrolPositionValidQuestDO> getQuestList(@Param("paramMap") Map<String, Object> paramMap);

    List<SafetyPatrolPositionValidQuestDTO> getTaskList(SafetyPatrolPositionValidQuestDTO en);
}