package com.yutong.modules.common.vo;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EipToDoVO implements Serializable {

    @ApiModelProperty("第三方系统标识")
    private String appName;
    @ApiModelProperty("待办来源的模块")
    private String modelId;
    @ApiModelProperty("待办在原系统唯一标识")
    private String modelName;
    @ApiModelProperty("待办标题")
    private String subject;
    @ApiModelProperty("对应待办的链接地址(全路径)")
    private String link;
    @ApiModelProperty("待办类型：1表示审批类待办 2表示为通知类待办")
    private String type;
    @ApiModelProperty("待办对应接收人，数据格式为JSON")
    private String targets;
    @ApiModelProperty("格式为:yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @ApiModelProperty("待办的创建者。数据格式为JSON")
    private String docCreator;
    @ApiModelProperty("待办优先级。如：按紧急（1）、急（2）、一般（3）")
    private String level;
    @ApiModelProperty("待办附加标识")
    private String param1;
    @ApiModelProperty("备用参数，方便以后参数的扩展")
    private String others;

    public EipToDoVO() {
    }

    public EipToDoVO(String modelId, String modelName, String subject, String link, String type, String targets, String createTime, String docCreator, String level) {
        this.appName = "SAFETY";
        this.modelId = modelId;
        this.modelName = modelName;
        this.subject = subject;
        this.link = link;
        this.type = StringUtils.isBlank(type) ? "1" : type;
        this.targets = targets;
        this.createTime = StringUtils.isBlank(createTime) ? DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") : createTime;
        this.docCreator = docCreator;
        this.level = StringUtils.isBlank(level) ? "3" : level;
    }

    public EipToDoVO(String modelId, String modelName, String subject, String link, String type, String targets, String createTime, String docCreator, String level, String others) {
        this.appName = "SAFETY";
        this.modelId = modelId;
        this.modelName = modelName;
        this.subject = subject;
        this.link = link;
        this.type = StringUtils.isBlank(type) ? "1" : type;
        this.targets = targets;
        this.createTime = StringUtils.isBlank(createTime) ? DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") : createTime;
        this.docCreator = docCreator;
        this.level = StringUtils.isBlank(level) ? "3" : level;
        this.others = "isOpenInMobile".equals(others) ? "{\"isOpenInMobile\":\"0\"}" : others;
    }

    public EipToDoVO(String modelId, String modelName, String subject, String link, String targets, String others) {
        this.appName = "SAFETY";
        this.modelId = modelId;
        this.modelName = modelName;
        this.subject = subject;
        this.link = link;
        this.type = "1";
        this.targets = "[{\"PersonNo\":\"" + targets + "\"}]";
        this.createTime = StringUtils.isBlank(createTime) ? DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") : createTime;
        this.level = "3";
        //others = isOpenInMobile 标识禁止移动端预览
        this.others = "isOpenInMobile".equals(others) ? "{\"isOpenInMobile\":\"0\"}" : others;
    }
}
