package com.yutong.modules.user.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.api.*;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.dto.SafetySysUserDTO;
import com.yutong.modules.user.dto.SafetySysUserImpoetDTO;
import com.yutong.modules.user.dto.YtsafetySysUserDataRuleDTO;
import com.yutong.modules.user.mapper.YtsafetySysResponsibleMapper;
import com.yutong.modules.user.mapper.YtsafetySysResponsibleMappingMapper;
import com.yutong.modules.user.query.SafetySysUserQuery;
import com.yutong.modules.user.entity.*;
import com.yutong.modules.user.mapper.SafetySysUserMapper;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.SafetySysUserVO;
import com.yutong.modules.user.vo.YtsafetySysUserDataRuleVO;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date: 2026-05-22
 * @Version: V1.0
 */
@Log4j2
@Service
public class SafetySysUserServiceImpl extends ServiceImpl<SafetySysUserMapper, SafetySysUser> implements ISafetySysUserService {

    @Autowired
    private IYtsafetySysUserFactoryService ytsafetySysUserFactoryService;
    @Autowired
    private SafetySysUserMapper safetySysUserMapper;
    @Autowired
    private ISafetyFieldManageService safetyFieldManageService;
    @Autowired
    private ISafetySysUserRoleService safetySysUserRoleService;
    @Autowired
    private ISafetySysRoleService safetySysRoleService;
    @Autowired
    private ISafetyGroupInfoService safetyGroupInfoService;
    @Autowired
    private IYtsafetySysUserDataRuleService ytsafetySysUserDataRuleService;
    @Autowired
    private IYtsafetySysUserDepartService ytsafetySysUserDepartService;
    @Autowired
    private SafetySysPermService safetySysPermService;
    @Autowired
    private SafetySysUserServiceImportImpl safetySysUserServiceImport;
    @Autowired
    private YtsafetySysResponsibleMapper ytsafetySysResponsibleMapper;

    @Autowired
    private YtsafetySysResponsibleMappingMapper sysResponsibleMappingMapper;

