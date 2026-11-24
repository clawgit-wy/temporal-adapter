package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yutong.modules.user.convert.SafetySysRoleConvert;
import com.yutong.modules.user.convert.SysPermConvert;
import com.yutong.modules.user.dto.SafetySysPermDTO;
import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.SafetySysRolePerm;
import com.yutong.modules.user.entity.SafetySysUserRole;
import com.yutong.modules.user.mapper.*;
import com.yutong.modules.user.model.AuthItem;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.vo.*;
import io.swagger.annotations.ApiModelProperty;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import com.yutong.modules.user.constant.CustomConstantConfig;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Service
public class SafetySysPermServiceImpl extends ServiceImpl<SafetySysPermMapper, SafetySysPerm> implements SafetySysPermService {

    @Autowired
    SafetySysPermMapper safetySysPermMapper;
    @Autowired
    private SysPermConvert sysPermConvert;
    @Resource
    private SafetySysRoleConvert safetySysRoleConvert;
    @Autowired
    private SafetySysUserMapper safetySysUserMapper;
    @Resource
    private SafetySysUserRoleMapper safetySysUserRoleMapper;
    @Autowired
    private SafetySysRolePermMapper safetySysRolePermMapper;
    @Autowired
    private SafetySysRoleMapper sysRoleMapper;

    @Resource
    private CustomConstantConfig customConstantConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public void deletePermission(String id) throws JeecgBootException {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userName = sysUser==null?"":sysUser.getUsername();
        SafetySysPerm safetySysPerm = this.getById(id);
        if(safetySysPerm==null) {
            throw new JeecgBootException("未找到菜单信息");
        }
        removeById(safetySysPerm);
        // 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
        this.removeChildrenBy(String.valueOf(safetySysPerm.getId()),userName);

        safetySysPermMapper.deleteSysRolePerm(String.valueOf(safetySysPerm.getId()));
        safetySysPermMapper.deleteUserDataRule(String.valueOf(safetySysPerm.getId()),userName);

    }

