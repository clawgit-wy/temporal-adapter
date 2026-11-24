package com.yutong.safety.core.service;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmAuditOptionVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.bpm.BpmOperations;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.bpm.operation.BpmUserVO;
import com.yutong.safety.core.db.entity.bpm.operation.ParamFQ;
import com.yutong.safety.core.db.entity.bpm.operation.Parameter;
import com.yutong.safety.core.db.mapper.SafetyBpmWebServiceMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程相关操作服务实现
 * @author lukunpeng
 * @time 2024-4-30
 */
@Service
public class SafetyBpmProcessService {

    @Autowired
    SafetyBpmWebServiceMapper safetyBpmWebServiceMapper;

    @Autowired
    WebserviceUtil webserviceUtil;

    @Value("${bpm.flowWebService}")
    String baseBpmUrl;

    @Value("${bpm.createAndApprove}")
    private String createAndApproveUrl;

    @Value("${bpm.selfFlowInfo.url}")
    private String selfFlowInfoUrl;

    private static String SYS_ID = "SAFETY";

    public String createProcess(String subject, String modelId, String formId, String token) {

        String userName = TokenUtils.getTockenUser(token).get("userName");
        PreconditonsEx.checkArgument(!"".equals(userName), "2000","未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, "");
        //拼接参数调用接口
        StringBuilder param = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        param.append("<soapenv:Header/><soapenv:Body><int:CreateProcess>");
        param.append("<flowTemplateId></flowTemplateId>");
        param.append("<formId>").append(formIdStr).append("</formId>");
        param.append("<creator>{\"LoginName\":\"").append(userName).append("\"}</creator>");
        param.append("<exParam>{\"docSubject\":\"").append(subject).append("\"}</exParam>");
        param.append("<language></language>");
        param.append("</int:CreateProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("subject:{},调用触发流程接口参数:{}", subject, param.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, param.toString());
        JSONObject jsonObject = JSON.parseObject(webResult);
        //记录并解析返回值
        LogUtils.info("subject:{},调用触发流程接口返回值:{}", subject, webResult);
        JSONObject createProcessResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:CreateProcessResponse");
        String result = createProcessResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            LogUtils.error("创建bpm流程失败subject：{},接口createProcess解析json失败：result={}",result,e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "创建bpm流程失败subject："+subject+";解析json失败：result="+result);
        }
        String status = result.substring(0, 1);
        String msg = retObj.get(status) + "";
        if ("F".equals(status)) {
            LogUtils.info("创建bpm流程失败subject：{},msg:{}", subject, msg);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        }
        return msg;
    }

    /**
     *
     * @param subject 流程主题
     * @param modelId 对接的modelId
     * @param formId 对接formId
     * @param userName 创建人
     * @return
     */
    public String createProcessByUserName(String subject, String modelId, String formId, String userName) {

        PreconditonsEx.checkArgument(!"".equals(userName), "2000","未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, "");
        //拼接参数调用接口
        StringBuilder param = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        param.append("<soapenv:Header/><soapenv:Body><int:CreateProcess>");
        param.append("<flowTemplateId></flowTemplateId>");
        param.append("<formId>").append(formIdStr).append("</formId>");
        param.append("<creator>{\"LoginName\":\"").append(userName).append("\"}</creator>");
        param.append("<exParam>{\"docSubject\":\"").append(subject).append("\"}</exParam>");
        param.append("<language></language>");
        param.append("</int:CreateProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("subject:{},调用触发流程接口参数:{}", subject, param.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, param.toString());
        JSONObject jsonObject = JSON.parseObject(webResult);
        //记录并解析返回值
        LogUtils.info("subject:{},调用触发流程接口返回值:{}", subject, webResult);
        JSONObject createProcessResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:CreateProcessResponse");
        String result = createProcessResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            LogUtils.error("创建bpm流程失败subject：{},接口createProcess解析json失败：result={}",result,e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "创建bpm流程失败subject："+subject+";解析json失败：result="+result);
        }
        String status = result.substring(0, 1);
        String msg = retObj.get(status) + "";
        if ("F".equals(status)) {
            LogUtils.info("创建bpm流程失败subject：{},msg:{}", subject, msg);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        }
        return msg;
    }

    public String approveProcess(String id, String flowId, String modelId, String formId, String fdParameterJson, String token) {

        String userName = TokenUtils.getTockenUser(token).get("userName");
        PreconditonsEx.checkArgument(!"".equals(userName), "2000","未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, id);
        //拼接参数调用接口
        StringBuilder param = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        param.append("<soapenv:Header/><soapenv:Body><int:ApproveProcess>");
        param.append("<formId>").append(formIdStr).append("</formId>");
        param.append("<processId>").append(flowId).append("</processId>");
        param.append("<handler>{\"LoginName\":\"").append(userName).append("\"}</handler>");
        param.append("<processParam>").append(fdParameterJson).append("</processParam>");
        param.append("</int:ApproveProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用审批流程接口参数: param={}", param.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, param.toString());
        LogUtils.info("调用审批流程接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject approveProcessResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:ApproveProcessResponse");
        return approveProcessResponse.getString("return");
    }

    /**
     * 流程审批
     * @param id
     * @param flowId bpm流程ID
     * @param modelId 模板ID
     * @param formId  formId
     * @param fdParameterJson 入参
     * @param userName 用户
     * @return 返回串
     */
    public String approveProcessByUserName(String id, String flowId, String modelId, String formId,
                                           String fdParameterJson, String userName) {

        PreconditonsEx.checkArgument(!"".equals(userName), "2000","未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, id);
        //拼接参数调用接口
        StringBuilder param = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        param.append("<soapenv:Header/><soapenv:Body><int:ApproveProcess>");
        param.append("<formId>").append(formIdStr).append("</formId>");
        param.append("<processId>").append(flowId).append("</processId>");
        param.append("<handler>{\"LoginName\":\"").append(userName).append("\"}</handler>");
        param.append("<processParam>").append(fdParameterJson).append("</processParam>");
        param.append("</int:ApproveProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用审批流程接口参数: param={}", param.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, param.toString());
        LogUtils.info("调用审批流程接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject approveProcessResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:ApproveProcessResponse");
        return approveProcessResponse.getString("return");
    }

    public String approveProcessByTaskId(String id, String flowId, String modelId, String formId,String userName, String auditNoteMsg){
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, id);
        String fdParameterJson = getParameterJson(flowId, formIdStr, userName, auditNoteMsg);
        //拼接参数调用接口
        StringBuilder param = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        param.append("<soapenv:Header/><soapenv:Body><int:ApproveProcess>");
        param.append("<formId>").append(formIdStr).append("</formId>");
        param.append("<processId>").append(flowId).append("</processId>");
        param.append("<handler>{\"LoginName\":\"").append(userName).append("\"}</handler>");
        param.append("<processParam>").append(fdParameterJson).append("</processParam>");
        param.append("</int:ApproveProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用审批流程接口参数: param={}", param.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, param.toString());
        LogUtils.info("调用审批流程接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject approveProcessResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:ApproveProcessResponse");
        return approveProcessResponse.getString("return");
    }

    private String getParameterJson(String flowId, String formIdStr, String userName, String auditNoteMsg) {
        if (StringUtils.isEmpty(auditNoteMsg)) {
            auditNoteMsg = "同意";
        }
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetCurrentNodesInfo>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("</int:GetCurrentNodesInfo></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程节点信息接口参数: " + sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程节点信息接口返回值webResult:{}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject aReturn = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetCurrentNodesInfoResponse");
        String result = aReturn.getString("return");
        JSONObject retObj;
        try {
            retObj = JSON.parseObject("{" + result + "}");
        } catch (Exception e) {
            LogUtils.error("接口getCurrentNodesInfo解析json失败：result={}",result,e);
            return "";
        }
        String status = result.substring(0, 1);
        String taskId = "";
        if ("F".equals(status)) {
            String msg = retObj.get(status) + "";
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            Object o = retObj.get(status);
            if (o instanceof JSONArray) {
                JSONArray retArr = (JSONArray) o;
                try {
                    JSONObject currentNodes = retArr.getJSONObject(0);
                    JSONArray workitems = currentNodes.getJSONArray("workitems");
                    for (int i=0; i<workitems.size(); i++) {
                        JSONObject item = workitems.getJSONObject(i);
                        if(userName.equals(item.getString("expecter"))){
                            taskId = item.getString("taskId");
                            break;
                        }
                    }
                } catch (Exception e) {
                    LogUtils.error("接口getCurrentNodesInfo解析json失败：retArr={}",retArr,e);
                    return "";
                }
            }
        }
        return "{\"sysWfBusinessForm.fdParameterJson\":{\"taskId\":\""+ taskId +"\",\"activityType\":\"reviewWorkitem\",\"operationType\":\"handler_pass\",\"param\":{\"notifyType\":\"todo\",\"auditNote\":\""+auditNoteMsg+"\",\"notifyOnFinish\":true}}}"
                ;
    }

    
    public String createAndApproveProcess(String docSubject,String modelId, String formId, String token) {

        String formInstanceId = safetyBpmWebServiceMapper.findIdByFormId(formId);

        String userName = TokenUtils.getTockenUser(token).get("userName");
        PreconditonsEx.checkArgument(!"".equals(userName), "2000", "未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, formInstanceId);

        JSONObject creator = new JSONObject();
        creator.put("LoginName", userName);

        JSONObject exParam = new JSONObject();
        exParam.put("docSubject", docSubject);

        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ext=\"http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><ext:CreateAndApproveProcess>");
        sb.append("<flowTemplateId></flowTemplateId>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<creator>").append(creator.toString()).append("</creator>");

        sb.append("<exParam>").append(exParam.toString()).append("</exParam>");
        sb.append("</ext:CreateAndApproveProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("createAndApproveProcess调用创建流BPM程信息接口参数: " + sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(createAndApproveUrl, sb.toString());
        LogUtils.info("createAndApproveProcess调用创建BPM流程信息接口返回值webResult:{}", webResult);
        org.json.JSONObject retObj;
        String status;
        JSONObject aReturn = null;
        try {
            JSONObject jsonObject = JSON.parseObject(webResult);
            aReturn = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:CreateAndApproveProcessResponse");
            retObj = new org.json.JSONObject("{" + aReturn.getString("return") + "}");
            status = aReturn.getString("return").substring(0, 1);
        } catch (JSONException e) {
            LogUtils.error("接口createAndApproveProcess解析json失败：aReturn={}", aReturn, e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "接口createAndApproveProcess解析json失败：aReturn=" + aReturn);
        }
        if ("F".equals(status)) {
            LogUtils.info("创建bpm流程失败subject：{}", docSubject);
            throw new BizException(ResponseEnum.FAIL.getCode(), "建bpm流程失败 docSubject:" + docSubject);
        }
        return Joiner.on(",").join(formInstanceId,retObj.getString(status));
    }

    public String createAndApproveProcessByUserName(String docSubject,String modelId, String formId, String userName,String formInstanceId) {

        if(StringUtils.isBlank(formInstanceId)){
            formInstanceId = safetyBpmWebServiceMapper.findIdByFormId(formId);
        }

        PreconditonsEx.checkArgument(!"".equals(userName), "2000", "未获取到登录人");
        //组装formId参数
        String formIdStr = getFormIdObj(modelId, formId, formInstanceId);

        JSONObject creator = new JSONObject();
        creator.put("LoginName", userName);

        JSONObject exParam = new JSONObject();
        exParam.put("docSubject", docSubject);

        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ext=\"http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><ext:CreateAndApproveProcess>");
        sb.append("<flowTemplateId></flowTemplateId>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<creator>").append(creator.toString()).append("</creator>");

        sb.append("<exParam>").append(exParam.toString()).append("</exParam>");
        sb.append("</ext:CreateAndApproveProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("createAndApproveProcess调用创建流BPM程信息接口参数: " + sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(createAndApproveUrl, sb.toString());
        LogUtils.info("createAndApproveProcess调用创建BPM流程信息接口返回值webResult:{}", webResult);
        org.json.JSONObject retObj;
        String status;
        JSONObject aReturn = null;
        try {
            JSONObject jsonObject = JSON.parseObject(webResult);
            aReturn = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:CreateAndApproveProcessResponse");
            retObj = new org.json.JSONObject("{" + aReturn.getString("return") + "}");
            status = aReturn.getString("return").substring(0, 1);
        } catch (JSONException e) {
            LogUtils.error("接口createAndApproveProcess解析json失败：aReturn={}", aReturn, e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "接口createAndApproveProcess解析json失败：aReturn=" + aReturn);
        }
        if ("F".equals(status)) {
            LogUtils.info("创建bpm流程失败subject：{}", docSubject);
            throw new BizException(ResponseEnum.FAIL.getCode(), "建bpm流程失败 docSubject:" + docSubject);
        }
        return Joiner.on(",").join(formInstanceId,retObj.getString(status));
    }

    public String getAuthFields(String id, String flowId, String formId, String nodeId) {
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetFieldAuthInfo>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append(" <nodeFactIds>").append(nodeId).append("</nodeFactIds>");
        sb.append("</int:GetFieldAuthInfo></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程校验字段接口参数: GetFieldAuthInfo={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程校验字段接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getFieldAuthInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetFieldAuthInfoResponse");
        String result = getFieldAuthInfoResponse.getString("return");
        org.json.JSONObject retObj = null;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            LogUtils.error("接口getAuthFields解析json失败：result={}",result,e);
            return new JSONObject().toJSONString();
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            return retObj.get(status).toString();
        }
    }

    public Object getNowNode(String id, String flowId, String formId) {
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetCurrentNodesInfo>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("</int:GetCurrentNodesInfo></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程校验字段接口参数: GetCurrentNodesInfo={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程校验字段接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getCurrentNodesInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetCurrentNodesInfoResponse");
        String result = getCurrentNodesInfoResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            String msg = String.format("接口GetCurrentNodesInfo返回值解析json失败：result={}",result);
            LogUtils.error(msg,e);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            return retObj.get(status).toString();
        }
    }

    /**
     * 获取当前节点处理人
     * @param id 实例ID
     * @param flowId 流程ID
     * @param formId formId
     * @return 返回值
     */
    public Object getApproverList(String id, String flowId, String formId) {
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetApproverList>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("</int:GetApproverList></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程当前节点处理人接口参数: GetApproverList={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程当前节点处理人接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getCurrentNodesInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetApproverListResponse");
        String result = getCurrentNodesInfoResponse.getString("return");
        org.json.JSONObject retObj = null;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            LogUtils.error("接口GetApproverList返回值解析json失败：result={}",result,e);
            return new JSONObject().toJSONString();
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            return retObj.get(status).toString();
        }
    }

    /**
     *
     * @param modelId 流程modelId
     * @param formId 流程formId
     * @param formInstanceId 实体类ID
     * @return 返回值
     */
    public String getFormIdObj(String modelId, String formId, String formInstanceId){
        JSONObject formIdObj = new JSONObject();
        formIdObj.put("sysId", "SAFETY");
        if (StringUtils.isNotEmpty(modelId)){
            formIdObj.put("modelId", modelId);
        }
        formIdObj.put("templateFormId", formId);
        if (StringUtils.isNotEmpty(formInstanceId)) {
            formIdObj.put("formInstanceId", formInstanceId);
        }
        return formIdObj.toString();
    }

    /**
     * 获取流程当前节点
     * @param id ID
     * @param flowId 流程ID
     * @param formId formId
     * @return 返回值
     */
    public Object getCurrentNodesInfo(String id, String flowId, String formId) {
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetCurrentNodesInfo>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("</int:GetCurrentNodesInfo></soapenv:Body></soapenv:Envelope>");
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程当前节点信息接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getCurrentNodesInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetCurrentNodesInfoResponse");
        String result = getCurrentNodesInfoResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            String msg = String.format("接口GetCurrentNodesInfo返回值解析json失败：result={}",result);
            LogUtils.error(msg,e);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            return retObj.get(status).toString();
        }
    }

    /**
     * 获取流程审批记录集合
     * @param id 信息ID
     * @param flowId 流程ID
     * @param formId fromId
     * @return 返回值
     */
    public List<BpmAuditOptionVO> getAuditOptionList(String id, String flowId, String formId,String userName) {
        List<BpmAuditOptionVO> optionVOList;
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetAuditOptionList>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("<actionUid>{\"LoginName\":\"").append(userName).append("\"}</actionUid>");
        sb.append("<pageNo>1</pageNo>");
        sb.append("<pageSize>100</pageSize>");
        sb.append("</int:GetAuditOptionList></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程当前节点处理人接口参数: GetApproverList={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程当前节点处理人接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getCurrentNodesInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetAuditOptionListResponse");
        String result = getCurrentNodesInfoResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
        } catch (JSONException e) {
            String mes = String.format("接口GetApproverList返回值解析json失败：result={}",result);
            LogUtils.error(mes,e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),mes);
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            org.json.JSONObject msg = retObj.getJSONObject(status);
            org.json.JSONArray jary = msg.getJSONArray("datas");
            optionVOList = JSONArray.parseArray(jary.toString(),BpmAuditOptionVO.class);
        }
        return optionVOList;
    }

    /**
     * 获取流程操作清单
     * @param param 入参
     * @return 返回值
     */
    public VO<List<BpmOperations>> getOperationList(BpmProcessParam param) {
        VO<List<BpmOperations>> result;
        try {
            Map formId = new HashMap();
            formId.put("sysId", SYS_ID);
            formId.put("modelId", param.getModelId());
            formId.put("templateFormId", param.getTemplateFormId());
            formId.put("formInstanceId", param.getFormInstanceId());

            Map creatorMap = new HashMap();
            creatorMap.put("LoginName", param.getUserName());
            String creator = JsonUtils.toString(creatorMap);

            //接口xml参数
            String xmlStr = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">" +
                    "<soapenv:Header/><soapenv:Body><int:GetOperationList>" +
                    "<formId>" + JSON.toJSONString(formId) + "</formId>" +
                    "<processId>" + param.getProcessId() + "</processId>" +
                    "<actionUid>" + creator + "</actionUid>" +
                    "</int:GetOperationList></soapenv:Body></soapenv:Envelope>";
            LogUtils.info(xmlStr);
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("接口返回结果：" + ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                if(rt.startsWith("T:")){
                    String taskInfo = rt.substring(rt.indexOf("T:")+2);
                    JSONArray array = JSONArray.parseArray(taskInfo);
                    List<BpmOperations> arrayLists = new ArrayList<>();
                    for(int i=0;i<array.size();i++){
                        arrayLists.add(array.getJSONObject(i).toJavaObject(BpmOperations.class));
                    }
                    result = VoUtil.successData(arrayLists);
                }else {
                    result = VoUtil.fail(rt);
                }
            }else {
                result = VoUtil.fail("getOperationList失败");
            }
        }catch (Exception e){
            result = VoUtil.fail("getOperationList失败");
            LogUtils.error("getOperationList失败", e);
        }
        return result;

    }

    /**
     * 整理流程入参JSON
     * @param infoId 信息记录ID
     * @param flowId BMP流程实例ID
     * @param modelId BMP对接的modelId
     * @param processFormId 对接的fromId
     * @param userName 用户名
     * @param isTransfer 是否转办
     * @param auditNote 审批意见
     * @param transferUser 转办人
     * @return 返回值
     */
    public BpmProcessParam initProcessParam(String infoId,String flowId,String modelId,String processFormId,
                                            String userName,Boolean isTransfer,String auditNote,String transferUser){

        BpmProcessParam processParam = new BpmProcessParam();
        processParam.setUserName(userName);
        processParam.setModelId(modelId);
        processParam.setTemplateFormId(processFormId);
        processParam.setProcessId(flowId);
        processParam.setFormInstanceId(infoId);
        VO<List<BpmOperations>> vo = this.getOperationList(processParam);
        if(vo == null || !"0".equals(vo.getCode())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询BPM操作信息异常");
        }
        List<BpmOperations> list = vo.getData();
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前账号没有可操作的权限");
        }

        String bpmTaskId = "";

        String operationoType = isTransfer?"handler_commission":"handler_pass";
        String operationName = isTransfer?"转办":"审批通过";
        String activityType = "reviewWorkitem";
        for (BpmOperations bpmOperations : list) {
            activityType = bpmOperations.getActivityType();
            //起草人提交
            if("draftWorkitem".equals(activityType)){
                operationoType = "drafter_submit";
                operationName = "起草人提交";
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
            //节点审批
            if ("reviewWorkitem".equals(activityType)) {
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
        }
        ParamFQ paramFQ = new ParamFQ();

        auditNote = StringUtils.isBlank(auditNote)?operationName:auditNote;
        paramFQ.setOperationName(operationName);
        paramFQ.setNotifyType("todo");
        paramFQ.setAuditNote(auditNote);
        if(isTransfer){
            paramFQ.setToOtherHandlerIds("{\"LoginName\":\""+transferUser+"\"}");
        }

        Parameter parameter = new Parameter(bpmTaskId,processParam.getProcessId(),activityType, operationoType);
        parameter.setParam(paramFQ);
        Map map = new HashMap();
        map.put("sysWfBusinessForm.fdParameterJson", parameter);
        processParam.setProcessParam(JsonUtils.toString(map));

        return processParam;
    }

    /**
     * 整理流程入参JSON-废弃
     * @param infoId 信息记录ID
     * @param flowId BMP流程实例ID
     * @param modelId BMP对接的modelId
     * @param processFormId 对接的fromId
     * @param userName 用户名
     * @param auditNote 审批意见
     * @return 返回值
     */
    public BpmProcessParam initProcessAbandonParam(String infoId,String flowId,String modelId,String processFormId,
                                                  String userName,String auditNote){

        BpmProcessParam processParam = new BpmProcessParam();
        processParam.setUserName(userName);
        processParam.setModelId(modelId);
        processParam.setTemplateFormId(processFormId);
        processParam.setProcessId(flowId);
        processParam.setFormInstanceId(infoId);
        VO<List<BpmOperations>> vo = this.getOperationList(processParam);
        if(vo == null || !"0".equals(vo.getCode())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询BPM操作信息异常");
        }
        List<BpmOperations> list = vo.getData();
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前账号没有可操作的权限");
        }

        String bpmTaskId = "";

        String operationoType = "handler_abandon";
        String operationName = "废弃";
        String activityType = "reviewWorkitem";
        for (BpmOperations bpmOperations : list) {
            activityType = bpmOperations.getActivityType();
            //节点驳回
            if("reviewWorkitem".equals(activityType)){
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
        }
        ParamFQ paramFQ = new ParamFQ();

        auditNote = StringUtils.isBlank(auditNote)?operationName:auditNote;
        paramFQ.setOperationName(operationName);
        paramFQ.setNotifyType("todo");
        paramFQ.setAuditNote(auditNote);
        Parameter parameter = new Parameter(bpmTaskId,processParam.getProcessId(),activityType, operationoType);
        parameter.setParam(paramFQ);
        Map map = new HashMap();
        map.put("sysWfBusinessForm.fdParameterJson", parameter);
        processParam.setProcessParam(JsonUtils.toString(map));

        return processParam;
    }


    /**
     * 整理流程入参JSON
     * @param infoId 信息记录ID
     * @param flowId BMP流程实例ID
     * @param modelId BMP对接的modelId
     * @param processFormId 对接的fromId
     * @param userName 用户名
     * @param refusePassedToThisNode  驳回通过后是否直接返回本节点 true/false
     * @param auditNote 审批意见
     * @param jumpToNodeId 驳回目标的节点编号
     * @return 返回值
     */
    public BpmProcessParam initProcessRejectParam(String infoId,String flowId,String modelId,String processFormId,
                                            String userName,Boolean refusePassedToThisNode,String auditNote,String jumpToNodeId){

        BpmProcessParam processParam = new BpmProcessParam();
        processParam.setUserName(userName);
        processParam.setModelId(modelId);
        processParam.setTemplateFormId(processFormId);
        processParam.setProcessId(flowId);
        processParam.setFormInstanceId(infoId);
        VO<List<BpmOperations>> vo = this.getOperationList(processParam);
        if(vo == null || !"0".equals(vo.getCode())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询BPM操作信息异常");
        }
        List<BpmOperations> list = vo.getData();
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前账号没有可操作的权限");
        }

        String bpmTaskId = "";

        String operationoType = "handler_refuse";
        String operationName = "驳回";
        String activityType = "reviewWorkitem";
        for (BpmOperations bpmOperations : list) {
            activityType = bpmOperations.getActivityType();
            //节点驳回
            if("reviewWorkitem".equals(activityType)){
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
        }
        ParamFQ paramFQ = new ParamFQ();

        auditNote = StringUtils.isBlank(auditNote)?operationName:auditNote;
        paramFQ.setOperationName(operationName);
        paramFQ.setNotifyType("todo");
        paramFQ.setAuditNote(auditNote);
        paramFQ.setJumpToNodeId(StringUtils.isBlank(jumpToNodeId)?"N2":jumpToNodeId);
        paramFQ.setRefusePassedToThisNode(refusePassedToThisNode!=null?refusePassedToThisNode:false);

        Parameter parameter = new Parameter(bpmTaskId,processParam.getProcessId(),activityType, operationoType);
        parameter.setParam(paramFQ);
        Map map = new HashMap();
        map.put("sysWfBusinessForm.fdParameterJson", parameter);
        processParam.setProcessParam(JsonUtils.toString(map));

        return processParam;
    }


    /**
     * 获取当前节点处理人
     * @param id 信息ID
     * @param flowId BPM流程实例ID
     * @param formId form表单
     * @return 返回值
     */
    public List<BpmUserVO> getApproverListNew(String id, String flowId, String formId) {
        List<BpmUserVO> userVOS = new ArrayList<>();
        //组装formId参数
        String formIdStr = getFormIdObj("", formId, id);
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><int:GetApproverList>");
        sb.append("<formId>").append(formIdStr).append("</formId>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("</int:GetApproverList></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用获取流程当前节点处理人接口参数: GetApproverList={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(baseBpmUrl, sb.toString());
        LogUtils.info("调用获取流程当前节点处理人接口返回值: webResult={}", webResult);
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getCurrentNodesInfoResponse = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:GetApproverListResponse");
        String result = getCurrentNodesInfoResponse.getString("return");
        org.json.JSONObject retObj;
        try {
            retObj = new org.json.JSONObject("{" + result + "}");
            String status = result.substring(0, 1);
        } catch (JSONException e) {
            LogUtils.error("接口GetApproverList返回值解析json失败：result={}",result,e);
            return userVOS;
        }
        String status = result.substring(0, 1);
        if ("F".equals(status)) {
            String msg = retObj.getString(status);
            throw new BizException(ResponseEnum.FAIL.getCode(), msg);
        } else {
            userVOS = JsonUtils.parseList(retObj.get(status).toString(),BpmUserVO.class);
            return userVOS;
        }
    }

    /**
     * 传阅
     * @param flowId 流程ID
     * @param fromUser 操作人
     * @param toUser 指向转办
     * @return 返回值
     */
    public Object circulate(String flowId, String fromUser, String toUser) {
        String[] split = toUser.split(",");
        StringBuilder toUserStr = new StringBuilder("[");

        for (String str : split) {
            toUserStr.append("{\"LoginName\":\"").append(str).append("\"},");
        }

        toUserStr = new StringBuilder(toUserStr.substring(0, toUserStr.length() - 1) + "]");

        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ext=\"http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        sb.append("<soapenv:Header/><soapenv:Body><ext:passProcess>");
        sb.append("<processId>").append(flowId).append("</processId>");
        sb.append("<circulated>{\"LoginName\":\"").append(fromUser).append("\"}</circulated>");
        sb.append("<beCirculated>").append(toUserStr).append("</beCirculated>");
        sb.append("</ext:passProcess></soapenv:Body></soapenv:Envelope>");
        LogUtils.info("调用BPM传阅接口地址: "+createAndApproveUrl+"; 参数: " + sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(createAndApproveUrl, sb.toString());
        LogUtils.info("BPM传阅接口返回值webResult:{}", webResult);
        org.json.JSONObject retObj;
        String status;
        JSONObject aReturn = null;
        try {
            JSONObject jsonObject = JSON.parseObject(webResult);
            aReturn = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:passProcessResponse");
            status = aReturn.get("return").toString();
        } catch (JSONException e) {
            LogUtils.error("接口createAndApproveProcess解析json失败：aReturn={}", aReturn, e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "接口createAndApproveProcess解析json失败：aReturn=" + aReturn);
        }
        return status;
    }

    public Object getProcessId(String formInstanceId,String type) {
        /*
            safety_spot_check_process 设备设置点检
            safety_process_patrol 治安巡查
        */
        if(StringUtils.isBlank(type)){
            type = "safety_spot_check_process";
        }
         return safetyBpmWebServiceMapper.getProcessId(formInstanceId,type);
    }


    /**
     * 获取当前节点处理人
     * @param flowIdStr BPM流程实例ID拼接串
     * @return 返回值
     */
    public List<BpmCurNodeVO> getFlowInfoForSelf(String flowIdStr) {
        List<BpmCurNodeVO> userVOS = new ArrayList<>();
        //拼接参数调用接口
        StringBuilder sb = new StringBuilder();
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.self.kmss.landray.com/\">");
        sb.append("   <soapenv:Header/>");
        sb.append("   <soapenv:Body>");
        sb.append("      <ws:getReviewInfosNew>");
        sb.append("         <modelIds>").append(flowIdStr).append("</modelIds><modelName></modelName>");
        sb.append("      </ws:getReviewInfosNew>");
        sb.append("   </soapenv:Body>");
        sb.append("</soapenv:Envelope>");
        LogUtils.info("调用批量获取流程当前信息接口入参: getReviewInfosNew={}", sb.toString());
        String webResult = webserviceUtil.doWebservicePostNew(selfFlowInfoUrl, sb.toString());
        JSONObject jsonObject = JSON.parseObject(webResult);
        JSONObject getReviewInfosNew = jsonObject.getJSONObject("soap:Envelope").getJSONObject("soap:Body").getJSONObject("ns2:getReviewInfosNewResponse").getJSONObject("return");
        if(getReviewInfosNew != null && "S".equals(getReviewInfosNew.getString("msg"))){
            userVOS = JsonUtils.parseList(getReviewInfosNew.getString("data"), BpmCurNodeVO.class);
        }
        return userVOS;
    }
}
