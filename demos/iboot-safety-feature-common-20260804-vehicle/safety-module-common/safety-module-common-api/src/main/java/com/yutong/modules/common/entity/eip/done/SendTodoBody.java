package com.yutong.modules.common.entity.eip.done;

import com.yutong.modules.common.entity.bpm.approve.CanApprovalProcessResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SendTodoBody {

    @XmlElement(name = "sendTodoResponse", namespace = "http://webservice.notify.sys.kmss.landray.com/")
    private SendTodoResponse sendTodoResponse;
}
