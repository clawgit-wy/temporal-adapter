package com.yutong.modules.common.entity.bpm.flowauth;

import com.yutong.modules.common.entity.bpm.approve.CanApprovalProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSearchFlowAuthBody {

    @XmlElement(name = "getSearchFlowAuthResponse", namespace = "http://ws.self.kmss.landray.com/")
    private GetSearchFlowAuthResponse searchFlowAuthResponse;
}
