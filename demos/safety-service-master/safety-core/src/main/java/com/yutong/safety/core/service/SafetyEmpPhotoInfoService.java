package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.UpdatePhotoEnum;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.param.web.EmpowerDeviceListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.enter.search.EmpCardParam;
import com.yutong.safety.core.db.entity.log.SafetyLogEmployeeIssuedDO;
import com.yutong.safety.core.db.entity.log.param.LogEmployeeIssuedListParam;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleVO;
import com.yutong.safety.core.db.entity.system.vo.EmployeeMessageVO;
import com.yutong.safety.core.db.mapper.SafetyBlackMessageDOMapper;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.dto.device.SafetyRegionDeviceInfoDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.build.SafetyBuildAutoDeptHandler;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.device.SafetyRegionDeviceInfoService;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.enter.SafetyEmpCardService;
import com.yutong.safety.core.service.log.SafetyLogEmployeeIssuedService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import com.yutong.safety.core.service.park.SafetyParkPowerRuleService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import static com.yutong.safety.core.service.SafetyDataSendLogService.sendPost;

@Slf4j
@Service
public class SafetyEmpPhotoInfoService {

    @Value("${iam.empInfo.url}")
    private String empInfoUrl;

    @Value("${iam.empInfoForHour.url}")
    private String empInfoUrlForHour;

    @Value("${iam.emplyee.byWorkNo.url}")
    private String iamEmployForWorkNoUrl;

    @Value("${iam.updatePhoto.url}")
    private String updatePhotoUrl;

    @Value("${iam.FaceModify.url}")
    private String faceModifyUrl;

    @Value("${iam.FaceIssue.url}")
    private String faceIssueUrl;

    @Value("${iam.Project.url}")
    private String projectUrl;

    @Value("${iam.DmsDeviceRead.url}")
    private String dmsDeviceReadUrl;

    @Value("${iam.getPhotoUrl.url}")
    private String employeeByNoUrl;

    @Value("${power.default.factory}")
    private String defaultDoorPower;

    @Value("${iam.apikey}")
    private String apikey;

    @Value("${iam.CardModify.url}")
    String cardModifyUrl;

    @Value("${crk.EditStaff.url}")
    private String editStaffUrl;

    @Value("${crk.QueryStaff.url}")
    private String crkQueryStaffUrl;

    @Value("${crk.IssueDoorView.url}")
    private String doorUrl;

    @Value("${crk.Unregister.url}")
    private String doorUnregisterUrl;
    //卡注销入参
    private static String cardParam ="{\"SubSystems\":[2],\"DepositMoney\":0,\"VmsOperMoney\":0,\"CmsOperMoney\":0,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":1,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10卡注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"&Gid\",\"ProjectGids\":[\"&Gid\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";

    private static final String targetId="ABYJS";

    private static final String isAll="false";

    //下发类型的字典FIELD_TYPE
    private final static String FIELD_ISSUE_TYPE = "logIssueType";
    //生活照更新定时任务
    private static final String ISSUED_TYPE_CODE = "log_issue_type_3";
    //人脸凭证下发定时任务
    private static final String ISSUED_TYPE_CODE2 = "log_issue_type_2";
    //员工发卡
    private static final String ISSUED_TYPE_CODE6 = "log_issue_type_6";

    //人行默认组织ID
    private static final String CRK_DEFAUTL_ORGAN_ID = "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7";
    //出入口默认项目GID
    private static final String CRK_DEFAULT_GID = "FF7F139D-B80C-46D0-B330-1118F00255B7";
    //新员工人脸发行
    private static final String ISSUED_TYPE_CODE5 = "log_issue_type_5";
    //人脸不合格更新发行
    private static final String ISSUED_TYPE_CODE7 = "log_issue_type_7";

    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private MailCenterUtils mailCenterUtils;
    @Resource
    private DataSource yktDataSource;

    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Resource
    private SafetyLogEmployeeIssuedService safetyLogEmployeeIssuedService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;
    @Resource
    private SafetyRegionDeviceInfoService safetyRegionDeviceInfoService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Autowired
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;
    @Autowired
    private SafetyParkPowerRuleService safetyParkPowerRuleService;
    @Autowired
    private SafetyEmpCardService safetyEmpCardService;
    @Autowired
    private SafetySystemParamService safetySystemParamService;
    @Autowired
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Autowired
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyBlackMessageDOMapper safetyBlackMessageDOMapper;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyBuildAutoDeptHandler safetyBuildAutoDeptHandler;