    public void removeChildrenBy(String parentId,String userName) {
        LambdaQueryWrapper<SafetySysPerm> query = new LambdaQueryWrapper<>();
        // 封装查询条件parentId为主键,
        query.eq(SafetySysPerm::getParentId, parentId);
        // 查出该主键下的所有子级
        List<SafetySysPerm> safetySysPermList = this.list(query);
        if (safetySysPermList != null && safetySysPermList.size() > 0) {
            // id
            String id = "";
            // 查出的子级数量
            Long num = Long.valueOf(0);
            // 如果查出的集合不为空, 则先删除所有
            this.remove(query);
            // 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
            for (int i = 0, len = safetySysPermList.size(); i < len; i++) {
                id = String.valueOf(safetySysPermList.get(i).getId());
                safetySysPermMapper.deleteSysRolePerm(id);
                safetySysPermMapper.deleteUserDataRule(id,userName);
                num = this.count(new LambdaQueryWrapper<SafetySysPerm>().eq(SafetySysPerm::getParentId, id));
                // 如果有, 则递归
                if (num > 0) {
                    this.removeChildrenBy(id,userName);
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdatePerm(SafetySysPermDTO PermDto) throws JeecgBootException {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SafetySysPerm safetySysPerm = sysPermConvert.toVO(PermDto);
        if (StringUtils.hasText(safetySysPerm.getMappingClass())){
            if (!ClassUtils.isPresent(safetySysPerm.getMappingClass(),
                    Thread.currentThread().getContextClassLoader())) {
                throw new JeecgBootException("包名不正确或映射类不存在：" + safetySysPerm.getMappingClass());
            }
        }
        Long pid = PermDto.getParentId();
        String currPath = "";
        if(pid !=null ){
            SafetySysPerm perm = getById(pid);
            if(perm!=null){
                String pPatch = perm.getPath();
                String currId = String.valueOf(safetySysPerm.getId());
                List<String> pathIds = Arrays.asList(pPatch.split("/"));
                if (pathIds.contains(currId)) {
                    throw new JeecgBootException("不能将自己的上级菜单设置为自己或自己的下级菜单");
                }
                int level = perm.getLevel()+1;
                safetySysPerm.setLevel(level);
                currPath = perm.getPath()+"/";
            }
        }else {
            safetySysPerm.setParentId(0L);
            safetySysPerm.setLevel(0);
        }
        safetySysPerm.setLastTime(new Date());
        safetySysPerm.setLastUser(sysUser==null?"":sysUser.getUsername());
        saveOrUpdate(safetySysPerm);
        currPath = currPath + safetySysPerm.getId();
        if(PermDto.getPath()==null){
            safetySysPerm.setPath(currPath);
            updateById(safetySysPerm);
        }else{
            if(!PermDto.getPath().equals(currPath)){
                safetySysPerm.setPath(currPath);
                updateById(safetySysPerm);
                updateChildrenPath(safetySysPerm.getId(), currPath);
            }
        }

    }

    private void updateChildrenPath(Long parentId, String parentPath) {
        LambdaQueryWrapper<SafetySysPerm> query = new LambdaQueryWrapper<>();
        query.eq(SafetySysPerm::getParentId, parentId);
        List<SafetySysPerm> children = list(query);
        SafetySysPerm perm = getById(parentId);
        if (children != null && !children.isEmpty()) {
            for (SafetySysPerm child : children) {
                String newPath = parentPath + "/" + child.getId();
                int level = perm.getLevel()+1;
                child.setLevel(level);
                child.setPath(newPath);
                updateById(child);
                updateChildrenPath(child.getId(), newPath);
            }
        }
    }

    @Override
    public List<PublicClass> getMenuClassInfo(String id) throws JeecgBootException {
        List<PublicClass> classList = new ArrayList<>();
        SafetySysPerm sysPerm = getById(id);
        if(sysPerm==null){
            throw new JeecgBootException("菜单不存在：" + id);
        }
        String mappingClass = sysPerm.getMappingClass();
        if(StringUtils.hasText(mappingClass)){
            if (!ClassUtils.isPresent(mappingClass,
                    Thread.currentThread().getContextClassLoader())) {
                throw new JeecgBootException("包名不正确或映射类不存在：" + mappingClass);
            }
            try{
                Class<?> clazz = Class.forName(mappingClass);
                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {
                    PublicClass publicClass = new PublicClass();
                    if(field.isAnnotationPresent(DataPermissionField.class)){
                        DataPermissionField showField = field.getAnnotation(DataPermissionField.class);
                        String dbCode = showField.deCode();
                        String dbName = showField.deName();
                        if(!StringUtils.hasText(dbCode)){
                            dbCode = field.getName();
                            dbCode = oConvertUtils.camelToUnderline(dbCode);
                        }
                        if(!StringUtils.hasText(dbName)){
                            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                            dbName = (apiModelProperty != null) ? apiModelProperty.value() : "";
                        }
                        List<PublicClass.EnumMapping> enumMappings = getEnumMappings(showField);
                        publicClass.setEnumMappings(enumMappings);
                        publicClass.setDbCode(dbCode);
                        publicClass.setDbName(dbName);
                        classList.add(publicClass);
                    }
                }
            }catch (Exception e){
                log.error(e.getMessage(), e);
                throw new JeecgBootException("获取包信息失败：" + mappingClass);
            }
        }
        return classList;
    }

    private List<PublicClass.EnumMapping> getEnumMappings(DataPermissionField showField) {
        try{
            String[] mappings = showField.enumMappings();
            List<PublicClass.EnumMapping> enumMappings = new ArrayList<>();
            for (String mapping : mappings){
                String[] parts = mapping.split(":");
                PublicClass.EnumMapping enumMapping = new PublicClass.EnumMapping();
                enumMapping.setEnumValue(parts[0]);
                enumMapping.setDescription(parts[1]);
                enumMappings.add(enumMapping);
            }
            return enumMappings;
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<MenuTree> getMenuTreeList(SafetySysPermDTO permDto) throws JeecgBootException {

        // 1. 查询全部未删除的权限数据，按 sort 升序
        LambdaQueryWrapper<SafetySysPerm> permWrapper = new LambdaQueryWrapper<SafetySysPerm>()
                .eq(SafetySysPerm::getDeleteFalg, "0")
                .like(StringUtils.hasText(permDto.getPermName()),SafetySysPerm::getPermName,permDto.getPermName())
                .orderByAsc(SafetySysPerm::getSort);
        List<SafetySysPerm> permList = safetySysPermMapper.getSafetySysPermList(permWrapper);
        // 2. 构建菜单树
        List<MenuTree> allTrees = safetySysRoleConvert.safetySysPermToMenuTree(permList);
        List<MenuTree> rootTrees = new ArrayList<>();
        // 筛选根节点（父ID为"0"）
        if(!StringUtils.hasText(permDto.getPermName())){
             rootTrees = allTrees.stream()
                    .filter(tree -> "0".equals(tree.getPid()))
                    .collect(Collectors.toList());
            for (MenuTree root : rootTrees) {
                buildChildren(allTrees, root);
            }
        }else{
            rootTrees = allTrees;
        }
        return rootTrees;
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
    public boolean getPermUrlIsExist(String permUrl,String id) throws JeecgBootException {
        boolean result = false;
        LambdaQueryWrapper<SafetySysPerm> permWrapper = new LambdaQueryWrapper<SafetySysPerm>()
                .eq(SafetySysPerm::getDeleteFalg, "0")
                .eq(SafetySysPerm::getPermUrl,permUrl);
        long count = count(permWrapper);
        if(StringUtils.hasText(id)){
            SafetySysPerm safetySysPerm = getById(id);
            if(!safetySysPerm.getPermUrl().equals(permUrl)&&count>=1){
                result = true;
            }
        }else{
            result = count>0;
        }

        return result;
    }

    /**
     * 获取菜单权限
     * @param roleIds
     * @return
     */
    private List<MenuNodeVO> getUserMenuTree(List<Long> roleIds,boolean isAdmin) {
        LambdaQueryWrapper<SafetySysPerm> permWrapper = new LambdaQueryWrapper<SafetySysPerm>();
        //如果用户有系统管理员角色，则获取所有菜单权限
        if(isAdmin){
            permWrapper.in(SafetySysPerm::getCate, 1, 2)
                    .eq(SafetySysPerm::getDeleteFalg, "0")
//                    .eq(SafetySysPerm::getIsShow, "1")
                    .orderByAsc(SafetySysPerm::getSort);
        }else{
            // 2. 根据roleId列表查询permId列表
            List<Long> permIds = safetySysRolePermMapper.selectList(
                            Wrappers.<SafetySysRolePerm>lambdaQuery()
                                    .in(SafetySysRolePerm::getRoleId, roleIds)
                    ).stream()
                    .map(SafetySysRolePerm::getPermId)
                    .distinct()
                    .collect(Collectors.toList());

            if (permIds.isEmpty()) {
                return Collections.emptyList();
            }
            // 3. 根据permId列表和附加条件查询SafetySysPerm列表
            permWrapper.in(SafetySysPerm::getId, permIds)
                    .in(SafetySysPerm::getCate, 1, 2)
                    .eq(SafetySysPerm::getDeleteFalg, "0")
//                    .eq(SafetySysPerm::getIsShow, "1")
                    .orderByAsc(SafetySysPerm::getSort);
        }
        List<SafetySysPerm> permList = list(permWrapper);
        List<SafetySysPerm> allMenus = new ArrayList<>();
        if(isAdmin){
            allMenus = permList.stream().filter(p -> p.getCate() == 1).collect(Collectors.toList());
        }else{
            allMenus = safetySysPermMapper.querySysPermMenu();
        }
        Map<Long, SafetySysPerm> mapPerm = allMenus.stream()
                .collect(Collectors.toMap(SafetySysPerm::getId, // key：id字段
                        vo -> vo,                            // value：vo对象
                        (oldVal, newVal) -> oldVal           // 重复id时保留旧值
                ));
        Map<Long, SafetySysPerm> permMap = new HashMap<>();
        List<SafetySysPerm> rootPerms = new ArrayList<>();
        for (SafetySysPerm perm : permList) {
            if(!permMap.containsKey(perm.getId())) {
                permMap.put(perm.getId(), perm);
            }
            //添加父节点
            if(StringUtils.hasText(perm.getPath())) {
                List<Long> paths = Arrays.stream(perm.getPath().split("/")).map(Long::parseLong).collect(Collectors.toList());
                for (Long l :paths) {
                    if(Objects.equals(l, perm.getId())) continue;
                    SafetySysPerm pMenu = mapPerm.get(l);
//                    && !permMap.containsKey(l)
                    if (pMenu != null ) {
                        if (!permMap.containsKey(l)) {
                            permMap.put(pMenu.getId(), pMenu);
                        }
                        // 如果父菜单不在rootPerms中，就添加
                        if (!rootPerms.contains(pMenu)) {
                            rootPerms.add(pMenu);
                        }

                    }
                }
            }
        }
        rootPerms = rootPerms.stream()
                .sorted(Comparator.comparingInt(SafetySysPerm::getSort))
                .collect(Collectors.toList());
        List<MenuNodeVO> rootMenus = new ArrayList<>();
        for (SafetySysPerm perm : rootPerms) {
            if (perm.getParentId() == 0L) {
                MenuNodeVO node = convertToMenuNode(perm);
                buildMenuChildren(permMap, node);
                rootMenus.add(node);
            }
        }
        return rootMenus;
    }

    @Override
    public UserPermissionVO queryUserPermission(String workNo) {
        UserPermissionVO vo = new UserPermissionVO();
        Long userId = safetySysUserMapper.selectIdByEmpNo(workNo);
        boolean isAdmin = false;
        List<Long> roleIds = new ArrayList<>();
//            // 1. 根据userId查询roleId列表
           if(userId!=null){
               roleIds = safetySysUserRoleMapper.selectList(
                               Wrappers.<SafetySysUserRole>lambdaQuery()
                                       .eq(SafetySysUserRole::getUserId, userId)
                                       .eq(SafetySysUserRole::getStatus, 0)
                       ).stream()
                       .map(SafetySysUserRole::getRoleId)
                       .collect(Collectors.toList());
               //查询是否拥有管理员角色
               SafetySysRole adminRole = sysRoleMapper.selectOne(
                       Wrappers.<SafetySysRole>lambdaQuery().eq(SafetySysRole::getRoleCode,customConstantConfig.getSysAdminRoleCode())
               );
               if(adminRole != null){
                   if(roleIds.contains(adminRole.getId())){
                       isAdmin = true;
                   }
               }
           }
        if (roleIds.isEmpty()) {
            SafetySysRole sysRole = sysRoleMapper.selectOne(Wrappers.<SafetySysRole>lambdaQuery().eq(SafetySysRole::getRoleCode, customConstantConfig.getSysGuestRoleCode()));
            if(sysRole==null) return vo;
            roleIds.add(sysRole.getId());
        }
        List<AuthItem> allAuth = safetySysRolePermMapper.queryAllAuth();
        List<AuthItem> auths = new ArrayList<>();
        if(isAdmin){
            auths = allAuth;
        }else{
            auths = safetySysRolePermMapper.queryAuth(roleIds);
        }
        List<String> codeList = auths.stream().map(AuthItem::getAction).collect(Collectors.toList());
        vo.setMenu(getUserMenuTree(roleIds,isAdmin));
        vo.setAllAuth(allAuth);
        vo.setAuth(auths);
        vo.setCodeList(codeList);
        vo.setSysSafeMode(false);
        return vo;
    }

    private void buildMenuChildren(Map<Long, SafetySysPerm> permMap, MenuNodeVO parent) {
        long parentIdLong;
        try {
            parentIdLong = Long.parseLong(parent.getId());
        } catch (NumberFormatException e) {
            return;
        }
        List<MenuNodeVO> children = new ArrayList<>();
        for (SafetySysPerm perm : permMap.values()) {
            if (perm.getParentId() == parentIdLong) {
                MenuNodeVO child = convertToMenuNode(perm);
                child.setRedirect(perm.getRedirect());
                children.add(child);
                buildMenuChildren(permMap, child);
            }
        }
        children.sort(Comparator.comparingInt(c -> permMap.get(Long.parseLong(c.getId())).getSort()));
        parent.setChildren(children.isEmpty() ? null : children);

//        if (!children.isEmpty()) {
//            boolean allChildrenShown = children.stream().allMatch(c -> "0".equals(c.getIsShow()));
//            if (allChildrenShown) {
//                parent.setIsShow("0");
//            }
//        }
    }

    private MenuNodeVO convertToMenuNode(SafetySysPerm perm) {
        MenuNodeVO node = new MenuNodeVO();
        node.setId(String.valueOf(perm.getId()));
        node.setPath(perm.getPermUrl());
        node.setComponent(perm.getComponent());
        if(perm.getCate()==1){
            node.setRedirect(perm.getRedirect());
        }
        node.setRoute(String.valueOf(1));
        node.setName(String.valueOf(perm.getId()));
        node.setIsShow(perm.getIsShow());
        MetaVO meta = new MetaVO();
        meta.setComponentName(perm.getComponentName());
        meta.setTitle(perm.getPermName());
        meta.setIcon(perm.getIcon());
        meta.setKeepAlive("1".equals(perm.getKeepAlive()));
        meta.setInternalOrExternal(false);
        node.setMeta(meta);
        return node;
    }


}
