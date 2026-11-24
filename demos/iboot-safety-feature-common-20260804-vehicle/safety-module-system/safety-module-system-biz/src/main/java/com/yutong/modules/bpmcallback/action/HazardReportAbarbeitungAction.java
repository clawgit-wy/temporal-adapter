package com.yutong.modules.bpmcallback.action;


import com.yutong.modules.common.constant.enums.ProcessDocStatusEnum;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.api.IYTSafetyHazardDelayApplyService;
import com.yutong.modules.risk.api.IYTSafetyHazardReportService;
import com.yutong.modules.risk.api.IYTSafetyHazardResultService;
import com.yutong.modules.risk.entity.YTSafetyHazardDelayApply;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import com.yutong.modules.risk.enums.HazardReportStatus;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ytsafety_hazard_report_abarbeitung")
public class HazardReportAbarbeitungAction extends NodeAction{

    @Autowired
    private IYTSafetyHazardReportService safetyHazardReportService;

    @Autowired
    private IYTSafetyHazardResultService safetyHazardResultService;

    @Autowired
    private IYTSafetyHazardDelayApplyService safetyHazardDelayApplyService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        log.error("隐患上报整改回调:{}", JsonUtils.toJsonString(event));
        System.out.println("隐患上报整改回调:"+JsonUtils.toJsonString(event));
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        updateBusiness(processId,event);
    }

    private void updateBusiness(Long processId,BpmMethodProcessDTO event) {
        log.info("处理隐患上报整改回调, processId: {}", processId);
        YTSafetyHazardReport hazardReport = safetyHazardReportService.getById(processId);
        if (hazardReport == null) {
            log.warn("隐患上报流程不存在, processId: {}", processId);
            return;
        }

//        if (ProcessDocStatusEnum.circulation.getCode() == event.getProcessData().getDocStatus()){
//            //处理整改审核/延期申请审核
//            if (hazardReport.getSubmitType() == 1){
//                YTSafetyHazardResult hazardResult = null;
//                if (hazardReport.getCurrentManageId() != null){
//                    hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
//                }else {
//                    hazardResult = safetyHazardResultService.getNewestByProcessId(processId);
//                }
//                if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
//                    hazardResult.setCheckSign(1);
//                    hazardResult.setStatus(2);
//                    safetyHazardResultService.updateById(hazardResult);
//                }
//            }else if (hazardReport.getSubmitType() == 2){
//                YTSafetyHazardDelayApply hazardDelayApply = null;
//                if (hazardReport.getCurrentManageId() != null){
//                    hazardDelayApply = safetyHazardDelayApplyService.getById(hazardReport.getCurrentManageId());
//                }else {
//                    hazardDelayApply = safetyHazardDelayApplyService.getNewestByProcessId(processId);
//                }
//                if (hazardDelayApply != null && (hazardDelayApply.getCheckSign() == null || hazardDelayApply.getCheckSign() != 1)){
//                    hazardDelayApply.setCheckSign(1);
//                    hazardDelayApply.setStatus(2);
//                    safetyHazardDelayApplyService.updateById(hazardDelayApply);
//                    hazardReport.setRectifyDelayTime(hazardDelayApply.getApplyDeadline());
//                }
//            }
//        }else {
//            //处理整改审核/延期申请审核
//            if (hazardReport.getSubmitType() == 1){
//                YTSafetyHazardResult hazardResult = null;
//                if (hazardReport.getCurrentManageId() != null){
//                    hazardResult = safetyHazardResultService.getById(hazardReport.getCurrentManageId());
//                }else {
//                    hazardResult = safetyHazardResultService.getNewestByProcessId(processId);
//                }
//                if (hazardResult != null && (hazardResult.getCheckSign() == null || hazardResult.getCheckSign() != 1)){
//                    hazardResult.setCheckSign(1);
//                    hazardResult.setStatus(3);
//                    safetyHazardResultService.updateById(hazardResult);
//                }
//            }else if (hazardReport.getSubmitType() == 2){
//                YTSafetyHazardDelayApply hazardDelayApply = null;
//                if (hazardReport.getCurrentManageId() != null){
//                    hazardDelayApply = safetyHazardDelayApplyService.getById(hazardReport.getCurrentManageId());
//                }else {
//                    hazardDelayApply = safetyHazardDelayApplyService.getNewestByProcessId(processId);
//                }
//                if (hazardDelayApply != null && (hazardDelayApply.getCheckSign() == null || hazardDelayApply.getCheckSign() != 1)){
//                    hazardDelayApply.setCheckSign(1);
//                    hazardDelayApply.setStatus(3);
//                    safetyHazardDelayApplyService.updateById(hazardDelayApply);
//                }
//            }
//        }

        hazardReport.setStatus(HazardReportStatus.RECTIFYING.getCode());

        safetyHazardReportService.updateById(hazardReport);
    }
}
