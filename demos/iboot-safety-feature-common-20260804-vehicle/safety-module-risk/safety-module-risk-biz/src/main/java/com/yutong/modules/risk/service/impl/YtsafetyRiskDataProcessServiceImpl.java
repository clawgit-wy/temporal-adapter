package com.yutong.modules.risk.service.impl;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.common.api.bpm.IYtsafetyProcessNodeConfigService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.constant.enums.ProcessOpTypeEnum;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.constants.enums.ApplyTypeEnum;
import com.yutong.modules.risk.constants.enums.RiskLevelEnum;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoProcessDetailsDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDetailsDTO;
import com.yutong.modules.risk.entity.*;
import com.yutong.modules.risk.mapper.YtsafetyRiskDataMapper;
import com.yutong.modules.risk.mapper.YtsafetyRiskDataProcessMapper;
import com.yutong.modules.risk.service.*;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.IYtsafetySysDictItemService;
import com.yutong.modules.user.api.UserAuthApiService;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import com.yutong.modules.user.vo.SafetyGroupInfoTreeVO;
import com.yutong.modules.user.vo.SafetyOfficerVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CustomConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Description: 风险数据流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Service
public class YtsafetyRiskDataProcessServiceImpl extends ServiceImpl<YtsafetyRiskDataProcessMapper, YtsafetyRiskDataProcess> implements IYtsafetyRiskDataProcessService {

    private static final String MODEL_FLAG_ADD = "ytsafety_risk_data_add";
    private static final String MODEL_FLAG_BATCH = "ytsafety_risk_data_batch";
    private static final String FLOW_SUBJECT_ADD = "发起的风险数据库新增信息";
    private static final String FLOW_SUBJECT_BATCH = "发起的风险数据库批量变更信息";
    private static final Map<String, String> RISK_TYPE_ABBR_MAP = new HashMap<String, String>() {{
        put("消防", "XF");
        put("治安", "ZA");
    }};

    @Autowired
    private YtsafetyRiskDataProcessMapper processMapper;

    @Autowired
    private IYtsafetyRiskDataProcessDetailsService detailsService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private IYtsafetySysDictItemService sysDictItemService;

    @Autowired
    private IYtsafetyRiskGradingStandardService riskGradingStandardService;

    @Autowired
    private UserAuthApiService userAuthApiService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    private IYtsafetyLocationInfoService locationInfoService;

    @Autowired
    private IYtsafetyHomeworkStageService homeworkStageService;

    @Autowired
    private IYTSafetyRiskLevelDictService riskLevelDictService;

    @Autowired
    private YtsafetyRiskDataMapper riskDataMapper;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    YTSafetyWaterFlowNoService ytSafetyWaterFlowNoService;

    @Autowired
    IYtsafetyProcessNodeConfigService processNodeConfigService;

    @Autowired
    @Lazy
    private YtsafetyRiskDataProcessServiceImpl self;

    @Resource(name = "parallelIoExecutor")
    private Executor parallelIoExecutor;

    @Value("${ytsafety.process.config.nickAccount}")
    private String nickAccount;


    @Override
    public YtsafetyRiskDataProcess add(YtsafetyRiskDataDTO dto) {
        LoginUser user = BusUtits.getLoginUser();
        Set<String> roleCodes = userAuthApiService.queryUserRoleCodes(user.getEmpNo());
        return doAdd(dto, user, roleCodes, true);
    }

    /**
     * 核心添加逻辑，接受已缓存的用户信息和角色编码，避免异步场景下重复获取
     * @param throwOnEmpty true=add路径(空审批人抛异常), false=addJob路径(空审批人不抛异常)
     */
    private YtsafetyRiskDataProcess doAdd(YtsafetyRiskDataDTO dto, LoginUser user, Set<String> roleCodes, boolean throwOnEmpty) {
        YtsafetyRiskDataProcess process = new YtsafetyRiskDataProcess();
        BeanUtils.copyProperties(dto, process);
        Long id = IdWorker.getId();
        process.setId(id);
        process.setApplyNo(user.getEmpNo());
        process.setApplyLogin(user.getEipName());
        process.setApplyName(user.getRealname());
        process.setApplyDepartCode(user.getOrgCode());
        process.setApplyDepartName(user.getOrgName());
        process.setSuperApproval(user.getEipName());
        process.setSuperApprovalName(user.getRealname());
        initProcessMeta(process, dto.getChangeType(), DateUtils.formatDate(), user.getRealname());

        boolean isSupper = roleCodes != null && (roleCodes.contains(roleCodeConstant.getSysAdminRoleCode())
                || roleCodes.contains(roleCodeConstant.getBusinessSystemLead()));
        process.setIsSupper(isSupper ? 1 : 0);
        process.setIsApprove(0);

        // 并行：流水号生成 + 风险编码生成 + 明细校验，三者互不依赖
        CompletableFuture<String> flowCodeFuture = CompletableFuture.supplyAsync(
                this::generateTaskCode, parallelIoExecutor);
        CompletableFuture<Void> riskCodesFuture = CompletableFuture.runAsync(
                () -> generateDetailRiskCodes(dto.getDetails()), parallelIoExecutor);
        validateAndSyncDetails(process, dto.getDetails());
        process.setFlowCode(flowCodeFuture.join());
        riskCodesFuture.join();

        if(dto.getOpType()==1) {
            if (process.getRiskLevel() != null && process.getRiskLevel().equals(RiskLevelEnum.CRITICAL.getCode())&&process.getIsSupper()==0) {
                process.setIsApprove(1);
            }
            fillApproversParallel(process, dto.getDetails(), throwOnEmpty);
        }
        processMapper.insert(process);
        detailsService.save(process.getId(), dto.getDetails(), user);
        doSubmitProcess(process, dto.getOpType(), user);
        return process;
    }

