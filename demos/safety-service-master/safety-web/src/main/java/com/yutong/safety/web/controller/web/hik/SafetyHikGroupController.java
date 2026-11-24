package com.yutong.safety.web.controller.web.hik;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikGroupAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikGroupQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.hik.SafetyHikGroupHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "海康配置-人脸分组")
@RestController
@RequestMapping("/web/safety/hik/group")
public class SafetyHikGroupController extends AbstractSafetyWebBaseController<SafetyHikGroupDO, SafetyHikGroupDOExample, SafetyHikGroupQueryParam, SafetyHikGroupAddParam> {

    @Resource
    private SafetyHikGroupHandler safetyHikGroupHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyHikGroupDO, SafetyHikGroupDOExample, SafetyHikGroupQueryParam, SafetyHikGroupAddParam> getHandler() {
        return safetyHikGroupHandler;
    }

    @ApiOperation(value = "数据更新")
    @RequestMapping(value = "/syncUpdateInfo", method = {RequestMethod.POST})
    public VO syncUpdateInfo(@RequestHeader("X-Access-Token") String token) {
        return safetyHikGroupHandler.syncUpdateInfo(TokenUtils.getRealName(token));
    }
}
