package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.entity.SafetySysRolePerm;

/**
 * @Description: 角色权限关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
public interface ISafetySysRolePermService extends IService<SafetySysRolePerm> {

    /**
     * 物理删除角色权限绑定关系
     */
    void physicalDeleteByRoleId(Long roleId);
}
