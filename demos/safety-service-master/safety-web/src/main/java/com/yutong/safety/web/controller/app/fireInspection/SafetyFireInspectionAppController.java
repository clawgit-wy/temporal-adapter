package com.yutong.safety.web.controller.app.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionEipProcessParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailQueryParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionStandardHandler;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskInfoDetailHandler;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskInfoHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "消防巡检-APP接口")
@RestController
@RequestMapping("/app/fireInspection/safety/taskInfo")
public class SafetyFireInspectionAppController {

    @Resource
    private SafetyFireInspectionTaskInfoHandler safetyFireInspectionTaskInfoHandler;
    @Resource
    private SafetyFireInspectionStandardHandler safetyFireInspectionStandardHandler;

    @Resource
    private SafetyFireInspectionTaskInfoDetailHandler safetyFireInspectionTaskInfoDetailHandler;

    @ApiOperation(value = "掌宇-任务统计")
    @RequestMapping(value = "/getTaskNum", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getTaskNum(SafetyFireInspectionTaskInfoQueryParams param) {
        return safetyFireInspectionTaskInfoHandler.getTaskNum(param);
    }

    @ApiOperation(value = "掌宇-任务列表")
    @RequestMapping(value = "/getList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getList(SafetyFireInspectionTaskInfoQueryParams param) {
        return safetyFireInspectionTaskInfoHandler.getList(param);
    }

    @ApiOperation(value = "掌宇-任务详情")
    @RequestMapping(value = "/getDetailList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getList(SafetyFireInspectionTaskInfoDetailQueryParams param) {
        return safetyFireInspectionTaskInfoDetailHandler.getDetailList(param);
    }

    @ApiOperation(value = "掌宇-任务完成提交")
    @RequestMapping(value = "/submitTaskInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Object submitTaskInfo(@RequestBody SafetyFireInspectionTaskInfoDetailAddParams param) {
        return safetyFireInspectionTaskInfoDetailHandler.submitTaskInfo(param);
    }

    @ApiOperation(value = "EIP-流程整改结果回写")
    @RequestMapping(value = "/eipProcessStatusWriteBack", method = {RequestMethod.POST})
    public Object eipProcessStatusWriteBack(@RequestBody List<SafetyFireInspectionEipProcessParams> processParams) {
        return safetyFireInspectionTaskInfoDetailHandler.eipProcessStatusWriteBack(processParams);
    }

    @ApiOperation(value = "检查标准导入")
    @RequestMapping(value = "/importData", method = {RequestMethod.POST})
    public Object importData(MultipartFile file) {
        return safetyFireInspectionStandardHandler.importData(file);
    }
}
