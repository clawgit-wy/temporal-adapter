package com.yutong.safety.web.controller.web.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikThresholdAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikThresholdQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.hik.SafetyHikThresholdHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "海康配置-阈值")
@RestController
@RequestMapping("/web/safety/hik/threshold")
public class SafetyHikThresholdController extends AbstractSafetyWebBaseController<SafetyHikThresholdDO, SafetyHikThresholdDOExample, SafetyHikThresholdQueryParam, SafetyHikThresholdAddParam> {

    @Resource
    private SafetyHikThresholdHandler safetyHikThresholdHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyHikThresholdDO, SafetyHikThresholdDOExample, SafetyHikThresholdQueryParam, SafetyHikThresholdAddParam> getHandler() {
        return safetyHikThresholdHandler;
    }
}
