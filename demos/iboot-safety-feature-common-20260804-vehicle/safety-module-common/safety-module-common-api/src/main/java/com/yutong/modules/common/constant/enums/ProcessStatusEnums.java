package com.yutong.modules.common.constant.enums;

import lombok.Getter;

/**
 * 流程状态
 */
@Getter
public enum ProcessStatusEnums {

    //草稿
    draft("draft", 0),

    //审批中
    approve("approve", 1),

    //完成
    finish("finish", 2),

    //废弃
    discarded("discarded", 3),

    //驳回
    deny("deny", 4);
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
    ProcessStatusEnums(String type, int code) {
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
