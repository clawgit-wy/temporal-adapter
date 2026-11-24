package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigHistoryDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigHistoryDOExample;
import com.yutong.safety.core.db.entity.entry.param.EntryConfigHistoryQueryParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryFactoryRecordVO;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEntryConfigHistoryDOMapper extends SafetyWebBaseMapper<SafetyEntryConfigHistoryDO, SafetyEntryConfigHistoryDOExample> {

    List<EntryFactoryRecordVO> tallyEntryList(@Param("params") EntryConfigHistoryQueryParams params);

    int insertBatch(@Param("list") List<SafetyEntryConfigHistoryDO> list);

    int batchSave(@Param("list") List<SafetyEntryConfig> list);
}