package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.dto.SafetyEmployeeMessageDTO;

import java.util.List;

public interface SafetyBuildWhiteRuleDTOMapper extends SafetyBuildWhiteRuleDOMapper {
    List<SafetyEmployeeMessageDTO> selectRuleUser();
}