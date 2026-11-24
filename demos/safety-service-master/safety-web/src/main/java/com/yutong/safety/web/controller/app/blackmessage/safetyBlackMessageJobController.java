package com.yutong.safety.web.controller.app.blackmessage;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyBlackMessageHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "黑名单定时任务")
@RestController
@RequestMapping("/app/blackMessage/safety/job")
public class safetyBlackMessageJobController {

    @Resource
    private SafetyBlackMessageHandler safetyBlackMessageHandler;

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Deprecated
    @ApiOperation(value = "黑名单、组织、员工台账、外来人员数据增量同步至中间库")
    @RequestMapping(value = "/syncToHkAdd", method = {RequestMethod.POST})
    public VO syncToHkAdd() {
        return safetyBlackMessageHandler.syncToHkAdd();
    }

    @ApiOperation(value = "定时变更海康分组")
    @RequestMapping(value = "/updateHkiGroup", method = {RequestMethod.POST})
    public VO updateHkiGroup() {
        return safetyArtemisHandler.updateHkiGroup();
    }

    @ApiOperation(value = "定时变更状态")
    @RequestMapping(value = "/updateBlackMessageStatus", method = {RequestMethod.POST})
    public VO updateBlackMessageStatus() {
        return safetyBlackMessageHandler.updateBlackMessageStatus();
    }

}
