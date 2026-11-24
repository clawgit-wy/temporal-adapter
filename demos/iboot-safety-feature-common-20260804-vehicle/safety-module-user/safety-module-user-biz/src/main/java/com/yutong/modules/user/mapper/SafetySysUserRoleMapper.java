package com.yutong.modules.user.mapper;

import com.yutong.modules.user.entity.SafetySysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @Description: 账号角色关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetySysUserRoleMapper extends BaseMapper<SafetySysUserRole> {


    Set<String> queryUserRoles(@Param("username") String username);

    Set<String> queryUserRoleCodes(@Param("username") String username);

    Set<String>queryUserAuths(@Param("username") String username);

    Set<String>queryVisitorAuths(@Param("roleCode") String roleCode);

    Set<String>queryUserAdminAuths();

}
