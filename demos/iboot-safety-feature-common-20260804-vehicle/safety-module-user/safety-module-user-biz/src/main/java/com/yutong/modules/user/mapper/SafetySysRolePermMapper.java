package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.SafetySysRolePerm;
import com.yutong.modules.user.model.AuthItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 角色权限关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
public interface SafetySysRolePermMapper extends BaseMapper<SafetySysRolePerm> {

    /**
     * 物理删除角色权限绑定关系
     */
    @Delete("DELETE FROM safety_sys_role_perm WHERE role_id = #{roleId}")
    int physicalDeleteByRoleId(@Param("roleId") Long roleId);

    @Select("select code action,perm_name `describe`,'1' type,'1' status from safety_sys_perm where delete_falg='0' and cate=3")
    public List<AuthItem> queryAllAuth();

    public List<AuthItem> queryAuth(@Param("roleIds") List<Long> roleIds );
}
