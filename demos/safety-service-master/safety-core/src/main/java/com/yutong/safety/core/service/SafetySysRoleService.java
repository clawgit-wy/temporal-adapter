package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.sys.SysRoleListParam;
import com.yutong.safety.common.param.web.sys.SysRoleParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.sys.SysRolePermVO;
import com.yutong.safety.common.vo.sys.SysRoleVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.mapper.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create gwcuiyx 2023.07.26
 * 角色表--service
 */
@Service
public class SafetySysRoleService {
    @Autowired
    private SafetySysRoleDOMapper safetySysRoleDOMapper;
    @Autowired
    private SafetySysRolePermDOMapper safetySysRolePermDOMapper;
    @Autowired
    private SafetySysRolePermDOBizMapper safetySysRolePermDOBizMapper;
    @Autowired
    private SafetySysUserRoleDOMapper safetySysUserRoleDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 分页数据总数量
     * @param param
     * @return
     */
    public long pageListCount(SysRoleListParam param) {
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdGreaterThan(1);
        criteria.andDeleteFalgEqualTo("0");
        if (StringUtils.isNotEmpty(param.getRoleName())) {
            criteria.andRoleNameLike("%"+param.getRoleName()+"%");
        }
        return safetySysRoleDOMapper.countByExample(example);
    }

    /**
     * 分页数据列表
     * @param param
     * @return
     */
    public List<SafetySysRoleDO> pageList(SysRoleListParam param) {
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdGreaterThan(1);
        criteria.andDeleteFalgEqualTo("0");
        if (StringUtils.isNotEmpty(param.getRoleName())) {
            criteria.andRoleNameLike("%"+param.getRoleName()+"%");
        }
        example.setOrderByClause(" id asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetySysRoleDOMapper.selectByExample(example);
    }


    /**
     * 分页数据列表
     * @param roleName 角色名称
     * @return
     */
    public SafetySysRoleDO getByRoleName(String roleName) {
        if(StringUtils.isEmpty(roleName)){
            return new SafetySysRoleDO();
        }
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdGreaterThan(1);
        criteria.andDeleteFalgEqualTo("0");
        criteria.andRoleNameEqualTo(roleName);
        List<SafetySysRoleDO> list = safetySysRoleDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return new SafetySysRoleDO();
        }
        return list.get(0);
    }


