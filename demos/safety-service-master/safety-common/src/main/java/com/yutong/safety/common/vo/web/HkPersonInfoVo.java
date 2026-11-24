package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.util.Date;

/**
 * 海康人员信息返回实体类
 */
@Data
public class HkPersonInfoVo {
    private String personId;
    private String personName;
    private Integer gender;
    private String orgPath;
    private String orgIndexCode;
    private String orgName;
    private Integer certificateType;
    private String certificateNo;
    private String fingerPrint;
    private Date updateTime;
    private Date birthday;
    private String phoneNo;
    private String address;
    private String email;
    private String education;
    private String nation;
    private String jobNo;
}
