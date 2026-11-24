package com.yutong.safety.web.controller.app.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyInterfaceLogHandler;
import com.yutong.safety.core.handler.build.*;
import com.yutong.safety.core.handler.common.SafetyPowerIssueHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(tags = "楼宇门禁-定时任务管理")
@RestController
@RequestMapping("/app/build/safety/buildJob")
public class SafetyBuildJobController {

    @Autowired
    private SafetyBuildDeviceHandler safetyBuildDeviceHandler;

    @Autowired
    private SafetyBuildAreaHandler safetyBuildAreaHandler;

    @Autowired
    private SafetyBuildAutoDeptHandler safetyBuildAutoDeptHandler;

    @Autowired
    private SafetyBuildInOutHistoryHandler safetyBuildInOutHistoryHandler;

    @Autowired
    private SafetyBuildLongConfirmHandler safetyBuildLongConfirmHandler;

    @Autowired
    private SafetyBuildDepartConfirmHandler safetyBuildDepartConfirmHandler;

    @Autowired
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Autowired
    private SafetyBuildEmployeePowerHandler safetyBuildEmployeePowerHandler;

    @Resource
    private SafetyPowerIssueHandler safetyPowerIssueHandler;

    @Resource
    private SafetyInterfaceLogHandler safetyInterfaceLogHandler;

    /**
     * 初始化员工的楼宇门禁权限
     * @param empNo 员工号可为空
     */
    @ApiOperation(value = "初始化员工的楼宇门禁权限-数据初始化")
    @RequestMapping(value = "/initEmployeePower",method = {RequestMethod.GET,RequestMethod.POST})
    public Object initEmployeePower(String empNo){
        safetyBuildDeviceHandler.initEmployeePower(empNo);
        //清楚终端没有的权限信息
        safetyBuildDeviceHandler.clearBuildPowerXKP("");
        return null;
    }

    /**
     * 初始化员工的楼宇门禁权限
     * @param empNo 员工号
     */
    @ApiOperation(value = "初始化员工的责任部门")
    @RequestMapping(value = "/initEmpDutyDept",method = {RequestMethod.GET,RequestMethod.POST})
    public Object initEmpDutyDept(String empNo){
        return safetyBuildDeviceHandler.initEmpDutyDept(empNo);
    }


