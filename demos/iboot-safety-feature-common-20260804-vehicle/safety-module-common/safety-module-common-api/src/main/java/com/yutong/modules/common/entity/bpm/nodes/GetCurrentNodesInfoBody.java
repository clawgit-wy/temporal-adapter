package com.yutong.modules.common.entity.bpm.nodes;

import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrentNodesInfoBody {
    @XmlElement(name = "GetCurrentNodesInfoResponse", namespace = "http://interfaces.lbpmdocking.sys.kmss.landray.com/")
    private GetCurrentNodesInfoResponse currentNodesInfoResponse;
}
