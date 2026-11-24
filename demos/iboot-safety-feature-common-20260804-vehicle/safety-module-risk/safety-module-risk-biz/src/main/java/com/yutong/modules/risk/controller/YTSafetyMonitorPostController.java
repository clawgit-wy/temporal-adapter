package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.dto.SafetyMonitorPostProcessDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.handler.YTSafetyMonitorPostHandler;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.vo.YTSafetyMonitorPostExportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 监控岗位上报相关controller
 * @author lukunpeng
 * @time 2024-4-30
 */
@Api(tags = "岗位验证、业务验证")
@RestController
@RequestMapping("/web/safety/monitor/post")
public class YTSafetyMonitorPostController {

    @Autowired
    private YTSafetyMonitorPostHandler safetyMonitorPostHandler;

    @Autowired
    private YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @Autowired
    private IEipDoneService eipDoneService;


    /**
     * 保存数据创建流程
     * @return
     */
    @AutoLog(value = "监控岗位-保存数据创建流程", operateType = 2)
    @ApiOperation(value = "监控岗位-保存数据创建流程")
    @PostMapping("/saveAndCreateProcess")
    public Result<?> saveAndCreateProcess(@RequestBody SafetyMonitorPostProcessDTO processDTO) {
        // 1. 生成业务主键
        long id = IdWorker.getId();
        processDTO.setId(id);
        String s = safetyMonitorPostHandler.saveAndCerateProcess(processDTO);
        // 更新任务状态为已完成
        if (processDTO.getTaskId() != null) {
            YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(processDTO.getTaskId());
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
                taskExecution.setReportPostId(id);
                ytSafetyTaskExecutionService.updateById(taskExecution);
            }
        }
        //更新待办为已办
        // 消除代办 - 20241024发版
        if(!ObjectUtils.isEmpty(processDTO.getTaskId())){
            String processId = String.valueOf(processDTO.getTaskId());
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
        return Result.OK("流程创建成功", s);
    }

    /**
     * 数据新增或者更新操作
     * @param
     * @return
     */
    @AutoLog(value = "监控岗位-更新数据审批流程", operateType = 3)
    @ApiOperation(value = "监控岗位-更新数据审批流程")
    @PostMapping("/updateAndAudit")
    public Result<?> updateAndAudit(@RequestBody SafetyMonitorPostProcessDTO processDTO) {
        return Result.OK(safetyMonitorPostHandler.updateAndAudit(processDTO));
    }

    /**
     * 获取当前节点ID
     * @return
     */
    @AutoLog(value = "监控岗位-获取当前节点ID", operateType = 1)
    @ApiOperation(value = "监控岗位-获取当前节点ID")
    @GetMapping("/getNowNode")
    public Result<?> getNowNode(@RequestParam("id") String id, @RequestParam("flowId") String flowId, @RequestParam("formId") String formId) {
        return Result.OK(safetyMonitorPostHandler.getNowNode(id, flowId, formId));
    }

    @AutoLog(value = "监控岗位-传阅", operateType = 3)
    @ApiOperation("监控岗位-传阅")
    @GetMapping("/circulate")
    public Result<?> circulate(@RequestParam("flowId") String flowId, @RequestParam("fromUser") String fromUser, @RequestParam("toUser") String toUser) {
        return Result.OK(safetyMonitorPostHandler.circulate(flowId, fromUser, toUser));
    }


    @AutoLog(value = "监控岗位-获取字段权限", operateType = 1)
    @ApiOperation(value = "监控岗位-获取字段权限")
    @GetMapping("/getAuthFields")
    public Result<?> getAuthFields(@RequestParam("id") Long id, @RequestParam("flowId") String flowId, @RequestParam("formId") String formId, @RequestParam("nodeId") String nodeId) {
        return Result.OK(safetyMonitorPostHandler.getAuthFields(id, flowId, nodeId));
    }

    @AutoLog(value = "监控岗位-查询单个", operateType = 1)
    @ApiOperation(value = "监控岗位-查询单个")
    @GetMapping("/detail")
    public Result<?> getDetail(@RequestParam("id") Long id) {
        return Result.OK(safetyMonitorPostHandler.getDetail(id));
    }

    @AutoLog(value = "岗位上报-分页列表查询", operateType = 1)
    @ApiOperation(value = "监控岗位-分页数据列表查询接口")
    @PostMapping("/list")
    public Result<?> getList(@RequestBody SafetyMonitorPostProcessDTO processDTO) {
        Page<SafetyMonitorPostProcessDTO> page = new Page<>(processDTO.getPageNo(), processDTO.getPageSize());
        IPage<SafetyMonitorPostProcessDTO> pageList = safetyMonitorPostHandler.getList(page, processDTO);
        return Result.OK(pageList);
    }

    @AutoLog(value = "监控岗位上报-导出", operateType = 1)
    @ApiOperation(value = "监控岗位-根据条件导出")
    @GetMapping("/exportData")
    public ModelAndView exportData(HttpServletRequest request, SafetyMonitorPostProcessDTO processDTO) {
        List<YTSafetyMonitorPostExportVO> exportList = safetyMonitorPostHandler.exportMonitorPost(request, processDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "监控岗位验证结果列表");
        mv.addObject(NormalExcelConstants.CLASS, YTSafetyMonitorPostExportVO.class);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("监控岗位验证结果列表", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "监控岗位验证结果");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }
}
