package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildDepartConfirmDO implements Serializable {

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

    //对接部门
    private String corresDeptCode;

    private String corresDeptName;
    //对接部门
    private String corresEmpCode;

    private String corresEmpName;

    private String confirmFlag;

    private String corresFlag;

    private String confirmEmpCode;

    private String confirmEmpName;

    private String confirmRoleType;

    private String createTime;

    private String updateTime;

    /**
     * 原部门责任人
     */
    private String oldDutyEmpCode;
    private String oldDutyEmpName;
}