package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEntryBlankProcessDetailDOMapper extends SafetyWebBaseMapper<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample> {

    List<SafetyEntryBlankProcessDetailDO> selectProcessAndDetail(SafetyEntryBlankProcessDetailQueryParams param);

    long selectProcessAndDetailCount(SafetyEntryBlankProcessDetailQueryParams param);

    void batchSave(@Param("list") List<SafetyEntryBlankProcessDetailDO> detailList);

    void batchUpdate(List<SafetyEntryBlankProcessDetailDO> detailList);
}