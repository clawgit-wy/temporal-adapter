package com.yutong.safety.core.db.mapper.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDOExample;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;

import java.util.List;

public interface SafetyHikAbnormalEnterDOMapper extends SafetyWebBaseMapper<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample> {

    void batchSave(List<SafetyHikAbnormalEnterDO> list);
}