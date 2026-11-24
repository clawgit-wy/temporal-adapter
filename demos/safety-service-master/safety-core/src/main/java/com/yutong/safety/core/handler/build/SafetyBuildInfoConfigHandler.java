package com.yutong.safety.core.handler.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDO;
import com.yutong.safety.core.service.build.SafetyBuildInfoConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SafetyBuildInfoConfigHandler {
    @Autowired
    SafetyBuildInfoConfigService safetyBuildInfoConfigService;

    public List<SafetyBuildInfoConfigDO> query(SafetyBuildInfoConfigDO param) {
        return safetyBuildInfoConfigService.query(param);
    }

    public void update(SafetyBuildInfoConfigDO param) {
        safetyBuildInfoConfigService.update(param);
    }
}
