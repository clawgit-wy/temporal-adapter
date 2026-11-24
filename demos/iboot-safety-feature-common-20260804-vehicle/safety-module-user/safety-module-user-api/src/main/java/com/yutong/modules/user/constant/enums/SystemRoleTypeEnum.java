package com.yutong.modules.user.constant.enums;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jeecg.common.constant.enums.UserStatusEnum;

/**
 * 系统角色类型
 */
@Getter
public enum SystemRoleTypeEnum {

    //非安保角色
    UnSafety("unSafety", 0),

    //安保角色
    Safety("Safety", 1),

    //系统角色
    System("System", 2);

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
    SystemRoleTypeEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static Integer getCodeByTypeName(String typeName) {
        for (SystemRoleTypeEnum e : SystemRoleTypeEnum.values()) {
            if (typeName.equals(e.getType())) {
                return e.getCode();
            }
        }
        return 0;
    }

}
