package com.yutong.modules.common.service.impl;

import com.yutong.modules.common.api.IEipWebService;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.vo.EipToDoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class EipWebServiceImpl implements IEipWebService {

    @Value("${ytsafety.eip.todo.formEmpNo}")
    private String formEmpNo;

    @Resource
    private ThirdApiFactory thirdApiFactory;

    @Override
    public void createEipToDo(EipToDoVO eipToDoVO) {
        log.info("【发起EIP代办】入参{}", eipToDoVO.toString());
        try {
            StringBuilder params = new StringBuilder();
            params.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.notify.sys.kmss.landray.com/\">");
            params.append("   <soapenv:Header/>");
            params.append("   <soapenv:Body>");
            params.append("      <web:sendTodo>");
            params.append("         <arg0>");
            params.append("            <appName>").append(eipToDoVO.getAppName()).append("</appName>");
            params.append("            <createTime>").append(DateUtils.date2Str(new Date(), DateUtils.datetimeFormat.get())).append("</createTime>");
            params.append("            <docCreator>{\"PersonNo\":\"").append(formEmpNo).append("\"}</docCreator>");
            params.append("            <level>3</level>");
            params.append("            <link>").append(eipToDoVO.getLink()).append("</link>");
            params.append("            <modelId>").append(eipToDoVO.getModelId()).append("</modelId>");
            params.append("            <modelName>").append(eipToDoVO.getModelName()).append("</modelName>");
            params.append("            <subject>").append(eipToDoVO.getSubject()).append("</subject>");
            params.append("            <targets>").append(eipToDoVO.getTargets()).append("</targets>");
            params.append("            <type>").append(eipToDoVO.getType()).append("</type>");

            //是否能打开，1为打开，0或默认加锁
            if (StringUtils.isNotBlank(eipToDoVO.getOthers())) {
                params.append("            <others>").append(eipToDoVO.getOthers()).append("</others>");
            }
            params.append("         </arg0>");
            params.append("      </web:sendTodo>");
            params.append("   </soapenv:Body>");
            params.append("</soapenv:Envelope>");
            String resultXml = thirdApiFactory.executeMethod("eip_send_to_do", params.toString());

            log.info("【触发EIP代办】返回值{}", resultXml);
        } catch (Exception e) {
            log.error("【触发EIP代办失败】{}", e.getMessage());
        }
    }
}
