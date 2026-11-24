package com.yutong.modules.common.entity.bpm.approve;

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
public class CanApprovalProcess {

    @XmlElement
    private String formId;

    @XmlElement
    private String actionUid;

    @XmlElement
    private String language;
}