    @Autowired
    private IYtsafetySysResponsibleService ytsafetySysResponsibleService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(SafetySysUserDTO safetySysUserDto) {
        //判断是否存在
        LambdaQueryWrapper<SafetySysUser> wrapper = new LambdaQueryWrapper<SafetySysUser>()
                .eq(SafetySysUser::getEmpNo, safetySysUserDto.getEmpNo())
                .eq(SafetySysUser::getDeleteFalg, 0);
        SafetySysUser sysUser = safetySysUserMapper.selectOne(wrapper);
        log.info("sysUser:{}", sysUser);
        if (BeanUtil.isNotEmpty(sysUser)) {
            log.error("当前用户{}已存在", safetySysUserDto.getEmpNo());
            throw new RuntimeException("已存在相同员工号");
        }

        SafetySysUser safetySysUser = new SafetySysUser();
        BeanUtils.copyProperties(safetySysUserDto, safetySysUser);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (BeanUtil.isNotEmpty(user)) {
            safetySysUser.setModifyEmp(user.getRealname());
//            safetySysUser.setModifyEmpNo(user.getId());
        }
        Date date = new Date();
        safetySysUser.setCreateTime(date);
        safetySysUser.setUpdateTime(date);
        safetySysUserMapper.insert(safetySysUser);
        Long userId = safetySysUser.getId();

        //授权厂区厂id
        String factoryId = safetySysUserDto.getPowerFactoryId();
        if (StrUtil.isNotBlank(factoryId)) {
            String[] factoryIds = factoryId.split(",");
            //绑定用户和厂区的授权关系
            List<SafetyFieldManage> SafetyFieldManageList = safetyFieldManageService.lambdaQuery()
                    .in(SafetyFieldManage::getId, Arrays.asList(factoryIds))
                    .eq(SafetyFieldManage::getDeleteFalg, 0)
                    .list();
            if (CollUtil.isNotEmpty(SafetyFieldManageList)) {
                List<YtsafetySysUserFactory> YtsafetySysUserFactoryList = new ArrayList<>();
                for (SafetyFieldManage safetyFieldManage : SafetyFieldManageList) {
                    YtsafetySysUserFactory ytsafetySysUserFactory = new YtsafetySysUserFactory();
                    ytsafetySysUserFactory.setUserId(userId)
                            .setFactoryCode(safetyFieldManage.getFieldCode())
                            .setFactoryId(safetyFieldManage.getId())
                            .setFactoryName(safetyFieldManage.getFieldName())
                            .setCreateTime(new Date());
                    YtsafetySysUserFactoryList.add(ytsafetySysUserFactory);
                }
                ytsafetySysUserFactoryService.saveBatch(YtsafetySysUserFactoryList);
            }
        }

        //添加角色
        String roleId = safetySysUserDto.getRoleId();
        String[] roleIds = roleId.split(",");
        List<SafetySysUserRole> userRoles = new ArrayList();
        for (int i = 0; i < roleIds.length; i++) {
            SafetySysRole sysRole = safetySysRoleService.lambdaQuery()
                    .eq(SafetySysRole::getId, roleIds[i])
                    .eq(SafetySysRole::getDeleteFalg, "0")
                    .eq(SafetySysRole::getRoleType, "2")
                    .one();
            if (BeanUtil.isEmpty(sysRole)) {
                log.error("角色id:{}不存在", roleIds[i]);
                throw new RuntimeException("选择角色不存在");
            }
            SafetySysUserRole safetySysUserRole = new SafetySysUserRole()
                    .setUserId(userId)
                    .setDelFlag(0)
                    .setRoleCode(sysRole.getRoleCode())
                    .setCreateTime(date)
                    .setRoleId(sysRole.getId())
                    .setRoleType(sysRole.getRoleType())
                    .setStatus(0);
            userRoles.add(safetySysUserRole);
        }
        safetySysUserRoleService.saveBatch(userRoles);

        //绑定部门
        String departmentId = safetySysUserDto.getDepartmentId();
        if (StrUtil.isNotEmpty(departmentId)) {
            String[] departmentIds = departmentId.split(",");
            List<SafetyGroupInfo> SafetyGroupInfos = safetyGroupInfoService.lambdaQuery()
                    .in(SafetyGroupInfo::getId, Arrays.asList(departmentIds))
                    .list();
            if (CollUtil.isNotEmpty(SafetyGroupInfos)) {
                List<YtsafetySysUserDepart> ytsafetySysUserDeparts = new ArrayList<>();
                for (SafetyGroupInfo safetyGroupInfo : SafetyGroupInfos) {
                    ytsafetySysUserDeparts.add(new YtsafetySysUserDepart()
                            .setDepartCode(safetyGroupInfo.getCode())
                            .setUserId(userId)
                            .setDepartId(safetyGroupInfo.getId())
                            .setDepartName(safetyGroupInfo.getName())
                            .setCreateTime(new Date()));
                }
                ytsafetySysUserDepartService.saveBatch(ytsafetySysUserDeparts);
            }
        }

        //数据权限配置
        List<YtsafetySysUserDataRuleDTO> userDataRuleList = safetySysUserDto.getUserDataRule();
        List<YtsafetySysUserDataRule> sysUserDataRules = new ArrayList<>();

        if (CollUtil.isNotEmpty(userDataRuleList)) {
            for (YtsafetySysUserDataRuleDTO sysUserDataRule : userDataRuleList) {
                YtsafetySysUserDataRule userDataRule = BeanUtil.toBean(sysUserDataRule, YtsafetySysUserDataRule.class);
                if (BeanUtil.isNotEmpty(user)) {
                    userDataRule.setCreateUser(user.getUsername());
                }
                userDataRule.setCreateTime(new Date())
                        .setUserId(userId)
                        .setDelFlag(0)
                        .setVersion(0);
                sysUserDataRules.add(userDataRule);
            }
            ytsafetySysUserDataRuleService.saveBatch(sysUserDataRules);
        }

    }

