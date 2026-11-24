package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class SafetyEntryBlankProcessQueryParams extends WebPageParam {

    private String bpmFlowId;

}
