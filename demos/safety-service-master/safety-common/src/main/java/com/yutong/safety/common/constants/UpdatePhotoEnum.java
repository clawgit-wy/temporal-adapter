package com.yutong.safety.common.constants;


public enum UpdatePhotoEnum {
    FACEISSUE(0,"新增照片"),
    FACEMODIFY(1,"修改照片"),
    STAFFSTATE(2,"员工状态");

    private int code;
    private String message;

    UpdatePhotoEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
