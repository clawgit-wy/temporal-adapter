package com.yutong.safety.core.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.UpdatePhotoEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEmpCardVO;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterApproveMapper;
import com.yutong.safety.core.dto.*;
import com.yutong.safety.core.dto.device.SafetyRegionDeviceInfoDTO;
import com.yutong.safety.core.dto.outsider.OutsiderFacoryChangeDTO;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.device.SafetyRegionDeviceInfoService;
import com.yutong.safety.core.service.employeeprivatecar.SafetyEmployeePrivateCarService;
import com.yutong.safety.core.service.enter.IncomingService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lukunpeng
 * @time 2024-6-14
 */
@Component
public class SafetyOutsiderPromiseChangeHandler {

    //失败
    public static final int FAIL_CODE = 1;
    private static final String targetId="ABYJS";
    @Value("${ehr.promise}")
    String promiseUrl;
    @Value("${iam.getPhotoUrl.url}")
    String getPhotoUrl;
    @Value("${iam.FaceModify.url}")
    String faceModifyUrl;
    @Value("${iam.FaceIssue.url}")
    String faceIssueUrl;
    @Value("${iam.CardModify.url}")
    String cardModifyUrl;
    @Value("${iam.apikey}")
    private String apikey;
    @Value("${crk.issueParkView.url}")
    private String issueParkViewUrl;
    @Value("${crk.IssueDoorView.url}")
    private String crkIssueDoorViewUrl;
    @Value("${crk.getIssueDoorView.custom.url}")
    private String crkGetDoorViewCustomUrl;
    @Autowired
    private SafetyBpmProcessService safetyBpmProcessService;
    @Autowired
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Autowired
    private SafetyWaterFlowNoService safetyWaterFlowNoService;
    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    private SafetyEnterApproveMapper enterApproveMapper;
    @Autowired
    private IncomingService incomingService;
    @Autowired
    SafetyRegionDeviceInfoService safetyRegionDeviceInfoService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyEmployeePrivateCarService safetyEmployeePrivateCarService;
    @Autowired
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;

    public Object createFlow(SafetyOutsiderPromiseChangeDTO dto, String token) {
        String flowId = safetyBpmProcessService.createAndApproveProcess(dto.getProcessParam().getSubject(),  dto.getProcessParam().getModelId(), dto.getProcessParam().getFormId(), token);
        String flowNo = getFlowNo();
        List<SafetyOutsiderPromiseChangeDetailDTO> detailDTOS = JSONArray.parseArray(dto.getDetailDTOs(), SafetyOutsiderPromiseChangeDetailDTO.class);
        safetyOutsiderPromiseChangeService.addDetailData(detailDTOS, flowId.split(",")[1]);
        List<SafetyOutsiderPromiseChangeDetailDTO> oldDetailDTOS = JSONArray.parseArray(dto.getOldDetailDTOs(), SafetyOutsiderPromiseChangeDetailDTO.class);
        safetyOutsiderPromiseChangeService.addOldDetailData(oldDetailDTOS, flowId.split(",")[1]);
        //判断是否涉密 根据ID获取工厂名称 包含 研发中心、工程试验中心
        StringBuilder factoryIds = new StringBuilder();
        for (SafetyOutsiderPromiseChangeDetailDTO detail: detailDTOS) {
            String factory = detail.getFactory();
            if(StringUtils.isEmpty(factory)){
                continue;
            }
            factoryIds.insert(0, factory + ",");
        }
        String factoryCodes = safetyOutsiderPromiseChangeService.findFactoryCodes(factoryIds.toString());
        String secretLeader = "";
        dto.setSecret("0");
        //写涉密区审核人
        if(factoryCodes.contains("03")){
            dto.setSecret("1");
            secretLeader+= secretAuditor("03");
        }
        if(factoryCodes.contains("10")){
            dto.setSecret("1");
            secretLeader+= secretAuditor("10");
        }
        String collect = Arrays.stream(secretLeader.split(";"))
                .distinct()
                .collect(Collectors.joining(";"));
        dto.setSecretAuditor(collect);
        dto.setFlowNo(flowNo);
        dto.setFlowId(flowId.split(",")[1]);
        dto.setStatus("20");
        safetyOutsiderPromiseChangeService.addData(dto);
        return flowId;
    }

    public Object view(String flowId) {
        SafetyOutsiderPromiseChangeDTO view = safetyOutsiderPromiseChangeService.view(flowId);
        if(view == null){
            view = new SafetyOutsiderPromiseChangeDTO();
            return JSON.toJSON(view);
        }
        List<SafetyOutsiderPromiseChangeDetailDTO> details  = safetyOutsiderPromiseChangeService.findDetailsByFlowId(flowId);
        List<SafetyOutsiderPromiseChangeDetailDTO> oldDetails = safetyOutsiderPromiseChangeService.findOldDetailsByFlowId(flowId);
        view.setDetailDTOs(JSONArray.toJSON(details).toString());
        view.setOldDetailDTOs(JSONArray.toJSON(oldDetails).toString());
        return JSON.toJSON(view);
    }

    public Object audit(SafetyOutsiderPromiseChangeDTO dto, String token) {
        String detailDTOs = dto.getDetailDTOs();
        List<SafetyOutsiderPromiseChangeDetailDTO> details = JSONArray.parseArray(detailDTOs, SafetyOutsiderPromiseChangeDetailDTO.class);
        if(!ObjectUtils.isEmpty(details)){
            safetyOutsiderPromiseChangeService.updateDetails(details);
        }
        return safetyBpmProcessService.approveProcess(dto.getId(), dto.getProcessParam().getFlowId(),
                dto.getProcessParam().getModelId(), dto.getProcessParam().getFormId(), dto.getProcessParam().getFdParameterJson(), token);
    }

    /**
     * 根据登录名审批流程
     * @param dto 入参
     * @return
     */
    public Object auditByUserName(SafetyOutsiderPromiseChangeDTO dto) {
        String detailDTOs = dto.getDetailDTOs();
        List<SafetyOutsiderPromiseChangeDetailDTO> details = JSONArray.parseArray(detailDTOs, SafetyOutsiderPromiseChangeDetailDTO.class);
        if(!ObjectUtils.isEmpty(details)){
            safetyOutsiderPromiseChangeService.updateDetails(details);
        }
        String userName = dto.getLoginName();
        return safetyBpmProcessService.approveProcessByUserName(dto.getId(), dto.getProcessParam().getFlowId(),
                dto.getProcessParam().getModelId(), dto.getProcessParam().getFormId(), dto.getProcessParam().getFdParameterJson(), userName);
    }

    public Object findPageList(SafetyOutsiderPromiseDTO dto, String token) {
        if (!checkAdmin(token)){
            String empNo = TokenUtils.getWorkNo(token);
            try{
                if(!StringUtils.isEmpty(empNo) && empNo.startsWith("01")){
                    empNo = safetySpotCheckTaskService.employeeConvertWorkNo(empNo);
                }
            }catch (Exception e){
                throw new BizException(ResponseEnum.EXCEPTION.getCode(),"员工号转换失败！");
            }
            dto.setPowerNo(empNo);
        }
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeService.findPageList(dto);
        int total = safetyOutsiderPromiseChangeService.getCount(dto);
        return new PageVO<>(total, list);
    }


    public Object findPageListNoPower(SafetyOutsiderPromiseDTO dto) {
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeService.findPageList(dto);
        int total = safetyOutsiderPromiseChangeService.getCount(dto);
        return new PageVO<>(total, list);
    }

