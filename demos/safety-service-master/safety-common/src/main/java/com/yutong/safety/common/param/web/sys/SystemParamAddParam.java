package com.yutong.safety.common.param.web.sys;

import com.yutong.safety.common.param.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemParamAddParam implements Check {

    private Integer id;
    @ApiModelProperty("参数编码")
    private String paramCode;
    @ApiModelProperty("参数名")
    private String paramName;
    @ApiModelProperty("值")
    private String paramValue;
    @ApiModelProperty("描述")
    private String paramDesc;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("修改人")
    private String modifyBy;

    @Override
    public void check() {
    }
}
