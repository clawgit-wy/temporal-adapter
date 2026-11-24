package com.yutong.safety.core.db.entity.park.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import lombok.Data;

@Data
public class ParkPowerRuleAddParam extends SafetyParkPowerRuleDO implements Check{
    @Override
    public void check() {
    }
}
