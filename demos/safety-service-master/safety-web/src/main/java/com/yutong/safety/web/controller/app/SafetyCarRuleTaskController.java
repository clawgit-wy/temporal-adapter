package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.handler.SafetyCarOutRuleHandler;
import com.yutong.safety.core.handler.SafetyGroupHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆检查规则调整,同步门禁系统
 * @author lukunpeng
 * @time 2024-4-3
 */
@RestController
@RequestMapping("/app/safety/carRule")
public class SafetyCarRuleTaskController {

    @Autowired
    private SafetyCarOutRuleHandler safetyCarOutRuleHandler;

    @Autowired
    private SafetyGroupHandler safetyGroupHandler;

    @ApiOperation(value = "车辆检查规则调整,同步门禁系统定时任务")
    @RequestMapping("/doTask")
    public Object doTask() {
        return safetyCarOutRuleHandler.doTask();
    }

    /**
     * 定时任务执行增量或全量数据
     * @return
     */
    @ApiOperation(value = "增量同步组织结构")
    @RequestMapping("/taskGroup")
    public Object taskGroup(String beginDate) {
        safetyGroupHandler.taskGroup(false, beginDate);
        return null;
    }


    /**
     * 定时任务执行增量或全量数据
     * @return
     */
    @ApiOperation(value = "全量同步组织结构")
    @RequestMapping("/taskAllGroup")
    public Object taskAllGroup() {
        safetyGroupHandler.taskGroup(true, null);
        return null;
    }
}
