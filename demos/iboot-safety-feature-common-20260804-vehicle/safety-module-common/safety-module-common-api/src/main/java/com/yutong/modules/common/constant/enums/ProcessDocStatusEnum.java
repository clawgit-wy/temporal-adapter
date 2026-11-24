package com.yutong.modules.common.constant.enums;

import lombok.Getter;

@Getter
public enum ProcessDocStatusEnum {

    //草稿
    draft("draft", 10),

    //撤回
    recall("recall", 12),

    //流转中
    circulation("circulation", 20),

    //废弃
    error("error", 21),

    //驳回
    deny("deny", 11);

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
    ProcessDocStatusEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static Integer getCodeByTypeName(String typeName) {
        for (ProcessStatusEnums e : ProcessStatusEnums.values()) {
            if (typeName.equals(e.getType())) {
                return e.getCode();
            }
        }
        return 0;
    }
}
