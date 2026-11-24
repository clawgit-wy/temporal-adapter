package com.yutong.safety.core.db.entity.park.vo;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import lombok.Data;

@Data
public class ParkPowerRuleChangeVO extends SafetyParkPowerRuleDO {
    /**
     * 历史岗位名称
     */
    private String hisPostName;
    /**
     * 历史-组织编码
     */
    private String hisOrgCode;
    /**
     * 历史-授权厂区
     */
    private String hisFactoryPower;
}
