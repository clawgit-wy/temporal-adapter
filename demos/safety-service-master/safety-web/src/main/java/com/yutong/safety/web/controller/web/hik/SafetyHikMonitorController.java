package com.yutong.safety.web.controller.web.hik;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.SafetyFieldManageHandler;
import com.yutong.safety.core.handler.hik.SafetyHikMonitorHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "海康配置-抓拍摄像头信息")
@RestController
@RequestMapping("/web/safety/hik/monitor")
public class SafetyHikMonitorController extends AbstractSafetyWebBaseController<SafetyHikMonitorDO, SafetyHikMonitorDOExample, SafetyHikMonitorQueryParam, SafetyHikMonitorAddParam> {

    @Resource
    private SafetyHikMonitorHandler safetyHikMonitorHandler;

    @Resource
    private SafetyFieldManageHandler safetyFieldManageHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyHikMonitorDO, SafetyHikMonitorDOExample, SafetyHikMonitorQueryParam, SafetyHikMonitorAddParam> getHandler() {
        return safetyHikMonitorHandler;
    }

    @ApiOperation(value = "数据更新")
    @RequestMapping(value = "/syncUpdateInfo", method = {RequestMethod.POST})
    public VO syncUpdateInfo(String type, @RequestHeader("X-Access-Token") String token) {
        return safetyHikMonitorHandler.syncUpdateInfo(type, TokenUtils.getRealName(token));
    }

    @ApiOperation(value = "下拉框")
    @RequestMapping(value = "/dropDownBox", method = {RequestMethod.GET})
    public VO dropDownBox() {
        return safetyFieldManageHandler.dropDownBox();
    }
}
