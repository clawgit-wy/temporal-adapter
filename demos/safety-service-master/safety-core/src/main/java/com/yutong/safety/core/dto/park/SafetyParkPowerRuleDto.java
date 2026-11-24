package com.yutong.safety.core.dto.park;

import lombok.Data;

import java.util.Set;

@Data
public class SafetyParkPowerRuleDto {

    /**
     * 规则数据ID
     */
    private Integer id;
    /**
     * 规则信息，岗位和组织
     */
    private String name;
    /**
     * 厂区编码
     */
    private String factoryPower;
    /**
     * 符合的人员信息
     */
    private Set<String> empList;

}
