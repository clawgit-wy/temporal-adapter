package com.yutong.safety.web.controller.web.visitor;

import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDOExample;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationConfigAddParam;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationConfigQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "访客授权配置")
@RestController
@RequestMapping("/web/safety/visitor/authorizationConfig")
public class SafetyVisitorAuthorizationController extends AbstractSafetyWebBaseController<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample, SafetyVisitorAuthorizationConfigQueryParam, SafetyVisitorAuthorizationConfigAddParam> {

    @Resource
    private SafetyVisitorAuthorizationHandler safetyVisitorAuthorizationHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample, SafetyVisitorAuthorizationConfigQueryParam, SafetyVisitorAuthorizationConfigAddParam> getHandler() {
        return safetyVisitorAuthorizationHandler;
    }
}
