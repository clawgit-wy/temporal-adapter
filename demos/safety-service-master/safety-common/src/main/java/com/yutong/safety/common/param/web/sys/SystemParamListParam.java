package com.yutong.safety.common.param.web.sys;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemParamListParam extends WebPageParam {

    @ApiModelProperty("参数编码")
    private String paramCode;
    @ApiModelProperty("参数名")
    private String paramName;
    @ApiModelProperty("描述")
    private String paramDesc;
    @ApiModelProperty("要排除的ID")
    private Integer unId;

    private Integer begin;
}
