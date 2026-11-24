package com.yutong.modules.common.factory.action;

import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@Component("eipJson")
public class EipJsonService extends ActionService{


    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        if(config.getInterfaceMethod().equalsIgnoreCase("get")){
            return get(config,input);
        }
        if(config.getInterfaceMethod().equalsIgnoreCase("post")){
            return post(config,input);
        }
        return "";
    }

    public String post(YtsafetyApiRouteConfig config, Object input) {
        String url = config.getSysUrl() + (config.getIntefacePath() == null ? "" : config.getIntefacePath());
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json;charset=UTF-8");
        header.add("Accept", MediaType.APPLICATION_JSON.getType());
        header.add("Connection", "close");
        header.add("cache-control", "no-cache");
        header.add("Postman-Token", "f3df757a-f4b7-4081-b002-1ba38e9ecaf0");
        HttpEntity<Object> requestEntity = new HttpEntity<>(input, header);
        return restTemplate.postForEntity(url, requestEntity, String.class).getBody();
    }

    public String get(YtsafetyApiRouteConfig config, Object input) {
        String url = config.getSysUrl() + (config.getIntefacePath() == null ? "" : config.getIntefacePath());
        // 将入参拼接为查询字符串
        String queryString = buildQueryString(input);
        if (StringUtils.isNotBlank(queryString)) {
            // 区分链接里面是否已有 ?：已有则用 & 追加，否则用 ? 起始
            url += (url.contains("?") ? "&" : "?") + queryString;
        }
        HttpHeaders header = new HttpHeaders();
        header.add("Connection", "close");
        header.add("cache-control", "no-cache");
        header.add("Postman-Token", "f3df757a-f4b7-4081-b002-1ba38e9ecaf0");
        HttpEntity<Object> requestEntity = new HttpEntity<>(header);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }

    /**
     * 将入参转换为 key=value&key=value 形式的查询字符串
     * 支持 Map、JSON 字符串，以及已是查询字符串形式的入参
     */
    private String buildQueryString(Object input) {
        if (input == null) {
            return "";
        }
        // Map：逐项拼接
        if (input instanceof Map) {
            return appendParams((Map<?, ?>) input);
        }
        String text = input.toString();
        if (StringUtils.isBlank(text)) {
            return "";
        }
        // JSON 对象字符串：解析后拼接
        if (text.trim().startsWith("{")) {
            JSONObject jsonObject = JSONObject.parseObject(text);
            return appendParams(jsonObject);
        }
        // 已是 key=value&key=value 形式：原样返回
        return text;
    }

    /**
     * 遍历参数集合，URL 编码后拼接为查询字符串
     */
    private String appendParams(Map<?, ?> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : params.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(encode(String.valueOf(entry.getKey())))
                    .append("=")
                    .append(encode(String.valueOf(entry.getValue())));
        }
        return sb.toString();
    }

    /**
     * URL 编码
     */
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("URL编码失败：{}", e.getMessage());
            return value;
        }
    }
}
