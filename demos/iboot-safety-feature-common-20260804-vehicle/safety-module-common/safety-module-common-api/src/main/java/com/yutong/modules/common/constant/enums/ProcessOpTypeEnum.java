package com.yutong.modules.common.constant.enums;

import lombok.Getter;

/**
 * 流程操作类型
 */
@Getter
public enum ProcessOpTypeEnum {

    //草稿
    draft("draft", 0),

    //创建并启用流程
    submit("submit", 1);

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
    ProcessOpTypeEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static Integer getCodeByTypeName(String typeName) {
        for (ProcessOpTypeEnum e : ProcessOpTypeEnum.values()) {
            if (typeName.equals(e.getType())) {
                return e.getCode();
            }
        }
        return 0;
    }
}
