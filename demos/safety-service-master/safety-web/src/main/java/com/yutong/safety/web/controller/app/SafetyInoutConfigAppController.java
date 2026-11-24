package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.param.app.InoutConfigAppParam;
import com.yutong.safety.core.handler.SafetyInoutConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-4-9 9:41
 */
@Api(tags = "出入信息查询权限")
@RestController
@RequestMapping("/app/safety/inoutConfig")
public class SafetyInoutConfigAppController {

    @Autowired
    private SafetyInoutConfigHandler safetyInoutConfigHandler;

    @ApiOperation(value = "获取员工权限")
    @RequestMapping("/getPermission")
    public Object getPermission(InoutConfigAppParam param) {
        return safetyInoutConfigHandler.getPermission(param.getEmpCode());
    }
}
