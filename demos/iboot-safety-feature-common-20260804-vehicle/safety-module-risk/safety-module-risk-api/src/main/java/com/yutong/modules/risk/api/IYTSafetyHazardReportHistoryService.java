package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.entity.YTSafetyHazardReportHistory;
import com.yutong.modules.risk.vo.YTSafetyHazardReportHistoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IYTSafetyHazardReportHistoryService extends IService<YTSafetyHazardReportHistory> {

    List<YTSafetyHazardReportHistoryVO> listBYHazardReportId(Long reportId);
}
