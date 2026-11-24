package com.yutong.safety.core.handler.pub;

import org.springframework.web.multipart.MultipartFile;

public interface PubFileHandler {

    String picUpload(MultipartFile file, String token);
    String imgToBase64(String url);

}
