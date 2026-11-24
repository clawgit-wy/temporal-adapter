package com.yutong.safety.web.controller.web.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskObjectQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionSelectHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "消防巡检-下拉框")
@RestController
@RequestMapping("/web/fireInspection/safety/select")
public class SafetyFireInspectionSelectController {

    @Resource
    private SafetyFireInspectionSelectHandler safetyFireInspectionSelectHandler;

    /**
     * 风险等级
     */
    @ApiOperation(value = "风险等级下拉框接口")
    @RequestMapping(value = "/getRiskLevel", method = {RequestMethod.GET})
    public Object getRiskLevel() {
        return safetyFireInspectionSelectHandler.getRiskLevel();
    }

    /**
     * 检查方法
     */
    @ApiOperation(value = "检查方法下拉框接口")
    @RequestMapping(value = "/getInspectionMethod", method = {RequestMethod.GET})
    public Object getInspectionMethod() {
        return safetyFireInspectionSelectHandler.getInspectionMethod();
    }

    /**
     * 作业环节
     */
    @ApiOperation(value = "作业环节下拉框接口")
    @RequestMapping(value = "/getWorkStep", method = {RequestMethod.GET})
    public Object getWorkStep() {
        return safetyFireInspectionSelectHandler.getWorkStep();
    }

    /**
     * 任务对象
     */
    @ApiOperation(value = "默认任务对象下拉框接口")
    @RequestMapping(value = "/getDefaultTaskObject", method = {RequestMethod.GET})
    public Object getDefaultTaskObject(SafetyFireInspectionTaskObjectQueryParams params) {
        return safetyFireInspectionSelectHandler.getDefaultTaskObject(params);
    }

    /**
     * 任务对象
     */
    @ApiOperation(value = "自定义任务对象下拉框接口")
    @RequestMapping(value = "/getTaskObject", method = {RequestMethod.GET})
    public Object getTaskObject(SafetyFireInspectionTaskObjectQueryParams params) {
        return safetyFireInspectionSelectHandler.getTaskObject(params);
    }
}
