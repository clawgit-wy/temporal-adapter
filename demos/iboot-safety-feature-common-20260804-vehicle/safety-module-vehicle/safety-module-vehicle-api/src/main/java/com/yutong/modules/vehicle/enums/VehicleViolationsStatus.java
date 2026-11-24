package com.yutong.modules.vehicle.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 隐患上报状态枚举
 */
@Getter
public enum VehicleViolationsStatus {

    DRAFT(0, "草稿"),
    APPROVING(1, "审批中"),
    RECONSIDER(2, "复议"),
    RECONSIDER_APPROVING(3, "复议审批"),
    PUNISHMENT (4, "处罚已完成"),
    PUNISHMENT_ENDS  (5, "不处罚已完成"),
    ABORTED(30, "废弃");

    private final int code;
    private final String desc;

    VehicleViolationsStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VehicleViolationsStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(s -> s.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的状态码: " + code));
    }
}
