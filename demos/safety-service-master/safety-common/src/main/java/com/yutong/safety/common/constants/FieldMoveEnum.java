package com.yutong.safety.common.constants;

/**
 * Created by gwcuiyx on 23-07-12.
 */
public enum FieldMoveEnum {

    UP(0), DOWN(1);

    private int type;

    FieldMoveEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
