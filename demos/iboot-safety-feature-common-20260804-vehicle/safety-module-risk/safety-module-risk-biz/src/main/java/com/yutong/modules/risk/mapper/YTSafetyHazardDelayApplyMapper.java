package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YTSafetyHazardDelayApply;
import com.yutong.modules.risk.vo.YTSafetyHazardDelayApplyVO;

import java.util.List;

public interface YTSafetyHazardDelayApplyMapper extends BaseMapper<YTSafetyHazardDelayApply> {

    List<YTSafetyHazardDelayApplyVO> listBYHazardReportId(Long id);

    YTSafetyHazardDelayApply getNewestByProcessId(Long processId);
}
