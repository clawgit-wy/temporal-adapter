package com.yutong.modules.common.entity.bpm.approve;

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
public class ApproveProcess {

    @XmlTransient
    private Long formInstanceId;

    @XmlTransient
    private String curNodeId;

    @XmlElement
    private String formId;

    @XmlElement
    private String processId;

    @XmlElement
    private String handler;

    @XmlElement
    private String processParam;

}
