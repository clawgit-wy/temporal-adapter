package com.yutong.modules.common.entity.bpm.passprocess;

import com.yutong.modules.common.entity.bpm.approve.CanApprovalProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PassProcessBody {
    @XmlElement(name = "passProcessResponse", namespace = "http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/")
    private PassProcessResponse passProcessResponse;
}
