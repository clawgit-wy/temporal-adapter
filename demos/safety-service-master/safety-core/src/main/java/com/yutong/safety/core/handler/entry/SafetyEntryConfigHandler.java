package com.yutong.safety.core.handler.entry;

import cn.hutool.poi.excel.ExcelReader;
import com.yutong.safety.common.constants.*;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.BlackMessageListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.EmpowerDeviceListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.*;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterApproveRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.db.entity.entry.*;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.*;
import com.yutong.safety.core.db.entity.entry.vo.EntryFactoryRecordVO;
import com.yutong.safety.core.db.entity.hik.vo.SafetyHikOneToManyVO;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.dto.entry.SafetyEntryButtJointDto;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.enter.SafetyEnterAuthService;
import com.yutong.safety.core.service.enter.SafetyEnterIssuanceService;
import com.yutong.safety.core.service.enter.SafetyEnterRegisterService;
import com.yutong.safety.core.service.entry.*;
import com.yutong.safety.core.service.wechat.PushWxMesService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.*;

@Component
public class SafetyEntryConfigHandler extends AbstractSafetyWebBaseHandler<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams, SafetyEntryConfigAddParams> {
    @Resource
    private SafetyBaseService safetyBaseService;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;
    @Resource
    private SafetyEnterRegisterService enterRegisterService;
    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;
    @Resource
    private SafetyEntryConfigHistoryService safetyEntryConfigHistoryService;
    @Resource
    private SafetyBlackMessageService safetyBlackMessageService;
    @Resource
    private SafetyDictItemService safetyDictItemService;
    @Resource
    private SafetyEnterAuthService safetyEnterAuthService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;
    @Resource
    private SafetyEntryImportFailService safetyEntryImportFailService;
    @Resource
    private SafetyEnterRegisterService safetyEnterRegisterService;
    @Resource
    private SafetyEntryConfigOperateService safetyEntryConfigOperateService;
    @Resource
    private SafetyPermissionDelegationHandler safetyPermissionDelegationHandler;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private EipCommonService eipCommonService;
    @Resource
    private PushWxMesService pushWxMesService;
    @Resource
    private SafetyEntryExternalNetworkHandler safetyEntryExternalNetworkHandler;
    @Resource
    private SafetyEnterIssuanceService safetyEnterIssuanceService;
    @Resource
    private SafetyEntryBlankProcessLogService safetyEntryBlankProcessLogService;
    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;
    @Resource
    private SafetySystemParamService safetySystemParamService;

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    @Value("${bpm.backCall.formUrl}")
    private String bpmBackCallFormUrl;

    //代办的发起人待定
    @Value("${an.eip.todo.from}")
    private String formEmpNo;

