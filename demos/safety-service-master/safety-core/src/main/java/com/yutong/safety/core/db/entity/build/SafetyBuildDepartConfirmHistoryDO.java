package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildDepartConfirmHistoryDO implements Serializable {
    private Integer id;

    private String employeeCode;

    private String employeeName;

    private String oldDeptCode;

    private String oldDeptName;

    private String newDeptCode;

    private String newDeptName;

    private String oldDutyDeptCode;

    private String oldDutyDeptName;

    private String dutyDeptCode;

    private String dutyDeptName;

    private String dutyEmpCode;

    private String dutyEmpName;

    private String deviceCode;

    private String deviceName;

    private String confirmFlag;

    private String corresFlag;

    private String corresDeptCode;

    private String corresDeptName;

    private String confirmEmpCode;

    private String confirmRoleType;

    private String createTime;

    private String updateTime;

    private String corresEmpCode;

    private String createBy;

    private static final long serialVersionUID = 1L;
}