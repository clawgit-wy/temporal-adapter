package com.yutong.safety.web.controller.web.visitor;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationAddParam;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationCreateParam;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationQueryParam;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationApprovalHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "访客审批授权")
@RestController
@RequestMapping("/web/safety/visitor/authorization")
public class SafetyVisitorAuthorizationApprovalController {

    @Resource
    private SafetyVisitorAuthorizationApprovalHandler safetyVisitorAuthorizationApprovalHandler;

    @ApiOperation(value = "分页数据列表查询接口-PC数据页")
    @RequestMapping(value = "/pageTokenList", method = {RequestMethod.GET, RequestMethod.POST})
    public VO pageTokenList(SafetyVisitorAuthorizationQueryParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyVisitorAuthorizationApprovalHandler.pageTokenList(param, token);
    }


    @ApiOperation(value = "新增和修改接口")
    @RequestMapping(value = "/saveAndUpdate", method = {RequestMethod.POST})
    public VO saveAndUpdate(@RequestBody SafetyVisitorAuthorizationAddParam info, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyVisitorAuthorizationApprovalHandler.saveAndUpdate(info, token);
        } catch (BizException e) {
            return VoUtil.fail(e.getCode(), e.getMsg());
        }
    }

    @ApiOperation(value = "创建和审批流程")
    @RequestMapping(value = "/saveAndUpdateProcess", method = {RequestMethod.POST})
    public VO saveAndUpdateProcess(@RequestBody SafetyVisitorAuthorizationCreateParam info, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyVisitorAuthorizationApprovalHandler.saveAndUpdateProcess(info, token);
        } catch (BizException e) {
            return VoUtil.fail(e.getCode(), e.getMsg());
        }
    }

    @ApiOperation(value = "授权人校验")
    @RequestMapping(value = "/checkEmpNo", method = {RequestMethod.POST})
    public VO checkEmpNo(String empNo) {
        return safetyVisitorAuthorizationApprovalHandler.checkEmpNo(empNo);
    }

    @ApiOperation(value = "授权人科长校验")
    @RequestMapping(value = "/checkSectionChief", method = {RequestMethod.POST})
    public VO checkSectionChief(String empNo) {
        return safetyVisitorAuthorizationApprovalHandler.checkSectionChief(empNo);
    }


    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET})
    public void export(SafetyVisitorAuthorizationQueryParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        safetyVisitorAuthorizationApprovalHandler.export(response, param, token);
    }

    @ApiOperation(value = "流程详情页")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.GET})
    public VO getDetail(Integer processId) {
        return safetyVisitorAuthorizationApprovalHandler.getDetail(processId);
    }

    @ApiOperation(value = "访客审批授权状态变更")
    @RequestMapping(value = "/updateFlowStatus", method = {RequestMethod.GET})
    public void updateFlowStatus() {
        safetyVisitorAuthorizationApprovalHandler.updateFlowStatus("19e6905d40605bc34f455de4e90a24a3", "30");
    }
}
