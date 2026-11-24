package com.yutong.modules.bpmcallback.action;

import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.api.IYTSafetyHazardReportNodeRoleService;
import com.yutong.modules.risk.api.IYTSafetyHazardReportService;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.enums.HazardReportStatus;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ytsafety_hazard_report_verify2")
public class HazardReportVerifyTwoAction extends NodeAction {

    @Autowired
    private IYTSafetyHazardReportService safetyHazardReportService;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private ISafetyGroupInfoService safetyGroupInfoService;

    @Autowired
    private IYTSafetyHazardReportNodeRoleService safetyHazardReportNodeRoleService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        log.error("隐患上报验证2回调:{}", JsonUtils.toJsonString(event));
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        updateBusiness(processId, event);
    }

    private void updateBusiness(Long processId, BpmMethodProcessDTO event) {
        log.info("处理隐患上报验证2回调, processId: {}", processId);
        YTSafetyHazardReport hazardReport = safetyHazardReportService.getById(processId);
        if (hazardReport == null) {
            log.warn("隐患上报流程不存在, processId: {}", processId);
            return;
        }
        hazardReport.setStatus(HazardReportStatus.VERIFYING.getCode());

        safetyHazardReportService.updateById(hazardReport);
    }

}
