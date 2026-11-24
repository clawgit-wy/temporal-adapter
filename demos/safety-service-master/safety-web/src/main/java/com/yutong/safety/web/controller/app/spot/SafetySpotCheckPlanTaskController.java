package com.yutong.safety.web.controller.app.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.SpotTaskListAppParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRecordListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetySpotCheckPlanHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckRecordHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 设备设施点击-任务设置管理
 *
 * @author gwxiexj
 * @since 2024-05-07 15:34
 */
@Api(tags = "设备设施点检-点检任务app相关请求")
@RestController
@RequestMapping("/app/safety/spotCheck/plan")
public class SafetySpotCheckPlanTaskController {

    @Autowired
    private SafetySpotCheckPlanHandler safetySpotCheckPlanHandler;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    @Autowired
    private SafetySpotCheckRecordHandler safetySpotCheckRecordHandler;

    /**
     * 定时生成点检任务 建议0点以前执行
     * @return 返回值
     */
    @ApiOperation(value = "定时任务-生成周期点检任务")
    @RequestMapping("/taskSpotCheck")
    public Object taskSpotCheck() {
        try {
            safetySpotCheckPlanHandler.taskSpotCheck();
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("生成点检任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"生成点检任务异常");
        }
        return null;
    }

    /**
     * 定时生成点检任务 建议0点以前执行
     * @return 返回值
     */
    @ApiOperation(value = "定时任务-生成周期点检任务新逻辑")
    @RequestMapping("/taskSpotCheckNew")
    public Object taskSpotCheckNew() {
        try {
            safetySpotCheckPlanHandler.taskSpotCheckNew();
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("生成点检任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"生成点检任务异常");
        }
        return null;
    }

    /**
     * 定时标记延期未点检任务 建议凌晨上班前执行
     * @return
     */
    @ApiOperation(value = "定时任务-标记延期未点检任务")
    @RequestMapping("/taskInitDelaySpot")
    public Object taskInitDelaySpot(String taskEnd){
        try {
            safetySpotCheckPlanHandler.taskInitDelaySpot(taskEnd);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("生成点检任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"生成点检任务异常");
        }
        return null;
    }

    /**
     * 定时任务-取消临时点检人
     * @return
     */
    @ApiOperation(value = "定时任务-取消临时点检人")
    @RequestMapping("/taskCancelTempCheck")
    public Object taskCancelTempCheck(){
        String taskEnd = DateUtils.format(new Date(),1);
        return safetySpotCheckPlanHandler.taskCancelTempCheck(taskEnd);
    }

    /**
     * 获取点检进度列表
     * @return 返回值
     */
    @ApiOperation(value = "获取点检进度列表")
    @RequestMapping("/selectSpotToDo")
    public Object selectSpotToDo(SpotTaskListAppParam param){
        try {
            return safetySpotCheckTaskHandler.selectSpotToDo(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("获取点检进度列表异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"获取点检进度列表异常");
        }
    }

    /**
     * 设备设施点检-获取设备设施点检详情
     * @param param 入参 teamId,planCheckEmpNo,planId,taskBeginDate,taskEndDate
     * @return 返回值
     */
    @ApiOperation(value = "获取设备设施点检详情")
    @RequestMapping("/getTaskDetails")
    public Object getTaskDetails(SpotCheckRecordListParam param) {
        try {
            return safetySpotCheckRecordHandler.getTaskDetails(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("获取设备设施点检详情异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"获取设备设施点检详情异常");
        }
    }

    /**
     * 设备设施点检-获取设备设施点检详情
     * @param param 入参 deviceStatus,validateStatus,planCheckEmpNo
     * @return 返回值
     */
    @ApiOperation(value = "获取设备点检记录")
    @RequestMapping("/getRecordList")
    public Object getRecordList(SpotCheckRecordListParam param) {
        try {
            String endDate = param.getSpotEndDate();
            return safetySpotCheckRecordHandler.getRecordList(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("获取设备点检记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"获取设备点检记录异常");
        }
    }

    /**
     * 设备设施点检-获取设备设施点检详情
     * @param param 入参 传入 deviceId,planEmpNo
     * @return 返回值
     */
    @ApiOperation(value = "获取设备是否已点检")
    @RequestMapping("/checkDevicePower")
    public Object checkDevicePower(SpotCheckRecordListParam param) {
        String errMsg = "";
        try {
            return safetySpotCheckRecordHandler.checkDevicePower(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            if(StringUtils.isEmpty(errMsg)){
                errMsg = "校验设备异常，请联系管理员";
            }
            LogUtils.error(errMsg,e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),errMsg);
        }
    }

    /**
     * 设备设施点检-获取设备设施点检详情
     * @param workNo 入参 员工号
     * @return 返回值
     */
    @ApiOperation(value = "01员工号转成6开头的")
    @RequestMapping("/employeeConvertWorkNo")
    public Object  employeeConvertWorkNo(String workNo){
        //01开头的员工号转为60开头的新员工号
        String newWorkNo = workNo;
        try{
            if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
                newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
            }
            return newWorkNo;
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("获取设备点检记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"员工号转换失败！");
        }
    }

    // public Object send

    /**
     * 定时将未完成的检修流程和最新的task任务关联起来  建议凌晨上班前执行,放置其他定时任务后
     * @return 返回值
     */
    @ApiOperation(value = "定时任务-定时将未完成的检修流程和最新的task任务关联起来")
    @RequestMapping("/taskRelaProcessToNewTask")
    public Object taskRelaProcessToNewTask() {
        try {
            safetySpotCheckPlanHandler.taskRelaProcessToNewTask();
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("定时将未完成的检修流程和最新的task任务关联异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"定时将未完成的检修流程和最新的task任务关联异常");
        }
        return null;
    }
}
