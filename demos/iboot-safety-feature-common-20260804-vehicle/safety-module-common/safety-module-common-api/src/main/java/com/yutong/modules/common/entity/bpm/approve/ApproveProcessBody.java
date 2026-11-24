package com.yutong.modules.common.entity.bpm.approve;

import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ApproveProcessBody {
    @XmlElement(name = "ApproveProcessResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private ApproveProcessResponse approveProcessResponse;
}
