package com.yutong.safety.web.controller.app.spot;

import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/safety")
public class SafetyAppController {

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;


    /**
     * 获取当前节点处理人
     * @return
     */
    @RequestMapping("/getApproverList")
    public Object getApproverList(String id, String flowId, String formId) {
        return safetyBpmProcessHandler.getApproverList(id, flowId, formId);
    }

    /**
     * 查看流程
     * @return
     */
    @ApiOperation("查看流程")
    @RequestMapping("/view")
    public Object view(String flowId){
        return safetyOutsiderPromiseChangeHandler.view(flowId);
    }
}
