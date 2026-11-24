package com.yutong.modules.common.entity.eip.done;

import io.swagger.annotations.ApiModelProperty;
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
public class EipSetTodoDoneArg {

    @XmlElement
    @ApiModelProperty("第三方系统标识")
    private String appName;

    @XmlElement
    private String key;

    @XmlElement
    @ApiModelProperty("待办在原系统唯一标识")
    private String modelId;

    @XmlElement
    @ApiModelProperty("待办来源的模块")
    private String modelName;

    @XmlElement
    private Integer optType=1;

    @XmlElement
    private String param1;

    @XmlElement
    private String param2;

    @XmlElement
    private String targets;

    @XmlElement
    @ApiModelProperty("待办类型：1表示审批类待办 2表示为通知类待办")
    private Integer type;
}
