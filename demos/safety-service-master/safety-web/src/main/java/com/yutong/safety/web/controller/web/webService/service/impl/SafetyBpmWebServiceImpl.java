package com.yutong.safety.web.controller.web.webService.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyBpmWebServiceHandler;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import com.yutong.safety.core.handler.build.SafetyBuildManageProcessHandler;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryBlankProcessDetailHandler;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutProcessHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import com.yutong.safety.core.handler.spot.SafetyPatrolRecordHandler;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationApprovalHandler;
import com.yutong.safety.core.service.enter.SafetyEnterFilingService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutEventsProcessService;
import com.yutong.safety.web.controller.web.webService.service.SafetyBpmWebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(serviceName = "safetyBpmWebService"//服务名
        ,targetNamespace = "http://com.yutong.safety.web.ws"//报名倒叙，并且和接口定义保持一致
        ,endpointInterface = "com.yutong.safety.web.controller.web.webService.service.SafetyBpmWebService" )
@Component
public class SafetyBpmWebServiceImpl implements SafetyBpmWebService {

    @Autowired
    SafetyBpmWebServiceHandler safetyBpmWebServiceHandler;
    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;
    @Autowired
    SafetyEnterFilingHandler safetyEnterFilingHandler;
    @Autowired
    private SafetyGoodsOutProcessHandler safetyGoodsOutProcessHandler;
    @Autowired
    private SafetyEnterFilingService safetyEnterFilingService;
    @Autowired
    private SafetyGoodsOutEventsProcessService safetyGoodsOutEventsProcessService;
    @Autowired
    SafetyBuildManageProcessHandler safetyBuildManageProcessHandler;
    @Autowired
    private SafetyBuildProcessHandler safetyBuildProcessHandler;
    @Autowired
    SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;
    @Autowired
    private SafetyPatrolRecordHandler safetyPatrolRecordHandler;

    @Resource
    private SafetyEntryBlankProcessDetailHandler safetyEntryBlankProcessDetailHandler;

    @Resource
    private SafetyVisitorAuthorizationApprovalHandler safetyVisitorAuthorizationApprovalHandler;

    @Override
    public String getTemplateFormList(String sysId, String language) {
        if(!"SAFETY".equals(sysId)){ //非本系统流程,不做处理
            return "F";
        }
        List<Map<String, String>> modelMap = safetyBpmWebServiceHandler.findAllModels();
        JSONArray modelArr = new JSONArray();
        for (Map<String, String> model: modelMap) {
            JSONObject reultMap = new JSONObject();
            reultMap.put("modelId", model.get("model_id"));
            reultMap.put("modelName", model.get("model_name"));
            reultMap.put("templateFormId", model.get("form_id"));
            reultMap.put("templateFormName", model.get("form_name"));
            reultMap.put("formUrl", model.get("form_url"));
            modelArr.add(reultMap);
        }
        return modelArr.toString();
    }

    @Override
    public String getFormFieldList(String sysId, String modelId, String templateFormId, String language) {
        List<Map<String,String>> list = safetyBpmWebServiceHandler.findTableColumns(templateFormId);
        JSONArray retArr = new JSONArray();
        for (Map<String,String> map: list) {
            JSONObject reultMap = new JSONObject();
            reultMap.put("fieldId",map.get("name"));
            reultMap.put("fieldName",map.get("comment"));
            String type = "String";
            if("int".contains(map.get("type"))){
                type = "Number";
            }else if(map.get("type").contains("date")){
                type = "Date";
            }
            reultMap.put("type",type);
            retArr.add(reultMap);
        }
        return retArr.toString();
    }

    @Override
    public String getFormFieldValueList(String sysId, String modelId, String templateFormId, String formInstanceId, String fieldIds, String language) {
        //如果传的有值,根据传的字段取数据. 否则获取表中全部字段,然后取值
        fieldIds = StringUtils.isEmpty(fieldIds)?fieldIds:fieldIds.replaceAll(";", ",");
        LogUtils.info("getFormFieldValueList入参：===sysId="+sysId+";modelId="+modelId+";templateFormId="+templateFormId+";formInstanceId="+formInstanceId+";fieldIds="+fieldIds+";language="+language);
        List<Map<String,Object>> fieldValueMapList = safetyBpmWebServiceHandler.findFieldValues(templateFormId, fieldIds, formInstanceId);

        Map<String,String> fieldMap = new HashMap<>();
        if(StringUtils.isNotBlank(fieldIds)){
            String[] fieldArr = fieldIds.split(",");
            LogUtils.info("getFormFieldValueList入参：fileArr.size ==="+fieldArr.length);
            for(String cur:fieldArr){
                fieldMap.put(cur,cur);
            }
        }

        JSONArray retArr = new JSONArray();
        if(CollectionUtils.isNotEmpty(fieldValueMapList)){
            Map<String,Object> filesValueMap = fieldValueMapList.get(0);
            if(filesValueMap != null){
                for(Map.Entry<String,Object> entry:filesValueMap.entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue()==null? "": String.valueOf(entry.getValue());
                    JSONObject resultMap = new JSONObject();
                    resultMap.put("fieldId", key);
                    resultMap.put("fieldValue", value);
                    retArr.add(resultMap);
                    try{
                        fieldMap.remove(key);
                    }catch (Exception e){
                        LogUtils.info("getFormFieldValueList获取字段值,list移出key="+key);
                    }
                }
            }

            /**/
            //补充指定字段
            if(!fieldMap.isEmpty()){
                for(Map.Entry<String,String> entry:fieldMap.entrySet()){
                    JSONObject resultMap = new JSONObject();
                    resultMap.put("fieldId", entry.getKey());
                    resultMap.put("fieldValue", "");
                    retArr.add(resultMap);
                }
            }
        }

        LogUtils.info("getFormFieldValueList获取字段值====="+retArr.toString());
        return retArr.toString();
    }

