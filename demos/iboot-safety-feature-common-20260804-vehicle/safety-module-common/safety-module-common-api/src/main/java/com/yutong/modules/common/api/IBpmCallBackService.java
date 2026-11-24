package com.yutong.modules.common.api;

import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;

public interface IBpmCallBackService {

    /**
     * 回调函数
     * @param event
     */
    public void processCallBack(BpmMethodProcessDTO event);
}
