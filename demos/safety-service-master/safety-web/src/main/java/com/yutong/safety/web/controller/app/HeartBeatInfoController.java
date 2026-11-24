package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.handler.SafetySheetSetHandler;
import com.yutong.safety.core.service.SafetySheetSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端获取表单设置字段内容
 */
@RestController
@RequestMapping("/app/heart")
public class HeartBeatInfoController {
    @Autowired
    private SafetySheetSetHandler safetySheetSetHandler;

    /**
     * 心跳检查接口
     * @return
     */
    @RequestMapping("/heartInfo")
    public Object getHeartBeatInfo() {
        return safetySheetSetHandler.getHeartBeatInfo();
    }

}
