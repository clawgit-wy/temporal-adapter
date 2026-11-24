package com.yutong.modules.common.entity.bpm.createapprove;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateAndApproveProcess {
    @XmlTransient
    private Long formInstanceId;
    @XmlElement
    private String flowTemplateId;
    @XmlElement
    private String formId;
    @XmlElement
    private String creator;
    @XmlElement
    private String exParam;
}
