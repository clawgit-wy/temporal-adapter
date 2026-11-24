package com.yutong.modules.common.utils;

import com.yutong.modules.common.entity.bpm.SoapBody;
import com.yutong.modules.common.entity.bpm.SoapEnvelope;
import com.yutong.modules.common.entity.eip.todo.EipSendTodo;
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

/**
 * EIP待办xml转换帮助类
 */
public class EipXmlUtil {

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
        nsMapping.put("http://webservice.notify.sys.kmss.landray.com/", "web");

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
     * 转换待办为soap格式
     */
    public static String sendTodoToSoap(EipSendTodo sendTodo) {
        String webNs = "http://webservice.notify.sys.kmss.landray.com/";
        QName qName = new QName(webNs, "sendTodo");
        JAXBElement<EipSendTodo> element = new JAXBElement<>(qName, EipSendTodo.class, sendTodo);

        SoapBody<EipSendTodo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<EipSendTodo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        return beanToXml(envelope, sendTodo.getClass(), sendTodo.getArg0().getClass());
    }

    /**
     * 获取目标人员
     * @param empNos
     * @return
     */
    public static String getTaragets(List<String> empNos){
        if(empNos==null||empNos.isEmpty()) return null;
        List<Object> logins = new ArrayList<>();
        for (String empNo:empNos) {
            Object obj = new Object() {
                public final String PersonNo = empNo;
            };
            logins.add(obj);
        }
        return JsonUtils.toJsonString(logins);
    }
    /**
     * 获取创建人员
     * @param empNo
     * @return
     */
    public static String docCreator(String empNo) {
        Object obj = new Object() {
            public final String PersonNo = empNo;
        };
        return JsonUtils.toJsonString(obj);
    }


}
