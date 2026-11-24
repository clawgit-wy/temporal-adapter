package com.yutong.safety.core.service.hik;

import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikThresholdQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.hik.SafetyHikThresholdDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyHikThresholdService extends AbstractSafetyWebBaseService<SafetyHikThresholdDO, SafetyHikThresholdDOExample, SafetyHikThresholdQueryParam> {

    @Resource
    private SafetyHikThresholdDOMapper safetyHikThresholdDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyHikThresholdDO, SafetyHikThresholdDOExample> getMapper() {
        return safetyHikThresholdDOMapper;
    }

    @Override
    protected void formatExample(SafetyHikThresholdDOExample example, SafetyHikThresholdQueryParam param, boolean pageType) {
        SafetyHikThresholdDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getSceneCode())) {
            criteria.andSceneCodeEqualTo(param.getSceneCode());
        }
        if (StringUtils.isNotBlank(param.getSceneName())) {
            criteria.andSceneNameLike("%" + param.getSceneName() + "%");
        }
        if (StringUtils.isNotBlank(param.getUpdateName())) {
            criteria.andUpdateByEqualTo(param.getUpdateName());
        }
        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
    }

    @Override
    protected void batchDelete(SafetyHikThresholdDOExample example, List<String> idList) {
        SafetyHikThresholdDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            List<Integer> list = idList.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(list);
        }
    }

    @Override
    protected void isExist(SafetyHikThresholdDO data, SafetyHikThresholdDOExample example) {
        SafetyHikThresholdDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(data.getSceneName())) {
            criteria.andSceneNameEqualTo(data.getSceneName());
        }
        if (data.getId() != null) {
            criteria.andIdNotEqualTo(data.getId());
            criteria.andSceneCodeEqualTo(data.getSceneCode());
        }
    }

    /**
     * 根据指定的编码获取阈值信息
     *
     * @param thresholdEnum 阈值配置的编码，固定的
     * @return 返回具体的阈值
     */
    public String selectValueByCode(SafetyHikThresholdEnum thresholdEnum) {
        SafetyHikThresholdQueryParam example = new SafetyHikThresholdQueryParam();
        example.setSceneCode(thresholdEnum.getName());
        SafetyHikThresholdDO safetyHikThresholdDOS = this.selectOne(example);
        String value = "90";
        if (safetyHikThresholdDOS != null) {
            value = safetyHikThresholdDOS.getSceneValue();
        }
        return value;
    }
}
