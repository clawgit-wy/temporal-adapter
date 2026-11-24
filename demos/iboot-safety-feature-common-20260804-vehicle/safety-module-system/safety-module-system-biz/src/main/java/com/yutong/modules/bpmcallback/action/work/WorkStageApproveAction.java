package com.yutong.modules.bpmcallback.action.work;

import com.yutong.modules.bpmcallback.action.NodeAction;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作业完成回调
 */
@Slf4j
@Component("work_stage_propose_callback")
public class WorkStageApproveAction extends NodeAction {
    @Autowired
    private IYtsafetyHomeworkStageService ytsafetyHomeworkStageService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        /**获取流程id*/
        String processId = event.getFormId().getFormInstanceId();
        Integer docStatus = event.getProcessData().getDocStatus();
        ytsafetyHomeworkStageService.copyProcessDetailsToStage(Long.valueOf(processId));
    }
}
