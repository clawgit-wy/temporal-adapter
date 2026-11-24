package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lkp
 * @time 2023-10-23
 * 定时任务service类
 */
@Service
public class SafetyTaskService {

    @Value("${iam.findSeparatedEmployees.url}")
    private String unregisterUrl;

    @Value("${iam.Project.url}")
    private String projectUrl;

    @Value("${iam.DmsDeviceRead.url}")
    private String deviceUrl;

    @Value("${crk.IssueDoorView.url}")
    private String doorUrl;

    @Value("${crk.IssueParkView.url}")
    private String parkUrl;

    @Value("${crk.FaceUnregister.url}")
    private String faceUnregistertUrl;

    @Value("${crk.Unregister.url}")
    private String doorUnregistertUrl;

    @Value("${crk.ParkUnregister.url}")
    private String parkUnregistertUrl;

    //卡注销入参
    private static String cardParam ="{\"SubSystems\":[2],\"DepositMoney\":0,\"VmsOperMoney\":0,\"CmsOperMoney\":0,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":1,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10卡注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"&Gid\",\"ProjectGids\":[\"&Gid\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";
    //车注销入参
    private static String carParam ="{\"OperMoney\":150,\"Token\":\"$Token\",\"IsExistCard\":true,\"TokenType\":0,\"UseModel\":0,\"Plate\":null,\"TokenOper\":12,\"Remark\":\"中台12:10车注销\",\"OperNo\":\"999999\",\"OperName\":\"超级管理员\",\"Redate\":\"$Redate\",\"Gid\":\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\",\"ProjectGids\":[\"1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,\"IsLegal\":true}";

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    private MailCenterUtils mailCenterUtils;

    @Autowired
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    @Autowired
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;

