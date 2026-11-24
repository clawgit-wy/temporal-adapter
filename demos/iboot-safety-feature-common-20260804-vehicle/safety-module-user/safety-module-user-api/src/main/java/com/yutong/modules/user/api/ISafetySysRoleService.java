package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.SafetySysRoleDTO;
import com.yutong.modules.user.dto.SetRolePermDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.query.SafetySysRoleQuery;
import com.yutong.modules.user.vo.RolePermConfigVO;
import com.yutong.modules.user.vo.SafetySysRoleOptionListVO;
import com.yutong.modules.user.vo.SafetySysRoleVO;

import java.util.List;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface ISafetySysRoleService extends IService<SafetySysRole> {

    /**
     * 分页查询角色列表
     */
    IPage<SafetySysRoleVO> queryPageList(SafetySysRoleQuery safetySysRoleQuery, Integer pageNo, Integer pageSize);

    /**
     * 添加角色
     */
    boolean save(SafetySysRoleDTO safetySysRoleDTO);

    /**
     * 编辑角色
     */
    boolean update(SafetySysRoleDTO safetySysRoleDTO);

    /**
     * 删除角色（软删除）
     */
    boolean delete(Long id);

    /**
     * 获取角色下拉框列表
     */
    List<SafetySysRoleOptionListVO> optionlist();

    /**
     * 查询所有角色列表（用于导出）
     */
    List<SafetySysRoleVO> queryAllList(SafetySysRoleQuery safetySysRoleQuery);

    /**
     * 设置角色权限
     *
     * @param setRolePermDTO 角色授权DTO
     * @return 是否成功
     */
    boolean setRolePerms(SetRolePermDTO setRolePermDTO);

    /**
     * 查询角色权限配置（菜单树 + 已勾选的权限）
     */
    RolePermConfigVO queryRolePerms(Long roleId);

    /**
     * 批量补全角色编码
     */
    int batchFillRoleCodes();
}
