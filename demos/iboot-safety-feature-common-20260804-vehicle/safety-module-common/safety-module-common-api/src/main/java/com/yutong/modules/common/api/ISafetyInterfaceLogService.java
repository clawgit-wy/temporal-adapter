package com.yutong.modules.common.api;

import com.yutong.modules.common.entity.SafetyInterfaceLog;

public interface ISafetyInterfaceLogService {
    /**
     * 保存
     * @param interfaceLog
     */
    public void saveInterfaceLog(SafetyInterfaceLog interfaceLog);
}
