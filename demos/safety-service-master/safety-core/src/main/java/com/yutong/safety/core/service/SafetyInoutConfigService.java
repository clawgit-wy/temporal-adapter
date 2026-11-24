package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.InoutConfigAddParam;
import com.yutong.safety.common.param.web.InoutConfigListParam;
import com.yutong.safety.common.param.web.InoutConfigUpdateParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyInoutConfigDO;
import com.yutong.safety.core.db.entity.SafetyInoutConfigDOExample;
import com.yutong.safety.core.db.mapper.SafetyInoutConfigDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gwcuiyx
 * @date 2024-4-8 16:16
 */
@Service
public class SafetyInoutConfigService {
    @Autowired
    private SafetyInoutConfigDOMapper safetyInoutConfigDOMapper;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public List<SafetyInoutConfigDO> pageList(InoutConfigListParam param, Map<Integer, String> factoryNames) {
        SafetyInoutConfigDOExample example = new SafetyInoutConfigDOExample();
        SafetyInoutConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpCode())) {
            criteria.andEmpCodeLike("%"+param.getEmpCode()+"%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getPermissionType())) {
            criteria.andPermissionTypeEqualTo(param.getPermissionType());
        }
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        example.setOrderByClause("update_time DESC");
        List<SafetyInoutConfigDO> list = safetyInoutConfigDOMapper.selectByExample(example);
        for (SafetyInoutConfigDO inoutConfigDO : list) {
            if (factoryNames.containsKey(inoutConfigDO.getFactoryId())) {
                inoutConfigDO.setFactoryName(factoryNames.get(inoutConfigDO.getFactoryId()));
            }
        }
        return list;
    }

    /**
     * 分页总数据量
     * @param param
     * @return
     */
    public long pageListCount(InoutConfigListParam param) {
        SafetyInoutConfigDOExample example = new SafetyInoutConfigDOExample();
        SafetyInoutConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpCode())) {
            criteria.andEmpCodeLike("%"+param.getEmpCode()+"%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getPermissionType())) {
            criteria.andPermissionTypeEqualTo(param.getPermissionType());
        }
        return safetyInoutConfigDOMapper.countByExample(example);
    }

    /**
     * 添加接口
     * @param param
     */
    public void add(InoutConfigAddParam param, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetyInoutConfigDO safetyInoutConfigDO = new SafetyInoutConfigDO();
        BeanUtils.copyProperties(param, safetyInoutConfigDO);
        safetyInoutConfigDO.setCreateBy(tokenUser.get("realName"));
        safetyInoutConfigDO.setUpdateBy(tokenUser.get("realName"));
        safetyInoutConfigDO.setCreateTime(new Date());
        safetyInoutConfigDO.setUpdateTime(new Date());
        safetyInoutConfigDO.setDeleteFalg(false);
        PreconditonsEx.checkArgument(safetyInoutConfigDOMapper.insert(safetyInoutConfigDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 数据更新接口
     * @param param
     * @param token
     */
    public void update(InoutConfigUpdateParam param, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetyInoutConfigDO safetyInoutConfigDO = getInfoById(param.getId());
        if(safetyInoutConfigDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetyInoutConfigDO);
        safetyInoutConfigDO.setUpdateTime(new Date());
        safetyInoutConfigDO.setUpdateBy(tokenUser.get("realName"));
        PreconditonsEx.checkArgument(safetyInoutConfigDOMapper.updateByPrimaryKey(safetyInoutConfigDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 根据id获取数据
     * @param id
     * @return
     */
    public SafetyInoutConfigDO getInfoById(Integer id) {
        return safetyInoutConfigDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 数据删除接口
     * @param id
     * @param token
     */
    public void delete(Integer id, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetyInoutConfigDO safetyInoutConfigDO = getInfoById(id);
        if(safetyInoutConfigDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyInoutConfigDO.setUpdateBy(tokenUser.get("realName"));
        safetyInoutConfigDO.setUpdateTime(new Date());
        safetyInoutConfigDO.setDeleteFalg(true);
        PreconditonsEx.checkArgument(safetyInoutConfigDOMapper.updateByPrimaryKey(safetyInoutConfigDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 数据导出查询
     * @param param
     * @param token
     * @return
     */
    public List<SafetyInoutConfigDO> export(InoutConfigListParam param, String token) {
        SafetyInoutConfigDOExample example = new SafetyInoutConfigDOExample();
        SafetyInoutConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpCode())) {
            criteria.andEmpCodeLike("%"+param.getEmpCode()+"%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getPermissionType())) {
            criteria.andPermissionTypeEqualTo(param.getPermissionType());
        }
        example.setOrderByClause("update_time DESC");
        return safetyInoutConfigDOMapper.selectByExample(example);
    }

    /**
     * 手机端获取数据
     * @param empCode
     * @return
     */
    public Set<String> getPermission(String empCode) {
        SafetyInoutConfigDOExample example = new SafetyInoutConfigDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andEmpCodeEqualTo(empCode);
        List<SafetyInoutConfigDO> list = safetyInoutConfigDOMapper.selectByExample(example);
        Set<String> set = new HashSet<>();
        list.forEach(obj -> set.add(obj.getPermissionType()));
        return set;
    }

    /**
     * 获取对象数据
     * @param empCode
     * @return
     */
    public SafetyInoutConfigDO getObjectPermission(String empCode) {
        SafetyInoutConfigDOExample example = new SafetyInoutConfigDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andEmpCodeEqualTo(empCode);
        example.setOrderByClause("update_time DESC");
        List<SafetyInoutConfigDO> list = safetyInoutConfigDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

}
