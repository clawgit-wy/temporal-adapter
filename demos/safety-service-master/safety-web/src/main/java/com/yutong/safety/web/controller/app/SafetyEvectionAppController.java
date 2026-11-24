package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.param.app.PersonStateAddParam;
import com.yutong.safety.core.handler.SafetyStaffEvectionHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-10-10 16:13
 */
@Api(tags = "公休信息")
@RestController
@RequestMapping("/app/safety/staffEvection")
public class SafetyEvectionAppController {

    @Autowired
    private SafetyStaffEvectionHandler safetyStaffEvectionHandler;

    @RequestMapping("/addList")
    public Object addList(@RequestBody List<PersonStateAddParam> list) {
        return safetyStaffEvectionHandler.addList(list);
    }
}
