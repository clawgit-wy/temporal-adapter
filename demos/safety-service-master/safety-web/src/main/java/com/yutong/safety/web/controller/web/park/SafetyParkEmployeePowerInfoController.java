package com.yutong.safety.web.controller.web.park;

import com.yutong.safety.core.handler.park.SafetyParkEmployeePowerInfoHanler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "员工车辆厂区权限明细")
@RestController
@RequestMapping("/web/park/safety/parkEmployeePower")
public class SafetyParkEmployeePowerInfoController {
    @Autowired
    SafetyParkEmployeePowerInfoHanler safetyParkEmployeePowerInfoHanler;

    @ApiOperation(value = "根据id查询明细")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.POST})
    public Object getDetailById(String userCode){
        return safetyParkEmployeePowerInfoHanler.getDetailById(userCode);
    }

}