    @Override
    public YtsafetyRiskDataProcess addJob(YtsafetyRiskDataDTO dto) {
        if(dto.getDetails()==null||dto.getDetails().isEmpty()) return null;
        LoginUser jobUser = new LoginUser();
        jobUser.setEmpNo(CustomConstant.nickempno);
        jobUser.setEipName(CustomConstant.nickLogin);
        jobUser.setRealname(CustomConstant.nickempname);
        jobUser.setOrgCode(dto.getDetails().get(0).getOrgCode());
        jobUser.setOrgName(dto.getDetails().get(0).getOrgName());
        Set<String> jobRoleCodes = Collections.singleton(roleCodeConstant.getSysAdminRoleCode());
        return doAdd(dto, jobUser, jobRoleCodes, false);
    }

    /**
     * 获取提案人
     * @param nodeFlag
     * @param details
     * @return
     */
    private String queryBatchOrgCodeApprovers(String nodeFlag,String riskType,String riskLevel,Integer isSupper,List<YtsafetyRiskDataDetailsDTO> details){
        String dataType = riskType + riskLevel;
        List<String> orgCodes = details.stream().map(YtsafetyRiskDataDetailsDTO::getOrgCode).filter(com.baomidou.mybatisplus.core.toolkit.StringUtils::isNotEmpty).collect(Collectors.toList());
        return processNodeConfigService.queryProposalApprovers("ytsafety_risk_data",nodeFlag,dataType,orgCodes);
    }

    private String queryApprovers(String nodeFlag, String riskType,String riskLevel,Integer isSupper,Long factoryId,String orgCode) {
        String dataType = riskType + riskLevel;
        if (isSupper == 1) {
            dataType = riskType + "负责人";
        }
        return processNodeConfigService.queryApprovers("ytsafety_risk_data", nodeFlag, dataType, factoryId, orgCode);
    }

    /**
     * 并行查询并设置流程各节点审批人，2~4个远程调用并行执行
     * @param throwOnEmpty 审批人为空时是否抛异常
     */
    private void fillApproversParallel(YtsafetyRiskDataProcess process,
                                        List<YtsafetyRiskDataDetailsDTO> details,
                                        boolean throwOnEmpty) {
        String riskTypeName = process.getRiskTypeName();
        String riskLevel = process.getRiskLevel();
        Integer isSupper = process.getIsSupper();
        Long factoryId = process.getFactoryId();
        String orgCode = process.getApplyDepartCode();

        CompletableFuture<String> superApprovalFuture = null;
        CompletableFuture<String> authorizeApprovalFuture = null;
        CompletableFuture<String> proposalApprovalFuture = null;
        CompletableFuture<String> recordApprovalFuture;

        if (process.getIsApprove() == 1) {
            superApprovalFuture = CompletableFuture.supplyAsync(
                    () -> queryApprovers("approve", riskTypeName, riskLevel, isSupper, factoryId, orgCode),
                    parallelIoExecutor);
        }
        authorizeApprovalFuture = CompletableFuture.supplyAsync(
                () -> queryApprovers("authorize", riskTypeName, riskLevel, isSupper, factoryId, orgCode),
                parallelIoExecutor);
        if (Objects.equals(ApplyTypeEnum.BATCH_PUSH.getCode(), process.getChangeType())) {
            proposalApprovalFuture = CompletableFuture.supplyAsync(
                    () -> queryBatchOrgCodeApprovers("proposal", riskTypeName, riskLevel, isSupper, details),
                    parallelIoExecutor);
        }
        recordApprovalFuture = CompletableFuture.supplyAsync(
                () -> queryApprovers("record", riskTypeName, riskLevel, isSupper, factoryId, orgCode),
                parallelIoExecutor);

        if (superApprovalFuture != null) {
            String superApproval = superApprovalFuture.join();
            if (throwOnEmpty && StringUtils.isEmpty(superApproval))
                throw new RuntimeException("待审批人员不能为空请配置后在保存");
            process.setSuperApproval(superApproval);
        }
        if (authorizeApprovalFuture != null) {
            String authorizeApproval = authorizeApprovalFuture.join();
            if (throwOnEmpty && StringUtils.isEmpty(authorizeApproval))
                throw new RuntimeException("待批准人员不能为空请配置后在保存");
            process.setAuthorizeApproval(authorizeApproval);
        }
        if (proposalApprovalFuture != null) {
            String proposalApproval = proposalApprovalFuture.join();
            if (throwOnEmpty && StringUtils.isEmpty(proposalApproval))
                throw new RuntimeException("提案人员不能为空请配置后在保存");
            process.setProposalApproval(proposalApproval);
        }
        String recordApproval = recordApprovalFuture.join();
        process.setRecordApproval(recordApproval);
    }