    /**
     * 数据添加操作
     * @param param
     */
    public void insertSysRole(SysRoleParam param, String tocken) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetySysRoleDO safetySysRoleDO = new SafetySysRoleDO();
        BeanUtils.copyProperties(param, safetySysRoleDO);
        safetySysRoleDO.setLastTime(new Date());
        safetySysRoleDO.setRoleDesc(param.getRoleName());
        safetySysRoleDO.setDeleteFalg("0");
        safetySysRoleDO.setLastUser(tockenUser.get("realName"));
        PreconditonsEx.checkArgument(safetySysRoleDOMapper.insert(safetySysRoleDO) == 1, ResponseEnum.FAIL);
        setRolePerms(safetySysRoleDO.getId(), param.getPermIds());

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("角色管理");
        log.setAbility("添加");
        log.setMessage(safetySysRoleDO.getRoleName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void updateSysRole(SysRoleParam param, String tocken) {
        SafetySysRoleDO safetySysRoleDO = safetySysRoleDOMapper.selectByPrimaryKey(param.getId());
        if(safetySysRoleDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        BeanUtils.copyProperties(param, safetySysRoleDO);
        safetySysRoleDO.setLastTime(new Date());
        safetySysRoleDO.setLastUser(tockenUser.get("realName"));
        PreconditonsEx.checkArgument(safetySysRoleDOMapper.updateByPrimaryKey(safetySysRoleDO) == 1, ResponseEnum.FAIL);
        setRolePerms(safetySysRoleDO.getId(), param.getPermIds());

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("角色管理");
        log.setAbility("修改");
        log.setMessage(safetySysRoleDO.getRoleName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void deleteSysRole(Integer id, String tocken) {
        //先检查是否有数据
        SafetySysRoleDO safetySysRoleDO = safetySysRoleDOMapper.selectByPrimaryKey(id);
        if(safetySysRoleDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        safetySysRoleDO.setDeleteFalg("1");
        safetySysRoleDO.setLastTime(new Date());
        PreconditonsEx.checkArgument(safetySysRoleDOMapper.updateByPrimaryKey(safetySysRoleDO) == 1, ResponseEnum.FAIL);
        //同步删除角色关联权限资源记录
        deleteRolePermsByRoleId(id);
        //同步删除账号对应的角色
        deleteUserByRoleId(id);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("角色管理");
        log.setAbility("删除");
        log.setMessage(safetySysRoleDO.getRoleName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 删除员工对应的权限
     * @param id
     */
    public void deleteUserByRoleId(Integer id) {
        SafetySysUserRoleDOExample example = new SafetySysUserRoleDOExample();
        SafetySysUserRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        safetySysUserRoleDOMapper.deleteByExample(example);
    }

    /**
     * 删除指定员工对应的指定权限权限

     * @param userId 用户ID
     * @param roleId 角色ID
     */
    public void deleteRole(List<Integer> userId, Integer roleId) {
        SafetySysUserRoleDOExample example = new SafetySysUserRoleDOExample();
        SafetySysUserRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdIn(userId);
        criteria.andRoleIdEqualTo(roleId);
        safetySysUserRoleDOMapper.deleteByExample(example);
    }

    /**
     * 根据id获取数据
     * @param id
     * @return
     */
    public Object getRoleHasPerms(Integer id) {
        SafetySysRolePermDOExample example = new SafetySysRolePermDOExample();
        SafetySysRolePermDOExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        List<SafetySysRolePermDO> list = safetySysRolePermDOMapper.selectByExample(example);
        List<Integer> has = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(list)) {
            has = list.stream().map(SafetySysRolePermDO::getPermId).collect(Collectors.toList());
        }
        return view(id, has);
    }

    /**
     * 获取详情数据
     * @param id
     * @return
     */
    public SysRolePermVO view(Integer id, List<Integer> has) {
        SafetySysRoleDO safetySysRoleDO = safetySysRoleDOMapper.selectByPrimaryKey(id);
        SysRolePermVO sysRolePermVO = new SysRolePermVO();
        sysRolePermVO.setId(safetySysRoleDO.getId());
        sysRolePermVO.setRoleName(safetySysRoleDO.getRoleName());
        sysRolePermVO.setPermIds(has);
        return sysRolePermVO;
    }

    /**
     * 数据设置
     * @param roleId
     * @param permIds
     * @return
     */
    public Object setRolePerms(Integer roleId, String permIds) {
        //删除原来的角色权限关联记录
        deleteRolePermsByRoleId(roleId);
        //添加角色权限关联记录
        addRolePerms(permIds, roleId);
        //修改角色信息变更日期
//        updateRole(roleId);
        return null;
    }

    /**
     * 根据roleId删除中间表
     * @param roleId
     */
    public void deleteRolePermsByRoleId(Integer roleId) {
        SafetySysRolePermDOExample example = new SafetySysRolePermDOExample();
        SafetySysRolePermDOExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        safetySysRolePermDOMapper.deleteByExample(example);
    }

    /**
     * 数据添加功能
     * @param permIds
     */
    public void addRolePerms(String permIds, Integer roleId) {
        String[] permArr = permIds.split(",");
        Map map = new HashMap();
        map.put("roleId", roleId);
        map.put("permIds", permArr);
        safetySysRolePermDOBizMapper.setRolePerms(map);
    }

    /**
     * 修改角色信息变更日期
     * @param roleId
     */
    public void updateRole(Integer roleId) {
        SafetySysRoleDO safetySysRoleDO = safetySysRoleDOMapper.selectByPrimaryKey(roleId);
        safetySysRoleDO.setLastTime(new Date());
        safetySysRoleDOMapper.updateByPrimaryKey(safetySysRoleDO);
    }

    /**
     * 数据列表查询
     * @return
     */
    public List<SysRoleVO> selectSysRoles() {
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
//        criteria.andIdGreaterThan(1);
        criteria.andDeleteFalgEqualTo("0");
        example.setOrderByClause(" id asc ");
        List<SafetySysRoleDO> list = safetySysRoleDOMapper.selectByExample(example);
        List<SysRoleVO> sysRoleList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(list)) {
            SysRoleVO sysRoleVO = null;
            for (SafetySysRoleDO safetySysRoleDO : list) {
                sysRoleVO = new SysRoleVO();
                sysRoleVO.setId(safetySysRoleDO.getId());
                sysRoleVO.setName(safetySysRoleDO.getRoleName());
                sysRoleList.add(sysRoleVO);
            }
        }
        return sysRoleList;
    }

    /**
     * 查询所有角色列表
     * @return
     */
    public List<SafetySysRoleDO> getAllRoleList() {
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo("0");
        return safetySysRoleDOMapper.selectByExample(example);
    }

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    public List<SafetySysRoleDO> export(SysRoleListParam param, String tocken) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetySysRoleDOExample example = new SafetySysRoleDOExample();
        SafetySysRoleDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdGreaterThan(1);
        criteria.andDeleteFalgEqualTo("0");
        if (StringUtils.isNotEmpty(param.getRoleName())) {
            criteria.andRoleNameLike("%"+param.getRoleName()+"%");
        }
        example.setOrderByClause(" id asc ");
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("角色管理");
        log.setAbility("导出");
        log.setMessage("所有");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetySysRoleDOMapper.selectByExample(example);
    }

}
