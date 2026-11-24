package com.yutong.modules.common.factory.action;

import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("yxtrebot")
public class YxtRebotService  extends ActionService {

    @SuppressWarnings("unchecked")
    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        String url = config.getSysUrl()+config.getIntefacePath();
        if(input instanceof  Map) {
            Map<String, String> inputMap = (Map<String, String>) input;
            Map<String, String> params = new HashMap<>();
            log.info("发送机器人消息开始，接收人={}，发送人={}", inputMap.get("to"), inputMap.get("from"));
            params.put("appId", "1");
            params.put("sessionType", "r");
            params.put("contentType", "0");
//            params.put("content", "测试消息联通性");
//            params.put("from", "220122");
//            params.put("to", "91019877");
            params.putAll(inputMap);
            //提交参数设置
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            //将参数放入MultiValueMap中
            for(Map.Entry<String, String> entry : params.entrySet()){
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                map.add(mapKey, mapValue);
            }
            // 发送post请求，并输出结果
//            String realUrl = String.format(url,
//                    URLEncoder.encode(inputMap.get("content"), "UTF-8"),
//                    URLEncoder.encode(inputMap.get("from"), "UTF-8"),
//                    URLEncoder.encode(inputMap.get("to"), "UTF-8")
//            );
            String s = restTemplate.postForObject(url, map, String.class);
            return s;
        }else{
            throw new Exception("推送消息时参数异常");
        }
    }
}
