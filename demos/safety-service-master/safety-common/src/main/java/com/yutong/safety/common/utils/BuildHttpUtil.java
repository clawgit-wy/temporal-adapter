package com.yutong.safety.common.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 楼宇门禁Http请求工具类
 * @author gwxiexj
 * @since 2025-03-24 18:44
 */
@Component
public class BuildHttpUtil {

    @Value("${build.encryption.key}")
    private String buildEncryKey;

    @Value("${build.signature.key}")
    private String buildSignKey;

    @Value("${build.appid}")
    private String buildAppID;

    @Value("${build.dpcode}")
    private String buildDpcode;

    /**
     * 楼宇门禁POST请求
     * @param url 楼宇门禁对应接口
     * @param data JSON格式入参
     * @return 返回值
     */
    public String sendBuildHttpPost(String url,String data) throws Exception{
        String entryParam = BuildEncryptUtil.desEncrypt(data,buildEncryKey);
        String cipherString = URLEncoder.encode(entryParam, String.valueOf(StandardCharsets.UTF_8));
        Map<String,String> openReq = new HashMap<>();
        openReq.put("appid",buildAppID);
        openReq.put("ctdate",DateUtils.format(new Date(),5));
        openReq.put("data",cipherString);
        openReq.put("dpcode",buildDpcode);
        openReq.put("msgid", UUID.randomUUID().toString());
        String sign = BuildEncryptUtil.sha256Sign(openReq, buildSignKey);
        openReq.put("sign",sign);
        LogUtils.info("sendBuildHttpPost请求入参="+openReq+";url="+url);
        org.json.JSONObject jsonBody = new org.json.JSONObject(openReq);
        String result = HttpUtil.createPost(url).body(jsonBody.toString()).execute().body();
        try{
            JSONObject retObj = JSONObject.parseObject(result);
            if(retObj.getInteger("retcode") != 200){
                //重试一次
                result = HttpUtil.createPost(url).body(jsonBody.toString()).execute().body();
                retObj = JSONObject.parseObject(result);
            }
            if(retObj.getInteger("retcode") != 200){
                LogUtils.error("sendBuildHttpPost返回异常",result);
                return ResponseEnum.FAIL.getName();
            }
            else{
                String rd = URLDecoder.decode(retObj.getString("data"), "UTF-8");
                return BuildEncryptUtil.desDecrypt(rd,buildEncryKey);
            }
        }catch (Exception e){
            LogUtils.error("sendBuildHttpPost返回异常",e);
            return ResponseEnum.FAIL.getName();
        }
    }

}
