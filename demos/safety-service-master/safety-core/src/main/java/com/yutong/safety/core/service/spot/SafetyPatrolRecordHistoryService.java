package com.yutong.safety.core.service.spot;

import com.yutong.safety.common.param.web.spot.PatrolRecordHistoryListParam;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolRecordHistoryDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolRecordHistoryDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolRecordHistoryDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyPatrolRecordHistoryService {

    @Autowired
    private SafetyPatrolRecordHistoryDOMapper safetyPatrolRecordHistoryDOMapper;

    /**
     * 更新
     * @param recordHistoryDO
     * @return
     */
    public int insert(SafetyPatrolRecordHistoryDO recordHistoryDO){
        return safetyPatrolRecordHistoryDOMapper.insert(recordHistoryDO);
    }

    /**
     * 更新
     * @param recordHistoryDO
     * @return
     */
    public int update(SafetyPatrolRecordHistoryDO recordHistoryDO){
        return safetyPatrolRecordHistoryDOMapper.updateByPrimaryKeySelective(recordHistoryDO);
    }

    /**
     * 获取检索列表
     * @param listParam
     * @return
     */
    public List<SafetyPatrolRecordHistoryDO> getList(PatrolRecordHistoryListParam listParam){
        SafetyPatrolRecordHistoryDOExample example = new SafetyPatrolRecordHistoryDOExample();
        SafetyPatrolRecordHistoryDOExample.Criteria criteria = example.createCriteria();
        if(listParam.getProcessId() != null){
            criteria.andProcessIdEqualTo(listParam.getProcessId());
        }
        return safetyPatrolRecordHistoryDOMapper.selectByExample(example);
    }

    /**
     * 根据主键ID获取对象
     * @param id 主键ID
     * @return
     */
    public SafetyPatrolRecordHistoryDO getPrimaryKey(Integer id){
        return safetyPatrolRecordHistoryDOMapper.selectByPrimaryKey(id);
    }
}
