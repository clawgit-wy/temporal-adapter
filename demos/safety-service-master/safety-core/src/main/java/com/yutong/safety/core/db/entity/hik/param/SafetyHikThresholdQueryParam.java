package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyHikThresholdQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("最近提交人")
    private String updateName;

    @ApiModelProperty("场景编码")
    private String sceneCode;
}
