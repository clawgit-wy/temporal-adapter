package com.yutong.modules.common.entity.bpm.flowinfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class FlowInfoApprover {


    private String nodeIds;

    private String nodeNames;

    private String nodeStartDate;
    private String fdCurrNos;

    private String fdCurrNames;
    private String fdCurrLoginNames;

    private String fdStatus;

    private String creatorName;

    private String fdNo;

    private String fdCreateTime;

    private String modelId;


}
