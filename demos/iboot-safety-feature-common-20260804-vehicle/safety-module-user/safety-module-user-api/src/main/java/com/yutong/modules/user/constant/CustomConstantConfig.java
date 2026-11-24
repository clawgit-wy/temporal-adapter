package com.yutong.modules.user.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 自定义配置
 */
@Component("customConstantConfig")
public class CustomConstantConfig {

    @Value("${ytsafety.custom.constant.sysAdminRoleCode}")
    private String sysAdminRoleCode;

    @Value("${ytsafety.custom.constant.sysGuestRoleCode}")
    private String sysGuestRoleCode;

    @Value("${ytsafety.custom.constant.topParentCode}")
    private String topParentCode;

    public String getSysAdminRoleCode() {
        return sysAdminRoleCode;
    }

    public String getSysGuestRoleCode() {
        return sysGuestRoleCode;
    }

    public String getTopParentCode() {
        return topParentCode;
    }
}