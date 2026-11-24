package org.jeecg.common.constant.enums;

/**
 * 用户状态 正常:NORMAL,禁用:DISABLED,锁定:LOCKED,删除:DELETE
 */
public enum UserStatusEnum {


    NORMAL("NORMAL", 1),

    DISABLED("DISABLED", 2),

    LOCKED("LOCKED", 3),

    DELETE("DELETE", 4);

    /**
     * 用户状态 正常:NORMAL,禁用:DISABLED,锁定:LOCKED,删除:DELETE
     */
    String type;

    /**
     * 编码
     */
    int code;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 构造器
     *
     * @param type 类型
     * @param code 编码(请求方式)
     */
    UserStatusEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static Integer getCodeByTypeName(String typeName) {
        for (UserStatusEnum e : UserStatusEnum.values()) {
            if (typeName.startsWith(e.getType())) {
                return e.getCode();
            }
        }
        return 1;
    }
}
