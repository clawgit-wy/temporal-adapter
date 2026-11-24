package com.yutong.safety.common.constants;

/**
 * 人员类型
 */
public enum UserTypeEnum {
    ZS("正式员工"), LD("绿都员工"), WX("外协员工");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    UserTypeEnum(String name) {
        this.name = name;
    }
}
