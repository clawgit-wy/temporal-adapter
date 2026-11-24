package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.dto.YTSafetyHazardDescriptionDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardDescription;
import com.yutong.modules.risk.vo.YTSafetyHazardDescriptionVO;
import org.springframework.stereotype.Service;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Service
public interface IYTSafetyHazardDescriptionService extends IService<YTSafetyHazardDescription> {

    /**
     * 根据HazardReportId清除对应的数据
     * @param hazardReportId
     */
    void clearBYHazardReportId(Long hazardReportId);

    /**
     * 添加隐患问题
     */
    void batchAdd(Long reportId, List<YTSafetyHazardDescriptionDTO> descriptionList);

    List<YTSafetyHazardDescriptionVO> listBYHazardReportId(Long id);

    List<YTSafetyHazardDescriptionVO> listByReportIds(@Param("reportIds") List<Long> reportIds);
}