    @Override
    public YtsafetyRiskDataProcess edit(YtsafetyRiskDataDTO dto) {
        YtsafetyRiskDataProcess process = processMapper.selectById(dto.getId());
        process.setApplyReason(dto.getApplyReason());
        validateAndSyncDetails(process, dto.getDetails());
        generateDetailRiskCodes(dto.getDetails());
        if(dto.getOpType()==1) {
            fillApproversParallel(process, dto.getDetails(), true);
        } else {
            process.setRecordApproval(queryApprovers("record", process.getRiskTypeName(), process.getRiskLevel(),process.getIsSupper(), process.getFactoryId(), process.getApplyDepartCode()));
        }
        processMapper.updateById(process);
        detailsService.save(process.getId(), dto.getDetails(), BusUtits.getLoginUser());
        submitProcess(process, dto.getOpType());
        return process;
    }

    @Override
    public List<YtsafetyRiskDataProcessVO> batchPush(YtsafetyRiskDataDTO dto) {
        if (dto == null || dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("批量推送的详情数据不能为空");
        }
        List<YtsafetyRiskDataDetailsDTO> allDetails = dto.getDetails();

        // 单次遍历：校验字段非空、一致性检查、按orgId分组
        Long commonFactoryId = null;
        Long commonRiskTypeId = null;
        LinkedHashMap<Long, List<YtsafetyRiskDataDetailsDTO>> orgGroupMap = new LinkedHashMap<>();

        for (YtsafetyRiskDataDetailsDTO detail : allDetails) {
            if (detail.getFactoryId() == null) {
                throw new IllegalArgumentException("详情数据中存在厂区为空的记录，请检查");
            }
            if (detail.getRiskTypeId() == null) {
                throw new IllegalArgumentException("详情数据中存在风险类型为空的记录，请检查");
            }
            if (detail.getOrgId() == null) {
                throw new IllegalArgumentException("详情数据中存在组织为空的记录，请检查");
            }

            if (commonFactoryId == null) {
                commonFactoryId = detail.getFactoryId();
            } else if (!commonFactoryId.equals(detail.getFactoryId())) {
                throw new RuntimeException("推送厂区不一致");
            }

            if (commonRiskTypeId == null) {
                commonRiskTypeId = detail.getRiskTypeId();
            } else if (!commonRiskTypeId.equals(detail.getRiskTypeId())) {
                throw new RuntimeException("推送风险类型不一致");
            }

            orgGroupMap.computeIfAbsent(detail.getOrgId(), k -> new ArrayList<>()).add(detail);
        }

        // 预获取共享数据，避免每个分组重复调用外部接口
        LoginUser user = BusUtits.getLoginUser();
        Set<String> roleCodes = userAuthApiService.queryUserRoleCodes(user.getEmpNo());

        // 并行处理各组织分组（通过self代理真正触发@Async）
        List<Future<YtsafetyRiskDataProcess>> futures = new ArrayList<>(orgGroupMap.size());
        for (Map.Entry<Long, List<YtsafetyRiskDataDetailsDTO>> entry : orgGroupMap.entrySet()) {
            YtsafetyRiskDataDTO groupDto = new YtsafetyRiskDataDTO();
            groupDto.setApplyReason(dto.getApplyReason());
            groupDto.setChangeType(dto.getChangeType());
            groupDto.setOpType(dto.getOpType());
            groupDto.setDetails(entry.getValue());
            futures.add(self.syncAdd(groupDto, user, roleCodes));
        }

        // 等待所有分组完成，收集结果
        List<YtsafetyRiskDataProcessVO> resultList = new ArrayList<>(orgGroupMap.size());
        for (Future<YtsafetyRiskDataProcess> future : futures) {
            try {
                YtsafetyRiskDataProcess process = future.get();
                if (process != null) {
                    YtsafetyRiskDataProcessVO vo = new YtsafetyRiskDataProcessVO();
                    BeanUtils.copyProperties(process, vo);
                    resultList.add(vo);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("批量推送被中断", e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                throw new RuntimeException(cause != null ? cause.getMessage() : "批量推送处理失败", cause);
            }
        }
        return resultList;
    }

    @Async
    public Future<YtsafetyRiskDataProcess> syncAdd(YtsafetyRiskDataDTO dto, LoginUser user, Set<String> roleCodes) {
        return new AsyncResult<>(doAdd(dto, user, roleCodes, true));
    }


    @Override
    public YtsafetyRiskDataProcessVO queryById(Long id) {
        YtsafetyRiskDataProcessVO vo = processMapper.queryById(id);
        LoginUser user = BusUtits.getLoginUser();
        BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(vo.getId(), user.getEipName());
        BeanUtils.copyProperties(detailsVO, vo);
        List<FieldAuth> fieldAuths = safetySummaryProcessService.queryFieldAuth(id);
        vo.setIsCanEdit(isProcessEdit(fieldAuths, vo.getIsCanApprove(),vo.getIsCanEdit()));
        List<YtsafetyRiskDataDetailsVO> details = detailsService.queryList(id);
        if (vo.getChangeType() != null && (Objects.equals(vo.getChangeType(), ApplyTypeEnum.CHANGE_MODIFY.getCode()) ||Objects.equals(vo.getChangeType(), ApplyTypeEnum.BATCH_PUSH.getCode()))) {
            compareDetailsWithExisting(details);
        }
        vo.setDetails(details);
        return vo;
    }

    private boolean isProcessEdit(List<FieldAuth>  fieldAuths, Boolean isCanApprove,Boolean isCanEdit ){
        if(isCanEdit) return true;
        if(fieldAuths==null|| fieldAuths.isEmpty()) return false;
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> "proposal_approval".equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        if(fieldAuth==null) return false;
        return isCanApprove;
    }

    @Override
    public List<YtsafetyRiskDataDetailsVO> importData(YtsafetyRiskDataDTO dto, List<YtsafetyRiskDataDetailsImportVO> excelData) {
        if (excelData == null || excelData.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 校验风险类型和厂区是否所有数据都一致
        Set<String> riskTypeNames = excelData.stream()
                .map(YtsafetyRiskDataDetailsImportVO::getRiskTypeName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> factoryNames = excelData.stream()
                .map(YtsafetyRiskDataDetailsImportVO::getFactoryName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (riskTypeNames.size() > 1) {
            throw new IllegalArgumentException("导入数据中的风险类型不一致，请检查");
        }
        if (factoryNames.size() > 1) {
            throw new IllegalArgumentException("导入数据中的厂区不一致，请检查");
        }

        String riskTypeName = riskTypeNames.iterator().next();
        String factoryName = factoryNames.iterator().next();

        // 2. 获取厂区主键
        Long factoryId = getFactoryIdByName(factoryName);

        // 3. 构建风险类型字典缓存（按风险类型过滤，key=riskIdentifyName）
        Map<String, YtSafetyRiskTypeDictVO> riskIdentifyMap = buildRiskIdentifyMap(riskTypeName);

        // 4. 收集所有唯一的风险类别，批量构建分级标准缓存
        // key: riskCategoryId_type_description → YtsafetyRiskGradingStandard
        Map<String, YtsafetyRiskGradingStandard> gradingCache = new HashMap<>();
        Set<String> uniqueCategories = excelData.stream()
                .map(YtsafetyRiskDataDetailsImportVO::getRiskIdentifyName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (String categoryName : uniqueCategories) {
            YtSafetyRiskTypeDictVO dictVO = findCategoryDict(riskIdentifyMap, categoryName);
            if (dictVO != null) {
                loadGradingStandards(dictVO.getRiskTypeId(), dictVO.getRiskCategoryId(), gradingCache);
            }
        }

        // 5. 构建位置信息缓存（按厂区过滤，key=locationName）
        Map<String, YtsafetyLocationInfo> locationMap = buildLocationMap(factoryId);

        // 6. 构建作业环节缓存（按厂区过滤，key=linkName）
        Map<String, YtsafetyHomeworkStage> jobStepMap = buildJobStepMap(factoryId, excelData);

        // 7. 逐行转换
        List<YtsafetyRiskDataDetailsVO> result = new ArrayList<>();
        for (YtsafetyRiskDataDetailsImportVO row : excelData) {
            YtsafetyRiskDataDetailsVO vo = convertImportRow(row, factoryId, riskTypeName,
                    riskIdentifyMap, locationMap, jobStepMap, gradingCache);
            vo.setProcessId(dto.getId());
            result.add(vo);
        }

        // 与已有风险数据进行字段比对
        if (dto.getChangeType() != null && (dto.getChangeType().equals(ApplyTypeEnum.BATCH_PUSH.getCode())||dto.getChangeType().equals(ApplyTypeEnum.BATCH_ADD.getCode()))) {
            compareDetailsWithExisting(result);
        }
        return result;
    }

    @Override
    public List<YtSafetyRiskTypeDictVO> queryRiskTypeDict() {
        List<SafetyDictItem> allItems = sysDictItemService.lambdaQuery()
                .eq(SafetyDictItem::getSupperCode, "RISK_TYPE")
                .eq(SafetyDictItem::getIsUse, "1")
                .list();

        if (allItems == null || allItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, SafetyDictItem> idMap = allItems.stream()
                .collect(Collectors.toMap(SafetyDictItem::getId, item -> item, (a, b) -> a));

        Set<Long> parentIds = allItems.stream()
                .map(SafetyDictItem::getPid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SafetyDictItem> leafItems = allItems.stream()
                .filter(item -> !parentIds.contains(item.getId()))
                .collect(Collectors.toList());

        List<YtSafetyRiskTypeDictVO> result = new ArrayList<>();
        for (SafetyDictItem leaf : leafItems) {
            YtSafetyRiskTypeDictVO vo = new YtSafetyRiskTypeDictVO();
            vo.setRiskIdentifyId(leaf.getId());
            vo.setRiskIdentifyName(leaf.getItemName());

            SafetyDictItem riskFactor = idMap.get(leaf.getPid());
            if (riskFactor != null) {

                vo.setRiskCategoryId(riskFactor.getId());
                vo.setRiskCategoryCode(riskFactor.getItemCode());
                vo.setRiskCategoryName(riskFactor.getItemName());

                SafetyDictItem riskCategory = idMap.get(riskFactor.getPid());
                if (riskCategory != null) {
                    vo.setRiskFactorId(riskCategory.getId());
                    vo.setRiskFactorName(riskCategory.getItemName());
                    SafetyDictItem riskType = idMap.get(riskCategory.getPid());
                    if (riskType != null) {
                        vo.setRiskTypeId(riskType.getId());
                        vo.setRiskTypeName(riskType.getItemName());
                    }
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        LoginUser user = BusUtits.getLoginUser();
        processParamDTO.setLoginName(user.getEipName());
        processParamDTO.setNodeHandler(user.getEipName());
        processParamDTO.setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        YtsafetyRiskDataProcess process = processMapper.selectById(processParamDTO.getFormInstanceId());
        processMapper.updateById(process);
        return safetySummaryProcessService.executeProcess(processParamDTO);
    }

    /**
     * 依据员工号查询用户角色编码集合
     */
    private Set<String> getUserRoleCodes(String empNo) {
        return userAuthApiService.queryUserRoleCodes(empNo);
    }

    @NotNull
    private  YtsafetyRiskDataDetailsVO getYtsafetyRiskDataDetailsVO(YtsafetyRiskDataDetailsImportVO row) {
        YtsafetyRiskDataDetailsVO vo = new YtsafetyRiskDataDetailsVO();
        vo.setRiskCode(row.getRiskCode());
        vo.setLocationName(row.getLocationName());
        vo.setRiskIdentifyName(row.getRiskIdentifyName());
        vo.setJobStepName(row.getJobStepName());
        vo.setCheckObject(row.getCheckObject());
        vo.setRiskDescription(row.getRiskDescription());
        vo.setPossibilityName(row.getPossibilityName());
        vo.setSeverityName(row.getSeverityName());
        return vo;
    }

    /**
     * 根据变更类型初始化流程模型标识和主题
     */
    private void initProcessMeta(YtsafetyRiskDataProcess process, Integer changeType, String dateStr, String realname) {
        if (changeType == ApplyTypeEnum.ADD.getCode().intValue()||changeType==ApplyTypeEnum.CHANGE_MODIFY.getCode().intValue()) {
            process.setModelFlag(MODEL_FLAG_ADD);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_ADD);
        } else {
            process.setModelFlag(MODEL_FLAG_BATCH);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_BATCH);
        }
    }

    /**
     * 验证明细数据中的厂区和风险类型是否一致，并同步到流程表
     */
    private void validateAndSyncDetails(YtsafetyRiskDataProcess process, List<YtsafetyRiskDataDetailsDTO> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        Set<Long> factoryIds = details.stream()
                .map(YtsafetyRiskDataDetailsDTO::getFactoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> riskTypeIds = details.stream()
                .map(YtsafetyRiskDataDetailsDTO::getRiskTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (factoryIds.size() > 1) {
            throw new IllegalArgumentException("明细数据中的厂区不一致，请检查");
        }
        if (riskTypeIds.size() > 1) {
            throw new IllegalArgumentException("明细数据中的风险类型不一致，请检查");
        }
        YtsafetyRiskDataDetailsDTO firstDetail = details.get(0);
        process.setFactoryId(firstDetail.getFactoryId());
        process.setFactoryName(firstDetail.getFactoryName());
        if (firstDetail.getRiskTypeId() != null) {
            process.setRiskTypeId(firstDetail.getRiskTypeId());
            process.setRiskTypeName(firstDetail.getRiskTypeName());
        }
        String highestRiskLevel = details.stream()
                .map(YtsafetyRiskDataDetailsDTO::getRiskLevel)
                .filter(Objects::nonNull)
                .map(RiskLevelEnum::getByCode)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(RiskLevelEnum::getSort))
                .map(RiskLevelEnum::getCode)
                .orElse(null);
        process.setRiskLevel(highestRiskLevel);
    }

    @Async
    public void submitProcess(YtsafetyRiskDataProcess process, Integer opType) {
        doSubmitProcess(process, opType, BusUtits.getLoginUser());
    }

    private void doSubmitProcess(YtsafetyRiskDataProcess process, Integer opType, LoginUser user) {
        BusUtits.sleepBeforeSubmit();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(process.getModelFlag());
        dto.setFormInstanceId(process.getId());
        dto.setFlowCode(process.getFlowCode());
        dto.setFlowSubject(process.getFlowSubject());
        dto.setCurNodeHandler(process.getSuperApproval());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
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

    /**
     * 填充详情数据（批量生成风险编码，避免逐条DB调用）
     */
    private void generateDetailRiskCodes(List<YtsafetyRiskDataDetailsDTO> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        // 按风险类型分组，批量生成风险编码
        Map<String, List<YtsafetyRiskDataDetailsDTO>> groupMap = new LinkedHashMap<>();
        for (YtsafetyRiskDataDetailsDTO detail : details) {
            if (StringUtils.isBlank(detail.getRiskCode()) && StringUtils.isNotBlank(detail.getRiskTypeName())) {
                groupMap.computeIfAbsent(detail.getRiskTypeName(), k -> new ArrayList<>()).add(detail);
            }
        }
        for (Map.Entry<String, List<YtsafetyRiskDataDetailsDTO>> entry : groupMap.entrySet()) {
            String riskTypeName = entry.getKey();
            List<YtsafetyRiskDataDetailsDTO> group = entry.getValue();
            String abbr = RISK_TYPE_ABBR_MAP.getOrDefault(riskTypeName, riskTypeName);
            Integer year = DateUtils.getYear();
            String dataStr = year + "-01-01";
            Date date = DateUtils.parseShortDate(dataStr);
            List<String> sequences = ytSafetyWaterFlowNoService.batchGetNowByTypeDayNum("RiskCode", date, group.size());
            for (int i = 0; i < group.size(); i++) {
                group.get(i).setRiskCode(String.format("FX-%s-%s%s", abbr, year, sequences.get(i)));
            }
        }
        for (YtsafetyRiskDataDetailsDTO detail : details) {
            detail.setSafetyRoleCode(roleCodeConstant.getSafetyOfficer());
            detail.setManagerRoleCode(roleCodeConstant.getSafetySupervisor());
        }
    }

    /**
     * 生成风险编码
     * @param riskTypeName
     * @return
     */
    public String generateRiskCode(String riskTypeName) {
        String abbr = RISK_TYPE_ABBR_MAP.getOrDefault(riskTypeName, riskTypeName);
        Integer year = DateUtils.getYear();
        String dataStr = year + "-01-01";
        String sequence = ytSafetyWaterFlowNoService.getNowByTypeDayNum("RiskCode", DateUtils.parseShortDate(dataStr));
        return String.format("FX-%s-%s%s", abbr, year, sequence);
    }

    /**
     * 生成风险流程编码
     * @return
     */
    public String generateTaskCode() {
        String dateStr = DateUtils.formatDate(new Date(),"yyyyMMdd");
        String sequence = ytSafetyWaterFlowNoService.getNowByTypeDayNum("RiskFlowCode", DateUtils.parseDatetime(DateUtils.formatDate(new Date())));
        return String.format("FXSJK%s%s", dateStr, sequence);
    }

    // ======================== importData 辅助方法 ========================

    /**
     * 根据厂区名称获取厂区主键
     */
    private Long getFactoryIdByName(String factoryName) {
        return safetyCommonService.queryFactoryDrop().stream()
                .filter(f -> factoryName.equals(f.getFieldName()))
                .findFirst()
                .map(SafetyFieldManageVO::getId)
                .orElseThrow(() -> new IllegalArgumentException("厂区不存在: " + factoryName));
    }

    /**
     * 构建风险识别维度缓存（按风险类型过滤）
     * key = riskIdentifyName, value = 完整的四级分类信息
     */
    private Map<String, YtSafetyRiskTypeDictVO> buildRiskIdentifyMap(String riskTypeName) {
        return queryRiskTypeDict().stream()
                .filter(v -> riskTypeName.equals(v.getRiskTypeName()))
                .collect(Collectors.toMap(
                        YtSafetyRiskTypeDictVO::getRiskIdentifyName,
                        v -> v,
                        (a, b) -> a));
    }

    /**
     * 从缓存中按风险类别名称查找字典VO
     */
    private YtSafetyRiskTypeDictVO findCategoryDict(Map<String, YtSafetyRiskTypeDictVO> riskIdentifyMap,
                                                     String riskCategoryName) {
        return riskIdentifyMap.get(riskCategoryName);
    }

    /**
     * 批量加载分级标准到缓存
     * key格式: riskCategoryId_type_description
     */
    private void loadGradingStandards(Long riskTypeId, Long riskCategoryId,
                                       Map<String, YtsafetyRiskGradingStandard> cache) {
        SafetyDictItem riskTypeItem = sysDictItemService.getById(riskTypeId);
        if (riskTypeItem == null) {
            return;
        }
        Integer riskType = "ONE_FIRE_PC".equals(riskTypeItem.getItemCode()) ? 2 : 1;

        SafetyDictItem categoryItem = sysDictItemService.getById(riskCategoryId);
        if (categoryItem == null) {
            return;
        }
        Integer formula = (categoryItem.getItemCode() != null && categoryItem.getItemCode().endsWith("FPC")) ? 2 : 1;

        List<YtsafetyRiskGradingStandard> standards = riskGradingStandardService.lambdaQuery()
                .eq(YtsafetyRiskGradingStandard::getRiskType, riskType)
                .eq(YtsafetyRiskGradingStandard::getFormula, formula)
                .in(YtsafetyRiskGradingStandard::getType, Arrays.asList(1, 2, 3))
                .list();

        for (YtsafetyRiskGradingStandard std : standards) {
            String key = riskCategoryId + "_" + std.getType() + "_" + std.getDescription();
            cache.put(key, std);
        }
    }

    /**
     * 构建位置信息缓存（按厂区过滤）
     * key = locationName
     */
    private Map<String, YtsafetyLocationInfo> buildLocationMap(Long factoryId) {
        return locationInfoService.lambdaQuery()
                .eq(YtsafetyLocationInfo::getFactoryId, factoryId)
                .eq(YtsafetyLocationInfo::getDelFlag, 0)
                .list().stream()
                .collect(Collectors.toMap(
                        YtsafetyLocationInfo::getLocationName,
                        v -> v,
                        (a, b) -> a));
    }

    /**
     * 构建作业环节缓存（按厂区过滤，仅当存在 jobStepName 时加载）
     * key = linkName
     */
    private Map<String, YtsafetyHomeworkStage> buildJobStepMap(Long factoryId,
                                                                List<YtsafetyRiskDataDetailsImportVO> excelData) {
        boolean hasJobStep = excelData.stream()
                .anyMatch(v -> StringUtils.isNotBlank(v.getJobStepName()));
        if (!hasJobStep) {
            return Collections.emptyMap();
        }
        return homeworkStageService.lambdaQuery()
                .eq(YtsafetyHomeworkStage::getFactoryId, factoryId)
                .eq(YtsafetyHomeworkStage::getDelFlag, 0)
                .list().stream()
                .collect(Collectors.toMap(
                        YtsafetyHomeworkStage::getLinkName,
                        v -> v,
                        (a, b) -> a));
    }

    /**
     * 将单行导入VO转换为详情VO
     */
    private YtsafetyRiskDataDetailsVO convertImportRow(YtsafetyRiskDataDetailsImportVO row,
                                                        Long factoryId, String riskTypeName,
                                                        Map<String, YtSafetyRiskTypeDictVO> riskIdentifyMap,
                                                        Map<String, YtsafetyLocationInfo> locationMap,
                                                        Map<String, YtsafetyHomeworkStage> jobStepMap,
                                                        Map<String, YtsafetyRiskGradingStandard> gradingCache) {
        YtsafetyRiskDataDetailsVO vo = getYtsafetyRiskDataDetailsVO(row);
        vo.setId(IdWorker.getId());
        vo.setPreventMeasure(row.getPreventMeasure());

        // 补全风险分类信息
        YtSafetyRiskTypeDictVO riskDict = riskIdentifyMap.get(row.getRiskIdentifyName());
        if (riskDict != null) {
            vo.setRiskTypeId(riskDict.getRiskTypeId());
            vo.setRiskTypeName(riskDict.getRiskTypeName());
            vo.setRiskCategoryId(riskDict.getRiskCategoryId());
            vo.setRiskCategoryCode(riskDict.getRiskCategoryCode());
            vo.setRiskCategoryName(riskDict.getRiskCategoryName());
            vo.setRiskFactorId(riskDict.getRiskFactorId());
            vo.setRiskFactorName(riskDict.getRiskFactorName());
            vo.setRiskIdentifyId(riskDict.getRiskIdentifyId());
        } else {
            row.addError("风险类别[" + row.getRiskIdentifyName() + "]在系统中不存在");
        }

        // 补全位置信息（带出厂区、区域、组织）
        YtsafetyLocationInfo location = locationMap.get(row.getLocationName());
        if (location != null) {
            vo.setFactoryId(location.getFactoryId());
            vo.setFactoryName(location.getFactoryName());
            vo.setAreaId(location.getAreaId());
            vo.setAreaName(location.getAreaName());
            vo.setOrgId(location.getOrgId());
            vo.setOrgCode(location.getOrgCode());
            vo.setOrgName(location.getOrgName());
            vo.setLocationId(location.getId());
            vo.setLocationName(location.getLocationName());
        } else {
            row.addError("位置[" + row.getLocationName() + "]在系统中不存在");
        }

        // 补全作业环节信息
        if (StringUtils.isNotBlank(row.getJobStepName())) {
            YtsafetyHomeworkStage stage = jobStepMap.get(row.getJobStepName());
            if (stage != null) {
                vo.setJobStepId(stage.getId());
                vo.setJobStepCode(stage.getLinkCode());
                vo.setJobStepName(stage.getLinkName());
            } else {
                row.addError("作业环节[" + row.getJobStepName() + "]在系统中不存在");
            }
        }

        // 补全分级标准信息（暴露频率、可能性、严重性）
        Long riskCategoryId = vo.getRiskCategoryId();
        if (riskCategoryId != null) {
            resolveGradingStandard(riskCategoryId, 1, row.getExposeFreqName(), gradingCache,
                    vo::setExposeFreqId, vo::setExposeFreqName, row, "风险暴露频率");
            resolveGradingStandard(riskCategoryId, 2, row.getPossibilityName(), gradingCache,
                    vo::setPossibilityId, vo::setPossibilityName, row, "风险可能性");
            resolveGradingStandard(riskCategoryId, 3, row.getSeverityName(), gradingCache,
                    vo::setSeverityId, vo::setSeverityName, row, "后果严重性");
        }

        // 计算风险等级
        YTSafetyRiskLevelDict riskLevel = riskLevelDictService.calculateRiskLevel(
                vo.getExposeFreqId(), vo.getPossibilityId(), vo.getSeverityId());
        if (riskLevel != null) {
            vo.setRiskValue(riskLevel.getRiskValue());
            vo.setRiskWarnColor(riskLevel.getRiskColor());
            vo.setRiskLevel(riskLevel.getRiskLevel());
        }

        // 生成风险编码
        if (StringUtils.isBlank(vo.getRiskCode()) && StringUtils.isNotBlank(vo.getRiskTypeName())) {
            vo.setRiskCode(generateRiskCode(vo.getRiskTypeName()));
        }

        // 设置角色编码
        vo.setSafetyRoleCode(roleCodeConstant.getSafetyOfficer());
        vo.setManagerRoleCode(roleCodeConstant.getSafetySupervisor());

        // 查询安全员名称和安全主管名称
        String orgCode = vo.getOrgCode();
        if (StringUtils.isNotBlank(orgCode)) {
            if (StringUtils.isNotBlank(vo.getSafetyRoleCode())) {
                List<SafetyOfficerVO> safetyOfficers = safetyCommonService.queryGroupInfoRoles(orgCode, vo.getSafetyRoleCode());
                if (safetyOfficers != null && !safetyOfficers.isEmpty()) {
                    vo.setSafetyNames(safetyOfficers.stream()
                            .map(SafetyOfficerVO::getEmpName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(",")));
                }
            }
            if (StringUtils.isNotBlank(vo.getManagerRoleCode())) {
                List<SafetyOfficerVO> managers = safetyCommonService.queryGroupInfoRoles(orgCode, vo.getManagerRoleCode());
                if (managers != null && !managers.isEmpty()) {
                    vo.setManagerNames(managers.stream()
                            .map(SafetyOfficerVO::getEmpName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(",")));
                }
            }
        }

        return vo;
    }

    /**
     * 从缓存中匹配分级标准
     */
    private void resolveGradingStandard(Long riskCategoryId, Integer type, String description,
                                         Map<String, YtsafetyRiskGradingStandard> cache,
                                         Consumer<Long> idSetter,
                                         Consumer<String> nameSetter,
                                         YtsafetyRiskDataDetailsImportVO row,
                                         String fieldName) {
        if (StringUtils.isBlank(description)) {
            return;
        }
        String key = riskCategoryId + "_" + type + "_" + description;
        YtsafetyRiskGradingStandard std = cache.get(key);
        if (std != null) {
            idSetter.accept(std.getId());
            nameSetter.accept(std.getDescription());
        } else {
            row.addError(fieldName + "[" + description + "]在系统中不存在");
        }
    }

    /**
     * 批量查询已有风险数据并与新数据进行字段比对
     */
    private void compareDetailsWithExisting(List<YtsafetyRiskDataDetailsVO> details) {
        List<String> riskCodes = details.stream()
                .map(YtsafetyRiskDataDetailsVO::getRiskCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (riskCodes.isEmpty()) {
            return;
        }
        List<YtsafetyRiskData> riskDataList = riskDataMapper.selectList(
                new LambdaQueryWrapper<YtsafetyRiskData>()
                        .in(YtsafetyRiskData::getRiskCode, riskCodes)
                        .eq(YtsafetyRiskData::getDelFlag, 0));
        if (riskDataList.isEmpty()) {
            return;
        }
        Map<String, YtsafetyRiskData> riskDataMap = riskDataList.stream()
                .collect(Collectors.toMap(
                        YtsafetyRiskData::getRiskCode,
                        r -> r,
                        (a, b) -> a));
        for (YtsafetyRiskDataDetailsVO detail : details) {
            YtsafetyRiskData existing = riskDataMap.get(detail.getRiskCode());
            if (existing != null) {
                YtsafetyRiskDataDetailsVO existingVo = new YtsafetyRiskDataDetailsVO();
                BeanUtils.copyProperties(existing, existingVo);
                compareWithExisting(detail, existingVo);
            }
        }
    }

    /**
     * 与已有数据进行字段比对，记录差异字段编码和原有值
     */
    private void compareWithExisting(YtsafetyRiskDataDetailsVO newVo, YtsafetyRiskDataDetailsVO existing) {
        List<String> fieldCodes = new ArrayList<>();
        HashMap<String, String> fieldsValues = new HashMap<>();

        compareField("riskTypeName", newVo.getRiskTypeName(), existing.getRiskTypeName(), fieldCodes, fieldsValues);
        compareField("jobStepName", newVo.getJobStepName(), existing.getJobStepName(), fieldCodes, fieldsValues);
        compareField("factoryName", newVo.getFactoryName(), existing.getFactoryName(), fieldCodes, fieldsValues);
        compareField("areaName", newVo.getAreaName(), existing.getAreaName(), fieldCodes, fieldsValues);
        compareField("orgName", newVo.getOrgName(), existing.getOrgName(), fieldCodes, fieldsValues);
        compareField("locationName", newVo.getLocationName(), existing.getLocationName(), fieldCodes, fieldsValues);
        compareField("checkObject", newVo.getCheckObject(), existing.getCheckObject(), fieldCodes, fieldsValues);
        compareField("riskFactorName", newVo.getRiskFactorName(), existing.getRiskFactorName(), fieldCodes, fieldsValues);
        compareField("riskCategoryName", newVo.getRiskCategoryName(), existing.getRiskCategoryName(), fieldCodes, fieldsValues);
        compareField("riskIdentifyName", newVo.getRiskIdentifyName(), existing.getRiskIdentifyName(), fieldCodes, fieldsValues);
        compareField("exposeFreqName", newVo.getExposeFreqName(), existing.getExposeFreqName(), fieldCodes, fieldsValues);
        compareField("possibilityName", newVo.getPossibilityName(), existing.getPossibilityName(), fieldCodes, fieldsValues);
        compareField("severityName", newVo.getSeverityName(), existing.getSeverityName(), fieldCodes, fieldsValues);
        compareField("riskDescription", newVo.getRiskDescription(), existing.getRiskDescription(), fieldCodes, fieldsValues);
        compareField("preventMeasure", newVo.getPreventMeasure(), existing.getPreventMeasure(), fieldCodes, fieldsValues);

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
