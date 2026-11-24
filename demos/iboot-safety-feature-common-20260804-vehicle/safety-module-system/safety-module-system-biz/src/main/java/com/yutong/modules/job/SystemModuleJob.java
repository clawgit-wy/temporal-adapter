package com.yutong.modules.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataService;
import com.yutong.modules.user.api.IYtsafetySysResponsibleService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class SystemModuleJob {

    @Autowired
    private IYtsafetySysResponsibleService sysResponsibleService;

    @Autowired
    private YTSafetyTaskService ytSafetyTaskService;

    @Autowired
    private IYtsafetyRiskDataService safetyRiskDataService;

    @XxlJob("executeResponsibleDateJob")
    //@Scheduled(cron = "0 */5 * * * ?") // 核心：每天6点
    //@Scheduled(cron = "0 * * * * ?")
    public ReturnT<String> executeResponsibleDateJob(String param) {
        try {
            // 业务逻辑...
            Date currentDate = DateUtils.parseDate(DateUtils.formatDate(),"yyyy-MM-dd") ;
            sysResponsibleService.updateSysResponsibleStatus(currentDate);
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }

    @XxlJob("executeRiskDataUpateJob")
    //@Scheduled(cron = "0 */5 * * * ?") // 核心：每天6点
    //@Scheduled(cron = "0 * * * * ?")
    public ReturnT<String> executeRiskDataUpateJob(String param) {
        try {
            // 业务逻辑...
            Date currentDate = DateUtils.parseDate(DateUtils.formatDate(),"yyyy-MM-dd") ;
            safetyRiskDataService.pushOverdueData(currentDate);
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }

//    更新所有待执行任务为执行中
    @XxlJob("executeUpdateTaskStatus")
    public ReturnT<String> executeUpdateTaskStatus(String param) {
        try {
            ytSafetyTaskService.updateTaskStatusPening();
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }

    //    更新所有今天结束但未完成的任务状态为超期未完成
    @XxlJob("executeTaskExecutionStatus")
    public ReturnT<String> executeTaskExecutionStatus(String param) {
        try {
            ytSafetyTaskService.updateUserTaskExecutionStatus();
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }

//    更新所有结束日期小于等于当前时间的任务状态为已过期
    @XxlJob("executeTaskStatusExpired")
    public ReturnT<String> executeTaskStatusExpired(String param) {
        try {
            ytSafetyTaskService.updateTaskStatusExpired();
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }

    //    任务下发
    @XxlJob("executeIssueTasks")
    public ReturnT<String> executeIssueTasks(String param) {
        try {
            ytSafetyTaskService.issueTasks(null);
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
        } catch (Exception e) {
            log.error("任务执行失败", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "执行失败：" + e.getMessage());
        }
    }




}
