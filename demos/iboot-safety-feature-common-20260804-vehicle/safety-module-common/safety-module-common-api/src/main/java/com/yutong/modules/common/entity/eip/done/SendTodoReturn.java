package com.yutong.modules.common.entity.eip.done;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SendTodoReturn {

    @XmlElement(name = "message")
    @ApiModelProperty("异常消息")
    private String message;

    @XmlElement(name = "returnState")
    @ApiModelProperty("返回类型：0 表示未操作 1 操作失败 2 操作成功")
    private Integer returnState;
}
