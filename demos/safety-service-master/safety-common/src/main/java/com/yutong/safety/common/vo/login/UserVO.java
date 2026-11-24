package com.yutong.safety.common.vo.login;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户基本信息
 */
@Data
public class UserVO implements Serializable {
    /**
     * ima中天tocken
     */
    private String tocken;
    /**
     * 员工号
     */
    private String workNo;
    /**
     * 姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * eip登录名
     */
    private String userName;
    /**
     * 邮箱
     */
    private String email;

    /**
     * EIP的token
     */
    private String eipToken;
}
