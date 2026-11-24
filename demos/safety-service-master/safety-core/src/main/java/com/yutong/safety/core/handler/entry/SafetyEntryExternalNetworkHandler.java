package com.yutong.safety.core.handler.entry;

import cn.hutool.poi.excel.ExcelReader;
import com.yutong.safety.common.constants.*;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.BlackMessageListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyBlankYxtMsgVO;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.competitor.CompetitorMangeVO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.entry.*;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigAddParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigQueryParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryImportFailQueryParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryFactoryRecordVO;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterAuthMapper;
import com.yutong.safety.core.dto.entry.SafetyEntryButtJointDto;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.SafetyBlackMessageService;
import com.yutong.safety.core.service.SafetyDictItemService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.enter.SafetyEnterIssuanceService;
import com.yutong.safety.core.service.enter.SafetyEnterRegisterService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessLogService;
import com.yutong.safety.core.service.entry.SafetyEntryConfigHistoryService;
import com.yutong.safety.core.service.entry.SafetyEntryConfigService;
import com.yutong.safety.core.service.entry.SafetyEntryImportFailService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.*;

@Component
public class SafetyEntryExternalNetworkHandler extends AbstractSafetyWebBaseHandler<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams, SafetyEntryConfigAddParams> {

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Resource
    private YxtRebotUtils yxtRebotUtils;

    @Resource
    private SafetyEnterAuthMapper enterAuthMapper;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyDictItemService safetyDictItemService;

    @Resource
    private SafetyEnterRegisterHandler enterRegisterHandler;

    @Resource
    private SafetyEnterRegisterService safetyEnterRegisterService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyEntryCompanyRegisterHandler safetyEntryCompanyRegisterHandler;

    @Resource
    private SafetyEntryImportFailService safetyEntryImportFailService;

    @Resource
    private SafetyBlackMessageService safetyBlackMessageService;

    @Resource
    private SafetyEntryConfigHandler safetyEntryConfigHandler;

    @Resource
    private SafetyEntryConfigHistoryService safetyEntryConfigHistoryService;

    @Resource
    private SafetyEnterIssuanceService safetyEnterIssuanceService;

    @Resource
    private SafetyEntryBlankProcessLogService safetyEntryBlankProcessLogService;

    @Resource
    private SafetyPermissionDelegationHandler safetyPermissionDelegationHandler;

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetySystemParamService safetySystemParamService;

    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;

    @Override
    public AbstractSafetyWebBaseService<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams> getService() {
        return safetyEntryConfigService;
    }

    @Override
    public String getModuleName() {
        return "备案人员信息表";
    }

    @Override
    protected String getTemplateName() {
        return "entryEmpInfo";
    }

    @Override
    protected String getRoleName() {
        return null;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyEntryConfig> dataList) {
        return null;
    }

    /**
     * 数据导入
     *
     * @param param 文件信息
     * @param token 用户信息
     */
    @Override
    public VO importData(CommonImportParam param, String token) {
        String phone = TokenUtils.getString(token, "phone");
        String versionNo = System.currentTimeMillis() + phone;

        // 取出Excel中的数据
        List<SafetyEntryConfig> dataList = importDataList(param, false);

        // 校验数据
        Pair<List<SafetyEntryConfig>, List<SafetyEntryImportFailDO>> pair = importCheck(dataList, versionNo, token);
        List<SafetyEntryConfig> successList = pair.getLeft();
        List<SafetyEntryImportFailDO> errorList = pair.getRight();

        // 第四部分：数据持久化
        batchSaveImportData(successList, errorList);
        if (CollectionUtils.isNotEmpty(successList)) {
            // 发送消息
            sendMsg(successList.get(0).getButtJointCode());
            safetyEntryConfigHandler.batchSaveOperateNoToken(successList,EntryOperateEnum.OPER_UPDATE.getCode(),"外部导入", TokenUtils.getTockenUser(token));
        }

        // 第五部分：结果返回
        return successData(buildImportResult(successList.size(), errorList.size(), versionNo));
    }

    /**
     * 文件读取， 因为模版内容大致一致，所以公用一个， 用type区分不同之处
     * @param param 文件信息
     * @param type true： 存在公司
     * @return 返回解析到的数据
     */
    public List<SafetyEntryConfig> importDataList(CommonImportParam param, boolean type) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(param.getFile());

