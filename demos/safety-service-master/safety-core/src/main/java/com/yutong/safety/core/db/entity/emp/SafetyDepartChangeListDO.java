package com.yutong.safety.core.db.entity.emp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyDepartChangeListDO implements Serializable {
    private Integer id;

    private String employeeCode;

    private String employeeName;

    private String oldDeptCode;

    private String oldDeptName;

    private String newDeptCode;

    private String newDeptName;

    private String createTime;

    private String updateTime;

    private String changeType;

    @ApiModelProperty("责任部门编码")
    private String dutyDeptCode;

    @ApiModelProperty("责任部门")
    private String dutyDeptName;

    @ApiModelProperty("责任人员工号")
    private String dutyEmpCode;

    @ApiModelProperty("责任人")
    private String dutyEmpName;

    private String buildConfirmFlag;

    private String whiteConfirmFlag;

    private static final long serialVersionUID = 1L;

}