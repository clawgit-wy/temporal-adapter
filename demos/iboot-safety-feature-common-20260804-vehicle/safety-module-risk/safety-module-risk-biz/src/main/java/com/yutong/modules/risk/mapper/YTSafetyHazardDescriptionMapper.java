package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YTSafetyHazardDescription;
import com.yutong.modules.risk.vo.YTSafetyHazardDescriptionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YTSafetyHazardDescriptionMapper extends BaseMapper<YTSafetyHazardDescription> {

    List<YTSafetyHazardDescriptionVO> listBYHazardReportId(Long id);

    List<YTSafetyHazardDescriptionVO> listByReportIds(@Param("reportIds") List<Long> reportIds);
}
