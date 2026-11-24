package com.yutong.safety.core.service.pub;

import com.alibaba.fastjson.JSONObject;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.HttpFileUtil;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.pub.PubFileHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Service
@Slf4j
public class PubFileService implements PubFileHandler {
    @Value("${safety.web.file.uploadUrl}")
    private String picUploadUrl;

    @Override
    public String picUpload(MultipartFile file, String token) {
        LogUtils.info("picUploadUrl:{}", picUploadUrl);
        String picFileUrl = HttpFileUtil.uploadNewFile(picUploadUrl, file, token);
        if(StringUtils.isEmpty(picFileUrl)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "上传异常");
        }

        JSONObject resultJson = (JSONObject) JsonUtils.parse(picFileUrl);
        if(!"0".equals(resultJson.getString("code"))) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "上传异常");
        }

        String picFilePath = resultJson.getString("data");
        picFilePath = org.apache.commons.lang3.StringUtils.substringBefore(picFilePath, "_");
        return picFilePath;
    }

    @Override
    public String imgToBase64(String img) {
        byte[] buffer = null;
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 创建URL
            URL url = new URL(img);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        Base64.Encoder encode = Base64.getEncoder();
        return ObjectUtils.isEmpty(buffer)?"":encode.encodeToString(buffer);
    }
}
