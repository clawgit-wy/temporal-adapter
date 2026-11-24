package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.IamTokenApiService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.HttpFileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IamTokenApiServiceImpl implements IamTokenApiService {

    @Value("${ytsafety.eip.todo.apikey}")
    private String apikey;

    @Value("${ytsafety.eip.todo.iamUrl}")
    private String iamUrl;

    @Override
    public String getToken(String sessionKey) {
        String LOGIN_URL = iamUrl+apikey;
        JSONObject param = new JSONObject();
        param.put("sessionKey", sessionKey);
        param.put("src", "");

        String result = HttpFileUtil.sendPost(LOGIN_URL, param.toJSONString());
        if (result == null) {
            log.error("获取IAM token失败，请求结果为null");
            throw new JeecgBootException("获取IAM token失败");
        }

        JSONObject resp = JSON.parseObject(result);
        if (resp == null || !Boolean.TRUE.equals(resp.getBoolean("success"))) {
            log.error("获取IAM token失败，响应：{}", result);
            throw new JeecgBootException("获取IAM token失败: " + resp.getString("message"));
        }

        JSONObject data = resp.getJSONObject("result");
        if (data == null) {
            log.error("获取IAM token失败，响应中result为空，响应：{}", result);
            throw new JeecgBootException("获取IAM token失败");
        }

        return data.getString("token");
    }
}
