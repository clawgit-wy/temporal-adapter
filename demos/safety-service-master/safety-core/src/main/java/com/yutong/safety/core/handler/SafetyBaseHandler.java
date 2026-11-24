package com.yutong.safety.core.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.UpdatePhotoEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.EipWriteBackParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.*;
import com.yutong.safety.common.vo.competitor.CompetitorMangeVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.db.entity.SafetyVisitorHairpinDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.enter.result.SafetyEmpCardVO;
import com.yutong.safety.core.dto.*;
import com.yutong.safety.core.dto.build.SafetyBuildEmpPowerDTO;
import com.yutong.safety.core.dto.device.SafetyRegionDeviceInfoDTO;
import com.yutong.safety.core.handler.common.SafetyBaseCommonHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.device.SafetyRegionDeviceInfoService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.enter.SafetyEmpCardService;
import com.yutong.safety.core.service.faceUnqualified.SafetyFaceUnqualifiedService;
import okhttp3.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.yutong.safety.core.service.SafetyDataSendLogService.sendPost;


@Component
public class SafetyBaseHandler extends SafetyBaseCommonHandler {

    private static final String targetId="ABYJS";
    private static final String FLOW_STATUS_ABANDON = "00";
    //人事信息默认组织ID
    private static final String STAFF_ORGANTION_ID = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";
    private static final String CRK_EMP_CAR_RID = "C97611CE-C6EA-4582-9680-55934B79F9F7";
    private static final String CRK_EMP_WX_CAR_RID = "A4807A31-2DAA-462C-BC27-FAC3C2044720";
    private static final String CRK_EMP_CAR_GID = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";
    //车行默认组织ID
    private static final String CAR_DEFAULT_ORGANTION = "0D864B2A-A7BD-4511-94A2-A5AB6E453588";
    private static final String BUILD_ADMIN_ROLE = "门禁系统管理员,超级管理员";
    //车注销入参
    private static final String carParam ="{\"OperMoney\":150,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":0,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10车注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\",\"ProjectGids\":[\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";
    private static final String CRK_CAR_STCM = "2C3DB448-9918-4665-A4C1-C99797E2A4DD";

    @Resource
    private SafetyBaseService safetyBaseService;
    @Resource
    private SafetyEmpCardService safetyEmpCardService;
    @Resource
    private SafetySysUserService safetySysUserService;
    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;
    @Resource
    private SafetyRegionDeviceInfoService safetyRegionDeviceInfoService;
    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private WebserviceUtil webserviceUtil;
    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;
    @Resource
    private SafetyGroupService safetyGroupService;
    @Resource
    private SafetySysRoleService safetySysRoleService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyInterfaceLogHandler safetyInterfaceLogHandler;
    @Resource
    private SafetyLogMessageService safetyLogMessageService;
    @Resource
    private EipCommonService eipCommonService;
    @Resource
    private SafetyFaceUnqualifiedService safetyFaceUnqualifiedService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private YxtRebotUtils yxtRebotUtils;

    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${iam.updatePhoto.url}")
    private String updatePhotoUrl;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    @Value("${eip.flowLink}")
    private String eipFlowLink;

    @Value("${crk.QueryStaff.url}")
    private String crkQueryStaffUrl;
    @Value("${iam.getPhotoUrl.url}")
    String getPhotoUrl;
    @Value("${crk.IssueDoorView.url}")
    private String crkIssueDoorViewUrl;
    @Value("${crk.getIssueDoorView.custom.url}")
    private String crkGetDoorViewCustomUrl;
    @Value("${crk.IssueParkView.url}")
    private String carInFactoryInvehicleUrl;
    @Value("${iam.FaceModify.url}")
    String faceModifyUrl;
    @Value("${iam.FaceIssue.url}")
    String faceIssueUrl;
    @Value("${iam.CardModify.url}")
    String cardModifyUrl;
    @Value("${iam.apikey}")
    private String apikey;
    @Value("${crk.insertUrl}")
    private String crkInsertUrl;
    @Value("${crk.updateUrl}")
    private String crkUpdateUrl;
    @Value("${ehr.token}")
    String tokenUrl;
    @Value("${ehr.promise}")
    String promiseUrl;
    //出入口-根据员工号查询人事ID
    @Value("${crk.staff.getByCustom.url}")
    private String staffGetByCustomUrl;
    @Value("${crk.ParkUnregister.url}")
    private String parkUnregistertUrl;
    @Value("${crk.vehicle.staff.url}")
    private String crkVehicleSaffUrl;

    /**
     * OCR Url
     */
    @Value("${sg.ocr.idCard.url}")
    private String idCardUrl;
    /**
     * OCR appId
     */
    @Value("${sg.ocr.appId}")
    private String idCardAppId;

    /**
     * 供应商公司信息接口
     */
    @Value("${supply.company.url}")
    private String supplyCompanyUrl;

    @Value("${iam.sendMsgSms.url}")
    private String sendMsgSmsUrl;

    @Value("${iam.sendMsgSms.smsConfigId}")
    private String smsConfigId;

    @Value("${competitor.mange.query.url}")
    private String competitorMangeQueryUrl;

    @Value("${competitor.mange.confirm.url}")
    private String competitorMangeConfirmUrl;


