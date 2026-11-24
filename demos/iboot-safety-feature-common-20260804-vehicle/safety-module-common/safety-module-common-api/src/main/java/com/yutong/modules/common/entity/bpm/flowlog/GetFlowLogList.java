package com.yutong.modules.common.entity.bpm.flowlog;

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
public class GetFlowLogList {

    @XmlElement
    private String formId;

    @XmlElement
    private String processId;

    @XmlElement
    private Integer pageNo=1;

    @XmlElement
    private Integer pageSize=1000;
}
