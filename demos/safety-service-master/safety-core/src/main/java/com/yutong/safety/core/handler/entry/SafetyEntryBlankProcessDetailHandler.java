package com.yutong.safety.core.handler.entry;

import com.alibaba.fastjson.JSON;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.ValidationUtil;
import com.yutong.safety.common.vo.BpmOperationVO;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDOExample;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailAppParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessQueryParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryBlackProcessVO;
import com.yutong.safety.core.db.entity.entry.vo.EntryFactoryRecordVO;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterAuthMapper;
import com.yutong.safety.core.dto.BlackMessageDto;
import com.yutong.safety.core.dto.entry.SafetyEntryButtJointDto;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.enter.SafetyEnterRegisterService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessDetailService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessService;
import com.yutong.safety.core.service.entry.SafetyEntryConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yutong.safety.common.constants.Constants.*;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.*;

@Component
public class SafetyEntryBlankProcessDetailHandler extends AbstractSafetyWebBaseHandler<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample, SafetyEntryBlankProcessDetailQueryParams, SafetyEntryBlankProcessDetailAppParams> {

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    @Resource
    private SafetyEntryConfigHandler safetyEntryConfigHandler;

    @Resource
    private SafetyDictItemService safetyDictItemService;

    @Resource
    private SafetyEnterRegisterService safetyEnterRegisterService;

    @Resource
    private SafetyBpmProcessService safetyBpmProcessService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyBlackMessageService safetyBlackMessageService;

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    private SafetyEntryBlankProcessService safetyEntryBlankProcessService;

    @Resource
    private SafetyEntryExternalNetworkHandler safetyEntryExternalNetworkHandler;

    @Resource
    private SafetyPermissionDelegationHandler safetyPermissionDelegationHandler;

    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;

    @Resource
    private SafetyEnterAuthMapper enterAuthMapper;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyEntryBlankProcessDetailService safetyEntryBlankProcessDetailService;

    @Override
    public AbstractSafetyWebBaseService<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample, SafetyEntryBlankProcessDetailQueryParams> getService() {
        return safetyEntryBlankProcessDetailService;
    }

    @Override
    public String getModuleName() {
        return "黑名单入场备案";
    }

    @Override
    protected String getTemplateName() {
        return "entryEmpInfoForBlack";
    }

