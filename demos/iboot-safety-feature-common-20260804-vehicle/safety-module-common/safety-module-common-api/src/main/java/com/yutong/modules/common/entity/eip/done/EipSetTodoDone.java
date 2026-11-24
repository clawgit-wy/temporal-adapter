package com.yutong.modules.common.entity.eip.done;

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
public class EipSetTodoDone {

    @XmlElement(name = "arg0")
    private EipSetTodoDoneArg arg0;
}
