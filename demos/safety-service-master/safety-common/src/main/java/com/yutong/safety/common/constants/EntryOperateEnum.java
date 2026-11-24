package com.yutong.safety.common.constants;

import com.alibaba.excel.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * eip对应操作状态
 */
public enum EntryOperateEnum {
    OPER_INSERT("新增"),
    OPER_UPDATE("编辑"),
    OPER_AUDIT("审核通过"),
    OPER_REFUSE("驳回"),
    OPER_DELETE("注销"),
    OPER_EXTENSION("续期");

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    EntryOperateEnum(String code) {
        this.code = code;
    }

}
