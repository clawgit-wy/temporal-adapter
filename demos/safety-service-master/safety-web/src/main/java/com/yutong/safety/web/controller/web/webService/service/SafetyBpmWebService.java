package com.yutong.safety.web.controller.web.webService.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 对接BPM系统必须实现的六大接口
 * 需要获取当前登录人,所以必须放在web目录下
 *
 * @author lukunpeng
 * @time 2024-4-26
 */
@WebService(name="safetyBpmWebService",targetNamespace = "http://com.yutong.safety.web.ws")
public interface SafetyBpmWebService {
    /**
     * 接口一 BPM调用此接口，获取异构系统表单模板记录，用于配置流程模板
     * @param sysId 异构系统标识 必填
     * @param language
     * @return JSON字符串
     *   例：[{
     *         "modelName":"财务",  //表单所属模块名称
     *         "modelId":"model-01",  //表单所属模块ID
     *         "templateFormId":"table-01",  //表单唯一标识
     *         "templateFormName":"出差报销",  //表单名称
     *         "formUrl":"http://eip.yutong.com"  //表单访问地址
     *       }]
     */
    @WebMethod
    String getTemplateFormList(@WebParam(name = "sysId") String sysId,
                               @WebParam(name = "language") String language);

    /**
     * 接口二 BPM调用此接口，获取异构系统表单字段记录
     * @param sysId 异构系统标识
     * @param modelId 异构系统表单模块编号
     * @param templateFormId 异构系统表单编号 必填
     * @param language
     * @return JSON字符串
     *  例：[{
     *       "fieldId":"cost_type",  //表单字段标识
     *       "fieldName":"报销类型",  //表单字段名称
     *       "type":"String"  //表单字段类型（String;Number;Boolean;Date;Array_String;Array_Number）
     *     }]
     */
    @WebMethod
    String getFormFieldList(@WebParam(name = "sysId") String sysId,
                            @WebParam(name = "modelId") String modelId,
                            @WebParam(name = "templateFormId") String templateFormId,
                            @WebParam(name = "language") String language);

    /**
     * 接口三 BPM调用此接口，根据接口二获取的表单字段记录，查询表单对应的值
     * @param sysId 异构系统标识 必填
     * @param modelId 异构系统模块标识 可为空
     * @param templateFormId 异构系统表单模板标识 必填
     * @param formInstanceId 异构系统表单实例标识 必填
     * @param fieldIds 异构系统表单字段标识集合 可为空
     * 【注】字段ID集，以逗号或分号分隔。若此参数为NULL或""时，则返回formInstanceId下的所有字段。
     * @return JSON字符串
     *  例：[{
     *        "fieldId":"cost_type",  //表单字段标识
     *        "fieldValue":"交通费"  //表单字段值
     *     }]
     */
    @WebMethod
    String getFormFieldValueList(@WebParam(name = "sysId") String sysId,
                                 @WebParam(name = "modelId") String modelId,
                                 @WebParam(name = "templateFormId") String templateFormId,
                                 @WebParam(name = "formInstanceId") String formInstanceId,
                                 @WebParam(name = "fieldIds") String fieldIds,
                                 @WebParam(name = "language") String language);

    /**
     * 接口四 BPM调用此接口，获取不同业务逻辑的函数，用于流程节点事件的配置
     * @param sysId 异构系统标识 必填
     * @param modelId 异构系统模块标识 可为空
     * @param templateFormId 异构系统表单模板标识 必填
     * @return JSON字符串
     *  例：[{
     *        "functionId":"flow_status",  //函数标识
     *        "functionName":"更新流程状态",  //函数名称
     *        "functionDes":"更新流程状态"   //函数描述
     *     }]
     * */
    @WebMethod
    String getMethodInfo(@WebParam(name = "sysId") String sysId,
                         @WebParam(name = "modelId") String modelId,
                         @WebParam(name = "templateFormId") String templateFormId,
                         @WebParam(name = "language") String language);

    /**
     * 接口五 BPM调用此接口，将接口四配置的函数回传给异构系统，
     * 异构系统可根据函数标识，实现不同业务逻辑（如修改表单数据等）
     * @param formId 异构系统标识
     * @param functionId 函数标识
     * @param processData BPM流程数据
     * @return 可自定义返回值，不影响流程
     * */
    @WebMethod
    String doMethodProcess(@WebParam(name = "formId") String formId,
                           @WebParam(name = "functionId") String functionId,
                           @WebParam(name = "processData") String processData,
                           @WebParam(name = "language") String language);

    /**
     * 接口六 暂无用，可不用实现
     * */
    @WebMethod
    String synchronizeTemplate();
}
