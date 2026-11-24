package com.yutong.safety.core.service.entry;

import com.google.common.collect.Lists;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessLogDO;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryBlankProcessLogDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyEntryBlankProcessLogService {
    @Resource
    private SafetyEntryBlankProcessLogDOMapper safetyEntryBlankProcessLogDOMapper;

    public void batchSave(List<SafetyEntryBlankProcessLogDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<List<SafetyEntryBlankProcessLogDO>> partition = Lists.partition(list, 1000);
        partition.forEach(li -> safetyEntryBlankProcessLogDOMapper.batchInsert(li));
    }

    public void save(SafetyEntryBlankProcessLogDO data) {
        safetyEntryBlankProcessLogDOMapper.insert(data);
    }
}
