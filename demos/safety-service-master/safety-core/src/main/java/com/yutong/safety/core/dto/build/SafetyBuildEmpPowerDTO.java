package com.yutong.safety.core.dto.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import lombok.Data;

import java.util.Set;

@Data
public class SafetyBuildEmpPowerDTO extends SafetyBuildEmployeePowerDO {

    private Set<String> deviceSet;
}
