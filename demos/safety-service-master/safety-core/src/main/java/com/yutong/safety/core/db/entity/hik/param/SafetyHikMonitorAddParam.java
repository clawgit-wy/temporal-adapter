package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import lombok.Data;

@Data
public class SafetyHikMonitorAddParam extends SafetyHikMonitorDO implements Check {
    @Override
    public void check() {

    }
}