    /**
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo) {
        return safetyBaseService.employeeConvertWorkNo(oldWorkNo);
    }

    /**
     * 获取照片 优先获取生活照,没有生活照,获取证件照
     * @param empNo 员工编号
     * @param type 照片类型：0证件照，1生活照
     * @return 照片的URL或路径
     */
    public String getPhoto(String empNo, int type){
        //获取员工照片
        JSONObject param = new JSONObject();
        param.put("targetId",targetId);
        param.put("workNo",empNo);
        param.put("photoType",type);
        HashMap headMap = new HashMap();
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
            if (response == null || response.body() == null) {
                throw new IOException("Response or response body is null");
            }
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
     * 下发车权限
     * @param issuedLogDO 日志记录
     * @param carDevices 车行设备拼接串
     * @param carNos 车辆信息
     * @return 返回下发状态
     */
    public boolean downCarPower(SafetyBuildIssuedLogDO issuedLogDO, String carDevices,
                                List<SafetyEmployeePrivateCarDO> carNos, String telephone,
                                String userType) {
        boolean result = false;
        if(CollectionUtils.isEmpty(carNos)){return false;}
        String tcm = CRK_EMP_CAR_RID;
        if("WX".equals(userType)){
            tcm = CRK_EMP_WX_CAR_RID;
        }
        for (SafetyEmployeePrivateCarDO carNoInfo : carNos) {
            boolean bool = false;
            String carNo = carNoInfo.getCarNo();
            String tcmName = "";
            String bodyParams = "\"TokenId='"+carNo+"'\"";
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");
            String endTime = DateUtils.format(DateUtils.parse(issuedLogDO.getEndTime(),1),1)+" 23:59:59";//结束日期
            //查询出入口是否有该辆车的进出厂记录
            String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);
            if (org.apache.commons.lang3.StringUtils.isEmpty(returnJson)) {
                LogUtils.info("出入口接口未找到{}的出入厂数据",carNo);
                continue;
            }else {
                com.alibaba.fastjson.JSONObject retObject = com.alibaba.fastjson.JSONObject.parseObject(returnJson);
                com.alibaba.fastjson.JSONObject retMap = retObject.getJSONObject("Model");
                if (retMap == null) {
                    bool = true;
                }else{
                    tcmName = retMap.getString("TcmName");
                }
            }
            //TODO 只修改员工车辆和外协车辆的权限
            if("".equals(tcmName)||"员工车辆".equals(tcmName) || "外协车辆".equals(tcmName)
                    || "运输车辆".equals(tcmName)|| "临时运输车辆".equals(tcmName)) {
                if (bool) {
                    if (!"false".equals(syncCrk(issuedLogDO, carDevices, carNoInfo, telephone, endTime,tcm))) {
                        result = true;
                    }
                } else {
                    if (!"false".equals(updateCrk(issuedLogDO, carDevices, carNoInfo, telephone, endTime,tcm))) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    /**
     * 下发车辆入场权限-sub
     * @param issuedLogDO 封装的日志DO
     * @param carDevices 车辆对应设备
     * @param carInfo 车辆信息
     * @return 返回值
     */
    public String syncCrk(SafetyBuildIssuedLogDO issuedLogDO, String carDevices,
                          SafetyEmployeePrivateCarDO carInfo,
                          String telephone,String endTime,String tcm) {
        OkHttpClient client = new OkHttpClient();
        String[] AuthDevice = carDevices.split(",");

        String carNo = carInfo.getCarNo();
        String carColor = carInfo.getCarColor();
        String carType = carInfo.getVehicleType();

        String StaffNo = issuedLogDO.getEmployeeCode();    //人员编号
        String StaffName = issuedLogDO.getEmployeeName();       //人员姓名
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "eippark";   //操作员姓名
        String OperNo = "eippark";     //操作员编号
        Integer ID = 0;         //操作编号
        String[] ProjectGids = new String[]{CRK_EMP_CAR_GID};    //项目编号集合
        Map<String,Object> map = new HashMap<>();

        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("TelphoneNo", telephone);
        map.put("Redate", Redate);
        map.put("OperName", OperName);
        map.put("OperNo", OperNo);
        map.put("OrganizationId", CAR_DEFAULT_ORGANTION);
        map.put("Gid", CRK_EMP_CAR_GID);
        map.put("ID", ID);
        map.put("ProjectGids", ProjectGids);
        map.put("VehicleBand", carType); //车辆型号
        map.put("VehicleColor", carColor); //车辆颜色

        map.put("TokenType", 0);//凭证类型（0 车牌 1 卡片 2 ETC 3纸票 4二维码 5 生物）
        map.put("IcNumber", "");//Ic号
        map.put("IdNumber", "");//Id号
        map.put("Deposit", 0);//办卡押金
        map.put("IsWrite", false);//输入/录入
        map.put("IsLegal", true);//是否合法

        com.alibaba.fastjson.JSONObject ParkIssue = new com.alibaba.fastjson.JSONObject();
        ParkIssue.put("StaffNo", StaffNo);
        ParkIssue.put("StaffName", StaffName);
        ParkIssue.put("TelphoneNo", telephone);
        ParkIssue.put("Redate", Redate);
        ParkIssue.put("OperName", OperName);
        ParkIssue.put("OperNo", OperNo);
        ParkIssue.put("OrginazitionId", CAR_DEFAULT_ORGANTION);//使用车行默认组织ID
        ParkIssue.put("Gid", CRK_EMP_CAR_GID);
        ParkIssue.put("ID", ID);
        ParkIssue.put("ProjectGids", ProjectGids);
        ParkIssue.put("TokenOper", 1);
        ParkIssue.put("AlreadyIn", false);
        ParkIssue.put("TokenId", carNo);//这个是关键值，出入口系统根据这个判断是否唯一车辆
        ParkIssue.put("SerialNo", carNo);
        ParkIssue.put("Tcm", tcm);
        ParkIssue.put("PayType", 0);
        ParkIssue.put("AccountBalance", 0);
        ParkIssue.put("BeginDate",DateUtils.format(new Date(),2));
        ParkIssue.put("EndDate",endTime);
        ParkIssue.put("AuthDevice", AuthDevice);
        ParkIssue.put("Token", carNo); //车牌号
        ParkIssue.put("VehicleColor", carColor); //车辆颜色
        ParkIssue.put("VehicleBand", carType); //车辆类型
        ParkIssue.put("TokenType", 0);
        ParkIssue.put("UseModel", 1);
        ParkIssue.put("Plate", carNo);

        map.put("ParkIssue", ParkIssue);

        String toCrkString = JSON.toJSONString(map);
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, toCrkString);
        Request request = new Request.Builder()
                .url(crkInsertUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("入厂请求同步-出入口系统 接口地址 {} \n 请求参数 {}", crkInsertUrl, toCrkString);

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response == null || response.body() == null) {
                throw new IOException("Response or response body is null");
            }
            String str = response.body().string();
            LogUtils.info("下发入厂请求同步-出入口系统新增 返回结果：" + str);
            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(str);
            if(obj.get("State") != null){
                //
                com.alibaba.fastjson.JSONObject stateObj = obj.getJSONObject("State");
                if(stateObj.get("IsSucess") != null){
                    String IsSucess = stateObj.get("IsSucess").toString();
                    LogUtils.info("下发入厂请求同步-出入口系统新增成功：" + IsSucess);
                    return IsSucess;
                }
            }
        } catch (IOException e) {
            LogUtils.info("大门门禁下发入厂请求同步-出入口系统新增调用失败:{};参数={}", e.getMessage(),ParkIssue);
        }
        return "false";
    }

    /**
     * 更新车辆入场权限-SUB
     * @param issuedLogDO 封装的日志DO
     * @param carDevices 车行设备拼接串
     * @param carInfo 车辆信息
     * @return 返回值
     */
    public String updateCrk(SafetyBuildIssuedLogDO issuedLogDO, String carDevices,
                            SafetyEmployeePrivateCarDO carInfo,
                            String telephone,String endTime,String tcm) {
        OkHttpClient client = new OkHttpClient();
        String[] AuthDevice = carDevices.split(",");
        String carNo = carInfo.getCarNo();
        String carColor = carInfo.getCarColor();
        String carType = carInfo.getVehicleType();

        LogUtils.info("厂区映射 AuthDevice={}", AuthDevice.length);

        String StaffNo = issuedLogDO.getEmployeeCode();    //人员编号
        String StaffName = issuedLogDO.getEmployeeName();       //人员姓名
        //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "eippark";   //操作员姓名
        String OperNo = "eippark";     //操作员编号

        String[] ProjectGids = new String[]{CRK_EMP_CAR_GID};    //项目编号集合
        Map<String,Object> map = new HashMap();

        map.put("TokenId", carNo);       //车牌号
        map.put("Token", carNo);        //车牌号
        map.put("Plate", carNo);        //车牌号
        map.put("VehicleColor", carColor); //车辆颜色
        map.put("VehicleBand", carType); //车辆类型
        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("Tcm", tcm); //车辆类型 有：运输车辆，临时运输车辆，员工车辆，公务车辆，外协车辆，领导车辆
        map.put("PayType", "0");
        map.put("AccountBalance", "0");
        map.put("TelphoneNo", telephone);        //车上第一个人的电话
        map.put("OrginazitionId", CAR_DEFAULT_ORGANTION);
        map.put("BeginDate", DateUtils.format(new Date(),2));
        map.put("EndDate", endTime);
        map.put("AuthDevice", AuthDevice);
        map.put("IsExistCard", false);
        map.put("TokenType", 0);
        map.put("UseModel", 1);
        map.put("TokenOper", 2);
        map.put("OperNo", OperNo);        //mobile eippark
        map.put("OperName", OperName);        //mobile eippark
        map.put("IsUpdateBeginDate", true);
        map.put("IsUpdateEndDate", true);
        map.put("Redate", Redate);
        map.put("Gid", CRK_EMP_CAR_GID);
        map.put("ProjectGids", ProjectGids);
        map.put("OldAccountBlace", 0);
        map.put("IsLegal", true);

        String toCrkString = JSON.toJSONString(map);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, toCrkString);
        Request request = new Request.Builder()
                .url(crkUpdateUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("入厂请求同步-出入口系统更新 接口地址 {} \n 请求参数 {}", crkUpdateUrl, toCrkString);

        Response response;
        try {
            response = client.newCall(request).execute();
            if (response == null || response.body() == null) {
                throw new IOException("Response or response body is null");
            }
            String str = response.body().string();
            LogUtils.info("更新入厂请求同步-出入口系统更新 返回结果 \n {}", str);
            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(str);
            if(obj.get("IsSucess") != null){
                String IsSucess = obj.get("IsSucess").toString();
                LogUtils.info("更新入厂请求同步-出入口系统更新成功：" + IsSucess);
                return IsSucess;
            }
        } catch (Exception e) {
            LogUtils.info("大门门禁更新接口调用失败：{};参数={}",e.getMessage(),toCrkString);
        }
        return "false";
    }

    /**
     * 更新卡权限
     * @param dto 封装的日志DO
     * @param type 类型更新/发行
     * @param empCardVO 出入口人事信息
     * @param deviceGidMap 富士项目及设备对应关系
     * @return 下发状态
     */
    public boolean updateCardData(SafetyBuildIssuedLogDO dto, String type,
                               SafetyEmpCardVO empCardVO,Map<String, List<String>> deviceGidMap) {
        AtomicBoolean issueFlag = new AtomicBoolean(false);
        String empNo = dto.getEmployeeCode();
        String empName = dto.getEmployeeName();
        //根据员工号获取员工token
        String returnValue = HttpFileUtil.sendPost(crkIssueDoorViewUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
        com.alibaba.fastjson.JSONObject retObject = com.alibaba.fastjson.JSONObject.parseObject(returnValue);
        Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
        if(!sucess){
            LogUtils.info("StaffNo={},未找到有效的卡TokenId",empNo);
            return false;
        }
        String endDate = DateUtils.format(DateUtils.parse(dto.getEndTime(),1),1);//结束日期
        com.alibaba.fastjson.JSONObject model = retObject.getJSONObject("Model");

        com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
        params.put("beginDate",DateUtils.format(new Date(),2));
        params.put("endDate",endDate+" 00:00:00");
        params.put("orginazitionId",empCardVO.getOrgId());
        params.put("serialNo",model.getString("SerialNo"));
        params.put("staffName",empName);
        params.put("staffNo",empNo);
        params.put("stcm",CRK_CAR_STCM);
        params.put("tcm",model.getString("TcmId")); //卡类型
        params.put("token",model.getString("TokenId"));
        params.put("tokenId",model.getString("TokenId"));
        params.put("rid", UUID.randomUUID());
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
            String checkGid = HttpFileUtil.sendPost(crkGetDoorViewCustomUrl, "{\"where\": \"StaffNo='"+empNo+"' and Gid='"+key+"' and TokenType=1 \"}");
            com.alibaba.fastjson.JSONObject checkGidObject = com.alibaba.fastjson.JSONObject.parseObject(checkGid);
            if(!ObjectUtils.isEmpty(checkGidObject)){
                com.alibaba.fastjson.JSONArray gidRecords = checkGidObject.getJSONArray("Records");
                if(CollectionUtils.isEmpty(gidRecords)){
                    params.put("tokenOper",1);
                }
            }
            com.alibaba.fastjson.JSONArray doorGrant = new com.alibaba.fastjson.JSONArray();
            if(value != null && !value.isEmpty()){
                for(String deviceModelStr: value){
                    com.alibaba.fastjson.JSONObject deviceModel = com.alibaba.fastjson.JSONObject.parseObject(deviceModelStr);
                    com.alibaba.fastjson.JSONObject doorGrantMap = new com.alibaba.fastjson.JSONObject();
                    doorGrantMap.put("collectCode", deviceModel.getString("collectCode"));
                    doorGrantMap.put("d1Grant", 1);
                    doorGrantMap.put("d1OpenMode", 1);
                    doorGrantMap.put("d1Valid", 0);
                    doorGrantMap.put("devName", deviceModel.getString("DevName"));
                    doorGrantMap.put("devNo", deviceModel.getString("DevNo"));
                    doorGrantMap.put("tokenId", model.getString("TokenId"));
                    /*设备设为动态获取 解决人脸和卡两用设备，卡权限授权时下发参数中设备类型要设置为31才生效 */
                    String devTypeCode = deviceModel.getString("DevTypeCode");
                    if(org.apache.commons.lang3.StringUtils.isBlank(devTypeCode)){
                        devTypeCode = "00";
                    }
                    //给697人脸调用卡片发行接口发行 "DevTypeCode": "31", 时新增参数 "CollectCode": "20",（多加个这个）
                    if("31".equals(devTypeCode)){
                        doorGrantMap.put("CollectCode","20");
                    }
                    doorGrantMap.put("devTypeCode",devTypeCode);
                    doorGrantMap.put("gid", deviceModel.getString("Gid"));
                    doorGrantMap.put("rid", UUID.randomUUID());
                    doorGrant.add(doorGrantMap);
                }
                params.put("doorGrant",doorGrant);
                LogUtils.info("updateCardData的card修改,请求参数={}" , params);
                Map<String,Object> retVal = this.handleRequestT(cardModifyUrl, params,empNo,empName);
                boolean retStatus = (boolean) retVal.get("issuedStats");
                if(retStatus){
                    issueFlag.set(retStatus);
                }
                LogUtils.info("updateCardData的card修改name={}返回消息={}" , empName ,retVal.get("errorMsg"));
            }
        });
        return issueFlag.get();
    }

    /**
     * 人脸发行
     * @param dto 封装的日志DO
     * @param empCardVO 出入口人事信息
     * @param deviceGidMap 设备与项目关系集合
     * @param lifePhoto 生活照地址
     * @return 下发状态
     */
    public boolean updateFaceData(SafetyBuildIssuedLogDO dto, SafetyEmpCardVO empCardVO,
                               Map<String, List<String>> deviceGidMap,String lifePhoto) {
        AtomicBoolean issueFlag = new AtomicBoolean(false);
        String empNo = dto.getEmployeeCode();
        String empName = dto.getEmployeeName();
        byte[] imageBytes = getPictureDataInputStream(lifePhoto);
        String base64_str = DatatypeConverter.printBase64Binary(imageBytes);

        String dateTime = dto.getBeginTime();
        if(StringUtils.isEmpty(dateTime)){
            dateTime = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        }
        String beginDate = DateUtils.format(DateUtils.parse(dateTime,1),1);//开始日期
        if(!StringUtils.isEmpty(dto.getBeginTime())){
            beginDate = dto.getBeginTime().substring(0,10);
        }
        String endDate = DateUtils.format(DateUtils.parse(dto.getEndTime(),1),1);//结束日期

        String finalBeginDate = beginDate;
        deviceGidMap.forEach((key, value) -> {
            if(!value.isEmpty()){
                com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
                com.alibaba.fastjson.JSONObject Staff = new com.alibaba.fastjson.JSONObject();
                Staff.put("StaffNo", empNo);//员工号
                Staff.put("StaffName", empName); //姓名
                Staff.put("OrganizationId", empCardVO.getOrgId());
                Staff.put("Gid",key);
                params.put("BeginTime", finalBeginDate +" 00:00:00");//开始日期
                params.put("EndTime",endDate+" 23:59:59");
                params.put("AuthorizeDeviceNo", value);
                String[] ProjectGids = new String[]{key};
                params.put("ProjectGids", ProjectGids);
                params.put("Staff", Staff);
                params.put("Gid", key);
                params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
                LogUtils.info("updateFaceData人脸修改请求参数={}" , params.toJSONString());
                params.put("FacePicture", base64_str);

                Map<String,Object> requestReturn = handleRequest(faceModifyUrl,params,empName,empNo);
                String errorMsg = (String) requestReturn.get("errorMsg");
                boolean modifyStats = (boolean) requestReturn.get("issuedStats");
                if(!modifyStats && (errorMsg.contains("未授权")|| errorMsg.contains("无门禁发行记录"))){
                    /*人脸更新提示未授权时，进行人脸发行操作 250721 */
                    params.put("OperType", UpdatePhotoEnum.FACEISSUE.getCode());
                    //执行卡发行
                    Map<String,Object> issueReturn = handleRequest(faceIssueUrl,params,empName,empNo);
                    boolean issuedStats = (boolean) issueReturn.get("issuedStats");
                    if(!issuedStats){
                        LogUtils.info("updateFaceData人脸发行失败name={}返回消息={}" , empName ,issueReturn.get("errorMsg"));
                    }else{
                        issueFlag.set(issuedStats);
                    }
                } else {
                    issueFlag.set(modifyStats);
                }
            }
        });
        return issueFlag.get();
    }

    /**
     * 处理请求
     * @param postUrl 请求URL
     * @param params 提交参数
     * @param name 员工姓名
     * @param workNo 员工号
     * @return 返回值
     */
    public Map<String,Object> handleRequest(String postUrl,JSONObject params,String name,String workNo){
        boolean issuedStats = true;
        Map<String,Object> retMap = new HashMap<>();
        String r4 = HttpFileUtil.sendPost(postUrl, params.toJSONString());
        LogUtils.info("权限下发name={},workNo={},返回消息={}" , name , workNo, r4);

        if (org.apache.commons.lang3.StringUtils.isBlank(r4)) {
            issuedStats = false;
        }else{
            JSONObject json4 = JSONObject.parseObject(r4);
            if(!"200".equals(json4.get("State")+"")){
                issuedStats = false;
            }
        }
        retMap.put("errorMsg",r4);
        retMap.put("issuedStats",issuedStats);
        return retMap;
    }

    /**
     * 处理请求
     * @param postUrl 请求URL
     * @param params 提交参数
     * @param name 员工姓名
     * @param workNo 员工号
     * @return 返回值
     */
    public Map<String,Object> handleRequestT(String postUrl,JSONObject params,String name,String workNo){
        Map<String,Object> retMap = new HashMap<>();
        String response = HttpFileUtil.sendPost(postUrl, params.toJSONString());
        LogUtils.info("卡权限下发name={},workNo={},返回消息={}" , name , workNo, response);

        boolean issuedStats = false;

        if (!StringUtils.isEmpty(response)) {
            try {
                JSONObject respJson = JSONObject.parseObject(response);
                if (respJson.containsKey("Model")) {
                    JSONObject model = respJson.getJSONObject("Model");
                    if (model != null && model.getBooleanValue("Result")) {
                        issuedStats = true;
                    }
                }
            } catch (JSONException e) {
                LogUtils.error("卡返回值解析失败：", response);
            }
        }

        retMap.put("errorMsg", response);
        retMap.put("issuedStats", issuedStats);
        return retMap;
    }

    /**
     * 根据厂区编码获取厂区名称
     * @param paramDTO 赋值对象
     */
    public void getParkNameBYCode(ParamDTO paramDTO, Map<Integer, String> map) {
        try {
            // 厂区编码不为空并且厂区名称为空的时候才做处理
            String factoryId = paramDTO.getFactoryId();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(factoryId)) {
                // 厂区编码字符串转List
                List<String> list = Arrays.asList(factoryId.split(","));
                List<String> nameList = new ArrayList<>();
                // 根据字符串取出对应的厂区编码
                list.forEach(li -> {
                    String name = map.get(Integer.parseInt(li));
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
                        nameList.add(name);
                    }
                });
                // 赋值
                paramDTO.setFactoryName(String.join(",", nameList));
            }
        } catch (Exception e) {
            LogUtils.info("getParkNameBYCode:员工号：{}，厂区编码：{}，，根据厂区编码获取厂区名称异常！", paramDTO.getEmpNo(), map.keySet());
        }
    }

    /**
     * 获取组织ID和 curId
     * @param empCardVO 入参
     */
    public SafetyEmpCardVO generalCrkOrgId(SafetyEmpCardVO empCardVO) {
        //TODO 增加从出入口查询的逻辑
        if(empCardVO == null || StringUtils.isEmpty(empCardVO.getCrkId())){
            return null;
        }
        String crkId = empCardVO.getCrkId();
        if (StringUtils.isEmpty(empCardVO.getCrkId())) {
            // 没有值，则提供默认值
            empCardVO.setOrgId(STAFF_ORGANTION_ID);
            empCardVO.setCurRid(UUID.randomUUID().toString());
        } else {
            // 调用出入口接口获取组织ID
            Map<String,String> crkEmpMap = queryCrkEmpInfo(crkId);
            String orgid = crkEmpMap.get("orgid");
            String curId = crkEmpMap.get("rid");
            if (org.apache.commons.lang3.StringUtils.isBlank(orgid)) {
                // 为空赋值为默认的值
                empCardVO.setOrgId(STAFF_ORGANTION_ID);
            } else {
                empCardVO.setOrgId(orgid);
            }
            if(org.apache.commons.lang3.StringUtils.isBlank(curId)){
                empCardVO.setCurRid(UUID.randomUUID().toString());
            } else {
                empCardVO.setCurRid(curId);
            }
        }
        return empCardVO;
    }

    /**
     * 根据出入口人事ID查询组织ID
     * @param crkId 人事ID
     * @return 查询结果
     */
    public Map<String,String> queryCrkEmpInfo(String crkId){
        Map<String,String>  retMap = new HashMap<>();
        try {
            String getRet = webserviceUtil.sendHttpForGet(crkQueryStaffUrl + "/" + crkId);
            JSONObject retObj = JSONObject.parseObject(getRet);
            if (retObj != null && retObj.getJSONObject("Model") != null) {
                String orgid = retObj.getJSONObject("Model").getString("OrganizationId");
                String rid = retObj.getJSONObject("Model").getString("rid");
                retMap.put("orgid",orgid);
                retMap.put("rid",rid);
            } else {
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"查询人事{}失败\"}", crkId);
            }
            return retMap;
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-查询人事异常crkId{}\"}",crkId);
            LogUtils.error("出入口-查询人事异常,{}",e);
            return retMap;
        }
    }

    /**
     * 获取出入口楼宇门禁权限
     * @return 返回查询结果
     */
    public JSONObject getCrkBuildPower(String empNos,String pageSize,String curPageNo){
        String curDate = DateUtils.format(new Date(),1);
        //查询截止日期>当前时间，且按员工分组ID最大（最新）的记录
        StringBuilder whereSql = new StringBuilder(" state=1 and Id in (SELECT MAX(ID) FROM IssueDoorView GROUP BY TokenId,Tokentype,StaffNo,Gid) ");
        whereSql.append(" and EndDate>='").append(curDate).append("' ");
        if(!StringUtils.isEmpty(empNos)){
            empNos = empNos.replace(",","','");
            whereSql.append(" and StaffNo in ('").append(empNos).append("') ");
        }
        JSONObject param = new JSONObject();
        param.put("where",whereSql.toString());
        if(StringUtils.isEmpty(pageSize)){
            pageSize = "500";
        }
        if(StringUtils.isEmpty(curPageNo)){
            curPageNo = "1";
        }
        param.put("PageSize",pageSize);  //设置每页的条数
        param.put("CurrentPage",curPageNo); //设置检索的页码
        LogUtils.info("入参：{}",param.toJSONString());
        try {
            String returnStr = HttpFileUtil.sendPost(crkGetDoorViewCustomUrl, param.toJSONString());
            if (StringUtils.isEmpty(returnStr)) {
                LogUtils.info("获取CRK门禁参数返回{}", returnStr);
                return new JSONObject();
            }
            return JSONObject.parseObject(returnStr);
        }catch (Exception e){
            LogUtils.error("查询富士门禁权限接口异常",e);
        }
        return new JSONObject();
    }

    /**
     *
     * @param jsonArray 门禁权限集合
     * @return
     */
    public List<SafetyBuildEmpPowerDTO> formatBuildEmpPowerDTO(JSONArray jsonArray){
        List<SafetyBuildEmpPowerDTO> dtoList = new ArrayList<>();
        if(jsonArray == null || jsonArray.isEmpty()){
            return dtoList;
        }
        for(int i=0;i<jsonArray.size();i++){
            SafetyBuildEmpPowerDTO dto = new SafetyBuildEmpPowerDTO();
            JSONObject curObj = jsonArray.getJSONObject(i);
            String authDevice = curObj.getString("AuthDevice"); //设备授权
            String staffNo = curObj.getString("StaffNo"); //员工号
            if(StringUtils.isEmpty(authDevice) || StringUtils.isEmpty(staffNo)){
                break;
            }
            String beginDate = curObj.getString("BeginDate").replace("T"," ");
            String endDate = curObj.getString("EndDate").replace("T"," ");
            Set<String> authDeviceSet = PinYinUtils.formatBinaryStr(authDevice);
            if(authDeviceSet.isEmpty()){
                break;
            }
            dto.setDeviceSet(authDeviceSet);
            dto.setEmployeeCode(staffNo);
            dto.setBeginTime(beginDate);
            dto.setEndTime(endDate);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<SafetyBuildEmployeePowerDO> formatEmpDataForUpdate(Map<String, SafetyBuildDeviceDO> deviceMap,
                                                                   List<SafetyBuildEmpPowerDTO> buildEmpPowerDTOS,
                                                                   Map<String, SafetyEmployeeMessageDO> employeeMap){

        List<SafetyBuildEmployeePowerDO> retList = new ArrayList<>();
        Map<String,List<SafetyBuildEmpPowerDTO>> buildEmpPowerMap = buildEmpPowerDTOS.stream()
                .collect(Collectors.groupingBy(SafetyBuildEmpPowerDTO::getEmployeeCode));
        //员工门禁权限
        buildEmpPowerMap.forEach((key,value)->{
            if(value.isEmpty()){
                LogUtils.info("员工{}，富士权限为空，不处理",key);
                return;
            }
            //员工台账中不存在此员工
            SafetyEmployeeMessageDO employee = employeeMap.get(key);
            if(employee == null){
                LogUtils.info("员工{}不存在，权限集合为{}",key,value.stream()
                        .map(SafetyBuildEmpPowerDTO::getDeviceSet).collect(Collectors.toSet()));
                return;
            }
            String empName = employee.getEmpName();
            Set<String> allDevices = new HashSet<>();
            for(SafetyBuildEmpPowerDTO dto:value){
                allDevices.addAll(dto.getDeviceSet());
            }
            String beginTime = value.get(0).getBeginTime();
            String endTime = value.get(0).getEndTime();

            //设备
            allDevices.forEach(val->{
                SafetyBuildDeviceDO curDevice = deviceMap.get(val);
                if(curDevice == null){
                    return;
                }
                SafetyBuildEmployeePowerDO curBuildPower = new SafetyBuildEmployeePowerDO();
                curBuildPower.setEmployeeCode(key);
                curBuildPower.setEmployeeName(empName);
                curBuildPower.setDeviceCode(curDevice.getDeviceCode());
                curBuildPower.setDeviceName(curDevice.getAnDeviceName());
                curBuildPower.setBeginTime(beginTime);
                curBuildPower.setEndTime(endTime);
                curBuildPower.setCreateBy("system");
                curBuildPower.setDeleteFlag("0");
                curBuildPower.setDeviceType("2"); //富士
                curBuildPower.setRemark("富士权限初始化");

                retList.add(curBuildPower);
            });
        });
        return retList;
    }

    /**
     * 获取组织ID和 curId
     * @param empNo 员工 ID
     * @return 返回组织ID和 curId left:组织ID right：curId
     */
    public Pair<String, Object> generalOrgId(String empNo) {
        // 1.获取人事ID 如果获取失败返回默认信息
        SafetyEmpCard safetyEmpCard = safetyEmpCardService.selectCrkIdByEmpNo(empNo);
        if (safetyEmpCard == null || org.apache.commons.lang3.StringUtils.isEmpty(safetyEmpCard.getCrkId()))
            return Pair.of(STAFF_ORGANTION_ID, UUID.randomUUID());
        String crkId = safetyEmpCard.getCrkId();

        // 2.调用出入口接口获取组织ID
        Map<String,String> crkEmpMap = queryCrkEmpInfo(crkId);
        String orgid = crkEmpMap.get("orgid");
        String curId = crkEmpMap.get("rid");
        if(org.apache.commons.lang3.StringUtils.isBlank(orgid)){
            // 为空赋值为默认的值
            orgid = STAFF_ORGANTION_ID;
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(curId)){
            return Pair.of(orgid, UUID.randomUUID());
        }
        return Pair.of(orgid, curId);
    }

    /**
     * 根据厂区编码获取厂区名称
     * @param factoryIds 赋值对象
     */
    public String getParkNameBYCode(String factoryIds, Map<Integer, String> factoryMap) {
        try {
            // 厂区编码不为空并且厂区名称为空的时候才做处理
            if (!StringUtils.isEmpty(factoryIds)) {
                // 厂区编码字符串转List
                List<String> list = Arrays.asList(factoryIds.split(","));
                List<String> nameList = new ArrayList<>();
                // 根据字符串取出对应的厂区编码
                list.forEach(li -> {
                    String name = factoryMap.get(Integer.parseInt(li));
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
                        nameList.add(name);
                    }
                });
                return String.join(",", nameList);
            }
        } catch (Exception e) {
            LogUtils.info("getParkNameBYCode:根据厂区编码{}获取厂区名称异常！",factoryIds);
        }
        return "";
    }

    /**
     * 校验当前用户的类型 1-员工 2-管理员 3-绝密负责人
     * @param token 登录人令牌
     * @return 返回值
     */
    public String checkUserType(String token){
        String roleType = "1"; //普通用户
        boolean isAdmin = safetySysUserService.checkRole(token,BUILD_ADMIN_ROLE);
        if(isAdmin){
            return "2"; //管理员
        }
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(TokenUtils.getWorkNo(token));
        List<SafetyBuildPowerDO> manePowerList = safetyBuildPowerService.selectList(listParam);
        if(CollectionUtils.isEmpty(manePowerList)){
            return roleType;
        }
        String dutyDeptCode = manePowerList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).distinct().collect(Collectors.joining(";"));
        roleType = "3"+":"+dutyDeptCode;
        return roleType;
    }

    public void updateFaceData(SafetyBuildIssuedLogDO dto, String empDevice,Pair<String, Object> pair) {
        String empNo = dto.getEmployeeCode();
        String empName = dto.getEmployeeName();
        //获取生活照
        String lifePhoto = getPhoto(empNo, 1);
        if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
            //获取证件照
            lifePhoto = getPhoto(empNo, 0);
        }
        //未获取到照片
        if(org.springframework.util.StringUtils.isEmpty(lifePhoto)){
            LogUtils.info("{}获取员工人脸照片失败!",empNo);
            return;
        }
        //图片转化为二进制
        lifePhoto = lifePhoto + "&apikey=" + apikey;
        byte[] imageBytes = getPictureDataInputStream(lifePhoto);
        String base64_str = DatatypeConverter.printBase64Binary(imageBytes);

        String dateTime = dto.getBeginTime();
        if(StringUtils.isEmpty(dateTime)){
            dateTime = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        }
        String beginDate = DateUtils.format(DateUtils.parse(dateTime,1),1);//开始日期
        String endDate = DateUtils.format(DateUtils.parse(dto.getEndTime(),1),1);//结束日期

        Map<String, List<String>> deviceGidMap = getDeviceGidMapFromAb(empDevice);
        deviceGidMap.forEach((key, value) -> {
            if(!value.isEmpty()){
                com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
                com.alibaba.fastjson.JSONObject Staff = new com.alibaba.fastjson.JSONObject();
                Staff.put("StaffNo", empNo);//员工号
                Staff.put("StaffName", empName); //姓名
                Staff.put("OrganizationId", pair.getLeft());
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
                LogUtils.info("issuedBuildPowerForProcessForTwoFs人脸修改请求参数={}" , params);
                params.put("FacePicture", base64_str);

                Map<String,Object> requestReturn = handleRequest(faceModifyUrl,params,empName,empNo);
                String errorMsg = (String) requestReturn.get("errorMsg");
                LogUtils.info("issuedBuildPowerForProcessForTwoFs人脸修改name={}返回消息={}" , empName ,errorMsg);

                boolean modifyStats = (boolean) requestReturn.get("issuedStats");
                if(!modifyStats && (errorMsg.contains("未授权")|| errorMsg.contains("无门禁发行记录"))){
                    /*人脸更新提示未授权时，进行人脸发行操作 250721 */
                    params.put("OperType", UpdatePhotoEnum.FACEISSUE.getCode());
                    //执行卡发行
                    Map<String,Object> issueReturn = handleRequest(faceIssueUrl,params,empName,empNo);
                    boolean issuedStats = (boolean) issueReturn.get("issuedStats");
                    if(!issuedStats){
                        LogUtils.info("issuedBuildPowerForProcessForTwoFs人脸发行失败name={}返回消息={}" , empName ,issueReturn.get("errorMsg"));
                    }
                }
            }
        });
    }

    public boolean updateCardData(SafetyBuildIssuedLogDO dto, String empDevice, String type,Pair<String, Object> pair) {
        AtomicBoolean issueFlag = new AtomicBoolean(false);
        String empNo = dto.getEmployeeCode();
        String empName = dto.getEmployeeName();
        //根据设备号,获取所属gid 按照项目下发
        Map<String, List<String>> deviceGidMap = getDeviceGidMapModelFromAb(empDevice);
        //根据员工号获取员工token
        String returnValue = HttpFileUtil.sendPost(crkIssueDoorViewUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
        com.alibaba.fastjson.JSONObject retObject = com.alibaba.fastjson.JSONObject.parseObject(returnValue);
        Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
        if(sucess){
            String endDate = DateUtils.format(DateUtils.parse(dto.getEndTime(),1),1);//结束日期
            com.alibaba.fastjson.JSONObject model = retObject.getJSONObject("Model");

            com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
            params.put("beginDate",DateUtils.format(new Date(),2));
            params.put("endDate",endDate+" 00:00:00");
            params.put("orginazitionId",pair.getLeft());
            params.put("serialNo",model.getString("SerialNo"));
            params.put("staffName",empName);
            params.put("staffNo",empNo);
            params.put("stcm",CRK_CAR_STCM);
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
                String checkGid = HttpFileUtil.sendPost(crkGetDoorViewCustomUrl, "{\"where\": \"StaffNo='"+empNo+"' and Gid='"+key+"' and TokenType=1 \"}");
                com.alibaba.fastjson.JSONObject checkGidObject = com.alibaba.fastjson.JSONObject.parseObject(checkGid);
                if(!ObjectUtils.isEmpty(checkGidObject)){
                    com.alibaba.fastjson.JSONArray gidRecords = checkGidObject.getJSONArray("Records");
                    if(CollectionUtils.isEmpty(gidRecords)){
                        params.put("tokenOper",1);
                    }
                }
                com.alibaba.fastjson.JSONArray doorGrant = new com.alibaba.fastjson.JSONArray();
                if(value != null && !value.isEmpty()){
                    for(String deviceModelStr: value){
                        com.alibaba.fastjson.JSONObject deviceModel = com.alibaba.fastjson.JSONObject.parseObject(deviceModelStr);
                        com.alibaba.fastjson.JSONObject doorGrantMap = new com.alibaba.fastjson.JSONObject();
                        doorGrantMap.put("collectCode", deviceModel.getString("collectCode"));
                        doorGrantMap.put("d1Grant", 1);
                        doorGrantMap.put("d1OpenMode", 1);
                        doorGrantMap.put("d1Valid", 0);
                        doorGrantMap.put("devName", deviceModel.getString("DevName"));
                        doorGrantMap.put("devNo", deviceModel.getString("DevNo"));
                        doorGrantMap.put("tokenId", model.getString("TokenId"));
                        /*设备设为动态获取 解决人脸和卡两用设备，卡权限授权时下发参数中设备类型要设置为31才生效 */
                        String devTypeCode = deviceModel.getString("DevTypeCode");
                        if(org.apache.commons.lang3.StringUtils.isBlank(devTypeCode)){
                            devTypeCode = "00";
                        }
                        //给697人脸调用卡片发行接口发行 "DevTypeCode": "31", 时新增参数 "CollectCode": "20",（多加个这个）
                        if("31".equals(devTypeCode)){
                            doorGrantMap.put("CollectCode","20");
                        }
                        doorGrantMap.put("devTypeCode",devTypeCode);
                        doorGrantMap.put("gid", deviceModel.getString("Gid"));
                        doorGrantMap.put("rid", UUID.randomUUID());
                        doorGrant.add(doorGrantMap);
                    }
                    params.put("doorGrant",doorGrant);
                    LogUtils.info("issuedBuildPowerForProcessForTwoFs的card修改,请求参数={}" , params);
                    String modifyResult = HttpFileUtil.sendPost(cardModifyUrl, params.toJSONString());
                    LogUtils.info("issuedBuildPowerForProcessForTwoFs的card修改name={}返回消息={}" , empName ,modifyResult);
                    if(!org.springframework.util.StringUtils.isEmpty(modifyResult)){
                        com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject.parseObject(modifyResult);
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

    /**
     * 处理设备与项目关系-用于脸和车辆权限
     * @param devices 设备串
     * @return 项目，设备编码集合 的Map对象
     */
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

    /**
     * 处理设备与项目关系-用于员工卡权限更新和下发
     * @param devices 设备串
     * @return 项目，设备对象集合 的Map对象
     */
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
                LogUtils.error("异常代码："+ResponseEnum.EXCEPTION.getCode()+"，获取项目及设备关系异常："+e.getMessage());
                LogUtils.error("getDeviceGidMapModelFromAb处理设备信息的时候出错了，信息={}",e);
            }
        }
        return deviceGidMap;
    }

    /**
     * 外协回写EHR
     * @param list 外协员工集合
     */
    public void sendToEHR(List<SafetyOutsiderPromiseDTO> list) {
        //获取EHR的Token
        String token = safetyBaseService.getEhrToken();
        JSONObject dataHeader = new JSONObject();
        dataHeader.put("Content-Type","application/json");
        dataHeader.put("Authorization", "Bearer "+token);

        JSONObject dataParam = new JSONObject();
        dataParam.put("headerParamMap",new HashMap<>());
        dataParam.put("requestParamMap",new HashMap<>());
        JSONObject pathVariableMap = new JSONObject();
        pathVariableMap.put("tenantId","0");
        dataParam.put("pathVariableMap",pathVariableMap);

        JSONArray payload = new JSONArray();
        for (SafetyOutsiderPromiseDTO dto:list) {
            JSONObject data = new JSONObject();
            data.put("hhrEmpid", dto.getEmpNo());
            //根据名称获取厂区ID
            if(!StringUtils.isEmpty(dto.getFactory())){
                String factoryCodes = safetyOutsiderPromiseChangeService.findFactoryCodes(dto.getFactory());
                data.put("hhrAccessSite", factoryCodes);
            }
            data.put("hhrEmployInitiator", dto.getCreatorNo());
            data.put("hhrResidentDate", dto.getServiceEndDate());
            payload.add(data);
        }
        LogUtils.info("外包外协权限变更回传EHRpayload={}",payload);
        dataParam.put("payload",payload.toString());
        String dataResult = webserviceUtil.sendHttp(promiseUrl, "2", dataParam.toString(), dataHeader, "POST");
        JSONObject dataObj = JSONObject.parseObject(dataResult);
        if(!ObjectUtils.isEmpty(dataObj) && "200".equals(dataObj.getString("status"))){
            String retPayload = dataObj.getString("payload");
            LogUtils.info("外包外协权限变更回传EHR结果={}",retPayload);
        }
    }

    /**
     * 外协厂区回写EHR
     * @param list 外协员工集合
     */
    public void sendToEHRForFactory(List<SafetyOutsiderPromiseDTO> list) {
        JSONObject tokenParam = new JSONObject();
        tokenParam.put("grant_type","client_credentials");
        tokenParam.put("client_id","eip");
        tokenParam.put("client_secret","eip@123");
        String tokenResult = webserviceUtil.sendHttp(tokenUrl, "1", tokenParam.toString(), new HashMap(), "POST");
        JSONObject tokenObj = JSONObject.parseObject(tokenResult);
        String token = tokenObj.getString("access_token");

        JSONObject dataHeader = new JSONObject();
        dataHeader.put("Content-Type","application/json");
        dataHeader.put("Authorization", "Bearer "+token);

        JSONObject dataParam = new JSONObject();
        dataParam.put("headerParamMap",new HashMap<>());
        dataParam.put("requestParamMap",new HashMap<>());
        JSONObject pathVariableMap = new JSONObject();
        pathVariableMap.put("tenantId","0");
        dataParam.put("pathVariableMap",pathVariableMap);

        JSONArray payload = new JSONArray();
        for (SafetyOutsiderPromiseDTO dto:list) {
            JSONObject data = new JSONObject();
            data.put("hhrEmpid", dto.getEmpNo());
            //根据名称获取厂区ID
            if(StringUtils.isEmpty(dto.getFactory())){
                LogUtils.info("员工{}的厂区为{}，不进行回写",dto.getEmpNo(),dto.getFactory());
                continue;
            }
            String factoryCodes = safetyOutsiderPromiseChangeService.findFactoryCodes(dto.getFactory());
            data.put("hhrAccessSite", factoryCodes);
            //对接人和服务截止日期置空字符串，EHR接收到空字符时不更新对应字段
            data.put("hhrEmployInitiator", "");
            data.put("hhrResidentDate", "");
            payload.add(data);
        }
        //无有效数据时，不回写
        if(payload.size()<=0){
            return;
        }
        LogUtils.info("外包外协权限变更回传EHRpayload={}",payload);
        dataParam.put("payload",payload.toString());
        String dataResult = webserviceUtil.sendHttp(promiseUrl, "2", dataParam.toString(), dataHeader, "POST");
        JSONObject dataObj = JSONObject.parseObject(dataResult);
        if(!ObjectUtils.isEmpty(dataObj) && "200".equals(dataObj.getString("status"))){
            String retPayload = dataObj.getString("payload");
            LogUtils.info("外包外协权限变更回传EHR结果={}",retPayload);
        }
    }

    /**
     * 根据员工号查询出入口中对呀的人事ID
     * @param empNo 员工号
     * @return 返回
     */
    public String getCrkStaffId(String empNo){
        try {
            JSONObject param = new JSONObject();
            if(StringUtils.isEmpty(empNo)){
                return "";
            }
            param.put("where","staffno ='"+empNo+"'");

            String postRet = HttpFileUtil.sendPost(staffGetByCustomUrl, param.toJSONString());
            JSONObject retObj = JSONObject.parseObject(postRet);
            JSONArray records = retObj.getJSONArray("Records");
            if (records != null && records.size()>0) {
                JSONObject oneRecord = records.getJSONObject(0);
                return oneRecord.getInteger("ID").toString();
            } else {
                LogUtils.error("出入口-获取员工{}人事信息异常，{}",empNo,postRet);
            }
            return "";
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            LogUtils.error("出入口-获取员工"+empNo+"人事信息异常,{}",e);
            return "";
        }
    }

    /**
     * 根据员工号查询出入口中对呀的人事信息
     * @param empNo 员工号
     * @return 返回
     */
    public SafetyEmpCardVO getCrkStaffInfo(String empNo){
        SafetyEmpCardVO empCardVO = new SafetyEmpCardVO();
        empCardVO.setEmpNo(empNo);
        try {
            empCardVO.setOrgId(STAFF_ORGANTION_ID);
            empCardVO.setCurRid(UUID.randomUUID().toString());

            JSONObject param = new JSONObject();
            if(StringUtils.isEmpty(empNo)){
                return empCardVO;
            }
            param.put("where","staffno ='"+empNo+"'");

            String postRet = HttpFileUtil.sendPost(staffGetByCustomUrl, param.toJSONString());
            JSONObject retObj = JSONObject.parseObject(postRet);
            JSONArray records = retObj.getJSONArray("Records");
            if (records != null && records.size()>0) {
                JSONObject oneRecord = records.getJSONObject(0);
                String orgId = oneRecord.getString("OrganizationId");
                if(!StringUtils.isEmpty(orgId)){
                    empCardVO.setOrgId(orgId);
                }
                String curId = oneRecord.getString("Rid");
                if(!StringUtils.isEmpty(curId)){
                    empCardVO.setCurRid(curId);
                }
                String crkId = oneRecord.getInteger("ID").toString();
                empCardVO.setCrkId(crkId);

            } else {
                LogUtils.error("出入口-获取员工{}人事信息异常，{}",empNo,postRet);
            }
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            LogUtils.error("出入口-获取员工"+empNo+"人事信息异常,{}",e);
        }
        return empCardVO;
    }

 /**
     * 分批获取出入口人事信息 (300 条数据每次)
     * @param empNos 员工号
     * @return 返回人事信息
     */
    public List<SafetyEmpCardVO> getCrkStaffs(List<String> empNos) {
        if (CollectionUtils.isEmpty(empNos)) {
            return new ArrayList<>();
        }
        List<List<String>> partition = ListUtils.partition(empNos,300);
        // 1.获取人事ID,组织ID和rid 如果获取失败返回默认信息
        List<SafetyEmpCardVO> list = new ArrayList<>();
        partition.forEach(e->{
            List<SafetyEmpCardVO> subList = this.getCrkStaffs(String.join(",", empNos));
            list.addAll(subList);
        });
        return list;
    }

    /**
     * 根据员工号查询出入口中对呀的人事ID
     * @param empNos 员工号
     * @return 返回
     */
    public List<SafetyEmpCardVO> getCrkStaffs(String empNos){
        List<SafetyEmpCardVO> retList = new ArrayList<>();
        try {
            JSONObject param = new JSONObject();
            if(StringUtils.isEmpty(empNos)){
                return retList;
            }
            param.put("where","staffno in('"+empNos.replaceAll(",","','")+"')");

            String postRet = HttpFileUtil.sendPost(staffGetByCustomUrl, param.toJSONString());
            JSONObject retObj = JSONObject.parseObject(postRet);
            JSONArray records = retObj.getJSONArray("Records");
            if (records != null && !records.isEmpty()) {
                for(int i=0;i<records.size();i++){
                    JSONObject cur = records.getJSONObject(i);
                    SafetyEmpCardVO one = new SafetyEmpCardVO();
                    one.setEmpNo(cur.getString("StaffNo"));
                    one.setCrkId(cur.getInteger("ID").toString());
                    one.setOrgId(cur.getString("OrganizationId"));
                    one.setCurRid(cur.getString("Rid"));

                    retList.add(one);
                }
            } else {
                LogUtils.error("出入口-获取员工{}人事信息异常，{}",empNos,postRet);
            }
            return retList;
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            LogUtils.error("出入口-获取员工"+empNos+"人事信息异常,{}",e);
            return retList;
        }
    }

    /**
     * 获取组织ID和 curId
     * @param paramDTOS 入参
     */
    public void generalOrgIdForBatch(List<ParamDTO> paramDTOS) {
        List<String> empNos = paramDTOS.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        // 1.获取人事ID,组织ID和rid 如果获取失败返回默认信息
        List<SafetyEmpCardVO> list = this.getCrkStaffs(empNos);
        Map<String, SafetyEmpCardVO> map = list.stream().collect(Collectors.toMap(SafetyEmpCardVO::getEmpNo, t -> t, (t1, t2) -> t2));
        paramDTOS.forEach(li -> {
            SafetyEmpCardVO empCard = map.get(li.getEmpNo());
            if (empCard == null) {
                // 没有值，则提供默认值
                li.setOrgId(STAFF_ORGANTION_ID);
                li.setCurId(UUID.randomUUID().toString());
            } else {
                String orgid = empCard.getOrgId();
                String curId = empCard.getCurRid();
                if (StringUtils.isEmpty(orgid)) {
                    // 为空赋值为默认的值
                    li.setOrgId(STAFF_ORGANTION_ID);
                } else {
                    li.setOrgId(orgid);
                }
                if(StringUtils.isEmpty(curId)){
                    li.setCurId(UUID.randomUUID().toString());
                } else {
                    li.setCurId(curId);
                }
            }
        });
    }

    /**
     * 批量 获取出入口 人事信息 组织ID和RIG
     * @param empNos 入参
     */
    public Map<String,SafetyEmpCardVO> generalCrkStaffForBatch(List<String> empNos) {
        if(CollectionUtils.isEmpty(empNos)){
           return new HashMap<>();
        }
        List<List<String>> partition = ListUtils.partition(empNos,300);
        // 1.获取人事ID,组织ID和rid 如果获取失败返回默认信息
        List<SafetyEmpCardVO> list = new ArrayList<>();
        partition.forEach(e->{
            List<SafetyEmpCardVO> subList = this.getCrkStaffs(String.join(",", empNos));
            list.addAll(subList);
        });
        Map<String, SafetyEmpCardVO> map = list.stream().collect(Collectors.toMap(SafetyEmpCardVO::getEmpNo, t -> t, (t1, t2) -> t2));
        for(String empNo:empNos){
            SafetyEmpCardVO curVO = map.get(empNo);
            if(curVO == null){
                curVO = new SafetyEmpCardVO();

                curVO.setEmpNo(empNo);
                curVO.setOrgId(STAFF_ORGANTION_ID);
                curVO.setCurRid(UUID.randomUUID().toString());
                map.put(empNo,curVO);
            }
        }
        return map;
    }

    /**
     * 更新员工新开普楼宇权限
     * @param empNo 员工号
     * @param buildList 新开普设备楼宇权限
     */
    public void sendXkpEmpPower(String empNo,List<BuildEmployeePowerVO> buildList
            ,String sendType,String beginTime,String endTime){
        if(CollectionUtils.isEmpty(buildList)){
            return;
        }
        AtomicReference<String> powerEnd = new AtomicReference<>(endTime);
        if(StringUtils.isEmpty(endTime)){
            Optional<BuildEmployeePowerVO> doMaxAuto = buildList.stream().max(Comparator.comparing(BuildEmployeePowerVO::getEndTime));
            doMaxAuto.ifPresent(build -> {
                powerEnd.set(build.getEndTime());
            });
        }
        // 2. 单次遍历构建所有数据（减少3次流操作）
        StringJoiner deviceCodeJoiner = new StringJoiner(",");
        StringJoiner deviceNameJoiner = new StringJoiner(",");
        Set<String> factoryIdSet = new HashSet<>(); // 自动去重

        buildList.forEach(vo  -> {
            deviceCodeJoiner.add(vo.getDeviceCode());
            deviceNameJoiner.add(vo.getDeviceName());
            if (!StringUtils.isEmpty(vo.getAnFactoryCode()))  {
                factoryIdSet.add(vo.getAnFactoryCode());
            }
        });

        SafetyBuildIssuedLogDO safetyBuildIssuedLogDO = new SafetyBuildIssuedLogDO();
        safetyBuildIssuedLogDO.setEmployeeCode(empNo);
        safetyBuildIssuedLogDO.setDoorId(String.join(",",  factoryIdSet));
        safetyBuildIssuedLogDO.setDeviceCode(deviceCodeJoiner.toString());
        safetyBuildIssuedLogDO.setDeviceName(deviceNameJoiner.toString());
        safetyBuildIssuedLogDO.setIssuedType(sendType);
        safetyBuildIssuedLogDO.setBeginTime(beginTime);
        safetyBuildIssuedLogDO.setEndTime(endTime);

        safetyBuildIssuedLogService.issuedBuildPower(safetyBuildIssuedLogDO);
    }
    /**
     * 根据已发行的日志数据，获取具有相同员工编号的公共楼宇信息，并进行相应的处理
     * @param issuedLogDOS 需要处理的数据列表
     */
    public void getCommonEmpNoBuildInfo(List<SafetyBuildIssuedLogDO> issuedLogDOS, String reason) {
        // 如果数据为空，则直接返回
        if (CollectionUtils.isEmpty(issuedLogDOS)) {
            return;
        }
        // 将数据按员工编号分组，方便后续处理
        Map<String, List<SafetyBuildIssuedLogDO>> dataMap = issuedLogDOS.stream().collect(Collectors.groupingBy(SafetyBuildIssuedLogDO::getEmployeeCode));
        // 准备关闭公共楼宇的参数列表
        List<SafetyClosePublicBuildParams> list = new ArrayList<>();
        // 遍历每个员工的记录，提取需要关闭的设备信息
        dataMap.forEach((key, logDOS) -> {
            // 提取不为空的设备代码，并去重
            List<String> deviceCodes = logDOS.stream().map(SafetyBuildIssuedLogDO::getDeviceCode).filter(com.alibaba.excel.util.StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceCodes)) {
                // 根据员工编号和设备代码创建关闭参数
                list.add(createClosePublicBuildParams(key, deviceCodes));
            }
        });
        // 为空，直接返回
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 执行关闭公共建筑的操作，获得处理后的键值对列表
        List<SafetyKeyValueDto> keyValueDtos = closePublicBuildIng(list, false, reason);
        // 如果没有处理后的键值对，直接返回
        if (CollectionUtils.isEmpty(keyValueDtos)) {
            return;
        }

        Map<String, List<SafetyKeyValueDto>> keyValueMap = keyValueDtos.stream()
                .collect(Collectors.groupingBy(SafetyKeyValueDto::getKey));
        // 数据构建
        keyValueMap.forEach((empCode, keyValueDto) -> {
            List<SafetyBuildIssuedLogDO> logs = dataMap.get(empCode);
            if (CollectionUtils.isEmpty(logs)) {
                return;
            }
            SafetyBuildIssuedLogDO firstLog = logs.get(0);
            SafetyBuildIssuedLogDO newLog = new SafetyBuildIssuedLogDO();
            BeanUtils.copyProperties(firstLog, newLog);
            newLog.setDeviceType("2");
            String deviceCode = keyValueDto.stream().map(SafetyKeyValueDto::getValue).filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.joining(","));
            String deviceName = keyValueDto.stream().map(SafetyKeyValueDto::getValueTwo).filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.joining(","));
            newLog.setDeviceCode(deviceCode);
            newLog.setDeviceName(deviceName);
            issuedLogDOS.add(newLog);
        });
    }

    /**
     * 关闭员工楼栋中的公共门禁
     * 逻辑：判断当前员工是否存在当前楼栋的非公共门禁，不存在就关闭当前员工的非公共门禁
     * 实现步骤：1.获取指定员工非公共权限的楼栋信息
     *          2.获取指定员工的公共权限楼栋信息
     *          3.根据楼栋，判断员工存在次楼栋的公共权限时，是否存在非公共权限， 如果不存在非公共权限，就要关闭此员工的公共权限
     *          4.执行关闭权限的操作
     * @param list 需要处理的数据
     * @param boolType 类型， true：表示没有进行下发， 需要在此方法进行入库和下发，false：表示不需要再此方法进行下发和入库
     */
    public List<SafetyKeyValueDto> closePublicBuildIng(List<SafetyClosePublicBuildParams> list, Boolean boolType, String msg) {
        // 返回值，返回的是在方法外需要进行下发和删除的数据
        List<SafetyKeyValueDto> result = new ArrayList<>();

        // 获取初始需要的数据
        List<SafetyClosePublicBuildDto> safetyClosePublicBuildDtos = getInitData(list);
        if (CollectionUtils.isEmpty(safetyClosePublicBuildDtos)) {
            return result;
        }

        Map<String, SafetyClosePublicBuildDto> publicBuildingMap = new HashMap<>();
        Map<String, SafetyClosePublicBuildDto> noPublicBuildingMap = new HashMap<>();
        // 获取员工的公共权限楼栋信息 和非公共楼宇权限信息
        for (SafetyClosePublicBuildDto dto : safetyClosePublicBuildDtos) {
            String key = dto.getEmpNo() + dto.getBuildingCode();
            if ("1".equals(dto.getIsPublic())) {
                publicBuildingMap.put(key, dto);
            } else {
                noPublicBuildingMap.put(key, dto);
            }
        }

        Set<String> deleteIds = new HashSet<>();
        for (SafetyClosePublicBuildParams closeInfo : list) {
            String empNo = closeInfo.getEmpNo();
            boolean type = closeInfo.isType();
            List<String> closeDeviceCode = closeInfo.getCloseDeviceCode();

            if (type) {
                List<String> buildingCodes = closeInfo.getBuildingCode();
                if (CollectionUtils.isEmpty(buildingCodes)) {
                    continue;
                }
                // 数据筛查， 获取需要处理的数据ID
                handlePublicBuildingCase(empNo, buildingCodes, closeDeviceCode, publicBuildingMap, noPublicBuildingMap, deleteIds, result);
            } else {
                // 数据筛查， 获取需要处理的数据ID
                handleNonPublicBuildingCase(empNo, closeDeviceCode, safetyClosePublicBuildDtos, publicBuildingMap, noPublicBuildingMap, deleteIds, result);
            }
        }

        // 4.新开普的门禁关闭
        processPowerClosure(deleteIds, result, msg);

        // 5.执行关闭权限的操作
        if (boolType && !CollectionUtils.isEmpty(deleteIds)) {
            safetyBuildEmployeePowerService.delete(new ArrayList<>(deleteIds), msg);
        }
        return result;
    }

    /**
     * 关闭新开普的门禁
     * @param deleteIds
     * @param result
     */
    private void processPowerClosure(Set<String> deleteIds, List<SafetyKeyValueDto> result, String msg) {
        List<BuildEmployeePowerVO> powerTimeVoList = safetyBuildEmployeePowerService.selectCommonEmpPowerList(new ArrayList<>(deleteIds));
        if (CollectionUtils.isEmpty(powerTimeVoList)) {
            return;
        }

        // 关闭新开普的门禁
        Map<String, List<BuildEmployeePowerVO>> commonCloseList = powerTimeVoList.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        String endDate = DateUtils.format(DateUtils.addDays(new Date(), -1), 1);
        commonCloseList.forEach((key, value) -> sendXkpEmpPower(key, value, "关闭", endDate, endDate));

        // 删除新开铺的公共门禁
        List<String> xkpDeleteIds = powerTimeVoList.stream().map(SafetyBuildEmployeePowerDO::getId).distinct().collect(Collectors.toList());
        safetyBuildEmployeePowerService.delete(new ArrayList<>(xkpDeleteIds), msg);

        // 过滤掉新开普的公共楼宇ID
        deleteIds.removeAll(xkpDeleteIds);
        // 生成新的返回值
        filterDeviceCodes(commonCloseList, result);
    }
    /**
     * 获取初始数据
     */
    private List<SafetyClosePublicBuildDto> getInitData(List<SafetyClosePublicBuildParams> list) {
        // 赋值楼栋编码
        setBuildingCodes(list);

        // 获取员工信息
        List<String> empNoList = list.stream().map(SafetyClosePublicBuildParams::getEmpNo).collect(Collectors.toList());
        // 获取员工的楼宇门禁信息
        List<SafetyClosePublicBuildDto> safetyClosePublicBuildDtos = safetyBuildEmployeePowerService.selectPowerByEmpNo(empNoList);
        if (CollectionUtils.isEmpty(safetyClosePublicBuildDtos)) {
            return Lists.newArrayList();
        }
        // 过滤公共区域为空的数据
        return safetyClosePublicBuildDtos.stream().filter(li -> !StringUtils.isEmpty(li.getBuildingCode()) && !StringUtils.isEmpty(li.getIsPublic())).collect(Collectors.toList());
    }

    /**
     * 初始化需要关闭的设备对应的楼栋编码
     * @param list 需要初始化的数据
     */
    private void setBuildingCodes(List<SafetyClosePublicBuildParams> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 步骤1：收集所有设备代码
        Set<String> allDeviceCode = list.stream().flatMap(param -> param.getCloseDeviceCode().stream()).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(allDeviceCode)){
            return;
        }
        // 步骤2：获取设备对应的楼栋编码信息
        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceService.selectList(new ArrayList<>(allDeviceCode));
        if (!CollectionUtils.isEmpty(deviceDOList)) {
            Map<String, String> dataMap = deviceDOList.stream().filter(li -> !StringUtils.isEmpty(li.getAnBuildingCode())).collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, SafetyBuildDeviceDO::getAnBuildingCode));
            list.forEach(li -> {
                if (li.isType()) {
                    Set<String> buildingCodes = li.getCloseDeviceCode().stream().map(dataMap::get).filter(ln -> !StringUtils.isEmpty(ln)).collect(Collectors.toSet());
                    li.setBuildingCode(new ArrayList<>(buildingCodes));
                }
            });
        }
    }

    /**
     * 批量注销车牌号
     * @param list 需要注销的车牌号信息， key:车牌号信息， value: Y 成功， N 失败
     */
    public void carNoLogOut(List<SafetyKeyValueDto> list){
        String reDate = DateUtils.format(new Date(), 2);
        list.forEach(safetyKeyValueDto -> processCarNoLogout(safetyKeyValueDto, reDate));
    }

    /**
     * 具体的接口调用
     * @param safetyKeyValueDto 车辆信息
     * @param reDate 时间
     */
    private void processCarNoLogout(SafetyKeyValueDto safetyKeyValueDto, String reDate) {
        String cartNo = safetyKeyValueDto.getKey();
        try {
            String request = carParam.replace("$Token", cartNo).replace("$Redate", reDate);
            String returnValue = sendPost(parkUnregistertUrl, request);
            JSONObject data = JSONObject.parseObject(returnValue);
            if (data.getBoolean("IsSucess")) {
                LogUtils.info(String.format("需要注销的车牌号：%s成功", cartNo));
                safetyKeyValueDto.setValue(Constants.Y);
            } else {
                LogUtils.warn(String.format("需要注销的车牌号：%s失败，原因：%s", cartNo, returnValue));
                safetyKeyValueDto.setValue(Constants.N);
                safetyKeyValueDto.setValueTwo(data.getString("Describe"));
            }
        } catch (Exception e) {
            LogUtils.warn(String.format("需要注销的车牌号：%s失败，异常：%s", cartNo, e.getMessage()));
            safetyKeyValueDto.setValue(Constants.N);
        }
    }

    /**
     * 判断当前用户的权限（作用：列表的展示）
     * 如果员工拥有指定的权限，返回空，不指定员工的权限；否则返回员工号，指定员工只能看自己的数据
     *
     * @param token    token
     * @param roleName 角色名称
     * @return 员工号或者空值， 空值表示员工拥有指定角色的权限，可以看到所有数据， 员工号表示员工只能看到自己创建的数据
     */
    public String getListPermission(String token, String roleName) {
        boolean isAdmin = safetySysUserService.checkRole(token, roleName);
        // 表示当前员工拥有指定的权限
        if (isAdmin) {
            return "";
        }
        // 没有权限，返回当前员工的员工号
        return TokenUtils.getWorkNo(token);
    }

    /**
     * 过滤掉黑名单员工
     * @param employeeMessageDOList 员工集合
     * @return 返回不在黑名单的员工集合
     */
    public List<SafetyEmployeeMessageDO> filterBlackEmployee(List<SafetyEmployeeMessageDO> employeeMessageDOList){
        // 1.获取人员列表中的存在黑名单的人员信息
        List<String> empNos = employeeMessageDOList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        List<String> fileterEmpNos = safetyBaseService.filterBlackEmployee(empNos);
        return employeeMessageDOList.stream().filter(li -> !fileterEmpNos.contains(li.getEmpNo())).collect(Collectors.toList());
    }

    /**
     * 过滤掉黑名单员工
     * @param empNos 员工号集合
     * @return 返回不在黑名单的员工号
     */
    public List<String> filterBlackEmpNos(List<String> empNos){
        // 1.获取人员列表中的存在黑名单的人员信息
        List<String> fileterEmpNos = safetyBaseService.filterBlackEmployee(empNos);
        return empNos.stream().filter(li -> !fileterEmpNos.contains(li)).collect(Collectors.toList());
    }

    /**
     * 校验车行服务中是否存在员工人事信息
     * @param empNo 员工号
     * @return 返回状态
     */
    public boolean checkCrkFromVehicle(String empNo){
        try {
            JSONObject param = new JSONObject();
            if(StringUtils.isEmpty(empNo)){
                return false;
            }
            param.put("where","staffno ='"+empNo+"'");

            String postRet = HttpFileUtil.sendPost(crkVehicleSaffUrl, param.toJSONString());
            JSONObject retObj = JSONObject.parseObject(postRet);
            JSONArray records = retObj.getJSONArray("Records");
            if (records != null && records.size()>0) {
                JSONObject oneRecord = records.getJSONObject(0);
                LogUtils.info("车行系统员工{}的人事信息为{}",empNo,oneRecord.toJSONString());
                return true;
            } else {
                LogUtils.error("出入口-获取员工{}人事信息异常，{}",empNo,postRet);
            }
            return false;
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            LogUtils.error("出入口-获取员工"+empNo+"人事信息异常,{}",e);
            return false;
        }
    }

    /**
     * 获取当前部门的所有子部门 和当前部门
     *
     * @param deptCode 当前部门编码
     * @return 返回所有子部门编码和当前部门编码
     */
    public List<String> getDeptCodeList(String deptCode) {
        // 部门匹配
        List<String> codeList = new ArrayList<>();
        if (!StringUtils.isEmpty(deptCode)) {
            codeList.add(deptCode);
            // 获取所有子组织编码数据
            List<GroupTreeVO> groupTreeVOS = safetyGroupService.selectGroupAll(deptCode);
            if (!CollectionUtils.isEmpty(groupTreeVOS)) {
                codeList.addAll(groupTreeVOS.stream().map(GroupTreeVO::getKey).collect(Collectors.toList()));
            }
        }
        return codeList;
    }

    /**
     * 获取当前部门的所有子部门 和当前部门
     *
     * @param deptName 当前部门名称
     * @return 返回所有子部门编码和当前部门编码
     */
    public List<String> getDeptNameList(String deptName) {
        List<String> result = new ArrayList<>();
        // 根据部门名称获取部门编码
        List<SafetyGroupInfoDO> listByName = safetyGroupService.getListByName(deptName);
        if (CollectionUtils.isEmpty(listByName)) {
            return result;
        }
        // 提取部门编码，过滤空值
        Set<String> empCodeList = listByName.stream().map(SafetyGroupInfoDO::getCode).filter(li -> !StringUtils.isEmpty(li)).collect(Collectors.toSet());
        // // 遍历每个部门编码，获取对应部门编码的所有下级部门信息
        empCodeList.forEach(li -> result.addAll(this.getDeptCodeList(li)));
        return result;
    }

    /**
     * 删除指定员工的，指定角色
     */
    public void deleteRoleByEmpNo(String empNo, String roleName) {
        // 获取账号信息
        List<Integer> userIdList = safetySysUserService.getUserIdByCodeList(Collections.singletonList(empNo));
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }

        // 获取角色信息
        SafetySysRoleDO roleDO = safetySysRoleService.getByRoleName(roleName);
        if (roleDO.getId() == null) {
            return;
        }

        // 数据删除
        safetySysRoleService.deleteRole(userIdList, roleDO.getId());
    }

    /**
     * 根据公司代码获取供应商公司名称
     * @param code 编码
     * @return 返回供应商公司名称
     */
    public String getSupplyCompanyName(String code) {
        String result = null;
        try {
            // 入参
            Map<String, String> map = new HashMap<>();
            map.put("LIFNR", code);
            String request = JSON.toJSONString(map);

            String returnValue = sendPost(supplyCompanyUrl, request);
            JSONObject data = JSONObject.parseObject(returnValue);
            // RETURN_STATUS为空表示有值，==E表示没值 { "NAME1": "福耀集团长春有限公司", "RETURN_STATUS": "", "RETURN_VALUES": "" }
            result = data.getString("NAME1");
        } catch (Exception e) {
            LogUtils.warn(String.format("获取供应商公司信息失败：供应商编码=%s，异常：%s", code, e.getMessage()));
        }
        return result;
    }

    public String syncEhrBlank(String begTime,String endTime){
        safetyBaseService.selectEhrBlank(begTime,endTime);
        return "1";
    }

    /**
     * 调用中台发送短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @param templateCode 短信验证码模版
     * @return 结果
     */
    public String sendCodeSms(String phone, String code, String templateCode) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("smsConfigId", smsConfigId);
        param.put("smsType", "0");
        param.put("phoneNos", phone);
        param.put("verifyCode", code);
        param.put("templateCode", templateCode);
        String jsonParams = JsonUtils.toString(param);
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type","application/json");
        LogUtils.info("短信bodyParam：{}", jsonParams.toString());
        String ret = webserviceUtil.postJsonStr(sendMsgSmsUrl, jsonParams, headerMap);
        LogUtils.info("发送短信返回值：{}", ret);
        return ret;
    }
    /**
     * 前往非友商客户接口获取信息
     * @param cardNo 身份证号
     * @return 返回值为list
     */
    public List<CompetitorMangeVO.AccInfo> queryCompetitorUrl(List<String> cardNo) {
        try {
            // 非友商信息客户信息查询
            List<Map<String, String>> params = cardNo.stream().map(li -> {
                Map<String, String> map = new HashMap<>();
                map.put("sysName", "SAFETY");
                map.put("accName", "");
                map.put("countryCode", "");
                map.put("overseasMark", "F");
                map.put("usci", li);
                return map;
            }).collect(Collectors.toList());
            String request = JSON.toJSONString(params);
            String returnValue = sendPost(competitorMangeQueryUrl, request);
            CompetitorMangeVO result = JSONObject.parseObject(returnValue, CompetitorMangeVO.class);
            LogUtils.info("非友商客户信息获取：入参" + request + "---出参：" + result);
            if (!Objects.equals(result.getCode(), 1000)) {
                throw new BizException(ResponseEnum.FAIL.getCode(),"验证失败-非友商客户信息接口调用失败。");
            }
            CompetitorMangeVO.Data data = result.getData();
            List<CompetitorMangeVO.AccInfo> accInfo = data.getAccInfo();
            long count = accInfo.stream().map(CompetitorMangeVO.AccInfo::getIsNoAccess).filter(li -> Objects.equals(li, "T")).count();
            if (count > 0) {
                Map<String, String> headParam = new HashMap<>();
                headParam.put("Content-Type", "application/x-www-form-urlencoded");
                String confirmValue = webserviceUtil.sendHttp(String.format("%s&logid=%s", competitorMangeConfirmUrl, data.getLogId()), "1", null, headParam, "POST");
                CompetitorMangeVO confirmResult = JSONObject.parseObject(confirmValue, CompetitorMangeVO.class);
                LogUtils.info("非友商客户信息确认：出参：" + confirmResult);
            }
            return accInfo;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"验证失败-非友商客户信息接口调用失败。");
        }
    }

    /**
     * 前往非友商客户接口获取信息
     * @param cardNo 身份证号
     * @return 返回值为map, key: 身份证号， value：非友商时的负责人
     */
    public Map<String, String> toCompetitorOfBlack(List<String> cardNo) {
        // 1. 获取账号信息列表
        List<CompetitorMangeVO.AccInfo> accInfos = queryCompetitorUrl(cardNo);

        // 2. 提取员工编号列表
        List<String> empNos = extractEmpNos(accInfos);

        // 3. 查询员工消息信息
        List<SafetyEmployeeMessageDO> messageDOList = safetyEmployeeMessageService.getListByStaffNo(empNos);

        // 4. 构建员工编号集合用于快速查找
        Set<String> empNoSet = convertToEmpNoSet(messageDOList);

        // 5. 构建结果映射
        return buildResultMap(accInfos, empNoSet);
    }

    public Pair<Boolean, String> checkCompetitor(Map<String, String> competitorMap, String cardNo) {
        String competitorDirector = competitorMap.getOrDefault(cardNo, "");
        String[] split = competitorDirector.split("#");
        Boolean bool = !StringUtils.isEmpty(competitorDirector) && Objects.equals(split[0], "T");
        String competitor = split.length == 3 ? split[2] : "";
        return Pair.of(bool, competitor);
    }
    public Pair<Boolean, String> checkCompetitorToEmpNo(Map<String, String> competitorMap, String cardNo) {
        String competitorDirector = competitorMap.getOrDefault(cardNo, "");
        String[] split = competitorDirector.split("#");
        Boolean bool = !StringUtils.isEmpty(competitorDirector) && Objects.equals(split[0], "T");
        String competitor = split.length == 3 ? split[1] : "";
        return Pair.of(bool, competitor);
    }

    /**
     * 黑名单发送宇信通消息
     * @param info 消息细信息
     */
    public void sendYxtMsg(SafetyBlankYxtMsgVO info) {
        if (StringUtils.isEmpty(info.getWorkEmpNo())) {
            return;
        }
        // 通过redis判断今天是否已经发过消息， 发过的直接结束
        String empInfo = info.getCardNo() + info.getWorkEmpNo();
        String redisKey = Constants.REDIS_BLACK_YXT_MSG_PRE + "empInfo" + empInfo;
        Object value = redisUtil.get(redisKey);
        if (value != null) {
            return;
        }
        info.check();
        String eipFlowUrl = eipFlowLink.replace("view&fdId=%s", "add&fdTemplateId=172b5bcc7e5d47efa7f434d4195928b3&s_css=default");
        // 发送消息
        String msg = info.getYxtMsgEnum().getMsg().replace("{workEmpName}", info.getWorkEmpName())
                .replace("{blankEmpName}", info.getBlankEmpName())
                .replace("{cardNo}", info.getCardNo())
                .replace("{phone}", info.getPhone())
                .replace("{currentDate}", DateUtils.format(info.getCurrentDate(), 2))
                .replace("{anEipTodoUrl}", anEipTodoUrl + "blackRecord")
                .replace("{eipFlowLink}", eipFlowUrl);
        if (StringUtils.isEmpty(info.getCompetitorInfo())) {
            msg = msg.replace("联系责任人{competitorInfo}，或", "");
        } else {
            msg = msg.replace("{competitorInfo}", info.getCompetitorInfo());
        }
        String supplierName = info.getSupplierName();
        String supplierPhone = info.getSupplierPhone();
        if (!StringUtils.isEmpty(supplierName) && !StringUtils.isEmpty(supplierPhone)) {
            msg = msg.replace("{supplier}", String.format("，提交人：%s，电话：%s", supplierName, supplierPhone));
        } else if (!StringUtils.isEmpty(supplierName) && StringUtils.isEmpty(supplierPhone)) {
            msg = msg.replace("{supplier}", String.format("，提交人：%s", supplierName));
        } else {
            msg = msg.replace("{supplier}", "");
        }

        yxtRebotUtils.postForEntity(null, incomintRebotId, info.getWorkEmpNo(), msg, new RestTemplate());

        // 保存redis
        redisUtil.set(redisKey, empInfo, DateUtils.getRemainderMinute());
    }

    /**
     * 黑名单发送宇信通消息(删除redis缓存)
     * @param buttJoinCodes 对接人信息
     * @param idCards 身份证号
     */
    public void clearRedis(String buttJoinCodes, String idCards) {
        if (StringUtils.isEmpty(buttJoinCodes) || StringUtils.isEmpty(idCards)) {
            return;
        }
        for (String s : buttJoinCodes.split(",")) {
            for (String s1 : idCards.split(",")) {
                redisUtil.del(Constants.REDIS_BLACK_YXT_MSG_PRE + "empInfo" + s1 + s);
            }
        }
    }

    /**
     * 排除中高层员工
     * @param empNoList 员工号
     * @return 返回排除后的员工号
     */
    public Set<String> excludeLeader(Set<String> empNoList){
        if (CollectionUtils.isEmpty(empNoList)) {
            return empNoList;
        }
        List<SafetyEmployeeMessageDO> leaderList = safetyEmployeeMessageService.getLeaderList();
        if (CollectionUtils.isEmpty(leaderList)) {
            return empNoList;
        }
        Set<String> leaderEmpNoList = leaderList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toSet());
        return empNoList.stream().filter(li -> !leaderEmpNoList.contains(li)).collect(Collectors.toSet());
    }

    /**
     * 前往中台获取黑名单员工照片
     * @param workNo 员工号
     * @param photoType 0：证件照；1：生活照
     */
    public String queryBlackMessagePhoto(String workNo, Integer photoType) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetId", "SAFETY");
        params.put("workNo", workNo);
        params.put("photoType", photoType);
        String request = JSON.toJSONString(params);
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = sendPost(getPhotoUrl, request);
            JSONObject result = JSONObject.parseObject(respBody);
            LogUtils.info("中台获取照片：入参" + request + "---出参：" + result);
            Boolean success = result.getBoolean("success");
            if (Boolean.FALSE.equals(success)) {
                return null;
            }
            String value = result.getString("result");
            if (!StringUtils.isEmpty(value) && value.startsWith("http")) {
                return value;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("中台获取照片接口失败:入参" + request);
            return null;
        } finally {
//            safetyInterfaceLogHandler.recordLog("ZT_GET_PHOTO", getPhotoUrl, startTime, request, respBody);
        }
    }

    /**
     * 照片获取
     * @param workNo 工号
     * @return 返回照片详情
     */
    public Pair<String, String> getPhoto(String workNo) {
        String lifePhoto = queryBlackMessagePhoto(workNo, 1);
        if (!StringUtils.isEmpty(lifePhoto) && !Base64ImageDecoder.isPhoto(lifePhoto)) {
            lifePhoto = lifePhoto + "&apikey=" + apikey;
        }
        String idPhoto = queryBlackMessagePhoto(workNo, 0);
        if (!StringUtils.isEmpty(idPhoto) && !Base64ImageDecoder.isPhoto(idPhoto)) {
            idPhoto = idPhoto + "&apikey=" + apikey;
        }
        return Pair.of(lifePhoto, idPhoto);
    }

    /**
     * 照片获取-生活照
     * @param workNo 工号
     * @return 返回照片详情
     */
    public String getLifePhoto(String workNo) {
        String lifePhoto = queryBlackMessagePhoto(workNo, 1);
        if (!StringUtils.isEmpty(lifePhoto) && !Base64ImageDecoder.isPhoto(lifePhoto)) {
            lifePhoto = lifePhoto + "&apikey=" + apikey;
        }
        LogUtils.info("照片获取-生活照-workNo{}， lifePhoto{}", workNo, lifePhoto);
        return lifePhoto;
    }

    public String getPhotoUrl(String address) {
        if (StringUtils.isEmpty(address)) {
            return null;
        }
        //图片转化为二进制
        if(address.startsWith("http")){
            if (Base64ImageDecoder.isPhoto(address)) {
                return address;
            } else {
                return address + "&apikey=" + apikey;
            }
        }else{
            return String.format(updatePhotoUrl, address);
        }
    }
    public void addSendLog(SafetyVisitorHairpinDO hairpinDO, String userName, String ability) {
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperator(userName);
        log.setOperatePage("访客发卡");
        log.setAbility(ability);
        log.setMessage(hairpinDO.getVisitorName() + ";" + hairpinDO.getVisitorPhone() + ";" + hairpinDO.getVisitorIdCard());
        safetyLogMessageService.addLogMessage(log);
    }

    public VO eipWriteBackForPhotoUpdate(EipWriteBackParam vo) {
        Long id = safetyFaceUnqualifiedService.selectListByEmpNo(vo.getEmpNo());
        if (id != null) {
            vo.setId(id);
            safetyFaceUnqualifiedService.updateById(vo);
            // 消除EIP代办
            String modelId = "YT-SAFETY-faceUnqualifiedRecordConfirm-" + vo.getEmpNo();
            eipCommonService.eipToDoDone(modelId, vo.getEmpNo());
            return VoUtil.success();
        }
        return VoUtil.fail("更新失败：" + vo);
    }

    /**
     * 中台OCR照片识别接口
     * @param idCardPhotoUrl 照片url
     * @param imageType 照片后缀
     * @return 返回OCR识别结果
     */
    public String idCardPhotoOcr(String idCardPhotoUrl, String imageType) {
        String imageBase = Base64ImageDecoder.imgBase64(idCardPhotoUrl);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", idCardAppId);
        params.put("imageType", imageType);
        params.put("imageBase64", imageBase);
        String request = JSON.toJSONString(params);
        String respBody;
        try {
            respBody = sendPost(idCardUrl, request);
            JSONObject result = JSONObject.parseObject(respBody);
            LogUtils.info("中台OCR识别：入参照片：{}---出参：{}", idCardPhotoUrl, result);
            Boolean success = result.getBoolean("success");
            if (Boolean.FALSE.equals(success)) {
                return null;
            }
            return result.getString("result");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("中台OCR识别失败:入参照片：{}", idCardPhotoUrl);
            return null;
        }
    }
}
