package com.yutong.modules.risk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.constant.enums.ProcessOpTypeEnum;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.utils.PinyinUtil;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.constants.enums.ApplyTypeEnum;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDetailsDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcess;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcessDetails;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcess;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.mapper.YtsafetyHomeworkStageProcessMapper;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessService;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageService;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.UserAuthApiService;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import com.yutong.modules.user.vo.SafetyOfficerVO;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description: 作业环节流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetyHomeworkStageProcessServiceImpl extends ServiceImpl<YtsafetyHomeworkStageProcessMapper, YtsafetyHomeworkStageProcess> implements IYtsafetyHomeworkStageProcessService {
    private static final String MODEL_FLAG_WORK_ADD = "ytsafety_homework_stage";
    private static final String MODEL_FLAG_WORK_BATCH = "ytsafety_homework_batch_stage";
    private static final String MODEL_FLAG_WORK_PUSH = "ytsafety_homework_stage_push";
    private static final String FLOW_SUBJECT_WORK_ADD = "作业环节变更流程";
    private static final String FLOW_SUBJECT_WORK_BATCH = "作业环节批量变更流程";
    private static final String FLOW_SUBJECT_WORK_PUSH = "作业环节更新推送流程";
    @Autowired
    private IYtsafetyHomeworkStageProcessDetailsService homeworkStageProcessDetailsService;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private UserAuthApiService userAuthApiService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IYtsafetyHomeworkStageService homeworkStageService;

    @Autowired
    private ISafetyCommonService commonService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    IYtsafetyResponsibleProcessService safetyResponsibleProcessService;

    @Override
    @SneakyThrows
    public YtsafetyHomeworkStageProcess add(YtsafetyHomeworkStageProcessDTO dto) {
        /**如果申请类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getChangeType())){
            dto.setChangeType(0);
        }
        /**操作类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getOpType())){
            dto.setOpType(0);
        }
        YtsafetyHomeworkStageProcess process = BeanUtil.toBean(dto, YtsafetyHomeworkStageProcess.class);
        LoginUser loginUser = BusUtits.getLoginUser();
        /**如果是超级管理员或业务系统负责人提交数据直接跳过审批*/
        Set<String> userRoleSet = getUserRoleCodes(loginUser.getEmpNo());
        if (userRoleSet.contains(roleCodeConstant.getSysAdminRoleCode()) || userRoleSet.contains(roleCodeConstant.getBusinessSystemLead())){
            process.setAdminSubmit(1);
        }else{
            process.setAdminSubmit(0);
        }
        /**设置流程主键id*/
        Long processId = IdWorker.getId();
        /**封装必填字段*/
        setAddData(process,loginUser,processId);
        initProcessMeta(process, dto.getChangeType(), DateUtils.formatDate(), loginUser.getRealname());
        /**验证明细数据中的厂区和风险类型是否一致，并同步到流程表*/
        validateAndSyncDetails(process, dto.getDetails());
        if (!CollectionUtils.isEmpty(dto.getDetails())) {
            /**如果不是存草稿，就校验参数不为空*/
            if(dto.getOpType()!=0){
                validateDetails(dto.getDetails());
            }else{
                validateDetailsDraft(dto.getDetails());
            }
            List<YtsafetyHomeworkStageProcessDetails> detailsList = new ArrayList<>();
            setDetailsList(detailsList,dto,processId,loginUser);
            /**保存流程详情*/
            save(process);
            homeworkStageProcessDetailsService.saveBatch(detailsList);
        }
        submitProcess(process, dto.getOpType(),loginUser);
        return process;
    }

    @Override
    @SneakyThrows
    public YtsafetyHomeworkStageProcess edit(YtsafetyHomeworkStageProcessDTO dto) {
        /**如果申请类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getChangeType())){
            dto.setChangeType(0);
        }
        /**操作类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getOpType())){
            dto.setOpType(0);
        }
        YtsafetyHomeworkStageProcess process = BeanUtil.toBean(dto, YtsafetyHomeworkStageProcess.class);
        LoginUser loginUser = BusUtits.getLoginUser();
        /**如果是超级管理员或业务系统负责人提交数据直接跳过审批*/
        Set<String> userRoleSet = getUserRoleCodes(loginUser.getEmpNo());
        if (userRoleSet.contains(roleCodeConstant.getSysAdminRoleCode()) || userRoleSet.contains(roleCodeConstant.getBusinessSystemLead())){
            process.setAdminSubmit(1);
        }else{
            process.setAdminSubmit(0);
        }
        /**封装必填字段*/
        setUpdateData(process,loginUser);
        initProcessMeta(process, dto.getChangeType(), DateUtils.formatDate(), loginUser.getRealname());
        /**验证明细数据中的厂区和风险类型是否一致，并同步到流程表*/
        validateAndSyncDetails(process, dto.getDetails());
        if (!CollectionUtils.isEmpty(dto.getDetails())) {
            /**如果不是存草稿，就校验参数不为空*/
            if(dto.getOpType()!=0){
                validateDetails(dto.getDetails());
            }else{
                validateDetailsDraft(dto.getDetails());
            }
            List<YtsafetyHomeworkStageProcessDetails> detailsList = new ArrayList<>();
            setDetailsList(detailsList,dto,process.getId(),loginUser);
            /**保存流程详情*/
            updateById(process);
            homeworkStageProcessDetailsService.deleteByProcessId(process.getId());
            homeworkStageProcessDetailsService.saveBatch(detailsList);
        }
        submitProcess(process, dto.getOpType(),loginUser);
        return process;
    }

    @Override
    public List<YtsafetyHomeworkStageProcessVO> batchPush(YtsafetyHomeworkStageProcessDTO dto) {
        if (dto == null || dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("批量推送的详情数据不能为空");
        }
        List<YtsafetyHomeworkStageProcessDetailsDTO> allDetails = dto.getDetails();
        Set<Long> distinctFactoryIds = allDetails.stream().filter(Objects::nonNull).map(YtsafetyHomeworkStageProcessDetailsDTO::getFactoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        if(distinctFactoryIds.size()>1){
            throw new JeecgBootException("只能推送单厂区数据记录，请检查后重新操作");
        }
        LinkedHashMap<String, List<YtsafetyHomeworkStageProcessDetailsDTO>> groupMap = new LinkedHashMap<>();
        for (YtsafetyHomeworkStageProcessDetailsDTO detail : allDetails) {
            if (detail.getFactoryId() == null) {
                throw new IllegalArgumentException("详情数据中存在厂区为空的记录，请检查");
            }
            groupMap.computeIfAbsent(detail.getOrgCode(), k -> new ArrayList<>()).add(detail);
        }
        List<YtsafetyHomeworkStageProcessVO> resultList = new ArrayList<>(groupMap.size());
        for (Map.Entry<String, List<YtsafetyHomeworkStageProcessDetailsDTO>> entry : groupMap.entrySet()) {
            YtsafetyHomeworkStageProcessDTO groupDto = new YtsafetyHomeworkStageProcessDTO();
            groupDto.setApplyReason(dto.getApplyReason());
            groupDto.setChangeType(dto.getChangeType());
            groupDto.setOpType(dto.getOpType());
            groupDto.setDetails(entry.getValue());
            YtsafetyHomeworkStageProcess process = addPush(groupDto);
            YtsafetyHomeworkStageProcessVO vo = new YtsafetyHomeworkStageProcessVO();
            BeanUtil.copyProperties(process, vo);
            resultList.add(vo);
        }
        return resultList;
    }

    /**
     * 更新推送待办流程
     * @param dto
     * @return
     */
    @SneakyThrows
    private YtsafetyHomeworkStageProcess addPush(YtsafetyHomeworkStageProcessDTO dto) {
        /**如果申请类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getChangeType())){
            dto.setChangeType(0);
        }
        /**操作类型为空，就赋默认值0*/
        if(Objects.isNull(dto.getOpType())){
            dto.setOpType(0);
        }
        YtsafetyHomeworkStageProcess process = BeanUtil.toBean(dto, YtsafetyHomeworkStageProcess.class);
        LoginUser loginUser = BusUtits.getLoginUser();
        process.setAdminSubmit(0);
        /**设置流程主键id*/
        Long processId = IdWorker.getId();
        /**封装必填字段*/
        String orgCode = dto.getDetails().get(0).getOrgCode();
        setAddDataPush(process,loginUser,processId,orgCode);
        initProcessMeta(process, dto.getChangeType(), DateUtils.formatDate(), loginUser.getRealname());
        /**验证明细数据中的厂区和风险类型是否一致，并同步到流程表*/
        validateAndSyncDetails(process, dto.getDetails());
        if (!CollectionUtils.isEmpty(dto.getDetails())) {
            /**如果不是存草稿，就校验参数不为空*/
            if(dto.getOpType()!=0){
                validateDetails(dto.getDetails());
            }else{
                validateDetailsDraft(dto.getDetails());
            }
            List<YtsafetyHomeworkStageProcessDetails> detailsList = new ArrayList<>();
            setDetailsList(detailsList,dto,processId,loginUser);
            /**保存流程详情*/
            save(process);
            homeworkStageProcessDetailsService.saveBatch(detailsList);
        }
        submitProcess(process, dto.getOpType(),loginUser);
        return process;
    }
    /**
     * 封装推送流程新增字段
     * @param process
     * @param loginUser
     */
    private void setAddDataPush(YtsafetyHomeworkStageProcess process, LoginUser loginUser,Long processId,String orgCode) {
        process.setId(processId);
        process.setApplyNo(loginUser.getEmpNo());
        process.setApplyLogin(loginUser.getEipName());
        process.setApplyName(loginUser.getRealname());
        process.setApplyDepartCode(loginUser.getOrgCode());
        process.setApplyDepartName(loginUser.getOrgName());
        process.setFlowCode(generateTaskCode());
        List<SafetyOfficerVO> submitList = commonService.queryGroupInfoRoles(orgCode,roleCodeConstant.getSafetyOfficer());
        if(CollectionUtil.isEmpty(submitList)){
            throw new JeecgBootException("未找到所属安全员，请核实后提交");
        }
        String submitApproval = submitList.stream().map(SafetyOfficerVO::getUserName).filter(Objects::nonNull).collect(Collectors.joining(","));
        process.setSubmitApproval(submitApproval);
        List<SafetyOfficerVO> list = commonService.queryGroupInfoRoles(orgCode,roleCodeConstant.getFirefighter());
        if(CollectionUtil.isEmpty(list)){
            throw new JeecgBootException("未找到所属组织消防员，请核实后提交");
        }
        String superApproval = list.stream().map(SafetyOfficerVO::getUserName).filter(Objects::nonNull).collect(Collectors.joining(","));
        process.setSuperApproval(superApproval);
        process.setCreateUser(loginUser.getEmpNo());
        process.setCreateName(loginUser.getRealname());
        process.setCreateTime(new Date());
        process.setDelFlag(0);
        process.setVersion(0);
        process.setUpdateUser(loginUser.getEmpNo());
        process.setUpdateTime(new Date());
        process.setUpdateName(loginUser.getRealname());
    }

    @Override
    public YtsafetyHomeworkStageProcessVO queryById(Long id) {
        /**先查询更新中的数据，如果不存在查询主键数据*/
        List<YtsafetyHomeworkStageProcessDetails> detailList = homeworkStageProcessDetailsService.queryListByChangeId(id);
        if(CollectionUtil.isNotEmpty(detailList)){
            YtsafetyHomeworkStageProcessDetails details = detailList.get(0);
            YtsafetyHomeworkStageProcess process = getById(details.getProcessId());
            if (process == null) {
                return null;
            }
            YtsafetyHomeworkStageProcessVO vo = new YtsafetyHomeworkStageProcessVO();
            BeanUtil.copyProperties(process, vo);
            List<YtsafetyHomeworkStageVO> detailVOs = new ArrayList<>();
            YtsafetyHomeworkStageVO detailVO = new YtsafetyHomeworkStageVO();
            BeanUtil.copyProperties(details, detailVO);
            detailVOs.add(detailVO);
            vo.setDetails(detailVOs);
            return vo;
        }else{
            YtsafetyHomeworkStageProcessDetails details = homeworkStageProcessDetailsService.getById(id);
            if(Objects.nonNull(details)){
                YtsafetyHomeworkStageProcess process = getById(details.getProcessId());
                if (process == null) {
                    return null;
                }
                YtsafetyHomeworkStageProcessVO vo = new YtsafetyHomeworkStageProcessVO();
                BeanUtil.copyProperties(process, vo);
                List<YtsafetyHomeworkStageVO> detailVOs = new ArrayList<>();
                YtsafetyHomeworkStageVO detailVO = new YtsafetyHomeworkStageVO();
                BeanUtil.copyProperties(details, detailVO);
                detailVOs.add(detailVO);
                vo.setDetails(detailVOs);
                return vo;
            }
            return null;
        }
    }

    @Override
    public YtsafetyHomeworkStageProcessVO queryByProcessId(Long processId) {
        YtsafetyHomeworkStageProcess process = getById(processId);
        if (process == null) {
            return null;
        }
        YtsafetyHomeworkStageProcessVO vo = new YtsafetyHomeworkStageProcessVO();
        BeanUtil.copyProperties(process, vo);
        LoginUser user = BusUtits.getLoginUser();
        BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(process.getId(),user.getEipName());
        List<FieldAuth> fieldAuths = safetySummaryProcessService.queryFieldAuth(processId);
        if(detailsVO.getIsCanApprove() && process.getChangeType().equals(ApplyTypeEnum.BATCH_PUSH.getCode())){
            detailsVO.setIsCanEdit(isProcessEdit(fieldAuths,detailsVO.getIsCanApprove()));
        }
        BeanUtils.copyProperties(detailsVO, vo);
        List<YtsafetyHomeworkStageProcessDetails> detailsList = homeworkStageProcessDetailsService.list(
                new LambdaQueryWrapper<YtsafetyHomeworkStageProcessDetails>()
                        .eq(YtsafetyHomeworkStageProcessDetails::getProcessId, process.getId())
        );
        List<YtsafetyHomeworkStageVO> detailVOs = new ArrayList<>();
        for (YtsafetyHomeworkStageProcessDetails detail : detailsList) {
            YtsafetyHomeworkStageVO detailVO = new YtsafetyHomeworkStageVO();
            BeanUtil.copyProperties(detail, detailVO);
            if(ObjectUtil.isNotNull(detail.getChangeId())){
                YtsafetyHomeworkStage stage = homeworkStageService.getById(detail.getChangeId());
                if(ObjectUtil.isNotNull(stage)){
                    compareWithExisting(detailVO,stage);
                }
            }
            detailVOs.add(detailVO);
        }
        vo.setDetails(detailVOs);
        return vo;
    }
    private boolean isProcessEdit(List<FieldAuth> fieldAuths,Boolean isCanApprove){
        if(fieldAuths==null|| fieldAuths.isEmpty()) {
            return false;
        }
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> "submit_approval".equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        if(fieldAuth==null){
            return false;
        }
        return isCanApprove;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<YtsafetyHomeworkStageVO> importData(YtsafetyHomeworkStageProcessDTO dto, List<YtsafetyHomeworkStageImportVO> excelData) {
        if (excelData == null || excelData.isEmpty()) {
            return new ArrayList<>();
        }
        List<HomeworkStageParamVO> factoryList = this.baseMapper.getFactoryInfoList();
        List<HomeworkStageParamVO> areaList = this.baseMapper.getAreaInfoList();
        List<HomeworkStageParamVO> orgList = this.baseMapper.getOrgInfoByList();
        List<YtsafetyHomeworkStageVO> stages = new ArrayList<>();
        for (YtsafetyHomeworkStageImportVO row : excelData) {
            YtsafetyHomeworkStageVO stageVO = new YtsafetyHomeworkStageVO();
            //stageVO.setId(IdWorker.getId());
            stageVO.setLinkName(row.getLinkName());
            stageVO.setFactoryName(row.getFactoryName());
            stageVO.setAreaName(row.getAreaName());
            stageVO.setOrgName(row.getOrgName());
            stageVO.setLocationNames(row.getLocationName());
            resolveIds(stageVO,factoryList,areaList,orgList);
            if (ObjectUtil.isNotNull(row.getLinkCode())){
                stageVO.setLinkCode(row.getLinkCode());
                YtsafetyHomeworkStage stage = homeworkStageService.getByLinkCode(row.getLinkCode());
                if(ObjectUtil.isNotNull(stage)){
                    compareWithExisting(stageVO,stage);
                    stageVO.setChangeId(stage.getId());
                }
            }
            stages.add(stageVO);
        }
        log.info("作业环节批量导入完成，成功写入{}条数据", stages.size());
        return stages;
    }

    /**
     * 审批流程
     * @param processParamDTO
     * @return
     */
    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        BpmReponseParamsVO bpmReponseParamsVO = safetyResponsibleProcessService.executeProcess(processParamDTO);
        List<YtsafetyHomeworkStageProcessDetails> detailsList =  homeworkStageProcessDetailsService.queryDetailsList(processParamDTO.getFormInstanceId());
        if (CollectionUtil.isNotEmpty(detailsList)){
            for(YtsafetyHomeworkStageProcessDetails details : detailsList){
                if(ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
                    details.setBpmState(ProcessStatusEnums.deny.getCode());
                }
                if(ProcessStatusEnums.discarded.getCode() == bpmReponseParamsVO.getFlowStatus()){
                    details.setBpmState(ProcessStatusEnums.discarded.getCode());
                }
                if(ProcessStatusEnums.finish.getCode() == bpmReponseParamsVO.getFlowStatus()){
                    details.setBpmState(ProcessStatusEnums.finish.getCode());
                }
            }
            homeworkStageProcessDetailsService.updateBatchById(detailsList);
        }
        return bpmReponseParamsVO;
    }
    /**
     * 封装作业环节明细列表对象
     * @param detailsList
     * @param dto
     * @param processId
     * @param loginUser
     */
    private void setDetailsList(List<YtsafetyHomeworkStageProcessDetails> detailsList,YtsafetyHomeworkStageProcessDTO dto
            ,Long processId,LoginUser loginUser){
        for (YtsafetyHomeworkStageProcessDetailsDTO detailDTO : dto.getDetails()) {
            YtsafetyHomeworkStageProcessDetails details = new YtsafetyHomeworkStageProcessDetails();
            BeanUtils.copyProperties(detailDTO,details);
            /**封装必填字段*/
            setAddData(details,loginUser,processId);
            /**如果是草稿状态是草稿，不是为审批中*/
            Integer bpmState = dto.getOpType()==0 ? 0 : 1;
            details.setBpmState(bpmState);
            if(ObjectUtil.isNull(detailDTO.getLinkCode())){
                String areaCode = PinyinUtil.getFirstLetter(detailDTO.getAreaName());
                String linkName = PinyinUtil.getFirstLetterAndInt(detailDTO.getLinkName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String dateStr = dateFormat.format(new Date());
                if(ObjectUtil.isNotNull(detailDTO.getFactoryCode())){
                    String redisKey = "ytsafety:task:sequence:work" + dateStr;
                    Long sequence = redisUtil.incr(redisKey, 1);
                    String linkCode = "ZYHJ" + detailDTO.getFactoryCode() + "-" + areaCode + "-" + linkName + sequence;
                    details.setLinkCode(linkCode);
                }
            }else {
                YtsafetyHomeworkStage stage = homeworkStageService.getByLinkCode(detailDTO.getLinkCode());
                if(ObjectUtil.isNotNull(stage)){
                    details.setChangeId(stage.getId());
                }
            }
            detailsList.add(details);
        }
    }
    /**
     * 封装实体新增字段
     * @param details
     * @param loginUser
     */
    private void setAddData(YtsafetyHomeworkStageProcessDetails details, LoginUser loginUser,Long processId) {
        details.setId(IdWorker.getId());
        details.setProcessId(processId);
        details.setCreateUser(loginUser.getEmpNo());
        details.setCreateName(loginUser.getRealname());
        details.setCreateTime(new Date());
        details.setDelFlag(0);
        details.setUpdateUser(loginUser.getEmpNo());
        details.setUpdateTime(new Date());
        details.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装修改字段
     * @param details
     * @param loginUser
     */
    private void setUpdateData(YtsafetyHomeworkStageProcessDetails details, LoginUser loginUser) {
        details.setUpdateUser(loginUser.getEmpNo());
        details.setUpdateTime(new Date());
        details.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装流程新增字段
     * @param process
     * @param loginUser
     */
    private void setAddData(YtsafetyHomeworkStageProcess process, LoginUser loginUser,Long processId) {
        process.setId(processId);
        process.setApplyNo(loginUser.getEmpNo());
        process.setApplyLogin(loginUser.getEipName());
        process.setApplyName(loginUser.getRealname());
        process.setApplyDepartCode(loginUser.getOrgCode());
        process.setApplyDepartName(loginUser.getOrgName());
        process.setFlowCode(generateTaskCode());
        List<SafetyOfficerVO> list = commonService.queryGroupInfoRoles(loginUser.getOrgCode(),roleCodeConstant.getFirefighter());
        if(CollectionUtil.isEmpty(list)){
            throw new JeecgBootException("未找到所属组织消防员，请核实后提交");
        }
        String superApproval = list.stream().map(SafetyOfficerVO::getUserName).filter(Objects::nonNull).collect(Collectors.joining(","));
        process.setSuperApproval(superApproval);
        process.setCreateUser(loginUser.getEmpNo());
        process.setCreateName(loginUser.getRealname());
        process.setCreateTime(new Date());
        process.setDelFlag(0);
        process.setVersion(0);
        process.setUpdateUser(loginUser.getEmpNo());
        process.setUpdateTime(new Date());
        process.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装流程修改字段
     * @param process
     * @param loginUser
     */
    private void setUpdateData(YtsafetyHomeworkStageProcess process, LoginUser loginUser) {
        //process.setUpdateUser(loginUser.getEmpNo());
        //process.setUpdateTime(new Date());
        //process.setUpdateName(loginUser.getRealname());
    }

    /**
     * 根据变更类型初始化流程模型标识和主题
     */
    private void initProcessMeta(YtsafetyHomeworkStageProcess process, Integer changeType, String dateStr, String realname) {
        if (changeType == ApplyTypeEnum.ADD.getCode().intValue()||changeType==ApplyTypeEnum.CHANGE_MODIFY.getCode().intValue()) {
            process.setModelFlag(MODEL_FLAG_WORK_ADD);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_WORK_ADD);
        } else if(changeType == ApplyTypeEnum.BATCH_PUSH.getCode().intValue()){
            process.setModelFlag(MODEL_FLAG_WORK_PUSH);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_WORK_PUSH);
        }else {
            process.setModelFlag(MODEL_FLAG_WORK_BATCH);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_WORK_BATCH);
        }
    }

    /**
     * 验证明细数据中的厂区和风险类型是否一致，并同步到流程表
     */
    private void validateAndSyncDetails(YtsafetyHomeworkStageProcess process, List<YtsafetyHomeworkStageProcessDetailsDTO> details) {
        if (CollectionUtil.isEmpty(details)) {
            return;
        }
        Set<Long> factoryIds = details.stream()
                .map(YtsafetyHomeworkStageProcessDetailsDTO::getFactoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (factoryIds.size() > 1) {
            throw new JeecgBootException("明细数据中的厂区不一致，请检查");
        }
        YtsafetyHomeworkStageProcessDetailsDTO firstDetail = details.get(0);
        process.setFactoryId(firstDetail.getFactoryId());
        process.setFactoryName(firstDetail.getFactoryName());
    }

    /**
     * 发起流程审批
     * @param process
     * @param opType
     */
    private void submitProcess(YtsafetyHomeworkStageProcess process, Integer opType,LoginUser user) {
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(process.getModelFlag());
        dto.setFormInstanceId(process.getId());
        dto.setFlowCode(process.getFlowCode());
        dto.setFlowSubject(process.getFlowSubject());
        dto.setCurNodeHandler(process.getSuperApproval());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyEipName(user.getEipName());
        dto.setLoginName(user.getEipName());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setPreNodeHandlerName(user.getRealname() + "(" + user.getEmpNo() + ")");
        dto.setFactoryId(process.getFactoryId());
        dto.setFactoryName(process.getFactoryName());
        if (opType == null || opType == ProcessOpTypeEnum.draft.getCode()) {
            safetySummaryProcessService.saveDraft(dto);
        } else {
            safetySummaryProcessService.createAndApproveProcess(dto);
        }
    }

    @Lock4j(name = "ytsafety:task:work:cache")
    public String generateTaskCode() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateStr = dateFormat.format(new Date());
            String redisKey = "ytsafety:task:code:work" + dateStr;
            Long sequence = redisUtil.incr(redisKey, 1);
            return String.format("ZYHJ%s%04d", dateStr, sequence);
        } catch (Exception e) {
            log.error("生成任务编号时Redis操作失败", e);
            throw new RuntimeException("生成任务编号失败：" + e.getMessage(), e);
        }
    }

    /**
     * 依据名称补全VO中的主键字段
     */
    private void resolveIds(YtsafetyHomeworkStageVO vo,List<HomeworkStageParamVO> factoryList,
                            List<HomeworkStageParamVO> areaList,List<HomeworkStageParamVO> orgList) {
        Long factoryId = null;
        Long areaId = null;
        Long orgId = null;
        /**遍历厂区列表*/
        Optional<HomeworkStageParamVO> factoryResult = factoryList.stream()
                .filter(item -> vo.getFactoryName().equals(item.getRecordName()))
                .findFirst();
        if (factoryResult.isPresent()) {
            HomeworkStageParamVO factoryVO = factoryResult.get();
            vo.setFactoryId(factoryVO.getRecordId());
            factoryId = factoryVO.getRecordId();
            vo.setFactoryCode(factoryVO.getRecordCode());
        }else{
            throw new JeecgBootException("厂区位置信息不存在，请检查后重新录入");
        }
        /**遍历区域列表*/
        Optional<HomeworkStageParamVO> areaResult = areaList.stream()
                .filter(item -> vo.getAreaName().equals(item.getRecordName()))
                .findFirst();
        if (areaResult.isPresent()) {
            HomeworkStageParamVO areaVO = areaResult.get();
            areaId = areaVO.getRecordId();
            vo.setAreaId(areaVO.getRecordId());
            vo.setAreaCode(areaVO.getRecordCode());
        }else{
            throw new JeecgBootException("厂区区域位置信息不存在，请检查后重新录入");
        }
        /**遍历组织列表*/
        Optional<HomeworkStageParamVO> orgResult = orgList.stream()
                .filter(item -> vo.getOrgName().equals(item.getRecordName()))
                .findFirst();
        if (orgResult.isPresent()) {
            HomeworkStageParamVO orgVO = orgResult.get();
            orgId = orgVO.getRecordId();
            vo.setOrgId(orgVO.getRecordId());
            vo.setOrgCode(orgVO.getRecordCode());
        }else{
            throw new JeecgBootException("所属组织信息不存在，请检查后重新录入");
        }
        if (vo.getLocationNames() != null && !vo.getLocationNames().trim().isEmpty()) {
            List<String> locationNamesList = Arrays.stream(vo.getLocationNames().split(",")).collect(Collectors.toList());
            List<Long> locationIdsList = this.baseMapper.selectIdsByNameAndIds(locationNamesList,factoryId,areaId,orgId);
            if(CollectionUtil.isNotEmpty(locationIdsList)){
                if (locationIdsList.size() > 1){
                    String locationIds = locationIdsList.stream().map(String::valueOf).collect(Collectors.joining(","));
                    vo.setLocationIds(locationIds);
                }else {
                    vo.setLocationIds(locationIdsList.get(0).toString());
                }
            }else {
                throw new JeecgBootException("位置信息不存在，请检查后重新录入");
            }
        }else {
            throw new JeecgBootException("位置信息不存在，请检查后重新录入");
        }
        List<SafetyOfficerVO> officerVOList = commonService.queryGroupInfoSafetyOfficer(vo.getOrgCode());
        String safetyStaffId = officerVOList.stream().map(SafetyOfficerVO::getEmpNo).filter(Objects::nonNull).collect(Collectors.joining(","));
        String safetyStaff = officerVOList.stream().map(SafetyOfficerVO::getEmpName).filter(Objects::nonNull).collect(Collectors.joining(","));
        vo.setSafetyStaffId(safetyStaffId);
        vo.setSafetyStaff(safetyStaff);
        List<SafetyOfficerVO> managerList = commonService.queryGroupInfoRoles(vo.getOrgCode(),roleCodeConstant.getSafetySupervisor());
        String safetyManagerId = managerList.stream().map(SafetyOfficerVO::getEmpNo).filter(Objects::nonNull).collect(Collectors.joining(","));
        String safetyManager = managerList.stream().map(SafetyOfficerVO::getEmpName).filter(Objects::nonNull).collect(Collectors.joining(","));
        vo.setSafetyManagerId(safetyManagerId);
        vo.setSafetyManager(safetyManager);
    }
    /**
     * 校验明细数据必填字段
     */
    private void validateDetails(List<YtsafetyHomeworkStageProcessDetailsDTO> details) {
        for (int i = 0; i < details.size(); i++) {
            YtsafetyHomeworkStageProcessDetailsDTO detail = details.get(i);
            StringBuilder msg = new StringBuilder();
            if (isBlank(detail.getLinkName())) {msg.append("作业环节名称,");}
            if (detail.getFactoryId() == null) {msg.append("所属厂区id,");}
            if (isBlank(detail.getFactoryCode())){ msg.append("所属厂区编码,");}
            if (isBlank(detail.getFactoryName())) {msg.append("所属厂区名称,");}
            if (detail.getAreaId() == null) {msg.append("所属区域id,");}
            if (isBlank(detail.getAreaCode())){ msg.append("所属区域编码,");}
            if (isBlank(detail.getAreaName())) {msg.append("所属区域名称,");}
            if (detail.getOrgId() == null) {msg.append("所属组织id,");}
            if (isBlank(detail.getOrgName())) {msg.append("所属组织名称,");}
            if (isBlank(detail.getOrgCode())) {msg.append("所属组织编码,");}
            if (isBlank(detail.getLocationIds())){ msg.append("位置id,");}
            if (isBlank(detail.getSafetyStaff())||isBlank(detail.getSafetyStaffId())){ msg.append("安全员,");}
            if (isBlank(detail.getSafetyManager())||isBlank(detail.getSafetyManagerId())){ msg.append("安全主管,");}
            if (msg.length() > 0) {
                throw new JeecgBootException("第" + (i + 1) + "条明细数据缺失必填字段：" + msg.substring(0, msg.length() - 1));
            }
        }
    }

    /**
     * 存草稿校验明细数据必填字段
     */
    private void validateDetailsDraft(List<YtsafetyHomeworkStageProcessDetailsDTO> details) {
        for (int i = 0; i < details.size(); i++) {
            YtsafetyHomeworkStageProcessDetailsDTO detail = details.get(i);
            StringBuilder msg = new StringBuilder();
            if (isBlank(detail.getLinkName())) {msg.append("作业环节名称,");}
            if (msg.length() > 0) {
                throw new JeecgBootException("第" + (i + 1) + "条明细数据缺失必填字段：" + msg.substring(0, msg.length() - 1));
            }
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 依据员工号查询用户角色编码集合
     */
    private Set<String> getUserRoleCodes(String empNo) {
        return userAuthApiService.queryUserRoleCodes(empNo);
    }

    /**
     * 与已有数据进行字段比对，记录差异字段编码和原有值
     */
    private void compareWithExisting(YtsafetyHomeworkStageVO newVo, YtsafetyHomeworkStage existing) {
        List<String> fieldCodes = new ArrayList<>();
        HashMap<String, String> fieldsValues = new HashMap<>();
        compareField("linkCode", newVo.getLinkCode(), existing.getLinkCode(), fieldCodes, fieldsValues);
        compareField("linkName", newVo.getLinkName(), existing.getLinkName(), fieldCodes, fieldsValues);
        compareField("factoryCode", newVo.getFactoryCode(), existing.getFactoryCode(), fieldCodes, fieldsValues);
        compareField("factoryName", newVo.getFactoryName(), existing.getFactoryName(), fieldCodes, fieldsValues);
        compareField("areaCode", newVo.getAreaCode(), existing.getAreaCode(), fieldCodes, fieldsValues);
        compareField("areaName", newVo.getAreaName(), existing.getAreaName(), fieldCodes, fieldsValues);
        compareField("orgName", newVo.getOrgName(), existing.getOrgName(), fieldCodes, fieldsValues);
        compareField("orgCode", newVo.getOrgCode(), existing.getOrgCode(), fieldCodes, fieldsValues);
        compareField("locationIds", newVo.getLocationIds(), existing.getLocationIds(), fieldCodes, fieldsValues);
        compareField("locationNames", newVo.getLocationNames(), existing.getLocationNames(), fieldCodes, fieldsValues);
        compareField("safetyStaffId", newVo.getSafetyStaffId(), existing.getSafetyStaffId(), fieldCodes, fieldsValues);
        compareField("safetyStaff", newVo.getSafetyStaff(), existing.getSafetyStaff(), fieldCodes, fieldsValues);
        compareField("safetyManager", newVo.getSafetyManager(), existing.getSafetyManager(), fieldCodes, fieldsValues);
        compareField("safetyManagerId", newVo.getSafetyManagerId(), existing.getSafetyManagerId(), fieldCodes, fieldsValues);
        newVo.setFieldCodes(fieldCodes);
        newVo.setFieldsValues(fieldsValues);
    }

    private void compareField(String fieldCode, String newVal, String oldVal,
                              List<String> fieldCodes, HashMap<String, String> fieldsValues) {
        if (!Objects.equals(newVal, oldVal)) {
            fieldCodes.add(fieldCode);
        }
        fieldsValues.put(fieldCode, oldVal);
    }
}
