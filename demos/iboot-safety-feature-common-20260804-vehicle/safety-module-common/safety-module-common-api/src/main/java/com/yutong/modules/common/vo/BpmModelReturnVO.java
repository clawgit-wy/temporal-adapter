package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="获取异构系统表单字段记录", description="获取异构系统表单字段记录")
public class BpmModelReturnVO {

    @ApiModelProperty(value = "模型主键")
    private String modelId;

    @ApiModelProperty(value = "表单主键")
    private String formId;

    @ApiModelProperty(value = "表单地址")
    private String formUrl;

    @ApiModelProperty(value = "模板主键")
    private String templateId;
}
