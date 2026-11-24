package com.yutong.modules.bpmcallback.action;

import com.yutong.modules.common.constant.enums.ProcessDocStatusEnum;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.api.IYTSafetyHazardReportService;
import com.yutong.modules.risk.api.IYTSafetyHazardResultService;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import com.yutong.modules.risk.enums.HazardReportStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component("ytsafety_hazard_report_publish")
public class HazardReportPublishAction extends NodeAction{

    @Autowired
    private IYTSafetyHazardReportService safetyHazardReportService;

    @Autowired
    private IYTSafetyHazardResultService safetyHazardResultService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        if (StringUtils.equals(event.getProcessData().getNodeFactId(), "N3")) {
            updateBusiness(processId,event);
        }
    }

    private void updateBusiness(Long processId,BpmMethodProcessDTO event) {
        log.info("处理隐患上报结束回调, processId: {}", processId);
        YTSafetyHazardReport hazardReport = safetyHazardReportService.getById(processId);
        if (hazardReport == null) {
            log.warn("隐患上报流程不存在, processId: {}", processId);
            return;
        }
        //处理整改审核/延期申请审核
        YTSafetyHazardResult hazardResult = null;
        if (hazardReport.getCurrentManageId() != null){
            hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
        }else {
            hazardResult = safetyHazardResultService.getNewestByProcessId(processId);
        }
        if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
            hazardResult.setCheckSign(1);
            hazardResult.setStatus(2);
            safetyHazardResultService.updateById(hazardResult);
        }

        hazardReport.setStatus(HazardReportStatus.DONE.getCode());
        hazardReport.setRectifyCompleteTime(new Date());

        // 判断是否逾期完成
        Date originalDeadline = hazardReport.getRectifyDeadline();  // 原始截止期限
        Date delayTime = hazardReport.getRectifyDelayTime();
        Date completeTime = hazardReport.getRectifyCompleteTime();

        if (delayTime != null) {
            originalDeadline = delayTime;  // 有延期时间，用延期时间替代
        }

        if (completeTime != null && originalDeadline != null && completeTime.after(originalDeadline)) {
            hazardReport.setStatus(HazardReportStatus.OVERDUE.getCode());
        }
        safetyHazardReportService.updateById(hazardReport);
    }
}
