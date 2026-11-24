package com.yutong.safety.web.controller.pub.wechat;

import com.yutong.safety.core.dto.wechat.WxRequestDTO;
import com.yutong.safety.core.handler.wechat.WxMobileHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author caomg
 * @date 2024-8-12 10:59
 * @description WxMobileController
 */
@Api(tags = "微信")
@RestController
@RequestMapping("/wx")
public class WxMobileController {
    @Autowired
    WxMobileHandler wxMobileHandler;

    @RequestMapping("/pushMessageToWx")
    @ApiOperation("微信消息发送")
    public Object pushMessageToWx(@RequestBody WxRequestDTO en) {
        return  wxMobileHandler.pushMessageToWx(en);
    }
}
