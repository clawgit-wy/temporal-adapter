package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.result.BpmModelRt;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyBpmWebServiceMapper {

    public List<Map<String, String>> findAllModels();

    List<Map<String, String>> findTableColumns(String templateFormId);

    List<Map<String, Object>> findFieldValues(@Param("tableName") String templateFormId, @Param("columns") String fieldIds, @Param("flowId") String formInstanceId);

    List<Map<String, String>> findCallBacks(String modelId);

    void updateFlowStatus(@Param("table") String tableName, @Param("flowId") String processId, @Param("status") String release);

    String findIdByFormId(@Param("table")String formId);

    List<Map<String,String>> findBpmModelByFormId(String formId);

    BpmModelRt selectModelInfo(@Param("modelId")String modelId, @Param("formId")String formId);

    String getProcessId(@Param("formInstanceId")String formInstanceId,@Param("type")String type);
}
