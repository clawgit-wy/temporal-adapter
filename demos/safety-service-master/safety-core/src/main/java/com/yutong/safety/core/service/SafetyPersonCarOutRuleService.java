package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.PersonCarOutRuleListParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.SafetyPersonCarOutRuleDO;
import com.yutong.safety.core.db.entity.SafetyPersonCarOutRuleDOExample;
import com.yutong.safety.core.db.mapper.SafetyPersonCarOutRuleDOBizMapper;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-10-15 10:33
 */
@Service
public class SafetyPersonCarOutRuleService {
    @Autowired
    private SafetyPersonCarOutRuleDOBizMapper safetyPersonCarOutRuleDOBizMapper;

    /**
     * 新增方法
     * @param safetyPersonCarOutRuleDO
     */
    public void add(SafetyPersonCarOutRuleDO safetyPersonCarOutRuleDO) {
        PreconditonsEx.checkArgument(safetyPersonCarOutRuleDOBizMapper.insert(safetyPersonCarOutRuleDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 数据更新
     * @param safetyPersonCarOutRuleDO
     */
    public void update(SafetyPersonCarOutRuleDO safetyPersonCarOutRuleDO) {
        PreconditonsEx.checkArgument(safetyPersonCarOutRuleDOBizMapper.updateByPrimaryKey(safetyPersonCarOutRuleDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 根据参数数据获取对象数据
     * @param id
     * @param employeeId
     * @param carNo
     * @return
     */
    public SafetyPersonCarOutRuleDO getInfo(Integer id, Integer employeeId, String carNo) {
        if (Objects.nonNull(id)) {
            return safetyPersonCarOutRuleDOBizMapper.selectByPrimaryKey(id);
        }
        SafetyPersonCarOutRuleDOExample example = new SafetyPersonCarOutRuleDOExample();
        SafetyPersonCarOutRuleDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo(false);
        if (Objects.nonNull(employeeId)) {
            criteria.andEmployeeIdEqualTo(employeeId);
        }
        if (StringUtils.isNotEmpty(carNo)) {
            criteria.andCarNoEqualTo(carNo);
        }
        List<SafetyPersonCarOutRuleDO> list = safetyPersonCarOutRuleDOBizMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public List<SafetyPersonCarOutRuleDTO> pageList(PersonCarOutRuleListParam param) {
        return safetyPersonCarOutRuleDOBizMapper.pageList(param.getCheckFlag(), param.getEmpNo(), param.getEmpName(), param.getPositionName(), param.getOffset(), param.getPageSize());
    }

    /**
     * 分页总条数
     * @param param
     * @return
     */
    public long pageListCount(PersonCarOutRuleListParam param) {
        return safetyPersonCarOutRuleDOBizMapper.pageListCount(param.getCheckFlag(), param.getEmpNo(), param.getEmpName(), param.getPositionName());
    }

}
