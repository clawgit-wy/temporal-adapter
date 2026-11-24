package com.yutong.safety.core.service;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.SafetyInterfaceLogDOExample;
import com.yutong.safety.core.db.entity.SafetyInterfaceLogDOWithBLOBs;
import com.yutong.safety.core.db.mapper.SafetyInterfaceLogDOMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SafetyInterfaceLogService {

    @Resource
    private SafetyInterfaceLogDOMapper safetyInterfaceLogDOMapper;

    public void save(SafetyInterfaceLogDOWithBLOBs data) {
        safetyInterfaceLogDOMapper.insert(data);
    }

    public Long selectCount() {
        SafetyInterfaceLogDOExample example = new SafetyInterfaceLogDOExample();
        return safetyInterfaceLogDOMapper.countByExample(example);
    }

    public void backupInterfaceLog() {
        String timeFormat = DateUtils.format(new Date(), 10);
        String tableName = "safety.safety_interface_log";
        String backupTableName = "safety.tmp_backup_" + timeFormat + "_safety_interface_log";
        safetyInterfaceLogDOMapper.updateReName(tableName, backupTableName);
        safetyInterfaceLogDOMapper.copyTableName(tableName, backupTableName);
    }
}
