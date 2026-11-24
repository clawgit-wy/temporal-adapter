package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class LdapUserVO{
    private Long id;
    private String eip;
    private String email;
    private String uid;
    private String name;
    private Date birthday;
    private String sex;
    private String mobile;
    private String telephone;
    private String street;
    private String isEnabled;
    private String headurl;
    private String signature;
    private String innerphone;
    private String empgroup;
    private String code;
    private String codename;
    private String codepath;
    private String namepath;
    private String positionCode;
    private String positionName;
    //是否EIP有效用户 0-否  1-是
    private Integer checkType;
}
