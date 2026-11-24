package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildManageProcessDO implements Serializable {

    private String id;
    private String bpmFlowId;
    private String processCode;
    private String processSubject;
    private String processStatus;
    private String roleType;
    private String createEmployeeNo;
    private String createEmployeeLogin;
    private String createEmployeeName;
    private String createDeptCode;
    private String createDeptName;
    private String dutyDeptCode;
    private String dutyDeptName;
    private String oldDeviceCode;
    private String oldDeviceName;
    private String newDeviceCode;
    private String newDeviceName;
    private String oldDepartEmployeeLogin;
    private String oldDepartEmployeeName;
    private String neweDepartEmployeeLogin;
    private String newDepartEmployeeName;
    private String leaderEmployeeLogin;
    private String leaderEmployeeName;
    private String topSecretFlag;
    private String topSecretEmployeeLogin;
    private String topSecretEmployeeName;
    private String patrolEmployeeLogin;
    private String patrolEmployeeName;
    private String sendCopyEmployeeLogin;
    private String sendCopyEmployeeName;
    private String curNodeHandler;
    private String curHandlerName;
    private String curNodeId;
    private String curNodeName;
    private String nodeFlag;
    private String createTime;
    private String applyStatus;
    private String refuseReason;
    private String applyEmployee;
    private String applyRole;
    private String applyTime;
    private Integer devicedChange;
    private String newDepartEmployeeCode;
    private String oldDepartEmployeeCode;
    private String dutyDeptCodePath;
    private String combFactoryName;
    private String combBuild;
    private String buildSysManagerEip;
    private String buildSysManagerRole;
    private String buildSysManagerRoleCode;
    private static final long serialVersionUID = 1L;

    private String remark;
}