package com.yutong.safety.web.controller.app.visitor;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationApprovalHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "访客审批授权")
@RestController
@RequestMapping("/app/safety/visitor/job")
public class SafetyVisitorJobController {

    @Resource
    private SafetyVisitorAuthorizationApprovalHandler safetyVisitorAuthorizationApprovalHandler;

    @ApiOperation(value = "访客授权审批定时任务")
    @RequestMapping(value = "/taskVisitorApproval", method = {RequestMethod.POST})
    public VO taskVisitorApproval(String type) {
        return safetyVisitorAuthorizationApprovalHandler.taskVisitorApproval(type);
    }
}