    @ApiOperation(value = "定时更新门禁设备-每天一次")
    @RequestMapping(value = "/updateDeviceTask",method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateDeviceTask(String areaId) {
        int i;
        try{
            safetyBuildAreaHandler.updateAreaTask();
            i = safetyBuildDeviceHandler.updateDeviceTask(areaId);
            //移除 DEVICE_STATUS = 0  或  REMOVE_FLAG = 1的门禁 对应的权限
            safetyBuildDeviceHandler.removeDevicePower();
            //更新设备后，有新增的设备时给保卫主管角色 宇信通消息 [安保管理平台]请维护新增楼宇门禁基础信息
            safetyBuildDeviceHandler.sendMessageForNewDevice();
            return 0;
        }catch (Exception e){
            LogUtils.error("定时更新门禁设备-每天一次异常",e);
            return 0;
        }
    }

    /**
     * 根据组织自动授权更新权限
     * @return 返回值
     */
    @ApiOperation(value = "根据组织自动授权更新权限")
    @RequestMapping(value = "/updatePowerTask",method = {RequestMethod.GET,RequestMethod.POST})
    public Object updatePowerTask(String beginDate){
        if(StringUtils.isBlank(beginDate)){
            beginDate = DateUtils.format(DateUtils.addDays(new Date(),-1),1);
        }
        try{
            safetyBuildAutoDeptHandler.updatePowerByDate(beginDate);
        }catch (Exception e){
            LogUtils.error("根据组织自动授权更新权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"根据组织自动授权更新权限异常");
        }
        return null;
    }

    @ApiOperation(value = "更新门禁出入记录-保留最近一条记录")
    @RequestMapping(value = "/modifyRecordsTask",method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyRecordsTask(){
        try{
            safetyBuildInOutHistoryHandler.updateRecordTask();
        }catch (Exception e){
            LogUtils.error("更新门禁出入记录异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"更新门禁出入记录异常");
        }
        return null;
    }

    @ApiOperation(value = "更新长期未进出门禁信息，在上面定时任务之后")
    @RequestMapping(value = "/initBuildLongTerm",method = {RequestMethod.GET,RequestMethod.POST})
    public Object initBuildLongTerm(){
        try{
            safetyBuildLongConfirmHandler.initBuildLongTermV2();
        }catch (Exception e){
            LogUtils.error("更新长期未进出门禁信息异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"更新长期未进出门禁信息异常");
        }
        return null;
    }

    @ApiOperation(value = "定时发送组织变更的代办-早9点半")
    @RequestMapping(value = "/sendDepartToDo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object sendDepartToDo(String isAll){
        try{
            safetyBuildDepartConfirmHandler.sendDepartToDo(isAll);
        }catch (Exception e){
            LogUtils.error("定时发送组织变更的代办异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"定时发送组织变更的代办异常");
        }
        return null;
    }

    @ApiOperation(value = "自动确认部门变更门禁权限")
    @RequestMapping(value = "/autoConfirmChangePower",method = {RequestMethod.GET,RequestMethod.POST})
    public Object autoConfirmChangePower(){
        try{
            safetyBuildDepartConfirmHandler.autoConfirmPower();
        }catch (Exception e){
            LogUtils.error("自动确认部门变更门禁权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"自动确认部门变更门禁权限异常");
        }
        return null;
    }

    @ApiOperation(value = "自动确认调岗原对接门禁管理员变更门禁权限")
    @RequestMapping(value = "/autoDepartConfirmPower",method = {RequestMethod.GET,RequestMethod.POST})
    public Object autoDepartConfirmPower(){
        try{
            safetyBuildDepartConfirmHandler.autoDepartConfirmPower();
        }catch (Exception e){
            LogUtils.error("自动确认调岗原对接门禁管理员变更门禁权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"自动确认调岗原对接门禁管理员变更门禁权限异常");
        }
        return null;
    }

    @ApiOperation(value = "定时发送长期未进出的代办-个人-早9点10分")
    @RequestMapping(value = "/sendOwnerLongToDo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object sendOwnerLongToDo(){
        try{
            safetyBuildLongConfirmHandler.sendOwnerLongToDo();
        }catch (Exception e){
            LogUtils.error("定时发送长期未进出的代办异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"定时发送长期未进出的代办异常");
        }
        return null;
    }

    @ApiOperation(value = "定时下发和注销厂区门禁")
    @RequestMapping(value = "/autoChangeFactoryAreaPower",method = {RequestMethod.GET,RequestMethod.POST})
    public Object autoChangeFactoryAreaPower(){
        try {
            safetyParkPowerProcessHandler.autoChangeFactoryAreaPower();
        }catch (Exception e) {
            LogUtils.error("定时下发和注销厂区门禁异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"定时下发和注销厂区门禁异常");
        }
        return null;
    }

    /**
     * 富士（出入口）门禁设备
     * 一次性接口
     */
    @ApiOperation(value = "富士（出入口）门禁权限初始化")
    @RequestMapping(value = "/initCrkBuildPower",method = {RequestMethod.GET,RequestMethod.POST})
    public void initCrkBuildPower(String empNos){
        try{
            safetyBuildEmployeePowerHandler.initCrkBuildPower(empNos);
        }catch (BizException e){
            throw e;
        }catch (Exception e){
            LogUtils.error("初始化富士门禁权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"初始化富士门禁权限异常");
        }
    }

    @ApiOperation(value = "测试-初始化指定员工的组织自动授权逻辑")
    @RequestMapping(value = "/testInitAutoBuild",method = {RequestMethod.GET,RequestMethod.POST})
    public void testInitAutoBuild(String empNoStr){
        try{
            if(StringUtils.isBlank(empNoStr)){
                return;
            }
            List<String> empNos = Arrays.asList(empNoStr.split(";"));
            safetyBuildAutoDeptHandler.initAutoBuildForNewEmp(empNos);
        }catch (Exception e){
            LogUtils.error("员工组织自动授权逻辑异常",e);
        }
    }

    @ApiOperation(value = "定时关闭租赁到期的车辆权限")
    @RequestMapping(value = "/autoCloseCarPower",method = {RequestMethod.GET,RequestMethod.POST})
    public void autoCloseCarPower(String carNos){
        try{
            safetyParkPowerProcessHandler.autoCloseCarPower(carNos);
        }catch (Exception e){
            LogUtils.error("定时关闭租赁到期的车辆权限异常",e);
        }
    }

    @ApiOperation(value = "员工超7天不确认长期未进出门禁时，自动消除待办和取消待确认的门禁")
    @RequestMapping(value = "/closeOwnerLongToDo", method = {RequestMethod.GET, RequestMethod.POST})
    public Object closeOwnerLongToDo() {
        try {
            safetyBuildLongConfirmHandler.closeOwnerLongToDo();
        } catch (Exception e) {
            LogUtils.error("自动消除待办和取消待确认的门禁异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "自动消除待办和取消待确认的门禁异常");
        }
        return null;
    }

    @ApiOperation(value = "大门规则权限下发")
    @RequestMapping(value = "/issueDoorRulePower", method = {RequestMethod.GET, RequestMethod.POST})
    public VO issueDoorRulePower(Integer beginNum, Integer operationNum) {
        return safetyPowerIssueHandler.issueDoorRulePower(beginNum, operationNum);
    }

    @ApiOperation(value = "日志表定时备份")
    @RequestMapping(value = "/backupInterfaceLog", method = {RequestMethod.POST})
    public VO backupInterfaceLog() {
        return safetyInterfaceLogHandler.backupInterfaceLog();
    }

    @ApiOperation(value = "消除长期未进出的代办")
    @RequestMapping(value = "/delTodo", method = {RequestMethod.POST})
    public VO delTodo(String workNos) {
        return safetyBuildLongConfirmHandler.delTodoOfWorkNo(workNos);
    }
}
