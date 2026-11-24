package com.yutong.modules.risk.enums;

import lombok.Getter;
import java.util.Arrays;

/**
 * 隐患上报状态枚举
 */
@Getter
public enum HazardReportStatus {

    DRAFT(0, "草稿"),
    APPROVING(1, "审批中"),
    RECTIFYING(2, "整改中"),
    VERIFYING(3, "验证中"),
    PARTIAL(4, "部分完成"),
    DONE(5, "已完成"),
    EXPECTED_INCOMPLETE(6, "预期未完成"),
    OVERDUE(7, "逾期完成"),
    DELAY_CHECK(8, "延期审批中"),
    DRAW_UP(9, "起草"),
    ABORTED(30, "废弃");

    private final int code;
    private final String desc;

    HazardReportStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HazardReportStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(s -> s.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的状态码: " + code));
    }
}
