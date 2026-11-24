package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerLog;
import com.yutong.safety.core.db.mapper.build.SafetyBuildEmployeePowerLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyEmployeePowerLogService {

    @Resource
    private SafetyBuildEmployeePowerLogMapper safetyBuildEmployeePowerLogMapper;

    public void batchInsert(List<SafetyBuildEmployeePowerLog> saveLog) {
        safetyBuildEmployeePowerLogMapper.batchInsert(saveLog);
    }
}
