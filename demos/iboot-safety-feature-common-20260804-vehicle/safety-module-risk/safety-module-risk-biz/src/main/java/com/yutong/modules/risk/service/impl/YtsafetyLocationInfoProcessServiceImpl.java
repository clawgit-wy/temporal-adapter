package com.yutong.modules.risk.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.bpm.IYtsafetyProcessNodeConfigService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.constant.enums.ProcessOpTypeEnum;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.utils.PinyinUtil;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.constants.enums.ApplyTypeEnum;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoDTO;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoProcessDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.mapper.YtsafetyLocationInfoProcessMapper;
import com.yutong.modules.risk.service.*;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoImportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessVO;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.UserAuthApiService;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import com.yutong.modules.user.vo.SafetyGroupInfoTreeVO;
import org.jeecg.common.util.DateUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @Description: 位置信息流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Service
public class YtsafetyLocationInfoProcessServiceImpl extends ServiceImpl<YtsafetyLocationInfoProcessMapper, YtsafetyLocationInfoProcess> implements IYtsafetyLocationInfoProcessService {

    private static final String MODEL_FLAG_ADD = "ytsafety_location_info_add";
    private static final String FLOW_SUBJECT_ADD = "发起的位置信息添加流程";
    private static final String MODEL_FLAG_BATCH = "ytsafety_location_info_batch";
    private static final String FLOW_SUBJECT_BATCH = "发起的位置信息批量变更流程";
    private static final String FLOW_CODE_PREFIX = "WZXX";
    private static final String LOCATION_CODE_PREFIX = "WZ";

    @Autowired
    private YtsafetyLocationInfoProcessMapper processMapper;

    @Autowired
    private IYtsafetyLocationInfoProcessDetailsService detailsService;

    @Autowired
    private IYtsafetyLocationInfoService locationInfoService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private UserAuthApiService userAuthApiService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    YTSafetyWaterFlowNoService ytSafetyWaterFlowNoService;

    @Autowired
    IYtsafetyProcessNodeConfigService processNodeConfigService;

    @Autowired
    @Lazy
    private YtsafetyLocationInfoProcessServiceImpl self;

    @Resource(name = "parallelIoExecutor")
    private Executor parallelIoExecutor;

    @Override
    public YtsafetyLocationInfoProcess add(YtsafetyLocationInfoDTO dto) {
        LoginUser user = BusUtits.getLoginUser();

        // 并行获取用户角色和保卫状态，两个远程调用互不依赖
        CompletableFuture<Set<String>> roleCodesFuture = CompletableFuture.supplyAsync(
                () -> userAuthApiService.queryUserRoleCodes(user.getEmpNo()), parallelIoExecutor);
        CompletableFuture<Long> defendCountFuture = CompletableFuture.supplyAsync(
                () -> safetyCommonService.checkDefend(user.getEmpNo()), parallelIoExecutor);

        Set<String> roleCodes = roleCodesFuture.join();
        boolean isSupper = roleCodes != null && (roleCodes.contains(roleCodeConstant.getSysAdminRoleCode())
                || roleCodes.contains(roleCodeConstant.getBusinessSystemLead()));
        Long defendCount = defendCountFuture.join();
        return doAdd(dto, user, isSupper ? 1 : 0, defendCount != null && defendCount > 0 ? 1 : 0);
    }

