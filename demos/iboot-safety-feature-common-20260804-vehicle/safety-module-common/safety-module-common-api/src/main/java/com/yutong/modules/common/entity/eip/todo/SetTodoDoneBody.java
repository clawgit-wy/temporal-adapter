package com.yutong.modules.common.entity.eip.todo;

import com.yutong.modules.common.entity.eip.done.SendTodoResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SetTodoDoneBody {

    @XmlElement(name = "setTodoDoneResponse", namespace = "http://webservice.notify.sys.kmss.landray.com/")
    private SetTodoDoneResponse todoDoneResponse;
}
