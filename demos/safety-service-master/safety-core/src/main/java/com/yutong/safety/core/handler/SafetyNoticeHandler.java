package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.NoticeParam;
import com.yutong.safety.core.service.SafetyNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  create gwcuiyx 2023.07.12
 *  访客须知--handler
 */
@Component
public class SafetyNoticeHandler {
    @Autowired
    private SafetyNoticeService safetyNoticeService;

    /**
     * 数据获取功能接口
     * @return
     */
    public Object getNotice() {
        return safetyNoticeService.getNotice();
    }

    /**
     * 数据新增或者更新接口
     * @param param
     * @return
     */
    public Object saveOrUpdate(NoticeParam param, String tocken) {
        safetyNoticeService.saveOrUpdate(param, tocken);
        return null;
    }

}
