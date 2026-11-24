package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.SpotCheckRecordListParam;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.dto.SpotCheckRecordDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetySpotCheckRecordDOBizMapper {

    List<SpotCheckRecordDTO> selectRecordHistory(SpotCheckRecordListParam param);

    long selectRecordHistoryCount(SpotCheckRecordListParam param);

    int updatePlanEmpBatch(SpotCheckRecordListParam param);
    int updatePlanEmpBatchNew(SpotCheckRecordListParam param);

    //更新record表的taskID
    void batchUpdateTaskId(@Param("taskIdList") List<Map<String,Integer>> taskIdList);

    void batchUpdateRecord(@Param("taskIdList") List<SafetySpotCheckRecordDO> taskIdList);
}