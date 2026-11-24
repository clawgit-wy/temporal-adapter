package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.param.web.spot.PatrolDealHistoryListParam;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolDealHistoryDO;
import com.yutong.safety.core.service.spot.SafetyPatrolDealHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 治安巡查-历史处理记录
 * @author gwxiexj
 * @since 2024-09-18 16:48
 */
@Component
public class SafetyPatrolDealHistoryHandler {

    @Autowired
    private SafetyPatrolDealHistoryService safetyPatrolDealHistoryService;


    public List<SafetyPatrolDealHistoryDO> getDealHistoryDO(int processId){
        PatrolDealHistoryListParam param = new PatrolDealHistoryListParam();
        param.setProcessId(processId);
        return safetyPatrolDealHistoryService.getList(param);
    }
}
