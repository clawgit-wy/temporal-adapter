package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.EmployeeSetParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gwcuiyx
 * @date 2024-4-3 16:51
 */
@Api(tags = "员工信息台账定时任务接口")
@RestController
@RequestMapping("/app/employee")
public class SafetyEmployeeAppController {
    @Autowired
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    /**
     * 定时任务执行增量或全量数据
     * @param isAll 是否全量 true/false
     * @param dateTime 非全量时数据起始日期 yyyy-MM-dd
     * @return 返回执行状态
     */
    @ApiOperation(value = "同步员工信息")
    @RequestMapping(value = "/tasksMessage",method = {RequestMethod.GET,RequestMethod.POST})
    public Object tasksMessage(Boolean isAll, String dateTime) {
       try{
           return safetyEmployeeMessageHandler.tasksMessage(isAll, dateTime);
       }catch (BizException ex){
           throw new BizException(ex.getCode(),ex.getMsg());
       }catch (Exception e){
           LogUtils.error("同步员工台账异常",e);
           throw new BizException(ResponseEnum.EXCEPTION.getCode(),"同步员工台账异常"+e.getMessage());
       }
    }

    /**
     * 访客被访人设置接口
     * @param param 入参规则
     * @return 校验结果
     */
    @ApiOperation(value = "校验访客被访人设置规则逻辑")
    @RequestMapping("/checkEmployeeSet")
    public Object checkEmployeeSet(EmployeeSetParam param) {
        return safetyEmployeeMessageHandler.checkEmployeeSet(param);
    }

    /**
     * 根据员工号同步员工信息
     * @param workNo 员工号
     * @return 返回值
     */
    @RequestMapping("/tasksMessageByWorkNo")
    public Object tasksMessageByWorkNo(String workNo) {
        return safetyEmployeeMessageHandler.tasksMessageByWorkNo(workNo);
    }

    @ApiOperation(value = "根据用户node更新更新时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "empNo",value = "用户node",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "updateTime",value = "更新时间(yyyy-MM-dd HH:mm:ss)",dataType = "String",paramType = "query")
    })
    @RequestMapping("/updateTimeByEmpNo")
    public Object updateTimeByEmpNo(String empNo, String updateTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empNo", empNo);
        paramMap.put("updateTime", updateTime);
        return safetyEmployeeMessageHandler.updateTimeByEmpNo(paramMap);
    }

    /**
     *
     * @param workNo 员工号
     * @param phone 手机号
     * @return 员工信息
     */
    @ApiOperation(value = "根据员工号获取员工信息")
    @RequestMapping(value = "/getEmployeeByWorkNo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmployeeByWorkNo(String workNo,String phone){
        if(StringUtils.isBlank(workNo) && StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号或手机号不能为空");
        }
        return safetyEmployeeMessageHandler.getEmployeeByWorkNo(workNo,phone);
    }

}
