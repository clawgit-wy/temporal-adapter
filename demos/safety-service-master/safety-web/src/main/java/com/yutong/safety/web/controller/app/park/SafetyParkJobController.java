package com.yutong.safety.web.controller.app.park;

import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.park.SafetyParkEmployeeClassifiedHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "园区权限-定时任务管理")
@RestController
@RequestMapping("/app/park/safety/parkJob")
public class SafetyParkJobController {

    @Resource
    private SafetyParkPowerRuleHandler safetyParkPowerRuleHandler;

    @Resource
    private SafetyParkEmployeeClassifiedHandler safetyParkEmployeeClassifiedHandler;

    @ApiOperation(value = "人员默认权限调整-根据人员变更记录")
    @RequestMapping(value = "/doTaskByChange",method = {RequestMethod.GET,RequestMethod.POST})
    public void doTaskByChange(String begeinTime){
        try{
            safetyParkPowerRuleHandler.doTaskOnlyEmployee(begeinTime);
        }catch (Exception e){
            LogUtils.error("人员默认权限调整-根据人员变更记录异常",e);
        }
    }

    @ApiOperation(value = "根据人员厂区权限同步车权限")
    @RequestMapping(value = "/syncCarParkPower",method = {RequestMethod.GET,RequestMethod.POST})
    public void syncCarParkPower(String empNo){
        try{
            safetyParkPowerRuleHandler.syncCarParkPower(empNo);
        }catch (Exception e){
            LogUtils.error("根据人员厂区权限同步车权限异常",e);
        }
    }

    @ApiOperation(value = "定时生成-无涉密业务关联员工数据列表")
    @RequestMapping(value = "/autoGeneralClassifiedData",method = {RequestMethod.GET,RequestMethod.POST})
    public void autoGeneralClassifiedData(){
        try{
            safetyParkEmployeeClassifiedHandler.autoGeneralClassifiedData();
        }catch (Exception e){
            LogUtils.error("生成无涉密业务关联员工数据记录异常",e);
        }
    }
}
