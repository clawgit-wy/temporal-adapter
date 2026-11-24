package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigOperateDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigOperateDOExample;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEntryConfigOperateDOMapper
        extends SafetyWebBaseMapper<SafetyEntryConfigOperateDO, SafetyEntryConfigOperateDOExample> {

    void batchSave(@Param("list") List<SafetyEntryConfigOperateDO> list);
}