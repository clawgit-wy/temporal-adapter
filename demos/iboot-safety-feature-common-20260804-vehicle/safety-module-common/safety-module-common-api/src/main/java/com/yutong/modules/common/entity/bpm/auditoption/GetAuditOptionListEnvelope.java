package com.yutong.modules.common.entity.bpm.auditoption;

import com.yutong.modules.common.entity.bpm.approve.ApproveProcessBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetAuditOptionListEnvelope {

    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetAuditOptionListBody body;
}
