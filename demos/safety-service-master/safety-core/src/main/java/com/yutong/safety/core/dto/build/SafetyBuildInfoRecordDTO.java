package com.yutong.safety.core.dto.build;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class SafetyBuildInfoRecordDTO extends WebPageParam {
    private String beginDate;
    private String endDate;
    private Integer id;
    private String infoType;
    private String infoContent;
    private String infoTime;
    private String infoConfirm;
    private String needPage;

}
