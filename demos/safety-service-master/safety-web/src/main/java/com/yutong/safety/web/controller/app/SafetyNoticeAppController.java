package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.handler.SafetyNoticeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/safety/notice")
public class SafetyNoticeAppController {
    @Autowired
    private SafetyNoticeHandler safetyNoticeHandler;

    @RequestMapping("/info")
    public Object getNotice() {
        return safetyNoticeHandler.getNotice();
    }

}
