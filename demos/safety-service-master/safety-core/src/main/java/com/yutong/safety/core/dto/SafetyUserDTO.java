package com.yutong.safety.core.dto;

import java.io.Serializable;

public class SafetyUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String userCode;
    private Integer roleId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
