package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.api.IApiRouteConfigService;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.factory.action.YxtRebotService;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.dto.*;
import com.yutong.modules.risk.entity.YTSafetyHoliday;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.entity.YTSafetyTaskInfo;
import com.yutong.modules.risk.entity.YtsafetyTaskInfoExtension;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.listener.SafetyTaskImportListener;
import com.yutong.modules.risk.mapper.YTSafetyHolidayMapper;
import com.yutong.modules.risk.mapper.YTSafetyTaskInfoMapper;
import com.yutong.modules.risk.mapper.YtsafetyTaskInfoExtensionMapper;
import com.yutong.modules.risk.service.IYTSafetyHolidayService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.service.impl.YtsafetyRiskDataServiceImpl;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.dto.TaskUserQueryDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.excel.core.ExcelImportProcessor;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Api(tags="任务")
@RestController
@RequestMapping("/sys/task")
public class YTSafetyTaskInfoController {

    @Autowired
    YTSafetyTaskService ytSafetyTaskService;

    @Autowired
    YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @Autowired
    YtsafetyTaskInfoExtensionMapper ytsafetyTaskInfoExtensionMapper;

    @Autowired
    YTSafetyTaskInfoMapper ytSafetyTaskInfoMapper;

    @Autowired
    ISafetySysRoleService safetySysRoleService;

    @Autowired
    private FileService fileService;

    @Value("${ytsafety.custom.constant.safetyOfficer}")
    private String safetyOfficer;

    @Autowired
    IYtsafetyResponsibleProcessService safetyResponsibleProcessService;

    @Autowired
    IYTSafetyHolidayService ytSafetyHolidayService;

    @Autowired
    YTSafetyHolidayMapper ytSafetyHolidayMapper;
    @Autowired
    private IEipDoneService eipDoneService;
    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;



    @AutoLog(value = "获取所有人员", operateType = 1)
    @ApiOperation(value = "获取所有人员", notes = "获取所有人员")
    @PostMapping(value = "/getUser")
    public Result<?> getUser(@RequestBody TaskUserQueryDTO userQueryDTO) {
        return Result.OK(ytSafetyTaskInfoMapper.getTaskUser(userQueryDTO));
    }

    @AutoLog(value = "获取所有岗位",operateType = 1)
    @ApiOperation(value = "获取所有岗位", notes = "获取所有岗位")
    @PostMapping(value = "/getPosition")
    public Result<?> getPosition(@RequestBody TaskUserQueryDTO userQueryDTO) {
        LambdaQueryWrapper<SafetySysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SafetySysRole::getRoleType,1,2);

