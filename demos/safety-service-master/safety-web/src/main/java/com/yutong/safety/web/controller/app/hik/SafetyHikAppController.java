package com.yutong.safety.web.controller.app.hik;


import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.handler.hik.SafetyHikAbnormalEnterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "进出分析")
@RestController
@RequestMapping("/app/safety/hik")
public class SafetyHikAppController {

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetyHikAbnormalEnterHandler safetyHikAbnormalEnterHandler;

    @ApiOperation(value = "照片检索")
    @RequestMapping(value = "/searchPhoto", method = {RequestMethod.GET, RequestMethod.POST})
    public VO searchPhoto(String imageUrl) {
        return safetyArtemisHandler.searchPhoto(imageUrl);
    }

    @ApiOperation(value = "定时更新异常入侵记录-海康平台触发调用")
    @RequestMapping(value = "/syncAbnormalEnter", method = {RequestMethod.POST})
    public VO syncAbnormalEnter() {
        return safetyHikAbnormalEnterHandler.syncAbnormalEnter();
    }
}
