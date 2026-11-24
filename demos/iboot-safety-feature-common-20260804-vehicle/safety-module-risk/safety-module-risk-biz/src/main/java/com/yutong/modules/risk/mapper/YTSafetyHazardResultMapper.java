package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import com.yutong.modules.risk.vo.YTSafetyHazardResultVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YTSafetyHazardResultMapper extends BaseMapper<YTSafetyHazardResult> {

    List<YTSafetyHazardResultVO> listBYHazardReportId(Long id);

    List<YTSafetyHazardResultVO> listByReportIds(@Param("reportIds") List<Long> reportIds);

    YTSafetyHazardResult getNewestByProcessId(@Param("processId") Long processId);
}
