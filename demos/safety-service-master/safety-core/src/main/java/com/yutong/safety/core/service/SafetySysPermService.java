package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.sys.SysPermParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.SafetySysPermDO;
import com.yutong.safety.core.db.entity.SafetySysPermDOExample;
import com.yutong.safety.core.db.mapper.SafetySysPermDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetySysPermDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * create gwcuiyx 2023.07.26
 * 权限资源表--service
 */
@Service
public class SafetySysPermService {
    @Autowired
    private SafetySysPermDOMapper safetySysPermDOMapper;

    @Autowired
    private SafetySysPermDOBizMapper safetySysPermDOBizMapper;

    /**
     * 查询权限资源
     * @return
     */
    public List<Map> selectPermList() {
        return safetySysPermDOBizMapper.selectSysPermList();
    }

    /**
     * 权限资源新增
     * @param param
     */
    public void insertSysPerm(SysPermParam param) {
        //新增权限资源记录
        SafetySysPermDO safetySysPermDO = new SafetySysPermDO();
        BeanUtils.copyProperties(param, safetySysPermDO);
        safetySysPermDO.setLastTime(new Date());
        safetySysPermDO.setDeleteFalg("0");
        PreconditonsEx.checkArgument(safetySysPermDOMapper.insert(safetySysPermDO) == 1, ResponseEnum.FAIL);
        //同步对超级管理员角色增加关联记录

    }

    /**
     * 权限资源更新操作
     * @param param
     */
    public void updateSysPerm(SysPermParam param) {
        SafetySysPermDO safetySysPermDO = safetySysPermDOMapper.selectByPrimaryKey(param.getId());
        if(safetySysPermDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetySysPermDO);
        safetySysPermDO.setLastTime(new Date());
        PreconditonsEx.checkArgument(safetySysPermDOMapper.updateByPrimaryKey(safetySysPermDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void deleteSysPerm(Integer id) {
        //先检查是否有数据
        SafetySysPermDO safetySysPermDO = safetySysPermDOMapper.selectByPrimaryKey(id);
        if(safetySysPermDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetySysPermDO.setDeleteFalg("1");
        //验证是否有子资源
        SafetySysPermDOExample example = new SafetySysPermDOExample();
        SafetySysPermDOExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        criteria.andDeleteFalgEqualTo("0");
        List<SafetySysPermDO> sonPerms = safetySysPermDOMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(sonPerms)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"请先删除子资源");
        } else {
            //删除权限资源记录
            PreconditonsEx.checkArgument(safetySysPermDOMapper.updateByPrimaryKey(safetySysPermDO) == 1, ResponseEnum.FAIL);
            //删除角色权限关联表中记录
        }
    }

    /**
     * 根据员工号获取员工对应的权限信息
     * @param empNo 员工号
     * @return 返回权限信息
     */
    public List<SafetySysPermDO> selectListByEmpNoRole(String empNo) {
        return safetySysPermDOMapper.selectListByEmpNoRole(empNo);
    }
}
