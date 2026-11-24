package com.yutong.safety.core.service.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryBlankProcessDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyEntryBlankProcessService extends AbstractSafetyWebBaseService<SafetyEntryBlankProcessDO, SafetyEntryBlankProcessDOExample, SafetyEntryBlankProcessQueryParams> {

    @Resource
    private SafetyEntryBlankProcessDOMapper safetyEntryBlankProcessDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryBlankProcessDO, SafetyEntryBlankProcessDOExample> getMapper() {
        return safetyEntryBlankProcessDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryBlankProcessDOExample example, SafetyEntryBlankProcessQueryParams param, boolean pageType) {
        SafetyEntryBlankProcessDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getBpmFlowId())) {
            criteria.andBpmFlowIdEqualTo(param.getBpmFlowId());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryBlankProcessDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyEntryBlankProcessDO data, SafetyEntryBlankProcessDOExample example) {

    }
}
