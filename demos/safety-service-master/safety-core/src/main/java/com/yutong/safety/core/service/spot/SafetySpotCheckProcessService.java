package com.yutong.safety.core.service.spot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckProcessDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckRecordDOMapper;
import com.yutong.safety.core.dto.SafetySpotCheckProcessDTO;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lukunpeng
 * @time 2024-5-15
 */
@Service
public class SafetySpotCheckProcessService {
    @Autowired
    private SafetySpotCheckProcessDOMapper safetySpotCheckProcessDOMapper;

    @Autowired
    private SafetySpotCheckRecordDOMapper safetySpotCheckRecordDOMapper;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Value("${bpm.spot.todo}")
    private String todoPre;
    @Value("${iam.retention.robotId}")
    private String robotId;
    @Value("${im.sendMsg.postUrl}")
    private String sendMsgImUrl;
    @Value("${iam.emplyee.byWorkNo.url}")
    private String iamEmployForWorkNoUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private MailCenterUtils mailCenterUtils;

    @Autowired
    SafetyEmployeeMessageDOMapper safetyEmployeeMessageDOMapper;

    /**
     * 扫码进入点检开始页面
     * 判断是否是 点检人或者计划点检人 是 则返回设备信息
     * @param deviceId 设备ID
     * @param username 用户名
     * @return 返回值
     */
    public SafetySpotCheckProcessDTO findForAdd(String deviceId, String username) {
        //判断是不是设备点检人或者计划点检人
        String dat = DateUtils.format(new Date(), 1);
        int nums = safetySpotCheckProcessDOMapper.authUser(deviceId,username,dat);
        //不是点检人,不能点检
        if(nums <= 0){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"非指定点检人不能点检该设备!");
        }
        //是返回设备信息
        SafetySpotCheckProcessDTO forAdd = safetySpotCheckProcessDOMapper.findForAdd(deviceId);
        List<SafetySpotCheckRuleDO> checkRuleDOS = safetySpotCheckProcessDOMapper.findRules(deviceId);
        List<SafetySpotLinkRecordRuleDO> recordRuleDOS = safetySpotCheckProcessDOMapper.findLinkRules(deviceId);
        forAdd.setRecordRuleDOS(JSONArray.toJSONString(recordRuleDOS));
        forAdd.setRuleDOS(JSONArray.toJSONString(checkRuleDOS));
        UserMessageListParam param = new UserMessageListParam();
        param.setPowerFactoryId(forAdd.getFactoryId());
        param.setRoleName("治安管理员");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        String reformUser = "";
        for (SafetySysUserDO userDO: list){
            reformUser = Joiner.on(";").join(reformUser,userDO.getUserName());
        }
        forAdd.setReformUser(reformUser);
        return  forAdd;
    }

    public List<String> findIds(String deviceId) {
        return safetySpotCheckProcessDOMapper.findIds(deviceId);
    }
    public List<String> findIdsAudit(String flowId) {
        return safetySpotCheckProcessDOMapper.findIdsAudit(flowId);
    }

    public void save(SafetySpotCheckProcessDTO processDTO) {
        safetySpotCheckProcessDOMapper.insert(processDTO);
    }

    public void updateRecord(SafetySpotCheckRecordDO recordDO, List<String> recordIds, String flowId) {
        safetySpotCheckProcessDOMapper.updateRecord(recordDO, recordIds, flowId);
    }

    public void updateLinkRecordRule(List<SafetySpotLinkRecordRuleDO> recordRuleDOS, List<String> recordIds) {
        safetySpotCheckProcessDOMapper.updateLinkRecordRule(recordRuleDOS, recordIds);
    }
    public SafetySpotCheckProcessDTO view(String flowId) {
        //根据流程ID查询recordId最大的那条记录 展示相关点检信息
        SafetySpotCheckProcessDTO view = safetySpotCheckProcessDOMapper.findForView(flowId);
        if (view == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"No record found for flow ID: " + flowId);
        }
        String nodeFlag = view.getNodeFlag();
        if(!"1".equals(nodeFlag) && !StringUtils.isEmpty(view.getReformUser())){
            //增加整改责任人联系电话，app实现拨打电话
            Map<String, String> messageDO = this.getEmpInfoByUsername(view.getReformUser());
            if(messageDO != null && !StringUtils.isEmpty(messageDO.get("phoneNo"))){
                view.setReformUserPhone(messageDO.get("phoneNo"));
            }
        }
        //根据流程获取检测record集合
        SafetySpotCheckRecordDOExample recordDOExample = new SafetySpotCheckRecordDOExample();
        recordDOExample.createCriteria().andFlowIdEqualTo(flowId);
        List<SafetySpotCheckRecordDO> recordDOList = safetySpotCheckRecordDOMapper.selectByExample(recordDOExample);
        Integer recordId = null;
        if(!CollectionUtils.isEmpty(recordDOList)){
            recordId = recordDOList.get(0).getId();
        }

        List<SafetySpotCheckRuleDO> checkRuleDOS = safetySpotCheckProcessDOMapper.findRules(view.getDeviceId());
        List<SafetySpotLinkRecordRuleDO> recordRuleDOS = safetySpotCheckProcessDOMapper.findLinkRulesTwo(view.getDeviceId(),recordId);
        List<SafetySpotCheckResultDO> recordResultDOS = safetySpotCheckProcessDOMapper.findRecordResult(recordId+"");
        view.setRecordRuleDOS(JSONArray.toJSONString(recordRuleDOS));
        view.setRuleDOS(JSONArray.toJSONString(checkRuleDOS));
        view.setResultDTOS(JSONArray.toJSONString(recordResultDOS));
        return view;
    }

    public void updateProcessData(SafetySpotCheckProcessDTO processDTO) {
        safetySpotCheckProcessDOMapper.updateProcessData(processDTO);
    }

    public void saveResult(SafetySpotCheckResultDO resultDTO, List<String> recordIds) {
        safetySpotCheckProcessDOMapper.insertResult(resultDTO, recordIds);
    }

    public void delay(String flowId, String delayTime,SafetySpotCheckProcessDTO processDTO) {
        safetySpotCheckProcessDOMapper.updateProcessDelayTime(flowId, delayTime);
        safetySpotCheckProcessDOMapper.updateRecordDelayTime(flowId, delayTime);
        //更新整改记录 计划延期时间
        List<String> resultIds = safetySpotCheckProcessDOMapper.selectHistoryResult(flowId);
        if(!ObjectUtils.isEmpty(resultIds) && resultIds.get(0) !=null){
            safetySpotCheckProcessDOMapper.updateHistoryResult(resultIds,delayTime);
        }else{
            List<String> recordIds = safetySpotCheckProcessDOMapper.findIdsAudit(flowId);
            if(!ObjectUtils.isEmpty(recordIds)){
                try {
                    SafetySpotCheckResultDO resultDO = new SafetySpotCheckResultDO();
                    Map<String,String>  reformUser = this.getEmpInfoByUsername(processDTO.getReformUser());
                    if(reformUser != null){
                        String reformUserName = this.getEmpInfoByUsername(processDTO.getReformUser()).get("empName");
                        resultDO.setReformName(reformUserName);
                    }
                    resultDO.setReformFinish("0"); //未整改完成
                    resultDO.setPlanTime(DateUtils.parse(processDTO.getReformTime(), 1));
                    resultDO.setDelayTime(DateUtils.parse(delayTime, 1));

                    resultDO.setReformTime(new Date());
                    resultDO.setReformDelay("1");
                    safetySpotCheckProcessDOMapper.insertResult(resultDO, recordIds);
                }catch (Exception e){
                    LogUtils.error("新增整改记录异常：",e);
                }
            }
        }
    }

    public void updateRecordStatus(String flowId, Map<String, String> user, String type) {
        safetySpotCheckProcessDOMapper.updateRecordStatus(flowId, user, type);
    }

    public void updateValidDetailU(String validResult, String flowId,String validRemark, Map<String, String> user) {
        if(!StringUtils.isEmpty(validResult) && !StringUtils.isEmpty(flowId)){
            safetySpotCheckProcessDOMapper.updateValidDetailU(validResult, flowId,validRemark,user);
        }
    }

    public Map<String, String> getEmpInfoByUsername(String username) {
        return safetySpotCheckProcessDOMapper.getEmpInfoByUsername(username);
    }

    public List<SafetyEmployeeMessageDO> getEmpInfoByUserCodeList(List<String> list) {
        return safetyEmployeeMessageDOMapper.selectByEmpNoList(list);
    }

    public String getFactoryById(String deviceId) {
        return safetySpotCheckProcessDOMapper.getFactoryById(deviceId);
    }

    /**
     * 查询待通知的流程信息
     * @param params 入参
     * @return 返回值
     */
    public List<SafetySpotCheckProcessDTO> selectRemindProcessList(Map<String,Object> params){
        return safetySpotCheckProcessDOMapper.selectRemindProcessList(params);
    }

    /**
     * 发送验证消息提醒
     * @return 返回值
     */
    public boolean sendPlanReformTips(SafetySpotCheckProcessDTO processDTO,String title,String content,String lastAuditUserName){

        //给整改责任人上级发通知
        //【公共服务设备设施点检】设备整改流程主题，设备整改人张瑶延期整改，将计划整改完成时间由2024/05/26调整到2024/06/07，请知悉。流程链接：XXX
        String toEmpNo = "";
        String toEmail = "";
        //是否转派 转派时给已转派的治安管理员和整改责任人发邮件
        if("1".equals(processDTO.getCommissionFlag()) && !StringUtils.isEmpty(lastAuditUserName)){
            String[] auditUserArr = lastAuditUserName.split(";");
            for(String curUserName:auditUserArr){
                Map<String,String> curUser = this.getEmpInfoByUsername(curUserName);
                if(curUser != null){
                    toEmpNo = Joiner.on(";").join(curUser.get("empNo"),toEmpNo);
                    String email = curUser.get("email");
                    toEmail = Joiner.on(";").join(email,toEmail);
                }
            }
        }else{
            String deviceId = processDTO.getDeviceId();
            String factoryId = this.getFactoryById(deviceId);
            //抄送治安管理员
            UserMessageListParam param = new UserMessageListParam();
            param.setPowerFactoryId(factoryId);
            param.setRoleName("治安管理员");
            List<SafetySysUserDO> list = safetySysUserService.getList(param);
            for(SafetySysUserDO userDO: list){
                Map<String,String> curUser = this.getEmpInfoByUsername(userDO.getUserName());
                if(curUser != null){
                    toEmpNo = Joiner.on(";").join(curUser.get("empNo"),toEmpNo);
                    String email = curUser.get("email");
                    toEmail = Joiner.on(";").join(email,toEmail);
                }
            }
        }
        String flowUrl = Joiner.on("").join(todoPre,"&formInstanceId=",processDTO.getId(),
                "&processId=",processDTO.getFlowId());
        String.format(title,processDTO.getCheckName(),processDTO.getDeviceName(),processDTO.getDeviceCode());
        String.format(content,processDTO.getCheckName(),processDTO.getDeviceName(),processDTO.getDeviceCode(),flowUrl);

        String[] userNoArr = toEmpNo.split(";");
        for(String userNo:userNoArr){
            Map<String, String> params = new HashMap<>();
            params.put("appId", "1");
            params.put("sessionType", "r");
            params.put("contentType", "0");
            params.put("content", content);
            params.put("from", robotId);
            //判断是否外协 5/6开头的员工号 需要转换为 01开头
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(userNo) && (userNo.startsWith("5") || userNo.startsWith("6"))){
                userNo = getOldEmpNo(userNo);
            }
            params.put("to", userNo);
            LogUtils.info("发送宇信通信息={}", params);

            if (org.apache.commons.lang3.StringUtils.isNotEmpty(userNo)){
                RestTemplate restTemplate = new RestTemplate();
                postForEntity(sendMsgImUrl, params, restTemplate);
            }
        }
        mailCenterUtils.sendMailNew(title, content, toEmail, "治安管理邮箱", "zagl@yutong.com", null);
        return true;
    }

    public void postForEntity(String url, Map<String, String> params, RestTemplate restTemplate) {
        //提交参数设置
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        //将参数放入MultiValueMap中
        for(Map.Entry<String, String> entry : params.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            map.add(mapKey, mapValue);
        }
        // 发送post请求，并输出结果
        String result = restTemplate.postForObject(url, map, String.class);
        LogUtils.info("发送宇信通信息结果={}", result);
    }

    /**
     * 获取01员工号
     * @param userNo 员工号
     * @return 返回值
     */
    public String getOldEmpNo(String userNo) {
        String url = String.format(iamEmployForWorkNoUrl, "ABYJS", userNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                JSONArray resultJSON = userJson.getJSONArray("result");
                JSONObject record = resultJSON.getJSONObject(0);
                if("NORMAL".equals(record.getString("status")) && record.get("oldWorkNo") != null){
                    return record.getString("oldWorkNo");
                }
            }
        }
        return "";
    }
}
