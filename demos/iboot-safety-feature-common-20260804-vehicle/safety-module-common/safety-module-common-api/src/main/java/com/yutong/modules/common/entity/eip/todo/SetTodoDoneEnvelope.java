package com.yutong.modules.common.entity.eip.todo;

import com.yutong.modules.common.entity.eip.done.SendTodoBody;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"body"})
public class SetTodoDoneEnvelope {

    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private SetTodoDoneBody body;
}
