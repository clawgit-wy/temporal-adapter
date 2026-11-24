package com.yutong.modules.common.entity.bpm.create;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateResponseBody {

    @XmlElement(name = "CreateProcessResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private CreateProcessResponse createProcessResponse;

}
