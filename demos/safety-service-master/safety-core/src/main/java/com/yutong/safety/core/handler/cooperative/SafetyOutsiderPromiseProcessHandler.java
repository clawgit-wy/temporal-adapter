package com.yutong.safety.core.handler.cooperative;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseProcessDTO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckRecordHandler;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author caomg
 * @date 2024-11-5 15:29
 * @description SafetyOutsiderPromiseProcessHandler
 */
@Component
public class SafetyOutsiderPromiseProcessHandler {

    @Autowired
    SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    @Autowired
    SafetySpotCheckRecordHandler safetySpotCheckRecordHandler;

    public Object findPageList(SafetyOutsiderPromiseProcessDTO dto, String token) {
        String bpmUrlPre = safetySpotCheckRecordHandler.getBpmPreUrl("safety_outsider_promise_change");
        dto.setBpmUrlPre(bpmUrlPre);

        List<SafetyOutsiderPromiseProcessDTO> list = safetyOutsiderPromiseChangeService.findPageProcessList(dto);
        int total = safetyOutsiderPromiseChangeService.getProcessCount(dto);
        return new PageVO<>(total, list);
    }
}
