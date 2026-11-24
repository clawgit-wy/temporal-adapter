package com.yutong.safety.core.service.spot;

import com.yutong.safety.common.param.web.spot.PatrolDealHistoryListParam;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolDealHistoryDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolDealHistoryDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolDealHistoryDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 治安巡查-处理记录信息
 * @author gwxiexj
 * @since 2024-06-07 16:41
 */
@Service
public class SafetyPatrolDealHistoryService {

    @Autowired
    private SafetyPatrolDealHistoryDOMapper safetyPatrolDealHistoryDOMapper;

    public int save(SafetyPatrolDealHistoryDO dealHistoryDO) {
        return safetyPatrolDealHistoryDOMapper.insert(dealHistoryDO);
    }

    public int update(SafetyPatrolDealHistoryDO dealHistoryDO) {
        return safetyPatrolDealHistoryDOMapper.updateByPrimaryKeySelective(dealHistoryDO);
    }

    public List<SafetyPatrolDealHistoryDO> getList(PatrolDealHistoryListParam param){
        SafetyPatrolDealHistoryDOExample example = new SafetyPatrolDealHistoryDOExample();
        SafetyPatrolDealHistoryDOExample.Criteria criteria = example.createCriteria();
        if(param.getProcessId() != null){
            criteria.andProcessIdEqualTo(param.getProcessId());
        }
        return safetyPatrolDealHistoryDOMapper.selectByExample(example);
    }
}
