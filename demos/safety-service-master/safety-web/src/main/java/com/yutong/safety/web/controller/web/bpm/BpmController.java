package com.yutong.safety.web.controller.web.bpm;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.handler.bpm.BpmServiceHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * BPM 前端控制器
 * </p>
 *
 * @author renhaoxiang
 * @since 2023-21
 */

@Api(tags = "BPM接口测试")
@RestController
@RequestMapping("/bpm/")
public class BpmController {
    @Autowired
    private BpmServiceHandler bpmServiceHandler;

    /*创建审批流*/
    @RequestMapping(value = "createBpmFlow", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "创建审批流")
    public VO createBpmFlow(BpmProcessParam processParam) {
        return bpmServiceHandler.createBpmFlow(processParam);
    }
    /*创建and提交审批流*/
    @RequestMapping(value = "createAndApproveBpmFlow", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "创建and提交审批流")
    public VO createAndApproveBpmFlow(BpmProcessParam processParam) {
        String rt = bpmServiceHandler.createAndApproveBpmFlow(processParam);
        return VoUtil.successData(rt);
    }
    /*提交审批流*/
    @RequestMapping(value = "approveBpmFlow", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "纯接口审批流程")
    public VO approveBpmFlow(BpmProcessParam processParam) {
        String rt = bpmServiceHandler.approveBpmFlowJK(processParam);
        return VoUtil.successData(rt);
    }
    /*废弃审批流*/
    @RequestMapping(value = "discardBpmFlow", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "废弃审批流")
    public VO discardBpmFlow(BpmProcessParam param) {
        String rt = bpmServiceHandler.discardBpmFlow(param);
        return VoUtil.successData(rt);
    }
    /*流程合法性校验*/
    @RequestMapping(value = "validateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "流程合法性校验")
    public VO validateProcess(BpmProcessParam param) {
        return bpmServiceHandler.validateProcess(param);
    }

    /*获取taskInfo*/
    @RequestMapping(value = "getTasksInfo", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "获取taskInfo")
    public VO getTasksInfo(BpmProcessParam params) {
        return this.bpmServiceHandler.getTasksInfo(params);
    }

    @RequestMapping(value = "getOperationList", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "获取操作集")
    public VO getOperationList(BpmProcessParam params) {
        return this.bpmServiceHandler.getOperationList(params);
    }

    /*获取流程当前节点信息*/
    @RequestMapping(value = "getCurrentNodesInfo", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "获取流程当前节点信息")
    public VO getCurrentNodesInfo(String processId) {
        return this.bpmServiceHandler.getCurrentNodesInfo(processId);
    }

}

