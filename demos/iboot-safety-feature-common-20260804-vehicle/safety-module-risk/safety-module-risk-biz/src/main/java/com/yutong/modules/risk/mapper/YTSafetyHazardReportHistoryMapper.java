package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YTSafetyHazardReportHistory;
import com.yutong.modules.risk.vo.YTSafetyHazardReportHistoryVO;

import java.util.List;

public interface YTSafetyHazardReportHistoryMapper extends BaseMapper<YTSafetyHazardReportHistory> {

    List<YTSafetyHazardReportHistoryVO> listBYHazardReportId(Long reportId);
}
