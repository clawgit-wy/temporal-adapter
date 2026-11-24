package com.yutong.modules.common.entity.bpm.flowlog;

import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionListResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetFlowLogListBody {
    @XmlElement(name = "GetFlowLogListResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private GetFlowLogListResponse flowLogListResponse;
}
