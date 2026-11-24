package com.yutong.modules.common.dto;

import com.yutong.modules.common.entity.bpm.FormIdEntity;
import com.yutong.modules.common.entity.bpm.FunctionIdEntity;
import com.yutong.modules.common.entity.bpm.ProcessDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BPM回调接口-流程执行模板", description="流程执行模板")
public class BpmMethodProcessDTO {

    @ApiModelProperty(value = "异构系统标识")
    private FormIdEntity formId;

    @ApiModelProperty(value = "函数标识")
    private FunctionIdEntity functionId;

    @ApiModelProperty(value = "BPM流程数据")
    private ProcessDataEntity processData;

    @ApiModelProperty(value = "多语言")
    private String language;
}
