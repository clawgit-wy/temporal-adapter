package com.yutong.modules.common.entity.bpm.fields;

import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessBody;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetFieldAuthInfoEnvelope {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetFieldAuthInfoBody body;

}
