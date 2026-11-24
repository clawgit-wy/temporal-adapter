package com.yutong.modules.common.entity.bpm.passprocess;

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
public class PassProcess {

    @XmlElement
    private String processId;

    @XmlElement
    private String circulated;

    @XmlElement
    private String beCirculated;
}
