package com.yutong.modules.user.constant.enums;

import lombok.Getter;

/**
 * 系统用户类型
 */
@Getter
public enum SystemUserTypeEnum {

    //非安保人员
    UnSafety("unSafety", 0),

    //安保人员
    Safety("Safety", 1);

    /**
     * 用户状态 正常:NORMAL,禁用:DISABLED,锁定:LOCKED,删除:DELETE
     */
    private String type;

    /**
     * 编码
     */
    private int code;


    /**
     * 构造器
     *
     * @param type 类型
     * @param code 编码(请求方式)
     */
    SystemUserTypeEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static Integer getCodeByTypeName(String typeName) {
        for (SystemUserTypeEnum e : SystemUserTypeEnum.values()) {
            if (typeName.equals(e.getType())) {
                return e.getCode();
            }
        }
        return 0;
    }
}
