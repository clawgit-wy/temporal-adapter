package com.yutong.safety.web.controller.web.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterAddParam;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterQueryParams;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryCompanyRegisterHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "外部供应商注册信息表")
@RestController
@RequestMapping("/web/entry/safety/companyRegister")
public class SafetyEntryCompanyRegisterController extends AbstractSafetyWebBaseController<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample, SafetyEntryCompanyRegisterQueryParams, SafetyEntryCompanyRegisterAddParam> {

    @Resource
    private SafetyEntryCompanyRegisterHandler safetyEntryCompanyRegisterHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample, SafetyEntryCompanyRegisterQueryParams, SafetyEntryCompanyRegisterAddParam> getHandler() {
        return safetyEntryCompanyRegisterHandler;
    }
}
