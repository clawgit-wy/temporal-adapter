package com.yutong.safety.common.constants;

public enum RuleStateEnum {
    YES(true),NO(false);

    private boolean code;

    RuleStateEnum(boolean code) {
        this.code = code;
    }

    public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }
}