    @Override
    public IPage<SafetySysUserVO> queryPageList(Page<SafetySysUserVO> page, SafetySysUserQuery safetySysUserQueryDto) {
        String departmentId = safetySysUserQueryDto.getDepartmentId();
        if (StrUtil.isNotBlank(departmentId)) {
            SafetyGroupInfo safetyGroupInfo = safetyGroupInfoService.getById(departmentId);
            List<SafetyGroupInfo> safetyGroupInfoList = safetyGroupInfoService.lambdaQuery()
                    .like(SafetyGroupInfo::getOrgCodePath, safetyGroupInfo.getCode())
                    .eq(SafetyGroupInfo::getStatus, "Y")
                    .list();
            if (!safetyGroupInfoList.isEmpty()) {
                Set<String> collect = safetyGroupInfoList.stream().map(map -> map.getId().toString()).collect(Collectors.toSet());
                safetySysUserQueryDto.setDepartmentId(String.join(",",collect));
            }
        }
        return safetySysUserMapper.queryPageList(page, safetySysUserQueryDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSysUserById(SafetySysUserDTO userDto) {

        Long userId = userDto.getId();
        Date date = new Date();
        //删除原有授权关系
        ytsafetySysUserFactoryService.remove(new LambdaQueryWrapper<YtsafetySysUserFactory>().eq(YtsafetySysUserFactory::getUserId, userId));
        //授权厂区厂id
        String factoryId = userDto.getPowerFactoryId();
        if (StrUtil.isNotBlank(factoryId)) {
            String[] factoryIds = factoryId.split(",");
            //绑定用户和厂区的授权关系
            List<SafetyFieldManage> SafetyFieldManageList = safetyFieldManageService.lambdaQuery()
                    .in(SafetyFieldManage::getId, Arrays.asList(factoryIds))
                    .eq(SafetyFieldManage::getDeleteFalg, 0)
                    .list();
            if (CollUtil.isNotEmpty(SafetyFieldManageList)) {
                List<YtsafetySysUserFactory> YtsafetySysUserFactoryList = new ArrayList<>();
                for (SafetyFieldManage safetyFieldManage : SafetyFieldManageList) {
                    YtsafetySysUserFactory ytsafetySysUserFactory = new YtsafetySysUserFactory();
                    ytsafetySysUserFactory.setUserId(userId)
                            .setFactoryCode(safetyFieldManage.getFieldCode())
                            .setFactoryId(safetyFieldManage.getId())
                            .setFactoryName(safetyFieldManage.getFieldName())
                            .setCreateTime(date);
                    YtsafetySysUserFactoryList.add(ytsafetySysUserFactory);
                }
                ytsafetySysUserFactoryService.saveBatch(YtsafetySysUserFactoryList);
            }
        }
        //绑定部门
        ytsafetySysUserDepartService.remove(new LambdaQueryWrapper<YtsafetySysUserDepart>().eq(YtsafetySysUserDepart::getUserId, userId));
        String departmentId = userDto.getDepartmentId();
        if (StrUtil.isNotEmpty(departmentId)) {
            String[] departmentIds = departmentId.split(",");
            List<SafetyGroupInfo> SafetyGroupInfos = safetyGroupInfoService.lambdaQuery()
                    .in(SafetyGroupInfo::getId, Arrays.asList(departmentIds))
                    .list();
            if (CollUtil.isNotEmpty(SafetyGroupInfos)) {
                List<YtsafetySysUserDepart> ytsafetySysUserDeparts = new ArrayList<>();
                for (SafetyGroupInfo safetyGroupInfo : SafetyGroupInfos) {
                    ytsafetySysUserDeparts.add(new YtsafetySysUserDepart()
                            .setDepartCode(safetyGroupInfo.getCode())
                            .setUserId(userId)
                            .setDepartId(safetyGroupInfo.getId())
                            .setDepartName(safetyGroupInfo.getName())
                            .setCreateTime(date));
                }
                ytsafetySysUserDepartService.saveBatch(ytsafetySysUserDeparts);
            }
        }

        //删除原有角色关系
        LambdaQueryWrapper<SafetySysUserRole> wrapper = new LambdaQueryWrapper<SafetySysUserRole>()
                .eq(SafetySysUserRole::getRoleType, "2")
                .eq(SafetySysUserRole::getUserId, userId);
        safetySysUserRoleService.remove(wrapper);
        //添加角色
        String roleId = userDto.getRoleId();
        String[] roleIds = roleId.split(",");
        List<SafetySysUserRole> userRoles = new ArrayList();

        for (int i = 0; i < roleIds.length; i++) {
            SafetySysRole sysRole = safetySysRoleService.lambdaQuery()
                    .eq(SafetySysRole::getId, roleIds[i])
                    .eq(SafetySysRole::getDeleteFalg, "0")
                    .eq(SafetySysRole::getRoleType, "2")
                    .one();
            if (BeanUtil.isEmpty(sysRole)) {
                log.error("角色id:{}不存在", roleIds[i]);
                throw new RuntimeException("选择角色不存在");
            }
            SafetySysUserRole safetySysUserRole = new SafetySysUserRole()
                    .setUserId(userId)
                    .setDelFlag(0)
                    .setRoleCode(sysRole.getRoleCode())
                    .setCreateTime(date)
                    .setRoleId(sysRole.getId())
                    .setRoleType(sysRole.getRoleType())
                    .setStatus(0);
            userRoles.add(safetySysUserRole);
        }
        safetySysUserRoleService.saveBatch(userRoles);
        LoginUser currentUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<SafetySysUser> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(SafetySysUser::getId, userId);
        updateWrapper.set(SafetySysUser::getPowerFactoryId, userDto.getPowerFactoryId());
        updateWrapper.set(SafetySysUser::getUpdateTime, new Date());
        if (BeanUtil.isNotEmpty(currentUser)) {
            updateWrapper.set(SafetySysUser::getModifyEmp, currentUser.getRealname());
//            updateWrapper.set(SafetySysUser::getModifyEmpNo, currentUser.getId());
        }
        safetySysUserMapper.update(null, updateWrapper);
        //数据权限配置
        ytsafetySysUserDataRuleService.remove(new LambdaQueryWrapper<YtsafetySysUserDataRule>().eq(YtsafetySysUserDataRule::getUserId, userId));
        //数据权限配置
        List<YtsafetySysUserDataRuleDTO> userDataRuleList = userDto.getUserDataRule();
        List<YtsafetySysUserDataRule> sysUserDataRules = new ArrayList<>();
        if (CollUtil.isNotEmpty(userDataRuleList)) {
            for (YtsafetySysUserDataRuleDTO sysUserDataRule : userDataRuleList) {
                YtsafetySysUserDataRule bean = BeanUtil.toBean(sysUserDataRule, YtsafetySysUserDataRule.class);
                if (BeanUtil.isNotEmpty(currentUser)) {
                    bean.setCreateUser(currentUser.getUsername());
                }
                bean.setCreateTime(date)
                        .setUserId(userId)
                        .setDelFlag(0)
                        .setVersion(0);
                sysUserDataRules.add(bean);
            }
            ytsafetySysUserDataRuleService.saveBatch(sysUserDataRules);
        }

        //同步处理安全责任人映射数据
        ytsafetySysResponsibleService.rebuildResponsibleMapping(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        SafetySysUser sysUser = this.lambdaQuery()
                .eq(SafetySysUser::getId, id)
                .one();
        if (BeanUtil.isEmpty(sysUser)) {
            throw new RuntimeException("用户不存在");
        }
        if (sysUser.getDeleteFalg() == 1) {
            throw new RuntimeException("用户已删除");
        }
        ytsafetySysUserDataRuleService.lambdaUpdate()
                .set(YtsafetySysUserDataRule::getDelFlag , 1)
                .eq(YtsafetySysUserDataRule::getUserId, id)
                .update();
        LambdaUpdateWrapper<YtsafetySysResponsible> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(YtsafetySysResponsible::getUserId, id)
                .set(YtsafetySysResponsible::getDelFlag, 1);
        ytsafetySysResponsibleMapper.update(null ,wrapper);
        safetySysUserRoleService.lambdaUpdate()
                .eq(SafetySysUserRole::getUserId, id)
                .set(SafetySysUserRole::getDelFlag, 1)
                .update();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        deleteResponsibleMapping(id);
        this.lambdaUpdate()
                .eq(SafetySysUser::getId, id)
                .set(SafetySysUser::getDeleteFalg, 1)
//                .set(SafetySysUser::getModifyEmpNo, loginUser.getId())
                .set(SafetySysUser::getModifyEmp, loginUser.getRealname())
                .set(SafetySysUser::getUpdateTime, new Date())
                .update();
    }

    /**
     * 删除安全责任人映射数据
     * @param userId
     */
    private void deleteResponsibleMapping(String userId){
        if(StringUtils.isEmpty(userId)) return;
        LambdaQueryWrapper<YtsafetySysResponsibleMapping> mappingWrapper = new LambdaQueryWrapper<>();
        mappingWrapper.eq(YtsafetySysResponsibleMapping::getUserId, userId);
        sysResponsibleMappingMapper.delete(mappingWrapper);
    }

    @Override
    public SafetySysUserVO queryById(Long id) {
        SafetySysUserVO safetySysUserVo = safetySysUserMapper.queryById(id);
        return safetySysUserVo;
    }

    @Override
    public List<SafetySysUserVO> queryList(SafetySysUserQuery safetySysUserQueryDto) {
        return safetySysUserMapper.queryList(safetySysUserQueryDto);
    }

    @Override
    public List<SafetySysUserImpoetDTO> importExcel(List<SafetySysUserImpoetDTO> safetySysUserList) {
        List<String> empNos = safetySysUserList.stream().map(SafetySysUserImpoetDTO::getEmpNo).filter(Objects::nonNull).collect(Collectors.toList());
        Set<String> duplicateEmpNos = empNos.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting())) // 统计每个编号出现次数
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1) // 只保留重复的
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        List<SafetySysUserImpoetDTO> list = new ArrayList<>();
        for (int i = 0; i < safetySysUserList.size(); i++) {
            SafetySysUserImpoetDTO safetySysUserImpoetDTO = safetySysUserList.get(i);
            if (BeanUtil.isEmpty(safetySysUserImpoetDTO)) {
                continue;
            }
            try {
                safetySysUserServiceImport.addUser(safetySysUserImpoetDTO, duplicateEmpNos );
            } catch (Exception e) {
                safetySysUserImpoetDTO.setErrorMsg(e.getMessage());
                list.add(safetySysUserImpoetDTO);
            }
        }
        return list;
    }

