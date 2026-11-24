package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.api.IYTSafetyHazardDelayApplyService;
import com.yutong.modules.risk.entity.YTSafetyHazardDelayApply;
import com.yutong.modules.risk.mapper.YTSafetyHazardDelayApplyMapper;
import com.yutong.modules.risk.vo.YTSafetyHazardDelayApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YTSafetyHazardDelayApplyServiceImpl extends ServiceImpl<YTSafetyHazardDelayApplyMapper, YTSafetyHazardDelayApply> implements IYTSafetyHazardDelayApplyService {

    @Autowired
    private YTSafetyHazardDelayApplyMapper safetyHazardDelayApplyMapper;

    @Override
    public List<YTSafetyHazardDelayApplyVO> listBYHazardReportId(Long id) {
        return safetyHazardDelayApplyMapper.listBYHazardReportId(id);
    }

    @Override
    public YTSafetyHazardDelayApply getNewestByProcessId(Long processId) {
        return safetyHazardDelayApplyMapper.getNewestByProcessId(processId);
    }
}
