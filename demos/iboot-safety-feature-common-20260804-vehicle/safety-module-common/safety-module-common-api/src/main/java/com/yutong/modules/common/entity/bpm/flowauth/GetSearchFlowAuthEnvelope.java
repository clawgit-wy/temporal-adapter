package com.yutong.modules.common.entity.bpm.flowauth;

import com.yutong.modules.common.entity.bpm.approve.CanApprovalProcessBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetSearchFlowAuthEnvelope {

    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetSearchFlowAuthBody body;
}
