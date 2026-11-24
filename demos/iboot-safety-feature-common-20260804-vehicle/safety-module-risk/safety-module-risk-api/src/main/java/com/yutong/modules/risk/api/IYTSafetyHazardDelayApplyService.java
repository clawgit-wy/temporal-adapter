package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.entity.YTSafetyHazardDelayApply;
import com.yutong.modules.risk.vo.YTSafetyHazardDelayApplyVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IYTSafetyHazardDelayApplyService extends IService<YTSafetyHazardDelayApply> {

    List<YTSafetyHazardDelayApplyVO> listBYHazardReportId(Long id);

    YTSafetyHazardDelayApply getNewestByProcessId(Long processId);
}
