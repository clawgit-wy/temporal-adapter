package com.yutong.modules.risk.constants.enums;

import lombok.Getter;

@Getter
public enum ApplyTypeEnum {

    ADD(0, "新增"),
    CHANGE_ADD(1, "变更新增"),
    BATCH_ADD(2, "批量新增"),
    CHANGE_MODIFY(3, "变更修改"),
    BATCH_PUSH(4, "批量推送");

    private final Integer code;
    private final String desc;

    ApplyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