    /**
     * 注销人脸 同时注销 卡 车辆 权限定时任务实现
     * @author lkp
     * @time 2023-10-23
     * @return
     */
    public Object faceUnregister(String dateStr){
        //调用中台获取前一天离职的人员
        List<String> empNos = getEmpNos(dateStr);
        //注销人脸
        //cancleFace(empNos,gid); 20240510 增加注销其他项目逻辑,改为使用人脸凭证注销
        cancleFaceNew(empNos);
        //注销卡
        cancleCard(empNos);
        //注销车辆
        cancleCar(empNos);
        //取消正式工大门权限
        safetyParkEmpPowerReportService.batchDelEmployeePower(empNos,"定时任务注销离职人员权限");
        //删除离职人员的门禁权限
        safetyBuildEmployeePowerService.batchDelEmployeePower(empNos);
        //清空离职员工的创建时间
        safetyEmployeeMessageService.updateCreateTime(empNos, Constants.DISABLED);
        return "S";
    }
    private List<String> getEmpNos(String date){
        //接口每次最多返回1000条数据,为保证数据不缺失,循环获取全部数据
        int pageNo = 1;
        List<String> empNos = new ArrayList<>();
        if(StringUtils.isEmpty(date)){
            date = DateUtils.format(LocalDateTime.now().minusDays(1).toDate(), 2);
        }
        while(true){
            String url = String.format(unregisterUrl, date,pageNo);
            String post = webserviceUtil.sendHttpForGet(url);
            LogUtils.info("查询前一天离职人员返回值:" + post);
            if(StringUtils.isEmpty(post)){
                return empNos;
            }
            JSONObject content = JSONObject.parseObject(post);
            Boolean success = (Boolean) content.get("success");
            if(success){
                JSONObject result = content.getJSONObject("result");
                JSONArray records = result.getJSONArray("records");
                for(int i=0; i<records.size(); i++){
                    JSONObject detail = records.getJSONObject(i);
                    String workNo = detail.getString("workNo");
                    empNos.add(workNo);
                }
                int current = result.getIntValue("current");
                int pages = result.getIntValue("pages");
                //总页数为0 或者当前页数等于总页数 跳出循环
                if(pages == 0 || current == pages){
                    break;
                }
            }else{
                break;
            }
            pageNo++;
        }
        return empNos;
    }
    /**
     * 人脸注销接口不支持其他项目注销.使用卡注销接口,获取人脸凭证注销
     * @param empNos
     */
    private void cancleFaceNew(List<String> empNos) {
        String redate = DateUtils.format(new Date(), 2);
        List<Object> ret = new ArrayList<>();
        try {
            for (String empNo:empNos) {
                //获取tokenId
                String returnValue = HttpFileUtil.sendPost(doorUrl, "\"StaffNo='" + empNo + "'and TokenType =5 and State=1\"");
                JSONObject retObject = JSONObject.parseObject(returnValue);
                Boolean sucess = false;

                if (!ObjectUtils.isEmpty(retObject.getJSONObject("State")) && retObject.getJSONObject("State").containsKey("IsSucess")) {
                    sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
                }

                if(sucess){
                    JSONArray models = retObject.getJSONArray("Models");
                    for (int i = 0; i < models.size(); i++) {
                        JSONObject obj = models.getJSONObject(i);
                        String tokenId = obj.getString("TokenId");
                        String gid = obj.getString("Gid");
                        String param = cardParam.replace("$Token", tokenId).replaceAll("&Gid", gid).replace("$Redate",redate);;
                        //注销卡
                        String rest = HttpFileUtil.sendPost(doorUnregistertUrl, param);
                        JSONObject od = JSONObject.parseObject(rest);
                        if (od.getBoolean("IsSucess")){
                            LogUtils.info("需要注销的卡={}, gid={} 注销成功",empNo, gid);
                        }else{
                            LogUtils.info("需要注销的卡={}, gid={} 注销失败 原因: {}",empNo, gid,rest);
                            ret.add(Joiner.on("-").join(empNo,"失败原因",rest));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.info("注销人脸失败：{}",e);
        }
        if(!CollectionUtils.isEmpty(ret)){
            mailCenterUtils.sendMail("人脸注销失败",ret.toString(),"kongwk@yutong.com","koaadmin","koaadmin@yutong.com");
            mailCenterUtils.sendMail("人脸注销失败",ret.toString(),"gaoweia@yutong.com","koaadmin","koaadmin@yutong.com");
        }
    }
    private void cancleCar(List<String> empNos) {
        String redate = DateUtils.format(new Date(), 2);
        //注销车权限
        try{
            for (String empNo:empNos) {
                LogUtils.info("查询车权限的入参:" + empNo);
                //查询车牌信息 生产地址
                String json = HttpFileUtil.sendPost(parkUrl,"\"TcmType=2 and StaffNo='" + empNo + "'\"");
                //解析返回值获取车牌号
                JSONObject jsonObject = JSONObject.parseObject(json);
                //是否需要注销车权限
                Boolean isSucess = jsonObject.getJSONObject("State").getBoolean("IsSucess");
                if (isSucess) {
                    JSONArray models = jsonObject.getJSONArray("Models");
                    if (CollectionUtils.isNotEmpty(models)) {
                        for (int i = 0; i < models.size(); i++) {
                            //TokenId 车牌号
                            String tokenId = models.getJSONObject(i).getString("TokenId");
                            if (StringUtils.isNotEmpty(tokenId)) {
                                String param = carParam.replace("$Token", tokenId).replace("$Redate", redate);
                                //调用注销逻辑
                                String returnValue = HttpFileUtil.sendPost(parkUnregistertUrl,param);
                                JSONObject data = JSONObject.parseObject(returnValue);
                                if (data.getBoolean("IsSucess")) {
                                    LogUtils.info("需要注销的车牌号：" + tokenId + "成功");
                                } else {
                                    LogUtils.warn("需要注销的车牌号：" + tokenId + "失败" + returnValue);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error("注销车权限报错：", e);
        }
    }

    private void cancleCard(List<String> empNos) {
        String redate = DateUtils.format(new Date(), 2);
        try {
            for (String empNo:empNos) {
                //获取tokenId
                String returnValue = HttpFileUtil.sendPost(doorUrl, "\"StaffNo='" + empNo + "'and TokenType =1 and State=1\"");
                JSONObject retObject = JSONObject.parseObject(returnValue);
                Boolean sucess = retObject.getJSONObject("State").getBoolean("IsSucess");
                if(sucess){
                    JSONArray models = retObject.getJSONArray("Models");
                    for (int i = 0; i < models.size(); i++) {
                        JSONObject obj = models.getJSONObject(i);
                        String tokenId = obj.getString("TokenId");
                        String gid = obj.getString("Gid");
                        String param = cardParam.replace("$Token", tokenId).replaceAll("&Gid", gid).replace("$Redate",redate);;
                        //注销卡
                        String rest = HttpFileUtil.sendPost(doorUnregistertUrl, param);
                        JSONObject od = JSONObject.parseObject(rest);
                        if (od.getBoolean("IsSucess")){
                            LogUtils.info("需要注销的卡："+gid+empNo+"成功");
                        }else{
                            LogUtils.warn("需要注销的卡："+gid+empNo+"失败"+returnValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.warn("注销卡失败："+e);
        }
    }

    private void cancleFace(List<String> empNos, String gid) {
        List<Object> ret = new ArrayList<>();
        String[] gids = new String[]{gid};
        try {
            for (String empNo:empNos) {
                JSONObject param = new JSONObject();
                JSONObject staff = new JSONObject();
                staff.put("StaffNo", empNo);
                staff.put("OrganizationId", "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7");
                staff.put("Gid", gid);

                param.put("OperType", 2);
                param.put("ProjectGids", gids);
                param.put("StaffState", 0);
                param.put("Staff", staff);

                String result = HttpFileUtil.sendPost(faceUnregistertUrl, param.toJSONString());
                JSONObject retObj = JSONObject.parseObject(result);
                LogUtils.warn(empNo+" 人脸注销结果:"+result);
                if(!"200".equals(retObj.getString("State"))){
                    LogUtils.warn(empNo+" 人脸注销失败:"+result);
                    ret.add(empNo+"失败原因："+result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("人脸注销失败：", e);
        }
        if(ret.size() > 0){
            mailCenterUtils.sendMail("人脸注销失败",ret.toString(),"kongwk@yutong.com","koaadmin","koaadmin@yutong.com");
            mailCenterUtils.sendMail("人脸注销失败",ret.toString(),"gaoweia@yutong.com","koaadmin","koaadmin@yutong.com");
        }
    }

}
