package com.yutong.safety.core.handler.openclose;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.openclose.SafetyOpenCloseDO;
import com.yutong.safety.core.service.openclose.SafetyOpenCloseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author caomg
 * @date 2024-11-21 10:54
 * @description OpenCloseHandler
 */
@Component
public class SafetyOpenCloseHandler {
    @Autowired
    SafetyOpenCloseService safetyOpenCloseService;

    public void setOpenStatus(SafetyOpenCloseDO en) {
        safetyOpenCloseService.setOpenStatus(en);
    }

    public SafetyOpenCloseDO getOpenInfo(SafetyOpenCloseDO en) {
        return safetyOpenCloseService.getOpenInfo(en);
    }
}
