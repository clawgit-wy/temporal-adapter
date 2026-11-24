package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BPM回调接口-传阅", description="BPM回调接口-传阅")
public class BpmPassProcessDTO {

    @ApiModelProperty(value = "流程主键")
    @NotNull
    private String processId;

    @ApiModelProperty(value = "传阅人 登录EIP账号")
    @NotNull
    private String circulated;

    @ApiModelProperty(value = "被传阅人 登录EIP账号列表")
    @NotNull
    private List<String> beCirculated;
}
