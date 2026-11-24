package com.yutong.safety.core.service.wechat;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.wechat.ResponseStatusEnum;
import com.yutong.safety.core.dto.wechat.WxRequestDTO;
import com.yutong.safety.core.handler.wechat.WxMobileHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * @author caomg
 * @date 2024-8-12 11:04
 * @description WxMobileService
 */
@Service
@Slf4j
public class WxMobileService implements WxMobileHandler {

    @Value("${token.appid}")
    private String tokenAppid;

    @Value("${applet.openIdUrl}")
    private String appletUrl;

    @Value("${token.appsecret}")
    private String tokenAppsecret;

    @Value("${push.message.gettokenurl}")
    private String getTokenUrl;

    @Value("${push.message.towxurl}")
    private String pushMessageToWxUrl;

    @Value("${smile.getSubscribe}")
    private String getSubscribe;

    @Value("${smile.incrementSubscribe}")
    private String incrementSubscribe;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Override
    public VO pushMessageToWx(WxRequestDTO en) {
        LogUtils.info("进入微信消息发送方法");
        LogUtils.info("restTemplate="+restTemplate);
        //调用smile的接口查询token
        long timestamp = System.currentTimeMillis();
        //签名 String sign=appid+timestamp+appsecret;
        String sign = DigestUtils.md5DigestAsHex((tokenAppid + timestamp + tokenAppsecret).getBytes());

        try {
            Map headParam = new HashMap();
            headParam.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("appid", tokenAppid);
            bodyParam.put("timestamp", String.valueOf(timestamp));
            bodyParam.put("sign", sign);
            //调接口查token
            String tokenResult = webserviceUtil.sendHttp(getTokenUrl, "1", bodyParam.toString(), headParam, "POST");

            LogUtils.info("获取token的接口调用");
            String code = JSON.parseObject(tokenResult).get("code").toString();
            String msg = ObjectUtil.isNotEmpty(JSON.parseObject(tokenResult).get("msg"))?JSON.parseObject(tokenResult).get("msg").toString():"";
            String data = ObjectUtil.isNotEmpty(JSON.parseObject(tokenResult).get("data"))?JSON.parseObject(tokenResult).get("data").toString():"";
            LogUtils.info("获取token的状态码"+code);
            if(!ResponseStatusEnum.SUCCESS.getCode().equals(code)){
                return new VO(code,msg,data);
            }
            LogUtils.info("获取token"+data);


            Map headParamWx = new HashMap();
            headParamWx.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headParamWx.put("token", data);
            headParamWx.put("appid", tokenAppid);
            String jsonString = JSONObject.toJSONString(en);

            //调接口发消息
            LogUtils.info("发送微信消息的远程地址="+pushMessageToWxUrl);
            String pushResult = webserviceUtil.sendHttp(pushMessageToWxUrl, "2", jsonString, headParamWx, "POST");

            String pushResultCode = JSON.parseObject(pushResult).get("code").toString();
            String pushResultMsg = ObjectUtil.isNotEmpty(JSON.parseObject(pushResult).get("msg"))?JSON.parseObject(pushResult).get("msg").toString():"";
            String pushResultData = ObjectUtil.isNotEmpty(JSON.parseObject(pushResult).get("data"))?JSON.parseObject(pushResult).get("data").toString():"";
            LogUtils.info("微信发送的状态码 "+pushResultCode+"=====pushResultMsg===="+pushResultMsg+"=====pushResultData===="+pushResultData);
            return new VO(pushResultCode,pushResultMsg,pushResultData);
        } catch (Exception e){
            LogUtils.error("微信推送出错。。。" + e.getMessage());
            return new VO(ResponseStatusEnum.FAIL.getCode(), e.getMessage());
        }
    }

    @Override
    public String getOpenId(String mobile) {
        String openIdUrl = String.format(appletUrl, mobile);
        HttpHeaders headers=new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(openIdUrl, HttpMethod.GET, requestEntity, String.class);
        String body = exchange.getBody();
        LogUtils.info("获取applet系统中的openId对应数据：{}", body);
        if (StringUtils.isNoneBlank(body) && body.contains("openId")) {
            JSONObject jsonObject = JSON.parseObject(body);
            if("0".equals(jsonObject.getString("code"))) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                return dataObject.getString("openId");
            }
        }
        return null;
    }

    @Override
    public String getSubscribe(String openId) {
        if (StringUtils.isBlank(openId)) {throw new BizException(ResponseEnum.EXCEPTION.getCode(), "openId不能为空！");}

        Map<String, String> map = new HashMap<>();
        map.put("openId", openId);
        String jsonString = JSONObject.toJSONString(map);

        //调接口发消息
        LogUtils.info("发送微信消息的远程地址="+getSubscribe);
        Map headParamWx = new HashMap();
        headParamWx.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String result = webserviceUtil.sendHttp(getSubscribe, "2", jsonString, headParamWx, "POST");

        String pushResultCode = JSON.parseObject(result).get("code").toString();
        String pushResultMsg = ObjectUtil.isNotEmpty(JSON.parseObject(result).get("msg"))?JSON.parseObject(result).get("msg").toString():"";
        String pushResultData = ObjectUtil.isNotEmpty(JSON.parseObject(result).get("data"))?JSON.parseObject(result).get("data").toString():"";
        LogUtils.info("微信发送的状态码"+pushResultCode+"=====pushResultMsg===="+pushResultMsg+"=====pushResultData===="+pushResultData);

        return pushResultData;
    }

}
