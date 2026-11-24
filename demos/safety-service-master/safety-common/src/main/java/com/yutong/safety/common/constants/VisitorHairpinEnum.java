package com.yutong.safety.common.constants;

public enum VisitorHairpinEnum {
    WFK(0,"未发卡"),
    YFK(1,"已发卡"),
    YTK(2,"已退卡"),
    SPZ(3,"审批中"),
    CSGB(4,"超时关闭"),
    WDF(5,"未到访"),
    BH(6,"驳回"),
    FQ(7,"废弃"),
    YJC(8,"已进厂"),
    YCC(9,"已出厂"),
    ZL(10,"滞留");

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    VisitorHairpinEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
