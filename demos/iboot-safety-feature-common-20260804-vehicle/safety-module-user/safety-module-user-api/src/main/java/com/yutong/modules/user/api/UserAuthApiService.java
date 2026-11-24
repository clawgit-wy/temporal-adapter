package com.yutong.modules.user.api;

import org.jeecg.common.exception.JeecgBootException;

import java.util.Set;

public interface UserAuthApiService {

    Set<String> queryUserRoles(String username)throws JeecgBootException;

    Set<String> queryUserRoleCodes(String username)throws JeecgBootException;

    Set<String> queryUserAuths(String username)throws JeecgBootException;

    Set<String> queryUserAdminAuths()throws JeecgBootException;

    Set<String> queryVisitorAuths(String roleCode)throws JeecgBootException;
}
