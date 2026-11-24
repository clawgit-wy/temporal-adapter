package com.yutong.modules.bpmcallback;

import com.yutong.modules.bpmcallback.action.NodeAction;
import com.yutong.modules.common.factory.action.ActionService;

/**
 * 执行分发器
 */
public interface IBpmCallBackFactory {

    public NodeAction getNodeAction(String actionType);
}