        return Result.OK(safetySysRoleService.list(queryWrapper));
    }

    @AutoLog(value = "获取风险数据",operateType = 1)
    @ApiOperation(value = "获取风险数据", notes = "获取风险数据")
    @PostMapping(value = "/getTaskRiskData")
    public Result<?> getTaskRiskData(@RequestBody YtsafetyTaskRiskDataDTO riskDataDTO) {
        Page<YtsafetyTaskRiskDataVO> page = new Page<>(riskDataDTO.getPageNo(), riskDataDTO.getPageSize());
        Map resultMap = new HashMap();
        riskDataDTO.setSafetyOfficer(safetyOfficer);
        IPage<YtsafetyTaskRiskDataVO> pageList =ytSafetyTaskInfoMapper.getTaskRiskData(page,riskDataDTO);
        List<String[]> pairs = new ArrayList<>();
        for (YtsafetyTaskRiskDataVO record : pageList.getRecords()) {
            pairs.add(new String[]{record.getOrgCode(), safetyOfficer});
        }
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyTaskRiskDataVO record : pageList.getRecords()) {
            record.setSafetyOfficerName(getRoleNames(nameMap, record.getOrgCode(), safetyOfficer));
        }
        resultMap.put("pageList",pageList);
        return Result.OK(resultMap);
    }

    @AutoLog(value = "获取风险数据安全员",operateType = 1)
    @ApiOperation(value = "获取风险数据安全员", notes = "获取风险数据安全员")
    @PostMapping(value = "/getTaskRiskDataSafetyOfficer")
    public Result<List<YtsafetyTaskUserVO>> getTaskRiskDataSafetyOfficer(@RequestBody YtsafetyTaskRiskDataDTO riskDataDTO) {
        return Result.OK(ytSafetyTaskService.getTaskRiskDataSafetyOfficer(riskDataDTO));
    }

    private String getRoleNames(Map<String, String> nameMap, String orgCode, String roleCode) {
        if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
            return "";
        }
        return nameMap.getOrDefault(orgCode + "_" + roleCode, "");
    }

    @AutoLog(value = "获取风险等级下拉框", operateType = 1)
    @ApiOperation(value = "获取风险等级下拉框", notes = "获取风险等级下拉框")
    @GetMapping(value = "/getRiskLevelDict")
    public Result<?> getRiskLevelDict() {
        return Result.OK(ytSafetyTaskInfoMapper.getRiskLevelDict());
    }

    @RequiresPermissions("tasklog:sleep")
    @AutoLog(value = "休眠任务", operateType = 3)
    @ApiOperation(value = "休眠任务", notes = "休眠任务")
    @GetMapping(value = "/setTaskDormancy")
    public Result<?> setTaskDormancy(@RequestParam(name = "id") String id) {
        String[] ids = id.split(",");
        LambdaQueryWrapper<YTSafetyTaskInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(YTSafetyTaskInfo::getId,ids);
        List<YTSafetyTaskInfo> taskInfoList = ytSafetyTaskService.list(queryWrapper);
        if (taskInfoList.stream().anyMatch(taskInfo -> taskInfo.getDormantDate() != null)) {
            return Result.error("有任务正在休眠中");
        }
        ytSafetyTaskInfoMapper.setTaskDormancy(ids);
        return Result.OK("操作成功!");
    }

    @RequiresPermissions("tasklog:delsleep")
    @AutoLog(value = "取消休眠任务", operateType = 3)
    @ApiOperation(value = "取消休眠任务", notes = "取消休眠任务")
    @GetMapping(value = "/cancelsetTaskDormancy")
    public Result<?> cancelsetTaskDormancy(@RequestParam(name = "id") String id) {

        if (StringUtils.isBlank(id)) {
            return Result.error("ID不能为空");
        }

        String[] ids = id.split(",");
        List<YTSafetyTaskInfo> taskInfoList = ytSafetyTaskService.list(
                new LambdaQueryWrapper<YTSafetyTaskInfo>().in(YTSafetyTaskInfo::getId,ids));

        Date now = new Date();
        taskInfoList.forEach(taskInfo -> {
            taskInfo.setDormantDate(null);
            if (taskInfo.getEndDate() != null && taskInfo.getEndDate().before(now)) {
                // 结束时间 <= 当前时间 → 已过期
                taskInfo.setTaskStatus(CustomConstant.TaskStatus.EXPIRED);
            }
            if (taskInfo.getStartDate() != null && taskInfo.getStartDate().after(now)) {
                // 开始时间 > 当前时间 → 待执行
                taskInfo.setTaskStatus(CustomConstant.TaskStatus.PENDING);
            }
        });

        ytSafetyTaskService.updateBatchById(taskInfoList);
        return Result.OK("操作成功!");
    }

    @RequiresPermissions(value = {"tasklog:add", "patroltasklog:add", "specialtasklog:add"}, logical = Logical.OR)
    @AutoLog(value = "添加任务",operateType = 3)
    @ApiOperation(value = "添加任务", notes = "添加任务")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody YTSafetyTaskInfoDTO taskInfoDTO) {
        ytSafetyTaskService.addTask(taskInfoDTO);
        return Result.OK("添加成功");
    }

    @AutoLog(value = "审批任务",operateType = 3)
    @ApiOperation(value = "审批任务", notes = "审批任务")
    @PostMapping(value = "/processApprove")
    public Result<?> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        BpmReponseParamsVO bpmReponseParamsVO = safetyResponsibleProcessService.executeProcess(processParamDTO);
        log.info("审批通过返回数据:{}",bpmReponseParamsVO);
        String taskId = String.valueOf(bpmReponseParamsVO.getFormInstanceId());
        YTSafetyTaskExecution byId = ytSafetyTaskExecutionService.getById(taskId);
        if(ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            byId.setTaskStatus(CustomConstant.TaskStatus.REJECT);
        }
        if(ProcessStatusEnums.discarded.getCode() == bpmReponseParamsVO.getFlowStatus()){
            byId.setTaskStatus(CustomConstant.TaskStatus.DISCARDED);
        }
        ytSafetyTaskExecutionService.updateById(byId);
        return Result.OK(bpmReponseParamsVO);
    }

    // 编辑的时候更新下次执行日期

    @RequiresPermissions("tasklog:edit")
    @AutoLog(value = "编辑任务",operateType = 3)
    @ApiOperation(value = "编辑任务", notes = "编辑任务")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody YTSafetyTaskInfoDTO taskInfoDTO) {
        ytSafetyTaskService.editTask(taskInfoDTO);
        return Result.OK("编辑成功");
    }

    @RequiresPermissions("tasklog:del")
    @AutoLog(value = "删除任务",operateType = 4)
    @ApiOperation(value = "删除任务", notes = "删除任务")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") Long id) {
        ytSafetyTaskService.deleteTask(id);
        return Result.OK("删除成功");
    }

    @AutoLog(value = "查询任务详情",operateType = 1)
    @ApiOperation(value = "查询任务详情", notes = "查询任务详情")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") Long id) {
        YTSafetyTaskInfoVO taskInfoVO = ytSafetyTaskService.queryById(id);

        String attachmentId = taskInfoVO.getAttachmentId();
        String attachmentImgId = taskInfoVO.getAttachmentImgId();
        if(StringUtils.isNotBlank(attachmentId)){
            List<String> fileIdList = Arrays.stream(attachmentId.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
            taskInfoVO.setAttachmentList( list);
        }
        if(StringUtils.isNotBlank(attachmentImgId)){
            List<String> fileIdList = Arrays.stream(attachmentImgId.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
            taskInfoVO.setAttachmentImgList(list);
        }
        return Result.ok(taskInfoVO);
    }

    @RequiresPermissions("tasklog:list")
    @AutoLog(value = "任务列表",operateType = 1)
    @ApiOperation(value = "任务列表", notes = "任务列表")
    @PostMapping(value = "/list")
    public Result<?> queryPageList(@RequestBody YTSafetyTaskInfoDTO taskInfoDTO) {
        Page<YTSafetyTaskInfoVO> page = new Page<>(taskInfoDTO.getPageNo(), taskInfoDTO.getPageSize());
        IPage<YTSafetyTaskInfoVO> pageList = ytSafetyTaskService.queryPage(page, taskInfoDTO);
        return Result.ok(pageList);
    }

    @RequiresPermissions("tasklog:export")
    @AutoLog(value = "任务列表-导出", operateType = 5)
    @ApiOperation(value = "任务列表-导出", notes = "任务列表-导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, YTSafetyTaskInfoDTO taskInfoDTO) {

        List<YTSafetyTaskInfoExporVO> ytSafetyTaskInfoExporVOS = ytSafetyTaskService.exportTaskList(request, taskInfoDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "任务台账");
        mv.addObject(NormalExcelConstants.CLASS, YTSafetyTaskInfoExporVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("任务台账", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "任务台账");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, ytSafetyTaskInfoExporVOS);
        return mv;
    }

    @RequiresPermissions(value = {"patroltasklog:list", "specialtasklog:list"}, logical = Logical.OR)
    @AutoLog(value = "任务台账",operateType = 1)
    @ApiOperation(value = "任务台账", notes = "任务台账")
    @PostMapping(value = "/getTaskExecution")
    public Result<?> getTaskExecution(@RequestBody YTSafetyTaskExecutionDTO taskExecutionDTO) {
        Page<YTSafetyTaskExecutionVO> page = new Page<>(taskExecutionDTO.getPageNo(), taskExecutionDTO.getPageSize());
        IPage<YTSafetyTaskExecutionVO> pageList = ytSafetyTaskExecutionService.queryPage(page, taskExecutionDTO);
        return Result.ok(pageList);
    }

    @AutoLog(value = "巡查任务详情APP",operateType = 1)
    @ApiOperation(value="巡查任务详情APP", notes="巡查任务详情APP")
    @GetMapping(value = "/xcrwDetailApp")
    public Result<YTSafetyXcrwTaskExecutionDetailVO> xcrwDetailApp(@RequestParam(name = "id") String id) {
        return Result.OK(ytSafetyTaskService.queryTaskExecutionDetailApp(id));
    }

    @AutoLog(value = "巡查任务详情PC",operateType = 1)
    @ApiOperation(value="巡查任务详情PC", notes="巡查任务详情PC")
    @GetMapping(value = "/xcrwDetailPc/{id}")
    public Result<YTSafetyXcrwTaskExecutionDetailVO> xcrwDetailPc(@PathVariable("id") String id) {
        return Result.OK(ytSafetyTaskService.queryTaskExecutionDetailPc(id));
    }

    @AutoLog(value = "专项任务详情",operateType = 1)
    @ApiOperation(value="专项任务详情", notes="专项任务详情")
    @GetMapping(value = "/zxrwDetail")
    public Result<YTSafetyZxrwTaskExecutionDetailVO> zxrwDetail(@RequestParam(name = "id") String id) {
        return Result.OK(ytSafetyTaskService.queryTaskZxrwExecutionDetailPc(id));
    }

    @AutoLog(value = "开始任务",operateType = 3)
    @ApiOperation(value="开始任务", notes="开始任务")
    @GetMapping(value = "/startTask")
    public Result<?> startTask(@RequestParam(name = "id") String id) {
        YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(id);
        if(taskExecution.getTaskCompleteTime()!=null){
            throw new IllegalArgumentException("任务已开始");
        }
        YTSafetyTaskExecution taskExecutionVO = new YTSafetyTaskExecution();
        taskExecutionVO.setId(Long.valueOf(id));
        taskExecutionVO.setTaskStartTime(new Date());
        ytSafetyTaskExecutionService.updateById(taskExecutionVO);
        return Result.OK("操作成功!");
    }

    @AutoLog(value = "更新图片",operateType = 3)
    @ApiOperation(value="更新图片", notes="更新图片")
    @PostMapping(value = "/updatePhoto")
    public Result<?> updatePhoto(@RequestParam("id") String id,
                                 @RequestParam(value = "fileIds", required = false) String fileIds,
                                 @RequestParam(value = "photoFileds", required = false) String photoFileds) {
//        YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(id);
        YTSafetyTaskExecution taskExecutionVO = new YTSafetyTaskExecution();
        taskExecutionVO.setId(Long.valueOf(id));
        taskExecutionVO.setAttachmentImgId(fileIds);
        ytSafetyTaskExecutionService.updateById(taskExecutionVO);
        return Result.OK("操作成功!");
    }

    @AutoLog(value = "巡查任务结束任务",operateType = 3)
    @ApiOperation(value="巡查任务结束任务", notes="巡查任务结束任务")
    @GetMapping(value = "/xcrwEndTask")
    public Result<?> xcrwEndTask(@RequestParam(name = "id") String id) {
        YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(id);
        if(taskExecution.getTaskCompleteTime()!=null){
            throw new IllegalArgumentException("任务已结束");
        }
        Date startDate = taskExecution.getTaskStartTime();
        if(startDate==null){
            throw new IllegalArgumentException("任务还没开始!");
        }
        if(StringUtils.isBlank(taskExecution.getAttachmentImgId())){
            throw new IllegalArgumentException("请上传图片");
        }
        Date endDate = new Date();
        long diffInMillis = endDate.getTime() - startDate.getTime();
//        long minutes = Math.round((double) diffInMillis / (60 * 1000));
        long minutes = (long) Math.ceil((double) diffInMillis / (60 * 1000));
        taskExecution.setCompleteInterval(String.valueOf(minutes));
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
        ytSafetyTaskExecutionService.updateById(taskExecution);
        //        设置EIP代办为已办
        EipSetTodoDoneArg eipSetTodoDoneArg = new EipSetTodoDoneArg();
        eipSetTodoDoneArg.setAppName("安消管理平台");
        eipSetTodoDoneArg.setModelId(String.valueOf(taskExecution.getId()));
        eipSetTodoDoneArg.setType(1);
        eipSetTodoDoneArg.setModelName("安消管理平台");
        eipDoneService.eipToDoDone(eipSetTodoDoneArg);
        return Result.OK("操作成功!");
    }

    @AutoLog(value = "专项任务结束任务",operateType = 3)
    @ApiOperation(value="专项任务结束任务", notes="专项任务结束任务")
    @PostMapping(value = "/zxrwEndTask")
    public Result<?> zxrwEndTask(@RequestParam("id") String id,
                                 @RequestParam(value = "fileIds", required = false) String fileIds,
                                 @RequestParam(value = "photoFileds", required = false) String photoFileds,
                                 @RequestParam(value = "exports", required = false) String exports) {

        YTSafetyTaskExecution taskExecution = ytSafetyTaskExecutionService.getById(id);

        if(CustomConstant.TaskStatus.APPROVAL.equals(taskExecution.getTaskStatus())){
            throw new IllegalArgumentException("任务正在审批中");
        }

        if(taskExecution.getTaskCompleteTime()!=null){
            throw new IllegalArgumentException("任务已结束");
        }
        LambdaQueryWrapper<YtsafetyTaskInfoExtension> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YtsafetyTaskInfoExtension::getTaskInfoId, taskExecution.getTaskInfoId());
        YtsafetyTaskInfoExtension exVo = ytsafetyTaskInfoExtensionMapper.selectOne(queryWrapper);
        if("1".equals(exVo.getFileMust())){
            if(StringUtils.isEmpty(fileIds)){
                throw new IllegalArgumentException("请上传附件");
            }
        }
        if("1".equals(exVo.getPhotoMust())){
            if(StringUtils.isEmpty(photoFileds)){
                throw new IllegalArgumentException("请上传照片");
            }
        }
        ytSafetyTaskService.zxrwEndTask(id,fileIds,photoFileds,exports);
        return Result.OK("提交成功!");
    }

    @AutoLog(value = "我的任务列表",operateType = 1)
    @ApiOperation(value="我的任务列表", notes="我的任务列表")
    @PostMapping(value = "/queryMyTaskList")
    public Result<YTSafetyMyTaskVO> queryMyTaskList(@RequestParam(value = "execScope",defaultValue = "1") String execScope,
                                                    @RequestParam(value = "startDate",required = false) String startDate,
                                                    @RequestParam(value = "endDate",required = false) String endDate,
                                                    @RequestParam(value = "isCompleted",defaultValue = "1") Integer isCompleted,
                                                    @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                                                    @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        return Result.OK(ytSafetyTaskService.queryMyTaskList(execScope,startDate,endDate,isCompleted,pageNo,pageSize));
    }



    @RequiresPermissions("patroltasklog:export")
    @AutoLog(value = "巡查任务台账-导出", operateType = 5)
    @ApiOperation(value = "巡查任务台账-导出", notes = "巡查任务台账-导出")
    @GetMapping(value = "/exportXlsPatrolMission")
    public ModelAndView exportXlsPatrolMission(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO) {
        List<YTSafetyTaskExecutionXCRWExportVO> ytSafetyTaskExecutionXCRWExportVOS = ytSafetyTaskExecutionService.exportPatrolMissionQueryPage(request, taskExecutionDTO);

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "巡查任务台账");
        mv.addObject(NormalExcelConstants.CLASS, YTSafetyTaskExecutionXCRWExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("巡查任务台账", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "巡查任务台账");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, ytSafetyTaskExecutionXCRWExportVOS);
        return mv;
    }
    @RequiresPermissions("specialtasklog:export")
    @AutoLog(value = "专项任务台账-导出", operateType = 5)
    @ApiOperation(value = "专项任务台账-导出", notes = "专项任务台账-导出")
    @GetMapping(value = "/exportXlsSpecialTask")
    public ModelAndView exportXlsSpecialTask(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO) {

        List<YTSafetyTaskExecutionZXRWExportVO> ytSafetyTaskExecutionZXRWExportVOS = ytSafetyTaskExecutionService.exportSpecialTaskQueryPage(request, taskExecutionDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "专项任务台账");
        mv.addObject(NormalExcelConstants.CLASS, YTSafetyTaskExecutionZXRWExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("专项任务台账", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "专项任务台账");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, ytSafetyTaskExecutionZXRWExportVOS);
        return mv;
    }


    // ==================== 节假日管理 ====================

    @RequiresPermissions("taskManagement:holidayAdd")
    @AutoLog(value = "节假日-新增", operateType = 3)
    @ApiOperation(value = "节假日-新增/编辑", notes = "节假日-新增/编辑，holidayDate支持逗号分割多个日期批量插入")
    @PostMapping(value = "/holiday/add")
    public Result<?> holidayAdd(@RequestBody YTSafetyHolidayDTO holidayDTO) {
        if (StringUtils.isBlank(holidayDTO.getHolidayDate())) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (holidayDTO.getDayType() == null) {
            throw new IllegalArgumentException("类型不能为空");
        }
        YTSafetyHoliday holiday = new YTSafetyHoliday();
        int currentYear = LocalDate.now().getYear();
        holiday.setHolidayYear(String.valueOf(currentYear));
        holiday.setId(holidayDTO.getId());
        holiday.setHolidayDate(holidayDTO.getHolidayDate().trim());
        holiday.setHolidayName(holidayDTO.getHolidayName());
        holiday.setDayType(holidayDTO.getDayType());
        holiday.setDescription(holidayDTO.getDescription());
        ytSafetyHolidayService.saveOrUpdate(holiday);
        return Result.OK("保存成功");
    }

    @RequiresPermissions("taskManagement:holidayDel")
    @AutoLog(value = "节假日-删除", operateType = 4)
    @ApiOperation(value = "节假日-删除", notes = "节假日-删除，支持传多个id用逗号分割批量删除")
    @DeleteMapping(value = "/holiday/delete")
    public Result<?> holidayDelete(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID不能为空");
        }
        String[] ids = id.split(",");
        ytSafetyHolidayService.removeByIds(Arrays.asList(ids));
        return Result.OK("删除成功");
    }

    @RequiresPermissions("taskManagement:holidayList")
    @AutoLog(value = "节假日-列表", operateType = 1)
    @ApiOperation(value = "节假日-列表", notes = "节假日-分页列表")
    @GetMapping(value = "/holiday/list")
    public Result<?> holidayList(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                @RequestParam(value = "dayType", required = false) Integer dayType,
                                @RequestParam(value = "holidayName", required = false) String holidayName,
                                 @RequestParam(value = "holidayYear", required = false) String holidayYear,
                                 @RequestParam(value = "holidayDate", required = false) String holidayDate) {
        Page<YTSafetyHoliday> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<YTSafetyHoliday> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dayType!=null,YTSafetyHoliday::getDayType, dayType)
                .like(StringUtils.isNotBlank(holidayName),YTSafetyHoliday::getHolidayName, holidayName)
                .eq(StringUtils.isNotBlank(holidayYear),YTSafetyHoliday::getHolidayYear, holidayYear)
                .like(StringUtils.isNotBlank(holidayDate),YTSafetyHoliday::getHolidayDate, holidayDate);

        queryWrapper.orderByAsc(YTSafetyHoliday::getHolidayDate);
        IPage<YTSafetyHoliday> pageList = ytSafetyHolidayService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @PostMapping("/import")
    public ModelAndView importExcel(@RequestParam("file") MultipartFile file,HttpServletRequest request,HttpServletResponse response){
        try {
            SafetyTaskImportListener listener = new SafetyTaskImportListener();
            return ExcelImportProcessor.processImport(file,request,response, YTSafetyTaskInfoImportVO.class,listener,
                    1,0,"任务",
                    successList -> {
//                      导入成功,保存数据库
//                        successList.size();
//                        System.out.println("导入成功");
                    }
            );
        } catch (Exception e) {
            log.error("导入异常", e);
            throw new RuntimeException("导入失败：" + e.getMessage());
        }
    }
}
