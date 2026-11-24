package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDOExample;
import java.util.List;

import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SafetyEntryImportFailDOMapper extends SafetyWebBaseMapper<SafetyEntryImportFailDO, SafetyEntryImportFailDOExample> {
    void batchSave(@Param("list") List<SafetyEntryImportFailDO> list);

    List<SafetyEntryBlankProcessDetailDO> getDtoListByVersionNo(String version);
}