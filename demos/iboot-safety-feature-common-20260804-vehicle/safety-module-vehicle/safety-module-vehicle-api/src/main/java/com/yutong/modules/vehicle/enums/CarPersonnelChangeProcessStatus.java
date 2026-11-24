package com.yutong.modules.vehicle.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 车辆人员变更流程状态枚举
 */
@Getter
public enum CarPersonnelChangeProcessStatus {

    DRAFT(0, "草稿"),
    ADJUST(1, "调整中"),
    CHECK(2, "审核中"),
    DONE(3, "已完成"),
    DRAW_UP(9, "起草"),
    ABORTED(30, "废弃");

    private final int code;
    private final String desc;

    CarPersonnelChangeProcessStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CarPersonnelChangeProcessStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(s -> s.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的状态码: " + code));
    }
}
