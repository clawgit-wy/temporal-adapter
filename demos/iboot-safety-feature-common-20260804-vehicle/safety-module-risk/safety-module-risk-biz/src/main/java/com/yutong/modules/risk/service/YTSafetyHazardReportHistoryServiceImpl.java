package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.api.IYTSafetyHazardReportHistoryService;
import com.yutong.modules.risk.entity.YTSafetyHazardReportHistory;
import com.yutong.modules.risk.mapper.YTSafetyHazardReportHistoryMapper;
import com.yutong.modules.risk.vo.YTSafetyHazardReportHistoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class YTSafetyHazardReportHistoryServiceImpl extends ServiceImpl<YTSafetyHazardReportHistoryMapper, YTSafetyHazardReportHistory> implements IYTSafetyHazardReportHistoryService {

    @Autowired
    private  YTSafetyHazardReportHistoryMapper safetyHazardReportHistoryMapper;
    @Override
    public List<YTSafetyHazardReportHistoryVO> listBYHazardReportId(Long reportId) {
        return safetyHazardReportHistoryMapper.listBYHazardReportId(reportId);
    }
}
