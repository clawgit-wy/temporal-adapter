package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterHistoryDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterHistoryDOExample;
import java.util.List;

import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SafetyEntryCompanyRegisterHistoryDOMapper extends SafetyWebBaseMapper<SafetyEntryCompanyRegisterHistoryDO, SafetyEntryCompanyRegisterHistoryDOExample> {

    void saveSelect(@Param("empNo") String empNo);
}