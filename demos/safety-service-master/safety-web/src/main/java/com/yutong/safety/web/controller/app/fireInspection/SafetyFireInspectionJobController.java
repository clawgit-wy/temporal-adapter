package com.yutong.safety.web.controller.app.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskInfoHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "消防巡检-定时任务管理")
@RestController
@RequestMapping("/app/fireInspection/safety/job")
public class SafetyFireInspectionJobController {

    @Resource
    private SafetyFireInspectionTaskInfoHandler safetyFireInspectionTaskInfoHandler;

    /**
     * 定时任务在当天8点前执行即可， 只有周一和周四会触发
     */
    @ApiOperation(value = "定时下发巡检任务")
    @RequestMapping(value = "/autoInspectionTask", method = {RequestMethod.GET, RequestMethod.POST})
    public Object autoInspectionTask() {
        try {
            safetyFireInspectionTaskInfoHandler.autoInspectionTask();
        } catch (Exception e) {
            LogUtils.error("定时下发巡检任务异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "定时下发巡检任务异常");
        }
        return null;
    }
}
