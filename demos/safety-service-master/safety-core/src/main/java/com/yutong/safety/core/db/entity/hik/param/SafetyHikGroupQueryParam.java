package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyHikGroupQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("海浪平台唯一标识")
    private String indexCode;

    @ApiModelProperty("海康平台名称")
    private String name;

    @ApiModelProperty("楼栋信息")
    private String buildCode;

    private Integer status;

    private List<String> names;
}
