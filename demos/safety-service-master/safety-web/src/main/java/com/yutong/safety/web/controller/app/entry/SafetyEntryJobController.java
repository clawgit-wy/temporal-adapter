package com.yutong.safety.web.controller.app.entry;


import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyBlackMessageHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryBlankProcessDetailHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryCompanyRegisterHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryConfigHandler;
import com.yutong.safety.core.handler.entry.SafetyPermissionDelegationHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "外来人员入厂备案-定时任务管理")
@RestController
@RequestMapping("/app/entry/safety/job")
public class SafetyEntryJobController {
    @Resource
    private SafetyEntryCompanyRegisterHandler safetyEntryCompanyRegisterHandler;

    @Resource
    private SafetyPermissionDelegationHandler safetyPermissionDelegationHandler;

    @Resource
    private SafetyBlackMessageHandler safetyBlackMessageHandler;

    @Resource
    private SafetyEntryBlankProcessDetailHandler safetyEntryBlankProcessDetailHandler;

    @Resource
    private SafetyEntryConfigHandler safetyEntryConfigHandler;


    /**
     * 初始化员工的楼宇门禁权限
     */
    @ApiOperation(value = "同步EHR黑名单")
    @RequestMapping(value = "/syncEhrBlank", method = {RequestMethod.GET, RequestMethod.POST})
    public Object syncEhrBlank(String begTime, String endTime) {
        return safetyBlackMessageHandler.syncEhrBlack(begTime, endTime);
    }

    /**
     * 每天17点邮件提醒对接人，提醒内容中包含备案人员信息
     */
    @ApiOperation(value = "备案过期任务提醒")
    @RequestMapping(value = "/entryOverdueTask", method = {RequestMethod.GET, RequestMethod.POST})
    public VO entryOverdueTask() {
        return safetyEntryCompanyRegisterHandler.entryOverdueTask();
    }

    /**
     * 备案权限下发， 30 分钟执行一次，下发的数据为当前时间 + 33分钟，预留3分钟的时间差
     */
    @ApiOperation(value = "备案权限下发")
    @RequestMapping(value = "/entryPermissionDelegationTask", method = {RequestMethod.GET, RequestMethod.POST})
    public VO entryPermissionDelegationTask() {
        return safetyPermissionDelegationHandler.entryPermissionDelegationTask();
    }

    @ApiOperation(value = "黑名单入场备案测试接口")
    @RequestMapping(value = "/entryBpmToSafety", method = {RequestMethod.GET, RequestMethod.POST})
    public VO entryBpmToSafety(String processId, String code) {
         safetyEntryBlankProcessDetailHandler.updateFlowStatus(processId, code);
         return null;
    }

    @ApiOperation(value = "定时清理无效的备案信息")
    @RequestMapping(value = "/clearEntryConfig", method = {RequestMethod.GET, RequestMethod.POST})
    public VO clearEntryConfig() {
        safetyEntryConfigHandler.clearEntryConfig();
        return null;
    }

    @ApiOperation(value = "清理消息发送的redis")
    @RequestMapping(value = "/clearRedis", method = {RequestMethod.GET, RequestMethod.POST})
    public VO clearRedis(String buttJoinCodes, String idCards) {
        return safetyEntryConfigHandler.clearRedis(buttJoinCodes, idCards);
    }
}
