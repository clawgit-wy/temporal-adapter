package com.yutong.modules.common.factory;

import com.yutong.modules.common.factory.action.ActionService;

/**
 * 执行发送类型
 */
public interface IThirdApiFactory {

    public ActionService getActionService(String actionType);
}
