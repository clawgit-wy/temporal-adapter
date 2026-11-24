package com.yutong.safety.core.db.mapper.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDOExample;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyHikGroupDOMapper extends SafetyWebBaseMapper<SafetyHikGroupDO, SafetyHikGroupDOExample> {

    void batchSave(@Param("list") List<SafetyHikGroupDO> list);

    void batchUpdate(@Param("list") List<SafetyHikGroupDO> list);
}