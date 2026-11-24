package com.yutong.modules.common.entity.bpm.create;

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
public class CreateProcess {

    @XmlElement
    private String flowTemplateId;

    @XmlElement
    private String formId;

    @XmlElement
    private String creator;

    @XmlElement
    private String exParam;

    @XmlElement
    private String language;
}
