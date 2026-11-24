package com.yutong.modules.common.entity.bpm.approve;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CanApprovalProcessBody {

    @XmlElement(name = "CanApprovalProcessResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private CanApprovalProcessResponse approveProcessResponse;
}
