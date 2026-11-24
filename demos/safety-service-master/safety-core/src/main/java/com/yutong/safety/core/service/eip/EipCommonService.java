package com.yutong.safety.core.service.eip;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.EipToDoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * EIP接口调用公用方法
 *
 * @author gwxiexj
 * @since 2024-09-10 19:55
 */
@Service
public class EipCommonService {

    @Value("${eip.todo.flow}")
    private String eipToDoUrl;

    //代办的发起人待定
    @Value("${an.eip.todo.from}")
    private String formEmpNo;

    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 发起EIP代办
     * @return 接口返回值
     */
    public String createEipToDo(EipToDoVO toDoVO) {
        LogUtils.info("【发起EIP代办】入参{}", toDoVO.toString());
        StringBuilder params = new StringBuilder();
        params.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.notify.sys.kmss.landray.com/\">");
        params.append("   <soapenv:Header/>");
        params.append("   <soapenv:Body>");
        params.append("      <web:sendTodo>");
        params.append("         <arg0>");
        params.append("            <appName>").append(toDoVO.getAppName()).append("</appName>");
        params.append("            <createTime>").append(DateUtils.format(new Date(),2)).append("</createTime>");
        params.append("            <docCreator>").append(toDoVO.getDocCreator()).append("</docCreator>");
        params.append("            <level>3</level>");
        params.append("            <link>").append(toDoVO.getLink()).append("</link>");
        params.append("            <modelId>").append(toDoVO.getModelId()).append("</modelId>");
        params.append("            <modelName>").append(toDoVO.getModelName()).append("</modelName>");
        params.append("            <subject>").append(toDoVO.getSubject()).append("</subject>");
        params.append("            <targets>").append(toDoVO.getTargets()).append("</targets>");
        params.append("            <type>").append(toDoVO.getType()).append("</type>");

        //是否能打开，1为打开，0或默认加锁
        if(!StringUtils.isEmpty(toDoVO.getOthers())){
            params.append("            <others>").append(toDoVO.getOthers()).append("</others>");
        }

        params.append("         </arg0>");
        params.append("      </web:sendTodo>");
        params.append("   </soapenv:Body>");params.append("</soapenv:Envelope>");
        String webResult = webserviceUtil.doWebservicePost(eipToDoUrl, params.toString());
        LogUtils.info("【触发EIP代办】返回值{}",webResult);
        Map<String, Object> resultMap = JsonUtils.toMap(webResult);
        Map<String, Object> envelopeMap = JsonUtils.toMap(String.valueOf(resultMap.get("soap:Envelope")));
        Map<String, Object> bodyMap = JsonUtils.toMap(String.valueOf(envelopeMap.get("soap:Body")));
        Map<String, Object> multiRefMap = JsonUtils.toMap(String.valueOf(bodyMap.get("ns1:sendTodoResponse")));
        Map<String, Object> retMap = JsonUtils.toMap(String.valueOf(multiRefMap.get("return")));
        String returnState = String.valueOf(retMap.get("returnState"));
        if(StringUtils.isNotEmpty(returnState)) {
            return returnState;
        }
        return null;
    }

    /**
     * 设置EIP代办为已办
     * @param toDoVO 入参分支对象
     * @return 返回值结果
     */
    public String setEipToDoDone(EipToDoVO toDoVO) {
        LogUtils.info("【设置已办】入参{}", toDoVO.toString());
        StringBuilder params = new StringBuilder();
        params.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.notify.sys.kmss.landray.com/\">");
        params.append("   <soapenv:Header/>");
        params.append("   <soapenv:Body>");
        params.append("      <web:setTodoDone>");
        params.append("         <arg0>");
        params.append("            <appName>").append(toDoVO.getAppName()).append("</appName>");
        params.append("            <key></key>");
        params.append("            <modelId>").append(toDoVO.getModelId()).append("</modelId>");
        params.append("            <modelName></modelName>");
        params.append("            <optType>1</optType>");
        params.append("            <param1></param1>");
        params.append("            <param2></param2>");
        params.append("            <targets></targets>");
        params.append("            <type>").append(toDoVO.getType()).append("</type>");
        params.append("         </arg0>");
        params.append("      </web:setTodoDone>");
        params.append("   </soapenv:Body>");
        params.append("</soapenv:Envelope>");
        String webResult = webserviceUtil.doWebservicePost(eipToDoUrl, params.toString());
        LogUtils.info("【触发EIP代办】返回值{}",webResult);
        Map<String, Object> resultMap = JsonUtils.toMap(webResult);
        Map<String, Object> envelopeMap = JsonUtils.toMap(String.valueOf(resultMap.get("soap:Envelope")));
        Map<String, Object> bodyMap = JsonUtils.toMap(String.valueOf(envelopeMap.get("soap:Body")));
        Map<String, Object> multiRefMap = JsonUtils.toMap(String.valueOf(bodyMap.get("ns1:setTodoDoneResponse")));
        Map<String, Object> retMap = JsonUtils.toMap(String.valueOf(multiRefMap.get("return")));
        String returnState = String.valueOf(retMap.get("returnState"));
        if(StringUtils.isNotEmpty(returnState)) {
            return returnState;
        }
        return null;
    }

    public void eipToDoDone(String modelId, String empCode) {
        try{
            EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
            this.setEipToDoDone(eipToDoVO);
        }catch (Exception e){
            LogUtils.error("【消除代办异常】" + ",from " + formEmpNo + " to " + empCode + ":", e);
        }
    }

    public void createEipOfDefaultCreator(EipToDoVO toDoVO, String empCode) {
        try{
            if (StringUtils.isEmpty(toDoVO.getDocCreator())) {
                toDoVO.setDocCreator("{\"PersonNo\":\"" + formEmpNo + "\"}");
            }
            this.createEipToDo(toDoVO);
        }catch (Exception e){
            LogUtils.error("【发送代办失败】" + toDoVO.getModelName() + ",from " + formEmpNo + " to " + empCode + ":", e);
        }
    }


}
