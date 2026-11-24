package com.yutong.modules.user.service.impl;

import com.yutong.modules.user.entity.SafetySysRolePerm;
import com.yutong.modules.user.mapper.SafetySysRolePermMapper;
import com.yutong.modules.user.api.ISafetySysRolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 角色权限关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
@Service
public class SafetySysRolePermServiceImpl extends ServiceImpl<SafetySysRolePermMapper, SafetySysRolePerm> implements ISafetySysRolePermService {

    @Autowired
    private SafetySysRolePermMapper safetySysRolePermMapper;

    @Override
    public void physicalDeleteByRoleId(Long roleId) {
        safetySysRolePermMapper.physicalDeleteByRoleId(roleId);
    }
}