    @Override
    public String getMethodInfo(String sysId, String modelId, String templateFormId, String language) {
        List<Map<String,String>> methodMapList = safetyBpmWebServiceHandler.findCallBacks(modelId);

        JSONArray retArr = new JSONArray();
        if(CollectionUtils.isNotEmpty(methodMapList)){
            for (Map<String, String> methodMap:methodMapList) {
                JSONObject reultMap = new JSONObject();
                reultMap.put("functionId", methodMap.get("function_id"));
                reultMap.put("functionName", methodMap.get("function_name"));
                reultMap.put("functionDes", methodMap.get("function_desc"));
                retArr.add(reultMap);
            }
        }
        return retArr.toString();
    }

    @Override
    public String doMethodProcess(String formId, String functionId, String processData, String language) {
        LogUtils.info("流程回调日志: formId={}, functionId={}, processData={} ", formId, functionId, processData);
        JSONObject functionIdObj = JSONObject.parseObject(functionId);
        String functionIdValue = functionIdObj.getString("functionId");
        JSONObject processObj = JSONObject.parseObject(processData);
        String processId = processObj.getString("processId");
        String docStatus = processObj.getString("docStatus");
        if(StringUtils.isEmpty(functionIdValue)){
            JSONObject formIdObj = JSONObject.parseObject(formId);
            String templateFormId = formIdObj.getString("templateFormId");
            safetyBpmWebServiceHandler.updateFlowStatus(templateFormId, processId, EipStateEnum.RELEASE.getCode());
            return "T";
        }
        //流程发布回调接口
        switch(functionIdValue){
            case "safety_spot_check_publish" : safetyBpmWebServiceHandler.updateFlowStatus("safety_spot_check_process", processId, EipStateEnum.RELEASE.getCode()); break;
            case "safety_monitor_post_publish" : safetyBpmWebServiceHandler.updateFlowStatus("safety_monitor_post_process", processId, EipStateEnum.RELEASE.getCode()); break;
            case "safety_process_patrol_publish" : safetyBpmWebServiceHandler.updateFlowStatus("safety_process_patrol", processId, EipStateEnum.RELEASE.getCode()); break;
            case "safety_process_patrol_enter" : safetyPatrolRecordHandler.bpmCallBackProcess(processId,processData); break;
            case "safety_outsider_promise_change" : safetyOutsiderPromiseChangeHandler.updateFlowStatus(processId); break;
            case "safety_enter_filing_state_20" : safetyEnterFilingHandler.updateEnterFilingState(formId,processData,"20"); break;
            case "safety_enter_filing_state_30" : safetyEnterFilingHandler.updateEnterFilingState(formId,processData,"30"); break;
            case "safety_enter_filing_state_40" : safetyEnterFilingHandler.updateEnterFilingState(formId,processData,"40"); break;
            case "safety_goods_out_process":safetyGoodsOutProcessHandler.bpmCallBackGoodsOut(processId,docStatus,processData); break;
            case "safety_enter_filing":safetyEnterFilingService.bpmCallBackEnterFiling(processId); break;
            case "safety_goods_out_events_process":safetyGoodsOutEventsProcessService.bpmCallBackEventProcess(processId); break;
            case "safety_build_manage_process":safetyBuildManageProcessHandler.bpmCallBackProcess(processId,processData); break;
            case "safety_build_manage_process_discard":safetyBuildManageProcessHandler.discardProcess(processId,processData); break;
            case "safety_build_process_public":safetyBuildProcessHandler.updateFlowStatus( processId,EipStateEnum.RELEASE.getCode()); break;
            case "safety_build_process_enter":safetyBuildProcessHandler.bpmCallBackProcess(processId,processData); break;
            case "safety_build_process_discard":safetyBuildProcessHandler.discardProcess(processId,processData); break;
            case "safety_build_process_after":safetyBuildProcessHandler.afterProcess(processId,processData); break;
            case "safety_park_power_process":safetyParkPowerProcessHandler.bpmCallBackProcess(processId,processData); break;
            case "safety_park_power_process_discard":safetyParkPowerProcessHandler.discardProcess(processId,processData); break;
            case "safety_enter_filing_pass":safetyEnterFilingService.callThirdPartyInterface(processId); break;
            case "safety_park_power_process_publish":safetyParkPowerProcessHandler.publishProcess(processId,processData); break;
            case "safety_entry_blank_process_pass":safetyEntryBlankProcessDetailHandler.updateFlowStatus(processId,EipStateEnum.RELEASE.getCode()); break;
            case "safety_visitor_authorization_approval_pass":safetyVisitorAuthorizationApprovalHandler.updateFlowStatus(processId,EipStateEnum.RELEASE.getCode()); break;
        }


        return "T";
    }

    @Override
    public String synchronizeTemplate() {
        return null;
    }
}
