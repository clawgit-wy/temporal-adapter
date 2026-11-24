package com.yutong.safety.core.service.hik;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.hik.SafetyHikMonitorDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyHikMonitorService extends AbstractSafetyWebBaseService<SafetyHikMonitorDO, SafetyHikMonitorDOExample, SafetyHikMonitorQueryParam> {
    @Resource
    private SafetyHikMonitorDOMapper safetyHikMonitorDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyHikMonitorDO, SafetyHikMonitorDOExample> getMapper() {
        return safetyHikMonitorDOMapper;
    }

    @Override
    protected void formatExample(SafetyHikMonitorDOExample example, SafetyHikMonitorQueryParam param, boolean pageType) {
        SafetyHikMonitorDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getIndexCode())) {
            criteria.andIndexCodeEqualTo(param.getIndexCode());
        }
        if (StringUtils.isNotBlank(param.getName())) {
            criteria.andNameLike("%" + param.getName() + "%");
        }
        if (StringUtils.isNotBlank(param.getFactoryCode())) {
            criteria.andFactoryCodeEqualTo(param.getFactoryCode());
        }
        if (StringUtils.isNotBlank(param.getBuildCode())) {
            criteria.andBuildCodeEqualTo(param.getBuildCode());
        }
        if (StringUtils.isNotBlank(param.getUpdateBy())) {
            criteria.andUpdateByEqualTo(param.getUpdateBy());
        }

        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
    }

    @Override
    protected void batchDelete(SafetyHikMonitorDOExample example, List<String> idList) {
        SafetyHikMonitorDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            List<Integer> list = idList.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(list);
        }
    }

    @Override
    protected void isExist(SafetyHikMonitorDO data, SafetyHikMonitorDOExample example) {
        SafetyHikMonitorDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        criteria.andIndexCodeEqualTo(data.getIndexCode());
        if (data.getId() != null) {
            criteria.andIdNotEqualTo(data.getId());
        }
    }

    public void batchSave(List<SafetyHikMonitorDO> hikList) {
        safetyHikMonitorDOMapper.batchSave(hikList);
    }

    public void batchUpdate(List<SafetyHikMonitorDO> updateList) {
        safetyHikMonitorDOMapper.batchUpdate(updateList);
    }
}
