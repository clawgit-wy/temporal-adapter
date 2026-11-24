package com.yutong.modules.risk.constants.enums;

import lombok.Getter;

@Getter
public enum RiskTypeEnum {
    XF("消防", 1, "消防"),
    ZA("治安", 2, "治安");

    private final String code;
    private final Integer sort;
    private final String desc;

    RiskTypeEnum(String code, Integer sort, String desc) {
        this.code = code;
        this.sort = sort;
        this.desc = desc;
    }
}
