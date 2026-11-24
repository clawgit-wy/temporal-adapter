package com.yutong.safety.core.service.openclose;

import com.yutong.safety.core.db.entity.openclose.SafetyOpenCloseDO;
import com.yutong.safety.core.db.mapper.openclose.SafetyOpenCloseDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author caomg
 * @date 2024-11-21 10:55
 * @description OpenCloseService
 */
@Service
public class SafetyOpenCloseService {
    @Autowired
    SafetyOpenCloseDOMapper safetyOpenCloseDOMapper;

    public void setOpenStatus(SafetyOpenCloseDO en) {
        safetyOpenCloseDOMapper.setOpenStatus(en);
    }

    public SafetyOpenCloseDO getOpenInfo(SafetyOpenCloseDO en) {
        return safetyOpenCloseDOMapper.getOpenInfo(en);
    }
}
