package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.service.SafetyTaskService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lkp
 * @time 2023-10-23
 */
@Api(tags = "定时任务接口")
@RestController
@RequestMapping("/app/faceTask")
public class SafetyFaceTaskController {

    @Resource
    private SafetyTaskService safetyTaskService;

    /**
     * 注销人脸 同时注销 卡 车辆 权限定时任务 每天晚上 00:10:00 06:00:00 各执行一次
     * @author lkp
     * @time 2023-10-23
     * @return
     */
    @RequestMapping("/faceUnregister")
    public Object faceUnregister(){
        return safetyTaskService.faceUnregister("");
    }

    /**
     * 注销人脸 同时注销 卡 车辆 权限定时任务 每天晚上 00:30:00 执行一次
     * @author lkp
     * @time 2023-10-23
     * @return
     */
    @RequestMapping("/faceUnregisterHand")
    public Object faceUnregisterHand(String dateStr){
        return safetyTaskService.faceUnregister(dateStr);
    }
}
