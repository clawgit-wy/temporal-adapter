package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.entity.YTSafetyValidateScenePost;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.handler.YTSafetyValidateScenePostHandler;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.vo.YTSafetyValidateScenePostExportVO;
import com.yutong.modules.risk.vo.YTSafetyValidateScenePostSimulateExportVO;
import com.yutong.modules.risk.web.ValidateScenePostAddParam;
import com.yutong.modules.risk.web.ValidateScenePostListParam;
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
 * 岗位验证-(现场岗位、模拟岗位)岗位验证上报及验证结果
 * @author gwxiexj
 * @since 2024-06-24 14:08
 */
@Api(tags = "岗位验证、业务验证")
@RestController
@RequestMapping("/web/safety/validate/scenePost")
public class YTSafetyValidateScenePostController {

    @Autowired
    private YTSafetyValidateScenePostHandler safetyValidateScenePostHandler;

    @Autowired
    private YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @Autowired
    private IEipDoneService eipDoneService;

    /**
     * 岗位验证-验证结果列表查询方法
     * @param param
     * @return
     */
    @AutoLog(value = "岗位验证-分页数据列表查询", operateType = 1)
    @ApiOperation(value = "现场岗位、模拟岗位-数据列表查询接口")
    @PostMapping(value = "/listPage")
    public Result<IPage<YTSafetyValidateScenePost>> listPage(@RequestBody ValidateScenePostListParam param) {
        Page<YTSafetyValidateScenePost> page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<YTSafetyValidateScenePost> pageList = safetyValidateScenePostHandler.pageList(page, param);
        return Result.OK(pageList);
    }

    /**
     * 数据导出操作
     * @param param
     */
    @AutoLog(value = "岗位验证-数据导出", operateType = 1)
    @ApiOperation(value = "现场岗位、模拟岗位-数据导出接口")
    @GetMapping("/export")
    public ModelAndView export(HttpServletRequest request, ValidateScenePostListParam param) {
        List<?> exportList = safetyValidateScenePostHandler.exportScenePost(request, param);
        Integer dataType = param.getDataType();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String sheetName;
        if(dataType == 1) {
            sheetName = "岗位验证-现场岗位验证结果";
        }else {
            sheetName = "岗位验证-模拟岗位验证结果";
        }
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, sheetName);
        mv.addObject(NormalExcelConstants.CLASS, dataType == 1 ? YTSafetyValidateScenePostExportVO.class : YTSafetyValidateScenePostSimulateExportVO.class);
        ExportParams exportParams = new ExportParams(sheetName, "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), sheetName);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @AutoLog(value = "岗位验证-添加", operateType = 2)
    @ApiOperation(value = "现场岗位、模拟岗位-添加接口")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ValidateScenePostAddParam param) {
        Long save = safetyValidateScenePostHandler.save(param);
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
                taskExecution.setReportPostId(save);
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
     * 数据更新操作
     * @param param
     * @return
     */
    @AutoLog(value = "岗位验证-更新", operateType = 3)
    @ApiOperation(value = "现场岗位、模拟岗位-更新接口")
    @PostMapping(value = "/update")
    public Result<?> update(@RequestBody ValidateScenePostAddParam param) {
        safetyValidateScenePostHandler.update(param);
        return Result.OK("更新成功！");
    }

    /**
     * 数据查看操作
     * @param id ID
     * @return
     */
    @AutoLog(value = "岗位验证-查看详情", operateType = 1)
    @ApiOperation(value = "现场岗位、模拟岗位-查看详情接口")
    @GetMapping(value = "/view")
    public Result<YTSafetyValidateScenePost> view(@RequestParam("id") Long id) {
        return Result.OK(safetyValidateScenePostHandler.selectByParimaryKey(id));
    }
}
