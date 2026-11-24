package com.yutong.modules.risk.controller;

import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.entity.YTSafetyValidateScenePost;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.handler.YTSafetyValidateScenePostHandler;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.web.ValidateScenePostAddParam;
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
 * 岗位验证-(现场岗位、模拟岗位)岗位验证上报及验证结果（移动端）
 * @author gwxiexj
 * @since 2024-06-24 14:08
 */
@Api(tags = "岗位验证-现场岗位、模拟岗位")
@RestController
@RequestMapping("/app/safety/validate/scenePost")
public class YTSafetyValidateScenePostAppController {

    @Autowired
    private YTSafetyValidateScenePostHandler safetyValidateScenePostHandler;

    @Autowired
    private YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @Autowired
    private IEipDoneService eipDoneService;

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "现场岗位、模拟岗位添加接口", tags = {"岗位验证-现场岗位、模拟岗位", "岗位验证、业务验证-app"})
    @PostMapping("/add")
    public Result<?> add(ValidateScenePostAddParam param) {
        param.setEventSource(1);
        //保存，获取新增记录的主键ID
        Long savedId = safetyValidateScenePostHandler.save(param);

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

        return Result.OK("添加成功！");
    }

    /**
     * 数据查看操作
     * @param id ID
     * @return
     */
    @ApiOperation(value = "现场岗位、模拟岗位查看详情接口", tags = {"岗位验证-现场岗位、模拟岗位", "岗位验证、业务验证-app"})
    @GetMapping("/view")
    public Result<YTSafetyValidateScenePost> view(@RequestParam("id") Long id) {
        return Result.OK(safetyValidateScenePostHandler.selectByParimaryKey(id));
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "现场岗位、模拟岗位-更新接口")
    @PostMapping(value = "/update")
    public Result<?> update(ValidateScenePostAddParam param) {
        safetyValidateScenePostHandler.update(param);
        return Result.OK("更新成功！");
    }
}
