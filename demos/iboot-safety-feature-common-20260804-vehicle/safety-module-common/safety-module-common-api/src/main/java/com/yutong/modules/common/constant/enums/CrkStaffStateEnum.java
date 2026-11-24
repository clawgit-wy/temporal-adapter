package com.yutong.modules.common.constant.enums;

public enum CrkStaffStateEnum {
    FACE_ISSUE(0,"新增照片"),
    FACE_MODIFY(1,"修改照片"),
    STAFF_STATE(2,"员工状态");

    private int code;
    private String message;

    CrkStaffStateEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
}
