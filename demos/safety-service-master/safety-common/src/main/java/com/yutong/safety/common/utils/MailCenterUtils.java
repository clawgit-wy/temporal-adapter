package com.yutong.safety.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MailCenterUtils {

    @Value("${iam.mail.url}")
    private String mailUrl;

    @Value("${iam.mail.templateCode}")
    private String templateCode;

    @Autowired
    private WebserviceUtil webserviceUtil;


    /**
     * @author lkp
     * @time 2023-10-24
     * 邮件发送数据
     * @param subject
     * @param text
     * @param toMail
     * @return
     */
    public String sendMail(String subject, String text, String toMail, String sender, String senderFrom) {

        Map headParam = new HashMap();
        headParam.put("Content-Type", "application/json");
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("emailConfigId", templateCode);//邮件配置Id
        bodyParam.put("title", subject);//标题
        bodyParam.put("content", text);//内容（和模板ID二选一）
        bodyParam.put("senderFrom", senderFrom);//发送人邮箱
        bodyParam.put("recipient", toMail);//接收人邮箱
        //自己来做
        bodyParam.put("sender", sender);//发送人-备注
        bodyParam.put("copier", "");//抄送人
        bodyParam.put("bcopier", "");//密码抄送人

        String result = webserviceUtil.sendHttp(mailUrl, "2", bodyParam.toString(), headParam, "POST");
        if(StringUtils.isNotEmpty(result)) {
            JSONObject resJson = JSONObject.parseObject(result);
            //默认是T，成功是F
            if(resJson.containsKey("success") && resJson.getBoolean("success")) {
                return "F";
            }
        }
        return "T";
    }

    public String sendMailNew(String subject, String text, String toMail, String sender, String senderFrom, String copiers) {

        Map headParam = new HashMap();
        headParam.put("Content-Type", "application/json");
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("emailConfigId", templateCode);//邮件配置Id
        bodyParam.put("title", subject);//标题
        bodyParam.put("content", text);//内容（和模板ID二选一）
        bodyParam.put("senderFrom", senderFrom);//发送人邮箱
        bodyParam.put("recipient", toMail);//接收人邮箱
        //自己来做
        bodyParam.put("sender", sender);//发送人-备注
        bodyParam.put("copier", copiers);//抄送人 多个抄送人 分号拼接
        bodyParam.put("bcopier", "");//密码抄送人
        String result = webserviceUtil.sendHttp(mailUrl, "2", bodyParam.toString(), headParam, "POST");
        if(StringUtils.isNotEmpty(result)) {
            JSONObject resJson = JSONObject.parseObject(result);
            //默认是T，成功是F
            if(resJson.containsKey("success") && resJson.getBoolean("success")) {
                return "F";
            }
        }
        return "T";
    }
}