    @Override
    public List<MenuTree> getTree(Long userId) {
        // 1. 一次性查询所有符合条件的菜单
        List<SafetySysPerm> allPermList = safetySysPermService.lambdaQuery()
                .isNull(SafetySysPerm::getExternalUrl)
                .eq(SafetySysPerm::getIsShow, 1)
                .in(SafetySysPerm::getCate, 1, 2)
                .list();

        if (allPermList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 一次性查询用户数据规则
        List<YtsafetySysUserDataRule> allRuleList = Collections.emptyList();
        if (userId != null) {
            allRuleList = ytsafetySysUserDataRuleService.lambdaQuery()
                    .eq(YtsafetySysUserDataRule::getUserId, userId)
                    .list();
        }

        // 3. 规则转Map：permId -> 规则列表
        Map<Long, List<YtsafetySysUserDataRule>> ruleMap = allRuleList.stream()
                .collect(Collectors.groupingBy(YtsafetySysUserDataRule::getPermId));

        // 4. 菜单转 MenuTree
        Map<Long, MenuTree> treeMap = new HashMap<>();
        for (SafetySysPerm perm : allPermList) {
            MenuTree tree = new MenuTree();
            tree.setId(String.valueOf(perm.getId()));
            tree.setName(perm.getPermName());
            tree.setCate(String.valueOf(perm.getCate()));
            tree.setLevel(perm.getLevel());
            tree.setCode(perm.getCode());
            tree.setPid(String.valueOf(perm.getParentId()));

            // ====================== 关键：无规则时设为 null ======================
            List<YtsafetySysUserDataRule> rules = ruleMap.get(perm.getId());
            if (rules != null && !rules.isEmpty()) {
                List<YtsafetySysUserDataRuleVO> ruleVos = rules.stream()
                        .map(item -> {
                            YtsafetySysUserDataRuleVO vo = new YtsafetySysUserDataRuleVO();
                            BeanUtils.copyProperties(item, vo);
                            return vo;
                        }).collect(Collectors.toList());
                tree.setUserDataRuleVOS(ruleVos);
            } else {
                tree.setUserDataRuleVOS(null); // 没有规则 → 设为 null
            }

            // 默认 children 为空数组
            tree.setChildren(new ArrayList<>());
            treeMap.put(perm.getId(), tree);
        }

        // 5. 构建树结构
        List<MenuTree> rootMenuList = new ArrayList<>();
        for (MenuTree tree : treeMap.values()) {
            Long pid = Long.valueOf(tree.getPid());
            if (pid == 0) {
                rootMenuList.add(tree);
            } else {
                MenuTree parentTree = treeMap.get(pid);
                if (parentTree != null) {
                    parentTree.getChildren().add(tree);
                }
            }
        }

        return rootMenuList;
    }

    @Override
    public void sync()  {
        List<SafetySysUser> list = this.lambdaQuery()
                .isNotNull(SafetySysUser::getPowerFactoryId)
                .ne(SafetySysUser::getPowerFactoryId, "")
                .list();
        List<YtsafetySysUserFactory> newFactorys = new ArrayList<>();
        for (SafetySysUser safetySysUser : list) {
            if(StringUtils.isNotEmpty(safetySysUser.getPowerFactoryId())) {
                //保存工厂
                List<String> factoryIds = Arrays.stream(safetySysUser.getPowerFactoryId().split(","))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
                LambdaQueryWrapper<SafetyFieldManage> factoryQuery = new LambdaQueryWrapper<>();
                factoryQuery.in(SafetyFieldManage::getId, factoryIds)
                        .eq(SafetyFieldManage::getDeleteFalg, 0)
                        .eq(SafetyFieldManage::getFieldType, "factory");
                List<YtsafetySysUserFactory> ytsafetySysUserFactories = ytsafetySysResponsibleMapper.queryUserFactory(factoryQuery, safetySysUser.getId());
//                log.info("ytsafetySysUserFactories:{}",ytsafetySysUserFactories);

                newFactorys.addAll(ytsafetySysUserFactories);
            }
        }
        ytsafetySysUserFactoryService.saveBatch(newFactorys);

    }

    @Override
    public LoginUser queryUser(String empNo) {
        return baseMapper.queryLoginUser(empNo);
    }

    @Override
    public List<LoginUser> queryUserListByEmpNos(List<String> empNos) {
        return baseMapper.queryUserListByEmpNos(empNos);
    }
}
