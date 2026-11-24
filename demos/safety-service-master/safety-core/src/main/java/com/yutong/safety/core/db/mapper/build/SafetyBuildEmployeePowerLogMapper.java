package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildEmployeePowerLogMapper {

    int batchInsert(@Param(value = "list") List<SafetyBuildEmployeePowerLog> list);
}
