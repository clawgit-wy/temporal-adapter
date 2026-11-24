package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.core.db.mapper.SafetyBpmWebServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SafetyBpmWebServiceService {

    @Autowired
    SafetyBpmWebServiceMapper safetyBpmWebServiceMapper;

    public List<Map<String, String>> findAllModels() {
        return safetyBpmWebServiceMapper.findAllModels();
    }

    public List<Map<String, String>> findTableColumns(String templateFormId) {
        return safetyBpmWebServiceMapper.findTableColumns(templateFormId);
    }

    public List<Map<String, Object>> findFieldValues(String templateFormId, String fieldIds, String formInstanceId) {
        return safetyBpmWebServiceMapper.findFieldValues(templateFormId, fieldIds, formInstanceId);
    }

    public List<Map<String, String>> findCallBacks(String modelId) {
        return safetyBpmWebServiceMapper.findCallBacks(modelId);
    }

    public void updateFlowStatus(String tableName, String processId, String release) {
        safetyBpmWebServiceMapper.updateFlowStatus(tableName, processId, release);
    }

    public Map<String,String> findBpmModelByFormId(String formId){
        List<Map<String,String>> list = safetyBpmWebServiceMapper.findBpmModelByFormId(formId);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }


    /**
     * 拼接bpm链接
     * @param formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = this.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }
}
