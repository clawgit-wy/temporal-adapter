package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessLogDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessLogDOExample;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEntryBlankProcessLogDOMapper extends SafetyWebBaseMapper<SafetyEntryBlankProcessLogDO, SafetyEntryBlankProcessLogDOExample> {

    void batchInsert(@Param("list") List<SafetyEntryBlankProcessLogDO> list);
}