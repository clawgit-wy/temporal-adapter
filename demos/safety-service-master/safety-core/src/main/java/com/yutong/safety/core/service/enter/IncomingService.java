package com.yutong.safety.core.service.enter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.enter.EnterApproveCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.enter.IncomingMapper;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.handler.enter.IncomingHandler;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class IncomingService implements IncomingHandler {

    @Autowired
    private IncomingMapper incomingMapper;

    private static String CRK_ORGANTION_ID = "0D864B2A-A7BD-4511-94A2-A5AB6E453588";

    /**
     * 出入口系统url
     */
    @Value("${crk.updateUrl}")
    private String crkUpdateUrl;

    @Value("${crk.insertUrl}")
    private String crkInsertUrl;

    @Value("${crk.IssueParkView.url}")
    private String carInFactoryInvehicleUrl;

    @Value("${sap.syncUrl}")
    private String sapSyncUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    private SafetyFieldManageDOMapper safetyFieldManageDOMapper;


    @Override
    public List<SafetyEnterCrk> selectSafetyEnterCrk(String id) {
        return incomingMapper.selectSafetyEnterCrk(id);
    }

    @Override
    public String insertCarAuth(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list) {
        String ret = "false";
        boolean bool = false;
        String bodyParams = "\"TokenId='"+param.getFdCarNo()+"'\"";
        Map headParams = new HashMap(4);
        headParams.put("Content-Type","application/json");
        String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);
        if (StringUtils.isEmpty(returnJson)) {
            LogUtils.info("出入口接口未找到{}的出入厂数据",param.getFdCarNo());
            bool = true;
        }else {
            JSONObject retObject = JSONObject.parseObject(returnJson);
            JSONObject retMap = retObject.getJSONObject("Model");
            if (retMap == null) {
                bool = true;
            }
        }
        if (bool) {
            ret = syncCrk(param, list);
        } else {
            ret = updateCrk(param, list);
        }

        return ret;
    }

    /**
     * 是否临时运输车辆
     * @param param
     * @return
     */
    public boolean isTemporarilyCar(SafetyEnterDockCrk param){
        boolean bool = false;

        //先根据厂区判断：当审批后的厂区全部都在临时运输车辆管理里面，直接判定该车为临时运输车辆；否则就根据车辆类型判断
        String factory = param.getFactory();
        if(!StringUtils.isBlank(factory)){
            List<String> list = Arrays.stream(factory.split(","))
                    .filter(s->!s.isEmpty())
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(list)){
                Integer i = safetyFieldManageDOMapper.selectFactoryList(list);
                if(list.size()==i){
                    return true;
                }
            }
        }

        if(StringUtils.isBlank(param.getFdFilingCarTypeName())){
            return bool;
        }
        String temporarilyCar = safetyFieldManageDOMapper.isTemporarilyCar(param.getFdFilingCarTypeName());
        if(!StringUtils.isBlank(temporarilyCar) && "是".equals(temporarilyCar)){
            bool = true;
        }
        return bool;
    }

    /**
     * 同步给入出口系统
     * 以车辆为单位，先插入，再更新
     * 如果插入失败，可能车辆已经录入过了，那么再更新
     *
     * @param param
     */
    @Override
    public String syncCrk(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list) {
        OkHttpClient client = new OkHttpClient();
        //先根据选择的厂区，查询对应的通道号
        LogUtils.info("厂区映射 syncCrkParam={}", param);
        String[] AuthDevice = queryChannelByPark(list);
        if (null == AuthDevice) {
            AuthDevice = new String[]{"1", "2", "3", "4", "5", "6", "7", "23", "24", "25", "26", "27", "28", "31", "32", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "55", "56", "79", "80", "81", "82", "85", "86", "87", "88", "94", "95", "105", "106"};    //项目编号集合
        }
        LogUtils.info("厂区映射 AuthDevice={}", AuthDevice.length);

        SafetyEnterFilingPer per = new SafetyEnterFilingPer();
        List<SafetyEnterFilingPer> perList = param.getPerList();
        for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
            if ("10".equals(safetyEnterFilingPer.getFdPerTypeCode())) {
                per = safetyEnterFilingPer;
            }
        }

        String StaffNo = per.getFdPerPhone();    //人员编号
        String StaffName = per.getFdPerName();       //人员姓名
        String TelphoneNo = per.getFdPerPhone();     //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = StringUtils.isEmpty(param.getOperName()) ? "mobile" : param.getOperName();   //操作员姓名
        String OperNo = StringUtils.isEmpty(param.getOperNo()) ? "mobile" : param.getOperNo();     //操作员编号
        //String OrginazitionId = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";//组织机构编号
        String Gid = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";    //项目编号
        Integer ID = 0;         //操作编号
        String[] ProjectGids = new String[]{"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8"};    //项目编号集合
        Map<String,Object> map = new HashMap();

        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("TelphoneNo", TelphoneNo);
        map.put("Redate", Redate);
        map.put("OperName", OperName);
        map.put("OperNo", OperNo);
        map.put("OrginazitionId", CRK_ORGANTION_ID);
        map.put("Gid", Gid);
        map.put("ID", ID);
        map.put("ProjectGids", ProjectGids);

        map.put("TokenType", 0);//凭证类型（0 车牌 1 卡片 2 ETC 3纸票 4二维码 5 生物）
        map.put("IcNumber", "");//Ic号
        map.put("IdNumber", "");//Id号
        map.put("Deposit", 0);//办卡押金
        map.put("IsWrite", false);//输入/录入
        map.put("IsLegal", true);//是否合法

        JSONObject ParkIssue = new JSONObject();
        ParkIssue.put("StaffNo", StaffNo);
        ParkIssue.put("StaffName", StaffName);
        ParkIssue.put("TelphoneNo", TelphoneNo);
        ParkIssue.put("Redate", Redate);
        ParkIssue.put("OperName", OperName);
        ParkIssue.put("OperNo", OperNo);
        ParkIssue.put("OrginazitionId", CRK_ORGANTION_ID);
        ParkIssue.put("Gid", Gid);
        ParkIssue.put("ID", ID);
        ParkIssue.put("ProjectGids", ProjectGids);

        ParkIssue.put("TokenOper", 1);
        ParkIssue.put("AlreadyIn", false);
        ParkIssue.put("TokenId", param.getFdCarNo());//这个是关键值，出入口系统根据这个判断是否唯一车辆
        ParkIssue.put("SerialNo", param.getFdCarNo());
        //长期车辆：C33E7736-9FB3-4932-B4CC-030FAA76EBFC
        //临时车辆：1E0667B0-90D7-46A9-8A68-50D7E4673759
        String tcm = isTemporarilyCar(param)?"1E0667B0-90D7-46A9-8A68-50D7E4673759":"C33E7736-9FB3-4932-B4CC-030FAA76EBFC";
        ParkIssue.put("Tcm", tcm);
        ParkIssue.put("PayType", 0);
        ParkIssue.put("AccountBalance", 0);
        ParkIssue.put("BeginDate", param.getBeginTime());
        ParkIssue.put("EndDate", param.getEndTime());
        ParkIssue.put("AuthDevice", AuthDevice);
        ParkIssue.put("Token", param.getFdCarNo());
        ParkIssue.put("TokenType", 0);
        ParkIssue.put("UseModel", 1);
        ParkIssue.put("Plate", param.getFdCarNo());

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
            String str = response.body().string();
            LogUtils.info("下发入厂请求同步-出入口系统新增 返回结果：" + str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("State") != null){
                //
                JSONObject stateObj = obj.getJSONObject("State");
                if(stateObj.get("IsSucess") != null){
                    String IsSucess = stateObj.get("IsSucess").toString();
                    LogUtils.info("下发入厂请求同步-出入口系统新增成功：" + IsSucess);
                    return IsSucess;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.info("下发入厂请求同步-出入口系统新增调用失败");
        }
        return "false";
    }

    @Override
    public String updateCrk(SafetyEnterDockCrk param, List<SafetyEmpowerDeviceDO> list) {
        OkHttpClient client = new OkHttpClient();
        LogUtils.info("厂区映射 updateCrkParam={}", param);
        String[] AuthDevice = queryChannelByPark(list);
        if (null == AuthDevice) {
            AuthDevice = new String[]{"1", "2", "3", "4", "5", "6", "7", "23", "24", "25", "26", "27", "28", "31", "32", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "55", "56", "79", "80", "81", "82", "85", "86", "87", "88", "94", "95", "105", "106"};    //项目编号集合
        }
        LogUtils.info("厂区映射 AuthDevice={}", AuthDevice.length);

        SafetyEnterFilingPer per = new SafetyEnterFilingPer();
        List<SafetyEnterFilingPer> perList = param.getPerList();
        for (SafetyEnterFilingPer safetyEnterFilingPer : perList) {
            if ("10".equals(safetyEnterFilingPer.getFdPerTypeCode())) {
                per = safetyEnterFilingPer;
            }
        }

        String StaffNo = per.getFdPerPhone();    //人员编号
        String StaffName = per.getFdPerName();       //人员姓名
        String TelphoneNo = per.getFdPerPhone();   //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = StringUtils.isEmpty(param.getOperName()) ? "mobile" : param.getOperName();   //操作员姓名
        String OperNo = StringUtils.isEmpty(param.getOperNo()) ? "mobile" : param.getOperNo();     //操作员编号
        //String OrginazitionId = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";//组织机构编号

        String Gid = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";    //项目编号
        String[] ProjectGids = new String[]{"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8"};    //项目编号集合

        Map<String,Object> map = new HashMap();

        map.put("TokenId", param.getFdCarNo());       //车牌号
        map.put("Token", param.getFdCarNo());        //车牌号
        map.put("Plate", param.getFdCarNo());        //车牌号
        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        String tcm = isTemporarilyCar(param)?"1E0667B0-90D7-46A9-8A68-50D7E4673759":"C33E7736-9FB3-4932-B4CC-030FAA76EBFC";
        map.put("Tcm", tcm);
        map.put("PayType", "0");
        map.put("AccountBalance", "0");
        map.put("TelphoneNo", TelphoneNo);        //车上第一个人的电话
        map.put("OrginazitionId", CRK_ORGANTION_ID);

        map.put("BeginDate", param.getBeginTime());
        map.put("EndDate", param.getEndTime());
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
        map.put("Gid", Gid);
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
            String str = response.body().string();
            LogUtils.info("更新入厂请求同步-出入口系统更新 返回结果 \n {}", str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("IsSucess") != null){
                String IsSucess = obj.get("IsSucess").toString();
                LogUtils.info("更新入厂请求同步-出入口系统更新成功：" + IsSucess);
                return IsSucess;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.info("接口调用失败");
        }
        return "false";
    }


    private String[] queryChannelByPark(List<SafetyEmpowerDeviceDO> list) {
        if(CollectionUtils.isNotEmpty(list)){
            List<String> nos = new ArrayList<>();
            for (SafetyEmpowerDeviceDO channel : list) {
                if (StringUtils.isBlank(channel.getEmpowerNo())) {
                    LogUtils.info(channel.getFactoryName() + "通道号为空");
                    continue;
                }
                String channelNo = channel.getEmpowerNo();
                String[] channelNos = channelNo.split(",");
                Collections.addAll(nos, channelNos);
            }

            String[] nums = new String[nos.size()];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = nos.get(i);
            }
            return nums;
        }
        return null;
    }

    /**
     * 车辆入厂备案权限同步给SAP，车辆入厂是会给接口人发消息
     * @param list
     */
    @Override
    public void syncSap(List<SafetyEnterDockCrk> list) {

        OkHttpClient client = new OkHttpClient();

        JSONArray jsonArray = new JSONArray();
        for (SafetyEnterDockCrk temp : list) {
            List<EnterApproveCrk> approveList = temp.getApproveList();
            if(CollectionUtils.isEmpty(approveList)){
                continue;
            }
            for (EnterApproveCrk crk :approveList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TokenNo", temp.getFdCarNo());
                // 无装车单传空
                jsonObject.put("Vbeln", StringUtils.isEmpty(temp.getFdDetailedImg()) ? "" : temp.getFdDetailedImg());
                jsonObject.put("Lfdat", temp.getBeginTime().substring(0,10));
                jsonObject.put("Contact", crk.getOperName());
                jsonObject.put("Tel", crk.getOperPhone());
                jsonObject.put("Erdat", temp.getEndTime());
                jsonObject.put("LIFNR", temp.getProviderCode());
                jsonObject.put("LNAME", temp.getProviderName());

                // 送货人信息处理
                List<SafetyEnterFilingPer> perList = temp.getPerList();
                jsonObject.put("SNAME", perList.stream().distinct().map(SafetyEnterFilingPer::getFdPerName).collect(Collectors.joining("/")));
                jsonObject.put("STEL", perList.stream().distinct().map(SafetyEnterFilingPer::getFdPerPhone).collect(Collectors.joining("/")));
                jsonArray.add(jsonObject);
            }
        }
        if (jsonArray.isEmpty()) {
            return;
        }
        JSONObject object = new JSONObject();
        object.put("ZYUYUE_ST_PI", jsonArray);

        String toSapString = JSON.toJSONString(object);
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, toSapString);
        Request request = new Request.Builder()
                .url(sapSyncUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        LogUtils.info("入厂请求同步 Sap系统 接口地址 {} \n 请求参数 {}", sapSyncUrl, toSapString);

        Response response;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            LogUtils.info("入厂请求同步 Sap系统 返回结果 \n {}", str);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.info("入厂请求同步 Sap系统,接口调用失败");
        }
    }

    /**
     * 外协下发权限
     * @param param
     * @param list
     */
    public void insertCarAuthPrivate(SafetyOutsiderPromiseDTO param, List<SafetyEmpowerDeviceDO> list,
                                     List<SafetyEmployeePrivateCarDO> carList) {
        boolean bool = false;

        if (CollectionUtils.isEmpty(carList)) {return;}

        for (SafetyEmployeePrivateCarDO privateCar : carList) {
            String carNo = privateCar.getCarNo();
            String bodyParams = "\"TokenId='"+carNo+"'\"";
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");
            String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);
            if (StringUtils.isEmpty(returnJson)) {
                LogUtils.info("出入口接口未找到外协{}的出入厂数据",carNo);
                bool = true;
            }else {
                JSONObject retObject = JSONObject.parseObject(returnJson);
                JSONObject retMap = retObject.getJSONObject("Model");
                if (retMap == null) {
                    bool = true;
                }
            }
            if(bool){
                syncCrkWx(param,privateCar,list);
            }else {
                updateCrkWx(param,privateCar,list);
            }
        }
    }

    /**
     * 外协新增入厂车辆权限
     * @param param
     * @param privateCarDO
     * @param list
     * @return
     */
    public String syncCrkWx(SafetyOutsiderPromiseDTO param, SafetyEmployeePrivateCarDO privateCarDO,
                            List<SafetyEmpowerDeviceDO> list) {
        String carNo = privateCarDO.getCarNo();
        String carColor = privateCarDO.getCarColor();
        String vehicleType = privateCarDO.getVehicleType();

        OkHttpClient client = new OkHttpClient();
        //先根据选择的厂区，查询对应的通道号
        LogUtils.info("外协厂区映射 syncCrkParam={}", param);
        String[] AuthDevice = queryChannelByPark(list);
        if (null == AuthDevice) {
            AuthDevice = new String[]{"1", "2", "3", "4", "5", "6", "7", "23", "24", "25", "26", "27", "28", "31", "32", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "55", "56", "79", "80", "81", "82", "85", "86", "87", "88", "94", "95", "105", "106"};    //项目编号集合
        }
        LogUtils.info("外协厂区映射 AuthDevice={}", AuthDevice.length);

        String StaffNo = param.getEmpNo();    //人员编号
        String StaffName = param.getEmpName();       //人员姓名
        String TelphoneNo = param.getPhoneNo();     //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "mobile";   //操作员姓名
        String OperNo = "mobile";     //操作员编号
        String beginDate = DateUtils.format(new Date(), 2);    //开始时间
        String endDate =   StringUtils.isBlank(param.getServiceEndDate())?"2099-12-31":param.getServiceEndDate();    //结束时间";
        //String OrginazitionId = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";//组织机构编号
        String Gid = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";    //项目编号
        Integer ID = 0;         //操作编号
        String[] ProjectGids = new String[]{"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8"};    //项目编号集合
        Map<String,Object> map = new HashMap();

        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("TelphoneNo", TelphoneNo);
        map.put("Redate", Redate);
        map.put("OperName", OperName);
        map.put("OperNo", OperNo);
        map.put("OrginazitionId", CRK_ORGANTION_ID);
        map.put("Gid", Gid);
        map.put("ID", ID);
        map.put("ProjectGids", ProjectGids);

        map.put("TokenType", 0);//凭证类型（0 车牌 1 卡片 2 ETC 3纸票 4二维码 5 生物）
        map.put("IcNumber", "");//Ic号
        map.put("IdNumber", "");//Id号
        map.put("Deposit", 0);//办卡押金
        map.put("IsWrite", false);//输入/录入
        map.put("IsLegal", true);//是否合法

        map.put("VehicleBand", vehicleType); //车辆型号
        map.put("VehicleColor", carColor); //车辆颜色

        JSONObject ParkIssue = new JSONObject();
        ParkIssue.put("StaffNo", StaffNo);
        ParkIssue.put("StaffName", StaffName);
        ParkIssue.put("TelphoneNo", TelphoneNo);
        ParkIssue.put("Redate", Redate);
        ParkIssue.put("OperName", OperName);
        ParkIssue.put("OperNo", OperNo);
        ParkIssue.put("OrginazitionId", CRK_ORGANTION_ID);
        ParkIssue.put("Gid", Gid);
        ParkIssue.put("ID", ID);
        ParkIssue.put("ProjectGids", ProjectGids);

        ParkIssue.put("VehicleColor", carColor); //车辆颜色
        ParkIssue.put("VehicleBand", vehicleType); //车辆类型

        ParkIssue.put("TokenOper", 1);
        ParkIssue.put("AlreadyIn", false);
        ParkIssue.put("TokenId", carNo);//这个是关键值，出入口系统根据这个判断是否唯一车辆
        ParkIssue.put("SerialNo", carNo);
        //长期车辆：C33E7736-9FB3-4932-B4CC-030FAA76EBFC
        //临时车辆：1E0667B0-90D7-46A9-8A68-50D7E4673759
        //临时运输车辆	1E0667B0-90D7-46A9-8A68-50D7E4673759
        //员工车辆	C97611CE-C6EA-4582-9680-55934B79F9F7
        //公务车辆	2882BCD7-44A2-41A1-BE7D-F2C1304DBF88
        //外协车辆	A4807A31-2DAA-462C-BC27-FAC3C2044720
        //领导车辆	6CCF9944-A068-4B65-B4C4-3F227CC7671D
        //T7商务车	4A0B5A8D-E905-4267-B1B5-A1CC0B5ABB7A
        ParkIssue.put("Tcm", "A4807A31-2DAA-462C-BC27-FAC3C2044720");
        ParkIssue.put("PayType", 0);
        ParkIssue.put("AccountBalance", 0);
        ParkIssue.put("BeginDate", beginDate);
        ParkIssue.put("EndDate", endDate);
        ParkIssue.put("AuthDevice", AuthDevice);
        ParkIssue.put("Token", carNo);
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
        LogUtils.info("外协入厂请求同步-出入口系统 接口地址 {} \n 请求参数 {}", crkInsertUrl, toCrkString);

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            LogUtils.info("外协下发入厂请求同步-出入口系统新增 返回结果：{}" , str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("State") != null){
                //
                JSONObject stateObj = obj.getJSONObject("State");
                if(stateObj.get("IsSucess") != null){
                    String IsSucess = stateObj.get("IsSucess").toString();
                    LogUtils.info("外协下发入厂请求同步-出入口系统新增成功：{}" , IsSucess);
                    return IsSucess;
                }
            }
        } catch (IOException e) {
            LogUtils.info("外协下发入厂请求同步-出入口系统新增调用失败，信息{}",e.getMessage());
        }
        return "false";
    }

    /**
     * 外协更新入厂车辆权限
     * @param param
     * @param privateCarDO
     * @param list
     * @return
     */
    public String updateCrkWx(SafetyOutsiderPromiseDTO param, SafetyEmployeePrivateCarDO privateCarDO, List<SafetyEmpowerDeviceDO> list) {
        String carNo = privateCarDO.getCarNo();
        String carColor = privateCarDO.getCarColor();
        String vehicleType = privateCarDO.getVehicleType();

        OkHttpClient client = new OkHttpClient();
        LogUtils.info("外协厂区映射 updateCrkParam={}", param);
        String[] AuthDevice = queryChannelByPark(list);
        if (null == AuthDevice) {
            AuthDevice = new String[]{"1", "2", "3", "4", "5", "6", "7", "23", "24", "25", "26", "27", "28", "31", "32", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "55", "56", "79", "80", "81", "82", "85", "86", "87", "88", "94", "95", "105", "106"};    //项目编号集合
        }
        LogUtils.info("外协厂区映射 AuthDevice={}", AuthDevice.length);

        String StaffNo = param.getEmpNo();    //人员编号
        String StaffName = param.getEmpName();       //人员姓名
        String TelphoneNo = param.getPhoneNo();     //电话号码
        String Redate = DateUtils.format(new Date(), 2);    //登记时间
        String OperName = "mobile";   //操作员姓名
        String OperNo = "mobile";     //操作员编号
        String beginDate = DateUtils.format(new Date(), 2);    //开始时间
        String endDate =   StringUtils.isBlank(param.getServiceEndDate())?"2099-12-31":param.getServiceEndDate();    //结束时间";
        //String OrginazitionId = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";//组织机构编号

        String Gid = "1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8";    //项目编号
        String[] ProjectGids = new String[]{"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8"};    //项目编号集合

        Map<String,Object> map = new HashMap();

        map.put("TokenId", carNo);       //车牌号
        map.put("Token", carNo);        //车牌号
        map.put("Plate", carNo);        //车牌号
        map.put("StaffNo", StaffNo);
        map.put("StaffName", StaffName);
        map.put("VehicleColor", carColor); //车辆颜色
        map.put("VehicleBand", vehicleType); //车辆类型
        //临时运输车辆	1E0667B0-90D7-46A9-8A68-50D7E4673759
        //员工车辆	C97611CE-C6EA-4582-9680-55934B79F9F7
        //公务车辆	2882BCD7-44A2-41A1-BE7D-F2C1304DBF88
        //外协车辆	A4807A31-2DAA-462C-BC27-FAC3C2044720
        //领导车辆	6CCF9944-A068-4B65-B4C4-3F227CC7671D
        //T7商务车	4A0B5A8D-E905-4267-B1B5-A1CC0B5ABB7A
        map.put("Tcm", "A4807A31-2DAA-462C-BC27-FAC3C2044720");
        map.put("PayType", "0");
        map.put("AccountBalance", "0");
        map.put("TelphoneNo", TelphoneNo);        //车上第一个人的电话
        map.put("OrginazitionId", CRK_ORGANTION_ID);

        map.put("BeginDate", beginDate);
        map.put("EndDate", endDate);
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
        map.put("Gid", Gid);
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
        LogUtils.info("外协入厂请求同步-出入口系统更新 接口地址 {} \n 请求参数 {}", crkUpdateUrl, toCrkString);

        Response response;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            LogUtils.info("外协更新入厂请求同步-出入口系统更新 返回结果 \n {}", str);
            JSONObject obj = JSON.parseObject(str);
            if(obj.get("IsSucess") != null){
                String IsSucess = obj.get("IsSucess").toString();
                LogUtils.info("外协更新入厂请求同步-出入口系统更新成功：{}" , IsSucess);
                return IsSucess;
            }
        } catch (IOException e) {
            LogUtils.info("外协接口调用失败，信息{}",e.getMessage());
        }
        return "false";
    }
}
