package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.bpm.IBpmWebService;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;
import com.yutong.modules.common.mapper.BpmWebServiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BpmWebServiceImpl implements IBpmWebService {

    @Autowired
    private BpmWebServiceMapper bpmWebServiceMapper;


    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;



    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Value("${ytsafety.db.name}")
    private String dbName;

    @Override
    public String getTemplateFormList(BpmTemplateFormDTO bpmTemplateFormDTO) {
        if (!bpmSysId.equalsIgnoreCase(bpmTemplateFormDTO.getSysId())) {
            //非本系统流程,不做处理
            return "F";
        }
        List<Map<String, String>> modelMap = bpmWebServiceMapper.findAllModels();
        JSONArray modelArr = new JSONArray();
        for (Map<String, String> model : modelMap) {
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
    public String getFormFieldList(BpmTemplateFormDTO bpmTemplateFormDTO) {
        List<Map<String,String>> list = bpmWebServiceMapper.findTableColumns(dbName,bpmTemplateFormDTO.getTemplateFormId());
        JSONArray retArr = new JSONArray();
        for (Map<String,String> map: list) {
            JSONObject reultMap = new JSONObject();
            reultMap.put("fieldId", map.get("name"));
            reultMap.put("fieldName", map.get("comment"));
            String type = "String";
            String boolType = map.get("type");
            if(map.get("type").contains("tinyint")){
                boolType="boolean";
            }
            if (boolType.contains("int")) {
                type = "Number";
            } else if (boolType.contains("date")) {
                type = "Date";
            }else if (boolType.contains("boolean")) {
                type = "Boolean";
            }
            reultMap.put("type", type);
            retArr.add(reultMap);
        }
        return retArr.toString();
    }

    @Override
    public String getFormFieldValueList(BpmTemplateFormDTO bpmTemplateFormDTO) {
        //如果传的有值,根据传的字段取数据. 否则获取表中全部字段,然后取值
        String fieldIds = StringUtils.isEmpty(bpmTemplateFormDTO.getFieldIds())?bpmTemplateFormDTO.getFieldIds():bpmTemplateFormDTO.getFieldIds().replaceAll(";", ",");
        List<Map<String,Object>> fieldValueMapList = bpmWebServiceMapper.findFieldValues(bpmTemplateFormDTO.getTemplateFormId(), fieldIds, bpmTemplateFormDTO.getFormInstanceId());
        if(CollectionUtils.isEmpty(fieldValueMapList)) {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                fieldValueMapList = bpmWebServiceMapper.findFieldValues(bpmTemplateFormDTO.getTemplateFormId(), fieldIds, bpmTemplateFormDTO.getFormInstanceId());
                if (CollectionUtils.isNotEmpty(fieldValueMapList)) {
                    break;
                }
            }
        }
        Map<String,String> fieldMap = new HashMap<>();
        if(StringUtils.isNotBlank(fieldIds)){
            String[] fieldArr = fieldIds.split(",");
            for(String cur:fieldArr){
                fieldMap.put(cur,cur);
            }
        }

        JSONArray retArr = new JSONArray();
        if(CollectionUtils.isNotEmpty(fieldValueMapList)){
            Map<String,Object> filesValueMap = fieldValueMapList.get(0);
            if(filesValueMap != null){
                for(Map.Entry<String,Object> entry:filesValueMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                    JSONObject resultMap = new JSONObject();
                    resultMap.put("fieldId", key);
                    resultMap.put("fieldValue", value);
                    retArr.add(resultMap);
                    fieldMap.remove(key);
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
        return retArr.toString();
    }

    @Override
    public String getMethodInfo(BpmTemplateFormDTO bpmTemplateFormDTO) {
        List<Map<String,String>> methodMapList = bpmWebServiceMapper.findCallBacks(bpmTemplateFormDTO.getModelId());
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
    public String doMethodProcess(BpmMethodProcessDTO bpmTemplateFormVO) {
        eventPublisher.publishEvent(bpmTemplateFormVO);
        return "T";
    }
}
