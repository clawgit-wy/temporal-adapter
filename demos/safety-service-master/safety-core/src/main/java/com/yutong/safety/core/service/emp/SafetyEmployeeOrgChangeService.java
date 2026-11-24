package com.yutong.safety.core.service.emp;

import com.yutong.safety.core.db.entity.emp.SafetyEmployeeOrgChangeDO;
import com.yutong.safety.core.db.entity.emp.SafetyEmployeeOrgChangeDOExample;
import com.yutong.safety.core.db.entity.emp.param.SafetyEmployeeOrgChangeQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.emp.SafetyEmployeeOrgChangeDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyEmployeeOrgChangeService extends AbstractSafetyWebBaseService<SafetyEmployeeOrgChangeDO, SafetyEmployeeOrgChangeDOExample, SafetyEmployeeOrgChangeQueryParam> {

    @Resource
    private SafetyEmployeeOrgChangeDOMapper safetyEmployeeOrgChangeDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEmployeeOrgChangeDO, SafetyEmployeeOrgChangeDOExample> getMapper() {
        return safetyEmployeeOrgChangeDOMapper;
    }

    @Override
    protected void formatExample(SafetyEmployeeOrgChangeDOExample example, SafetyEmployeeOrgChangeQueryParam param, boolean pageType) {
        SafetyEmployeeOrgChangeDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getEmpNo())) {
            criteria.andEmpNoEqualTo(param.getEmpNo());
        }
        if (StringUtils.isNotBlank(param.getOrgCode())) {
            criteria.andOrgCodeEqualTo(param.getOrgCode());
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        if (pageType) {
            example.setLimit(param.getLimit());
        }
    }

    @Override
    protected void batchDelete(SafetyEmployeeOrgChangeDOExample example, List<String> idList) {
        SafetyEmployeeOrgChangeDOExample.Criteria criteria = example.createCriteria();
    }

    @Override
    protected void isExist(SafetyEmployeeOrgChangeDO data, SafetyEmployeeOrgChangeDOExample example) {

    }
}
