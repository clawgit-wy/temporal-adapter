package com.yutong.safety.web.controller.web;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  公共web接口
 */
@Api(tags = "安保web端功能方法")
@RestController
@RequestMapping("/web/safety/base")
public class SafetyBaseWebController {

    @Resource
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    @ApiOperation("获取员工明细")
    @RequestMapping(value = "/getEmployeeInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmployeeInfo(String empNo, @RequestHeader("X-Access-Token") String tocken){
        if(StringUtils.isEmpty(empNo)){
            empNo = TokenUtils.getWorkNo(tocken);
        }
        if(StringUtils.isEmpty(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        SafetyEmployeeMessageDO messageDO = safetyEmployeeMessageHandler.getEmployeeByWorkNo(empNo,"");
        return messageDO;
    }
}
