package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import com.yutong.safety.core.handler.SafetyVisitorHairpinHandler;
import com.yutong.safety.core.handler.build.SafetyBuildWhiteListHandler;
import com.yutong.safety.core.handler.device.SafetyRegionDeviceInfoHandler;
import com.yutong.safety.core.handler.emp.SafetyDepartChangeListHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutExceptionRecordHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import com.yutong.safety.core.handler.spot.SafetyPatrolPlanHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/timingController")
public class SafetyAppTimingController {

    @Autowired
    private SafetyEnterFilingHandler enterFilingHandler;

    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;

    @Autowired
    private SafetyVisitorHairpinHandler safetyVisitorHairpinHandler;

    @Autowired
    private SafetyPatrolPlanHandler safetyPatrolPlanHandler;

    @Autowired
    SafetyGoodsOutExceptionRecordHandler safetyGoodsOutExceptionRecordHandler;

    @Autowired
    SafetyDepartChangeListHandler safetyDepartChangeListHandler;

    @Autowired
    SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Autowired
    SafetyBuildWhiteListHandler safetyBuildWhiteListHandler;

    @Autowired
    SafetyRegionDeviceInfoHandler safetyRegionDeviceInfoHandler;

    @ApiOperation(value = "定时任务-入场备案流程超时状态更新")
    @RequestMapping(value = "/updateEnterTimeOut", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateEnterTimeOut() {
        enterFilingHandler.updateEnterTimeOut();
        return "T";
    }

    @ApiOperation("定时下发EHR及门禁改写权限")
    @RequestMapping("/issueEHRTiming")
    public Object issueEHRTiming(){
        safetyOutsiderPromiseChangeHandler.issueEHRTiming();
        return null;
    }

    @RequestMapping("/permissionDelivery")
    @ApiOperation("访客发卡权限定时下发")
    public Object permissionDelivery() {
        safetyVisitorHairpinHandler.permissionDelivery();
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation(value = "定时生成岗位验证任务")
    @RequestMapping(value = "/taskPositionEdit", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskPositionEdit() {
        try {
            safetyPatrolPlanHandler.taskPositionEdit();
        }catch (Exception e){
            LogUtils.error("定时生成岗位验证任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"定时生成岗位验证任务异常");
        }
        return null;
    }

    @ApiOperation(value = "定时生成岗位验证通知")
    @RequestMapping(value = "/taskPositionMes", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskPositionMes() {
        try {
            safetyPatrolPlanHandler.taskPositionMes();
        }catch (Exception e){
            LogUtils.error("定时生成岗位验证通知异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"定时生成岗位验证通知异常");
        }
        return null;
    }

    @ApiOperation("定时任务-生成异常数据")
    @RequestMapping("/createExceptionData")
    public Object createExceptionData(){
        try {
            safetyGoodsOutExceptionRecordHandler.createExceptionData();
        }catch (Exception e){
            LogUtils.info("定时任务-生成异常数据异常，异常信息{}",e.getMessage());
        }
        return null;
    }

    @ApiOperation("定时任务-岗位变动发消息（门禁）")
    @RequestMapping("/sendMesWithDeptChange")
    public Object sendMesWithDeptChange(){
        try {
            safetyDepartChangeListHandler.sendMesWithDeptChange();
        }catch (Exception e){
            LogUtils.info("定时任务-岗位变动发消息（门禁）异常，异常信息{}",e.getMessage());
        }
        return null;
    }

    @ApiOperation("定时更新人车权限")
    @RequestMapping(value = "/updatePower", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updatePower(){
        safetyParkPowerProcessHandler.updatePower();
        return null;
    }

    @ApiOperation("定时更新人车权限-增加楼宇权限逻辑")
    @RequestMapping(value = "/updatePowerNew", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updatePowerNew(){
        safetyParkPowerProcessHandler.updatePowerNew();
        return null;
    }

    @ApiOperation("人员组织变更时同步删除出门证权限")
    @RequestMapping(value = "/deleteOutPower", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteOutPower(){
        safetyParkPowerProcessHandler.deleteOutPower();
        return null;
    }

    @ApiOperation("更新白名单")
    @RequestMapping(value = "/updateWhiteList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateWhiteList(){
        safetyBuildWhiteListHandler.updateWhiteList();
        return null;
    }


    @ApiOperation("从出入口取设备")
    @RequestMapping(value = "/updateDeviceFromCrk", method = {RequestMethod.POST})
    public Object updateDeviceFromCrk(){
        safetyRegionDeviceInfoHandler.save();
        return null;
    }

    @ApiOperation("初始化出入口设备表的设备类型")
    @RequestMapping(value = "/initRegionDeviceType", method = {RequestMethod.POST})
    public Object initRegionDeviceType() {
        safetyRegionDeviceInfoHandler.initRegionDeviceType();
        return null;
    }

    @ApiOperation("定时关闭前30分钟下发的访客权限")
    @RequestMapping(value = "/closeVisitorPower", method = {RequestMethod.POST})
    public VO closeVisitorPower() {
        return safetyVisitorHairpinHandler.closeVisitorPower();
    }
}
