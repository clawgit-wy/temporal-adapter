package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDO;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author lukunpeng
 * @time 2024-4-30
 *
 * 流程相关操作handler
 */
@Component
public class SafetyBpmProcessHandler {

    @Autowired
    SafetyBpmProcessService safetyBpmProcessService;

    /**
     * 触发流程
     * @param subject 流程主题
     * @param formId 对接BPM系统模块表 formId 流程唯一标识
     * @return
     */
    public String createProcess(String subject, String modelId, String formId, String token){
        return safetyBpmProcessService.createProcess(subject, modelId, formId, token);
    };


    /**
     * 审批流程
     * @param flowId 流程ID
     * @param modelId 模块ID
     * @param formId 流程表单ID
     * @param fdParameterJson 审批操作参数
     * @return
     */
    public String approveProcess(String id, String flowId, String modelId, String formId, String fdParameterJson, String token){
        return  safetyBpmProcessService.approveProcess(id, flowId, modelId, formId, fdParameterJson, token);
    };

    /**
     * 获取当前节点字段权限 0表示可编辑，1表示不可见，2表示只读
     * @param flowId 流程ID
     * @param formId 流程表单ID
     * @param nodeId 节点ID
     * @return
     */
    public String getAuthFields(String id, String flowId, String formId, String nodeId) {
        return  safetyBpmProcessService.getAuthFields(id, flowId, formId, nodeId);
    }

    /**
     * 根据流程ID获取当前节点ID
     * @param flowId
     * @return
     */
    public Object getNowNode(String id, String flowId, String formId) {
        return  safetyBpmProcessService.getNowNode(id, flowId, formId);
    }

    public Object getApproverList(String id, String flowId, String formId) {
        return  safetyBpmProcessService.getApproverList(id, flowId, formId);
    }

    public Object getCurrentNodesInfo(String id, String flowId, String formId) {
        return  safetyBpmProcessService.getCurrentNodesInfo(id, flowId, formId);
    }



    /**
     * 获取审批意见
     * @param id ID
     * @param flowId 流程ID
     * @param formId formId
     * @param userName 登录名
     * @return 返回值
     */
    public Object getAuditOptionList(String id, String flowId, String formId,String userName){
        return safetyBpmProcessService.getAuditOptionList(id, flowId, formId, userName);
    };


    public Object circulate(String flowId, String fromUser, String toUser) {
        return safetyBpmProcessService.circulate(flowId, fromUser, toUser);
    }

    public Object getProcessId(String formInstanceId,String type) {
        return safetyBpmProcessService.getProcessId(formInstanceId,type);
    }

    //获取流程当前节点处理人信息
    public Object getProcessCurDealHandler(String bpmFlowId){
        return  safetyBpmProcessService.getFlowInfoForSelf(bpmFlowId);
    }
}
