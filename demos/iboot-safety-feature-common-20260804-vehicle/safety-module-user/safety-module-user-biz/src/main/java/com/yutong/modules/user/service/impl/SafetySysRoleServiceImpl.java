package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.convert.SafetySysRoleConvert;
import com.yutong.modules.user.dto.SafetySysRoleDTO;
import com.yutong.modules.user.dto.SetRolePermDTO;
import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.SafetySysRolePerm;
import com.yutong.modules.user.entity.SafetySysUserRole;
import com.yutong.modules.user.mapper.SafetySysRoleMapper;
import com.yutong.modules.user.mapper.SafetySysUserRoleMapper;
import com.yutong.modules.user.query.SafetySysRoleQuery;
import com.yutong.modules.user.api.ISafetySysRolePermService;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.RolePermConfigVO;
import com.yutong.modules.user.vo.SafetySysRoleOptionListVO;
import com.yutong.modules.user.vo.SafetySysRoleVO;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import com.yutong.modules.user.constant.CustomConstantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Service
public class SafetySysRoleServiceImpl extends ServiceImpl<SafetySysRoleMapper, SafetySysRole> implements ISafetySysRoleService {

    @Resource
    private SafetySysRoleConvert safetySysRoleConvert;

    @Autowired
    private SafetySysUserRoleMapper userRoleMapper;

    @Autowired
    private ISafetySysRolePermService safetySysRolePermService;

    @Resource
    private SafetySysPermService safetySysPermService;

    @Lazy
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private CustomConstantConfig customConstantConfig;

    @Override
    public IPage<SafetySysRoleVO> queryPageList(SafetySysRoleQuery safetySysRoleQuery, Integer pageNo, Integer pageSize) {
        Page<SafetySysRoleVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.selectRolePage(page, safetySysRoleQuery);
    }

