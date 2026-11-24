package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SafetyBpmWebServiceHandler {

    @Autowired
    SafetyBpmWebServiceService safetyBpmWebServiceService;

    public List<Map<String, String>> findAllModels() {
        return safetyBpmWebServiceService.findAllModels();
    }

    public List<Map<String, String>> findTableColumns(String templateFormId) {
        return safetyBpmWebServiceService.findTableColumns(templateFormId);
    }

    public List<Map<String, Object>> findFieldValues(String templateFormId, String fieldIds, String formInstanceId) {
        return safetyBpmWebServiceService.findFieldValues(templateFormId, fieldIds,formInstanceId);
    }

    public List<Map<String, String>> findCallBacks(String modelId) {
        return safetyBpmWebServiceService.findCallBacks(modelId);
    }

    public void updateFlowStatus(String tableName, String processId, String release) {
        safetyBpmWebServiceService.updateFlowStatus(tableName, processId, release);
    }
}
