package com.yutong.safety.web.controller.web;

import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lkp
 * @date 20240402
 */
@Api(tags = "获取人员岗位")
@RestController
@RequestMapping("/web/safety/empPost")
public class SafetyEmpPostController {
    @Autowired
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;
    /**
     * 数据添加接口
     * @return
     */
    @ApiOperation(value = "获取人员岗位")
    @RequestMapping("/getPost")
    public Object getPost() {
        return safetyEmployeeMessageHandler.getPost();
    }
}