    /**
     * 更新流程状态发布时间 将流程中的过程明细写入存量表safety_outsider_promise
     * 审批流程的过程数据处理
     * @param processId
     */
    @Async
    public void updateFlowStatus(String processId) {
        try {
            //更新流程状态,发布时间
            safetyOutsiderPromiseChangeService.updateFlowStatus(processId);
            //将流程中的过程明细写入存量表safety_outsider_promise
            safetyOutsiderPromiseChangeService.updatePromiseByFlowId(processId);

            //获取流程审批后的数据,用于插入或者更新正式数据表
            List<String> processIdList = new ArrayList<>();
            processIdList.add(processId);
            List<SafetyOutsiderPromiseDTO> spList = safetyOutsiderPromiseChangeService.findFlowDataList(processIdList);
            try{
                //回写EHR
                safetyBaseHandler.sendToEHR(spList);
            }catch (Exception e){
                LogUtils.error("回写EHR异常",e);
            }
            //判断并关闭权限
            try{
                List<OutsiderFacoryChangeDTO> closeList = new ArrayList<>();
                SafetyOutsiderPromiseProcessDTO paramDto = new SafetyOutsiderPromiseProcessDTO();
                paramDto.setFlowId(processId);
                List<SafetyOutsiderPromiseProcessDTO> changList = safetyOutsiderPromiseChangeService.findProcessList(paramDto);
                if(!CollectionUtils.isEmpty(changList)){
                    for(SafetyOutsiderPromiseProcessDTO curObj:changList){
                        OutsiderFacoryChangeDTO cur = new OutsiderFacoryChangeDTO(curObj.getCooperativePersonId()
                                ,curObj.getCooperativePerson(), curObj.getAuthorizedFacility(), curObj.getChangedFacility());
                        closeList.add(cur);
                    }
                    //识别出关闭的厂区，同步关闭楼宇权限
                    closeEmpPower(closeList);
                }
            }catch (Exception e){
                LogUtils.error("识别出关闭的厂区,步关闭楼宇权限",e);
            }
            //下发权限，并回传EHR
            List<SafetyOutsiderPromiseDTO> oldPromise = safetyOutsiderPromiseChangeService.findDetailsByFlowIdList(processIdList);
            if(!CollectionUtils.isEmpty(oldPromise)){
                sendToCrk(oldPromise);
            }
            //下发车辆权限
            sendCarProcessToCRK(spList);
        }catch (Exception e){
            LogUtils.error("SafetyOutsiderPromiseChangeHandler.updateFlowStatus方法异常",e);
        }
    }

    /**
     * 校验response
     *
     * @param response
     * @return true校验通过，有查询结果
     */
    private boolean checkResponse(Response response) {
        if (Objects.isNull(response) || 200 != response.code() || Objects.isNull(response.body())) {
            LogUtils.error("查询错误,response={}", response);
            return false;
        }
        return true;
    }

    /**
     * 获取员工名下车辆信息
     * @param empNo 员工号
     * @return 车辆集合
     */
    public List<VehicleListDTO> getByFunc(String empNo) {
        List<String> carList = new ArrayList<>();
        List<VehicleListDTO> result = new ArrayList<>();
        String requestData = "\"StaffNo='" + empNo + "'\"";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, requestData);
        Request request = new Request.Builder()
                .url(issueParkViewUrl)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("查询外协车辆列表接口地址 {} \n 请求参数 {}", issueParkViewUrl, requestData);
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();

            if (!checkResponse(response)) {
                return result;
            }

            Map<String, Object> map = JSON.parseObject(response.body().string(), Map.class);
            JSONObject stateObject = (JSONObject)map.get("State");
            ResponseSateUtils stateVo = stateObject.toJavaObject(ResponseSateUtils.class);
            if (FAIL_CODE == stateVo.getCode() || !stateVo.isSucess()) {
                LogUtils.error("查询错误,response={}", response);
                return result;
            }

            JSONArray models = (JSONArray) map.get("Models");
            List<JSONObject> list = models.toJavaObject(List.class);