    @Override
    protected String getRoleName() {
        return "超级管理员";
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyEntryBlankProcessDetailDO> dataList) {
        String[] title = {"状态", "人员状态", "审核人员工号", "审核人岗位", "姓名", "公司名称", "身份证号", "手机号", "备案开始时间", "备案截止时间", "备案厂区", "对接人员工号", "对接人姓名", "对接人手机号", "提交时间", "地址"};
        // 使用流操作简化填充数据

        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        StringUtils.isNotBlank(ENTRY_BLACK_STATUS.get(info.getStatus())) ? ENTRY_BLACK_STATUS.get(info.getStatus()) : "未知状态",
                        StringUtils.isNotBlank(ENTRY_BLACK_PROCESS_DETAIL_STATUS.get(info.getApplyStatus())) ? ENTRY_BLACK_PROCESS_DETAIL_STATUS.get(info.getApplyStatus()) : "未知状态",
                        info.getApplyEmpNo(),
                        info.getApplyEmpPosition(),
                        info.getName(),
                        info.getCompanyName(),
                        info.getCardNo(),
                        info.getPhone(),
                        info.getEnterStartTime(),
                        info.getEnterEndTime(),
                        info.getEnterFactory(),
                        info.getButtJointCode(),
                        info.getButtJointName(),
                        info.getButtJointPhone(),
                        DateUtils.format(info.getCreateTime(), 2),
                        anEipTodoUrl + "blackListEnterApply?formInstanceId=" + info.getProcessId()
                })
                .toArray(String[][]::new);
        return Pair.of(title, values);
    }

    /**
     * 黑名单人员信息表导入
     *
     * @param param 导入的文件信息
     * @param token 用户信息
     * @return 成功的数据
     */
    @Override
    public VO importData(CommonImportParam param, String token) {
        try {
            // 读取EXCEl数据
            List<SafetyEntryConfig> dataList = safetyEntryExternalNetworkHandler.importDataList(param, true);

            // 数据校验
            Pair<List<SafetyEntryConfig>, List<String>> pair = this.importCheck(dataList, token);
            List<SafetyEntryConfig> successList = pair.getLeft();
            List<String> errorList = pair.getRight();

            // 构建返回数据
            List<SafetyEntryBlankProcessDetailDO> detailList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(successList)) {
                // 根据离职前的信息
                List<String> cardNoList = successList.stream().map(SafetyEntryConfig::getCardNo).collect(Collectors.toList());
                List<BlackMessageDto> black = safetyBlackMessageService.selectBlackEmpInfo(cardNoList);
                Map<String, BlackMessageDto> dataMap = black.stream().collect(Collectors.toMap(BlackMessageDto::getIdCardNo, t -> t, (oldInfo, newInfo) -> oldInfo, HashMap::new));
                // 备案信息
                Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
                // 厂区信息
                Map<Integer, String> factoryDataMap = safetyFieldManageService.getFactoryNameList();
                successList.forEach(li -> {
                    SafetyEntryBlankProcessDetailDO dto = new SafetyEntryBlankProcessDetailDO();
                    BeanUtils.copyProperties(li, dto);
                    BlackMessageDto blackMessageDto = dataMap.get(li.getCardNo());
                    if (blackMessageDto != null) {
                        dto.setOrgCodePath(blackMessageDto.getOrgCodePath());
                        dto.setOrgNamePath(blackMessageDto.getOrgNamePath());
                        dto.setPositionType(blackMessageDto.getPositionType());
                    }
                    dto.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, dto.getRecordTypeCode()));
                    dto.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(factoryDataMap, dto.getEnterFactoryCode()));
                    detailList.add(dto);
                });
            }

            HashMap<String, Object> result = new HashMap<>();
            result.put("data", detailList);
            result.put("message", CollectionUtils.isNotEmpty(errorList) ? String.join("", errorList) : "");
            return successData(result);
        } catch (BizException e) {
            return fail(e.getMsg());
        }
    }

    /**
     * 查询列表和导出 数据后置处理
     *
     * @param list 数据集
     */
    @Override
    public void afterSelect(List<SafetyEntryBlankProcessDetailDO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            // 厂区信息
            Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
            // 备案信息
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            // 审核人的岗位信息
            List<String> empNoList = list.stream().map(SafetyEntryBlankProcessDetailDO::getApplyEmpNo).collect(Collectors.toList());
            List<SafetyEmployeeMessageDO> listByStaffNo = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(empNoList)) {
                listByStaffNo = safetyEmployeeMessageService.getListByStaffNo(empNoList);
            }
            Map<String, String> empDataMap = listByStaffNo.stream().filter(li -> StringUtils.isNotBlank(li.getPositionName())).collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, SafetyEmployeeMessageDO::getPositionName));
            list.forEach(li -> {
                String positionName = empDataMap.get(li.getApplyEmpNo());
                if (StringUtils.isNotBlank(positionName)) {
                    li.setApplyEmpPosition(positionName);
                }
                li.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, li.getRecordTypeCode()));
                li.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(dataMap, li.getEnterFactoryCode()));
            });
        }
    }

    /**
     * 导出数据的数据获取方法重写
     *
     * @param param 入参
     * @return 返回数据集
     */
    @Override
    public List<SafetyEntryBlankProcessDetailDO> getExportData(SafetyEntryBlankProcessDetailQueryParams param) {
        param.setPageFlag(false);
        List<SafetyEntryBlankProcessDetailDO> dataList = safetyEntryBlankProcessDetailService.selectProcessAndDetail(param);
        if (dataList.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        return dataList;
    }

    /**
     * 数据分页数据获取方法重写
     *
     * @param param 入参
     * @return 返回数据集
     */
    @Override
    public Pair<Long, List<SafetyEntryBlankProcessDetailDO>> pageDataSelect(SafetyEntryBlankProcessDetailQueryParams param) {
        param.setPageFlag(true);
        param.setPageSize(param.getLimit());
        if (StringUtils.isNotBlank(param.getRoleWorkNo()) && DATA_PERMISSION_EMP.contains(param.getRoleWorkNo())) {
            param.setRoleWorkNo("");
        }
        List<SafetyEntryBlankProcessDetailDO> list = safetyEntryBlankProcessDetailService.selectProcessAndDetail(param);
        long count = safetyEntryBlankProcessDetailService.selectProcessAndDetailCount(param);

        return Pair.of(count, list);
    }


    /**
     * 流程的创建
     *
     * @param info  新增的数据实体类
     * @param token 员工信息
     */
    @Override
    public VO saveAndUpdate(SafetyEntryBlankProcessDetailAppParams info, String token) {
        try {
            // 数据校验
            List<SafetyEntryBlankProcessDetailDO> detailList = info.getDetailList();
            if (CollectionUtils.isEmpty(detailList)) {
                return fail("请添加黑名单人员信息");
            }

            // 创建流程
            if (info.saveOrUpdate()) {
                // 数据持久化
                Integer save = save(info, token);
                // 创建流程
                createBpmFlow(info, save);
                return success();
            }

            // 审核人提交节点
            return reviewBpmFlow(info, token);
        } catch (BizException e) {
            return fail(e.getMsg());
        }
    }

    /**
     * 创建流程
     */
    private void createBpmFlow(SafetyEntryBlankProcessDetailAppParams info, Integer id) {
        String processSubject = info.getCreateEmpName() + "【" + info.getCreateEmpNo() + "】提交的受限人员入厂申请流程";
        //手动提交事务
        String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(processSubject,
                ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, info.getCreateEmpLogin(), id.toString());
        if (StringUtils.isBlank(returnFlowId)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "创建流程异常返回值" + returnFlowId);
        }
        String bpmFlowId = returnFlowId.split(",")[1];

        // 保存流程ID;
        SafetyEntryBlankProcessDO dto = new SafetyEntryBlankProcessDO();
        dto.setId(id);
        dto.setBpmFlowId(bpmFlowId);
        safetyEntryBlankProcessService.updateByPrimaryKey(dto);
    }

    /**
     * 审核流程
     */
    private VO reviewBpmFlow(SafetyEntryBlankProcessDetailAppParams info, String token) {
        BpmOperationVO bpmOperationVO = JSON.parseObject(info.getFdParameterJson(), BpmOperationVO.class);
        String operationType = bpmOperationVO.getOperationType();
        if (StringUtils.isBlank(operationType)) {
            return fail("流程审批异常");
        }
        // 获取审核意见
        String auditNote = bpmOperationVO.getParam().getAuditNote();

        String userName = TokenUtils.getUserName(token);
        if (StringUtils.isBlank(userName)) {
            return fail("token失效");
        }
        // 数据持久化
        update(info, token);
        // 初始化流程参数
        BpmProcessParam processParam;
        switch (operationType) {
            case BPM_HANDLER_SUBMIT:
                // 起草人提交
                processParam = safetyBpmProcessService.initProcessParam(info.getId().toString(), info.getBpmFlowId(),
                        ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, userName, false, auditNote, "");

                info.setStatus(ENTRY_BLACK_PROCESS_STATUS_WAIT);
                break;

            case BPM_HANDLER_PASS:
                // 审批通过
                processParam = safetyBpmProcessService.initProcessParam(info.getId().toString(), info.getBpmFlowId(),
                        ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, userName, false, auditNote, "");
                break;

            case BPM_HANDLER_REFUSE:
                // 审批驳回
                processParam = safetyBpmProcessService.initProcessRejectParam(info.getId().toString(), info.getBpmFlowId(),
                        ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, userName, false, auditNote,
                        bpmOperationVO.getParam().getJumpToNodeId());
                info.setStatus(ENTRY_BLACK_PROCESS_STATUS_REJECT);
                break;

            case BPM_HANDLER_ABANDON:
                // 流程废弃
                processParam = safetyBpmProcessService.initProcessAbandonParam(
                        info.getId().toString(), info.getBpmFlowId(),
                        ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, userName, auditNote);

                info.setStatus("00");
                break;

            default:
                return fail("流程审批失败，不支持的操作类型: " + operationType);
        }
        String status = safetyBpmProcessService.approveProcessByUserName(info.getId().toString(), info.getBpmFlowId(),
                ENTRY_PROCESS_MODEL_ID, ENTRY_PROCESS_FORM_ID, processParam.getProcessParam(), userName);
        // 处理返回状态
        String code = status.substring(0, 1);
        if ("T".equals(code)) {
            // 数据状态
            updateStatus(info, token);
            return success();
        } else {
            return fail(status);
        }
    }

    private void updateStatus(SafetyEntryBlankProcessDetailAppParams info, String token) {
        SafetyEntryBlankProcessDO safetyEntryBlankProcessDO = new SafetyEntryBlankProcessDO();
        BeanUtils.copyProperties(info, safetyEntryBlankProcessDO);
        safetyEntryBlankProcessDO.setPublishTime(new Date());
        safetyEntryBlankProcessService.save(safetyEntryBlankProcessDO, token);
    }

    /**
     * 保存流程数据
     */
    public Integer save(SafetyEntryBlankProcessDetailAppParams info, String token) {
        String empNo = info.getApplyEmpNo();
        List<SafetyEntryBlankProcessDetailDO> detailList = info.getDetailList();
        // 数据持久化
        Date date = new Date();
        SafetyEntryBlankProcessDO safetyEntryBlankProcessDO = new SafetyEntryBlankProcessDO();
        BeanUtils.copyProperties(info, safetyEntryBlankProcessDO);
        safetyEntryBlankProcessDO.setStatus(ENTRY_BLACK_PROCESS_STATUS_WAIT);

        List<String> competitorDirectorList = detailList.stream().map(SafetyEntryBlankProcessDetailDO::getCompetitorDirector).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(competitorDirectorList)) {
            List<String> applyEmpNo = new ArrayList<>();
            List<String> applyEmpName = new ArrayList<>();
            List<String> applyEmpLogin = new ArrayList<>();
            if (StringUtils.isNotBlank(empNo)) {
                applyEmpNo.add(empNo);
                applyEmpName.add(info.getApplyEmpName());
                applyEmpLogin.add(info.getApplyEmpLogin());
            }
            List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.getListByStaffNo(competitorDirectorList);
            list.forEach(li -> {
                applyEmpNo.add(li.getEmpNo());
                applyEmpName.add(li.getEmpName());
                applyEmpLogin.add(li.getEmpLogin());
            });

            safetyEntryBlankProcessDO.setApplyEmpNo(String.join(",", applyEmpNo));
            safetyEntryBlankProcessDO.setApplyEmpName(String.join(",", applyEmpName));
            safetyEntryBlankProcessDO.setApplyEmpLogin(String.join(",", applyEmpLogin));
        }

        safetyEntryBlankProcessService.save(safetyEntryBlankProcessDO, token);
        Integer id = safetyEntryBlankProcessDO.getId();

        detailList.forEach(li -> {
            li.setProcessId(id);
            li.setApplyStatus(ENTRY_BLACK_PROCESS_DETAIL_STATUS_WAIT);
            li.setCreateTime(date);
            if (Objects.equals(li.getBlankType(), "0") && StringUtils.isNotBlank(empNo)) {
                li.setCompetitorDirector(empNo);
            }
        });
        safetyEntryBlankProcessDetailService.batchSave(detailList);
        return id;
    }

    /**
     * 修改流程数据
     */
    public void update(SafetyEntryBlankProcessDetailAppParams info, String token) {
        List<SafetyEntryBlankProcessDetailDO> detailList = info.getDetailList();
        // 数据持久化
        Date date = new Date();
        SafetyEntryBlankProcessDO safetyEntryBlankProcessDO = new SafetyEntryBlankProcessDO();
        BeanUtils.copyProperties(info, safetyEntryBlankProcessDO);
        safetyEntryBlankProcessDO.setPublishTime(date);
        safetyEntryBlankProcessService.save(safetyEntryBlankProcessDO, token);

        detailList.forEach(li -> {
            li.setApplyTime(date);
            li.setApplyAudit(info.getApplyAudit());
        });
        safetyEntryBlankProcessDetailService.batchUpdateStatus(detailList);

        SafetyEntryBlankProcessDetailQueryParams queryParams = new SafetyEntryBlankProcessDetailQueryParams();
        queryParams.setProcessId(info.getId());
        queryParams.setNotApplyStatus("2");
        long count = safetyEntryBlankProcessDetailService.selectCount(queryParams);
        if (count == 0) {
            SafetyEntryBlankProcessDO doInfo = new SafetyEntryBlankProcessDO();
            doInfo.setApprovalStatus(1);
            doInfo.setId(info.getId());
            safetyEntryBlankProcessService.save(doInfo, token);
        }
    }

    /**
     * 流程详情数据获取
     *
     * @param processId 主表数据ID
     * @param token
     */
    public VO getDetail(Integer processId, String token) {
        EntryBlackProcessVO info = new EntryBlackProcessVO();
        // 主表数据
        SafetyEntryBlankProcessDO process = safetyEntryBlankProcessService.findById(processId);
        if (process == null) {
            return fail("获取流程信息失败");
        }
        BeanUtils.copyProperties(process, info);

        // 部门信息
        SafetyEmployeeMessageDO data = safetyEmployeeMessageService.getByNo(info.getCreateEmpNo());
        if (data != null) {
            info.setDepartment(data.getOrgNamePath());
        }

        // 明细数据
        SafetyEntryBlankProcessDetailQueryParams params = new SafetyEntryBlankProcessDetailQueryParams();
        params.setProcessId(processId);
        String workNo = TokenUtils.getWorkNo(token);
        if (!DATA_PERMISSION_EMP.contains(workNo)) {
            if (!Objects.equals(workNo, info.getCreateEmpNo()) && Objects.equals(info.getStatus(), ENTRY_BLACK_PROCESS_STATUS_WAIT)) {
                params.setCompetitorDirector(workNo);
            }
        }
        List<SafetyEntryBlankProcessDetailDO> list = safetyEntryBlankProcessDetailService.selectList(params, false);
        if (CollectionUtils.isNotEmpty(list)) {
            // 根据离职前的信息
            List<String> cardNoList = list.stream().map(SafetyEntryBlankProcessDetailDO::getCardNo).collect(Collectors.toList());
            List<BlackMessageDto> black = safetyBlackMessageService.selectBlackEmpInfo(cardNoList);
            Map<String, BlackMessageDto> dataMap = black.stream().collect(Collectors.toMap(BlackMessageDto::getIdCardNo, t -> t, (oldInfo, newInfo) -> oldInfo, HashMap::new));
            // 备案信息
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            // 厂区信息
            Map<Integer, String> factoryDataMap = safetyFieldManageService.getFactoryNameList();
            list.forEach(li -> {
                BlackMessageDto blackMessageDto = dataMap.get(li.getCardNo());
                if (blackMessageDto != null) {
                    li.setOrgCodePath(blackMessageDto.getOrgCodePath());
                    li.setOrgNamePath(blackMessageDto.getOrgNamePath());
                    li.setPositionType(blackMessageDto.getPositionType());
                }
                li.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, li.getRecordTypeCode()));
                li.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(factoryDataMap, li.getEnterFactoryCode()));
            });
        }
        info.setDetailList(list);
        return successData(info);
    }

    /**
     * 数据校验
     *
     * @param dataList EXCEL的数据
     * @param token    用户信息
     * @return 返回成功的数据列表和失败的信息
     */
    private Pair<List<SafetyEntryConfig>, List<String>> importCheck(List<SafetyEntryConfig> dataList, String token) {
        String workNo = TokenUtils.getWorkNo(token);

        // 第二部分：数据准备
        // 备案人信息
        Map<String, SafetyEnterRegisterRt> registerMap = safetyEntryExternalNetworkHandler.getStringSafetyEnterRegisterRtMap(dataList);

        // 黑名单信息
        List<String> cardNoList = dataList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cardNoList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "未找到有效的身份证号，请核查提交信息");
        }
        Map<String, SafetyBlackMessageDO> blackMap = safetyEntryExternalNetworkHandler.getStringSafetyBlackMessageDOMap(cardNoList);
        // 对接人信息
        SafetyEntryButtJointDto buttJointInfo = this.selectButtJointInfo(workNo);

        // 备案次数
        String curYears = DateUtils.format(new Date(), 1).substring(0, 4);
        Map<String, EntryFactoryRecordVO> voMap = safetyEntryConfigHandler.getEntryFactoryRecordVOs(cardNoList, Collections.singletonList(workNo), curYears);


        // 友商客户信息
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(cardNoList);

        // 第三部分：数据处理与校验
        List<String> errorInfoList = new ArrayList<>();
        List<SafetyEntryConfig> successList = new ArrayList<>();
        for (SafetyEntryConfig li : dataList) {
            setInfo(li, token);

            StringBuilder sb = new StringBuilder();
            // 2.校验步骤
            boolean isValid = this.validateEntry(li, buttJointInfo, registerMap, blackMap, voMap, sb, competitorMap);
            if (!isValid) {
                errorInfoList.add("序号" + li.getNum() + "信息错误:" + sb.toString());
                continue;
            }
            // 3.成功的数据
            successList.add(li);
        }

        return Pair.of(successList, errorInfoList);
    }
    private void setInfo(SafetyEntryConfig li, String token) {
        li.setCreates(token);
        String phone = TokenUtils.getPhone(token);
        li.setSubmitPhone(phone);
        li.setStatus(ENTRY_CONFIG_STATUS_WAIT);
        if (StringUtils.isBlank(li.getRecordTypeCode())) {
            li.setButtJointCode(TokenUtils.getWorkNo(token));
            li.setButtJointName(TokenUtils.getRealName(token));
            li.setButtJointPhone(phone);
        }
    }

    private SafetyEntryButtJointDto selectButtJointInfo(String empNo) {
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(empNo);

        if (CollectionUtils.isEmpty(authList)) {
            return null;
        }
        // 备案信息
        Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
        // 厂区信息
        Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
        Map<String, String> newDataMap = dataMap.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().toString(), Map.Entry::getValue
        ));
        SafetyEnterAuthRt authRt = authList.get(0);
        SafetyEntryButtJointDto data = new SafetyEntryButtJointDto();
        BeanUtils.copyProperties(authRt, data);
        data.setFilingPark(safetyEntryExternalNetworkHandler.stringToMap(newDataMap, authRt.getFdFactoryCode()));
        data.setFilingType(safetyEntryExternalNetworkHandler.stringToMap(recordDataMap, authRt.getFdFilingUserTypeCode()));
        return data;
    }

    /**
     * 数据校验
     *
     * @param excel         导入的数据信息
     * @param buttJointInfo 对接人信息
     * @param registerMap   登录人信息
     * @param blackMap      黑名单信息
     * @return true 表示没有问题， false 表示有问题
     */
    private boolean validateEntry(SafetyEntryConfig excel, SafetyEntryButtJointDto buttJointInfo,
                                  Map<String, SafetyEnterRegisterRt> registerMap, Map<String, SafetyBlackMessageDO> blackMap, Map<String, EntryFactoryRecordVO> voMap, StringBuilder sb, Map<String, String> competitorMap) {
        // true 需要校验对接人， false 不需要校验对接人, 对接人按空处理
        boolean blank = StringUtils.isNotBlank(excel.getRecordTypeCode());
        if (blank && buttJointInfo == null) {
            sb.append("对接人信息不存在");
            return false;
        }

        formatValidation(excel, buttJointInfo, sb, blank, false);
        if (sb.length() > 0) return false;

        if (blank) {
            registerValidation(registerMap, excel, sb);
            if (sb.length() > 0) return false;
        }

        blackMessageValidation(blackMap, excel, sb, competitorMap);
        if (sb.length() > 0) return false;

        if (blank) {
            checkForFilingNum(excel, voMap, buttJointInfo.getFdLimit(), sb);
            return sb.length() <= 0;
        } else {
            return true;
        }
    }



    /**
     * 校验备案次数
     * @param enterFactoryConfig 数据对象
     * @param curFillingMap 备案新限制信息
     * @param limitDay 限制的数量
     */
    private void checkForFilingNum(SafetyEntryConfig enterFactoryConfig,
                                   Map<String, EntryFactoryRecordVO> curFillingMap,
                                   Integer limitDay,
                                   StringBuilder sb) {
        String key = safetyEntryConfigHandler.generateUniqueKey(enterFactoryConfig.getButtJointCode(), enterFactoryConfig.getCardNo(), enterFactoryConfig.getRecordTypeCode());
        EntryFactoryRecordVO curVO = curFillingMap.get(key);
        Integer nowDay = curVO == null ? 0 : curVO.getRecordNum();
        if (nowDay >= limitDay) {
            sb.append("备案次数超出限制,不允许备案;");
        }
    }

    /**
     * 黑名单校验
     * @param blackDataMap 黑名单信息
     * @param li 导入的数据信息
     * @param sb 提示信息
     */
    private void blackMessageValidation(Map<String, SafetyBlackMessageDO> blackDataMap, SafetyEntryConfig li,  StringBuilder sb, Map<String, String> competitorMap) {
        SafetyBlackMessageDO safetyBlackMessageDO = blackDataMap.get(li.getCardNo());
        if (safetyBlackMessageDO == null) {
            String competitorDirector = competitorMap.getOrDefault(li.getCardNo(), "");
            String[] split = competitorDirector.split("#");
            if (StringUtils.isBlank(competitorDirector)) {
                sb.append("非黑名单人员;");
            } else if (split.length < 2) {
                li.setBlankType("0");
            } else {
                li.setBlankType("1");
                li.setCompetitorDirector(split[1]);
            }
        } else {
            if ("EHR导入".equals(safetyBlackMessageDO.getSource())) {
                sb.append("禁止入厂-验证失败;");
            } else {
                li.setBlankType("0");
            }
        }
    }

    private void registerValidation(Map<String, SafetyEnterRegisterRt> dataMap, SafetyEntryConfig li, StringBuilder sb) {
        SafetyEnterRegisterRt safetyEnterRegisterRt = dataMap.get(li.getPhone());
        if (safetyEnterRegisterRt == null) {
            sb.append("未登记信息;");
        } else {
            if (StringUtils.isBlank(safetyEnterRegisterRt.getFdImg())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "备案人身份信息不存在，未登记信息;");
            } else {
                li.setPhotoUrl(safetyEnterRegisterRt.getFdImg());
            }
            if (!safetyEnterRegisterRt.getFdName().equals(li.getName()) && !safetyEnterRegisterRt.getFdCardNo().equals(li.getCardNo())) {
                sb.append("登记信息姓名或身份证号不正确;");
            }
        }
    }

    private void formatValidation(SafetyEntryConfig info, SafetyEntryButtJointDto buttJointInfo, StringBuilder sb, boolean blank, boolean type) {
        if (StringUtils.isBlank(info.getCompanyName())) {
            sb.append("公司名称为空；");
        }
        if (!ValidationUtil.isMobileValid(info.getPhone())) {
            sb.append("手机号格式不正确；");
        }
        if (!ValidationUtil.isIdCardValid(info.getCardNo())) {
            sb.append("身份号格式不正确；");
        }

        String startTime = info.getEnterStartTime().trim();
        String endTime = info.getEnterEndTime().trim();
        int startTimeLength = startTime.length();
        int endTimeLength = endTime.length();
        if (!DateUtils.isValidDateFormat(startTime, (startTimeLength == 10) ? 1 : 3) || !DateUtils.isValidDateFormat(endTime, (endTimeLength == 10) ? 1 : 3)) {
            sb.append("时间格式不正确[yyyy-MM-dd HH:mm 或 yyyy-MM-dd]；");
        } else {
            if (startTimeLength == 10) {
                info.setEnterStartTime(startTime + " 00:00");
            }
            if (endTimeLength == 10) {
                info.setEnterEndTime(endTime + " 23:59");
            }
            if (blank) {
                // 备案人的限制备案时间
                Integer fdLimitDay = buttJointInfo.getFdLimitDay();
                // 备案人允许的指定开始时间的截止时间
                Date endDate = DateUtils.parse(info.getEnterEndTime(), 3);
                Date lastEndDate = DateUtils.addDays(DateUtils.parse(info.getEnterStartTime(), 3), fdLimitDay == null ? 0 : fdLimitDay);
                if (DateUtils.compareDates(endDate, lastEndDate) == 1) {
                    sb.append("备案时间不能超过").append(fdLimitDay).append("天；");
                }
                if (!DateUtils.isBeforeStr(DateUtils.format(new Date(), 1), DateUtils.format(endDate, 1))) {
                    sb.append("备案截止时间需大于今天；");
                }
            }
        }
        if (type) {
            if (info.getNightFlag() != 0 && info.getNightFlag() != 1) {
                sb.append("是否夜间作业格式不正确（0,1）;");
            }
        } else {
            if (!NIGHT_OR_DAYS.contains(info.getNightOrDay())) {
                sb.append("是否夜间作业格式不正确").append(NIGHT_OR_DAYS).append(";");
            } else {
                info.setNightFlag("是".equals(info.getNightOrDay()) ? 1 : 0);
            }
        }

        if (blank) {
            Map<String, String> filingType = buttJointInfo.getFilingType();
            Map<String, String> filingPark = buttJointInfo.getFilingPark();
            String recordType = filingType.get(info.getRecordTypeCode());
            if (StringUtils.isBlank(recordType)) {
                sb.append("备案类型不正确;");
            } else {
                info.setRecordType(recordType);
            }

            boolean checkResult = safetyEntryExternalNetworkHandler.checkFilingPark(filingPark, info.getEnterFactoryCode());
            if (checkResult) {
                sb.append("厂区格式不正确，对接人可备案厂区信息").append(filingPark).append(";");
            }

            info.setButtJointCode(buttJointInfo.getFdEmpNo());
            info.setButtJointName(buttJointInfo.getFdEmpName());
            info.setButtJointPhone(buttJointInfo.getFdPhone());
        }
    }

    /**
     * 状态回写
     *
     * @param processId 流程ID
     * @param code      流程状态
     */
    public void updateFlowStatus(String processId, String code) {
        LogUtils.info("updateFlowStatus:processId" +  processId + "code:" + code);
        // 获取流程信息
        SafetyEntryBlankProcessQueryParams listParam = new SafetyEntryBlankProcessQueryParams();
        listParam.setBpmFlowId(processId);
        List<SafetyEntryBlankProcessDO> list = safetyEntryBlankProcessService.selectList(listParam, false);
        if (CollectionUtils.isEmpty(list)) {
            LogUtils.error(ResponseEnum.FAIL.getCode(), "未找到对应ID=" + processId + "的BPM流程的员工权限调整信息");
            return;
        }
        // 判断流程状态
        SafetyEntryBlankProcessDO processDO = list.get(0);
        //已废弃的流程直接退出
        if ("00".equals(processDO.getStatus())) {
            return;
        }

        // 变更流程状态
        SafetyEntryBlankProcessDO param = new SafetyEntryBlankProcessDO();
        param.setStatus(code);
        param.setId(processDO.getId());
        param.setPublishTime(new Date());
        safetyEntryBlankProcessService.updateByPrimaryKey(param);

        // 检查状态 非通过状态的话结束， 通过状态的话
        if (!ENTRY_BLACK_PROCESS_STATUS_PASS.equals(code)) {
            return;
        }
        // 获取流程的数据
        SafetyEntryBlankProcessDetailQueryParams queryParams = new SafetyEntryBlankProcessDetailQueryParams();
        queryParams.setProcessId(processDO.getId());
        queryParams.setApplyStatus(ENTRY_BLACK_PROCESS_DETAIL_STATUS_PASS);
        List<SafetyEntryBlankProcessDetailDO> detailDOList = safetyEntryBlankProcessDetailService.selectList(queryParams, false);
        if (CollectionUtils.isEmpty(detailDOList)) {
            return;
        }

        // 创建人的入场方式
        final Integer entryMethod = safetyEntryConfigService.selectEntryMethodByWorkNo(processDO.getCreateEmpNo());

        Set<String> cardNos = new HashSet<>();
        List<SafetyEntryConfig> safetyEntryConfigs = new ArrayList<>();
        detailDOList.forEach(li -> {
            String recordTypeCode = li.getRecordTypeCode();
            //备案类型不为空，添加备案并下发权限
            if(StringUtils.isBlank(recordTypeCode)){
                return;
            }
            SafetyEntryConfig safetyEntryConfig = new SafetyEntryConfig();
            BeanUtils.copyProperties(li, safetyEntryConfig);
            safetyEntryConfig.setCreates("黑名单入场审批回写", "黑名单入场审批回写");
            safetyEntryConfig.setStatus(ENTRY_CONFIG_STATUS_PASS);
            safetyEntryConfig.setDelFlag(SafetyWebBaseEntity.NORMAL);
            safetyEntryConfig.setBlankFlag(1);
            safetyEntryConfig.setEntryMethod(entryMethod);
            safetyEntryConfigs.add(safetyEntryConfig);
            cardNos.add(li.getCardNo());
        });
        if(!CollectionUtils.isEmpty(safetyEntryConfigs)){
            safetyEntryExternalNetworkHandler.batchSaveImportData(safetyEntryConfigs, Lists.newArrayList());
        }
        // 权限下发 只下发入厂方式为刷脸+二维码和刷脸， 二维码的不下发
        if (entryMethod != 2 && CollectionUtils.isNotEmpty(cardNos)) {
            safetyPermissionDelegationHandler.issuePermissionsRealTimeToCrk(new ArrayList<>(cardNos), null, processDO.getCreateEmpName());
        }
    }

    /**
     * 黑名单数据黑名单校验
     * @param info 前端传输的黑名单数据
     * @param token 用户信息
     * @return 返回校验后的数据
     */
    public VO checkInfo(SafetyEntryConfig info, String token) {
        String workNo = TokenUtils.getWorkNo(token);
        String cardNo = info.getCardNo();
        info.setSubmitPhone(TokenUtils.getPhone(token));
        if (StringUtils.isBlank(cardNo)) {
            return fail("身份证号为空");
        }
        List<String> cardNoList = Collections.singletonList(cardNo);
        // 对接人信息
        SafetyEntryButtJointDto buttJointInfo = this.selectButtJointInfo(workNo);
        boolean blank = StringUtils.isNotBlank(info.getRecordTypeCode());
        if (blank && buttJointInfo == null) {
            return fail("对接人信息不存在");
        }
        // 基础信息
        StringBuilder sb = new StringBuilder();
        formatValidation(info, buttJointInfo, sb, blank, true);
        if (sb.length() > 0) {
            return fail(sb.toString());
        }
        setInfo(info, token);

        if (blank) {
            // 备案人信息
            String msg = registerValidation(info);
            if (StringUtils.isNotBlank(msg)) {
                return fail(msg);
            }
            // 备案次数
            String curYears = DateUtils.format(new Date(), 1).substring(0, 4);
            Map<String, EntryFactoryRecordVO> voMap = safetyEntryConfigHandler.getEntryFactoryRecordVOs(cardNoList, Collections.singletonList(workNo), curYears);
            checkForFilingNum(info, voMap, buttJointInfo.getFdLimit(), sb);
            if (StringUtils.isNotBlank(sb.toString())) {
                return fail(sb.toString());
            }
        }
        // 黑名单校验
        // 友商客户信息
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(cardNoList);
        Map<String, SafetyBlackMessageDO> blackMap = safetyEntryExternalNetworkHandler.getStringSafetyBlackMessageDOMap(cardNoList);
        blackMessageValidation(blackMap, info, sb, competitorMap);
        if (sb.length() > 0) {
            return fail(sb.toString());
        }

        // 根据离职前的信息
        List<BlackMessageDto> black = safetyBlackMessageService.selectBlackEmpInfo(cardNoList);
        Map<String, BlackMessageDto> dataMap = black.stream().collect(Collectors.toMap(BlackMessageDto::getIdCardNo, t -> t, (oldInfo, newInfo) -> oldInfo, HashMap::new));
        // 备案信息
        Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
        // 厂区信息
        Map<Integer, String> factoryDataMap = safetyFieldManageService.getFactoryNameList();

        SafetyEntryBlankProcessDetailDO dto = new SafetyEntryBlankProcessDetailDO();
        BeanUtils.copyProperties(info, dto);
        BlackMessageDto blackMessageDto = dataMap.get(info.getCardNo());
        if (blackMessageDto != null) {
            dto.setOrgCodePath(blackMessageDto.getOrgCodePath());
            dto.setOrgNamePath(blackMessageDto.getOrgNamePath());
            dto.setPositionType(blackMessageDto.getPositionType());
        }
        dto.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, dto.getRecordTypeCode()));
        dto.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(factoryDataMap, dto.getEnterFactoryCode()));
        return successData(dto);
    }

    private String registerValidation(SafetyEntryConfig info) {
        String msg = "";
        SafetyEnterRegisterRt registerByPhone = safetyEnterRegisterService.selectInfo(info.getPhone());
        if (registerByPhone == null) {
            msg = "未登记信息";
        } else {
            if (StringUtils.isBlank(registerByPhone.getFdImg())) {
                msg = "备案人身份信息不存在，未登记信息";
            } else {
                info.setPhotoUrl(registerByPhone.getFdImg());
            }
            if (!registerByPhone.getFdName().equals(info.getName()) && !registerByPhone.getFdCardNo().equals(info.getCardNo())) {
                msg = "登记信息姓名或身份证号不正确";
            }
        }
        return msg;
    }
}
