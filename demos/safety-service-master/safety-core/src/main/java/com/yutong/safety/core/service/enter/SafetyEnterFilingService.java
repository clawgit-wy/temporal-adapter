package com.yutong.safety.core.service.enter;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.constants.SafetyYxtMsgEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.utils.excel.OutXSSFUtil;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyBlankYxtMsgVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.enter.*;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingBody;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingPerBody;
import com.yutong.safety.core.db.entity.enter.result.*;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingAppSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessLogDO;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.entity.result.BpmModelRt;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.entity.system.param.BlackCheckParam;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.enter.*;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.bpm.BpmServiceHandler;
import com.yutong.safety.core.handler.enter.IncomingHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.handler.pub.PubFileHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessDetailService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessLogService;
import com.yutong.safety.core.service.wechat.PushWxMesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SafetyEnterFilingService implements SafetyEnterFilingHandler {
    @Value("${logistics.card.tcmid}")
    private String logisticsCardTcmid;
    @Autowired
    private SafetyEnterFilingMapper enterFilingMapper;
    @Autowired
    private SafetyEnterFilingHistoryMapper enterFilingHistoryMapper;
    @Autowired
    private SafetyDictItemService dictItemService;
    @Autowired
    private SafetyEnterRegisterMapper enterRegisterMapper;
    @Autowired
    private SafetyEnterRegisterHandler enterRegisterHandler;
    @Autowired
    private SafetyEnterProcessMapper enterProcessMapper;
    @Autowired
    private SafetyEnterProtectMapper enterProtectMapper;
    @Autowired
    private SafetyEnterApproveMapper enterApproveMapper;
    @Autowired
    private SafetyEnterAuthMapper safetyEnterAuthMapper;
    @Autowired
    private BpmServiceHandler bpmServiceHandler;
    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    private IncomingHandler incomingHandler;
    @Autowired
    private IncomingMapper incomingMapper;
    @Autowired
    private PubFileHandler pubFileHandler;
    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyEnterIssuanceService safetyEnterIssuanceService;
    @Resource
    private SafetyEntryBlankProcessLogService safetyEntryBlankProcessLogService;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Autowired
    private YxtRebotUtils yxtRebotUtils;
    @Autowired
    private SafetyBpmWebServiceService safetyBpmWebServiceService;
    @Autowired
    PushWxMesService pushWxMesService;
    @Autowired
    private SafetyEnterCheckMapper enterCheckMapper;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    SafetyFieldManageDOMapper safetyFieldManageDOMapper;
    @Autowired
    SafetyEnterFilingService safetyEnterFilingService;
    @Autowired
    private SafetyBlackMessageService safetyBlackMessageService;
    @Autowired
    private SafetyEntryBlankProcessDetailService safetyEntryBlankProcessDetailService;

    @Resource
    private SafetyEnterRegisterService safetyEnterRegisterService;

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetySystemParamService safetySystemParamService;


    @Value("${drivingLicense}")
    private String drivingLicenseUrl;
    @Value("${driving_id}")
    private String drivingId;
    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;
    @Value("${car.in.factory.invehicle.url}")
    private String carInFactoryInvehicleUrl;

    @Value("${tms.saveEntryApply.url}")
    private String saveEntryApplyUrl;

    private static String ENTER_BPM_FORM_ID = "safety_enter_filing";
    private static String SEARCH_ADMIN_ROLE_NAME="物流车备案信息查询";

    @Override
    public List<String> selectFilingFactory(String id) {
        List<String> list = new ArrayList<>();
        String factory = enterFilingMapper.selectFilingFactory(id);
        if(StringUtils.isNotBlank(factory)){
            String[] fs = factory.split(";");
            Collections.addAll(list, fs);
        }
        return list;
    }

    @Override
    public String approveEnterFiling(SafetyEnterApprove approve, String token) {
        if(approve.getFdFilingId() == null){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        if(StringUtils.isBlank(token) && StringUtils.isBlank(approve.getApproveCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        if(approve.getApproveType()==1 && StringUtils.isBlank(approve.getFdFilingFactoryCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String filingState = enterFilingMapper.selectEnterFilingState(approve.getFdFilingId());
        if("30".equals(filingState)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "流程是已审批状态，不能重复审批");
        }
        long timeStamp = System.currentTimeMillis();
        Date enterBegin = approve.getFdFilingBegin();
        if(enterBegin != null){
            approve.setFdFilingBegin(DateUtils.parse(DateUtils.format(enterBegin,1)+" 00:00:00",2));
        }
        Date enterEnd = approve.getFdFilingEnd();
        if(enterEnd != null){
            approve.setFdFilingEnd(DateUtils.parse(DateUtils.format(enterEnd,1)+" 23:59:59",2));
        }

        String msg;
        String userCode;
        String userName;
        if(StringUtils.isBlank(token)){
            userCode = approve.getApproveCode();
            userName = approve.getApproveName();
        }else {
            userCode = TokenUtils.getWorkNo(token);
            userName = TokenUtils.getRealName(token);
        }
        String eipLoginName = safetyEmployeeMessageService.getLoginNameByNo(userCode);
        SafetyEnterProcessRt processRt = enterProcessMapper.selectEnterProcessByInstanceId(approve.getFdFilingId());
        if (processRt == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"Process not found for instance ID: " + approve.getFdFilingId());
        }
        BpmProcessParam processParam = new BpmProcessParam();
        BeanUtils.copyProperties(processRt,processParam);
        processParam.setProcessId(processRt.getFlowId());
        processParam.setUserName(eipLoginName);

        int at = approve.getApproveType();
        StringBuilder sendMsg = new StringBuilder();
        String fdOperation;
        String fdStateCode;
        String fdState;
        Map<String, String> ho = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
        Map<String, String> fs = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");

        if("31".equals(filingState)){
            msg = "T:";
            fdStateCode = "31";
        }else{
            switch (at){
                case 1:
                    msg = bpmServiceHandler.approveBpmFlowJK(processParam);
                    fdStateCode = "30";
                    break;
                case 2:
                    msg = bpmServiceHandler.discardBpmFlow(processParam);
                    fdStateCode = "21";
                    break;
                default:
                    msg = bpmServiceHandler.discardBpmFlow(processParam);
                    fdStateCode = "40";
                    break;
            }
        }
        fdState = fs.get(fdStateCode);
        // 更新审批状态
        SafetyEnterFiling enterFiling = new SafetyEnterFiling();
        enterFiling.setId(approve.getFdFilingId());
        enterFiling.setFdStateCode(fdStateCode);
        enterFiling.setFdState(fdState);
        enterFiling.setTimeOutDeal("0");
        enterFilingMapper.updateEnterFilingState(enterFiling);
        LogUtils.info(timeStamp+"-approveEnterFiling-01："+DateUtils.format(new Date(),2));
        if(at == 1){
            fdOperation = ho.get("50");
            //初始化厂区名称信息
            if(StringUtils.isBlank(approve.getFdFilingFactory())){
                List<SafetyFieldManageDO> factoryList = safetyFieldManageService.getFactoryListNew();
                Map<Integer, SafetyFieldManageDO> factoryMap = factoryList.stream().collect(Collectors.toMap(SafetyFieldManageDO::getId, Function.identity()));

                Map<String,String> factoryInfos = this.getFactoryInfosById(approve.getFdFilingFactoryCode(),factoryMap);
                if(factoryInfos != null){
                    String factoryNames = factoryInfos.get("factoryNames");
                    approve.setFdFilingFactory(factoryNames);
                }
            }
            // 添加审批节点修改的表单数据
            approve.setApproveCode(userCode);
            approve.setApproveName(userName);
            //获取接口人的部门
            List<SafetyEnterAuthRt> authRtList = safetyEnterAuthMapper.selectEnterAuthByEmpNo(userCode);
            if(!CollectionUtils.isEmpty(authRtList)){
                approve.setApproveDepart(authRtList.get(0).getFdEmpDepartment());
            }

            Integer up = enterApproveMapper.updateEnterFilingApprove(approve);
            if(up == null || up == 0){
                enterApproveMapper.insertEnterFilingApprove(approve);
            }
            LogUtils.info(timeStamp+"-approveEnterFiling-02："+DateUtils.format(new Date(),2));
            // 下发权限
            SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrkNew(approve.getFdFilingId());
            if(dockCrk != null){
                insertCrkTwo(dockCrk, true, false);
                //审批通过后增加车辆环保信息
                if("30".equals(dockCrk.getFdStateCode())){
                    SafetyEnterProtect prt = new SafetyEnterProtect();
                    String carNo = dockCrk.getFdCarNo();
                    SafetyEnterProtectRt protectRt = enterProtectMapper.selectEnterProtectByCarNo(carNo);
                    if(protectRt == null){
                        BeanUtils.copyProperties(dockCrk,prt);
                        prt.setFdCarNo(carNo);
                        prt.setCreateUser(userCode);
                        prt.setCreateTime(new Date());
                        enterProtectMapper.insertEnterProtect(prt);
                    }
                    LogUtils.info(timeStamp+"-approveEnterFiling-03："+DateUtils.format(new Date(),2));
                }
                try{
                    List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
                    curCrkList.add(dockCrk);
                    incomingHandler.syncSap(curCrkList);
                    LogUtils.info(timeStamp+"-approveEnterFiling-04："+DateUtils.format(new Date(),2));
                }catch (Exception e){
                    LogUtils.error("下发SAP异常",e);
                }
            }
        }else{
            sendMsg.append("抱歉，您的入厂备案申请未审核通过");
            fdOperation = ho.get("51");
        }

        // 通知物流司机
        String phones = "";
        String wxPhone = "";
        List<SafetyEnterFilingPer> perList = enterFilingMapper.selectEnterFilingPerList(approve.getFdFilingId());
        for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
            if (StringUtils.isNotBlank(phones)) {
                phones += ",";
            }
            phones += safetyEnterFilingPer.getFdPerPhone();
            //物流司机
            if ("10".equals(safetyEnterFilingPer.getFdPerTypeCode())) {
                wxPhone = safetyEnterFilingPer.getFdPerPhone();
            }
        }
        //审核通过发送微信消息
        LogUtils.info("weixin");
        if(at == 1){
            //发送微信消息
            String carNo = wxPhone;//手机号
            String fdCarNo = processRt.getFdCarNo();//车牌号
            String warn = "您的物流车入厂申请已通过";
            LogUtils.info("weixin1发送前");
            pushWxMesService.pushWxMes(carNo,fdCarNo,warn);
            LogUtils.info(timeStamp+"-approveEnterFiling-05："+DateUtils.format(new Date(),2));
            LogUtils.info("weixin2发送后");
        }

        if(at != 2){
            // 同步历史记录
            SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, approve.getFdFilingId(), fdOperation, userCode, userName);
            insertFilingHistory(enterFilingHistory, perList);
            LogUtils.info(timeStamp+"-approveEnterFiling-06："+DateUtils.format(new Date(),2));
        }
        LogUtils.info(timeStamp+"-approveEnterFiling-时长："+(System.currentTimeMillis()-timeStamp));
        return msg;
    }

    @Override
    public void approveEnterFilingBatch(String ids, Integer approveType, String token) {
        if(StringUtils.isBlank(ids)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String userCode = TokenUtils.getWorkNo(token);
        List<Map<String,String>> list = new ArrayList();
        String[] idArr = ids.split(";");
        for (String s : idArr) {
            List<Map> dockList = enterFilingMapper.selectDockList(s);

            if (CollectionUtils.isEmpty(dockList)) {
                continue;
            }
            if (dockList.size() > 1) {
                String msg = dockList.get(0).get("carNo") + " 存在多个接口人，不能批量审批";
                throw new BizException(ResponseEnum.FAIL.getCode(), msg);
            }else if(dockList.size() == 1){
                if(!dockList.get(0).get("dockNo").equals(userCode)){
                    String msg = "当前操作人不是 " + dockList.get(0).get("carNo") + " 流程的接口人";
                    throw new BizException(ResponseEnum.FAIL.getCode(), msg);
                }
            }else {
                String msg = dockList.get(0).get("carNo") + " 不是待审状态";
                throw new BizException(ResponseEnum.FAIL.getCode(), msg);
            }

            Map map = new HashMap();
            map.put("carNo", dockList.get(0).get("carNo"));
            map.put("factoryCode", dockList.get(0).get("factoryCode"));
            map.put("factoryName", dockList.get(0).get("factoryName"));
            map.put("filingBegin", dockList.get(0).get("filingBegin"));
            map.put("filingEnd", dockList.get(0).get("filingEnd"));
            map.put("id", s);
            list.add(map);
        }

        for (Map<String, String> map : list) {
            SafetyEnterApprove approve = new SafetyEnterApprove();
            approve.setFdFilingId(map.get("id"));
            approve.setFdFilingFactoryCode(map.get("factoryCode"));
            approve.setFdFilingFactory(map.get("factoryName"));
            approve.setFdFilingBegin(DateUtils.parse(map.get("filingBegin"), 1));
            approve.setFdFilingEnd(DateUtils.parse(map.get("filingEnd"), 1));
            approve.setApproveType(approveType);
            String msg = approveEnterFiling(approve, token);
            if(StringUtils.isNotBlank(msg) && !msg.startsWith("T")){
                String carNo = map.get("carNo");
                throw new BizException(ResponseEnum.FAIL.getCode(), carNo + "审批异常： " + msg);
            }
        }
    }

    //权限下发
    public void insertCrkNew(SafetyEnterDockCrk dockCrk, boolean updateCar, boolean updateOld) {
        if(dockCrk == null || CollectionUtils.isEmpty(dockCrk.getApproveList())){
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"下发权限数据不能为空,dockCrk={},\"}",dockCrk);
            throw new BizException(ResponseEnum.FAIL.getCode(), "下发权限数据不能为空");
        }else {
            LogUtils.info("更新备案人权限 执行下发数据权限：" + JsonUtils.toString(dockCrk));
        }
        List<SafetyFieldManageDO> factoryList = safetyFieldManageService.getFactoryListNew();
        Map<Integer,SafetyFieldManageDO> factoryMap = factoryList.stream().collect(Collectors.toMap(SafetyFieldManageDO::getId, Function.identity()));
        List<SafetyEnterIssuanceDO> issuanceDOList = new ArrayList<>();
        Map p = new HashMap();
        String factoryCode = "";
        String factoryName = "";
        List<EnterApproveCrk> list = dockCrk.getApproveList();
        int dockNum = list.size();
        Set<String> factoryCodeSet = new HashSet<>();
        Set<String> factoryNameSet = new HashSet<>();
        for (EnterApproveCrk enterApproveCrk : list) {
            String[] curFactoryArr = enterApproveCrk.getFdFilingFactoryCode().split(",");
            for(String code:curFactoryArr){
                if(StringUtils.isBlank(code)){
                    continue;
                }
                String fname = factoryMap.get(Integer.parseInt(code))==null?"":factoryMap.get(Integer.parseInt(code)).getFieldName();
                factoryCodeSet.add(code);
                factoryNameSet.add(fname);
            }
        }
        String[] fs = factoryCodeSet.toArray(new String[factoryCodeSet.size()]);
        factoryCode = String.join(",", factoryCodeSet);
        factoryName = String.join(",",factoryNameSet);

        p.put("factoryCode",fs);
        if(updateCar){
            p.put("filingType","物流车");
            List<SafetyEmpowerDeviceDO> list1 = enterApproveMapper.checkEmpowerDevice(p);
            if(CollectionUtils.isEmpty(list1)){
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"{}工厂未找到物流车入厂通道号\"}",fs);
                throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流车入厂通道号");
            }
            // 删除物流车权限
            if(updateOld){
                String preDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
                dockCrk.setBeginTime(preDate + " 00:00:00");
                dockCrk.setEndTime(preDate + " 23:59:59");
            }else {
                if (dockNum > 1) {
                    // 多个接口人 默认下发当天日期权限，其他日期权限通过定时任务下发
                    dockCrk.setBeginTime(DateUtils.format(new Date(), 1) + " 00:00:00");
                    dockCrk.setEndTime(DateUtils.format(new Date(), 1) + " 23:59:59");
                } else {
                    // 一个接口人 默认下发实际审批的日期权限
                    dockCrk.setBeginTime(list.get(0).getFdFilingBegin());
                    dockCrk.setEndTime(list.get(0).getFdFilingEnd());
                }
            }
            String carRet = "false";
            try {
                carRet = incomingHandler.insertCarAuth(dockCrk, list1);
            }catch (Exception e){
                LogUtils.error("下发物流车权限失败",e);
            }

            SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
            issuanceDO.setFdEnterTypeCode("10");
            issuanceDO.setEnterFactoryCode(factoryCode);
            issuanceDO.setEnterFactory(factoryName);
            issuanceDO.setEnterStartTime(dockCrk.getBeginTime());
            issuanceDO.setEnterEndTime(dockCrk.getEndTime());
            issuanceDO.setIsDelete(0);
            issuanceDO.setFdFilingId(dockCrk.getId());
            issuanceDO.setIssuanceResult(carRet);
            issuanceDO.setFdName(dockCrk.getFdCarNo());
            issuanceDO.setCreateBy(dockCrk.getOperName());

            issuanceDOList.add(issuanceDO);
        }
        if(updateOld){
            String preDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
            dockCrk.setBeginTime(preDate+" 00:00:00");
            dockCrk.setEndTime(preDate+" 23:59:59");
        }else {
            if(dockNum > 1){
                String curDate = DateUtils.format(new Date(), 1);
                dockCrk.setBeginTime(curDate + " 00:00:00");
                dockCrk.setEndTime(curDate + " 23:59:59");
            }else {
                dockCrk.setBeginTime(list.get(0).getFdFilingBegin());
                dockCrk.setEndTime(list.get(0).getFdFilingEnd());
            }
        }

        // 下发物流司机权限
        p.put("filingType","物流司机");
        List<SafetyEmpowerDeviceDO> list2 = enterApproveMapper.checkEmpowerDevice(p);
        if(CollectionUtils.isEmpty(list2)){
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"{}工厂未找到物流司机入厂通道号\"}",fs);
            throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流司机入厂通道号");
        }

        Set<Integer> nos = new HashSet<>();
        for (SafetyEmpowerDeviceDO channel : list2) {
            if (StringUtils.isBlank(channel.getEmpowerNo())) {
                LogUtils.info(channel.getFactoryName() + "通道号为空");
                continue;
            }
            String channelNo = channel.getEmpowerNo();
            Set<String> channelNos = Sets.newHashSet(channelNo.split(","));
            List<Integer> curNos = channelNos.stream().map(Integer::parseInt).collect(Collectors.toList());
            nos.addAll(curNos);
        }
        int[] nums = nos.stream().mapToInt(Integer::intValue).toArray();

        List<SafetyEnterFilingPer> perList = dockCrk.getPerList();
        for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
            String crossdata;
            //下发人脸
            String imgUrl = safetyEnterFilingPer.getFdPerImg();
            if (StringUtils.isNotBlank(imgUrl)) {
                crossdata = pubFileHandler.imgToBase64(imgUrl);
                String phoneNo = safetyEnterFilingPer.getFdPerPhone();
                String idno = safetyEnterFilingPer.getFdPerCard();
                String staffname = safetyEnterFilingPer.getFdPerName();
                String crosstype = "0";
                String endtime = dockCrk.getEndTime();
                String rt = "false";
                try{
                    rt = enterRegisterHandler.doorQrCode("W" + phoneNo, idno, staffname, nums, crosstype, endtime, crossdata, logisticsCardTcmid);
                }catch (Exception e){
                    LogUtils.error("更新备案人操作 执行下发权限异常",e);
                }
                LogUtils.info("更新备案人操作 执行下发权限结果：{} {} {} {}", phoneNo, staffname, endtime, rt);

                //整理出日志下发信息
                SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
                issuanceDO.setFdEnterTypeCode("20");
                issuanceDO.setEnterFactoryCode(factoryCode);
                issuanceDO.setEnterFactory(factoryName);
                issuanceDO.setEnterStartTime(dockCrk.getBeginTime());
                issuanceDO.setEnterEndTime(dockCrk.getEndTime());
                issuanceDO.setIsDelete(0);
                issuanceDO.setIssuanceResult(rt);
                issuanceDO.setFdCardNo(idno);
                issuanceDO.setFdPerPhone(phoneNo);
                issuanceDO.setFdName(staffname);
                issuanceDO.setFdImg(imgUrl);
                issuanceDO.setCreateBy(dockCrk.getOperName());
                issuanceDOList.add(issuanceDO);
            }
        }
        //批量新增日志下发权限
        if(!CollectionUtils.isEmpty(issuanceDOList)){
            safetyEnterIssuanceService.saveBatch(issuanceDOList);
        }
    }

    //权限下发
    public void insertCrkTwo(SafetyEnterDockCrk dockCrk, boolean updateCar, boolean updateOld) {
        if(dockCrk == null || CollectionUtils.isEmpty(dockCrk.getApproveList())){
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"下发权限数据不能为空,dockCrk={},\"}",dockCrk);
            throw new BizException(ResponseEnum.FAIL.getCode(), "下发权限数据不能为空");
        }else {
            LogUtils.info("更新备案人权限 执行下发数据权限：" + JsonUtils.toString(dockCrk));
        }
        List<SafetyFieldManageDO> factoryList = safetyFieldManageService.getFactoryListNew();
        Map<Integer,SafetyFieldManageDO> factoryMap = factoryList.stream().collect(Collectors.toMap(SafetyFieldManageDO::getId, Function.identity()));
        List<SafetyEnterIssuanceDO> issuanceDOList = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        String factoryCode = "";
        String factoryName = "";
        List<EnterApproveCrk> list = dockCrk.getApproveList();
        int dockNum = list.size();
        Set<String> factoryCodeSet = new HashSet<>();
        Set<String> factoryNameSet = new HashSet<>();
        for (EnterApproveCrk enterApproveCrk : list) {
            String[] curFactoryArr = enterApproveCrk.getFdFilingFactoryCode().split(",");
            for(String code:curFactoryArr){
                if(StringUtils.isBlank(code)){
                    continue;
                }
                String fname = factoryMap.get(Integer.parseInt(code))==null?"":factoryMap.get(Integer.parseInt(code)).getFieldName();
                factoryCodeSet.add(code);
                factoryNameSet.add(fname);
            }
        }
        String[] fs = factoryCodeSet.toArray(new String[factoryCodeSet.size()]);
        factoryCode = String.join(",", factoryCodeSet);
        factoryName = String.join(",",factoryNameSet);

        p.put("factoryCode",fs);
        if(updateCar){
            p.put("filingType","物流车");
            List<SafetyEmpowerDeviceDO> list1 = enterApproveMapper.checkEmpowerDevice(p);
            if(CollectionUtils.isEmpty(list1)){
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"{}工厂未找到物流车入厂通道号\"}",fs);
                throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流车入厂通道号");
            }
            // 删除物流车权限
            if(updateOld){
                String preDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
                dockCrk.setBeginTime(preDate + " 00:00:00");
                dockCrk.setEndTime(preDate + " 23:59:59");
            }else {
                if (dockNum > 1) {
                    // 多个接口人 默认下发当天日期权限，其他日期权限通过定时任务下发
                    dockCrk.setBeginTime(DateUtils.format(new Date(), 1) + " 00:00:00");
                    dockCrk.setEndTime(DateUtils.format(new Date(), 1) + " 23:59:59");
                } else {
                    // 一个接口人 默认下发实际审批的日期权限
                    dockCrk.setBeginTime(list.get(0).getFdFilingBegin());
                    dockCrk.setEndTime(list.get(0).getFdFilingEnd());
                }
            }
            String carRet = "false";
            try {
                carRet = incomingHandler.insertCarAuth(dockCrk, list1);
            }catch (Exception e){
                LogUtils.error("下发物流车权限失败",e);
            }

            SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
            issuanceDO.setFdEnterTypeCode("10");
            issuanceDO.setEnterFactoryCode(factoryCode);
            issuanceDO.setEnterFactory(factoryName);
            issuanceDO.setEnterStartTime(dockCrk.getBeginTime());
            issuanceDO.setEnterEndTime(dockCrk.getEndTime());
            issuanceDO.setIsDelete(0);
            issuanceDO.setFdFilingId(dockCrk.getId());
            issuanceDO.setIssuanceResult(carRet);
            issuanceDO.setFdName(dockCrk.getFdCarNo());
            issuanceDO.setCreateBy(dockCrk.getOperName());

            issuanceDOList.add(issuanceDO);
        }
        if(updateOld){
            String preDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
            dockCrk.setBeginTime(preDate+" 00:00:00");
            dockCrk.setEndTime(preDate+" 23:59:59");
        }else {
            if(dockNum > 1){
                String curDate = DateUtils.format(new Date(), 1);
                dockCrk.setBeginTime(curDate + " 00:00:00");
                dockCrk.setEndTime(curDate + " 23:59:59");
            }else {
                dockCrk.setBeginTime(list.get(0).getFdFilingBegin());
                dockCrk.setEndTime(list.get(0).getFdFilingEnd());
            }
        }

        List<String> cardNos = dockCrk.getPerList().stream().map(li -> li.getFdPerPhone() + "" + li.getFdPerCard()).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

        // 合并下发人脸
        // 获取符合条件的数据
        List<SafetyEnterFilingPer> filingPers = enterFilingMapper.selectMergeEnterFiling(cardNos);
        Map<String, List<SafetyEnterFilingPer>> listMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(filingPers)) {
            listMap = filingPers.stream().collect(Collectors.groupingBy(li -> li.getFdPerPhone() + "" + li.getFdPerCard()));
            List<String> codeList = Arrays.stream(filingPers.stream().map(SafetyEnterFilingPer::getMergeFactoryCode).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")).split(",")).distinct().collect(Collectors.toList());
             if (CollectionUtils.isNotEmpty(codeList)) {
                 p.put("factoryCode", codeList);
             }
        }

        // 下发物流司机权限
        p.put("filingType","物流司机");
        List<SafetyEmpowerDeviceDO> list2 = enterApproveMapper.checkEmpowerDevice(p);
        if(CollectionUtils.isEmpty(list2)){
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"{}工厂未找到物流司机入厂通道号\"}",fs);
            throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流司机入厂通道号");
        }

        Map<Integer, String> dataMap = list2.stream().collect(Collectors.toMap(SafetyEmpowerDeviceDO::getFactoryId, SafetyEmpowerDeviceDO::getEmpowerNo, (t1, t2) -> t1));

        List<SafetyEnterFilingPer> perList = dockCrk.getPerList();
        for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
            LogUtils.info("物流车下发人：{}-{}", safetyEnterFilingPer.getFdPerPhone(), safetyEnterFilingPer.getFdPerCard());
            // 厂区
            List<SafetyEnterFilingPer> filingPers1 = listMap.get(safetyEnterFilingPer.getFdPerPhone() + "" + safetyEnterFilingPer.getFdPerCard());
            LogUtils.info("物流车下发人的厂区：{}", filingPers1);
            if (CollectionUtils.isEmpty(filingPers1)) {
                continue;
            }
            List<String> codeList = Arrays.stream(filingPers1.stream().map(SafetyEnterFilingPer::getMergeFactoryCode).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")).split(",")).distinct().collect(Collectors.toList());
            String factoryNames = codeList.stream().map(li -> factoryMap.get(Integer.parseInt(li)) == null ? "" : factoryMap.get(Integer.parseInt(li)).getFieldName()).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

            // 通道号
            String nos = codeList.stream().map(li -> dataMap.getOrDefault(Integer.parseInt(li), "")).filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(","));
            LogUtils.info("物流车下发厂区的通道号：{}", nos);
            if (StringUtils.isBlank(nos)) {
                continue;
            }
            List<Integer> curNos = Arrays.stream(nos.split(","))
                    .filter(part -> !StringUtils.isBlank(part))
                    .map(Integer::parseInt)
                    .distinct()
                    .collect(Collectors.toList());
            int[] nums = curNos.stream().mapToInt(Integer::intValue).toArray();
            String crossdata;
            //下发人脸
            String imgUrl = safetyEnterFilingPer.getFdPerImg();
            if (StringUtils.isNotBlank(imgUrl)) {
                crossdata = pubFileHandler.imgToBase64(imgUrl);
                String phoneNo = safetyEnterFilingPer.getFdPerPhone();
                String idno = safetyEnterFilingPer.getFdPerCard();
                String staffname = safetyEnterFilingPer.getFdPerName();
                String crosstype = "0";
                String endtime = dockCrk.getEndTime();
                String rt = "false";
                try{
                    rt = enterRegisterHandler.doorQrCode("W" + phoneNo, idno, staffname, nums, crosstype, endtime, crossdata, logisticsCardTcmid);
                }catch (Exception e){
                    LogUtils.error("更新备案人操作 执行下发权限异常",e);
                }
                LogUtils.info("更新备案人操作 执行下发权限结果：{} {} {} {}", phoneNo, staffname, endtime, rt);

                //整理出日志下发信息
                SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
                issuanceDO.setFdEnterTypeCode("20");
                issuanceDO.setEnterFactoryCode(String.join(",", codeList));
                issuanceDO.setEnterFactory(factoryNames);
                issuanceDO.setEnterStartTime(dockCrk.getBeginTime());
                issuanceDO.setEnterEndTime(dockCrk.getEndTime());
                issuanceDO.setIsDelete(0);
                issuanceDO.setIssuanceResult(rt);
                issuanceDO.setFdCardNo(idno);
                issuanceDO.setFdPerPhone(phoneNo);
                issuanceDO.setFdName(staffname);
                issuanceDO.setFdImg(imgUrl);
                issuanceDO.setCreateBy(dockCrk.getOperName());
                issuanceDOList.add(issuanceDO);
            }
        }
        //批量新增日志下发权限
        if(!CollectionUtils.isEmpty(issuanceDOList)){
            safetyEnterIssuanceService.saveBatch(issuanceDOList);
        }
    }


    @Override
    public void insertCrk(String id, String token, List<SafetyEnterCrk> oldList) {
        if(StringUtils.isNotBlank(token)){
            String userCode = TokenUtils.getWorkNo(token);
            LogUtils.info("{} 执行了下发物流车入厂权限",userCode);
        }
        // 下发权限
        SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrkNew(id);
        if(ObjectUtils.isEmpty(dockCrk)){
            return;
        }
        insertCrkNew(dockCrk, true, false);
        try{
            List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
            curCrkList.add(dockCrk);
            incomingHandler.syncSap(curCrkList);
        }catch (Exception e){
            LogUtils.error("下发SAP异常",e);
        }
    }

    @Override
    public Map getSearchParams() {
        // 备案厂区
        List<DictItemRt> list1 = enterFilingMapper.selectFilingFactoryList();
        // 备案类型
        List<DictItemRt> list2 = dictItemService.selectDictItemListBySupperCode("enter_filing_type");
        // 人员类型
        List<DictItemRt> list3 = dictItemService.selectDictItemListBySupperCode("enter_per_type");
        // 状态
        List<DictItemRt> list4 = dictItemService.selectDictItemListBySupperCode("enter_filing_state");
        // 操作方式
        List<DictItemRt> list5 = dictItemService.selectDictItemListBySupperCode("enter_history_operation");
        Map map = new HashMap();
        map.put("p1", list1);
        map.put("p2", list2);
        map.put("p3", list3);
        map.put("p4", list4);
        map.put("p5", list5);
        return map;
    }

    @Override
    public PageVO selectEnterFilingPage(SafetyEnterFilingSh enterFilingSh, String token) {
        String isAdmin = checkAdmin(token);
        if(!"T".equals(isAdmin)){
            if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                isAdmin = "T";
            }
        }
        enterFilingSh.setIsAdmin(isAdmin);
        List<SafetyEnterFilingRt> list = enterFilingMapper.selectEnterFilingList(enterFilingSh);
        Integer count = enterFilingMapper.selectEnterFilingCount(enterFilingSh);
        return new PageVO(count, list);
    }

    @Override
    public PageVO selectEnterFilingListApp(SafetyEnterFilingAppSh enterFilingAppSh) {
        if(enterFilingAppSh==null || StringUtils.isBlank(enterFilingAppSh.getPhoneNo())){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        List<SafetyEnterFilingAppRt> list = enterFilingMapper.selectEnterFilingAppList(enterFilingAppSh);
        Integer count = enterFilingMapper.selectEnterFilingAppCount(enterFilingAppSh);
        return new PageVO(count, list);
    }

    @Override
    public SafetyEnterFilingPerAppRt selectEnterFilingPerApp(String id) {
        SafetyEnterFilingPerAppRt rt = new SafetyEnterFilingPerAppRt();
        List<SafetyEnterFilingPer> list = enterFilingMapper.selectEnterFilingPerList(id);
        if(CollectionUtils.isNotEmpty(list)){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getFdPerTypeCode().equals("10")){
                    rt.setDriver(list.get(i));
                    list.remove(i);
                    break;
                }
            }
            rt.setPer(list);
        }
        return rt;
    }

    /**
     * 限制已发布状态的车辆
     * @param carNo
     * @return
     */
    @Override
    public List selectEnterFilingCar(String carNo) {
        if(StringUtils.isBlank(carNo)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        return enterFilingMapper.selectCarList(carNo);
    }

    @Override
    public SafetyEnterFilingInfoRt selectEnterFilingInfo(String id) {
        if(StringUtils.isBlank(id)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        SafetyEnterFilingInfoRt safetyEnterFilingInfoRt = enterFilingMapper.selectEnterFilingById(id);

        if(ObjectUtils.isEmpty(safetyEnterFilingInfoRt)){
            return null;
        }

        //获取备案厂区信息，并根据厂区表进行排序
        String fdFactoryCode = safetyEnterFilingInfoRt.getFdFilingFactoryCode();
        if(StringUtils.isNotBlank(fdFactoryCode)){
            List<String> list = Arrays.asList(fdFactoryCode.split(","));
            List<SafetyFieldManageDO> listByIdList = safetyFieldManageDOMapper.getListByIdList(list);

            StringBuilder factoryCode = new StringBuilder();
            StringBuilder factoryName = new StringBuilder();

            listByIdList.stream().forEach(en -> {
                factoryCode.append(",").append(en.getId());
                factoryName.append(",").append(en.getFieldName());
            });

            if (StringUtils.isNotBlank(factoryCode)) {
                safetyEnterFilingInfoRt.setFdFilingFactoryCode(factoryCode.toString().substring(1));
            }
            if (StringUtils.isNotBlank(factoryName)) {
                safetyEnterFilingInfoRt.setFdFilingFactory(factoryName.toString().substring(1));
            }
        }

        //获取审批厂区信息，并根据厂区表进行排序
        String approveFilingFactoryCode = safetyEnterFilingInfoRt.getApproveFilingFactoryCode();
        if(StringUtils.isNotBlank(approveFilingFactoryCode)) {
            List<String> list = Arrays.asList(approveFilingFactoryCode.split(","));
            List<SafetyFieldManageDO> listByIdList = safetyFieldManageDOMapper.getListByIdList(list);

            StringBuilder factoryCode = new StringBuilder();
            StringBuilder factoryName = new StringBuilder();

            listByIdList.stream().forEach(en -> {
                factoryCode.append(",").append(en.getId());
                factoryName.append(",").append(en.getFieldName());
            });

            if (StringUtils.isNotBlank(factoryCode)) {
                safetyEnterFilingInfoRt.setApproveFilingFactoryCode(factoryCode.substring(1));
            }
            if (StringUtils.isNotBlank(factoryName)) {
                safetyEnterFilingInfoRt.setApproveFilingFactory(factoryName.substring(1));

            }
        }

        return safetyEnterFilingInfoRt;
    }

    @Override
    public void insertEnterFilingWeb(SafetyEnterFilingBody enterFiling, String token) {
        if(StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "提交人信息获取异常");
        }
        if(CollectionUtils.isEmpty(enterFiling.getDock())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "接口人信息不能为空");
        }
        if(enterFiling.getDock().size() == 1
                && StringUtils.isBlank(enterFiling.getFiling().getFdFilingFactoryCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案厂区不能为空");
        }
        if (CollectionUtils.isEmpty(enterFiling.getPer())) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案人员不能为空");
        }
        String userCode = TokenUtils.getWorkNo(token);
        String userName = TokenUtils.getRealName(token);
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");
        enterFiling.getFiling().setFdStateCode("31");
        enterFiling.getFiling().setFdState(operations.get("31"));
        enterFiling.getFiling().setFdFilingTypeCode("10");
        enterFiling.getFiling().setFdFilingType("物流车辆");
        BpmModelRt bpmModelRt = bpmServiceHandler.getModelInfo();
        insertEnterFiling(enterFiling, userCode, userName, bpmModelRt);

        //发送微信消息
        String carNo = enterFiling.getPer().get(0).getFdPerPhone();//手机号
        String fdCarNo = enterFiling.getPrt().getFdCarNo();//车牌号
        SafetyEnterFiling filing = enterFiling.getFiling();
        String warn = "您的物流车入厂申请已通过";

        pushWxMesService.pushWxMes(carNo,fdCarNo,warn);
    }

    @Override
    public void insertEnterFilingApp(SafetyEnterFilingBody enterFiling) {
        String userCode = enterFiling.getUserCode();
        String userName = enterFiling.getUserName();
        if(StringUtils.isBlank(userCode)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "提交人信息不能为空");
        }
        if(CollectionUtils.isEmpty(enterFiling.getDock())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "接口人信息不能为空");
        }
        if(enterFiling.getDock().size() == 1
                && StringUtils.isBlank(enterFiling.getFiling().getFdFilingFactoryCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案厂区不能为空");
        }
        if (CollectionUtils.isEmpty(enterFiling.getPer())) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案人员不能为空");
        }
        // 黑名单校验
        blackMessageValidation(enterFiling);

        Date enterBegin = enterFiling.getFiling().getFdFilingBegin();
        if(enterBegin != null){
            enterFiling.getFiling().setFdFilingBegin(DateUtils.parse(DateUtils.format(enterBegin,1)+" 00:00:00",2));
        }
        Date enterEnd = enterFiling.getFiling().getFdFilingEnd();
        if(enterEnd != null){
            enterFiling.getFiling().setFdFilingEnd(DateUtils.parse(DateUtils.format(enterEnd,1)+" 23:59:59",2));
        }
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");
        enterFiling.getFiling().setFdStateCode("20");
        enterFiling.getFiling().setFdState(operations.get("20"));
        enterFiling.getFiling().setFdFilingTypeCode("10");
        enterFiling.getFiling().setFdFilingType("物流车辆");
        BpmModelRt bpmModelRt = bpmServiceHandler.getModelInfo();
        if(bpmModelRt == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"物流车入厂流程配置获取异常，请进行查看");
        }
        insertEnterFiling(enterFiling, userCode, userName, bpmModelRt);
    }

    /**
     * 校验点检人黑名单权限
     * @param idCardNo 随行人员身份证信息
     * @param dockCode 接口人电话拼接
     * @param dataMap
     * @return
     */
    private boolean checkIsBlank(String idCardNo, String dockCode, Boolean checkFlag, Map<String, SafetyEnterFilingPer> dataMap){
        if(!checkFlag){
            return true;
        }
        if (StringUtils.isBlank(dockCode)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"验证未通过,请填写接口人信息");
        }
        // 存在黑名单判断 接口人的手机号是否在备案权限管理表中
        String[] interfacePersons = dockCode.split(";");
        List<SafetyEnterAuthRt> authList = enterCheckMapper.checkDockAuthBlack(interfacePersons);
        // 检查权限信息
        if (CollectionUtils.isEmpty(authList)) {
            LogUtils.info("用户{}，校验黑名单未通过",idCardNo);
            throw new BizException(ResponseEnum.FAIL.getCode(),"校验失败");
        }
        // 统计有效权限数目
        long authorizedCount = authList.stream().filter(li -> "否".equals(li.getFdBlack())).count();
        // 此处要达到，接口人的数量都在备案权限信息表中， 并且都是'是'校验通过  验证所有接口人都具有权限
        if (interfacePersons.length != authList.size() || interfacePersons.length != authorizedCount) {
            sendYxtMsg(idCardNo, authList, dataMap);
            throw new BizException(ResponseEnum.FAIL.getCode(),"校验失败");
        }
        return true;
    }

    /**
     * 非友商校验
     * @param dataMap 身份证信息
     * @param dockCode
     * @param filingPerMap
     * @param enterFiling
     * @return 返回正常的身份证信息
     */
    private Map<String, Boolean> subCheckCompetitor(Map<String, Boolean> dataMap, String dockCode, Map<String, SafetyEnterFilingPer> filingPerMap, SafetyEnterFilingBody enterFiling) {
        // 获取 为false的身份证号
        List<String> cardList = dataMap.entrySet().stream().filter(map -> !map.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cardList)) {
            return dataMap;
        }
        // 友商客户
        Map<String, Boolean> resultMap = new HashMap<>();
        Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(cardList);
        dataMap.keySet().forEach(key -> {
            Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, key);
            if (Boolean.TRUE.equals(pair.getLeft())) {
                LogUtils.info("用户{}，非友商客户，校验未通过", key);
                sendYxtMsg(SafetyYxtMsgEnum.LOGISTICS_COMPETITOR_MSG, key, dockCode, filingPerMap, pair.getRight());
                disBlankProcessLog(enterFiling, key, filingPerMap, "验证失败");
                throw new BizException(ResponseEnum.FAIL.getCode(), "验证失败");
            }
            resultMap.put(key, dataMap.get(key));
        });
        return resultMap;
    }

    /**
     * 黑名单校验规则
     * @param cardList 身份证号
     * @param dockCode dockCode
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param enterFiling
     * @return 返回MAP  key：身份证号，false：true：黑名单， false 非安保黑名单
     */
    private Map<String, Boolean> subCheckBlankList(List<String> cardList, String dockCode, String startTime, String endTime, Map<String, SafetyEnterFilingPer> dataMap, SafetyEnterFilingBody enterFiling){
        if (StringUtils.isBlank(dockCode)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"验证未通过,请填写接口人信息");
        }
        if (CollectionUtils.isEmpty(cardList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"身份证信息不存在");
        }
        List<SafetyBlackMessageDO> blackList = safetyBlackMessageService.getBlackMessageList(cardList);
        if (CollectionUtils.isNotEmpty(blackList)) {
            Map<String, Boolean> resultMap = new HashMap<>();
            blackList.forEach(li -> {
                String idCardNo = li.getIdCardNo();
                if (Constants.BLANK_FIRST_SOURCE.equals(li.getSource())) {
                    LogUtils.info("用户{}，一级黑名单，核验未通过",idCardNo);
                    sendYxtMsg(SafetyYxtMsgEnum.LOGISTICS_EHR_MSG, idCardNo, dockCode, dataMap, null);
                    disBlankProcessLog(enterFiling, idCardNo, dataMap, "核验失败");
                    throw new BizException(ResponseEnum.FAIL.getCode(),"核验失败");
                }
                if (checkEntry(idCardNo, dockCode, startTime, endTime))  {
                    resultMap.put(idCardNo, true);
                } else {
                    String cardNo = li.getCardNo();
                    boolean bool = false;
                    if(StringUtils.isNotBlank(cardNo)){
                        bool = safetyBlackMessageService.checkCardNo(cardNo);
                    }
                    resultMap.put(idCardNo, bool);
                }
            });
            return resultMap;
        } else {
            return cardList.stream().collect(Collectors.toMap(li -> li, li -> false));
        }
    }

    private void sendYxtMsg(String idCardNo, List<SafetyEnterAuthRt> authList, Map<String, SafetyEnterFilingPer> dataMap) {
        if (CollectionUtils.isNotEmpty(authList)) {
            for (SafetyEnterAuthRt li : authList) {
                SafetyEnterFilingPer per = dataMap.getOrDefault(idCardNo, new SafetyEnterFilingPer());
                SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(SafetyYxtMsgEnum.LOGISTICS_RESIGN_MSG, li.getFdEmpNo(), li.getFdEmpName(),
                        per.getFdPerName(), idCardNo, per.getFdPerPhone(), null);
                safetyBaseHandler.sendYxtMsg(vo);
            }
        }
    }

    private void sendYxtMsg(SafetyYxtMsgEnum yxtMsgEnum, String idCardNo, String dockCode, Map<String, SafetyEnterFilingPer> dataMap, String competitorInfo) {
        // 存在黑名单判断 接口人的手机号是否在备案权限管理表中
        String[] interfacePersons = dockCode.split(";");
        List<SafetyEnterAuthRt> authList = enterCheckMapper.checkDockAuthBlack(interfacePersons);
        if (CollectionUtils.isNotEmpty(authList)) {
            for (SafetyEnterAuthRt li : authList) {
                SafetyEnterFilingPer per = dataMap.getOrDefault(idCardNo, new SafetyEnterFilingPer());
                SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(yxtMsgEnum, li.getFdEmpNo(), li.getFdEmpName(),
                        per.getFdPerName(), idCardNo, per.getFdPerPhone(),
                        competitorInfo);
                safetyBaseHandler.sendYxtMsg(vo);
            }
        }
    }

    private boolean checkEntry(String idCardNo, String dockCode, String startTime, String endTime) {
        //判断二级黑名单是否有“黑名单入厂申请”记录
        SafetyEntryBlankProcessDetailQueryParams detailQueryParams = new SafetyEntryBlankProcessDetailQueryParams();
        detailQueryParams.setCardNo(idCardNo);
        detailQueryParams.setApplyStatus("1"); //审批通过的
        detailQueryParams.setContainBeginTime(startTime);
        detailQueryParams.setContainEndTime(endTime);
        detailQueryParams.setButtJoint(dockCode); //增加对接人
        detailQueryParams.setNullRecordType(true); //查询备案类型为空的数据
        List<SafetyEntryBlankProcessDetailDO> applyList = safetyEntryBlankProcessDetailService.selectList(detailQueryParams,false);
        //没有“黑名单入厂申请”的记录时，返回true
        return CollectionUtils.isEmpty(applyList);
    }

    @Override
    public void insertEnterFiling(SafetyEnterFilingBody body, String workNo, String tokenUser,BpmModelRt bpm) {
        List<SafetyEnterFilingPer> perList = body.getPer();
        String driverName = "";
        for (SafetyEnterFilingPer per : perList) {
            if("10".equals(per.getFdPerTypeCode())){
                driverName = per.getFdPerName();
            }
        }
        // 主表数据
        Date date = new Date();
        List<SafetyEnterFiling> enterFilings = new ArrayList<>();
        List<SafetyEnterFilingDock> dockList = body.getDock();
        SafetyEnterProtect prt = body.getPrt();
        for (SafetyEnterFilingDock safetyEnterFilingDock : dockList) {
            String formInstanceId = UUID.randomUUID().toString().replace("-", "");
            SafetyEnterFiling filing = new SafetyEnterFiling();
            BeanUtils.copyProperties(body.getFiling(), filing);
            String goodsName = filing.getFdGoodsName();
            if(StringUtils.isNotBlank(goodsName)){
                goodsName.replaceAll("\\\\x","");
            }
            filing.setFdGoodsName(goodsName); //处理表情
            filing.setFdDockNo(safetyEnterFilingDock.getFdDockNo());
            filing.setFdDockName(safetyEnterFilingDock.getFdDockName());
            filing.setFdDockPhone(safetyEnterFilingDock.getFdDockPhone());
            filing.setFdDockDepartment(safetyEnterFilingDock.getFdDockDepartment());
            filing.setId(formInstanceId);
            //初始化车辆环保信息
            if(!ObjectUtils.isEmpty(prt)){
                filing.setFdCarBelong(prt.getFdCarBelong());
                filing.setFdDriverName(prt.getFdDriverName());
                filing.setFdProtectGrade(prt.getFdProtectGrade());
                filing.setFdDetailedImg(prt.getFdDetailedImg());
                filing.setFdVin(prt.getFdVin());
                filing.setFdCarImg(prt.getFdCarImg());
                filing.setFdCarDate(prt.getFdCarDate());
                filing.setFdEngine(prt.getFdEngine());
            }
            enterFilings.add(filing);
        }
        // 触发审批流程给接口人
        String fdKey = UUID.randomUUID().toString().replace("-", "");
        String fdStateCode = body.getFiling().getFdStateCode();
        for (SafetyEnterFiling enterFiling : enterFilings) {
            SafetyEnterProcess enterProcess = new SafetyEnterProcess();
            // 供应商名称+司机姓名+车牌号+送货/取货车辆入厂申请
            StringBuffer subject = new StringBuffer();
            subject.append(enterFiling.getFdSupplierName());
            subject.append(driverName);
            subject.append(enterFiling.getFdCarNo());
            subject.append(enterFiling.getFdEnterType());
            subject.append("入厂申请");
            String formInstanceId = enterFiling.getId();

            // 添加主表数据
            enterFiling.setFdKey(fdKey);
            enterFiling.setCreateTime(date);
            enterFiling.setCreateUser(workNo);
            enterFiling.setUpdateTime(date);
            enterFiling.setUpdateUser(tokenUser);
            enterFiling.setFdGoodsUpdateTime(enterFiling.getFdFilingBegin());
            int row = enterFilingMapper.insertEnterFiling(enterFiling);

            //先保存备案信息再触发流程
            if ("20".equals(fdStateCode)) {
                // 根据接口人员工号获取登录名
                String empNo = enterFiling.getFdDockNo();
                String eipLoginName = safetyEmployeeMessageService.getLoginNameByNo(empNo);
                BpmProcessParam processParam = new BpmProcessParam();
                processParam.setBpmTemplateId(bpm.getTemplateId());
                processParam.setFormInstanceId(formInstanceId);
                processParam.setModelId(bpm.getModelId());
                processParam.setTemplateFormId(bpm.getFormId());
                processParam.setSubject(subject.toString());
                processParam.setUserName(eipLoginName);
                if(row > 0){
                    String flowId = bpmServiceHandler.createAndApproveBpmFlow(processParam);
                    enterProcess.setFlowId(flowId);
                }
            }else{
                enterProcess.setFlowId("");
            }
            // 添加审批记录
            enterProcess.setTitle(subject.toString());
            enterProcess.setCreator(workNo);
            enterProcess.setCreateTime(date);
            enterProcess.setStatus(fdStateCode);
            enterProcess.setModelId(bpm.getModelId());
            enterProcess.setTemplateFormId(bpm.getFormId());
            enterProcess.setFormInstanceId(formInstanceId);
            enterProcessMapper.insertEnterProcess(enterProcess);

            // 添加子表数据
            Map perTypes = dictItemService.selectDictItemMapBySupperCode("enter_per_type");
            for (SafetyEnterFilingPer per : perList) {
                SafetyEnterRegisterRt registerRt = enterRegisterHandler.selectEnterRegisterByCard(per.getFdPerCard());
                String perType = String.valueOf(perTypes.get(per.getFdPerTypeCode()));
                per.setFdPerType(perType);
                if(registerRt != null){
                    per.setFdPerImg(registerRt.getFdImg());
                }
                per.setFilingId(enterFiling.getId());
            }
            enterFilingMapper.insertEnterFilingPerBatch(perList);

            //新增历史记录
            Map<String, String> ho = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
            SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, enterFiling.getId(), ho.get("30"), workNo, tokenUser);
            enterFilingHistory.setFdCarNo(enterFiling.getFdCarNo());
            insertFilingHistory(enterFilingHistory,perList);
        }

        if ("31".equals(fdStateCode)) {
            for (SafetyEnterFiling enterFiling : enterFilings) {
                // 手动添加
                SafetyEnterApprove approve = new SafetyEnterApprove();
                approve.setApproveCode(workNo);
                approve.setApproveName(tokenUser);
                approve.setApproveType(1);
                approve.setFdFilingBegin(enterFiling.getFdFilingBegin());
                approve.setFdFilingEnd(enterFiling.getFdFilingEnd());
                approve.setFdFilingFactoryCode(enterFiling.getFdFilingFactoryCode());
                approve.setFdFilingFactory(enterFiling.getFdFilingFactory());
                approve.setFdFilingId(enterFiling.getId());

                approveEnterFiling(approve, null);
            }
        }
    }

    /**
     * 添加备案记录
     */
    public void insertFilingHistory(SafetyEnterFilingHistory filingHistory, List<SafetyEnterFilingPer> perList){
        enterFilingHistoryMapper.insertEnterFilingHistory(filingHistory);
        if (CollectionUtils.isNotEmpty(perList)) {
            List<SafetyEnterFilingPerHistory> perHisList = new ArrayList<>();
            for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
                SafetyEnterFilingPerHistory his = new SafetyEnterFilingPerHistory();
                his.setFilingId(filingHistory.getId());
                his.setFdPerTypeCode(safetyEnterFilingPer.getFdPerTypeCode());
                his.setFdPerType(safetyEnterFilingPer.getFdPerType());
                his.setFdPerName(safetyEnterFilingPer.getFdPerName());
                his.setFdPerCard(safetyEnterFilingPer.getFdPerCard());
                his.setFdPerPhone(safetyEnterFilingPer.getFdPerPhone());
                his.setFdPerImg(safetyEnterFilingPer.getFdPerImg());
                perHisList.add(his);
            }
            enterFilingHistoryMapper.insertEnterFilingPerHistory(perHisList);
        }
    }

    @Override
    public void updateEnterFiling(SafetyEnterFilingBody body, String token) {
        long timesStas = System.currentTimeMillis();
        if(StringUtils.isBlank(body.getFiling().getId())){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        if(CollectionUtils.isEmpty(body.getDock())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "接口人信息不能为空");
        }
        if(body.getDock().size() == 1
                && StringUtils.isBlank(body.getFiling().getFdFilingFactoryCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案厂区不能为空");
        }
        LogUtils.info(timesStas+"-updateEnterFiling-01:"+DateUtils.format(new Date(),2));
        // 更新前数据
        SafetyEnterFiling enterFiling = body.getFiling();
        SafetyEnterFilingInfoRt old = enterFilingMapper.selectEnterFilingById(enterFiling.getId());
        // 原数据
        SafetyEnterDockCrk oldCrk = incomingMapper.selectSafetyEnterDockCrk(enterFiling.getId());

        //更新数据
        safetyEnterFilingService.updateData(token,enterFiling,body,old);

        //下发权限
        LogUtils.info("更新备案人操作 开始下发老数据权限");
        insertCrkNew(oldCrk, false, true);
        LogUtils.info("更新备案人操作 开始下发新数据权限");
        SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrkNew(enterFiling.getId());
        if(dockCrk != null){
            insertCrkNew(dockCrk, true, false);
            try{
                List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
                curCrkList.add(dockCrk);
                incomingHandler.syncSap(curCrkList);
            }catch (Exception e1){
                LogUtils.error("下发SAP异常",e1);
            }
        }
    }

    public void updateData(String token, SafetyEnterFiling enterFiling, SafetyEnterFilingBody body, SafetyEnterFilingInfoRt old){
        long timesStas = System.currentTimeMillis();
        // 执行更新
        String workNo = TokenUtils.getWorkNo(token);
        String tokenUser = TokenUtils.getRealName(token);
        enterFiling.setUpdateTime(new Date());
        enterFiling.setUpdateUser(tokenUser);
        enterFilingMapper.updateEnterFiling(enterFiling);

        LogUtils.info(timesStas+"-updateEnterFiling-02:"+DateUtils.format(new Date(),2));
        // 子表数据
        enterFilingMapper.deleteEnterFilingPerAll(enterFiling.getId());
        List<SafetyEnterFilingPer> perList = body.getPer();
        if(CollectionUtils.isNotEmpty(perList)){
            for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
                safetyEnterFilingPer.setFilingId(enterFiling.getId());
            }
            enterFilingMapper.insertEnterFilingPerBatch(perList);
            LogUtils.info(timesStas+"-updateEnterFiling-03:"+DateUtils.format(new Date(),2));
        }
        // 同步历史表
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
        String fdOperation = operations.get("10");
        String b = DateUtils.format(enterFiling.getFdFilingBegin(), 2);
        String e = DateUtils.format(enterFiling.getFdFilingEnd(), 2);
        if(!b.equals(old.getFdFilingBegin()) || !e.equals(old.getFdFilingEnd())){
            fdOperation = operations.get("40");
        }
        //保存修改记录
        SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, enterFiling.getId(), fdOperation, workNo, tokenUser);
        insertFilingHistory(enterFilingHistory, perList);
        LogUtils.info(timesStas+"-updateEnterFiling-04:"+DateUtils.format(new Date(),2));
        //更新对接人审批后记录
        SafetyEnterApprove approve = new SafetyEnterApprove();
        BeanUtils.copyProperties(enterFiling,approve);
        approve.setApproveCode(old.getDockList().get(0).getFdDockNo());
        approve.setApproveName(old.getDockList().get(0).getFdDockNo());
        approve.setApproveDepart(old.getDockList().get(0).getFdDockDepartment());
        approve.setApproveType(1);
        approve.setFdFilingBegin(DateUtils.parse(DateUtils.format(enterFiling.getFdFilingBegin(),1)+" 00:00:00",2));
        approve.setFdFilingEnd(DateUtils.parse(DateUtils.format(enterFiling.getFdFilingEnd(),1)+" 23:59:59",2));
        approve.setFdFilingId(enterFiling.getId());
        Integer up = enterApproveMapper.updateEnterFilingApprove(approve);

        LogUtils.info(timesStas+"-updateEnterFiling-05:"+DateUtils.format(new Date(),2));
        if(up == null || up == 0){
            enterApproveMapper.insertEnterFilingApprove(approve);
            LogUtils.info(timesStas+"-updateEnterFiling-06:"+DateUtils.format(new Date(),2));
        }
        LogUtils.info(timesStas+"-updateData-时长:"+(System.currentTimeMillis()-timesStas));
    }

    @Override
    public void updateEnterFilingPer(SafetyEnterFilingPerBody perBody) {
        long timeStas = System.currentTimeMillis();
        if(StringUtils.isBlank(perBody.getUserCode()) || StringUtils.isBlank(perBody.getUserName())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "当前用户的手机号和姓名不能为空");
        }
        List<SafetyEnterFilingPer> perList = perBody.getPer();
        if(StringUtils.isBlank(perBody.getFilingId()) || CollectionUtils.isEmpty(perList)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        LogUtils.info("更新备案人操作 新数据：" + JsonUtils.toString(perBody));
        // 原数据
        SafetyEnterDockCrk old = incomingMapper.selectSafetyEnterDockCrk(perBody.getFilingId());
        LogUtils.info("更新备案人操作 老数据：" + JsonUtils.toString(old));
        // 更新操作
        Boolean bool = false;
        List delList = new ArrayList();
        List<SafetyEnterFilingPer> delPerList = new ArrayList<>();
        StringBuilder updateMsg = new StringBuilder();
        for (SafetyEnterFilingPer per : perList) {
            per.setFilingId(perBody.getFilingId());
            if (per.getId() != null && per.getId() != 0) {
                Integer change = enterFilingMapper.checkEnterFilingPerChange(per);
                if (change == null || change == 0) {
                    bool = true;
                }
                enterFilingMapper.updateEnterFilingPer(per);
                delList.add(per.getId());
                delPerList.add(per);
            } else {
                enterFilingMapper.insertEnterFilingPer(per);
                delList.add(per.getId());
                bool = true;
            }
            if (bool) {
                updateMsg.append(per.getFdPerName()).append(per.getFdPerPhone()).append(";");
            }
        }
        LogUtils.info(timeStas+"-updateEnterFilingPer-01:"+DateUtils.format(new Date(),2));
        if(CollectionUtils.isNotEmpty(delList)){
            Map map = new HashMap();
            map.put("list", delList);
            map.put("filingId", perBody.getFilingId());
            enterFilingMapper.deleteEnterFilingPer(map);
            try{
                old.setPerList(delPerList);
                //废弃权限
                insertCrkNew(old,false,true);
            }catch (Exception e){
                LogUtils.warn("随行人员下发权限异常",e.getMessage());
            }
        }
        LogUtils.info(timeStas+"-updateEnterFilingPer-02:"+DateUtils.format(new Date(),2));
        // 同步历史表
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
        SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, perBody.getFilingId(), operations.get("10"), perBody.getUserCode(), perBody.getUserName());
        insertFilingHistory(enterFilingHistory, perList);

        // 如果数据发送变化，从新下发权限
        if(bool){
            //发送机器人消息推送
            LogUtils.info("更新备案人操作 开始下发老数据权限");
            LogUtils.info("更新备案人操作 开始下发新数据权限");
            //insertCrkNew(old,false,true);
            SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrkNew(perBody.getFilingId());
            //更新下发权限
            insertCrkNew(dockCrk,false,false);
            LogUtils.info(timeStas+"-updateEnterFilingPer-03:"+DateUtils.format(new Date(),2));
            String msg = Joiner.on("").join("【物流车入厂备案】",
                    dockCrk.getFdCarNo(),
                    "司机",
                    dockCrk.getEnterDrvierName(),
                    dockCrk.getEnterDrvierPhone(),
                    "修改了随行人员信息为：“,",
                    updateMsg.toString(),
                    "“");
            if(StringUtils.isNotEmpty(dockCrk.getFlowId())){
                msg = Joiner.on("").join(msg,"备案详情：");
            }
            sendYxtMsg(dockCrk,msg,dockCrk.getFdDockNo());
            LogUtils.info(timeStas+"-updateEnterFilingPer-04:"+DateUtils.format(new Date(),2));
        }

        LogUtils.info(timeStas+"-updateEnterFilingPer-时长:"+(System.currentTimeMillis()-timeStas));
    }

    @Override
    public String updateEnterFilingState(String formId, String processData, String state) {
        String rt = "T";
        LogUtils.info("物流车辆入厂备案回调参数 formId={} ,processData={}",formId, processData);
        try {
            JSONObject formIdObj = JSONObject.parseObject(formId);
            String formInstanceId = formIdObj.getString("formInstanceId");
            Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");
            SafetyEnterFiling enterFiling = new SafetyEnterFiling();
            enterFiling.setId(formInstanceId);
            enterFiling.setFdStateCode(state);
            enterFiling.setFdState(operations.get(state));
            enterFilingMapper.updateEnterFilingState(enterFiling);

        }catch (Exception e){
            LogUtils.error("物流车辆入厂备案回调执行异常", e);
            rt = "F:"+e.getMessage();
        }
        return rt;
    }

    @Override
    public String updateEnterTimeOut() {
        List<String> list = enterFilingMapper.selectEnterTimeOut();
        if(CollectionUtils.isNotEmpty(list)){
            Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");
            Map map = new HashMap();
            map.put("fdStateCode", "50");
            map.put("fdState", operations.get("50"));
            map.put("list", list);
            enterFilingMapper.updateEnterTimeOut(map);

            // 同步历史表
            Map<String, String> ho = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
            for (String s : list) {
                SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, s, ho.get("10"), "TASK", "定时任务");
                List<SafetyEnterFilingPer> perList = enterFilingMapper.selectEnterFilingPerList(s);
                insertFilingHistory(enterFilingHistory, perList);
            }
        }
        return null;
    }

    @Override
    public String updateEnterOwer(String id, String carNo, String dockNo, String date, Boolean isTask) {
        long s = System.currentTimeMillis();
        LogUtils.info("定时任务更新物流车及人员入厂权限 执行开始");
        List<String> list = new ArrayList<>();
        if(StringUtils.isBlank(id)){
            if(StringUtils.isBlank(date)){
                date = DateUtils.format(new Date(), 2);
            }else {
                isTask = true;
                date = date + " 12:00:00";
            }
            Map<String,String> map = new HashMap();
            map.put("dockNo", dockNo);
            map.put("carNo", carNo);
            map.put("date", date);
            list = enterFilingMapper.selectTodayEnterMaxList(map);
        }else {
            list.add(id);
        }
        if(CollectionUtils.isEmpty(list)){
            LogUtils.info("定时任务更新物流车及人员入厂权限 执行结束 未找到待下发车辆列表！");
            return null;
        }
        LogUtils.info("定时任务更新物流车及人员入厂权限,isTask={}",isTask);
        if(isTask){
            //下发指定日期内的有效流程
            for (String value : list) {
                SafetyEnterDockCrk dockCrk = incomingMapper.selectEnterDockCrkByMap(value,date);
                if (dockCrk != null) {
                    try{
                        insertCrkTwo(dockCrk, true, false);
                        List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
                        curCrkList.add(dockCrk);
                        incomingHandler.syncSap(curCrkList);
                    }catch (Exception e){
                        LogUtils.error("下发SAP异常",e);
                    }
                }
            }
        }else{
            //下发当前日期内的有效流程
            for (String value : list) {
                SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrkNew(value);
                if (dockCrk != null) {
                    try{
                        insertCrkTwo(dockCrk, true, false);
                        List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
                        curCrkList.add(dockCrk);
                        incomingHandler.syncSap(curCrkList);
                    }catch (Exception e){
                        LogUtils.error("下发SAP异常",e);
                    }
                }
            }
        }

        long e = System.currentTimeMillis();
        LogUtils.info("定时任务更新物流车及人员入厂权限 执行结束 用时{}毫秒", e-s);
        return null;
    }

    @Override
    public void deleteEnterFiling(String id, String token) {
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        //下发权限到删除时间
        SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrk(id);
        insertCrkNew(dockCrk,true,true);

        SafetyEnterFilingHistoryRt old = enterFilingHistoryMapper.selectEnterFilingForHistory(id);
        enterFilingMapper.deleteEnterFilingPerAll(id);
        enterFilingMapper.deleteEnterFiling(id);
        enterApproveMapper.deleteEnterFilingApproveAll(id);

        // 同步历史表
        String workNo = TokenUtils.getWorkNo(token);
        String tokenUser = TokenUtils.getRealName(token);
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
        SafetyEnterFilingHistory enterFilingHistory = convertHistory(old, null, operations.get("20"), workNo, tokenUser);
        insertFilingHistory(enterFilingHistory, old.getPerList());
    }

    /**
     * 入厂备案历史数据转换
     * @param filing
     * @param id
     * @param fdOperation
     * @param userCode
     * @param userName
     * @return
     */
    private SafetyEnterFilingHistory convertHistory(SafetyEnterFilingHistoryRt filing, String id, String fdOperation, String userCode, String userName){
        if(id != null){
            filing = enterFilingHistoryMapper.selectEnterFilingForHistory(id);
        }
        SafetyEnterFilingHistory history = new SafetyEnterFilingHistory();
        BeanUtils.copyProperties(filing,history);

        history.setFdOperation(fdOperation);
        history.setFdOperationTime(new Date());
        history.setFdOperationUserCode(userCode);
        history.setFdOperationUserName(userName);
        history.setFdFilingBegin(DateUtils.parseNull(filing.getFdFilingBegin(), 1));
        history.setFdFilingEnd(DateUtils.parseNull(filing.getFdFilingEnd(), 1));
        history.setApproveFilingBegin(DateUtils.parseNull(filing.getApproveFilingBegin(), 1));
        history.setApproveFilingEnd(DateUtils.parseNull(filing.getApproveFilingEnd(), 1));
        history.setCreateTime(DateUtils.parseNull(filing.getCreateTime(), 2));
        history.setCreateUser(filing.getCreateUser());
        history.setFdGoodsUpdateTime(DateUtils.parseNull(filing.getGoodsUpdateTime(), 2));
        return history;
    }

    @Override
    public List<ErrorInfo> importEnterFiling(MultipartFile file, String token) {
         List<ErrorInfo> eList = new ArrayList<>();

        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();

        //根据token获取当前用户的对接人信息
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已过期，请重新登录！");
        }
        SafetyEnterAuthSh authSh = new SafetyEnterAuthSh();
        authSh.setFdEmpNo(workNo);
        SafetyEnterAuthRt authRt = safetyEnterAuthMapper.selectEmpInfoByEmpNo(authSh);
        if(authRt == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"登录人"+workNo+"不是接口人！");
        }
        Integer limitDay = authRt.getFdLimitDay();
        String powerFactoryCode = authRt.getFdFactoryCode();

        //获取工厂集合
        List<SafetyFieldManageDO> factoryList = safetyFieldManageService.getFactoryListNew();
        Map<String, SafetyFieldManageDO> factoryMap = factoryList.stream().collect(Collectors.toMap(SafetyFieldManageDO::getFieldCode, Function.identity()));
        //获取状态字典
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");

        List<SafetyEnterFiling> filingList = new ArrayList<>();
        List<SafetyEnterApprove> crkList = new ArrayList<>();
        List<SafetyEnterFilingPer> perList = new ArrayList<>();
        List<SafetyEnterProcess> processeList = new ArrayList<>();
        List<SafetyEnterFilingHistory> filingHistoryList = new ArrayList<>();

        Date date = new Date();
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            StringBuilder rowError = new StringBuilder();
            //序号,车牌号,司机姓名*,司机手机号*,司机身份证号*,随行人员姓名,随行人员手机号,随行人员身份证号,准入时间-始*(yyyy-MM-dd),准入时间-终*(yyyy-MM-dd),备案厂区*
            try {
                String carNo = ExcelUtils.getValue(row.getCell(1));  //车牌号
                String driverName = ExcelUtils.getValue(row.getCell(2)); //司机姓名
                String driverPhone = ExcelUtils.getValue(row.getCell(3)); //司机手机号
                String driverCardNo = ExcelUtils.getValue(row.getCell(4));  //司机身份证号
                String followName = ExcelUtils.getValue(row.getCell(5)); //随行人姓名
                String followPhone = ExcelUtils.getValue(row.getCell(6)); //随行人手机号
                String followCardNo = ExcelUtils.getValue(row.getCell(7)); //随行人身份证号
                String enterBegin = ExcelUtils.getValue(row.getCell(8)); //准入时间-始
                String enterEnd = ExcelUtils.getValue(row.getCell(9));  //准入时间-终
                String factoryCode = ExcelUtils.getValue(row.getCell(10));  //备案厂区

                if(StringUtils.isEmpty(driverName) || StringUtils.isEmpty(driverPhone)
                        || StringUtils.isEmpty(driverCardNo) || StringUtils.isEmpty(enterBegin)
                        || StringUtils.isEmpty(enterEnd) || StringUtils.isEmpty(factoryCode)){
                    rowError.append("必填项为空;");
                }
                SafetyEnterProtectRt carInfo = enterProtectMapper.selectEnterProtectByCarNo(carNo);
                if(carInfo == null){
                    rowError.append(carNo).append("”没有进行首次备案，请联系司机主动提交流程;");
                }
                Map<String,String> factoryInfos = this.getFactoryInfos(factoryCode,factoryMap);
                if(factoryInfos == null){
                    rowError.append("备案厂区“").append(factoryCode).append("”不存在;");
                }else {
                    String[] factoryArr = factoryInfos.get("factoryIds").split(",");
                    String[] codeArr = factoryInfos.get("factoryCodes").split(",");
                    StringBuilder errorCode = new StringBuilder();
                    for (int i = 0; i < factoryArr.length; i++) {
                        if (!powerFactoryCode.contains(factoryArr[i])) {
                            errorCode.append(codeArr[i]).append(";");
                        }
                    }
                    if (StringUtils.isNotBlank(errorCode.toString())) {
                        rowError.append("没有备案厂区“").append(errorCode.toString()).append("”备案权限;");
                    }
                }
                SafetyEnterRegisterRt driverRegister = enterRegisterMapper.selectEnterRegisterByPhone(driverPhone);
                if(driverRegister == null){
                    rowError.append("物流车司机“").append(driverName).append("-").append(driverPhone).append("”未登记;");
                }
                SafetyEnterRegisterRt followRegister = null;
                if(StringUtils.isNotBlank(followPhone)){
                    followRegister = enterRegisterMapper.selectEnterRegisterByPhone(followPhone);
                    if(followRegister == null){
                        followRegister = new SafetyEnterRegisterRt();
                        followRegister.setFdCardNo(followCardNo);
                        followRegister.setFdName(followName);
                        followRegister.setFdPhone(followPhone);
                    }
                }
                int enterDay = (int) (DateUtils.loseDayNew(enterBegin,enterEnd)+1);
                if(enterDay > limitDay){
                    rowError.append("备案时间范围不能超过").append(limitDay).append("天;");
                }
                //有异常信息时，不处理该行数据
                if(StringUtils.isNotBlank(rowError.toString())){
                    ErrorInfo errorInfo = new ErrorInfo();
                    errorInfo.setRowNum(rowNum);
                    errorInfo.setErrorInfo(rowError.toString());
                    eList.add(errorInfo);
                    continue;
                }

                String formInstanceId = UUID.randomUUID().toString().replace("-", "");
                String fdKey = UUID.randomUUID().toString().replace("-", "");
                Date enterBeginTime = DateUtils.parse(enterBegin+" 00:00:00",2);
                Date enterEndTime = DateUtils.parse(enterEnd+" 23:59:59",2);
                //物流车备案信息整理
                SafetyEnterFiling filing = new SafetyEnterFiling();
                filing.setFdCarNo(carInfo.getFdCarNo());
                filing.setFdStateCode("31");
                filing.setFdState(operations.get("31"));
                filing.setFdFilingTypeCode("10");
                filing.setFdFilingType("物流车辆");
                filing.setId(formInstanceId);
                filing.setFdFilingBegin(enterBeginTime);
                filing.setFdFilingEnd(enterEndTime);
                filing.setFdGoodsUpdateTime(enterBeginTime);
                filing.setFdDockNo(authRt.getFdEmpNo());
                filing.setFdDockName(authRt.getFdEmpName());
                filing.setFdFilingFactory(factoryInfos.get("factoryNames"));
                filing.setFdFilingFactoryCode(factoryInfos.get("factoryIds"));
                filing.setFdDockNo(authRt.getFdEmpNo());
                filing.setFdDockName(authRt.getFdEmpName());
                filing.setFdDockDepartment(authRt.getFdEmpDepartment());
                filing.setFdDockPhone(authRt.getFdPhone());
                filing.setCreateTime(date);
                filing.setCreateUser(authRt.getFdEmpName());
                filing.setUpdateTime(date);
                filing.setUpdateUser(authRt.getFdEmpName());
                filing.setFdKey(fdKey);

                filingList.add(filing);

                //物流车备案历史记录
                SafetyEnterFilingHistory filingHistory = new SafetyEnterFilingHistory();
                BeanUtils.copyProperties(file,filingHistory);
                filingHistory.setFdFilingId(filing.getId());
                filingHistoryList.add(filingHistory);

                //物流车接口人信息
                SafetyEnterApprove crk = new SafetyEnterApprove();
                crk.setFdFilingId(formInstanceId);
                crk.setFdFilingBegin(enterBeginTime);
                crk.setFdFilingEnd(enterEndTime);
                crk.setFdFilingFactory(factoryInfos.get("factoryNames"));
                crk.setFdFilingFactoryCode(factoryInfos.get("factoryIds"));
                crk.setApproveCode(authRt.getFdEmpNo());
                crk.setApproveName(authRt.getFdEmpName());
                crk.setApproveDepart(authRt.getFdEmpDepartment());
                crkList.add(crk);

                //物流车司机整理
                SafetyEnterFilingPer filingPer = new SafetyEnterFilingPer();
                filingPer.setFdPerCard(driverRegister.getFdCardNo());
                filingPer.setFdPerImg(driverRegister.getFdImg());
                filingPer.setFdPerName(driverRegister.getFdName());
                filingPer.setFdPerPhone(driverRegister.getFdPhone());
                filingPer.setFdPerType("物流司机");
                filingPer.setFdPerTypeCode("10");
                filingPer.setFilingId(formInstanceId);
                perList.add(filingPer);

                if(followRegister != null){
                    //物流车随行人员整理
                    SafetyEnterFilingPer followPer = new SafetyEnterFilingPer();
                    followPer.setFdPerCard(followRegister.getFdCardNo());
                    followPer.setFdPerImg(followRegister.getFdImg());
                    followPer.setFdPerName(followRegister.getFdName());
                    followPer.setFdPerPhone(followRegister.getFdPhone());
                    followPer.setFdPerType("随行人员");
                    followPer.setFdPerTypeCode("20");
                    followPer.setFilingId(formInstanceId);
                    perList.add(followPer);
                }

                SafetyEnterProcess enterProcess = new SafetyEnterProcess();
                // 供应商名称+司机姓名+车牌号+送货/取货车辆入厂申请
                enterProcess.setFlowId("");
                // 添加审批记录
                String subject = (StringUtils.isBlank(filing.getFdSupplierName()) ? "" : filing.getFdSupplierName()) +
                        driverName +
                        driverCardNo +
                        filing.getFdEnterType() +
                        "入厂申请";
                enterProcess.setTitle(subject);
                enterProcess.setCreator(workNo);
                enterProcess.setCreateTime(date);
                enterProcess.setStatus(filing.getFdStateCode());
                enterProcess.setModelId("enter");
                enterProcess.setTemplateFormId("safety_enter_filing");
                enterProcess.setFormInstanceId(formInstanceId);
                processeList.add(enterProcess);

                //发送微信消息
                String position = enterFilingMapper.selectFilingFactoryByCode(factoryCode);
                String warn = "您的物流车入厂申请已通过";

                pushWxMesService.pushWxMes(carNo,driverPhone,warn);

            }catch (Exception e){
                LogUtils.error("导入数据异常rowNum={},e={}",rowNum, e);
                rowError.append("第").append(rowNum).append("行导入异常，导入终止！").append(e.getMessage());
                //有异常信息时，不处理该行数据
                if(StringUtils.isNotBlank(rowError.toString())){
                    ErrorInfo errorInfo = new ErrorInfo();
                    errorInfo.setRowNum(rowNum);
                    errorInfo.setErrorInfo(rowError.toString());
                    eList.add(errorInfo);
                }
            }
        }
        if(CollectionUtils.isEmpty(filingList)){
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setRowNum(0);
            errorInfo.setErrorInfo("未找到有效数据！");
            eList.add(errorInfo);
        }
        if(!CollectionUtils.isEmpty(eList)){
            return eList;
        }
        try {
            enterFilingMapper.insertEnterFilingBatch(filingList);
            enterFilingMapper.insertEnterFilingPerBatch(perList);
            enterProcessMapper.insertEnterProcessBatch(processeList);
            enterApproveMapper.insertEnterFilingApproveBatch(crkList);
        }catch (Exception e){
            LogUtils.error("导入数据异常,e={}", e);
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setRowNum(0);
            errorInfo.setErrorInfo("导入异常，导入终止！"+e.getMessage());
            eList.add(errorInfo);
            return eList;
        }

        //保存备案历史记录以及下发权限
        Map<String,List<SafetyEnterFilingPer>> perMap = perList.stream().collect(Collectors.groupingBy(SafetyEnterFilingPer::getFilingId));
        Map<String,List<SafetyEnterFilingHistory>> hisMap = filingHistoryList.stream().collect(Collectors.groupingBy(SafetyEnterFilingHistory::getFdFilingId));
        String idStr = filingHistoryList.stream().map(SafetyEnterFilingHistory::getFdFilingId).collect(Collectors.joining(","));
        List<SafetyEnterDockCrk> dockCrks = incomingMapper.selectSafetyEnterDockCrkList(idStr);
        if(CollectionUtils.isEmpty(dockCrks)){
            return eList;
        }
        for(SafetyEnterDockCrk crk:dockCrks){
            String filingId = crk.getId();
            List<SafetyEnterFilingPer> curPerList = perMap.get(filingId);
            SafetyEnterFilingHistory his = hisMap.get(filingId).get(0);
            insertFilingHistory(his,curPerList);
            //下发权限
            if(crk != null){
                insertCrkNew(crk, true, false);
            }
        }

        try{
            incomingHandler.syncSap(dockCrks);
        }catch (Exception e){
            LogUtils.error("下发SAP异常",e);
        }
        return eList;
    }

    /**
     * 根据工厂编码获取，工厂ID拼接串和工厂名称拼接串
     * @param factoryCode 工厂编码
     * @param factoryMap 工厂集合
     * @return 返回值
     */
    private Map<String,String> getFactoryInfos(String factoryCode,Map<String, SafetyFieldManageDO> factoryMap){
        Map<String,String> retFactoryMap = new HashMap<>();
        String[] codeArray = factoryCode.split(",");
        StringBuilder idStr = new StringBuilder();
        StringBuilder codeStr = new StringBuilder();
        StringBuilder nameStr = new StringBuilder();
        for(String code:codeArray){
            if(StringUtils.isNotBlank(code)){
                SafetyFieldManageDO fieldManageDO = factoryMap.get(code);
                if(ObjectUtils.isEmpty(fieldManageDO)){
                    continue;
                }
                if(!"".equals(idStr.toString())){
                    idStr.append(",");
                    nameStr.append(",");
                    codeStr.append(",");
                }
                idStr.append(fieldManageDO.getId());
                nameStr.append(fieldManageDO.getFieldName());
                codeStr.append(fieldManageDO.getFieldCode());
            }
        }
        if(StringUtils.isEmpty(idStr.toString())){
            return  null;
        }
        retFactoryMap.put("factoryIds",idStr.toString());
        retFactoryMap.put("factoryCodes",codeStr.toString());
        retFactoryMap.put("factoryNames",nameStr.toString());
        return retFactoryMap;
    }

    /**
     * 根据工厂编码获取，工厂ID拼接串和工厂名称拼接串
     * @param factoryIds 工厂编码
     * @param factoryMap 工厂集合
     * @return 返回值
     */
    private Map<String,String> getFactoryInfosById(String factoryIds,Map<Integer, SafetyFieldManageDO> factoryMap){
        Map<String,String> retFactoryMap = new HashMap<>();
        String[] codeArray = factoryIds.split(",");
        StringBuilder idStr = new StringBuilder();
        StringBuilder codeStr = new StringBuilder();
        StringBuilder nameStr = new StringBuilder();
        for(String curId:codeArray){
            if(StringUtils.isNotBlank(curId)){
                SafetyFieldManageDO fieldManageDO = factoryMap.get(Integer.parseInt(curId));
                if(ObjectUtils.isEmpty(fieldManageDO)){
                    continue;
                }
                if(!"".equals(idStr.toString())){
                    idStr.append(",");
                    nameStr.append(",");
                    codeStr.append(",");
                }
                idStr.append(fieldManageDO.getId());
                nameStr.append(fieldManageDO.getFieldName());
                codeStr.append(fieldManageDO.getFieldCode());
            }
        }
        if(StringUtils.isEmpty(idStr.toString())){
            return  null;
        }
        retFactoryMap.put("factoryIds",idStr.toString());
        retFactoryMap.put("factoryCodes",codeStr.toString());
        retFactoryMap.put("factoryNames",nameStr.toString());
        return retFactoryMap;
    }

    /**
     * 导出
     * @param enterFilingSh 检索条件
     * @param token 当前用户token
     * @return 返回值
     */
    @Override
    public byte[] exportEnterFiling(SafetyEnterFilingSh enterFilingSh, String token) {
        byte[] content = null;
        //定义HSSFWorkbook对象
        XSSFWorkbook wb = null;
        //定义输出流
        ByteArrayOutputStream os = null;
        InputStream fis = null;
        try {
            //查询要导出的数据
            String isAdmin = checkAdmin(token);
            if(!"T".equals(isAdmin)){
                if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                    isAdmin = "T";
                }
            }
            enterFilingSh.setIsAdmin(isAdmin);
            enterFilingSh.setExport("Y");
            List<SafetyEnterFilingRt> list = enterFilingMapper.selectEnterFilingList(enterFilingSh);
            //获取模板
            ClassPathResource classPathResource = new ClassPathResource("excel/enterFilingExport.xlsx");
            fis = classPathResource.getInputStream();
            wb = new XSSFWorkbook(fis);
            //写入Excel数据
            XSSFSheet sheet = wb.getSheetAt(0);
            //创建数据对象，用来保存list数据
            Object[][] values = new Object[list.size()][sheet.getRow(0).getPhysicalNumberOfCells()];
            for(int i=0; i<list.size(); i++) {
                SafetyEnterFilingRt d = list.get(i);
                values[i][0] = d.getFdState();
                values[i][1] = d.getFdFilingType();
                values[i][2] = d.getFdEnterType();
                values[i][3] = d.getFdCarNo();
                values[i][4] = d.getFdFilingFactory();
                values[i][5] = d.getFdFilingBegin();
                values[i][6] = d.getFdFilingEnd();
                values[i][7] = d.getApproveFilingFactory();
                values[i][8] = d.getApproveFilingEnd();
                StringBuilder sbf = new StringBuilder();
                List<SafetyEnterFilingPer> perList = d.getPerList();
                for (SafetyEnterFilingPer per : perList) {
                    sbf.append(per.getFdPerType()).append(" ");
                    sbf.append(per.getFdPerName()).append(" ");
                    sbf.append(per.getFdPerCard()).append(" ");
                    sbf.append(per.getFdPerPhone()).append("\r\n");
                }
                values[i][9] = sbf.toString();
                values[i][10] = d.getFdProtectGrade();
                values[i][11] = d.getFdDetailedImg();
                values[i][12] = d.getFdSupplierName();
                values[i][13] = d.getFdGoodsName();
                values[i][14] = d.getFdGoodsWeight();
                values[i][15] = d.getFdCarImg();
                values[i][16] = d.getCreateTime();
                values[i][17] = d.getFdDockName();
                values[i][18] = d.getFdDockNo();
                values[i][19] = d.getFdDockDepartment();
                values[i][20] = d.getFdRemark();
            }
            //写入数据
            OutXSSFUtil.getXSSFWorkbook(values, wb, sheet, 1, 11);
            os = new ByteArrayOutputStream();
            wb.write(os);
            content = os.toByteArray();
        }catch (Exception e){
            LogUtils.error("数据导出异常e={}", e);
        }finally {
            StreamUtil.close(fis);
            StreamUtil.close(os);
            PubExcelUtil.close(wb);
        }
        return content;
    }

    /**
     * 更新物流车货物信息
     * @param enterFiling 车辆备案信息
     */
    public void updateFilingGoods(SafetyEnterFiling enterFiling){
        long timeStamp = System.currentTimeMillis();
        SafetyEnterFilingInfoRt filingRt = enterFilingMapper.selectEnterFilingById(enterFiling.getId());
        if(StringUtils.isBlank(enterFiling.getId()) ||ObjectUtils.isEmpty(filingRt)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"请传入有效的参数信息!");
        }
        List<SafetyEnterFilingPer> filingPers = filingRt.getPerList();
        enterFiling.setFdGoodsUpdateTime(new Date());
        enterFiling.setUpdateTime(new Date());
        enterFilingMapper.updateEnterFiling(enterFiling);
        LogUtils.info(timeStamp+"-updateFilingGoods-01:"+DateUtils.format(new Date(),2));

        SafetyEnterFiling newEnterFiling = enterFilingMapper.selectByPrimary(enterFiling.getId());
        //更新历史
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_history_operation");
        SafetyEnterFilingHistory enterFilingHistory = convertHistory(null, enterFiling.getId(), operations.get("10"), newEnterFiling.getFdDockPhone(),enterFiling.getUpdateUser());
        insertFilingHistory(enterFilingHistory, filingPers);

        //发送宇信通消息通知
        SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrk(enterFiling.getId());
        LogUtils.info(timeStamp+"-updateFilingGoods-02:"+DateUtils.format(new Date(),2));
        if(!ObjectUtils.isEmpty(dockCrk)){
            String msg = Joiner.on("").join("【物流车入厂备案】",
                    newEnterFiling.getFdCarNo(),
                    "司机",
                    dockCrk.getEnterDrvierName(),
                    dockCrk.getEnterDrvierPhone(),
                    "修改了货物信息为：“,",
                    enterFiling.getFdGoodsName(),
                    enterFiling.getFdGoodsWeight(),
                    "“");
            if(StringUtils.isNotEmpty(dockCrk.getFlowId())){
                msg = Joiner.on("").join(msg,"备案详情：");
            }
            sendYxtMsg(dockCrk,msg,dockCrk.getFdDockNo());
            LogUtils.info(timeStamp+"-updateFilingGoods-03:"+DateUtils.format(new Date(),2));
        }
        LogUtils.info(timeStamp+"-updateFilingGoods-时长:"+(System.currentTimeMillis()-timeStamp));
    }

    @Override
    public Map drivingLicense(String url) {
        if(StringUtils.isBlank(url)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String suffix = url.substring(url.lastIndexOf(".")+1);
        String base64 = pubFileHandler.imgToBase64(url);
        Map headParam = new HashMap();
        headParam.put("Content-Type", "application/json");
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("appId", drivingId);
        bodyParam.put("imageType", suffix);
        bodyParam.put("imageBase64", base64);
        String result = webserviceUtil.sendHttp(drivingLicenseUrl, "2", bodyParam.toString(), headParam, "POST");
        if(StringUtils.isBlank(result)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"驾驶证数据解析失异常");
        }
        JSONObject obj = JSON.parseObject(result);
        if(obj.getInteger("code") != 200){
            throw new BizException(ResponseEnum.FAIL.getCode(),"驾驶证数据解析失异常");
        }
        JSONObject ret = obj.getJSONObject("result");
        if(!"00".equals(ret.getString("code"))){
            throw new BizException(ResponseEnum.FAIL.getCode(),"驾驶证数据解析失异常");
        }
        JSONObject cardInfo = ret.getJSONObject("cardInfo");
        Map map = new HashMap();
        map.put("fdVin", cardInfo.getString("vin"));
        map.put("fdEngine", cardInfo.getString("engineNo"));
        map.put("fdCarDate", cardInfo.getString("registerDate"));
        return map;
    }

    @Override
    public String checkAdmin(String token){
        String userCode = TokenUtils.getWorkNo(token);
        String userName = TokenUtils.getUserName(token);
        List<String> list = enterFilingMapper.selectAdministrator(userName);
        if (CollectionUtils.isNotEmpty(list)){
            return "T";
        }
        return userCode;
    }

    public void sendYxtMsg(SafetyEnterDockCrk dockCrk,String msg,String toEmpNos){
        String flowUrl = "";
        if(!StringUtils.isEmpty(dockCrk.getFlowId())){
            Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(ENTER_BPM_FORM_ID);
            if(bpmModel == null){
                LogUtils.info("机器人消息推送终止-未找到{}对应的流程模板配置",ENTER_BPM_FORM_ID);
            }
            String modelId = bpmModel.get("model_id");
            String todoPre = bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+modelId+"&templateFormId="+ENTER_BPM_FORM_ID;

            flowUrl = Joiner.on("").join(
                    todoPre,
                    "&formInstanceId=",
                    dockCrk.getId(),
                    "&processId=",
                    dockCrk.getFlowId()
            );
        }
        String sendmsg = Joiner.on("").join(msg,
                flowUrl
        );
        RestTemplate restTemplate = new RestTemplate();
        yxtRebotUtils.postForEntity(null,incomintRebotId,toEmpNos,sendmsg,restTemplate);
    }

    /**
     * 校验是否查询管理员权限
     * @param token 当前token
     * @param roleName 查询管理员角色名称
     * @return 是否
     */
    private boolean checkSearchAdmin(String token,String roleName){
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName(roleName);
        param.setUserName(userName);
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if(CollectionUtils.isEmpty(list)){
            return false;
        }
        return true;
    }

    @Override
    public boolean validTelNumberByIdCard(String phone, String idCard) {
        boolean flag = true;
        SafetyEnterRegisterRt rt = enterRegisterMapper.selectEnterRegisterByCard(idCard);
        if (!ObjectUtils.isEmpty(rt) && StringUtils.isNotBlank(rt.getFdPhone()) && (!phone.equals(rt.getFdPhone()))) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean verifyBeforeInsert(SafetyEnterFilingBody enterFiling) {
        boolean flag = true;
        //基础信息校验
        verifyParam(enterFiling);
        Map<String, Object> paramMap = new HashMap<>();

        //流程发起前校验  详情参考需求：21518
        //1、首先判断该车的司机变化与否；变化，直接通过；否，进行下一步。
        String fdCarNo = enterFiling.getFiling().getFdCarNo();//車牌
        List<SafetyEnterFilingPer> perList = enterFiling.getPer();//备案人员信息

        String fdPerCard = "";//身份证号
        for (SafetyEnterFilingPer per : perList) {
            if ("10".equals(per.getFdPerTypeCode())) {
                fdPerCard = per.getFdPerCard();
            }
        }

        //申请开始时间
        Date fdFilingBegin = enterFiling.getFiling().getFdFilingBegin();
        //申请结束时间
        Date fdFilingEnd = enterFiling.getFiling().getFdFilingEnd();

        paramMap.put("fdCarNo", fdCarNo);
        paramMap.put("fdPerCard", fdPerCard);
        paramMap.put("fdFilingBegin", DateUtils.format(fdFilingBegin,1));
        paramMap.put("fdFilingEnd", DateUtils.format(fdFilingEnd,1));

        //根据当前人和当前车牌号去查询包含申请入场开始时间的数据
        List<Map<String, Object>> maps = enterFilingMapper.verifyBeforeInsert(paramMap);


        if (CollectionUtils.isEmpty(maps)) {
            return true;
        }

        //数据不为空，就判断申请厂区是否有重叠
        String fdFilingFactoryCode = enterFiling.getFiling().getFdFilingFactoryCode();
        StringBuilder factoryCodeOld = new StringBuilder();
        maps.stream().forEach(m -> {
            factoryCodeOld.append(m.get("factoryCode"));
        });

        if (StringUtils.isNotBlank(factoryCodeOld)) {
            String factoryOld = factoryCodeOld.toString();
            String[] split = fdFilingFactoryCode.split(",");

            for (String s : split) {
                if (factoryOld.contains(s)) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    }

    @Override
    public Map<String, Object> verifyBeforeApprove(SafetyEnterApprove approve) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean flag = true;
        LogUtils.info("verifyBeforeApprove:"+approve);
        //根据流程号查询车辆信息
        String fdFilingId = approve.getFdFilingId();
        LogUtils.info("verifyBeforeApprove:"+fdFilingId);


        SafetyEnterFiling safetyEnterFiling = enterFilingMapper.selectByPrimary(fdFilingId);
        LogUtils.info("verifyBeforeApprove:"+safetyEnterFiling);

        if (ObjectUtils.isEmpty(safetyEnterFiling)) {
            resultMap.put("flag", true);
            resultMap.put("mes", "审批通过");
            return resultMap;
        }

        String fdCarNo = safetyEnterFiling.getFdCarNo();

        //查询身份证号
        //Map<String, Object> personByProcessId = enterFilingMapper.getPersonByProcessId(fdFilingId);
        //String perCard = personByProcessId.get("perCard").toString();

        //申请入场开始时间
        Date fdFilingBegin = approve.getFdFilingBegin();
        //申请结束时间
        Date fdFilingEnd = approve.getFdFilingEnd();

        //封装参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fdCarNo", fdCarNo);
        //paramMap.put("fdPerCard", perCard);
        paramMap.put("fdFilingBegin", DateUtils.format(fdFilingBegin,1));
        paramMap.put("fdFilingEnd", DateUtils.format(fdFilingEnd,1));

        //根据当前人和当前车牌号去查询包含申请入场开始时间的数据
        List<Map<String, Object>> maps = enterFilingMapper.verifyBeforeApprove(paramMap);

        if (CollectionUtils.isEmpty(maps)) {
            resultMap.put("flag", true);
            resultMap.put("mes", "审批通过");
            return resultMap;
        }

        //不为空，判断厂区是否重叠
        //审批厂区
        String approveFactory = approve.getFdFilingFactoryCode();

        //只取最新的一条数据
        Map<String, Object> map = maps.get(0);
        String factoryCode = map.get("factoryCode").toString();

        if (StringUtils.isNotBlank(factoryCode)) {
            String[] split = approveFactory.split(",");

            for (String s : split) {
                if (factoryCode.contains(s)) {
                    flag = false;
                    break;
                }
            }
        }

        if(flag){
            resultMap.put("flag", true);
            resultMap.put("mes", "审批通过");
            return resultMap;
        }

        //厂区重复，判断当前车辆是否在厂
        //获取现有已审批的厂区信息
        String factory = map.get("factory").toString();
        String beginTime = map.get("beginTime").toString();
        String endTime = map.get("endTime").toString();

        //调用远程接口，查看车辆是否在厂
        String bodyParams = "\"tokenNo='"+fdCarNo+"'\"";
        Map headParams = new HashMap(4);
        headParams.put("Content-Type","application/json");
        String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);

        String mes = "该车辆当前已有入厂门禁权限（" + factory + "）;日期：" + beginTime + " - " + endTime + "），审批通过将覆盖原有权限，以本次备案为准，是否确认审批通过？";
        if (StringUtils.isBlank(returnJson)) {
            LogUtils.info("出入口接口未找到{}的出入场数据",fdCarNo);
            //查询不到统一按照不在厂处理
        }else{
            JSONObject retObject = JSONObject.parseObject(returnJson);
            if (retObject == null || !retObject.containsKey("Models")) {
                resultMap.put("flag", false);
                resultMap.put("mes", mes);
                return resultMap;
            }
            JSONArray retMap = retObject.getJSONArray("Models");
            if(retMap != null && retMap.size() > 0){
                mes = "该车辆当前正在厂区内（" + factory + "）;日期：" + beginTime + " - " + endTime + "），审批通过将覆盖原有权限，以本次备案为准，是否确认审批通过？";
            }
        }

        resultMap.put("flag", false);
        resultMap.put("mes", mes);
        return resultMap;
    }

    public void verifyParam(SafetyEnterFilingBody enterFiling){
        String userCode = enterFiling.getUserCode();
        if(StringUtils.isBlank(userCode)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "提交人信息不能为空");
        }
        if(CollectionUtils.isEmpty(enterFiling.getDock())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "接口人信息不能为空");
        }
        if(enterFiling.getDock().size() == 1
                && StringUtils.isBlank(enterFiling.getFiling().getFdFilingFactoryCode())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案厂区不能为空");
        }
        if (CollectionUtils.isEmpty(enterFiling.getPer())) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "备案人员不能为空");
        }
        // 黑名单校验
        blackMessageValidation(enterFiling);

        Date enterBegin = enterFiling.getFiling().getFdFilingBegin();
        if(enterBegin != null){
            enterFiling.getFiling().setFdFilingBegin(DateUtils.parse(DateUtils.format(enterBegin,1)+" 00:00:00",2));
        }
        Date enterEnd = enterFiling.getFiling().getFdFilingEnd();
        if(enterEnd != null){
            enterFiling.getFiling().setFdFilingEnd(DateUtils.parse(DateUtils.format(enterEnd,1)+" 23:59:59",2));
        }
        Map<String, String> operations = dictItemService.selectDictItemMapBySupperCode("enter_filing_state");
        enterFiling.getFiling().setFdStateCode("20");
        enterFiling.getFiling().setFdState(operations.get("20"));
        enterFiling.getFiling().setFdFilingTypeCode("10");
        enterFiling.getFiling().setFdFilingType("物流车辆");
        BpmModelRt bpmModelRt = bpmServiceHandler.getModelInfo();
        if(bpmModelRt == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"物流车入厂流程配置获取异常，请进行查看");
        }
    }

    /**
     * 黑名单校验
     */
    private void blackMessageValidation(SafetyEnterFilingBody enterFiling) {

        //入厂时间段
        //判断开始时间是否是今天，是的话，以当前时间为准
        Date date1 = enterFiling.getFiling().getFdFilingBegin();
        Date date2 = new Date();
        Date beginTime = date1.getTime() < date2.getTime() ? date2 : date1;
        String startTime = DateUtils.format(beginTime, 2);
        String endTime = DateUtils.format(enterFiling.getFiling().getFdFilingEnd(), 1) + " 23:59:59";

        String dockCode = enterFiling.getDock().stream().map(SafetyEnterFilingDock::getFdDockPhone).collect(Collectors.joining());
        List<String> cardList = enterFiling.getPer().stream().map(SafetyEnterFilingPer::getFdPerCard).distinct().collect(Collectors.toList());
        Map<String, SafetyEnterFilingPer> dataMap = enterFiling.getPer().stream().filter(li -> StringUtils.isNotBlank(li.getFdPerCard())).collect(Collectors.toMap(SafetyEnterFilingPer::getFdPerCard, v -> v, (t1, t2) -> t1));

        // 安保黑名单校验
        Map<String, Boolean> stringBooleanMap = subCheckBlankList(cardList, dockCode, startTime, endTime, dataMap, enterFiling);

        // 友商黑名单
        Map<String, Boolean> dataMp = subCheckCompetitor(stringBooleanMap, dockCode, dataMap, enterFiling);

        // 黑名单校验
        dataMp.keySet().forEach(key -> checkIsBlank(key, dockCode, dataMp.get(key), dataMap));
    }


    /**
     * 黑名单日志数据整理
     */
    private void disBlankProcessLog(SafetyEnterFilingBody enterFiling, String idCardNo, Map<String, SafetyEnterFilingPer> dataMap, String remark) {
        try {
            List<SafetyEnterFilingDock> dock = enterFiling.getDock();
            List<String> empNos = new ArrayList<>();
            List<String> empNames = new ArrayList<>();
            List<String> phones = new ArrayList<>();
            dock.forEach(li -> {
                empNos.add(li.getFdDockNo());
                empNames.add(li.getFdDockName());
                phones.add(li.getFdDockPhone());
            });
            SafetyEnterFilingPer per = dataMap.getOrDefault(idCardNo, new SafetyEnterFilingPer());
            SafetyEntryBlankProcessLogDO log = new SafetyEntryBlankProcessLogDO();
            log.setSubmitOpenId(enterFiling.getOpenId());
            log.setSubmitType("物流车");
            log.setSubmitName(enterFiling.getUserName());
            log.setSubmitPhone(enterFiling.getUserCode());
            log.setButtJointCode(String.join(",", empNos));
            log.setButtJointName(String.join(",", empNames));
            log.setButtJointPhone(String.join(",", phones));
            log.setBlankCard(idCardNo);
            log.setBlankName(per.getFdPerName());
            log.setBlankPhone(per.getFdPerPhone());
            log.setRemark(remark);
            log.setCreates(enterFiling.getUserCode(), enterFiling.getUserName());
            safetyEntryBlankProcessLogService.save(log);
        } catch (Exception e) {
            LogUtils.error("黑名单日志数据入库出错了" + e.getMessage());
        }
    }

    //删除同一司机同一车在同一时间内厂区重叠的并且已经审批通过的数据
    public String deleteApprovedData(String flowId){
        //查询数据
        SafetyEnterFiling safetyEnterFiling = enterFilingMapper.selectByPrimary(flowId);
        LogUtils.info("deleteApprovedData[safetyEnterFiling]："+safetyEnterFiling);
        safetyEnterFiling.setFdFilingEnd(DateUtils.parse(DateUtils.format(safetyEnterFiling.getFdFilingEnd(),1),1));

        //查询同一司机同一车在相同时间内已经审批完成的数据
        List<Map<String, Object>> overlapDataList = enterFilingMapper.getOverlapDataList(safetyEnterFiling);
        LogUtils.info("deleteApprovedData[overlapDataList]："+overlapDataList);

        //无重叠
        if(CollectionUtils.isEmpty(overlapDataList)){return "";}

        //当前审批的厂区
        String fdFilingFactoryCode = safetyEnterFiling.getFdFilingFactoryCode();
        if(StringUtils.isBlank(fdFilingFactoryCode)){return "";}
        String[] split = fdFilingFactoryCode.split(",");
        List<String> processIdList = new ArrayList<>();

        //遍历重叠数据，看厂区是否重叠
        overlapDataList.stream().forEach(e -> {
            if(ObjectUtils.isEmpty(e.get("factoryCode"))){return;}
            String factoryCode = e.get("factoryCode").toString();

            //厂区有一个重叠，就记录流程id
            for (String s : split){
                if(factoryCode.contains(s)){
                    String processId = e.get("id").toString();
                    processIdList.add(processId);
                    break;
                }
            }
        });

        //删除重叠的数据
        if(CollectionUtils.isNotEmpty(processIdList)){
            enterFilingMapper.deleteByProcessIdList(processIdList);
            LogUtils.info("deleteApprovedData[processIdList]："+processIdList);
        }
        return "";
    }

    @Override
    public Map<String, Object> getAlertMes(String phoneNo) {
        return enterFilingMapper.getAlertMes(phoneNo);
    }

    @Override
    public void setAlertMesHidden(String phoneNo, String mesId) {
        enterFilingMapper.deleteByPhoneNo(phoneNo);
        enterFilingMapper.setAlertMesHidden(phoneNo, mesId);
    }

    /**
     * BPM流程超时废弃流程回调
     * @param processId
     */
    public void bpmCallBackEnterFiling(String processId){
        //根据流程id查询流程信息
        List<SafetyEnterFilingRt> list = new ArrayList<>();
        List<SafetyEnterFilingRt> safetyEnterFilingRts = enterFilingMapper.selectEnterList(processId);

        if(!CollectionUtils.isEmpty(safetyEnterFilingRts)){
            safetyEnterFilingRts.forEach(en -> {
                if(!"0".equals(en.getTimeOutDeal())){
                    en.setFdState("超时结束");
                    en.setFdStateCode("60");
                    list.add(en);
                }
            });

            LogUtils.info("超时回调更新的数据有{}",list);

            if(!CollectionUtils.isEmpty(list)){
                enterFilingMapper.updateStatusByList(list);
                enterFilingMapper.updateStatusHistoryByList(list);
            }
        }
    }

    /**
     * 调用第三方接口
     * @param processId
     */
    public void callThirdPartyInterface(String processId) {
        LogUtils.info("物流车审批后，调用第三方接口，主流程数据id：{}", processId);
        //获取数据
        List<SafetyEnterFilingRt> list = enterFilingMapper.selectEnterThirdList(processId);
        if (CollectionUtils.isEmpty(list)) {
            LogUtils.info("物流车审批通过后，未能获取到物流信息 formId={}", processId);
            return;
        }
        //调用第三方接口数据
        SafetyEnterFilingRt safetyEnterFilingRt = list.get(0);
        if (Objects.isNull(safetyEnterFilingRt.getFdDeliveryType()) || safetyEnterFilingRt.getFdDeliveryType().equals(Constants.STATE_TRUE)) {
            LogUtils.info("物流车申请，非物料送货，不调用TMS接口,{}", safetyEnterFilingRt.getFdDeliveryType());
            return;
        }
        //开始调用第三方接口
        //获取三个月的数据是否有
        Integer total = enterFilingMapper.selectCountByCarNo(safetyEnterFilingRt.getId(), safetyEnterFilingRt.getFdCarNo());
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject sonObject = new JSONObject();
        sonObject.put("factoryCode", safetyEnterFilingRt.getFdFilingFactoryCode());
        sonObject.put("carNo", safetyEnterFilingRt.getFdCarNo());
        sonObject.put("deliveryType", "Y");
        sonObject.put("effectiveTimeFrom", safetyEnterFilingRt.getFdFilingBegin());
        sonObject.put("effectiveTimeTo", safetyEnterFilingRt.getFdFilingEnd());
        sonObject.put("supplier", safetyEnterFilingRt.getFdSupplierName());
        sonObject.put("driver", safetyEnterFilingRt.getFdDriverName());
        sonObject.put("driverPhone", safetyEnterFilingRt.getCreateUser());
        sonObject.put("driverFlag", total > 0 ? "N" : "Y");
        jsonArray.add(sonObject);
        jsonObject.put("dataArr", jsonArray);
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type", "application/json");
        LogUtils.info("调用TMS接口传参：{}", jsonObject.toJSONString());
        String resultStr = webserviceUtil.postJsonStr(saveEntryApplyUrl, jsonObject.toJSONString(), headerMap);
        LogUtils.info("调用TMS接口数据结果：{}", resultStr);
    }

    @Override
    public void sendYXTMes(Map<String, String> paramMap) {
        List<SafetyEnterFilingRt> dataList = enterFilingMapper.getMesByKafka(paramMap);

        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        dataList.forEach(s -> {
            StringBuilder perStr = new StringBuilder();
            StringBuilder phoneStr = new StringBuilder();
            if (!CollectionUtils.isEmpty(s.getPerList())) {
                s.getPerList().forEach(en -> {
                    perStr.append(en.getFdPerName()).append("/");
                    phoneStr.append(en.getFdPerPhone()).append("/");
                });
            }

            String msg = Joiner.on("")
                    .join(s.getFdDriverName(),
                            "的",
                            s.getFdCarNo(),
                            "已于",
                            paramMap.get("carTime"),
                            "从",
                            paramMap.get("doorName"),
                            paramMap.get("type"),
                            "，送货人姓名：",
                            StringUtils.isBlank(perStr) ? "空" : perStr.substring(0, perStr.toString().length() - 1),
                            "，送货人电话：",
                            StringUtils.isBlank(phoneStr) ? "空" : phoneStr.substring(0, phoneStr.toString().length() - 1)
                    );
            SafetyEnterDockCrk dockCrk = new SafetyEnterDockCrk();
            BeanUtils.copyProperties(s, dockCrk);
            sendYxtMsg(dockCrk, msg, dockCrk.getFdDockNo());
        });
    }

    @Override
    public VO getRegisterInfo(String phone) {
        SafetyEnterRegisterRt info = safetyEnterRegisterService.selectInfo(phone);
        if (info == null) {
            return VoUtil.fail("4", "未登记，请本人先在入场备案登记信息");
        }
        // 登记校验
        Boolean aBoolean = safetyArtemisHandler.checkOneToMany(info.getFdImg(), info.getFdName(), info.getFdCardNo(), SafetyHikThresholdEnum.VEHICLE);
        if (aBoolean) {
            SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_FILING_TODO");
            if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                return VoUtil.fail(info.getFdName() + "人脸校验失败，流程不可提交", info);
            }
        }
        return VoUtil.successData(info);
    }
}
