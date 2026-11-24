package com.yutong.safety.common.constants;

/**
 * Created by gwcuiyx on 23-07-12.
 */
public enum SheetSetEnum {
    NO(false),YES(true);

    private boolean code;

    SheetSetEnum(boolean code) {
        this.code = code;
    }

    public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }
}
