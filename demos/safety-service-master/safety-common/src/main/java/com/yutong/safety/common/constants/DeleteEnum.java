package com.yutong.safety.common.constants;

/**
 * @author caomg
 * @date 2024-8-8 14:49
 * @description DeleteEnum
 */
public enum DeleteEnum {
    DELETE("1","删除"),
    UN_DELETE("0","未删除"),
    EDIT_STATUS("0","编辑"),
    USE_STATUS("1","使用"),
    SIGN_OUT_STATUS("2","注销")
    ;

    private String code;
    private String desc;

    DeleteEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
