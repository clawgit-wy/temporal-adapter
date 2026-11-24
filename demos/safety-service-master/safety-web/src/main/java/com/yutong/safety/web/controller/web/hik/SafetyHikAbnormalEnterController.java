package com.yutong.safety.web.controller.web.hik;

import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikAbnormalEnterAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikAbnormalEnterQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.hik.SafetyHikAbnormalEnterHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "海康配置-异常进出记录")
@RestController
@RequestMapping("/web/safety/hik/abnormalEnter")
public class SafetyHikAbnormalEnterController extends AbstractSafetyWebBaseController<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample, SafetyHikAbnormalEnterQueryParam, SafetyHikAbnormalEnterAddParam> {

    @Resource
    private SafetyHikAbnormalEnterHandler safetyHikAbnormalEnterHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample, SafetyHikAbnormalEnterQueryParam, SafetyHikAbnormalEnterAddParam> getHandler() {
        return safetyHikAbnormalEnterHandler;
    }
}
