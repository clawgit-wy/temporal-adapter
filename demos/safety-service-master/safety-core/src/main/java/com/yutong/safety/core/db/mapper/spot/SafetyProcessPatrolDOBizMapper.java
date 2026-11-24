package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.PatrolNodeUpdateParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordListParam;
import com.yutong.safety.common.vo.web.spot.PatrolRecordVO;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.spot.SafetyProcessPatrolDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyProcessPatrolDOBizMapper extends SafetyProcessPatrolDOMapper{

    List<PatrolRecordVO> getPatrolRecordList(PatrolRecordListParam param);

    long getPatrolRecordCount(PatrolRecordListParam param);

    int modifyCurDealHandler(PatrolNodeUpdateParam param);

    int modifyFlowId(PatrolNodeUpdateParam param);

    SafetyProcessPatrolDO selectRecordsById(Integer id);

    int batchModifyCurDeal(@Param("list") List<BpmCurNodeVO> curNodeVOList);
}