package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.vo.web.spot.PatrolTaskReportVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyPatrolTaskDOBizMapper {

    /**
     * 批量更新周期任务
     * @param list 入参
     * @return 返回值
     */
    int batchUpdateTask(@Param("list") List<SafetySpotCheckTaskDO> list);

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    List<PatrolTaskReportVO> selectPatrolTaskReport(PatrolTaskListParam param);

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    List<PatrolTaskReportVO> selectPatrolTODODetail(PatrolTaskListParam param);

    List<PatrolTaskReportVO> selectDetailSummary(PatrolTaskListParam param);
}