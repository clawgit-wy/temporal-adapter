package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lukunpeng
 * @time 2024-5-13
 */
@Api(tags = "流程相关接口")
@RestController
@RequestMapping("/web/safety/process")
public class SafetyBpmProcessController {

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    /**
     * 获取当前节点ID
     * @return
     */
    @RequestMapping("/getNowNode")
    public Object getNowNode(String id, String flowId, String formId) {
        return safetyBpmProcessHandler.getNowNode(id, flowId, formId);
    }

    /**
     * 获取当前节点处理人
     * @return
     */
    @RequestMapping("/getApproverList")
    public Object getApproverList(String id, String flowId, String formId) {
        return safetyBpmProcessHandler.getApproverList(id, flowId, formId);
    }

    /**
     * 获取当前节点处理人
     * @return
     */
    @RequestMapping("/getCurrentNodesInfo")
    public Object getCurrentNodesInfo(String id, String flowId, String formId) {
        return safetyBpmProcessHandler.getCurrentNodesInfo(id, flowId, formId);
    }

    /**
     * 获取当前节点处理人
     * @return
     */
    @RequestMapping("/getAuditOptionList")
    public Object getAuditOptionList(String id, String flowId, String formId,String userName) {
        return safetyBpmProcessHandler.getAuditOptionList(id, flowId, formId,userName);
    }

    @ApiOperation("传阅")
    @RequestMapping("/circulate")
    public Object circulate(String flowId, String fromUser, String toUser) {
        return safetyBpmProcessHandler.circulate(flowId, fromUser, toUser);
    }

    @ApiOperation("根据formInstanceId查询流程id")
    @RequestMapping("/getProcessId")
    public Object getProcessId(@RequestParam(name = "formInstanceId",required = true) String formInstanceId, @RequestParam(name = "type",required = false) String type) {
        return safetyBpmProcessHandler.getProcessId(formInstanceId,type);
    }

    /**
     * 数据新增或者更新操作
     * @param bpmFlowId bpm流程ID
     * @return 返回值
     */
    @ApiOperation(value = "获取当前流程处理人")
    @RequestMapping(value = "/getProcessCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessCurDealHandler(String bpmFlowId){
        if(StringUtils.isBlank(bpmFlowId)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"入参必填");
        }
        return safetyBpmProcessHandler.getProcessCurDealHandler(bpmFlowId);
    }
}
