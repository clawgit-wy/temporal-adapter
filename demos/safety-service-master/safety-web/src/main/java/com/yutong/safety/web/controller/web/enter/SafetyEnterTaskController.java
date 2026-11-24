package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆备案定时任务
 * */
@Api(tags = "安保入厂-车辆备案定时任务")
@RestController
@RequestMapping("/web/enter/task/")
public class SafetyEnterTaskController {
    @Autowired
    private SafetyEnterFilingHandler enterFilingHandler;

    @ApiOperation(value = "定时任务-入场备案流程超时状态更新")
    @RequestMapping(value = "updateEnterTimeOut", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateEnterTimeOut() {
        enterFilingHandler.updateEnterTimeOut();
        return "T";
    }

    @ApiOperation(value = "定时任务-入场备案设备权限更新")
    @RequestMapping(value = "updateEnterOwer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateEnterOwer(String id,String carNo,String dockNo,String date,Boolean isTask) {
        enterFilingHandler.updateEnterOwer(id,carNo,dockNo,date,isTask);
        return "T";
    }

}
