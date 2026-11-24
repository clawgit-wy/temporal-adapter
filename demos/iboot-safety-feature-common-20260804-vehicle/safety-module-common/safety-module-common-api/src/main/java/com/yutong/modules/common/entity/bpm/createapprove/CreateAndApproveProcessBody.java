package com.yutong.modules.common.entity.bpm.createapprove;

import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateAndApproveProcessBody {
    @XmlElement(name = "CreateAndApproveProcessResponse", namespace = "http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/")
    private CreateAndApproveProcessResponse createAndApproveProcessResponse;
}
