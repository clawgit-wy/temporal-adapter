package com.yutong.safety.core.db.mapper.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDOExample;
import java.util.List;

import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SafetyHikMonitorDOMapper extends SafetyWebBaseMapper<SafetyHikMonitorDO, SafetyHikMonitorDOExample> {

    void batchSave(@Param("list") List<SafetyHikMonitorDO> hikList);

    void batchUpdate(@Param("list") List<SafetyHikMonitorDO> updateList);
}