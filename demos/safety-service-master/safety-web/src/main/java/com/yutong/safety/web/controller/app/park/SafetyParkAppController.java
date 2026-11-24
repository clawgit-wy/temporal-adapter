package com.yutong.safety.web.controller.app.park;


import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import com.yutong.safety.core.handler.park.SafetyParkEmployeePowerInfoHanler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "园区权限-APP接口类")
@RestController
@RequestMapping("/app/park/safety/parkApp")
public class SafetyParkAppController {

    @Resource
    private SafetyParkEmployeePowerInfoHanler safetyParkEmployeePowerInfoHanler;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Resource
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    /**
     * 获取员工的车行设备
     * @return
     */
    @RequestMapping(value = "/getEmpCarDevicesForEip",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmpCarDevicesForEip(String empNo){
        if(StringUtils.isBlank(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        if(empNo.startsWith("01")){
            empNo = safetyBaseHandler.employeeConvertWorkNo(empNo);
        }
        return safetyParkEmployeePowerInfoHanler.getEmpCarDevices(empNo);
    }

    /**
     * 测试回调方法
     * @param bpmFlowId
     * @param nodeId
     * @return
     */
    @RequestMapping(value = "/bpmCallBackProcess",method = {RequestMethod.GET,RequestMethod.POST})
    public Object bpmCallBackProcess(String bpmFlowId,String nodeId){
        if(StringUtils.isBlank(bpmFlowId) || StringUtils.isBlank(nodeId)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"流程ID和节点都不能为空");
        }
        String processData= "{\"nodeFactId\":\"N10\"}";
        safetyParkPowerProcessHandler.bpmCallBackProcess(bpmFlowId,processData);

        return null;
    }

    @ApiOperation("获取员工明细-校验大门权限")
    @RequestMapping(value = "/checkEmpParkPower",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmployeeInfo(String empNo){
        if(com.yutong.gw.utils.StringUtils.isEmpty(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        SafetyEmployeeMessageDO messageDO = safetyEmployeeMessageHandler.getEmployeeByWorkNo(empNo,"");
        if(messageDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"暂未查询到您的厂区权限，若您为新入职员工，可于次日再进行办理");
        }
        boolean checkFlag = safetyParkEmployeePowerInfoHanler.checkEmployeeForPark(empNo);
        if(!checkFlag){
            throw new BizException(ResponseEnum.FAIL.getCode(),"暂未查询到您的厂区权限，若您为新入职员工，可于次日再进行办理");
        }
        return messageDO;
    }
}
