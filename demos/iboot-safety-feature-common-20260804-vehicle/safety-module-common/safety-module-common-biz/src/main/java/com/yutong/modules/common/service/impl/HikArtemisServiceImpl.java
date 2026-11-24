package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.IHikArtemisService;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.vo.HikPictureCheckVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class HikArtemisServiceImpl implements IHikArtemisService {

    /**
     * 海康平台接口调用成功的CODE值
     */
    private static final String HIK_ITF_S_CODE = "0";

    @Resource
    private ThirdApiFactory thirdApiFactory;

    private HikPictureCheckVO artemisPictureCheck(String content) throws Exception {
        String result = thirdApiFactory.executeMethod("hik_picture_check", content);
        JSONObject respBody = JSONObject.parseObject(result);
        String code = respBody.getString("code");
        if (!StringUtils.equals(code, HIK_ITF_S_CODE) || StringUtils.isBlank(respBody.getString("data"))) {
            throw new Exception(respBody.getString("msg"));
        }
        String data = respBody.getString("data");
        return JsonUtils.parseObject(data, HikPictureCheckVO.class);
    }

    @Override
    public HikPictureCheckVO artemisPictureCheckOfUrl(String content) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("facePicUrl", content);
        return artemisPictureCheck(jsonBody.toJSONString());
    }

    @Override
    public HikPictureCheckVO artemisPictureCheckOfData(String content) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("facePicBinaryData", content);
        return artemisPictureCheck(jsonBody.toJSONString());
    }
}