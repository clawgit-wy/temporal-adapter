package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutEventProcessDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutProcessAddParam;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEventProcessDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutEventsProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "物资出门证WEB-异常上报流程管理")
@RestController
@RequestMapping("/web/safety/goodsOut/eventsProcess/process")
public class SafetyGoodsOutEventsProcessController {
    @Autowired
    SafetyGoodsOutEventsProcessHandler safetyGoodsOutEventsProcessHandler;

    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveAndCreateProcess(SafetyGoodsOutEventProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(entity.getReporterEip()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“reporterEip”不能为空");
        }
        if(StringUtils.isBlank(entity.getReporterEip())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            entity.setReporterEip(userName);
        }
        try {
            safetyGoodsOutEventsProcessHandler.saveAndCreateProcess(entity,token);
            return null;
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建出门证异常上报流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "审批流程")
    @RequestMapping(value = "/approvalProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object approvalProcess(@RequestBody SafetyGoodsOutEventProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(entity.getRemediationEip()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"整改负责人“remediationEip”不能为空");
        }
        if(StringUtils.isBlank(entity.getRemediationEip())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            entity.setRemediationEip(userName);
        }
        try {
            return safetyGoodsOutEventsProcessHandler.approvalProcess(entity,token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批出门证异常上报流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "查询流程详情")
    @RequestMapping(value = "/getProcessDetails", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessDetails(String id) {
        if(StringUtils.isBlank(id)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"“id”不能为空");
        }
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutEventsProcessHandler.getProcessDetails(id));
    }
}
