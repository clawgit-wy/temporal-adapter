package com.yutong.modules.common.api.eip;

import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import com.yutong.modules.common.entity.eip.todo.EipSendTodoArg;
import com.yutong.modules.common.entity.eip.todo.SetTodoDoneReturn;

/**
 * EIP待办服务
 */
public interface IEipDoneService {

    /**
     * 创建Eip待办
     * @param arg
     * @return
     */
    public SendTodoReturn createEipTodo(EipSendTodoArg arg);

    /**
     * 设置EIP代办为已办
     * @param arg
     * @return
     */
    public SetTodoDoneReturn eipToDoDone(EipSetTodoDoneArg arg);
}
