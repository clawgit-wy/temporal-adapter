package com.yutong.safety.core.handler.bpm;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.bpm.BpmOperations;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.result.BpmModelRt;

import java.util.List;

public interface BpmServiceHandler {
    /**
     * 保存表单后，同时创建BPM流程（草稿状态）
     * @return
     */
    VO createBpmFlow(BpmProcessParam processParam);
    /**
     * （二合一接口）保存表单后，同时创建和提交BPM流程
     * @return
     */
    String createAndApproveBpmFlow(BpmProcessParam processParam);
    /**
     * 审批BPM流程（有内嵌页面）
     * @return
     */
    String approveBpmFlow(BpmProcessParam processParam);
    /**
     * 纯接口审批BPM流程（无内嵌页面）
     * @return
     */
    String approveBpmFlowJK(BpmProcessParam processParam);

    /**
     * 创建人 废弃
     * @param param
     * @return
     */
    String discardBpmFlow(BpmProcessParam param);

    /**
     * 删除流程
     * @return
     */
    VO deleteBpmFlow(BpmProcessParam param);

    /**
     * 验证审批权限
     * @return
     */
   String canApprovalProcess(BpmProcessParam param);

    /**
     * 流程合法性校验
     * @return
     */
    VO validateProcess(BpmProcessParam param);

    /**
     * 可操作类型
     * @param param
     * @return
     */
    VO<List<BpmOperations>> getOperationList(BpmProcessParam param);

    /**
     * 当前账号task信息
     * @param param
     * @return
     */
    VO getTasksInfo(BpmProcessParam param);

    /**
     * 流程当前节点信息
     * @param processIds
     * @return
     */
    VO getCurrentNodesInfo(String processIds);
    VO getCurrentNodesInfo(String processIds, String modelName);

    BpmModelRt getModelInfo();


}
