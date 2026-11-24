package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildInOutHistoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildInOutHistoryDOBizMapper extends SafetyBuildInOutHistoryDOMapper{

    int batchUpdate(@Param("list") List<SafetyBuildInOutHistoryDO> list);

    Long getMaxHistoryId();
}