            for (JSONObject jsonObject : list) {
                VehicleListDTO vo = jsonObject.toJavaObject(VehicleListDTO.class);
                if(org.apache.commons.lang3.StringUtils.isBlank(vo.getPlate())){continue;}
                carList.add(vo.getPlate());
                result.add(vo);
            }
        } catch (IOException e) {
            LogUtils.error("查询车辆列表接口异常",e);
        }
        LogUtils.info("{}的车辆信息{}",empNo,carList);
        return result;
    }

    public Object issueEHRTiming(){
        List<String> processIdList = new ArrayList<>();
        //查询当前时间前 issueEhrTiming 个小时的审批完成的流程数据
        String beginTime = DateUtils.format(DateUtils.addHours(new Date(), -2), 2);
        String endTime = DateUtils.format(new Date(), 2);
        processIdList = safetyOutsiderPromiseChangeService.getLastTimeData(beginTime, endTime);

        if(CollectionUtils.isEmpty(processIdList)){
            return Collections.emptyList();
        }

        //获取流程审批后的数据,用于插入或者更新正式数据表
        List<SafetyOutsiderPromiseDTO> spList = safetyOutsiderPromiseChangeService.findFlowDataList(processIdList);
        if(CollectionUtils.isEmpty(spList)){
            return Collections.emptyList();
        }

        safetyOutsiderPromiseChangeService.saveOrUpdate(spList);
        safetyBaseHandler.sendToEHRForFactory(spList);
        List<SafetyOutsiderPromiseDTO> oldPromise = safetyOutsiderPromiseChangeService.findDetailsByFlowIdList(processIdList);
        if(!CollectionUtils.isEmpty(oldPromise)){
            sendToCrk(oldPromise);
        }
        //下发车辆权限
        sendCarProcessToCRK(spList);

        return Collections.emptyList();
    }

    public void sendCarProcessToCRK(List<SafetyOutsiderPromiseDTO> changeInfoList){
        List<String> empNoList = changeInfoList.stream().map(SafetyOutsiderPromiseDTO::getEmpNo).collect(Collectors.toList());
        //查询员工名下的车辆
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> carList = safetyEmployeePrivateCarService.selectList(privateCarDO);
        Map<String,List<SafetyEmployeePrivateCarDO>> carMap = carList.stream()
                .collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        changeInfoList.forEach(en -> {
            if(org.apache.commons.lang3.StringUtils.isBlank(en.getFactory())){return;}
            //查询对应的通道
            Map p = new HashMap();
            p.put("factoryCode", Arrays.asList(en.getFactory().split(",")));
            p.put("filingType","外协车辆");
            List<SafetyEmpowerDeviceDO> list1 = enterApproveMapper.checkEmpowerDevice(p);
            List<SafetyEmployeePrivateCarDO> curCarList = carMap.get(en.getEmpNo());
            //下发权限
            incomingService.insertCarAuthPrivate(en,list1,curCarList);
        });
    }

    //下发权限
    private void sendToCrk(List<SafetyOutsiderPromiseDTO> list) {
        //获取用户的楼宇权限
        List<String> empNoList = list.stream().map(SafetyOutsiderPromiseDTO::getEmpNo).collect(Collectors.toList());
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = getBuildDeviceId(empNoList);

        List<SafetyBuildIssuedLogDO> saveLogs = new ArrayList<>();
        String beginDate = DateUtils.format(new Date(),1);
        //获取所有的厂区名称；
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();

        for (SafetyOutsiderPromiseDTO dto:list) {
            try {
                //离岗状态不下发
                if(!ObjectUtils.isEmpty(dto.getStatus()) && dto.getStatus() == 0){
                    continue;
                }
                List<SafetyBuildEmployeePowerDO> buildPowerList = buildPowerMap.get(dto.getEmpNo());

                String buildDevices = "";
                String buildDeviceName = "";
                if(!CollectionUtils.isEmpty(buildPowerList)){
                    buildDevices = buildPowerList.stream()
                            .map(SafetyBuildEmployeePowerDO::getDeviceCode).distinct().collect(Collectors.joining(","));
                    buildDeviceName = buildPowerList.stream()
                            .map(SafetyBuildEmployeePowerDO::getDeviceName).distinct().collect(Collectors.joining(","));
                }

                if(!StringUtils.isEmpty(dto.getFactory())){
                    SafetyBuildIssuedLogDO curLog = initIssueLog(dto,beginDate,factoryMap);
                    curLog.setDeviceCode(buildDevices);
                    curLog.setDeviceName(buildDeviceName);

                    boolean issueFlag = false;
                    //根据工厂ID 设备类型(人脸) "外协" 获取授权设备号
                    String faceDevices = safetyOutsiderPromiseChangeService.findFaceDevices(dto.getFactory());
                    if(!StringUtils.isEmpty(buildDevices)){
                        faceDevices+=","+buildDevices;
                    }
                    if(StringUtils.isEmpty(faceDevices)){
                        LogUtils.info("员工号："+dto.getEmpNo()+"，未获取到 设备类型(人脸) \"外协\" 授权设备号！");
                    }else{
                        //更新门禁人脸权限
                        updateFaceData(dto, faceDevices);
                    }

                    //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
                    String cardDevices = safetyOutsiderPromiseChangeService.findCardDevices(dto.getFactory());
                    if(!StringUtils.isEmpty(buildDevices)){
                        cardDevices+=","+buildDevices;
                    }
                    //更新门禁卡权限
                    if(StringUtils.isEmpty(cardDevices)){
                        LogUtils.info("{\"code\":\"9999\",\"msg\":\"员工号："+dto.getEmpNo()+"，未获取到 设备类型(门禁) \"外协\" 授权设备号！\"}");
                    }else{
                        issueFlag = updateCardData(dto, cardDevices, "modify");
                    }
                    curLog.setIssuedStatus(issueFlag?"成功":"异常");

                    saveLogs.add(curLog);
                }
            } catch (Exception e) {
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"入参dto={}\"}",dto);
                LogUtils.error("sendToCrk下发出错----",e);
            }
        }
        if(!CollectionUtils.isEmpty(saveLogs)){
            safetyBuildIssuedLogService.batchInsert(saveLogs);
        }
    }

    private SafetyBuildIssuedLogDO initIssueLog(SafetyOutsiderPromiseDTO dto,String beginDate,
                                                Map<Integer, String> factoryMap){
        SafetyBuildIssuedLogDO curLog = new SafetyBuildIssuedLogDO();
        curLog.setEmployeeCode(dto.getEmpNo());
        curLog.setEmployeeName(dto.getEmpName());
        curLog.setDoorId(dto.getFactory());
        curLog.setIssuedType("开通");
        curLog.setTargetId("");
        curLog.setCreateBy(dto.getCreatorName());
        curLog.setRemark("外包外协权限调整");
        curLog.setBeginTime(beginDate);
        curLog.setEndTime(dto.getServiceEndDate());

        String factoryName = safetyBaseHandler.getParkNameBYCode(dto.getFactory(),factoryMap);
        curLog.setDoorName(factoryName);

        return curLog;
    }

    public Map<String, List<String>> getDeviceGidMapFromAb(String devices) {
        Map<String, List<String>> deviceGidMap = new HashMap<>();
        List<String> deviceList = Arrays.asList(devices.split(","));
        SafetyRegionDeviceInfoDTO param = new SafetyRegionDeviceInfoDTO();
        param.setDevNoList(deviceList);
        List<SafetyRegionDeviceInfoDO> list = safetyRegionDeviceInfoService.getList(param);
        if (CollectionUtils.isEmpty(list)) {return deviceGidMap;}

        deviceGidMap = list.stream().collect(Collectors.groupingBy(SafetyRegionDeviceInfoDO::getGid,Collectors.mapping(
                SafetyRegionDeviceInfoDO::getDevNo,
                Collectors.toList()
        )));
        return deviceGidMap;
    }

    public Map<String, List<String>> getDeviceGidMapModelFromAb(String devices) {
        Map<String, List<String>> deviceGidMap = new HashMap<>();
        List<String> deviceList = Arrays.asList(devices.split(","));
        SafetyRegionDeviceInfoDTO param = new SafetyRegionDeviceInfoDTO();
        param.setDevNoList(deviceList);
        List<SafetyRegionDeviceInfoDO> list = safetyRegionDeviceInfoService.getList(param);
        if (CollectionUtils.isEmpty(list)) {return deviceGidMap;}

        for (SafetyRegionDeviceInfoDO en : list) {
            try {
                String gid = en.getGid();
                if(StringUtils.isEmpty(gid)){continue;}
                List<String> gidMapList = deviceGidMap.get(gid);
                if(gidMapList == null){
                    gidMapList = new ArrayList<>();
                }
                SafetyRegionDeviceInfoDTO p = new SafetyRegionDeviceInfoDTO();
                BeanUtils.copyProperties(en, p);
                gidMapList.add(p.toString());
                deviceGidMap.put(gid,gidMapList);
            } catch (Exception e) {
                LogUtils.info("getDeviceGidMapModelFromAb处理设备信息的时候出错了，信息={}",e);
            }
        }
        return deviceGidMap;
    }

    private boolean updateCardData(SafetyOutsiderPromiseDTO dto, String empDevice, String type) {
        AtomicBoolean issueFlag = new AtomicBoolean(false);
        String empNo = dto.getEmpNo();
        String empName = dto.getEmpName();
        //根据设备号,获取所属gid 按照项目下发
        Map<String, List<String>> deviceGidMap = getDeviceGidMapModelFromAb(empDevice);
        //根据员工号获取员工token
        String returnValue = sendPost(crkIssueDoorViewUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
        JSONObject retObject = JSONObject.parseObject(returnValue);
        Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
        if(sucess){
            String endDate = getEndDateByEmpNo(dto.getEmpNo());
            JSONObject model = retObject.getJSONObject("Model");

            JSONObject params = new JSONObject();
            params.put("beginDate",DateUtils.format(new Date(),2));
            params.put("endDate",endDate+" 00:00:00");
            params.put("orginazitionId","00036914");
            params.put("serialNo",model.getString("SerialNo"));
            params.put("staffName",empName);
            params.put("staffNo",empNo);
            params.put("stcm","2C3DB448-9918-4665-A4C1-C99797E2A4DD");
            params.put("tcm",model.getString("TcmId"));
            params.put("token",model.getString("TokenId"));
            params.put("tokenId",model.getString("TokenId"));
            params.put("rid",UUID.randomUUID());
            params.put("tokenOper",2);
            params.put("tokenType",1);
            params.put("useModel",1);
            params.put("icStartTimeL1", "00:00");
            params.put("icStartTimeL2", "00:00");
            params.put("icStartTimeL3", "00:00");
            params.put("icStartTimeL4", "00:00");
            params.put("icStartTimeR1", "23:59");
            params.put("icStartTimeR2", "00:00");
            params.put("icStartTimeR3", "00:00");
            params.put("icStartTimeR4", "00:00");
            params.put("CarPass", "123456");

            deviceGidMap.forEach((key, value) -> {
                String[] ProjectGids = new String[]{key};
                params.put("projectGids",ProjectGids);
                params.put("gid",key);
                //根据员工号和gid获取是否已经授权过,没授权走新增
                String checkGid = sendPost(crkGetDoorViewCustomUrl, "{\"where\": \"StaffNo='"+empNo+"' and Gid='"+key+"' and TokenType=1 \"}");
                JSONObject checkGidObject = JSONObject.parseObject(checkGid);
                if(!ObjectUtils.isEmpty(checkGidObject)){
                    JSONArray gidRecords = checkGidObject.getJSONArray("Records");
                    if(CollectionUtils.isEmpty(gidRecords)){
                        params.put("tokenOper",1);
                    }
                }
                JSONArray doorGrant = new JSONArray();
                if(value != null && value.size() > 0){
                    for(String deviceModelStr: value){
                        JSONObject deviceModel = JSONObject.parseObject(deviceModelStr);
                        JSONObject doorGrantMap = new JSONObject();
                        doorGrantMap.put("collectCode", deviceModel.getString("collectCode"));
                        doorGrantMap.put("d1Grant", 1);
                        doorGrantMap.put("d1OpenMode", 1);
                        doorGrantMap.put("d1Valid", 0);
                        doorGrantMap.put("devName", deviceModel.getString("DevName"));
                        doorGrantMap.put("devNo", deviceModel.getString("DevNo"));
                        doorGrantMap.put("tokenId", model.getString("TokenId"));
                         /* 设备人脸识别授权卡片凭证的，设备参数里要走人脸的设备类型31 由BA确认上线日期*/
                        String devTypeCode = deviceModel.getString("DevTypeCode");
                        if(org.apache.commons.lang3.StringUtils.isBlank(devTypeCode)){
                            devTypeCode = "00";
                        }
                        //给697人脸调用卡片发行接口发行 "DevTypeCode": "31", 时新增参数 "CollectCode": "20",（多加个这个）
                        if("31".equals(devTypeCode)){
                            doorGrantMap.put("CollectCode","20");
                        }
                        doorGrantMap.put("devTypeCode", devTypeCode);
                        doorGrantMap.put("gid", deviceModel.getString("Gid"));
                        doorGrantMap.put("rid", UUID.randomUUID());
                        doorGrant.add(doorGrantMap);
                    }
                    params.put("doorGrant",doorGrant);
                    LogUtils.info("外协外包card修改,请求参数={}" , params);
                    String modifyResult = sendPost(cardModifyUrl, params.toJSONString());
                    LogUtils.info("外协外包card修改name={}返回消息={}" , empName ,modifyResult);
                    if(!StringUtils.isEmpty(modifyResult)){
                        JSONObject result = JSONObject.parseObject(modifyResult);
                        if(result.getJSONObject("State") != null && result.getJSONObject("State").getBoolean("IsSucess")){
                            issueFlag.set(true);
                            safetyOutsiderPromiseChangeService.updatePromiseCardStatus(empNo);
                        }
                    }
                }
            });
        }
        return issueFlag.get();
    }

    private String getEndDateByEmpNo(String empNo) {
        return safetyOutsiderPromiseChangeService.getEndDateByEmpNo(empNo);
    }

    private void updateFaceData(SafetyOutsiderPromiseDTO dto, String empDevice) {
        String empNo = dto.getEmpNo();
        String empName = dto.getEmpName();
        //获取生活照
        String lifePhoto = getPhoto(empNo, 1);
        if(StringUtils.isEmpty(lifePhoto)){
            //获取证件照
            lifePhoto = getPhoto(empNo, 0);
        }
        //未获取到照片
        if(StringUtils.isEmpty(lifePhoto)){
            LogUtils.info("{}获取员工人脸照片失败!",empNo);
            return;
        }
        //图片转化为二进制
        lifePhoto = lifePhoto + "&apikey=" + apikey;
        dto.setPhotoUrl(lifePhoto); //保存照片
        byte[] imageBytes = getPictureDataInputStream(lifePhoto);
        String base64_str = DatatypeConverter.printBase64Binary(imageBytes);

        String beginDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);//开始日期
        String endDate = getEndDateByEmpNo(dto.getEmpNo());//结束日期

        Map<String, List<String>> deviceGidMap = getDeviceGidMapFromAb(empDevice);
        deviceGidMap.forEach((key, value) -> {
            if (value == null || value.isEmpty()) {
                LogUtils.info("授权设备号列表为空，不进行人脸修改");
                return;
            }
            JSONObject params = new JSONObject();
            JSONObject Staff = new JSONObject();
            Staff.put("StaffNo", empNo);//员工号
            Staff.put("StaffName", empName); //姓名
            Staff.put("OrganizationId", "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7");
            Staff.put("Gid",key);
            params.put("BeginTime",beginDate+" 00:00:00");//开始日期
            params.put("EndTime",endDate+" 23:59:59");
            params.put("AuthorizeDeviceNo", value);
            String[] ProjectGids = new String[]{key};
            params.put("ProjectGids", ProjectGids);
            params.put("Staff", Staff);
            params.put("Gid", key);
            params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
            params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
            LogUtils.info("外包外协人脸修改请求参数={}" , params);
            params.put("FacePicture", base64_str);

            String modifyResult = sendPost(faceModifyUrl, params.toJSONString());
            LogUtils.info("外包外协人脸修改name={}返回消息={}" , empName ,modifyResult);
        });
    }

    /**
     * 获取照片 优先获取生活照,没有生活照,获取证件照
     * 照片类型：0证件照，1生活照
     * @param
     * @return
     */
    public String getPhoto(String empNo, int type){
        //获取员工照片
        JSONObject param = new JSONObject();
        param.put("targetId",targetId);
        param.put("workNo",empNo);
        param.put("photoType",type);
        Map<String,String> headMap = new HashMap();
        headMap.put("Content-Type","application/json");
        String result = webserviceUtil.sendHttp(getPhotoUrl,"2", param.toJSONString(), headMap,"POST");
        if (StringUtils.isEmpty(result)) {
            LogUtils.info("中台接口返回信息为空");
            return "";
        }
        JSONObject dataObj = JSONObject.parseObject(result);
        if (!dataObj.containsKey("success") || !dataObj.getBoolean("success")) {
            LogUtils.info("中台获取数据失败！");
            return "";
        }
        //获取result对象
        String lifePhoto = dataObj.getString("result");
        if (StringUtils.isEmpty(lifePhoto)){
            LogUtils.info("获取lifePhoto为空！");
            return "";
        }
        if(lifePhoto.indexOf("yutong.com") > 0){
            return lifePhoto;
        }
        return "";
    }

    public byte[] getPictureDataInputStream(String path) {
        Response response = null;
        InputStream ins = null;
        ByteArrayOutputStream output = null;
        byte[] data = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
            ins = response.body().byteStream();
            output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = ins.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();

        } catch (IOException e) {
            LogUtils.info("加载图片失败",e);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(output);
        }
        return data;
    }

    /**
     * type == "dept" 用于获取员工信息
     * 其他获取员工历史权限
     * @param empNos 员工号
     * @param type 类型
     * @return 返回值
     */
    public Object promise(String empNos, String type, String token) {
        JSONObject ret = new JSONObject();

        if(StringUtils.isEmpty(empNos)){
            LogUtils.error("方法promise 入参：empNos="+empNos+";type="+type);
            ret.put("status","F");
            ret.put("msg","提示您不是"+empNos+"的对接人，无权限申请!");
        }
        if("dept".equals(type)){
            return safetyOutsiderPromiseChangeService.empDept(empNos);
        }
        Map<String, String> user = TokenUtils.getTockenUser(token);
        List<SafetyOutsiderPromiseDTO> oldPromise = null;

        if(!checkAdmin(token)){ //非超级管理员 判断是否是创建人
            oldPromise = safetyOutsiderPromiseChangeService.findOldPromise(empNos, user.get("workNo"));
            if(ObjectUtils.isEmpty(oldPromise)){
                LogUtils.error("提示您不是"+empNos+"的对接人，无权限申请!");
                ret.put("status","F");
                ret.put("msg","提示您不是"+empNos+"的对接人，无权限申请!");
                return ret;
            }
        }else{ //超级管理员直接根据员工号查数据
            oldPromise = safetyOutsiderPromiseChangeService.findOldPromise(empNos, "");
        }
        ret.put("status","S");
        ret.put("msg","");
        ret.put("list",oldPromise);
        return ret;
    }

    /**
     * 用于获取员工信息
     * 其他获取员工历史权限
     * @param empStr 姓名或员工号
     * @return
     */
    public Object promiseByName(String empStr,String type, String token) {
        Map<String, String> user = TokenUtils.getTockenUser(token);
        List<SafetyOutsiderPromiseDTO> oldPromise = null;
        JSONObject ret = new JSONObject();
        if(!StringUtils.isEmpty(empStr) && empStr.startsWith("01")){
            empStr = safetySpotCheckTaskService.employeeConvertWorkNo(empStr);
        }
        ret.put("status","S");
        ret.put("msg","");
        if("dept".equals(type)){
            List retList = safetyOutsiderPromiseChangeService.getEmpListByDept(empStr);
            ret.put("list",retList);
            return ret;
        }
        oldPromise = safetyOutsiderPromiseChangeService.findPromiseByName(empStr, "");
        ret.put("list",oldPromise);
        return ret;
    }

    public HSSFWorkbook exportData(SafetyOutsiderPromiseDTO dto, String token) throws Exception {
        if (!checkAdmin(token)){
            String empNo = TokenUtils.getWorkNo(token);
            try{
                if(org.apache.commons.lang3.StringUtils.isNotBlank(empNo) && empNo.startsWith("01")){
                    empNo = safetySpotCheckTaskService.employeeConvertWorkNo(empNo);
                }
            }catch (Exception e){
                throw new BizException(ResponseEnum.EXCEPTION.getCode(),"员工号转换失败！");
            }
            dto.setPowerNo(empNo);
        }
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeService.exportData(dto);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"发起人姓名","发起人员工号","员工号","姓名","人脸照片识别","外协组织",
                "已授权厂区","调整时间","用工结束时间","权限状态","状态","备注"};
        String[][] values = new String[list.size()][];
        SafetyOutsiderPromiseDTO safetyOutsiderPromiseDTO;
        for(int i=0; i<list.size(); i++) {
            safetyOutsiderPromiseDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetyOutsiderPromiseDTO.getCreatorName();
            values[i][1] = safetyOutsiderPromiseDTO.getCreatorNo();
            values[i][2] = safetyOutsiderPromiseDTO.getEmpNo();
            values[i][3] = safetyOutsiderPromiseDTO.getEmpName();
            values[i][4] = safetyOutsiderPromiseDTO.getPhotoUrl();
            values[i][5] = safetyOutsiderPromiseDTO.getEmpOrg();
            values[i][6] = safetyOutsiderPromiseDTO.getFactoryName();
            values[i][7] = safetyOutsiderPromiseDTO.getUpdateTime();
            values[i][8] = safetyOutsiderPromiseDTO.getServiceEndDate();
            values[i][9] = safetyOutsiderPromiseDTO.getStatusDesc();
            values[i][10] = safetyOutsiderPromiseDTO.getStatus()==0?"离岗":"在岗";
            values[i][11] = safetyOutsiderPromiseDTO.getRemark();
        }
        String sheetName = "监控岗位验证结果列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    private boolean checkAdmin(String token){
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName("超级管理员");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        for (SafetySysUserDO userDO:list){
            if (userDO.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }
    /**
     * 生成流水号
     * @return
     */
    private String getFlowNo(){
        String date = DateUtils.format(new Date(),7).substring(2);
        String nowNum = safetyWaterFlowNoService.getNowByType("outsider_promise");
        return Joiner.on("").join("NO.YTBW-QX",date+nowNum);
    }

    @Async
    public void forEhrAdd(String promise) {
        List<SafetyOutsiderPromiseDTO> dto = JSONArray.parseArray(promise, SafetyOutsiderPromiseDTO.class);
        List<String> empNos = dto.stream().map(SafetyOutsiderPromiseDTO::getEmpNo).collect(Collectors.toList());
        SafetyEmployeeMessageDOExample doExample = new SafetyEmployeeMessageDOExample();
        doExample.createCriteria().andEmpNoIn(empNos);
        List<SafetyEmployeeMessageDO> employeeMessageDOS = safetyEmployeeMessageService.selectByExample(doExample);
        Map<String,SafetyEmployeeMessageDO> employeeMessageDOMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(employeeMessageDOS)){
            employeeMessageDOMap = employeeMessageDOS.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, Function.identity()));
        }
        List<ParamDTO> changeList = new ArrayList<>();

        List<SafetyOutsiderPromiseDTO> newList = new ArrayList<>();
        for(SafetyOutsiderPromiseDTO curDto:dto){
            if(!StringUtils.isEmpty(curDto.getFactory())){
                String factory = safetyOutsiderPromiseChangeService.findFactoryIds(curDto.getFactory());
                curDto.setFactory(factory);
            }
            SafetyEmployeeMessageDO employeeMessageDO = employeeMessageDOMap.get(curDto.getEmpNo());
            String orgNamePath = "";
            if(employeeMessageDO != null){
                orgNamePath = employeeMessageDO.getOrgNamePath();
            }
            if(!StringUtils.isEmpty(orgNamePath)){
                orgNamePath = orgNamePath.replace(",",">>");
            }
            curDto.setEmpOrg(orgNamePath);
            newList.add(curDto);
            ParamDTO pdto = new ParamDTO();
            BeanUtils.copyProperties(curDto,pdto);
            pdto.setFactoryId(curDto.getFactory());
            pdto.setPowerEndTime(curDto.getServiceEndDate());
            pdto.setType("0"); //更新
            pdto.setUserType("WX"); //外协
            changeList.add(pdto);
        }

        safetyOutsiderPromiseChangeService.saveOrUpdate(newList);
        sendToCrkNew(changeList);//人脸做更新和下发 卡只做更新 下发走定时任务
    }

    public Object importData(CommonImportParam param, String token) {
        Map<String, String> user = TokenUtils.getTockenUser(token);
        if(checkAdmin(token)){
            return safetyOutsiderPromiseChangeService.importData(param, "");
        }
        return safetyOutsiderPromiseChangeService.importData(param, user.get("workNo"));
    }

    public String secretAuditor(String factoryCode) {
        UserMessageListParam param = new UserMessageListParam();
        param.setFactoryCode(factoryCode);
        param.setRoleName("涉密区保卫");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        String secretAuditor = "";
        for (SafetySysUserDO userDO: list){
            secretAuditor = Joiner.on(";").join(secretAuditor,userDO.getUserName());
        }
        return secretAuditor;
    }

    /**
     * 发起POST请求
     *
     * @param url
     *            url
     * @param paramJson
     *            参数的json格式
     */
    public static String sendPost(String url, String paramJson) {

        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");
        try {
            int statusCode = 0;
            int i = 0;
            //链接异常时，可重试三次
            while (statusCode != HttpStatus.SC_OK && i<3) {
                // json格式的参数解析
                RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
                postMethod.setRequestEntity(entity);
                statusCode = httpClient.executeMethod(postMethod);
                if (statusCode == HttpStatus.SC_OK) {
                    String result = postMethod.getResponseBodyAsString();
                    postMethod.releaseConnection();
                    return result;
                }
                i++;
            }
            LogUtils.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}", url, paramJson, "HTTP状态码: " + statusCode);
        } catch (IOException e) {
            LogUtils.error("POST请求发出失败，请求的地址为{},错误信息为{}",url, e.getMessage());
        }finally {
            postMethod.releaseConnection();
        }
        return null;
    }

    public void cardDistribute() {
        //获取所有待下发卡的人员信息
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeService.getForCardDistribute();
        for (SafetyOutsiderPromiseDTO dto:list) {
            if (!StringUtils.isEmpty(dto.getFactory())) {
                //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
                String cardDevices = safetyOutsiderPromiseChangeService.findCardDevices(dto.getFactory());
                if(StringUtils.isEmpty(cardDevices)){
                    LogUtils.info("{\"code\":\"9999\",\"msg\":\"员工号："+dto.getEmpNo()+"，未获取到 设备类型(门禁) \"外协\" 授权设备号！\"}");
                }else{
                    //更新门禁卡权限
                    updateCardData(dto, cardDevices, "add");
                }
            }
        }
    }

    /**
     * 修改对接人及厂区信息
     * @param dto
     * @param token
     */
    public void updateCreator(SafetyOutsiderPromiseChangeDTO dto,String token){
        List<SafetyOutsiderPromiseDTO> oldList = safetyOutsiderPromiseChangeService.getEnById(dto.getId());
        SafetyOutsiderPromiseDTO oldFirst = oldList.get(0);
        SafetyOutsiderPromiseChangeDetailDTO oldDetail = new SafetyOutsiderPromiseChangeDetailDTO();
        BeanUtils.copyProperties(oldFirst,oldDetail);
        //流程编号
        String flowNo = getFlowNo();
        oldDetail.setFlowId(flowNo);
        oldDetail.setOrg(oldFirst.getEmpOrg()); //组织
        SafetyOutsiderPromiseChangeDetailDTO newDetail = new SafetyOutsiderPromiseChangeDetailDTO();
        BeanUtils.copyProperties(oldDetail,newDetail);
        newDetail.setFactory(dto.getFactory());
        newDetail.setServiceEndDate(dto.getServiceEndDate());
        dto.setFlowId(flowNo);

        safetyOutsiderPromiseChangeService.updateCreator(dto);
        //保存修改记录
        try {
            savePromisChange(dto,token,oldDetail,newDetail);
        } catch (Exception e) {
            LogUtils.error("保存记录异常",e);
        }
        List<SafetyOutsiderPromiseDTO> enList = safetyOutsiderPromiseChangeService.getEnById(dto.getId());
        try{
            safetyBaseHandler.sendToEHR(enList);
        }catch (Exception e){
            LogUtils.error("外协更新回写EHR异常",e);
        }
        try{
            List<OutsiderFacoryChangeDTO> closeList = new ArrayList<>();
            OutsiderFacoryChangeDTO cur = new OutsiderFacoryChangeDTO(newDetail.getEmpNo(),newDetail.getEmpName(), oldDetail.getFactory(), newDetail.getFactory());
            closeList.add(cur);
            //识别出关闭的厂区，同步关闭楼宇权限
            closeEmpPower(closeList);
        }catch (Exception e){
            LogUtils.error("识别出关闭的厂区,步关闭楼宇权限",e);
        }
        try{
            sendToCrk(enList);
            //下发车辆权限
            sendCarProcessToCRK(enList);
        }catch (Exception e){
            LogUtils.error("外协更新CRK权限异常",e);
        }
    }

    /**
     * 对比出要关闭的厂区
     * @param oldFactory
     * @param newFactory
     * @return
     */
    public Set<String> determineCloseFactories(String oldFactory, String newFactory) {
        // 将 oldFactory 分割成 Set
        Set<String> oldFactorySet = StringUtils.isEmpty(oldFactory)
                ? new HashSet<>()
                : Arrays.stream(oldFactory.split(","))
                .collect(Collectors.toSet());

        // 如果 newFactory 为空，直接返回 oldFactorySet
        if (StringUtils.isEmpty(newFactory)) {
            return oldFactorySet;
        }

        // 将 newFactory 分割成 Set
        Set<String> newFactorySet = Arrays.stream(newFactory.split(","))
                .collect(Collectors.toSet());

        // 计算需要关闭的厂区集合
        Set<String> closeFactorySet = oldFactorySet.stream()
                .filter(fac -> !newFactorySet.contains(fac))
                .collect(Collectors.toSet());

        return closeFactorySet;
    }

    /**
     * 根据变更记录判断并关闭大门楼宇权限
     * @param list 变更记录
     */
    public void closeEmpPower(List<OutsiderFacoryChangeDTO> list){
        //原来的厂区为空，或者厂区未发生变化的时候，不进行关闭操作
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<OutsiderFacoryChangeDTO> filterList = list.stream().filter(e->!StringUtils.isEmpty(e.getOldFactory()))
                .filter(e->!e.getOldFactory().equals(e.getNewFactory())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(filterList)){
            return;
        }
        Set<String> changeEmpNos = new HashSet<>();
        Map<String,Set<String>> closeFacMap = new HashMap<>();
        for(OutsiderFacoryChangeDTO curDto:filterList){
            Set<String> closeFactorySet = determineCloseFactories(curDto.getOldFactory(),curDto.getNewFactory());
            if(closeFactorySet == null || closeFactorySet.isEmpty()){
                continue;
            }
            changeEmpNos.add(curDto.getEmpNo());
            closeFacMap.put(curDto.getEmpNo(),closeFactorySet);
        }
        //关闭员工第三方权限
        modifyEmpPowerForClose(closeFacMap, true);
    }

    public boolean validProcess(String curEmpNo, String chooseEmpNo){
        return safetyOutsiderPromiseChangeService.validProcess(curEmpNo,chooseEmpNo);
    }

    public List<SafetyFieldManageDO> processFactoryByEmpNo(String empNo){
        return safetyOutsiderPromiseChangeService.processFactoryByEmpNo(empNo);
    }

    /**
     * 新的下发逻辑，合并楼宇门禁
     * @param list
     */
    public void sendToCrkNew(List<ParamDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        // 3.查询员工对应的车辆信息
        List<String> empnoList = list.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        List<String> filterEmpNos = safetyBaseHandler.filterBlackEmpNos(empnoList);
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empnoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarService.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        //获取员工的门禁权限和门禁对应的设备ID
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = getBuildDeviceId(empnoList);

        // 1.获取所有的厂区名称；
        Map<Integer, String> map = safetyFieldManageService.getFactoryNameList();
        List<ParamDTO> updateStatusList = new ArrayList<>();
        // 6.下发
        List<ParamDTO> openList = list.stream().filter(li -> li.getType().equals("0")).collect(Collectors.toList());
        // 初始化组织ID和curId
        Map<String, SafetyEmpCardVO> empCardMap = safetyBaseHandler.generalCrkStaffForBatch(empnoList);
        // 下发开始时间
        String xfBeginDate = DateUtils.format(new Date(), 1) + " 00:00:00";
        // 下发结束时间 默认是当前日志加1年后
        String xfEndDate = DateUtils.format(DateUtils.addYears(new Date(),1),1)+" 23:59:59";
        openList.forEach(li -> {
            if(StringUtils.isEmpty(li.getFactoryId())){
                return;
            }
            String curEmpNo = li.getEmpNo();
            //获取员工的出入口信息
            SafetyEmpCardVO empCardVO = empCardMap.get(curEmpNo);

            List<SafetyBuildEmployeePowerDO> buildPowerList = buildPowerMap.get(curEmpNo);
            if(buildPowerList == null){
                buildPowerList = new ArrayList<>();
            }
            //初始化厂区名称
            safetyBaseHandler.getParkNameBYCode(li,map);
            // 确定开始时间
            li.setBeginDate(xfBeginDate);
            // 确认结束时间
            String powerEndTime = li.getPowerEndTime();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(powerEndTime)) {
                li.setEndDate(li.getPowerEndTime());
            } else {
                li.setEndDate(xfEndDate);
                li.setPowerEndTime(xfEndDate);
            }
            List<SafetyEmployeePrivateCarDO> carinfoList = carinfoMap.get(li.getEmpNo());
            // 判断不在黑名单中时 更新外协进出权限
            if ("WX".equals(li.getUserType()) && !CollectionUtils.isEmpty(filterEmpNos) && filterEmpNos.contains(curEmpNo)) {
                if (wxPowerUpdate(li, carinfoList,empCardVO,buildPowerList)) {
                    // 下发成功，回写状态
                    updateStatusList.add(li);
                }
            }
        });
        LogUtils.info("成功更新{}条外协人员记录",updateStatusList.size());
        if (!CollectionUtils.isEmpty(updateStatusList)) {
            try {
                // 修改状态 同时更新生活照
                safetyOutsiderPromiseChangeService.batchUpdate(updateStatusList);
            }catch (Exception e){
                LogUtils.error("批量更新状态异常",e);
            }
        }
        try {
            // 修改状态 同时更新生活照
            safetyOutsiderPromiseChangeService.batchUpdateForPhoto(openList);
        }catch (Exception e){
            LogUtils.error("批量更新人脸照片异常",e);
        }
    }

    /**
     * 外协人脸下发
     * @param paramDTO 入参
     */
    public boolean wxPowerUpdate(ParamDTO paramDTO,List<SafetyEmployeePrivateCarDO> carList,
                                 SafetyEmpCardVO empCardVO,List<SafetyBuildEmployeePowerDO> buildPowerList) {
        String empNo = paramDTO.getEmpNo();
        String factoryId = paramDTO.getFactoryId();

        SafetyBuildIssuedLogDO info = new SafetyBuildIssuedLogDO();
        info.setEmployeeCode(empNo);
        info.setEmployeeName(paramDTO.getEmpName());
        info.setEndTime(paramDTO.getPowerEndTime());
        info.setBeginTime(paramDTO.getPowerBeginTime());
        // 下发人脸
        //根据工厂ID 设备类型获取授权设备号
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType","外协");
        paramMap.put("factoryIds",Arrays.asList(factoryId.split(",")));
        paramMap.put("regionName","大门人脸");

        String buildPowerStr = "";
        if(!CollectionUtils.isEmpty(buildPowerList)){
            buildPowerStr = buildPowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).distinct().collect(Collectors.joining(","));
        }

        boolean result = true;
        StringBuilder message = new StringBuilder();
        message.append(paramDTO.getEmpName()).append("[").append(paramDTO.getEmpNo()).append("]:人脸下发");
        try {
            String faceDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            if (StringUtils.isEmpty(faceDevices) && StringUtils.isEmpty(buildPowerStr)) {
                message.append("失败，未获取到设备类型(人脸) 授权设备号；---");
                LogUtils.info("wxPowerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(人脸) 授权设备号！",empNo,factoryId);
            } else {
                //获取生活照
                String lifePhoto = getPhoto(empNo, 1);
                if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
                    //获取证件照
                    lifePhoto = getPhoto(empNo, 0);
                }
                //未获取到照片
                if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
                    LogUtils.info("{}获取员工人脸照片失败!",empNo);
                    result = false;
                }else{
                    //图片转化为二进制
                    lifePhoto = lifePhoto + "&apikey=" + apikey;
                    paramDTO.setPhotoUrl(lifePhoto); //设置照片
                    faceDevices = PinYinUtils.stringFormat(faceDevices, buildPowerStr);
                    Map<String, List<String>> deviceGidMap = getDeviceGidMapFromAb(faceDevices);
                    // 更新门禁人脸权限
                    safetyBaseHandler.updateFaceData(info,empCardVO,deviceGidMap,lifePhoto);
                    message.append("成功；---");
                }
            }
        } catch (Exception e) {
            message.append("失败；---");
            result = false;
        }
        try {
            paramMap.put("regionName", "大门门禁");
            String cardDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            //更新门禁卡权限
            if (StringUtils.isEmpty(cardDevices)&& StringUtils.isEmpty(buildPowerStr)) {
                message.append("下发门禁失败，未获取到设备类型(门禁) 授权设备号；---");
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"wxPowerUpdate:员工号：{}，厂区：{}，未获取到 设备类型(门禁) \"外协\" 授权设备号！\"}",empNo,factoryId);
            } else {
                cardDevices = PinYinUtils.stringFormat(cardDevices, buildPowerStr);
                Map<String, List<String>> deviceGidMap = getDeviceGidMapModelFromAb(cardDevices);
                // 更新门禁卡权限
                safetyBaseHandler.updateCardData(info, "modify",empCardVO,deviceGidMap);
                message.append("下发门禁成功；---");
            }
        } catch (Exception e) {
            message.append("下发门禁失败；---");
            result = false;
        }
        try {
            if (CollectionUtils.isEmpty(carList)) {
                message.append("员工没有车辆信息");
            } else {
                paramMap.put("empowerType", "外协车辆");
                paramMap.put("regionName", "大门车行");
                String carDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
                if (org.apache.commons.lang3.StringUtils.isBlank(carDevices)) {
                    message.append("下发车辆失败，未获取到大门车行 授权设备号。");
                    LogUtils.info("wxPowerUpdate:员工号：{}，厂区：{}，，未获取到 大门车行 授权设备号！", empNo, factoryId);
                } else {
                    // 更新车权限
                    safetyBaseHandler.downCarPower(info, carDevices, carList,paramDTO.getPhoneNo(),"WX");
                    message.append("下发车辆成功。");
                }
            }
        } catch(Exception e) {
            message.append("下发车辆失败；---");
            result = false;
        }
        // 日志
        return result;
    }

    /**
     * 多个员工获取设备ID信息
     */
    public Map<String, List<SafetyBuildEmployeePowerDO>> getBuildDeviceId(List<String> employeeCodes) {
        Map<String, List<SafetyBuildEmployeePowerDO>> dataMap = new HashMap<>();
        // 根据员工号获取员工所有已开通门禁权限
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setEmpCodeList(employeeCodes);
        List<BuildEmployeePowerVO> employeePowerDOS = safetyBuildEmployeePowerService.selectList(listParam);
        // 过滤掉非富士的门禁数据
        employeePowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(employeePowerDOS)) {
            // 员工门禁 数据分组 根据员工号分组
            dataMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        }
        return dataMap;
    }


    /**
     * 多个员工获取设备ID信息-获取指定厂区下的
     * @param closeFacMap 关闭的员工数据
     * @Param isClose 是否需要关闭
     */
    public void modifyEmpPowerForClose(Map<String,Set<String>> closeFacMap,Boolean isClose) {

        Set<String> allFacSet = new HashSet<>();
        Set<String> empNoSet = new HashSet<>();
        closeFacMap.forEach((key,value)->{
            allFacSet.addAll(value);
            empNoSet.add(key);
        });

        // 根据员工号获取员工所有已开通门禁权限
        List<String> employeeCodes = new ArrayList<>(empNoSet);
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        if(CollectionUtils.isEmpty(employeeCodes) || CollectionUtils.isEmpty(allFacSet)){
            return;
        }
        listParam.setEmpCodeList(new ArrayList<>(employeeCodes));
        listParam.setFactoryIdList(new ArrayList<>(allFacSet));
        //获取员工信息
        List<SafetyEmployeeMessageDO> empMessDos = safetyEmployeeMessageService.getListByStaffNo(employeeCodes);
        Map<String,SafetyEmployeeMessageDO> empMap = empMessDos.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));

        List<BuildEmployeePowerVO> employeePowerDOS = safetyBuildEmployeePowerService.selectList(listParam);
        // 过滤掉非富士的门禁数据
        List<BuildEmployeePowerVO>  fsEmpPowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        String powerBeginDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(employeeCodes);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarService.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));
        // 初始化组织ID和curId
        Map<String, SafetyEmpCardVO> empCardMap = safetyBaseHandler.generalCrkStaffForBatch(employeeCodes);

        // 员工门禁 数据分组 根据员工号分组
        Map<String, List<SafetyBuildEmployeePowerDO>> fsDataMap = fsEmpPowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        empMap.forEach((empNo,curEmp)->{
            List<SafetyBuildEmployeePowerDO> buildList=fsDataMap.get(empNo);
            Set<String> closeFacSet = closeFacMap.get(empNo);
            if(closeFacSet == null ||closeFacSet.isEmpty()){
                return;
            }
            ParamDTO paramDTO = new ParamDTO();
            paramDTO.setEmpNo(empNo);
            paramDTO.setEmpName(curEmp.getEmpName());
            paramDTO.setFactoryId(closeFacSet.stream().collect(Collectors.joining(",")));
            // 使用StringBuilder来高效地拼接字符串
            StringBuilder deviceCodeBuilder = new StringBuilder();
            StringBuilder deviceNameBuilder = new StringBuilder();
            //无楼宇权限时，只关闭厂区权限
            if(buildList != null && !CollectionUtils.isEmpty(buildList)){
                for (SafetyBuildEmployeePowerDO build : buildList) {
                    if (deviceCodeBuilder.length() > 0) {
                        deviceCodeBuilder.append(",");
                        deviceNameBuilder.append(",");
                    }
                    deviceCodeBuilder.append(build.getDeviceCode());
                    deviceNameBuilder.append(build.getDeviceName());
                }
            }
            paramDTO.setDeviceCode(deviceCodeBuilder.toString());
            paramDTO.setDeviceName(deviceNameBuilder.toString());
            paramDTO.setPowerEndTime(powerBeginDate+" 23:59:59");
            paramDTO.setPowerBeginTime(powerBeginDate+" 00:00:00");

            List<SafetyEmployeePrivateCarDO> curCarList = carinfoMap.get(empNo);
            SafetyEmpCardVO empCardVO = empCardMap.get(empNo);
            //更新外包外协权限 大门+楼宇
            wxPowerUpdate(paramDTO, curCarList,empCardVO,buildList);
        });
        //新开普的楼宇权限
        List<BuildEmployeePowerVO>  xkpEmpPowerDOS = employeePowerDOS.stream().filter(li ->  "1".equals(li.getDeviceType())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(xkpEmpPowerDOS)){
            Map<String, List<SafetyBuildEmployeePowerDO>> xkpDataMap = xkpEmpPowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
            xkpDataMap.forEach((empNo,buildList)->{
                Set<String> closeFacSet = closeFacMap.get(empNo);
                if(closeFacSet == null ||closeFacSet.isEmpty()){
                    return;
                }
                closeXkpEmpPower(empNo,buildList,closeFacSet.stream().collect(Collectors.joining(",")));
            });
        }
        if(isClose && !CollectionUtils.isEmpty(employeePowerDOS)){
            SafetyBuildEmployeePowerDO record = new SafetyBuildEmployeePowerDO();
            record.setRemark("关闭厂区时关闭对应楼宇");
            List<String> closeBuildPowerIdList = employeePowerDOS.stream().map(BuildEmployeePowerVO::getId).collect(Collectors.toList());
            List<List<String>> parIdList = ListUtils.partition(closeBuildPowerIdList,1000);
            parIdList.forEach(e->{
                BuildEmployeePowerListParam param = new BuildEmployeePowerListParam();
                param.setIdList(closeBuildPowerIdList);
                safetyBuildEmployeePowerService.deleteByExample(param,record);
            });
        }
    }

    /**
     *
     * @param empNo 员工号
     * @param buildList 楼宇权限
     * @param factoryIdStr 厂区
     */
    public void closeXkpEmpPower(String empNo,List<SafetyBuildEmployeePowerDO> buildList,String factoryIdStr){
        String powerEndDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        SafetyBuildIssuedLogDO safetyBuildIssuedLogDO = new SafetyBuildIssuedLogDO();
        String deviceCode = buildList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.joining(","));
        String deviceName = buildList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).collect(Collectors.joining(","));

        safetyBuildIssuedLogDO.setEmployeeCode(empNo);
        safetyBuildIssuedLogDO.setDoorId(factoryIdStr);
        safetyBuildIssuedLogDO.setDeviceCode(deviceCode);
        safetyBuildIssuedLogDO.setDeviceName(deviceName);
        safetyBuildIssuedLogDO.setIssuedType("关闭");
        safetyBuildIssuedLogDO.setBeginTime(powerEndDate);
        safetyBuildIssuedLogDO.setEndTime(powerEndDate);

        safetyBuildIssuedLogService.issuedBuildPower(safetyBuildIssuedLogDO);
    }

    /**
     * 根据修改内容保存记录
     * @param dto
     * @return
     */
    public int savePromisChange(SafetyOutsiderPromiseChangeDTO dto,String token,
                                SafetyOutsiderPromiseChangeDetailDTO oldDetail,
                                SafetyOutsiderPromiseChangeDetailDTO newDetail){
        String flowNo = dto.getFlowId();
        Map<String,String> userMap = TokenUtils.getTockenUser(token);
        String workNo = userMap.get("workNo");
        dto.setCreator(workNo);
        dto.setCreatorName(userMap.get("realName"));
        dto.setCreateTime(new Date());
        List<SafetyOutsiderPromiseChangeDetailDTO> detailDTOS = new ArrayList<>();
        detailDTOS.add(newDetail);

        SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getByNo(workNo);
        if(employeeMessageDO != null && !StringUtils.isEmpty(employeeMessageDO.getOrgNamePath())){
            dto.setCreatorDept(employeeMessageDO.getOrgNamePath());
        }

        List<SafetyOutsiderPromiseChangeDetailDTO> oldDetailDTOS = new ArrayList<>();
        oldDetailDTOS.add(oldDetail);
        safetyOutsiderPromiseChangeService.addDetailData(detailDTOS, flowNo);
        safetyOutsiderPromiseChangeService.addOldDetailData(oldDetailDTOS, flowNo);
        //判断是否涉密 根据ID获取工厂名称 包含 研发中心、工程试验中心
        StringBuilder factoryIds = new StringBuilder();
        for (SafetyOutsiderPromiseChangeDetailDTO detail: detailDTOS) {
            String factory = detail.getFactory();
            if(StringUtils.isEmpty(factory)){
                continue;
            }
            factoryIds.insert(0, factory + ",");
        }
        dto.setFlowNo(flowNo);
        dto.setFlowId(flowNo);
        dto.setStatus("31");

        return safetyOutsiderPromiseChangeService.addData(dto);
    }
}
