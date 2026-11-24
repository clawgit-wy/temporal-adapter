package com.yutong.safety.common.constants;

import com.alibaba.excel.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * eip对应操作状态
 */
public enum EipStateEnum {
    RELEASE("30"),
    TIMEOUT("40"),
    DISCARD("00"),
    REJECT("11"),
    REJECT_DIY("10");

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    EipStateEnum(String code) {
        this.code = code;
    }

    private static final Map<String, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put("10","废弃");
        STATUS_MAP.put("20","审批中");
        STATUS_MAP.put("30","审批通过");
    }

    public static void registerStatus(String code, String desc) {
        STATUS_MAP.put(code,  desc); // 支持动态注册
    }

    public static String getStatusDesc(String status) {
        if (StringUtils.isBlank(status)) return "状态值为空";
        return STATUS_MAP.getOrDefault(status,  "未定义状态(" + status + ")");
    }
}
