package com.yutong.modules.user.query;

import lombok.Data;

@Data
public class SafetySysUserQuery {

    /**员工名*/
    private String empName;
    /**员工号*/
    private String empNo;
    /**手机号 */
    private String phone;
    /**角色id*/
    private String roleId;
    /**责任部门*/
    private String departmentId;
}
