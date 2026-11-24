package com.yutong.safety.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEmployeeMessageDTO implements Serializable {

    private Integer id;


    private String empNo;


    private String empName;


    private String phoneNo;


    private String userType;


    private String positionCode;


    private String positionName;


    private String positionType;


    private String orgNamePath;


    private String orgCodePath;


    private String parentCode;


    private String orgCode;


    private String orgName;

    private String email;


    private Date createTime;


    private Date updateTime;


    private String remark;


    private String status;

    /**
     * 员工登录名
     */
    private String empLogin;

    private Integer whiteId;
    private String whiteReason;
    private String whiteTime;
    private String whiteCreateBy;

    private static final long serialVersionUID = 1L;

}