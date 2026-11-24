package com.yutong.safety.core.service.hik;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikGroupQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.hik.SafetyHikGroupDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyHikGroupService extends AbstractSafetyWebBaseService<SafetyHikGroupDO, SafetyHikGroupDOExample, SafetyHikGroupQueryParam> {

    @Resource
    private SafetyHikGroupDOMapper safetyHikGroupDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyHikGroupDO, SafetyHikGroupDOExample> getMapper() {
        return safetyHikGroupDOMapper;
    }

    @Override
    protected void formatExample(SafetyHikGroupDOExample example, SafetyHikGroupQueryParam param, boolean pageType) {
        SafetyHikGroupDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getIndexCode())) {
            criteria.andIndexCodeEqualTo(param.getIndexCode());
        }
        if (StringUtils.isNotBlank(param.getName())) {
            criteria.andNameLike("%" + param.getName() + "%");
        }
        if (StringUtils.isNotBlank(param.getBuildCode())) {
            criteria.andBuildCodeLike("%" + param.getBuildCode() + "%");
        }
        if (param.getStatus() != null) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        if (CollectionUtils.isNotEmpty(param.getNames())) {
            criteria.andNameIn(param.getNames());
        }
        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
    }

    @Override
    protected void batchDelete(SafetyHikGroupDOExample example, List<String> idList) {
        SafetyHikGroupDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            List<Integer> list = idList.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(list);
        }
    }

    @Override
    protected void isExist(SafetyHikGroupDO data, SafetyHikGroupDOExample example) {
        SafetyHikGroupDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(data.getIndexCode())) {
            criteria.andIndexCodeEqualTo(data.getIndexCode());
        }
        criteria.andNameEqualTo(data.getName());
        if (data.getId() != null) {
            criteria.andIdNotEqualTo(data.getId());
        }
    }

    public void batchSave(List<SafetyHikGroupDO> list) {
        safetyHikGroupDOMapper.batchSave(list);
    }

    public void batchUpdate(List<SafetyHikGroupDO> list) {
        safetyHikGroupDOMapper.batchUpdate(list);
    }
}
