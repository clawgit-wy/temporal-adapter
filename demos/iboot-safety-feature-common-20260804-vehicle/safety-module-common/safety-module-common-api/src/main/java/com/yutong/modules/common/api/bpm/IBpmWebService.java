package com.yutong.modules.common.api.bpm;

import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;

public interface IBpmWebService {


    /**
     * BPM调用此接口，获取异构系统表单模板记录，用于配置流程模板
     * @param bpmTemplateFormDTO
     * @return TemplateFormResultVO list
     */
    String getTemplateFormList(BpmTemplateFormDTO bpmTemplateFormDTO);

    /**
     * 接口二 BPM调用此接口，获取异构系统表单字段记录
     * @param bpmTemplateFormDTO
     * @return FormFieldResultVO list
     */
    String getFormFieldList(BpmTemplateFormDTO bpmTemplateFormDTO);

    /**
     * BPM调用此接口，根据接口二获取的表单字段记录，查询表单对应的值
     * @param bpmTemplateFormDTO
     * @return FormFieldValueResultVO list
     */
    String getFormFieldValueList(BpmTemplateFormDTO bpmTemplateFormDTO);


    /**
     * BPM调用此接口，获取不同业务逻辑的函数，用于流程节点事件的配置
     * @param bpmTemplateFormDTO
     * @return MethodInfoResultVO
     */
    String getMethodInfo(BpmTemplateFormDTO bpmTemplateFormDTO);

    /**
     * BPM调用此接口，将接口四配置的函数回传给异构系统,异构系统可根据函数标识，实现不同业务逻辑（如修改表单数据等）
     * @param bpmTemplateFormVO
     * @return 返回值示例：成功：“T”；失败：“F:异常描述”
     */
    String doMethodProcess(BpmMethodProcessDTO bpmTemplateFormVO);

}
