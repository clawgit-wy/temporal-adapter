package com.yutong.safety.core.service.bpm;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.HttpWS;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.bpm.BpmOperations;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.bpm.operation.ParamFQ;
import com.yutong.safety.core.db.entity.bpm.operation.Parameter;
import com.yutong.safety.core.db.entity.result.BpmModelRt;
import com.yutong.safety.core.db.mapper.SafetyBpmWebServiceMapper;
import com.yutong.safety.core.handler.bpm.BpmServiceHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BpmService implements BpmServiceHandler {
    @Autowired
    WebserviceUtil webserviceUtil;

    @Value("${bpm.flowWebService}")
    String baseBpmUrl;

    @Value("${bpm.createAndApprove}")
    private String createAndApproveUrl;

    private static final String sysId = "SAFETY";

    @Autowired
    private SafetyBpmWebServiceMapper bpmWebServiceMapper;

    @Override
    public VO createBpmFlow(BpmProcessParam processParam) {
        LogUtils.info("创建审批流:{}", JsonUtils.toString(processParam));
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            return VoUtil.fail("formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            return VoUtil.fail("modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            return VoUtil.fail("templateFormId 不能为空");
        }

        VO vo;
        try {
            Map creatorMap = new HashMap();
            creatorMap.put("LoginName", processParam.getUserName());
            Map exParamMap = new HashMap();
            exParamMap.put("docSubject", processParam.getSubject());
            Map formIdMap = new HashMap();
            formIdMap.put("sysId", sysId);
            formIdMap.put("modelId", processParam.getModelId());
            formIdMap.put("templateFormId", processParam.getTemplateFormId());
            formIdMap.put("formInstanceId", processParam.getFormInstanceId());
            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><int:CreateProcess>");
            xmlData.append("<flowTemplateId>").append(processParam.getBpmTemplateId()).append("</flowTemplateId>");
            xmlData.append("<formId>").append(JsonUtils.toString(formIdMap)).append("</formId>");
            xmlData.append("<creator>").append(JsonUtils.toString(creatorMap)).append("</creator>");
            xmlData.append("<exParam>").append(JsonUtils.toString(exParamMap)).append("</exParam>");
            xmlData.append("</int:CreateProcess></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("创建流程返回结果："+ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                if(rt.startsWith("T:")){
                    String flowId = rt.substring(rt.indexOf("T:")+2);
                    vo = VoUtil.successData(flowId);
                }else {
                    vo = VoUtil.fail(rt);
                }
            }else {
                vo = VoUtil.fail("创建流程失败");
            }
        }catch (Exception e){
            LogUtils.error("创建流程异常", e);
            vo = VoUtil.fail(e.getMessage());
        }
        return vo;
    }

    @Override
    public String createAndApproveBpmFlow(BpmProcessParam processParam) {
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            throw new BizException(ResponseEnum.FAIL.getCode(),"formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            throw new BizException(ResponseEnum.FAIL.getCode(),"modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            throw new BizException(ResponseEnum.FAIL.getCode(),"templateFormId 不能为空");
        }

        Map formIdMap = new HashMap();
        formIdMap.put("sysId", sysId);
        formIdMap.put("modelId", processParam.getModelId());
        formIdMap.put("templateFormId", processParam.getTemplateFormId());
        formIdMap.put("formInstanceId", processParam.getFormInstanceId());
        Map creatorMap = new HashMap();
        creatorMap.put("LoginName", processParam.getUserName());
        Map exParamMap = new HashMap();
        exParamMap.put("docSubject", processParam.getSubject());

        //接口xml参数
        StringBuffer xmlData = new StringBuffer();
        xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ext=\"http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        xmlData.append("<soapenv:Header/><soapenv:Body><ext:CreateAndApproveProcess>");
        xmlData.append("<flowTemplateId>").append(processParam.getBpmTemplateId()).append("</flowTemplateId>");
        xmlData.append("<formId>").append(JsonUtils.toString(formIdMap)).append("</formId>");
        xmlData.append("<creator>").append(JsonUtils.toString(creatorMap)).append("</creator>");
        xmlData.append("<exParam>").append(JsonUtils.toString(exParamMap)).append("</exParam>");
        xmlData.append("</ext:CreateAndApproveProcess></soapenv:Body></soapenv:Envelope>");

        String xmlStr = xmlData.toString();

        //调用接口，获取返回结果
        String ret = HttpWS.postSoap(createAndApproveUrl, xmlStr);
        LogUtils.info("二合一接口提交流程返回结果：" + ret);

        //解析返回结果
        Document document = XmlUtil.parseXml(ret);
        NodeList flag = document.getElementsByTagName("return");
        if(flag.getLength() > 0){
            String rt = flag.item(0).getTextContent();
            if(rt.startsWith("T:")){
                String flowId = rt.substring(rt.indexOf("T:")+2);
                return flowId;
            }else if(rt.startsWith("S:")){
                String flowId = rt.substring(rt.indexOf("S:")+2);
                return "S:" + flowId;
            }else {
                throw new BizException(ResponseEnum.FAIL.getCode(),rt);
            }
        }else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"创建And提交流程执行失败");
        }
    }

    @Override
    public String approveBpmFlow(BpmProcessParam processParam) {
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "templateFormId 不能为空");
        }

        //根据BPM模板ID，创建流程
        Map formIdMap = new HashMap();
        formIdMap.put("sysId", sysId);
        formIdMap.put("modelId", processParam.getModelId());
        formIdMap.put("templateFormId", processParam.getTemplateFormId());
        formIdMap.put("formInstanceId", processParam.getFormInstanceId());
        String formId = JsonUtils.toString(formIdMap);
        Map creatorMap = new HashMap();
        creatorMap.put("LoginName", processParam.getUserName());
        String creator = JsonUtils.toString(creatorMap);

        //接口xml参数
        StringBuffer xmlData = new StringBuffer();
        xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
        xmlData.append("<soapenv:Header/><soapenv:Body><int:ApproveProcess>");
        xmlData.append("<formId>").append(formId).append("</formId>");
        xmlData.append("<processId>").append(processParam.getProcessId()).append("</processId>");
        xmlData.append("<handler>").append(creator).append("</handler>");
        xmlData.append("<formData></formData>");
        xmlData.append("<processParam>").append(processParam.getProcessParam()).append("</processParam>");
        xmlData.append("</int:ApproveProcess></soapenv:Body></soapenv:Envelope>");

        String xmlStr = xmlData.toString();

        //调用接口，获取返回结果
        String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
        LogUtils.info("提交流程返回结果：" + ret);
        //解析返回结果
        Document document = XmlUtil.parseXml(ret);
        NodeList flag = document.getElementsByTagName("return");
        if(flag.getLength() > 0) {
            String rt = flag.item(0).getTextContent();
            if (rt.startsWith("T")) {
                return "T:执行成功";
            }else {
                throw new BizException(ResponseEnum.FAIL.getCode(), rt);
            }
        }else {
            throw new BizException(ResponseEnum.FAIL.getCode(), "提交流程执行失败");
        }
    }

    @Override
    public String approveBpmFlowJK(BpmProcessParam processParam) {
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "templateFormId 不能为空");
        }

        VO<List<BpmOperations>> vo = getOperationList(processParam);
        if(!"0".equals(vo.getCode())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询BPM操作信息异常");
        }
        List<BpmOperations> list = vo.getData();
        if(list==null || list.size()==0){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前账号没有可操作的权限");
        }

        String bpmTaskId = "";
        for (BpmOperations bpmOperations : list) {
            String activityType = bpmOperations.getActivityType();
            if ("reviewWorkitem".equals(activityType)) {
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
        }

        ParamFQ paramFQ = new ParamFQ();
        paramFQ.setOperationName("审批通过");
        paramFQ.setNotifyType("todo");
        paramFQ.setAuditNote("审批通过");
        Parameter parameter = new Parameter(bpmTaskId,processParam.getProcessId(),"reviewWorkitem", "handler_pass");
        parameter.setParam(paramFQ);
        Map map = new HashMap();
        map.put("sysWfBusinessForm.fdParameterJson", parameter);
        processParam.setProcessParam(JsonUtils.toString(map));

        return approveBpmFlow(processParam);
    }

    @Override
    public String discardBpmFlow(BpmProcessParam processParam) {
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "templateFormId 不能为空");
        }

        VO<List<BpmOperations>> vo = getOperationList(processParam);
        if(!"0".equals(vo.getCode())){
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询BPM操作信息异常");
        }
        List<BpmOperations> list = vo.getData();
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前账号没有可操作的权限");
        }

        String bpmTaskId = "";
        for (BpmOperations bpmOperations : list) {
            String activityType = bpmOperations.getActivityType();
            if ("reviewNode".equals(activityType)) {
                bpmTaskId = bpmOperations.getTaskId();
                break;
            }
        }

        ParamFQ paramFQ = new ParamFQ();
        paramFQ.setOperationName("废弃");
        paramFQ.setNotifyType("todo");
        paramFQ.setAuditNote("废弃");
        Parameter parameter = new Parameter(bpmTaskId,processParam.getProcessId(),"reviewNode", "drafter_abandon");
        parameter.setParam(paramFQ);
        Map map = new HashMap();
        map.put("sysWfBusinessForm.fdParameterJson", parameter);
        processParam.setProcessParam(JsonUtils.toString(map));

        return approveBpmFlow(processParam);
    }

    @Override
    public VO deleteBpmFlow(BpmProcessParam processParam) {
        LogUtils.info("删除审批流:{}", JsonUtils.toString(processParam));
        if(StringUtils.isEmpty(processParam.getFormInstanceId())){
            return VoUtil.fail("formId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getModelId())){
            return VoUtil.fail("modelId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getTemplateFormId())){
            return VoUtil.fail("templateFormId 不能为空");
        }
        if(StringUtils.isEmpty(processParam.getProcessId())){
            return VoUtil.fail("processId 不能为空");
        }

        VO result;
        try {
            Map formIdMap = new HashMap();
            formIdMap.put("sysId", sysId);
            formIdMap.put("modelId", processParam.getModelId());
            formIdMap.put("templateFormId", processParam.getTemplateFormId());
            formIdMap.put("formInstanceId", processParam.getFormInstanceId());
            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.self.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><ws:DeleteProcess>");
            xmlData.append("<formId>").append(JsonUtils.toString(formIdMap)).append("</formId>");
            xmlData.append("<processId>").append(processParam.getProcessId()).append("</processId>");
            xmlData.append("</ws:DeleteProcess></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("删除流程返回结果："+ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                if(rt.startsWith("T:")){
                    result = VoUtil.successData(rt);
                }else {
                    result = VoUtil.fail(rt);
                }
            }else {
                result = VoUtil.fail("删除流程失败");
            }
        }catch (Exception e){
            LogUtils.error("删除流程异常", e);
            result = VoUtil.fail("删除流程异常");
        }
        return result;
    }

    @Override
    public String canApprovalProcess(BpmProcessParam processParam) {
        String result;
        try {
            Map formIdMap = new HashMap();
            formIdMap.put("sysId", sysId);
            formIdMap.put("modelId", processParam.getModelId());
            formIdMap.put("templateFormId", processParam.getTemplateFormId());
            formIdMap.put("formInstanceId", processParam.getFormInstanceId());
            Map actionUid = new HashMap();
            actionUid.put("LoginName", processParam.getUserName());

            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><int:CanApprovalProcess>");
            xmlData.append("<formId>").append(JsonUtils.toString(formIdMap)).append("</formId>");
            xmlData.append("<actionUid>").append(JsonUtils.toString(actionUid)).append("</actionUid>");
            xmlData.append("</int:CanApprovalProcess></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("接口返回结果：" + ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                if("T:true".equals(rt)){
                    result = "T";
                }else if(rt.startsWith("F:")){
                    result = rt;
                }else {
                    result = "F";
                }
            }else {
                result = "接口查询流程审批权限失败";
            }
        }catch (Exception e){
            result = "接口查询流程审批权限异常";
            LogUtils.error("接口查询流程审批权限异常", e);
        }
        return result;
    }

    @Override
    public VO validateProcess(BpmProcessParam processParam) {
        String formId = processParam.getFormInstanceId();
        String modelId = processParam.getModelId();
        String templateFormId = processParam.getTemplateFormId();
        String processId = processParam.getProcessId();
        LogUtils.info("审批流合法性校验:{},{},{},{}", modelId, templateFormId, formId, processId);
        if(StringUtils.isEmpty(formId)){
            return VoUtil.fail("formId 不能为空");
        }
        if(StringUtils.isEmpty(modelId)){
            return VoUtil.fail("modelId 不能为空");
        }
        if(StringUtils.isEmpty(templateFormId)){
            return VoUtil.fail("templateFormId 不能为空");
        }
        if(StringUtils.isEmpty(processId)){
            return VoUtil.fail("processId 不能为空");
        }

        VO result;
        try {
            Map creatorMap = new HashMap();
            creatorMap.put("LoginName", processParam.getUserName());
            Map formIdMap = new HashMap();
            formIdMap.put("sysId", sysId);
            formIdMap.put("modelId", processParam.getModelId());
            formIdMap.put("templateFormId", processParam.getTemplateFormId());
            formIdMap.put("formInstanceId", processParam.getFormInstanceId());
            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><int:ValidateProcess>");
            xmlData.append("<formId>").append(JsonUtils.toString(formIdMap)).append("</formId>");
            xmlData.append("<actionUid>").append(JsonUtils.toString(creatorMap)).append("</actionUid>");
            xmlData.append("<processId>").append(processId).append("</processId>");
            xmlData.append("<processParam>").append(processParam.getProcessParam()).append("</processParam>");
            xmlData.append("</int:ValidateProcess></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("流程合法性校验返回结果："+ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                if(rt.startsWith("T:")){
                    result = VoUtil.successData(rt);
                }else {
                    result = VoUtil.fail(rt);
                }
            }else {
                result = VoUtil.fail("流程合法性校验失败");
            }
        }catch (Exception e){
            LogUtils.error("流程合法性校验异常", e);
            result = VoUtil.fail("流程合法性校验异常");
        }
        return result;
    }

    @Override
    public VO<List<BpmOperations>> getOperationList(BpmProcessParam param) {
        VO<List<BpmOperations>> result;
        try {
            Map formId = new HashMap();
            formId.put("sysId", sysId);
            formId.put("modelId", param.getModelId());
            formId.put("templateFormId", param.getTemplateFormId());
            formId.put("formInstanceId", param.getFormInstanceId());

            Map creatorMap = new HashMap();
            creatorMap.put("LoginName", param.getUserName());
            String creator = JsonUtils.toString(creatorMap);

            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><int:GetOperationList>");
            xmlData.append("<formId>").append(JSON.toJSONString(formId)).append("</formId>");
            xmlData.append("<processId>").append(param.getProcessId()).append("</processId>");
            xmlData.append("<actionUid>").append(creator).append("</actionUid>");
            xmlData.append("</int:GetOperationList></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
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

    @Override
    public VO getTasksInfo(BpmProcessParam param) {
        VO result;
        try {
            Map formId = new HashMap();
            formId.put("sysId", sysId);
            formId.put("modelId", param.getModelId());
            formId.put("templateFormId", param.getTemplateFormId());
            formId.put("formInstanceId", param.getFormInstanceId());

            Map handlerMap = new HashMap();
            handlerMap.put("LoginName", param.getUserName());
            String handler = JsonUtils.toString(handlerMap);

            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:int=\"http://interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><int:GetTasksInfo>");
            xmlData.append("<formId>").append(JSON.toJSONString(formId)).append("</formId>");
            xmlData.append("<processId>").append(param.getProcessId()).append("</processId>");
            xmlData.append("<handler>").append(handler).append("</handler>");
            xmlData.append("</int:GetTasksInfo></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
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
                    result = VoUtil.successData(taskInfo);
                }else {
                    result = VoUtil.fail(rt);
                }
            }else {
                result = VoUtil.fail("getTasksInfo失败");
            }
        }catch (Exception e){
            result = VoUtil.fail("getTasksInfo异常");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public VO getCurrentNodesInfo(String processIds) {
        if(StringUtils.isEmpty(processIds)){
            return VoUtil.fail("processIds 不能为空");
        }

        VO result;
        try {
            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ext=\"http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><ext:getCurrentNodesInfo>");
            xmlData.append("<processIds>").append(processIds).append("</processIds>");
            xmlData.append("</ext:getCurrentNodesInfo></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("接口返回结果：" + ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                JSONObject json = JSON.parseObject(rt);
                if("T".equals(json.getString("code"))){
                    JSONArray info = json.getJSONArray("data");
                    result = VoUtil.successData(info);
                }else {
                    result = VoUtil.fail(json.getString("msg"));
                }
            }else {
                result = VoUtil.fail("getCurrentNodesInfo失败");
            }
        }catch (Exception e){
            result = VoUtil.fail("getCurrentNodesInfo异常");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public VO getCurrentNodesInfo(String processIds, String modelName) {
        if(StringUtils.isEmpty(processIds)){
            return VoUtil.fail("processIds 不能为空");
        }

        VO result;
        try {
            //接口xml参数
            StringBuffer xmlData = new StringBuffer();
            xmlData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.self.kmss.landray.com/\">");
            xmlData.append("<soapenv:Header/><soapenv:Body><ws:getReviewInfosNew>");
            xmlData.append("<modelIds>").append(processIds).append("</modelIds>");
            xmlData.append("<modelName>").append(StringUtils.isNotBlank(modelName)?modelName:"").append("</modelName>");
            xmlData.append("</ws:getReviewInfosNew></soapenv:Body></soapenv:Envelope>");

            String xmlStr = xmlData.toString();
            //调用接口，获取返回结果
            String ret = HttpWS.postSoap(baseBpmUrl, xmlStr);
            LogUtils.info("接口返回结果：" + ret);

            //解析返回结果
            Document document = XmlUtil.parseXml(ret);
            NodeList flag = document.getElementsByTagName("return");
            if(flag.getLength() > 0){
                String rt = flag.item(0).getTextContent();
                JSONObject json = JSON.parseObject(rt);
                if("S".equals(json.getString("msg"))){
                    JSONArray info = json.getJSONArray("data");
                    result = VoUtil.successData(info);
                }else {
                    result = VoUtil.fail(json.getString("value"));
                }
            }else {
                result = VoUtil.fail("getCurrentNodesInfo失败");
            }
        }catch (Exception e){
            result = VoUtil.fail("getCurrentNodesInfo异常");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public BpmModelRt getModelInfo() {
        String modelId = "enter";
        String templateFormId = "safety_enter_filing";
        BpmModelRt bpmModelRt = bpmWebServiceMapper.selectModelInfo(modelId, templateFormId);
        return bpmModelRt;
    }

}
