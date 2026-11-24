package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.param.app.FindEmpParam;
import com.yutong.safety.core.handler.SafetyExternalDetectionHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-2-28 17:16
 */
@RestController
@RequestMapping("/app/safety/external")
public class SafetyExternalAppController {

    @Autowired
    private SafetyExternalDetectionHandler safetyExternalDetectionHandler;

    @ApiOperation(value = "校验用户白名单信息，返回ldap用户信息")
    @RequestMapping("/guest/findEmp")
    public Object findEmp(FindEmpParam findEmpParam) {
        return safetyExternalDetectionHandler.findEmpNew(findEmpParam);
    }

}
