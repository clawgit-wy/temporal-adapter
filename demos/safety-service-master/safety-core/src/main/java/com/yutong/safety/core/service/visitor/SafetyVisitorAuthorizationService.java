package com.yutong.safety.core.service.visitor;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDOExample;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationConfigQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.visitor.SafetyVisitorAuthorizationDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyVisitorAuthorizationService extends AbstractSafetyWebBaseService<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample, SafetyVisitorAuthorizationConfigQueryParam> {

    @Resource
    private SafetyVisitorAuthorizationDOMapper safetyVisitorAuthorizationDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample> getMapper() {
        return safetyVisitorAuthorizationDOMapper;
    }

    @Override
    protected void formatExample(SafetyVisitorAuthorizationDOExample example, SafetyVisitorAuthorizationConfigQueryParam param, boolean pageType) {
        SafetyVisitorAuthorizationDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getEmpInfo())) {
            criteria.andEmpNoOrEmpNameEqualTo(param.getEmpInfo());
        }
        if (StringUtils.isNotBlank(param.getEmpNo())) {
            criteria.andEmpNoEqualTo(param.getEmpNo());
        }
        if (param.getConfigType() != null) {
            criteria.andConfigTypeEqualTo(param.getConfigType());
        }

        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
    }

    @Override
    protected void batchDelete(SafetyVisitorAuthorizationDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyVisitorAuthorizationDO data, SafetyVisitorAuthorizationDOExample example) {
        SafetyVisitorAuthorizationDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (data.getConfigType() == 1) {
            criteria.andEmpNoEqualTo(data.getEmpNo());
        } else {
            criteria.andEmpOrgCodePathEqualTo(data.getEmpOrgCodePath());
            criteria.andConfigTypeEqualTo(data.getConfigType());
        }

        if (data.getId() != null) {
            criteria.andIdNotEqualTo(data.getId());
        }
    }
}
