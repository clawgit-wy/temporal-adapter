package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyBuildIssuedLogDO implements Serializable {
    private Integer id;

    private String employeeCode;

    private String employeeName;

    private String dutyDeptName;

    private String deviceCode;

    private String deviceName;

    private String doorName;

    private String beginTime;

    private String endTime;

    private String sourceType;

    private String targetId;

    private String issuedType;

    private String remark;

    private String createBy;

    private Date createTime;

    private String issuedStatus;

    private String dutyDeptCode;

    private String bpmFlowId;

    private String issuedRemark;

    private String roleType;

    private String modelId;

    private String exceptDeviceCode;

    private String exceptDeviceName;

    private String doorId;

    //设备类型 1-门禁 2-富士
    private String deviceType;

    private String createEmpNo;

    private String telePhone;

    private static final long serialVersionUID = 1L;

}