    @Value("${entry.card.tcmid}")
    private String entryCardTcmid;

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
        return "entryEmpInfoForAdmin";
    }

    @Override
    protected String getRoleName() {
        return SafetyEntryConstants.ENTRY_ROLE_ADMIN;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyEntryConfig> dataList) {
        String[] title = {"状态", "对接人员工号", "对接人姓名", "备案类型", "姓名","公司名称","身份证号","手机号",
                "准入厂区", "准入时间-始", "准入时间-终", "是否夜间作业", "备注", "证件照","创建时间", "提交人手机号"};
        // 使用流操作简化填充数据
        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        EipStateEnum.getStatusDesc(info.getStatus()),
                        info.getButtJointCode(),
                        info.getButtJointName(),
                        info.getRecordType(),
                        info.getName(),
                        info.getCompanyName(),
                        info.getCardNo(),
                        info.getPhone(),
                        info.getEnterFactory(),
                        info.getEnterStartTime(),
                        info.getEnterEndTime(),
                        info.getNightFlag() == 0 ? "否" : "是",
                        info.getRemark(),
                        info.getPhotoUrl(),
                        DateUtils.format(info.getCreateTime(),2),
                        info.getSubmitPhone()
                })
                .toArray(String[][]::new);
        return Pair.of(title, values);
    }

    @Override
    protected void afterDelete(SafetyEntryConfig data,String reason, String token) {
        saveConfigOperate(data,reason,token,EntryOperateEnum.OPER_DELETE.getCode());
        // 注销权限
        // 判断是否需要注销权限
        boolean result = safetyEntryExternalNetworkHandler.revokePermissions(data);
        if (result) {
            // 权限注销
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            data.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, data.getRecordTypeCode()));
            data.setEnterEndTime(DateUtils.reduceDate(new Date(), -1 , 3));
            List<SafetyEnterIssuanceDO> list = new ArrayList<>();
            SafetyEnterIssuanceDO issuanceDO = safetyPermissionDelegationHandler.getSafetyEnterIssuanceDO(data, TokenUtils.getRealName(token));
            String deviceNo = safetyPermissionDelegationHandler.getEmpowerDevice(data.getEnterFactoryCode(), data.getRecordType());
            safetyPermissionDelegationHandler.issueCrk(issuanceDO, deviceNo);
            list.add(issuanceDO);
            safetyEnterIssuanceService.saveBatch(list);
        }
    }

    @Override
    public void beforeSelect(SafetyEntryConfigQueryParams param) {
        String roleNo = param.getRoleWorkNo();
        //不为空时，只能看到对接人=登录人的数据
        if(StringUtils.isNotBlank(roleNo)){
            param.setButtJoint(roleNo);
        }
    }

    @Override
    public void afterSelect(List<SafetyEntryConfig> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            // 厂区信息
            Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
            // 备案信息
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            list.forEach(li -> {
                li.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, li.getRecordTypeCode()));
                li.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(dataMap, li.getEnterFactoryCode()));
            });
        }
    }

    /**
     * 保存操作日志
     * @param data 备案信息
     * @param reason 删除原因
     * @param token 登录TOKEN
     * @param operateType 操作类型
     */
    public void saveConfigOperate(SafetyEntryConfig data,String reason,
                                   String token,String operateType){
        SafetyEntryConfigOperateDO operateDO = new SafetyEntryConfigOperateDO();
        BeanUtils.copyProperties(data,operateDO);
        operateDO.setId(null);
        operateDO.setOperateType(operateType);
        operateDO.setDelReason(reason);

        safetyEntryConfigOperateService.save(operateDO,token);
    }

    /**
     * 保存操作日志
     * @param data 备案信息
     * @param reason 删除原因
     * @param operateType 操作类型
     */
    public void saveConfigOperateNoToken(SafetyEntryConfig data,String reason,String operateType){
        SafetyEntryConfigOperateDO operateDO = new SafetyEntryConfigOperateDO();
        BeanUtils.copyProperties(data,operateDO);
        operateDO.setOperateType(operateType);
        operateDO.setDelReason(reason);

        safetyEntryConfigOperateService.save(operateDO);
    }

    /**
     * 保存操作日志
     * @param dataList 备案信息
     * @param operateType 操作类型
     */
    public void batchSaveOperateNoToken(List<SafetyEntryConfig> dataList,
                                         String operateType,
                                         String remark,
                                         Map<String, String> tokenUser){
        String realName = tokenUser.get("realName");
        String workNo = tokenUser.get("workNo");
        // 入场方式
        Integer entryMethod = safetyEntryConfigService.selectEntryMethodByWorkNo(workNo);
        List<SafetyEntryConfigOperateDO> operateDOS = new ArrayList<>();
        dataList.forEach(e->{
            SafetyEntryConfigOperateDO newOperate = new SafetyEntryConfigOperateDO();
            BeanUtils.copyProperties(e,newOperate);
            newOperate.setCreateBy(realName);
            newOperate.setCreateByNo(workNo);
            newOperate.setOperateType(operateType);
            newOperate.setRemark(remark);
            if (COMPANY_REGISTER_STATUS_PASS.equals(newOperate.getStatus())) {
                newOperate.setEntryMethod(entryMethod);
            }
            operateDOS.add(newOperate);
        });
        safetyEntryConfigOperateService.batchSave(operateDOS);
    }



    /**
     * 新增备案
     * @param params 备案信息
     * @return 保存状态
     */
    public String saveConfig(SafetyEntryConfigAddParams params, boolean type){
        String cardNo = params.getCardNo();
        //校验提交信息的合法性
        List<SafetyEntryConfigAddParams> list = new ArrayList<>();
        list.add(params);
        boolean modifyFlag = params.getModifyFlag() != null ? params.getModifyFlag() : false;
        //校验备案数据合法性
        String checkResult = checkFilingForBatch(list,modifyFlag, type);
        LogUtils.info("提交备件校验返回值："+checkResult);
        if(StringUtils.isNotBlank(checkResult)){
            return checkResult;
        }
        //保存
        for(SafetyEntryConfigAddParams curParams :list){
            saveSafetyEntryConfig(curParams);
        }
        SafetyEntryConfigAddParams curParams = list.get(0);
        //待审批状态时给对接人发代办和宇信通消息
        if(SafetyEntryConstants.ENTRY_CONFIG_STATUS_WAIT.equals(params.getStatus())){
            String content = "[安保管理平台]您有外来人员入厂备案申请待审批";
            //发送宇信通消息
            RestTemplate restTemplate = new RestTemplate();
            try {
                yxtRebotUtils.postForEntity(null, incomintRebotId, curParams.getButtJointCode(), content, restTemplate);
            }catch (Exception e){
                LogUtils.error("发送宇信通消息异常，{}", e);
            }
            //发送EIP代办
            try{
                Set<String> toEmpSet = list.stream().map(SafetyEntryConfigAddParams::getButtJointCode).collect(Collectors.toSet());
                sendEipToDo(toEmpSet,content);
            }catch (Exception e){
                LogUtils.error("发送EIP代办异常，{}", e);
            }
        }
        else if(ENTRY_CONFIG_STATUS_PASS.equals(params.getStatus())){
            try{
                //判断权限申请是否三分钟之内生效直接进行下发
                String entryStartTime = params.getEnterStartTime();
                if(entryStartTime.length()==16){
                    entryStartTime = entryStartTime+":00";
                }
                //提前五分钟
                String maxStartTime = DateUtils.format(DateUtils.addMinutes(new Date(),5),2);
                if(DateUtils.isBeforeTime(entryStartTime,maxStartTime)){
                    String startTime = DateUtils.format(new Date(), 3);
                    //1.下发权限
                    List<String> cardList = new ArrayList<>();
                    cardList.add(params.getCardNo());
                    List<SafetyEntryConfig> dataList = safetyPermissionDelegationHandler.getData(maxStartTime, cardList, false);
                    if (!CollectionUtils.isEmpty(dataList)) {
                        safetyPermissionDelegationHandler.issuePermissionsToCrk(dataList, startTime, "定时任务");
                    }
                }
            }catch (Exception e){
                LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"下发备案权限异常，身份证号cardNo="+cardNo);
                LogUtils.error("下发备案权限异常",e);
            }
            /*TODO 直接发布的给用户发送小程序消息*/
//            pushWxMesService.pushWxEntryConfig(curParams.getPhone(),curParams.getName(),curParams.getEnterStartTime(), curParams.getEnterEndTime(),curParams.getEnterFactory());
        }

        return "";
    }

    /**
     * 发送代办
     * @param empNoSet
     */
    public void sendEipToDo(Set<String> empNoSet,String content){
        if(CollectionUtils.isEmpty(empNoSet)){
            return;
        }
        for(String empCode:empNoSet){
            String modelId = "SAFETY-" + SafetyEntryConstants.EIPTODO_CONFIG_MODELID + "-" + empCode;
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empCode+":",e);
            }
            try {
                String taragets = "[{\"PersonNo\":\"" + empCode + "\"}]";
                String docCreator = "{\"PersonNo\":\"" + formEmpNo + "\"}";
                String modelName = "外来人员入厂备案申请";
                String link = "<![CDATA["+bpmBackCallFormUrl+"?sysId=SAFETY&modelId=" +SafetyEntryConstants.EIPTODO_CONFIG_MODEL+"&templateFormId="+ EIPTODO_CONFIG_PAGE+"]]>";
                //others = isOpenInMobile 标识禁止移动端预览
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, content, link, null, taragets, null, docCreator, null,"isOpenInMobile");
                eipCommonService.createEipToDo(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【发送代办失败】外来人员入厂备案申请 to "+empCode+":",e);
            }
        }
    }

    /**
     * 保存历史备案并更新备案
     * @param curParams 入参
     * @return 更新条数
     */
    public int saveSafetyEntryConfig(SafetyEntryConfigAddParams curParams) {
        //存历史表
        SafetyEntryConfigHistoryDO historyDO = new SafetyEntryConfigHistoryDO();
        BeanUtils.copyProperties(curParams,historyDO);
        historyDO.setId(null);
        safetyEntryConfigHistoryService.save(historyDO);
        //存主表-historyId用于状态更新
        SafetyEntryConfig data =  new SafetyEntryConfig();
        BeanUtils.copyProperties(curParams,data);
        Integer historyId = historyDO.getId();
        data.setHistoryId(historyId);
        String operType = EntryOperateEnum.OPER_INSERT.getCode();
        if(data.getId() != null){
            operType = EntryOperateEnum.OPER_UPDATE.getCode();
        }
        saveConfigOperateNoToken(data,"",operType);
        return safetyEntryConfigService.saveOrUpdate(data);
    }

    /**
     * 根据手机号获取当前生效的备案信息
     * @param phone 手机号
     * @return 返回备案明细
     */
    public Object getEntryConfigByPhone(String phone){
        List<SafetyEnterApproveRt> rtList = safetyEntryConfigService.getEntryConfigByPhone(phone);
        if(CollectionUtils.isEmpty(rtList)){
            return new VO<>(ResponseEnum.SUCCESS.getCode(),"用户未备案", "1");
        }
        Map<Integer,List<SafetyEnterApproveRt>> rtTypeMap = rtList.stream()
                .collect(Collectors.groupingBy(SafetyEnterApproveRt::getPowerType));
        List<SafetyEnterApproveRt> curPowerList = rtTypeMap.get(1);
        if(CollectionUtils.isEmpty(curPowerList)){
            SafetyEnterApproveRt afterPower = rtTypeMap.get(2).get(0);
            String enterBegin = afterPower.getEnterStartTime().substring(0,10);
            String enterEnd = afterPower.getEnterEndTime().substring(0,10);
            String retMsg = "您的入厂备案日期为"+enterBegin.substring(5,7)+"月"+enterBegin.substring(8,10)+"日-"
                    +enterEnd.substring(5,7)+"月"+enterEnd.substring(8,10)+"日,请在备案有效期内查看或重新提交备案流程";
            return new VO(ResponseEnum.SUCCESS.getCode(),retMsg, "2");
        }

        // 数据整合
        List<Map> faceMapList = new ArrayList<>();
        List<Map> qrCodeMapList = new ArrayList<>();
        Set<String> allFactoryCodes = new HashSet<>();
        Date minEndTime = null;
        // 二维码
        Set<String> factoryCodes = new HashSet<>();
        // 厂区
        Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
        for (SafetyEnterApproveRt safetyEnterApproveRt : curPowerList) {
            List<String> list = Arrays.asList(safetyEnterApproveRt.getEnterFactoryCode().split(","));
            if (!Objects.equals(1, safetyEnterApproveRt.getEntryMethod())) {
                // 二维码
                factoryCodes.addAll(list);
                qrCodeMapList.addAll(getDataMap(allFactoryCodes, dataMap, safetyEnterApproveRt, list, true));
            } else {
                // 刷脸
                faceMapList.addAll(getDataMap(allFactoryCodes, dataMap, safetyEnterApproveRt, list, false));
            }
            allFactoryCodes.addAll(list);
            Date enterEndTime = DateUtils.parse(safetyEnterApproveRt.getEnterEndTime(), 3);
            if (minEndTime == null || DateUtils.compareDates(minEndTime, enterEndTime) == 1) {
                minEndTime = enterEndTime;
            }
        }
        qrCodeMapList.addAll(faceMapList);

        SafetyEnterApproveRt maxRecord = curPowerList.stream()
                .max(Comparator.comparing(SafetyEnterApproveRt::getUpdateTime)).orElse(new SafetyEnterApproveRt());

        maxRecord.setFactorys(qrCodeMapList);
        maxRecord.setEnterEndTime(minEndTime == null ? maxRecord.getEnterEndTime() : DateUtils.format(minEndTime, 3));
        try{
            if (CollectionUtils.isNotEmpty(factoryCodes)) {
                getCrkDevice(maxRecord, factoryCodes);
            }
        }catch (Exception e){
            return new VO<>(ResponseEnum.SUCCESS.getCode(),e.getMessage(), "2");
        }
        return maxRecord;
    }

    private List<Map> getDataMap(Set<String> allFactoryCodes, Map<Integer, String> dataMap, SafetyEnterApproveRt safetyEnterApproveRt, List<String> list, boolean type) {
        return list.stream().filter(value -> !allFactoryCodes.contains(value)).map(ln -> {
            Map<String, Object> map = new HashMap<>();
            map.put("enterFactory", dataMap.getOrDefault(Integer.parseInt(ln), ""));
            map.put("enterFactoryCode", safetyEnterApproveRt.getEnterFactoryCode());
            map.put("qrCodeFlag", type);
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取备案人员类型对应的通道号
     * @param maxRecord 备案信息
     */
    public void getCrkDevice(SafetyEnterApproveRt maxRecord, Set<String> factoryCodes) {
        /*查询对应备案人员类型的入厂权限通道号*/
        EmpowerDeviceListParam empower = new EmpowerDeviceListParam();
        empower.setFactoryIdList(factoryCodes.stream()
                .map(Integer::parseInt) // 字符串转整数
                .collect(Collectors.toList()));
        empower.setEmpowerType(maxRecord.getRecordType());
        List<SafetyEmpowerDeviceDO> list = safetyEmpowerDeviceService.selectList(empower);
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未找到通道号,请联系管理员进行配置!");
        }
        int[] nums = safetyBaseService.fromatCrkDevice(list);
        String staffName = maxRecord.getName();
        String idNo = maxRecord.getCardId();
        String phoneNo = maxRecord.getPhone();
        String crossType = "1";
        String crossData = "";
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);

        rightNow.add(Calendar.MINUTE, +1);
        Date dt1 = rightNow.getTime();
        String oneHoursAgoTime = sdf.format(dt1);
        LogUtils.info("当前时间往后推1分钟：" + oneHoursAgoTime);

        // 根据手机号 当前系统时间往后推迟30S 生成二维码字符串
        String erCode = enterRegisterService.doorQrCode("B" + phoneNo, idNo, staffName, nums, crossType, oneHoursAgoTime, crossData, entryCardTcmid);
        maxRecord.setErwinCode(erCode);
    }

    /**
     * 根据手机号获取对接人信息
     * @param phone 手机号
     * @param beginTime 检索开始时间
     * @return 结果集
     */
    public Object getDockersByPhone(String phone,String beginTime){
        EntryConfigHistoryQueryParams params = new EntryConfigHistoryQueryParams();
        if(StringUtils.isBlank(beginTime)){
            params.setCreateTime(beginTime);
        }
        params.setPhone(phone);
        List<SafetyEntryConfigHistoryDO> voList = safetyEntryConfigHistoryService.selectList(params,false);
        if(CollectionUtils.isEmpty(voList)){
            return null;
        }
        //每个对接人最新的一条记录
        return voList.stream()
                // 按身份证号分组，每组内按创建时间降序排序
                .collect(Collectors.groupingBy(
                        SafetyEntryConfigHistoryDO::getButtJointCode,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .max(Comparator.comparing(SafetyEntryConfigHistoryDO::getCreateTime))
                                        .orElse(null)
                        )
                ))
                .values().stream()  // 提取分组结果的值
                .filter(Objects::nonNull)  // 过滤空值
                .collect(Collectors.toList());
    }

    /**
     * 获取历史备案记录
     * @param params 入参
     * @return 返回值
     */
    public Object getConfigHistoryByPhone(EntryConfigHistoryQueryParams params){
        List<SafetyEntryConfigHistoryDO> voList = safetyEntryConfigHistoryService.selectList(params,true);
        if(CollectionUtils.isEmpty(voList)){
            return null;
        }
        return voList;
    }

    /**
     *
     * @param enterList 备案清单
     * @param modifyFlag 是否更新注册标记
     * @return 返回状态
     */
    public String checkFilingForBatch(List<SafetyEntryConfigAddParams> enterList,Boolean modifyFlag, boolean type){

        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();

        StringBuilder returnMsg = new StringBuilder();
        //1-1 注册人员 身份证号
        List<String> carNoList = enterList.stream().map(SafetyEntryConfigAddParams::getCardNo)
                .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(carNoList)){
            return "未找到有效的身份证号，请核查提交信息";
        }
        Map<String, SafetyEnterRegisterRt> registerMap = getStringSafetyEnterRegisterRtMap(enterList);
        //1-2 注册人员 手机号
        List<String> phoneList = enterList.stream().map(SafetyEntryConfigAddParams::getPhone)
                .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, SafetyEnterRegisterRt> registerMapByPhone = getEnterRegisterRtMapForPhone(phoneList);
        //一级黑名单
        List<String> buttJointCodeList = enterList.stream().map(SafetyEntryConfigAddParams::getButtJointCode).distinct().collect(Collectors.toList());
        List<SafetyBlackMessageDO> blackMessageList = safetyBlackMessageService.getBlackMessageList(carNoList);
        Map<String,SafetyBlackMessageDO> blackMap = blackMessageList.stream().collect(Collectors.toMap(SafetyBlackMessageDO::getIdCardNo, e -> e));
        //2.获取外来人员备案类型信息
        List<DictItemRt> userTypeList = getDictEnterTypeList();
        if(CollectionUtils.isEmpty(userTypeList)){
            return "未找到有效的外来人员备案类型，请联系管理员维护信息";
        }
        Map<String,String> userTypeMap = userTypeList.stream().collect(Collectors.toMap(DictItemRt::getItemCode,
                DictItemRt::getItemName,
                (oldValue,newValue)->newValue));

        //3.获取对接人集合
        Map<String,SafetyEnterAuthRt> dockRtMap = getDockRtMap(buttJointCodeList);
        //4.获取备案人及对接人的备案次数
        String curYears = DateUtils.format(new Date(),1).substring(0,4);
        Map<String,EntryFactoryRecordVO> voMap = getEntryFactoryRecordVOs(carNoList,buttJointCodeList,curYears);

        List<SafetyEnterRegisterRt> registerRts = new ArrayList<>();

        int size = enterList.size();
        for(int i=0;i<size;i++){
            String preRes = "";
            if(size>1){
                preRes = "第"+(i+1)+"行";
            }
            SafetyEntryConfigAddParams curAddParams = enterList.get(i);
            String buttJointCode = curAddParams.getButtJointCode();
            SafetyEnterAuthRt curAuthRt = dockRtMap.get(buttJointCode);
            String sortFacCode = PinYinUtils.sortNumberString(curAddParams.getEnterFactoryCode());
            //处理准入厂区名称
            String enterFactoryName = safetyBaseService.getFactoryName(sortFacCode,factoryMap);
            curAddParams.setEnterFactoryCode(sortFacCode);
            curAddParams.setEnterFactory(enterFactoryName);
            String phone = curAddParams.getPhone();
            String cardNo = curAddParams.getCardNo();
            //校验提交信息合法性
            SafetyEnterRegisterRt cardRegister = registerMap.get(cardNo);
            SafetyEnterRegisterRt phoneRegister = registerMapByPhone.get(phone);
            String subMsg = checkForSubmit(curAddParams,blackMap,userTypeMap,preRes,curAuthRt,cardRegister,phoneRegister,modifyFlag, type);
            if(StringUtils.isBlank(subMsg)){
                subMsg = checkForFilingNum(curAddParams,voMap,curAuthRt,preRes);
            }
            if(StringUtils.isBlank(subMsg) && modifyFlag){
                if(!phone.equals(cardRegister.getFdPhone())){
                    cardRegister.setFdPhone(phone);
                    registerRts.add(cardRegister);
                }
            }
            //校验员工提交次数
            if(StringUtils.isNotBlank(subMsg)){
                returnMsg.append(subMsg).append(";");
            }
            curAddParams.setEntryMethod(safetyEntryConfigService.selectEntryMethodByWorkNo(curAddParams.getButtJointCode()));
        }
        //批量更新登记信息
        if(modifyFlag && !CollectionUtils.isEmpty(registerRts)){
            safetyEnterRegisterService.batchUpdate(registerRts);
        }
        return returnMsg.toString();
    }

    /**
     * 校验备案信息的合法性
     * @param enterFactoryConfig 备案信息
     * @param blackMap 黑名单
     * @param userTypeMap 外来人员备案类型
     * @param preMsg 提醒前缀
     * @return 返回校验结果
     */
    public String checkForSubmit(SafetyEntryConfigAddParams enterFactoryConfig,
                                 Map<String,SafetyBlackMessageDO> blackMap,
                                 Map<String,String> userTypeMap,
                                 String preMsg,
                                 SafetyEnterAuthRt curAuthRt,
                                 SafetyEnterRegisterRt cardRegister,
                                 SafetyEnterRegisterRt phoneRegister,Boolean mofidyFlag, boolean type){
        String result = preMsg+"操作失败，用户:【"+enterFactoryConfig.getName()+"】身份证：【"+enterFactoryConfig.getCardNo()+"】手机号：【"+enterFactoryConfig.getPhone()+"】";
        String idCard = enterFactoryConfig.getCardNo();
        if(ObjectUtils.isEmpty(cardRegister) || StringUtils.isBlank(cardRegister.getFdImg())){
            return result+"未注册";
        } else {
            // 登记校验
            Boolean aBoolean = safetyArtemisHandler.checkOneToMany(cardRegister.getFdImg(), cardRegister.getFdName(), cardRegister.getFdCardNo(), SafetyHikThresholdEnum.ENTRY);
            if (aBoolean) {
                SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_ENTRY_TODO");
                if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                    return result + cardRegister.getFdName() + "人脸校验失败";
                }
            }
        }
        String phone = enterFactoryConfig.getPhone();
        if(!phone.equals(cardRegister.getFdPhone())){
            if(!mofidyFlag){
                return result+"未注册";
            }
            else if (phoneRegister != null && mofidyFlag) {
                return result + "手机号已被绑定，请联系对接人处理";
            }
        }
        enterFactoryConfig.setPhotoUrl(cardRegister.getFdImg());

        SafetyBlackMessageDO blackMessageDO = blackMap.get(idCard);

        //1.先校验一级黑名单
        if (blackMessageDO != null && Constants.BLANK_FIRST_SOURCE.equals(blackMessageDO.getSource())) {
            sendYxtMsg(type, SafetyYxtMsgEnum.FILING_EHR_MSG, enterFactoryConfig, curAuthRt, null);
            return "禁止入场-核验失败-" + result;
        }
        //只拦截禁止入厂的
        if (blackMessageDO != null && ("是".equals(curAuthRt.getFdBlack()) && blackMessageDO.getState() == 1)) {
            sendYxtMsg(type, SafetyYxtMsgEnum.FILING_RESIGN_MSG, enterFactoryConfig, curAuthRt, null);
            return "禁止入场-校验失败-" + result;
        }
        // 非友商黑名单校验, true 表示是黑名单
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(Collections.singletonList(idCard));
        Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, idCard);
        if (pair.getLeft()) {
            sendYxtMsg(type, SafetyYxtMsgEnum.FILING_COMPETITOR_MSG, enterFactoryConfig, curAuthRt, pair.getRight());
            return "禁止入场-验证失败-" + result;
        }
        //2.获取对接人信息
        String recordTypeCode = enterFactoryConfig.getRecordTypeCode();//备案类型编码
        if(!userTypeMap.keySet().contains(recordTypeCode)){
            return result+"备案类型："+ recordTypeCode +"不存在，检查是否输入正确！";
        }
        String recordTypeName = userTypeMap.get(recordTypeCode);
        enterFactoryConfig.setRecordType(recordTypeName);
        // 判断对接人是否有权限
        if(curAuthRt == null || !curAuthRt.getFdFilingUserTypeCode().contains(recordTypeCode)){
            return result+"对接人员工号："+ enterFactoryConfig.getButtJointCode() +"不存在，检查是否输入正确！";
        }

        if(blackMessageDO == null){
            enterFactoryConfig.setBlankFlag(0);
        }else{
            enterFactoryConfig.setBlankFlag(1);
        }
        String enterStartTime = enterFactoryConfig.getEnterStartTime();
        String enterEndTime = enterFactoryConfig.getEnterEndTime();
        long dateInterval = DateUtils.loseDay(enterStartTime,enterEndTime);
        if(dateInterval>curAuthRt.getFdLimit()){
            return result+"备案时间区间超过备案时间限制天数";
        }
        return "";
    }

    private void sendYxtMsg(boolean type, SafetyYxtMsgEnum yxtMsgEnum, SafetyEntryConfigAddParams enterFactoryConfig, SafetyEnterAuthRt curAuthRt, String competitorInfo) {
        SafetyEntryBlankProcessLogDO log = disBlankProcessLog(enterFactoryConfig);
        log.setSubmitOpenId(enterFactoryConfig.getOpenId());
        log.setRemark(yxtMsgEnum.getErrorMessage());
        safetyEntryBlankProcessLogService.save(log);

        if (type) {
            SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(yxtMsgEnum,
                    curAuthRt.getFdEmpNo(), curAuthRt.getFdEmpName(),
                    enterFactoryConfig.getName(), enterFactoryConfig.getCardNo(), enterFactoryConfig.getPhone(),
                    competitorInfo);
            safetyBaseHandler.sendYxtMsg(vo);
        }
    }
    /**
     * 黑名单日志数据整理
     */
    private SafetyEntryBlankProcessLogDO disBlankProcessLog(SafetyEntryConfigAddParams enterFactoryConfig) {
        String name = enterFactoryConfig.getName();
        SafetyEntryBlankProcessLogDO log = new SafetyEntryBlankProcessLogDO();
        log.setSubmitName(name);
        log.setSubmitType("入场备案");
        log.setSubmitPhone(enterFactoryConfig.getCreateBy());
        log.setButtJointCode(enterFactoryConfig.getButtJointCode());
        log.setButtJointName(enterFactoryConfig.getButtJointName());
        log.setButtJointPhone(enterFactoryConfig.getButtJointPhone());
        log.setBlankCard(enterFactoryConfig.getCardNo());
        log.setBlankName(name);
        log.setBlankPhone(enterFactoryConfig.getPhone());
        log.setBlankCompany(enterFactoryConfig.getCompanyName());
        log.setRemark("Y");
        log.setCreates(enterFactoryConfig.getCreateBy(), name);
        return log;
    }

    /**
     * 批量校验备案信息
     * @param enterFactoryConfig 备案信息
     * @param curFillingMap 对接人备案权限
     * @param preMsg 弹窗提醒
     * @return 返回提醒信息
     */
    public String checkForFilingNum(SafetyEntryConfigAddParams enterFactoryConfig,
                                    Map<String,EntryFactoryRecordVO> curFillingMap,
                                    SafetyEnterAuthRt curAuthRt,
                                    String preMsg){
        String key = generateUniqueKey(enterFactoryConfig.getButtJointCode(),enterFactoryConfig.getCardNo(), enterFactoryConfig.getRecordTypeCode());
        EntryFactoryRecordVO curVO = curFillingMap.get(key);
        Integer nowDay = curVO==null?0:curVO.getRecordNum();
        Integer limitDay = curAuthRt.getFdLimit();
        if(nowDay>=limitDay){
            return preMsg+"备案次数超出限制,不允许备案";
        }
        return "";
    }

    /**
     * 批量更新
     * @param params
     * @param token
     * @return
     */
    public Object batchUpdate(SafetyEntryConfigBatchParams params,String token) {
        //处理准入厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();
        String enterFactoryName = safetyBaseService.getFactoryName(params.getEnterFactoryCode(),factoryMap);
        params.setEnterFactoryName(enterFactoryName);
        params.setStatus(COMPANY_REGISTER_STATUS_PASS);
        String[] idArr = params.getIdStr().split(",");
        SafetyEntryConfigQueryParams queryParams = new SafetyEntryConfigQueryParams();
        queryParams.setAllFlag(true);
        queryParams.setIdList(Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList()));
        List<SafetyEntryConfig> configList = safetyEntryConfigService.selectList(queryParams,false);
        // 所有元素浅拷贝，适合多数场景 保留变更前数据
        List<SafetyEntryConfig> oldConfigList = new ArrayList<>(configList);

        //校验数据是否允许批量修改
        configList = preCheckForBatch(params,configList,false, true);
        if(CollectionUtils.isEmpty(configList)){
            LogUtils.info("batchExtension入参异常","检验数据库集合为空，确认是否黑名单");
            return 0;
        }

        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String curEmpNo = tokenUser.get("workNo");
        String curEmpName = tokenUser.get("realName");
        int rows = 0;
        //保存备案记录信息
        List<Integer> historyList = configList.stream().map(SafetyEntryConfig::getHistoryId).collect(Collectors.toList());
        try {
            //批量更新历史
            safetyEntryConfigHistoryService.batchUpdateForAudit(historyList, params, curEmpNo, curEmpName, false);
            //批量更新入厂备案信息
            rows = safetyEntryConfigService.batchUpdateForAdmin(params, curEmpNo, curEmpName, false);
        }catch (Exception e){
            LogUtils.error("批量更新备案记录异常",e);
        }
        try{
            batchSaveOperateNoToken(configList,EntryOperateEnum.OPER_UPDATE.getCode(),"批量更新", tokenUser);
        }catch (Exception e){
            LogUtils.error("批量保存操作日志异常",e);
        }
        try{
            //判断权限申请是否30分钟之内生效直接进行下发
            String maxStartTime = DateUtils.format(DateUtils.addMinutes(new Date(),30),2);
            //1.下发权限 获取修改人半小时内生效的权限，若存在重新下发有效权限
            List<String> cardList = configList.stream().map(SafetyEntryConfig::getCardNo).distinct().collect(Collectors.toList());
            List<SafetyEntryConfig> dataList = safetyPermissionDelegationHandler.getData(maxStartTime, cardList, false);

            Map<String, List<SafetyEntryConfig>> oldCardNoDataMap = oldConfigList.stream().collect(Collectors.groupingBy(li -> li.getCardNo()));
            Map<String, List<SafetyEntryConfig>> newCardNoDataMap = configList.stream().collect(Collectors.groupingBy(li -> li.getCardNo()));

            //若不存在设置员工到期时间为当前，注销权限
            oldCardNoDataMap.forEach((key,value)->{
                List<SafetyEntryConfig> aliableConfig = newCardNoDataMap.get(key);
                if(CollectionUtils.isEmpty(aliableConfig)){
                    SafetyEntryConfig oldConfig = value.get(0);
                    oldConfig.setEnterEndTime(DateUtils.format(new Date(),2));
                    dataList.add(oldConfig);
                }
            });

            String startTime = DateUtils.format(new Date(), 3);
            if (!CollectionUtils.isEmpty(dataList)) {
                safetyPermissionDelegationHandler.issuePermissionsToCrk(dataList, startTime, "定时任务");
            }
        }catch (Exception e){
            LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"批量更新权限时，下发备案权限异常");
            LogUtils.error("下发备案权限异常",e);
        }
        return rows;
    }

    /**
     * 批量审批
     * @param params 批量审批入厂
     * @param token 用户token
     * @return
     */
    public Object batchAudit(SafetyEntryConfigBatchParams params,String token) {
        if(!SafetyEntryConstants.COMPANY_REGISTER_STATUS_REJECT.equals(params.getStatus())
                && !SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS.equals(params.getStatus())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"请选择审批状态");
        }
        //处理准入厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();
        String enterFactoryName = safetyBaseService.getFactoryName(params.getEnterFactoryCode(),factoryMap);
        params.setEnterFactoryName(enterFactoryName);

        String[] idArr = params.getIdStr().split(",");
        SafetyEntryConfigQueryParams queryParams = new SafetyEntryConfigQueryParams();
        queryParams.setAllFlag(true);
        queryParams.setIdList(Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList()));

        List<SafetyEntryConfig> configList = safetyEntryConfigService.selectList(queryParams,false);
        //校验时审批标记设置为true
        configList = preCheckForBatch(params,configList,true, true);

        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String curEmpNo = tokenUser.get("workNo");
        String curEmpName = tokenUser.get("realName");
        String status = params.getStatus();
        String operType = EntryOperateEnum.OPER_AUDIT.getCode();
        if(!COMPANY_REGISTER_STATUS_PASS.equals(status)){
            operType = EntryOperateEnum.OPER_REFUSE.getCode();
        }
        try{
            batchSaveOperateNoToken(configList,operType,"审批", tokenUser);
        }catch (Exception e){
            LogUtils.error("批量保存操作日志异常",e);
        }

        List<Integer> historyIdList = configList.stream().filter(e->e.getHistoryId()!=null)
                .map(SafetyEntryConfig::getHistoryId).distinct().collect(Collectors.toList());

        int rows = safetyEntryConfigService.batchUpdateForAdmin(params,curEmpNo,curEmpName,true);
        safetyEntryConfigHistoryService.batchUpdateForAudit(historyIdList,params,curEmpNo,curEmpName,true);

        //判断 审批通过时，给用户发微信小程序
        if(COMPANY_REGISTER_STATUS_PASS.equals(status)){
            try{
                //判断权限申请是否三分钟之内生效直接进行下发
                String entryStartTime = params.getEnterStartTime();
                if(entryStartTime.length()==16){
                    entryStartTime = entryStartTime+":00";
                }
                //提前五分钟
                String maxStartTime = DateUtils.format(DateUtils.addMinutes(new Date(),5),2);
                if(DateUtils.isBeforeTime(entryStartTime,maxStartTime)){
                    String startTime = DateUtils.format(new Date(), 3);
                    //1.下发权限
                    List<String> cardList = configList.stream().map(SafetyEntryConfig::getCardNo).distinct().collect(Collectors.toList());
                    List<SafetyEntryConfig> dataList = safetyPermissionDelegationHandler.getData(maxStartTime, cardList, false);
                    if (!CollectionUtils.isEmpty(dataList)) {
                        safetyPermissionDelegationHandler.issuePermissionsToCrk(dataList, startTime, "定时任务");
                    }
                }
            }catch (Exception e){
                LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"批量更新权限时，下发备案权限异常");
                LogUtils.error("下发备案权限异常",e);
            }
            for(SafetyEntryConfig curConfig:configList){
                String mobile = curConfig.getPhone();
                try{
                    //pushWxMesService.pushWxMes(mobile,"豫A91HG8","测试发送微信小程序内容");
                    pushWxMesService.pushWxEntryConfig(mobile,curConfig.getName(),curConfig.getEnterStartTime(),curConfig.getEnterEndTime(),curConfig.getEnterFactory());
                }catch (Exception e){
                    LogUtils.error(mobile+"发送微信小程序异常",e);
                }
            }
        }
        //取消代办
        entryConfigToDoDone(configList);

        return rows;
    }

    /**
     * 批量续期
     * @param params
     * @param token
     * @return
     */
    public Object batchExtension(SafetyEntryConfigBatchParams params,String token) {
        //处理准入厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();
        String enterFactoryName = safetyBaseService.getFactoryName(params.getEnterFactoryCode(),factoryMap);
        params.setEnterFactoryName(enterFactoryName);

        String[] idArr = params.getIdStr().split(",");
        SafetyEntryConfigQueryParams queryParams = new SafetyEntryConfigQueryParams();
        queryParams.setAllFlag(true);
        queryParams.setIdList(Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList()));
        List<SafetyEntryConfig> configList = safetyEntryConfigService.selectList(queryParams,false);

        configList = preCheckForBatch(params,configList,false, false);

        if(CollectionUtils.isEmpty(configList)){
            LogUtils.info("batchExtension入参异常","检验数据库集合为空，确认是否黑名单");
            return 0;
        }
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String curEmpNo = tokenUser.get("workNo");
        String curEmpName = tokenUser.get("realName");
        // 入厂方式
        Integer entryMethod = safetyEntryConfigService.selectEntryMethodByWorkNo(curEmpNo);
        configList.forEach(e->{
            e.setId(null);
            e.setCreateBy(curEmpName);
            e.setCreateByNo(curEmpNo);
            e.setUpdateBy("");
            e.setUpdateByNo("");
            e.setEntryMethod(entryMethod);
        });

        List<SafetyEntryConfig> newConfigList = new ArrayList<>();
        //批量添加历史
        safetyEntryConfigHistoryService.batchSaveHistory(configList);
        configList.forEach(e->{
            e.setHistoryId(e.getId());
            e.setId(null);
            newConfigList.add(e);
        });

        try{
            batchSaveOperateNoToken(newConfigList,EntryOperateEnum.OPER_INSERT.getCode(), "批量续期",tokenUser);
        }catch (Exception e){
            LogUtils.error("批量保存操作日志异常",e);
        }
        safetyEntryConfigService.batchSave(newConfigList);

        try{
            //判断权限申请是否三分钟之内生效直接进行下发
            String entryStartTime = params.getEnterStartTime();
            if(entryStartTime.length()==16){
                entryStartTime = entryStartTime+":00";
            }
            //提前五分钟
            String maxStartTime = DateUtils.format(DateUtils.addMinutes(new Date(),5),2);
            if(DateUtils.isBeforeTime(entryStartTime,maxStartTime)){
                String startTime = DateUtils.format(new Date(), 3);
                //1.下发权限
                List<String> cardList = configList.stream().map(SafetyEntryConfig::getCardNo).distinct().collect(Collectors.toList());
                List<SafetyEntryConfig> dataList = safetyPermissionDelegationHandler.getData(maxStartTime, cardList, false);
                if (!CollectionUtils.isEmpty(dataList)) {
                    safetyPermissionDelegationHandler.issuePermissionsToCrk(dataList, startTime, "定时任务");
                }
            }
        }catch (Exception e){
            LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"批量更新权限时，下发备案权限异常");
            LogUtils.error("下发备案权限异常",e);
        }
        return 0;
    }

    /**
     * 获取外来人员的备案类型
     * @return
     */
    public List<DictItemRt> getDictEnterTypeList() {
        List<DictItemRt> dictItemDOS = safetyDictItemService.selectDictItemListBySupperCode(Constants.FILLING_USER_TYPE_CODE);
        return dictItemDOS.stream()
                .filter(e -> "1".equals(e.getShowFlag())).collect(Collectors.toList());
    }

    /**
     * 获取对接人信息
     * @param buttJointCodeList 对接人集合
     * @return 集合
     */
    private Map<String, SafetyEnterAuthRt> getDockRtMap(List<String> buttJointCodeList) {
        SafetyEnterAuthSh authSh = new SafetyEnterAuthSh();
        authSh.setEmpNoList(buttJointCodeList);
        List<SafetyEnterAuthRt> dockRtList = safetyEnterAuthService.selectList(authSh);
        return dockRtList.stream()
                .collect(Collectors.toMap(SafetyEnterAuthRt::getFdEmpNo, e -> e));
    }

    /**
     * 统计备案人员的入厂次数
     * @param carNoList 入厂人员身份证号
     * @param buttJointCodeList 对接人员工号
     * @param curYears 指定年
     * @return 集合
     */
    public Map<String,EntryFactoryRecordVO> getEntryFactoryRecordVOs(List<String> carNoList,
                                                                      List<String> buttJointCodeList,
                                                                      String curYears) {
        EntryConfigHistoryQueryParams historyParams = new EntryConfigHistoryQueryParams();
        historyParams.setButtJointCodeList(buttJointCodeList);
        historyParams.setCardNoList(carNoList);
        List<EntryFactoryRecordVO> voList = safetyEntryConfigHistoryService.tallyEntryList(historyParams, curYears);
        return voList.stream().collect(Collectors.toMap(
                entity -> generateUniqueKey(
                        entity.getButtJointCode(),
                        entity.getCardNo(),
                        entity.getRecordTypeCode()
                ),
                entity -> entity,
                // 处理重复键冲突（保留最后出现的值）
                (existing, replacement) -> replacement
        ));
    }

    public String generateUniqueKey(Object prop1, Object prop2, Object prop3) {
        return String.format("%s|%s|%s",
                handleNull(prop1),
                handleNull(prop2),
                handleNull(prop3)
        );
    }

    private String handleNull(Object obj) {
        return (obj == null) ? "NULL" : obj.toString();
    }

    /**
     * 解析token
     * @param token
     * @param addParams
     */
    public void setCreateInfo(String token,SafetyEntryConfigAddParams addParams) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String workNo = tokenUser.get("workNo");
        String realName = tokenUser.get("realName");
        String phone = tokenUser.get("phone");
        //管理员创建或导入时，对接人设置为登录人
        addParams.setCreateBy(realName);
        addParams.setCreateByNo(workNo);
        addParams.setButtJointCode(workNo);
        addParams.setButtJointName(realName);
        addParams.setButtJointPhone(phone);
        addParams.setSubmitPhone(phone);
        addParams.setDelFlag(0);
    }

    /**
     * 判断厂区，时间是否一致
     * @param params 入参
     * @param list 集合
     * @return 返回值
     */
    public List<SafetyEntryConfig> preCheckForBatch(SafetyEntryConfigBatchParams params,List<SafetyEntryConfig> list,
                                    Boolean auditFlag, Boolean check){
        if(CollectionUtils.isEmpty(list)){
            return Collections.EMPTY_LIST;
        }
        // 高效检查：校验备案厂区，准入时间是否一致
        Set<String> distinctEnterFactory = getDistinctValues(list, SafetyEntryConfig::getEnterFactoryCode);
        Set<String> distinctStartTime = getDistinctValues(list, SafetyEntryConfig::getEnterStartTime);
        Set<String> distinctEndTime = getDistinctValues(list, SafetyEntryConfig::getEnterEndTime);
        Set<String> ditinctStatus = getDistinctValues(list, SafetyEntryConfig::getStatus);

        boolean checkFlag = distinctEnterFactory.size() == 1
                && distinctStartTime.size()==1
                && distinctEndTime.size()==1
                && ditinctStatus.size() == 1;
        if (!check) {
            // 批量续期时，状态为30 和40 的都可以续期
            checkFlag = ditinctStatus.stream().anyMatch(li -> (li.equals(COMPANY_REGISTER_STATUS_PASS) || li.equals(ENTRY_CONFIG_STATUS_LOGOUT)));
        }

        String checkStatus = SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS;
        if(auditFlag){
            checkStatus = SafetyEntryConstants.COMPANY_REGISTER_STATUS_WAIT;
        }
        if(checkFlag && check){
            List<String> statusList = new ArrayList<>(ditinctStatus);
            if(!checkStatus.equals(statusList.get(0))){
                throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"请选择正确的状态");
            }
        }
        //非审批状态 排除掉黑名单
        if(checkFlag && !auditFlag){
            Set<String> cardNoList = list.stream().map(SafetyEntryConfig::getCardNo).collect(Collectors.toSet());
            Map<String, SafetyBlackMessageDO> blackMap = getStringSafetyBlackMessageDOMap(new ArrayList<>(cardNoList));
            list = list.stream().filter(e->!blackMap.containsKey(e.getCardNo())).collect(Collectors.toList());
        }
        //校验通过后，给对象赋值
        if(checkFlag){
            String enterFactoryCode = params.getEnterFactoryCode();
            String enterBeginTime = params.getEnterStartTime();
            String enterEndTime = params.getEnterEndTime();
            String enterFactoryName = params.getEnterFactoryName();
            String newStatus;
            //批量审批时会传入审批结果 10-驳回 30-审批通过
            if(StringUtils.isNotBlank(params.getStatus())){
                newStatus = params.getStatus();
            } else {
                newStatus = "30";
            }
            list.forEach(e->{
                e.setEnterFactoryCode(enterFactoryCode);
                e.setEnterFactory(enterFactoryName);
                e.setEnterStartTime(enterBeginTime);
                e.setEnterEndTime(enterEndTime);
                e.setStatus(newStatus);  //对接人处理的直接审批通过状态
            });
        }

        if(!checkFlag){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"请选择备案权限一致人员");
        }
        return list;
    }

    private Set<String> getDistinctValues(List<SafetyEntryConfig> list,
                                          Function<SafetyEntryConfig, String> mapper) {
        return list.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    private Set<String> getDistinctStatusValues(List<SafetyEntryConfig> list,
                                          Function<SafetyEntryConfig, String> mapper) {
        return list.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public SafetyEntryConfig getInfoById(Integer id,String token){
        return safetyEntryConfigService.selectByPrimaryKey(id);
    }

    /**
     * 数据导入
     * @param param 文件信息
     * @param token 用户信息
     */
    @Override
    public VO importData(CommonImportParam param, String token) {
        Map<String,String> tokenUser = TokenUtils.getTockenUser(token);
        String phone = tokenUser.get("workNo");
        String versionNo = phone+"-"+System.currentTimeMillis();

        // 取出Excel中的数据
        List<SafetyEntryConfig> dataList = importDataList(param);

        // 校验数据
        Pair<List<SafetyEntryConfig>, List<SafetyEntryImportFailDO>> pair = importCheck(dataList, versionNo, token);
        List<SafetyEntryConfig> successList = pair.getLeft();
        List<SafetyEntryImportFailDO> errorList = pair.getRight();

        // 第四部分：数据持久化
        batchSaveImportData(successList, errorList,tokenUser);

        // 第五部分：结果返回
        return successData(buildImportResult(successList.size(), errorList.size(), versionNo));
    }

    //处理excel数据
    public List<SafetyEntryConfig> importDataList(CommonImportParam param) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(param.getFile());
        String[] titleArr = new String[]{"序号","姓名*","公司名称*","备案类型编码*","身份证号*","手机号*","准入厂区编码*","准入时间-始*(yyyy-MM-dd hh:mm)"
                ,"准入时间-终*(yyyy-MM-dd hh:mm)","是否夜间作业*","备注"};
        String[] fields = new String[]{"num","name","companyName","recordTypeCode","cardNo","phone","enterFactoryCode","enterStartTime",
                "enterEndTime","nightOrDay","remark"};
        for(int i=0;i<titleArr.length;i++){
            excelReader.addHeaderAlias(titleArr[i], fields[i]);
        }
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
        String empNo = TokenUtils.getString(token, "workNo");
        String realName = TokenUtils.getString(token, "realName");
        String phone = TokenUtils.getString(token, "phone");
        // 第二部分：数据准备
        List<String> phoneList = dataList.stream().map(SafetyEntryConfig::getPhone).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        Map<String, SafetyEnterRegisterRt> registerMap = getEnterRegisterRtMapForPhone(phoneList);
        //取所有的厂区字典和备案类型字典
        Map<Integer,String> factoryMap = safetyFieldManageService.getFactoryNameList();

        List<DictItemRt> userTypeList = getDictEnterTypeList();
        Map<String,String> userTypeMap = userTypeList.stream().collect(Collectors.toMap(DictItemRt::getItemCode, DictItemRt::getItemName));

        // 3.对接人信息
        SafetyEntryButtJointDto buttJointInfo = safetyEnterAuthService.selectButtJointInfo(empNo);

        // 4.入厂方式
        Integer entryMethod = safetyEntryConfigService.selectEntryMethodByWorkNo(empNo);

        // 5.黑名单信息
        List<String> cardNoList = dataList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cardNoList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "未找到有效的身份证号，请核查提交信息");
        }
        Map<String, SafetyBlackMessageDO> blackMap = getStringSafetyBlackMessageDOMap(cardNoList);

        // 备案次数
        String curYears = DateUtils.format(new Date(), 1).substring(0, 4);
        Map<String, EntryFactoryRecordVO> voMap = getEntryFactoryRecordVOs(cardNoList, Collections.singletonList(buttJointInfo.getFdEmpNo()), curYears);

        // 友商客户信息
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(cardNoList);

        // 第三部分：数据处理与校验
        List<SafetyEntryConfig> successList = new ArrayList<>();
        List<SafetyEntryImportFailDO> errorList = new ArrayList<>();
        List<SafetyHikOneToManyVO> checkList = new ArrayList<>();
        for (SafetyEntryConfig li : dataList) {
            // 1.数据设置与存储
            convertExcelToEntity(li, phone, realName,empNo,factoryMap,userTypeMap);
            li.setEntryMethod(entryMethod);

            StringBuilder sb = new StringBuilder();
            // 2.校验步骤
            Pair<Boolean, SafetyHikOneToManyVO> pair = validateEntry(li, buttJointInfo, registerMap, blackMap, voMap, sb, competitorMap);
            boolean isValid = pair.getLeft();
            if (!isValid) {
                addFailureInfo(li, sb.toString(), versionNo, errorList);
                continue;
            }
            // 3.成功的数据
            successList.add(li);
            checkList.add(pair.getRight());
        }
        // 成功的数据进行其他校验
       return hikCheck(successList, errorList, checkList, versionNo);
    }

    /**
     * 海康黑名单校验
     * @param successList 前置校验成功的数据
     * @param errorList 前置校验失败的数据
     * @param checkList 校验成功后准备进行海康校验的数据
     * @param versionNo 版本
     * @return 返回成功的数据和失败的数据
     */
    private Pair<List<SafetyEntryConfig>, List<SafetyEntryImportFailDO>> hikCheck(List<SafetyEntryConfig> successList, List<SafetyEntryImportFailDO> errorList, List<SafetyHikOneToManyVO> checkList, String versionNo) {
        if (CollectionUtils.isEmpty(checkList)) {
            return Pair.of(successList, errorList);
        }

        try {
            List<SafetyHikOneToManyVO> check = safetyArtemisHandler.check(checkList);
            // 校验失败的数据
            Map<String, String> dataMap = check.stream().filter(li -> StringUtils.isNotBlank(li.getResult())).collect(Collectors.toMap(SafetyHikOneToManyVO::getPhoneCardNum, SafetyHikOneToManyVO::getResult, (t1, t2) -> t1));
            if (dataMap.isEmpty()) {
                return Pair.of(successList, errorList);
            }

            List<SafetyEntryConfig> sList = new ArrayList<>();
            successList.forEach(li -> {
                String phoneCardNo = li.getPhone() + li.getCardNo();
                String failMsg = dataMap.get(phoneCardNo);
                if (StringUtils.isNotBlank(failMsg)) {
                    addFailureInfo(li, failMsg, versionNo, errorList);
                } else {
                    sList.add(li);
                }
            });
            return Pair.of(sList, errorList);
        } catch (Exception e) {
            LogUtils.error("海康检查失败：", e);
            // 将所有检查结果标记为失败
            for (SafetyEntryConfig config : successList) {
                addFailureInfo(config, "海康接口调用失败：人脸校验失败", versionNo, errorList);
            }
            return Pair.of(Collections.emptyList(), errorList);
        }
    }

    /**
     * 黑名单信息
     * @param cardNoList 身份证号
     * @return 返回黑名单的信息
     */
    public Map<String, SafetyBlackMessageDO> getStringSafetyBlackMessageDOMap(List<String> cardNoList) {
        BlackMessageListParam blackParam = new BlackMessageListParam();
        blackParam.setIdCardNoList(cardNoList);
        blackParam.setState(1); //禁止入厂
        List<SafetyBlackMessageDO> messageDOS = safetyBlackMessageService.selectList(blackParam);
        return messageDOS.stream().collect(Collectors.toMap(SafetyBlackMessageDO::getIdCardNo, Function.identity()));
    }

    /**
     * 登记人信息
     * @param dataList 数据集
     * @return 返回登记的结果
     */
    public Map<String, SafetyEnterRegisterRt> getStringSafetyEnterRegisterRtMap(List<SafetyEntryConfigAddParams> dataList) {
        List<String> cardNos = dataList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cardNos)) {
            return new HashMap<>();
        }

        // 2.登记人信息
        List<SafetyEnterRegisterRt> safetyEnterRegisterRts = safetyEnterRegisterService.selectInfoByCardNos(cardNos);
        if (CollectionUtils.isEmpty(safetyEnterRegisterRts)) {
            return new HashMap<>();
        }
        Map<String, String> dataMap = dataList.stream().filter(li -> StringUtils.isNotBlank(li.getCardNo()) && StringUtils.isNotBlank(li.getPhone())).collect(Collectors.toMap(SafetyEntryConfig::getCardNo, SafetyEntryConfig::getPhone, (t1, t2) -> t1));

        Map<String, SafetyEnterRegisterRt> confirmMap = new HashMap<>();
        Map<String, SafetyEnterRegisterRt> map = new HashMap<>();
        safetyEnterRegisterRts.forEach(li -> {
            String fdCardNo = li.getFdCardNo();
            String s = dataMap.get(fdCardNo);
            if (StringUtils.isNotEmpty(s) && Objects.equals(li.getFdPhone(), s)) {
                confirmMap.put(fdCardNo, li);
            }
            if (!map.containsKey(fdCardNo) || li.getId() > map.get(fdCardNo).getId()) {
                map.put(fdCardNo, li);
            }
        });
        // 会把确认的项覆盖进去
        map.putAll(confirmMap);
        return map;
    }

    /**
     * 登记人信息
     * @param phones 手机号数据集
     * @return 返回登记的结果
     */
    public Map<String, SafetyEnterRegisterRt> getEnterRegisterRtMapForPhone(List<String> phones) {
        // 2.登记人信息
        List<SafetyEnterRegisterRt> safetyEnterRegisterRts = safetyEnterRegisterService.selectInfoByPhones(phones);
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
    public void batchSaveImportData(List<SafetyEntryConfig> successList,
                                    List<SafetyEntryImportFailDO> errorList,
                                    Map<String,String> tokenUser) {
        if (!CollectionUtils.isEmpty(errorList)) {
            safetyEntryImportFailService.batchSave(errorList);
        }
        if (!CollectionUtils.isEmpty(successList)) {
            safetyEntryConfigHistoryService.batchSaveHistory(successList);

            List<SafetyEntryConfig> saveList = new ArrayList<>();

            //当前时间+五分钟
            String maxStartTime = DateUtils.format(DateUtils.addMinutes(new Date(),5),2);
            //待下发人员身份证号
            Set<String> sendCardList = new HashSet<>();
            successList.forEach(li -> {
                //判断权限申请是否三分钟之内生效直接进行下发
                String entryStartTime = li.getEnterStartTime();
                if(entryStartTime.length()==16){
                    entryStartTime = entryStartTime+":00";
                }
                //判断开始时间已生效，需要直接下发权限
                if(DateUtils.isBeforeTime(entryStartTime,maxStartTime)){
                    sendCardList.add(li.getCardNo());
                }
                li.setHistoryId(li.getId());
                li.setId(null);
                saveList.add(li);
            });
            safetyEntryConfigService.batchSave(saveList);
            try{
                batchSaveOperateNoToken(saveList,EntryOperateEnum.OPER_INSERT.getCode(),"导入",tokenUser);
            }catch (Exception e){
                LogUtils.error("批量保存操作日志异常",e);
            }
            //批量导入的数据进行权限下发
            try{
                if(!CollectionUtils.isEmpty(sendCardList)){
                    List<String> carList = new ArrayList<>(sendCardList);
                    String startTime = DateUtils.format(new Date(), 3);
                    //1.下发权限
                    List<SafetyEntryConfig> dataList = safetyPermissionDelegationHandler.getData(maxStartTime, carList, false);
                    if (!CollectionUtils.isEmpty(dataList)) {
                        safetyPermissionDelegationHandler.issuePermissionsToCrkAsync(dataList, startTime, "定时任务");
                    }
                }
            }catch (Exception e){
                LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"下发备案权限异常，身份证号cardNo="+sendCardList);
                LogUtils.error("下发备案权限异常",e);
            }
        }
    }

    /**
     * 数据校验
     *
     * @param excel         导入的数据信息
     * @param buttJointInfo 对接人信息
     * @param registerMap   登录人信息
     * @param blackMap      黑名单信息
     * @param competitorMap
     * @return true 表示有问题， false 表示没有问题
     */
    Pair<Boolean, SafetyHikOneToManyVO> validateEntry(SafetyEntryConfig excel, SafetyEntryButtJointDto buttJointInfo,
                          Map<String, SafetyEnterRegisterRt> registerMap, Map<String, SafetyBlackMessageDO> blackMap, Map<String, EntryFactoryRecordVO> voMap, StringBuilder sb, Map<String, String> competitorMap) {
        formatValidation(excel, buttJointInfo, sb);
        if (sb.length() > 0) return Pair.of(false, null);

        SafetyHikOneToManyVO vo = registerValidation(registerMap, excel, sb);
        if (sb.length() > 0) return Pair.of(false, null);

        blackMessageValidation(blackMap, excel, buttJointInfo, sb, competitorMap);
        if (sb.length() > 0) return Pair.of(false, null);

        checkForFilingNum(excel, voMap, buttJointInfo.getFdLimit(), sb);
        return Pair.of(sb.length() <= 0, vo);
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
            sb.append("备案类型不正确");
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
        if(info.getEnterStartTime().length()<18){
            info.setEnterStartTime(info.getEnterStartTime()+":00");
        }
        if(info.getEnterEndTime().length()<18){
            info.setEnterEndTime(info.getEnterEndTime()+":00");
        }

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
        List<String> subList = Arrays.asList(factoryCode.split(","));
        Set<String> allList = filingPark.keySet();
        // 数据校验
        return !allList.containsAll(subList);
    }

    private SafetyHikOneToManyVO registerValidation(Map<String, SafetyEnterRegisterRt> dataMap, SafetyEntryConfig li, StringBuilder sb) {
        SafetyHikOneToManyVO vo = new SafetyHikOneToManyVO();
        SafetyEnterRegisterRt safetyEnterRegisterRt = dataMap.get(li.getPhone());
        if (safetyEnterRegisterRt == null) {
            sb.append("未登记信息;");
        } else {
            if (StringUtils.isBlank(safetyEnterRegisterRt.getFdImg())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "备案人身份信息不存在，未登记信息");
            } else {
                li.setPhotoUrl(safetyEnterRegisterRt.getFdImg());
            }
            if (!safetyEnterRegisterRt.getFdName().equals(li.getName()) && !safetyEnterRegisterRt.getFdCardNo().equals(li.getCardNo())) {
                sb.append("登记信息姓名或身份证号不正确;");
            }
            vo.setPhoneCardNum(safetyEnterRegisterRt.getFdPhone() + safetyEnterRegisterRt.getFdCardNo());
            vo.setPhone(safetyEnterRegisterRt.getFdPhone());
            vo.setPhoto(safetyEnterRegisterRt.getFdImg());
            vo.setName(safetyEnterRegisterRt.getFdName());
            vo.setCardNum(safetyEnterRegisterRt.getFdCardNo());
        }
        return vo;
    }

    private void blackMessageValidation(Map<String, SafetyBlackMessageDO> blackDataMap, SafetyEntryConfig li, SafetyEntryButtJointDto buttJointInfo, StringBuilder sb, Map<String, String> competitorMap) {
        String errorMessage = "";
        SafetyBlackMessageDO safetyBlackMessageDO = blackDataMap.get(li.getCardNo());
        Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitorToEmpNo(competitorMap, li.getCardNo());

        li.setBlankType("0");
        if (safetyBlackMessageDO == null) {
            // 处理非友商黑名单情况
            if (Boolean.TRUE.equals(pair.getLeft())) {
                errorMessage = "禁止入厂-集团非友商客户";
                if (StringUtils.isNotBlank(pair.getRight())) {
                    li.setBlankType("1");
                    li.setCompetitorDirector(pair.getRight());
                }
            } else {
                li.setBlankFlag(0);
                return;
            }
        } else {
            if (Constants.BLANK_FIRST_SOURCE.equals(safetyBlackMessageDO.getSource())) {
                errorMessage = "禁止入厂-一级黑名单";
            } else if ("是".equals(buttJointInfo.getFdBlack())) {
                errorMessage = "禁止入厂-二级黑名单";
            } else if (Boolean.TRUE.equals(pair.getLeft())) {
                errorMessage = "禁止入厂-集团非友商客户";
                if (StringUtils.isNotBlank(pair.getRight())) {
                    li.setBlankType("1");
                    li.setCompetitorDirector(pair.getRight());
                }
            } else {
                li.setBlankFlag(1);
                return;
            }
        }
        sb.append(errorMessage);
    }

    /**
     * 将Excel数据转换为实体
     *
     * @param li       导入的数据对象
     * @param realName 姓名
     */
    private void convertExcelToEntity(SafetyEntryConfig li, String phone, String realName,String empNo,
                                      Map<Integer,String> factoryMap,Map<String,String> userTypeMap) {
        li.setCreates(empNo, realName);
        li.setButtJointCode(empNo);
        li.setButtJointName(realName);
        li.setButtJointPhone(phone);
        li.setStatus(ENTRY_CONFIG_STATUS_PASS);
        li.setSubmitPhone(phone);

        String enterFactory = safetyBaseService.getFactoryName(li.getEnterFactoryCode(),factoryMap);
        String userType = safetyBaseService.getValueForKey(li.getRecordTypeCode(),userTypeMap);
        li.setEnterFactory(enterFactory);
        li.setRecordType(userType);
    }

    /**
     * 添加失败信息
     *
     * @param excel     导入的数据对象
     * @param errorInfo 失败信息
     * @param versionNo 版本号
     * @param errorList 失败的数据集
     */
    private void addFailureInfo(SafetyEntryConfig excel, String errorInfo, String versionNo, List<SafetyEntryImportFailDO> errorList) {
        SafetyEntryImportFailDO failDO = new SafetyEntryImportFailDO();
        BeanUtils.copyProperties(excel, failDO);
        failDO.setFailRemark(errorInfo);
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
        String key = generateUniqueKey(enterFactoryConfig.getButtJointCode(), enterFactoryConfig.getCardNo(), enterFactoryConfig.getRecordTypeCode());
        EntryFactoryRecordVO curVO = curFillingMap.get(key);
        Integer nowDay = curVO == null ? 0 : curVO.getRecordNum();
        if (nowDay >= limitDay) {
            sb.append("备案次数超出限制,不允许备案");
        }
    }

    /**
     * 清除失效备案信息
     * 逻辑：
     *  有效数据：审批中和审批通过且在有效期内
     *  无效数据：排除有效的数据的全部是无效数据
     *  无效数据中存在有效数据的直接清理， 不存在的根据更新时间保留最新的一条
     */
    public void clearEntryConfig(){
        // 当前时间
        String curDate = DateUtils.format(new Date(),2);

        // 全部数据
        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        List<SafetyEntryConfig> allList = safetyEntryConfigService.selectList(params,false);
        if (CollectionUtils.isEmpty(allList)) {
            return;
        }

        // 获取有效的数据（审批通过和审批中的数据截止时间大于当前时间）
        params.setMinEnterEndTime(curDate);
        params.setStatusList(Arrays.asList(ENTRY_CONFIG_STATUS_PASS, ENTRY_CONFIG_STATUS_WAIT));
        List<SafetyEntryConfig> validList = safetyEntryConfigService.selectList(params,false);

        // 无效的数据
        List<SafetyEntryConfig> invalidList;
        Set<String> validSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(validList)) {
            List<Integer> ids = validList.stream().map(SafetyEntryConfig::getId).collect(Collectors.toList());
            validSet = validList.stream().filter(li -> StringUtils.isNotBlank(li.getCardNo()) && StringUtils.isNotBlank(li.getButtJointCode())).map(ln -> String.join(",", ln.getButtJointCode(), ln.getCardNo())).collect(Collectors.toSet());
            invalidList = allList.stream().filter(li -> !ids.contains(li.getId())).collect(Collectors.toList());
        } else {
            invalidList = allList;
        }
        if (CollectionUtils.isEmpty(invalidList)) {
            return;
        }

        Map<String, SafetyEntryConfig> preservedConfigs = new HashMap<>();
        List<SafetyEntryConfig> isDelete = new ArrayList<>();
        for (SafetyEntryConfig info : invalidList) {
            String onlyId = String.join(",", info.getButtJointCode(), info.getCardNo());
            if (validSet.contains(onlyId)) {
                // 有效数据包含无效数据，清理
                isDelete.add(info);
            } else {
                if (!preservedConfigs.containsKey(onlyId)) {
                    preservedConfigs.put(onlyId, info);
                } else {
                    // 保留最新的
                    if (info.getUpdateTime().after(preservedConfigs.get(onlyId).getUpdateTime())) {
                        isDelete.add(preservedConfigs.get(onlyId));
                        preservedConfigs.put(onlyId, info);
                    } else {
                        isDelete.add(info);
                    }
                }
            }
        }

        // 执行删除操作
        if (CollectionUtils.isNotEmpty(isDelete)) {
            // 取消代办
            entryConfigToDoDone(isDelete);
            // 数据删除
            List<Integer> ids = isDelete.stream().map(SafetyEntryConfig::getId).collect(Collectors.toList());
            List<List<Integer>> partition = ListUtils.partition(ids, 500);
            SafetyEntryConfigQueryParams delParams = new SafetyEntryConfigQueryParams();
            partition.forEach(li -> {
                delParams.setIdList(li);
                safetyEntryConfigService.deleteByExample(delParams);
            });
        }
    }

    /**
     * 取消代办
     * @param configList 审批的备案记录
     */
    public void entryConfigToDoDone(List<SafetyEntryConfig> configList){
        //1.提取出对接人信息
        if(CollectionUtils.isEmpty(configList)){
           return;
        }
        List<String> butJontCodes = configList.stream().map(SafetyEntryConfig::getButtJointCode)
                .distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(butJontCodes)){
            return;
        }
        //判断是否还有待审批的数据
        SafetyEntryConfigQueryParams queryParams = new SafetyEntryConfigQueryParams();
        queryParams.setButtJointCodeList(butJontCodes);
        queryParams.setStatus(ENTRY_CONFIG_STATUS_WAIT);
        queryParams.setMinEnterEndTime(DateUtils.format(new Date(),2));
        List<SafetyEntryConfig> unAuditList = safetyEntryConfigService.selectList(queryParams,false);
        Map<String,List<SafetyEntryConfig>> unAuditMap = unAuditList.stream()
                .collect(Collectors.groupingBy(SafetyEntryConfig::getButtJointCode));
        butJontCodes.forEach(empNo->{
            List<SafetyEntryConfig> curUnList = unAuditMap.get(empNo);
            if(CollectionUtils.isEmpty(curUnList)){
                String modelId = "SAFETY-" + SafetyEntryConstants.EIPTODO_CONFIG_MODELID + "-" + empNo;
                try{
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                    eipCommonService.setEipToDoDone(eipToDoVO);
                }catch (Exception e){
                    LogUtils.error("【入厂备案消除代办异常】"+",from "+formEmpNo+" to "+empNo+":",e);
                }
            }
        });
    }


    /**
     * 入厂方式配置
     * @param param
     * @param token
     * @return
     */
    public VO updateEntryMethod(SafetyEntryMethodParams param, String token) {
        // Token信息校验
        String workNo = TokenUtils.getWorkNo(token);
        if (StringUtils.isBlank(workNo)) {
            return VoUtil.fail("Token 失效");
        }
        // 数据落库
        safetyEntryConfigService.selectCountOfEntryMethod(workNo, param.getEntryMethod());
        return VoUtil.success();
    }

    public VO getEntryMethod(String token) {
        // Token信息校验
        String workNo = TokenUtils.getWorkNo(token);
        if (StringUtils.isBlank(workNo)) {
            return VoUtil.fail("Token 失效");
        }
        Integer entryMethod = safetyEntryConfigService.getEntryMethod(workNo);
        return VoUtil.successData(entryMethod);
    }

    public VO clearRedis(String buttJoinCodes, String idCards) {
        safetyBaseHandler.clearRedis(buttJoinCodes, idCards);
        return VoUtil.success();
    }
}
