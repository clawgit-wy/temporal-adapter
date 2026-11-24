package com.yutong.safety.core.db.entity.build;

import lombok.Data;

@Data
public class SafetyBuildEmployeePowerLog {

    private String controllerMethod;
    private String serviceMethod;
    private String requestParam;
    private String empNo;
    private String deviceCode;
    private String remark;
    private String createBy;
}
