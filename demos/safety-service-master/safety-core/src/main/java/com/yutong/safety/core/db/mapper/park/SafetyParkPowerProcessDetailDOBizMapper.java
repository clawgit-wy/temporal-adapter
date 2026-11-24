package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkPowerProcessDetailDOBizMapper extends SafetyParkPowerProcessDetailDOMapper {
    void batchSave(@Param("list") List<SafetyParkPowerProcessDetailDO> list);
    void updateByProId(@Param("pid")String pid);
}