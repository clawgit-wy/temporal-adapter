package com.yutong.safety.web.controller.app.hik;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.hik.SafetyHikGroupHandler;
import com.yutong.safety.core.handler.hik.SafetyHikMonitorHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "海康-定时任务")
@RestController
@RequestMapping("/app/safety/hik/job")
public class SafetyHikJobController {

    @Resource
    private SafetyHikGroupHandler safetyHikGroupHandler;

    @Resource
    private SafetyHikMonitorHandler safetyHikMonitorHandler;

    @ApiOperation(value = "定时更新抓拍机信息")
    @RequestMapping(value = "/syncUpdateInfo", method = {RequestMethod.POST})
    public VO syncUpdateInfo(String type) {
        return safetyHikMonitorHandler.syncUpdateInfo(type, "定时任务");
    }

    @ApiOperation(value = "定时更新分组数据")
    @RequestMapping(value = "/syncUpdateGroupInfo", method = {RequestMethod.POST})
    public VO syncUpdateGroupInfo() {
        return safetyHikGroupHandler.syncUpdateInfo("定时任务");
    }

    @ApiOperation(value = "批量创建分组数据")
    @RequestMapping(value = "/batchCreateGroupInfo", method = {RequestMethod.POST})
    public VO batchCreateGroupInfo() {
        return safetyHikGroupHandler.batchCreateGroupInfo("定时任务");
    }

    @ApiOperation(value = "定时更新在职人员的分组数据")
    @RequestMapping(value = "/saveOperationGroup", method = {RequestMethod.POST})
    public VO saveOperationGroup() {
        return safetyHikGroupHandler.saveOperationGroup();
    }

    @ApiOperation(value = "外来人员定时变更人脸分组-30分钟一次")
    @RequestMapping(value = "/updateHikGroupFaces", method = {RequestMethod.POST})
    public VO updateHikGroupFaces() {
        return safetyHikGroupHandler.updateHikGroupFaces();
    }
}
