package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SpotCheckResultParam extends WebPageParam {
    @ApiModelProperty("巡查日志ID")
    private Integer recordId;
}