    /**
     * @author lkp;
     * 调用中台判断人员是否有效,及获取人员信息
     * 调用出入口接口下发人脸权限
     * @param emps 员工信息
     */
    private void getEmpInfoAndSendData(List<Map<String, String>> emps) {
        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();
        //获取项目编号 一次性获取
        String[] productId = getGids();
        //获取设备号 一次性获取
        String[] deviceIds = getDevNos(productId);
        SafetyFieldManageDO filed = safetyFieldManageService.getByFiledCode(FIELD_ISSUE_TYPE,ISSUED_TYPE_CODE2);
        String issuedType;
        String issuedTypeDesc;
        String operateUser = "定时任务";
        String issuedBean;
        String issueMethod;
        String issuedDate = DateUtils.format(new Date(),2);
        if(!ObjectUtils.isEmpty(filed)){
            issuedType = filed.getFieldName();
            issuedTypeDesc = filed.getRemark();
            issuedBean = filed.getFieldValue(); //重新下发调用Bean
            issueMethod = filed.getEnvirInfo(); //重新下发调用method
        } else {
            issueMethod = "sendImgOnePerson";
            issuedBean = "SafetyEmpPhotoInfoService";
            issuedTypeDesc = "";
            issuedType = "人脸凭证下发定时任务";
        }
        List<SafetyLogEmployeeIssuedDO> successEmps = new ArrayList<>();
        String beginTime = DateUtils.format(new Date(),2);
        for (Map<String, String> emp : emps) {
            try {
                Map<String, String> data = emp;
                String empNo = data.get("empNo");
                //获取生活照
                String lifePhoto = getPhoto(empNo, 1);
                if (org.springframework.util.StringUtils.isEmpty(lifePhoto)) {
                    //获取证件照
                    lifePhoto = getPhoto(empNo, 0);
                }
                //添加下发失败日志记录逻辑
                SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(empNo,
                        data.get("empName"),lifePhoto,issuedDate,issuedType,issuedTypeDesc,"1",operateUser,
                        ISSUED_TYPE_CODE2,issuedBean,issueMethod);

                if (StringUtils.isEmpty(lifePhoto) || !lifePhoto.contains("http")) {
                    LogUtils.info("获取lifePhoto为空！");
                    recordLog(logIssuedDOs,logIssuedDO,new JSONObject(),"获取lifePhoto为空！");
                    continue;
                }
                JSONObject params = new JSONObject();
                JSONObject Staff = new JSONObject();

                String name = data.get("empName");
                String workNo = data.get("empNo");
                //图片转化为二进制
                lifePhoto = lifePhoto + "&apikey=" + apikey;
                byte[] imageBytes = getPictureDataInputStream(lifePhoto);
                String base64_str = DatatypeConverter.printBase64Binary(imageBytes);
                //员工号
                Staff.put("StaffNo", workNo);
                //姓名
                Staff.put("StaffName", name);
                Staff.put("OrganizationId", CRK_DEFAUTL_ORGAN_ID);
                Staff.put("Gid", productId[0]);
                //开始日期
                params.put("BeginTime", beginTime);
                //结束日期
                String endDate = safetyOutsiderPromiseChangeService.getEndDateByEmpNo(workNo);
                params.put("EndTime", endDate + " 23:59:59");
                params.put("AuthorizeDeviceNo", deviceIds);
                params.put("ProjectGids", productId);
                params.put("Staff", Staff);
                params.put("Gid", productId[0]);
                params.put("OperType", 0);
                params.put("StaffState", 2);
                params.put("FacePicture", base64_str);

                //发行场景1（有人员信息，没有授权）发行
                Map<String,Object> requestReturn= handleRequest(faceIssueUrl,params,name,workNo);
                //添加下发失败日志记录逻辑
                boolean issuedStats = (boolean) requestReturn.get("issuedStats");
                if(!issuedStats){
                    String errorMsg = (String) requestReturn.get("errorMsg");
                    LogUtils.error("员工"+workNo+"人脸发行场景1执行异常={}",errorMsg);
                    //
                    params.put("OperType", 0);
                    params.put("StaffState", 1);
                    Map<String,Object> requestReturn1= handleRequest(faceIssueUrl,params,name,workNo);
                    //添加下发失败日志记录逻辑
                    boolean issuedStats1 = (boolean) requestReturn1.get("issuedStats");
                    if(!issuedStats1){
                        String errorMsg1 = (String) requestReturn1.get("errorMsg");
                        LogUtils.error("员工"+workNo+"人脸发行场景2执行异常={}",errorMsg1);
                        recordLog(logIssuedDOs,logIssuedDO,params,errorMsg1);
                    }else{
                        logIssuedDO.setPowerEndDate(beginTime);
                        logIssuedDO.setPowerEndDate(endDate);
                        successEmps.add(logIssuedDO);
                    }
                }else{
                    logIssuedDO.setPowerEndDate(beginTime);
                    logIssuedDO.setPowerEndDate(endDate);
                    successEmps.add(logIssuedDO);
                }
            } catch (Exception e) {
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"人脸下发执行异常={}\"}", e.getMessage());
            }
        }
        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
        //权限更新到安保大门明细表中
        if(!CollectionUtils.isEmpty(successEmps)){
            try{
                dealEmpDoorData(successEmps);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"批量保存大门权限异常，{}\"}",e);
            }
        }
    }

    /**
     * 获取照片 优先获取生活照,没有生活照,获取证件照
     * 照片类型：0证件照，1生活照
     */
    private String getPhoto(String empNo, int type){
        //获取员工照片
        JSONObject param = new JSONObject();
        param.put("targetId",targetId);
        param.put("workNo",empNo);
        param.put("photoType",type);
        Map<String,String> headMap = new HashMap<>();
        headMap.put("Content-Type","application/json");
        String result = webserviceUtil.sendHttp(employeeByNoUrl,"2", param.toJSONString(), headMap,"POST");
        if (org.springframework.util.StringUtils.isEmpty(result)) {
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
        if (org.springframework.util.StringUtils.isEmpty(lifePhoto)){
            LogUtils.info("获取lifePhoto为空！");
            return "";
        }
        if(lifePhoto.indexOf("yutong.com") > 0){
            return lifePhoto;
        }
        return "";
    }

    /**
     * 定时更新员工照片信息
     */
    public void updatePhotoIncrementInfo(String dateTime){
        SafetyFieldManageDO filed = safetyFieldManageService.getByFiledCode(FIELD_ISSUE_TYPE,ISSUED_TYPE_CODE);
        String issuedType;
        String issuedTypeDesc;
        String operateUser = "定时任务";
        String issuedBean;
        String issueMethod;
        if(!ObjectUtils.isEmpty(filed)){
            issuedType = filed.getFieldName();
            issuedTypeDesc = filed.getRemark();
            issuedBean = filed.getFieldValue(); //重新下发调用Bean
            issueMethod = filed.getEnvirInfo(); //重新下发调用method
        } else {
            issueMethod = "modifyImgOnePerson";
            issuedBean = "SafetyEmpPhotoInfoService";
            issuedTypeDesc = "";
            issuedType = "生活照更新定时任务";
        }

        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();

        if(StringUtils.isEmpty(dateTime)){
            dateTime = DateUtils.format(LocalDateTime.now().minusDays(1).toDate(), 2);
        }

        JSONObject resultJSON;
        JSONObject userJson;
        JSONArray records;
        JSONObject record;
        int pageSize;
        byte[] imageBytes;

        //获取项目编号
        String[] productId = getGids();
        //获取设备号
        String[] deviceIds = getDevNos(productId);
        //定义url
        String url = String.format(empInfoUrl, isAll, dateTime, targetId, 1);
        //根据url从中台获取增量信息
        String result = webserviceUtil.sendHttpForGet(url);
        if (StringUtils.isEmpty(result)) {
            LogUtils.info("中台接口返回信息为空");
            return;
        }
        userJson=(JSONObject) JsonUtils.parse(result);
        if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
            LogUtils.info("中台获取数据失败！");
            return;
        }
        //获取result对象
        resultJSON = userJson.getJSONObject("result");
        if (ObjectUtils.isEmpty(resultJSON)){
            LogUtils.info("获取result对象为空！");
            return;
        }
        //获取总页码数
        pageSize = resultJSON.getInteger("pages");
        if (pageSize < 1) {
            return;
        }
        String beginTime = DateUtils.format(new Date(),2);
        List<SafetyLogEmployeeIssuedDO> successEmps = new ArrayList<>();
        JSONArray ret = new JSONArray();
        for (int i = 1; i <=pageSize ; i++) {
            String issuedDate = DateUtils.format(new Date(),2);
            if(i>1){
                url = String.format(empInfoUrl, isAll, dateTime, targetId, i);
                result = webserviceUtil.sendHttpForGet(url);
                if (!StringUtils.isNotEmpty(result)) {
                    continue;
                }
                userJson = (JSONObject) JsonUtils.parse(result);
                if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
                    continue;
                }
                resultJSON = userJson.getJSONObject("result");
            }

            records = resultJSON.getJSONArray("records");

            if (ObjectUtils.isEmpty(records)) {continue;}
            Map<String, Map<String,String>> map = dealData(records);
            
            Map<String,String> dateMap = map.get("dateMap");
            Map<String,String> devicesMap = map.get("devicesMap");
            Map<String,String> empCardMap = map.get("empCardMap"); //empNo,crkId

            for (int j = 0; j < records.size(); j++) {
                String workNo = "";
                try{
                    record = records.getJSONObject(j);
                    if (ObjectUtils.isEmpty(record)
                            || StringUtils.isBlank(record.getString("lifePhoto"))
                            || "DISABLED".equals(record.getString("status"))) {
                        continue;
                    }
                    JSONObject params = new JSONObject();
                    JSONObject Staff = new JSONObject();

                    String name = record.getString("name");
                    workNo = record.getString("workNo");

                    // 增加判断，生活照更新，判断人脸有没有下发过，未下发过人脸的不进行生活照更新 从员工台账表中判断safety_employee_message
                    SafetyEmployeeMessageDO employeeMessageInfo = safetyEmployeeMessageService.selectInfoByEmpNo(workNo);
                    if (employeeMessageInfo == null || employeeMessageInfo.getFaceIssueFlag() == 0) {
                        LogUtils.info("{}员工人脸未发行，不执行生活照更新",workNo);
                        continue;
                    }

                    String lifePhoto = record.getString("lifePhoto");
                    //图片转化为二进制
                    String s = String.format(updatePhotoUrl, lifePhoto);
                    imageBytes = getPictureDataInputStream(s);
                    String base64_str = DatatypeConverter.printBase64Binary(imageBytes);

                    //员工号
                    Staff.put("StaffNo", workNo);
                    //姓名
                    Staff.put("StaffName", name);
                    Staff.put("OrganizationId", CRK_DEFAUTL_ORGAN_ID);
                    Staff.put("Gid", productId[0]);
                    //开始日期
                    params.put("BeginTime",beginTime);
                    //结束日期
                    String endDate = (dateMap != null && !StringUtils.isEmpty(dateMap.get(workNo)))?dateMap.get(workNo):"2099-12-31";

                    params.put("EndTime",endDate+" 00:00:00");
                    params.put("AuthorizeDeviceNo", deviceIds);
                    params.put("ProjectGids", productId);
                    params.put("Staff", Staff);
                    params.put("Gid", productId[0]);
                    params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                    params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
                    params.put("FacePicture", base64_str);
                    String crkId = empCardMap.get(workNo);
                    if(StringUtils.isNotBlank(crkId)){
                        //查询出入口人事信息，重新设置组织ID
                        Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                        String orgid = crkEmpMap.get("orgid");
                        if(StringUtils.isNotBlank(orgid)){
                            Staff.put("OrganizationId", orgid);
                        }
                    }

                    //1.根据员工号获取授权厂区及设备号 获取不到取默认值
                    String devices = (devicesMap != null && !StringUtils.isEmpty(devicesMap.get(workNo)))?devicesMap.get(workNo):"";
                    try{
                        //判断devices不为空，走外包外协权限下发逻辑
                        if(StringUtils.isNotEmpty(devices)){
                            Map<String, List<String>> deviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(devices);
                            String finalWorkNo = workNo;
                            deviceGidMap.forEach((key, value) -> {
                                if(CollectionUtils.isEmpty(value)){
                                    return;
                                }
                                //增加下发异常的日志记录
                                SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(finalWorkNo,
                                        name,lifePhoto,issuedDate,issuedType,issuedTypeDesc,"1",operateUser,
                                        ISSUED_TYPE_CODE,issuedBean,issueMethod);
                                String[] ProjectGids = new String[]{key};
                                Staff.put("Gid", key);
                                params.put("AuthorizeDeviceNo", value);
                                params.put("ProjectGids", ProjectGids);
                                params.put("Staff", Staff);
                                params.put("Gid", key);

                                Map<String,Object> requestReturn= handleRequest(faceModifyUrl,params,name,finalWorkNo);
                                boolean issuedStats = (boolean) requestReturn.get("issuedStats");
                                if(!issuedStats){
                                    String errorMsg = (String) requestReturn.get("errorMsg");
                                    ret.add(finalWorkNo + "人脸修改失败原因：" + errorMsg);
                                    LogUtils.error("员工"+finalWorkNo+"人脸修改下发执行异常={}",errorMsg);
                                    recordLog(logIssuedDOs,logIssuedDO,params,errorMsg);
                                }
                            });
                        }else{
                            //2.正式工下发逻辑
                            //增加下发异常的日志记录
                            SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(workNo,
                                    name,lifePhoto,issuedDate,issuedType,issuedTypeDesc,"1",operateUser,
                                    ISSUED_TYPE_CODE,issuedBean,issueMethod);
                            Map<String,Object> requestReturn= handleRequest(faceModifyUrl,params,name,workNo);
                            boolean issuedStats = (boolean) requestReturn.get("issuedStats");
                            if(!issuedStats){
                                String errorMsg = (String) requestReturn.get("errorMsg");
                                ret.add(workNo + "人脸修改失败原因：" + errorMsg);
                                LogUtils.error("员工"+workNo+"人脸改写下发执行入参={}，异常={}",params.toJSONString(),errorMsg);
                                recordLog(logIssuedDOs,logIssuedDO,params,errorMsg);
                            }else{
                                logIssuedDO.setPowerBeginDate(beginTime);
                                logIssuedDO.setPowerEndDate(endDate);
                                successEmps.add(logIssuedDO);
                            }
                        }
                    }catch (Exception e1){
                        //增加下发异常的日志记录
                        SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(workNo,
                                name,lifePhoto,issuedDate,issuedType,issuedTypeDesc,"1",operateUser,
                                ISSUED_TYPE_CODE,issuedBean,issueMethod);
                        LogUtils.info("{}人脸修改失败原因：{}", workNo, e1);
                        recordLog(logIssuedDOs,logIssuedDO,params,e1.getMessage());
                    }
                }catch(Exception e){
                    LogUtils.info("{}人脸修改失败原因：{}", workNo, e);
                    ret.add(workNo + "人脸修改失败原因：" + e.getMessage());
                }
            }
        }
        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"批量保存下发异常日志异常，{}\"}",e);
            }
        }
        //权限同步到安保大门明细
        if(!CollectionUtils.isEmpty(successEmps)){
            try{
                dealEmpDoorData(successEmps);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"批量保存大门权限异常，{}\"}",e);
            }
        }
        if(!ret.isEmpty()){
            mailCenterUtils.sendMail("人脸权限下发失败",ret.toString(),"kongwk@yutong.com","koaadmin","koaadmin@yutong.com");
            mailCenterUtils.sendMail("人脸权限下发失败",ret.toString(),"gaoweia@yutong.com","koaadmin","koaadmin@yutong.com");
        }
    }

    /**
     * 处理员工信息，及外协人员的授权厂区
     * @param records
     * @return
     */
    public Map<String,Map<String,String>> dealData(JSONArray records){
        Map<String, Map<String,String>> resultMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        Map<String,String> dateMap = new HashMap<>();
        Map<String,String> devicesMap = new HashMap<>();
        for (int j = 0; j < records.size(); j++) {
            JSONObject record = records.getJSONObject(j);
            if (ObjectUtils.isEmpty(record)
                    || StringUtils.isBlank(record.getString("lifePhoto"))
                    || "DISABLED".equals(record.getString("status"))) {
                continue;
            }
            String workNo = record.getString("workNo");
            if(!StringUtils.isEmpty(workNo)){list.add(workNo);}
        }

        if(CollectionUtils.isEmpty(list)){return resultMap;}
        //结束日期
        List<SafetyOutsiderPromiseDTO> endDateByEmpNoList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(list);
        if(!CollectionUtils.isEmpty(endDateByEmpNoList)){
            dateMap = endDateByEmpNoList.stream().collect(Collectors.toMap(
                    SafetyOutsiderPromiseDTO::getEmpNo,
                    SafetyOutsiderPromiseDTO::getServiceEndDate,
                  (existingValue, newValue) -> newValue
            ));
        }
        resultMap.put("dateMap",dateMap);

        //根据员工号获取授权厂区及设备号 获取不到取默认值
        devicesMap = safetyOutsiderPromiseChangeService.findFactoryByEmpNoList(list);
        resultMap.put("devicesMap",devicesMap);
        //获取员工发卡记录
        Map<String,String> empCardMap = new HashMap<>();
        List<SafetyEmpCard> empCards = safetyEmpCardService.selectEmpCardDatasByEmpList(list);
        if(!CollectionUtils.isEmpty(empCards)){
            empCardMap = empCards.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, SafetyEmpCard::getCrkId));
        }
        resultMap.put("empCardMap",empCardMap);
        return resultMap;
    }

    private JSONObject fetchAndParseJson(String url) {
        try {
            String result = webserviceUtil.sendHttpForGet(url);
            if (StringUtils.isEmpty(result)) {
                LogUtils.info("中台接口返回信息为空");
                return null;
            }
            return (JSONObject) JsonUtils.parse(result);
        } catch (Exception e) {
            LogUtils.error("HTTP请求或JSON解析失败: " + e.getMessage());
            return null;
        }
    }


    /**
     * 定时更新员工照片信息-新逻辑
     */
    public void updatePhotoIncrementInfoNew(String dateTime){
        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();
        if(StringUtils.isEmpty(dateTime)){
            //默认近半小时的变更人员
            dateTime = DateUtils.format(DateUtils.addMinutes(new Date(),-31), 2);
        }
        String endTime = DateUtils.format(new Date(),2);
        LogUtils.info("生活照更新起止时间{}，{}",dateTime,endTime);
        JSONObject resultJSON;
        int pageSize;
        //定义url
        String url = String.format(empInfoUrlForHour, isAll, dateTime,endTime,targetId, 1);
        JSONObject userJson = fetchAndParseJson(url);
        if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
            LogUtils.info("中台获取数据失败！");
            return;
        }
        //获取result对象
        resultJSON = userJson.getJSONObject("result");
        if (ObjectUtils.isEmpty(resultJSON)){
            LogUtils.info("获取result对象为空！");
            return;
        }
        //获取总页码数
        pageSize = resultJSON.getInteger("pages");
        if (pageSize < 1) {
            return;
        }
        //2.取值正式员工大门人脸各厂区对应设备集合
        EmpowerDeviceListParam empowerParam = new EmpowerDeviceListParam();
        empowerParam.setEmpowerType("正式员工");
        empowerParam.setRegionName("大门人脸");
        List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);

        EmpowerDeviceListParam empowerParamForWX = new EmpowerDeviceListParam();
        empowerParamForWX.setEmpowerType("外协");
        empowerParamForWX.setRegionName("大门人脸");
        List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParamForWX);
        if(CollectionUtils.isEmpty(zsEmpowerDeviceS) ||CollectionUtils.isEmpty(wxEmpowerDeviceS)){
            LogUtils.error("\"code\":\"9999\",\"msg\":\"正式员工/外协的大门人脸设备未维护，请维护!\"");
        }
        List<SafetyLogEmployeeIssuedDO> successEmps = new ArrayList<>();
        JSONArray ret = new JSONArray();
        for (int i = 1; i <=pageSize ; i++) {
            //只有1页时不再重复查询
            if(i>1){
                url = String.format(empInfoUrlForHour, isAll, dateTime,endTime,targetId, i);
                userJson = fetchAndParseJson(url);;
                if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
                    continue;
                }
                resultJSON = userJson.getJSONObject("result");
            }

            JSONArray records = resultJSON.getJSONArray("records");
            if (ObjectUtils.isEmpty(records)) {continue;}

            //更新员工生活照
            updateLifePhoneTask(records,zsEmpowerDeviceS,wxEmpowerDeviceS);
        }
        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"批量保存下发异常日志异常，{}\"}",e);
            }
        }
        //权限同步到安保大门明细
        if(!ret.isEmpty()){
            mailCenterUtils.sendMail("人脸权限下发失败",ret.toString(),"kongwk@yutong.com","koaadmin","koaadmin@yutong.com");
            mailCenterUtils.sendMail("人脸权限下发失败",ret.toString(),"gaoweia@yutong.com","koaadmin","koaadmin@yutong.com");
        }
    }

    /**
     * 根据中台增量数据更新生活照
     * @param records
     */
    public void updateLifePhoneTask(JSONArray records,List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS
            ,List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS){
        //1.提取中台有更新的员工号
        List<String> empNos = new ArrayList<>();
        Map<String,String> empLifeMap = new HashMap<>();
        for (int j = 0; j < records.size(); j++) {
            JSONObject record = records.getJSONObject(j);
            if (ObjectUtils.isEmpty(record)
                    || StringUtils.isBlank(record.getString("lifePhoto"))
                    || "DISABLED".equals(record.getString("status"))) {
                continue;
            }
            String workNo = record.getString("workNo");
            if(!StringUtils.isEmpty(workNo)){
                empNos.add(workNo);
                empLifeMap.put(workNo,record.getString("lifePhoto"));
            }
        }
        if(CollectionUtils.isEmpty(empNos)){
            LogUtils.info("从中台未获取到有效员工：{}",JSONArray.toJSONString(records));
            return ;
        }
        //1.查询员工台账
        List<SafetyEmployeeMessageDO> curEmployeeList = safetyEmployeeMessageService.getListByStaffNo(empNos);
        if(CollectionUtils.isEmpty(curEmployeeList)){
            LogUtils.info("从员工信息台账表中未获取到有效员工：{}",String.join(",",empNos));
            return;
        }
        List<String> curEmpNos = curEmployeeList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());

        //2.增加获取员工门禁权限的逻辑
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = getBuildDeviceId(curEmpNos);

        //整理人脸下发的数据-大门权限对应设备
        Map<String,Object> modifyDataMap = formatFaceIssueData(curEmployeeList);

        Map<String,ParamDTO> empOwnerPowerMap = (Map<String, ParamDTO>) modifyDataMap.get("empOwnerPowerMap"); //正式工已授权厂区ID
        Map<String,ParkPowerRuleVO> empPowerMap = (Map<String, ParkPowerRuleVO>) modifyDataMap.get("empPowerMap");
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap = (Map<String, SafetyOutsiderPromiseDTO>) modifyDataMap.get("outPowerMap");
        Map<String, SafetyEmpCard> empCardMap = (Map<String, SafetyEmpCard>) modifyDataMap.get("empCardMap");

        List<SafetyEmpCard> modifyCardDatas = new ArrayList<>();
        SafetyLogEmployeeIssuedDO baseIssue = initIssueLog(ISSUED_TYPE_CODE);

        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();

        //4.组装下发参数执行卡发行操作
        curEmployeeList.forEach(emp->{
            //默认截止日期
            String curEndDate = Constants.DEFAULT_SERVER_END;
            EmployeeMessageVO messageVO = new EmployeeMessageVO();
            BeanUtils.copyProperties(emp,messageVO);
            String empNo = emp.getEmpNo();
            SafetyEmpCard curEmpCard = empCardMap.get(empNo);
            //判断是否有人事信息,无人事信息则新增
            if(curEmpCard == null || StringUtils.isBlank(curEmpCard.getCrkId())){
                JSONObject data = new JSONObject();
                data.put("outid",empNo);
                data.put("name",emp.getEmpName());
                data.put("sex",emp.getSex());
                String crkId = sendToCrkEditStaff(data); //正常返回人事ID 执行卡下发 未正常返回,暂不处理
                if(StringUtils.isNotEmpty(crkId)){
                    if(curEmpCard == null){
                        curEmpCard = new SafetyEmpCard();
                    }
                    curEmpCard.setEmpNo(empNo);
                    curEmpCard.setCrkId(crkId);
                    curEmpCard.setCardStatus("0"); //无卡
                    modifyCardDatas.add(curEmpCard);
                }
            }else{
                messageVO.setCrkId(curEmpCard.getCrkId());
            }
            Map<String,String> returnMap = getIssueFactory(emp,empPowerMap,outPowerMap,empOwnerPowerMap,curEndDate);
            String issueFactory = returnMap.get("issueFactory");
            //外协人员使用服务截止日期
            curEndDate = returnMap.get("curEndDate");
            if(StringUtils.isBlank(issueFactory)){
                LogUtils.info("员工{}未找到对应的厂区权限，不执行下发！",empNo);
                return;
            }
            String zsDevices = getEmpowerDevices(emp, issueFactory, "ZS".equals(emp.getUserType()) ? zsEmpowerDeviceS : wxEmpowerDeviceS);
            if(StringUtils.isBlank(zsDevices)){
                LogUtils.info("员工{}厂区{},未找到对应设备，不执行下发！",empNo,issueFactory);
                return;
            }
            //增加逻辑-获取员工富士设备的门禁设备
            List<SafetyBuildEmployeePowerDO> curBuildPower = buildPowerMap.get(empNo);
            Set<String> zsDevicesBuilder = new HashSet<>(Arrays.asList(zsDevices.split(",")));

            if (!CollectionUtils.isEmpty(curBuildPower)) {
                Set<String> curSet = curBuildPower.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.toSet());
                zsDevicesBuilder.addAll(curSet);
            }
            String faceDevices = String.join(",", zsDevicesBuilder);  // 输出a,b
            Map<String, List<String>> zsDeviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(faceDevices);
            //获取生活照
            String lifePhoto = empLifeMap.get(empNo);

            modifyFaceForOne(messageVO,baseIssue,logIssuedDOs,zsDeviceGidMap,curEndDate,lifePhoto, false);
        });

        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
    }

    /**
     * 人脸下发数据整理
     * @param employeeMessageDOList
     */
    private Map<String,Object> formatFaceIssueData(List<SafetyEmployeeMessageDO> employeeMessageDOList){
        Map<String,Object> modifyFaceDataMap = new HashMap<>();

        Map<String,ParamDTO> empOwnerPowerMap = Collections.emptyMap(); //正式工已授权厂区ID
        Map<String,ParkPowerRuleVO> empPowerMap = Collections.emptyMap();
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap = Collections.emptyMap();
        Map<String, SafetyEmpCard> empCardMap = Collections.emptyMap();

        List<String> zsEmpNoList = employeeMessageDOList.stream().filter(e->"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        List<String> wxEmpNoList = employeeMessageDOList.stream().filter(e->!"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());

        //3.获取新增员工的默认厂区规则
        if(!CollectionUtils.isEmpty(zsEmpNoList)){
            List<ParamDTO> empParPowers = safetyParkEmployeePowerInfoService.getUserList(zsEmpNoList);
            if(!CollectionUtils.isEmpty(empParPowers)){
                empOwnerPowerMap = empParPowers.stream().collect(Collectors.toMap(ParamDTO::getEmpNo,e->e));
            } else {
                empOwnerPowerMap = new HashMap<>();
            }
            List<ParkPowerRuleVO> empPowerList = safetyParkPowerRuleService.selectRulesForEmp(zsEmpNoList);
            if(!CollectionUtils.isEmpty(empPowerList)){
                empPowerMap = empPowerList.stream().collect(Collectors.toMap(ParkPowerRuleVO::getEmpNo,e->e));
            } else {
                empPowerMap = new HashMap<>();
            }
        } else {
            empOwnerPowerMap = new HashMap<>();
            empPowerMap = new HashMap<>();
        }

        if(!CollectionUtils.isEmpty(wxEmpNoList)){
            List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNoList);
            if(!CollectionUtils.isEmpty(outsiderList)){
                outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
            } else {
                outPowerMap = new HashMap<>();
            }
        } else {
            outPowerMap = new HashMap<>();
        }
        //获取待下发员工的出入口人事ID
        List<String> allEmpNoList = employeeMessageDOList.stream().map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        List<SafetyEmpCard> empCardDatas = safetyEmpCardService.selectEmpCardDatasByEmpList(allEmpNoList);
        if(CollectionUtils.isEmpty(empCardDatas)){
            empCardMap = new HashMap<>();
        }else{
            empCardMap = empCardDatas.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, safetyEmpCard -> safetyEmpCard));
        }

        modifyFaceDataMap.put("empCardMap",empCardMap);
        modifyFaceDataMap.put("outPowerMap",outPowerMap);
        modifyFaceDataMap.put("empOwnerPowerMap",empOwnerPowerMap);
        modifyFaceDataMap.put("empPowerMap",empPowerMap);

        return modifyFaceDataMap;
    }

    public byte[] getPictureDataInputStream(String path) {
        Response response = null;
        InputStream ins = null;
        ByteArrayOutputStream output = null;
        byte[] data = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder().url(path).method("GET", null).build();
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

    //获取项目编号信息
    public String[] getGids(){
        String r1 = sendPost(projectUrl, "{\"PageSize\":\"1000\"}");
        if(StringUtils.isBlank(r1)){
            return null;
        }
        JSONObject projects = JSONObject.parseObject(r1);
        if(projects.get("Records") == null){
            return null;
        }
        JSONArray projectsRecords = projects.getJSONArray("Records");
        if (ObjectUtils.isEmpty(projectsRecords)) {
            return null;
        }
        String[] productId = new String[1];
        for (int i = 0; i < projectsRecords.size(); i++) {
            JSONObject record = projectsRecords.getJSONObject(i);
            if (record.getString("Name").equals("默认项目")) {
                productId[0] = record.getString("Gid");
                break;
            }
        }
        return productId;
    }

    //获取设备编号信息
    public String[] getDevNos(String[] productId){
        String r2 = sendPost(dmsDeviceReadUrl,
                "{\"where\":\"DevTypeCode=31  and gid='" + productId[0] + "'\",\"PageSize\":\"1000\"}");
        JSONObject devices = JSONObject.parseObject(r2);
        JSONArray devicesRecords = devices.getJSONArray("Records");
        String[] deviceIds = new String[devicesRecords.size()];
        if (ObjectUtils.isEmpty(devicesRecords)) {
            LogUtils.info("设备信息为空");
            return null;
        }
        for (int i = 0; i < devicesRecords.size(); i++) {
            JSONObject record = devicesRecords.getJSONObject(i);
            deviceIds[i] = record.getString("DevNo");
        }
        return deviceIds;
    }

    //根据GID从数据库中获取
    public List<SafetyRegionDeviceInfoDO> getDevNosNew(String[] productId){
        List<String> gids = Arrays.asList(productId);
        SafetyRegionDeviceInfoDTO param = new SafetyRegionDeviceInfoDTO();
        param.setGidList(gids);
        param.setDevTypeCode("31");

        List<SafetyRegionDeviceInfoDO> deviceList = safetyRegionDeviceInfoService.getList( param);

        return deviceList;
    }

    /**
     * 代码欧化-下发逻辑 异常日志逻辑处理
     * @param logIssuedDOs 异常日志集合
     * @param logIssuedDO 异常日志
     * @param params 下发参数
     * @param errorDesc 下发失败描述
     */
    private void recordLog(List<SafetyLogEmployeeIssuedDO> logIssuedDOs,
                           SafetyLogEmployeeIssuedDO logIssuedDO, JSONObject params, String errorDesc) {
        //增加下发异常的日志记录
        JSONObject logObj = JSONObject.parseObject(params.toJSONString());
        //保存日志时，不保存人脸数据
        logObj.remove("FacePicture");
        String logStr = logObj.toJSONString();
        //超长时截取
        if (StringUtils.isNotBlank(logStr)) {
            logIssuedDO.setIssuedParam(logStr.length()>3000?logStr.substring(0,3000):logStr);
        }
        //限制长度
        if (StringUtils.isNotBlank(errorDesc)) {
            logIssuedDO.setExceptionDesc(errorDesc.length()>1000?errorDesc.substring(0,1000):errorDesc);
        }
        logIssuedDOs.add(logIssuedDO);
    }


    /**
     * 代码欧化-下发逻辑调用接口封装
     * @param params 人脸下发入参
     * @param name 下发人员姓名
     * @param workNo 下发人员员工号
     * @return 返回值
     */
    private Map<String,Object> handleRequest(String postUrl,JSONObject params,String name,String workNo){
        boolean issuedStats = true;
        Map<String,Object> retMap = new HashMap<>();
        String r4 = sendPost(postUrl, params.toJSONString());
        LogUtils.info("人脸权限下发name={},workNo={},返回消息={}" , name , workNo, r4);

        if (StringUtils.isBlank(r4)) {
            issuedStats = false;
            r4 = "接口调用异常";
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
     * 重新下发单个员工时方法
     * @param curObj 入参
     * @param employeeCode 下发员工号
     * @param employeeName 下发员姓名
     * @return 返回状态
     */
    public String sendImgOnePerson(JSONObject curObj,String employeeCode,String employeeName){
        curObj.put("StaffState", 2);
        String r4 = sendPost(faceIssueUrl, curObj.toJSONString());
        LogUtils.info("重新下发-人脸发行场景1 name={},workNo={},返回消息={}" , employeeCode , employeeName, r4);
        JSONObject json4 = JSONObject.parseObject(r4);
        if ("200".equals(json4.get("State")+"")) {
            return "SUCCESS";
        }else{
            //人脸发行场景1
            curObj.put("OperType", 0);
            curObj.put("StaffState", 1);
            String issueRet1 = sendPost(faceIssueUrl, curObj.toJSONString());
            LogUtils.info("重新下发-人脸发行场景2 name={},workNo={},返回消息={}" , employeeCode , employeeName, issueRet1);
            JSONObject jsonRet1 = JSONObject.parseObject(issueRet1);
            if ("200".equals(jsonRet1.get("State")+"")) {
                return "SUCCESS";
            }
            return r4;
        }
    }

    /**
     * 重新更新下发单个员工时方法-改写
     * @param curObj 入参
     * @param employeeCode 下发员工号
     * @param employeeName 下发员姓名
     * @return 返回状态
     */
    public String modifyImgOnePerson(JSONObject curObj,String employeeCode,String employeeName){
        //改写人脸
        curObj.put("OperType", 1);
        curObj.put("StaffState", 2);
        String r4 = sendPost(faceModifyUrl, curObj.toJSONString());
        JSONObject json4 = JSONObject.parseObject(r4);
        if ("200".equals(json4.get("State")+"")) {
            return "SUCCESS";
        }else{
            LogUtils.info("重新下发人脸修改失败，参数={}" , curObj.toJSONString());
            return r4;
        }
    }

    /**
     * 成功下发的权限，更新到大门权限明细表中（只处理正式工）
     * @param issuedDOS 日志集合
     */
    public void dealEmpDoorData(List<SafetyLogEmployeeIssuedDO> issuedDOS){
        String nowDate = DateUtils.format(new Date(),1);
        if(CollectionUtils.isEmpty(issuedDOS)){
           return;
        }
        List<String> empList = issuedDOS.stream().map(SafetyLogEmployeeIssuedDO::getEmployeeCode).distinct().collect(Collectors.toList());
        List<SafetyEmployeeMessageDO> empMessageList = safetyEmployeeMessageService.getListByStaffNo(empList);
        if(CollectionUtils.isEmpty(empMessageList)){
            return;
        }
        Map<String,List<SafetyLogEmployeeIssuedDO>> issuedMap = issuedDOS.stream().collect(Collectors.groupingBy(SafetyLogEmployeeIssuedDO::getEmployeeCode));
        if(StringUtils.isBlank(defaultDoorPower)){
            defaultDoorPower = "147,148,150,151,154,159,160,161";
        }
        Map<String, SafetyFieldManageDO> factoryMap = safetyFieldManageService.getFactoryList();

        List<SafetyParkEmployeePowerInfoDO> updateDoorPowerList = new ArrayList<>();

        for(String empNo:empList){
            if(empNo.startsWith("9")){
                List<SafetyLogEmployeeIssuedDO> curIssueList = issuedMap.get(empNo);
                if(CollectionUtils.isEmpty(curIssueList) || curIssueList.get(0) == null){
                    return;
                }
                String beginTime;
                if(StringUtils.isNotBlank(curIssueList.get(0).getPowerBeginDate())){
                    beginTime = curIssueList.get(0).getPowerBeginDate().substring(0,10);
                }else{
                    beginTime = nowDate;
                }
                String endTime = curIssueList.get(0).getPowerEndDate().substring(0,10);

                for(String factoryId:defaultDoorPower.split(",")){
                    String factoryName = "";
                    if(factoryMap.get(factoryId) != null){
                        factoryName = factoryMap.get(factoryId).getFieldName();
                    }
                    SafetyParkEmployeePowerInfoDO parkPower = new SafetyParkEmployeePowerInfoDO();
                    parkPower.setEmployeeCode(empNo);
                    parkPower.setFactoryId(factoryId);
                    parkPower.setFactoryName(factoryName);
                    parkPower.setIsDelete(Short.parseShort("0"));
                    parkPower.setRemark("人脸下发定时任务");
                    parkPower.setDownMark("0");
                    parkPower.setPowerBeginTime(beginTime);
                    parkPower.setPowerEndTime(endTime);

                    updateDoorPowerList.add(parkPower);
                }
            }
        }
        if(!CollectionUtils.isEmpty(updateDoorPowerList)){
            safetyParkPowerProcessService.dealPower(updateDoorPowerList,"ZS");
        }
    }

    private void updateLifePhoneForOne(List<SafetyEmployeeMessageDO> curEmployeeList,
                                         List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS,
                                         List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS){
        List<String> curEmpNos = curEmployeeList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        //2.增加获取员工门禁权限的逻辑
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = getBuildDeviceId(curEmpNos);

        //整理人脸下发的数据-大门权限对应设备
        Map<String,Object> modifyDataMap = formatFaceIssueData(curEmployeeList);

        Map<String,ParamDTO> empOwnerPowerMap = (Map<String, ParamDTO>) modifyDataMap.get("empOwnerPowerMap"); //正式工已授权厂区ID
        Map<String,ParkPowerRuleVO> empPowerMap = (Map<String, ParkPowerRuleVO>) modifyDataMap.get("empPowerMap");
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap = (Map<String, SafetyOutsiderPromiseDTO>) modifyDataMap.get("outPowerMap");
        Map<String, SafetyEmpCard> empCardMap = (Map<String, SafetyEmpCard>) modifyDataMap.get("empCardMap");

        List<SafetyEmpCard> modifyCardDatas = new ArrayList<>();
        SafetyLogEmployeeIssuedDO baseIssue = initIssueLog(ISSUED_TYPE_CODE);

        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        //4.组装下发参数执行卡发行操作
        curEmployeeList.forEach(emp->{
            //默认截止日期
            String curEndDate = Constants.DEFAULT_SERVER_END;
            EmployeeMessageVO messageVO = new EmployeeMessageVO();
            BeanUtils.copyProperties(emp,messageVO);
            String empNo = emp.getEmpNo();
            SafetyEmpCard curEmpCard = empCardMap.get(empNo);
            //判断是否有人事信息,无人事信息则新增
            if(curEmpCard == null || StringUtils.isBlank(curEmpCard.getCrkId())){
                JSONObject data = new JSONObject();
                data.put("outid",empNo);
                data.put("name",emp.getEmpName());
                data.put("sex",emp.getSex());
                String crkId = sendToCrkEditStaff(data); //正常返回人事ID 执行卡下发 未正常返回,暂不处理
                if(StringUtils.isNotEmpty(crkId)){
                    if(curEmpCard == null){
                        curEmpCard = new SafetyEmpCard();
                    }
                    curEmpCard.setEmpNo(empNo);
                    curEmpCard.setCrkId(crkId);
                    curEmpCard.setCardStatus("0"); //无卡
                    modifyCardDatas.add(curEmpCard);
                }
            }else{
                messageVO.setCrkId(curEmpCard.getCrkId());
            }
            Map<String,String> returnMap = getIssueFactory(emp,empPowerMap,outPowerMap,empOwnerPowerMap,curEndDate);
            String issueFactory = returnMap.get("issueFactory");
            //外协人员使用服务截止日期
            curEndDate = returnMap.get("curEndDate");
            if(StringUtils.isBlank(issueFactory)){
                LogUtils.info("员工{}未找到对应的厂区权限，不执行下发！",empNo);
                return;
            }
            String zsDevices = getEmpowerDevices(emp, issueFactory, "ZS".equals(emp.getUserType()) ? zsEmpowerDeviceS : wxEmpowerDeviceS);
            if(StringUtils.isBlank(zsDevices)){
                LogUtils.info("员工{}厂区{},未找到对应设备，不执行下发！",empNo,issueFactory);
                return;
            }
            //增加逻辑-获取员工富士设备的门禁设备
            List<SafetyBuildEmployeePowerDO> curBuildPower = buildPowerMap.get(empNo);
            Set<String> zsDevicesBuilder = new HashSet<>(Arrays.asList(zsDevices.split(",")));

            if (!CollectionUtils.isEmpty(curBuildPower)) {
                Set<String> curSet = curBuildPower.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.toSet());
                zsDevicesBuilder.addAll(curSet);
            }
            String faceDevices = String.join(",", zsDevicesBuilder);  // 输出a,b
            Map<String, List<String>> zsDeviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(faceDevices);
            //获取生活照
            String name =emp.getEmpName();
            //获取生活照
            String lifePhoto = getPhoto(empNo, 1);
            if (StringUtils.isEmpty(lifePhoto)) {
                //获取证件照
                lifePhoto = getPhoto(empNo, 0);
            };

            modifyOrIssueFace(messageVO,baseIssue,logIssuedDOs,zsDeviceGidMap,curEndDate,lifePhoto,errorList,successList);
        });

        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
    }

    /**
     * 自留窗口
     * @param messageDO 员工信息台账
     * @return 返回值
     */
    @Async
    public String modifyLifePhoneByEmp(SafetyEmployeeMessageDO messageDO){
        try {
            List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS = null;
            List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS = null;
            if(messageDO.getEmpNo().startsWith("9")){
                //2.取值正式员工大门人脸各厂区对应设备集合
                EmpowerDeviceListParam empowerParam = new EmpowerDeviceListParam();
                empowerParam.setEmpowerType("正式员工");
                empowerParam.setRegionName("大门人脸");
                zsEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);
            }else{
                EmpowerDeviceListParam empowerParamForWX = new EmpowerDeviceListParam();
                empowerParamForWX.setEmpowerType("外协");
                empowerParamForWX.setRegionName("大门人脸");
                wxEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParamForWX);
            }

            if(CollectionUtils.isEmpty(zsEmpowerDeviceS) && CollectionUtils.isEmpty(wxEmpowerDeviceS)){
                LogUtils.error("\"code\":\"9999\",\"msg\":\"正式员工/外协的大门人脸设备未维护，请维护!\"");
            }
            List<SafetyLogEmployeeIssuedDO> successEmps = new ArrayList<>();
            //更新员工生活照
            List<SafetyEmployeeMessageDO> empList = new ArrayList<>();
            empList.add(messageDO);
            updateLifePhoneForOne(empList,zsEmpowerDeviceS,wxEmpowerDeviceS);
        } catch (Exception e) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"人脸下发执行异常，{}\"}", e);
            return "人脸下发执行异常"+e.getMessage();
        }
        return "";
    }

    private void updateEmpCard(String empNo, String crkId,
                               SafetyEmpCard currentEmpCard,
                               List<SafetyEmpCard> modifyCardDatas) {
        if (currentEmpCard == null) {
            currentEmpCard = new SafetyEmpCard();
        }
        currentEmpCard.setEmpNo(empNo);
        currentEmpCard.setCrkId(crkId);
        currentEmpCard.setCardStatus("0"); // 无卡
        modifyCardDatas.add(currentEmpCard);
    }

    /**
     * 新员工人脸发行
     * @param beginDate 开始日期
     */
    public String issueFaceForNewEmp(String beginDate){
        if(StringUtils.isBlank(beginDate)){
            //默认取T-1天的数据
            beginDate = DateUtils.format(DateUtils.addDays(new Date(),-30),1);
        }
        //1.取出当前时间前新增的员工信息
        String endDate = DateUtils.format(DateUtils.addDays(new Date(),1),1); //获取T+1为截止日期
        EmployeeMessageListParam empParam = new EmployeeMessageListParam();
        empParam.setCreateBegin(beginDate);
        empParam.setCreateEnd(endDate);
        empParam.setFaceIssueFlag(Short.parseShort("0")); //人脸未下发人员
        empParam.setNoEmpNoStart("01"); //排除01开头的员工号
        List<SafetyEmployeeMessageDO> employeeMessageDOList = safetyEmployeeMessageService.selectList(empParam);
        //过滤掉UserType = LD 和 非WX人员组织路径中包含“绿都”的员工信息
        employeeMessageDOList = employeeMessageDOList.stream().filter(li ->(
                (StringUtils.isEmpty(li.getUserType()) || !li.getUserType().equals(Constants.USER_TYPE_LD)) &&
                 (StringUtils.isEmpty(li.getOrgNamePath())
                         ||!li.getOrgNamePath().contains(Constants.ORG_NAME_KEYS)
                         ||(Constants.USER_TYPE_WX.equals(li.getUserType()) && li.getOrgNamePath().contains(Constants.ORG_NAME_KEYS))))).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(employeeMessageDOList)){
            LogUtils.info("获取到员工信息：0条!");
            return "获取到0条数据";
        }

        //1.1 黑名单中的人员禁止下发人脸 条件：状态=禁止入厂，备注=禁止授权条件判断
        employeeMessageDOList = filterBlackEmployee(employeeMessageDOList);

        //2.取值正式员工大门人脸各厂区对应设备集合
        EmpowerDeviceListParam empowerParam = new EmpowerDeviceListParam();
        empowerParam.setEmpowerType("正式员工");
        empowerParam.setRegionName("大门人脸");
        List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);
        empowerParam.setEmpowerType("外协");
        List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);
        if(CollectionUtils.isEmpty(zsEmpowerDeviceS) ||CollectionUtils.isEmpty(wxEmpowerDeviceS)){
            LogUtils.error("\"code\":\"9999\",\"msg\":\"正式员工/外协的大门人脸设备未维护，请维护!\"");
        }

        List<String> zsEmpNoList = employeeMessageDOList.stream().filter(e->"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        List<String> wxEmpNoList = employeeMessageDOList.stream().filter(e->!"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());

        //3.获取新增员工的默认厂区规则
        Map<String,ParamDTO> empOwnerPowerMap; //正式工已授权厂区ID
        Map<String,ParkPowerRuleVO> empPowerMap;
        if(!CollectionUtils.isEmpty(zsEmpNoList)){
            List<ParamDTO> empParPowers = safetyParkEmployeePowerInfoService.getUserList(zsEmpNoList);
            if(!CollectionUtils.isEmpty(empParPowers)){
                empOwnerPowerMap = empParPowers.stream().collect(Collectors.toMap(ParamDTO::getEmpNo,e->e));
            } else {
                empOwnerPowerMap = new HashMap<>();
            }
            List<ParkPowerRuleVO> empPowerList = safetyParkPowerRuleService.selectRulesForEmp(zsEmpNoList);
            if(!CollectionUtils.isEmpty(empPowerList)){
                empPowerMap = empPowerList.stream().collect(Collectors.toMap(ParkPowerRuleVO::getEmpNo,e->e));
            } else {
                empPowerMap = new HashMap<>();
            }
        } else {
            empOwnerPowerMap = new HashMap<>();
            empPowerMap = new HashMap<>();
        }
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap;
        if(!CollectionUtils.isEmpty(wxEmpNoList)){
            List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNoList);
            if(!CollectionUtils.isEmpty(outsiderList)){
                outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
            } else {
                outPowerMap = new HashMap<>();
            }
        } else {
            outPowerMap = new HashMap<>();
        }
        //获取待下发员工的出入口人事ID
        List<String> allEmpNoList = employeeMessageDOList.stream().map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        List<SafetyEmpCard> empCardDatas = safetyEmpCardService.selectEmpCardDatasByEmpList(allEmpNoList);
        Map<String, SafetyEmpCard> empCardMap;
        if(CollectionUtils.isEmpty(empCardDatas)){
            empCardMap = new HashMap<>();
        }else{
            empCardMap = empCardDatas.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, safetyEmpCard -> safetyEmpCard));
        }
        //2.设置新员工默认厂区权限
        try{
            initEmpDefaultPark(employeeMessageDOList,empPowerMap,empOwnerPowerMap);
        }catch (Exception e){
            LogUtils.error("设置员工默认厂区逻辑异常",e);
        }
        //3.处理员工组织自动授权逻辑
        try{
            safetyBuildAutoDeptHandler.initAutoBuildForNewEmp(allEmpNoList);
        }catch (Exception e){
            LogUtils.error("员工组织自动授权逻辑异常",e);
        }

        //4.根据授权规则处理完后，重新厂区权限
        if(!CollectionUtils.isEmpty(zsEmpNoList)){
            //优先获取大门厂区的权限
            List<ParamDTO> empParPowers = safetyParkEmployeePowerInfoService.getUserList(zsEmpNoList);
            empOwnerPowerMap = empParPowers.stream().collect(Collectors.toMap(ParamDTO::getEmpNo,e->e));
        }
        if(!CollectionUtils.isEmpty(wxEmpNoList)){
            List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNoList);
            if(!CollectionUtils.isEmpty(outsiderList)){
                outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
            }
        }
        //5.增加获取员工富士设备门禁权限的逻辑
        String curDate = DateUtils.format(new Date(),1);
        BuildEmployeePowerListParam powerParam = new BuildEmployeePowerListParam();
        powerParam.setEmpCodeList(allEmpNoList);
        powerParam.setDeviceType("2");//只查询富士的设备
        powerParam.setPowerTimeEnd(curDate);
        List<SafetyBuildEmployeePowerDO> empBuildPowerConcat = safetyBuildEmployeePowerService.getEmpPowerList(powerParam);
        Map<String, List<SafetyBuildEmployeePowerDO>> buildPowerMap = empBuildPowerConcat.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));

        //6.增加获取员工新开普设备门禁权限的逻辑
        powerParam.setDeviceType("1");
        List<BuildEmployeePowerVO> xkpBuildPowerConcat = safetyBuildEmployeePowerService.selectEmpPowerList(powerParam);
        Map<String,List<BuildEmployeePowerVO>> xkpBuildPowerMap = xkpBuildPowerConcat.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));

        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();
        List<SafetyLogEmployeeIssuedDO> successEmps = new ArrayList<>();
        Set<String> modifyEmpSet = new HashSet<>();

        List<SafetyEmpCard> modifyCardDatas = new ArrayList<>();
        SafetyLogEmployeeIssuedDO baseIssue = initIssueLog(ISSUED_TYPE_CODE5);
        //4.组装下发参数执行卡发行操作
        Map<String, SafetyOutsiderPromiseDTO> finalOutPowerMap = outPowerMap;
        Map<String, ParamDTO> finalEmpOwnerPowerMap = empOwnerPowerMap;
        employeeMessageDOList.forEach(emp->{
            //默认截止日期
            String curEndDate = Constants.DEFAULT_SERVER_END;
            EmployeeMessageVO messageVO = new EmployeeMessageVO();
            BeanUtils.copyProperties(emp,messageVO);
            String empNo = emp.getEmpNo();
            SafetyEmpCard curEmpCard = empCardMap.get(empNo);
            //判断是否有人事信息,无人事信息则新增
            if(curEmpCard == null || StringUtils.isBlank(curEmpCard.getCrkId())){
                //补充逻辑，若人事信息为空时，调用一卡通接口再获取一次
                String crkId = safetyBaseHandler.getCrkStaffId(empNo);
                if(StringUtils.isBlank(crkId)){
                    //取不到时再创建
                    JSONObject data = new JSONObject();
                    data.put("outid",empNo);
                    data.put("name",emp.getEmpName());
                    data.put("sex",emp.getSex());
                    crkId = sendToCrkEditStaff(data);
                }else{
                    //查询出的人事ID需要记录，后续根据人事ID获取ORGID
                    messageVO.setCrkId(crkId);
                }
                if(StringUtils.isNotEmpty(crkId)){
                    updateEmpCard(empNo,crkId,curEmpCard,modifyCardDatas);
                }
            }else{
                messageVO.setCrkId(curEmpCard.getCrkId());
            }
            Map<String,String> returnMap = getIssueFactory(emp,empPowerMap, finalOutPowerMap, finalEmpOwnerPowerMap,curEndDate);
            String issueFactory = returnMap.get("issueFactory");
            curEndDate = returnMap.get("curEndDate");

            LogUtils.info("员工{}，对应的厂区权限{}",empNo,issueFactory);
            if(StringUtils.isBlank(issueFactory)){
                LogUtils.info("员工{}未找到对应的厂区权限，不执行下发！",empNo);
                return;
            }

            //下发新开普权限
            List<BuildEmployeePowerVO> xkpBuildList = xkpBuildPowerMap.get(empNo);
            if(!CollectionUtils.isEmpty(xkpBuildList)){
                try{
                    safetyBaseHandler.sendXkpEmpPower(empNo,xkpBuildList,"开通",curDate,curEndDate);
                }catch (Exception el){
                    LogUtils.error("新开普权限下发异常",el);
                }
            }
            String zsDevices = getEmpowerDevices(emp, issueFactory, "ZS".equals(emp.getUserType()) ? zsEmpowerDeviceS : wxEmpowerDeviceS);
            List<SafetyBuildEmployeePowerDO> employeePowerDOList = buildPowerMap.get(empNo);
            if(!CollectionUtils.isEmpty(employeePowerDOList)){
                zsDevices += "," + employeePowerDOList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).filter(li -> !StringUtils.isEmpty(li)).collect(Collectors.joining(","));
            }
            Map<String, List<String>> zsDeviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(zsDevices);

            //下发人脸
            issueFaceForOne(messageVO,baseIssue,logIssuedDOs,zsDeviceGidMap,successEmps,curEndDate,modifyEmpSet);
        });

        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
        //权限更新到安保大门明细表中
        if(!CollectionUtils.isEmpty(successEmps)){
            try{
                dealEmpDoorData(successEmps);
            }catch (Exception e){
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"批量保存大门权限异常，{}\"}",e);
            }
        }
        //更新员工状态
        if(!CollectionUtils.isEmpty(modifyEmpSet)){
            try {
                List<String> list = modifyEmpSet.stream().collect(Collectors.toList());
                safetyEmployeeMessageService.modifyFaceIssueFlag(list);
            }catch (Exception e){
                LogUtils.error("批量更新员工发卡状态",e);
            }
        }
        if(!CollectionUtils.isEmpty(modifyCardDatas)){
            try {
                safetyEmpCardService.saveOrUpdate(modifyCardDatas);
            }catch (Exception e){
                LogUtils.error("批量更新新增人事信息异常",e);
            }
        }
        return "执行完成";
    }

    /**
     * 处理员工默认厂区权限，并更新入库
     * @param empList 员工清单
     * @param empPowerMap 员工默认厂区集合（根据默认规则计算得出）
     * @param empOwnerPowerMap 员工厂区现有权限集合
     */
    public void initEmpDefaultPark(List<SafetyEmployeeMessageDO> empList,
                                   Map<String, ParkPowerRuleVO> empPowerMap,
                                   Map<String,ParamDTO> empOwnerPowerMap){
        if(CollectionUtils.isEmpty(empList)){
            return;
        }
        String beginDate = DateUtils.format(new Date(),1);
        List<SafetyParkEmployeePowerInfoDO> updateParkList = new ArrayList<>();

        for (SafetyEmployeeMessageDO curEmp : empList) {
            String userType = curEmp.getUserType();
            String empNo = curEmp.getEmpNo();

            // 正式工需要处理的厂区
            if (!"WX".equals(userType)) {
                // 优先查询已授权的大门权限
                ParamDTO curOwner = empOwnerPowerMap.get(empNo);
                if (curOwner != null && StringUtils.isNotEmpty(curOwner.getFactoryId())) {
                    continue;
                }

                String defaultFactory = getDefaultFactoryFormMap(empNo,empPowerMap);
                if (StringUtils.isBlank(defaultFactory)) {
                    continue;
                }

                for (String factoryId : defaultFactory.split(",")) {
                    SafetyParkEmployeePowerInfoDO parkPowerDO = safetyBuildAutoDeptHandler.formatParkPower(curEmp, factoryId, beginDate, Constants.DEFAULT_SERVER_END);
                    updateParkList.add(parkPowerDO);
                }
            }
        }
        if(!CollectionUtils.isEmpty(updateParkList)){
            safetyParkPowerProcessService.dealPower(updateParkList,"ZS");
        }
    }

    /**
     *
     * @param empNo 员工号
     * @param empPowerMap 默认厂区规则
     * @return
     */
    private String getDefaultFactoryFormMap(String empNo,Map<String, ParkPowerRuleVO> empPowerMap) {
        ParkPowerRuleVO curPowerRule = empPowerMap.get(empNo);
        if (curPowerRule != null && StringUtils.isNotBlank(curPowerRule.getFactoryPower())) {
            return curPowerRule.getFactoryPower();
        }
        return defaultDoorPower;
    }

    /**
     * 获取下发的工厂权限
     * @param emp 员工信息
     * @param empPowerMap 正式工厂区权限Map
     * @param outPowerMap 外协厂区权限Map
     * @param empOwnerPowerMap 待下发权限Map
     * @param curEndDate 权限到期时间
     */
    private Map<String,String> getIssueFactory(SafetyEmployeeMessageDO emp,
                                   Map<String, ParkPowerRuleVO> empPowerMap,
                                   Map<String, SafetyOutsiderPromiseDTO> outPowerMap,
                                   Map<String,ParamDTO> empOwnerPowerMap,
                                   String curEndDate) {
        String issueFactoryStr = "";
        String empNo = emp.getEmpNo();
        if ("ZS".equals(emp.getUserType())) {
            // 优先查询已授权的大门权限
            ParamDTO curOwner = empOwnerPowerMap.get(empNo);
            ParkPowerRuleVO curPowerRule = empPowerMap.get(empNo);
            //优先取curOnwer.getFactoryId()，为空时取curPowerRule.getFactoryPower()，也为空时取默认defaultDoorPower
            issueFactoryStr = Optional.ofNullable(curOwner)
                    .filter(owner -> StringUtils.isNotEmpty(owner.getFactoryId()))
                    .map(ParamDTO::getFactoryId)
                    .orElseGet(() -> Optional.ofNullable(curPowerRule)
                            .filter(rule -> StringUtils.isNotBlank(rule.getFactoryPower()))
                            .map(ParkPowerRuleVO::getFactoryPower)
                            .orElse(defaultDoorPower));
        } else {
            SafetyOutsiderPromiseDTO promiseDTO = outPowerMap.get(empNo);
            if (promiseDTO != null) {
                if (StringUtils.isNotBlank(promiseDTO.getFactory())) {
                    issueFactoryStr = promiseDTO.getFactory();
                }
                if (StringUtils.isNotBlank(promiseDTO.getServiceEndDate())) {
                    curEndDate = promiseDTO.getServiceEndDate();
                }
            }
        }
        Map<String,String> returnMap = new HashMap<>();
        returnMap.put("issueFactory",issueFactoryStr);
        returnMap.put("curEndDate",curEndDate);
        return returnMap;
    }

    /**
     * 发行人脸-新员工
     * @param emp 员工
     * @param baseIssueDO 基础日志
     * @param logIssuedDOs 下发日志
     * @param zsDeviceGidMap 项目-设备 集合
     * @param successEmps 下发成功集合
     * @param endDate 有效截止日期
     */
    private void issueFaceForOne(EmployeeMessageVO emp,SafetyLogEmployeeIssuedDO baseIssueDO,
                                 List<SafetyLogEmployeeIssuedDO> logIssuedDOs,Map<String, List<String>> zsDeviceGidMap,
                                 List<SafetyLogEmployeeIssuedDO> successEmps,String endDate,Set<String> modifyEmpSet){
        List<String> errorList = new ArrayList<>();
        String beginTime = baseIssueDO.getOperateTime();
        try {
            String empNo = emp.getEmpNo();
            String name =emp.getEmpName();
            //获取生活照
            String lifePhoto = getPhoto(empNo, 1);
            if (StringUtils.isEmpty(lifePhoto)) {
                //获取证件照
                lifePhoto = getPhoto(empNo, 0);
            }
            //添加下发失败日志记录逻辑
            SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(empNo,
                    name,lifePhoto,baseIssueDO.getOperateTime(),baseIssueDO.getIssuedType(),
                    baseIssueDO.getIssuedTypeDesc(),"1",baseIssueDO.getOperateUser(),
                    baseIssueDO.getIssuedTypeCode(),baseIssueDO.getIssuedBeanName(),baseIssueDO.getIssuedMethodName());
            if (StringUtils.isEmpty(lifePhoto) || !lifePhoto.contains("http")) {
                LogUtils.info("获取员工{}照片为空！",empNo);
                return;
            }
            JSONObject Staff = new JSONObject();
            //图片转化为二进制
            lifePhoto = lifePhoto + "&apikey=" + apikey;
            byte[] imageBytes = getPictureDataInputStream(lifePhoto);
            String base64_str = DatatypeConverter.printBase64Binary(imageBytes);
            String orgianId;
            //非本次新增的人事ID，需要查询组织ID
            String crkId = emp.getCrkId();
            if(StringUtils.isNotBlank(crkId)){
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                String orgid = crkEmpMap.get("orgid");
                if(StringUtils.isNotBlank(orgid)){
                    orgianId = orgid;
                }else{
                    orgianId = CRK_DEFAUTL_ORGAN_ID;
                }
            }else{
                orgianId = CRK_DEFAUTL_ORGAN_ID;
            }

            zsDeviceGidMap.forEach((key,value)->{
                JSONObject params = new JSONObject();
                if(value == null || CollectionUtils.isEmpty(value)){
                    return;
                }
                String[] pids = new String[]{key};
                String[] deviceIds = value.toArray(new String[0]);
                //员工号
                Staff.put("StaffNo", empNo);
                //姓名
                Staff.put("StaffName", name);
                Staff.put("OrganizationId", orgianId);
                Staff.put("Gid", pids[0]);
                //开始日期
                params.put("BeginTime", beginTime);
                //结束日期
                params.put("EndTime", endDate + " 23:59:59");
                params.put("AuthorizeDeviceNo", deviceIds);
                params.put("ProjectGids", pids);
                params.put("Staff", Staff);
                params.put("Gid", pids[0]);
                params.put("OperType", 0);
                params.put("StaffState", 2);  //场景1  有人员信息，没有授权  调用发行接口   "StaffState":2,"OperType":0
                LogUtils.info("issueFaceForOne人脸发行请求参数={}" , params.toJSONString());
                params.put("FacePicture", base64_str);

                //发行场景1（有人员信息，没有授权）发行
                Map<String,Object> requestReturn= handleRequest(faceIssueUrl,params,name,empNo);
                //添加下发失败日志记录逻辑
                boolean issuedStats = (boolean) requestReturn.get("issuedStats");
                if(!issuedStats){
                    String errorMsg = (String) requestReturn.get("errorMsg");
                    LogUtils.error("员工"+empNo+"人脸发行场景1执行异常={}",errorMsg);
                    //发行人脸时，提示已授权或重复注册的异常，不再记录日志 260413
                    if(!errorMsg.contains("已授权") && !errorMsg.contains("重复注册")){
                        params.put("OperType", 0);
                        params.put("StaffState", 1); //没人员信息，没有授权  调用发行接口  "StaffState":1,"OperType":0
                        Map<String,Object> requestReturn1= handleRequest(faceIssueUrl,params,name,empNo);
                        //添加下发失败日志记录逻辑
                        boolean issuedStats1 = (boolean) requestReturn1.get("issuedStats");
                        if(!issuedStats1){
                            String errorMsg1 = (String) requestReturn1.get("errorMsg");
                            LogUtils.error("员工"+empNo+"人脸发行场景2执行异常={}",errorMsg1);
                            //发行人脸时，提示已授权或重复注册的异常，不再记录日志 260413
                            if(!errorMsg.contains("已授权") && !errorMsg.contains("重复注册")
                                    &&!errorList.contains(empNo)){
                                errorList.add(empNo);
                                recordLog(logIssuedDOs,logIssuedDO,params,errorMsg);
                            }
                        }else{
                            logIssuedDO.setPowerEndDate(beginTime);
                            logIssuedDO.setPowerEndDate(endDate);
                            successEmps.add(logIssuedDO);
                        }
                    }
                }else{
                    logIssuedDO.setPowerEndDate(beginTime);
                    logIssuedDO.setPowerEndDate(endDate);
                    successEmps.add(logIssuedDO);
                }
                modifyEmpSet.add(empNo);
            });

        } catch (Exception e) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"人脸下发执行异常={}\"}", e.getMessage());
        }
    }

    /**
     * 更新或发行人脸-生活照紧急发行逻辑
     * @param emp 员工
     * @param baseIssueDO 基础日志
     * @param logIssuedDOs 下发日志
     * @param zsDeviceGidMap 项目-设备 集合
     * @param endDate 有效截止日期
     */
    private void modifyOrIssueFace(EmployeeMessageVO emp,SafetyLogEmployeeIssuedDO baseIssueDO,
                                  List<SafetyLogEmployeeIssuedDO> logIssuedDOs,Map<String, List<String>> zsDeviceGidMap,
                                  String endDate,String lifePhoto,List<String> errList,List<String> succList){
        String beginTime = baseIssueDO.getOperateTime();
        try {
            String empNo = emp.getEmpNo();
            String name =emp.getEmpName();
            //获取生活照
            if(StringUtils.isBlank(lifePhoto)){
                lifePhoto = getPhoto(empNo, 1);
            }
            if(StringUtils.isBlank(lifePhoto)){
                LogUtils.error("员工{}未取到生活照URL，请确认",empNo);
                return;
            }
            String imgUrl = lifePhoto;
            //图片转化为二进制
            if(lifePhoto.startsWith("http")){
                imgUrl = lifePhoto + "&apikey=" + apikey;
            }else{
                imgUrl = String.format(updatePhotoUrl, lifePhoto);
            }
            //添加下发失败日志记录逻辑
            SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(empNo,
                    name,imgUrl,baseIssueDO.getOperateTime(),baseIssueDO.getIssuedType(),
                    baseIssueDO.getIssuedTypeDesc(),"1",baseIssueDO.getOperateUser(),
                    baseIssueDO.getIssuedTypeCode(),baseIssueDO.getIssuedBeanName(),baseIssueDO.getIssuedMethodName());
            if (StringUtils.isEmpty(imgUrl) || !imgUrl.contains("http")) {
                LogUtils.info("获取员工{}照片为空！",empNo);
                return;
            }
            JSONObject params = new JSONObject();
            JSONObject Staff = new JSONObject();
            //图片转化为二进制
            byte[] imageBytes = safetyBaseHandler.getPictureDataInputStream(imgUrl);
            String base64_str = DatatypeConverter.printBase64Binary(imageBytes);
            String orgianId;
            //非本次新增的人事ID，需要查询组织ID
            String crkId = emp.getCrkId();
            if(StringUtils.isNotBlank(crkId)){
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                String orgid = crkEmpMap.get("orgid");
                if(StringUtils.isNotBlank(orgid)){
                    orgianId = orgid;
                }else{
                    orgianId = CRK_DEFAUTL_ORGAN_ID;
                }
            }else{
                orgianId = CRK_DEFAUTL_ORGAN_ID;
            }

            zsDeviceGidMap.forEach((key,value)->{
                try {
                    if (value == null || CollectionUtils.isEmpty(value)) {
                        return;
                    }
                    String[] pids = new String[]{key};
                    String[] deviceIds = value.toArray(new String[0]);
                    //员工号
                    Staff.put("StaffNo", empNo);
                    //姓名
                    Staff.put("StaffName", name);
                    Staff.put("OrganizationId", orgianId);
                    Staff.put("Gid", pids[0]);
                    //开始日期
                    params.put("BeginTime", beginTime);
                    //结束日期
                    params.put("EndTime", endDate + " 23:59:59");
                    params.put("AuthorizeDeviceNo", deviceIds);
                    params.put("ProjectGids", pids);
                    params.put("Staff", Staff);
                    params.put("Gid", pids[0]);
                    params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                    params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());  //场景1  有人员信息，没有授权  调用发行接口   "StaffState":2,"OperType":0
                    params.put("FacePicture",""); //打印日志使用
                    LogUtils.info("员工{},生活照更新参数{}", empNo, params.toJSONString());
                    params.put("FacePicture", base64_str);
                    //TODO 添加try catch添加異常日誌
                    Map<String, Object> requestReturn = handleRequest(faceModifyUrl, params, name, empNo);
                    boolean modifyStats = (boolean) requestReturn.get("issuedStats");
                    String errorMsg = (String) requestReturn.get("errorMsg");
                    if (!modifyStats) {
                        //发行场景1（有人员信息，没有授权）发行
                        params.put("OperType", UpdatePhotoEnum.FACEISSUE.getCode());
                        params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
                        Map<String,Object> returnM1= handleRequest(faceIssueUrl,params,name,empNo);
                        //添加下发失败日志记录逻辑
                        boolean m1Status = (boolean) returnM1.get("issuedStats");
                        if(!m1Status){
                            String m1Error = (String) returnM1.get("errorMsg");
                            LogUtils.error("员工"+empNo+"人脸发行场景1执行异常={}",m1Error);
                            //发行人脸时，提示已授权或重复注册的异常，不再记录日志 260413
                            if(!errorMsg.contains("已授权") && !errorMsg.contains("重复注册")){
                                params.put("StaffState", UpdatePhotoEnum.FACEMODIFY.getCode()); //没人员信息，没有授权  调用发行接口  "StaffState":1,"OperType":0
                                Map<String,Object> returnM2= handleRequest(faceIssueUrl,params,name,empNo);
                                //添加下发失败日志记录逻辑
                                boolean m2Status = (boolean) returnM2.get("issuedStats");
                                if(!m2Status){
                                    String m2Error = (String) returnM2.get("errorMsg");
                                    LogUtils.error("员工"+empNo+"人脸发行场景2执行异常={}",m2Error);
                                    //发行人脸时，提示已授权或重复注册的异常，不再记录日志 260413
                                    if(!errorMsg.contains("已授权") && !errorMsg.contains("重复注册")
                                            &&!errList.contains(empNo)){
                                        errList.add(empNo);
                                        params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                                        params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());
                                        recordLog(logIssuedDOs,logIssuedDO,params,errorMsg);
                                    }
                                }else{
                                    logIssuedDO.setPowerEndDate(beginTime);
                                    logIssuedDO.setPowerEndDate(endDate);
                                    succList.add(empNo);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    LogUtils.error("员工"+empNo+"生活照执行异常，GID="+key+",设备="+value,e);
                }
            });
        } catch (Exception e) {
            LogUtils.error("人脸生活照更新异常",e);
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"人脸下发执行异常={}\"}", e.getMessage());
        }
    }

    /**
     *  更新人脸-新员工
     * @param emp 员工
     * @param baseIssueDO 基础日志
     * @param logIssuedDOs 下发日志
     * @param zsDeviceGidMap 项目-设备 集合
     * @param endDate 有效截止日期
     * @param type true 照片为员工台账的照片， false为其他
     */
    private void modifyFaceForOne(EmployeeMessageVO emp, SafetyLogEmployeeIssuedDO baseIssueDO,
                                  List<SafetyLogEmployeeIssuedDO> logIssuedDOs, Map<String, List<String>> zsDeviceGidMap,
                                  String endDate, String lifePhoto, boolean type){
        String beginTime = baseIssueDO.getOperateTime();
        try {
            String empNo = emp.getEmpNo();
            String name =emp.getEmpName();
            //获取生活照
            if(StringUtils.isBlank(lifePhoto)){
                lifePhoto = getPhoto(empNo, 1);
            }
            String imgUrl = lifePhoto;
            //图片转化为二进制
            if (!type) {
                if (lifePhoto.startsWith("http")) {
                    imgUrl = lifePhoto + "&apikey=" + apikey;
                } else {
                    imgUrl = String.format(updatePhotoUrl, lifePhoto);
                }
            }
            //添加下发失败日志记录逻辑
            SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(empNo,
                    name,imgUrl,baseIssueDO.getOperateTime(),baseIssueDO.getIssuedType(),
                    baseIssueDO.getIssuedTypeDesc(),"1",baseIssueDO.getOperateUser(),
                    baseIssueDO.getIssuedTypeCode(),baseIssueDO.getIssuedBeanName(),baseIssueDO.getIssuedMethodName());
            if (StringUtils.isEmpty(imgUrl) || !imgUrl.contains("http")) {
                LogUtils.info("获取员工{}照片为空！",empNo);
                return;
            }
            JSONObject params = new JSONObject();
            JSONObject Staff = new JSONObject();
            //图片转化为二进制
            byte[] imageBytes = safetyBaseHandler.getPictureDataInputStream(imgUrl);
            String base64_str = DatatypeConverter.printBase64Binary(imageBytes);
            String orgianId;
            //非本次新增的人事ID，需要查询组织ID
            String crkId = emp.getCrkId();
            if(StringUtils.isNotBlank(crkId)){
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                String orgid = crkEmpMap.get("orgid");
                if(StringUtils.isNotBlank(orgid)){
                    orgianId = orgid;
                }else{
                    orgianId = CRK_DEFAUTL_ORGAN_ID;
                }
            }else{
                orgianId = CRK_DEFAUTL_ORGAN_ID;
            }

            zsDeviceGidMap.forEach((key,value)->{
                try {
                    if (value == null || CollectionUtils.isEmpty(value)) {
                        return;
                    }
                    String[] pids = new String[]{key};
                    String[] deviceIds = value.toArray(new String[0]);
                    //员工号
                    Staff.put("StaffNo", empNo);
                    //姓名
                    Staff.put("StaffName", name);
                    Staff.put("OrganizationId", orgianId);
                    Staff.put("Gid", pids[0]);
                    //开始日期
                    params.put("BeginTime", beginTime);
                    //结束日期
                    params.put("EndTime", endDate + " 23:59:59");
                    params.put("AuthorizeDeviceNo", deviceIds);
                    params.put("ProjectGids", pids);
                    params.put("Staff", Staff);
                    params.put("Gid", pids[0]);
                    params.put("OperType", UpdatePhotoEnum.FACEMODIFY.getCode());
                    params.put("StaffState", UpdatePhotoEnum.STAFFSTATE.getCode());  //场景1  有人员信息，没有授权  调用发行接口   "StaffState":2,"OperType":0
                    params.put("FacePicture",""); //打印日志使用
                    LogUtils.info("员工{},生活照更新参数{}", empNo, params.toJSONString());
                    params.put("FacePicture", base64_str);
                    //TODO 添加try catch添加異常日誌
                    Map<String, Object> requestReturn = handleRequest(faceModifyUrl, params, name, empNo);
                    boolean modifyStats = (boolean) requestReturn.get("issuedStats");
                    String errorMsg = (String) requestReturn.get("errorMsg");
                    if (!modifyStats) {
                        recordLog(logIssuedDOs, logIssuedDO, params, errorMsg);
                    }
                }catch (Exception e){
                    LogUtils.error("员工"+empNo+"生活照执行异常，GID="+key+",设备="+value,e);
                }
            });

        } catch (Exception e) {
            LogUtils.error("人脸生活照更新异常",e);
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"人脸下发执行异常={}\"}", e.getMessage());
        }
    }

    /**
     * @param issueTpeCode 初始化下发日志对象
     * @return
     */
    private SafetyLogEmployeeIssuedDO initIssueLog(String issueTpeCode){
        SafetyFieldManageDO filed = safetyFieldManageService.getByFiledCode(FIELD_ISSUE_TYPE,issueTpeCode);
        String issuedType;
        String issuedTypeDesc;
        String operateUser = "定时任务";
        String issuedBean;
        String issueMethod;
        String issuedDate = DateUtils.format(new Date(),2);
        if(!ObjectUtils.isEmpty(filed)){
            issuedType = filed.getFieldName();
            issuedTypeDesc = filed.getRemark();
            issuedBean = filed.getFieldValue(); //重新下发调用Bean
            issueMethod = filed.getEnvirInfo(); //重新下发调用method
        } else {
            issueMethod = "sendImgOnePerson";
            issuedBean = "SafetyEmpPhotoInfoService";
            issuedTypeDesc = "";
            issuedType = "新员工人脸凭证下发定时任务";
        }
        //日志的基本信息

        return new SafetyLogEmployeeIssuedDO("",
                "","",issuedDate,issuedType,
                issuedTypeDesc,"1",operateUser,
                issueTpeCode,issuedBean,issueMethod);
    }

    /**
     * 根据员工信息匹配规则
     * @param emp
     */
    public String getEmpowerDevices(SafetyEmployeeMessageDO emp,String issueFactory,
                                    List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS){
        List<Integer> factoryIdList = Arrays.stream(issueFactory.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        String zsDevices = zsEmpowerDeviceS.stream().filter(e->factoryIdList.contains(e.getFactoryId()))
                .map(SafetyEmpowerDeviceDO::getEmpowerNo).collect(Collectors.joining(","));
       return zsDevices;
    }

    /**
     *
     * 根据上次取到的最大ver从中间表查到期间更新的员工信息.
     * 根据员工号从员工卡下发记录表查询人事ID
     * 	没取到人事ID,调用人事新增接口,先新增人事信息,根据返回的人事ID,更新员工卡下发记录表的人事ID,此时默认卡标识激活,调用卡发行接口下发卡权限,下发失败卡状态为2.
     * 根据员工号从员工卡下发记录表查询tokenId
     * 	tokenId 不一样, 注销旧tokenId(卡下发记录表查询到的),下发新的tokenId
     * 	走卡下发, 更新下发记录表 tokenId,ver,最后更新时间
     * 非STATUS=1
     * 卡注销 更新 员工卡下发记录表 卡状态置为无效
     * @param beginDate
     * @author lkp
     * @time 2025-06-22
     */
    public Object issueCardForNewEmp(String beginDate) {
        //查询上次未下发成功的
        if(StringUtils.isBlank(beginDate)){
            beginDate = DateUtils.format(DateUtils.addDays(new Date(),-10),1);
        }
        //近10天未发卡的员工重新执行发卡操作
        issueCardForOldEmployee(beginDate);
        //查询最大ver
        String ver = safetyEmpCardService.selectMaxVer();
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PARK_CARD_LIMIT"); //获取卡下发限制条数
        String limit = "";
        if(!ObjectUtils.isEmpty(systemParamDO) && StringUtils.isNotBlank(systemParamDO.getParamValue())){
            limit = systemParamDO.getParamValue();
        }
        //根据ver查询中间表最新数据
        JSONObject yktData = getYKTDataByVer(ver,limit);
        //根据员工号从员工卡下发记录表查询人事ID
        JSONArray empNos = yktData.getJSONArray("empNos");
        List<SafetyEmpCard> empCardDatas = safetyEmpCardService.selectEmpCardDatas(empNos);
        Map<String, SafetyEmpCard> empCardMap = empCardDatas.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, safetyEmpCard -> safetyEmpCard));
        JSONArray dataArr = yktData.getJSONArray("data");
        if(dataArr == null || dataArr.size()<=0){
            LogUtils.info("卡下发-从一卡通中间表未找到>{}的增量数据",ver);
            return "";
        }
        //整理数据进行发卡或注销卡操作
        sendToCrkAll(empCardMap, dataArr);
        return "";
    }

    /**
     * 给T-10未发卡的员工重新发卡
     * @param beginDate 开始时间
     */
    private void issueCardForOldEmployee(String beginDate){
        EmpCardParam param = new EmpCardParam();
        param.setUpdateTimeBeign(beginDate);
        param.setCardStatus("0"); //未发卡成功
        param.setIsNullTokenId(false); //校验TokenId为空
        List<SafetyEmpCard> cardList = safetyEmpCardService.selectEmpCardDatasForParam(param);
        if(CollectionUtils.isEmpty(cardList)){
            return;
        }
        List<String> empNoList = cardList.stream().map(SafetyEmpCard::getEmpNo).collect(Collectors.toList());
        Map<String,SafetyEmpCard> empCardMap = cardList.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, e -> e));
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(empNoList);
        if(CollectionUtils.isEmpty(employeeList)){
            return;
        }
        // 黑名单中的人员禁止下发人脸 条件：状态=禁止入厂，备注=禁止授权条件判断
        employeeList = filterBlackEmployee(employeeList);
        JSONArray dataArr = new JSONArray();
        for(SafetyEmployeeMessageDO emp:employeeList){
            JSONObject obj = new JSONObject();
            String empNo = emp.getEmpNo();
            SafetyEmpCard empCard = empCardMap.get(empNo);
            obj.put("crkId",empCard.getCrkId());
            obj.put("outid",empNo);
            obj.put("scardsnr",empCard.getTokenId());
            obj.put("name",emp.getEmpName());
            obj.put("sex",emp.getSex());

            dataArr.add(obj);
        }
        LogUtils.info("需要重新发卡的员工有：{}",empNoList.toArray());
        sendToCrkDoorIssue(empNoList,dataArr);
    }

    /**
     * 处理人员下发逻辑
     * @param empCardMap 员工卡下发记录
     * @param dataArr 一卡通待下发信息
     */
    private void sendToCrkAll(Map<String, SafetyEmpCard> empCardMap, JSONArray dataArr) {
        List<String> empNos = new ArrayList<>();
        JSONArray addArr = new JSONArray();
        List<SafetyEmpCard> empCardDatas = new ArrayList<>();
        //获取批次的最大ver保存更新
        String ver = dataArr.getJSONObject(dataArr.size()-1).getString("ver");

        for (int i = 0; i < dataArr.size(); i++) {
            SafetyEmpCard empCardData = new SafetyEmpCard();

            JSONObject data = dataArr.getJSONObject(i);
            String scardsnr = data.getString("scardsnr");
            String outid = data.getString("outid");
            String status = data.getString("status");

            boolean addFlag = false;
            SafetyEmpCard empCard = empCardMap.get(outid);
            if("1".equals(status)){ //status == "1" 正常卡 新增或者更新
                //根据员工号判断是否有人事ID
                if(ObjectUtils.isEmpty(empCard) || StringUtils.isEmpty(empCard.getCrkId())){ //数据库没有记录,或者记录的人事ID为空 先要新增人事信息
                    //补充逻辑，若人事信息为空时，调用一卡通接口再获取一次
                    String crkId = safetyBaseHandler.getCrkStaffId(outid);
                    if(StringUtils.isNotBlank(crkId)){
                        addFlag = true;
                        addEmpCardData(empCardDatas,empCardData,outid,crkId,scardsnr,ver,"0");
                        data.put("crkId",crkId);
                    }else{
                        //调用一卡通接口创建人事ID，正常返回人事ID 执行卡下发 未正常返回,暂不处理
                        crkId = sendToCrkEditStaff(data);
                        if(StringUtils.isNotEmpty(crkId)){
                            addFlag = true;
                            addEmpCardData(empCardDatas,empCardData,outid,crkId,scardsnr,ver,"0");
                        }
                    }
                }else if(!ObjectUtils.isEmpty(empCard)
                        && StringUtils.isNotEmpty(scardsnr) && !scardsnr.equals(empCard.getTokenId())){ //tokenId 不一样, 注销旧tokenId(卡下发记录表查询到的),下发新的tokenId
                    JSONObject od = sendToCrkCancleCard(outid);//注销原卡
                    if (od==null || od.getBoolean("IsSucess")) { //注销成功,重新下发新tokenId权限
                        addFlag = true;
                        addEmpCardData(empCardDatas,empCardData,outid,empCard.getCrkId(),scardsnr,ver,"0");
                        //有人事ID记录，但是卡要变更的员工，记录人事ID
                        data.put("crkId",empCard.getCrkId());
                    }
                }
                if(addFlag){ //下发卡权限
                    empNos.add(outid);
                    addArr.add(data);
                }
            }else{ // 非正常卡 注销
                JSONObject od = sendToCrkCancleCard(outid);//注销卡
                if (od==null || od.getBoolean("IsSucess")) {
                    String crkId = "";
                    if(!ObjectUtils.isEmpty(empCard)&&!StringUtils.isEmpty(empCard.getCrkId())){
                        crkId = empCard.getCrkId();
                    }
                    addEmpCardData(empCardDatas,empCardData,outid,crkId,"",ver,"0");
                    LogUtils.info("卡下发定时任务,非正常卡注销成功: 员工号={}",outid);
                }else{
                    LogUtils.error("{\"code\":\"9999\",\"msg\":\"员工卡下发--非正常卡注销失败\"} 员工号={}",outid);
                }
            }
        }
        if(!CollectionUtils.isEmpty(empCardDatas)){
            safetyEmpCardService.saveOrUpdate(empCardDatas);
        }else{
            SafetyEmpCard empCardData = new SafetyEmpCard();
            JSONObject data = dataArr.getJSONObject(dataArr.size()-1);
            empCardData.setEmpNo(data.getString("outid"));
            empCardData.setVer(ver);
            safetyEmpCardService.updateByPrimary(empCardData);
        }
        if(!empNos.isEmpty()){
            sendToCrkDoorIssue(empNos, addArr);
            LogUtils.info("卡下发定时任务,新增人员下发完成");
        }
    }

    /**
     * 根据员工号,获取对应的卡片授权设备
     * @param empNos
     */
    private JSONObject getCrkCardDeviceByEmpNos(List<String> empNos){

        EmpowerDeviceListParam empowerParam = new EmpowerDeviceListParam();
        empowerParam.setRegionName("大门门禁");
        empowerParam.setEmpowerType("正式员工");
        List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);
        empowerParam.setEmpowerType("外协");
        List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);
        if(CollectionUtils.isEmpty(zsEmpowerDeviceS) ||CollectionUtils.isEmpty(wxEmpowerDeviceS)){
            LogUtils.error("\"code\":\"9999\",\"msg\":\"正式员工/外协的大门门禁设备未维护，请维护!\"");
        }
        List<SafetyEmployeeMessageDO> employeeMessageDOList = safetyEmployeeMessageService.getListByStaffNo(empNos);
        List<String> zsEmpNoList = employeeMessageDOList.stream().filter(e->"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        List<String> wxEmpNoList = employeeMessageDOList.stream().filter(e->!"ZS".equals(e.getUserType())).map(SafetyEmployeeMessageDO::getEmpNo).distinct().collect(Collectors.toList());
        //1.获取新增员工的默认厂区规则
        Map<String,ParamDTO> empOwnerPowerMap = new HashMap<>();
        Map<String,ParkPowerRuleVO> empPowerMap;
        if(!CollectionUtils.isEmpty(zsEmpNoList)){
            //优先获取大门厂区的权限
            List<ParamDTO> empParPowers = safetyParkEmployeePowerInfoService.getUserList(zsEmpNoList);
            empOwnerPowerMap = empParPowers.stream().collect(Collectors.toMap(ParamDTO::getEmpNo,e->e));
            //根据默认规则获取应授权厂区
            List<ParkPowerRuleVO> empPowerList = safetyParkPowerRuleService.selectRulesForEmp(zsEmpNoList);
            empPowerMap = empPowerList.stream().collect(Collectors.toMap(ParkPowerRuleVO::getEmpNo,e->e));
        } else {
            empOwnerPowerMap = new HashMap<>();
            empPowerMap = new HashMap<>();
        }
        Map<String,SafetyOutsiderPromiseDTO> outPowerMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(wxEmpNoList)){
            List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNoList);
            outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
        }
        //2.设置新员工默认厂区权限
        try{
            initEmpDefaultPark(employeeMessageDOList,empPowerMap,empOwnerPowerMap);
        }catch (Exception e){
            LogUtils.error("设置员工默认厂区逻辑异常",e);
        }
        //3.处理员工组织自动授权逻辑
        try{
            safetyBuildAutoDeptHandler.initAutoBuildForNewEmp(empNos);
        }catch (Exception e){
            LogUtils.error("员工组织自动授权逻辑异常",e);
        }

        //4.根据授权规则处理完后，重新厂区权限
        if(!CollectionUtils.isEmpty(zsEmpNoList)){
            //优先获取大门厂区的权限
            List<ParamDTO> empParPowers = safetyParkEmployeePowerInfoService.getUserList(zsEmpNoList);
            empOwnerPowerMap = empParPowers.stream().collect(Collectors.toMap(ParamDTO::getEmpNo,e->e));
        }
        if(!CollectionUtils.isEmpty(wxEmpNoList)){
            List<SafetyOutsiderPromiseDTO> outsiderList = safetyOutsiderPromiseChangeService.getEndDateByEmpNoList(wxEmpNoList);
            if(!CollectionUtils.isEmpty(outsiderList)){
                outPowerMap = outsiderList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo,e->e));
            }
        }
        //5.增加获取员工富士设备门禁权限的逻辑
        String curDate = DateUtils.format(new Date(),1);
        BuildEmployeePowerListParam powerParam = new BuildEmployeePowerListParam();
        powerParam.setEmpCodeList(empNos);
        powerParam.setDeviceType("2");//只查询富士的设备
        powerParam.setPowerTimeEnd(curDate);
        List<SafetyBuildEmployeePowerDO> empBuildPowerConcat = safetyBuildEmployeePowerService.getEmpPowerList(powerParam);
        Map<String, List<SafetyBuildEmployeePowerDO>> buildPowerMap = empBuildPowerConcat.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));

        //6.增加获取员工新开普设备门禁权限的逻辑
        powerParam.setDeviceType("1");//只查询富士的设备
        List<BuildEmployeePowerVO> xkpBuildPowerConcat = safetyBuildEmployeePowerService.selectEmpPowerList(powerParam);
        Map<String,List<BuildEmployeePowerVO>> xkpBuildPowerMap = xkpBuildPowerConcat.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));

        JSONObject empDatas = new JSONObject();
        //7.组装员工对应数据 员工类型, 对应的GID及设备
        Map<String, SafetyOutsiderPromiseDTO> finalOutPowerMap = outPowerMap;
        Map<String, ParamDTO> finalEmpOwnerPowerMap = empOwnerPowerMap;
        employeeMessageDOList.forEach(emp->{
            String curEmpNo = emp.getEmpNo();
            JSONObject empData = new JSONObject();
            String endTime = Constants.DEFAULT_SERVER_END;
            Map<String,String> returnMap = getIssueFactory(emp,empPowerMap, finalOutPowerMap, finalEmpOwnerPowerMap,endTime);
            String issueFactory = returnMap.get("issueFactory");
            endTime = returnMap.get("curEndDate");
            if(StringUtils.isBlank(issueFactory)){
                LogUtils.info("员工{}未找到对应的厂区权限，不执行下发！",curEmpNo);
                return;
            }
            List<BuildEmployeePowerVO> xkpBuildList = xkpBuildPowerMap.get(curEmpNo);
            //获取员工对应设备
            String zsDevices = getEmpowerDevices(emp,issueFactory,"ZS".equals(emp.getUserType())?zsEmpowerDeviceS:wxEmpowerDeviceS);
            //增加逻辑-获取员工富士设备的门禁设备
            List<SafetyBuildEmployeePowerDO> employeePowerDOList = buildPowerMap.get(curEmpNo);
            if(!CollectionUtils.isEmpty(employeePowerDOList)){
                zsDevices += "," + employeePowerDOList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).filter(li -> !StringUtils.isEmpty(li)).collect(Collectors.joining(","));
            }
            Map<String, List<String>> zsDeviceGidMap = safetyBaseHandler.getDeviceGidMapModelFromAb(zsDevices);
            empData.put("deviceInfo", zsDeviceGidMap);
            empData.put("endTime", endTime);

            empData.put("type", emp.getUserType());
            empData.put("xkpDevice", xkpBuildList);
            empDatas.put(curEmpNo, empData);
        });
        return empDatas;
    }

    private void addEmpCardData(List<SafetyEmpCard> empCardDatas, SafetyEmpCard empCardData,
                                String empNo, String crkId, String tokenId,
                                String ver, String cardStatus) {
        empCardData.setEmpNo(empNo);
        empCardData.setCrkId(crkId);
        empCardData.setTokenId(tokenId);
        empCardData.setVer(ver);
        empCardData.setCardStatus(cardStatus);
        empCardDatas.add(empCardData);
    }

    //整理发卡参数并执行下发
    private String sendToCrkDoorIssue(List<String> empNos, JSONArray dataArr) {
        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();
        SafetyFieldManageDO filed = safetyFieldManageService.getByFiledCode(FIELD_ISSUE_TYPE,ISSUED_TYPE_CODE6);
        String operateUser = "定时任务";
        if(ObjectUtils.isEmpty(filed)){
            filed = new SafetyFieldManageDO();
            filed.setFieldName("员工发卡定时任务");
            filed.setRemark("员工发卡定时作业");
            //重新下发调用Bean
            filed.setFieldValue("SafetyEmpPhotoInfoService");
            //重新下发调用method
            filed.setEnvirInfo("issueCardOnePerson");
        }
        String issuedDate = DateUtils.format(new Date(),2);
        String beginDate = DateUtils.format(new Date(),1)+" 00:00:00";
        String orgianId = CRK_DEFAUTL_ORGAN_ID;
        List<String> successEmpList = new ArrayList<>();
        List<String> errorEmpList = new ArrayList<>();
        //获取员工的设备权限
        JSONObject cardDevices = getCrkCardDeviceByEmpNos(empNos);

        for(int i=0; i<dataArr.size(); i++){
            JSONObject data = dataArr.getJSONObject(i);
            String token = data.getString("scardsnr");
            String empNo = data.getString("outid");
            String empName = data.getString("name");
            JSONObject param = new JSONObject();
            param.put("TokenOper", 1);
            param.put("TokenId", token);
            param.put("StaffNo", empNo);
            param.put("StaffName", empName);
            param.put("OrginazitionId", orgianId);
            if(cardDevices.getJSONObject(empNo) == null){
                continue;
            }
            String endDate = cardDevices.getJSONObject(empNo).getString("endTime");
            //下发新开普权限
            List<BuildEmployeePowerVO> xkpBuildList = (List<BuildEmployeePowerVO>) cardDevices.getJSONObject(empNo).get("xkpDevice");
            if(!CollectionUtils.isEmpty(xkpBuildList)){
                safetyBaseHandler.sendXkpEmpPower(empNo,xkpBuildList,"开通",beginDate,endDate);
            }
            String rid = empNo;
            String crkId = data.getString("crkId");
            //非本次新增的人事ID，需要查询组织ID
            if(StringUtils.isNotBlank(crkId)){
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                String orgid = crkEmpMap.get("orgid");
                String curId = crkEmpMap.get("rid");
                if(StringUtils.isNotBlank(orgid)){
                    orgianId = orgid;
                }
                if(StringUtils.isNotBlank(curId)){
                    rid = curId;
                }
            }
            String type = cardDevices.getJSONObject(empNo).getString("type");
            String tcm = "2A1C1286-0212-48CF-824E-0248560A010F";
            if("ZS".equals(type)){
                tcm = "BBB817CE-E6A9-4F9C-8C2D-BDFD2431C053";
            }
            param.put("Rid", rid);
            param.put("Tcm", tcm);
            param.put("BeginDate", beginDate);
            param.put("EndDate", endDate+" 23:59:59");
            param.put("CarPass", "123456");
            param.put("Token", token);
            param.put("SerialNo", token);
            param.put("stcm", "");
            param.put("IsExistCard", false);
            param.put("TokenType", 1); //发卡类型 1-实体卡 5-人脸
            param.put("UseModel", 1); //默认1
            param.put("OperNo", "999999");
            param.put("OperName", "超级管理员");
            param.put("IcStartTimeL1", "00:00");
            param.put("IcStartTimeR1", "23:59");

            Map<String, List<String>> deviceGidMap = (Map<String, List<String>>) cardDevices.getJSONObject(empNo).get("deviceInfo");
            SafetyFieldManageDO finalFiled = filed;
            deviceGidMap.forEach((key, value)->{
                String[] ProjectGids = new String[]{key};
                param.put("Redate", DateUtils.format(new Date(), 2));
                param.put("Gid", key);
                param.put("ProjectGids", ProjectGids);
                JSONArray doorGrant = new JSONArray();
                if(value != null && value.size() > 0) {
                    for (String deviceModelStr : value) {
                        JSONObject deviceModel = JSONObject.parseObject(deviceModelStr);
                        JSONObject doorGrantMap = new JSONObject();
                        doorGrantMap.put("CollectCode", deviceModel.getString("collectCode"));
                        doorGrantMap.put("D1Grant", 1);
                        doorGrantMap.put("D1OpenMode", 1);
                        doorGrantMap.put("D1Valid", 0);
                        doorGrantMap.put("DevName", deviceModel.getString("DevName"));
                        doorGrantMap.put("DevNo", deviceModel.getString("DevNo"));
                        doorGrantMap.put("TokenId", token);
                        /*设备设为动态获取 解决人脸和卡两用设备(697)，卡权限授权时下发参数中设备类型要设置为31才生效 */
                        String devTypeCode = deviceModel.getString("DevTypeCode");
                        if(StringUtils.isBlank(devTypeCode)){
                            devTypeCode = "00";
                        }
                        //给697人脸调用卡片发行接口发行 "DevTypeCode": "31", 时新增参数 "CollectCode": "20",（多加个这个）
                        if("31".equals(devTypeCode)){
                            doorGrantMap.put("CollectCode","20");
                        }
                        doorGrantMap.put("DevTypeCode",devTypeCode);
                        doorGrantMap.put("Gid", deviceModel.getString("Gid"));
                        doorGrantMap.put("Rid", UUID.randomUUID());
                        doorGrant.add(doorGrantMap);
                    }
                }
                param.put("DoorGrant", doorGrant);
                String paramsStr = param.toJSONString();
                LogUtils.info("卡下发定时任务,{} 下发参数 {}", empNo, paramsStr);
                String returnValue = sendPost(cardModifyUrl, paramsStr);
                LogUtils.info("卡下发定时任务,{}下发GID结果 {}", empNo, returnValue);
                JSONObject json = JSONObject.parseObject(returnValue);
                String errMessage = json.getJSONObject("Model").getString("ErrMessage");
                if(json.getJSONObject("State").getBoolean("IsSucess") && (StringUtils.isBlank(errMessage) || "重复注册".equals(errMessage))){
                    successEmpList.add(empNo);
                }else{
                    //只记录全下发卡失败的员工信息
                    if(!errorEmpList.contains(empNo) && !successEmpList.contains(empNo)){
                        errorEmpList.add(empNo);
                        //增加下发异常的日志记录
                        SafetyLogEmployeeIssuedDO logIssuedDO = new SafetyLogEmployeeIssuedDO(empNo,
                                empName,"",issuedDate,finalFiled.getFieldName(),finalFiled.getRemark(),"0",operateUser,
                                ISSUED_TYPE_CODE6, finalFiled.getFieldValue(), finalFiled.getEnvirInfo());
                        recordLog(logIssuedDOs,logIssuedDO,param,returnValue);
                    }
                }
            });
        }
        //排除掉发卡成功的
        List<SafetyLogEmployeeIssuedDO> newlogIssuedDOs = logIssuedDOs.stream().filter(e->!successEmpList.contains(e.getEmployeeCode())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(newlogIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(newlogIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
        if(!CollectionUtils.isEmpty(successEmpList)){
            try{
                SafetyEmpCard newEmpCard = new SafetyEmpCard();
                newEmpCard.setCardStatus("1"); //下发成功
                safetyEmpCardService.batchUpdate(successEmpList,newEmpCard);
            }catch (Exception e){
                LogUtils.error("批量发卡状态异常",e);
            }
        }

        return "";
    }

    //注销卡权限
    private JSONObject sendToCrkCancleCard(String empNo) {
        JSONObject od = null;
        try{
            String returnValue = sendPost(doorUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
            JSONObject retObject = JSONObject.parseObject(returnValue);
            Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
            if(sucess){
                JSONArray models = retObject.getJSONArray("Models");
                for (int i = 0; i < models.size(); i++) {
                    JSONObject obj = models.getJSONObject(i);
                    String tokenId = obj.getString("TokenId");
                    String gid = obj.getString("Gid");
                    String param = cardParam.replace("$Token", tokenId).replaceAll("&Gid", gid).replace("$Redate",DateUtils.format(new Date(), 2));;
                    //注销卡
                    String rest = sendPost(doorUnregisterUrl, param);
                    LogUtils.info("员工={}，的GID={}，卡注销返回值：{}",empNo,gid,rest);
                    od = JSONObject.parseObject(rest);
                }
            }
        }catch (Exception e){
            LogUtils.error("9999,员工"+empNo+"，卡注销异常",e);
        }
        return od;
    }

    /**
     * 添加人事ID
     * @param data 入参
     * @return 返回人事ID
     */
    private String sendToCrkEditStaff(JSONObject data) {
        try {
            JSONObject param = new JSONObject();
            param.put("StaffNo", data.getString("outid"));
            param.put("StaffName", data.getString("name"));
            param.put("Sex", data.getString("sex"));
            param.put("Birthday", DateUtils.format(new Date(), 2));
            param.put("OrganizationId", CRK_DEFAUTL_ORGAN_ID);
            param.put("RollDate", DateUtils.format(new Date(), 2));
            param.put("OperName", "超级管理员");
            param.put("Gid", CRK_DEFAULT_GID);
            param.put("Rid", data.getString("outid")); //以员工添加注册

            String postRet = sendPost(editStaffUrl, param.toJSONString());
            JSONObject retObj = JSONObject.parseObject(postRet);
            if (StringUtils.isNotEmpty(retObj.getString("Describe"))) {
                LogUtils.info("卡下发定时任务,新增人事: 员工号={}", data.getString("outid"));
            } else {
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            }
            return retObj.getString("Describe");
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"出入口-人事新增失败\"}");
            LogUtils.error("出入口-人事新增失败,{}",e);
            return "";
        }
    }

    /**
     * 根据增量ver获取一开通数据库
     * @param ver 上次执行到的最大增量
     * @return 结果集
     */
    private JSONObject getYKTDataByVer(String ver,String limit){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            JSONObject retObj = new JSONObject();
            JSONArray dataArr = new JSONArray();
            JSONArray empNos = new JSONArray();
            connection = yktDataSource.getConnection();
            StringBuilder sqlBuilder = new StringBuilder("SELECT sex, scardsnr, name, outid, status, ver, opendt, nousedate FROM base_customers WHERE ver > "+ver
                                      +" AND (SUBSTRING(outid, 1, 1) LIKE '[0-9]') ORDER BY ver");
            if (StringUtils.isNotBlank(limit)) {
                sqlBuilder.insert(6, " TOP(" + limit + ") ");
            }
            String sql = sqlBuilder.toString();

            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            LogUtils.info("ykt数据库查询数据");
            while (rs.next()){
                JSONObject data = new JSONObject();
                data.put("sex",rs.getString("sex"));
                data.put("scardsnr",rs.getString("scardsnr"));
                data.put("name",rs.getString("name"));
                data.put("outid",rs.getString("outid"));
                data.put("status",rs.getString("status"));
                data.put("ver",rs.getString("ver"));
                data.put("opendt",rs.getString("opendt"));
                data.put("nousedate",rs.getString("nousedate"));
                dataArr.add(data);
                empNos.add(rs.getString("outid"));
                LogUtils.info("ykt数据库查询到的数据:{}", rs.getString("outid"));
            }
            //根据获取到的数据调用中台判断人员是否有效,及获取人员信息 并调用出入口接口下发人脸权限
            LogUtils.info("ykt数据库查询数据数量:"+dataArr.size());
            retObj.put("data", dataArr);
            retObj.put("empNos", empNos);
            return retObj;
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"员工卡下发--中间库查询异常\"}",e);
        }finally {
            try {
                if(rs != null){
                    rs.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                LogUtils.error("连接一卡通中间库异常",e);
            }
        }
        return new JSONObject();
    }


    //整理发卡参数并执行下发
    private String issueCardOnePerson(JSONObject curObj,String employeeCode,String employeeName) {
        List<String> empNos = new ArrayList<>();
        empNos.add(employeeCode);
        List<SafetyEmpCard> cardList = safetyEmpCardService.selectEmpCardDatasByEmpList(empNos);
        if(CollectionUtils.isEmpty(cardList)){
            return "";
        }
        List<String> empNoList = cardList.stream().map(SafetyEmpCard::getEmpNo).collect(Collectors.toList());
        Map<String,SafetyEmpCard> empCardMap = cardList.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, e -> e));
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(empNoList);
        if(CollectionUtils.isEmpty(employeeList)){
            return "";
        }
        JSONArray dataArr = new JSONArray();
        for(SafetyEmployeeMessageDO emp:employeeList){
            JSONObject obj = new JSONObject();
            String empNo = emp.getEmpNo();
            SafetyEmpCard empCard = empCardMap.get(empNo);
            obj.put("crkId",empCard.getCrkId());
            obj.put("outid",empNo);
            obj.put("scardsnr",empCard.getTokenId());
            obj.put("name",emp.getEmpName());
            obj.put("sex",emp.getSex());
        }

        SafetyFieldManageDO filed = safetyFieldManageService.getByFiledCode(FIELD_ISSUE_TYPE,ISSUED_TYPE_CODE6);
        String operateUser = "定时任务";
        if(ObjectUtils.isEmpty(filed)){
            filed = new SafetyFieldManageDO();
            filed.setFieldName("员工发卡定时任务");
            filed.setRemark("员工发卡定时作业");
            //重新下发调用Bean
            filed.setFieldValue("SafetyEmpPhotoInfoService");
            //重新下发调用method
            filed.setEnvirInfo("issueCardOnePerson");
        }
        String issuedDate = DateUtils.format(new Date(),2);
        String beginDate = DateUtils.format(new Date(),1)+" 00:00:00";
        String orgianId = CRK_DEFAUTL_ORGAN_ID;
        List<String> successEmpList = new ArrayList<>();

        JSONObject cardDevices = getCrkCardDeviceByEmpNos(empNos);
        for(int i=0; i<dataArr.size(); i++){
            JSONObject data = dataArr.getJSONObject(i);
            String crkId = data.getString("crkId");
            String empNo = data.getString("outid");
            String token = data.getString("scardsnr");
            String empName = data.getString("name");
            String rid = empNo;
            //非本次新增的人事ID，需要查询组织ID
            if(StringUtils.isNotBlank(crkId)){
                Map<String,String> crkEmpMap = safetyBaseHandler.queryCrkEmpInfo(crkId);
                String orgid = crkEmpMap.get("orgid");
                String curId = crkEmpMap.get("rid");
                if(StringUtils.isNotBlank(orgid)){
                    orgianId = orgid;
                }
                if(StringUtils.isNotBlank(curId)){
                    rid = curId;
                }
            }

            JSONObject param = new JSONObject();
            param.put("TokenOper", 1);
            param.put("TokenId", token);
            param.put("StaffNo", empNo);
            param.put("StaffName", empName);
            param.put("OrginazitionId", orgianId);
            if(cardDevices.getJSONObject(empNo) == null){
                continue;
            }
            String type = cardDevices.getJSONObject(empNo).getString("type");
            String endDate = cardDevices.getJSONObject(empNo).getString("endTime")+" 23:59:59";
            String tcm = "2A1C1286-0212-48CF-824E-0248560A010F";
            if("ZS".equals(type)){
                tcm = "BBB817CE-E6A9-4F9C-8C2D-BDFD2431C053";
            }
            param.put("Tcm", tcm);
            param.put("BeginDate", beginDate);
            param.put("EndDate", endDate);
            param.put("CarPass", "123456");
            param.put("Token", token);
            param.put("SerialNo", token);
            param.put("stcm", "");
            param.put("IsExistCard", false);
            param.put("TokenType", 1);
            param.put("UseModel", 1); //默认1
            param.put("OperNo", "999999");
            param.put("OperName", "超级管理员");
            param.put("IcStartTimeL1", "00:00");
            param.put("IcStartTimeR1", "23:59");
            param.put("Redate", DateUtils.format(new Date(), 2));
            param.put("Rid", rid);
            Map<String, List<String>> deviceGidMap = (Map<String, List<String>>) cardDevices.getJSONObject(empNo).get("deviceInfo");
            deviceGidMap.forEach((key, value)->{
                String[] ProjectGids = new String[]{key};
                param.put("Gid", key);
                param.put("ProjectGids", ProjectGids);
                JSONArray doorGrant = new JSONArray();
                if(value != null && !value.isEmpty()) {
                    for (String deviceModelStr : value) {
                        JSONObject deviceModel = JSONObject.parseObject(deviceModelStr);
                        JSONObject doorGrantMap = new JSONObject();
                        doorGrantMap.put("collectCode", deviceModel.getString("collectCode"));
                        doorGrantMap.put("d1Grant", 1);
                        doorGrantMap.put("d1OpenMode", 1);
                        doorGrantMap.put("d1Valid", 0);
                        doorGrantMap.put("devName", deviceModel.getString("DevName"));
                        doorGrantMap.put("devNo", deviceModel.getString("DevNo"));
                        doorGrantMap.put("tokenId", token);
                        /*设备设为动态获取 解决人脸和卡两用设备，卡权限授权时下发参数中设备类型要设置为31才生效 */
                        String devTypeCode = deviceModel.getString("DevTypeCode");
                        if(StringUtils.isBlank(devTypeCode)){
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
                }
                param.put("DoorGrant", doorGrant);
                String paramsStr = param.toJSONString();
                String returnValue = sendPost(cardModifyUrl, paramsStr);
                LogUtils.info("卡下发定时任务,{}下发GID结果 {}", empNo, returnValue);
                JSONObject json = JSONObject.parseObject(returnValue);
                String errMessage = json.getJSONObject("Model").getString("ErrMessage");
                if(json.getJSONObject("State").getBoolean("IsSucess") && (StringUtils.isBlank(errMessage) || "重复注册".equals(errMessage))){
                    successEmpList.add(empNo);
                }
            });
        }
        if(!CollectionUtils.isEmpty(successEmpList)){
            try{
                SafetyEmpCard newEmpCard = new SafetyEmpCard();
                newEmpCard.setCardStatus("1"); //下发成功
                safetyEmpCardService.batchUpdate(successEmpList,newEmpCard);

                //修改下发日志
                SafetyLogEmployeeIssuedDO record = new SafetyLogEmployeeIssuedDO();
                LogEmployeeIssuedListParam param = new LogEmployeeIssuedListParam();
                record.setExceptionStatus("0");
                param.setEmployeeCode(employeeCode);
                param.setIssuedTypeCode(ISSUED_TYPE_CODE6);
                safetyLogEmployeeIssuedService.updateByExample(record,param);
            }catch (Exception e){
                LogUtils.error("批量发卡状态异常",e);
            }
        }
        return "";
    }

    /**
     * 去除黑名单人员
     *
     * @param employeeMessageDOList 原数据集
     * @return 筛选出黑名单人员的数据
     */
    public List<SafetyEmployeeMessageDO> filterBlackEmployee(List<SafetyEmployeeMessageDO> employeeMessageDOList) {
        // 1.获取人员列表中的存在黑名单的人员信息
        List<String> list = employeeMessageDOList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andCardNoIn(list);
        criteria.andStateEqualTo(1); // 状态=1 表示禁止入厂
        criteria.andRemarkEqualTo("禁止授权");
        List<SafetyBlackMessageDO> infoList = safetyBlackMessageDOMapper.selectByExample(example);

        // 2.返回去除黑名单中的人员信息
        List<String> empCodeList = infoList.stream().map(SafetyBlackMessageDO::getCardNo).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        LogUtils.info("员工{}在黑名单中，禁止进行人脸下发和卡下发！",String.join(",", empCodeList));
        return employeeMessageDOList.stream().filter(li -> !empCodeList.contains(li.getEmpNo())).collect(Collectors.toList());
    }

    /**
     * 多个员工获取设备ID信息
     */
    public Map<String, List<SafetyBuildEmployeePowerDO>> getBuildDeviceId(List<String> employeeCodes) {
        Map<String, List<SafetyBuildEmployeePowerDO>> dataMap = new HashMap<>();
        // 根据员工号获取员工所有已开通门禁权限
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setEmpCodeList(employeeCodes);
        //listParam.setOrderByClause(" UPDATE_TIME DESC");
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectByExample(listParam);
        // 过滤掉非富士的门禁数据
        employeePowerDOS = employeePowerDOS.stream().filter(li ->  "2".equals(li.getDeviceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(employeePowerDOS)) {
            // 员工门禁 数据分组 根据员工号分组
            dataMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        }
        return dataMap;
    }


    public void sendCrkForFaceUnqualified(List<SafetyEmployeeMessageDO> curEmployeeList,
                                       List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS,
                                       List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS){
        List<String> curEmpNos = curEmployeeList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());

        //2.增加获取员工门禁权限的逻辑
        Map<String,List<SafetyBuildEmployeePowerDO>> buildPowerMap = getBuildDeviceId(curEmpNos);

        //整理人脸下发的数据-大门权限对应设备
        Map<String,Object> modifyDataMap = formatFaceIssueData(curEmployeeList);

        Map<String, ParamDTO> empOwnerPowerMap = (Map<String, ParamDTO>) modifyDataMap.get("empOwnerPowerMap"); //正式工已授权厂区ID
        Map<String, ParkPowerRuleVO> empPowerMap = (Map<String, ParkPowerRuleVO>) modifyDataMap.get("empPowerMap");
        Map<String, SafetyOutsiderPromiseDTO> outPowerMap = (Map<String, SafetyOutsiderPromiseDTO>) modifyDataMap.get("outPowerMap");
        Map<String, SafetyEmpCard> empCardMap = (Map<String, SafetyEmpCard>) modifyDataMap.get("empCardMap");

        List<SafetyEmpCard> modifyCardDatas = new ArrayList<>();
        SafetyLogEmployeeIssuedDO baseIssue = initIssueLog(ISSUED_TYPE_CODE7);

        List<SafetyLogEmployeeIssuedDO> logIssuedDOs = new ArrayList<>();


        //4.组装下发参数执行卡发行操作
        curEmployeeList.forEach(emp->{
            String lifePhoto = emp.getPhoto();
            if (StringUtils.isBlank(lifePhoto)) {
                return;
            }
            //默认截止日期
            String curEndDate = Constants.DEFAULT_SERVER_END;
            EmployeeMessageVO messageVO = new EmployeeMessageVO();
            BeanUtils.copyProperties(emp,messageVO);
            String empNo = emp.getEmpNo();
            SafetyEmpCard curEmpCard = empCardMap.get(empNo);
            //判断是否有人事信息,无人事信息则新增
            if(curEmpCard == null || StringUtils.isBlank(curEmpCard.getCrkId())){
                JSONObject data = new JSONObject();
                data.put("outid",empNo);
                data.put("name",emp.getEmpName());
                data.put("sex",emp.getSex());
                String crkId = sendToCrkEditStaff(data); //正常返回人事ID 执行卡下发 未正常返回,暂不处理
                if(StringUtils.isNotEmpty(crkId)){
                    if(curEmpCard == null){
                        curEmpCard = new SafetyEmpCard();
                    }
                    curEmpCard.setEmpNo(empNo);
                    curEmpCard.setCrkId(crkId);
                    curEmpCard.setCardStatus("0"); //无卡
                    modifyCardDatas.add(curEmpCard);
                }
            }else{
                messageVO.setCrkId(curEmpCard.getCrkId());
            }
            Map<String,String> returnMap = getIssueFactory(emp,empPowerMap,outPowerMap,empOwnerPowerMap,curEndDate);
            String issueFactory = returnMap.get("issueFactory");
            //外协人员使用服务截止日期
            curEndDate = returnMap.get("curEndDate");
            if(StringUtils.isBlank(issueFactory)){
                LogUtils.info("员工{}未找到对应的厂区权限，不执行下发！",empNo);
                return;
            }
            String zsDevices = getEmpowerDevices(emp, issueFactory, "ZS".equals(emp.getUserType()) ? zsEmpowerDeviceS : wxEmpowerDeviceS);
            if(StringUtils.isBlank(zsDevices)){
                LogUtils.info("员工{}厂区{},未找到对应设备，不执行下发！",empNo,issueFactory);
                return;
            }
            //增加逻辑-获取员工富士设备的门禁设备
            List<SafetyBuildEmployeePowerDO> curBuildPower = buildPowerMap.get(empNo);
            Set<String> zsDevicesBuilder = new HashSet<>(Arrays.asList(zsDevices.split(",")));

            if (!CollectionUtils.isEmpty(curBuildPower)) {
                Set<String> curSet = curBuildPower.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.toSet());
                zsDevicesBuilder.addAll(curSet);
            }
            String faceDevices = String.join(",", zsDevicesBuilder);  // 输出a,b
            Map<String, List<String>> zsDeviceGidMap = safetyBaseHandler.getDeviceGidMapFromAb(faceDevices);
            modifyFaceForOne(messageVO,baseIssue,logIssuedDOs,zsDeviceGidMap,curEndDate,lifePhoto, true);
        });
        if(!CollectionUtils.isEmpty(logIssuedDOs)){
            try{
                safetyLogEmployeeIssuedService.batchInsert(logIssuedDOs);
            }catch (Exception e){
                LogUtils.error("批量保存下发异常日志报错",e);
            }
        }
    }
}