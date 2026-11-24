package com.yutong.modules.bpmcallback.action;

import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.api.IYTSafetyHazardReportService;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.enums.HazardReportStatus;
import com.yutong.modules.user.api.ISafetySysUserService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ytsafety_hazard_report_delay_check")
public class HazardReportDelayCheckAction extends NodeAction {

    @Autowired
    private IYTSafetyHazardReportService safetyHazardReportService;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        log.error("隐患上报延期回调:{}",JsonUtils.toJsonString(event));
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        updateBusiness(processId);

    }

    private void updateBusiness(Long processId) {
        log.info("处理隐患上报延期回调, processId: {}", processId);
        YTSafetyHazardReport hazardReport = safetyHazardReportService.getById(processId);
        if (hazardReport == null) {
            log.warn("隐患上报流程不存在, processId: {}", processId);
            return;
        }
        hazardReport.setStatus(HazardReportStatus.DELAY_CHECK.getCode());

        safetyHazardReportService.updateById(hazardReport);
    }
}
