package com.yutong.safety.common.constants;

/**
 * Created by gwcuiyx on 2023-08-04
 */
public enum BlackStateEnum {
    PROHIBIT("禁止入厂"), APPROVED("特批进厂");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    BlackStateEnum(String name) {
        this.name = name;
    }
}
