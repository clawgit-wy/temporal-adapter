package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.param.app.GeneralConfigAppParam;
import com.yutong.safety.common.param.web.GeneralConfigInfoParam;
import com.yutong.safety.core.handler.SafetyGeneralConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-2-27 17:33
 */
@RestController
@RequestMapping("/app/safety/config")
@Api(tags = "访客发卡发消息")
public class SafetyGeneralConfigAppController {
    @Autowired
    private SafetyGeneralConfigHandler safetyGeneralConfigHandler;

    /**
     * 获取数据操作接口
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/infoByType")
    public Object getConfigByType(GeneralConfigInfoParam param) {
        return safetyGeneralConfigHandler.getConfigByType(param.getGeneralType());
    }

    /**
     * 发送短信或者宇信通消息提醒功能接口
     * @param param
     * @return
     */
    @ApiOperation(value = "消息提醒功能")
    @RequestMapping("/sendNote")
    public Object sendNote(GeneralConfigAppParam param) {
        return safetyGeneralConfigHandler.sendNote(param);
    }
}
