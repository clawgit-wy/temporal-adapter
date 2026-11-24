package com.yutong.safety.web.controller.app.hot;

import com.yutong.safety.core.db.entity.hot.param.SafetyHotWorkLensParam;
import com.yutong.safety.core.db.entity.hot.param.SafetyMonitorVideoParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveListParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveParam;
import com.yutong.safety.core.handler.hot.SafetyHotWorkHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "动火作业")
@RestController
@RequestMapping("/app/safety/hot/work")
public class SafetyHotWorkAppController {

    @Autowired
    private SafetyHotWorkHandler safetyHotWorkHandler;

    @ApiOperation("分页查询镜头信息")
    @RequestMapping(value = "/lensList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getLensList(SafetyHotWorkLensParam param){
        return safetyHotWorkHandler.getLensList(param);
    }

    @ApiOperation(value = "同步EIP单据到掌宇", notes = "供EIP调用，第一次新增，后续状态变更则修改掌宇单据状态")
    @RequestMapping(value ="/sync", method = {RequestMethod.GET,RequestMethod.POST})
    public Object sync(SyncFireApproveParam fireApproveParam) {
        return safetyHotWorkHandler.sync(fireApproveParam);
    }

    @ApiOperation(value = "查询动火作业列表信息")
    @RequestMapping(value ="/getFireApproveList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getFireApproveList(SyncFireApproveListParam fireApproveListParam){
        return safetyHotWorkHandler.getFireApproveList(fireApproveListParam);
    }

    @ApiOperation(value = "查询监控设备视频链接")
    @RequestMapping(value ="/getMonitorVideoUrl", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getMonitorVideoUrl(SafetyMonitorVideoParam param){
        return safetyHotWorkHandler.getMonitorVideoUrl(param);
    }

    @ApiOperation("查询镜头状态信息")
    @RequestMapping(value = "/getLensStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getLensStatus(SafetyHotWorkLensParam param){
        return safetyHotWorkHandler.getLensStatus(param);
    }

}
