package com.yutong.modules.common.service.impl;

import com.yutong.modules.common.api.ISafetyInterfaceLogService;
import com.yutong.modules.common.entity.SafetyInterfaceLog;
import com.yutong.modules.common.mapper.SafetyInterfaceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SafetyInterfaceLogServiceImpl implements ISafetyInterfaceLogService {

    @Autowired
    private SafetyInterfaceLogMapper safetyInterfaceLogMapper;

    @Override
    public void saveInterfaceLog(SafetyInterfaceLog interfaceLog) {
        safetyInterfaceLogMapper.insert(interfaceLog);
    }
}
