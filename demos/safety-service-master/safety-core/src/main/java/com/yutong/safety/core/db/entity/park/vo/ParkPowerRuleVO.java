package com.yutong.safety.core.db.entity.park.vo;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import lombok.Data;

@Data
public class ParkPowerRuleVO extends SafetyParkPowerRuleDO {

    private String orgName;

    private String orgCodePath;

    private String orgNamePath;

    private String createTimeStr;

    private String updateTimeStr;

    private String empNo;
}
