package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildDepartChangeDO implements Serializable {
    private Integer id;

    private String employeeCode;

    private String employeeName;

    private String oldDeptCode;

    private String oldDeptName;

    private String newDeptCode;

    private String newDeptName;

    private String createTime;

    private String updateTime;

    private String buildConfirmFlag;

    private String whiteConfirmFlag;

    private String dutyDeptCode;

    private String dutyDeptName;

    private String dutyEmpCode;

    private String dutyEmpName;

    /**
     * 原部门责任人
     */
    private String oldDutyEmpCode;
    private String oldDutyEmpName;

    private static final long serialVersionUID = 1L;

}