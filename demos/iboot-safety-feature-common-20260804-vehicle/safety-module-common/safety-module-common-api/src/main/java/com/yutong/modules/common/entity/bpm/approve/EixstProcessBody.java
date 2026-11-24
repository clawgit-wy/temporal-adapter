package com.yutong.modules.common.entity.bpm.approve;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class EixstProcessBody {
    @XmlElement(name = "IsExistProcessResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private ExistProcessResponse existProcessResponse;
}
