package com.yutong.modules.common.entity.bpm.flowinfo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FlowInfoBody {
    @XmlElement(name = "getReviewInfosNewResponse", namespace = "http://ws.self.kmss.landray.com/")
    private FlowInfoResponse flowInfoResponse;
}
