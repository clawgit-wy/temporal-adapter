package com.yutong.safety.common.vo.sys;

import lombok.Data;

import java.util.Date;

@Data
public class SysUserVO {
    private Integer id;
    private String userName;
    private String empName;
    private String empNo;
    private String modifyEmp;
    private String modifyEmpNo;
    private Date createTime;
    private Date updateTime;
    private String roleName;
    private String roleId;
    private String factoryId;
    private String factoryName;
    private String powerFactoryId;
    private String powerFactoryName;
}
