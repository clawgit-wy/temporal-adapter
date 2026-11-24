package com.yutong.safety.core.service.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikOperationGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikOperationGroupDOExample;
import com.yutong.safety.core.db.mapper.hik.SafetyHikOperationGroupDOMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyHikOperationGroupService {
    private static final Integer LIMIT = 10000;

    @Resource
    private SafetyHikOperationGroupDOMapper safetyHikOperationGroupDOMapper;

    public void batchSave(List<SafetyHikOperationGroupDO> groupDOList) {
        safetyHikOperationGroupDOMapper.batchSave(groupDOList);
    }

    /**
     * 获取全量数据
     */
    public List<SafetyHikOperationGroupDO> selectList() {
        SafetyHikOperationGroupDOExample example = new SafetyHikOperationGroupDOExample();
        example.setLimit(LIMIT);
        return safetyHikOperationGroupDOMapper.selectByExample(example);
    }

    public void deleteByIds(List<Integer> idx) {
        SafetyHikOperationGroupDOExample example = new SafetyHikOperationGroupDOExample();
        SafetyHikOperationGroupDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(idx);
        safetyHikOperationGroupDOMapper.deleteByExample(example);
    }
    public List<SafetyHikOperationGroupDO> getIncrementGroupInfo(String start, String end) {
        return safetyHikOperationGroupDOMapper.getIncrementGroupInfo(start, end);
    }
}
