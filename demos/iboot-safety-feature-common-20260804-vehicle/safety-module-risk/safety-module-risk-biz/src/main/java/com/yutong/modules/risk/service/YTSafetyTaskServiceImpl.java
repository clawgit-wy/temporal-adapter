package com.yutong.modules.risk.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.api.IApiRouteConfigService;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.IThirdFactoryApi;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import com.yutong.modules.common.entity.bpm.NodeInfo;
import com.yutong.modules.common.entity.bpm.Operation;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import com.yutong.modules.common.entity.eip.todo.EipSendTodoArg;
import com.yutong.modules.common.factory.action.YxtRebotService;
import com.yutong.modules.common.utils.EipXmlUtil;
import com.yutong.modules.common.utils.YTWebserviceUtil;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.convert.SafetyTaskInfoConvert;
import com.yutong.modules.risk.dto.YTSafetyTaskInfoDTO;
import com.yutong.modules.risk.dto.YtsafetyTaskInfoExtensionDTO;
import com.yutong.modules.risk.dto.YtsafetyTaskRiskDataDTO;
import com.yutong.modules.risk.entity.*;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.mapper.*;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.dto.TaskUserQueryDTO;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YTSafetyTaskServiceImpl extends ServiceImpl<YTSafetyTaskInfoMapper, YTSafetyTaskInfo> implements YTSafetyTaskService {

    @Value("${ytsafety.custom.constant.safetyOfficer}")
    private String safetyOfficer;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    @Value("${ytsafety.eip.todo.url}")
    private String toDoUrl;

    @Value("${ytsafety.yxt.robotId}")
    private String robotId;

    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;

    @Resource
    private SafetyTaskInfoConvert taskInfoConvert;
    
    @Resource
    private YtsafetyTaskInfoExtensionMapper extensionMapper;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    YTSafetyTaskInfoFactoryRelMapper taskInfoFactoryRelMapper;
    @Resource
    YTSafetyTaskInfoOrgRelMapper taskInfoOrgRelMapper;
    @Resource
    YTSafetyTaskInfoAreaRelMapper taskInfoAreaRelMapper;
    @Autowired
    YTSafetyTaskInfoMapper ytSafetyTaskInfoMapper;
    @Autowired
    YtsafetyTaskInfoExtensionMapper taskInfoExecutionMapper;
    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;
    @Resource
    YTSafetyTaskExecutionMapper taskExecutionMapper;
    @Autowired
    private IEipDoneService eipDoneService;
    @Autowired
    private FileService fileService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ISafetyEmployeeMessageService employeeMessageService;

    @Autowired
    private ISafetySysUserService sysUserService;

    @Autowired
    private YTWebserviceUtil ytWebserviceUtil;

    @Autowired
    private IApiRouteConfigService apiRouteConfigService;

    @Autowired
    private YxtRebotService yxtRebotService;

    @Autowired
    YTSafetyWaterFlowNoService ytSafetyWaterFlowNoService;

    @Autowired
    private IThirdFactoryApi thirdFactoryApi;

    @Value("${ytsafety.yxt.yxtCode:}")
    private String yxtCode;

    @Value("${iam.mail.url:}")
    private String mailUrl;

    @Value("${iam.mail.templateCode:}")
    private String templateCode;

    @Value("${ytsafety.custom.constant.firefighter}")
    private String firefighter;
    @Autowired
    private IBpmProcessService bpmProcessService;
    @Autowired
    IYtsafetyResponsibleProcessService safetyResponsibleProcessService;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTask(YTSafetyTaskInfoDTO taskInfoDTO) {
        LoginUser loginUser = BusUtits.getLoginUser();
        YTSafetyTaskInfo taskInfo = taskInfoConvert.dtoToEntity(taskInfoDTO);
        taskInfo.setTaskStatus(CustomConstant.TaskStatus.PENDING);
        validateTaskConfig(taskInfoDTO);
        if ("GWYZ".equals(taskInfoDTO.getTaskType())){
            //监控岗位需要在移动端显示，但不能填报
            taskInfo.setExecScope("1,2");
        }else if ("YWYZ".equals(taskInfoDTO.getTaskType())){
            taskInfo.setExecScope("2");
        }
        if(StringUtils.isBlank(taskInfo.getExecScope())){
            throw new IllegalArgumentException("请选择任务下发范围");
        }
        // 计算最近一次执行日期
        if(CustomConstant.ExecType.ONCE.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
            //如果单次 立即下发
            taskInfoDTO.getTaskInfoExtension().setCurrentExecutionCycle(sdf.format(new Date()));
            taskInfo.setTaskStatus(CustomConstant.TaskStatus.RUNNING);
        }else{
            calculateAndSetCurrentExecutionCycle(taskInfoDTO,false);
        }
        if(taskInfoDTO.getTaskInfoExtension().getCurrentExecutionCycle()==null &&
                !CustomConstant.ExecType.ONCE.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
            throw new IllegalArgumentException("当前任务开始/结束时间内找不到符合条件的任务周期,请重新选择开始/结束时间或任务周期");
        }
        taskInfo.setTaskCode(ytSafetyWaterFlowNoService.getNowByTypeDay("RWXF"+taskInfoDTO.getTaskType(),null));
//       如果是专项任务  需要审批
        if(CustomConstant.TaskType.ZXRW.equals(taskInfo.getTaskType())){
            taskInfo.setFlowCode(taskInfo.getTaskCode());
            taskInfo.setModelFlag("special_task_process");
            taskInfo.setFlowSubject(loginUser.getRealname()+"下发任务："+taskInfo.getTaskName());
            if( CustomConstant.ApprovalType.INITIATOR_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                taskInfo.setApprovalName(loginUser.getRealname());
                taskInfo.setApprovalUser(loginUser.getEipName());
            }else if( CustomConstant.ApprovalType.FIREFIGHTER_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                List<String> approvalUserIds = baseMapper.approvefirefighters(String.valueOf(loginUser.getUserId()), firefighter);
                if(approvalUserIds.isEmpty()){
                    throw new IllegalArgumentException("审批人不存在");
                }
            }else if( CustomConstant.ApprovalType.CUSTOM_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                if(StringUtils.isBlank(taskInfoDTO.getApprovalName()) || StringUtils.isBlank(taskInfoDTO.getApprovalUser())){
                    throw new IllegalArgumentException("审批人不能为空");
                }
                taskInfo.setApprovalName(taskInfoDTO.getApprovalName());
                taskInfo.setApprovalUser(taskInfoDTO.getApprovalUser());
            }

        }
        YtsafetyTaskInfoExtensionDTO taskInfoExtension = taskInfoDTO.getTaskInfoExtension();
        if("3".equals(taskInfoDTO.getTaskInfoExtension().getIssueType())){
            if(StringUtils.isBlank(taskInfoExtension.getPersonnelOrgId())
                    &&StringUtils.isBlank(taskInfoExtension.getPersonnelFactoryId())
                    &&StringUtils.isBlank(taskInfoExtension.getPersonnelPostId())){
                throw new IllegalArgumentException("请选择岗位!");
            }
            TaskUserQueryDTO userQueryDTO = new TaskUserQueryDTO();
            userQueryDTO.setFactoryId(taskInfoExtension.getPersonnelFactoryId());
            userQueryDTO.setOrgCode(taskInfoExtension.getPersonnelOrgId());
            userQueryDTO.setRoleCode(taskInfoExtension.getPersonnelPostId());
            List<YtsafetyTaskUserVO> taskUser = ytSafetyTaskInfoMapper.getTaskUser(userQueryDTO);
            if(taskUser.isEmpty()){
                throw new IllegalArgumentException("所选岗位下人员为空!");
            }
            taskInfoExtension.setPersonnelName("");
            taskInfoExtension.setPersonnelId("");
        }else if( "2".equals(taskInfoDTO.getTaskInfoExtension().getIssueType())){
            if(StringUtils.isBlank(taskInfoExtension.getPersonnelId())||StringUtils.isBlank(taskInfoExtension.getPersonnelName())){
                throw new IllegalArgumentException("任务对象不允许为空!");
            }
        }else{
            YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
            riskDataDTO.setRiskTypeId(taskInfoDTO.getRiskCategoryId());
            riskDataDTO.setRiskLevel(taskInfoExtension.getRiskLevel());
            riskDataDTO.setFactoryId(taskInfoExtension.getFactoryId());
            riskDataDTO.setAreaId(taskInfoExtension.getAreaId());
            riskDataDTO.setOrgId(taskInfoExtension.getOrgId());
            riskDataDTO.setSafetyOfficer(safetyOfficer);
            List<YtsafetyTaskUserVO> SafetyOfficerList = getTaskRiskDataSafetyOfficer(riskDataDTO);
            if(SafetyOfficerList.isEmpty()){
                throw new IllegalArgumentException("任务对象不允许为空!");
            }
            taskInfoExtension.setPersonnelName("");
            taskInfoExtension.setPersonnelId("");
        }
        save(taskInfo);
        saveExtension(taskInfo.getId(), taskInfoExtension);
        if(CustomConstant.ExecType.ONCE.equals(taskInfoExtension.getExecType())){
            issueTasks(taskInfo.getId());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editTask(YTSafetyTaskInfoDTO taskInfoDTO) {
        if (taskInfoDTO.getId() == null) {
            throw new IllegalArgumentException("编辑任务时ID不能为空");
        }
        validateTaskConfig(taskInfoDTO);
        // 1. 检查任务是否存在
        YTSafetyTaskInfo existTask = this.getById(taskInfoDTO.getId());
        if (existTask == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        // 2. DTO转Entity并更新主表
        YTSafetyTaskInfo taskInfo = taskInfoConvert.dtoToEntity(taskInfoDTO);
        Date now = new Date();
//       编辑的时候  如果开始时间大于当前时间  →  任务状态改为待执行
//        if (taskInfo.getStartDate() != null && taskInfo.getStartDate().after(now)) {
            // 开始时间 > 当前时间 → 待执行
            taskInfo.setTaskStatus(CustomConstant.TaskStatus.PENDING);
//        } else if (taskInfo.getEndDate() != null && taskInfo.getEndDate().after(now)) {
//            // 开始时间 <= 当前时间 且 结束时间 > 当前时间 → 执行中
//            taskInfo.setTaskStatus(CustomConstant.TaskStatus.RUNNING);
//        }


        // 计算最近一次执行日期 如果是自定义 并且 最近一次的执行时间一过期
//       如果之定义周期, 如果任务的执行周期日期是 昨天及之前 → 说明任务已经过期了，用当前时间重新设置下次执行时间
//        如果今天或未来 → 保持原样
//        没有传日期或日期格式错误 → 默认使用当前时间
        if(!CustomConstant.ExecType.ONCE.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
            if(taskInfo.getStartDate().before(now)){
                throw new IllegalArgumentException("开始时间必须大于当前日期");
            }
        }
        if(CustomConstant.ExecType.CUSTOM.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
//            如果自定义周期 开始时间大于当前日期，那第一次执行时间就是开始时间
            taskInfoDTO.getTaskInfoExtension().setCurrentExecutionCycle(sdf.format(taskInfoDTO.getStartDate()));
        }else{
            if(CustomConstant.ExecType.ONCE.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
                taskInfo.setTaskStatus(CustomConstant.TaskStatus.RUNNING);
                taskInfoDTO.getTaskInfoExtension().setCurrentExecutionCycle(sdf.format(new Date()));
                taskInfo.setStartDate(null);
            }else{
                calculateAndSetCurrentExecutionCycle(taskInfoDTO,false);
                if(StringUtils.isBlank(taskInfoDTO.getTaskInfoExtension().getCurrentExecutionCycle())){
                    throw new IllegalArgumentException("当前任务开始/结束时间内找不到符合条件的任务周期,请重新选择开始/结束时间或任务周期");
                }
            }

        }
        YtsafetyTaskInfoExtensionDTO taskInfoExtension = taskInfoDTO.getTaskInfoExtension();
        if("3".equals(taskInfoDTO.getTaskInfoExtension().getIssueType())){
            if(StringUtils.isBlank(taskInfoExtension.getPersonnelOrgId())
                    &&StringUtils.isBlank(taskInfoExtension.getPersonnelFactoryId())
                    &&StringUtils.isBlank(taskInfoExtension.getPersonnelPostId())){
                throw new IllegalArgumentException("请选择岗位!");
            }
            TaskUserQueryDTO userQueryDTO = new TaskUserQueryDTO();
            userQueryDTO.setFactoryId(taskInfoExtension.getPersonnelFactoryId());
            userQueryDTO.setOrgCode(taskInfoExtension.getPersonnelOrgId());
            userQueryDTO.setRoleCode(taskInfoExtension.getPersonnelPostId());
            List<YtsafetyTaskUserVO> taskUser = ytSafetyTaskInfoMapper.getTaskUser(userQueryDTO);
            if(taskUser.isEmpty()){
                throw new IllegalArgumentException("所选岗位下人员为空!");
            }
            taskInfoDTO.getTaskInfoExtension().setPersonnelName("");
            taskInfoDTO.getTaskInfoExtension().setPersonnelId("");
        }else if( "2".equals(taskInfoDTO.getTaskInfoExtension().getIssueType())){
            if(StringUtils.isBlank(taskInfoExtension.getPersonnelId())||StringUtils.isBlank(taskInfoExtension.getPersonnelName())){
                throw new IllegalArgumentException("任务对象不允许为空!");
            }
        }else{
            YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
            riskDataDTO.setRiskTypeId(taskInfoDTO.getRiskCategoryId());
            riskDataDTO.setRiskLevel(taskInfoExtension.getRiskLevel());
            riskDataDTO.setFactoryId(taskInfoExtension.getFactoryId());
            riskDataDTO.setAreaId(taskInfoExtension.getAreaId());
            riskDataDTO.setOrgId(taskInfoExtension.getOrgId());
            riskDataDTO.setSafetyOfficer(safetyOfficer);
            List<YtsafetyTaskUserVO> SafetyOfficerList = getTaskRiskDataSafetyOfficer(riskDataDTO);
            if(SafetyOfficerList.isEmpty()){
                throw new IllegalArgumentException("任务对象不允许为空!");
            }
            taskInfoDTO.getTaskInfoExtension().setPersonnelName("");
            taskInfoDTO.getTaskInfoExtension().setPersonnelId("");
        }
        if(CustomConstant.TaskType.ZXRW.equals(taskInfo.getTaskType())){
            LoginUser loginUser = BusUtits.getLoginUser();
            taskInfo.setFlowCode(taskInfo.getTaskCode());
            taskInfo.setModelFlag("special_task_process");
            taskInfo.setFlowSubject(loginUser.getRealname()+"下发任务："+taskInfo.getTaskName());
            if( CustomConstant.ApprovalType.INITIATOR_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                taskInfo.setApprovalName(loginUser.getRealname());
                taskInfo.setApprovalUser(loginUser.getEipName());
            }else if( CustomConstant.ApprovalType.FIREFIGHTER_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                List<String> approvalUserIds = baseMapper.approvefirefighters(String.valueOf(loginUser.getUserId()), firefighter);
                if(approvalUserIds.isEmpty()){
                    throw new IllegalArgumentException("审批人不存在");
                }
            }else if( CustomConstant.ApprovalType.CUSTOM_APPROVAL.equals(taskInfoDTO.getIssueApproval())){
                if(StringUtils.isBlank(taskInfoDTO.getApprovalName()) || StringUtils.isBlank(taskInfoDTO.getApprovalUser())){
                    throw new IllegalArgumentException("审批人不能为空");
                }
                taskInfo.setApprovalName(taskInfoDTO.getApprovalName());
                taskInfo.setApprovalUser(taskInfoDTO.getApprovalUser());
            }

        }
        this.updateById(taskInfo);
        taskInfoDTO.getTaskInfoExtension().setTaskInfoId(String.valueOf(taskInfo.getId()));
        // 3. 先删除旧的扩展表数据，再插入新的
        saveExtension(taskInfo.getId(), taskInfoDTO.getTaskInfoExtension());
        if(CustomConstant.ExecType.ONCE.equals(taskInfoDTO.getTaskInfoExtension().getExecType())){
            issueTasks(taskInfo.getId());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        // 检查任务是否存在
        YTSafetyTaskInfo existTask = this.getById(id);
        if (existTask == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        // 先删除扩展表数据
        extensionMapper.delete(new LambdaQueryWrapper<YtsafetyTaskInfoExtension>()
                .eq(YtsafetyTaskInfoExtension::getTaskInfoId, String.valueOf(id)));
        
        this.removeById(id);
    }

    @Override
    public YTSafetyTaskInfoVO queryById(Long id) {
        YTSafetyTaskInfoVO taskDetail = baseMapper.getTaskDetail(id);
        if("1".equals(taskDetail.getTaskInfoExtension().getIssueType())){
            YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
            riskDataDTO.setRiskTypeId(taskDetail.getRiskCategoryId());
            riskDataDTO.setRiskLevel(taskDetail.getTaskInfoExtension().getRiskLevel());
            riskDataDTO.setFactoryId(taskDetail.getTaskInfoExtension().getFactoryId());
            riskDataDTO.setAreaId(taskDetail.getTaskInfoExtension().getAreaId());
            riskDataDTO.setOrgId(taskDetail.getTaskInfoExtension().getOrgId());
            riskDataDTO.setSafetyOfficer(safetyOfficer);
            List<YtsafetyTaskUserVO> SafetyOfficerList = getTaskRiskDataSafetyOfficer(riskDataDTO);
            taskDetail.setTaskUserList(SafetyOfficerList);
        }else if("3".equals(taskDetail.getTaskInfoExtension().getIssueType())){

            TaskUserQueryDTO userQueryDTO = new TaskUserQueryDTO();
            userQueryDTO.setFactoryId(taskDetail.getTaskInfoExtension().getPersonnelFactoryId());
            userQueryDTO.setOrgCode(taskDetail.getTaskInfoExtension().getPersonnelOrgId());
            userQueryDTO.setRoleCode(taskDetail.getTaskInfoExtension().getPersonnelPostId());
            List<YtsafetyTaskUserVO> taskUser = ytSafetyTaskInfoMapper.getTaskUser(userQueryDTO);
            taskDetail.setTaskUserList(taskUser);
        }
        return taskDetail;
    }

    @Override
    public IPage<YTSafetyTaskInfoVO> queryPage(Page<YTSafetyTaskInfoVO> page, YTSafetyTaskInfoDTO taskInfoDTO) {
        taskInfoDTO.setRoleCode(safetyOfficer);
        List<Map> safetyDictItems = baseMapper.getDictItem();
        IPage<YTSafetyTaskInfoVO> ytSafetyTaskInfoVOIPage = baseMapper.queryPage(page, taskInfoDTO);
        List<YTSafetyTaskInfoVO> records = ytSafetyTaskInfoVOIPage.getRecords();
        SafetySysRole safetySysRole = baseMapper.getSafetySysRole(safetyOfficer);
        records.forEach(record -> {
            if("1".equals(record.getIssueType())){
                record.setPersonnelName(safetySysRole.getRoleName());
            }else if("3".equals(record.getIssueType())){
                record.setPersonnelName(record.getPersonnelPostName());
            }

            if (record.getTaskStatus().equals(CustomConstant.TaskStatus.PENDING)){
                record.setTaskStatus("待执行");
            }else if (record.getTaskStatus().equals(CustomConstant.TaskStatus.RUNNING)){
                record.setTaskStatus("执行中");
            }else if(record.getTaskStatus().equals(CustomConstant.TaskStatus.EXPIRED)){
                record.setTaskStatus("已过期");
            }else if(record.getTaskStatus().equals(CustomConstant.TaskStatus.APPROVAL)){
                record.setTaskStatus("审批中");
            }else if(record.getTaskStatus().equals(CustomConstant.TaskStatus.REJECT)){
                record.setTaskStatus("已驳回");
            }

            if (record.getDormantDate()!=null){
                record.setTaskStatus("休眠中");
            }

            if(StringUtils.isNotBlank(record.getRiskCategoryId())){
                if(record.getRiskCategoryId().contains(",")){
                    record.setRiskCategoryId("治安,消防");
                }else{
                    safetyDictItems.stream().filter(item -> String.valueOf(item.get("id")).equals(record.getRiskCategoryId()))
                            .findFirst().ifPresent(item -> record.setRiskCategoryId((String) item.get("itemName")));
                }
            }else{
                record.setRiskCategoryId("-");
            }


        });
        return ytSafetyTaskInfoVOIPage;
    }
    
    /**
     * 保存扩展表信息
     * @param taskId 任务ID
     * @param extensionDTO 扩展信息DTO
     */
    private void saveExtension(Long taskId, YtsafetyTaskInfoExtensionDTO extensionDTO) {
        YtsafetyTaskInfoExtension extension = new YtsafetyTaskInfoExtension();
        BeanUtils.copyProperties(extensionDTO, extension);
        LambdaQueryWrapper<YtsafetyTaskInfoExtension> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YtsafetyTaskInfoExtension::getTaskInfoId, String.valueOf(taskId));
        YtsafetyTaskInfoExtension existExtension = extensionMapper.selectOne(queryWrapper);
        extension.setTaskInfoId(String.valueOf(taskId));

        if(existExtension!=null){
            extension.setId(existExtension.getId());
            extensionMapper.updateById(extension);
        }else{
            extensionMapper.insert(extension);
        }
        String areaId = extensionDTO.getAreaId();
        String factoryId = extensionDTO.getFactoryId();
        String orgId = extensionDTO.getOrgId();
        taskInfoAreaRelMapper.delete(
                new LambdaQueryWrapper<YTSafetyTaskInfoAreaRel>().eq(YTSafetyTaskInfoAreaRel::getTaskInfoId, taskId));
        if (StringUtils.isNotBlank(areaId)) {
            String[] areaIds = areaId.split( ",");
            for (String item : areaIds){
                YTSafetyTaskInfoAreaRel areaRel = new YTSafetyTaskInfoAreaRel();
                areaRel.setTaskInfoId(taskId);
                areaRel.setAreaId(Long.valueOf(item));
                taskInfoAreaRelMapper.insert(areaRel);
            }
        }

        taskInfoFactoryRelMapper.delete(
                new LambdaQueryWrapper<YTSafetyTaskInfoFactoryRel>().eq(YTSafetyTaskInfoFactoryRel::getTaskInfoId, taskId));
        if(StringUtils.isNotBlank(factoryId)){
            String[] factoryIds = factoryId.split( ",");
            for (String item : factoryIds){
                YTSafetyTaskInfoFactoryRel factoryRel = new YTSafetyTaskInfoFactoryRel();
                factoryRel.setTaskInfoId(taskId);
                factoryRel.setFactoryId(Long.valueOf(item));
                taskInfoFactoryRelMapper.insert(factoryRel);
            }
        }

        taskInfoOrgRelMapper.delete(
                new LambdaQueryWrapper<YTSafetyTaskInfoOrgRel>().eq(YTSafetyTaskInfoOrgRel::getTaskInfoId, taskId));
        if(StringUtils.isNotBlank(orgId)){
            String[] orgIds = orgId.split( ",");
            for (String item : orgIds){
                YTSafetyTaskInfoOrgRel orgRel = new YTSafetyTaskInfoOrgRel();
                orgRel.setTaskInfoId(taskId);
                orgRel.setOrgId(Long.valueOf(item));
                orgRel.setOrgName("");
                taskInfoOrgRelMapper.insert(orgRel);
            }
        }
    }

    private void validateTaskConfig(YTSafetyTaskInfoDTO dto) {
        LoginUser loginUser = BusUtits.getLoginUser();
        YtsafetyTaskInfoExtensionDTO taskInfoExt = dto.getTaskInfoExtension();
        if (taskInfoExt.getExecType().isEmpty()) {
            throw new IllegalArgumentException("执行类型不能为空");
        }
        String execType = taskInfoExt.getExecType();
        Integer taskFrequency = taskInfoExt.getTaskFrequency();
        String executionDate = taskInfoExt.getExecutionDate();
        Integer cycleType = taskInfoExt.getCycleType();
        String taskLevel = taskInfoExt.getTaskLevel();
        String inspectionCycle = dto.getInspectionCycle();

        if(dto.getTaskType().equals(CustomConstant.TaskType.XCRW)){

            if (taskInfoExt.getTaskLevel().isEmpty()) {
                throw new IllegalArgumentException("任务层级不能为空");
            }

            String riskLevel = taskInfoExt.getRiskLevel();
            if (riskLevel == null || riskLevel.isEmpty()) {
                throw new IllegalArgumentException("风险等级不能为空");
            }
//            List<String> riskGradingStandard = baseMapper.getRiskGradingStandard(riskLevel.split( ","));
            List<String> riskGradingStandard = Arrays.asList(riskLevel.split(","));

            if(CustomConstant.TaskLevel.COMPANY.equals(taskLevel)){//如果是公司
                if(CustomConstant.ExecType.CUSTOM.equals(execType)){//如果是自定义
                    double v = (taskFrequency * 28.0) / cycleType;
                    if (v < 1) {
                        throw new IllegalArgumentException("任务层级为公司时,检查周期-最低频次标准每月1次当前为"+inspectionCycle);
                    }
                }
                if(!riskGradingStandard.contains("重大风险")){
                    throw new IllegalArgumentException("任务层级为公司时,风险等级需包含重大风险");
                }
            }else if(CustomConstant.TaskLevel.DEPARTMENT.equals(taskLevel)){// 部门
                if(CustomConstant.ExecType.MONTHLY.equals(execType)){//如果是月
                    if(executionDate.split(",").length<4){
                        throw new IllegalArgumentException("任务层级为部门时,检查周期-最低频次标准每周1次当前为"+inspectionCycle);
                    }
                }
                if(CustomConstant.ExecType.CUSTOM.equals(execType)){//如果是自定义
                    double v = (taskFrequency * 28.0) / cycleType;
                    if (v < 4) {
                        throw new IllegalArgumentException("任务层级为部门时,检查周期-最低频次标准每周1次当前为"+inspectionCycle);
                    }

                }
                if(!riskGradingStandard.contains("重大风险") || !riskGradingStandard.contains("较大风险")){
                    throw new IllegalArgumentException("任务层级为部门时,风险等级需包含重大风险、较大风险");
                }
            }else{
                if(CustomConstant.ExecType.WEEKLY.equals(execType)){//如果是周
                    if(executionDate.split(",").length<2){
                        throw new IllegalArgumentException("任务层级为单元/工段/班组时,检查周期-最低频次标准每周2次当前为"+inspectionCycle);
                    }
                }
                if(CustomConstant.ExecType.MONTHLY.equals(execType)){//如果是月
                    if(executionDate.split(",").length<8){
                        throw new IllegalArgumentException("任务层级为单元/工段/班组时,检查周期-最低频次标准每周2次当前为"+inspectionCycle);
                    }
                }
                if(CustomConstant.ExecType.CUSTOM.equals(execType)){//如果是自定义
                    double v = (taskFrequency * 28.0) / cycleType;
                    if (v < 8) {
                        throw new IllegalArgumentException("任务层级为单元/工段/班组时,检查周期-最低频次标准每周2次当前为"+inspectionCycle);
                    }
                }
                if(CustomConstant.TaskLevel.UNIT.equals(taskLevel)){
                    if(!riskGradingStandard.contains("重大风险") || !riskGradingStandard.contains("较大风险") || !riskGradingStandard.contains("一般风险")){
                        throw new IllegalArgumentException("任务层级为单元时,风险等级需包含重大、较大风险、一般风险");
                    }
                }else{
                    if(!riskGradingStandard.contains("重大风险") || !riskGradingStandard.contains("较大风险") || !riskGradingStandard.contains("一般风险") || !riskGradingStandard.contains("低风险")){
                        throw new IllegalArgumentException("任务层级为工段/班组时,风险等级需包含重大风险、较大风险、一般风险、低风险");
                    }
                }
            }
        }

        switch (execType) {
            case CustomConstant.ExecType.DAILY:
            case CustomConstant.ExecType.ONCE:
                if (!isPositiveInteger(taskFrequency)) {
                    throw new IllegalArgumentException("单次/按天执行时，任务频率必须为正整数");
                }
                break;
            case CustomConstant.ExecType.WEEKLY:
            case CustomConstant.ExecType.MONTHLY:
                if (executionDate == null || executionDate.isEmpty()) {
                    throw new IllegalArgumentException("按周/月执行时，必须指定执行日期");
                }
                break;

            case CustomConstant.ExecType.CUSTOM:
                if (!isPositiveInteger(cycleType)) {
                    throw new IllegalArgumentException("自定义执行时，周期类型必须为正整数");
                }
                if (!isPositiveInteger(taskFrequency)) {
                    throw new IllegalArgumentException("自定义执行时，任务频率必须为正整数");
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的执行类型：" + execType);
        }

    }

    private boolean isPositiveInteger(Integer value) {
        if (value == null || value <= 0) {
            return false;
        }
        return value == Math.floor(value);
    }

    @Override
    public List<YTSafetyTaskInfoExporVO> exportTaskList(HttpServletRequest request, YTSafetyTaskInfoDTO taskInfoDTO) {
        // 查询导出数据
        SafetySysRole safetySysRole = baseMapper.getSafetySysRole(safetyOfficer);

        List<Map> safetyDictItems = baseMapper.getDictItem();
        taskInfoDTO.setRoleCode(safetyOfficer);
        List<YTSafetyTaskInfoExporVO> results = baseMapper.queryExportList(taskInfoDTO);
        for (YTSafetyTaskInfoExporVO vo : results){
            if("1".equals(vo.getIssueType())){
                vo.setPersonnelName(safetySysRole.getRoleName());
            }else if("3".equals(vo.getIssueType())){
                vo.setPersonnelName(vo.getPersonnelPostName());
            }
            if("1".equals(vo.getExecScope())){
                vo.setExecScope("电脑");
            }else if("2".equals(vo.getExecScope())){
                vo.setExecScope("手机");
            }else{
                vo.setExecScope("手机,电脑");
            }
            if (vo.getTaskStatus().equals(CustomConstant.TaskStatus.PENDING)){
                vo.setTaskStatus("待执行");
            }else if (vo.getTaskStatus().equals(CustomConstant.TaskStatus.RUNNING)){
                vo.setTaskStatus("执行中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.EXPIRED)){
                vo.setTaskStatus("已过期");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.APPROVAL)){
                vo.setTaskStatus("审批中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.REJECT)){
                vo.setTaskStatus("已驳回");
            }

            if (vo.getDormantDate()!=null){
                vo.setTaskStatus("休眠中");
            }

            String taskType = vo.getTaskType();
            vo.setDetailUrl(exportUrl + "taskAssignment?mode=detail&taskType=" + taskType + "&taskId=" + vo.getId());
            if (vo.getTaskType().equals(CustomConstant.TaskType.XCRW)){
                vo.setTaskType("巡查任务");
            }else if (vo.getTaskType().equals(CustomConstant.TaskType.ZXRW)){
                vo.setTaskType("专项任务");
            }else if(vo.getTaskType().equals(CustomConstant.TaskType.DDXG)){
                vo.setTaskType("定点巡更任务");
            }else if(vo.getTaskType().equals(CustomConstant.TaskType.YWYZ)){
                vo.setTaskType("业务验证");
            }else if(vo.getTaskType().equals(CustomConstant.TaskType.GWYZ)){
                vo.setTaskType("岗位验证");
                if ("319".equals(vo.getPostType())){
                    vo.setPostType("监控岗位");
                }else if ("355".equals(vo.getPostType())){
                    vo.setPostType("现场岗位");
                }else {
                    vo.setPostType("模拟岗位");
                }
            }

            if(StringUtils.isNotBlank(vo.getRiskCategoryId())){
                if(vo.getRiskCategoryId().contains(",")){
                    vo.setRiskCategory("治安,消防");
                }else{
                    safetyDictItems.stream().filter(item -> String.valueOf(item.get("id")).equals(vo.getRiskCategoryId()))
                            .findFirst().ifPresent(item -> vo.setRiskCategory((String) item.get("itemName")));
                }
            }else{
                vo.setRiskCategory("-");
            }

        }
        return results;
    }

//    更新所有待执行任务为执行中
    @Override
    public void updateTaskStatusPening() {
        ytSafetyTaskInfoMapper.updateTaskStatusPening();
    }

//    更新所有结束日期小于等于当前时间的任务状态为已过期
    @Override
    public void updateTaskStatusExpired() {
        ytSafetyTaskInfoMapper.updateTaskStatusExpired();
    }


//    更新所有今天结束但未完成的任务状态为超期未完成
    @Override
    public void updateUserTaskExecutionStatus() {
        ytSafetyTaskInfoMapper.updateUserTaskExecutionStatus();
    }


    //任务下发
    @Override
    public void issueTasks(Long taskInfoId) {
        List<YTSafetyHoliday> holidayList = ytSafetyTaskInfoMapper.queryHolidayList();
        Set<String> holidaySet = new HashSet<>();        // day_type=1 的日期（法定节假日）
        Set<String> workdayOnWeekendSet = new HashSet<>(); // day_type=2 的日期（调休工作日）
        for (YTSafetyHoliday holiday : holidayList){
            String[] dates = holiday.getHolidayDate().split(",");
            if(1==holiday.getDayType()){
                holidaySet.addAll(Arrays.asList(dates));
            }else{
                workdayOnWeekendSet.addAll(Arrays.asList(dates));
            }
        }
        List<YTSafetyTaskInfoVO> taskList = baseMapper.taskAssignment(taskInfoId);
        for (YTSafetyTaskInfoVO vo : taskList){
            boolean flag = true;
            YtsafetyTaskInfoExtensionVO exVo = vo.getTaskInfoExtension();
            int taskFrequency = exVo.getTaskFrequency();
            if(CustomConstant.ExecType.MONTHLY.equals(exVo.getExecType())||CustomConstant.ExecType.WEEKLY.equals(exVo.getExecType())){
//               如果是月或者周不根据频率下发
                taskFrequency = 1;
            }
            List<Long> taskExecutionIds = new ArrayList<>();
            for(int i = 0; i < taskFrequency; i++){
                try {
                    if(CustomConstant.ExecType.DAILY.equals(exVo.getExecType())){
                        if (1 == exVo.getSkipWeekend() || 1 == exVo.getSkipHoliday()) {
                            String todayStr = sdf.format(new Date());

                            // 1. 法定节假日 → 跳过
                            if (1 == exVo.getSkipHoliday() && holidaySet.contains(todayStr)) {
                                continue;
                            }

                            // 2. 判断今天是不是周末
                            Calendar calendar = Calendar.getInstance();
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

                            // 3. 如果是周末，且不是调休工作日，且开启了跳过周末 → 跳过
                            if (1 == exVo.getSkipWeekend() && isWeekend && !workdayOnWeekendSet.contains(todayStr)) {
                                continue;
                            }
                        }
                    }
                    if("1".equals(exVo.getIssueType())){
                        YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
                        riskDataDTO.setRiskTypeId(vo.getRiskCategoryId());
                        riskDataDTO.setRiskLevel(exVo.getRiskLevel());
                        riskDataDTO.setFactoryId(exVo.getFactoryId());
                        riskDataDTO.setAreaId(exVo.getAreaId());
                        riskDataDTO.setOrgId(exVo.getOrgId());
                        riskDataDTO.setSafetyOfficer(safetyOfficer);
                        List<YtsafetyTaskUserVO> SafetyOfficerList = getTaskRiskDataSafetyOfficer(riskDataDTO);
                        for (YtsafetyTaskUserVO userVO : SafetyOfficerList){
                            YTSafetyTaskExecution taskExecution = new YTSafetyTaskExecution();
                            taskExecution.setExecUserId(userVO.getEmpNo()==null?String.valueOf(userVO.getUserId()):userVO.getEmpNo());
                            taskExecution.setExecUserName(userVO.getEmpName());
                            taskExecution.setTaskInfoId(vo.getId());
                            taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.RUNNING);
                            // 补全任务执行信息
                            completeTaskExecutionInfo(taskExecution, vo, exVo);
                            taskExecutionMapper.insert(taskExecution);
                            taskExecutionIds.add(taskExecution.getId());
                            // 岗位验证任务下发通知
//                            sendGwzyNotify(vo, userVO.getEmpNo());
                            //发送代办
                            sendEipToDo(taskExecution, vo, userVO.getEmpNo());
                            //发送宇信通
//                            sendMessage(vo, userVO.getEmpNo(), String.valueOf(taskExecution.getId()));
                        }

                    }else if("2".equals(exVo.getIssueType())){
                        if(StringUtils.isNotBlank(exVo.getPersonnelId())&&StringUtils.isNotBlank(exVo.getPersonnelName())){
                            String[] userId =exVo.getPersonnelId().split(",") ;
                            String[] userName = exVo.getPersonnelName().split( ",");
                            for (int j = 0; j < userId.length; j++){
                                YTSafetyTaskExecution taskExecution = new YTSafetyTaskExecution();
                                taskExecution.setExecUserId(userId[j]);
                                taskExecution.setExecUserName(userName[j]);
                                taskExecution.setTaskInfoId(vo.getId());
                                taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.RUNNING);
                                // 补全任务执行信息
                                completeTaskExecutionInfo(taskExecution, vo, exVo);
                                taskExecutionMapper.insert(taskExecution);
                                taskExecutionIds.add(taskExecution.getId());
                                // 岗位验证任务下发通知
                                sendGwzyNotify(vo, userId[j]);
                                //发送代办
                                sendEipToDo(taskExecution, vo, userId[j]);
                                //发送宇信通
//                                sendMessage(vo, userId[j], String.valueOf(taskExecution.getId()));
                            }
                        }
                    }else if("3".equals(exVo.getIssueType())){
                        TaskUserQueryDTO userQueryDTO = new TaskUserQueryDTO();
                        userQueryDTO.setFactoryId(exVo.getPersonnelFactoryId());
                        userQueryDTO.setOrgCode(exVo.getPersonnelOrgId());
                        userQueryDTO.setRoleCode(exVo.getPersonnelPostId());
                        List<YtsafetyTaskUserVO> taskUser = ytSafetyTaskInfoMapper.getTaskUser(userQueryDTO);
                        for (YtsafetyTaskUserVO userVO : taskUser){
                            YTSafetyTaskExecution taskExecution = new YTSafetyTaskExecution();
                            taskExecution.setExecUserId(userVO.getEmpNo()==null?String.valueOf(userVO.getUserId()):userVO.getEmpNo());
                            taskExecution.setExecUserName(userVO.getEmpName());
                            taskExecution.setTaskInfoId(vo.getId());
                            taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.RUNNING);
                            // 补全任务执行信息
                            completeTaskExecutionInfo(taskExecution, vo, exVo);
                            taskExecutionMapper.insert(taskExecution);
                            taskExecutionIds.add(taskExecution.getId());
                            // 岗位验证任务下发通知
                            sendGwzyNotify(vo, userVO.getEmpNo());
                            //发送代办
                            sendEipToDo(taskExecution, vo, userVO.getEmpNo());
                            //发送宇信通
//                            sendMessage(vo, userVO.getEmpNo(), String.valueOf(taskExecution.getId()));
                        }
                    }
                } catch (Exception e) {
                    log.error("任务下发异常：{}", e.getMessage());
                    flag = false;
                    break;
                }
            }
            if(!CustomConstant.ExecType.ONCE.equals(exVo.getExecType())){
                calculateAndSetCurrentExecutionCycle(vo);
            }
            String nextExecutionTime = exVo.getCurrentExecutionCycle();
            Calendar nowCal = Calendar.getInstance();
            LambdaQueryWrapper<YTSafetyTaskExecution> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(YTSafetyTaskExecution::getId, taskExecutionIds);
            YTSafetyTaskExecution taskExecution = new YTSafetyTaskExecution();
            //如果是按周执行,每周执行两次, 第一次的任务执行结束日期为下一次任务的前一天
            if(exVo.getExecType().equals(CustomConstant.ExecType.WEEKLY)){
                // 获取本周周一和周日
                nowCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                nowCal.set(Calendar.HOUR_OF_DAY, 0);
                nowCal.set(Calendar.MINUTE, 0);
                nowCal.set(Calendar.SECOND, 0);
                nowCal.set(Calendar.MILLISECOND, 0);
                Date weekMonday = nowCal.getTime();

                nowCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                nowCal.add(Calendar.DAY_OF_MONTH, 6);
                nowCal.set(Calendar.HOUR_OF_DAY, 23);
                nowCal.set(Calendar.MINUTE, 59);
                nowCal.set(Calendar.SECOND, 59);
                nowCal.set(Calendar.MILLISECOND, 999);
                Date weekSunday = nowCal.getTime();

                if (StringUtils.isNotBlank(nextExecutionTime)) {
                    try{
                        Date nextDate = sdf.parse(nextExecutionTime);
                        if (!nextDate.before(weekMonday) && !nextDate.after(weekSunday)) {
                            // nextExecutionTime 在本周内，取前一天
                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime(nextDate);
                            tempCal.add(Calendar.DAY_OF_MONTH, -1);
                            taskExecution.setEndDate(tempCal.getTime());
                        } else {
                            // 不在本周内，取本周周日
                            taskExecution.setEndDate(weekSunday);
                        }
                    }catch (Exception e){
                        log.error("日期解析异常：{}", e.getMessage());
                    }
                }else{
                    // nextExecutionTime 为空，取本周周日
                    taskExecution.setEndDate(weekSunday);
                }
                taskExecutionMapper.update(taskExecution, queryWrapper);
            }
            //如果是按月执行,每月执行一次,本次任务执行结束日期为下一次任务的前一天
            if(exVo.getExecType().equals(CustomConstant.ExecType.MONTHLY)){
                // 获取本月第一天和最后一天
                nowCal.set(Calendar.DAY_OF_MONTH, 1);
                nowCal.set(Calendar.HOUR_OF_DAY, 0);
                nowCal.set(Calendar.MINUTE, 0);
                nowCal.set(Calendar.SECOND, 0);
                nowCal.set(Calendar.MILLISECOND, 0);
                Date monthFirst = nowCal.getTime();

                nowCal.set(Calendar.DAY_OF_MONTH, nowCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                nowCal.set(Calendar.HOUR_OF_DAY, 23);
                nowCal.set(Calendar.MINUTE, 59);
                nowCal.set(Calendar.SECOND, 59);
                nowCal.set(Calendar.MILLISECOND, 999);
                Date monthLast = nowCal.getTime();

                if (StringUtils.isNotBlank(nextExecutionTime)) {
                    try{
                        Date nextDate = sdf.parse(nextExecutionTime);
                        if (!nextDate.before(monthFirst) && !nextDate.after(monthLast)) {
                            // nextExecutionTime 在本月内，取前一天
                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime(nextDate);
                            tempCal.add(Calendar.DAY_OF_MONTH, -1);
                            taskExecution.setEndDate(tempCal.getTime());
                        } else {
                            // 不在本月内，取本月28日
                            Calendar tempCal = Calendar.getInstance();
                            tempCal.set(Calendar.DAY_OF_MONTH, 28);
                            taskExecution.setEndDate(tempCal.getTime());
                        }
                    }catch (Exception e){
                        log.error("日期解析异常：{}", e.getMessage());
                    }
                }else{
                    // nextExecutionTime为空，取本月28日
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.set(Calendar.DAY_OF_MONTH, 28);
                    taskExecution.setEndDate(tempCal.getTime());
                }
                taskExecutionMapper.update(taskExecution, queryWrapper);
            }
            YtsafetyTaskInfoExtension exInfo = new YtsafetyTaskInfoExtension();
            exInfo.setId(exVo.getExtensionId());
            exInfo.setCurrentExecutionCycle(exVo.getCurrentExecutionCycle());
            if(!flag){
                exInfo.setTaskStatus("fail");
            }else {
                exInfo.setTaskStatus("success");
            }
            taskInfoExecutionMapper.updateById(exInfo);
        }
    }

    public void sendEipToDo(YTSafetyTaskExecution taskExecution, YTSafetyTaskInfoVO taskVO,String empNo){
        try{
            EipSendTodoArg arg =new EipSendTodoArg();
            String id = String.valueOf(taskExecution.getId());
            String taskType = taskVO.getTaskType();
            String link = "";
            String createEmpNo = taskVO.getCreateUser();
            String others = "1";
//            if("2".equals(taskVO.getExecScope())){
//                link = exportUrl +"nullDetails";
//            }else{
                if(CustomConstant.TaskType.ZXRW.equals(taskType)){
                    link = toDoUrl+"?sysId="+bpmSysId+"&modelId=ytsafety_zx&templateFormId=ytsafety_db&BPM_NotifyTodoId=a&eip=true&isCompleted=0&taskId="+taskExecution.getId()+"&id="+taskExecution.getId();
                }else if(CustomConstant.TaskType.XCRW.equals(taskType)){
                    link = toDoUrl+"?sysId="+bpmSysId+"&modelId=ytsafety_xc&templateFormId=ytsafety_db&BPM_NotifyTodoId=a&eip=true&isCompleted=0&taskId="+taskExecution.getId()+"&id="+taskExecution.getId();
                }else if(CustomConstant.TaskType.GWYZ.equals(taskType) || CustomConstant.TaskType.YWYZ.equals(taskType)){
                    String templateFormId;
                    String modelId;
                    if ("355".equals(taskVO.getPostType())){
                        modelId = "spotxc";
                        templateFormId = "spot";
                    }else if ("356".equals(taskVO.getPostType())){
                        modelId = "simulate";
                        templateFormId = "db";
                    }else {
                        //业务验证
                        modelId = "yw";
                        templateFormId = "db";
                    }
                    link = toDoUrl+"?sysId="+bpmSysId+"&modelId=ytsafety_"+modelId+"&templateFormId=ytsafety_"+templateFormId+"&BPM_NotifyTodoId=a&eip=true&isCompleted=0&taskId="+taskExecution.getId()+"&id="+taskExecution.getId()+"&postType="+taskVO.getPostType();
                    if ("319".equals(taskVO.getPostType())){
                        //为监控岗位上报时，加上锁只能在pc上报
                        link = exportUrl + "taskreport?activeTab=monitor&taskId="+taskExecution.getId();
                        others = "0";
                    }
                }else {
                    return;
                }

            arg.setLink(link);
            arg.setModelId(id);
            arg.setCreateTime(DateUtils.formatDateTime());
            arg.setDocCreator(EipXmlUtil.docCreator(createEmpNo));
            arg.setTargets(EipXmlUtil.getTaragets(Arrays.asList(new String[]{empNo})));
            arg.setModelName("安消管理平台");
            arg.setAppName("安消管理平台");
            arg.setSubject("您有一条"+taskVO.getCreateName()+"创建的任务待完成!任务名称:"+taskVO.getTaskName());
            arg.setType(1);
            arg.setOthers("{\"isOpenInMobile\":\""+others+"\"}");
            SendTodoReturn xml = eipDoneService.createEipTodo(arg);
            log.info("发送待办任务:{}",xml);

        }catch (Exception e){
            log.error("发送待办任务异常",e);
        }
    }

    public void sendMessage(YTSafetyTaskInfoVO taskVO,String empNo,String id) {
        try{
            Map<String, String> input = new HashMap<>();
            String link = "";
            if(CustomConstant.TaskType.ZXRW.equals(taskVO.getTaskType())){
                link = exportUrl + "specialDetail?isCompleted=0&taskId=" + id;
            }else if(CustomConstant.TaskType.XCRW.equals(taskVO.getTaskType())){
                link = exportUrl + "patrolDetail?isCompleted=0&taskId=" + id;
//            }else if(CustomConstant.TaskType.GWYZ.equals(taskVO.getTaskType())){
//                String PostType = taskVO.getPostType();
//                link = exportUrl + "taskreport";
            }else{
                return;
            }
            YtsafetyApiRouteConfig config = apiRouteConfigService.queryApiRouteConfigByCode(yxtCode);
            String content = "您有一条"+taskVO.getCreateName()+"创建的任务待完成!任务名称:"+taskVO.getTaskName()+"点击跳转:"+link;
            Map<String, String> params = new HashMap<>();
            params.put("content", content);
            params.put("from", "220122");
            params.put("to", empNo);
            String run = yxtRebotService.run(config, params);
            log.info("发送宇信通:{}",run);
            log.info("宇信通消息发送完成: empNo={}", empNo);
        }catch (Exception e){
            log.error("宇信通消息发送失败: empNo={}", empNo, e);
        }
    }

    @Override
    public YTSafetyXcrwTaskExecutionDetailVO queryTaskExecutionDetailPc(String id) {

        YTSafetyXcrwTaskExecutionDetailVO taskExecutionDetailVO = new YTSafetyXcrwTaskExecutionDetailVO();
        YTSafetyTaskExecution ytsafetyTaskExtension =  taskExecutionMapper.selectById(id);
        String taskInfoId = String.valueOf(ytsafetyTaskExtension.getTaskInfoId());
        YTSafetyTaskInfo taskInfo = baseMapper.selectById(taskInfoId);
        LambdaQueryWrapper<YtsafetyTaskInfoExtension> query = new LambdaQueryWrapper<>();
        query.eq(YtsafetyTaskInfoExtension::getTaskInfoId, taskInfoId);
        YtsafetyTaskInfoExtension taskInfoExtension = taskInfoExecutionMapper.selectOne(query);
        taskExecutionDetailVO.setTaskName(taskInfo.getTaskName());
        taskExecutionDetailVO.setTaskType(getTaskType(taskInfo.getTaskType()));
        taskExecutionDetailVO.setEndDate(ytsafetyTaskExtension.getEndDate());
        taskExecutionDetailVO.setTaskDescription(taskInfo.getTaskDescription());
        taskExecutionDetailVO.setTaskStartTime(ytsafetyTaskExtension.getTaskStartTime());
        taskExecutionDetailVO.setTaskCompleteTime(ytsafetyTaskExtension.getTaskCompleteTime());
        taskExecutionDetailVO.setTaskStatus(ytsafetyTaskExtension.getTaskStatus());
        taskExecutionDetailVO.setExecScope(taskInfo.getExecScope());

        YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
        riskDataDTO.setAreaId(taskInfoExtension.getAreaId());
        riskDataDTO.setFactoryId(taskInfoExtension.getFactoryId());
        if(taskInfoExtension.getIssueType().equals("1")){
            riskDataDTO.setOrgId(ytsafetyTaskExtension.getExecUserId());//取当前用户部门
        }
        riskDataDTO.setRiskTypeId(taskInfo.getRiskCategoryId());
        riskDataDTO.setRiskLevel(taskInfoExtension.getRiskLevel());
        List<YtsafetyTaskRiskDataVO> riskDataVOList =ytSafetyTaskInfoMapper.queryTaskRiskDataList(riskDataDTO);

        //完成任务的图片
        String attachmentImgId = ytsafetyTaskExtension.getAttachmentImgId();
        if(StringUtils.isNotBlank(attachmentImgId)){
            taskExecutionDetailVO.setAttachmentImg(fileService.getById(attachmentImgId));
        }

        Map<Long, List<YtsafetyTaskRiskDataVO>> areaGroupMap = riskDataVOList.stream()
                .collect(Collectors.groupingBy(YtsafetyTaskRiskDataVO::getAreaId));

        // 构建任务范围列表
        List<YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork> scopeOfWorks = new ArrayList<>();
        for (Map.Entry<Long, List<YtsafetyTaskRiskDataVO>> entry : areaGroupMap.entrySet()) {
            List<YtsafetyTaskRiskDataVO> areaRiskList = entry.getValue();

            for (YtsafetyTaskRiskDataVO riskData : areaRiskList) {
                YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork scopeOfWork = new YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork();
                scopeOfWork.setFactoryName(riskData.getFactoryName());
                scopeOfWork.setAreaName(riskData.getAreaName());
                scopeOfWork.setLocationName(riskData.getLocationName());
                scopeOfWork.setRiskFactor(riskData.getRiskFactorName());
                scopeOfWork.setRiskCategoryId(riskData.getRiskCategoryId());
                scopeOfWork.setRiskCategoryName(riskData.getRiskTypeName());
                scopeOfWork.setRiskIdentifyType(riskData.getRiskIdentifyName());
                scopeOfWork.setRiskDescription(riskData.getRiskDescription());
                scopeOfWork.setPreventMeasure(riskData.getPreventMeasure());
                scopeOfWork.setRiskLevel(riskData.getRiskLevel());
                scopeOfWorks.add(scopeOfWork);
            }

        }
        taskExecutionDetailVO.setScopeOfWorks(scopeOfWorks);
        // 设置区域数量和位置点位数量
        long areaNum = areaGroupMap.size();
        long locationNum = riskDataVOList.stream()
                .map(YtsafetyTaskRiskDataVO::getLocationId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        taskExecutionDetailVO.setAreaNum(String.valueOf(areaNum));
        taskExecutionDetailVO.setLocationNum(String.valueOf(locationNum));

        return taskExecutionDetailVO;
    }

    @Override
    public YTSafetyXcrwTaskExecutionDetailVO queryTaskExecutionDetailApp(String id) {
        YTSafetyXcrwTaskExecutionDetailVO taskExecutionDetailVO = new YTSafetyXcrwTaskExecutionDetailVO();
        YTSafetyTaskExecution ytsafetyTaskExtension =  taskExecutionMapper.selectById(id);
        String taskInfoId = String.valueOf(ytsafetyTaskExtension.getTaskInfoId());
        YTSafetyTaskInfo taskInfo = baseMapper.selectById(taskInfoId);
        LambdaQueryWrapper<YtsafetyTaskInfoExtension> query = new LambdaQueryWrapper<>();
        query.eq(YtsafetyTaskInfoExtension::getTaskInfoId, taskInfoId);
        YtsafetyTaskInfoExtension taskInfoExtension = taskInfoExecutionMapper.selectOne(query);
        taskExecutionDetailVO.setTaskName(taskInfo.getTaskName());
        taskExecutionDetailVO.setTaskType(getTaskType(taskInfo.getTaskType()));
        taskExecutionDetailVO.setEndDate(ytsafetyTaskExtension.getEndDate());
        taskExecutionDetailVO.setTaskDescription(taskInfo.getTaskDescription());
        taskExecutionDetailVO.setTaskStartTime(ytsafetyTaskExtension.getTaskStartTime());
        taskExecutionDetailVO.setTaskCompleteTime(ytsafetyTaskExtension.getTaskCompleteTime());
        taskExecutionDetailVO.setTaskStatus(ytsafetyTaskExtension.getTaskStatus());
        taskExecutionDetailVO.setExecScope(taskInfo.getExecScope());
        //完成任务的图片
        String attachmentImgId = ytsafetyTaskExtension.getAttachmentImgId();
        if(StringUtils.isNotBlank(attachmentImgId)){
            taskExecutionDetailVO.setAttachmentImg(fileService.getById(attachmentImgId));
        }
        YtsafetyTaskRiskDataDTO riskDataDTO = new YtsafetyTaskRiskDataDTO();
        riskDataDTO.setAreaId(taskInfoExtension.getAreaId());
        riskDataDTO.setFactoryId(taskInfoExtension.getFactoryId());
        if(taskInfoExtension.getIssueType().equals("1")){//只有默认风险安全员的时候  根据对应的安全员部门查询对应的风险数据
            riskDataDTO.setOrgId(ytsafetyTaskExtension.getExecUserId());//取当前用户部门
        }
        riskDataDTO.setRiskTypeId(taskInfo.getRiskCategoryId());
        riskDataDTO.setRiskLevel(taskInfoExtension.getRiskLevel());

        List<YtsafetyTaskRiskDataVO> riskDataVOList =ytSafetyTaskInfoMapper.queryTaskRiskDataList(riskDataDTO);

        Map<Long, List<YtsafetyTaskRiskDataVO>> areaGroupMap = riskDataVOList.stream()
                .collect(Collectors.groupingBy(YtsafetyTaskRiskDataVO::getAreaId));

        List<YTSafetyXcrwTaskExecutionDetailVO.areaData> areaDatasList = new ArrayList<>();

        for (Map.Entry<Long, List<YtsafetyTaskRiskDataVO>> entry : areaGroupMap.entrySet()) {
            List<YtsafetyTaskRiskDataVO> areaRiskList = entry.getValue();

            YTSafetyXcrwTaskExecutionDetailVO.areaData areaData = new YTSafetyXcrwTaskExecutionDetailVO.areaData();

            if (!areaRiskList.isEmpty()) {
                areaData.setAreaName(areaRiskList.get(0).getAreaName());
            }

            List<YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork> scopeOfWorks = new ArrayList<>();
            for (YtsafetyTaskRiskDataVO riskData : areaRiskList) {
                YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork scopeOfWork = new YTSafetyXcrwTaskExecutionDetailVO.scopeOfWork();
                scopeOfWork.setFactoryName(riskData.getFactoryName());
                scopeOfWork.setAreaName(riskData.getAreaName());
                scopeOfWork.setLocationName(riskData.getLocationName());
                scopeOfWork.setRiskFactor(riskData.getRiskFactorName());
                scopeOfWork.setRiskCategoryId(riskData.getRiskCategoryId());
                scopeOfWork.setRiskCategoryName(riskData.getRiskTypeName());
                scopeOfWork.setRiskIdentifyType(riskData.getRiskIdentifyName());
                scopeOfWork.setRiskDescription(riskData.getRiskDescription());
                scopeOfWork.setPreventMeasure(riskData.getPreventMeasure());
                scopeOfWork.setRiskLevel(riskData.getRiskLevel());
                scopeOfWorks.add(scopeOfWork);
            }

            areaData.setScopeOfWorks(scopeOfWorks);
            areaDatasList.add(areaData);
        }
        taskExecutionDetailVO.setAreaDatas(areaDatasList);
        // 设置区域数量和位置点位数量
        long areaNum = areaGroupMap.size();
        long locationNum = riskDataVOList.stream()
                .map(YtsafetyTaskRiskDataVO::getLocationId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        taskExecutionDetailVO.setAreaNum(String.valueOf(areaNum));
        taskExecutionDetailVO.setLocationNum(String.valueOf(locationNum));

        return taskExecutionDetailVO;
    }

    @Override
    public YTSafetyZxrwTaskExecutionDetailVO queryTaskZxrwExecutionDetailPc(String id) {
        LoginUser user = BusUtits.getLoginUser();
        YTSafetyTaskExecution ytsafetyTaskInfoExtension =  taskExecutionMapper.selectById(id);
        String taskInfoId = String.valueOf(ytsafetyTaskInfoExtension.getTaskInfoId());
        YTSafetyTaskInfo taskInfo = baseMapper.selectById(taskInfoId);
        LambdaQueryWrapper<YtsafetyTaskInfoExtension> query = new LambdaQueryWrapper<>();
        query.eq(YtsafetyTaskInfoExtension::getTaskInfoId, taskInfoId);
        YtsafetyTaskInfoExtension taskInfoExtension = taskInfoExecutionMapper.selectOne(query);

        YTSafetyZxrwTaskExecutionDetailVO zxrwDetailVO = new YTSafetyZxrwTaskExecutionDetailVO();
        zxrwDetailVO.setTaskStatus(ytsafetyTaskInfoExtension.getTaskStatus());
        zxrwDetailVO.setTaskName(taskInfo.getTaskName());
        zxrwDetailVO.setTaskType(getTaskType(taskInfo.getTaskType()));
        zxrwDetailVO.setEndDate(ytsafetyTaskInfoExtension.getEndDate());
        zxrwDetailVO.setTaskDescription(taskInfo.getTaskDescription());
        zxrwDetailVO.setPhotoMust(taskInfoExtension.getPhotoMust());
        zxrwDetailVO.setFileMust(taskInfoExtension.getFileMust());
        zxrwDetailVO.setExecScope(taskInfo.getExecScope());
        zxrwDetailVO.setModelFlag(taskInfo.getModelFlag());
        zxrwDetailVO.setFlowCode(taskInfo.getFlowCode());
        zxrwDetailVO.setFlowSubject(taskInfo.getFlowSubject());
        zxrwDetailVO.setIssueApproval(taskInfo.getIssueApproval());
        zxrwDetailVO.setApprovalUser(taskInfo.getApprovalUser());
        zxrwDetailVO.setApprovalName(taskInfo.getApprovalName());
        if(CustomConstant.TaskStatus.RUNNING.equals(ytsafetyTaskInfoExtension.getTaskStatus()) ){
            zxrwDetailVO.setIsCanEdit(true);
            zxrwDetailVO.setIsCanApprove(false);
        }else{
            BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(Long.valueOf(id),user.getEipName());
            zxrwDetailVO.setFlowStatus(detailsVO.getFlowStatus());
            zxrwDetailVO.setIsCanEdit(detailsVO.getIsCanEdit());
            zxrwDetailVO.setIsCanApprove(detailsVO.getIsCanApprove());
        }


        String attachmentId = ytsafetyTaskInfoExtension.getAttachmentId();
        String attachmentImgId = ytsafetyTaskInfoExtension.getAttachmentImgId();
        String taskAttachmentId = taskInfo.getAttachmentId();
        String taskAttachmentImgId = taskInfo.getAttachmentImgId();

        if(StringUtils.isNotBlank(taskAttachmentId)){
            zxrwDetailVO.setTaskAttachmentList(resolveFileList(taskAttachmentId));
        }
        if(StringUtils.isNotBlank(taskAttachmentImgId)){
            zxrwDetailVO.setTaskAttachmentImgList(resolveFileList(taskAttachmentImgId));
        }
        if(StringUtils.isNotBlank(attachmentId)){
            zxrwDetailVO.setAttachmentList(resolveFileList(attachmentId));
        }
        if(StringUtils.isNotBlank(attachmentImgId)){
            zxrwDetailVO.setAttachmentImgList(resolveFileList(attachmentImgId));
        }

        return zxrwDetailVO;
    }


    private List<YTSafetyFileInfo> resolveFileList(String fileIds) {
        if (StringUtils.isBlank(fileIds)) {
            return Collections.emptyList();
        }
        List<String> idList = Arrays.stream(fileIds.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        return fileService.listByIds(idList);
    }

    @Override
    public YTSafetyMyTaskVO queryMyTaskList(String execScope,String startDate,String endDate,Integer isCompleted, Integer pageNo,Integer pageSize) {
        LoginUser loginUser = BusUtits.getLoginUser();
        YTSafetyMyTaskVO taskExecutionVO = new YTSafetyMyTaskVO();
        if (loginUser != null) {
            String empNo = loginUser.getEmpNo();
            String userId = String.valueOf(loginUser.getUserId());
            Page<YTSafetyMyTaskInfoVO> page = new Page<>(pageNo, pageSize);
            IPage<YTSafetyMyTaskInfoVO> myTaskList = baseMapper.queryMyTaskList(page,userId, empNo,execScope,isCompleted,startDate,endDate);
            Map resulrMap = baseMapper.getCompletedQuantity(userId, empNo,execScope,startDate,endDate);
            taskExecutionVO.setTaskInfoList(myTaskList);
            taskExecutionVO.setCompletedQuantity(Integer.valueOf(resulrMap.get("completedCount").toString()));
            taskExecutionVO.setTaskTotal(Integer.valueOf(resulrMap.get("totalCount").toString()));
        }
        return taskExecutionVO;
    }

    @Override
    public void zxrwEndTask(String id, String fileIds, String photoFileds,String exports) {
        LoginUser loginUser = BusUtits.getLoginUser();
        YTSafetyTaskExecution taskExecution = taskExecutionMapper.selectById( id);
        String taskInfoId = String.valueOf(taskExecution.getTaskInfoId());
        YTSafetyTaskInfo taskInfo = baseMapper.selectById(taskInfoId);

        Date endDate = new Date();
        taskExecution.setTaskStartTime(endDate);
//        taskExecution.setTaskCompleteTime(endDate);
        taskExecution.setAttachmentId(fileIds);
        taskExecution.setAttachmentImgId(photoFileds);

        LocalDate endDateOnly = endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate deadlineOnly = taskExecution.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String approval = null;
        String taskTaskStatus = taskExecution.getTaskStatus();
        if("1".equals(taskInfo.getIssueApproval())){
//          不需要审批
//            if (endDateOnly.isAfter(deadlineOnly)) {
//                taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED);
//            } else {
//                taskExecution.setTaskStatus(CustomConstant.TaskExecutionStatus.COMPLETED);
//            }
            approval = loginUser.getEipName();
            taskExecution.setApprovalUser(approval);
            taskExecutionMapper.updateById(taskExecution);
        }else if( "2".equals(taskInfo.getIssueApproval())){
            //任务发起人审批
            taskExecution.setTaskStatus(CustomConstant.TaskStatus.APPROVAL);
            taskExecution.setApprovalUser(taskInfo.getApprovalUser());
            taskExecutionMapper.updateById(taskExecution);
            approval = taskInfo.getApprovalUser();
        }else if( "3".equals(taskInfo.getIssueApproval())){//消防员审批
            List<String> approvalUserIds = baseMapper.approvefirefighters(String.valueOf(loginUser.getUserId()), firefighter);
            if(approvalUserIds.isEmpty()){
                throw new IllegalArgumentException("没有可审批的消防员!");
            }
            approval = StringUtils.join(approvalUserIds, ",");
            taskExecution.setTaskStatus(CustomConstant.TaskStatus.APPROVAL);
            taskExecution.setApprovalUser(approval);
            taskExecutionMapper.updateById(taskExecution);
        }else if( "4".equals(taskInfo.getIssueApproval())){//自定义人员审批
            taskExecution.setTaskStatus(CustomConstant.TaskStatus.APPROVAL);
            taskExecution.setApprovalUser(taskInfo.getApprovalUser());
            taskExecutionMapper.updateById(taskExecution);
            approval = taskInfo.getApprovalUser();
        }

        if(!taskTaskStatus.equals(CustomConstant.TaskStatus.REJECT)){
            submitProcess(taskInfo,taskExecution.getId(),approval);
        }

        if(taskTaskStatus.equals(CustomConstant.TaskStatus.REJECT) && !"pc".equals(exports)){
            NodeInfo node = safetySummaryProcessService.getCurrentNodeInfo(taskExecution.getId());
            BpmProcessParamDTO processParamDTO = new BpmProcessParamDTO();
            processParamDTO.setTaskId(node.getWorkitems().get(0).getTaskId());
            processParamDTO.setActivityType("draftWorkitem");
            processParamDTO.setFormInstanceId(taskExecution.getId());
            processParamDTO.setOperationType("drafter_submit");
            Operation operation = new Operation();
            operation.setNotifyType("{}");
            operation.setAuditNote("");
            operation.setNotifyOnFinish(true);
            processParamDTO.setParam(operation);
            LoginUser user = BusUtits.getLoginUser();
            processParamDTO.setLoginName(user.getEipName());
            processParamDTO.setNodeHandler(user.getEipName());
            processParamDTO.setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
            safetySummaryProcessService.executeProcess(processParamDTO);
        }

//        设置EIP代办为已办
        EipSetTodoDoneArg eipSetTodoDoneArg = new EipSetTodoDoneArg();
        eipSetTodoDoneArg.setAppName("安消管理平台");
        eipSetTodoDoneArg.setModelId(String.valueOf(taskExecution.getId()));
        eipSetTodoDoneArg.setType(1);
        eipSetTodoDoneArg.setModelName("安消管理平台");
        eipDoneService.eipToDoDone(eipSetTodoDoneArg);
    }

    private void submitProcess(YTSafetyTaskInfo taskInfo,Long id,String approval){
        LoginUser user = BusUtits.getLoginUser();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(taskInfo.getModelFlag());
        dto.setFormInstanceId(id);
        dto.setFlowCode(taskInfo.getFlowCode());
        dto.setFlowSubject(taskInfo.getFlowSubject());
        dto.setCurNodeHandler(approval);
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setLoginName(user.getEipName());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setPreNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        dto.setFlowCode(taskInfo.getFlowCode());
        safetySummaryProcessService.createAndApproveProcess(dto);
    }

    public String getTaskType(String taskType) {

        if(CustomConstant.TaskType.ZXRW.equals(taskType)){
            return "专项任务";
        }else if(CustomConstant.TaskType.DDXG.equals(taskType)){
            return "定点巡更任务";
        }else if(CustomConstant.TaskType.YWYZ.equals(taskType)){
            return "业务验证";
        }else if(CustomConstant.TaskType.GWYZ.equals(taskType)){
            return "岗位验证";
        }else if(CustomConstant.TaskType.XCRW.equals(taskType)){
            return "巡查任务";
        }
        return "";
    }

    /**
     * 岗位验证任务下发后发送通知（邮件+宇信通）
     * 参考旧项目 SafetyPatrolPlanHandler.taskPositionMes
     *
     * @param taskVO 任务信息
     * @param empNo  接收人员工号
     */
    private void sendGwzyNotify(YTSafetyTaskInfoVO taskVO, String empNo) {
        // 仅岗位验证类型走此逻辑
        if (!CustomConstant.TaskType.GWYZ.equals(taskVO.getTaskType())) {
            return;
        }
        if (StringUtils.isBlank(empNo)) {
            return;
        }
        try {
            // 获取员工邮箱
            SafetyEmployeeMessage employee = employeeMessageService.getByNo(empNo);
            String email = (employee != null) ? employee.getEmail() : null;

            // 构建消息内容
            String msgBody = buildGwzyMsgBody(taskVO);
            String mailBody = buildGwzyMailBody(taskVO);

            // 发送宇信通消息
            sendYxt(empNo, msgBody);

            // 发送邮件
            if (StringUtils.isNotBlank(email)) {
                sendGwzyMail(email, mailBody);
            }
        } catch (Exception e) {
            log.error("岗位验证通知发送失败: taskId={}, empNo={}", taskVO.getId(), empNo, e);
        }
    }

    /**
     * 构建宇信通消息内容（纯文本，参考老项目 taskPositionMes）
     */
    private String buildGwzyMsgBody(YTSafetyTaskInfoVO taskVO) {
        YtsafetyTaskInfoExtensionVO exVo = taskVO.getTaskInfoExtension();
        String postName = StringUtils.isNotBlank(taskVO.getPersonnelPostName())
                ? taskVO.getPersonnelPostName() : "";
        String beginDate = taskVO.getStartDate() != null ? sdf.format(taskVO.getStartDate()) : "";
        String endDate = taskVO.getEndDate() != null ? sdf.format(taskVO.getEndDate()) : "";
        Integer cycleDays = getCycleDays(exVo);
        Integer frequency = exVo != null && exVo.getTaskFrequency() != null ? exVo.getTaskFrequency() : 1;

        StringBuilder sb = new StringBuilder();
        sb.append("您有一条任务待处理——").append("任务名称").append(taskVO.getTaskName());
        sb.append("，任务周期：").append(beginDate).append("-").append(endDate);
        sb.append("，").append(cycleDays).append("天").append(frequency).append("次，请使用电脑点击链接提交;");
        sb.append(exportUrl + "taskreport");
        return sb.toString();
    }

    /**
     * 构建邮件正文（HTML格式，参考老项目 taskPositionMes）
     */
    private String buildGwzyMailBody(YTSafetyTaskInfoVO taskVO) {
        YtsafetyTaskInfoExtensionVO exVo = taskVO.getTaskInfoExtension();
        String postName = StringUtils.isNotBlank(taskVO.getPersonnelPostName())
                ? taskVO.getPersonnelPostName() : "";
        String beginDate = taskVO.getStartDate() != null ? sdf.format(taskVO.getStartDate()) : "";
        String endDate = taskVO.getEndDate() != null ? sdf.format(taskVO.getEndDate()) : "";
        Integer cycleDays = getCycleDays(exVo);
        Integer frequency = exVo != null && exVo.getTaskFrequency() != null ? exVo.getTaskFrequency() : 1;

        StringBuilder sb = new StringBuilder();
        sb.append("您有一条任务待处理——").append("任务名称").append(taskVO.getTaskName());
        sb.append("，任务周期：").append(beginDate).append("-").append(endDate).append("，");
        if (cycleDays!=null && frequency!=null){
            sb.append(cycleDays).append("天").append(frequency).append("次,");
        }
        sb.append("请使用电脑点击链接提交;");
        sb.append("<a href='").append(exportUrl).append("taskreport").append("' target='_blank'>").append(exportUrl).append("taskreport").append("</a><br>");
        if (StringUtils.isNotBlank(taskVO.getTaskDescription())) {
            sb.append("任务详情描述：<br>").append(taskVO.getTaskDescription().replaceAll("\n", "<br>"));
        }
        return sb.toString();
    }

    /**
     * 根据执行类型计算周期天数
     */
    private Integer getCycleDays(YtsafetyTaskInfoExtensionVO exVo) {
        if (exVo == null || exVo.getExecType() == null) {
            return null;
        }
        switch (exVo.getExecType()) {
            case CustomConstant.ExecType.DAILY:
                return 1;
            case CustomConstant.ExecType.WEEKLY:
                return 7;
            case CustomConstant.ExecType.MONTHLY:
                return 30;
            case CustomConstant.ExecType.CUSTOM:
                return exVo.getCycleType() != null ? exVo.getCycleType() : 1;
            default:
                return 1;
        }
    }

    /**
     * 发送宇信通消息
     */
    private void sendYxt(String empNo, String msg) {
        try {
            YtsafetyApiRouteConfig config = apiRouteConfigService.queryApiRouteConfigByCode(yxtCode);
            Map<String, String> params = new HashMap<>();
            params.put("content", msg);
            params.put("from", robotId);
            params.put("to", empNo);
            yxtRebotService.run(config, params);
            log.info("宇信通消息发送完成: empNo={}", empNo);
        } catch (Exception e) {
            log.error("宇信通消息发送失败: empNo={}", empNo, e);
        }
    }

    /**
     * 发送邮件
     */
    private void sendGwzyMail(String toEmail, String htmlContent) {
        try {
            Map<String, String> headParam = new HashMap<>();
            headParam.put("Content-Type", "application/json");
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("emailConfigId", templateCode);
            bodyParam.put("title", "治安巡查岗位上报提醒");
            bodyParam.put("content", htmlContent);
            bodyParam.put("senderFrom", "zagl@yutong.com");
            bodyParam.put("recipient", toEmail);
            bodyParam.put("sender", "治安管理邮箱");
            bodyParam.put("copier", "");
            bodyParam.put("bcopier", "");
            ytWebserviceUtil.postJsonStr(mailUrl, bodyParam.toString(), headParam);
            log.info("邮件发送完成: to={}", toEmail);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}", toEmail, e);
        }
    }

    /**
     * 测试发送岗位验证通知（测试接口用，无参，自动查一个岗位验证任务）
     */
    public void testSendGwzyNotify(String emp) {
        LambdaQueryWrapper<YTSafetyTaskInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YTSafetyTaskInfo::getTaskType, CustomConstant.TaskType.GWYZ);
        wrapper.last("LIMIT 1");
        YTSafetyTaskInfo taskInfo = ytSafetyTaskInfoMapper.selectOne(wrapper);
        if (taskInfo == null) {
            log.error("测试岗位验证通知-未找到岗位验证类型的任务");
            return;
        }
        YTSafetyTaskInfoVO taskVO = queryById(taskInfo.getId());
        if (taskVO == null) {
            log.error("测试岗位验证通知-任务详情查询为空: taskId={}", taskInfo.getId());
            return;
        }
        sendGwzyNotify(taskVO, emp);
    }

    @Override
    public void updateTaskExecutionIsAbnormal(Long taskExecutionId) {
        baseMapper.updateTaskExecutionIsAbnormal(taskExecutionId);
    }

    @Override
    public List<YtsafetyTaskUserVO> getTaskRiskDataSafetyOfficer(YtsafetyTaskRiskDataDTO riskDataDTO) {
        List<String> riskDataVOList =  ytSafetyTaskInfoMapper.queryTaskRiskOrgCodeList(riskDataDTO);
        if (riskDataVOList == null || riskDataVOList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String[]> pairs = new ArrayList<>();
        for (String orgCode : riskDataVOList) {
            pairs.add(new String[]{orgCode, safetyOfficer});
        }
        Map<String, List<YtsafetyTaskUserVO>> voMap = responsibleNameResolver.resolveNamesToUserVO(pairs);
        Set<Long> seenUserIds = new HashSet<>();
        List<YtsafetyTaskUserVO> result = new ArrayList<>();
        for (List<YtsafetyTaskUserVO> users : voMap.values()) {
            for (YtsafetyTaskUserVO user : users) {
                if (user.getUserId() != null && seenUserIds.add(user.getUserId())) {
                    result.add(user);
                }
            }
        }
        return result;
    }

    public void calculateAndSetCurrentExecutionCycle(YTSafetyTaskInfoVO taskVO) {
        YTSafetyTaskInfoDTO taskInfoDTO = new YTSafetyTaskInfoDTO();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        taskInfoDTO.setStartDate(calendar.getTime());

        taskInfoDTO.setEndDate(taskVO.getEndDate());
        YtsafetyTaskInfoExtensionDTO ytsafetyTaskInfoExtensionDTO = new YtsafetyTaskInfoExtensionDTO();

        ytsafetyTaskInfoExtensionDTO.setExecType(taskVO.getTaskInfoExtension().getExecType());
        ytsafetyTaskInfoExtensionDTO.setCurrentExecutionCycle(taskVO.getTaskInfoExtension().getCurrentExecutionCycle());
        ytsafetyTaskInfoExtensionDTO.setExecutionDate(taskVO.getTaskInfoExtension().getExecutionDate());
        ytsafetyTaskInfoExtensionDTO.setCycleType(taskVO.getTaskInfoExtension().getCycleType());

        taskInfoDTO.setTaskInfoExtension(ytsafetyTaskInfoExtensionDTO);
        calculateAndSetCurrentExecutionCycle(taskInfoDTO,true);
        taskVO.getTaskInfoExtension().setCurrentExecutionCycle(taskInfoDTO.getTaskInfoExtension().getCurrentExecutionCycle());
        System.out.println(taskVO.getTaskInfoExtension().getCurrentExecutionCycle());
    }

    /**
     * 补全任务执行信息
     * @param taskExecution 任务执行对象
     * @param taskVO 任务台账VO
     * @param extensionVO 任务扩展信息VO
     */
    private void completeTaskExecutionInfo(YTSafetyTaskExecution taskExecution, YTSafetyTaskInfoVO taskVO, YtsafetyTaskInfoExtensionVO extensionVO) {
        // 基本信息
//        taskExecution.setId(IdWorker.getId());
        taskExecution.setTaskCode(ytSafetyWaterFlowNoService.getNowByTypeDay(taskVO.getTaskType(),null));
        taskExecution.setTaskName(taskVO.getTaskName());
        taskExecution.setApprovalUser(taskVO.getApprovalUser());
        // 所属区域信息
        if (StringUtils.isNotBlank(extensionVO.getFactoryId())) {
            taskExecution.setFactoryId(extensionVO.getFactoryId());
        }
        if (StringUtils.isNotBlank(extensionVO.getAreaId())) {
            taskExecution.setAreaId(extensionVO.getAreaId());
        }
        if (StringUtils.isNotBlank(extensionVO.getOrgId())) {
            taskExecution.setOrgId(extensionVO.getOrgId());
        }
        
        // 风险信息
        if (StringUtils.isNotBlank(extensionVO.getRiskLevel())) {
            taskExecution.setRiskLevel(extensionVO.getRiskLevel());
        }
        if (StringUtils.isNotBlank(taskVO.getRiskCategoryId())) {
            taskExecution.setRiskCategoryId(taskVO.getRiskCategoryId());
        }
        // 时间信息
        Calendar calendar = Calendar.getInstance();
        if(extensionVO.getExecType().equals(CustomConstant.ExecType.ONCE)){
            // 单次执行：开始和结束时间都是今天
            Date today = new Date();
            taskExecution.setStartDate(today);
            taskExecution.setEndDate(taskVO.getEndDate());
        }else if(extensionVO.getExecType().equals(CustomConstant.ExecType.DAILY)){
            // 按天执行：开始和结束时间都是今天
            Date today = new Date();
            taskExecution.setStartDate(today);
            taskExecution.setEndDate(today);
        }else if(extensionVO.getExecType().equals(CustomConstant.ExecType.WEEKLY)){
            Date startDate = taskVO.getStartDate();
            Date endDate = taskVO.getEndDate();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date monday = calendar.getTime();

//            calendar.add(Calendar.DAY_OF_WEEK, 6);
//            Date sunday = calendar.getTime();

            if(startDate != null && !startDate.before(monday)){
                taskExecution.setStartDate(startDate);
            }else{
                taskExecution.setStartDate(monday);
            }

//            if(endDate != null && !endDate.before(sunday)){
//                taskExecution.setEndDate(sunday);
//            }else{
//                taskExecution.setEndDate(endDate);
//            }

        }else if(extensionVO.getExecType().equals(CustomConstant.ExecType.MONTHLY)){
            // 按月执行：本月第一天到最后一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDayOfMonth = calendar.getTime();
            
//            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//            Date lastDayOfMonth = calendar.getTime();

            // startDate：取本月第一天 和 用户开始时间 的较大值（不早于月初）
            Date startDate = taskVO.getStartDate();
            if (startDate != null && startDate.after(firstDayOfMonth)) {
                taskExecution.setStartDate(startDate);  // 用户开始时间在本月内 → 保留
            } else {
                taskExecution.setStartDate(firstDayOfMonth); // null 或早于月初 → 用月初
            }

            // endDate：取本月最后一天 和 用户结束时间 的较小值（不晚于月末）
//            Date endDate = taskVO.getEndDate();
//            if (endDate != null && endDate.before(lastDayOfMonth)) {
//                taskExecution.setEndDate(endDate);  // 用户结束时间在本月内 → 保留
//            } else {
//                taskExecution.setEndDate(lastDayOfMonth); // null 或晚于月末 → 用月末
//            }
        }else if(extensionVO.getExecType().equals(CustomConstant.ExecType.CUSTOM)){
            // 自定义执行：从今天开始，到 cycleType 天后结束
            Date today = new Date();
            taskExecution.setStartDate(today);
            int cycleType = extensionVO.getCycleType();
            if(cycleType==1){//如果自定义 每天发一次   结束日期就是当天
                taskExecution.setEndDate(new Date());
            }else{
                cycleType = extensionVO.getCycleType()-1;//如果是2天发一次  结束日期就是明天的23点 所以计算结束日期 加周期-1天
                calendar.setTime(today);
                calendar.add(Calendar.DAY_OF_MONTH, cycleType);
                Date endDate = calendar.getTime();
                if(endDate.after(taskVO.getEndDate())){  // 如果 friday > 结束时间
                    taskExecution.setEndDate(taskVO.getEndDate()); // 用结束时间（较小的）
                }else{                                   // 如果 friday <= 结束时间
                    taskExecution.setEndDate(endDate);    // 用 friday（较小的）
                }
            }
        }
    }

    /**
     * 计算并设置最近一次执行日期
     * @param taskInfoDTO 任务信息DTO
     */
    private void calculateAndSetCurrentExecutionCycle(YTSafetyTaskInfoDTO taskInfoDTO,boolean flag) {
        if (taskInfoDTO.getTaskInfoExtension() == null) {
            return;
        }

        YtsafetyTaskInfoExtensionDTO extensionDTO = taskInfoDTO.getTaskInfoExtension();
        String execType = extensionDTO.getExecType();
        Date startDate = taskInfoDTO.getStartDate()==null?new Date():taskInfoDTO.getStartDate();
        Date endDate = taskInfoDTO.getEndDate();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);

        Calendar nowCal = Calendar.getInstance();
        nowCal.set(Calendar.HOUR_OF_DAY, 0);
        nowCal.set(Calendar.MINUTE, 0);
        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);

        Date nearestDate = null;

        try {
            switch (execType) {
                case CustomConstant.ExecType.DAILY:
                    if (startDate.after(nowCal.getTime())&& !flag) {
                        nearestDate = startDate;
                        break;
                    }
                    Date currDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currDate);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    if(!calendar.after(endCal)){
                        nearestDate = calendar.getTime();
                    }

                    break;

                case CustomConstant.ExecType.WEEKLY:
                    // 按周执行：executionDate 为 "一,二,三" 等，表示周一到周日
                    nearestDate = calculateWeeklyExecutionDate(extensionDTO.getExecutionDate(), startCal, endCal, nowCal);
                    break;

                case CustomConstant.ExecType.MONTHLY:
                    // 按月执行：executionDate 为 "1,2,3" 等，表示每月的日期
                    nearestDate = calculateMonthlyExecutionDate(extensionDTO.getExecutionDate(), startCal, endCal, nowCal);
                    break;

                case CustomConstant.ExecType.CUSTOM:
                    // 自定义执行：根据 cycleType 计算
                    if (startDate.after(nowCal.getTime()) && !flag) {
                        nearestDate = startDate;
                        break;
                    }
                    Integer cycleType = extensionDTO.getCycleType();
                    if (cycleType != null && cycleType > 0) {
                        nearestDate = calculateCustomExecutionDate(new Date(), cycleType, startCal, endCal);
                    }
                    break;
                default:
                    // 其他类型不处理
                    break;
            }
        } catch (Exception e) {
            log.error("计算执行日期失败", e);
        }

        // 设置计算结果
         extensionDTO.setCurrentExecutionCycle(nearestDate== null ? null : sdf.format(nearestDate));
        log.info("最近一次执行日期：" + extensionDTO.getCurrentExecutionCycle());
    }

    /**
     * 计算按周执行的最近日期
     * @param executionDate 执行日期字符串，如 "一,二,三"
     * @param startCal 开始时间
     * @param endCal 结束时间
     * @param nowCal 当前时间
     * @return 最近的执行日期
     */
    private Date calculateWeeklyExecutionDate(String executionDate, Calendar startCal, Calendar endCal, Calendar nowCal) {
        if (executionDate == null || executionDate.isEmpty()) {
            return null;
        }

        Map<String, Integer> WEEK_DAY_MAP = new HashMap<>();
        WEEK_DAY_MAP.put("1", Calendar.MONDAY);
        WEEK_DAY_MAP.put("2", Calendar.TUESDAY);
        WEEK_DAY_MAP.put("3", Calendar.WEDNESDAY);
        WEEK_DAY_MAP.put("4", Calendar.THURSDAY);
        WEEK_DAY_MAP.put("5", Calendar.FRIDAY);
        WEEK_DAY_MAP.put("6", Calendar.SATURDAY);
        WEEK_DAY_MAP.put("0", Calendar.SUNDAY);

        String[] days = executionDate.split(",");
        List<Integer> weekDays = new ArrayList<>();
        for (String day : days) {
            Integer calDay = WEEK_DAY_MAP.get(day.trim());
            if (calDay != null) {
                weekDays.add(calDay);
            }
        }

        if (weekDays.isEmpty()) {
            return null;
        }

        // 从今天和任务开始时间中较晚的一个开始查找（返回的日期必须大于当前日期）
        Calendar searchCal;
        if (nowCal.after(startCal)) {
            searchCal = (Calendar) nowCal.clone();
        } else {
            searchCal = (Calendar) startCal.clone();
        }

        // 最多搜索 7 天，找到第一个匹配的星期
        for (int i = 0; i < 7; i++) {
            int currentWeekDay = searchCal.get(Calendar.DAY_OF_WEEK);
            if (weekDays.contains(currentWeekDay)) {
                // 找到第一个匹配的星期，检查是否在任务时间范围内且不早于开始时间
                if (!searchCal.before(startCal) && !searchCal.after(endCal)) {
                    return searchCal.getTime();
                } else if (searchCal.after(endCal)) {
                    // 如果已经超过结束时间，返回 null
                    return null;
                }
            }
            searchCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return null;
    }

    /**
     * 计算按月执行的最近日期
     * @param executionDate 执行日期字符串，如 "1,2,3"
     * @param startCal 开始时间
     * @param endCal 结束时间
     * @param nowCal 当前时间
     * @return 最近的执行日期
     */
    private Date calculateMonthlyExecutionDate(String executionDate, Calendar startCal, Calendar endCal, Calendar nowCal) {
        if (executionDate == null || executionDate.isEmpty()) {
            return null;
        }

        // 解析 executionDate，获取月份中的日期列表
        String[] days = executionDate.split(",");
        List<Integer> monthDays = new ArrayList<>();
        for (String day : days) {
            try {
                int dayNum = Integer.parseInt(day.trim());
                if (dayNum >= 1 && dayNum <= 28) {
                    monthDays.add(dayNum);
                }
            } catch (NumberFormatException e) {
                // 忽略无效数据
            }
        }

        if (monthDays.isEmpty()) {
            return null;
        }

        // 对日期进行排序
        monthDays.sort(Integer::compareTo);

        // 从任务开始时间的月份开始，往后查找最近的匹配日期（最多搜索 12 个月）
        Calendar searchCal = (Calendar) startCal.clone();

        for (int monthOffset = 0; monthOffset < 12; monthOffset++) {
            Calendar tempCal = (Calendar) searchCal.clone();
            tempCal.add(Calendar.MONTH, monthOffset);

            for (Integer day : monthDays) {
                Calendar candidateCal = (Calendar) tempCal.clone();
                candidateCal.set(Calendar.DAY_OF_MONTH, day);

                // 检查日期是否有效（例如 2 月没有 30 号）
                if (candidateCal.get(Calendar.DAY_OF_MONTH) != day) {
                    continue;
                }

                // 检查是否在任务时间范围内且日期必须大于当前日期
                if (!candidateCal.before(startCal) && !candidateCal.after(endCal) && !candidateCal.before(nowCal)) {
                    return candidateCal.getTime();
                }
            }
        }

        return null;
    }

    /**
     * 计算自定义执行的最近日期
     * @param startDate 开始日期
     * @param cycleType 周期类型（天数）
     * @param startCal 开始时间
     * @param endCal 结束时间
     * @param nowCal 当前时间
     * @return 最近的执行日期
     */

    private Date calculateCustomExecutionDate(Date startDate, Integer cycleType, Calendar startCal, Calendar endCal) {
        Calendar resultCal = Calendar.getInstance();
        resultCal.setTime(startDate);
        resultCal.add(Calendar.DAY_OF_MONTH, cycleType);

        // 检查结果是否在 [startCal, endCal] 范围内
        if (!resultCal.before(startCal) && !resultCal.after(endCal)) {
            System.out.println( "结果日期：" + sdf.format(resultCal.getTime()));
            return resultCal.getTime();
        }

        return null;
    }

}
