package com.yutong.modules.bpmcallback.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.entity.YTSafetyTaskInfo;
import com.yutong.modules.risk.enums.CustomConstant;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component("ytsafety_special_task_finish")
public class SpecialTaskFinishAction extends NodeAction{

    @Autowired
    YTSafetyTaskExecutionService ytSafetyTaskExecutionService;
    @Override
    public void execute(BpmMethodProcessDTO event) {

        log.info("==============专项任务回调来了=============:{}",event);
        String formInstanceId = event.getFormId().getFormInstanceId();
        YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(formInstanceId);
        Date completeTime = new Date();
        taskExecution.setTaskCompleteTime(completeTime);

        LocalDate endLocalDate = taskExecution.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate completeLocalDate = completeTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        boolean isCompleteOverdue = completeLocalDate.isAfter(endLocalDate);
        taskExecution.setTaskStatus(isCompleteOverdue
                ? CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED
                : CustomConstant.TaskExecutionStatus.COMPLETED);

        ytSafetyTaskExecutionService.updateById(taskExecution);
        log.error(JsonUtils.toJsonString(event));
    }
}
