package com.yutong.modules.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.IThirdFactoryApi;
import com.yutong.modules.user.api.*;
import com.yutong.modules.user.constant.enums.SystemRoleTypeEnum;
import com.yutong.modules.user.dto.YtsafetySysResponsibleDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleImportDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleQueryDTO;
import com.yutong.modules.user.entity.*;
import com.yutong.modules.user.mapper.*;
import com.yutong.modules.user.vo.*;
import com.yutong.modules.user.constant.CustomConstantConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Service
public class YtsafetySysResponsibleServiceImpl extends ServiceImpl<YtsafetySysResponsibleMapper, YtsafetySysResponsible> implements IYtsafetySysResponsibleService {

    @Autowired
    private YtsafetySysResponsibleMapper sysResponsibleMapper;

    @Autowired
    private SafetySysUserMapper sysUserMapper;

    @Autowired
    private IYtsafetySysUserFactoryService sysUserFactoryService;

    @Autowired
    private IYtsafetySysUserDepartService sysUserDepartService;

    @Autowired
    private ISafetySysUserRoleService sysUserRoleService;

    @Autowired
    private SafetySysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SafetySysRoleMapper safetySysRoleMapper;

    @Autowired
    private SafetyCommonMapper commonMapper;

    @Autowired
    private IThirdFactoryApi thirdFactoryApi;

    @Autowired
    private SafetyGroupInfoMapper groupInfoMapper;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private IYtsafetySysResponsibleMappingService sysResponsibleMappingService;

    @Autowired
    private YtsafetySysResponsibleMappingMapper sysResponsibleMappingMapper;

    @Resource
    private CustomConstantConfig customConstantConfig;

    //安保小助手机器人ID
    @Value("${ytsafety.yxt.robotId}")
    private String yxtRebotId;
    @Value("${ytsafety.yxt.yxtCode}")
    private String yxtCode;


    @Override
    public IPage<YtsafetySysResponsibleVO> queryPage(Page<YtsafetySysResponsibleVO> page, YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO) {
        if(sysResponsibleQueryDTO.getDepartId()!=null) {
            SafetyGroupInfo groupInfo = groupInfoMapper.selectById(sysResponsibleQueryDTO.getDepartId());
            sysResponsibleQueryDTO.setDepartPath(groupInfo.getOrgCodePath());
        }
        return sysResponsibleMapper.queryPage(page,sysResponsibleQueryDTO);
    }

