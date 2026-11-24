package com.yutong.modules.risk.constants.enums;

import io.swagger.models.auth.In;
import lombok.Getter;

/**
 * 风险等级枚举
 */
@Getter
public enum RiskLevelEnum {

    LOW("低风险", 1, "低风险","蓝"),
    GENERAL("一般风险", 2, "一般风险","黄"),
    MAJOR("较大风险", 3, "较大风险","橙"),
    CRITICAL("重大风险", 4, "重大风险","红");

    private final String code;
    private final Integer sort;
    private final String desc;
    private final String color;

    RiskLevelEnum(String code, Integer sort, String desc, String color) {
        this.code = code;
        this.sort = sort;
        this.desc = desc;
        this.color=color;
    }

    public static RiskLevelEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (RiskLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
