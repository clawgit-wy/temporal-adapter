package com.yutong.modules.common.entity.eip.todo;

import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SetTodoDoneResponse {

    @XmlElement(name = "return")
    private SetTodoDoneReturn todoDoneReturn;
}