    /**
     * 根据角色 ID 查询用户数量
     */
    private Long countUsersByRoleId(Long roleId) {
        LambdaQueryWrapper<SafetySysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SafetySysUserRole::getRoleId, roleId)
                .eq(SafetySysUserRole::getDelFlag,0);
        return userRoleMapper.selectCount(userRoleWrapper);
    }

    @Lock4j(name = "safety:sys:role:add")
    @Override
    public boolean save(SafetySysRoleDTO safetySysRoleDTO) {
        // 校验角色名称是否已存在
        LambdaQueryWrapper<SafetySysRole> checkRoleNameWrapper = new LambdaQueryWrapper<>();
        checkRoleNameWrapper.eq(SafetySysRole::getRoleName, safetySysRoleDTO.getRoleName())
                .eq(SafetySysRole::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString());
        if (this.count(checkRoleNameWrapper) > 0) {
            throw new RuntimeException("已存在相同角色名称");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SafetySysRole safetySysRole = BeanUtil.copyProperties(safetySysRoleDTO, SafetySysRole.class);
        // 生成8位随机字母+数字并校验唯一性
        String random;
        LambdaQueryWrapper<SafetySysRole> checkCodeWrapper;
        do {
            random = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
            checkCodeWrapper = new LambdaQueryWrapper<>();
            checkCodeWrapper.eq(SafetySysRole::getRoleCode, "ROLE_" + random);
        } while (this.count(checkCodeWrapper) > 0);
        safetySysRole.setRoleDesc(safetySysRole.getRoleName());
        safetySysRole.setRoleCode("ROLE_" + random);
        safetySysRole.setLastUser(loginUser != null ? loginUser.getRealname() : null);
        safetySysRole.setDeleteFalg("0");
        safetySysRole.setLastTime(new Date());
        return this.save(safetySysRole);
    }

    @Override
    public boolean update(SafetySysRoleDTO safetySysRoleDTO) {
        if (safetySysRoleDTO.getId() == null){
            return false;
        }
        SafetySysRole safetySysRole = this.getById(safetySysRoleDTO.getId());
        if (safetySysRole == null || "1".equals(safetySysRole.getDeleteFalg())){
            return false;
        }
        // 系统角色不允许修改
        if (customConstantConfig.getSysGuestRoleCode().equals(safetySysRole.getRoleCode()) || customConstantConfig.getSysAdminRoleCode().equals(safetySysRole.getRoleCode())) {
            throw new IllegalArgumentException("系统角色禁止修改");
        }
        // 校验角色名称是否已存在（排除当前角色）
        LambdaQueryWrapper<SafetySysRole> checkRoleNameWrapper = new LambdaQueryWrapper<>();
        checkRoleNameWrapper.eq(SafetySysRole::getRoleName, safetySysRoleDTO.getRoleName())
                .eq(SafetySysRole::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString());
        if (safetySysRoleDTO.getId().equals(safetySysRole.getId())) {
            checkRoleNameWrapper.ne(SafetySysRole::getId, safetySysRoleDTO.getId());
        }
        if (this.count(checkRoleNameWrapper) > 0) {
            throw new IllegalArgumentException("已存在相同角色名称");
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        BeanUtil.copyProperties(safetySysRoleDTO, safetySysRole);
        safetySysRole.setRoleDesc(safetySysRole.getRoleName());
        safetySysRole.setLastUser(loginUser != null ? loginUser.getRealname() : null);
        safetySysRole.setLastTime(new Date());
        return this.updateById(safetySysRole);
    }

    @Override
    public boolean delete(Long id) {
        SafetySysRole safetySysRole = this.getById(id);
        if (safetySysRole == null || "1".equals(safetySysRole.getDeleteFalg())) {
            return false;
        }
        // 系统角色不允许删除
        if (customConstantConfig.getSysGuestRoleCode().equals(safetySysRole.getRoleCode()) || customConstantConfig.getSysAdminRoleCode().equals(safetySysRole.getRoleCode())) {
            throw new IllegalArgumentException("系统角色禁止删除");
        }
        // 查询角色下用户数量
        Long userCount = countUsersByRoleId(id);
        // 判断：大于0 就阻止删除
        if (userCount != null && userCount > 0) {
            // 抛业务异常（更规范）
            throw new IllegalArgumentException("该角色下存在用户，请清除用户后再删除！");
        }
        // 先更新操作人信息
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        safetySysRole.setLastUser(loginUser != null ? loginUser.getRealname() : null);
        safetySysRole.setLastTime(new Date());
        this.updateById(safetySysRole);
        // 先删除角色原有的权限
        LambdaQueryWrapper<SafetySysRolePerm> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SafetySysRolePerm::getRoleId, id);
        safetySysRolePermService.remove(deleteWrapper);
        boolean success = baseMapper.deleteById(id) > 0;
        //清除用户权限缓存
        if (success){
            clearAllPermissionCache(CommonConstant.PREFIX_USER_SHIRO_CACHE);
        }
        // 再执行逻辑删除，@TableLogic 会自动将 deleteFalg 设置为 "1"
        return success;
    }

    @Override
    public List<SafetySysRoleVO> queryAllList(SafetySysRoleQuery safetySysRoleQuery) {
        return baseMapper.queryAllList(safetySysRoleQuery);
    }


    @Override
    public boolean setRolePerms(SetRolePermDTO setRolePermDTO) {
        // 先物理删除角色原有的权限绑定关系（避免逻辑删除字段残留数据）
        safetySysRolePermService.physicalDeleteByRoleId(setRolePermDTO.getRoleId());

        // 再插入新的权限，排除菜单类型（cate=1）
        if (setRolePermDTO.getPermIds() != null && !setRolePermDTO.getPermIds().isEmpty()) {
            // 查询有效的权限，过滤掉菜单类型（cate=1）
            LambdaQueryWrapper<SafetySysPerm> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(SafetySysPerm::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString())
                    .in(SafetySysPerm::getId, setRolePermDTO.getPermIds());
            List<SafetySysPerm> permList = safetySysPermService.list(permWrapper);
            Set<Long> allowedIds = permList.stream()
                    .filter(p -> p.getCate() != 1)
                    .map(SafetySysPerm::getId)
                    .collect(Collectors.toSet());

            ArrayList<SafetySysRolePerm> safetySysRolePermList = new ArrayList<>();
            for (Long permId : setRolePermDTO.getPermIds()) {
                if (allowedIds.contains(permId)) {
                    SafetySysRolePerm rolePerm = new SafetySysRolePerm();
                    rolePerm.setRoleId(setRolePermDTO.getRoleId());
                    rolePerm.setPermId(permId);
                    rolePerm.setDelFlag(0);
                    rolePerm.setCreateTime(new Date());
                    safetySysRolePermList.add(rolePerm);
                }
            }
            safetySysRolePermService.saveBatch(safetySysRolePermList, 1000);
        }
        //修改角色编辑时间
        SafetySysRole safetySysRole = this.getById(setRolePermDTO.getRoleId());
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        safetySysRole.setLastUser(loginUser != null ? loginUser.getRealname() : null);
        safetySysRole.setLastTime(new Date());
        this.updateById(safetySysRole);
        //清除用户权限缓存
        clearAllPermissionCache(CommonConstant.PREFIX_USER_SHIRO_CACHE);
        return true;
    }

    @Override
    public RolePermConfigVO queryRolePerms(Long roleId) {
        RolePermConfigVO result = new RolePermConfigVO();
        // 1. 查询全部未删除的权限数据，按 sort 升序
        LambdaQueryWrapper<SafetySysPerm> permWrapper = new LambdaQueryWrapper<SafetySysPerm>()
                .eq(SafetySysPerm::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString())
                .orderByAsc(SafetySysPerm::getSort);
        List<SafetySysPerm> permList = safetySysPermService.list(permWrapper);

        // 2. 构建菜单树
        List<MenuTree> allTrees = safetySysRoleConvert.safetySysPermToMenuTree(permList);
        // 筛选根节点（父ID为"0"）
        List<MenuTree> rootTrees = allTrees.stream()
                .filter(tree -> "0".equals(tree.getPid()))
                .collect(Collectors.toList());
        for (MenuTree root : rootTrees) {
            buildChildren(allTrees, root);
        }
        result.setTreeList(rootTrees);

        // 3. 查询角色已授权的权限 ID 列表
        LambdaQueryWrapper<SafetySysRolePerm> rolePermWrapper = new LambdaQueryWrapper<SafetySysRolePerm>()
                .eq(SafetySysRolePerm::getRoleId, roleId)
                .eq(SafetySysRolePerm::getDelFlag, 0);
        List<String> checkedIds = safetySysRolePermService.list(rolePermWrapper)
                .stream()
                .map(safetySysRolePerm -> String.valueOf(safetySysRolePerm.getPermId()))
                .collect(Collectors.toList());
        result.setIds(checkedIds);

        return result;
    }

    /**
     * 递归构建菜单树子节点
     */
    private void buildChildren(List<MenuTree> allTrees, MenuTree parent) {
        List<MenuTree> children = allTrees.stream()
                .filter(tree -> Objects.equals(tree.getPid(), parent.getId()))
                .collect(Collectors.toList());
        parent.setChildren(children);
        parent.setIsLeaf(children.isEmpty());
        for (MenuTree child : children) {
            buildChildren(allTrees, child);
        }
    }

    @Override
    public List<SafetySysRoleOptionListVO> optionlist() {
        LambdaQueryWrapper<SafetySysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SafetySysRole::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString());
        List<SafetySysRole> roleList = this.list(lambdaQueryWrapper);
        return safetySysRoleConvert.entityListToOptionListVO(roleList);
    }

    @Override
    public int batchFillRoleCodes() {
        LambdaQueryWrapper<SafetySysRole> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SafetySysRole::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString())
        wrapper.and(w -> w.isNull(SafetySysRole::getRoleCode).or().eq(SafetySysRole::getRoleCode, ""));
        List<SafetySysRole> roleList = this.list(wrapper);
        if (roleList.isEmpty()) {
            return 0;
        }
        List<SafetySysRole> toUpdate = new ArrayList<>();
        for (SafetySysRole role : roleList) {
            String random;
            LambdaQueryWrapper<SafetySysRole> checkWrapper;
            do {
                random = RandomStringUtils.randomAlphanumeric(8);
                checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(SafetySysRole::getRoleCode, "ROLE_" + random.toUpperCase());
            } while (this.count(checkWrapper) > 0);
            role.setRoleCode("ROLE_" + random.toUpperCase());
            toUpdate.add(role);
        }
        this.updateBatchById(toUpdate);
        return toUpdate.size();
    }

    /**
     *  获取指定前缀的一系列key
     *  使用scan命令代替keys, Redis是单线程处理，keys命令在KEY数量较多时，
     *  操作效率极低【时间复杂度为O(N)】，该命令一旦执行会严重阻塞线上其它命令的正常请求
     * @param keyPrefix
     * @return
     */
    private Set<String> getAllPermissionCacheKeys(String keyPrefix) {
        String realKey = keyPrefix + "*";

        try {
            return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> binaryKeys = new HashSet<>();
                //springboot2.4后用法
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(realKey).count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }

                return binaryKeys;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  删除指定前缀的一系列key
     * @param keyPrefix
     */
    public void clearAllPermissionCache(String keyPrefix) {
        try {
            Set<String> keys = getAllPermissionCacheKeys(keyPrefix);
            redisTemplate.delete(keys);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
