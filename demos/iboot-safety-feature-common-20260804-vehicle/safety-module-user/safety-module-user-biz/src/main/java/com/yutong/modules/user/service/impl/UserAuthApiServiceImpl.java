package com.yutong.modules.user.service.impl;

import com.yutong.modules.user.mapper.SafetySysUserRoleMapper;
import com.yutong.modules.user.api.UserAuthApiService;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserAuthApiServiceImpl implements UserAuthApiService {
    @Autowired
    SafetySysUserRoleMapper safetySysUserRoleMapper;

    @Override
    public Set<String> queryUserRoles(String username)throws JeecgBootException {
        return safetySysUserRoleMapper.queryUserRoles(username);
    }

    @Override
    public Set<String> queryUserRoleCodes(String username)throws JeecgBootException {
        return safetySysUserRoleMapper.queryUserRoleCodes(username);
    }

    @Override
    public Set<String> queryUserAuths(String username)throws JeecgBootException {
        return safetySysUserRoleMapper.queryUserAuths(username);
    }

    @Override
    public Set<String> queryUserAdminAuths() throws JeecgBootException {
        return safetySysUserRoleMapper.queryUserAdminAuths();
    }

    @Override
    public Set<String> queryVisitorAuths(String roleCode) throws JeecgBootException {
        return safetySysUserRoleMapper.queryVisitorAuths(roleCode);
    }
}