    @Transactional(rollbackFor= Exception.class)
    @Override
    public boolean saveSysResponsible(YtsafetySysResponsibleDTO sysResponsibleDTO) {
        YtsafetySysResponsible sysResponsible = new YtsafetySysResponsible();
        BeanUtils.copyProperties(sysResponsibleDTO, sysResponsible);
        SafetySysUser sysUser = sysResponsibleMapper.querySysUser(sysResponsibleDTO.getEmpNo());
        if (sysUser != null && sysUser.getId() != null) {
            sysResponsible.setUserId(sysUser.getId());
            sysResponsibleDTO.setUserId(sysUser.getId());
            updateUser(sysResponsibleDTO);
        } else {
            sysResponsible.setUserId(saveUser(sysResponsibleDTO));
            sysResponsibleDTO.setUserId(sysResponsible.getUserId());
        }
        if (sysResponsibleMapper.checkSysResponsibleExists(sysResponsibleDTO.getUserId())) {
            throw new RuntimeException(String.format("员工号：%s，员工名：%s，已存在安全责任人信息库中", sysResponsibleDTO.getEmpNo(), sysResponsibleDTO.getEmpName()));
        }
        sysResponsible.setUpdateSource(0);
        sysResponsible.setCreateTime(new Date());
        sysResponsible.setUpdateTime(new Date());
        sysResponsible.setVersion(0);
        sysResponsible.setDelFlag(0);
        sysResponsible.setId(IdWorker.getId());
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser != null) {
            sysResponsible.setUpdateUser(loginUser.getUsername());
            sysResponsible.setCreateUser(loginUser.getUsername());
        }
        boolean result = sysResponsibleMapper.insert(sysResponsible) == 1;
        if(result){
            //同步处理安全责任人映射数据
            rebuildResponsibleMapping(sysResponsible.getUserId());
        }
        return result;
    }

    /**
     * 保存用户
     * @param sysResponsibleDTO
     */
    private Long saveUser(YtsafetySysResponsibleDTO sysResponsibleDTO) {
        SafetySysUser safetySysUser = new SafetySysUser();
        BeanUtils.copyProperties(sysResponsibleDTO,safetySysUser);
        SafetyEmployeeMessage employeeMessage = sysResponsibleMapper.queryEmployeeMessageByEip(sysResponsibleDTO.getEmpNo());
        if(employeeMessage==null) throw new RuntimeException("EIP用户信息不存在");

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(StringUtils.isEmpty(employeeMessage.getEmpLogin())) throw new RuntimeException("eip用户名不能为空");
        safetySysUser.setUserName(employeeMessage.getEmpLogin());
        safetySysUser.setUserType(sysResponsibleDTO.getUserType());
        safetySysUser.setUpdateTime(new Date());
        safetySysUser.setDeleteFalg(0);
        safetySysUser.setCreateTime(new Date());
        if(loginUser!=null){
            safetySysUser.setModifyEmp(loginUser.getUsername());
        }
        sysUserMapper.insert(safetySysUser);

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getPowerFactoryId())) {
            //保存工厂
            List<String> factoryIds = Arrays.stream(sysResponsibleDTO.getPowerFactoryId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetyFieldManage> factoryQuery = new LambdaQueryWrapper<>();
            factoryQuery.in(SafetyFieldManage::getId, factoryIds);
            List<YtsafetySysUserFactory> newFactorys = sysResponsibleMapper.queryUserFactory(factoryQuery, safetySysUser.getId());
            sysUserFactoryService.saveBatch(newFactorys);
        }

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getDepartId())) {
            //保存部门
            List<String> departIds = Arrays.stream(sysResponsibleDTO.getDepartId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetyGroupInfo> departQuery = new LambdaQueryWrapper<>();
            departQuery.in(SafetyGroupInfo::getId, departIds);
            departQuery.eq(SafetyGroupInfo::getStatus,"Y");
            List<YtsafetySysUserDepart> newDeparts = sysResponsibleMapper.queryUserDepart(departQuery, safetySysUser.getId());
            sysUserDepartService.saveBatch(newDeparts);
        }

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getRoleId())) {
            //保存角色
            List<String> roleIds = Arrays.stream(sysResponsibleDTO.getRoleId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetySysRole> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.in(SafetySysRole::getId, roleIds);
            roleQuery.eq(SafetySysRole::getDeleteFalg,'0');
            List<SafetySysUserRole> newRoles = sysResponsibleMapper.queryUserRole(roleQuery, safetySysUser.getId(), sysResponsibleDTO.getStatus());
            sysUserRoleService.saveBatch(newRoles);
        }
        return safetySysUser.getId();
    }

    /**
     * 更新用户
     * @param sysResponsibleDTO
     */
    private void updateUser(YtsafetySysResponsibleDTO sysResponsibleDTO){
        SafetySysUser safetySysUser = sysUserMapper.selectById(sysResponsibleDTO.getUserId());
        safetySysUser.setUpdateTime(new Date());
        safetySysUser.setPowerFactoryId(sysResponsibleDTO.getPowerFactoryId());
        SafetyEmployeeMessage employeeMessage = sysResponsibleMapper.queryEmployeeMessageByEip(safetySysUser.getEmpNo());
        if(employeeMessage==null) throw new RuntimeException("EIP用户信息不存在");
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        safetySysUser.setUserName(employeeMessage.getEmpLogin());
        safetySysUser.setUserType(sysResponsibleDTO.getUserType());
        safetySysUser.setUpdateTime(new Date());
        safetySysUser.setDeleteFalg(0);
        safetySysUser.setCreateTime(new Date());
        if(loginUser!=null){
            safetySysUser.setModifyEmp(loginUser.getUsername());
        }
        sysUserMapper.updateById(safetySysUser);

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getPowerFactoryId())) {
            //保存工厂
            List<String> factoryIds = Arrays.stream(sysResponsibleDTO.getPowerFactoryId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetyFieldManage> factoryQuery = new LambdaQueryWrapper<>();
            factoryQuery.in(SafetyFieldManage::getId, factoryIds);
            List<YtsafetySysUserFactory> newFactorys = sysResponsibleMapper.queryUserFactory(factoryQuery, safetySysUser.getId());
            sysResponsibleMapper.deleteUserFactory(safetySysUser.getId());
            sysUserFactoryService.saveBatch(newFactorys);
        }else{
            sysResponsibleMapper.deleteUserFactory(safetySysUser.getId());
        }

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getDepartId())) {
            //保存部门
            List<String> departIds = Arrays.stream(sysResponsibleDTO.getDepartId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetyGroupInfo> departQuery = new LambdaQueryWrapper<>();
            departQuery.in(SafetyGroupInfo::getId, departIds);
            departQuery.eq(SafetyGroupInfo::getStatus,"Y");
            List<YtsafetySysUserDepart> newDeparts = sysResponsibleMapper.queryUserDepart(departQuery, safetySysUser.getId());
            sysResponsibleMapper.deleteUserDepart(safetySysUser.getId());
            sysUserDepartService.saveBatch(newDeparts);
        } else {
            sysResponsibleMapper.deleteUserDepart(safetySysUser.getId());
        }

        if(StringUtils.isNotEmpty(sysResponsibleDTO.getRoleId())) {
            //保存角色
            List<String> roleIds = Arrays.stream(sysResponsibleDTO.getRoleId().split(","))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SafetySysRole> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.in(SafetySysRole::getId, roleIds);
            roleQuery.eq(SafetySysRole::getDeleteFalg,'0');
            List<SafetySysUserRole> newRoles = sysResponsibleMapper.queryUserRole(roleQuery, safetySysUser.getId(), sysResponsibleDTO.getStatus());
            LambdaQueryWrapper<SafetySysUserRole> userRoleQuery = new LambdaQueryWrapper<>();
            userRoleQuery.eq(SafetySysUserRole::getUserId, safetySysUser.getId());
            userRoleQuery.in(SafetySysUserRole::getRoleType, Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(), SystemRoleTypeEnum.Safety.getCode()}));
            sysResponsibleMapper.deleteRoleByUserId(userRoleQuery);
            sysUserRoleService.saveBatch(newRoles);
        }else{
            LambdaQueryWrapper<SafetySysUserRole> userRoleQuery = new LambdaQueryWrapper<>();
            userRoleQuery.eq(SafetySysUserRole::getUserId, safetySysUser.getId());
            userRoleQuery.in(SafetySysUserRole::getRoleType, Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(), SystemRoleTypeEnum.Safety.getCode()}));
            sysResponsibleMapper.deleteRoleByUserId(userRoleQuery);
        }
    }

    /**
     * 查询现有角色
     * @param userId
     * @return
     */
    private String queryUserRoles(Long userId){
        return sysResponsibleMapper.queryUserSystemRole(userId,0);
    }

    /**
     * 删除安全责任人映射数据
     * @param userId
     */
    private void deleteResponsibleMapping(Long userId){
        if(userId==null) return;
        LambdaQueryWrapper<YtsafetySysResponsibleMapping> mappingWrapper = new LambdaQueryWrapper<>();
        mappingWrapper.eq(YtsafetySysResponsibleMapping::getUserId, userId);
        sysResponsibleMappingMapper.delete(mappingWrapper);
    }

    /**
     * 重建安全责任人映射数据
     * 数据关系：ytsafety_sys_responsible × ytsafety_sys_user_depart × ytsafety_sys_user_factory × safety_sys_user_role 相互组合
     * 注意：授权厂区(factorys)、授权部门(departs)可能为空，为空时对应字段以NULL生成映射记录；
     *      仅当无有效安保/非安保角色时才不生成映射数据
     * @param userId
     */
    @Override
    public void rebuildResponsibleMapping(Long userId){
        if(userId==null) return;
        //先删除原有映射数据
        deleteResponsibleMapping(userId);

        //查询责任信息
        LambdaQueryWrapper<YtsafetySysResponsible> responsibleWrapper = new LambdaQueryWrapper<>();
        responsibleWrapper.eq(YtsafetySysResponsible::getUserId, userId);
        responsibleWrapper.eq(YtsafetySysResponsible::getDelFlag, 0);
        YtsafetySysResponsible sysResponsible = this.getOne(responsibleWrapper, false);
        if(sysResponsible==null) return;

        //查询用户授权厂区
        LambdaQueryWrapper<YtsafetySysUserFactory> factoryWrapper = new LambdaQueryWrapper<>();
        factoryWrapper.eq(YtsafetySysUserFactory::getUserId, userId);
        List<YtsafetySysUserFactory> factorys = sysUserFactoryService.list(factoryWrapper);

        //查询用户授权部门
        LambdaQueryWrapper<YtsafetySysUserDepart> departWrapper = new LambdaQueryWrapper<>();
        departWrapper.eq(YtsafetySysUserDepart::getUserId, userId);
        List<YtsafetySysUserDepart> departs = sysUserDepartService.list(departWrapper);

        //查询用户角色（有效，且角色类型为安保/非安保）
        LambdaQueryWrapper<SafetySysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SafetySysUserRole::getUserId, userId);
        roleWrapper.eq(SafetySysUserRole::getDelFlag, 0);
        roleWrapper.in(SafetySysUserRole::getRoleType, Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(), SystemRoleTypeEnum.Safety.getCode()}));
        List<SafetySysUserRole> roles = sysUserRoleService.list(roleWrapper);

        if(CollUtil.isEmpty(roles)) return;

        //查询角色名称
        List<Long> roleIds = roles.stream().map(SafetySysUserRole::getRoleId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> roleNameMap = new HashMap<>();
        if(CollUtil.isNotEmpty(roleIds)){
            LambdaQueryWrapper<SafetySysRole> roleNameWrapper = new LambdaQueryWrapper<>();
            roleNameWrapper.in(SafetySysRole::getId, roleIds);
            roleNameMap = safetySysRoleMapper.selectList(roleNameWrapper).stream()
                    .collect(Collectors.toMap(SafetySysRole::getId, SafetySysRole::getRoleName, (oldVal, newVal) -> oldVal));
        }

        //查询部门路径
        List<Long> departIds = CollUtil.isEmpty(departs) ? new ArrayList<>()
                : departs.stream().map(YtsafetySysUserDepart::getDepartId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SafetyGroupInfo> groupInfoMap = new HashMap<>();
        if(CollUtil.isNotEmpty(departIds)){
            LambdaQueryWrapper<SafetyGroupInfo> groupInfoWrapper = new LambdaQueryWrapper<>();
            groupInfoWrapper.in(SafetyGroupInfo::getId, departIds);
            groupInfoMap = groupInfoMapper.selectList(groupInfoWrapper).stream()
                    .collect(Collectors.toMap(SafetyGroupInfo::getId, item -> item, (oldVal, newVal) -> oldVal));
        }

        //厂区、部门可能为空，为空时用单个null占位参与组合，保证映射数据仍能生成（对应字段为NULL）
        List<YtsafetySysUserFactory> factoryList = CollUtil.isEmpty(factorys) ? Collections.singletonList(null) : factorys;
        List<YtsafetySysUserDepart> departList = CollUtil.isEmpty(departs) ? Collections.singletonList(null) : departs;

        Date now = new Date();
        List<YtsafetySysResponsibleMapping> mappings = new ArrayList<>();
        for(YtsafetySysUserFactory factory : factoryList){
            for(YtsafetySysUserDepart depart : departList){
                for(SafetySysUserRole role : roles){
                    YtsafetySysResponsibleMapping mapping = new YtsafetySysResponsibleMapping();
                    mapping.setUserId(userId);
                    mapping.setEmpNo(sysResponsible.getEmpNo());
                    mapping.setEmpName(sysResponsible.getEmpName());
                    mapping.setUserName(sysResponsible.getUserName());
                    mapping.setFactoryId(factory==null ? null : factory.getFactoryId());
                    mapping.setFactoryName(factory==null ? null : factory.getFactoryName());
                    mapping.setDepartId(depart==null ? null : depart.getDepartId());
                    mapping.setDepartName(depart==null ? null : depart.getDepartName());
                    mapping.setRoleId(role.getRoleId());
                    mapping.setRoleName(roleNameMap.get(role.getRoleId()));
                    mapping.setDepartCode(depart==null ? null : depart.getDepartCode());
                    SafetyGroupInfo groupInfo = (depart==null || depart.getDepartId()==null) ? null : groupInfoMap.get(depart.getDepartId());
                    mapping.setDepartPath(groupInfo==null ? null : groupInfo.getOrgCodePath());
                    mapping.setRoleCode(role.getRoleCode());
                    mapping.setCreateTime(now);
                    mappings.add(mapping);
                }
            }
        }
        if(CollUtil.isNotEmpty(mappings)){
            sysResponsibleMappingService.saveBatch(mappings);
        }
    }

    /**
     * 老数据同步：遍历有效的安全责任信息，重建安全责任人映射数据
     * @return 同步的责任人数量
     */
    @Transactional(rollbackFor= Exception.class)
    @Override
    public int syncHistoryResponsibleMapping() {
        LambdaQueryWrapper<YtsafetySysResponsible> responsibleWrapper = new LambdaQueryWrapper<>();
        responsibleWrapper.eq(YtsafetySysResponsible::getDelFlag, 0);
        responsibleWrapper.isNotNull(YtsafetySysResponsible::getUserId);
        List<YtsafetySysResponsible> responsibles = this.list(responsibleWrapper);
        if(CollUtil.isEmpty(responsibles)) return 0;
        int count = 0;
        for(YtsafetySysResponsible responsible : responsibles){
            rebuildResponsibleMapping(responsible.getUserId());
            count++;
        }
        return count;
    }

    @Transactional(rollbackFor= Exception.class)
    @Override
    public boolean editSysResponsible(YtsafetySysResponsibleDTO sysResponsibleDTO) {
        YtsafetySysResponsible sysResponsible = sysResponsibleMapper.selectById(sysResponsibleDTO.getId());
        if(sysResponsible==null) throw new RuntimeException("安全责任信息已被删除，请刷新列表后在操作！");
        sysResponsible.setStatus(sysResponsibleDTO.getStatus());
        sysResponsible.setValidPeriod(sysResponsibleDTO.getValidPeriod());
        sysResponsible.setRemark(sysResponsibleDTO.getRemark());
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysResponsible.setUpdateTime(new Date());
        if(loginUser!=null){
            sysResponsible.setUpdateUser(loginUser.getUsername());
        }
        updateUser(sysResponsibleDTO);
        sysResponsible.setUserType(sysResponsibleDTO.getUserType());
        boolean result = sysResponsibleMapper.updateById(sysResponsible)==1;
        if(result){
            //同步处理安全责任人映射数据
            rebuildResponsibleMapping(sysResponsible.getUserId());
        }
        return result;
    }

    @Transactional(rollbackFor= Exception.class)
    @Override
    public void deleteSysResponsible(Long id) {
        YtsafetySysResponsible sysResponsible = sysResponsibleMapper.selectById(id);
        if(sysResponsible==null) throw new RuntimeException("安全责任信息已被删除，请刷新列表后在操作！");
        LambdaQueryWrapper<SafetySysUserRole> userRoleQuery = new LambdaQueryWrapper<>();
        userRoleQuery.eq(SafetySysUserRole::getUserId,sysResponsible.getUserId());
        userRoleQuery.in(SafetySysUserRole::getRoleType,Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(),SystemRoleTypeEnum.Safety.getCode()}));
        sysUserRoleMapper.delete(userRoleQuery);
        //同步删除安全责任人映射数据
        deleteResponsibleMapping(sysResponsible.getUserId());
        sysResponsibleMapper.deleteById(id);
    }

    @Override
    public YtsafetySysResponsibleEditVO querySysResponsibleById(Long id) {
        YtsafetySysResponsible sysResponsible = sysResponsibleMapper.selectById(id);
        if(sysResponsible==null) throw new RuntimeException("安全责任信息已被删除，请刷新列表后在操作！");
        return sysResponsibleMapper.querySysResponsibleEditVO(id);
    }

    @Override
    public YtsafetySysResponsibleEditVO querySysUserByEmpNo(String empNo) {
        return sysResponsibleMapper.querySysUserByEmpNo(empNo);
    }

    @Override
    public YtsafetySysResponsibleEditVO queryResponsibleByEmpNo(String empNo) {
        return sysResponsibleMapper.queryResponsibleByEmpNo(empNo);
    }

    @Override
    public void updateSysResponsibleStatus(Date currentDate) {
        LambdaQueryWrapper<YtsafetySysResponsible> queryResponsibleWrapper =new LambdaQueryWrapper<YtsafetySysResponsible>();
        //queryResponsibleWrapper.eq(YtsafetySysResponsible::getDelFlag,0);
        queryResponsibleWrapper.eq(YtsafetySysResponsible::getStatus,0);
        queryResponsibleWrapper.isNotNull(YtsafetySysResponsible::getValidPeriod);
        queryResponsibleWrapper.lt(YtsafetySysResponsible::getValidPeriod,currentDate);
        List<Long> userId = this.list(queryResponsibleWrapper).stream().map(YtsafetySysResponsible::getUserId).collect(Collectors.toList());
        if(userId.isEmpty()) return;
        LambdaQueryWrapper<YtsafetySysResponsible> updateResponsibleWrapper =new LambdaQueryWrapper<YtsafetySysResponsible>();
        updateResponsibleWrapper.in(YtsafetySysResponsible::getUserId,userId);
        updateResponsibleWrapper.lt(YtsafetySysResponsible::getValidPeriod,currentDate);
        sysResponsibleMapper.updateResponsibleStatus(updateResponsibleWrapper,1);
        LambdaQueryWrapper<SafetySysUserRole> updateUserRoleWrapper =new LambdaQueryWrapper<SafetySysUserRole>();
        updateUserRoleWrapper.in(SafetySysUserRole::getUserId,userId);
        updateUserRoleWrapper.eq(SafetySysUserRole::getDelFlag,0);
        updateUserRoleWrapper.in(SafetySysUserRole::getRoleType,Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(),SystemRoleTypeEnum.Safety.getCode()}));
        sysResponsibleMapper.updateRolesStatus(updateUserRoleWrapper,1);
        //同步删除安全责任人映射数据
        LambdaQueryWrapper<YtsafetySysResponsibleMapping> mappingWrapper = new LambdaQueryWrapper<>();
        mappingWrapper.in(YtsafetySysResponsibleMapping::getUserId, userId);
        sysResponsibleMappingMapper.delete(mappingWrapper);
    }

    @Override
    public ModelAndView exportSysResponsible(HttpServletRequest request, YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO) {
        if(sysResponsibleQueryDTO.getDepartId()!=null) {
            SafetyGroupInfo groupInfo = groupInfoMapper.selectById(sysResponsibleQueryDTO.getDepartId());
            sysResponsibleQueryDTO.setDepartPath(groupInfo.getOrgCodePath());
        }
        List<YtsafetySysResponsibleVO> results = sysResponsibleMapper.queryExportList(sysResponsibleQueryDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "安全责任人信息库");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetySysResponsibleVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("安全责任人信息库", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "安全责任人");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, results);
        return mv;
    }

    @Override
    public List<YtsafetySysResponsibleImportDTO> importSysResponsible(List<YtsafetySysResponsibleImportDTO> safetySysUserDtoList) throws Exception {
        List<String> empNos = safetySysUserDtoList.stream().map(YtsafetySysResponsibleImportDTO::getEmpNo).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        List<YtsafetySysResponsibleImportDTO> msgVo = new ArrayList<YtsafetySysResponsibleImportDTO>();
        if (CollUtil.isEmpty(empNos)) {
            throw new RuntimeException("请导入正确数据");
        }
        List<YtsafetySysResponsibleEditVO> sysResponsibles = sysResponsibleMapper.querySysUserByUserNo(empNos);
        if(sysResponsibles==null||sysResponsibles.isEmpty()){
            safetySysUserDtoList.forEach(item -> {
                item.setErrorMsg("EIP用户信息不存在");
                msgVo.add(item);
            });
            return msgVo;
        }
        //映射成Map
        Map<String, YtsafetySysResponsibleEditVO> map = sysResponsibles.stream()
                .collect(Collectors.toMap(YtsafetySysResponsibleEditVO::getEmpNo, // key：id字段
                        vo -> vo,                            // value：vo对象
                        (oldVal, newVal) -> oldVal           // 重复id时保留旧值
                ));

        List<SafetyFieldManageVO> factorys = commonMapper.queryFactoryDrop();
        Map<String, Long> factoryMap = factorys.stream()
                .collect(Collectors.toMap(SafetyFieldManageVO::getFieldName, // key：id字段
                        SafetyFieldManageVO::getId,                            // value：vo对象
                        (oldVal, newVal) -> oldVal           // 重复id时保留旧值
                ));

//        List<SafetyGroupInfoPathVO> groupInfos = commonMapper.queryGroupInfoPath();
//        Map<String, Long> groupInfoMap = groupInfos.stream()
//                .collect(Collectors.toMap(x -> x.getOrgNamePath().replace(",", "_"), // key：id字段
//                        SafetyGroupInfoPathVO::getId,                            // value：vo对象
//                        (oldVal, newVal) -> oldVal         // 重复id时保留旧值
//                ));
        Map<String, Long> groupInfoMap = safetyCommonService.queryGroupInfoMap();


        LambdaQueryWrapper<SafetySysRole> queryWrapper = new LambdaQueryWrapper<SafetySysRole>();
        queryWrapper.eq(SafetySysRole::getDeleteFalg, "0");
        queryWrapper.in(SafetySysRole::getRoleType, Arrays.asList(new Integer[]{SystemRoleTypeEnum.UnSafety.getCode(),SystemRoleTypeEnum.Safety.getCode()}));
        List<SafetySysRoleOptionListVO> roles = commonMapper.optionlist(queryWrapper);
        Map<String, Long> rolesMap = roles.stream()
                .collect(Collectors.toMap(SafetySysRoleOptionListVO::getRoleName, // key：id字段
                        SafetySysRoleOptionListVO::getId,                            // value：vo对象
                        (oldVal, newVal) -> oldVal         // 重复id时保留旧值
                ));

        for (YtsafetySysResponsibleImportDTO dto : safetySysUserDtoList) {
            try {
                if(dto.getIsRepeat()==1) throw new RuntimeException("信息有重复");
                if(StringUtils.isEmpty(dto.getEmpNo())) throw new RuntimeException("员工号不能为空");
                YtsafetySysResponsibleEditVO vo = map.get(dto.getEmpNo());
                if(vo==null) throw new RuntimeException("EIP用户信息不存在");
                YtsafetySysResponsibleDTO sysResponsibleDTO = new YtsafetySysResponsibleDTO();
                sysResponsibleDTO.setUserId(vo.getUserId());
                sysResponsibleDTO.setStatus(0);
                sysResponsibleDTO.setValidPeriod(dto.getValidPeriod());
                sysResponsibleDTO.setRoleId(getRoleIds(vo.getRoleId(), dto.getRoleName(), rolesMap));
                sysResponsibleDTO.setBelongFactoryId(vo.getBelongFactoryId());
                sysResponsibleDTO.setBelongFactoryName(vo.getBelongFactoryName());
                sysResponsibleDTO.setEmpName(vo.getEmpName());
                sysResponsibleDTO.setEmpNo(vo.getEmpNo());
                sysResponsibleDTO.setPowerFactoryId(getFactoryIds(vo.getPowerFactoryId(), dto.getFactoryName(), factoryMap));
                sysResponsibleDTO.setDepartId(getDepart(vo.getDepartId(), dto.getDepartName(), groupInfoMap));
                sysResponsibleDTO.setUserType(vo.getUserType());
                saveSysResponsible(sysResponsibleDTO);
            } catch (Exception e) {
                dto.setErrorMsg(e.getMessage());
                msgVo.add(dto);
            }
        }
        return msgVo;
    }

    /**
     * 厂区处理
     * @param oldIds
     * @param importFactory
     * @return
     */
    private String getFactoryIds(String oldIds,String importFactory, Map<String, Long> factoryMap){
        if(StringUtils.isEmpty(importFactory)) importFactory="";
        String[] obj = importFactory.split(",");
        if (StringUtils.isEmpty(oldIds)) oldIds = "";
        List<String> source = Arrays.asList(oldIds.split(","));
        StringBuilder oldIdsBuilder = new StringBuilder(oldIds);
        for(String key:obj){
            if(StringUtils.isEmpty(key.trim())) continue;
            String temp= String.valueOf(factoryMap.get(key)).toLowerCase().replace("null","");
            if(StringUtils.isEmpty(temp)) throw new RuntimeException(key+"厂区不存在");
            if(!oldIdsBuilder.toString().isEmpty()&&source.contains(temp)){
                temp="";
            }
            if(!temp.isEmpty()){
                oldIdsBuilder.append(",").append(temp);
            }
        }
        oldIds = oldIdsBuilder.toString();
        if(oldIds.startsWith(",")) oldIds = oldIds.substring(1);
        return oldIds;
    }

    /**
     * 角色处理
     * @param oldIds
     * @param importRoles
     * @return
     */
    private String getRoleIds(String oldIds,String importRoles,Map<String, Long> rolesMap) {
        if(StringUtils.isEmpty(importRoles)) throw new RuntimeException("角色不能为空");
        String[] obj = importRoles.split(",");
        if (StringUtils.isEmpty(oldIds)) oldIds = "";
        List<String> source = Arrays.asList(oldIds.split(","));
        StringBuilder oldIdsBuilder = new StringBuilder(oldIds);
        for (String key : obj) {
            if(StringUtils.isEmpty(key.trim())) continue;
            String temp = String.valueOf(rolesMap.get(key)).toLowerCase().replace("null","");;
            if(StringUtils.isEmpty(temp)) throw new RuntimeException(key+"角色不存在");
            if (!oldIdsBuilder.toString().isEmpty() && source.contains(temp)) {
                temp = "";
            }
            if (!temp.isEmpty()) {
                oldIdsBuilder.append(",").append(temp);
            }
        }
        oldIds = oldIdsBuilder.toString();
        if(oldIds.startsWith(",")) oldIds = oldIds.substring(1);
        return oldIds;
    }

    /**
     * 角色处理
     * @param oldIds
     * @param importDeparts
     * @return
     */
    private String getDepart(String oldIds,String importDeparts,Map<String, Long> groupInfoMap){
        if( StringUtils.isEmpty(importDeparts))importDeparts="";
        String[] obj = importDeparts.split(",");
        if (StringUtils.isEmpty(oldIds)) oldIds = "";
        List<String> source = Arrays.asList(oldIds.split(","));
        StringBuilder oldIdsBuilder = new StringBuilder(oldIds);
        for(String key:obj){
            if(StringUtils.isEmpty(key.trim())) continue;
            String temp= String.valueOf(groupInfoMap.get(key)).toLowerCase().replace("null","");;
            if(StringUtils.isEmpty(temp)) throw new RuntimeException(key+"部门不存在");
            if(!oldIdsBuilder.toString().isEmpty()&&source.contains(temp)){
                temp="";
            }
            if(!temp.isEmpty()){
                oldIdsBuilder.append(",").append(temp);
            }
        }
        oldIds = oldIdsBuilder.toString();
        if(oldIds.startsWith(",")) oldIds = oldIds.substring(1);
        return oldIds;
    }


}
