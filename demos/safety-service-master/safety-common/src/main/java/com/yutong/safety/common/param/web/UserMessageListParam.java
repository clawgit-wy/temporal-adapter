package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.13
 */
@Data
public class UserMessageListParam extends WebPageParam {

    private String empNo;

    private String empName;

    private String factoryId;

    private String roleName;

    private String roleNameArr;

    private Integer roleId;

    private String powerFactoryId;

    private String powerFactoryArr;

    private String factoryCode;

    private String userName;
}
