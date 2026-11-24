package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.core.handler.pub.PubFileHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "安保入厂-公共接口")
@RestController
@RequestMapping("/web/pub/")
public class SafetyPubController {
    @Autowired
    private PubFileHandler pubFileHandler;

    /**
     * 上传图片
     * @return
     */
    @ApiOperation(value = "文件上传中转接口（包含图片）")
    @RequestMapping("file/picUpload")
    public Object picUpload(MultipartFile file, @RequestHeader("X-Access-Token") String token) {
        return pubFileHandler.picUpload(file, token);
    }

}
