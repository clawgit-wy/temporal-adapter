package com.yutong.safety.core.service.hik;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikAbnormalEnterQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.hik.SafetyHikAbnormalEnterDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyHikAbnormalEnterService extends AbstractSafetyWebBaseService<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample, SafetyHikAbnormalEnterQueryParam> {

    @Resource
    private SafetyHikAbnormalEnterDOMapper safetyHikAbnormalEnterDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample> getMapper() {
        return safetyHikAbnormalEnterDOMapper;
    }

    @Override
    protected void formatExample(SafetyHikAbnormalEnterDOExample example, SafetyHikAbnormalEnterQueryParam param, boolean pageType) {
        SafetyHikAbnormalEnterDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        example.setOrderByClause(" event_time DESC");
        if (StringUtils.isNotBlank(param.getName())) {
            criteria.andNameEqualTo(param.getName());
        }
        if (StringUtils.isNotBlank(param.getEmpNo())) {
            criteria.andEmpNoEqualTo(param.getEmpNo());
        }
        if (StringUtils.isNotBlank(param.getFactoryCode())) {
            criteria.andFactoryCodeEqualTo(param.getFactoryCode());
        }
        if (StringUtils.isNotBlank(param.getBuildCode())) {
            criteria.andBuildCodeEqualTo(param.getBuildCode());
        }
        if (StringUtils.isNotBlank(param.getBeginEventTime()) && StringUtils.isNotBlank(param.getEndEventTime())) {
            criteria.andEventTimeBetween(param.getBeginEventTime(), param.getEndEventTime());
        }
        if (StringUtils.isNotBlank(param.getCameraIndexcode())) {
            criteria.andCameraIndexcodeEqualTo(param.getCameraIndexcode());
        }
        if (StringUtils.isNotBlank(param.getOrgCode())) {
            criteria.andOrgCodeEqualTo(param.getOrgCode());
        }

        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
    }

    @Override
    protected void batchDelete(SafetyHikAbnormalEnterDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyHikAbnormalEnterDO data, SafetyHikAbnormalEnterDOExample example) {

    }

    public void batchSave(List<SafetyHikAbnormalEnterDO> list) {
        safetyHikAbnormalEnterDOMapper.batchSave(list);
    }
}
