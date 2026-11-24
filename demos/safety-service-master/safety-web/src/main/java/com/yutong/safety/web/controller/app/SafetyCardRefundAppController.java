package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.handler.SafetyCardRefundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-2-2 16:51
 */
@RestController
@RequestMapping("/app/safety/refund")
public class SafetyCardRefundAppController {

    @Autowired
    private SafetyCardRefundHandler safetyCardRefundHandler;


    /**
     * 定时任务
     * @return
     */
    @RequestMapping("/taskDays")
    public Object taskCardRefundDays() {
        safetyCardRefundHandler.taskCardRefundDays();
        return null;
    }
}
