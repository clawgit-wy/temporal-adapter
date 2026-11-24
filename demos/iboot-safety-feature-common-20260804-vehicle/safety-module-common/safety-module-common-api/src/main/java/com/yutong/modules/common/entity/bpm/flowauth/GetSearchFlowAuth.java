package com.yutong.modules.common.entity.bpm.flowauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSearchFlowAuth {

    @XmlElement
    private String modelIds;

    @XmlElement
    private String modelName;
}