    /**
     * 核心添加逻辑，接受已缓存的用户信息和派生值，避免重复远程调用
     */
    private YtsafetyLocationInfoProcess doAdd(YtsafetyLocationInfoDTO dto, LoginUser user,
                                               Integer isSupper, Integer isGuard) {
        if(dto.getDetails()==null||dto.getDetails().isEmpty()) throw new RuntimeException("列表明细不能为空");
        YtsafetyLocationInfoProcess process = new YtsafetyLocationInfoProcess();
        BeanUtils.copyProperties(dto, process);
        Long id = IdWorker.getId();
        process.setId(id);
        process.setApplyNo(user.getEmpNo());
        process.setApplyLogin(user.getEipName());
        process.setApplyName(user.getRealname());
        process.setApplyDepartCode(user.getOrgCode());
        process.setApplyDepartName(user.getOrgName());
        process.setFlowCode(generateFlowCode());
        process.setIsSupper(isSupper);
        process.setIsGuard(isGuard);

        initProcessMeta(process, dto.getChangeType(), DateUtils.formatDate(), user.getRealname());
        syncFactoryFromDetails(process, dto.getDetails());
        generateLocationCodes(dto.getDetails());
        if(dto.getOpType()==1) {
            fillApproversParallel(process, dto.getDetails());
        }
        processMapper.insert(process);
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            detailsService.save(process.getId(), dto.getDetails(), user);
        }
        doSubmitProcess(process, dto.getOpType(), user);
        return process;
    }

    /**
     * 获取提案人
     * @param nodeFlag
     * @param isGuard
     * @param factoryId
     * @param details
     * @return
     */
    private String queryBatchOrgCodeApprovers(String nodeFlag, Integer isGuard,List<YtsafetyLocationInfoProcessDetailsDTO> details){
        List<String> orgCodes = details.stream().map(YtsafetyLocationInfoProcessDetailsDTO::getOrgCode).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        return processNodeConfigService.queryProposalApprovers("ytsafety_location_info",nodeFlag,isGuard==1?"保卫":"非保卫",orgCodes);
    }

    private String queryApprovers(String nodeFlag, Integer isGuard,Long factoryId,String orgCode){
       return processNodeConfigService.queryApprovers("ytsafety_location_info",nodeFlag,isGuard==1?"保卫":"非保卫",factoryId,orgCode);
    }

    /**
     * 并行查询并设置流程各节点审批人（审批、批准、提案、备案），多个远程调用并行执行
     */
    private void fillApproversParallel(YtsafetyLocationInfoProcess process,
                                        List<YtsafetyLocationInfoProcessDetailsDTO> details) {
        CompletableFuture<String> superApprovalFuture = null;
        CompletableFuture<String> authorizeApprovalFuture = null;
        CompletableFuture<String> proposalApprovalFuture = null;
        CompletableFuture<String> recordApprovalFuture = null;

        if (process.getIsSupper() == 0) {
            if (process.getIsGuard() == 0) {
                superApprovalFuture = CompletableFuture.supplyAsync(
                        () -> queryApprovers("approve", 0, process.getFactoryId(), process.getApplyDepartCode()),
                        parallelIoExecutor);
            }
            authorizeApprovalFuture = CompletableFuture.supplyAsync(
                    () -> queryApprovers("authorize", process.getIsGuard(), process.getFactoryId(), process.getApplyDepartCode()),
                    parallelIoExecutor);
        }
        if (Objects.equals(ApplyTypeEnum.BATCH_PUSH.getCode(), process.getChangeType())) {
            proposalApprovalFuture = CompletableFuture.supplyAsync(
                    () -> queryBatchOrgCodeApprovers("proposal", process.getIsGuard(), details),
                    parallelIoExecutor);
        }
        recordApprovalFuture = CompletableFuture.supplyAsync(
                () -> queryApprovers("record", process.getIsGuard(), process.getFactoryId(), process.getApplyDepartCode()),
                parallelIoExecutor);

        if (superApprovalFuture != null) {
            String superApproval = superApprovalFuture.join();
            if (StringUtils.isEmpty(superApproval))
                throw new RuntimeException("待审批人员不能为空请配置后在保存");
            process.setSuperApproval(superApproval);
        }
        if (authorizeApprovalFuture != null) {
            String authorizeApproval = authorizeApprovalFuture.join();
            if (StringUtils.isEmpty(authorizeApproval))
                throw new RuntimeException("待批准人员不能为空请配置后在保存");
            process.setAuthorizeApproval(authorizeApproval);
        }
        if (proposalApprovalFuture != null) {
            String proposalApproval = proposalApprovalFuture.join();
            if (StringUtils.isEmpty(proposalApproval))
                throw new RuntimeException("提案人员不能为空请配置后在保存");
            process.setProposalApproval(proposalApproval);
        }
        String recordApproval = recordApprovalFuture.join();
        process.setRecordApproval(recordApproval);
    }

    /**
     * 根据变更类型初始化流程模型标识和主题
     */
    private void initProcessMeta(YtsafetyLocationInfoProcess process, Integer changeType, String dateStr, String realname) {
        if (changeType == ApplyTypeEnum.ADD.getCode().intValue() || changeType == ApplyTypeEnum.CHANGE_MODIFY.getCode().intValue()) {
            process.setModelFlag(MODEL_FLAG_ADD);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_ADD);
        } else {
            process.setModelFlag(MODEL_FLAG_BATCH);
            process.setFlowSubject(dateStr + realname + FLOW_SUBJECT_BATCH);

        }
    }

    /**
     * 获取流程编码
     * @return
     */
    public String generateFlowCode() {
        String dateStr = DateUtils.formatDate(new Date(),"yyyyMMdd");
        String sequence = ytSafetyWaterFlowNoService.getNowByTypeDayNum("LocationFlowCode", DateUtils.parseShortDate(DateUtils.formatDate(new Date())));
        return String.format("%s%s%s", FLOW_CODE_PREFIX, dateStr, sequence);
    }

    /**
     * 获取位置编码
     * @param factoryCode
     * @param areaName
     * @param locationName
     * @return
     */
    public String generateLocationCode(String factoryCode, String areaName, String locationName) {
        if(StringUtils.isNotEmpty(factoryCode)||StringUtils.isNotEmpty(areaName)||StringUtils.isNotEmpty(locationName)){
            return null;
        }
        Long sequence = ytSafetyWaterFlowNoService.autoIncrementKey("LocationCode");
        return generateLocationCode(factoryCode, areaName, locationName, sequence);
    }

    private String generateLocationCode(String factoryCode, String areaName, String locationName, Long sequence) {
        if(StringUtils.isEmpty(factoryCode)||StringUtils.isEmpty(areaName)||StringUtils.isEmpty(locationName)){
            return null;
        }
        String areaPart = PinyinUtil.getFirstLetter(areaName);
        String locationPart = PinyinUtil.getFirstLetterAndInt(locationName);
        return String.format("%s%s-%s-%s-%d", LOCATION_CODE_PREFIX, factoryCode, areaPart, locationPart, sequence);
    }

    private void syncFactoryFromDetails(YtsafetyLocationInfoProcess process, List<YtsafetyLocationInfoProcessDetailsDTO> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        Set<Long> factoryIds = details.stream()
                .map(YtsafetyLocationInfoProcessDetailsDTO::getFactoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (factoryIds.size() > 1) {
            throw new IllegalArgumentException("明细数据中的厂区不一致，请检查");
        }
        YtsafetyLocationInfoProcessDetailsDTO firstDetail = details.get(0);
        process.setFactoryId(firstDetail.getFactoryId());
        process.setFactoryName(firstDetail.getFactoryName());
    }

    @Override
    public void edit(YtsafetyLocationInfoDTO dto) {
        if(dto.getId()==null) throw new RuntimeException("编辑时主键不能为空");
        YtsafetyLocationInfoProcess process = processMapper.selectById(dto.getId());
        process.setApplyReason(dto.getApplyReason());
        syncFactoryFromDetails(process, dto.getDetails());
        generateLocationCodes(dto.getDetails());
        if(dto.getOpType()==1) {
            fillApproversParallel(process, dto.getDetails());
        }
        processMapper.updateById(process);
        detailsService.save(process.getId(), dto.getDetails(), BusUtits.getLoginUser());
        submitProcess(process, dto.getOpType());
    }

    @Override
    public List<YtsafetyLocationInfoProcessVO> batchPush(YtsafetyLocationInfoDTO dto) {
        if (dto == null || dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("批量推送的详情数据不能为空");
        }
        List<YtsafetyLocationInfoProcessDetailsDTO> allDetails = dto.getDetails();

        // 单次遍历：校验字段非空、厂区一致性、按orgId分组
        Long commonFactoryId = null;
        LinkedHashMap<Long, List<YtsafetyLocationInfoProcessDetailsDTO>> orgGroupMap = new LinkedHashMap<>();

        for (YtsafetyLocationInfoProcessDetailsDTO detail : allDetails) {
            if (detail.getFactoryId() == null) {
                throw new IllegalArgumentException("详情数据中存在厂区为空的记录，请检查");
            }
            if (detail.getOrgId() == null) {
                throw new IllegalArgumentException("详情数据中存在组织为空的记录，请检查");
            }

            if (commonFactoryId == null) {
                commonFactoryId = detail.getFactoryId();
            } else if (!commonFactoryId.equals(detail.getFactoryId())) {
                throw new RuntimeException("推送厂区不一致");
            }

            orgGroupMap.computeIfAbsent(detail.getOrgId(), k -> new ArrayList<>()).add(detail);
        }

        // 预获取共享数据，避免每个分组重复调用外部接口
        LoginUser user = BusUtits.getLoginUser();
        Set<String> roleCodes = userAuthApiService.queryUserRoleCodes(user.getEmpNo());
        boolean isSupper = roleCodes != null && (roleCodes.contains(roleCodeConstant.getSysAdminRoleCode())
                || roleCodes.contains(roleCodeConstant.getBusinessSystemLead()));
        Long defendCount = safetyCommonService.checkDefend(user.getEmpNo());
        Integer isGuard = defendCount != null && defendCount > 0 ? 1 : 0;
        Integer isSupperInt = isSupper ? 1 : 0;

        // 并行处理各组织分组（通过self代理真正触发@Async）
        List<Future<YtsafetyLocationInfoProcess>> futures = new ArrayList<>(orgGroupMap.size());
        for (Map.Entry<Long, List<YtsafetyLocationInfoProcessDetailsDTO>> entry : orgGroupMap.entrySet()) {
            YtsafetyLocationInfoDTO groupDto = new YtsafetyLocationInfoDTO();
            groupDto.setApplyReason(dto.getApplyReason());
            groupDto.setChangeType(dto.getChangeType());
            groupDto.setOpType(dto.getOpType());
            groupDto.setDetails(entry.getValue());
            futures.add(self.syncAdd(groupDto, user, isSupperInt, isGuard));
        }

        // 等待所有分组完成，收集结果
        List<YtsafetyLocationInfoProcessVO> resultList = new ArrayList<>(orgGroupMap.size());
        for (Future<YtsafetyLocationInfoProcess> future : futures) {
            try {
                YtsafetyLocationInfoProcess process = future.get();
                if (process != null) {
                    YtsafetyLocationInfoProcessVO vo = new YtsafetyLocationInfoProcessVO();
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
    public Future<YtsafetyLocationInfoProcess> syncAdd(YtsafetyLocationInfoDTO dto, LoginUser user,
                                                        Integer isSupper, Integer isGuard) {
        return new AsyncResult<>(doAdd(dto, user, isSupper, isGuard));
    }

    @Override
    public List<YtsafetyLocationInfoProcessDetailsVO> importData(YtsafetyLocationInfoDTO dto, List<YtsafetyLocationInfoImportVO> excelData) {
        if (excelData == null || excelData.isEmpty()) {
            return Collections.emptyList();
        }

        // 校验厂区是否所有数据都一致
        Set<String> factoryNames = excelData.stream()
                .map(YtsafetyLocationInfoImportVO::getFactoryName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (factoryNames.size() > 1) {
            throw new IllegalArgumentException("导入数据中的厂区不一致，请检查");
        }

        // 1. 查询厂区映射 (factoryName → {id, code})
        List<SafetyFieldManageVO> factorys = safetyCommonService.queryFactoryDrop();
        Map<String, SafetyFieldManageVO> factoryMap = factorys.stream()
                .filter(vo -> vo.getFieldName() != null)
                .collect(Collectors.toMap(SafetyFieldManageVO::getFieldName,
                        vo -> vo,
                        (oldVal, newVal) -> oldVal));

        // 2. 查询区域映射 (areaName → id)
        List<SafetyFieldManageVO> regions = safetyCommonService.queryAllRiskRegion();
        Map<String, SafetyFieldManageVO> regionMap = regions.stream()
                .filter(vo -> vo.getFieldName() != null)
                .collect(Collectors.toMap(SafetyFieldManageVO::getFieldName,
                        vo -> vo,
                        (oldVal, newVal) -> oldVal));

        // 3. 查询组织映射 (orgName → {id, code})
        Map<String, SafetyGroupInfoTreeVO> groupInfoMap = buildGroupInfoNameMap();

        // 4. 逐行转换Excel数据为VO，并依据名称反查补全主键字段
        List<YtsafetyLocationInfoProcessDetailsVO> result = new ArrayList<>();
        for (YtsafetyLocationInfoImportVO row : excelData) {
            YtsafetyLocationInfoProcessDetailsVO vo = new YtsafetyLocationInfoProcessDetailsVO();

            // 厂区
            SafetyFieldManageVO factory = factoryMap.get(row.getFactoryName());
            if (factory == null) {
                row.addError("厂区名称[" + row.getFactoryName() + "]在系统中不存在");
            } else {
                vo.setFactoryId(factory.getId());
                vo.setFactoryCode(factory.getFieldCode());
                vo.setFactoryName(factory.getFieldName());
            }

            // 区域
            SafetyFieldManageVO area = regionMap.get(row.getAreaName());
            if (area == null) {
                row.addError("区域名称[" + row.getAreaName() + "]在系统中不存在");
            } else {
                vo.setAreaId(area.getId());
                vo.setAreaCode(area.getFieldCode());
                vo.setAreaName(row.getAreaName());
                vo.setMainImage(area.getFileUrl());
            }
            String orgName = BusUtits.reverseCommaStr(row.getOrgName()) ;
            // 组织
            SafetyGroupInfoTreeVO groupInfo = groupInfoMap.get(orgName);
            if (groupInfo == null) {
                row.addError("组织名称[" + row.getOrgName() + "]在系统中不存在");
            } else {
                vo.setOrgId(groupInfo.getId());
                vo.setOrgCode(groupInfo.getCode());
                vo.setOrgName(row.getOrgName());
            }

            // 位置
            vo.setLocationName(row.getLocationName());
            vo.setLocationCode(row.getLocationCode());

            result.add(vo);
        }
        return result;
    }

    /**
     * 构建组织名称→组织信息映射（按叶子节点名称索引）
     */
    private Map<String, SafetyGroupInfoTreeVO> buildGroupInfoNameMap() {
        return safetyCommonService.queryGroupInfoFactoryMap();
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        if(processParamDTO.getFormInstanceId()==null) throw new RuntimeException("实例主键不能为空");
        LoginUser user = BusUtits.getLoginUser();
        processParamDTO.setLoginName(user.getEipName());
        processParamDTO.setNodeHandler(user.getEipName());
        processParamDTO.setNodeHandlerName(user.getRealname() + "(" + user.getEmpNo() + ")");
        YtsafetyLocationInfoProcess process = processMapper.selectById(processParamDTO.getFormInstanceId());
        processMapper.updateById(process);
        return safetySummaryProcessService.executeProcess(processParamDTO);
    }

    /**
     * 依据员工号查询用户角色编码集合
     */
    private Set<String> getUserRoleCodes(String empNo) {
        return userAuthApiService.queryUserRoleCodes(empNo);
    }

    @Override
    public YtsafetyLocationInfoProcessVO queryById(Long id) {
        YtsafetyLocationInfoProcessVO vo = processMapper.queryById(id);
        LoginUser user = BusUtits.getLoginUser();
        BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(vo.getId(), user.getEipName());
        BeanUtils.copyProperties(detailsVO, vo);
        List<FieldAuth> fieldAuths = safetySummaryProcessService.queryFieldAuth(id);
        vo.setIsCanEdit(isProcessEdit(fieldAuths, vo.getIsCanApprove(),vo.getIsCanEdit()));
        List<YtsafetyLocationInfoProcessDetailsVO> details = detailsService.queryList(id);
        if(details==null||details.isEmpty()) return vo;
        vo.setDetails(details);

        if (vo.getChangeType()!=null&&(vo.getChangeType().equals(ApplyTypeEnum.CHANGE_MODIFY.getCode()) || vo.getChangeType().equals(ApplyTypeEnum.BATCH_PUSH.getCode()))) {
            Map<String, YtsafetyLocationInfo> existingMap = buildExistingMap(details);
            for (YtsafetyLocationInfoProcessDetailsVO detail : details) {
                compareWithExisting(detail, existingMap);
            }
        }

        return vo;
    }

    private boolean isProcessEdit(List<FieldAuth>  fieldAuths,Boolean isCanApprove,boolean isCanEdit){
        if(isCanEdit) return true;
        if(fieldAuths==null|| fieldAuths.isEmpty()) return false;
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> "proposal_approval".equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        if(fieldAuth==null) return false;
        return isCanApprove;
    }

    private Map<String, YtsafetyLocationInfo> buildExistingMap(List<YtsafetyLocationInfoProcessDetailsVO> details) {
        List<String> locationCodes = details.stream()
                .map(YtsafetyLocationInfoProcessDetailsVO::getLocationCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (locationCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<YtsafetyLocationInfo> existingList = locationInfoService.list(
                new LambdaQueryWrapper<YtsafetyLocationInfo>()
                        .in(YtsafetyLocationInfo::getLocationCode, locationCodes)
                        .eq(YtsafetyLocationInfo::getDelFlag, 0)
        );
        return existingList.stream()
                .collect(Collectors.toMap(YtsafetyLocationInfo::getLocationCode, e -> e, (a, b) -> a));
    }

    private void compareWithExisting(YtsafetyLocationInfoProcessDetailsVO detail,
                                      Map<String, YtsafetyLocationInfo> existingMap) {
        if (StrUtil.isBlank(detail.getLocationCode())) {
            return;
        }
        YtsafetyLocationInfo existing = existingMap.get(detail.getLocationCode());
        if (existing == null) {
            return;
        }
        List<String> fieldCodes = new ArrayList<>();
        HashMap<String, String> fieldsValues = new HashMap<>();
        compareField(fieldCodes, fieldsValues, "factoryName", existing.getFactoryName(), detail.getFactoryName());
        compareField(fieldCodes, fieldsValues, "areaName", existing.getAreaName(), detail.getAreaName());
        compareField(fieldCodes, fieldsValues, "orgName", existing.getOrgName(), detail.getOrgName());
        compareField(fieldCodes, fieldsValues, "locationName", existing.getLocationName(), detail.getLocationName());
        detail.setFieldCodes(fieldCodes);
        detail.setFieldsValues(fieldsValues);
    }

    private void compareField(List<String> fieldCodes, HashMap<String, String> fieldsValues,
                              String fieldName, String oldValue, String newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            fieldCodes.add(fieldName);
            fieldsValues.put(fieldName, oldValue);
        }
    }

    private void generateLocationCodes(List<YtsafetyLocationInfoProcessDetailsDTO> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        List<YtsafetyLocationInfoProcessDetailsDTO> needGenerate = details.stream()
                .filter(d -> StrUtil.isBlank(d.getLocationCode()))
                .collect(Collectors.toList());
        if (needGenerate.isEmpty()) {
            return;
        }
        Long startSeq = ytSafetyWaterFlowNoService.batchAutoIncrementKey("LocationCode", needGenerate.size());
        long seq = startSeq;
        for (YtsafetyLocationInfoProcessDetailsDTO detail : needGenerate) {
            detail.setLocationCode(generateLocationCode(
                    detail.getFactoryCode(), detail.getAreaName(), detail.getLocationName(), seq++));
        }
    }

    @Async
    public void submitProcess(YtsafetyLocationInfoProcess process, Integer opType) {
        doSubmitProcess(process, opType, BusUtits.getLoginUser());
    }

    private void doSubmitProcess(YtsafetyLocationInfoProcess process, Integer opType, LoginUser user) {
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

}
