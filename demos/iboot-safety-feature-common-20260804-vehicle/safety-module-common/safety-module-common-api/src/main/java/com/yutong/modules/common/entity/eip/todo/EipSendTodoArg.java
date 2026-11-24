package com.yutong.modules.common.entity.eip.todo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
public class EipSendTodoArg {

    @XmlElement
    @ApiModelProperty("第三方系统标识")
    private String appName;

    @XmlElement
    @ApiModelProperty("格式为:yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @XmlElement
    @ApiModelProperty("待办的创建者。数据格式为JSON")
    private String docCreator;

    @XmlElement
    @ApiModelProperty("待办优先级。如：按紧急（1）、急（2）、一般（3）")
    private Integer level=3;

    @XmlElement
    @ApiModelProperty("对应待办的链接地址(全路径)")
    private String link;

    @XmlElement
    @ApiModelProperty("待办在原系统唯一标识")
    private String modelId;

    @XmlElement
    @ApiModelProperty("待办来源的模块")
    private String modelName;

    @XmlElement
    @ApiModelProperty("待办标题")
    private String subject;

    @XmlElement
    @ApiModelProperty("待办对应接收人，数据格式为JSON")
    private String targets;

    @XmlElement
    @ApiModelProperty("待办类型：1表示审批类待办 2表示为通知类待办")
    private Integer type;

    @XmlElement
    @ApiModelProperty("备用参数，方便以后参数的扩展")
    private String others;
}
