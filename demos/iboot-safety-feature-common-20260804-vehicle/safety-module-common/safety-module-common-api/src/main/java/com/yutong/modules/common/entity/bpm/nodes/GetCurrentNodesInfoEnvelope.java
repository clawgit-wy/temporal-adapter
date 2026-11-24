package com.yutong.modules.common.entity.bpm.nodes;

import com.yutong.modules.common.entity.bpm.create.CreateResponseBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetCurrentNodesInfoEnvelope {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetCurrentNodesInfoBody body;
}