        excelReader.addHeaderAlias("序号", "num");
        excelReader.addHeaderAlias("姓名*", "name");
        if (type) {
            excelReader.addHeaderAlias("公司名称*", "companyName");
            excelReader.addHeaderAlias("备案类型编码", "recordTypeCode");
        } else {
            excelReader.addHeaderAlias("备案类型编码*", "recordTypeCode");
        }
        excelReader.addHeaderAlias("身份证号*", "cardNo");
        excelReader.addHeaderAlias("手机号*", "phone");
        excelReader.addHeaderAlias("准入厂区编码*", "enterFactoryCode");
        excelReader.addHeaderAlias("准入时间-始*(yyyy-MM-dd hh:mm)", "enterStartTime");
        excelReader.addHeaderAlias("准入时间-终*(yyyy-MM-dd hh:mm)", "enterEndTime");
        excelReader.addHeaderAlias("是否夜间作业*", "nightOrDay");
        excelReader.addHeaderAlias("备注", "remark");

        // 1.获取数据
        List<SafetyEntryConfig> dataList = excelReader.read(0, 1, SafetyEntryConfig.class);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导入数据为空");
        }

        return dataList;
    }

    /**
     * 数据检查
     *
     * @param dataList  数据集
     * @param versionNo 版本号
     * @param token     用户细信息
     * @return
     */
    public Pair<List<SafetyEntryConfig>, List<SafetyEntryImportFailDO>> importCheck(List<SafetyEntryConfig> dataList, String versionNo, String token) {
        String empNo = TokenUtils.getString(token, "buttJointCode");
        String realName = TokenUtils.getString(token, "realName");
        String phone = TokenUtils.getString(token, "phone");
        // 第二部分：数据准备
        Map<String, SafetyEnterRegisterRt> registerMap = getStringSafetyEnterRegisterRtMap(dataList);

        // 3.对接人信息
        SafetyEntryButtJointDto buttJointInfo = this.selectButtJointInfo(empNo);

        // 4.供应商负责人备案信息
        SafetyEntryCompanyRegisterDO supplyInfo = safetyEntryCompanyRegisterHandler.getSupplyInfo(phone);

        // 5.黑名单信息
        List<String> cardNoList = dataList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cardNoList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "未找到有效的身份证号，请核查提交信息");
        }
        Map<String, SafetyBlackMessageDO> blackMap = getStringSafetyBlackMessageDOMap(cardNoList);

        // 备案次数
        String curYears = DateUtils.format(new Date(), 1).substring(0, 4);
        Map<String, EntryFactoryRecordVO> voMap = safetyEntryConfigHandler.getEntryFactoryRecordVOs(cardNoList, Collections.singletonList(buttJointInfo.getFdEmpNo()), curYears);

        // 友商客户信息
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(cardNoList);

        // 黑名单校验日志
        List<SafetyEntryBlankProcessLogDO> logList = new ArrayList<>();
        // 第三部分：数据处理与校验
        List<SafetyEntryConfig> successList = new ArrayList<>();
        List<SafetyEntryImportFailDO> errorList = new ArrayList<>();
        for (SafetyEntryConfig li : dataList) {
            // 1.数据设置与存储
            convertExcelToEntity(li, supplyInfo, phone, realName);

            StringBuilder sb = new StringBuilder();
            // 2.校验步骤
            boolean isValid = validateEntry(li, buttJointInfo, registerMap, blackMap, voMap, sb, competitorMap, logList);
            if (!isValid) {
                addFailureInfo(li, sb, versionNo, errorList);
                continue;
            }
            // 3.成功的数据
            successList.add(li);
        }
        if (CollectionUtils.isNotEmpty(logList)) {
            logList.forEach(li -> li.setCreates(phone, realName));
            safetyEntryBlankProcessLogService.batchSave(logList);
        }
        return Pair.of(successList, errorList);
    }

    /**
     * 黑名单信息
     * @param cardNoList 身份证号
     * @return 返回黑名单的信息
     */
    public Map<String, SafetyBlackMessageDO> getStringSafetyBlackMessageDOMap(List<String> cardNoList) {
        BlackMessageListParam blackParam = new BlackMessageListParam();
        blackParam.setIdCardNoList(cardNoList);
        blackParam.setState(1);
        List<SafetyBlackMessageDO> messageDOS = safetyBlackMessageService.selectList(blackParam);
        return messageDOS.stream().collect(Collectors.toMap(SafetyBlackMessageDO::getIdCardNo, Function.identity()));
    }

    /**
     * 登记人信息
     * @param dataList 数据集
     * @return 返回登记的结果
     */
    public Map<String, SafetyEnterRegisterRt> getStringSafetyEnterRegisterRtMap(List<SafetyEntryConfig> dataList) {
        // 2.登记人信息
        List<String> phoneList = dataList.stream().map(SafetyEntryConfig::getPhone).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<SafetyEnterRegisterRt> safetyEnterRegisterRts = safetyEnterRegisterService.selectInfoByPhones(phoneList);
        return safetyEnterRegisterRts.stream().collect(Collectors.toMap(SafetyEnterRegisterRt::getFdPhone, Function.identity()));
    }


    /**
     * 构建导入结果
     *
     * @param successSize 成功的数量
     * @param errorSize   失败的数量
     * @param versionNo   版本号
     * @return 返回失败的数据集，和提示细信息
     */
    private HashMap<String, Object> buildImportResult(int successSize, int errorSize, String versionNo) {
        SafetyEntryImportFailQueryParams queryParams = new SafetyEntryImportFailQueryParams();
        queryParams.setVersionNo(versionNo);
        List<SafetyEntryImportFailDO> failDOS = safetyEntryImportFailService.selectList(queryParams, false);
        if (CollectionUtils.isNotEmpty(failDOS)) {
            // 备案信息
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            // 厂区信息
            Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
            failDOS.forEach(li -> {
                li.setRecordType(getRecordType(recordDataMap, li.getRecordTypeCode()));
                li.setEnterFactory(getFilingPark(dataMap, li.getEnterFactoryCode()));
            });
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", String.format("导入成功%d个，失败%d个", successSize, errorSize));
        result.put("data", failDOS);
        return result;
    }

    /**
     * 数据入库
     *
     * @param successList 成功的数据
     * @param errorList   失败的数据
     */
    public void batchSaveImportData(List<SafetyEntryConfig> successList, List<SafetyEntryImportFailDO> errorList) {
        if (!CollectionUtils.isEmpty(errorList)) {
            safetyEntryImportFailService.batchSave(errorList);
        }
        if (!CollectionUtils.isEmpty(successList)) {
            safetyEntryConfigHistoryService.batchSaveHistory(successList);
            List<SafetyEntryConfig> saveList = new ArrayList<>();
            successList.forEach(li -> {
                li.setHistoryId(li.getId());
                li.setId(null);
                saveList.add(li);
            });

            safetyEntryConfigService.batchSave(saveList);
        }
    }

    /**
     * 生成行唯一键
     *
     * @param config SafetyEntryConfig实体
     * @return 行唯一键
     */
    private String generateRowKey(SafetyEntryConfig config) {
        // 对编码进行排序，保证一样
        String factoryCodes = Arrays.stream(config.getEnterFactoryCode().split(",")).sorted().collect(Collectors.joining(","));
        return config.getButtJointCode() + config.getCardNo() + factoryCodes +
                DateUtils.format(DateUtils.parse(config.getEnterStartTime(), 3), 10);
    }

    /**
     * 数据校验
     *
     * @param excel         导入的数据信息
     * @param buttJointInfo 对接人信息
     * @param registerMap   登录人信息
     * @param blackMap      黑名单信息
     * @param logList
     * @return true 表示有问题， false 表示没有问题
     */
    boolean validateEntry(SafetyEntryConfig excel, SafetyEntryButtJointDto buttJointInfo,
                          Map<String, SafetyEnterRegisterRt> registerMap, Map<String, SafetyBlackMessageDO> blackMap, Map<String, EntryFactoryRecordVO> voMap, StringBuilder sb, Map<String, String> competitorMap, List<SafetyEntryBlankProcessLogDO> logList) {
        formatValidation(excel, buttJointInfo, sb);
        if (sb.length() > 0) return false;

        registerValidation(registerMap, excel, sb);
        if (sb.length() > 0) return false;

        blackMessageValidation(blackMap, excel, buttJointInfo, sb, competitorMap, logList);
        if (sb.length() > 0) return false;

        checkForFilingNum(excel, voMap, buttJointInfo.getFdLimit(), sb);
        return sb.length() <= 0;
    }

    /**
     * 将Excel数据转换为实体
     *
     * @param li       导入的数据对象
     * @param realName 姓名
     * @param phone    手机号
     */
    private void convertExcelToEntity(SafetyEntryConfig li, SafetyEntryCompanyRegisterDO supplyInfo, String phone, String realName) {
        li.setCompanyCode(supplyInfo.getFdCompanyCode());
        li.setCompanyName(supplyInfo.getFdCompany());
        li.setCreates(phone, realName);
        li.setSubmitPhone(phone);
        li.setStatus(ENTRY_CONFIG_STATUS_WAIT);
    }

    /**
     * 添加失败信息
     *
     * @param excel     导入的数据对象
     * @param errorInfo 失败信息
     * @param versionNo 版本号
     * @param errorList 失败的数据集
     */
    private void addFailureInfo(SafetyEntryConfig excel, StringBuilder errorInfo, String versionNo, List<SafetyEntryImportFailDO> errorList) {
        SafetyEntryImportFailDO failDO = new SafetyEntryImportFailDO();
        BeanUtils.copyProperties(excel, failDO);
        failDO.setFailRemark(errorInfo.toString());
        failDO.setVersionNo(versionNo);
        failDO.setDelFlag(SafetyWebBaseEntity.NORMAL);
        errorList.add(failDO);
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
     * @param buttJointInfo 对接人信息
     * @param sb 提示信息
     */
    private void blackMessageValidation(Map<String, SafetyBlackMessageDO> blackDataMap, SafetyEntryConfig li, SafetyEntryButtJointDto buttJointInfo, StringBuilder sb, String token) {
        SafetyBlackMessageDO safetyBlackMessageDO = blackDataMap.get(li.getCardNo());
        SafetyYxtMsgEnum result = SafetyYxtMsgEnum.NONE;
        String competitor = "";
        if (safetyBlackMessageDO == null) {
            // 处理非友商黑名单情况
            Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(Collections.singletonList(li.getCardNo()));
            Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, li.getCardNo());
            if (Boolean.TRUE.equals(pair.getLeft())) {
                result = SafetyYxtMsgEnum.FILING_COMPETITOR_MSG;
                competitor = pair.getRight();
            } else {
                li.setBlankFlag(0);
            }
        } else {
            if (Constants.BLANK_FIRST_SOURCE.equals(safetyBlackMessageDO.getSource())) {
                result = SafetyYxtMsgEnum.FILING_EHR_MSG;
            } else if ("是".equals(buttJointInfo.getFdBlack())) {
                result = SafetyYxtMsgEnum.FILING_RESIGN_MSG;
            } else {
                Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(Collections.singletonList(li.getCardNo()));
                Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, li.getCardNo());
                if (Boolean.TRUE.equals(pair.getLeft())) {
                    result = SafetyYxtMsgEnum.FILING_COMPETITOR_MSG;
                    competitor = pair.getRight();
                } else {
                    li.setBlankFlag(1);
                }
            }
        }
        sb.append(result.getErrorMessage());
        if (result != SafetyYxtMsgEnum.NONE) {
            // 日志
            SafetyEntryBlankProcessLogDO processLog = new SafetyEntryBlankProcessLogDO();
            processLog.setEntryInit(li);
            processLog.setSubmitName(TokenUtils.getRealName(token));
            processLog.setSubmitPhone(TokenUtils.getPhone(token));
            processLog.setCreates(TokenUtils.getPhone(token), TokenUtils.getRealName(token));
            processLog.setButtJointPhone(buttJointInfo.getFdPhone());
            processLog.setRemark(result.getErrorMessage());
            safetyEntryBlankProcessLogService.save(processLog);

            SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(result,
                    buttJointInfo.getFdEmpNo(), buttJointInfo.getFdEmpName(),
                    li.getName(), li.getCardNo(), li.getPhone(), competitor,
                    TokenUtils.getRealName(token), TokenUtils.getPhone(token));
            safetyBaseHandler.sendYxtMsg(vo);
        }
    }
    /**
     * 黑名单校验
     * @param blackDataMap 黑名单信息
     * @param li 导入的数据信息
     * @param buttJointInfo 对接人信息
     * @param sb 提示信息
     */
    private void blackMessageValidation(Map<String, SafetyBlackMessageDO> blackDataMap, SafetyEntryConfig li, SafetyEntryButtJointDto buttJointInfo, StringBuilder sb, Map<String, String> competitorMap, List<SafetyEntryBlankProcessLogDO> logList) {
        SafetyBlackMessageDO safetyBlackMessageDO = blackDataMap.get(li.getCardNo());
        String competitorDirector = competitorMap.getOrDefault(li.getCardNo(), "");
        String[] split = competitorDirector.split("#");
        boolean competitorBlack = StringUtils.isNotBlank(competitorDirector) && Objects.equals(split[0], "T");
        SafetyYxtMsgEnum result = SafetyYxtMsgEnum.NONE;
        String competitor = "";
        if (safetyBlackMessageDO == null) {
            // 处理非友商黑名单情况
            if (competitorBlack) {
                result = SafetyYxtMsgEnum.FILING_RESIGN_MSG;
                competitor = split.length == 3 ? split[2] : "";
            } else {
                li.setBlankFlag(0);
            }
        } else {
            if (Constants.BLANK_FIRST_SOURCE.equals(safetyBlackMessageDO.getSource())) {
                result = SafetyYxtMsgEnum.FILING_EHR_MSG;
            } else if ("是".equals(buttJointInfo.getFdBlack())) {
                result = SafetyYxtMsgEnum.FILING_COMPETITOR_MSG;
            } else if (competitorBlack) {
                result = SafetyYxtMsgEnum.FILING_RESIGN_MSG;
                competitor = split.length == 3 ? split[2] : "";
            } else {
                li.setBlankFlag(1);
            }
        }

        sb.append(result.getErrorMessage());
        if (result != SafetyYxtMsgEnum.NONE) {
            // 黑名单校验 的日志数据
            SafetyEntryBlankProcessLogDO log = new SafetyEntryBlankProcessLogDO();
            log.setEntryInit(li);
            log.setRemark(result.getErrorMessage());
            logList.add(log);

            SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(result,
                    buttJointInfo.getFdEmpNo(), buttJointInfo.getFdEmpName(),
                    li.getName(), li.getCardNo(), li.getPhone(), competitor,
                    li.getCreateBy(), li.getCreateByNo());
            safetyBaseHandler.sendYxtMsg(vo);
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
            // 登记校验
            Boolean aBoolean = safetyArtemisHandler.checkOneToMany(safetyEnterRegisterRt.getFdImg(), safetyEnterRegisterRt.getFdName(), safetyEnterRegisterRt.getFdCardNo(), SafetyHikThresholdEnum.ENTRY);
            if (aBoolean) {
                SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_ENTRY_TODO");
                if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                    sb.append(safetyEnterRegisterRt.getFdName()).append("人脸校验失败");
                }
            }
        }
    }

    private void formatValidation(SafetyEntryConfig info, SafetyEntryButtJointDto buttJointInfo, StringBuilder sb) {
        Map<String, String> filingPark = buttJointInfo.getFilingPark();
        Map<String, String> filingType = buttJointInfo.getFilingType();
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
        String recordType = filingType.get(info.getRecordTypeCode());
        if (StringUtils.isBlank(recordType)) {
            sb.append("备案类型不正确;");
        } else {
            info.setRecordType(recordType);
        }
        boolean checkResult = checkFilingPark(filingPark, info.getEnterFactoryCode());
        if (checkResult) {
            sb.append("厂区格式不正确，对接人可备案厂区信息").append(filingPark).append(";");
        }
        if (!NIGHT_OR_DAYS.contains(info.getNightOrDay())) {
            sb.append("是否夜间作业格式不正确").append(NIGHT_OR_DAYS).append(";");
        } else {
            info.setNightFlag("是".equals(info.getNightOrDay()) ? 1 : 0);
        }
        info.setButtJointCode(buttJointInfo.getFdEmpNo());
        info.setButtJointName(buttJointInfo.getFdEmpName());
        info.setButtJointPhone(buttJointInfo.getFdPhone());
    }

    /**
     * 备案厂区校验结果
     * @return true；表示校验失败， false：表示校验成功
     */
    public boolean checkFilingPark(Map<String, String> filingPark, String factoryCode) {
        // 为空校验
        if (StringUtils.isBlank(factoryCode)) {
            return true;
        }
        // 数据校验
        return Arrays.stream(factoryCode.split(",")).map(filingPark::get).allMatch(StringUtils::isBlank);
    }

    /**
     * 备案厂区賦值
     * @param filingPark 所有的厂区信息
     * @param factoryCode 指定的厂区编码
     * @return 匹配到的厂区名称
     */
    public String getFilingPark(Map<Integer, String> filingPark, String factoryCode) {
        // 为空校验
        if (StringUtils.isBlank(factoryCode)) {
            return "";
        }
        // 数据获取
        return Arrays.stream(factoryCode.split(",")).map(Integer::parseInt).distinct().map(filingPark::get).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }
    /**
     * 备案类型賦值
     * @param recordDataMap 所有的备案类型信息
     * @param recordTypeCode 指定的备案类型编码
     * @return 匹配到的厂区名称
     */
    public String getRecordType(Map<String, String> recordDataMap, String recordTypeCode) {
        // 为空校验
        if (StringUtils.isBlank(recordTypeCode)) {
            return "";
        }
        // 数据获取
        String recordType = recordDataMap.get(recordTypeCode);
        if (StringUtils.isNotBlank(recordType)) {
            return recordType;
        }
        return "";
    }

    /**
     * 外网分页查询
     *
     * @param params 入参
     * @param token  token用户信息
     * @return 返回当前用户的数据
     */
    public PageVO<SafetyEntryConfig> getNetworkPageList(SafetyEntryConfigQueryParams params, String token) {
        String phone = TokenUtils.getString(token, "phone");
        params.setSubmitPhone(phone);
        Pair<Long, List<SafetyEntryConfig>> pair = this.pageHistoryDataSelect(params);
        List<SafetyEntryConfig> right = pair.getRight();
        try {
            if (CollectionUtils.isNotEmpty(right)) {
                // 备案信息
                Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
                // 厂区信息
                Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
                right.forEach(li -> {
                    li.setRecordType(getRecordType(recordDataMap, li.getRecordTypeCode()));
                    li.setEnterFactory(getFilingPark(dataMap, li.getEnterFactoryCode()));
                });
            }
            return new PageVO<>(pair.getLeft(), right);
        } catch (Exception e) {
            return new PageVO<>(pair.getLeft(), right);
        }
    }


    private Pair<Long, List<SafetyEntryConfig>> pageHistoryDataSelect(SafetyEntryConfigQueryParams param) {
        String createTime = param.getCreateTime();
        if (StringUtils.isBlank(createTime)) {
            param.setCreateTime(DateUtils.format(DateUtils.addMonths(new Date(), -1), 2));
        }
        long total = safetyEntryConfigService.selectHistoryCount(param);
        List<SafetyEntryConfig> list = safetyEntryConfigService.selectHistoryList(param);
        return Pair.of(total, list);
    }

    /**
     * 外网创建数据
     *
     * @param info  数据对象
     * @param token 用户信息
     */
    public Object networkSave(SafetyEntryConfigAddParams info, String token) {
        String phone = TokenUtils.getString(token, "phone");
        info.setSubmitPhone(phone);
        SafetyEntryConfig data = new SafetyEntryConfig();
        BeanUtils.copyProperties(info, data);

        // 备案人信息
        SafetyEnterRegisterRt registerByPhone = enterRegisterHandler.selectInfo(data.getPhone());
        if (registerByPhone == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "备案人身份信息不存在，未登记信息");
        } else {
            if (StringUtils.isBlank(registerByPhone.getFdImg())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "备案人身份信息不存在，未登记信息");
            }
            if (!registerByPhone.getFdName().equals(data.getName()) && !registerByPhone.getFdCardNo().equals(data.getCardNo())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "备案人身份信息不正确，登记信息姓名或身份证号不正确");
            }
        }
        // 登记校验
        Boolean aBoolean = safetyArtemisHandler.checkOneToMany(registerByPhone.getFdImg(), registerByPhone.getFdName(), registerByPhone.getFdCardNo(), SafetyHikThresholdEnum.ENTRY);
        if (aBoolean) {
            SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_ENTRY_TODO");
            if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), registerByPhone.getFdName() + "人脸校验失败");
            }
        }

        // 对接人信息
        SafetyEntryButtJointDto buttJointInfo = this.getButtJointInfo(token);

        // 供应商负责人备案信息
        SafetyEntryCompanyRegisterDO supplyInfo = safetyEntryCompanyRegisterHandler.getSupplyInfo(phone);
        data.setCompanyCode(supplyInfo.getFdCompanyCode());
        data.setCompanyName(supplyInfo.getFdCompany());

        // 黑名单校验
        Map<String, SafetyBlackMessageDO> blackMap = getStringSafetyBlackMessageDOMap(Collections.singletonList(data.getCardNo()));
        StringBuilder sb = new StringBuilder();
        blackMessageValidation(blackMap, data, buttJointInfo, sb, token);
        if (sb.length() > 0) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        // 获取备案人及对接人的备案次数
        String curYears = DateUtils.format(new Date(), 1).substring(0, 4);
        Map<String, EntryFactoryRecordVO> voMap = safetyEntryConfigHandler.getEntryFactoryRecordVOs(Collections.singletonList(data.getCardNo()), Collections.singletonList(buttJointInfo.getFdEmpNo()), curYears);
        checkForFilingNum(info, voMap, buttJointInfo.getFdLimit(), sb);
        if (StringUtils.isNotBlank(sb.toString())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        data.setStatus(ENTRY_CONFIG_STATUS_WAIT);
        data.setPhotoUrl(registerByPhone.getFdImg());
        data.setButtJointPhone(buttJointInfo.getFdPhone());

        SafetyEntryConfigHistoryDO registerHistoryDO = new SafetyEntryConfigHistoryDO();
        BeanUtils.copyProperties(data, registerHistoryDO);
        safetyEntryConfigHistoryService.save(registerHistoryDO);
        data.setHistoryId(registerHistoryDO.getId());
        data.setId(null);

        service().save(data, token);
        // 发送消息
        sendMsg(buttJointInfo.getFdEmpNo());
        safetyEntryConfigHandler.batchSaveOperateNoToken(Collections.singletonList(data),EntryOperateEnum.OPER_UPDATE.getCode(),"外部新增", TokenUtils.getTockenUser(token));
        return null;
    }


    public void sendMsg(String empNo) {
        String content = "[安保管理平台]您有外来人员入厂备案申请待审批";
        //发送宇信通消息
        RestTemplate restTemplate = new RestTemplate();
        try {
            yxtRebotUtils.postForEntity(null, incomintRebotId, empNo, content, restTemplate);
        }catch (Exception e){
            LogUtils.error("发送宇信通消息异常，{}", e);
        }
        //发送EIP代办
        try{
            Set<String> toEmpSet = Collections.singleton(empNo);
            safetyEntryConfigHandler.sendEipToDo(toEmpSet,content);
        }catch (Exception e){
            LogUtils.error("发送EIP代办异常，{}", e);
        }
    }

    /**
     * 登录人对应的对接人备案信息
     *
     * @param token 用户信息
     */
    public SafetyEntryButtJointDto getButtJointInfo(String token) {
        String empNo = TokenUtils.getString(token, "buttJointCode");
        return selectButtJointInfo(empNo);
    }

    SafetyEntryButtJointDto selectButtJointInfo(String empNo) {
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(empNo);

        if (CollectionUtils.isEmpty(authList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人信息不存在");
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
        data.setFilingPark(stringToMap(newDataMap, authRt.getFdFactoryCode()));
        data.setFilingType(stringToMap(recordDataMap, authRt.getFdFilingUserTypeCode()));
        return data;
    }

    public Map<String, String> stringToMap(Map<String, String> dataMap, String code) {
        try {
            if (dataMap == null || dataMap.isEmpty() || StringUtils.isBlank(code)) {
                return new HashMap<>();
            }
            List<String> codeList = Arrays.asList(code.split(","));

            return dataMap.entrySet().stream().filter(entry -> codeList.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人备案厂区或备案类型解析出错");
        }
    }

    public Object logoutPermission(Integer id, String token) {
        // 获取数据
        SafetyEntryConfigHistoryDO info = safetyEntryConfigHistoryService.findById(id);
        if (info == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据不存在");
        }

        // 更新准入截止时间
        SafetyEntryConfigHistoryDO data = new SafetyEntryConfigHistoryDO();
        data.setId(id);
        data.setStatus(ENTRY_CONFIG_STATUS_LOGOUT);
        data.setEnterEndTime(DateUtils.format(new Date(), 3));
        safetyEntryConfigHistoryService.save(data, token);

        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        params.setHistoryId(id);
        List<SafetyEntryConfig> list = safetyEntryConfigService.selectList(params, false);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        list.forEach(li -> {
            logoutPermission(li.getId(), "", token, false);
        });
        return null;
    }

    /**
     * 注销权限  准入时间截止时间更新为当前时间，注销二维码和人脸权限
     *
     * @param id    数据行ID
     * @param token 用户信息
     */
    public Object logoutPermission(Integer id,String reason, String token, boolean type) {
        if (id == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据行ID为空");
        }
        // 获取数据
        SafetyEntryConfig info = safetyEntryConfigService.findById(id);
        if (info == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据不存在");
        }
        // 更新准入截止时间
        SafetyEntryConfig data = new SafetyEntryConfig();
        data.setId(id);
        data.setStatus(ENTRY_CONFIG_STATUS_LOGOUT);
        data.setEnterEndTime(DateUtils.format(new Date(), 3));
        service().save(data, token);
        if (type) {
            SafetyEntryConfigHistoryDO historyDO = new SafetyEntryConfigHistoryDO();
            historyDO.setId(info.getHistoryId());
            historyDO.setStatus(ENTRY_CONFIG_STATUS_LOGOUT);
            historyDO.setEnterEndTime(DateUtils.format(new Date(), 3));
            safetyEntryConfigHistoryService.save(historyDO, token);
        }
        try{
            safetyEntryConfigHandler.saveConfigOperate(info, reason, token,EntryOperateEnum.OPER_DELETE.getCode());
        }catch (Exception e){
            LogUtils.error("批量保存操作日志异常",e);
        }

        // 判断是否需要注销权限
        boolean result = revokePermissions(info);
        if (result) {
            // 权限注销
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            info.setRecordType(getRecordType(recordDataMap, info.getRecordTypeCode()));
            info.setEnterEndTime(DateUtils.reduceDate(new Date(), -1 , 3));
            List<SafetyEnterIssuanceDO> list = new ArrayList<>();
            SafetyEnterIssuanceDO issuanceDO = safetyPermissionDelegationHandler.getSafetyEnterIssuanceDO(info, TokenUtils.getRealName(token));
            String deviceNo = safetyPermissionDelegationHandler.getEmpowerDevice(info.getEnterFactoryCode(), info.getRecordType());
            safetyPermissionDelegationHandler.issueCrk(issuanceDO, deviceNo);
            list.add(issuanceDO);
            safetyEnterIssuanceService.saveBatch(list);

            // 重新下发
            safetyPermissionDelegationHandler.issuePermissionsAgainCrk(Collections.singletonList(issuanceDO.getFdCardNo()), TokenUtils.getRealName(token));
        }
        return null;
    }

    /**
     * 注销权限判断， 判断当前用户是否还存在当前厂区的备案截止日期当前日期的信息， 存在就禁止注销，不存在可以注销
     * @param info 当前用户的信息
     * @return true：可以注销， false表示不能注销
     */
    public boolean revokePermissions(SafetyEntryConfig info) {
        boolean result = true;
        List<String> targetFactoryCodes = Arrays.asList(info.getEnterFactoryCode().split(","));
        // 获取当前用户的所有数据
        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        params.setCardNo(info.getCardNo());
        params.setStatus(ENTRY_CONFIG_STATUS_PASS);
        params.setId(info.getId());
        params.setRecordTypeCode(info.getRecordTypeCode());
        params.setMinEnterEndTime(DateUtils.format(new Date(), 3));
        List<SafetyEntryConfig> configs = safetyEntryConfigService.selectList(params, false);
        // 判断
        if (CollectionUtils.isNotEmpty(configs)) {
            for (SafetyEntryConfig li : configs) {
                Date endDate = DateUtils.parse(li.getEnterEndTime(), 3);
                // 当前时间在历史数据截止日期的前面，表示可以注销，直接结束
                if (DateUtils.compareDates(new Date(), endDate) == 1) {
                    continue;
                }
                // 判断其它信息是否存在
                List<String> currentFactoryCodes = Arrays.asList(li.getEnterFactoryCode().split(","));
                if (CollectionUtils.isNotEmpty(currentFactoryCodes) && currentFactoryCodes.containsAll(targetFactoryCodes)) {
                    result = false;
                    break; // 无需继续检查
                }
            }
        }
        return result;
    }
}
