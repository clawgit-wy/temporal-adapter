package com.yutong.modules.risk.controller;

import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.entity.YTSafetyValidateEnvironment;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.handler.YTSafetyValidateEnvironmentHandler;
import com.yutong.modules.risk.service.YTSafetyValidateEnvironmentService;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.web.ValidateEnvironmentAddParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author gwxiexj
 * @time 2024-06-12 10:34
 */
@Api(tags = "业务验证-货车环保标准app")
@RestController
@RequestMapping("/app/safety/validate/environment")
public class YTSafetyValidateEnvironAppController {

    @Autowired
    private YTSafetyValidateEnvironmentHandler safetyValidateEnvironmentHandler;

    @Autowired
    private YTSafetyValidateEnvironmentService ytSafetyValidateEnvironmentService;

    @Autowired
    private YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @Autowired
    private IEipDoneService eipDoneService;

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "货车环保标准添加接口", tags = {"业务验证-货车环保标准app", "岗位验证、业务验证-app"} )
    @PostMapping("/add")
    public Result<?> add(ValidateEnvironmentAddParam param) {
        if (param.getTaskId() != null) {
            YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(param.getTaskId());
            if (taskExecution.getTaskCompleteTime() != null) {
                throw new IllegalArgumentException("任务已完成");
            }
        }

        param.setEventSource(1);
        param.setBusinessType("货车环保标准抽查");
        //保存，获取新增记录的主键ID
        Long savedId = safetyValidateEnvironmentHandler.save(param);

        // 更新任务状态为已完成
        if (param.getTaskId() != null) {
            YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(param.getTaskId());
            if (taskExecution != null && taskExecution.getTaskCompleteTime() == null) {
                Date endDate = new Date();
                taskExecution.setTaskStartTime(endDate);
                taskExecution.setTaskCompleteTime(endDate);

                LocalDate endDateOnly = endDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate deadlineOnly = taskExecution.getEndDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if(endDateOnly.isAfter(deadlineOnly)){
                    taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED);
                }else{
                    taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.COMPLETED);
                }
                taskExecution.setReportPostId(savedId);
                ytSafetyTaskExecutionService.updateById(taskExecution);
            }
        }
        //更新待办为已办
        // 消除代办 - 20241024发版
        if(!ObjectUtils.isEmpty(param.getTaskId())){
            String processId = String.valueOf(param.getTaskId());
            try{
                // 消除代办
                EipSetTodoDoneArg eipSetTodoDoneArg = new EipSetTodoDoneArg();
                eipSetTodoDoneArg.setAppName("安消管理平台");
                eipSetTodoDoneArg.setModelId(processId);
                eipSetTodoDoneArg.setType(1);
                eipSetTodoDoneArg.setModelName("安消管理平台");
                eipDoneService.eipToDoDone(eipSetTodoDoneArg);
            }catch (Exception e){
                YTLogUtils.error("【消除代办异常】",e);
            }
        }

        return Result.OK();
    }

    @ApiOperation("基本信息查询")
    @RequestMapping(value = "/getBaseInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Result<SafetyPersonCarOutRuleDTO> getBaseInfo(SafetyPersonCarOutRuleDTO en){
        return Result.ok(ytSafetyValidateEnvironmentService.getBaseInfo(en));
    }


    /**
     * 数据查看操作
     * @param id ID
     * @return
     */
    @ApiOperation(value = "业务验证-货车环保标准查看详情接口", tags = {"业务验证-货车环保标准app", "岗位验证、业务验证-app"} )
    @GetMapping("/view")
    public Result<YTSafetyValidateEnvironment> view(@RequestParam("id") Long id) {
        return Result.OK(safetyValidateEnvironmentHandler.selectByPrimaryKey(id));
    }
}
