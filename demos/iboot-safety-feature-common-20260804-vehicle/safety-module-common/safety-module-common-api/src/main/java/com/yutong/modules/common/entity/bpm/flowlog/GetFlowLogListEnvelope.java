package com.yutong.modules.common.entity.bpm.flowlog;

import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionListBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetFlowLogListEnvelope {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetFlowLogListBody body;
}
