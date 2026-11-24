package com.yutong.modules.common.entity.bpm.Validate;

import com.yutong.modules.common.entity.bpm.approve.ApproveProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateProcessBody {
    @XmlElement(name = "ValidateProcessResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private ValidateProcessResponse validateProcessResponse;

}
