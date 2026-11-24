package com.yutong.modules.common.entity.bpm.tasks;

import com.yutong.modules.common.entity.bpm.approve.ApproveProcessBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class GetTasksInfoEnvelope {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private GetTasksInfoBody body;
}
