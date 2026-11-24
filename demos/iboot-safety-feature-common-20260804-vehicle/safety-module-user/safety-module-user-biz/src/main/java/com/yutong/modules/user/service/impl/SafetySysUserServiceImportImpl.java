package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yutong.modules.user.api.*;
import com.yutong.modules.user.dto.SafetySysUserDTO;
import com.yutong.modules.user.dto.SafetySysUserImpoetDTO;
import com.yutong.modules.user.entity.*;
import com.yutong.modules.user.mapper.SafetySysUserMapper;
import com.yutong.modules.user.service.*;
import com.yutong.modules.user.vo.SafetySysUserImportVO;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class SafetySysUserServiceImportImpl {

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
    private ISafetyEmployeeMessageService employeeMessageService;
    @Autowired
    private IYTSafetySysOrgFactoryService safetySysOrgFactoryService;


    @Transactional(rollbackFor = Exception.class)
    public void addUser(SafetySysUserImpoetDTO safetySysUserDto, Set<String> duplicateEmpNos) {
        if (duplicateEmpNos.contains(safetySysUserDto.getEmpNo())) {
            throw new RuntimeException("员工号重复");
        }
        SafetyEmployeeMessage employeeMessage = employeeMessageService.lambdaQuery()
                .eq(SafetyEmployeeMessage::getEmpNo, safetySysUserDto.getEmpNo())
                .last("limit 1")
                .one();
        log.info("employeeMessage:{}", employeeMessage);
        if (BeanUtil.isEmpty(employeeMessage)) {
            log.error("员工号:{}不存在", safetySysUserDto.getEmpNo());
            throw new RuntimeException("员工不存在");
        }
        if (StrUtil.isBlank(employeeMessage.getEmpLogin())) {
            log.error("eip用户名不存在");
            throw new RuntimeException("eip用户名不存在");
        }

        //判断账户是否存在
        LambdaQueryWrapper<SafetySysUser> queryWrapper = new LambdaQueryWrapper<SafetySysUser>()
                .eq(SafetySysUser::getEmpNo, safetySysUserDto.getEmpNo())
                .eq(SafetySysUser::getDeleteFalg, "0")
                .last("limit 1");
        SafetySysUser sysUser = safetySysUserMapper.selectOne(queryWrapper);
        if (BeanUtil.isNotEmpty(sysUser)) {
            throw new RuntimeException("用户已存在");
        }

        Date date = new Date();
        SafetySysUser safetySysUser = new SafetySysUser();
        safetySysUser.setEmpNo(employeeMessage.getEmpNo())
                .setEmpName(employeeMessage.getEmpName())
                .setUserName(employeeMessage.getEmpLogin());

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (BeanUtil.isNotEmpty(user)) {
            safetySysUser.setModifyEmp(user.getRealname());
//                safetySysUser.setModifyEmpNo(user.getId());
        }
        safetySysUser.setCreateTime(date);
        safetySysUser.setUpdateTime(date);
        //保存
        safetySysUserMapper.insert(safetySysUser);
        Long userId = safetySysUser.getId();

        //责任部门
        String departmentId = safetySysUserDto.getDepartmentId();
        if (StrUtil.isNotBlank(departmentId)) {
            String[] split = departmentId.split(",");
            List<YtsafetySysUserDepart> ytsafetySysUserDeparts = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                String path = split[i].replace("_", ",");
                SafetyGroupInfo safetyGroupInfo = safetyGroupInfoService.lambdaQuery()
                        .eq(SafetyGroupInfo::getOrgNamePath, path)
                        .eq(SafetyGroupInfo::getStatus, "Y")
                        .last("limit 1")
                        .one();
                if (BeanUtil.isNotEmpty(safetyGroupInfo)) {
                    ytsafetySysUserDeparts.add(new YtsafetySysUserDepart()
                            .setDepartCode(safetyGroupInfo.getCode())
                            .setUserId(userId)
                            .setDepartId(safetyGroupInfo.getId())
                            .setDepartName(safetyGroupInfo.getName())
                            .setCreateTime(date));
                }

            }
            ytsafetySysUserDepartService.saveBatch(ytsafetySysUserDeparts);

        }

        //授权厂区厂id
        String powerFactoryId = safetySysUserDto.getPowerFactoryId();
        if (StrUtil.isNotBlank(powerFactoryId)) {
            String[] factoryIds = powerFactoryId.split(",");
            //绑定用户和厂区的授权关系
            List<SafetyFieldManage> SafetyFieldManageList = safetyFieldManageService.lambdaQuery()
                    .in(SafetyFieldManage::getFieldName, Arrays.asList(factoryIds))
                    .eq(SafetyFieldManage::getDeleteFalg, 0)
                    .eq(SafetyFieldManage::getFieldType, "factory")
                    .list();
            if (CollUtil.isNotEmpty(SafetyFieldManageList)) {
                List<YtsafetySysUserFactory> YtsafetySysUserFactoryList = new ArrayList<>();
                for (SafetyFieldManage safetyFieldManage : SafetyFieldManageList) {
                    if (BeanUtil.isEmpty(safetyFieldManage)) {
                        continue;
                    }
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
            List<String> collect = SafetyFieldManageList.stream()
                    .map(ytsafetySysUserFactory -> String.valueOf(ytsafetySysUserFactory.getId()))
                    .collect(Collectors.toList());
            LambdaUpdateWrapper<SafetySysUser> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SafetySysUser::getId, userId)
                    .set(SafetySysUser::getPowerFactoryId, String.join(",", collect));
            safetySysUserMapper.update(null, wrapper);

        }
        //添加角色
        String roleId = safetySysUserDto.getRoleId();
        if (StrUtil.isBlank(roleId)) {
            log.error("角色不能为空");
            throw new RuntimeException("角色不能为空");
        }
        String[] roleIds = roleId.split(",");
        List<SafetySysUserRole> userRoles = new ArrayList();
        for (int j = 0; j < roleIds.length; j++) {
            SafetySysRole sysRole = safetySysRoleService.lambdaQuery()
                    .eq(SafetySysRole::getRoleName, roleIds[j])
                    .eq(SafetySysRole::getDeleteFalg, "0")
                    .eq(SafetySysRole::getRoleType, "2")
                    .one();
            if (BeanUtil.isEmpty(sysRole)) {

                log.error("角色id:{}不存在", roleId);
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


    }
}
