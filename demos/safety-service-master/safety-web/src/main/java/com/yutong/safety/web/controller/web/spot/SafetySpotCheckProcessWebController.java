package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.core.dto.SafetySpotCheckProcessDTO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lukunpeng
 * @time 2024-5-14
 */
@Api(tags = "设备设施点检-流程相关-web端")
@RestController
@RequestMapping("/web/safety/spot/process")
public class SafetySpotCheckProcessWebController {

    @Autowired
    SafetySpotCheckProcessHandler safetySpotCheckProcessHandler;

    /**
     * 扫码查询设备详情
     * @param deviceId
     * @return
     */
    @ApiOperation("查询单个")
    @RequestMapping("/findForAdd")
    public Object findForAdd(String deviceId, String loginName){
        return safetySpotCheckProcessHandler.findForAdd(deviceId, loginName);
    }

    /**
     * 创建流程
     * @return
     */
    @ApiOperation("创建流程")
    @RequestMapping("/createFlow")
    public Object createFlow(SafetySpotCheckProcessDTO processDTO){
        return safetySpotCheckProcessHandler.createFlow(processDTO);
    }
    /**
     * 查看流程
     * @return
     */
    @ApiOperation("查看流程")
    @RequestMapping("/view")
    public Object view(String flowId){
        return safetySpotCheckProcessHandler.view(flowId);
    }

    /**
     * 审批流程
     * @return
     */
    @ApiOperation("审批流程")
    @RequestMapping("/audit")
    public Object audit(SafetySpotCheckProcessDTO processDTO){
        return safetySpotCheckProcessHandler.audit(processDTO);
    }
    /**
     * 延迟整改--更新计划整改完成时间
     * @return
     */
    @ApiOperation("延迟整改")
    @RequestMapping("/delay")
    public Object delay(String flowId, String delayTime, String leaderUsername){
        return safetySpotCheckProcessHandler.delay(flowId, delayTime, leaderUsername);
    }
}
