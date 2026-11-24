package com.yutong.safety.core.handler.enter;

import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;

import java.util.List;

public interface IncomingHandler {
    List<SafetyEnterCrk> selectSafetyEnterCrk(String id);
    String insertCarAuth(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list);
    String syncCrk(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list);
    String updateCrk(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list);

    void syncSap(List<SafetyEnterDockCrk> list);
}
