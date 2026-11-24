package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.dto.YTSafetyHazardResultDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import com.yutong.modules.risk.vo.YTSafetyHazardResultVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IYTSafetyHazardResultService extends IService<YTSafetyHazardResult> {

    /**
     * 根据HazardReportId清除对应的数据
     * @param hazardReportId
     */
    void clearBYHazardReportId(Long hazardReportId);

    void batchAdd(Long reportId, List<YTSafetyHazardResultDTO> resultList);

    List<YTSafetyHazardResultVO> listBYHazardReportId(Long id);

    List<YTSafetyHazardResultVO> listByReportIds(@Param("reportIds") List<Long> reportIds);

    YTSafetyHazardResult getNewestByProcessId(Long processId);
}
