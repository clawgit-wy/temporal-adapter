package com.yutong.modules.common.mapper;

import com.yutong.modules.common.vo.FormFieldResultVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface BpmWebServiceMapper {

    /**
     * 查询所有模型信息
     * @return
     */
    public List<Map<String, String>> findAllModels();

    /**
     * 查询表字段信息
     * @param templateFormId
     * @return
     */
    public List<Map<String, String>> findTableColumns(String dataBaseName, String templateFormId);

    /**
     * 查询字段数据
     * @param templateFormId
     * @param fieldIds
     * @param formInstanceId
     * @return
     */
    public List<Map<String, Object>> findFieldValues(@Param("tableName") String templateFormId, @Param("columns") String fieldIds, @Param("flowId") String formInstanceId);

    /**
     * 查询回调函数
     * @param modelId
     * @return
     */
    public List<Map<String, String>> findCallBacks(String modelId);

    /**
     * 更新流程状态
     * @param tableName
     * @param processId
     * @param release
     */
    public void updateFlowStatus(@Param("table") String tableName, @Param("flowId") String processId, @Param("status") String release);

    /**
     * 依据表单主键查询模型信息
     * @param formId
     * @return
     */
    public List<Map<String,String>> findBpmModelByFormId(String formId);

    /**
     * 筛选模型信息
     * @param modelId
     * @param formId
     * @return
     */
    public FormFieldResultVO selectModelInfo(@Param("modelId")String modelId, @Param("formId")String formId);

    /**
     * 获取流程主键
     * @param formInstanceId
     * @param dataTable
     * @return
     */
    public String getProcessId(@Param("formInstanceId")String formInstanceId,@Param("dataTable")String dataTable);

}
