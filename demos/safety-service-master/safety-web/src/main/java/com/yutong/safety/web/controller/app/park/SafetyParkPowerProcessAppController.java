package com.yutong.safety.web.controller.app.park;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.dto.park.SafetyParkPowerProcessDTO;
import com.yutong.safety.core.handler.park.SafetyParkEmployeePowerInfoHanler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "人车权限调整流程APP")
@RestController
@RequestMapping("/app/park/safety/parkPowerProcessApp")
public class SafetyParkPowerProcessAppController {
    @Autowired
    SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Autowired
    SafetyParkEmployeePowerInfoHanler safetyParkEmployeePowerInfoHanler;

    @ApiOperation(value = "根据userCode查询当前授权厂区")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.POST})
    public Object getDetailById(String userCode){
        return safetyParkEmployeePowerInfoHanler.getDetailById(userCode);
    }


    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody SafetyParkPowerProcessDTO entity) {
        if(StringUtils.isBlank(entity.getCreateEmployeeNo())){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“CreateEmployeeNo”不能为空");
        }
        try {
            safetyParkPowerProcessHandler.saveAndCreateProcessApp(entity);
            return null;
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("APP端创建人车大门权限调整流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "测试下发功能")
    @RequestMapping(value = "/testSendToCrk", method = {RequestMethod.POST})
    public Object testSendToCrk(String bpmFlowId){
        safetyParkPowerProcessHandler.testSendToCrk(bpmFlowId);
        return null;
    }

}
