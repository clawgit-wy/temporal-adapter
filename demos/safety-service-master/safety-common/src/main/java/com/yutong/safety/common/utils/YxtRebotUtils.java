package com.yutong.safety.common.utils;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.alibaba.excel.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 宇信通机器人消息推送
 */
@Component
public class YxtRebotUtils {

    @Value("${im.sendMsg.postUrl}")
    private String imSendMsgPostUrl;
    /**
     * 发送机器人消息
     * @param url 机器人消息推送URL
     * @param rebotId 机器人ID
     * @param toEmpNos 消息接收人员工号
     * @param msg 消息内容
     * @param restTemplate rest模板
     */
    public void postForEntity(String url,String rebotId,String toEmpNos,String msg, RestTemplate restTemplate) {

        Map<String, String> params = new HashMap<>();
        LogUtils.info("发送机器人消息开始，接收人={}，内容={}", toEmpNos, msg);
        params.put("appId", "1");
        params.put("sessionType", "r");
        params.put("contentType", "0");
        params.put("content", msg);
        params.put("from", rebotId);
        params.put("to", toEmpNos);

        if(StringUtils.isBlank(url)){
            url = imSendMsgPostUrl;
        }
        //提交参数设置
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        //将参数放入MultiValueMap中
        for(Map.Entry<String, String> entry : params.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            map.add(mapKey, mapValue);
        }
        // 发送post请求，并输出结果
        String result = restTemplate.postForObject(url, map, String.class);
        LogUtils.info("发送机器人消息结果={}", result);
    }
}
