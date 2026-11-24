package com.yutong.modules.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.entity.bpm.*;
import com.yutong.modules.common.entity.bpm.create.CreateResponseEnvelope;
import com.yutong.modules.common.entity.bpm.create.CreateProcess;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JaxbXmlUtil {

    /**
     * 多个实体类传入，解决not known to this context
     * @param obj 待序列化根对象
     * @param clazzes 内部所有用到的实体class
     */
    public static <T> String beanToXml(T obj, Class<?>... clazzes)  {
        try {
            Class<?> rootCls = obj.getClass();
            // 合并根类 + 内部实体类数组
            Class<?>[] allCls = new Class<?>[clazzes.length + 1];
            allCls[0] = rootCls;
            System.arraycopy(clazzes, 0, allCls, 1, clazzes.length);
            JAXBContext context = JAXBContext.newInstance(allCls);
            return doMarshal(context, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> String doMarshal(JAXBContext context, T obj) throws Exception {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // 1. 定义命名空间-前缀映射
        Map<String, String> nsMapping = new HashMap<>();
        nsMapping.put("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        nsMapping.put("http://interfaces.lbpmdocking.sys.kmss.landray.com/", "int");
        nsMapping.put("http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/", "ext");
        nsMapping.put("http://ws.self.kmss.landray.com/", "ws");

        // 2. 实例化自定义映射器
        SoapNsPrefixMapper prefixMapper = new SoapNsPrefixMapper(nsMapping);

        // 3. 反射调用 setProperty，绕过编译强类型检查，解决类型不匹配报错
        Method setPropMethod = marshaller.getClass().getMethod("setProperty", String.class, Object.class);
        setPropMethod.invoke(marshaller, "com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    // XML字符串 解析为对象（新增，解析返回报文）
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String xml, Class<T> targetCls)  {
        try {
            JAXBContext context = JAXBContext.newInstance(targetCls);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            return (T) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换创建流程为soap格式
     * @returnc
     */
    public static String CreateProcessToSaop(CreateProcess createProcess) throws Exception {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "CreateProcess");
        JAXBElement<CreateProcess> element = new JAXBElement<>(qName, CreateProcess.class, createProcess);

        // 3. 封装报文结构
        SoapBody<CreateProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<CreateProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        return beanToXml(envelope,createProcess.getClass());
    }

    /**
     * 拼接额外参数
     * @return
     */
    public static String CombineExParam(String docSub){
        Object obj = new Object(){
            public final String docSubject = docSub;
        };
        return JsonUtils.toJsonString(obj);
    }

    /**
     * 拼接创建人
     * @param loginName
     * @return
     */
    public static String CombineCreator(String loginName){
        Object obj = new Object(){
            public final String LoginName = loginName;
        };
        return JsonUtils.toJsonString(obj);
    }

    /**
     * 拼接创建人
     * @param loginNames
     * @return
     */
    public static String CombineCreator(List<String> loginNames){
        if(loginNames==null||loginNames.isEmpty()) return null;
        List<Object> logins = new ArrayList<>();
        for (String loginName:loginNames) {
            Object obj = new Object() {
                public final String LoginName = loginName;
            };
            logins.add(obj);
        }
        return JsonUtils.toJsonString(logins);
    }

    /**
     * 拼接FormId
     * @param formInstanceId
     * @param sysId
     * @param modelId
     * @param formId
     * @return
     */
    public static String CombineFormId(String formInstanceId,String sysId,String modelId,String formId){
        JSONObject formIdObj = new JSONObject();
        formIdObj.put("sysId", sysId);
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
     * 转换parameterJson
     * @return
     */
    public static String CombineParameterJson(String taskId,String auditNoteMsg,String activityType){
        Parameter parameter = new Parameter();
        parameter.setTaskId(taskId);
        parameter.setActivityType(activityType);
        parameter.setOperationType("handler_pass");
        Operation operation = new Operation();
        operation.setAuditNote(auditNoteMsg);
        operation.setNotifyType("todo");
        operation.setNotifyOnFinish(true);
        parameter.setParam(operation);
        JSONObject parameterJson = new JSONObject();
        parameterJson.put("sysWfBusinessForm.fdParameterJson",parameter);
        return parameterJson.toString();
    }

    /**
     * 转换parameterJson
     * @return
     */
    public static String CombineParameterJson(Parameter parameter){
        JSONObject parameterJson = new JSONObject();
        parameterJson.put("sysWfBusinessForm.fdParameterJson",parameter);
        return parameterJson.toString();
    }

    /**
     * 附加参数
     * @param parameter
     * @param additionsParameter
     * @return
     */
    public static String combineParameterJson(Parameter parameter, List<AdditionsParameter> additionsParameter){
        JSONObject parameterJson = new JSONObject();
        parameterJson.put("sysWfBusinessForm.fdParameterJson",parameter);
        if(additionsParameter!=null&&!additionsParameter.isEmpty()) {
            parameterJson.put("sysWfBusinessForm.fdAdditionsParameterJson", parameter);
        }
        return parameterJson.toString();
    }

    /**
     * 获取流程状态
     * @param operationType
     * @return
     */
    public static Integer getProcessStatus(String operationType){
        ProcessStatusEnums enums = null;
        switch (operationType){
            case "handler_refuse":
                enums =  ProcessStatusEnums.deny;
                break;
            case "handler_abandon":
                enums =  ProcessStatusEnums.discarded;
                break;
            case "drafter_submit":
            case "handler_pass":
            default:
                enums = ProcessStatusEnums.approve;
                break;
        }
        return enums.getCode();
    }
}
