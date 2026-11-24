package com.yutong.safety.core.db.entity.park;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyParkEmployeePowerHistoryDO implements Serializable {

    private Integer id;

    private String employeeCode;

    private String factoryId;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private String factoryName;

    private String employeeName;

    private byte[] details;

    private static final long serialVersionUID = 1L;

}